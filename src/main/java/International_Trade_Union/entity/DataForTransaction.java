package International_Trade_Union.entity;

import lombok.Data;

@Data
public class DataForTransaction {

    private int numberBlock;

    private String sender;
    private String customer;
    private double digitalDollar;
    private double digitalStockBalance;
    private double bonusForMiner;
    private String voteEnum;

    public DataForTransaction() {
    }

    public DataForTransaction(
            int numberBlock,
            String sender,
            String customer,
            double digitalDollar,
            double digitalStockBalance,
            double bonusForMiner,
            String voteEnum) {
        this.numberBlock = numberBlock;
        this.sender = sender;
        this.customer = customer;
        this.digitalDollar = digitalDollar;
        this.digitalStockBalance = digitalStockBalance;
        this.bonusForMiner = bonusForMiner;
        this.voteEnum = voteEnum;

    }
}
