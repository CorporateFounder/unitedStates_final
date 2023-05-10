package International_Trade_Union.entity;


import lombok.Data;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;

import java.util.List;
import java.util.Map;

@Data
public class ResultMiningData {
    private Map<String, Account> balance;
    private Blockchain blockchain;
    private Block miningBlock;
    private List<Block> lastBlock;
    private Account governments;

}
