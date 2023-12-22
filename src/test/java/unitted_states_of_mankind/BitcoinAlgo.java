package unitted_states_of_mankind;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

public class BitcoinAlgo {
    public static BigInteger calculateTarget(long difficulty) {

//        BigInteger maxTarget = new BigInteger(Seting.MAX_TARGET_v29, 16);
        BigInteger maxTarget = new BigInteger("0000000FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);

        return maxTarget.divide(BigInteger.valueOf(difficulty));

    }

    public static boolean isValidHash(String hash, BigInteger target) {

        BigInteger hashInt = new BigInteger(hash, 16);

        return hashInt.compareTo(target) <= 0;

    }

    public static void main(String[] args) {
        String hash = "0000000000b7d33dc2fdf61c21a2e395e6a635cb14b8d106aca81403b5676041";

        Block block = new Block();

        int diff = 1;

        System.out.println("**********************************************");


        while (true){

            Timestamp lastTime = new Timestamp(UtilsTime.getUniversalTimestamp());
            BigInteger target = calculateTarget(diff);
            long nonce = 0;
            while (true){
                hash = UtilsUse.sha256hash("hello kitty " + "" + nonce);



                Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
                Long result = actualTime.toInstant().until(lastTime.toInstant(), ChronoUnit.SECONDS);
                if(isValidHash(hash, target)){
                    System.out.println("*********************************");
                    System.out.println("hash: " + hash );
                    System.out.println("nonce: " + nonce);
                    System.out.println("time second: " + result);
                    System.out.println("diff: " + diff);
                    System.out.println("*********************************");
                    break;
                }
                if(result < -350){
                    break;
                }
                nonce++;
            }

            Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
            Long result = actualTime.toInstant().until(lastTime.toInstant(), ChronoUnit.SECONDS);

            if(result > -150){
                diff++;
            }else {
                System.out.println("finish: ");
                System.out.println("*********************************");
                System.out.println("hash: " + hash );
                System.out.println("nonce: " + nonce);
                System.out.println("time second: " + result);
                System.out.println("diff: " + diff);
                System.out.println("*********************************");
                break;
            }
        }
    }


}
