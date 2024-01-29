package International_Trade_Union.model;

import lombok.Data;

@Data
public class LiteVersionWiner {
    private long index;
    private String address;
    private String hash;
    private int countTransactions;
    private double staking;
    private long bigRandom;
    private long diff;

    public LiteVersionWiner() {
    }

    public LiteVersionWiner(long index, String address, String hash, int countTransactions, double staking, int bigRandom, long diff) {
        this.index = index;
        this.address = address;
        this.hash = hash;
        this.countTransactions = countTransactions;
        this.staking = staking;
        this.bigRandom = bigRandom;
        this.diff = diff;
    }
}
