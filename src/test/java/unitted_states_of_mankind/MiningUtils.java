package unitted_states_of_mankind;

import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

public class MiningUtils {

    // Метод для генерации SHA-256 хеша из строки
    public static String calculateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Преобразование байтов в шестнадцатеричную строку
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для вычисления таргета на основе сложности
    public static String calculateTarget(long difficulty) {
        // Максимальное значение цели (все f)

//        String maxTarget = calculateMaxTarget(difficulty);
        String maxTarget ="000000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";


        // Вычисление таргета: maxTarget / difficulty
        BigInteger maxTargetValue = new BigInteger(maxTarget, 16);
        BigInteger targetValue = maxTargetValue.divide(BigInteger.valueOf(difficulty));

        // Преобразование значения таргета в строку в шестнадцатеричной системе
        String target = targetValue.toString(16);

        // Дополнение нулями до 64 символов
        while (target.length() < 64) {
            target = "0" + target;
        }

        return target;
    }

    public static int calculateMinHashZeros(String maxTarget, long complexity) {
        if (maxTarget == null || maxTarget.isBlank()) {
            throw new IllegalArgumentException("Invalid max target");
        }
        if (complexity <= 0) {
            throw new IllegalArgumentException("Invalid complexity");
        }

        // Convert max target string to BigInteger
        BigInteger maxTargetValue = new BigInteger(maxTarget, 16);

        // Calculate target value
        BigInteger targetValue = maxTargetValue.divide(BigInteger.valueOf(complexity));

        // Convert target value to binary string
        String targetBin = targetValue.toString(2);

        // Count leading zeros in binary string
        int minZeros = 0;
        for (char c : targetBin.toCharArray()) {
            if (c == '0') {
                minZeros++;
            } else {
                break;
            }
        }

        return minZeros;
    }
    // Метод для проверки валидности хеша с учетом сложности
    public static boolean isValidHash(String hash, String target) {
        return hash.compareTo(target) <= 0;
    }

    public static void main(String[] args) {
        // Пример данных блока для майнинга
        String previousHash = "0000000000000000000000000000000000000000000000000000000000000000";
        String merkleRoot = "axdb6f38752e0ac6ea1e9ebe8987e80ebdfbce4f0c52f1b00a08866ec59a6755";
        long timestamp = 1634582400; // Пример времени в секундах с начала эпохи
        // Target TestingBlock Time (in seconds)
        int targetBlockTime = 150;

        // Calculate Difficulty based on Target TestingBlock Time
//        int difficulty = calculateDifficulty(targetBlockTime);
// Calculate Max Target based on Difficulty

//
//         Calculate Difficulty based on Target TestingBlock Time
        //diff=1 secndond 13 diff 1
        //diff=1 secndond 12 diff 2
        long difficulty = 1;
        // Пример блока с предыдущим хешем, меркловским корнем и временем
        String blockData = previousHash + merkleRoot + timestamp;
        String target = calculateTarget(difficulty);


        Timestamp lastIndex = new Timestamp(UtilsTime.getUniversalTimestamp());
        // Начало процесса майнинга
        long nonce = 0;
        while (true) {
            String hash = calculateHash("hello" + nonce);
//            String hash = UtilsUse.sha256hash(blockData + nonce);
            System.out.println(nonce);
            if (isValidHash(hash, target)) {
                System.out.println("TestingBlock mined! Nonce: " + nonce);
                System.out.println("Hash: " + hash);
                System.out.println("target: " + target);

                break;
            }
            nonce++;
        }

        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());


        Long result = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.SECONDS);
        System.out.println("result: " + result);
    }
    // Method to calculate difficulty based on target block time
    private static int calculateDifficulty(int targetBlockTime) {
        return (int) Math.pow(2, 256) / targetBlockTime;
    }

    // Method to calculate max target based on difficulty
    private static String calculateMaxTarget(long difficulty) {
        BigInteger maxTargetValue = BigInteger.valueOf(difficulty);
        return maxTargetValue.toString(16);
    }
}