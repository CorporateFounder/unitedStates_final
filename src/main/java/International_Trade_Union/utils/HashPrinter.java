package International_Trade_Union.utils;

import static International_Trade_Union.utils.BlockchainDifficulty.bytesToBinary;
import static International_Trade_Union.utils.BlockchainDifficulty.countLeadingZeros;

public class HashPrinter {
    public static void printBinary(byte[] hash) {

        String binary = bytesToBinary(hash);

        int leadingZeros = countLeadingZeros(binary);

        System.out.println("Leading zeros: " + leadingZeros);

        for(int i = 0; i < binary.length(); i+=8) {
            System.out.print(binary.substring(i, i+8) + " ");
        }

    }



    public static void main(String[] args) {

        byte[] hash = {
                (byte)0xf6, (byte)0xdd, (byte)0xd9, (byte)0x97,
                (byte)0xaf, (byte)0x44, (byte)0x45, (byte)0x36
        };

        printBinary(hash);

    }

}
