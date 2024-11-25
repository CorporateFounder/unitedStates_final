package unitted_states_of_mankind.votingTest;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Keys;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;
import International_Trade_Union.vote.Laws;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;

public class ApprovalVoting {

    /**
     * Выбирает директоров с использованием одобрительного голосования среди топовых меценатов.
     *
     * @param block        Блок, содержащий транзакции для обработки.
     * @param transactions Карта для хранения максимальной транзакции на каждого отправителя.
     * @param isFinish     Указывает, следует ли выполнить финальный подсчет.
     * @return Список избранных директоров, если isFinish равно true; иначе пустой список.
     * @throws Exception Если возникает ошибка при обработке.
     */
    public List<String> electDirectors(Block block, Map<String, DtoTransaction> transactions, boolean isFinish) throws Exception {
        // Обработка блока
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            if ("BUDGET".equals(transaction.getCustomer())) {
                String sender = transaction.getSender();
                double amount = transaction.getDigitalDollar();

                // Обновление максимальной суммы для каждого отправителя
                transactions.merge(sender, transaction, (existingTx, newTx) ->
                        existingTx.getDigitalDollar() < newTx.getDigitalDollar() ? newTx : existingTx
                );
            }
        }

        if (!isFinish) {
            // Продолжаем собирать данные, возвращаем пустой список
            return Collections.emptyList();
        } else {
            // Выполняем финальный подсчет и выбираем директоров

            // Шаг 1: Отбор топ-500 отправителей по максимальной сумме
            List<Map.Entry<String, DtoTransaction>> sortedTransactions = transactions.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        int compareByAmount = Double.compare(
                                e2.getValue().getDigitalDollar(),
                                e1.getValue().getDigitalDollar()
                        );
                        if (compareByAmount != 0) {
                            return compareByAmount;
                        }
                        // Детерминированный порядок для одинаковых сумм
                        try {
                            long e1Deterministic = hashToLong(e1.getKey());
                            long e2Deterministic = hashToLong(e2.getKey());
                            return Long.compare(e1Deterministic, e2Deterministic);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException("SHA-256 недоступен", ex);
                        }
                    })
                    .limit(500)
                    .collect(Collectors.toList());

            Set<String> topSenders = sortedTransactions.stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            // Шаг 2: Сбор бюллетеней от топовых отправителей
            Map<String, Laws> validBallots = new HashMap<>();
            for (Map.Entry<String, DtoTransaction> entry : transactions.entrySet()) {
                String sender = entry.getKey();
                DtoTransaction transaction = entry.getValue();
                if (topSenders.contains(sender)) {
                    Laws bulletin = transaction.getLaws();
                    if (isValidApprovalBulletin(bulletin)) {
                        validBallots.put(sender, bulletin);
                    }
                }
            }

            // Шаг 3: Подсчет голосов одобрительного голосования
            Map<String, Integer> candidateVotes = new HashMap<>();
            for (Laws ballot : validBallots.values()) {
                for (String candidate : ballot.getLaws()) {
                    candidateVotes.put(candidate, candidateVotes.getOrDefault(candidate, 0) + 1);
                }
            }

            // Шаг 4: Выбор директоров на основе наибольшего количества голосов
            int numberOfDirectors = 5;
            List<String> electedDirectors = candidateVotes.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        int compareByVotes = Integer.compare(e2.getValue(), e1.getValue());
                        if (compareByVotes != 0) {
                            return compareByVotes;
                        }
                        // Детерминированный порядок для одинакового количества голосов
                        try {
                            long e1Deterministic = hashToLong(e1.getKey());
                            long e2Deterministic = hashToLong(e2.getKey());
                            return Long.compare(e1Deterministic, e2Deterministic);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException("SHA-256 недоступен", ex);
                        }
                    })
                    .limit(numberOfDirectors)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            return electedDirectors;
        }
    }

    /**
     * Преобразует строку в детерминированное значение long с использованием SHA-256.
     *
     * @param input Входная строка.
     * @return Значение long, полученное из хэша.
     * @throws NoSuchAlgorithmException Если SHA-256 недоступен.
     */
    private long hashToLong(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.wrap(hashBytes);
        return buffer.getLong();
    }

    /**
     * Проверяет, является ли предоставленный объект Laws допустимым бюллетенем одобрительного голосования.
     *
     * @param laws Объект Laws для проверки.
     * @return True, если бюллетень валиден, иначе False.
     * @throws IOException Если возникает ошибка при обработке.
     */
    public static boolean isValidApprovalBulletin(Laws laws) throws IOException {
        // Проверка, что объект Laws и его поля не равны null
        if (laws == null || laws.getPacketLawName() == null || laws.getHashLaw() == null || laws.getLaws() == null) {
            return false;
        }

        // Проверка, что PacketLawName равен "APPROVAL_BULLETIN"
        if (!"APPROVAL_BULLETIN".equals(laws.getPacketLawName())) {
            return false;
        }

        // Проверка, что список laws содержит не более 10 записей
        if (laws.getLaws().size() > 10) {
            return false;
        }

        // Проверка, что каждая запись в laws является валидным публичным ключом без пробелов
        for (String lawEntry : laws.getLaws()) {
            if (lawEntry == null || lawEntry.trim().isEmpty() || lawEntry.contains(" ")) {
                return false;
            }
        }

        // Проверка хэша
        Laws lawsForHash = new Laws();
        lawsForHash.setPacketLawName(laws.getPacketLawName());
        lawsForHash.setLaws(laws.getLaws());

        String expectedHash = Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(lawsForHash));

        return expectedHash.equals(laws.getHashLaw());
    }

    // Другие методы...

    @Test
    public void testApprovalVotingWithIncrementalProcessing() throws Exception {
        Map<String, Account> balances = new HashMap<>();
        Base58 base = new Base58();
        Map<String, DtoTransaction> transactions = new HashMap<>();

        // Создание отправителей с заданными балансами
        List<Keys> keyPairs = new ArrayList<>();
        List<Account> senders = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();

        int numberOfSenders = 600; // Общее количество отправителей
        int[] balancesArray = new int[numberOfSenders];
        int money = 10;
        for (int i = 0; i < numberOfSenders; i++) {
            balancesArray[i] = money;
            money += 10;
        }

        // Инициализация отправителей и их балансов
        for (int i = 0; i < numberOfSenders; i++) {
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
        }

        // Создание бюллетеней и блоков
        for (int i = 0; i < numberOfSenders; i++) {
            Account senderAccount = senders.get(i);
            String senderPubKey = senderAccount.getAccount();
            String senderPrivKey = keyPairs.get(i).getPrivkey();
            PrivateKey senderPrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(senderPrivKey));

            // Создание бюллетеня (до 10 кандидатов)
            Laws bulletin = new Laws();
            bulletin.setPacketLawName("APPROVAL_BULLETIN");
            List<String> candidates = getRandomCandidates(senders, 10);
            bulletin.setLaws(candidates);
            bulletin.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(bulletin)));

            // Создание транзакции с отправленной суммой (DigitalDollar)
            double transactionAmount = balancesArray[i] * 1.5; // Пример суммы
            DtoTransaction transaction = new DtoTransaction(
                    senderPubKey,
                    "BUDGET",
                    transactionAmount,
                    0.0,
                    bulletin,
                    0.0,
                    VoteEnum.YES
            );

            // Подписание транзакции
            byte[] transactionSign = UtilsSecurity.sign(senderPrivateKey, transaction.toSign());
            transaction.setSign(transactionSign);

            // Создание блока и добавление транзакции
            Block block = new Block();
            block.setDtoTransactions(new ArrayList<>());
            block.getDtoTransactions().add(transaction);
            block.setHashBlock(UtilsUse.sha256hash(Integer.toString(i)));
            blocks.add(block);
        }

        // Обработка блоков инкрементально
        List<String> electedDirectors = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            boolean isFinish = (i == blocks.size() - 1);
            List<String> result = electDirectors(blocks.get(i), transactions, isFinish);
            if (isFinish) {
                electedDirectors = result;
            }
        }

        // Вывод избранных директоров
        System.out.println("Избранные директора:");
        for (String director : electedDirectors) {
            System.out.println(director);
        }
    }

    /**
     * Генерирует список случайных публичных ключей кандидатов.
     *
     * @param senders Список всех отправителей.
     * @param max     Максимальное количество кандидатов для выбора.
     * @return Список публичных ключей кандидатов.
     */
    private List<String> getRandomCandidates(List<Account> senders, int max) {
        List<String> candidates = senders.stream()
                .map(Account::getAccount)
                .collect(Collectors.toList());

        Collections.shuffle(candidates);
        return candidates.stream().limit(max).collect(Collectors.toList());
    }

    // Добавьте другие необходимые методы и классы по мере необходимости
}
