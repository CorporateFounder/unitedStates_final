package International_Trade_Union.entity.services;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.entities.EntityDtoTransaction;
import International_Trade_Union.entity.repository.EntityAccountRepository;
import International_Trade_Union.entity.repository.EntityBlockRepository;
import International_Trade_Union.entity.repository.EntityDtoTransactionRepository;
import International_Trade_Union.entity.repository.EntityLawsRepository;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class BlockService {
    private static EntityBlockRepository blockService;
    private static EntityLawsRepository lawService;
    private static EntityDtoTransactionRepository dtoService;

    private static EntityAccountRepository accountService;


    @Autowired
    private EntityBlockRepository entityBlockRepository;

    @Autowired
    private EntityLawsRepository entityLawsRepository;
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




//    public static void deleteEntityBlocksAndRelatedData(Long threshold) {
//        blockService.deleteBySpecialIndexGreaterThanOrEqualTo(threshold);
//    }

    public static long sizeBlock(){
        return  blockService.count();
    }
    public static EntityBlock lastBlock(){
        return blockService.findBySpecialIndex(blockService.count()-1);
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
    public static void saveAccountAll(List<EntityAccount> entityAccounts){

        List<EntityAccount> entityResult = new ArrayList<>();
        for (EntityAccount entityAccount : entityAccounts) {
            if(accountService.findByAccount(entityAccount.getAccount()) != null){
                EntityAccount temp = accountService.findByAccount(entityAccount.getAccount());
                temp.setDigitalDollarBalance(entityAccount.getDigitalDollarBalance());
                temp.setDigitalStockBalance(entityAccount.getDigitalStockBalance());
                entityResult.add(temp);
            }else {
                entityResult.add(entityAccount);
            }
        }
        accountService.saveAll(entityResult);
        accountService.flush();

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
