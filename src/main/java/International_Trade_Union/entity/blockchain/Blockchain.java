package International_Trade_Union.entity.blockchain;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@JsonAutoDetect
@Data
public class Blockchain implements Cloneable{
    private List<Block> blockchainList;
    //как часто должно создаваться блок в миллисекундах 1000 миллисекунд = 1 секунд
    private long BLOCK_GENERATION_INTERVAL;
    //каждые сколько блоков должен происходить перерасчет сложности
    private int DIFFICULTY_ADJUSTMENT_INTERVAL;
    //блок действителен, если значение блока меньше данного занчения в миллисекунда
    private long INTERVAL_TARGET;
    private String ADDRESS_FOUNDER;
    public int sizeBlockhain(){

        return blockchainList.size();
    }

    public void setBlockchainList(List<Block> blockchainList) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        this.blockchainList = blockchainList;

    }

    public Blockchain(long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL, long INTERVAL_TARGET, String ADDRESS_FOUNDER) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        this(new ArrayList<>(), BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL, INTERVAL_TARGET, ADDRESS_FOUNDER);

    }
    public Blockchain(List<Block> blockchainList, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL, long INTERVAL_TARGET,String ADDRESS_FOUNDER) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        this.blockchainList = blockchainList;
        this.BLOCK_GENERATION_INTERVAL = BLOCK_GENERATION_INTERVAL;
        this.DIFFICULTY_ADJUSTMENT_INTERVAL = DIFFICULTY_ADJUSTMENT_INTERVAL;
        this.INTERVAL_TARGET = INTERVAL_TARGET;
        this.ADDRESS_FOUNDER = ADDRESS_FOUNDER;
        Block block = genesisBlock();
        addBlock(block);
    }

    public void addBlock(Block newBlock) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
//        if(blockchainList.size() > 2){
//            boolean time = UtilsBlock.isValidTimestamp(blockchainList.get(blockchainList.size()-1), newBlock, INTERVAL_TARGET);
//            if(!time){
//                System.out.println("time out block add " + time);
//               return;
//            }
//        }
        blockchainList.add(newBlock);


    }

    public Block genesisBlock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, InvalidKeyException {
        Base base = new Base58();
        //dto sign
        //pub byte keys
        //transactions
        List<DtoTransaction> transactions = new ArrayList<>();

        DtoTransaction gold = new DtoTransaction(Seting.BASIS_ADDRESS, ADDRESS_FOUNDER,
                Seting.FOUNDERS_REMUNERATION_DIGITAL_DOLLAR, Seting.FOUNDERS_REMNUNERATION_DIGITAL_STOCK, new Laws(), 0.0, VoteEnum.YES);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(Seting.BASIS_PASSWORD));
        byte[] signGold = UtilsSecurity.sign(privateKey, gold.toSign());
        gold.setSign(signGold);
        transactions.add(gold);

        String genesisHash = genesisPrevHash();
        Block block = new Block(transactions,  genesisHash, ADDRESS_FOUNDER, ADDRESS_FOUNDER,  Seting.HASH_COMPLEXITY_GENESIS, blockchainList.size());
        return block;
    }



    public String genesisPrevHash() throws IOException {
        return UtilsUse.hashComplexityStr(Seting.CORPORATE_CHARTER_DRAFT, Seting.HASH_COMPLEXITY_GENESIS);
    }

    public String getHashBlock(int index) throws IOException {
        return blockchainList.get(index).hashForBlockchain();
    }
    public Block getBlock(int index){
        return blockchainList.get(index);
    }



    public boolean validatedBlockchain() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
       return UtilsBlock.validation(blockchainList, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
    }

    public String jsonString() throws IOException {
        return UtilsJson.objToStringJson(blockchainList);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Blockchain)) return false;
        Blockchain that = (Blockchain) o;
        return getBlockchainList().equals(that.getBlockchainList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBlockchainList());
    }

    public  List<Block> subBlock(int startIndex, int finishIndex) throws CloneNotSupportedException {
        List<Block> temporary = this.getBlockchainList().subList(startIndex, finishIndex);
        List<Block> result = new ArrayList<>();
        for (Block block : temporary) {
            result.add(block.clone());
        }
        return result;
    }


    @Override
    public List<Block> clone() throws CloneNotSupportedException {
        List<Block> result = new ArrayList<>();
        for (Block block : blockchainList) {
            result.add(block.clone());
        }
        return result;

    }
}
