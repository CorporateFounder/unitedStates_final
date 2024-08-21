package International_Trade_Union.model;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

@Data
public class Account implements Cloneable {
    private String account;
    private BigDecimal digitalDollarBalance;
    private BigDecimal digitalStockBalance;
    private BigDecimal digitalStakingBalance;

    public Account(String account, BigDecimal digitalDollarBalance) {
        this(account, digitalDollarBalance, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public Account(String account, BigDecimal digitalDollarBalance, BigDecimal digitalStockBalance, BigDecimal digitalStakingBalance) {
        this.account = account;
        this.digitalDollarBalance = trimToScale(digitalDollarBalance);
        this.digitalStockBalance = trimToScale(digitalStockBalance);
        this.digitalStakingBalance = trimToScale(digitalStakingBalance);
    }

    public Account() {
    }

    private BigDecimal trimToScale(BigDecimal value) {
        if (value != null) {
            return value.setScale(8, RoundingMode.DOWN);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account1 = (Account) o;
        return getAccount().equals(account1.getAccount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccount());
    }

    private DtoTransaction sendMoney(String recipient, String privatekey, BigDecimal digitalDollar, BigDecimal digitalStock, Laws laws, BigDecimal minerRewards, VoteEnum voteEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, IOException, InvalidKeyException {
        DtoTransaction transaction = null;
        if (account.equals(recipient)) {
            System.out.printf("sender %s, recipient %s cannot be equals! Error!%n", account, recipient);
            return transaction;
        }

        if (digitalDollarBalance.compareTo(digitalDollar.add(minerRewards)) < 0) {
            System.out.println("sender don't have dollar");
            return transaction;
        }
        if (digitalStockBalance.compareTo(digitalStock) < 0) {
            System.out.println("sender don't have stock");
            return transaction;
        }
        if (digitalStakingBalance.compareTo(digitalDollar) < 0) {
            System.out.println("sender don't have staking");
            return transaction;
        } else {
            Base base = new Base58();
            PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(privatekey));
            transaction = new DtoTransaction(this.getAccount(), recipient, digitalDollar.doubleValue(), digitalStock.doubleValue(), laws, minerRewards.doubleValue(), voteEnum);
            byte[] signGold = UtilsSecurity.sign(privateKey, transaction.toSign());
            transaction.setSign(signGold);
        }

        return transaction;
    }

    public DtoTransaction send(String recipient, String privateKey, BigDecimal digitalDollar, BigDecimal digitalReputation, Laws laws, BigDecimal minerRewards, VoteEnum voteEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, IOException, NoSuchProviderException, InvalidKeyException {
        return sendMoney(recipient, privateKey, digitalDollar, digitalReputation, laws, minerRewards, voteEnum);
    }

    @Override
    public Account clone() throws CloneNotSupportedException {
        return new Account(
                this.account,
                this.digitalDollarBalance != null ? trimToScale(new BigDecimal(this.digitalDollarBalance.toString())) : null,
                this.digitalStockBalance != null ? trimToScale(new BigDecimal(this.digitalStockBalance.toString())) : null,
                this.digitalStakingBalance != null ? trimToScale(new BigDecimal(this.digitalStakingBalance.toString())) : null
        );
    }
}
