package International_Trade_Union.utils;

import International_Trade_Union.entity.blockchain.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UtilsDIfficult {

    public static int difficultyBing(List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL) {
        int difficulty = 1;
        Block latestBlock = blocks.get(blocks.size() - 1);
        if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
            difficulty = getAdjustedDifficultyBing(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
            System.out.println("difficulty: change dificulty: " + difficulty);
        } else {
            difficulty = latestBlock.getHashCompexity();
        }

        return difficulty == 0 ? 1 : difficulty;
    }

    private static int getAdjustedDifficultyBing(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL) {
        List<Long> blockTimes = new ArrayList<>();
        for (int i = blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL; i < blocks.size(); i++) {
            blockTimes.add(blocks.get(i).getTimestamp().getTime());
        }
        Collections.sort(blockTimes);
        long medianTime = blockTimes.get(blockTimes.size() / 2);

        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);

        if (medianTime < BLOCK_GENERATION_INTERVAL) {
            return prevAdjustmentBlock.getHashCompexity() + 1;
        } else if (medianTime > BLOCK_GENERATION_INTERVAL) {
            return prevAdjustmentBlock.getHashCompexity() - 1;
        } else {
            return prevAdjustmentBlock.getHashCompexity();
        }
    }

    /**получить сложность*/
    public static int getAdjustedDifficulty(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);

        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestBlock.getTimestamp().getTime() - prevAdjustmentBlock.getTimestamp().getTime();

        if(timeTaken < timeExpected / 2){
            return prevAdjustmentBlock.getHashCompexity() + 1;
        }else if(timeTaken > timeExpected * 2){
            return prevAdjustmentBlock.getHashCompexity() - 1;
        }else {
            return prevAdjustmentBlock.getHashCompexity();
        }
    }


    public static int getAdjustedDifficulty2(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        double percent = 2.3;
        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestBlock.getTimestamp().getTime() - prevAdjustmentBlock.getTimestamp().getTime();

        if(timeTaken < timeExpected / percent){
            return prevAdjustmentBlock.getHashCompexity() + 1;
        }else if(timeTaken > timeExpected * percent){
            return prevAdjustmentBlock.getHashCompexity() - 1;
        }else {
            return prevAdjustmentBlock.getHashCompexity();
        }
    }
}
