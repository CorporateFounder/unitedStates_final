package International_Trade_Union.entity;


import lombok.Data;
import International_Trade_Union.entity.blockchain.block.Block;

import java.util.List;

@Data
public class EntityChain {

    private int size;
    private List<Block> blocks;

    public EntityChain() {
    }

    public EntityChain(int sizeBlockhain, List<Block> blockchainList) {
        this.size = sizeBlockhain;
        this.blocks = blockchainList;
    }
}
