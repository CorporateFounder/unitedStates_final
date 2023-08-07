package International_Trade_Union.entity.blockchain.block;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.exception.NotValidTransactionException;
import International_Trade_Union.utils.UtilsJson;
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
import java.util.List;
import java.util.Objects;

@JsonAutoDetect
@Data
public final class Block implements Cloneable {
    private static long randomNumberProofStatic = 0;
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
        this.timestamp = new Timestamp(System.currentTimeMillis());
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
    //TODO
    public String findHash(int hashCoplexity) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        if (!verifyesTransSign()){
            throw new NotValidTransactionException();
        }

        this.randomNumberProof = randomNumberProofStatic;
        String hash = "";
        while (true){
            this.randomNumberProof++;
            System.out.println("A number is selected to generate the correct hash: " + randomNumberProof);
            BlockForHash block = new BlockForHash(this.dtoTransactions,
                    this.previousHash, this.minerAddress, this.founderAddress,
                    this.randomNumberProof, this.minerRewards, this.hashCompexity, this.timestamp, this.index);
            hash = block.hashForTransaction();
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
