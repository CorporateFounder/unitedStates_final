package International_Trade_Union.model;

import lombok.Data;

@Data
public class Keys {
    private String pubkey;
    private String privkey;

    public Keys(String pubkey, String privkey) {
        this.pubkey = pubkey;
        this.privkey = privkey;
    }
}
