package International_Trade_Union.model;

import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class CreateAccount {
    public static Map<String, String> create() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        Map<String, String> create = new HashMap<>();
        Base base = new Base58();
        Keys keyPair = UtilsSecurity.generateKeyPair();
        String pubkey = keyPair.getPubkey();
        String privKey = keyPair.getPrivkey();
        System.out.println("pubkey: " + pubkey);
        System.out.println("privKey: " + privKey);
        create.put("pubKey", pubkey);
        create.put("privKey", privKey);
        return create;
    }
}
