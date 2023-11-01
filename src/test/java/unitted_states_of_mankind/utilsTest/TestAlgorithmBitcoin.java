package unitted_states_of_mankind.utilsTest;


import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

@SpringBootTest
public class TestAlgorithmBitcoin {

    private static String hashTest = "00000000001526cb10e0b076fda5798e0cba38f7a3e4990c2d098cbc15f8a81b";

    @Test
    public void standart(){
        long sizeZ = UtilsUse.hashCount(hashTest, 66500);
        System.out.println("size: " + sizeZ);
    }

    @Test
    public void bitcount(){
        int size = countZeros(hashTest.getBytes());
        System.out.println("size: " + size);
    }
    @Test
    public void miningTest(){

            int diff = 1;

            for (int i = 0; i < hashTest.length(); i++) {

                Timestamp lastIndex = new Timestamp(UtilsTime.getUniversalTimestamp());

                int count = 0;

                while (true){
                    String hash = "hello world";
                    hash += count;

                    hash = UtilsUse.sha256hash(hash);
                    int size = countZeros(hash.getBytes());
                    System.out.println("size: " + size);
                    if(size == diff){
                        System.out.println("size zero: " + size);
                        break;
                    }
                    count++;
                }
                Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
                Long result = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.SECONDS);
                System.out.println("second: " + result);
                if(result > 150){
                    break;
                }
                else {
                    diff++;
                }
        }

    }
    public static int countZeros(byte[] hashBytes) {
        int count = 0;
        for (byte b : hashBytes) {
            if (b == 0) {
                count += 8;
            } else {
                count += Integer.bitCount(b);
            }
        }
        return count;
    }



    public static boolean isBlockValid(BigInteger blockHash, int blockHeight, BigInteger target) {
        // Получить target из сложности


        // Проверить, меньше ли хеш блока target
        return blockHash.compareTo(target) <= 0;
    }


}
