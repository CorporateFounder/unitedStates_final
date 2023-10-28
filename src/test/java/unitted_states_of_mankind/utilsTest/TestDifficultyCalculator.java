package unitted_states_of_mankind.utilsTest;

import International_Trade_Union.utils.DifficultyCalculator;
import International_Trade_Union.utils.UtilsTime;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TestDifficultyCalculator {

    @Test
    public void TestDifficultyCalculator() throws NoSuchAlgorithmException {

        Timestamp lastIndex = new Timestamp(UtilsTime.getUniversalTimestamp());



        int diff = 1;
         int i = 0;
        String hello;
        while (true){
            hello = "hello";
            hello += i;
            i++;

            boolean is = DifficultyCalculator.isValidBlock(hello.getBytes(StandardCharsets.UTF_8), BigInteger.valueOf(diff));

            System.out.println("before: " + hello);
            if(is)
                break;;
        }

        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
        Long result = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.SECONDS);
        System.out.println("*****************************************************");
        System.out.println("finish: " + hello);
        System.out.println("result_" + result);
    }
}
