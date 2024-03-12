package International_Trade_Union.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
public class MyHost {
    public MyHost() {
    }

    public MyHost(String host, String nameServer, String pubkey) {
        this.host = host;
        this.nameServer = nameServer;
        this.pubkey = pubkey;
    }

    private String host;

    private String nameServer;
    private String pubkey;

}
