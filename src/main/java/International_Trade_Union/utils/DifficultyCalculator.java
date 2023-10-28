package International_Trade_Union.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DifficultyCalculator {
    // Метод, который вычисляет ожидаемую сложность, чтобы каждый блок находился каждые 150 секунд, а регуляция происходила каждые 288 блоков.
    public static BigInteger calculateDifficulty(BigInteger previousTarget, long previousTime, long currentTime) {
        // Количество блоков в интервале регулировки
        final int INTERVAL = 288;
        // Желаемое время нахождения одного блока в секундах
        final int TARGET_TIME = 150;
        // Максимальное значение цели (сложность 1)
        final BigInteger MAX_TARGET = new BigInteger("0000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
        // Минимальное значение цели (максимальная сложность)
        final BigInteger MIN_TARGET = BigInteger.ONE;

        // Вычисляем время, затраченное на предыдущий интервал блоков
        long timeSpent = currentTime - previousTime;
        // Ограничиваем время сверху и снизу, чтобы избежать слишком больших изменений сложности
        if (timeSpent < TARGET_TIME * INTERVAL / 4) {
            timeSpent = TARGET_TIME * INTERVAL / 4;
        }
        if (timeSpent > TARGET_TIME * INTERVAL * 4) {
            timeSpent = TARGET_TIME * INTERVAL * 4;
        }
        // Вычисляем новую цель как произведение предыдущей цели и фактора корректировки
        BigInteger newTarget = previousTarget.multiply(BigInteger.valueOf(timeSpent));
        // Делим на желаемое время для всего интервала
        newTarget = newTarget.divide(BigInteger.valueOf(TARGET_TIME * INTERVAL));
        // Проверяем, что новая цель не выходит за границы максимальной и минимальной сложности
        if (newTarget.compareTo(MAX_TARGET) > 0) {
            newTarget = MAX_TARGET;
        }
        if (newTarget.compareTo(MIN_TARGET) < 0) {
            newTarget = MIN_TARGET;
        }
        // Возвращаем новую цель
        return newTarget;
    }

    // Метод, который проверяет, соответствует ли хеш блока заданной сложности. Я буду использовать стандартный алгоритм SHA-256 для хеширования заголовка блока и сравнивать полученный хеш с целью.
    public static boolean isValidBlock(byte[] blockHeader, BigInteger target) {
        // Используем класс MessageDigest для хеширования заголовка блока
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(blockHeader);
            // Преобразуем хеш в объект BigInteger для сравнения с целью
            BigInteger hashValue = new BigInteger(1, hash);
            // Если хеш меньше или равен цели, то блок действителен
            return hashValue.compareTo(target) <= 0;
        } catch (NoSuchAlgorithmException e) {
            // Если алгоритм SHA-256 не поддерживается, выбрасываем исключение
            throw new RuntimeException(e);
        }
    }

}


