package International_Trade_Union.vote;

import lombok.Data;

@Data
public class Vote {
    private String address;
    private double vote;

    public Vote() {
    }

    public Vote(String address, double vote) {
        this.address = address;
        this.vote = vote;
    }
}
