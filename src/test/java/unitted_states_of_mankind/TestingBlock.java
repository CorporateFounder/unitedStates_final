package unitted_states_of_mankind;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.exception.NotValidTransactionException;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;

import International_Trade_Union.model.MyLogger;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;

import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;

import java.io.IOException;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static International_Trade_Union.setings.Seting.*;

@JsonAutoDetect
@Data
public final class TestingBlock implements Cloneable {
    //мой класс
    private static long randomNumberProofStatic = 0;
    Set<String> ORIGINAL_ADDRESSES = new HashSet<>(Arrays.asList("http://194.87.236.238:82"));
    public static String ADDRESS_HTTP = "http://194.87.236.238:82";
    String SERVER = "";
    String POOL_ADDRESS = "POOL ADDRESS";
    private static ObjectMapper mapper = new ObjectMapper();
    private static int START_BLOCK_DECIMAL_PLACES = 268765;
    private static boolean isStopMining = false;
    private static int SENDING_DECIMAL_PLACES_2 = 2;
    private static String NAME_LAW_ADDRESS_START = "LIBER";
    private static String BASIS_ADDRESS = "faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ";
    private static String BASIS_PASSWORD = "3hupFSQNWwiJuQNc68HiWzPgyNpQA2yy9iiwhytMS7rZyfPddNRwtvExeevhayzN6xL2YmTXN6NCA8jBhV9ge1w8KciHedGUMgZyq2T7rDdvekVNwEgf5pQrELv8VAEvQ4Kb5uviXJFuMyuD1kRAGExrZym5nppyibEVnTC9Uiw8YzUh2JmVT9iUajnVV3wJ5foMs";

    private List<DtoTransaction> dtoTransactions;
    private String previousHash;
    private String minerAddress;
    private String founderAddress;

    //аналог это nonce в биткоин.
    //analogue is a nonce in Bitcoin.
    private long randomNumberProof;
    private double minerRewards;

    //сложность которому блок должен соответствовать.
    //difficulty that the block must match.
    private long hashCompexity;
    private Timestamp timestamp;
    private long index;
    private String hashBlock;

    private static int YEAR = 360;
    private static int V28_CHANGE_ALGORITH_DIFF_INDEX = 133750;

    private static int customDiff = 17;
    private static double ERCENT_MONEY_MILTON_FRIMDAN = 1.02;
    private static double PERCENT_MONEY_MILTON_FRIMDAN2 = 1.005;

    public String hashForTransaction() throws IOException {
        if (this != null) {
            BlockForHash block = new BlockForHash(this.getDtoTransactions(),
                    this.previousHash,
                    this.minerAddress,
                    this.founderAddress,
                    this.randomNumberProof,
                    this.minerRewards,
                    this.hashCompexity,
                    this.timestamp,
                    this.index);


            // New hashing algorithm
            String staticBlockHash = DigestUtils.sha256Hex(block.jsonStringWithoutProof());
            String proofString = Long.toString(block.randomNumberProof);
            return DigestUtils.sha256Hex(staticBlockHash + proofString);


        }
        return "";
    }


    public static long getRandomNumberProofStatic() {
        return randomNumberProofStatic;
    }

    public static void setRandomNumberProofStatic(long randomNumberProofStatic) {
        TestingBlock.randomNumberProofStatic = randomNumberProofStatic;
    }

    public static boolean isTransactionValid(BigDecimal value) {
        return value.scale() <= SENDING_DECIMAL_PLACES_2;
    }

    public TestingBlock(List<DtoTransaction> dtoTransactions, String previousHex, String minerAddress, String founderAddress, long hashCompexity, long index) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        this.dtoTransactions = dtoTransactions;
        this.previousHash = previousHex;
        this.minerAddress = minerAddress;
        this.minerRewards = miningRewardsCount();
        this.hashCompexity = hashCompexity;
        this.founderAddress = founderAddress;
        this.timestamp = new Timestamp(UtilsTime.getUniversalTimestamp());
//        this.timestamp = Timestamp.valueOf( OffsetDateTime.now( ZoneOffset.UTC ).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        this.index = index;
        this.hashBlock = chooseMultiString(hashCompexity);

    }

    public TestingBlock(List<DtoTransaction> dtoTransactions, String previousHash, String minerAddress, String founderAddress, long randomNumberProof, double minerRewards, long hashCompexity, Timestamp timestamp, long index, String hashBlock) {
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
    public class BlockForHash {
        private List<DtoTransaction> transactions;
        private String previousHash;
        private String minerAddress;
        private String founderAddress;
        private long randomNumberProof;
        private double minerRewards;
        private long hashCompexity;
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
                            long hashCompexity,
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


        public String jsonString() throws IOException {
            return UtilsJson.objToStringJson(this);
        }

        // New method to get JSON string without randomNumberProof
        @JsonIgnore
        public String jsonStringWithoutProof() throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            // Create a map of all fields except randomNumberProof
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("transactions", this.transactions);
            fieldMap.put("previousHash", this.previousHash);
            fieldMap.put("minerAddress", this.minerAddress);
            fieldMap.put("founderAddress", this.founderAddress);
            fieldMap.put("minerRewards", this.minerRewards);
            fieldMap.put("hashCompexity", this.hashCompexity);
            fieldMap.put("timestamp", this.timestamp);
            fieldMap.put("index", this.index);

            return mapper.writeValueAsString(fieldMap);
        }

    }

    public TestingBlock() {
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

    private static double miningRewardsCount(List<DtoTransaction> dtoTransactions) {
        double rewards = 0.0;
        for (DtoTransaction dtoTransaction : dtoTransactions) {

            rewards += dtoTransaction.getBonusForMiner();
        }

        return rewards;
    }

    public String jsonString() throws IOException {
        return objToStringJson(this);
    }

    public static String objToStringJson(Object object) throws IOException {

        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, object);
        return writer.toString();
    }

    /**
     * Метод отвечает за поиск блока, добывает блок.
     * The method is responsible for searching for a block and mining the block.
     */
    public String chooseMultiString(long hashCompexity) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        String result = "";
        try {
            result = findHash(hashCompexity);
        } catch (Exception e) {
            // Handle or log the exception
            e.printStackTrace();
        }
        return result;

    }


    public String findHash(long hashCoplexity) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException, Exception {
        if (!verifyesTransSign()) {
            throw new NotValidTransactionException();
        }
        String hash = "";


        //Многоточный майнинг.
        //Multi-thead mining.
        hash = findHash_MT2(hashCoplexity);


        //hash = findHash_org(hashCoplexity);

        return hash;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value *= factor;
        return (double) Math.round(value) / factor;
    }

    public static double calculateMinedMoneyFridman(long index, double currentReward, double diffMoney, double G) {
        // Проверяем, активирован ли механизм увеличения награды

        double percentMoneyMiltonFrimdan = PERCENT_MONEY_MILTON_FRIMDAN2;
        double moneyFridman = MONEY_MILTON_FRIDMAN2;
        int divider = 4;

        diffMoney = (diffMoney - 22) / divider;
        if (diffMoney < 0) diffMoney = 0;
        double result = (G / divider) + diffMoney;

        // Рассчитываем количество блоков в году
        long blocksPerYear = (long) Seting.MILTON_MONEY_DAY * Seting.YEAR;

        // Вычисляем количество блоков, прошедших с начала отсчета
        long blocksSinceStart = index - Seting.MONEY_MILTON_FRIDMAN_INDEX;

        // Определяем количество полных лет, прошедших с начала отсчета
        int yearsPassed = (int) (blocksSinceStart / blocksPerYear);


        // Рассчитываем награду за блок с учетом ежегодного увеличения
        double newBlockReward = (moneyFridman + result) * Math.pow(percentMoneyMiltonFrimdan, yearsPassed);

        // Округляем новую награду до двух знаков после запятой
        newBlockReward = round(newBlockReward, 2);

        // Суммируем текущую награду с новой наградой
        double updatedReward = currentReward + newBlockReward;

        // Округляем итоговую награду до двух знаков после запятой
        updatedReward = round(updatedReward, 2);

        return updatedReward;
    }

    public static double blocksReward(List<DtoTransaction> actual, List<DtoTransaction> prev, long index) {
        // Задаем пороговый индекс
        int ALGORITM_MINING_2 = Seting.ALGORITM_MINING_2;
        Base base = new Base58();
        actual = actual.stream().sorted(Comparator.comparing(t -> base.encode(t.getSign()))).collect(Collectors.toList());
        prev = prev.stream().sorted(Comparator.comparing(t -> base.encode(t.getSign()))).collect(Collectors.toList());


        // Определяем фильтр на основе значения индекса
        Predicate<DtoTransaction> transactionFilter;

        transactionFilter = t -> !t.getSender().equals(Seting.BASIS_ADDRESS);


        // Подсчитываем уникальные адреса и суммы для актуальных транзакций
        long actualUniqAddress = actual.stream()
                .filter(transactionFilter)
                .map(DtoTransaction::getSender)
                .distinct()
                .count();

        double actualSumDollar = actual.stream()
                .filter(transactionFilter)
                .mapToDouble(DtoTransaction::getDigitalDollar)
                .sum();

        // Подсчитываем уникальные адреса и суммы для предыдущих транзакций
        long prevUniqAddress = prev.stream()
                .filter(transactionFilter)
                .map(DtoTransaction::getSender)
                .distinct()
                .count();

        double prevSumDollar = prev.stream()
                .filter(transactionFilter)
                .mapToDouble(DtoTransaction::getDigitalDollar)
                .sum();


        // Возвращаем коэффициент, если выполнены условия, иначе 0
        return actualUniqAddress > prevUniqAddress && actualSumDollar > prevSumDollar ? Seting.COEFFICIENT : 0;
    }


    private static List<DtoTransaction> instance = new ArrayList<>();

    public static synchronized List<DtoTransaction> getInstance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        instance = new ArrayList<>();

        //считываем с пула транзакции из дисковери.

        try {

            Base base = new Base58();
            System.out.println("get transactions from server: " + ADDRESS_HTTP + "its time 45 seconds");
            String json = UtilUrl.readJsonFromUrl(ADDRESS_HTTP + "/getTransactions");
            List<DtoTransaction> list;
            if (!json.isEmpty()) {
                list = UtilsJson.jsonToDtoTransactionList(json);
                list = list.stream()
                        .filter(t -> t.getSign() != null && !base.encode(t.getSign()).isEmpty())
                        .filter(t -> t.getDigitalDollar() > 0 || t.getDigitalStockBalance() > 0)
                        .collect(Collectors.toList());
                instance.addAll(list);
            } else {
                list = new ArrayList<>();
            }


        } catch (IOException | JSONException e) {

            System.out.println("AllTransaction: getInstance: Error");

        }


        instance.addAll(UtilsTransaction.readLineObject(Seting.ORGINAL_ALL_TRANSACTION_FILE));
        instance = instance.stream().distinct().collect(Collectors.toList());


        return instance;
    }

    public BlockForHash blockForHash(Account minner, long index, Timestamp timestamp) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, JSONException {

        System.out.println("index: " + index);
        //получение транзакций с сети
        List<DtoTransaction> listTransactions = getInstance();
        Base base = new Base58();
        //определение валидных транзакций


        //проверяет целостность транзакции, что они подписаны правильно


        long difficulty = customDiff;


        String json = UtilUrl.readJsonFromUrl(ADDRESS_HTTP + "/prevBlock");
        Block prevBlock = UtilsJson.jsonToBLock(json);


        //доход майнера
        double minerRewards = 0;
        double digitalReputationForMiner = 0;


        //доход основателя
        double founderReward = 0;
        double founderDigigtalReputationReward = 0;


        int day = 432;
        int period = 120;
        int mulptipleperiod = MULTIPLIER2;


        long money = (index - V28_CHANGE_ALGORITH_DIFF_INDEX)
                / (day * period);
        money = (long) (mulptipleperiod - money);
        money = money < 1 ? 1 : money;

        double G = blocksReward(listTransactions, prevBlock.getDtoTransactions(), index);
        int multiplier = (int) money;


        //фридман модель рост в 0.005%
        minerRewards = calculateMinedMoneyFridman(index, minerRewards, difficulty, G);
        digitalReputationForMiner = calculateMinedMoneyFridman(index, digitalReputationForMiner, difficulty, G);


        minerRewards *= multiplier;
        digitalReputationForMiner *= multiplier;


        int decimal = SENDING_DECIMAL_PLACES_2;


        minerRewards = round(minerRewards, decimal);
        digitalReputationForMiner = round(digitalReputationForMiner, decimal);
        founderReward = round(founderReward, decimal);
        founderDigigtalReputationReward = round(founderDigigtalReputationReward, decimal);


        //суммирует все вознаграждения майнеров
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(BASIS_PASSWORD));
        double sumRewards = listTransactions.stream().collect(Collectors.summingDouble(DtoTransaction::getBonusForMiner));


        String addressFounrder = "nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43";
        if (!addressFounrder.equals(prevBlock.getFounderAddress())) {
            System.out.println("wrong founder address: ");
            return null;
        }
        //вознаграждение основателя
        DtoTransaction founderRew = new DtoTransaction(BASIS_ADDRESS, addressFounrder, founderReward, founderDigigtalReputationReward, new Laws(), 0.0, VoteEnum.YES);
        byte[] signFounder = UtilsSecurity.sign(privateKey, founderRew.toSign());

        founderRew.setSign(signFounder);
        listTransactions.add(founderRew);


        System.out.println("Mining: miningBlock: difficulty: " + difficulty + " index: " + index);


        //вознаграждения майнера
        DtoTransaction minerRew = new DtoTransaction(BASIS_ADDRESS, minner.getAccount(), minerRewards, digitalReputationForMiner, new Laws(), sumRewards, VoteEnum.YES);


        //подписывает
        byte[] signGold = UtilsSecurity.sign(privateKey, minerRew.toSign());
        minerRew.setSign(signGold);


        listTransactions.add(minerRew);

        listTransactions = listTransactions.stream().filter(UtilsUse.distinctByKeyString(t -> {
            try {
                return t.getSign() != null ? base.encode(t.getSign()) : null;
            } catch (Exception e) {
                e.printStackTrace();
                return null; // или другое значение по умолчанию
            }
        })).collect(Collectors.toList());

        listTransactions = listTransactions.stream().filter(t -> t != null).collect(Collectors.toList());
        listTransactions = listTransactions.stream().sorted(Comparator.comparing(t -> base.encode(t.getSign()))).collect(Collectors.toList());


        double minerReward = miningRewardsCount(listTransactions);
        //так получает время.
        //  Timestamp timestamp = new Timestamp(UtilsTime.getUniversalTimestamp());
        BlockForHash blockForHash = new BlockForHash(
                listTransactions, prevBlock.getHashBlock(), minner.getAccount(),
                addressFounrder, 0, minerReward,
                difficulty, timestamp, index);


        // Hash the static part of the block once

        return blockForHash;
    }

    public String hashMiningWallet(long hashCoplexity, long index, Account minner, Timestamp timestamp) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, JSONException {
        if (!verifyesTransSign()) {
            throw new NotValidTransactionException();
        }
        BlockForHash block = blockForHash(minner, index, timestamp);

        String staticBlockHash = DigestUtils.sha256Hex(block.jsonStringWithoutProof());
        final int numThreads = Runtime.getRuntime().availableProcessors() - 1;

        final AtomicBoolean solutionFound = new AtomicBoolean(false);
        final CompletableFuture<String> solution = new CompletableFuture<>();
        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);

//     System.out.println("-------------------");
//      System.out.println(">>HASHSTR  :"+hashStr);
        this.randomNumberProof = 0;
        // Hash the static part of the block once


        final long range = Long.MAX_VALUE / 10240;


        System.out.println(">>numThreads: " + numThreads + " hashCoplexity:" + hashCoplexity + " Length: " + jsonString().length());


// Хешируем статические поля один раз (это можно сделать при инициализации или первом вызове)

        IntStream.range(0, numThreads).forEach(i -> {
            executor.submit(() -> {
                long endTime, duration;

                double durationInMilliseconds;
                long min = range * i;
                long max = i == numThreads - 1 ? Long.MAX_VALUE : min + range;
                String hash = "";
                long startTime = System.nanoTime();
                endTime = startTime;
                int cnt = 0;

                for (long k = min; k < max; k++) {
                    if (solutionFound.get()) {
                        break;
                    }


                    if (i == 0 && k % 100000 == 0) {
                        if (isAdvanced() == 1) {
                            solutionFound.set(true);
                            solution.complete("");
                            //               this.randomNumberProof = k;
                            Mining.miningIsObsolete = true;
                        }//isAdvanced() == 1

                        // Display status
                        if (k % 100000 == 0) {

                            endTime = System.nanoTime();


                            duration = endTime - startTime;  // Time in nanoseconds
                            durationInMilliseconds = duration / 1_000_000.0;  // Convert to milliseconds

                            double hashRate = (k - min) / 1000 / durationInMilliseconds * numThreads;
                            String formattedHashRate = String.format("%.2f", hashRate);
                            System.out.print("Hash rate: " + formattedHashRate + " KH/S\r");
                            System.out.flush();  // Ensures the printed content is immediately displayed

                        }//if (k % 400000 == 0)

                    } //i==0
                    // В цикле:

                    String proofString = Long.toString(k);
                    hash = DigestUtils.sha256Hex(staticBlockHash + proofString);


                    //Использует последний алгоритм добычи, где сумма единиц в битах должна быть ниже
                    //или равно 100 - сложность.
                    //Uses the latest mining algorithm, where the sum of units in bits must be lower
                    //or equal to 100 - difficulty.
                    //if (UtilsUse.chooseComplexity(hash, hashCoplexity, index)) {
                    if (isValidHashV29(hash, 100 - (int) hashCoplexity)) {


                        if (!solutionFound.getAndSet(true)) {
                            solution.complete(hash);
                            System.out.println("TestingBlock found: hash: " + hash + " k: " + k + " at Thread " + i);

//                          System.out.println("!!>>"+generateJsonWithProof(jsonParts, k));
                            this.randomNumberProof = k;

                        }
                    }
                }
            });
        });

        try {
            return solution.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("************************************");
            e.printStackTrace();
            System.out.println("************************************");
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow();
        }
    }

    Map<String, TestingBlock> blockMap = new HashMap<>();
    Map<String, TestingBlock> allBlocks = new HashMap<>();

    public void apiPool(long nonce, String minerAddress, String finalHash, BlockForHash blockForHash) throws IOException {


        TestingBlock block = new TestingBlock();
        block.setDtoTransactions(blockForHash.getTransactions());
        block.setPreviousHash(blockForHash.getPreviousHash());
        block.setFounderAddress(blockForHash.getFounderAddress());
        block.setMinerRewards(blockForHash.getMinerRewards());
        block.setHashCompexity(blockForHash.getHashCompexity());
        block.setTimestamp(blockForHash.getTimestamp());
        block.setIndex(blockForHash.getIndex());
        block.setHashBlock(finalHash);
        block.setRandomNumberProof(nonce);
        block.setMinerAddress(POOL_ADDRESS);

        //если блокчейн правильный хэш имеет то добавляем в список адрессов которые могут получить вознгаграждение
        if (block.getHashBlock().equals(block.hashForTransaction())) {
            blockMap.put(minerAddress, block);
            allBlocks.put(block.getHashBlock(), block);
        }

    }

    public static List<DtoTransaction> balanceTransaction(List<DtoTransaction> transactions, Map<String, Account> basis, long index) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();
        List<DtoTransaction> dtoTransactions = new ArrayList<>();
        Map<String, Account> balances = new HashMap<>();
        try {
            balances = UtilsUse.balancesClone(basis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Создаём EnumSet из всех возможных значений VoteEnum
        EnumSet<VoteEnum> voteSet = EnumSet.allOf(VoteEnum.class);
        transactions = transactions.stream().sorted(Comparator.comparing(t -> base.encode(t.getSign()))).collect(Collectors.toList());

        for (DtoTransaction transaction : transactions) {

            boolean result = false;


            if (!voteSet.contains(transaction.getVoteEnum())) {
                System.out.println("Value is not contained in VoteEnum enum");
                MyLogger.saveLog("Value is not contained in VoteEnum enum");
                continue;
            }

            if (balances.containsKey(transaction.getSender())) {
                Account sender = balances.get(transaction.getSender());
                Account customer = balances.get(transaction.getCustomer());


                if (customer == null) {
                    customer = new Account(transaction.getCustomer(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                }
                balances.put(customer.getAccount(), customer);
                basis.put(customer.getAccount(), customer);

                if (sender == null || customer == null) {
                    MyLogger.saveLog("balanceTransaction:transaction: null: " + transaction);
                    MyLogger.saveLog("balanceTransaction: sender or customer null: " + sender + ": " + customer);
                    continue;
                }

                BigDecimal transactionDigitalDollar = new BigDecimal(Double.toString(transaction.getDigitalDollar()));
                BigDecimal transactionDigitalStock = new BigDecimal(Double.toString(transaction.getDigitalStockBalance()));
                BigDecimal transactionBonusForMiner = new BigDecimal(Double.toString(transaction.getBonusForMiner()));

                // Check for null or negative values in transaction amounts and sender's balances
                if ((transactionDigitalDollar == null || transactionDigitalStock == null || transactionBonusForMiner == null ||
                        sender.getDigitalDollarBalance() == null || sender.getDigitalStockBalance() == null ||
                        transactionDigitalDollar.compareTo(BigDecimal.ZERO) < 0 ||
                        transactionDigitalStock.compareTo(BigDecimal.ZERO) < 0 ||
                        transactionBonusForMiner.compareTo(BigDecimal.ZERO) < 0 ||
                        sender.getDigitalDollarBalance().compareTo(BigDecimal.ZERO) < 0 ||
                        sender.getDigitalStockBalance().compareTo(BigDecimal.ZERO) < 0) && !Seting.BASIS_ADDRESS.equals(transaction.getSender())) {
                    MyLogger.saveLog("balanceTransaction: transactionDigitalDollar: " + transactionDigitalDollar);
                    MyLogger.saveLog("balanceTransaction: sender.getDigitalDollarBalance(): " + sender.getDigitalDollarBalance());
                    MyLogger.saveLog("balanceTransaction: transactionDigitalStock: " + transactionDigitalStock);
                    MyLogger.saveLog("balanceTransaction: transactionBonusForMiner: " + transactionBonusForMiner);
                    MyLogger.saveLog("balanceTransaction: sender.getDigitalStockBalance(): " + sender.getDigitalStockBalance());

                    continue;
                }

                if (BASIS_ADDRESS.equals(transaction.getSender())) {
                    result = UtilsBalance.sendMoney(sender, customer, transactionDigitalDollar, transactionDigitalStock, transactionBonusForMiner, transaction.getVoteEnum());// Ensure the sender has enough balance for the transaction, including the bonus for the miner
                } else if (transaction.getVoteEnum().equals(VoteEnum.YES) || transaction.getVoteEnum().equals(VoteEnum.NO)) {
                    if (sender.getAccount().equals(customer.getAccount())) {
                        MyLogger.saveLog("balanceTransaction: Seting.BASIS_ADDRESS.equals(transaction.getSender()): " + BASIS_ADDRESS.equals(transaction.getSender()));
                        continue;
                    }
                    if (sender.getDigitalStockBalance().compareTo(transactionDigitalStock) >= 0 && sender.getDigitalDollarBalance().compareTo(transactionDigitalDollar.add(transactionBonusForMiner)) >= 0) {
                        result = UtilsBalance.sendMoney(sender, customer, transactionDigitalDollar, transactionDigitalStock, transactionBonusForMiner, transaction.getVoteEnum());
                    }
                } else if (transaction.getVoteEnum().equals(VoteEnum.STAKING) && sender.getAccount().equals(customer.getAccount())) {
                    if (sender.getDigitalDollarBalance().compareTo(transactionDigitalDollar.add(transactionBonusForMiner)) >= 0) {
                        result = UtilsBalance.sendMoney(sender, customer, transactionDigitalDollar, transactionDigitalStock, transactionBonusForMiner, transaction.getVoteEnum());
                    }
                } else if (transaction.getVoteEnum().equals(VoteEnum.UNSTAKING) && sender.getAccount().equals(customer.getAccount())) {
                    if (sender.getDigitalStakingBalance().compareTo(transactionDigitalDollar.add(transactionBonusForMiner)) >= 0) {
                        result = UtilsBalance.sendMoney(sender, customer, transactionDigitalDollar, transactionDigitalStock, transactionBonusForMiner, transaction.getVoteEnum());
                    }
                } else if (transaction.getVoteEnum().equals(VoteEnum.REMOVE_YOUR_VOICE) && transaction.getCustomer().startsWith("LIBER")) {
                    result = UtilsBalance.sendMoney(sender, customer, transactionDigitalDollar, transactionDigitalStock, transactionBonusForMiner, transaction.getVoteEnum());
                }


                if (result) {
                    dtoTransactions.add(transaction);
                    balances.put(sender.getAccount(), sender);
                    balances.put(customer.getAccount(), customer);
                } else {
                    MyLogger.saveLog("balanceTransaction: json: " + UtilsJson.objToStringJson(transaction));
                    MyLogger.saveLog("balanceTransaction: sender: " + sender);
                    MyLogger.saveLog("balanceTransaction: index: " + index);
                }
            }
        }
        return dtoTransactions;
    }

    public String findHash_MT2(long hashCoplexity) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        if (!verifyesTransSign()) {
            throw new NotValidTransactionException();
        }


        final int numThreads = Runtime.getRuntime().availableProcessors() - 1;

        final AtomicBoolean solutionFound = new AtomicBoolean(false);
        final CompletableFuture<String> solution = new CompletableFuture<>();
        final ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        BlockForHash block = new BlockForHash(
                this.dtoTransactions, this.previousHash, this.minerAddress,
                this.founderAddress, 0, this.minerRewards,
                this.hashCompexity, this.timestamp, this.index);
        String hashStr = block.jsonString();
//     System.out.println("-------------------");
//      System.out.println(">>HASHSTR  :"+hashStr);
        this.randomNumberProof = 0;
        // Hash the static part of the block once
        String staticBlockHash = DigestUtils.sha256Hex(block.jsonStringWithoutProof());


        final long range = Long.MAX_VALUE / 10240;


        System.out.println(">>numThreads: " + numThreads + " hashCoplexity:" + hashCoplexity + " Length: " + jsonString().length());

        // Предполагается, что эти переменные уже определены
        String staticFieldsHash;
        String randomNumberProof;

// Хешируем статические поля один раз (это можно сделать при инициализации или первом вызове)

//       System.out.println(">>[0]:"+jsonParts[0]);
//       System.out.println(">>[1]:"+jsonParts[1]);
        IntStream.range(0, numThreads).forEach(i -> {
            executor.submit(() -> {
                long endTime, duration;

                double durationInMilliseconds;
                long min = range * i;
                long max = i == numThreads - 1 ? Long.MAX_VALUE : min + range;
                String hash = "";
                long startTime = System.nanoTime();
                endTime = startTime;
                int cnt = 0;

                for (long k = min; k < max; k++) {
                    if (solutionFound.get()) {
                        break;
                    }


                    if (i == 0 && k % 100000 == 0) {
                        if (isAdvanced() == 1) {
                            solutionFound.set(true);
                            solution.complete("");
                            //               this.randomNumberProof = k;
                            Mining.miningIsObsolete = true;
                        }//isAdvanced() == 1

                        // Display status
                        if (k % 100000 == 0) {

                            endTime = System.nanoTime();


                            duration = endTime - startTime;  // Time in nanoseconds
                            durationInMilliseconds = duration / 1_000_000.0;  // Convert to milliseconds

                            double hashRate = (k - min) / 1000 / durationInMilliseconds * numThreads;
                            String formattedHashRate = String.format("%.2f", hashRate);
                            System.out.print("Hash rate: " + formattedHashRate + " KH/S\r");
                            System.out.flush();  // Ensures the printed content is immediately displayed

                        }//if (k % 400000 == 0)

                    } //i==0
                    // В цикле:

                    String proofString = Long.toString(k);
                    hash = DigestUtils.sha256Hex(staticBlockHash + proofString);


                    //Использует последний алгоритм добычи, где сумма единиц в битах должна быть ниже
                    //или равно 100 - сложность.
                    //Uses the latest mining algorithm, where the sum of units in bits must be lower
                    //or equal to 100 - difficulty.
                    //if (UtilsUse.chooseComplexity(hash, hashCoplexity, index)) {
                    if (isValidHashV29(hash, 100 - (int) hashCoplexity)) {


                        if (!solutionFound.getAndSet(true)) {
                            solution.complete(hash);
                            System.out.println("TestingBlock found: hash: " + hash + " k: " + k + " at Thread " + i);

//                          System.out.println("!!>>"+generateJsonWithProof(jsonParts, k));
                            this.randomNumberProof = k;

                        }
                    }
                }
            });
        });

        try {
            return solution.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("************************************");
            e.printStackTrace();
            System.out.println("************************************");
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow();
        }
    }

    public static boolean isValidHashV29(String hash, int difficulty) {
        // Вычислить сумму битов в хэше
        int bitSum = getBitSum2(hash);
        // Проверить, меньше ли или равна сумма битов заданному уровню сложности
        return bitSum <= difficulty;
    }

    public static int getBitSum2(String hash) {
        int bitSum = 0;
        String hashUpper = hash.toUpperCase();
        for (int i = 0; i < hashUpper.length(); i += 2) {
            String hex = hashUpper.substring(i, i + 2);
            int hexValue = Integer.parseInt(hex, 16);
            while (hexValue > 0) {
                bitSum += hexValue & 1;
                hexValue >>= 1;
            }
        }
        return bitSum;
    }


    /*if the index advanced, then we quit the current hasfind() */
    private int isAdvanced() {

        try {
            String s;
//       s="http://125.229.48.110:16888";
            s = "http://194.87.236.238:82";
            for (String address : ORIGINAL_ADDRESSES) {
                s = address;
            }
            String server = SERVER;
            if (!server.isEmpty() && !server.isBlank()) {
                s = server;
            }


            String sizeStr = UtilUrl.readJsonFromUrl_silent(s + "/size");
            Long cur_index = Long.parseLong(sizeStr);


//        System.out.print("#");
            // System.out.println("#### cur_index "+cur_index+" index:"+this.index);
            if (cur_index > this.index) {
                System.out.println("######### STOP: cur_index " + cur_index + " index:" + this.index + "######");
                return 1;
            }

            //если true, то прекращаем майнинг
            if (isStopMining) {
                System.out.println("mining will be stopped");
                return 1;

            }
        } catch (JSONException | IOException e) {


            System.out.println("isAdvanced:  error");
            return 0;
        }


        return 0;

    }

    //TODO


    @Override
    public boolean equals(Object o) {


        if (this == o) return true;
        if (!(o instanceof TestingBlock)) return false;
        TestingBlock block = (TestingBlock) o;
        return getRandomNumberProof() == block.getRandomNumberProof() && Double.compare(block.getMinerRewards(), getMinerRewards()) == 0 && getHashCompexity() == block.getHashCompexity() && getIndex() == block.getIndex() && Objects.equals(getDtoTransactions(), block.getDtoTransactions()) && Objects.equals(getPreviousHash(), block.getPreviousHash()) && Objects.equals(getMinerAddress(), block.getMinerAddress()) && Objects.equals(getFounderAddress(), block.getFounderAddress()) && Objects.equals(getTimestamp(), block.getTimestamp()) && Objects.equals(getHashBlock(), block.getHashBlock());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDtoTransactions(), getPreviousHash(), getMinerAddress(), getFounderAddress(), getRandomNumberProof(), getMinerRewards(), getHashCompexity(), getTimestamp(), getIndex(), getHashBlock());
    }

    @Override
    public TestingBlock clone() throws CloneNotSupportedException {
        return new TestingBlock(this.dtoTransactions, this.previousHash, this.minerAddress, this.founderAddress,
                this.randomNumberProof, this.minerRewards, this.hashCompexity, this.timestamp, this.index,
                this.hashBlock);
    }
}
