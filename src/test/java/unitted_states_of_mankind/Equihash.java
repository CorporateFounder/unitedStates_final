package unitted_states_of_mankind;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Equihash {
    private static Random random = new Random();

    private static final int N = 50; // equation size
    private static final int K = 3; // number of solutions

    int maxSolutions = 10;
    private BigInteger[] Z; // R array
    private BigInteger[] X; // solution
    public Equihash(int maxSolutions) {
        this.maxSolutions = 100;
        Z = new BigInteger[N];

        // initialize Z array randomly
        for(int i=0; i<N; i++) {
            Z[i] = BigInteger.valueOf(random.nextInt());
        }
    }



    public static void main(String[] args) throws NoSuchAlgorithmException {

        Equihash solver = new Equihash(100);

        List<BigInteger[]> solutions = solver.solve(100);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        for(BigInteger[] solution : solutions) {

            byte[] bytes = new byte[32];
            System.arraycopy(solution, 0, bytes, 0, 32);

            byte[] hash = digest.digest(bytes);

            String hexHash = bytesToHex(hash);

            System.out.println("Solution hash: " + hexHash);

        }

    }private static String bytesToHex(byte[] bytes) {

        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();

    }
    public Equihash() {
        Z = new BigInteger[N];
        X = new BigInteger[K];

        // initialize R array randomly
        Random random = new Random();
        for(int i=0; i<N; i++) {
            Z[i] = BigInteger.valueOf(random.nextInt());
        }
    }

    public List<BigInteger[]> solve(int maxSolutions) {

        List<BigInteger[]> solutions = new ArrayList<>();

        int K = 9;
        BigInteger[] X = new BigInteger[K];

        List<BigInteger> possibilities = getAllPossibilities(maxSolutions);

        for(BigInteger x1 : possibilities) {

            if(solutions.size() >= maxSolutions){
                return solutions;
            }
            System.out.println("Processed first index x1 ");
            for(BigInteger x2 : possibilities) {

                if(solutions.size() >= maxSolutions){
                    return solutions;
                }
                System.out.println("Processed first index x2");

                for(BigInteger x3 : possibilities) {

                    if(solutions.size() >= maxSolutions){
                        return solutions;
                    }
//                    System.out.println("Processed first index x3");

                    for(BigInteger x4 : possibilities) {
//                        System.out.println("Processed first index x4");
                        for(BigInteger x5 : possibilities) {

                            if(solutions.size() >= maxSolutions){
                                return solutions;
                            }

//                            System.out.println("Processed first index x5");
                            for(BigInteger x6 : possibilities) {

                                if(solutions.size() >= maxSolutions){
                                    return solutions;
                                }
//                                System.out.println("Processed first index x6");
                                for(BigInteger x7 : possibilities) {

                                    if(solutions.size() >= maxSolutions){
                                        return solutions;
                                    }
//                                    System.out.println("Processed first index x7");
                                    for(BigInteger x8 : possibilities) {

                                        if(solutions.size() >= maxSolutions){
                                            return solutions;
                                        }
//                                        System.out.println("Processed first index x8");
                                        for(BigInteger x9 : possibilities) {

                                            if(solutions.size() >= maxSolutions){
                                                return solutions;
                                            }
//                                            System.out.println("Processed first index x9");
                                            X[0] = x1;
                                            X[1] = x2;
                                            X[2] = x3;
                                            X[3] = x4;
                                            X[4] = x5;
                                            X[5] = x6;
                                            X[6] = x7;
                                            X[7] = x8;
                                            X[8] = x9;

                                            if(verify(X, solutions)) {

                                                BigInteger[] solution = X.clone();
                                                solutions.add(solution);

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return solutions;

    }
    private List<BigInteger> getAllPossibilities(int maxSolutions) {
// Оценка общего числа решений
        long totalSolutions = (long)Math.pow(2, N/K);

// Задаем желаемое количество решений
        int numberOfSolutions = maxSolutions;

// Ограничиваем значениеnumberOfSolutions общим количеством
        if(totalSolutions < numberOfSolutions) {
            numberOfSolutions = (int)totalSolutions;
        }
        List<BigInteger> possibilities = new ArrayList<>();

        // Порождаем случайные байтовые последовательности длиной 32 байта
        // Это входные данные для хеширования SHA-256

        for(int i = 0; i < numberOfSolutions; i++) {

            byte[] bytes = new byte[32];

            // Генерация псевдослучайных байт
            random.nextBytes(bytes);

            // Конвертируем в BigInteger для хранения в списке
            BigInteger value = new BigInteger(bytes);

            possibilities.add(value);
        }

        return possibilities;

    }

    private  boolean verify(BigInteger[] solution, List<BigInteger[]> solutions) {

        for(int i=0; i<N; i++) {

            BigInteger prod1 = BigInteger.ONE;
            BigInteger prod2 = BigInteger.ONE;

            for(int j=0; j<K; j++) {
                if(solution[j].equals(BigInteger.ONE)) {
                    prod1 = prod1.multiply(Z[i]);
                } else {
                    prod2 = prod2.multiply(Z[i].xor(solution[j]));
                }
            }

            if(!prod1.add(prod2).equals(BigInteger.ZERO)) {
                return false;
            }
        }

        if(solutions.size() >= maxSolutions){
            return false;
        }

        return true;
    }
}
