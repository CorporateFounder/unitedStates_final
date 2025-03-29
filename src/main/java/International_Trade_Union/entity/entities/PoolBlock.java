package International_Trade_Union.entity.entities;

import International_Trade_Union.entity.blockchain.block.Block;
import lombok.Data;

@Data
public class PoolBlock {

    public PoolBlock() {
    }

    public PoolBlock(String miner, Block block) {
        this.miner = miner;
        this.block = block;
    }

    String miner;
    Block block;
}

