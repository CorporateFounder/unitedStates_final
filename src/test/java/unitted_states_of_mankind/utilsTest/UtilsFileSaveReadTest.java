package unitted_states_of_mankind.utilsTest;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.simulation.AccountSimulation;
import International_Trade_Union.simulation.GenerateAccountsSimulation;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsFileSaveRead;
import International_Trade_Union.utils.UtilsJson;
import unitted_states_of_mankind.networkTest.TransactionsTest;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@SpringBootTest
public class UtilsFileSaveReadTest {
    private static Blockchain blockchain;

    static {
        try {
            blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

}
