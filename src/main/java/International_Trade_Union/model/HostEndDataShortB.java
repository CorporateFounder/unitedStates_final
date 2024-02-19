package International_Trade_Union.model;

import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import lombok.Data;

@Data
public class HostEndDataShortB {
    private String host;
    private DataShortBlockchainInformation dataShortBlockchainInformation;

    public HostEndDataShortB() {
    }

    public HostEndDataShortB(String host, DataShortBlockchainInformation dataShortBlockchainInformation) {
        this.host = host;
        this.dataShortBlockchainInformation = dataShortBlockchainInformation;
    }
}
