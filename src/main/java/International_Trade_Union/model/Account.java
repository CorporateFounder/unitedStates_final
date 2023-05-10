package International_Trade_Union.model;


import lombok.Data;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;


@Data
public class Account {
    private String account;
    private double digitalDollarBalance;
    private double digitalStockBalance;


    public Account(String account, double digitalDollarBalance) {
        this(account, digitalDollarBalance, 0.0);

    }

    public Account(String account, double digitalDollarBalance, double digitalStockBalance) {
        this.account = account;
        this.digitalDollarBalance = digitalDollarBalance;
        this.digitalStockBalance = digitalStockBalance;
    }

    public Account() {
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

    private DtoTransaction sendMoney(String recipient, String privatekey, double digitalDollar, double digitalStock, Laws laws, double minerRewards, VoteEnum voteEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, IOException, InvalidKeyException {

        DtoTransaction transaction = null;
        if (account.equals(recipient)){
            System.out.println("sender %s, recipient %s cannot be equals! Error!".format(account,recipient));
            return transaction;
        }

            if(digitalDollarBalance < digitalDollar + minerRewards  ){
                System.out.println("sender don't have digitalDollar");
                return transaction;
            }
            if(digitalStockBalance < digitalStock){
                System.out.println("sender don't have digitalReputation");
                return transaction;
            }
            else{
                Base base = new Base58();
                PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(privatekey));
                 transaction = new DtoTransaction(this.getAccount(), recipient, digitalDollar, digitalStock, laws, minerRewards, voteEnum);
                byte[] signGold = UtilsSecurity.sign(privateKey, transaction.toSign());
                transaction.setSign(signGold);
            }

       return transaction;
    }

//      recipient - получатель
//      gold сумма отправки, last Block - это послдний блок.
    public DtoTransaction send(String recipient, String privateKey, double digitalDollar, double digitalReputation, Laws laws,  double minerRewards, VoteEnum voteEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, IOException, NoSuchProviderException, InvalidKeyException {
         return sendMoney(recipient,privateKey, digitalDollar, digitalReputation, laws, minerRewards, voteEnum);
    }





}
