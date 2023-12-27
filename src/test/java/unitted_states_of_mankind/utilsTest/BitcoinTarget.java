package unitted_states_of_mankind.utilsTest;

import java.math.BigInteger;

public class BitcoinTarget {

    // Максимальная цель, равная 2^224
    public static final BigInteger MAX_TARGET = new BigInteger("26959535291011309493156476344723991336010898738574164086137773096960");

    // Среднее время генерации блока в секундах, которое стремится поддерживать система
    public static final int BLOCK_TIME = 150;

    // Количество блоков, через которое происходит корректировка сложности
    public static final int ADJUSTMENT_INTERVAL = 2016;

    // Вычисляет текущую цель на основе номера последнего блока и номера последнего блока, на котором произошла корректировка сложности
    public static BigInteger calculateTarget(int currentBlock, int lastAdjustmentBlock) {
        // Цель = максимальная цель * 2 ^ ((текущий блок - последний блок корректировки) * среднее время блока / (интервал корректировки * 600))
        BigInteger exponent = BigInteger.valueOf((currentBlock - lastAdjustmentBlock) * BLOCK_TIME / (ADJUSTMENT_INTERVAL * 600));
        BigInteger base = BigInteger.valueOf(2);
        BigInteger factor = base.pow(exponent.intValue());
        BigInteger target = MAX_TARGET.multiply(factor);
        return target;
    }

    // Преобразует цель в 4-байтовое представление с индексом и коэффициентом
    public static String targetToBits(BigInteger target) {
        // Находим наибольший индекс, такой что 256 ^ (индекс - 3) <= цель
        int index = 3;
        BigInteger power = BigInteger.valueOf(256).pow(index - 3);
        while (power.compareTo(target) <= 0) {
            index++;
            power = power.multiply(BigInteger.valueOf(256));
        }
        index--;
        power = power.divide(BigInteger.valueOf(256));

        // Находим коэффициент, такой что коэффициент * 256 ^ (индекс - 3) <= цель < (коэффициент + 1) * 256 ^ (индекс - 3)
        BigInteger coefficient = target.divide(power);

        // Преобразуем индекс и коэффициент в 16-ричную строку
        String hexIndex = Integer.toHexString(index);
        String hexCoefficient = coefficient.toString(16);
        if (hexIndex.length() < 2) {
            hexIndex = "0" + hexIndex;
        }
        if (hexCoefficient.length() < 6) {
            hexCoefficient = "0".repeat(6 - hexCoefficient.length()) + hexCoefficient;
        }
        String bits = hexIndex + hexCoefficient;
        return bits;
    }

    // Тестируем код на примере из вопроса
    public static void main(String[] args) {
        // Биты из вопроса: 388618029
        String bits = "1729d72d";
        // Цель, соответствующая этим битам
        BigInteger target = new BigInteger(bits, 16);
        // Номер последнего блока
        int currentBlock = 538695;
        // Номер последнего блока, на котором произошла корректировка сложности
        int lastAdjustmentBlock = 538560;
        // Вычисляем новую цель для следующего блока
        BigInteger newTarget = calculateTarget(currentBlock + 1, lastAdjustmentBlock);
        // Преобразуем новую цель в биты
        String newBits = targetToBits(newTarget);
        // Выводим результаты
        System.out.println("last target: " + target);
        System.out.println("last big: " + bits);
        System.out.println("new target: " + newTarget);
        System.out.println("new bit: " + newBits);
    }
}
