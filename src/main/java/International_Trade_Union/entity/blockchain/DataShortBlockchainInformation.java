package International_Trade_Union.entity.blockchain;

import lombok.Data;

@Data
public class DataShortBlockchainInformation {
    private long size;
    private boolean isValidation;
    private long hashCount;
    private double staking;
    private long transactions;
    private int bigRandomNumber;

    public DataShortBlockchainInformation() {
    }

    public DataShortBlockchainInformation(long size, boolean isValidation, long hashCount, double staking,  long transactions, int bigRandomNumber) {
        this.size = size;
        this.isValidation = isValidation;
        this.hashCount = hashCount;
        this.staking = staking;
        this.transactions = transactions;
        this.bigRandomNumber = bigRandomNumber;

    }

    @Override
    public DataShortBlockchainInformation clone() throws CloneNotSupportedException {
        return new DataShortBlockchainInformation(size, isValidation, hashCount, staking,  transactions, bigRandomNumber);
    }
}

