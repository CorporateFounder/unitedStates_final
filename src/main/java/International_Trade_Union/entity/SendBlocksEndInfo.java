package International_Trade_Union.entity;

import International_Trade_Union.entity.blockchain.block.Block;
import lombok.Data;

import java.util.List;

@Data
public class SendBlocksEndInfo {
    private int version;
    private List<Block> list;
    public SendBlocksEndInfo() {
    }

    public SendBlocksEndInfo(int version, List<Block> list) {
        this.version = version;
        this.list = list;
    }
}
