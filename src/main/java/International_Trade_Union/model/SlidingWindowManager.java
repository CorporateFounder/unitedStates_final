package International_Trade_Union.model;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.SlidingWindowBalanceEntity;
import International_Trade_Union.entity.repository.SlidingWindowBalanceRepository;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlidingWindowManager {

    @Autowired
    private SlidingWindowBalanceRepository slidingWindowBalanceRepository;

    // Temporary in-memory window for balance changes
    private Map<Long, Map<String, Account>> temporaryWindow = new LinkedHashMap<>();

    // Add new balance changes to the temporary window


    public void addWindow(Long windowKey, Map<String, Account> balances) {
        temporaryWindow.put(windowKey, balances);

        // If the number of windows exceeds the limit, remove the oldest one
        if (temporaryWindow.size() > Seting.SLIDING_WINDOW_BALANCE) {
            Long oldestKey = temporaryWindow.keySet().iterator().next();
            temporaryWindow.remove(oldestKey);
        }
    }

    // Retrieve the account for a given window key and account ID
    public Account getAccount(Long windowKey, String accountId) {
        // Check the temporary window first
        Map<String, Account> window = temporaryWindow.get(windowKey);
        if (window != null && window.containsKey(accountId)) {
            return window.get(accountId);
        }

        // If not found, check the database for this window key
        SlidingWindowBalanceEntity balanceEntity = slidingWindowBalanceRepository.findByWindowKeyAndAccountId(windowKey, accountId);
        if (balanceEntity != null) {
            return new Account(
                balanceEntity.getAccountId(),
                balanceEntity.getDigitalDollarBalance(), // digitalDollarBalance
                balanceEntity.getDigitalStockBalance(),   // digitalStockBalance
                balanceEntity.getDigitalStakingBalance()  // digitalStakingBalance
            );
        }

        // If still not found, search in previous windows
        return findAccountInPreviousWindows(accountId, windowKey);
    }

    // Find the account in previous windows if not found in the current one
    private Account findAccountInPreviousWindows(String accountId, Long currentWindowKey) {
        List<SlidingWindowBalanceEntity> balances = slidingWindowBalanceRepository.findByAccountIdOrderByWindowKeyAsc(accountId);

        for (SlidingWindowBalanceEntity balance : balances) {
            if (balance.getWindowKey() <= currentWindowKey) {
                return new Account(
                    balance.getAccountId(),
                    balance.getDigitalDollarBalance(),
                    balance.getDigitalStockBalance(),
                    balance.getDigitalStakingBalance()
                );
            }
        }
        return null;
    }

    // Remove a balance snapshot from the temporary window
    public void remove(Long windowKey) {
        temporaryWindow.remove(windowKey);
    }

    // Persist the temporary window data to the database

    @Transactional // добавляем аннотацию для управления транзакциями

    public synchronized void saveWindowsToFile() {
        for (Map.Entry<Long, Map<String, Account>> entry : temporaryWindow.entrySet()) {
            Long windowKey = entry.getKey();
            Map<String, Account> balances = entry.getValue();

            for (Map.Entry<String, Account> balanceEntry : balances.entrySet()) {
                Account account = balanceEntry.getValue();
                SlidingWindowBalanceEntity balanceEntity = new SlidingWindowBalanceEntity();
                balanceEntity.setWindowKey(windowKey);
                balanceEntity.setAccountId(balanceEntry.getKey());
                balanceEntity.setDigitalDollarBalance(account.getDigitalDollarBalance());
                balanceEntity.setDigitalStockBalance(account.getDigitalStockBalance());
                balanceEntity.setDigitalStakingBalance(account.getDigitalStakingBalance());

                slidingWindowBalanceRepository.save(balanceEntity);
            }
        }

        // Clear the temporary window after successful persistence
        temporaryWindow.clear();

        // Clean up old blocks if the count exceeds 50
        long count = slidingWindowBalanceRepository.count();
        if (count > Seting.SLIDING_WINDOW_BALANCE) {
            slidingWindowBalanceRepository.deleteTopByOrderByWindowKeyAsc();
        }
    }

    // Retrieve all accounts for a specific window key
    public Map<String, Account> getWindow(Long windowKey) {
        // Check temporary data first
        if (temporaryWindow.containsKey(windowKey)) {
            return temporaryWindow.get(windowKey);
        }

        // Load from database if not found in temporary storage
        List<SlidingWindowBalanceEntity> balances = slidingWindowBalanceRepository.findByWindowKey(windowKey);
        return balances.stream()
                .collect(Collectors.toMap(
                        SlidingWindowBalanceEntity::getAccountId,
                        balanceEntity -> new Account(
                            balanceEntity.getAccountId(),
                            balanceEntity.getDigitalDollarBalance(),
                            balanceEntity.getDigitalStockBalance(),
                            balanceEntity.getDigitalStakingBalance()
                        )
                ));
    }

    // Clear temporary window data
    public void clearTemporaryWindow() {
        temporaryWindow.clear();
    }

    // Check if a window exists either in memory or in the database
    public boolean containsWindow(Long windowKey) {
        // Check the temporary window first
        if (temporaryWindow.containsKey(windowKey)) {
            return true;  // Window found in temporary data
        }

        // Check the database
        List<SlidingWindowBalanceEntity> balances = slidingWindowBalanceRepository.findByWindowKey(windowKey);
        return !balances.isEmpty();  // If the database has records, the window exists
    }

    // Retrieve balances for specific accounts by window key
    public Map<String, Account> getBalancesForAccounts(List<String> accountIds, Long windowKey) throws IOException {
        Map<String, Account> accountBalances = new LinkedHashMap<>();

        // Check temporary data first
        Map<String, Account> windowBalances = temporaryWindow.get(windowKey);
        if (windowBalances != null) {
            for (String accountId : accountIds) {
                if (windowBalances.containsKey(accountId)) {
                    accountBalances.put(accountId, windowBalances.get(accountId)); // Add from temporary data
                }
            }
        }

        // Get the remaining accounts that are not in the temporary window
        List<String> remainingAccounts = accountIds.stream()
                .filter(accountId -> !accountBalances.containsKey(accountId))
                .collect(Collectors.toList());

        // Fetch from the database if remaining accounts are not found in the temporary window
        if (!remainingAccounts.isEmpty()) {
            List<SlidingWindowBalanceEntity> slidingWindowBalances;
            try {
                slidingWindowBalances = slidingWindowBalanceRepository.findByWindowKeyAndAccountIdIn(windowKey, remainingAccounts);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Error fetching balances from the database", e);
            }

            // Convert SlidingWindowBalanceEntity to Account and add to the result
            for (SlidingWindowBalanceEntity balanceEntity : slidingWindowBalances) {
                Account account = new Account(
                    balanceEntity.getAccountId(),
                    balanceEntity.getDigitalDollarBalance(),
                    balanceEntity.getDigitalStockBalance(),
                    balanceEntity.getDigitalStakingBalance()
                );
                accountBalances.put(balanceEntity.getAccountId(), account);

                // Update temporary window with new data
                if (windowBalances == null) {
                    windowBalances = new LinkedHashMap<>();
                }
                windowBalances.put(balanceEntity.getAccountId(), account);
            }

            // Add updated temporary window back
            temporaryWindow.put(windowKey, windowBalances);
        }

        return accountBalances;
    }

    // Collect account IDs from a block
    public List<String> getAccountIdsFromBlock(Block block) {
        Set<String> accountIds = new HashSet<>();

        // Iterate through each transaction in the block
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            // Add sender if exists and is not blank
            if (transaction.getSender() != null && !transaction.getSender().isBlank()) {
                accountIds.add(transaction.getSender());
            }

            // Add customer if exists and is not blank
            if (transaction.getCustomer() != null && !transaction.getCustomer().isBlank()) {
                accountIds.add(transaction.getCustomer());
            }
        }

        return new ArrayList<>(accountIds);
    }
}
