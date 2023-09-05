package International_Trade_Union.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;


import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilsBlock {


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
        files = files.stream().sorted().collect(Collectors.toList());
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
        files = files.stream().sorted().collect(Collectors.toList());
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

        for (final File fileEntry : folder.listFiles()) {
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

        for (final File fileEntry : folder.listFiles()) {
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
    public static int difficulty(List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL) {
        //DIFFICULTY_ADJUSTMENT_INTERVAL = 288
        //BLOCK_GENERATION_INTERVAL =  150000 милисекунд
        int difficulty = 1;
        Block latestBlock = blocks.get(blocks.size() - 1);
        if(latestBlock.getIndex() > 576){
            difficulty = UtilsDIfficult.getAdjustedDifficulty(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
//            System.out.println("difficult: " + difficulty + " index: " + latestBlock.getIndex());
        }

        else if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {

            difficulty = UtilsDIfficult.getAdjustedDifficulty(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
            //более умеренная модель сложности


        } else {
            difficulty = latestBlock.getHashCompexity();
        }

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
            return false;

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
                if(thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION) {
                    minerReward = thisBlock.getHashCompexity() * Seting.MONEY;
                    minerPowerReward = thisBlock.getHashCompexity() * Seting.MONEY;
                    minerReward += thisBlock.getIndex()%2 == 0 ? 0 : 1;
                    minerPowerReward += thisBlock.getIndex()%2 == 0 ? 0 : 1;
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
                    if(thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION){
                        if(thisBlock.getHashCompexity() >= 8){
                            if(transaction.getDigitalDollar() != thisBlock.getHashCompexity() ||
                                    thisBlock.getHashCompexity() != transaction.getDigitalStockBalance()){
                                System.out.println("wrong reward founder: index: " + thisBlock.getIndex()
                                        + ":reward dollar: " + transaction.getDigitalDollar() + ": reward stock: "
                                        + transaction.getDigitalStockBalance()
                                        + " difficult: " + thisBlock.getHashCompexity()
                                        + " founder: " + addressFounder);
                                validated = false;
                                break;
                            }
                        }
                        else {
                            if(transaction.getDigitalDollar() != 8 || transaction.getDigitalStockBalance() != 8){
                                System.out.println("wrong reward founder: index: " + thisBlock.getIndex()
                                        + ":reward dollar: " + transaction.getDigitalDollar() + ": reward stock: "
                                        + transaction.getDigitalStockBalance() + " difficult: " + thisBlock.getHashCompexity());
                                validated = false;
                                break;
                            }
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
        if (!UtilsUse.hashComplexity(thisBlock.getHashBlock(), thisBlock.getHashCompexity())) {
            System.out.println("does't start hash with 0");
            System.out.println("this block hash: " + thisBlock.getHashBlock());
            return false;
        }




        if (thisBlock.getIndex() > Seting.NEW_START_DIFFICULT) {

            int diff = UtilsBlock.difficulty(lastBlock, Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
            if (thisBlock.getHashCompexity() < diff - 1) {
                System.out.println("utils Block: actual difficult: " + thisBlock.getHashCompexity() + ":expected: "
                        + diff);
                System.out.println("wrong difficult");
                return false;
            }
        }

        if (!actualPrevHash.equals(recordedPrevHash)) {
            System.out.println("Blockchain is invalid, expected: " + recordedPrevHash + " actual: " + actualPrevHash);
            System.out.println("index block: " + thisBlock.getIndex());
            System.out.println("wrong chain hash");
            return false;
        }


        if (thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION){
            if (previusblock.getMinerAddress().equals(thisBlock.getMinerAddress())) {
                System.out.println("two times in a row the same address cannot mine a block, you need to alternate");
                return false;
            }

            if(previusblock.getIndex()+1 != thisBlock.getIndex()) {
                System.out.println("wrong index sequence");
                return false;
            }
        }


        return validated;
    }

    public static void deleteFiles() {
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BLOCKCHAIN_FILE);
//        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BOARD_0F_SHAREHOLDERS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BALANCE_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.BALANCE_REPORT_ON_DESTROYED_COINS);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.CURRENT_BUDGET_END_EMISSION);


        UtilsFileSaveRead.deleteFile(Seting.TEMPORARY_BLOCKCHAIN_FILE);
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

            if(i < checkIndex){
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
                System.out.println("wrong blockchain missing block: " + index + " index: " + block.getIndex());
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
            validated = validationOneBlock(block.getFounderAddress(),
                    prevBlock,
                    block,
                    BLOCK_GENERATION_INTERVAL,
                    DIFFICULTY_ADJUSTMENT_INTERVAL,
                    tempList);
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
