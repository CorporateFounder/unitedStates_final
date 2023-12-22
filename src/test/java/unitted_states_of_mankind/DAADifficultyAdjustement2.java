package unitted_states_of_mankind;

import java.util.ArrayList;

public class DAADifficultyAdjustement2 {


    public static void main(String[] args) {
        // Пример данных блоков для адаптации сложности
        ArrayList<Integer> blockTimes = new ArrayList<>();
        blockTimes.add(150);  // Время майнинга блока в секундах
        blockTimes.add(150);
        blockTimes.add(150);
        blockTimes.add(150);

        for (int i = 0; i < 140; i++) {
            blockTimes.add(150);
        }

        // Вызов метода адаптации сложности
        adjustDifficulty(blockTimes);
    }

    public static void adjustDifficulty(ArrayList<Integer> blockTimes) {
        int targetBlockTime = 150;  // Желаемое время майнинга блока в секундах
        int blocksToAdjust = 144;  // Количество блоков, после которых происходит адаптация сложности

        // Подсчет среднего времени майнинга для последних блоков
        int sumBlockTimes = 0;
        for (int i = blockTimes.size() - blocksToAdjust; i < blockTimes.size(); i++) {
            sumBlockTimes += blockTimes.get(i);
        }
        int averageBlockTime = sumBlockTimes / blocksToAdjust;

        // Адаптация сложности
        int newDifficulty = targetBlockTime * blocksToAdjust / averageBlockTime;

        // Вывод новой сложности
        System.out.println("new diff: " + newDifficulty);
    }


}

