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
//    private static EntityBlockRepository blockService;
//    private static EntityLawsRepository lawService;
//    private static EntityDtoTransactionRepository dtoService;
//
//    private static EntityAccountRepository accountService;


    @Autowired
    private EntityBlockRepository entityBlockRepository;


    @Autowired
    private EntityDtoTransactionRepository dtoTransactionRepository;

    @Autowired
    private EntityAccountRepository entityAccountRepository;




    public  void deletedAll(){
        entityBlockRepository.deleteAll();
        entityAccountRepository.deleteAll();
        entityLawsRepository.deleteAll();
        dtoTransactionRepository.deleteAll();



    }

    public  EntityLawsRepository getLawService() {
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

    public  void saveBlock(EntityBlock entityBlock) {

        entityBlockRepository.save(entityBlock);
        entityBlockRepository.flush();

    }


    @Transactional
    public  void deleteEntityBlocksAndRelatedData(Long threshold) {
        Session session = entityManager.unwrap(Session.class);
        session.setJdbcBatchSize(50);
        entityBlockRepository.deleteAllBySpecialIndexGreaterThanEqual(threshold);
        entityBlockRepository.flush();
        session.clear();
    }




    public List<EntityAccount> findByAccountIn(Map<String, Account> map){
        List<String> accounts = map.entrySet().stream().map(t->t.getValue().getAccount()).collect(Collectors.toList());
        return entityAccountRepository.findByAccountIn(accounts);
    }


    public  List<EntityAccount> findAllAccounts(){
        return entityAccountRepository.findAll();

    }




    public  long sizeBlock(){
        return  entityBlockRepository.count();
    }
    public  EntityBlock lastBlock(){
        return entityBlockRepository.findBySpecialIndex(entityBlockRepository.count()-1);
    }

    @Transactional
    public void saveAllBLockF(List<EntityBlock> entityBlocks){
        Session session = entityManager.unwrap(Session.class);
        session.setJdbcBatchSize(50);
        entityBlockRepository.saveAll(entityBlocks);
        entityBlockRepository.flush();
        session.clear();
    }
    public  void saveAllBlock(List<EntityBlock> entityBlocks) {
        entityBlockRepository.saveAll(entityBlocks);
        entityBlockRepository.flush();
    }
    public  void removeAllBlock(List<EntityBlock> entityBlocks){
        entityBlockRepository.deleteAll(entityBlocks);
        entityBlockRepository.flush();
    }
    public  void saveAccount(EntityAccount entityAccount){

        entityAccountRepository.save(entityAccount);
        entityAccountRepository.flush();
    }



    @Transactional

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
    public  void saveAccountAll(List<EntityAccount> entityAccounts){


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
        entityAccountRepository.batchInsert(accounts, digitalDollarBalances, digitalStockBalances, digitalStakingBalances);

        // Обновить кэш с новыми данными (необязательно, зависит от логики)
        for (EntityAccount entityAccount : entityAccounts) {
            cache.put(entityAccount.getAccount(), entityAccount);
        }

    }

    public  EntityBlock findByHashBlock(String hashBlock){
        return entityBlockRepository.findByHashBlock(hashBlock);
    }

    public  EntityDtoTransaction findBySign(String sign){
        Base64.Decoder decoder = Base64.getDecoder();

// декодируем строку обратно в массив байтов
        byte[] decoded = decoder.decode(sign);
        return dtoTransactionRepository.findBySign(decoded);

    }
    @javax.transaction.Transactional
    public  List<EntityDtoTransaction> findAllDto(){
        return dtoTransactionRepository.findAll();
    }

    @Transactional
    public  EntityDtoTransaction findByIdDto(long id){
        return dtoTransactionRepository.findById(id);
    }

    @Transactional
    public  EntityBlock findById(long id){
        return entityBlockRepository.findById(id);
    }

    @Transactional
    public  EntityBlock findBySpecialIndex(long specialIndex){
        return entityBlockRepository.findBySpecialIndex(specialIndex);
    }

    public  List<EntityBlock> findAllByIdBetween(long from, long to){
        return entityBlockRepository.findAllByIdBetween(from, to);
    }

    @Transactional
    public  List<EntityBlock> findBySpecialIndexBetween(long from, long to){
        return entityBlockRepository.findBySpecialIndexBetween(from, to);
    }

    public  List<EntityBlock> findAll() {
        return entityBlockRepository.findAll();
    }

    public  EntityAccount entityAccount(String account){
        return entityAccountRepository.findByAccount(account);
    }

    public  long countBlock() {
        return entityBlockRepository.count();
    }

    public  long countAccount() {
        return entityAccountRepository.count();
    }

    public  boolean isEmpty() {
        boolean exists = entityBlockRepository.existsById(1L);
        return exists;
    }


    public  List<DtoTransaction> findBySender(String sender, int from, int to) throws IOException {
        Pageable firstPageWithTenElements = (Pageable) PageRequest.of(from, to);
        List<EntityDtoTransaction> list =
                 dtoTransactionRepository.findBySender(sender, firstPageWithTenElements)
                        .getContent();
        List<DtoTransaction> dtoTransactions =
                UtilsBlockToEntityBlock.entityDtoTransactionToDtoTransaction(list);
        return dtoTransactions;
    }

    public  List<DtoTransaction> findByCustomer(String customer, int from, int to) throws IOException {
        Pageable firstPageWithTenElements = (Pageable) PageRequest.of(from, to);
        List<EntityDtoTransaction> list =
                 dtoTransactionRepository.findByCustomer(customer,firstPageWithTenElements)
                        .getContent();
        List<DtoTransaction> dtoTransactions =
                UtilsBlockToEntityBlock.entityDtoTransactionToDtoTransaction(list);
        return dtoTransactions;
    }

    public  long countSenderTransaction(String sender){
        return dtoTransactionRepository.countBySender(sender);
    }

    public  long countCustomerTransaction(String customer){
        return dtoTransactionRepository.countByCustomer(customer);
    }

}
