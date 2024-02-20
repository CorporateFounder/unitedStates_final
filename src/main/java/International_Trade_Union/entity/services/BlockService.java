package International_Trade_Union.entity.services;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.entities.EntityDtoTransaction;
import International_Trade_Union.entity.repository.*;
import International_Trade_Union.model.Account;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BlockService {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private EntityLawsRepository entityLawsRepository;
    private static EntityBlockRepository blockService;
    private static EntityLawsRepository lawService;
    private static EntityDtoTransactionRepository dtoService;

    private static EntityAccountRepository accountService;


    @Autowired
    private EntityBlockRepository entityBlockRepository;


    @Autowired
    private EntityDtoTransactionRepository dtoTransactionRepository;

    @Autowired
    private EntityAccountRepository entityAccountRepository;


    @PostConstruct
    public void init() {
        blockService = entityBlockRepository;
        lawService = entityLawsRepository;
        dtoService = dtoTransactionRepository;
        accountService = entityAccountRepository;

    }

    public static void deletedAll(){
        blockService.deleteAll();
        accountService.deleteAll();
        lawService.deleteAll();
        dtoService.deleteAll();

        blockService.flush();
        accountService.flush();
        lawService.flush();
        dtoService.findAll();


    }

    public static EntityLawsRepository getLawService() {
        return lawService;
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

    public static void saveBlock(EntityBlock entityBlock) {

        blockService.save(entityBlock);
        blockService.flush();

    }





    public  void deleteEntityBlocksAndRelatedData(Long threshold) {
        Session session = entityManager.unwrap(Session.class);
        session.setJdbcBatchSize(50);
        entityBlockRepository.deleteAllBySpecialIndexGreaterThanEqual(threshold);
        entityBlockRepository.flush();
        session.clear();
    }



    public List<EntityAccount> findByAccountIn(Map<String, Account> map){
        List<String> accounts = map.entrySet().stream().map(t->t.getValue().getAccount()).collect(Collectors.toList());
        return accountService.findByAccountIn(accounts);
    }


    public static List<EntityAccount> findAllAccounts(){
        return accountService.findAll();

    }




    public static long sizeBlock(){
        return  blockService.count();
    }
    public static EntityBlock lastBlock(){
        return blockService.findBySpecialIndex(blockService.count()-1);
    }

    @Transactional
    public void saveAllBLockF(List<EntityBlock> entityBlocks){
        entityBlockRepository.saveAll(entityBlocks);
        entityBlockRepository.flush();
    }
    public static void saveAllBlock(List<EntityBlock> entityBlocks) {
        blockService.saveAll(entityBlocks);
        blockService.flush();
    }
    public static void removeAllBlock(List<EntityBlock> entityBlocks){
        blockService.deleteAll(entityBlocks);
        blockService.flush();
    }
    public static void saveAccount(EntityAccount entityAccount){

        accountService.save(entityAccount);
        accountService.flush();
    }




    public void saveAccountAllF(List<EntityAccount> entityAccounts){
        Session session = entityManager.unwrap(Session.class);
        session.setJdbcBatchSize(50);
        List<EntityAccount> entityResult = new ArrayList<>();
        for (EntityAccount entityAccount : entityAccounts) {
            if(entityAccountRepository.findByAccount(entityAccount.getAccount()) != null){
                EntityAccount temp = entityAccountRepository.findByAccount(entityAccount.getAccount());
                temp.setDigitalDollarBalance(entityAccount.getDigitalDollarBalance());
                temp.setDigitalStockBalance(entityAccount.getDigitalStockBalance());
                entityResult.add(temp);
            }else {
                entityResult.add(entityAccount);
            }
        }

        entityAccountRepository.saveAll(entityResult);
        entityAccountRepository.flush();
        session.clear();
    }
    public static void saveAccountAll(List<EntityAccount> entityAccounts){


        // Кэш для результатов findByAccount
        Map<String, EntityAccount> cache = new HashMap<>();

        // Списки для пакетного обновления
        List<String> accounts = new ArrayList<>();
        List<Double> digitalDollarBalances = new ArrayList<>();
        List<Double> digitalStockBalances = new ArrayList<>();
        List<Double> digitalStakingBalances = new ArrayList<>();

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
        accountService.batchInsert(accounts, digitalDollarBalances, digitalStockBalances, digitalStakingBalances);

        // Обновить кэш с новыми данными (необязательно, зависит от логики)
        for (EntityAccount entityAccount : entityAccounts) {
            cache.put(entityAccount.getAccount(), entityAccount);
        }

    }

    public static EntityBlock findByHashBlock(String hashBlock){
        return blockService.findByHashBlock(hashBlock);
    }

    public static EntityDtoTransaction findBySign(String sign){
        Base64.Decoder decoder = Base64.getDecoder();

// декодируем строку обратно в массив байтов
        byte[] decoded = decoder.decode(sign);
        return dtoService.findBySign(decoded);

    }
    public static List<EntityDtoTransaction> findAllDto(){
        return dtoService.findAll();
    }
    public static EntityDtoTransaction findByIdDto(long id){
        return dtoService.findById(id);
    }
    public static EntityBlock findById(long id){
        return blockService.findById(id);
    }
    public static EntityBlock findBySpecialIndex(long specialIndex){
        return blockService.findBySpecialIndex(specialIndex);
    }

    public static List<EntityBlock> findAllByIdBetween(long from, long to){
        return blockService.findAllByIdBetween(from, to);
    }
    public static List<EntityBlock> findBySpecialIndexBetween(long from, long to){
        return blockService.findBySpecialIndexBetween(from, to);
    }

    public static List<EntityBlock> findAll() {
        return blockService.findAll();
    }

    public static EntityAccount entityAccount(String account){
        return accountService.findByAccount(account);
    }

    public static long countBlock() {
        return blockService.count();
    }

    public static long countAccount() {
        return accountService.count();
    }

    public static boolean isEmpty() {
        boolean exists = blockService.existsById(1L);
        return exists;
    }


    public static List<DtoTransaction> findBySender(String sender, int from, int to) throws IOException {
        Pageable firstPageWithTenElements = (Pageable) PageRequest.of(from, to);
        List<EntityDtoTransaction> list =
                 dtoService.findBySender(sender, firstPageWithTenElements)
                        .getContent();
        List<DtoTransaction> dtoTransactions =
                UtilsBlockToEntityBlock.entityDtoTransactionToDtoTransaction(list);
        return dtoTransactions;
    }

    public static List<DtoTransaction> findByCustomer(String customer, int from, int to) throws IOException {
        Pageable firstPageWithTenElements = (Pageable) PageRequest.of(from, to);
        List<EntityDtoTransaction> list =
                 dtoService.findByCustomer(customer,firstPageWithTenElements)
                        .getContent();
        List<DtoTransaction> dtoTransactions =
                UtilsBlockToEntityBlock.entityDtoTransactionToDtoTransaction(list);
        return dtoTransactions;
    }

    public static long countSenderTransaction(String sender){
        return dtoService.countBySender(sender);
    }

    public static long countCustomerTransaction(String customer){
        return dtoService.countByCustomer(customer);
    }

}
