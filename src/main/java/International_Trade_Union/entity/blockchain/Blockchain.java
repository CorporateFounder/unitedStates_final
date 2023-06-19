package International_Trade_Union.entity.blockchain;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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

    public static DataShortBlockchainInformation checkEqualsFromFile(String fileName, List<Block> blocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        boolean valid = true;
        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex))
                .collect(Collectors.toList());
        File folder = new File(fileName);
        Block prevBlock = null;
        int size = 0;
        long hashCount = 0;
        boolean isStart = false;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                    size += 1;
                    Block block = UtilsJson.jsonToBLock(s);
                    if(prevBlock == null){
                        prevBlock = block;
                        continue;
                    }

                    //момент откуда начинается проверка

                    if(block.getIndex() == blocks.get(0).getIndex() || isStart){
                        isStart = true;
                        block = blocks.get((int) block.getIndex());
                        valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                                prevBlock,
                                block,
                                Seting.BLOCK_GENERATION_INTERVAL,
                                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                                new ArrayList<>());
                    }else {
                        valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                                prevBlock,
                                block,
                                Seting.BLOCK_GENERATION_INTERVAL,
                                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                                new ArrayList<>());
                    }

                    if(valid == false){
                        System.out.println("ERROR: UtilsBlock: validation: prevBLock.Hash():" + prevBlock.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: index:" + block.getIndex());
                        System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + block.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + Seting.BLOCK_GENERATION_INTERVAL);
                        System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
                        return new DataShortBlockchainInformation(size, valid, hashCount);
                    }
                    hashCount += UtilsUse.hashCount(block.getHashBlock());

                    prevBlock = block;


                }

            }
        }
        //если блокчейн внеший выше текущего
        if(size-1 < blocks.get(blocks.size()-1).getIndex()){
            System.out.println("Blockchain: checkEqualsFromFile: start");
            for (int i = size; i < blocks.get(blocks.size()-1).getIndex(); i++) {
                size += 1;
                if(prevBlock == null){
                    prevBlock = blocks.get(i);
                    continue;
                }
                System.out.println("Blockchain: checkEqualsFromFile: size: " + size);
                valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                        prevBlock,
                        blocks.get(i),
                        Seting.BLOCK_GENERATION_INTERVAL,
                        Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                        new ArrayList<>());

                if(valid == false){
                    System.out.println("ERROR: UtilsBlock: validation: prevBLock.Hash():" + prevBlock.getHashBlock());
                    System.out.println("ERROR: UtilsBlock: validation: index:" + blocks.get(i).getIndex());
                    System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + blocks.get(i).getHashBlock());
                    System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + Seting.BLOCK_GENERATION_INTERVAL);
                    System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
                    return new DataShortBlockchainInformation(size, valid, hashCount);
                }
                hashCount  += UtilsUse.hashCount(blocks.get(i).getHashBlock());
                prevBlock = blocks.get(i);
            }

        }

        return new DataShortBlockchainInformation(size, valid, hashCount);
    }

    public static DataShortBlockchainInformation checkEqualsFromFileSave(String fileName, List<Block> blocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        boolean valid = true;
        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex))
                .collect(Collectors.toList());
        File folder = new File(fileName);
        Block prevBlock = null;
        int size = 0;
        long hashCount = 0;
        boolean isStart = false;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                    size += 1;
                    Block block = UtilsJson.jsonToBLock(s);
                    if(prevBlock == null){
                        prevBlock = block;
                        continue;
                    }

                    //момент откуда начинается проверка

                    if(block.getIndex() == blocks.get(0).getIndex() || isStart){
                        isStart = true;
                        block = blocks.get((int) block.getIndex());
                        valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                                prevBlock,
                                block,
                                Seting.BLOCK_GENERATION_INTERVAL,
                                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                                new ArrayList<>());
                    }else {
                        valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                                prevBlock,
                                block,
                                Seting.BLOCK_GENERATION_INTERVAL,
                                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                                new ArrayList<>());
                    }

                    if(valid == false){
                        System.out.println("ERROR: UtilsBlock: validation: prevBLock.Hash():" + prevBlock.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: index:" + block.getIndex());
                        System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + block.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + Seting.BLOCK_GENERATION_INTERVAL);
                        System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
                        return new DataShortBlockchainInformation(size, valid, hashCount);
                    }
                    hashCount += UtilsUse.hashCount(block.getHashBlock());

                    prevBlock = block;


                }

            }
        }
        //если блокчейн внеший выше текущего
        if(size < blocks.get(blocks.size()-1).getIndex()){
            for (Block block : blocks) {
                size += 1;
                if(prevBlock == null){
                   prevBlock = block;
                   continue;
                }
                valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                        prevBlock,
                        block,
                        Seting.BLOCK_GENERATION_INTERVAL,
                        Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                        new ArrayList<>());

                if(valid == false){
                    System.out.println("ERROR: UtilsBlock: validation: prevBLock.Hash():" + prevBlock.getHashBlock());
                    System.out.println("ERROR: UtilsBlock: validation: index:" + block.getIndex());
                    System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + block.getHashBlock());
                    System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + Seting.BLOCK_GENERATION_INTERVAL);
                    System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
                    return new DataShortBlockchainInformation(size, valid, hashCount);
                }
                hashCount  += UtilsUse.hashCount(block.getHashBlock());
                prevBlock = block;
            }

        }

        return new DataShortBlockchainInformation(size, valid, hashCount);
    }

    public static boolean deletedLastStrFromFile(int index, String filename) throws IOException {
        boolean valid = false;
        File folder = new File(filename);

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                   valid =  UtilsFileSaveRead.deleted(index, s, s+"temp");
                    if (valid){
                        break;
                    }
                }

            }
        }


        return valid;
    }
    public static DataShortBlockchainInformation checkFromFile(
            String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        boolean valid = true;
        File folder = new File(filename);
        Block prevBlock = null;
        int size = 0;
         long hashCount = 0;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                    size += 1;

                    Block block = UtilsJson.jsonToBLock(s);
                    if(prevBlock == null){
                        prevBlock = block;
                        continue;
                    }
                    hashCount += UtilsUse.hashCount(block.getHashBlock());
                    valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                            prevBlock,
                            block,
                            Seting.BLOCK_GENERATION_INTERVAL,
                            Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                            new ArrayList<>());

                    if(valid == false){
                        System.out.println("ERROR: UtilsBlock: validation: prevBLock.Hash():" + prevBlock.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: index:" + block.getIndex());
                        System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + block.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + Seting.BLOCK_GENERATION_INTERVAL);
                        System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
                        return new DataShortBlockchainInformation(size, valid, hashCount);
                    }

                    prevBlock = block;

                }

            }
        }


        return new DataShortBlockchainInformation(size, valid, hashCount);
    }

    public static Block indexFromFile(int index, String filename) throws JsonProcessingException {
        File folder = new File(filename);
        Block block = null;
        int size = 0;

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {

                    if(size == index){
                        block = UtilsJson.jsonToBLock(s);
                        if(index == block.getIndex())
                            return block;
                    }

                    size += 1;
                }

            }
        }


        return block;
    }
    public static List<Block> subFromFile(int indexFrom, int indexTo, String filename) throws JsonProcessingException {
        File folder = new File(filename);
        Block block = null;
        int size = 0;
        List<Block> blocks = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {

                    if(size >= indexFrom &&  size < indexTo){
                        block = UtilsJson.jsonToBLock(s);
                        if( block.getIndex() >= indexFrom && block.getIndex() < indexTo) {
                            blocks.add(block);
                        }

                    }

                    size += 1;
                }

            }
        }

        return blocks;
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
//        Blockchain blockchain = Mining.getBlockchain(
//                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                BlockchainFactoryEnum.ORIGINAL);
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
