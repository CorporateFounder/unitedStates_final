package unitted_states_of_mankind;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static International_Trade_Union.controllers.BasisController.sendAllBlocksToStorage;
import static International_Trade_Union.setings.Seting.BLOCK_GENERATION_INTERVAL;
import static International_Trade_Union.setings.Seting.DIFFICULTY_ADJUSTMENT_INTERVAL;

@SpringBootTest
public class TestDifficulty {

    @Test
    public void sendBlock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Block latestBlock = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIDqiS0qpZAkuH/jHel8ATMKS4vdGsMQGLc291b8S8skZAiBr+EX+H+alBlcYh+0LmFi7cHjz4i7fzzrrOMpGev7vBQ==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"mrvciisgWFrD4iu8n6BJA7Fber7mB5bxkJ5PjNrAuDWZ\",\"digitalDollar\":120.0,\"digitalStockBalance\":120.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIEhkOUrUnnrTrOgJZp33WGXk7sSP9bdH49yMAsMXIsrVAiEAjoiUoARIe20Y+C+wa316cfyLfshPqyxPnXlHpsZ7eGE=\"}],\"previousHash\":\"00e6f91ede5e5002ac61964725a236584c4493335933b940775d6c683cec43f7\",\"minerAddress\":\"mrvciisgWFrD4iu8n6BJA7Fber7mB5bxkJ5PjNrAuDWZ\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":25,\"minerRewards\":0.0,\"hashCompexity\":4,\"timestamp\":1694386776,\"index\":24859,\"hashBlock\":\"04ce9419132041dc05d5b059f4aa01d5c6b1fcd313fd2bfb0dbea082f4691d9b\"}");

        Block test = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"25TjMCZzaQsRoGkJ61Nb8JFTVPiWN4GhSPrKFA3Y4g3WN\",\"digitalDollar\":400.0,\"digitalStockBalance\":400.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIH6jPXinUv60qjhnWccbxj1J3WO0w4qYau3JPBvrpKL8AiAu2jT2X09i3hkLVGe853Orbm+d8EGSjJcVug46wwMYIg==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCID3uKQVnp3CqHUJiLNeNLJUgmrA3DEdzxElKx7EPDZCZAiAkFNy0GPyTFAujInlReEdWd9aYMaxmnt1p5H1C6Ma/0A==\"}],\"previousHash\":\"000007dbfc74eabf4d7d06327f9cdac5f0fc47a9f0402a4d613fb04a948cfa80\",\"minerAddress\":\"25TjMCZzaQsRoGkJ61Nb8JFTVPiWN4GhSPrKFA3Y4g3WN\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":96756,\"minerRewards\":0.0,\"hashCompexity\":5,\"timestamp\":1689918281224,\"index\":20829,\"hashBlock\":\"00000d4b174a6ccf161ad92ec91dcf1fcbac4287a81c4053fe33083c5a5b143c\"}");
        Blockchain blockchain = Mining.getBlockchain(
                "C://testingBlock/",
                BlockchainFactoryEnum.ORIGINAL);


        List<Block> sends = new ArrayList<>();
        sends.add(latestBlock);
        sendAllBlocksToStorage(sends);
    }
    @Test
    public void testDifficulty() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Block latestBlock = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIDqiS0qpZAkuH/jHel8ATMKS4vdGsMQGLc291b8S8skZAiBr+EX+H+alBlcYh+0LmFi7cHjz4i7fzzrrOMpGev7vBQ==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"mrvciisgWFrD4iu8n6BJA7Fber7mB5bxkJ5PjNrAuDWZ\",\"digitalDollar\":120.0,\"digitalStockBalance\":120.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIEhkOUrUnnrTrOgJZp33WGXk7sSP9bdH49yMAsMXIsrVAiEAjoiUoARIe20Y+C+wa316cfyLfshPqyxPnXlHpsZ7eGE=\"}],\"previousHash\":\"00e6f91ede5e5002ac61964725a236584c4493335933b940775d6c683cec43f7\",\"minerAddress\":\"mrvciisgWFrD4iu8n6BJA7Fber7mB5bxkJ5PjNrAuDWZ\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":25,\"minerRewards\":0.0,\"hashCompexity\":4,\"timestamp\":1694386776,\"index\":24858,\"hashBlock\":\"04ce9419132041dc05d5b059f4aa01d5c6b1fcd313fd2bfb0dbea082f4691d9b\"}");


        Blockchain blockchain = Mining.getBlockchain(
                "C://testingBlock/",
                BlockchainFactoryEnum.ORIGINAL);
        List<Block> blocks = blockchain.getBlockchainList();


        long countBlock = blocks.get(blocks.size()-1).getIndex()-blocks.get(0).getIndex();
        long startTime = blocks.get(0).getTimestamp().getTime();
        long endTime = blocks.get(blocks.size()-1).getTimestamp().getTime();


        Long result = blocks.get(blocks.size()-1).getTimestamp().toInstant().until(blocks
        .get(0).getTimestamp().toInstant(), ChronoUnit.MINUTES);
        System.out.println("minutes spent finding all blocks: " + result);
        System.out.println("startTime: " + startTime);
        System.out.println("endTime: " + endTime);
        System.out.println("how many blocks: " + countBlock);
        System.out.println("last diff: " + latestBlock.getHashCompexity());
        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
        System.out.println("actualTime: " + actualTime);

        long difficulty_adjustment_interval = 288;



        long newDiff = UtilsDIfficult.getAdjustedDifficultyMedian(latestBlock,
                blocks, difficulty_adjustment_interval, DIFFICULTY_ADJUSTMENT_INTERVAL);
        System.out.println("median: " + newDiff);

        newDiff = UtilsDIfficult.getAdjustedDifficulty(latestBlock,
                blocks, difficulty_adjustment_interval, DIFFICULTY_ADJUSTMENT_INTERVAL);
        System.out.println("weight: " + newDiff);
        Timestamp prev = blocks.get(0).getTimestamp();
        Timestamp curent = null;
        for (int i = 1; i < blocks.size(); i++) {
            System.out.println("index: " + blocks.get(i).getIndex());
            Block block = blocks.get(i);
            curent = block.getTimestamp();
           if(prev.getTime() < curent.getTime()){
               System.out.println(" timing");
           }

            result = curent.toInstant().until(prev.toInstant(), ChronoUnit.SECONDS);
           prev = curent;
            System.out.println("current time: " + curent);
            System.out.println("result: " + result);
            if ( !blocks.get(i).getHashBlock().equals(blocks.get(i).hashForTransaction())) {
                System.out.println("wrong ");
            }else {
                System.out.println("true hash");
            }
            String target = BlockchainDifficulty.calculateTarget(block.getHashCompexity());
            if (!UtilsUse.chooseComplexity(block.getHashBlock(), block.getHashCompexity(), block.getIndex(), target)) {
                System.out.println("does't start hash with 0");

                System.out.println("this block hash: " + block.getHashBlock());
                if(block.getIndex() >= Seting.NEW_START_DIFFICULT)
                    BlockchainDifficulty.printBinary(block.getHashBlock().getBytes());


            }
            System.out.println("block diff: " + block.getHashCompexity());

//            System.out.println("********************************************");
//            UtilsDIfficult.printBitSet(block.getHashBlock().getBytes());
        }
    }




    private static final int MOVING_AVERAGE_WINDOW = 288;
    public static final int MAX_DIFFICULTY = 10000;

    public static int getAdjustedDifficulty(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL) {
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        double percentChange = 1.05; // Use smaller percentage change
        int changeLimit = 5; // Limit change amount

        long expectedTime = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long actualTime = latestBlock.getTimestamp().getTime() - prevAdjustmentBlock.getTimestamp().getTime();

        double movingAverageRatio = calculateMovingAverageRatio(blocks); // Calculate moving average
        double adjustedDifficulty = prevAdjustmentBlock.getHashCompexity();

        if (actualTime < expectedTime / percentChange) {
            adjustedDifficulty += Math.min(changeLimit, (adjustedDifficulty * (expectedTime - actualTime) / expectedTime) * movingAverageRatio);
        } else if (actualTime > expectedTime * percentChange) {
            adjustedDifficulty -= Math.min(changeLimit, (adjustedDifficulty * (actualTime - expectedTime) / expectedTime) * movingAverageRatio);
        }

        // Add bounds checking
        return (int) Math.max(1, Math.min(adjustedDifficulty, MAX_DIFFICULTY));
    }

    private static double calculateMovingAverageRatio(List<Block> blocks) {
        List<Long> blockTimes = new ArrayList<>();

        for (int i = blocks.size() - 1; i >= Math.max(0, blocks.size() - MOVING_AVERAGE_WINDOW); i--) {
            Block block = blocks.get(i);
            blockTimes.add(block.getTimestamp().getTime());
        }

        double medianTime = calculateMedian(blockTimes);
        long expectedTime = BLOCK_GENERATION_INTERVAL * MOVING_AVERAGE_WINDOW;

        return medianTime / expectedTime;
    }

    private static double calculateMedian(List<Long> values) {
        Collections.sort(values);
        int size = values.size();
        int middle = size / 2;

        if (size % 2 == 1) {
            return values.get(middle);
        } else {
            return (values.get(middle - 1) + values.get(middle)) / 2.0;
        }
    }
}
