package International_Trade_Union.entity.blockchain;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.MyLogger;
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
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;


@JsonAutoDetect
@Data
public class Blockchain implements Cloneable {

    private static BlockService blockService;

    public static BlockService getBlockService() {
        return blockService;
    }

    public static void setBlockService(BlockService blockService) {
        Blockchain.blockService = blockService;
    }

    private List<Block> blockchainList;
    //как часто должно создаваться блок в миллисекундах 1000 миллисекунд = 1 секунд
    //каждый блок должен находиться каждые 150 секунд.
    //how often should a block be created in milliseconds 1000 milliseconds = 1 seconds
    //each block should be found every 150 seconds.
    private long BLOCK_GENERATION_INTERVAL;
    //каждые сколько блоков должен происходить перерасчет сложности, каждые 288 блоков происходит регулировка сложности.
    //every number of blocks the difficulty should be recalculated, every 288 blocks the difficulty should be adjusted.
    private int DIFFICULTY_ADJUSTMENT_INTERVAL;
    //блок действителен, если значение блока меньше данного занчения в миллисекунда
    private long INTERVAL_TARGET;

    //адрес основателя.
    //founder's address.
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

    /**
     * Этим методом был создан генезис блок.
     * This method was used to create the genesis block.
     */
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

    /**
     * TODO не используется.
     * TODO is not used.
     */

    public static BigDecimal stakingForDataShort(BigDecimal staking) {
        if (staking.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return staking.divide(BigDecimal.valueOf(Seting.ONE_HUNDRED_THOUSAND), BigDecimal.ROUND_HALF_UP);
    }


    /**
     * Проверяет блок на целостность по отношению к предыдущим блокам.
     * Checks the block for integrity in relation to previous blocks.
     */
    public static DataShortBlockchainInformation shortCheck(
            Block prevBlock, List<Block> blocks,
            DataShortBlockchainInformation data,
            List<Block> tempList,
            Map<String, Account> balances,
            List<String> sign,
            Map<String, Account> balanceForValidation,
            List<String> signaturesNotTakenIntoAccount) {
        int size = (int) data.getSize();

        if (size >= blocks.get(0).getIndex() + 1 || prevBlock == null) {
            System.out.println("size: " + size + blocks.get(0).getIndex());
            System.out.println(" shortCheck: null");
            return new DataShortBlockchainInformation(size, false, 0, BigDecimal.ZERO, 0, 0);
        }

        long hashcount = data.getHashCount();
        BigDecimal staking = data.getStaking();
        int bigRandomNumber = data.getBigRandomNumber();
        long transactions = data.getTransactions();
        boolean validation = false;

        try {
            Block prev = prevBlock.clone();
            List<Block> blockList = new ArrayList<>();

            if (size < Seting.V34_NEW_ALGO) {
                for (Block tempBlock : tempList) {
                    blockList.add(tempBlock.clone());
                }
                blockList = blockList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            }

            System.out.println("shortCheck: blocks size: " + blocks.size() +
                    " 0: " + blocks.get(0).getIndex() + " blocks size: " + blocks.get(blocks.size() - 1).getIndex());
            System.out.println("shortCheck: blockList: size: " + blockList.size());
            System.out.println("shortCheck: tempList: size: " + tempList.size());

            List<String> signs = new ArrayList<>();
            for (Block block : blocks) {
                blockList.add(prev);
                if (blockList.size() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                    blockList.remove(0);
                }

                if (size < Seting.V34_NEW_ALGO) {
                    blockList = blockList.stream()
                            .sorted(Comparator.comparing(Block::getIndex))
                            .collect(Collectors.toList());
                }

                validation = UtilsBlock.validationOneBlock(
                        Seting.ADDRESS_FOUNDER,
                        prev,
                        block,
                        blockList,
                        blockService,
                        balanceForValidation,
                        signs,
                        signaturesNotTakenIntoAccount);
                prev = block.clone();
                size++;

                hashcount += UtilsUse.powerDiff(block.getHashCompexity());
                Account miner = balances.get(block.getMinerAddress());
                miner = miner != null ? miner : new Account(block.getMinerAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                System.out.println("shortCheck miner: " + miner);

                staking = staking.add(stakingForDataShort(miner.getDigitalStakingBalance()));
                bigRandomNumber += UtilsUse.bigRandomWinner(block, miner);
                System.out.println("shortCheck: size: " + block.getIndex() + " validation: " + validation + " size: " + size);

                transactions += block.getDtoTransactions().size();

                balances = UtilsBalance.calculateBalance(balances, block, sign, signaturesNotTakenIntoAccount);
                balanceForValidation = UtilsUse.balancesClone(balances);

                if (!validation) {
                    System.out.println("false shortCheck");
                    return new DataShortBlockchainInformation(size, validation, hashcount, staking, transactions, bigRandomNumber);
                }
            }
        } catch (Exception e) {
            System.out.println("-------------------------------------------");
            e.printStackTrace();
            System.out.println("-------------------------------------------");
        }

        return new DataShortBlockchainInformation(size, validation, hashcount, staking, transactions, bigRandomNumber);
    }


    public static DataShortBlockchainInformation checkFromFile(
            String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        boolean valid = true;
        File folder = new File(filename);
        Block prevBlock = null;
        int size = 0;
        int index = 0;
        long hashCount = 0;
        BigDecimal staking = BigDecimal.ZERO;
        long epoch = 0;
        long transactions = 0;
        int bigRandomNumber = 0;

        Map<String, Account> balances = new HashMap<>();
        List<String> sign = new ArrayList<>();
        List<Block> tempList = new ArrayList<>();
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));

        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            }
        }).collect(Collectors.toList());

        List<String> signs = new ArrayList<>();
        for (final File fileEntry : folders) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                System.out.println("file name: " + fileEntry.getName());
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                    size += 1;
                    index += 1;
                    Block block = null;
                    try {
                        block = UtilsJson.jsonToBLock(s);
                    } catch (JsonProcessingException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    }
                    boolean haveTwoIndexOne = false;
                    if (block.getIndex() == 1 && !haveTwoIndexOne) {
                        index = 1;
                        haveTwoIndexOne = true;
                        block.getHashBlock().equals(Seting.ORIGINAL_HASH);
                    }
                    if (index != block.getIndex()) {
                        System.out.println("1. checkFromFile:wrong blockchain missing block: " + size + " index: " + block.getIndex());
                        valid = false;
                        System.out.println("index: " + index + " block.index: " + block.getIndex());
                        return new DataShortBlockchainInformation(size, valid, hashCount, staking, transactions, bigRandomNumber);
                    }

                    if (prevBlock == null) {
                        prevBlock = block;
                        continue;
                    }

                    hashCount += UtilsUse.powerDiff(block.getHashCompexity());
                    try {
                        balances = UtilsBalance.calculateBalance(balances, block, sign, new ArrayList<>());
                    } catch (IOException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (SignatureException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (InvalidKeySpecException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (NoSuchProviderException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    }
                    Account miner = balances.get(block.getMinerAddress());
                    miner = miner != null ? miner : new Account(block.getMinerAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

                    staking = staking.add(stakingForDataShort(miner.getDigitalStakingBalance()));
                    transactions += block.getDtoTransactions().size();
                    bigRandomNumber += UtilsUse.bigRandomWinner(block, miner);

                    if (size < Seting.V34_NEW_ALGO) {
                        tempList.add(prevBlock);
                        if (tempList.size() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                            tempList.remove(0);
                        }
                    }

                    try {
                        valid = UtilsBlock.validationOneBlock(Seting.ADDRESS_FOUNDER,
                                prevBlock,
                                block,
                                tempList,
                                blockService,
                                UtilsUse.balancesClone(balances),
                                signs,
                                new ArrayList<>());
                    } catch (IOException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (SignatureException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (InvalidKeySpecException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (NoSuchProviderException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    } catch (CloneNotSupportedException e) {
                        MyLogger.saveLog("checkFromFile: ", e);
                        throw new RuntimeException(e);
                    }

                    System.out.println("checkfromfile: index:  " + block.getIndex());
                    if (!valid) {
                        System.out.println("ERROR: UtilsBlock: validation: prevBlock.Hash():" + prevBlock.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: index:" + block.getIndex());
                        System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + block.getHashBlock());
                        System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + Seting.BLOCK_GENERATION_INTERVAL);
                        System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
                        size++;
                        return new DataShortBlockchainInformation(size, valid, hashCount, staking, transactions, bigRandomNumber);
                    }

                    prevBlock = block;
                }
            }
        }

        return new DataShortBlockchainInformation(size, valid, hashCount, staking, transactions, bigRandomNumber);
    }


    public static boolean deletedLastStrFromFile(String temp, int index) throws IOException {
        boolean valid = false;
        File folder = new File(temp);

        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));

        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            }
        }).collect(Collectors.toList());

        for (final File fileEntry : folders) {
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

        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));

        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            }
        }).collect(Collectors.toList());

        for (final File fileEntry : folders) {

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
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));

        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            }
        }).collect(Collectors.toList());

        for (final File fileEntry : folders) {

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
    {
    }


    //название файла, по индексу
    public static File indexNameFileBlock(int index, String filename) throws JsonProcessingException {
        if (index == 0) {
            return new File("0.txt");
        }

        File folder = new File(filename);

//        Arrays.sort(files); // сортируем файлы по имени
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));

        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            }
        }).collect(Collectors.toList());


        int left = 0; // левая граница поиска
        int right = folders.size() - 1; // правая граница поиска
        while (left <= right) { // пока границы не сомкнутся
            int mid = (left + right) / 2; // находим середину
            File file = folders.get(mid); // берем файл в середине
            if (file.isDirectory()) { // если это директория, пропускаем ее
                left = mid + 1;
                continue;
            }
            List<String> list = UtilsFileSaveRead.reads(file.getAbsolutePath()); // читаем содержимое файла
            Block first = UtilsJson.jsonToBLock(list.get(0)); // получаем первый блок в файле
            Block last = UtilsJson.jsonToBLock(list.get(list.size() - 1)); // получаем последний блок в файле
            if (first.getIndex() <= index && index <= last.getIndex()) { // если индекс находится в диапазоне файла
                if (binarySearchBlock(list, index) != null) {
                    return file;
                }
                ; // ищем блок бинарным поиском внутри файла
            } else if (index < first.getIndex()) { // если индекс меньше первого блока в файле
                right = mid - 1; // сдвигаем правую границу налево
            } else { // если индекс больше последнего блока в файле
                left = mid + 1; // сдвигаем левую границу направо
            }
        }
        return null; // если индекс не найден, возвращаем null
    }

    /**
     * удалить файлы блокчейна
     */
    public static void deleteFileBlockchain(int deleteFrom, String directoryPath) {
        File folder = new File(directoryPath);

        File[] files = folder.listFiles();

        if (files != null) {
            for (File f : files) {
                try {
                    int fileNumber = Integer.parseInt(f.getName().replace(".txt", ""));
                    if (fileNumber >= deleteFrom) {
                        boolean deleted = f.delete();
                        if (!deleted) {
                            System.err.println("Не удалось удалить файл: " + f.getName());
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Неверный формат файла: " + f.getName());
                }
            }
        }
    }

    public static Block indexFromFileBing(int index, String filename) throws JsonProcessingException {
        if (index == 0) {
            Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":6.5E7,\"digitalStockBalance\":6.5E7,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIDDW9fKvwUY0aXpvamxOU6pypicO3eCqEVM9LDFrIpjIAiEA81Zh7yCBbJOLrAzx4mg5HS0hMdqvB0obO2CZARczmfY=\"}],\"previousHash\":\"0234a350f4d56ae45c5ece57b08c54496f372bc570bd83a465fb6d2d85531479\",\"minerAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685942742706,\"index\":1,\"hashBlock\":\"08b1e6634457a40d3481e76ebd377e76322706e4ea27013b773686f7df8f8a4c\"}");
            return block;
        }

        File folder = new File(filename);

//        Arrays.sort(files); // сортируем файлы по имени
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));

        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            }
        }).collect(Collectors.toList());


        int left = 0; // левая граница поиска
        int right = folders.size() - 1; // правая граница поиска
        while (left <= right) { // пока границы не сомкнутся
            int mid = (left + right) / 2; // находим середину
            File file = folders.get(mid); // берем файл в середине
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
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));

        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
            }
        }).collect(Collectors.toList());
        for (final File fileEntry : folders) {
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

    public static DataShortBlockchainInformation rollBackShortCheck(
            List<Block> blocks,
            DataShortBlockchainInformation data,
            Map<String, Account> balances
    ) throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        int size = (int) data.getSize();
        long hashcount = data.getHashCount();
        BigDecimal staking = data.getStaking();
        long transactions = data.getTransactions();
        int bigRandomNumber = data.getBigRandomNumber();
        boolean validation = true;

        for (int i = blocks.size() - 1; i >= 0; i--) {
            size--;

            hashcount -= UtilsUse.powerDiff(blocks.get(i).getHashCompexity());
            staking = staking.subtract(stakingForDataShort(balances.get(blocks.get(i).getMinerAddress()).getDigitalStakingBalance()));

            transactions -= blocks.get(i).getDtoTransactions().size();
            bigRandomNumber -= UtilsUse.bigRandomWinner(blocks.get(i), balances.get(blocks.get(i).getMinerAddress()));
            balances = UtilsBalance.rollbackCalculateBalance(balances, blocks.get(i));
        }

        return new DataShortBlockchainInformation(size, validation, hashcount, staking, transactions, bigRandomNumber);
    }


}

