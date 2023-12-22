package unitted_states_of_mankind;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DAADifficultyAdjustement {
    public static void main(String[] args) {
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < 288; i++) {
            Block block = new Block();
            block.setTime(400l);
            block.setDifficulty(BigDecimal.valueOf(1));
            blocks.add(block);
        }
        BigDecimal bigDecimal = getNextDifficulty(blocks);
        System.out.println(bigDecimal);

    }

    private static final long TARGET_BLOCK_INTERVAL = 150;

    public static BigDecimal getNextDifficulty(List<Block> last144Blocks) {

        long sumBlockIntervals = 0;

        for (int i = 0; i < 143; i++) {
            long interval = last144Blocks.get(i+1).getTime()- last144Blocks.get(i).getTime();
            sumBlockIntervals += interval;
        }

        long avgBlockInterval = sumBlockIntervals / 143;

        if (Math.abs(avgBlockInterval - TARGET_BLOCK_INTERVAL) > (TARGET_BLOCK_INTERVAL * 0.5 / 100)) {
            double adjustment = avgBlockInterval / (double) TARGET_BLOCK_INTERVAL;
            return last144Blocks.get(143).getDifficulty().multiply(BigDecimal.valueOf(adjustment));
        } else {
            return last144Blocks.get(143).getDifficulty();
        }

    }

}



@Data
class Block {

    private Long time;
    private BigDecimal difficulty;

    // геттеры и сеттеры
}
