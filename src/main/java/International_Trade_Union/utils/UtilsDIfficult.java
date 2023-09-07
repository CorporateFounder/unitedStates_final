package International_Trade_Union.utils;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;

import java.util.List;

public class UtilsDIfficult {



    /**получить сложность*/
    public static int getAdjustedDifficulty(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);


        double percentGrow = 2.6;
        double percentDown = 1.3;
        if(latestBlock.getIndex() > Seting.NEW_START_ADJUSTMENT){
            percentDown = 2.0;
            percentGrow = 2.0;
        }

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



}
