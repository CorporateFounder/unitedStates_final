package International_Trade_Union.config;


import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.setings.Seting;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class BLockchainFactory {

    /**Возвращает параметры блока включая интервал добычи, адрес основателя и как часто происходит изменения сложности.
     * Returns block parameters including mining interval, founder's address, and how often difficulty changes occur.*/
    public static Blockchain getBlockchain(BlockchainFactoryEnum factoryEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        switch (factoryEnum){
            case TEST:
                return new Blockchain(
                        Seting.BLOCK_GENERATION_INTERVAL_TEST,
                        Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST,
                        Seting.INTERVAL_TARGET_TEST,
                        Seting.ADDRESS_FOUNDER_TEST);

            case ORIGINAL:
                return new Blockchain(
                        Seting.BLOCK_GENERATION_INTERVAL,
                        Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                        Seting.INTERVAL_TARGET,
                        Seting.ADDRESS_FOUNDER
                );
            default: return null;
        }
    }
}
