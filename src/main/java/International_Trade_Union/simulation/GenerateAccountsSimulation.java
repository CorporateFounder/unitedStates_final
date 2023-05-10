package International_Trade_Union.simulation;



import International_Trade_Union.model.Keys;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class GenerateAccountsSimulation {
    public static List<AccountSimulation> accountSimulations(int count) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Keys keyPair = null;
        List<AccountSimulation> list = new ArrayList<>();
        Base base = new Base58();
        for (int i = 0; i < count; i++) {
            keyPair = UtilsSecurity.generateKeyPair();
            String pub =keyPair.getPubkey();
            String priv = keyPair.getPrivkey();
            AccountSimulation accountSimulation = new AccountSimulation(pub, priv, 0.0, 0.0);
            list.add(accountSimulation);
        }
        return list;

    }
}
