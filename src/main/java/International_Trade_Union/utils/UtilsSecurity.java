package International_Trade_Union.utils;



import International_Trade_Union.model.Keys;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;

//https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html
//https://stackoverflow.com/questions/8451205/create-privatekey-and-publickey-having-a-byte-array-encoded-in-base-64
public class UtilsSecurity {
    private static final String SPEC = "secp256k1";
    private static final String ALGO_ECDSA = "ECDSA";
    private static final String PROVIDER = "BC";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    public static Keys generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, InvalidKeySpecException {
       Base base = new Base58();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SPEC);
        KeyPairGenerator g = KeyPairGenerator.getInstance(ALGO_ECDSA, PROVIDER);
        g.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = g.generateKeyPair();
        String pub = base.encode(UtilsSecurity.compressed(keyPair.getPublic().getEncoded()));
        String priv = base.encode(keyPair.getPrivate().getEncoded());
        Keys keys = new Keys(pub, priv);
        return keys;
    }

    public static PrivateKey privateBytToPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory generator = KeyFactory.getInstance(ALGO_ECDSA, PROVIDER);
        return generator.generatePrivate(privateKeySpec);
    }

    public static PublicKey publicByteToPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        //new X509EncodedKeySpec
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytes);
        KeyFactory generator = KeyFactory.getInstance(ALGO_ECDSA, PROVIDER);
        return generator.generatePublic(publicKeySpec);
    }


    public static KeyPair createKeyPairWithPrivkeyPubKey(PrivateKey privateKey, PublicKey publicKey){
        return new KeyPair(publicKey, privateKey);
    }


    public static byte[] sign(PrivateKey privateKey, String hex) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException {
        Signature signature = Signature.getInstance(ALGO_ECDSA, PROVIDER);
        signature.initSign(privateKey);
        signature.update(hex.getBytes(StandardCharsets.UTF_8));
        byte[] signByte = signature.sign();
        return signByte;
    }

    public static boolean verify(String sha256message, byte[] sign, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(ALGO_ECDSA, PROVIDER);
        signature.initVerify(publicKey);
        signature.update(sha256message.getBytes(StandardCharsets.UTF_8));
        return signature.verify(sign);
    }


    public static byte[] compressed(byte[] pub) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeySpecException {

        // === NOT PART OF THE CODE, JUST GETTING TEST VECTOR ===
        PublicKey publicKey = UtilsSecurity.publicByteToPublicKey(pub);

        ECPublicKey key = (ECPublicKey) publicKey;
        byte[] x = key.getW().getAffineX().toByteArray();
        byte[] y = key.getW().getAffineY().toByteArray();

        // assumes that x and y are (unsigned) big endian encoded
        BigInteger xbi = new BigInteger(1, x);
        BigInteger ybi = new BigInteger(1, y);
        X9ECParameters x9 = ECNamedCurveTable.getByName(SPEC);
        ASN1ObjectIdentifier oid = ECNamedCurveTable.getOID(SPEC);
        ECCurve curve = x9.getCurve();
        ECPoint point = curve.createPoint(xbi, ybi);
        ECNamedDomainParameters dParams = new ECNamedDomainParameters(oid,
                x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
        ECPublicKeyParameters pubKey = new ECPublicKeyParameters(point, dParams);
        System.out.println(pubKey);

        // some additional encoding tricks
        byte[] compressed = point.getEncoded(true);
        return compressed;
    }
    public static ECPublicKey decodeKey(byte[] encoded) {

        ECNamedCurveParameterSpec params = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(SPEC);
        org.bouncycastle.jce.spec.ECPublicKeySpec keySpec = new ECPublicKeySpec(params.getCurve().decodePoint(encoded), params);
        return new BCECPublicKey(ALGO_ECDSA, keySpec, BouncyCastleProvider.CONFIGURATION);
    }
}
