package International_Trade_Union.utils;

import International_Trade_Union.entity.blockchain.block.Block;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class UtilsDIfficult {
public  static void printBitSet(byte[] bytes) {
        BitSet bits = BitSet.valueOf(bytes);

        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                System.out.print(1);
            } else {
                System.out.print(0);
            }
        }

        System.out.println();
    }


    /**получить сложность*/

    public static long getAdjustedDifficulty(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);

        double percentGrow = 2.0;
        double percentDown = 2.0;

        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestBlock.getTimestamp().getTime() - prevAdjustmentBlock.getTimestamp().getTime();

        if(timeTaken < timeExpected / percentGrow){
            return prevAdjustmentBlock.getHashCompexity() + 1;
        }else if(timeTaken > timeExpected * percentDown){
            return prevAdjustmentBlock.getHashCompexity() - 1;
        }else {

            return prevAdjustmentBlock.getHashCompexity();
        }
    }

    //    int DIFFICULTY_ADJUSTMENT_INTERVAL = 288
    //    long BLOCK_GENERATION_INTERVAL = 150000

    //***********************************************************************************
    //V28_CHANGE_ALGORITH_DIFF_INDEX
    public static long v28_changeAlgorith_diff(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        // Медианное время от индекса 0 до 10 из blocks
        List<Long> adjustmentBlockTimes = new ArrayList<>();
        for (int i = 0; i < Math.min(DIFFICULTY_ADJUSTMENT_INTERVAL, blocks.size()); i++) {
            adjustmentBlockTimes.add(blocks.get(i).getTimestamp().getTime());
        }
        Collections.sort(adjustmentBlockTimes);
        long prevTime = adjustmentBlockTimes.get(adjustmentBlockTimes.size() / 2);

        // Включает время latestBlock и 10 последних индексов из blocks
        List<Long> latestBlockTimes = new ArrayList<>();
        latestBlockTimes.add(latestBlock.getTimestamp().getTime());
        for (int i = Math.max(blocks.size() - 30, 0); i < blocks.size(); i++) {
            latestBlockTimes.add(blocks.get(i).getTimestamp().getTime());
        }
        Collections.sort(latestBlockTimes);
        long latestTime = latestBlockTimes.get(latestBlockTimes.size() / 2);


        double percentGrow = 2.3;
        double percentDown = 1.6;


        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestTime - prevTime;


        if(timeTaken < timeExpected / percentGrow){
            return prevAdjustmentBlock.getHashCompexity() + 1;
        }else if(timeTaken > timeExpected * percentDown){
            return prevAdjustmentBlock.getHashCompexity() - 1;
        }else {
            return prevAdjustmentBlock.getHashCompexity();
        }
    }
    //***********************************************************************************

    public static long v2getAdjustedDifficultyMedian(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        // Медианное время от индекса 0 до 10 из blocks
        List<Long> adjustmentBlockTimes = new ArrayList<>();
        for (int i = 0; i < Math.min(DIFFICULTY_ADJUSTMENT_INTERVAL, blocks.size()); i++) {
            adjustmentBlockTimes.add(blocks.get(i).getTimestamp().getTime());
        }
        Collections.sort(adjustmentBlockTimes);
        long prevTime = adjustmentBlockTimes.get(adjustmentBlockTimes.size() / 2);

        // Включает время latestBlock и 10 последних индексов из blocks
        List<Long> latestBlockTimes = new ArrayList<>();
        latestBlockTimes.add(latestBlock.getTimestamp().getTime());
        for (int i = Math.max(blocks.size() - 30, 0); i < blocks.size(); i++) {
            latestBlockTimes.add(blocks.get(i).getTimestamp().getTime());
        }
        Collections.sort(latestBlockTimes);
        long latestTime = latestBlockTimes.get(latestBlockTimes.size() / 2);


        double percentGrow = 2.7;
        double percentDown = 1.6;


        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestTime - prevTime;


        if(timeTaken < timeExpected / percentGrow){
            return prevAdjustmentBlock.getHashCompexity() + 1;
        }else if(timeTaken > timeExpected * percentDown){
            return prevAdjustmentBlock.getHashCompexity() - 1;
        }else {
            return prevAdjustmentBlock.getHashCompexity();
        }
    }
    public static long getAdjustedDifficultyMedian(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
        // Медианное время от индекса 0 до 10 из blocks
        List<Long> adjustmentBlockTimes = new ArrayList<>();
        for (int i = 0; i < Math.min(DIFFICULTY_ADJUSTMENT_INTERVAL, blocks.size()); i++) {
            adjustmentBlockTimes.add(blocks.get(i).getTimestamp().getTime());
        }
        Collections.sort(adjustmentBlockTimes);
        long prevTime = adjustmentBlockTimes.get(adjustmentBlockTimes.size() / 2);

        // Включает время latestBlock и 10 последних индексов из blocks
        List<Long> latestBlockTimes = new ArrayList<>();
        latestBlockTimes.add(latestBlock.getTimestamp().getTime());
        for (int i = Math.max(blocks.size() - 30, 0); i < blocks.size(); i++) {
            latestBlockTimes.add(blocks.get(i).getTimestamp().getTime());
        }
        Collections.sort(latestBlockTimes);
        long latestTime = latestBlockTimes.get(latestBlockTimes.size() / 2);


        double percentGrow = 2.6;
        double percentDown = 1.6;


        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestTime - prevTime;

        if(timeTaken < timeExpected / percentGrow){
            return prevAdjustmentBlock.getHashCompexity() + 1;
        }else if(timeTaken > timeExpected * percentDown){
            return prevAdjustmentBlock.getHashCompexity() - 1;
        }else {
            return prevAdjustmentBlock.getHashCompexity();
        }
    }



}
