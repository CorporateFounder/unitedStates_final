package International_Trade_Union.utils;


import International_Trade_Union.model.Account;
import com.fasterxml.jackson.core.JsonProcessingException;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;


import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.setings.Seting.SPECIAL_FORK_BALANCE;

public class UtilsBlock {
    //wallet

    //this need olny find cheater
    public static Map<String, Account> cheater = new HashMap<>();


    public static void saveBlocks(List<Block> blocks, String filename) throws IOException {
        int fileLimit = Seting.SIZE_FILE_LIMIT * 1024 * 1024;

        //папка чтобы проверить есть ли
        File folder = new File(filename);
        List<String> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file.getAbsolutePath());
            }
        }

        int count = 0;
        files = files.stream().sorted(new Comparator<String> () {
            public int compare (String f1, String f2) {
                String [] parts1 = f1.split ("\\D+");
                String [] parts2 = f2.split ("\\D+");
                int len = Math.min (parts1.length, parts2.length);
                for (int i = 0; i < len; i++) {
                    try {
                        int n1 = Integer.parseInt (parts1[i]);
                        int n2 = Integer.parseInt (parts2[i]);
                        if (n1 != n2) {
                            return n1 - n2;
                        }
                    } catch (NumberFormatException e) {
                        // not a number, compare as strings
                        int cmp = parts1[i].compareTo (parts2[i]);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }
                // all equal so far, compare by length
                return parts1.length - parts2.length;
            }
        }).collect(Collectors.toList());
        String nextFile = "";

        if (files.size() > 0) {
            nextFile = files.get(files.size() - 1);

            count = Integer.parseInt(nextFile.replaceAll("[^\\d]", ""));


        }

        File file = new File(nextFile);

        if (file.length() >= fileLimit) {
            count++;

        }

        nextFile = filename + count + ".txt";

        List<String> jsons = new ArrayList<>();
        for (Block block : blocks) {
            String json = UtilsJson.objToStringJson(block);
            jsons.add(json);
        }

//        String json = UtilsJson.objToStringJson(minerAccount);
//        UtilsFileSaveRead.save(json + "\n", nextFile);
        UtilsFileSaveRead.saves(jsons, nextFile, true);
    }

    public static void saveBLock(Block block, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        int fileLimit = Seting.SIZE_FILE_LIMIT * 1024 * 1024;

        //папка чтобы проверить есть ли
        File folder = new File(filename);
        List<String> files = new ArrayList<>();


        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file.getAbsolutePath());
            }
        }


        int count = 0;
        files = files.stream().sorted(new Comparator<String> () {
            public int compare (String f1, String f2) {
                String [] parts1 = f1.split ("\\D+");
                String [] parts2 = f2.split ("\\D+");
                int len = Math.min (parts1.length, parts2.length);
                for (int i = 0; i < len; i++) {
                    try {
                        int n1 = Integer.parseInt (parts1[i]);
                        int n2 = Integer.parseInt (parts2[i]);
                        if (n1 != n2) {
                            return n1 - n2;
                        }
                    } catch (NumberFormatException e) {
                        // not a number, compare as strings
                        int cmp = parts1[i].compareTo (parts2[i]);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }
                // all equal so far, compare by length
                return parts1.length - parts2.length;
            }
        }).collect(Collectors.toList());
        String nextFile = "";

        if (files.size() > 0) {
            nextFile = files.get(files.size() - 1);

            count = Integer.parseInt(nextFile.replaceAll("[^\\d]", ""));


        }

        File file = new File(nextFile);

        if (file.length() >= fileLimit) {
            count++;

        }


        nextFile = filename + count + ".txt";

        String json = UtilsJson.objToStringJson(block);
        UtilsFileSaveRead.save(json + "\n", nextFile);

    }


    public static List<Block> read(String nameFile) throws FileNotFoundException, JsonProcessingException {
        return UtilsJson.jsonToListBLock(UtilsFileSaveRead.read(nameFile));
    }

    public static List<Block> readLineObject(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Block> blocks = new ArrayList<>();
        File folder = new File(filename);

        //******************************************************************
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));
        folders = folders.stream().sorted(new Comparator<File> () {
            public int compare (File f1, File f2) {
                String [] parts1 = f1.getName ().split ("\\D+");
                String [] parts2 = f2.getName ().split ("\\D+");
                int len = Math.min (parts1.length, parts2.length);
                for (int i = 0; i < len; i++) {
                    try {
                        int n1 = Integer.parseInt (parts1[i]);
                        int n2 = Integer.parseInt (parts2[i]);
                        if (n1 != n2) {
                            return n1 - n2;
                        }
                    } catch (NumberFormatException e) {
                        // not a number, compare as strings
                        int cmp = parts1[i].compareTo (parts2[i]);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }
                // all equal so far, compare by length
                return parts1.length - parts2.length;
            }
        }).collect(Collectors.toList());
        //******************************************************************
        for (final File fileEntry : folders) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {

                    Block block = UtilsJson.jsonToBLock(s);
                    blocks.add(block);
                }

            }
        }
        blocks = blocks
                .stream()
                .sorted(Comparator.comparing(Block::getIndex))
                .collect(Collectors.toList());

        return blocks;
    }

    public static Blockchain readBLock(String nameFile, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL, long INTERVAL_TARGET, String ADDRESS_FOUNDER) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Block> blocks = null;
        List<List<Block>> list = new ArrayList<>();

        File folder = new File(nameFile);
        //******************************************************************
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));
        folders = folders.stream().sorted(new Comparator<File> () {
            public int compare (File f1, File f2) {
                String [] parts1 = f1.getName ().split ("\\D+");
                String [] parts2 = f2.getName ().split ("\\D+");
                int len = Math.min (parts1.length, parts2.length);
                for (int i = 0; i < len; i++) {
                    try {
                        int n1 = Integer.parseInt (parts1[i]);
                        int n2 = Integer.parseInt (parts2[i]);
                        if (n1 != n2) {
                            return n1 - n2;
                        }
                    } catch (NumberFormatException e) {
                        // not a number, compare as strings
                        int cmp = parts1[i].compareTo (parts2[i]);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                }
                // all equal so far, compare by length
                return parts1.length - parts2.length;
            }
        }).collect(Collectors.toList());
        //******************************************************************
        for (final File fileEntry : folders) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                blocks = UtilsJson.jsonToListBLock(UtilsFileSaveRead.read(fileEntry.getAbsolutePath()));
                list.add(blocks);
            }
        }

        // new Blockchain(BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL, INTERVAL_TARGET, ADDRESS_FOUNDER);
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        blockchain.setBlockchainList(new ArrayList<>());

        for (List<Block> lists : list) {
            for (int i = 0; i < lists.size(); i++) {
                blockchain.addBlock(lists.get(i));
            }

        }

        List<Block> blockList = blockchain.getBlockchainList()
                .stream()
                .sorted(Comparator.comparing(Block::getIndex))
                .collect(Collectors.toList());
        blockchain.setBlockchainList(blockList);

        return blockchain;
    }

    public static Blockchain readBLock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return readBLock(Seting.TEST_FILE_WRITE_INFO, Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL, Seting.INTERVAL_TARGET, Seting.ADDRESS_FOUNDER);
    }

    public static boolean isValidTimestamp(Block newBlock, Block prevBLock, long timestamp) {
        return (prevBLock.getTimestamp().getTime() - timestamp < newBlock.getTimestamp().getTime())
                && newBlock.getTimestamp().getTime() < System.currentTimeMillis();
    }

    //https://lhartikk.github.io/jekyll/update/2017/07/13/chapter2.html
    //сайт сложности
    //https://lhartikk.github.io/jekyll/update/2017/07/13/chapter2.html
    //https://tproger.ru/translations/blockchain-explained/

    //new https://guicommits.com/building-blockchain-with-python/

    /**
     * определяет сложность, раз пол дня корректирует сложность. В сутках 576 блоков.
     * каждый блок добывается примерно 2.3 минуты
     */
    public static long difficulty(List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL) {
        //DIFFICULTY_ADJUSTMENT_INTERVAL = 288
        //BLOCK_GENERATION_INTERVAL =  150000 милисекунд
        long difficulty = 1;
        Block latestBlock = blocks.get(blocks.size() - 1);
        if (latestBlock.getIndex() > Seting.NEW_START_DIFFICULT - 3
                && latestBlock.getIndex() < Seting.NEW_START_DIFFICULT + 288) {
            difficulty = 3;
            return difficulty;
        } else if (latestBlock.getIndex() > Seting.NEW_START_DIFFICULT + 288 && latestBlock.getIndex() < Seting.CHANGE_MEET_DIFFICULTY) {
            difficulty = UtilsDIfficult.getAdjustedDifficulty(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
        } else if(latestBlock.getIndex() >= Seting.CHANGE_MEET_DIFFICULTY && latestBlock.getIndex() < Seting.CHANGE_MEET_DIFFICULTY + 288){
            difficulty = 3;
        }else if(latestBlock.getIndex() >= Seting.CHANGE_MEET_DIFFICULTY + 288 && latestBlock.getIndex() < Seting.v3MeetsDifficulty){
            difficulty = UtilsDIfficult.getAdjustedDifficultyMedian(latestBlock,
                    blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
        }
        else if(latestBlock.getIndex() > Seting.v3MeetsDifficulty && latestBlock.getIndex() < Seting.v3MeetsDifficulty + 288){
            difficulty = 2;
        }else if(latestBlock.getIndex() >= Seting.v3MeetsDifficulty + 288 &&latestBlock.getIndex() < Seting.v4MeetsDifficulty){
            difficulty = UtilsDIfficult.getAdjustedDifficultyMedian(latestBlock,
                    blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
        } else if (latestBlock.getIndex() >= Seting.v4MeetsDifficulty && latestBlock.getIndex() < Seting.v4MeetsDifficulty + 288) {
            difficulty = 2;
        } else if (latestBlock.getIndex() >= Seting.v4MeetsDifficulty +288 && latestBlock.getIndex() < Seting.V28_CHANGE_ALGORITH_DIFF_INDEX) {


            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {

                difficulty = UtilsDIfficult.v2getAdjustedDifficultyMedian(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        }



        ///*****************************************************************************************
        else if (latestBlock.getIndex() >= Seting.V28_CHANGE_ALGORITH_DIFF_INDEX && latestBlock.getIndex() < Seting.V28_CHANGE_ALGORITH_DIFF_INDEX + 288) {
            difficulty = 1;
        } else if (latestBlock.getIndex() >= Seting.V28_CHANGE_ALGORITH_DIFF_INDEX +288 && latestBlock.getIndex() < Seting.V30_INDEX_ALGO) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v28_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        }

        else if (latestBlock.getIndex() >= Seting.V30_INDEX_ALGO && latestBlock.getIndex() < Seting.V30_INDEX_ALGO + 288) {

            difficulty = 1;
        } else if (latestBlock.getIndex() >= Seting.V30_INDEX_ALGO +288 && latestBlock.getIndex() < Seting.V30_INDEX_DIFF) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        }

        else if (latestBlock.getIndex() >= Seting.V30_INDEX_DIFF && latestBlock.getIndex() < Seting.V30_INDEX_DIFF + 288) {

            difficulty = 1;
        } else if (latestBlock.getIndex() >= Seting.V30_INDEX_ALGO +288 && latestBlock.getIndex() < Seting.V30_INDEX_DIFF) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_1_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        }

        else if (latestBlock.getIndex() >= Seting.V30_1_FIXED_DIFF) {
            System.out.println("algo V30_1_FIXED_DIFF");
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_1_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        }
//        if(Seting.IS_TEST && latestBlock.getIndex() == Seting.V30_INDEX_ALGO -1){
//            difficulty = 1;
//        }


        return difficulty == 0 ? 1 : difficulty;
    }
    public static boolean validationOneBlock(
            String addressFounder,
            Block previusblock,
            Block thisBlock,
            long blockGenerationInterval,
            int difficultyAdjustmentInterval,
            List<Block> lastBlock) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        if (!addressFounder.equals(thisBlock.getFounderAddress())) {
            System.out.println("genesis address not equals block founder: ");
            System.out.println("genesis address: " + addressFounder);
            System.out.println("block address: " + thisBlock.getFounderAddress());
//            return false;

        }
        if(thisBlock.getHashCompexity() < 1){
            System.out.println("wrong: difficulty less 1: " + thisBlock.getHashCompexity());
//            return false;
        }
        if(thisBlock == null){
            System.out.println("wrong: block is null: ");
//            return false;
        }
        if(thisBlock.getHashBlock().isEmpty() || thisBlock.getHashBlock() == null){
            System.out.println("wrong: hash empty or null");
//            return false;
        }
        if(thisBlock.getMinerAddress().isEmpty() || thisBlock.getMinerAddress() == null){
            System.out.println("wrong: miner address empty or null");
//            return false;
        }
        if(thisBlock.getFounderAddress().isEmpty() || thisBlock.getFounderAddress() == null){
            System.out.println("wrong: miner address empty or null");
//            return false;
        }



        String actualPrevHash = previusblock.hashForBlockchain();
        String recordedPrevHash = thisBlock.getPreviousHash();


        boolean validated = true;
        int countBasisSendFounder = 0;
        int countBasisSendAll = 0;
        finished:
        for (DtoTransaction transaction : thisBlock.getDtoTransactions()) {
            if (transaction.verify() && transaction.getSender().equals(Seting.BASIS_ADDRESS)) {
                double minerReward = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
                double minerPowerReward = Seting.DIGITAL_STOCK_REWARDS_BEFORE;
                if (thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION) {
                    minerReward = thisBlock.getHashCompexity() * Seting.MONEY;
                    minerPowerReward = thisBlock.getHashCompexity() * Seting.MONEY;
                    minerReward += thisBlock.getIndex() % 2 == 0 ? 0 : 1;
                    minerPowerReward += thisBlock.getIndex() % 2 == 0 ? 0 : 1;
                }
                if(thisBlock.getIndex() > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX){
                    long money = (thisBlock.getIndex() - Seting.V28_CHANGE_ALGORITH_DIFF_INDEX)
                            / (576 * Seting.YEAR);
                    money = (long) (Seting.MULTIPLIER - money);
                    money = money < 1 ? 1: money;

                    double G = UtilsUse.blocksReward(thisBlock.getDtoTransactions(), previusblock.getDtoTransactions());
                    minerReward = (Seting.V28_REWARD + G) * money;
                    minerPowerReward = (Seting.V28_REWARD + G) * money;

                }

                if (thisBlock.getIndex() == Seting.SPECIAL_BLOCK_FORK && thisBlock.getMinerAddress().equals(Seting.FORK_ADDRESS_SPECIAL)) {
                    minerReward = SPECIAL_FORK_BALANCE;
                    minerPowerReward = SPECIAL_FORK_BALANCE;
                }

                if (transaction.getSender().equals(Seting.BASIS_ADDRESS) &&
                        transaction.getCustomer().equals(thisBlock.getMinerAddress()) && transaction.getDigitalDollar() > minerReward
                        && thisBlock.getIndex() > 1) {
                    System.out.println("wrong transaction: reward miner wrong digital dollar: " + minerReward + " index: " + thisBlock.getIndex());
                    System.out.println("sendmoney " + transaction.getDigitalDollar());
                    validated = false;
                    break;
                }
                if (transaction.getSender().equals(Seting.BASIS_ADDRESS) &&
                        transaction.getCustomer().equals(thisBlock.getMinerAddress()) && transaction.getDigitalStockBalance()
                        > minerPowerReward
                        && thisBlock.getIndex() > 1) {
                    System.out.println("wrong transaction: reward miner wrong digital stock: " + minerPowerReward + " need: " + transaction.getDigitalStockBalance());
                    System.out.println(transaction);
                    validated = false;
                    break;
                }


                if (transaction.getSender().equals(Seting.BASIS_ADDRESS)
                        && transaction.getCustomer().equals(addressFounder)) {
                    countBasisSendFounder += 1;
                    if (thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION && thisBlock.getIndex() <= Seting.V28_CHANGE_ALGORITH_DIFF_INDEX) {
                        if (thisBlock.getHashCompexity() >= 8) {
                            if (transaction.getDigitalDollar() != thisBlock.getHashCompexity() ||
                                    thisBlock.getHashCompexity() != transaction.getDigitalStockBalance()) {
                                System.out.println("wrong reward founder: index: " + thisBlock.getIndex()
                                        + ":reward dollar: " + transaction.getDigitalDollar() + ": reward stock: "
                                        + transaction.getDigitalStockBalance()
                                        + " difficult: " + thisBlock.getHashCompexity()
                                        + " founder: " + addressFounder);
                                validated = false;
                                break;
                            }
                        } else {
                            if (transaction.getDigitalDollar() != 8 || transaction.getDigitalStockBalance() != 8) {
                                System.out.println("wrong reward founder: index: " + thisBlock.getIndex()
                                        + ":reward dollar: " + transaction.getDigitalDollar() + ": reward stock: "
                                        + transaction.getDigitalStockBalance() + " difficult: " + thisBlock.getHashCompexity());
                                validated = false;
                                break;
                            }
                        }

                    }
                    else if(thisBlock.getIndex() > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX){
                        if(transaction.getDigitalDollar() < minerReward/Seting.DOLLAR || transaction.getDigitalDollar() > minerReward){
                            System.out.printf("wrong founder reward dollar: index: %d, " +
                                    " expected : %f, dollar actual: %f: ", thisBlock.getIndex(),
                                    (minerReward/Seting.DOLLAR), transaction.getDigitalDollar());
                            validated = false;
                            break;
                        }
                        if (transaction.getDigitalStockBalance() < minerPowerReward/Seting.STOCK || transaction.getDigitalStockBalance() > minerPowerReward){
                            System.out.printf("wrong founder reward stock: index: %d, " +
                                            " expected : %f, dollar actual: %f: ", thisBlock.getIndex(),
                                    (minerPowerReward/Seting.STOCK), transaction.getDigitalStockBalance());
                            validated = false;
                            break;
                        }
                    }
                }

                if (transaction.getSender().equals(Seting.BASIS_ADDRESS) &&
                        !transaction.getCustomer().equals(addressFounder)) {
                    countBasisSendAll += 1;

                }

                if (countBasisSendFounder > 2 && thisBlock.getIndex() > 1) {
                    System.out.println("basis sender send for founder uper one: " + countBasisSendFounder);
                    validated = false;
                    break;
                }

                if (countBasisSendAll > 1 && thisBlock.getIndex() > 1) {
                    System.out.println("basis sender send uper two: " + countBasisSendAll + " block index: " + thisBlock.getIndex());
                    validated = false;
                    break;
                }
            } else if (!transaction.verify()) {
                System.out.println("wrong transaction: " + transaction + " verify: " + transaction.verify());
                validated = false;
                break finished;
            }

        }

        String target = BlockchainDifficulty.calculateTarget(thisBlock.getHashCompexity());
        BigInteger bigTarget = BlockchainDifficulty.calculateTargetV30(thisBlock.getHashCompexity());
        if (!UtilsUse.chooseComplexity(thisBlock.getHashBlock(), thisBlock.getHashCompexity(), thisBlock.getIndex(), target, bigTarget)) {
            System.out.println("does't start hash with 0");

            System.out.println("this block hash: " + thisBlock.getHashBlock());


            return false;
        }

        if (thisBlock.getIndex() > Seting.v4MeetsDifficulty) {
            long diff = UtilsBlock.difficulty(lastBlock, Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
            if (thisBlock.getHashCompexity() != diff ) {
                System.out.println("utils Block: actual difficult: " + thisBlock.getHashCompexity() + ":expected: "
                        + diff);
                System.out.println("wrong difficult");
                return false;
            }
        }


        if (thisBlock.getIndex() > Seting.NEW_CHECK_UTILS_BLOCK && !thisBlock.getHashBlock().equals(thisBlock.hashForTransaction())) {
            System.out.println("false hash added wrong hash");
            System.out.println("actual: " + thisBlock.getHashBlock());
            System.out.println("expected: " + thisBlock.hashForTransaction());
            System.out.println("miner address: " + thisBlock.getMinerAddress());


//            //for find cheater
//            stop:
//            for (DtoTransaction dtoTransaction : thisBlock.getDtoTransactions()) {
//                if(dtoTransaction.getSender().equals(Seting.BASIS_ADDRESS) &&
//                dtoTransaction.getCustomer().equals(thisBlock.getMinerAddress())){
//                    String address = thisBlock.getMinerAddress();
//                    double dollar = dtoTransaction.getDigitalDollar();
//                    double stock = dtoTransaction.getDigitalStockBalance();
//                    System.out.printf("cheater address %s: stole dollar %f end stock %f: from block index %d ",
//                            address, dollar, stock, thisBlock.getIndex());
//
//
//                    if(cheater.containsKey(address)){
//                        double sumDollar = cheater.get(address).getDigitalDollarBalance() + dollar;
//                        double sumStock = cheater.get(address).getDigitalStockBalance() + stock;
//                        Account account = new Account(address, sumDollar, sumStock);
//                        cheater.put(address, account);
//                    }else {
//                        Account account = new Account(address, dollar, stock);
//                        cheater.put(address, account);
//                    }
//                    break stop;
//                }
//            }

            return false;
        }


        if (!actualPrevHash.equals(recordedPrevHash)) {

            System.out.println("Blockchain is invalid, expected: " + recordedPrevHash + " actual: " + actualPrevHash);
            System.out.println("index block: " + thisBlock.getIndex());
            System.out.println("wrong chain hash");
            return false;
        }


        if (thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION) {
//            if (previusblock.getMinerAddress().equals(thisBlock.getMinerAddress())) {
//                System.out.println("two times in a row the same address cannot mine a block, you need to alternate");
//                return false;
//            }

            if (previusblock.getIndex() + 1 != thisBlock.getIndex()) {
                System.out.println("wrong index sequence");
                return false;
            }
        }


        return validated;
    }

    public static void deleteFiles() {
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_TEMPORARY_BLOCKS);
//        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BOARD_0F_SHAREHOLDERS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BALANCE_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.BALANCE_REPORT_ON_DESTROYED_COINS);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.CURRENT_BUDGET_END_EMISSION);
        UtilsFileSaveRead.deleteAllFiles(Seting.H2_DB);


        UtilsFileSaveRead.deleteFile(Seting.TEMPORARY_BLOCKCHAIN_FILE);
        UtilsFileSaveRead.deleteFile(Seting.ORIGINAL_TEMPORARY_SHORT);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_ALL_CLASSIC_LAWS);

    }

    public static List<DtoTransaction> validDto(List<Block> blocks, List<DtoTransaction> transactions) {

        List<DtoTransaction> transactionArrayList = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            for (DtoTransaction dtoTransaction : blocks.get(i).getDtoTransactions()) {
                transactionArrayList.add(dtoTransaction);
            }

        }
        transactions.removeAll(transactionArrayList);
        return transactions;


    }

    public static boolean validation(List<Block> blocks, int checkIndex, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        boolean validated = true;
        int index = 0;

        Block prevBlock = null;
        boolean haveTwoIndexOne = false;

        List<Block> tempList = new ArrayList<>();
        for (int i = 1; i < blocks.size(); i++) {
            index++;

            if (i < checkIndex) {
                System.out.println("already checked");
                return true;
            }
            Block block = blocks.get(i);


            if (block.getIndex() == 1 && haveTwoIndexOne == false) {
                index = 1;
                haveTwoIndexOne = true;
                block.getHashBlock().equals(Seting.ORIGINAL_HASH);
            }
            if (index != block.getIndex()) {
                System.out.println("method: validation: wrong blockchain missing block: " + index + " index: " + block.getIndex());
                validated = false;
                return validated;
            }
            if (prevBlock == null) {
                prevBlock = block;
//                temporary.add(block);
                continue;
            }


            tempList.add(prevBlock);
            if (tempList.size() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                tempList.remove(0);
            }
//            tempList = tempList.stream().distinct().collect(Collectors.toList());
            validated = validationOneBlock(block.getFounderAddress(),
                    prevBlock,
                    block,
                    BLOCK_GENERATION_INTERVAL,
                    DIFFICULTY_ADJUSTMENT_INTERVAL,
                    tempList);

//            SaveBalances.saveBalances(cheater, "C://testing/cheaters/");
            if (validated == false) {

                System.out.println("ERROR: UtilsBlock: validation: prevBLock.Hash():" + prevBlock.getHashBlock());
                System.out.println("ERROR: UtilsBlock: validation: index:" + block.getIndex());
                System.out.println("ERROR: UtilsBlock: validation: block.Hash():" + block.getHashBlock());
                System.out.println("ERROR: UtilsBlock: validation: BLOCK_GENERATION_INTERVAL:" + BLOCK_GENERATION_INTERVAL);
                System.out.println("ERROR: UtilsBlock: validation: DIFFICULTY_ADJUSTMENT_INTERVAL:" + DIFFICULTY_ADJUSTMENT_INTERVAL);

                return false;
            }

            prevBlock = block;
        }
        return validated;
    }
}
