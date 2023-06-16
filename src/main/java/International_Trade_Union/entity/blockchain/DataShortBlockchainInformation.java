package International_Trade_Union.entity.blockchain;

import lombok.Data;

@Data
public class DataShortBlockchainInformation {
    private long size;
    private boolean isValidation;

    public DataShortBlockchainInformation() {
    }

    public DataShortBlockchainInformation(long size, boolean isValidation) {
        this.size = size;
        this.isValidation = isValidation;
    }
}

