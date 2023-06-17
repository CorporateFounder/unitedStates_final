package International_Trade_Union.entity.blockchain;

import lombok.Data;

@Data
public class DataShortBlockchainInformation {
    private long size;
    private boolean isValidation;
    private long hashCount;

    public DataShortBlockchainInformation() {
    }

    public DataShortBlockchainInformation(long size, boolean isValidation, long hashCount) {
        this.size = size;
        this.isValidation = isValidation;
        this.hashCount = hashCount;
    }
}

