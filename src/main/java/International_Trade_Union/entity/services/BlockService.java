package International_Trade_Union.entity.services;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.entities.EntityDtoTransaction;
import International_Trade_Union.entity.repository.EntityAccountRepository;
import International_Trade_Union.entity.repository.EntityBlockRepository;
import International_Trade_Union.entity.repository.EntityDtoTransactionRepository;
import International_Trade_Union.entity.repository.EntityLawsRepository;
import International_Trade_Union.model.Account;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BlockService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private EntityLawsRepository entityLawsRepository;

    @Autowired
    private EntityBlockRepository entityBlockRepository;

    @Autowired
    private EntityDtoTransactionRepository dtoTransactionRepository;

    @Autowired
    private EntityAccountRepository entityAccountRepository;


    @Transactional
    public void deletedAll() throws IOException {
        try {
            entityBlockRepository.deleteAll();
            entityAccountRepository.deleteAll();
            entityLawsRepository.deleteAll();
            dtoTransactionRepository.deleteAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("deletedAll: error: save: ", e);

        }


    }

    public EntityLawsRepository getLawService() {
        return entityLawsRepository;
    }


    public EntityBlockRepository getEntityBlockRepository() {
        return entityBlockRepository;
    }

    public EntityLawsRepository getEntityLawsRepository() {
        return entityLawsRepository;
    }


    public EntityAccountRepository getEntityAccountRepository() {
        return entityAccountRepository;
    }

    public void saveBlock(EntityBlock entityBlock) throws IOException {
        try {
            entityBlockRepository.save(entityBlock);
            entityBlockRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("saveBlock: error: save: ", e);

        }


    }


    @Transactional
    public void deleteEntityBlocksAndRelatedData(Long threshold) throws IOException {
        try {

            entityBlockRepository.deleteAllBySpecialIndexGreaterThanEqual(threshold);
            entityBlockRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("deleteEntityBlocksAndRelatedData: error: save: ", e);

        }
    }


    public List<EntityAccount> findByAccountIn(Map<String, Account> accounts) throws IOException {
        try {
            List<String> accountIds = new ArrayList<>(accounts.keySet());
            return entityAccountRepository.findByAccountIn(accountIds);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findByAccountIn: error: ", e);
        }
    }

    @Transactional(readOnly = true)
    public double getTotalDigitalDollarBalance() {
        Double totalBalance = entityAccountRepository.getTotalDigitalDollarBalance();
        return (totalBalance != null) ? totalBalance : 0.0;
    }


    public EntityAccount findByAccount(String account) throws IOException {
        EntityAccount entityAccounts = null;
        try {
            entityAccounts = entityAccountRepository.findByAccount(account);
            if (entityAccounts == null) {
                return new EntityAccount(account, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return new EntityAccount(account, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return entityAccounts;
    }

    public List<EntityAccount> findBYAccountString(List<String> accounts) throws IOException {
        List<EntityAccount> entityAccounts = new ArrayList<>();
        try {
            accounts = accounts.stream().filter(t -> t != null).collect(Collectors.toList());
            entityAccounts = entityAccountRepository.findByAccountIn(accounts);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findBYAccountString: error: save: ", e);

        }

        return entityAccounts;
    }

    @Transactional
    public void saveAllBlocksAndAccounts(List<EntityBlock> entityBlocks, List<EntityAccount> entityAccounts) throws IOException {
        try {
            saveAllBLockF(entityBlocks);
            saveAccountAllF(entityAccounts);
        } catch (Exception e) {
            throw new IOException("saveAllBlocksAndAccounts: error: ", e);
        }
    }

    public List<EntityAccount> findByDtoAccounts(List<DtoTransaction> transactions) throws IOException {
        List<String> accounts = new ArrayList<>();
        for (DtoTransaction transaction : transactions) {
            accounts.add(transaction.getSender());
            accounts.add(transaction.getCustomer());
        }

        List<EntityAccount> entityAccounts;
        try {
            entityAccounts = entityAccountRepository.findByAccountIn(accounts);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findByDtoAccounts: error: save: ", e);
        }

        // Создаем карту для быстрого поиска существующих аккаунтов по их идентификатору
        Map<String, EntityAccount> accountMap = entityAccounts.stream()
                .collect(Collectors.toMap(EntityAccount::getAccount, Function.identity()));

        // Проходим по списку аккаунтов и добавляем недостающие
        for (String account : accounts) {
            if (!accountMap.containsKey(account)) {
                EntityAccount newAccount = new EntityAccount(account, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                entityAccounts.add(newAccount);
            }
        }

        return entityAccounts;
    }


    public List<EntityAccount> findAllAccounts() throws IOException {
        List<EntityAccount> entityAccounts = new ArrayList<>();
        try {
            entityAccounts = entityAccountRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findAllAccounts: error: save: ", e);

        }

        return entityAccounts;
    }


    public long sizeBlock() throws IOException {
        long size = 0;
        try {
            size = entityBlockRepository.count();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("sizeBlock: error: save: ", e);
        }

        return size;
    }


    public EntityBlock lastBlock() throws IOException {
        EntityBlock entityBlock = null;
        try {
            entityBlock = entityBlockRepository.findBySpecialIndex(entityBlockRepository.count() - 1);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("lastBlock: error: save: ", e);
        }
        return entityBlock;
    }

    @Transactional
    public void saveAllBLockF(List<EntityBlock> entityBlocks) {
        Session session = null;
        try {
            session = entityManager.unwrap(Session.class);
            session.setJdbcBatchSize(50);
            entityBlockRepository.saveAll(entityBlocks);
            entityBlockRepository.flush();

        } finally {

            session.clear();
        }

    }

    @Transactional
    public void saveAllBlock(List<EntityBlock> entityBlocks) throws IOException {
        try {
            entityBlockRepository.saveAll(entityBlocks);
            entityBlockRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("saveAllBlock: error: save: ", e);
        }

    }

    public void removeAllBlock(List<EntityBlock> entityBlocks) throws IOException {
        try {
            entityBlockRepository.deleteAll(entityBlocks);
            entityBlockRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("removeAllBlock: error: save: ", e);

        }
    }

    public void saveAccount(EntityAccount entityAccount) throws IOException {
        try {
            entityAccountRepository.save(entityAccount);
            entityAccountRepository.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("saveAccount: error: save: ", e);

        }

    }

    private boolean accountsAreEqual(EntityAccount existingAccount, EntityAccount newAccount) {
        if (existingAccount == null || newAccount == null) {
            return false;
        }

        if (existingAccount.getDigitalDollarBalance() == null || newAccount.getDigitalDollarBalance() == null
                || existingAccount.getDigitalStockBalance() == null || newAccount.getDigitalStockBalance() == null
                || existingAccount.getDigitalStakingBalance() == null || newAccount.getDigitalStakingBalance() == null) {
            return false;
        }

        return existingAccount.getDigitalDollarBalance().compareTo(newAccount.getDigitalDollarBalance()) == 0
                && existingAccount.getDigitalStockBalance().compareTo(newAccount.getDigitalStockBalance()) == 0
                && existingAccount.getDigitalStakingBalance().compareTo(newAccount.getDigitalStakingBalance()) == 0;
    }

    @Transactional
    public void saveAccountAllF(List<EntityAccount> entityAccounts) {
        Session session = null;
        try {
            session = entityManager.unwrap(Session.class);
            session.setJdbcBatchSize(50);

            // Fetch all existing accounts in a single query
            List<String> accountNames = entityAccounts.stream()
                    .map(EntityAccount::getAccount)
                    .collect(Collectors.toList());

            Map<String, EntityAccount> existingAccounts = entityAccountRepository.findAllByAccountIn(accountNames)
                    .stream()
                    .collect(Collectors.toMap(EntityAccount::getAccount, account -> account));

            List<EntityAccount> entityResult = entityAccounts.stream()
                    .map(entityAccount -> {
                        if (existingAccounts.containsKey(entityAccount.getAccount())) {
                            EntityAccount existingAccount = existingAccounts.get(entityAccount.getAccount());
                            if (!accountsAreEqual(existingAccount, entityAccount)) {
                                existingAccount.setDigitalDollarBalance(entityAccount.getDigitalDollarBalance());
                                existingAccount.setDigitalStockBalance(entityAccount.getDigitalStockBalance());
                                existingAccount.setDigitalStakingBalance(entityAccount.getDigitalStakingBalance());
                            }
                            return existingAccount;
                        } else {
                            return entityAccount;
                        }
                    })
                    .collect(Collectors.toList());

            entityAccountRepository.saveAll(entityResult);
            entityAccountRepository.flush();
        } finally {
            if (session != null) {
                session.clear();
            }
        }
    }

    @Transactional
    public void saveAccountAll(List<EntityAccount> entityAccounts) throws IOException {
        try {
            // Кэш для результатов findByAccount
            Map<String, EntityAccount> cache = new HashMap<>();

            // Списки для пакетного обновления
            List<String> accounts = new ArrayList<>();
            List<BigDecimal> digitalDollarBalances = new ArrayList<>();
            List<BigDecimal> digitalStockBalances = new ArrayList<>();
            List<BigDecimal> digitalStakingBalances = new ArrayList<>();

            for (EntityAccount entityAccount : entityAccounts) {
                EntityAccount cachedAccount = cache.get(entityAccount.getAccount());

                if (cachedAccount != null) {
                    // Обновить существующую запись в кэше
                    cachedAccount.setDigitalDollarBalance(entityAccount.getDigitalDollarBalance());
                    cachedAccount.setDigitalStockBalance(entityAccount.getDigitalStockBalance());
                } else {
                    // Добавить новую запись для пакетного обновления
                    accounts.add(entityAccount.getAccount());
                    digitalDollarBalances.add(entityAccount.getDigitalDollarBalance());
                    digitalStockBalances.add(entityAccount.getDigitalStockBalance());
                    digitalStakingBalances.add(entityAccount.getDigitalStakingBalance());
                }

                // Сохранить в кэш для потенциального обновления
                cache.put(entityAccount.getAccount(), entityAccount);

            }

            // Пакетное обновление
            entityAccountRepository.batchInsert(accounts, digitalDollarBalances, digitalStockBalances, digitalStakingBalances);

            // Обновить кэш с новыми данными (необязательно, зависит от логики)
            for (EntityAccount entityAccount : entityAccounts) {
                cache.put(entityAccount.getAccount(), entityAccount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("saveAccountAll: error: save: ", e);

        }

    }

    public EntityBlock findByHashBlock(String hashBlock) {
        return entityBlockRepository.findByHashBlock(hashBlock);
    }


    public EntityDtoTransaction findBySign(String sign) throws IOException {
        EntityDtoTransaction entityDtoTransaction = null;
        try {
            entityDtoTransaction = dtoTransactionRepository.findBySign(sign);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findBySign: error: save: ", e);

        }
        return entityDtoTransaction;
    }


    public boolean existsBySign(byte[] sign) {
        Base base = new Base58();
        return dtoTransactionRepository.existsBySign(base.encode(sign));
    }


    public List<EntityDtoTransaction> findAllDto() throws IOException {
        List<EntityDtoTransaction> dtoTransactions = new ArrayList<>();
        try {
            dtoTransactions = dtoTransactionRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findAllDto: error: save: ", e);

        }
        return dtoTransactions;
    }


    public EntityDtoTransaction findByIdDto(long id) {
        return dtoTransactionRepository.findById(id);
    }


    public EntityBlock findById(long id) {
        return entityBlockRepository.findById(id);
    }


    public EntityBlock findBySpecialIndex(long specialIndex) throws IOException {
        EntityBlock entityBlock = null;
        try {
            entityBlock = entityBlockRepository.findBySpecialIndex(specialIndex);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findBySpecialIndex: error: save: ", e);

        }

        return entityBlock;
    }

    public List<EntityBlock> findAllByIdBetween(long from, long to) {
        return entityBlockRepository.findAllByIdBetween(from, to);
    }


    public List<EntityBlock> findBySpecialIndexBetween(long from, long to) throws IOException {
        List<EntityBlock> entityBlocks = null;
        try {
            entityBlocks = entityBlockRepository.findBySpecialIndexBetween(from, to);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findBySpecialIndexBetween: error: save: ", e);

        }
        return entityBlocks;

    }

    public List<EntityBlock> findAll() {
        return entityBlockRepository.findAll();
    }

    public EntityAccount entityAccount(String account) {
        return entityAccountRepository.findByAccount(account);
    }

    public long countBlock() {
        return entityBlockRepository.count();
    }


    public long countAccount() {
        return entityAccountRepository.count();
    }


    public boolean isEmpty() throws IOException {
        boolean exists = false;
        try {
            exists = entityBlockRepository.existsById(1L);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("isEmpty: error: save: ", e);

        }

        return exists;
    }


    public List<DtoTransaction> findBySender(String sender, int from, int to) throws IOException {
        List<DtoTransaction> dtoTransactions = null;
        try {
            Pageable firstPageWithTenElements = (Pageable) PageRequest.of(from, to);
            List<EntityDtoTransaction> list =
                    dtoTransactionRepository.findBySender(sender, firstPageWithTenElements)
                            .getContent();
            dtoTransactions =
                    UtilsBlockToEntityBlock.entityDtoTransactionToDtoTransaction(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findBySender: error: save: ", e);

        }
        return dtoTransactions;
    }


    public List<DtoTransaction> findByCustomer(String customer, int from, int to) throws IOException {
        List<DtoTransaction> dtoTransactions = null;
        try {
            Pageable firstPageWithTenElements = (Pageable) PageRequest.of(from, to);
            List<EntityDtoTransaction> list =
                    dtoTransactionRepository.findByCustomer(customer, firstPageWithTenElements)
                            .getContent();
            dtoTransactions =
                    UtilsBlockToEntityBlock.entityDtoTransactionToDtoTransaction(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("findByCustomer: error: save: ", e);

        }


        return dtoTransactions;
    }


    public long countSenderTransaction(String sender) throws IOException {
        long size = 0;
        try {
            size = dtoTransactionRepository.countBySender(sender);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("countSenderTransaction: error: save: ", e);

        }

        return size;

    }


    public long countCustomerTransaction(String customer) throws IOException {
        long size = 0;
        try {
            size = dtoTransactionRepository.countByCustomer(customer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("countCustomerTransaction: error: save: ", e);

        }

        return size;

    }


    @Transactional(readOnly = true)
    public List<EntityBlock> findBlocksByTransactionSign(String sign) throws IOException {
        List<EntityBlock> entityBlocks = new ArrayList<>();
        try {
            entityBlocks = entityBlockRepository.findBlocksByTransactionSign(sign);
        } catch (Exception e) {
            e.printStackTrace();
            entityBlocks = new ArrayList<>();
            throw new IOException("findBlocksByTransactionSign: error: save: ", e);
        } finally {
            return entityBlocks;
        }

    }
}
