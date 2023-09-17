package International_Trade_Union.entity.blockchain.block;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.exception.NotValidTransactionException;
import International_Trade_Union.model.Mining;

import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsStorage;
import International_Trade_Union.utils.UtilsTime;
import International_Trade_Union.utils.UtilsUse;
import ch.qos.logback.core.pattern.FormatInfo;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@JsonAutoDetect
@Data
public final class Block implements Cloneable {
    private static volatile boolean blockFound = false;
    private static long randomNumberProofStatic = 0;
    private static int INCREMENT_VALUE = 50000;
    private static int THREAD_COUNT = 10;



    private static boolean MULTI_THREAD = false;
    private static volatile String foundHash = "";

    public static int getThreadCount() {
        return THREAD_COUNT;
    }

    public static void setThreadCount(int threadCount) {
        THREAD_COUNT = threadCount;
    }

    public static boolean isMultiThread() {
        return MULTI_THREAD;
    }

    public static void setMultiThread(boolean multiThread) {
        MULTI_THREAD = multiThread;
    }

    private List<DtoTransaction> dtoTransactions;
    private String previousHash;
    private String minerAddress;
    private String founderAddress;
    private long randomNumberProof;
    private double minerRewards;
    private int hashCompexity;
    private Timestamp timestamp;
    private long index;
    private String hashBlock;

    public String hashForTransaction() throws IOException {

        if(this != null){
            BlockForHash block = new BlockForHash(this.getDtoTransactions(),
                    this.previousHash,
                    this.minerAddress,
                    this.founderAddress,
                    this.randomNumberProof,
                    this.minerRewards,
                    this.hashCompexity,
                    this.timestamp,
                    this.index); return UtilsUse.sha256hash(block.jsonString());
        }


       return "";
    }

    public static long getRandomNumberProofStatic() {
        return randomNumberProofStatic;
    }

    public static void setRandomNumberProofStatic(long randomNumberProofStatic) {
        Block.randomNumberProofStatic = randomNumberProofStatic;
    }

    public Block(List<DtoTransaction> dtoTransactions, String previousHex, String minerAddress, String founderAddress, int hashCompexity, long index) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        this.dtoTransactions = dtoTransactions;
        this.previousHash = previousHex;
        this.minerAddress = minerAddress;
        this.minerRewards = miningRewardsCount();
        this.hashCompexity = hashCompexity;
        this.founderAddress = founderAddress;
        this.timestamp = new Timestamp(UtilsTime.getUniversalTimestamp());
//        this.timestamp = Timestamp.valueOf( OffsetDateTime.now( ZoneOffset.UTC ).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        this.index = index;
        this.hashBlock = chooseMultiString(hashCompexity, MULTI_THREAD);

    }

    public Block(List<DtoTransaction> dtoTransactions, String previousHash, String minerAddress, String founderAddress, long randomNumberProof, double minerRewards, int hashCompexity, Timestamp timestamp, long index, String hashBlock) {
        this.dtoTransactions = dtoTransactions;
        this.previousHash = previousHash;
        this.minerAddress = minerAddress;
        this.founderAddress = founderAddress;
        this.randomNumberProof = randomNumberProof;
        this.minerRewards = minerRewards;
        this.hashCompexity = hashCompexity;
        this.timestamp = timestamp;
        this.index = index;
        this.hashBlock = hashBlock;
    }

    @JsonAutoDetect
    @Data
    public class BlockForHash {
        private List<DtoTransaction> transactions;
        private String previousHash;
        private String minerAddress;
        private String founderAddress;
        private long randomNumberProof;
        private double minerRewards;
        private int hashCompexity;
        private Timestamp timestamp;
        private long index;


        public BlockForHash() {
        }


        public BlockForHash(List<DtoTransaction> transactions,
                            String previousHash,
                            String minerAddress,
                            String founderAddress,
                            long randomNumberProof,
                            double minerRewards,
                            int hashCompexity,
                            Timestamp timestamp,
                            long index) {
            this.transactions = transactions;
            this.previousHash = previousHash;
            this.minerAddress = minerAddress;
            this.founderAddress = founderAddress;
            this.randomNumberProof = randomNumberProof;
            this.minerRewards = minerRewards;
            this.hashCompexity = hashCompexity;
            this.timestamp = timestamp;
            this.index = index;

        }

        public String hashForTransaction() throws IOException {
            return UtilsUse.sha256hash(jsonString());
        }

        public String jsonString() throws IOException {
            return UtilsJson.objToStringJson(this);
        }
    }

    public Block() {
    }

    public String hashForBlockchain()
            throws
            IOException {
        return this.hashBlock;
    }


    public boolean verifyesTransSign() throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        for (DtoTransaction dtoTransaction : dtoTransactions) {
            if (!dtoTransaction.verify())
                return false;
        }
        return true;
    }

    private double miningRewardsCount() {
        double rewards = 0.0;
        for (DtoTransaction dtoTransaction : dtoTransactions) {

            rewards += dtoTransaction.getBonusForMiner();
        }

        return rewards;
    }

    public String jsonString() throws IOException {
        return UtilsJson.objToStringJson(this);
    }

    public String multipleFindHash(int hashCoplexity) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        System.out.println("find hash method");

        if (!verifyesTransSign()) {
            throw new NotValidTransactionException();
        }
        int differrentNumber = 0;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            int finalDifferrentNumber = differrentNumber;
            Thread thread = new Thread(() -> {
                long nonce = randomNumberProofStatic + finalDifferrentNumber;

                String tempHash = "";
                int size = UtilsStorage.getSize();
                Timestamp previus = new Timestamp(UtilsTime.getUniversalTimestamp());
                String nameThread = Thread.currentThread().getName();
                while (!blockFound){
                    BlockForHash block = new BlockForHash(this.dtoTransactions,
                            this.previousHash, this.minerAddress, this.founderAddress,
                            nonce, this.minerRewards, this.hashCompexity, this.timestamp, this.index);
                    System.out.printf("\tTrying %d to find a block: ThreadName %s:\n ", nonce , nameThread);
                    Instant instant1 = Instant.ofEpochMilli(UtilsTime.getUniversalTimestamp());
                    Instant instant2 = previus.toInstant();

                    Duration duration = Duration.between(instant1, instant2);
                    long seconds = duration.getSeconds();
                    try {
                        tempHash = block.hashForTransaction();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (seconds > 10 || seconds < -10) {
                        long milliseconds = instant1.toEpochMilli();
                        previus  = new Timestamp(milliseconds);
                        previus.setTime(milliseconds);

                        //проверяет устаревание майнинга, если устарел - прекращает майнинг

                        int tempSize = UtilsStorage.getSize();
                        if (size < tempSize) {
                            Mining.miningIsObsolete = true;
                            System.out.println("someone mined a block before you, the search for this block is no longer relevant and outdated: " + tempHash);

                            synchronized (Block.class) {
                                if (!blockFound) {
                                    blockFound = true;
                                }
                            }
                            System.out.println("Block found: hash: " + tempHash);
                            break;

                        }



                    }

                    //если true, то прекращаем майнинг
                    if (Mining.isIsMiningStop()) {
                        System.out.println("mining will be stopped");

                        synchronized (Block.class) {
                            if (!blockFound) {
                                blockFound = true;
                            }
                        }
                        System.out.println("Block found: hash: " + tempHash);
                        break;

                    }


                    //если true, то прекращаем майнинг. Правильный блок найден
                    if (UtilsUse.chooseComplexity(tempHash, hashCoplexity, index)) {
                        System.out.println("block found: hash: " + tempHash);
                        synchronized (Block.class) {
                            if (!blockFound) {
                                blockFound = true;
                                this.randomNumberProof = nonce;

                                foundHash = tempHash;
                            }
                        }
                        System.out.println("Block found: hash: " + tempHash);
                        foundHash = tempHash;
                        break;
                    }
                    nonce++;
                }

            });

            threads.add(thread);
            thread.start();
            differrentNumber += INCREMENT_VALUE;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        blockFound = false;

            return  foundHash;
    }

    public String chooseMultiString(int hashCompexity, boolean MULTI_THREAD) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        if(MULTI_THREAD){
            return multipleFindHash(hashCompexity);
        }else {
            return findHash(hashCompexity);
        }
    }
    //TODO
    public String findHash(int hashCoplexity) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        System.out.println("find hash method");
        if (!verifyesTransSign()) {
            throw new NotValidTransactionException();
        }

        this.randomNumberProof = randomNumberProofStatic;
        String hash = "";
        //используется для определения кто-нибудь уже успел добыть блок.
        int size = UtilsStorage.getSize();
        Timestamp previus = new Timestamp(UtilsTime.getUniversalTimestamp());
        String nameThread = Thread.currentThread().getName();
        while (true) {
            //перебирает число nonce чтобы найти хеш
            this.randomNumberProof++;

            BlockForHash block = new BlockForHash(this.dtoTransactions,
                    this.previousHash, this.minerAddress, this.founderAddress,
                    this.randomNumberProof, this.minerRewards, this.hashCompexity, this.timestamp, this.index);
            hash = block.hashForTransaction();

            System.out.printf("\tTrying %d to find a block: ThreadName %s:\n ", randomNumberProof , nameThread);
            Instant instant1 = Instant.ofEpochMilli(UtilsTime.getUniversalTimestamp());
            Instant instant2 = previus.toInstant();

            Duration duration = Duration.between(instant1, instant2);
            long seconds = duration.getSeconds();
//          каждые десять секунд проверяем, что время между текущим и предыдущим запросом не больше 10


            if (seconds > 10 || seconds < -10) {
                long milliseconds = instant1.toEpochMilli();
                previus  = new Timestamp(milliseconds);
                previus.setTime(milliseconds);

                //проверяет устаревание майнинга, если устарел - прекращает майнинг

                int tempSize = UtilsStorage.getSize();
                if (size < tempSize) {
                    Mining.miningIsObsolete = true;
                    System.out.println("someone mined a block before you, the search for this block is no longer relevant and outdated: " + hash);

                    return hash;

                }
            }

            //если true, то прекращаем майнинг
            if (Mining.isIsMiningStop()) {
                System.out.println("mining will be stopped");
                return hash;

            }


            //если true, то прекращаем майнинг. Правильный блок найден
            if (UtilsUse.chooseComplexity(hash, hashCoplexity, index)) {
                System.out.println("block found: hash: " + hash);
                break;
            }

        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {


        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return getRandomNumberProof() == block.getRandomNumberProof() && Double.compare(block.getMinerRewards(), getMinerRewards()) == 0 && getHashCompexity() == block.getHashCompexity() && getIndex() == block.getIndex() && Objects.equals(getDtoTransactions(), block.getDtoTransactions()) && Objects.equals(getPreviousHash(), block.getPreviousHash()) && Objects.equals(getMinerAddress(), block.getMinerAddress()) && Objects.equals(getFounderAddress(), block.getFounderAddress()) && Objects.equals(getTimestamp(), block.getTimestamp()) && Objects.equals(getHashBlock(), block.getHashBlock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDtoTransactions(), getPreviousHash(), getMinerAddress(), getFounderAddress(), getRandomNumberProof(), getMinerRewards(), getHashCompexity(), getTimestamp(), getIndex(), getHashBlock());
    }

    @Override
    public Block clone() throws CloneNotSupportedException {
        return new Block(this.dtoTransactions, this.previousHash, this.minerAddress, this.founderAddress,
                this.randomNumberProof, this.minerRewards, this.hashCompexity, this.timestamp, this.index,
                this.hashBlock);
    }
}
