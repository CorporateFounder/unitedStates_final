package International_Trade_Union.entity.DtoTransaction;

import International_Trade_Union.controllers.BasisController;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;


import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;


@JsonAutoDetect
@Data
public class DtoTransaction implements Comparable<DtoTransaction>{
    private String sender;
    private String customer;
    private double digitalDollar;
    private double digitalStockBalance;
    private Laws laws;
    private double bonusForMiner;
    private VoteEnum voteEnum;
    private byte[] sign;



    public DtoTransaction(String sender, String customer, double digitalDollar, double digitalStockBalance, Laws laws, double bonusForMiner, VoteEnum voteEnum) {
        this.sender = sender;
        this.customer = customer;
        this.digitalDollar = digitalDollar;
        this.digitalStockBalance = digitalStockBalance;
        this.laws = laws;
        this.bonusForMiner = bonusForMiner;
        this.voteEnum = voteEnum;
    }

    public DtoTransaction() {
    }

    //TODO возможно стоит перевести проверку подписи в отдельный utils, под вопросом!!
    public boolean verify() throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        Base base = new Base58();
        byte[] pub = base.decode(sender);
        BCECPublicKey publicKey = (BCECPublicKey) UtilsSecurity.decodeKey(pub);
//        PublicKey publicKey = UtilsSecurity.publicByteToPublicKey(pub);
        String sha = sender + customer + digitalDollar + digitalStockBalance + laws + bonusForMiner;
        sha = UtilsUse.sha256hash(sha);
        if(sender.isBlank() || customer.isBlank() || digitalDollar < 0 || digitalStockBalance < 0 || bonusForMiner < 0 || laws == null){
            System.out.println("wrong dto transaction sender or customer blank? or dollar, reputation or reward less then 0");
            return false;
        }
        if(Seting.BASIS_ADDRESS.equals(publicKey))
            return true;
        return UtilsSecurity.verify(sha, sign, publicKey);
    }

    public String toSign(){
        String sha = sender + customer + digitalDollar + digitalStockBalance + laws + bonusForMiner;
        return UtilsUse.sha256hash(sha);
    }

//    public String hashForBlock() throws IOException {
//        return UtilsUse.sha256hash(jsonString());
//    }

    public String jsonString() throws IOException {
        return UtilsJson.objToStringJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DtoTransaction)) return false;
        DtoTransaction that = (DtoTransaction) o;
        return Double.compare(that.getDigitalDollar(), getDigitalDollar()) == 0 && Double.compare(that.getDigitalStockBalance(), getDigitalStockBalance()) == 0 && Double.compare(that.getBonusForMiner(), getBonusForMiner()) == 0 && getSender().equals(that.getSender()) && getCustomer().equals(that.getCustomer()) && getLaws().equals(that.getLaws()) && getVoteEnum() == that.getVoteEnum() && Arrays.equals(getSign(), that.getSign());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getSender(), getCustomer(), getDigitalDollar(), getDigitalStockBalance(), getLaws(), getBonusForMiner(), getVoteEnum());
        result = 31 * result + Arrays.hashCode(getSign());
        return result;
    }

    @Override
    public int compareTo(DtoTransaction o) {
        return 0;
    }
}
