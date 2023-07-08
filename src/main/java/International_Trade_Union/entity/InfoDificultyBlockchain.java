package International_Trade_Union.entity;

import lombok.Data;

@Data
public class InfoDificultyBlockchain {
    private long diffultyOneBlock;
    private long difficultyAllBlockchain;

    public InfoDificultyBlockchain(long diffultyOneBlock, long difficultyAllBlockchain) {
        this.diffultyOneBlock = diffultyOneBlock;
        this.difficultyAllBlockchain = difficultyAllBlockchain;
    }

    public InfoDificultyBlockchain() {
    }
}
