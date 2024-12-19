package International_Trade_Union.utils;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.services.BlockService;

import International_Trade_Union.model.Account;
import International_Trade_Union.model.MyLogger;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static International_Trade_Union.setings.Seting.SENDING_DECIMAL_PLACES;
import static International_Trade_Union.setings.Seting.SENDING_DECIMAL_PLACES_2;

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

    //так же найти моду из сложности блоков
    private List<Object[]> calculateModeFromBlocks(List<Block> blocks, long startRange, long endRange) {
        return blocks.stream()
                .filter(block -> block.getIndex() >= startRange && block.getIndex() <= endRange)
                .collect(Collectors.groupingBy(Block::getHashCompexity, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(entry -> new Object[] {entry.getKey(), entry.getValue()})
                .collect(Collectors.toList());
    }

    public static BigDecimal percentDifferent(BigDecimal first, BigDecimal second) {
        return first.divide(second).subtract(new BigDecimal(1)).multiply(new BigDecimal(Seting.HUNDRED_PERCENT));
    }

    public static byte[] sha256(String text) {
        MessageDigest digestNew;

        try {
            digestNew = MessageDigest.getInstance("SHA-256");
            return digestNew.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[32];
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
            Account clonedAccount = accountEntry.getValue().clone();
            temp.put(accountEntry.getKey(), clonedAccount);

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

        if (actual.getIndex() < Seting.WAIGHT_MINING_INDEX) {
            waight = Seting.WAIGHT_MINING;
            number = 1;
            limit = 55;
        } else if (actual.getIndex() >= Seting.WAIGHT_MINING_INDEX && actual.getIndex() <= Seting.NEW_ALGO_MINING) {
            waight = Seting.WAIGHT_MINING_2;
            number = 10;
            limit = 135;

        } else if (actual.getIndex() > Seting.NEW_ALGO_MINING) {
            waight = Seting.WAIGHT_MINING_3;
            number = 1;
            limit = 150;
        }
        // Генерация случайного числа в диапазоне от 0 до 150
        int result = deterministicRandom.nextInt(limit);;


        if (actual.getIndex() > Seting.NEW_ALGO_MINING) {





            // Получаем количество уникальных отправителей транзакций
            long transactionCount = actual.getDtoTransactions().stream()
                    .filter(UtilsUse.distinctByKey(DtoTransaction::getSender))
                    .count();

            // Подсчитываем сумму всех транзакций
            double transactionSum = actual.getDtoTransactions().stream()
                    .sorted(Comparator.comparing(DtoTransaction::getDigitalDollar).reversed())
                    .filter(UtilsUse.distinctByKey(DtoTransaction::getSender))
                    .mapToDouble(t -> t.getDigitalDollar() + t.getDigitalStockBalance())
                    .sum();

            // Рассчитываем очки за сумму транзакций
            double transactionSumPoints = calculateScore(transactionSum / Math.max(actual.getDtoTransactions().size(), 1), 1) * 4;

            // Очки за стейкинг
            long mineScore = calculateScore(miner.getDigitalStakingBalance().doubleValue(), number);

            int diffLimit = (int) (actual.getHashCompexity() - 19);
            diffLimit = diffLimit >= 0 ? diffLimit : 0;
            // Рассчитываем очки за количество транзакций
            double transactionCountPoints = Math.min(transactionCount, mineScore * 2 + diffLimit * 3);

            // Новая формула для максимального количества баллов за транзакции
            double maxTransactionPoints = diffLimit * 3 + mineScore;


            double transactionPoints = 0;

            if (actual.getIndex() > Seting.ONLY_SUM_BALANCE) {
                transactionPoints = calculateScore(transactionSum, 0.1);
            } else {
                transactionPoints = Math.max(transactionCountPoints, transactionSumPoints);
            }
            // Выбираем большее из количества и суммы транзакций

            // Ограничиваем баллы за транзакции новым максимумом
            transactionPoints = Math.min(transactionPoints, maxTransactionPoints);


            //новая модель подсчета баллов
            //TODO проверить работоспособность.
            int range = 0;
            int X = 0;
            int diffPoint = 0;
            if (actual.getIndex() > Seting.OPTIMAL_SCORE_INDEX) {
                diffPoint = (int) (actual.getHashCompexity() * 25);
                X = getX( (int) actual.getHashCompexity());

                transactionPoints = calculateScore(transactionSum, 0.1);

                // Ограничиваем transactionPoints до mineScore
                transactionPoints = Math.min(transactionPoints, mineScore);

                //диапазон
                range = (int) (X + mineScore );
                int random = deterministicRandom.nextInt(range );

                //результат
                result = (int) (diffPoint + random + transactionPoints + mineScore);
                return result;
            }


            // Финальный результат
            result = (int) (result + (actual.getHashCompexity() * waight) + mineScore + transactionPoints);
        } else {
            result = (int) ((int) (result + (actual.getHashCompexity() * waight)) + calculateScore(miner.getDigitalStakingBalance().doubleValue(), number));

        }

        return result;

    }


    public static long calculateScore(double x, double x0) {
        if (x <= 0) {
            return 0;
        }
        double score = Math.ceil(Math.log(x / x0) / Math.log(2));
        return Math.min(30, (long) score);
    }

    public static long calculateScore(BigDecimal x, BigDecimal x0) {
        if (x.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        BigDecimal log2 = new BigDecimal(Math.log(2));
        BigDecimal score = BigDecimal.valueOf(Math.ceil(Math.log(x.divide(x0, RoundingMode.HALF_UP).doubleValue()) / log2.doubleValue()));
        return Math.min(30, score.longValue());
    }

    //позволяет получить список балансов, если баланс до калькуляции в addBlock отличается от
    //баланса после изменения. Что позволяет добавлятьв h2 только те балансы которые изменились
    public static Map<String, Account> differentAccount(Map<String, Account> first, Map<String, Account> second) {
        Map<String, Account> thirdMap = new HashMap<>();
        for (Map.Entry<String, Account> entry : second.entrySet()) {
            String key = entry.getKey();
            if (key.isBlank() || key.isEmpty()) {
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
        BigDecimal account1DollarBalance = account1.getDigitalDollarBalance();
        BigDecimal account2DollarBalance = account2.getDigitalDollarBalance();

        BigDecimal account1StockBalance = account1.getDigitalStockBalance();
        BigDecimal account2StockBalance = account2.getDigitalStockBalance();

        BigDecimal account1StakingBalance = account1.getDigitalStakingBalance();
        BigDecimal account2StakingBalance = account2.getDigitalStakingBalance();

        return account1DollarBalance.compareTo(account2DollarBalance) != 0 ||
                account1StockBalance.compareTo(account2StockBalance) != 0 ||
                account1StakingBalance.compareTo(account2StakingBalance) != 0;
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
        Set<String> accountSet = blocks.stream()
                .flatMap(block -> block.getDtoTransactions().stream())
                .flatMap(transaction -> Stream.of(transaction.getSender(), transaction.getCustomer()))
                .filter(account -> account != null && !account.isBlank())
                .collect(Collectors.toSet()); // Используем Set для устранения дубликатов

        return blockService.findBYAccountString(new ArrayList<>(accountSet));
    }

    public static BigDecimal truncateAndRound(BigDecimal value) {
        return value.setScale(SENDING_DECIMAL_PLACES_2, RoundingMode.DOWN);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value *= factor;
        return (double) Math.round(value) / factor;
    }

    public static BigDecimal round(BigDecimal value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        return value.setScale(places, RoundingMode.HALF_UP);
    }

    //find dublicate in transactions list
    public static List<DtoTransaction> getDuplicateTransactions(Block block) {
        Base base = new Base58();
        Map<String, List<DtoTransaction>> groupedBySignature = block.getDtoTransactions().stream()
                .collect(Collectors.groupingBy(t -> base.encode(t.getSign())));

        List<DtoTransaction> duplicates = new ArrayList<>();

        for (List<DtoTransaction> transactions : groupedBySignature.values()) {
            if (transactions.size() > 1) {
                duplicates.addAll(transactions.subList(1, transactions.size())); // Добавляем все кроме первого
            }
        }

        return duplicates;
    }

    public static boolean isTransaction(DtoTransaction transaction) {
        boolean result = true;
        List<String> laws = transaction.getLaws().getLaws();
        String name = transaction.getLaws().getPacketLawName();

        boolean money = transaction.getDigitalDollar() < Seting.MINIMUM &&
                transaction.getDigitalStockBalance() < Seting.MINIMUM &&
                transaction.getBonusForMiner() < Seting.MINIMUM;

        if ((name == null || name.isEmpty()) && money) {
            System.out.println("package name null: " + transaction.getLaws().getPacketLawName());
            result = false;
        }
        if ((laws == null || laws.isEmpty()) && money) {
            System.out.println("laws list null: " + transaction.getLaws().getPacketLawName());
            result = false;
        }
        return result;
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

                if (Seting.BASIS_ADDRESS.equals(transaction.getSender())) {
                    result = UtilsBalance.sendMoney(sender, customer, transactionDigitalDollar, transactionDigitalStock, transactionBonusForMiner, transaction.getVoteEnum());// Ensure the sender has enough balance for the transaction, including the bonus for the miner
                } else if (transaction.getVoteEnum().equals(VoteEnum.YES) || transaction.getVoteEnum().equals(VoteEnum.NO)) {
                    if (sender.getAccount().equals(customer.getAccount())) {
                        MyLogger.saveLog("balanceTransaction: Seting.BASIS_ADDRESS.equals(transaction.getSender()): " + Seting.BASIS_ADDRESS.equals(transaction.getSender()));
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


    //возвращает скользящее окно для хранения последних 30 слепков балана
    public static Map<Long, Map<String, Account>> slideWindow() {
        // Replace the HashMap with a LinkedHashMap that has a size limit for the sliding window
        Map<Long, Map<String, Account>> windows = new LinkedHashMap<Long, Map<String, Account>>(30, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Map<String, Account>> eldest) {
                return size() > Seting.SLIDING_WINDOW_BALANCE; // Keep only the latest 30 entries
            }
        };

        return windows;
    }

    //определяет количество знаков после запятой и не пропускает транзакции с большим количеством знаков.
    public static boolean isTransactionValid(BigDecimal value, long index) {
        if (index < Seting.CHANGE_DECIMAL_2_INDEX)
            return value.scale() <= SENDING_DECIMAL_PLACES;
        else
            return value.scale() <= SENDING_DECIMAL_PLACES_2;
    }

    //метод тестирования баланса, достаточно ли денег для отправки.
    public static String checkSendBalance(Account sender, DtoTransaction dtoTransaction) {


        if (dtoTransaction.getVoteEnum().equals(VoteEnum.YES) || dtoTransaction.getVoteEnum().equals(VoteEnum.NO) || dtoTransaction.getVoteEnum().equals(VoteEnum.STAKING)) {
            BigDecimal dollarBD = BigDecimal.valueOf(dtoTransaction.getDigitalDollar());
            BigDecimal stockBD = BigDecimal.valueOf(dtoTransaction.getDigitalStockBalance());
            if (dollarBD.compareTo(sender.getDigitalDollarBalance()) > 0 || stockBD.compareTo(sender.getDigitalStockBalance()) > 0) {

                return "wrong transaction, less dollar or less stock balance";
            }
        }
        if (dtoTransaction.getVoteEnum().equals(VoteEnum.UNSTAKING)) {
            BigDecimal dollarBD = BigDecimal.valueOf(dtoTransaction.getDigitalDollar());

            if (dollarBD.compareTo(sender.getDigitalStakingBalance()) > 0) {

                return "wrong transaction, less staking balance";
            }
        }
        return "success";
    }
    /**
     * Рассчитывает награду за данный блок с учетом ежегодного увеличения на 4%.
     *
     * @param index Индекс текущего блока.
     * @return Награда за блок с двумя знаками после запятой.
     */
    /**
     * Рассчитывает награду за данный блок с учетом ежегодного увеличения на 2%.
     * Если индекс блока меньше порогового, возвращает переданную текущую награду.
     * Иначе, суммирует текущую награду с новой наградой и округляет результат до двух знаков.
     *
     * @param index         Индекс текущего блока.
     * @param currentReward Текущая сумма наград.
     * @return Обновленная сумма наград с двумя знаками после запятой.
     */
    public static double calculateMinedMoneyFridman(long index, double currentReward, double diffMoney, double G) {
        // Проверяем, активирован ли механизм увеличения награды
        if (index < Seting.MONEY_MILTON_FRIDMAN_INDEX) {
            return currentReward;
        }

        double percentMoneyMiltonFrimdan = Seting.PERCENT_MONEY_MILTON_FRIMDAN;
        double moneyFridman = Seting.MONEY_MILTON_FRIDMAN;
        int divider = 4;
        if(index > Seting.OPTIMAL_SCORE_INDEX){
            percentMoneyMiltonFrimdan = Seting.PERCENT_MONEY_MILTON_FRIMDAN2;
            moneyFridman = Seting.MONEY_MILTON_FRIDMAN2;

        }


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


    //возвращает X диапазон
    public static int getX(int difficulty) {
        // Стартовая точка для уровня сложности 17
        int baseLevel = 17;
        int baseValue = 170;

        // Рассчитываем значение по формуле
        return baseValue + (difficulty - baseLevel) * 15;
    }

}
