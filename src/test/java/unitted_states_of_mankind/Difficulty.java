package unitted_states_of_mankind;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Difficulty {
    public static void main(String[] args) {
        String hash = "0000000000b7d33dc2fdf61c21a2e395e6a635cb14b8d106aca81403b5676041";
        System.out.println(getBitSum(hash));
        Block block = new Block();
        block.setHashBlock("0000000000b7d33dc2fdf61c21a2e395e6a635cb14b8d106aca81403b5676041");
        int diff = 104;
        System.out.println(isValidBlock(block, diff));
        System.out.println("**********************************************");
        int originalDiff = 100;
        while (true){

            Timestamp lastTime = new Timestamp(UtilsTime.getUniversalTimestamp());
            long nonce = 0;
            while (true){
                hash = UtilsUse.sha256hash("hello kitty " + "" + nonce);



                Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
                Long result = actualTime.toInstant().until(lastTime.toInstant(), ChronoUnit.SECONDS);
                if(isValidHash(hash, originalDiff)){
                    System.out.println("*********************************");
                    System.out.println("hash: " + hash + " originalDiff: " + originalDiff);
                    System.out.println("nonce: " + nonce);
                    System.out.println("time second: " + result);
                    System.out.println("*********************************");
                    break;
                }
                if(result < -150){
                    break;
                }
                nonce++;
            }

            Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
            Long result = actualTime.toInstant().until(lastTime.toInstant(), ChronoUnit.SECONDS);

            if(result < -150){
                originalDiff--;
            }else {
                break;
            }
        }
    }

    // Функция, которая вычисляет сумму битов в хэше блока
    public static int getBitSum(String hash) {
        // Преобразовать хэш в двоичную строку
        String binary = new BigInteger(hash, 16).toString(2);
        // Инициализировать сумму битов
        int bitSum = 0;
        // Перебрать все символы в двоичной строке
        for (char c : binary.toCharArray()) {
            // Если символ равен '1', увеличить сумму битов на 1
            if (c == '1') {
                bitSum++;
            }
        }
        // Вернуть сумму битов
        return bitSum;
    }

    // Функция, которая проверяет, валиден ли блок по заданному критерию и операции
    public static boolean isValidBlock(Block block, int difficulty) {
        // Получить хэш блока
        String hash = block.getHashBlock();
        // Вычислить сумму битов в хэше
        int bitSum = getBitSum(hash);
        // Проверить, меньше ли или равна сумма битов заданному уровню сложности
        return bitSum <= difficulty;
    }

    public static boolean isValidHash(String hash, int difficulty){
        // Вычислить сумму битов в хэше
        int bitSum = getBitSum(hash);
        // Проверить, меньше ли или равна сумма битов заданному уровню сложности
        return bitSum <= difficulty;
    }

    // Функция, которая регулирует сложность майнинга в зависимости от времени нахождения блоков
    public static long adjustDifficulty(Block latestBlock, Block previousBLock, List<Block> blocks, long blockInterval, int difficultyInterval) {
        // Получить блок, который был сгенерирован перед последним изменением сложности
        Block prevAdjustmentBlock = previousBLock;
        // Вычислить ожидаемое время, которое должно было затратиться на нахождение блоков между изменениями сложности
        long expectedTime = blockInterval * difficultyInterval;
        // Вычислить фактическое время, которое было затрачено на нахождение блоков между изменениями сложности
        long actualTime = latestBlock.getTimestamp().getTime() - prevAdjustmentBlock.getTimestamp().getTime();
        // Получить текущий уровень сложности
        long currentDifficulty = prevAdjustmentBlock.getHashCompexity();
        // Инициализировать новый уровень сложности
        long newDifficulty = currentDifficulty;
        // Если фактическое время меньше половины ожидаемого времени, уменьшить новый уровень сложности на 1
        if (actualTime < expectedTime / 2) {
            newDifficulty--;
        }
        // Если фактическое время больше двух раз ожидаемого времени, увеличить новый уровень сложности на 1
        else if (actualTime > expectedTime * 2) {
            newDifficulty++;
        }
        // Если новый уровень сложности меньше 0, установить его равным 0
        if (newDifficulty < 0) {
            newDifficulty = 0;
        }
        // Если новый уровень сложности больше 256, установить его равным 256
        if (newDifficulty > 256) {
            newDifficulty = 256;
        }
        // Вернуть новый уровень сложности
        return newDifficulty;
    }

}