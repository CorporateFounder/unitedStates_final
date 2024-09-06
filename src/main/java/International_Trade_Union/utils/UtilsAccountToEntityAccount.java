package International_Trade_Union.utils;

import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.SlidingWindowBalanceEntity;
import International_Trade_Union.model.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilsAccountToEntityAccount {

    // Преобразование Account в SlidingWindowBalanceEntity
    public static SlidingWindowBalanceEntity accountToSlidingWindowBalanceEntity(Long windowKey, Account account) {
        SlidingWindowBalanceEntity balanceEntity = new SlidingWindowBalanceEntity();
        balanceEntity.setWindowKey(windowKey);
        balanceEntity.setAccountId(account.getAccount());
        balanceEntity.setDigitalDollarBalance(account.getDigitalDollarBalance());
        balanceEntity.setDigitalStockBalance(account.getDigitalStockBalance());
        balanceEntity.setDigitalStakingBalance(account.getDigitalStakingBalance());
        return balanceEntity;
    }

    // Преобразование SlidingWindowBalanceEntity в Account
    public static Account slidingWindowBalanceEntityToAccount(SlidingWindowBalanceEntity balanceEntity) {
        return new Account(
                balanceEntity.getAccountId(),
                balanceEntity.getDigitalDollarBalance(),
                balanceEntity.getDigitalStockBalance(),
                balanceEntity.getDigitalStakingBalance()
        );
    }

    // Преобразование списка SlidingWindowBalanceEntity в карту Account
    public static Map<String, Account> slidingWindowBalanceEntitiesToMapAccounts(List<SlidingWindowBalanceEntity> balanceEntities) {
        Map<String, Account> accountMap = new HashMap<>();
        for (SlidingWindowBalanceEntity balanceEntity : balanceEntities) {
            Account account = slidingWindowBalanceEntityToAccount(balanceEntity);
            accountMap.put(account.getAccount(), account);
        }
        return accountMap;
    }

    // Преобразование карты Account в список SlidingWindowBalanceEntity
    public static List<SlidingWindowBalanceEntity> accountsToSlidingWindowBalanceEntities(Long windowKey, Map<String, Account> accountMap) {
        List<SlidingWindowBalanceEntity> balanceEntities = new ArrayList<>();
        for (Account account : accountMap.values()) {
            SlidingWindowBalanceEntity balanceEntity = accountToSlidingWindowBalanceEntity(windowKey, account);
            balanceEntities.add(balanceEntity);
        }
        return balanceEntities;
    }

    // Преобразование EntityAccount в Account (существующая логика)
    public static Account entityAccountToAccount(EntityAccount entityAccount) {
        return new Account(
                entityAccount.getAccount(),
                entityAccount.getDigitalDollarBalance(),
                entityAccount.getDigitalStockBalance(),
                entityAccount.getDigitalStakingBalance()
        );
    }

    // Преобразование Account в EntityAccount (существующая логика)
    public static EntityAccount accountToEntityAccount(Account account) {
        return new EntityAccount(
                account.getAccount(),
                account.getDigitalDollarBalance(),
                account.getDigitalStockBalance(),
                account.getDigitalStakingBalance()
        );
    }

    // Преобразование списка EntityAccount в карту Account
    public static Map<String, Account> entityAccountsToMapAccounts(List<EntityAccount> entityAccounts) {
        Map<String, Account> accountMap = new HashMap<>();
        for (EntityAccount entityAccount : entityAccounts) {
            Account account = entityAccountToAccount(entityAccount);
            accountMap.put(account.getAccount(), account);
        }
        return accountMap;
    }

    // Преобразование карты Account в список EntityAccount
    public static List<EntityAccount> accountsToEntityAccounts(Map<String, Account> accountMap) {
        return accountMap.values().stream()
                .map(UtilsAccountToEntityAccount::accountToEntityAccount)
                .collect(Collectors.toList());
    }

    // Преобразование списка EntityAccount в список Account
    public static List<Account> entityAccountsToListAccounts(List<EntityAccount> entityAccounts) {
        return entityAccounts.stream()
                .map(UtilsAccountToEntityAccount::entityAccountToAccount)
                .collect(Collectors.toList());
    }

    // Преобразование списка Account в список EntityAccount
    public static List<EntityAccount> accountsToEntityAccounts(List<Account> accounts) {
        return accounts.stream()
                .map(UtilsAccountToEntityAccount::accountToEntityAccount)
                .collect(Collectors.toList());
    }
}
