package unitted_states_of_mankind.utilsTest;


import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
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
    public void bitcoin(){

        Timestamp lastIndex = new Timestamp(UtilsTime.getUniversalTimestamp());


        //1) second 187, diff 32
        //2) second 117 diff 32
        //3) second 89 diff 32
        //4) second 9 diff 32
        //5) second 317 diff 32
        //6) second 213 diff 32

        //1) second 274 diff 33
        //2) second 309 diff 33
        //3) second 10 diff 33
        //4) second 32 diff 33
        //5) second 232 diff 33

        //1) second 211 diff 34
        //2) second 274 diff 34
        //3) second 177 diff 34
        //4) second diff 34

        int diff = 34;
        String text = "dse r3ww3r";
        int nonce = 0;
        String result = text + nonce;
        while (true){
            result = text + nonce;
            if(checkHash(result, diff)){
                break;
            }
            nonce++;

        }
        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
        Long time = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.SECONDS);
        System.out.println("*******************************************");
        System.out.println(checkHash(result, diff));
        System.out.println("time: " + time);
        System.out.println(result);

        Base base = new Base58();
        System.out.println("hash " + base.encode(UtilsUse.sha256(result)));
    }



    public static boolean checkHash(String blockHash, int difficulty) {

        // Получаем количество ведущих нулей из сложности
        int leadingZeros = difficulty;

        // Преобразуем хеш в byte array
        byte[] hashBytes = UtilsUse.sha256(blockHash);

        // Проверяем, что хеш начинается с нужного кол-ва нулей
        for(int i=0; i < leadingZeros/8; i++) {
            if (hashBytes[i] != 0) {
                return false;
            }
        }

        // Хеш прошел проверку
        return true;

    }


}
