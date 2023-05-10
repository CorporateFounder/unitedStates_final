package unitted_states_of_mankind.utilsTest;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import unitted_states_of_mankind.entityTest.blockchainTest.BlockchainRead;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;

@SpringBootTest
public class UtilsBlockDifficultTest {
    //занимает около 19 минут
    @Ignore
//    @Test
    public void difficultyTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        int week = (int) (Seting.COUNT_BLOCK_IN_DAY * 1) + 1;
        int seccond = 2 * 1000;
        int DIFFICULTY_ADJUSTMENT_INTERVAL_TEST = 10;
        System.out.println("difficulty test secnond: " + seccond);
        Timestamp before = new Timestamp(System.currentTimeMillis());
        Blockchain blockchain = BlockchainRead.getBlockchain(week, 1,  BlockchainFactoryEnum.TEST,
                DIFFICULTY_ADJUSTMENT_INTERVAL_TEST,
                seccond);
        System.out.println("validation: "+blockchain.validatedBlockchain());
        System.out.println("blockchain size: " + blockchain.sizeBlockhain());

        Block next = blockchain.getBlock(blockchain.sizeBlockhain() - 1);
        Block prev = blockchain.getBlock(blockchain.sizeBlockhain() - 2);
        long differentTime = next.getTimestamp().getTime() - prev.getTimestamp().getTime();
        System.out.println("differentTime: " + differentTime);
        Timestamp timestamp = new Timestamp(differentTime);
        System.out.println("timestamp: " + timestamp);
        System.out.println("difficulty: " + next.getHashCompexity());
        Timestamp after = new Timestamp(System.currentTimeMillis());
        System.out.println("blockchain: interval: " + blockchain.getBLOCK_GENERATION_INTERVAL());

        differentTime = after.getTime() - before.getTime();
        Timestamp differentTimestamp = new Timestamp(differentTime);
        System.out.println("differentTimestamp: " + differentTimestamp);
        Assert.assertTrue(next.getHashCompexity() > 1);
        Assert.assertTrue(blockchain.validatedBlockchain());

    }
}
