package International_Trade_Union.entity.services;

import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.repository.EntityAccountRepository;
import International_Trade_Union.entity.repository.EntityBlockRepository;
import International_Trade_Union.entity.repository.EntityDtoTransactionRepository;
import International_Trade_Union.entity.repository.EntityLawsRepository;
import International_Trade_Union.model.Account;
import International_Trade_Union.utils.UtilsAccountToEntityAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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

    public static void saveBlock(EntityBlock entityBlock) {
        blockService.save(entityBlock);
    }

    public static void saveAllBlock(List<EntityBlock> entityBlocks) {
        blockService.saveAll(entityBlocks);
    }

    public static void saveAccount(EntityAccount entityAccount){
        accountService.save(entityAccount);
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

    }
    public static EntityBlock findById(long id){
        return blockService.findById(id);
    }

    public static List<EntityBlock> findAllByIdBetween(long from, long to){
        return blockService.findAllByIdBetween(from, to);
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


}
