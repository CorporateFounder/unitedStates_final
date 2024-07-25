package International_Trade_Union.utils;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.DtoTransaction.MerkleTree;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UtilsUse {
    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    // Метод, который вычисляет экономический рост или спад блокчейна в процентах
    //Пусть
    // At - средняя сумма транзакций в текущем блоке,
    // Ap - средняя сумма транзакций в предыдущем блоке,
    // Nt - количество транзакций в текущем блоке,
    // Np- количество транзакций в предыдущем блоке,
    // Ut - количество уникальных адресов в текущем блоке,
    // Up - количество уникальных адресов в предыдущем блоке.
    // Тогда экономический рост или спад блокчейна в процентах можно выразить как:
    //G=(Ap/At)*(Nt/Np)*(Ut/Up)-1
    //Эта формула учитывает, что если средняя сумма транзакций уменьшается, то экономика растет, а если количество транзакций и уникальных адресов увеличивается, то экономика также растет. Если G>0, то экономика блокчейна растет, а если G<0, то экономика блокчейна снижается.
    //формула
    public static double growth(double Ap, double At, double Np, double Nt, double Up, double Ut) {
        // Веса для каждого показателя
        double wA = 1.1; // Вес для средней суммы транзакций
        double wN = 0.99; // Вес для количества транзакций
        double wU = 1.21; // Вес для количества уникальных адресов
        // Формула, которая учитывает ваши критерии и логику
        double G = (wA * (Ap / At)) * (wN * (Nt / Np)) * (wU * (Ut / Up)) - 1;
        // Возвращаем результат


        //Награда дополнительная не может быть ниже нуля и выше 10
        G = G > 10 ? 10 : G;
        G = G < 0 ? 0 : G;
        G = Math.round(G);
        return G;
    }

    public static double sumDollarFromTransactions(Block block) {
        double sum = 0;
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            sum += transaction.getDigitalDollar();
        }
        return sum;
    }

    //получение уникальных адрессов
    public static int uniqAddress(Block block) {
        List<String> address = new ArrayList<>();
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            address.add(transaction.getSender());
        }
        return address.stream().distinct().collect(Collectors.toList()).size();
    }

    //ПОДСЧЕТ НАГРАДЫ
    public static double blocksReward(List<DtoTransaction> acutal, List<DtoTransaction> prev) {
        long actualUniqAddress = acutal.stream()
                .filter(t -> !t.getSender().equals(Seting.BASIS_ADDRESS))
                .map(t -> t.getSender())
                .distinct()
                .count();

        long prevUniqAddress = prev.stream()
                .filter(t -> !t.getSender().equals(Seting.BASIS_ADDRESS))
                .map(t -> t.getSender())
                .distinct()
                .count();

        double actualSumDollar = acutal.stream()
                .filter(t -> !t.getSender().equals(Seting.BASIS_ADDRESS))
                .mapToDouble(t -> t.getDigitalDollar())
                .sum();

        double prevSumDollar = prev.stream()
                .filter(t -> !t.getSender().equals(Seting.BASIS_ADDRESS))
                .mapToDouble(t -> t.getDigitalDollar())
                .sum();

        return actualUniqAddress > prevUniqAddress && actualSumDollar > prevSumDollar ? Seting.COEFFICIENT : 0;

    }

    //    одно число от другого в процентах
    public static Double percentDifferent(Double first, Double second) {
        return (first / second - 1) * Seting.HUNDRED_PERCENT;
    }

    //найти моду
    public static int mode(List<Integer> array) {
        HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
        int max = 1;
        int temp = 0;

        for (int i = 0; i < array.size(); i++) {

            if (hm.get(array.get(i)) != null) {

                int count = hm.get(array.get(i));
                count++;
                hm.put(array.get(i), count);

                if (count > max) {
                    max = count;
                    temp = array.get(i);
                }
            } else
                hm.put(array.get(i), 1);
        }
        return temp;
    }

    public static BigDecimal percentDifferent(BigDecimal first, BigDecimal second) {
        return first.divide(second).subtract(new BigDecimal(1)).multiply(new BigDecimal(Seting.HUNDRED_PERCENT));
    }

    public static byte[] sha256(String text) {
        return digest.digest(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha256hash(String text) {
        byte[] bytes = sha256(text);
        return bytesToHex(bytes);
    }

    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String generateRandomStr() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return generatedString;
    }

    public static double countPercents(double sum, double percent) {
        return sum * percent / Seting.HUNDRED_PERCENT;
    }

    public static BigDecimal countPercents(BigDecimal sum, BigDecimal percent) {
        return sum.multiply(percent).divide(new BigDecimal(Seting.HUNDRED_PERCENT));
    }

    public static double countGrowth(long block, double percent, double money) {
        long year = (long) (block / Seting.COUNT_BLOCK_IN_DAY / (Seting.YEAR / Seting.HALF_YEAR));
        double opeartion1 = 1 + (percent / Seting.HALF_YEAR) / Seting.HUNDRED_PERCENT;
        double operation2 = Math.pow(opeartion1, year);
        double result = money * operation2;
        return result;
    }


    public static boolean chooseComplexity(String literral, long hashComplexity, long index, String target, BigInteger bigTarget) {
        boolean result = false;
        if (index < Seting.NEW_START_DIFFICULT) {

            result = hashComplexity(literral, hashComplexity);
        } else if (index >= Seting.CHANGE_MEET_DIFFICULTY) {
            result = BlockchainDifficulty.v2MeetsDifficulty(literral.getBytes(), hashComplexity);
        } else {

            result = BlockchainDifficulty.meetsDifficulty(literral.getBytes(), hashComplexity);
        }
        if (index > Seting.v3MeetsDifficulty && index <= Seting.v4MeetsDifficulty) {
            result = BlockchainDifficulty.v3MeetsDifficulty(literral.getBytes(), hashComplexity);
        } else if (index > Seting.v4MeetsDifficulty) {
            result = BlockchainDifficulty.v4MeetsDifficulty(literral, hashComplexity);
        }
        if (index > Seting.v4MeetsDifficulty && index <= Seting.V28_CHANGE_ALGORITH_DIFF_INDEX) {
            result = BlockchainDifficulty.v4MeetsDifficulty(literral, hashComplexity);
        } else if (index > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX && index <= Seting.V29_CHANGE_ALGO_DIFF_INDEX) {
            result = BlockchainDifficulty.isValidHash(literral, target);
        } else if (index > Seting.V29_CHANGE_ALGO_DIFF_INDEX && index <= Seting.V30_INDEX_ALGO) {
            result = BlockchainDifficulty.isValidHashV29(literral, (int) (Seting.STANDART_FOR_TARGET - hashComplexity));
        } else if (index > Seting.V30_INDEX_ALGO && index <= Seting.V31_DIFF_END_MINING) {
            result = BlockchainDifficulty.isValidHashV30(literral, bigTarget);
        } else if (index > Seting.V31_DIFF_END_MINING) {
            result = BlockchainDifficulty.isValidHashV29(literral, (int) (Seting.STANDART_FOR_TARGET - hashComplexity));
        }

        return result;
    }


    public static boolean hashComplexity(String literral, long hashComplexity) {

        String regex = "^[0]{" + Long.toString(hashComplexity) + "}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(literral);
        return matcher.find();
    }

    public static String hashComplexityStr(String str, int hashComplexity) throws IOException {
        int randomNumberProof = 0;
        String hash = "";
        while (true) {
            randomNumberProof++;
            hash = UtilsUse.sha256hash(UtilsJson.objToStringJson(str + randomNumberProof));
            if (UtilsUse.hashComplexity(hash.substring(0, hashComplexity), hashComplexity)) {
                break;
            }

        }
        return hash;
    }

    //определяет соответствовать ли документ ли сумме денег.
    public static boolean sumTrue(List<String> laws, double moneyD, double moneyS, boolean isStock) {
        double sumD = 0;
        double sumS = 0;
        boolean isTrue = false;
        for (String s : laws) {
            try {
                String[] dollarEndStockStr =
                        s.split(" ");
                sumD += Double.parseDouble(dollarEndStockStr[1]);
                if (isStock) {
                    sumS += Double.parseDouble(dollarEndStockStr[2]);
                }
            } catch (Exception e) {
                System.out.println("sumException");
                return isTrue;
            }
        }


        if (sumD <= moneyD && sumS <= moneyS)
            isTrue = true;

        return isTrue;
    }

    //для филтрации в стриме, чтобы получить уникальные обекты по полям
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static <T> Predicate<T> distinctByKeyString(Function<? super T, String> keyExtractor) {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    //подсчитать количество нулей идущих подряд в hash
    public static long hashCount(String hash, long index) {
        long count = 0;
        //оптимизирован код
        for (int i = 0; i < hash.length(); i++) {
            if (hash.charAt(i) == '0') count++;
            else return count;
        }
        return count;
    }

    //подсчитывает долю в процентах одного числа от другого
    public static double percentageShare(double first, double allNumber) {
        return (first / allNumber) * Seting.HUNDRED_PERCENT;
    }

    //опреледеляет ближайщее число к году
    public static long nearestDateToYear(long block) {
        long period = (long) (Seting.COUNT_BLOCK_IN_DAY * Seting.YEAR);
        return block / period * period;
    }


    //медиана
    public static double median(List<Double> arr) {
        ArrayList<Double> list = new ArrayList(arr);
        System.out.println("UtilsUse start median");
        Collections.sort(list);

        double length = (double) list.size();
        System.out.println("length: " + length);
        int med = (int) Math.ceil(length / 2);

        System.out.println("med: " + med);

        double result = list.get(med - 1);
        System.out.println("result: " + result);
        return result;
    }

    public static Map<String, Account> balancesClone(Map<String, Account> balances) throws CloneNotSupportedException {
        Map<String, Account> temp = new HashMap<>();
        for (Map.Entry<String, Account> accountEntry : balances.entrySet()) {
            if (accountEntry == null
                    || accountEntry.getKey().isBlank()
                    || accountEntry.getValue() == null) {
                continue;
            }
            temp.put(accountEntry.getKey(), accountEntry.getValue().clone());
        }
        return temp;
    }

    public static long powerDiff(long diff) {
//        return (long) Math.pow(2, diff);
        return diff;
    }


    /**Вычисляет случайное число на основе предыдущего хэша и текущего и чем выше число, тем выше
     * значимость.*/
    /**
     * Вычисляет случайное число на основе предыдущего хэша и текущего и чем выше число, тем выше
     * значимость.
     */
    public static int bigRandomWinner(Block actual, Account miner) {
        // Конкатенация двух хешей
        String combinedHash = actual.getHashBlock();

        if (actual == null || actual.getHashBlock() == null || actual.getHashBlock().isBlank() || actual.getHashBlock().isEmpty())
            return 0;
        // Преобразование объединенных хешей в BigInteger
        BigInteger hashAsNumber = new BigInteger(combinedHash, 16);
        if (hashAsNumber == null) {
            return 0;
        }

        // Использование BigInteger как seed для детерминированного генератора случайных чисел
        Random deterministicRandom = new Random(hashAsNumber.longValue());

        int waight = 0;
        int number = 0;
        int limit = 135; // Предполагается, что limit это максимальное значение + 1

        if (actual.getIndex() < Seting.WAIGHT_MINING_INDEX ) {
            waight = Seting.WAIGHT_MINING;
            number = 1;
            limit = 55;
        } else if (actual.getIndex() >= Seting.WAIGHT_MINING_INDEX && actual.getIndex() <= Seting.NEW_ALGO_MINING) {
            waight = Seting.WAIGHT_MINING_2;
            number = 10;
            limit = 135;

        } else if(actual.getIndex() > Seting.NEW_ALGO_MINING) {
            waight = Seting.WAIGHT_MINING_3;
            number = 10;
            limit = 150;
        }
        // Генерация случайного числа в диапазоне от 0 до 150
        int result = deterministicRandom.nextInt(limit);
        if (actual.getIndex() > Seting.NEW_ALGO_MINING) {
            // Получаем количество уникальных отправителей транзакций
            long transactionCount = actual.getDtoTransactions().stream()
                    .filter(UtilsUse.distinctByKey(DtoTransaction::getSender))
                    .count();

            // Подсчитываем сумму всех транзакций
            double transactionSum = actual.getDtoTransactions().stream()
                    .sorted(Comparator.comparing(DtoTransaction::getDigitalDollar).reversed())
                    .filter(UtilsUse.distinctByKey(DtoTransaction::getSender))
                    .mapToDouble(t -> t.getDigitalDollar() + t.getDigitalStockBalance() + t.getBonusForMiner() * 4)
                    .sum();

            // Рассчитываем очки за сумму транзакций
            double transactionSumPoints = calculateScore(transactionSum / Math.max(actual.getDtoTransactions().size(), 1), 1) * 4;

            // Очки за стейкинг
            long mineScore = calculateScore(miner.getDigitalStakingBalance(), number);

            int diffLimit = (int) (actual.getHashCompexity() - 19);
            diffLimit = diffLimit >= 0? diffLimit: 0;
            // Рассчитываем очки за количество транзакций
            double transactionCountPoints = Math.min(transactionCount, mineScore * 2 + diffLimit * 3)  ;

            // Новая формула для максимального количества баллов за транзакции
            double maxTransactionPoints = diffLimit* 3 + mineScore;

            // Выбираем большее из количества и суммы транзакций
            double transactionPoints = Math.max(transactionCountPoints, transactionSumPoints);

            // Ограничиваем баллы за транзакции новым максимумом
            transactionPoints = Math.min(transactionPoints, maxTransactionPoints);

            // Финальный результат
            result = (int) (result + (actual.getHashCompexity() * waight) + mineScore + transactionPoints);
        }
        else {
            result = (int) ((int) (result + (actual.getHashCompexity() * waight)) + calculateScore(miner.getDigitalStakingBalance(), number));

        }

        return result;

    }

    public static long calculateScore(double x, double x0) {
        if (x <= 0) {
            return 0;
        }
        double score = Math.ceil(Math.log(x / x0) / Math.log(2));
        return Math.min(400, (long) score);
    }

    //позволяет получить список балансов, если баланс до калькуляции в addBlock отличается от
    //баланса после изменения. Что позволяет добавлятьв h2 только те балансы которые изменились
    public static Map<String, Account> differentAccount(Map<String, Account> first, Map<String, Account> second) {
        Map<String, Account> thirdMap = new HashMap<>();
        for (Map.Entry<String, Account> entry : second.entrySet()) {
            String key = entry.getKey();
            if(key.isBlank() || key.isEmpty()){
                continue;
            }
            Account accountInSecondMap = entry.getValue();
            Account accountInFirstMap = first.get(key);

            if (accountInFirstMap == null || areAccountsDifferent(accountInFirstMap, accountInSecondMap)) {
                thirdMap.put(key, accountInSecondMap);
            }
        }
        return thirdMap;
    }

    public static boolean areAccountsDifferent(Account account1, Account account2) {
        // Здесь определите логику сравнения объектов Account по балансам или адресам
        // Например:
        return account1.getDigitalDollarBalance() != account2.getDigitalDollarBalance() ||
                account1.getDigitalStockBalance() != account2.getDigitalStockBalance() ||
                account1.getDigitalStakingBalance() != account2.getDigitalStakingBalance();
    }


    public static Map<String, Account> getEqualsKeyBalance(
            Map<String, Account> tempBalance,
            Map<String, Account> originalBalance) {
        Map<String, Account> filteredMap = new HashMap<>();

        for (String key : tempBalance.keySet()) {
            if (originalBalance.containsKey(key)) {
                filteredMap.put(key, originalBalance.get(key));
            }
        }
        return filteredMap;
    }

    public static Map<String, Account> merge(Map<String, Account> first, Map<String, Account> second) {
        Map<String, Account> mergedMap = new HashMap<>(first);
        for (Map.Entry<String, Account> entry : second.entrySet()) {
            String key = entry.getKey();
            if (key.isBlank() || key.isEmpty()) {
                continue;
            }
            Account accountInSecondMap = entry.getValue();
            Account accountInFirstMap = first.get(key);

            if (accountInFirstMap == null || areAccountsDifferent(accountInFirstMap, accountInSecondMap)) {
                mergedMap.put(key, accountInSecondMap);
            }
        }
        return mergedMap;
    }

    public static List<EntityAccount> mergeAccounts(Map<String, Account> map, List<EntityAccount> db) {
        if (map == null || db == null) throw new IllegalArgumentException("map and db cannot be null");

        Map<String, EntityAccount> dbMap = db.stream()
                .collect(Collectors.toMap(e -> e.getAccount(), e -> e));

        for (Account account : map.values()) {
            if (account == null) continue; // Skip null accounts
            EntityAccount entityAccount = dbMap.get(account.getAccount());
            if (entityAccount != null) {
                entityAccount.setDigitalDollarBalance(account.getDigitalDollarBalance());
                entityAccount.setDigitalStockBalance(account.getDigitalStockBalance());
                entityAccount.setDigitalStakingBalance(account.getDigitalStakingBalance());
            } else {
                entityAccount = new EntityAccount(
                        account.getAccount(),
                        account.getDigitalDollarBalance(),
                        account.getDigitalStockBalance(),
                        account.getDigitalStakingBalance()
                );
                db.add(entityAccount);
                dbMap.put(account.getAccount(), entityAccount);
            }
        }

        return db;
    }


    public static List<EntityAccount> accounts(List<Block> blocks, BlockService blockService) throws IOException {
        List<String> accounts = new ArrayList<>();
        for (Block block : blocks) {
            for (DtoTransaction transaction : block.getDtoTransactions()) {
                if (transaction.getSender() != null && !transaction.getSender().isBlank())
                    accounts.add(transaction.getSender());

                if (transaction.getCustomer() != null && !transaction.getCustomer().isBlank())
                    accounts.add(transaction.getCustomer());
            }
        }
        return blockService.findBYAccountString(accounts);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value *= factor;
        return (double) Math.round(value) / factor;
    }

    public static String merkleHash(List<DtoTransaction> dtoTransactions){
        return new MerkleTree(dtoTransactions).getRoot();
    }

    public static String hashMerkleBlock(Block block){
        String hash =  "transactions"+merkleHash(block.getDtoTransactions())
               +"previousHash" + block.getPreviousHash()
               +"minerAddress" + block.getMinerAddress()
               + "minerAddress" + block.getFounderAddress()
                +"randomNumberProof" + block.getRandomNumberProof()
               +"minerRewards" + block.getMinerRewards()
                +"hashCompexity" + block.getHashCompexity()
                +"timestamp" + block.getTimestamp()
                +"index" + block.getIndex();
        return UtilsUse.sha256hash(hash);
    }
    public static String hashMerkleBlock(Block.BlockForHash block){
        String hash =  "transactions"+merkleHash(block.getTransactions())
                +"previousHash" + block.getPreviousHash()
                +"minerAddress" + block.getMinerAddress()
                + "minerAddress" + block.getFounderAddress()
                +"randomNumberProof" + block.getRandomNumberProof()
                +"minerRewards" + block.getMinerRewards()
                +"hashCompexity" + block.getHashCompexity()
                +"timestamp" + block.getTimestamp()
                +"index" + block.getIndex();
        return UtilsUse.sha256hash(hash);
    }

    public static String hashMining(Block.BlockForHash block, long randomNumberProof){
        String hash =  "transactions"+merkleHash(block.getTransactions())
                +"previousHash" + block.getPreviousHash()
                +"minerAddress" + block.getMinerAddress()
                + "minerAddress" + block.getFounderAddress()
                +"randomNumberProof" + randomNumberProof
                +"minerRewards" + block.getMinerRewards()
                +"hashCompexity" + block.getHashCompexity()
                +"timestamp" + block.getTimestamp()
                +"index" + block.getIndex();
        return UtilsUse.sha256hash(hash);
    }

    public static String hashMining(Block block, long randomNumberProof){
        String hash =  "transactions"+merkleHash(block.getDtoTransactions())
                +"previousHash" + block.getPreviousHash()
                +"minerAddress" + block.getMinerAddress()
                + "minerAddress" + block.getFounderAddress()
                +"randomNumberProof" + randomNumberProof
                +"minerRewards" + block.getMinerRewards()
                +"hashCompexity" + block.getHashCompexity()
                +"timestamp" + block.getTimestamp()
                +"index" + block.getIndex();
        return UtilsUse.sha256hash(hash);
    }
    public static String firstPartHash(Block.BlockForHash block){
       String firstPartHash =  "transactions"+merkleHash(block.getTransactions())
                +"previousHash" + block.getPreviousHash()
                +"minerAddress" + block.getMinerAddress()
                + "minerAddress" + block.getFounderAddress();
       return firstPartHash;
    }

    public static String firstPartHash(Block block){
        String firstPartHash =  "transactions"+merkleHash(block.getDtoTransactions())
                +"previousHash" + block.getPreviousHash()
                +"minerAddress" + block.getMinerAddress()
                + "minerAddress" + block.getFounderAddress();
        return firstPartHash;
    }
    public static String secondPartHash(Block.BlockForHash block){
        String secondPartHash = "minerRewards" + block.getMinerRewards()
                +"hashCompexity" + block.getHashCompexity()
                +"timestamp" + block.getTimestamp()
                +"index" + block.getIndex();
        return secondPartHash;
    }

    public static String secondPartHash(Block block){
        String secondPartHash = "minerRewards" + block.getMinerRewards()
                +"hashCompexity" + block.getHashCompexity()
                +"timestamp" + block.getTimestamp()
                +"index" + block.getIndex();
        return secondPartHash;
    }
    public static String finalHash(String firstPartHash, String secondPartHash, long numberRandomProof){
        String str = firstPartHash +"randomNumberProof" + numberRandomProof + secondPartHash;
       return UtilsUse.sha256hash(str);
    }
}
