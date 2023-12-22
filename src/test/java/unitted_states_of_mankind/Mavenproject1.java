package unitted_states_of_mankind;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

public class Mavenproject1 {
    public static int  getLeadingBinaryZeros(String hash){
        int bitSum = 0;
        String hashUpper = hash.toUpperCase();
        for (int i = 0; i < hashUpper.length(); i++) {
            String hex = hashUpper.substring(i, i+1);
//            System.out.println(hex);
//            System.out.println("hex: " + hex);
            int hexValue = Integer.parseInt(hex, 16);
            if(hexValue == 0){
                bitSum += 4;
//                System.out.println(
//                        "zero"
//                );
            }else {
                while (hexValue > 0){
                    int MSB = (hexValue & 0x8) >> 3;
//                    System.out.println("isb: " + MSB);
                    if(MSB ==1 ){
                        return bitSum;
                    }
                    bitSum++;
                    hexValue <<=1;
                }
            }
        }
        return bitSum;
    }

    public static void main(String[] args) {
        String hash = "0000000000b7d33dc2fdf61c21a2e395e6a635cb14b8d106aca81403b5676041";


        int diff = 28;

        System.out.println("**********************************************");


        while (true){

            Timestamp lastTime = new Timestamp(UtilsTime.getUniversalTimestamp());

            long nonce = 0;
            while (true){
                hash = UtilsUse.sha256hash("hello kitty " + "" + nonce);



                Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());
                Long result = actualTime.toInstant().until(lastTime.toInstant(), ChronoUnit.SECONDS);
                if(getLeadingBinaryZeros(hash) == diff){
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
