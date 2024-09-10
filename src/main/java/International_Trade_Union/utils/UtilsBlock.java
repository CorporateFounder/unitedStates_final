package International_Trade_Union.utils;


import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.MyLogger;
import International_Trade_Union.model.SlidingWindowManager;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;


import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.setings.Seting.*;

public class UtilsBlock {


    private static BlockService blockService;

    public static BlockService getBlockService() {
        return blockService;
    }

    public static void setBlockService(BlockService blockService) {
        UtilsBlock.blockService = blockService;
    }
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
        files = files.stream().sorted(new Comparator<String>() {
            public int compare(String f1, String f2) {
                int n1 = Integer.parseInt(f1.replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
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
        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
        for (Block block : blocks) {
            String json = UtilsJson.objToStringJson(block);
            jsons.add(json);
        }

//        String json = UtilsJson.objToStringJson(minerAccount);
//        UtilsFileSaveRead.save(json + "\n", nextFile);
        UtilsFileSaveRead.saves(jsons, nextFile, true);
    }

    /**
     * Записывает блок в файл, если файл больше 10 мегабайт, создает новый файл с новым идентификационным номером.
     */
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
        files = files.stream().sorted(new Comparator<String>() {
            public int compare(String f1, String f2) {
                int n1 = Integer.parseInt(f1.replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
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
        System.out.println("UtilsBlock: saveBlock: " + block.getIndex() + " file name: " + nextFile);

    }


    public static List<Block> read(String nameFile) throws FileNotFoundException, JsonProcessingException {
        return UtilsJson.jsonToListBLock(UtilsFileSaveRead.read(nameFile));
    }

    public static List<Block> readLineObject(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Block> blocks = new ArrayList<>();
        File folder = new File(filename);

        //******************************************************************
        List<File> folders = new ArrayList<>(List.of(folder.listFiles()));
        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
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
        folders = folders.stream().sorted(new Comparator<File>() {
            public int compare(File f1, File f2) {
                int n1 = Integer.parseInt(f1.getName().replaceAll("\\D+", ""));
                int n2 = Integer.parseInt(f2.getName().replaceAll("\\D+", ""));
                return Integer.compare(n1, n2);
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
        } else if (latestBlock.getIndex() >= Seting.CHANGE_MEET_DIFFICULTY && latestBlock.getIndex() < Seting.CHANGE_MEET_DIFFICULTY + 288) {
            difficulty = 3;
        } else if (latestBlock.getIndex() >= Seting.CHANGE_MEET_DIFFICULTY + 288 && latestBlock.getIndex() < Seting.v3MeetsDifficulty) {
            difficulty = UtilsDIfficult.getAdjustedDifficultyMedian(latestBlock,
                    blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
        } else if (latestBlock.getIndex() > Seting.v3MeetsDifficulty && latestBlock.getIndex() < Seting.v3MeetsDifficulty + 288) {
            difficulty = 2;
        } else if (latestBlock.getIndex() >= Seting.v3MeetsDifficulty + 288 && latestBlock.getIndex() < Seting.v4MeetsDifficulty) {
            difficulty = UtilsDIfficult.getAdjustedDifficultyMedian(latestBlock,
                    blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
        } else if (latestBlock.getIndex() >= Seting.v4MeetsDifficulty && latestBlock.getIndex() < Seting.v4MeetsDifficulty + 288) {
            difficulty = 2;
        } else if (latestBlock.getIndex() >= Seting.v4MeetsDifficulty + 288 && latestBlock.getIndex() < Seting.V28_CHANGE_ALGORITH_DIFF_INDEX) {


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
        } else if (latestBlock.getIndex() >= Seting.V28_CHANGE_ALGORITH_DIFF_INDEX + 288 && latestBlock.getIndex() < Seting.V30_INDEX_ALGO) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v28_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        } else if (latestBlock.getIndex() >= Seting.V30_INDEX_ALGO && latestBlock.getIndex() < Seting.V30_INDEX_ALGO + 288) {

            difficulty = 1;
        } else if (latestBlock.getIndex() >= Seting.V30_INDEX_ALGO + 288 && latestBlock.getIndex() < Seting.V30_INDEX_DIFF) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        } else if (latestBlock.getIndex() >= Seting.V30_INDEX_DIFF && latestBlock.getIndex() < Seting.V30_INDEX_DIFF + 288) {

            difficulty = 1;
        } else if (latestBlock.getIndex() >= Seting.V30_INDEX_ALGO + 288 && latestBlock.getIndex() < Seting.V30_INDEX_DIFF) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_1_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        } else if (latestBlock.getIndex() >= Seting.V30_1_FIXED_DIFF && latestBlock.getIndex() < Seting.V31_DIFF_END_MINING) {

            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_1_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        } else if (latestBlock.getIndex() >= Seting.V31_DIFF_END_MINING && latestBlock.getIndex() < Seting.V31_DIFF_END_MINING + 288) {
            System.out.println("algo V31_FIXED_DIFF");
            difficulty = 14;
        } else if (latestBlock.getIndex() >= Seting.V31_FIX_DIFF && latestBlock.getIndex() <= Seting.V32_FIX_DIFF) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_1_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        } else if (latestBlock.getIndex() > Seting.V32_FIX_DIFF && latestBlock.getIndex() < Seting.V32_FIX_DIFF + 10) {
            difficulty = 16;
        } else if (latestBlock.getIndex() >= Seting.V32_FIX_DIFF + 10) {
            if (latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0) {
                difficulty = UtilsDIfficult.v30_1_changeAlgorith_diff(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
                //более умеренная модель сложности
            } else {
                difficulty = latestBlock.getHashCompexity();
            }
        }

        if (latestBlock.getIndex() > Seting.V31_FIX_DIFF) {
            difficulty = difficulty < 11 ? 11 : difficulty;
        }
//        if(Seting.IS_TEST && latestBlock.getIndex() >= Seting.TEST_DIFF){
//            difficulty = 1;
//        }


        return difficulty == 0 ? 1 : difficulty;
    }

    public static boolean validationOneBlock(
            String addressFounder,
            Block previusblock,
            Block thisBlock,
            List<Block> lastBlock,
            BlockService blockService,
            Map<String, Account> balance,
            List<String> signs) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        Base base = new Base58();
        if (!addressFounder.equals(thisBlock.getFounderAddress())) {
            System.out.println("genesis address not equals block founder: ");
            System.out.println("genesis address: " + addressFounder);
            System.out.println("block address: " + thisBlock.getFounderAddress());
//            return false;

        }
        if (thisBlock.getHashCompexity() < 1) {
            System.out.println("wrong: difficulty less 1: " + thisBlock.getHashCompexity());
//            return false;
        }
        if (thisBlock == null) {
            System.out.println("wrong: block is null: ");
//            return false;
        }
        if (thisBlock.getHashBlock().isEmpty() || thisBlock.getHashBlock() == null) {
            System.out.println("wrong: hash empty or null");
//            return false;
        }
        if (thisBlock.getMinerAddress().isEmpty() || thisBlock.getMinerAddress() == null) {
            System.out.println("wrong: miner address empty or null");
//            return false;
        }
        if (thisBlock.getFounderAddress().isEmpty() || thisBlock.getFounderAddress() == null) {
            System.out.println("wrong: miner address empty or null");
//            return false;
        }


        String actualPrevHash = previusblock.hashForBlockchain();
        String recordedPrevHash = thisBlock.getPreviousHash();


        boolean validated = true;
        int countBasisSendFounder = 0;
        int countBasisSendAll = 0;


        if (thisBlock.getIndex() > Seting.DUBLICATE_IN_ONE_BLOCK_TRANSACTIONS && UtilsUse.getDuplicateTransactions(thisBlock).size() > 0) {
            System.out.println("the block contains transactions with duplicate signatures.");
            List<DtoTransaction> dublicates = UtilsUse.getDuplicateTransactions(thisBlock);
            for (int i = 0; i < dublicates.size(); i++) {
                System.out.println("dublicate: " + dublicates.get(i));
            }

            validated = false;
            return validated;
        }

        if (thisBlock.getIndex() > BALANCE_CHEKING) {
            Map<String, Account> balances = balance;

           List<Block> tempBlock = new ArrayList<>();
           tempBlock.add(previusblock);
           tempBlock.add(thisBlock);


           if(balances == null){
               balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findByDtoAccounts(previusblock.getDtoTransactions()));
               MyLogger.saveLog("validation balance from  null: " + balances);
           }

            List<DtoTransaction> transactions = thisBlock.getDtoTransactions()
                    .stream()
                    .filter(t->!t.getSender().equals(BASIS_ADDRESS))
                    .collect(Collectors.toList());
            int transactionsCount = transactions.size();
            List<DtoTransaction> temp = UtilsUse.balanceTransaction(transactions, balances, thisBlock.getIndex());
            int after = temp.size();


            if (after != transactionsCount) {
                System.out.println("*************************************");
                MyLogger.saveLog("transactions: " + transactions);
                MyLogger.saveLog("balance: " + balance);
                System.out.println("transactionsCount: " + transactionsCount + "\n");
                MyLogger.saveLog("transactionsCount: " + transactionsCount + "\n" + " index: " + thisBlock.getIndex());
                System.out.println("after: " + after + "\n");
                System.out.println("The block contains transactions where the user's balance is insufficient.");
                MyLogger.saveLog("The block contains transactions where the user's balance is insufficient.: index: " + thisBlock.getIndex());
                System.out.println("*************************************");
//                validated = false;
//                return validated;
            }
        }


        if (thisBlock.getIndex() > ALGORITM_MINING_2) {
            for (DtoTransaction dtoTransaction : thisBlock.getDtoTransactions()) {
                if (!dtoTransaction.getCustomer().equals(BASIS_ADDRESS)) {
                    if (dtoTransaction.getDigitalDollar() < MINIMUM
                            && dtoTransaction.getDigitalStockBalance() < MINIMUM
                    ) {
                        System.out.println("*************************************");
                        System.out.println("If a transaction is not a voting transaction, it cannot transfer less than 0.01 of both a dollar and shares at the same time.");
                        System.out.println("index: " + thisBlock.getIndex());
                        System.out.println("transaction: " + dtoTransaction);
                        System.out.println("*************************************");

                        MyLogger.saveLog("************************************");
                        MyLogger.saveLog("If a transaction is not a voting transaction, it cannot transfer less than 0.01 of both a dollar and shares at the same time.");
                        MyLogger.saveLog("index: " + thisBlock.getIndex());
                        MyLogger.saveLog("transaction: " + dtoTransaction);
                        MyLogger.saveLog("************************************");

                        validated = false;
                        return validated;
                    }
                    double digitalDollar = dtoTransaction.getDigitalDollar();
                    double digitalStock = dtoTransaction.getDigitalStockBalance();
                    double digitalBonus = dtoTransaction.getBonusForMiner();
                    if (!UtilsUse.isTransactionValid(BigDecimal.valueOf(digitalDollar))) {
                        System.out.println("the number dollar of decimal places exceeds ." + Seting.SENDING_DECIMAL_PLACES);
                        validated = false;
                        return validated;
                    }
                    if (!UtilsUse.isTransactionValid(BigDecimal.valueOf(digitalStock))) {
                        System.out.println("the number stock of decimal places exceeds ." + Seting.SENDING_DECIMAL_PLACES);
                        validated = false;
                        return validated;
                    }
                    if (!UtilsUse.isTransactionValid(BigDecimal.valueOf(digitalBonus))) {
                        System.out.println("the number bonus of decimal places exceeds ." + Seting.SENDING_DECIMAL_PLACES);
                        validated = false;
                        return validated;
                    }
                }

                if (thisBlock.getIndex() > ALGORITM_MINING_2) {
                    if (dtoTransaction.getVoteEnum().equals(VoteEnum.YES) || dtoTransaction.getVoteEnum().equals(VoteEnum.NO)) {
                        if (dtoTransaction.getSender().equals(dtoTransaction.getCustomer())) {
                            System.out.println("*************************************");
                            System.out.println("dtoSender: The sender and recipient address cannot be the same if VoteEnum.YES or NO");
                            System.out.println("transaction: : " + dtoTransaction);
                            System.out.println("index block: " + thisBlock.getIndex());
                            System.out.println("*************************************");
                            validated = false;
                            return validated;
                        }
                    }
                }
            }

        }
        List<DtoTransaction> transactions = thisBlock.getDtoTransactions();
        transactions = transactions.stream().sorted(Comparator.comparing(t->base.encode(t.getSign()))).collect(Collectors.toList());
        finished:
        for (DtoTransaction transaction : transactions) {
            if (transaction.verify() && transaction.getSender().equals(Seting.BASIS_ADDRESS)) {
                double minerReward = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
                double minerPowerReward = Seting.DIGITAL_STOCK_REWARDS_BEFORE;
                if (thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION) {
                    minerReward = thisBlock.getHashCompexity() * Seting.MONEY;
                    minerPowerReward = thisBlock.getHashCompexity() * Seting.MONEY;
                    minerReward += thisBlock.getIndex() % 2 == 0 ? 0 : 1;
                    minerPowerReward += thisBlock.getIndex() % 2 == 0 ? 0 : 1;
                }
                if (thisBlock.getIndex() > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX && thisBlock.getIndex() <= Seting.V34_NEW_ALGO) {
                    long money = (thisBlock.getIndex() - Seting.V28_CHANGE_ALGORITH_DIFF_INDEX)
                            / (576 * Seting.YEAR);
                    money = (long) (Seting.MULTIPLIER - money);
                    money = money < 1 ? 1 : money;

                    double G = UtilsUse.blocksReward(thisBlock.getDtoTransactions(), previusblock.getDtoTransactions(), thisBlock.getIndex());
                    minerReward = (Seting.V28_REWARD + G) * money;
                    minerPowerReward = (Seting.V28_REWARD + G) * money;

                }
                if (thisBlock.getIndex() > Seting.V34_NEW_ALGO) {
                    long money = (thisBlock.getIndex() - Seting.V28_CHANGE_ALGORITH_DIFF_INDEX)
                            / (576 * Seting.YEAR);
                    money = (long) (Seting.MULTIPLIER - money);
                    money = money < 1 ? 1 : money;

                    double moneyFromDif = 0;
                    double G = UtilsUse.blocksReward(thisBlock.getDtoTransactions(), previusblock.getDtoTransactions(), thisBlock.getIndex());

                    if (thisBlock.getIndex() > Seting.ALGORITM_MINING) {
                        moneyFromDif = (thisBlock.getHashCompexity() - DIFFICULT_MONEY) / 2;
                        moneyFromDif = moneyFromDif > 0 ? moneyFromDif : 0;
                    }

                    minerReward = (Seting.V28_REWARD + G + (thisBlock.getHashCompexity() * Seting.V34_MINING_REWARD) + moneyFromDif) * money;
                    minerPowerReward = (Seting.V28_REWARD + G + (thisBlock.getHashCompexity() * Seting.V34_MINING_REWARD) + moneyFromDif) * money;

                    if(thisBlock.getIndex() > ALGORITM_MINING_2){
                        minerReward += moneyFromDif * (MULT + G);
                        minerPowerReward += moneyFromDif * (MULT + G);
                    }


                    if (thisBlock.getIndex() > Seting.START_BLOCK_DECIMAL_PLACES && thisBlock.getIndex() <= ALGORITM_MINING) {
                        minerReward = UtilsUse.round(minerReward, Seting.DECIMAL_PLACES);
                        minerPowerReward = UtilsUse.round(minerPowerReward, Seting.DECIMAL_PLACES);
                    }


                    if (thisBlock.getIndex() > ALGORITM_MINING) {
                        minerReward = UtilsUse.round(minerReward, SENDING_DECIMAL_PLACES);
                        minerPowerReward = UtilsUse.round(minerPowerReward, SENDING_DECIMAL_PLACES);
                    }
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

                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                    UtilsFileSaveRead.save("If a transaction is not a voting transaction, it cannot transfer less than 0.01 of both a dollar and shares at the same time.", ERROR_FILE, true);
                    UtilsFileSaveRead.save("wrong transaction: reward miner wrong digital dollar: " + minerReward + " index: " + thisBlock.getIndex(), ERROR_FILE, true);
                    UtilsFileSaveRead.save("sendmoney " + transaction.getDigitalDollar(), ERROR_FILE, true);
                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                    validated = false;
                    break;
                }
                if (transaction.getSender().equals(Seting.BASIS_ADDRESS) &&
                        transaction.getCustomer().equals(thisBlock.getMinerAddress()) && transaction.getDigitalStockBalance()
                        > minerPowerReward
                        && thisBlock.getIndex() > 1) {
                    System.out.println("wrong transaction: reward miner wrong digital stock: " + minerPowerReward + " need: " + transaction.getDigitalStockBalance());
                    System.out.println(transaction);

                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                    UtilsFileSaveRead.save("wrong transaction: reward miner wrong digital stock: " + minerPowerReward + " need: " + transaction.getDigitalStockBalance(), ERROR_FILE, true);
                    UtilsFileSaveRead.save(transaction.jsonString(), ERROR_FILE, true);
                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

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


                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                                UtilsFileSaveRead.save("wrong reward founder: index: " + thisBlock.getIndex()
                                        + ":reward dollar: " + transaction.getDigitalDollar() + ": reward stock: "
                                        + transaction.getDigitalStockBalance()
                                        + " difficult: " + thisBlock.getHashCompexity()
                                        + " founder: " + addressFounder, ERROR_FILE, true);
                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                                validated = false;
                                break;
                            }
                        } else {
                            if (transaction.getDigitalDollar() != 8 || transaction.getDigitalStockBalance() != 8) {
                                System.out.println("wrong reward founder: index: " + thisBlock.getIndex()
                                        + ":reward dollar: " + transaction.getDigitalDollar() + ": reward stock: "
                                        + transaction.getDigitalStockBalance() + " difficult: " + thisBlock.getHashCompexity());

                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                                UtilsFileSaveRead.save("wrong reward founder: index: " + thisBlock.getIndex()
                                        + ":reward dollar: " + transaction.getDigitalDollar() + ": reward stock: "
                                        + transaction.getDigitalStockBalance() + " difficult: " + thisBlock.getHashCompexity(), ERROR_FILE, true);
                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                                validated = false;
                                break;
                            }
                        }

                    } else if (thisBlock.getIndex() > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX) {
                        if (thisBlock.getIndex() > START_BLOCK_DECIMAL_PLACES) {
                            double epsilon = 1e-9;  // Define a small margin of error
                            double expectedDollar = minerReward / Seting.DOLLAR;
                            double actualDollar = transaction.getDigitalDollar();
                            double expectedStock = minerPowerReward / Seting.STOCK;
                            double actualStock = transaction.getDigitalStockBalance();

                            if (thisBlock.getIndex() > ALGORITM_MINING) {
                                expectedDollar = UtilsUse.round(expectedDollar, SENDING_DECIMAL_PLACES);
                                expectedStock = UtilsUse.round(expectedStock, SENDING_DECIMAL_PLACES);
                            }

                            if (Math.abs(expectedDollar - actualDollar) > epsilon) {
                                System.out.printf("wrong founder reward dollar: index: %d, expected: %.10f, dollar actual: %.10f\n",
                                        thisBlock.getIndex(), expectedDollar, actualDollar);
                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                                UtilsFileSaveRead.save(String.format("1. wrong founder reward dollar: index: %d, expected: %.10f, dollar actual: %.10f\n",
                                        thisBlock.getIndex(), expectedDollar, actualDollar), ERROR_FILE, true);
                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                                validated = false;
                                break;
                            }

                            if (Math.abs(expectedStock - actualStock) > epsilon) {
                                System.out.printf("wrong founder reward stock: index: %d, expected: %.10f, stock actual: %.10f\n",
                                        thisBlock.getIndex(), expectedStock, actualStock);

                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                                UtilsFileSaveRead.save(String.format("1. wrong founder reward stock: index: %d, expected: %.10f, stock actual: %.10f\n",
                                        thisBlock.getIndex(), expectedStock, actualStock), ERROR_FILE, true);
                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                                validated = false;
                                break;
                            }
                        } else {
                            if (transaction.getDigitalDollar() < minerReward / Seting.DOLLAR || transaction.getDigitalDollar() > minerReward) {
                                System.out.printf("wrong founder reward dollar: index: %d, " +
                                                " expected : %f, dollar actual: %f: ", thisBlock.getIndex(),
                                        (minerReward / Seting.DOLLAR), transaction.getDigitalDollar());

                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                                UtilsFileSaveRead.save(String.format("2. wrong founder reward dollar: index: %d, " +
                                                " expected : %f, dollar actual: %f: ", thisBlock.getIndex(),
                                        (minerReward / Seting.DOLLAR), transaction.getDigitalDollar()), ERROR_FILE, true);
                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                                validated = false;
                                break;
                            }
                            if (transaction.getDigitalStockBalance() < minerPowerReward / Seting.STOCK || transaction.getDigitalStockBalance() > minerPowerReward) {
                                System.out.printf("wrong founder reward stock: index: %d, " +
                                                " expected : %f, dollar actual: %f: ", thisBlock.getIndex(),
                                        (minerPowerReward / Seting.STOCK), transaction.getDigitalStockBalance());

                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                                UtilsFileSaveRead.save(String.format("2. wrong founder reward stock: index: %d, " +
                                                " expected : %f, dollar actual: %f: ", thisBlock.getIndex(),
                                        (minerPowerReward / Seting.STOCK), transaction.getDigitalStockBalance()), ERROR_FILE, true);
                                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
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
                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                    UtilsFileSaveRead.save("basis sender send for founder uper one: " + countBasisSendFounder, ERROR_FILE, true);
                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);


                    validated = false;
                    break;
                }

                if (countBasisSendAll > 1 && thisBlock.getIndex() > 1) {
                    System.out.println("basis sender send uper two: " + countBasisSendAll + " block index: " + thisBlock.getIndex());
                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                    UtilsFileSaveRead.save("basis sender send uper two: " + countBasisSendAll + " block index: " + thisBlock.getIndex(), ERROR_FILE, true);
                    UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                    validated = false;
                    break;
                }
            } else if (!transaction.verify()) {
                System.out.println("wrong transaction: " + transaction + " verify: " + transaction.verify());

                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                UtilsFileSaveRead.save("wrong transaction: " + transaction + " verify: " + transaction.verify(), ERROR_FILE, true);
                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                validated = false;
                break finished;
            }

            if (thisBlock.getIndex() > Seting.DUPLICATE_INDEX) {
                if (blockService != null) {
                    if (blockService.existsBySign(transaction.getSign())) {
                        System.out.println("=====================================");
                        System.out.println("has duplicate transaction");
                        System.out.println("sign: " + base.encode(transaction.getSign()));
                        System.out.println("=====================================");
                        for (DtoTransaction dtoTransaction : thisBlock.getDtoTransactions()) {

                            System.out.println("dto: sign" + base.encode(transaction.getSign()));
                            System.out.println("dto:getSender " + dtoTransaction.getSender());
                            System.out.println("dto:getCustomer " + dtoTransaction.getCustomer());
                            System.out.println("dto:getDigitalDollar " + dtoTransaction.getDigitalDollar());
                            System.out.println("dto:getDigitalStockBalance " + dtoTransaction.getDigitalStockBalance());
                        }

                        UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                        UtilsFileSaveRead.save("has duplicate transaction", ERROR_FILE, true);
                        UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                        System.out.println("=====================================");
                        validated = false;
                        break finished;
                    }else if(thisBlock.getIndex() > Seting.CHECK_DUBLICATE_IN_DB_BLOCK && signs.contains(base.encode(transaction.getSign()))) {
                        MyLogger.saveLog("the transaction already exists in the blockchain: " + base.encode(transaction.getSign()) + " index: " + thisBlock.getIndex());
                        validated = false;
                        break  finished;
                    }
                    else {
                        signs.add(base.encode(transaction.getSign()));
                    }
                }
            }

        }

        String target = BlockchainDifficulty.calculateTarget(thisBlock.getHashCompexity());
        BigInteger bigTarget = BlockchainDifficulty.calculateTargetV30(thisBlock.getHashCompexity());
        if (!UtilsUse.chooseComplexity(thisBlock.getHashBlock(), thisBlock.getHashCompexity(), thisBlock.getIndex(), target, bigTarget)) {
            System.out.println("wrong difficulty hash");

            System.out.println("this block hash: " + thisBlock.getHashBlock());

            UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
            UtilsFileSaveRead.save("wrong difficulty hash", ERROR_FILE, true);
            UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

            return false;
        }

        if (thisBlock.getIndex() > Seting.v4MeetsDifficulty && thisBlock.getIndex() < Seting.V34_NEW_ALGO) {
            long diff = UtilsBlock.difficulty(lastBlock, Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
            if (thisBlock.getHashCompexity() != diff) {
                System.out.println("utils Block: actual difficult: " + thisBlock.getHashCompexity() + ":expected: "
                        + diff);
                System.out.println("wrong difficult");
                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
                UtilsFileSaveRead.save("utils Block: actual difficult: " + thisBlock.getHashCompexity() + ":expected: "
                        + diff, ERROR_FILE, true);
                UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

                return false;
            }
        } else if (thisBlock.getHashCompexity() >= Seting.V34_NEW_ALGO) {
            if (Seting.IS_TEST == false && thisBlock.getHashCompexity() < Seting.V34_MIN_DIFF) {
                System.out.printf("your diff %d, less than it %d\n", thisBlock.getHashCompexity(),
                        Seting.V34_MIN_DIFF);
            }
        }


        if (thisBlock.getIndex() > Seting.NEW_CHECK_UTILS_BLOCK && !thisBlock.getHashBlock().equals(thisBlock.hashForTransaction())) {
            System.out.println("false hash added wrong hash");
            System.out.println("actual: " + thisBlock.getHashBlock());
            System.out.println("expected: " + thisBlock.hashForTransaction());
            System.out.println("miner address: " + thisBlock.getMinerAddress());
            UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
            UtilsFileSaveRead.save("false hash added wrong hash", ERROR_FILE, true);
            UtilsFileSaveRead.save("expected: " + thisBlock.hashForTransaction(), ERROR_FILE, true);
            UtilsFileSaveRead.save("miner address: ", ERROR_FILE, true);
            UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

            return false;
        }


        if (!actualPrevHash.equals(recordedPrevHash)) {
            System.out.println("-------------------------------------------------------");

            System.out.println("Blockchain is invalid, expected: " + recordedPrevHash + " actual: " + actualPrevHash);
            System.out.println("actual index block: " + thisBlock.getIndex());

            System.out.println("previusblock: " + previusblock.getIndex());
            System.out.println("wrong chain hash");
            System.out.println("-------------------------------------------------------");

            UtilsFileSaveRead.save("************************************", ERROR_FILE, true);
            UtilsFileSaveRead.save("Blockchain is invalid, expected: " + recordedPrevHash + " actual: " + actualPrevHash, ERROR_FILE, true);
            UtilsFileSaveRead.save("expected: " + thisBlock.hashForTransaction(), ERROR_FILE, true);
            UtilsFileSaveRead.save("miner address: ", ERROR_FILE, true);
            UtilsFileSaveRead.save("************************************", ERROR_FILE, true);

            return false;
        }


        if (thisBlock.getIndex() > Seting.CHECK_UPDATING_VERSION) {

            if (previusblock.getIndex() + 1 != thisBlock.getIndex()) {
                System.out.println("wrong index sequence");
                return false;
            }
        }

        long timeDifferenceSeconds = (thisBlock.getTimestamp().getTime() - previusblock.getTimestamp().getTime()) / 1000;

        if (IS_SECURITY == true && thisBlock.getIndex() >= Seting.TIME_CHECK_BLOCK) {
            long actualTime = UtilsTime.getUniversalTimestamp() / 1000L;
            if (timeDifferenceSeconds < 100 || timeDifferenceSeconds > actualTime) {
                // Время thisBlock не соответствует условиям
                System.out.println("----------------------------------------------");
                System.out.println("wrong time different: " + timeDifferenceSeconds + " index: " + thisBlock.getIndex());
                System.out.println("wrong time different: " + timeDifferenceSeconds + " prev index: " + previusblock.getIndex());
                System.out.println("----------------------------------------------");
                return false;
            }
        }
        // Проверяем условия

        return validated;
    }


    public static void deleteFiles() {
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_TEMPORARY_BLOCKS);
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

    public static boolean validation(List<Block> blocks, int checkIndex, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL, BlockService blockService) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        boolean validated = true;
        int index = 0;

        Block prevBlock = null;
        boolean haveTwoIndexOne = false;
        Map<String, Account> balanceForValidation = new HashMap<>();
        List<Block> tempList = new ArrayList<>();
        List<String> signs = new ArrayList<>();
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

            balanceForValidation = UtilsBalance.calculateBalance(balanceForValidation, block, new ArrayList<>());
            validated = validationOneBlock(block.getFounderAddress(),
                    prevBlock,
                    block,
                    tempList,
                    blockService,
                    balanceForValidation,
                    signs);

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
