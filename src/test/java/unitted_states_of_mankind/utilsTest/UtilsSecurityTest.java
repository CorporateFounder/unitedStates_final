package unitted_states_of_mankind.utilsTest;


import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.model.Keys;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@SpringBootTest
public class UtilsSecurityTest {

    @Test
    public void createKeyTest(){
        Keys keyPair = null;
        try{
            keyPair = UtilsSecurity.generateKeyPair();
        }
        catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException |
               InvalidKeySpecException e){
            e.printStackTrace();
        }
        Assert.assertNotNull(keyPair);
    }

    @Test
    public void convertByteToPrivateKey(){
        Keys keyPair = null;
        try{
            Base base = new Base58();
            keyPair = UtilsSecurity.generateKeyPair();
            PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(keyPair.getPrivkey()));
            byte[] bytesPrivateKey = privateKey.getEncoded();
            PrivateKey privateKeyAfterByte = UtilsSecurity.privateBytToPrivateKey(bytesPrivateKey);

            Assert.assertEquals(privateKey, privateKeyAfterByte);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void convertByteToPublicKey(){
        Keys keyPair = null;
        try{
            Base base = new Base58();
            keyPair = UtilsSecurity.generateKeyPair();
            PublicKey publicKey = UtilsSecurity.decodeKey(base.decode(keyPair.getPubkey()));
            byte[] bytesPrivateKey = publicKey.getEncoded();
            PublicKey publicKeyAfterByte = UtilsSecurity.publicByteToPublicKey(bytesPrivateKey);

            Assert.assertEquals(publicKey, publicKeyAfterByte);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyTest(){
        Keys keyPair = null;
        try{
            Base base = new Base58();
            keyPair = UtilsSecurity.generateKeyPair();
            PublicKey publicKey = UtilsSecurity.decodeKey(base.decode(keyPair.getPubkey()));
            DtoTransaction dtoTransaction = new DtoTransaction(
                    "germes",  "mercury",  100.0, 50, null,  0.0,
                    VoteEnum.YES);
            String expectedHex = dtoTransaction.toSign();
            PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(keyPair.getPrivkey()));
            byte[] sign = UtilsSecurity.sign(privateKey, expectedHex);
            dtoTransaction.setSign(sign);
            Assert.assertTrue(UtilsSecurity.verify(expectedHex, sign, publicKey));

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
        } catch (InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void verifyNotTrueTest(){
        Keys keyPair = null;
        try{
            Base base = new Base58();
            keyPair = UtilsSecurity.generateKeyPair();
            PublicKey publicKey = UtilsSecurity.decodeKey(base.decode(keyPair.getPubkey()));
            DtoTransaction dtoTransaction = new DtoTransaction(
                    "germes",  "mercury", 100.0, 50, null, 0.0,
                    VoteEnum.YES);
            String expectedHex =toString();
            PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(keyPair.getPrivkey()));
            byte[] sign = UtilsSecurity.sign(privateKey, expectedHex);
            dtoTransaction.setSign(sign);

            Assert.assertTrue(!UtilsSecurity.verify("false", sign, publicKey));

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
        } catch (InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void createKeyPairWithPrivkeyPubKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Base base = new Base58();
        Keys keyPair = UtilsSecurity.generateKeyPair();
        PublicKey publicKey = (BCECPublicKey) UtilsSecurity.decodeKey(base.decode(keyPair.getPubkey()));
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(keyPair.getPrivkey()));
        KeyPair keyPair1 = UtilsSecurity.createKeyPairWithPrivkeyPubKey(privateKey, publicKey);
        PublicKey publicKey1 = keyPair1.getPublic();
        PrivateKey privateKey1 = keyPair1.getPrivate();
        byte[] pub = publicKey.getEncoded();
        byte[] pub1 = publicKey1.getEncoded();
        byte[] priv = privateKey.getEncoded();
        byte[] priv1 = privateKey1.getEncoded();

        Assert.assertEquals(base.encode(pub), base.encode(pub1));
        Assert.assertEquals(base.encode(priv), base.encode(priv1));
    }

    @Test
    public void getComperessedPublicKey() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        Base base = new Base58();
        Keys keyPair = UtilsSecurity.generateKeyPair();

        DtoTransaction dtoTransaction = new DtoTransaction(
                "germes",   "mercury",  100.0, 50, null, 0.0, VoteEnum.YES);
        String expectedHex = dtoTransaction.toSign();
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(keyPair.getPrivkey()));
        byte[] sign = UtilsSecurity.sign(privateKey, expectedHex);
        dtoTransaction.setSign(sign);

        System.out.println("pubkey: " + keyPair.getPubkey());
        BCECPublicKey publicKey1 = (BCECPublicKey) UtilsSecurity.decodeKey(base.decode(keyPair.getPubkey()));
        System.out.println("pub1 key: " + base.encode(publicKey1.getEncoded()));
        Assert.assertTrue(UtilsSecurity.verify(expectedHex, sign, publicKey1));

        Assert.assertTrue(keyPair.getPubkey().length() < 46);

    }

}
