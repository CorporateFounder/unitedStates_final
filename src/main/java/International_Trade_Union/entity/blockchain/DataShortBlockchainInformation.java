package International_Trade_Union.entity.blockchain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DataShortBlockchainInformation {
    private long size;
    private boolean isValidation;
    private long hashCount;
    private BigDecimal staking;
    private long transactions;
    private int bigRandomNumber;

    public DataShortBlockchainInformation() {
    }

    public DataShortBlockchainInformation(long size, boolean isValidation, long hashCount, BigDecimal staking,  long transactions, int bigRandomNumber) {
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

