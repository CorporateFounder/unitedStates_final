package International_Trade_Union.entity.services;

import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.repository.EntityBlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class BlockService {
    private static EntityBlockRepository blockService;


    @Autowired
    private EntityBlockRepository entityBlockRepository;


    @PostConstruct
    public void init() {
        blockService = entityBlockRepository;
    }
    public  static void save(EntityBlock entityBlock){
        blockService.save(entityBlock);
    }
    public static void saveAll(List<EntityBlock> entityBlocks){
        blockService.saveAll(entityBlocks);
    }

    public static List<EntityBlock> findAll(){
        return blockService.findAll();


    }

    public static long count(){
      return  blockService.count();
    }


    public static boolean isEmpty(){
        boolean exists = blockService.existsById(1L);
       return exists;
    }
}
