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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@JsonAutoDetect
@Data
public final class Block implements Cloneable {
    private static long randomNumberProofStatic = 0;
    private static int INCREMENT_VALUE = 200;
    private static int THREAD_COUNT = 5;

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
        this.hashBlock = findHash(hashCompexity);

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
    private class BlockForHash{
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
            IOException{
        return this.hashBlock;
    }


    public boolean verifyesTransSign() throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        for (DtoTransaction dtoTransaction : dtoTransactions) {
            if(!dtoTransaction.verify())
                return false;
        }
        return true;
    }

    private double miningRewardsCount(){
        double rewards = 0.0;
        for (DtoTransaction dtoTransaction : dtoTransactions) {

            rewards += dtoTransaction.getBonusForMiner();
        }

        return rewards;
    }

    public String jsonString() throws IOException {
        return UtilsJson.objToStringJson(this);
    }

    public String findHashMultiple(int hashComplexity)  {

        try {
            if (!verifyesTransSign()) {
                throw new NotValidTransactionException();
            }

            int increment = 0;
            this.randomNumberProof = randomNumberProofStatic + increment;

            AtomicReference<String> hashRef = new AtomicReference<>("");
            int size = UtilsStorage.getSize();
            Timestamp previous = Timestamp.from(Instant.now());
            AtomicReference<Timestamp> previousTimestamp = new AtomicReference<>(previous);

            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            boolean mining = true;
            while (mining) {
                this.randomNumberProof++;
                final int currentIncrement = increment;

                executor.execute(() -> {
                    BlockForHash block = new BlockForHash(this.dtoTransactions, this.previousHash,
                            this.minerAddress, this.founderAddress, this.randomNumberProof,
                            this.minerRewards, this.hashCompexity, this.timestamp, this.index);

                    String threadHash = null;
                    try {
                        threadHash = block.hashForTransaction();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Timestamp actualTime = Timestamp.from(Instant.now());
                    Long result = actualTime.toInstant().until(previousTimestamp.get().toInstant(), ChronoUnit.SECONDS);
                    if (result > 10 || result < -10) {
                        previousTimestamp.set(actualTime);
                    }

                    int tempSize = UtilsStorage.getSize();
                    if (size < tempSize) {
                        Mining.miningIsObsolete = true;
                        System.out.println("Someone mined a block before you, search is no longer relevant: " + threadHash);
                        return;
                    }

                    if (Mining.isIsMiningStop()) {
                        System.out.println("Mining stopped");
                        return;
                    }

                    if (UtilsUse.hashComplexity(threadHash.substring(0, hashComplexity), hashComplexity)) {
                        System.out.println("Block found: " + threadHash);
                        hashRef.set(threadHash);
                        executor.shutdownNow();
                        return;
                    }
                });

                increment += INCREMENT_VALUE;
            }

            executor.shutdown();
            return hashRef.get();
        } catch (NotValidTransactionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }
    //TODO
    public String findHash(int hashCoplexity) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        if (!verifyesTransSign()){
            throw new NotValidTransactionException();
        }

        this.randomNumberProof = randomNumberProofStatic;
        String hash = "";
        //используется для определения кто-нибудь уже успел добыть блок.
        int size = UtilsStorage.getSize();
        Timestamp previus = Timestamp.from(Instant.now());
        while (true){
            //перебирает число nonce чтобы найти хеш
            this.randomNumberProof++;

            BlockForHash block = new BlockForHash(this.dtoTransactions,
                    this.previousHash, this.minerAddress, this.founderAddress,
                    this.randomNumberProof, this.minerRewards, this.hashCompexity, this.timestamp, this.index);
            hash = block.hashForTransaction();

            Timestamp actualTime = Timestamp.from(Instant.now());
            Long result = actualTime.toInstant().until(previus.toInstant(), ChronoUnit.SECONDS);
//          каждые десять секунд проверяем, что время между текущим и предыдущим запросом не больше 10
            if(result > 10 || result < -10){
                previus = actualTime;
                //проверяет устаревание майнинга, если устарел - прекращает майнинг
                int tempSize = UtilsStorage.getSize();
                if(size < tempSize){
                    Mining.miningIsObsolete = true;
                    System.out.println("someone mined a block before you, the search for this block is no longer relevant and outdated: " + hash);
                    return hash;

                }
            }

            //если true, то прекращаем майнинг
            if(Mining.isIsMiningStop()){
                System.out.println("mining will be stopped");
                return hash;

            }

            //если true, то прекращаем майнинг. Правильный блок найден
            if(UtilsUse.hashComplexity(hash.substring(0, hashCoplexity), hashCoplexity))
            {
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
