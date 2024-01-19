package International_Trade_Union.entity.blockchain;

import lombok.Data;

@Data
public class DataShortBlockchainInformation {
    private long size;
    private boolean isValidation;
    private long hashCount;
    private double staking;
    private long epoch;
    private long transactions;

    public DataShortBlockchainInformation() {
    }

    public DataShortBlockchainInformation(long size, boolean isValidation, long hashCount, double staking, long epoch, long transactions) {
        this.size = size;
        this.isValidation = isValidation;
        this.hashCount = hashCount;
        this.staking = staking;
        this.epoch = epoch;
        this.transactions = transactions;

    }

    @Override
    public DataShortBlockchainInformation clone() throws CloneNotSupportedException {
        return new DataShortBlockchainInformation(size, isValidation, hashCount, staking, epoch, transactions);
    }
}

