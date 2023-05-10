package International_Trade_Union.simulation;

import lombok.Data;

@Data
public class AccountSimulation {
    private String publicKey;
    private String privateKey;
    private double digitalDollarBalance;
    private double digitalReputationBalance;


    public AccountSimulation(String publicKey, String privateKey, double digitalDollarBalance, double digitalReputationBalance) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.digitalDollarBalance = digitalDollarBalance;
        this.digitalReputationBalance = digitalReputationBalance;
    }

    public AccountSimulation(String publicKey, double digitalDollarBalance, double digitalReputationBalance) {
        this.publicKey = publicKey;
        this.digitalDollarBalance = digitalDollarBalance;
        this.digitalReputationBalance = digitalReputationBalance;
    }

    public AccountSimulation() {
    }


}
