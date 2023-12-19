package International_Trade_Union.utils;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;


public class BlockchainDifficulty {
  public static void printBinary(byte[] bytes) {
    for(byte b : bytes) {
      String binary = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
      System.out.print(binary);
    }
  }




  public static boolean meetsDifficulty(byte[] hash, long difficulty) {
   
    int zeroBits = countLeadingZeroBits(hash);
    return zeroBits >= difficulty;
  }
  public static String bytesToBinary(byte[] bytes) {
    String binary = "";
    for(byte b : bytes) {
      binary += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }
    return binary;
  }

  public static int countLeadingZeros(String binary) {

    int count = 0;
    for(int i = 0; i < binary.length(); i++) {
      if(binary.charAt(i) == '0') {
        count++;
      } else {
        break;
      }
    }

    return count;
  }
  public static boolean v2MeetsDifficulty(byte[]hash, long difficulty){
    int zeroBits = countLeadingZeroBits(hash);
    return zeroBits == difficulty;
  }
  public static boolean v3MeetsDifficulty(byte[]hash, long difiiculty){
    String binary = bytesToBinary(hash);

    int leadingZeros = countLeadingZeros(binary);
    return leadingZeros == difiiculty;
  }
  public static boolean v4MeetsDifficulty(String hash, long difficulty){
    int leadingZeros =countLeadingZeroBits(hash.getBytes());
    boolean isLeadingZerosInSympbol = UtilsUse.hashComplexity(hash, difficulty);
    return isLeadingZerosInSympbol && leadingZeros >= Seting.FIXED_BITE;
  }


  //ПОСЛЕДНЕЕ ОБНОВЛЕНИЕ, АНАЛОГ БИТКОИНА, НО УЛУЧШЕННЫЙ
  //***********************************************************************************************************
  public static String calculateTarget(long difficulty) {
    // Максимальное значение цели (все f)

//        String maxTarget = calculateMaxTarget(difficulty);
    String maxTarget =Seting.MAX_TARGET;

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
  public static boolean isValidHash(String hash, String target) {
    boolean result = hash.compareTo(target) <= 0;
    return result;
  }
  // Метод для вычисления таргета на основе сложности

  //***********************************************************************************************************
  private static Block getLatestBlock(List<Block> blocks) {
    return blocks.get(blocks.size() - 1);
  }

  private static Block getPreviousAdjustmentBlock(List<Block> blocks, int difficultyInterval) {
    return blocks.get(blocks.size() - difficultyInterval);
  }

    public static int countLeadingZeroBits(byte[] hash) {
        int bitLength = hash.length * 8;
        BitSet bits = BitSet.valueOf(hash);

        int count = 0;
        while (count < bitLength && !bits.get(count)) {
            count++;
        }

        return count;
    }

}