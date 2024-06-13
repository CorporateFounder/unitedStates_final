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
import java.math.BigInteger;
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


import static International_Trade_Union.setings.Seting.BLOCK_GENERATION_INTERVAL;
import static International_Trade_Union.setings.Seting.DIFFICULTY_ADJUSTMENT_INTERVAL;

@SpringBootTest
public class TestDifficulty {




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
