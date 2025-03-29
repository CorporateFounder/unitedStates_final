package unitted_states_of_mankind.votingTest;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Keys;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import org.junit.Test;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.*;
import java.util.stream.Collectors;

// ... остальные импорты

public class AssemblyTest {
    /**
     * Выбирает членов Ассамблеи на основе предоставленного блока и транзакций.
     *
     * @param block        Блок, содержащий транзакции.
     * @param transactions Карта транзакций с публичным ключом отправителя в качестве ключа.
     * @param isFinish     Флаг, указывающий, следует ли завершить обработку и выбрать победителей.
     * @return Список публичных ключей выбранных членов Ассамблеи или обновленные пожертвования.
     * @throws NoSuchAlgorithmException Если алгоритм SHA-256 недоступен.
     */
    public List<String> selectAssemblyMembers(Block block, Map<String, DtoTransaction> transactions, boolean isFinish) throws NoSuchAlgorithmException {
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            if ("BUDGET".equals(transaction.getCustomer())) {
                String sender = transaction.getSender();
                double amount = transaction.getDigitalDollar();

                // Обновление максимального пожертвования для отправителя
                transactions.merge(sender, transaction, (existingTx, newTx) -> {
                    return existingTx.getDigitalDollar() < newTx.getDigitalDollar() ? newTx : existingTx;
                });
            }
        }

        if (!isFinish) {
            return Collections.emptyList();
        } else {
            Map<String, Double> senderMaxContributions = new HashMap<>();
            for (DtoTransaction tx : transactions.values()) {
                if ("BUDGET".equals(tx.getCustomer())) {
                    senderMaxContributions.merge(tx.getSender(), tx.getDigitalDollar(), Double::max);
                }
            }

            String blockHash = block.getHashBlock();
            List<Map.Entry<String, Double>> sortedSenders = senderMaxContributions.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        int compareByAmount = Double.compare(e2.getValue(), e1.getValue());
                        if (compareByAmount != 0) {
                            return compareByAmount;
                        }
                        try {
                            long e1Deterministic = hashToLong(blockHash + e1.getKey());
                            long e2Deterministic = hashToLong(blockHash + e2.getKey());
                            return Long.compare(e1Deterministic, e2Deterministic);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException("SHA-256 алгоритм недоступен", ex);
                        }
                    })
                    .collect(Collectors.toList());

            List<String> top500Senders = sortedSenders.stream()
                    .limit(500)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            List<String> selectedMembers = weightedDeterministicSelection(
                    top500Senders,
                    senderMaxContributions,
                    51,
                    blockHash,
                    5.0 // Коэффициент мотивации
            );

            return selectedMembers;
        }
    }

    /**
     * Детерминированно выбирает заданное количество членов из топ-500 с учетом их веса.
     *
     * @param eligibleSenders       Список допустимых отправителей.
     * @param senderContributions   Карта отправителей и их максимальных пожертвований.
     * @param count                 Количество членов для выбора.
     * @param blockHash             Хэш блока для детерминированности.
     * @param motivationFactor      Коэффициент мотивации, усиливающий влияние пожертвований.
     * @return Список выбранных членов Ассамблеи.
     * @throws NoSuchAlgorithmException Если алгоритм SHA-256 недоступен.
     */
    private List<String> weightedDeterministicSelection(
            List<String> eligibleSenders,
            Map<String, Double> senderContributions,
            int count,
            String blockHash,
            double motivationFactor
    ) throws NoSuchAlgorithmException {
        long seed = hashToLong(blockHash);
        Random random = new Random(seed);

        Map<String, Double> weights = new HashMap<>();
        double totalContribution = senderContributions.values().stream().mapToDouble(Double::doubleValue).sum();

        double totalWeight = 0.0;
        for (String sender : eligibleSenders) {
            double contribution = senderContributions.getOrDefault(sender, 0.0);
            double weight = 1.0 + (contribution / totalContribution) * motivationFactor;
            weight = Math.min(weight, 1.5); // Установка верхнего предела веса, чтобы избежать доминирования
            weights.put(sender, weight);
            totalWeight += weight;
        }

        List<String> selectedMembers = new ArrayList<>();
        Set<String> selectedSet = new HashSet<>();
        while (selectedMembers.size() < count && selectedSet.size() < eligibleSenders.size()) {
            double target = random.nextDouble() * totalWeight;
            double cumulativeWeight = 0.0;

            for (String sender : eligibleSenders) {
                if (selectedSet.contains(sender)) continue;
                cumulativeWeight += weights.get(sender);
                if (cumulativeWeight >= target) {
                    selectedMembers.add(sender);
                    selectedSet.add(sender);
                    totalWeight -= weights.get(sender);
                    break;
                }
            }
        }

        return selectedMembers;
    }

    /**
     * Преобразует строковую комбинацию хэша блока и публичного ключа в значение типа long.
     *
     * @param input Строка для хэширования (например, комбинация хэша блока и публичного ключа).
     * @return Длинное значение, полученное из хэша.
     * @throws NoSuchAlgorithmException Если алгоритм SHA-256 недоступен.
     */
    private long hashToLong(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.wrap(hashBytes);
        return buffer.getLong();
    }



    /**
     * Детерминированно выбирает заданное количество членов из допустимых отправителей, используя хэш блока.
     *
     * @param eligibleSenders Список допустимых отправителей.
     * @param count           Количество членов для выбора.
     * @param blockHash       Хэш блока, используемый для детерминированного генератора случайных чисел.
     * @return Список выбранных членов Ассамблеи.
     * @throws NoSuchAlgorithmException Если алгоритм SHA-256 недоступен.
     */
    private List<String> deterministicSelection(List<String> eligibleSenders, int count, String blockHash) throws NoSuchAlgorithmException {
        // Преобразование хэша блока в сид
        long seed = hashToLong(blockHash);

        // Инициализация детерминированного генератора случайных чисел
        Random random = new Random(seed);

        // Перемешивание списка допустимых отправителей детерминированным образом
        List<String> shuffled = new ArrayList<>(eligibleSenders);
        Collections.shuffle(shuffled, random);

        // Выбор первых 'count' участников из перемешанного списка
        return shuffled.stream().limit(count).collect(Collectors.toList());
    }



    @Test
    public void testBudgetAssemblySelection() throws Exception {
        // Этап 1: Создание участников и их начальных данных
        Map<String, Account> balances = new HashMap<>();
        Base58 base = new Base58();
        List<Keys> keyPairs = new ArrayList<>();
        List<Account> senders = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();
        Map<String, DtoTransaction> patron = new HashMap<>();

        int size = 49;
        int[] balancesArray = new int[size];
        int money = 10;
        for (int i = 0; i < size; i++) {
            balancesArray[i] = money;
            money += 10;
        }

        System.out.println("size " + balancesArray.length);
        for (int i = 0; i < balancesArray.length; i++) {
            Keys keyPair = UtilsSecurity.generateKeyPair();
            keyPairs.add(keyPair);
            String pubkey = keyPair.getPubkey();
            Account account = new Account(
                    pubkey,
                    BigDecimal.valueOf(balancesArray[i]),
                    BigDecimal.valueOf(balancesArray[i]),
                    BigDecimal.valueOf(balancesArray[i])
            );
            senders.add(account);
            balances.put(pubkey, account);
            // Инициализация с нулевыми пожертвованиями
            // patron.put(pubkey, 0.0); // Убрано, так как patron теперь хранит DtoTransaction
        }

        // Этап 2: Создание бюллетеней и блоков
        for (int i = 0; i < size; i++) {
            Account senderAccount = senders.get(i);
            String senderPubKey = senderAccount.getAccount();
            String senderPrivKey = keyPairs.get(i).getPrivkey();
            PrivateKey senderPrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(senderPrivKey));

            // Создание бюллетеня
            Laws bulletin = new Laws();

            // Создание транзакции
            double transactionAmount = balancesArray[i] * 1.5; // Пример суммы в долларах
            DtoTransaction transaction = new DtoTransaction(
                    senderPubKey,
                    "BUDGET",
                    transactionAmount,
                    0.0,
                    bulletin,
                    0.0,
                    VoteEnum.YES
            );
            byte[] transactionSign = UtilsSecurity.sign(senderPrivateKey, transaction.toSign());
            transaction.setSign(transactionSign);

            // Создание блока и добавление транзакции
            Block block = new Block();
            block.setDtoTransactions(new ArrayList<>());
            block.getDtoTransactions().add(transaction);
            block.setHashBlock(UtilsUse.sha256hash(Integer.toString(i)));
            blocks.add(block);
        }

        List<String> selectedMembers = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            boolean isFinish = (i == blocks.size() - 1);
            selectedMembers = selectAssemblyMembers(blocks.get(i), patron, isFinish);
//            System.out.println("TestingBlock " + i + ": selected members count: " + selectedMembers.size());
        }
        System.out.println("Final selected members: " + selectedMembers.size());

        // Дополнительные проверки (можно использовать assert для JUnit)
        // Например:
        // assertEquals(51, selectedMembers.size());
        // Проверка уникальности выбранных членов
        Set<String> uniqueMembers = new HashSet<>(selectedMembers);
        // assertEquals(selectedMembers.size(), uniqueMembers.size());

        // Проверка, что выбранные члены находятся в топ-500 отправителей
        // и соответствуют логике сортировки и выбора
    }
}
