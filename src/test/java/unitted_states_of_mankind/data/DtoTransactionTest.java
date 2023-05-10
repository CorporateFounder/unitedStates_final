package unitted_states_of_mankind.data;


import International_Trade_Union.vote.Laws;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.model.Keys;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@SpringBootTest
public class DtoTransactionTest {

    @Test
    public void transactionTest(){
        Keys keyPair = null;

        try {
            Base base = new Base58();
            keyPair = UtilsSecurity.generateKeyPair();


            DtoTransaction dtoTransaction = new DtoTransaction(keyPair.getPubkey(), Seting.ADDRESS_FOUNDER_TEST,
                    100.0, 50.0, new Laws(),0.0, VoteEnum.YES);
            PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(keyPair.getPrivkey()));
            byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
            dtoTransaction.setSign(sign);
            System.out.println("dto transaction: " + dtoTransaction);
            Assert.assertTrue(dtoTransaction.verify());

        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}
