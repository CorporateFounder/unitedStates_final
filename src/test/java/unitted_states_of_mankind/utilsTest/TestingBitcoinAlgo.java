package unitted_states_of_mankind.utilsTest;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bouncycastle.util.Arrays.reverse;

@SpringBootTest
public class TestingBitcoinAlgo {

    private static final BigInteger MAX_VALUE = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

    public static BigInteger calculateTarget(double difficulty) {

        // Переводим difficulty в целое
        BigInteger difficultyInteger = BigInteger.valueOf((long) Math.pow(2, Math.ceil(Math.log(difficulty) / Math.log(2))));

        // Вычисляем target
        return MAX_VALUE.divide(difficultyInteger);
    }


    public static long hashCount(String hash) {
        long count = 0;
//

        //оптимизирован код
        for (int i = hash.length()-1; i > 0; i--) {
            if (hash.charAt(i) == '1') count++;
            else return count;
        }
        return count;
    }
    @Test
    public void test() throws DecoderException {
        int diff = 1;
        while (true){

            String text = "hello world";
            int nonce = 0;
            String hash = UtilsUse.sha256hash("hellrro world");
            Base base = new Base58();
            String base58 = base.encode(hash.getBytes());



            Timestamp lastIndex = new Timestamp(UtilsTime.getUniversalTimestamp());
            while (true){
                hash = UtilsUse.sha256hash(text + nonce);

//                base58 = base.encode(hash.getBytes());
//                if(hashCount(base58) == diff){
//                    break;
//                }
//
//                if(UtilsUse.hashComplexity(hash, diff)){
//                    break;
//                }


                byte[] hashBytes = Hex.decodeHex(hash);

                //bitcoin type
                BigInteger target = calculateTarget(diff);
//                System.out.println("diff: " + diff);
                BigInteger sha256hashBigInteger = new BigInteger(hash, 16);
                if (sha256hashBigInteger.compareTo(target) < 0) {
//                    System.out.println("The SHA-256 hash is less than or equal to the target value.");
                    break;
                } else {
//                    System.out.println("The SHA-256 hash is greater than the target value.");
                }
                nonce++;
                Timestamp timestamp = new Timestamp(UtilsTime.getUniversalTimestamp());
               long time =  lastIndex.toInstant().until(timestamp.toInstant(), ChronoUnit.MILLIS);
//                System.out.println("time: " + time);
                if(time > 150000){
                    break;
                }
            }
            System.out.println("******************************************");

            Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());


            Long result = lastIndex.toInstant().until(actualTime.toInstant(), ChronoUnit.MILLIS);
            System.out.println("time: " + result);
            System.out.println("hash: " + hash);
            System.out.println("base 58: " + base58);

            if(result < 150000){
                diff++;
            }
            else {
                break;
            }
        }

    }



}
