package unitted_states_of_mankind;

import International_Trade_Union.utils.UtilsUse;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadExample {
    private static volatile boolean blockFound = false;
    private static volatile String foundHash = "";

    public static void main(String[] args) {
        String strBlock = "hello world";
        int nonce = 0;
        int different = 100000;
        int THREAD = 3;

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREAD; i++) {
            final int threadNonce = nonce;
            final int threadDifferent = different;

            Thread thread = new Thread(() -> {
                int localNonce = threadNonce;
                int localDifferent = threadDifferent;
                String localHash = "";

                while (!blockFound) {
                    try {
                        localHash = UtilsUse.sha256hash(strBlock + localNonce);
                    }catch (ArrayIndexOutOfBoundsException e){
                        continue;
                    }
                    System.out.println("thread name: " + Thread.currentThread().getName());
                    if (UtilsUse.chooseComplexity(localHash, 2, 29674)) {
                        synchronized (MultiThreadExample.class) {
                            if (!blockFound) {
                                blockFound = true;
                                foundHash = localHash;
                            }
                        }
                        System.out.println("Block found: hash: " + localHash);
                        break;
                    }
                    localNonce++;
                }
            });

            threads.add(thread);
            thread.start();
            nonce += different;
            different += different;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Hash: " + foundHash);
    }

}
