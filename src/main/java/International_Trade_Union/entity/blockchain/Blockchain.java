package International_Trade_Union.entity.blockchain;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;


@JsonAutoDetect
@Data
public class Blockchain implements Cloneable {

    private List<Block> blockchainList;
    //как часто должно создаваться блок в миллисекундах 1000 миллисекунд = 1 секунд
    private long BLOCK_GENERATION_INTERVAL;
    //каждые сколько блоков должен происходить перерасчет сложности
    private int DIFFICULTY_ADJUSTMENT_INTERVAL;
    //блок действителен, если значение блока меньше данного занчения в миллисекунда
    private long INTERVAL_TARGET;
    private String ADDRESS_FOUNDER;

    public int sizeBlockhain() {

        return blockchainList.size();
    }

    public void setBlockchainList(List<Block> blockchainList) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        this.blockchainList = blockchainList;

    }

    public Blockchain(long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL, long INTERVAL_TARGET, String ADDRESS_FOUNDER) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        this(new ArrayList<>(), BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL, INTERVAL_TARGET, ADDRESS_FOUNDER);

    }

    public Blockchain(List<Block> blockchainList, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL, long INTERVAL_TARGET, String ADDRESS_FOUNDER) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        this.blockchainList = blockchainList;
        this.BLOCK_GENERATION_INTERVAL = BLOCK_GENERATION_INTERVAL;
        this.DIFFICULTY_ADJUSTMENT_INTERVAL = DIFFICULTY_ADJUSTMENT_INTERVAL;
        this.INTERVAL_TARGET = INTERVAL_TARGET;
        this.ADDRESS_FOUNDER = ADDRESS_FOUNDER;
        Block block = genesisBlock();
        addBlock(block);
    }

    public void addBlock(Block newBlock) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        blockchainList.add(newBlock);
    }

    public Block genesisBlock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, InvalidKeyException {
        Base base = new Base58();
        //dto sign
        //pub byte keys
        //transactions
        List<DtoTransaction> transactions = new ArrayList<>();

        DtoTransaction gold = new DtoTransaction(Seting.BASIS_ADDRESS, Seting.ADDRESS_FOUNDER,
                Seting.FOUNDERS_REMUNERATION_DIGITAL_DOLLAR, Seting.FOUNDERS_REMNUNERATION_DIGITAL_STOCK, new Laws(), 0.0, VoteEnum.YES);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(Seting.BASIS_PASSWORD));
        byte[] signGold = UtilsSecurity.sign(privateKey, gold.toSign());
        gold.setSign(signGold);
        transactions.add(gold);


        String genesisHash = genesisPrevHash();
        Block block = new Block(transactions, genesisHash, ADDRESS_FOUNDER, ADDRESS_FOUNDER, Seting.HASH_COMPLEXITY_GENESIS, blockchainList.size());
        return block;
    }

    public static Map<String, Object> shortCheck2(Block prevBlock, Block block, DataShortBlockchainInformation data, List<Block> tempList) throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Object> map = new HashMap<>();
        int size = (int) data.getSize();
        if (size >= block.getIndex() + 1 || prevBlock == null) {


            map.put("block", block);
            map.put("data", new DataShortBlockchainInformation(size, false, 0));
            return map;
        }
        long hashcount = data.getHashCount();
        boolean validation = false;
        Block prev = prevBlock.clone();


        System.out.println("block index: " + block.getIndex());


        validation = UtilsBlock.validationOneBlock(
                Seting.ADDRESS_FOUNDER,
                prev,
                block,
                Seting.BLOCK_GENERATION_INTERVAL,
                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                tempList);

        size++;


        hashcount += UtilsUse.hashCount(block.getHashBlock(), block.getIndex());
        if (validation == false) {
            System.out.println("false shorkCheck");
            map.put("block", block);
            map.put("data", new DataShortBlockchainInformation(size, validation, hashcount));
            return map;

        }


        map.put("block", block);
        map.put("data", new DataShortBlockchainInformation(size, validation, hashcount));
        return map;

    }

    public static DataShortBlockchainInformation shortCheck(Block prevBlock, List<Block> blocks, DataShortBlockchainInformation data, List<Block> tempList) throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        int size = (int) data.getSize();
        if (size >= blocks.get(0).getIndex() + 1 || prevBlock == null) {
            System.out.println("size: " + size + blocks.get(0).getIndex());
            System.out.println(" shortCheck: null");
            return new DataShortBlockchainInformation(size, false, 0);
        }
        long hashcount = data.getHashCount();
        boolean validation = false;
        Block prev = prevBlock.clone();
        List<Block> blockList = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            blockList.add(tempList.get(i).clone());
        }

        for (int i = 0; i < blocks.size(); i++) {
            blockList.add(prev);
            if (blockList.size() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                blockList.remove(0);
            }

            blockList = blockList.stream()
                    .sorted(Comparator.comparing(Block::getIndex))
                    .collect(Collectors.toList());
            validation = UtilsBlock.validationOneBlock(
                    Seting.ADDRESS_FOUNDER,
                    prev,
                    blocks.get(i),
                    Seting.BLOCK_GENERATION_INTERVAL,
                    Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                    blockList);
            prev = blocks.get(i).clone();
            size++;


            System.out.println("size: " + blockList.size());
            hashcount += UtilsUse.hashCount(blocks.get(i).getHashBlock(), blocks.get(i).getIndex());
            if (validation == false) {
                System.out.println("false shortCheck");
                return new DataShortBlockchainInformation(size, validation, hashcount);
            }

        }

        return new DataShortBlockchainInformation(size, validation, hashcount);

    }

    public static DataShortBlockchainInformation checkEqualsFromToBlockFile(String fileName, List<Block> blocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        boolean valid = true;
        File folder = new File(fileName);
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
                    if (block.getIndex() == 0) {
                        for (DtoTransaction transaction : block.getDtoTransactions()) {
                            if (transaction.getSender().equals(Seting.BASIS_ADDRESS)
                                    && transaction.getCustomer().equals(Seting.ADDRESS_FOUNDER)) {
                                if (transaction.getDigitalDollar() != Seting.FOUNDERS_REMUNERATION_DIGITAL_DOLLAR) {
                                    valid = false;
                                    return new DataShortBlockchainInformation(size, valid, hashCount);
                                }
                            }
                        }

                    }
                    if (prevBlock == null) {
                        prevBlock = block;
                        continue;
                    }
                    hashCount += UtilsUse.hashCount(block.getHashBlock(), block.getIndex());
                    valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                            prevBlock,
                            block,
                            Seting.BLOCK_GENERATION_INTERVAL,
                            Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                            new ArrayList<>());

                    if (valid == false) {
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
        System.out.println("Blockchain: checkEqualsFromToBlockFile: size: " + size
                + " blocks.getIndex + 1: " + (blocks.get(0).getIndex() + 1));
        if (size < (blocks.get(0).getIndex() + 1)) {
            for (Block block : blocks) {
                size += 1;


                if (prevBlock == null) {
                    prevBlock = block;
                    continue;
                }
                hashCount += UtilsUse.hashCount(block.getHashBlock(), block.getIndex());
                valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                        prevBlock,
                        block,
                        Seting.BLOCK_GENERATION_INTERVAL,
                        Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                        new ArrayList<>());

                if (valid == false) {
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


        return new DataShortBlockchainInformation(size, valid, hashCount);
    }


    public static boolean saveBalanceFromfile(String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        boolean valid = true;
        File folder = new File(filename);

        Map<String, Account> balances = new HashMap<>();
        List<String> signs = new ArrayList<>();
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
                System.out.println("is directory " + fileEntry.getName());
            } else {

                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {

                    Block block = UtilsJson.jsonToBLock(s);

                    UtilsBalance.calculateBalance(balances, block, signs);
                    balances = UtilsBalance.calculateBalanceFromLaw(balances, block, allLaws, allLawsWithBalance);
//

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
        int index = 0;
        long hashCount = 0;

        List<Block> tempList = new ArrayList<>();
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));
        folders = folders.stream().sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());

        for (final File fileEntry : folders) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {

                    size += 1;
                    index += 1;
                    Block block = UtilsJson.jsonToBLock(s);
                    boolean haveTwoIndexOne = false;
                    if (block.getIndex() == 1 && haveTwoIndexOne == false) {
                        index = 1;
                        haveTwoIndexOne = true;
                        block.getHashBlock().equals(Seting.ORIGINAL_HASH);
                    }
                    if (index != block.getIndex()) {
                        System.out.println("wrong blockchain missing block: " + size + " index: " + block.getIndex());
                        valid = false;
                        return new DataShortBlockchainInformation(size, valid, hashCount);
                    }

                    if (prevBlock == null) {
                        prevBlock = block;
                        continue;
                    }

                    hashCount += UtilsUse.hashCount(block.getHashBlock(), block.getIndex());

                    tempList.add(prevBlock);
                    if (tempList.size() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                        tempList.remove(0);
                    }
                    valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                            prevBlock,
                            block,
                            Seting.BLOCK_GENERATION_INTERVAL,
                            Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                            tempList);

                    if (valid == false) {
                        System.out.println("ERROR: UtilsBlock: validation: prevBLock.Hash():" + prevBlock.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: index:" + block.getIndex());
                        System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + block.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + Seting.BLOCK_GENERATION_INTERVAL);
                        System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
                        size++;
                        return new DataShortBlockchainInformation(size, valid, hashCount);
                    }

                    prevBlock = block;

                }

            }
        }

        return new DataShortBlockchainInformation(size, valid, hashCount);
    }

    public static boolean deletedLastStrFromFile(String temp, int index) throws IOException {
        boolean valid = false;
        File folder = new File(temp);


        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {

                valid = UtilsFileSaveRead.deleted(fileEntry.getAbsolutePath(), index);
                System.out.println("deletedLastStrFromFile: " + valid + " index: " + index +
                        " :" + fileEntry.getName());
                if (valid) {
                    System.out.println("deletedLastStrFromFile: " + valid);
                    break;
                }
            }
        }


        return valid;
    }

    public static Block hashFromFile(String hash, String filename) throws JsonProcessingException {
        File folder = new File(filename);
        Block block = null;


        for (final File fileEntry : folder.listFiles()) {

            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {

                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                    block = UtilsJson.jsonToBLock(s);

                    if (block.getHashBlock() == hash) {
                        return block;
                    }
                }
            }
        }
        return block;
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

                    if (index == size) {
                        block = UtilsJson.jsonToBLock(s);


                        if (block.getIndex() == size) {


                            return block;
                        }
                    }

                    size++;
                }

            }
        }


        return block;
    }
    // Константа для размера буфера
    public static Block indexFromFileBing(int index, String filename) throws JsonProcessingException {
        if(index == 0){
            Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":6.5E7,\"digitalStockBalance\":6.5E7,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIDDW9fKvwUY0aXpvamxOU6pypicO3eCqEVM9LDFrIpjIAiEA81Zh7yCBbJOLrAzx4mg5HS0hMdqvB0obO2CZARczmfY=\"}],\"previousHash\":\"0234a350f4d56ae45c5ece57b08c54496f372bc570bd83a465fb6d2d85531479\",\"minerAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685942742706,\"index\":1,\"hashBlock\":\"08b1e6634457a40d3481e76ebd377e76322706e4ea27013b773686f7df8f8a4c\"}");
            return block;
        }

        File folder = new File(filename);
        File[] files = folder.listFiles(); // получаем массив файлов
        Arrays.sort(files); // сортируем файлы по имени
        int left = 0; // левая граница поиска
        int right = files.length - 1; // правая граница поиска
        while (left <= right) { // пока границы не сомкнутся
            int mid = (left + right) / 2; // находим середину
            File file = files[mid]; // берем файл в середине
            if (file.isDirectory()) { // если это директория, пропускаем ее
                left = mid + 1;
                continue;
            }
            List<String> list = UtilsFileSaveRead.reads(file.getAbsolutePath()); // читаем содержимое файла
            Block first = UtilsJson.jsonToBLock(list.get(0)); // получаем первый блок в файле
            Block last = UtilsJson.jsonToBLock(list.get(list.size() - 1)); // получаем последний блок в файле
            if (first.getIndex() <= index && index <= last.getIndex()) { // если индекс находится в диапазоне файла
                return binarySearchBlock(list, index); // ищем блок бинарным поиском внутри файла
            } else if (index < first.getIndex()) { // если индекс меньше первого блока в файле
                right = mid - 1; // сдвигаем правую границу налево
            } else { // если индекс больше последнего блока в файле
                left = mid + 1; // сдвигаем левую границу направо
            }
        }
        return null; // если индекс не найден, возвращаем null
    }
    // метод для бинарного поиска блока в списке строк с json-объектами
    public static Block binarySearchBlock(List<String> list, int index) throws JsonProcessingException {
        int left = 0; // левая граница поиска
        int right = list.size() - 1; // правая граница поиска
        while (left <= right) { // пока границы не сомкнутся
            int mid = (left + right) / 2; // находим середину
            String s = list.get(mid); // берем строку в середине
            Block block = UtilsJson.jsonToBLock(s); // преобразуем ее в блок
            if (block.getIndex() == index) { // если индекс совпадает с искомым
                return block; // возвращаем блок
            } else if (index < block.getIndex()) { // если индекс меньше блока в середине
                right = mid - 1; // сдвигаем правую границу налево
            } else { // если индекс больше блока в середине
                left = mid + 1; // сдвигаем левую границу направо
            }
        }
        return null; // если индекс не найден, возвращаем null
    }
    public static boolean compareLists(List<Block> list1, List<Block> list2) {

        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }
    public static List<Block> subFromFileBing(int indexFrom, int indexTo, String filename) throws JsonProcessingException {
        List<Block> blocks = new ArrayList<>();
        for (int i = indexFrom; i < indexTo; i++) {
            Block temp = indexFromFileBing(i, filename);
            blocks.add(temp);
        }
        return blocks;
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

                    if (size >= indexFrom && size < indexTo) {
                        block = UtilsJson.jsonToBLock(s);
                        if (block.getIndex() >= indexFrom && block.getIndex() < indexTo) {
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

    public Block getBlock(int index) {
        return blockchainList.get(index);
    }


    public boolean validatedBlockchain() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
//        Blockchain blockchain = Mining.getBlockchain(
//                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                BlockchainFactoryEnum.ORIGINAL);
        return UtilsBlock.validation(blockchainList, 0, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
    }

    public boolean validatedBlockchain(int index) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        return UtilsBlock.validation(blockchainList, index, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
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

    public List<Block> subBlock(int startIndex, int finishIndex) throws CloneNotSupportedException {
        List<Block> temporary = this.getBlockchainList().subList(startIndex, finishIndex);
        List<Block> result = new ArrayList<>();
        for (Block block : temporary) {
            result.add(block.clone());
        }
        return result;
    }

    public static List<Block> clone(int start, int finish, List<Block> blocks) throws CloneNotSupportedException {
        List<Block> list = new ArrayList<>();
        for (int i = start; i < finish; i++) {
            list.add(blocks.get(i).clone());
        }
        return list;
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
