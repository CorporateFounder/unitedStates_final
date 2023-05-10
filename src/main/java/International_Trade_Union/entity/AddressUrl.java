package International_Trade_Union.entity;

import lombok.Data;

@Data
public class AddressUrl {
    private String address;

    public AddressUrl() {
    }

    public AddressUrl(String address) {
        this.address = address;
    }
}
