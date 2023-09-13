package International_Trade_Union.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Scrypt {
    private static final int N = 1024;
    private static final int r = 1;
    private static final int p = 1;

    public static String hash(String input) throws Exception {

        byte[] salt = generateSalt();

        byte[] hashed = pbkdf2(input.getBytes(), salt, N, r * p * 128);

        return bytesToHex(salt) + bytesToHex(hashed);

    }

    private static byte[] generateSalt() {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;

    }

    private static byte[] pbkdf2(byte[] input, byte[] salt, int iterations, int outputLength)
            throws Exception {

        PBEKeySpec spec = new PBEKeySpec(String.valueOf(input).toCharArray(), salt, iterations, outputLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return skf.generateSecret(spec).getEncoded();

    }

    private static String bytesToHex(byte[] bytes) {

        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();

    }

    public static boolean checkDifficulty(String hash, int difficulty) {

        // Текущая сложность - 3 ведущих нуля


        for(int i=0; i<hash.length(); i++) {

            if(i >= difficulty) {
                // Хэш длиннее чем сложность, не подходит
                return false;
            }

            char c = hash.charAt(i);

            if(c != '0') {
                // Найдена не нулевая цифра раньше времени
                return false;
            }

        }

        // Хэш начинается с difficulty нулей, подходит
        return true;

    }
}
