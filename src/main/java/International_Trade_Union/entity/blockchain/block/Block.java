package International_Trade_Union.entity.blockchain.block;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.exception.NotValidTransactionException;
import International_Trade_Union.model.Mining;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsStorage;
import International_Trade_Union.utils.UtilsUse;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@JsonAutoDetect
@Data
public final class Block implements Cloneable {
    private static long randomNumberProofStatic = 0;
    private static int INCREMENT_VALUE = 200;
    private static int THREAD_COUNT = 10;

    private static boolean MULTI_THREAD = false;

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
        this.timestamp = Timestamp.from(Instant.now());
//        this.timestamp = Timestamp.valueOf( OffsetDateTime.now( ZoneOffset.UTC ).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        this.index = index;
        this.hashBlock = chooseFindHash(hashCompexity, MULTI_THREAD);

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
    private class BlockForHash {
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

    public String chooseFindHash(int hashCompexity, boolean choose) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        if (choose) {
            return findHashConcurrently(hashCompexity);
        } else {
            return findHash(hashCompexity);
        }
    }

    public String findHashConcurrently(int hashComplexity)
            throws IOException, NoSuchAlgorithmException, SignatureException,
            NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        if (!verifyesTransSign()) {
            throw new NotValidTransactionException();
        }

        int valueRandomDifferent = 0;
        this.randomNumberProof = randomNumberProofStatic + valueRandomDifferent;
        String hash = "";

        int size = UtilsStorage.getSize();


        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executorService);


        for (int i = 0; i < THREAD_COUNT; i++) {

            completionService.submit(() -> {
                long tempRandomNumberProof = randomNumberProof;
                Timestamp previousTimestamp = Timestamp.from(Instant.now());

                while (true) {

                    tempRandomNumberProof++;
                    BlockForHash block = new BlockForHash(this.dtoTransactions, this.previousHash,
                            this.minerAddress, this.founderAddress, this.randomNumberProof,
                            this.minerRewards, this.hashCompexity, this.timestamp, this.index);
                    System.out.printf("Trying %d to find a block: ", tempRandomNumberProof);
                    String hashTemp = block.hashForTransaction();

                    if (UtilsUse.hashComplexity(hashTemp.substring(0, hashComplexity), hashComplexity)) {
                        System.out.println("Block found: Hash: " + hashTemp);
                        this.randomNumberProof = tempRandomNumberProof;
                        executorService.shutdownNow();
                        return hashTemp;
                    }

                    Timestamp currentTimestamp = Timestamp.from(Instant.now());
                    long timeDifference = currentTimestamp.toInstant().until(previousTimestamp.toInstant(), ChronoUnit.SECONDS);

                    if (timeDifference > 10 || timeDifference < -10) {
                        previousTimestamp = currentTimestamp;
                        int tempSize = UtilsStorage.getSize();
                        if (size < tempSize) {
                            Mining.miningIsObsolete = true;
                            System.out.println("Someone mined a block before you. The search for this block is no longer relevant and outdated: " + hashTemp);
                            executorService.shutdownNow();
                            return hashTemp;
                        }
                    }


                    if (Mining.isIsMiningStop()) {
                        System.out.println("Mining will be stopped");
                        executorService.shutdownNow();
                        return hashTemp;
                    }
                }

            });

            valueRandomDifferent += INCREMENT_VALUE;
        }

        stop:
        while (true){
            if(Mining.isIsMiningStop() || Mining.miningIsObsolete){
                break stop;
            }
            for (int i = 0; i < THREAD_COUNT; i++) {
                try {
                    Future<String> future = completionService.poll(100, TimeUnit.MILLISECONDS);
                    if (future != null) {
                        hash = future.get();
                        if (UtilsUse.hashComplexity(hash.substring(0, hashComplexity), hashComplexity)) {
                            System.out.println("Block found: Hash: " + hash);
                            executorService.shutdownNow();
                            return hash;
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    // Обработка исключений
                    e.printStackTrace();
                }
            }
            
        }



        return "0";
    }

    //TODO
    public String findHash(int hashCoplexity) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        if (!verifyesTransSign()) {
            throw new NotValidTransactionException();
        }

        this.randomNumberProof = randomNumberProofStatic;
        String hash = "";
        //используется для определения кто-нибудь уже успел добыть блок.
        int size = UtilsStorage.getSize();
        Timestamp previus = Timestamp.from(Instant.now());
        while (true) {
            //перебирает число nonce чтобы найти хеш
            this.randomNumberProof++;


            System.out.println("");
            BlockForHash block = new BlockForHash(this.dtoTransactions,
                    this.previousHash, this.minerAddress, this.founderAddress,
                    this.randomNumberProof, this.minerRewards, this.hashCompexity, this.timestamp, this.index);
            hash = block.hashForTransaction();
            System.out.printf("Trying %d to find a block: ", randomNumberProof);
            Timestamp actualTime = Timestamp.from(Instant.now());
            Long result = actualTime.toInstant().until(previus.toInstant(), ChronoUnit.SECONDS);
//          каждые десять секунд проверяем, что время между текущим и предыдущим запросом не больше 10
            if (result > 10 || result < -10) {
                previus = actualTime;
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
            if (UtilsUse.hashComplexity(hash.substring(0, hashCoplexity), hashCoplexity)) {
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
