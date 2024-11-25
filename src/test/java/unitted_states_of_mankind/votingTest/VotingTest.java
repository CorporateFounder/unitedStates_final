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
import International_Trade_Union.vote.CurrentLawVotes;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.UtilsCurrentLaw;
import International_Trade_Union.vote.VoteEnum;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VotingTest {



    public static boolean isBuletinTrueLaws(Laws laws) throws IOException {
        // Проверка, что объект laws и его поля не равны null
        if (laws == null || laws.getPacketLawName() == null || laws.getHashLaw() == null || laws.getLaws() == null) {
            return false;
        }

        // Проверка, что в списке laws каждая строка содержит только pub key без пробелов
        for (String lawEntry : laws.getLaws()) {
            if (lawEntry == null || lawEntry.trim().isEmpty()) {
                return false;
            }
            if (lawEntry.contains(" ")) {
                return false;
            }
        }

        // Создание копии объекта laws без поля hashLaw для корректного вычисления хэша
        Laws lawsForHash = new Laws();
        lawsForHash.setPacketLawName(laws.getPacketLawName());
        lawsForHash.setLaws(laws.getLaws());

        // Вычисление ожидаемого хэша
        String expectedHash = Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(lawsForHash));

        // Сравнение вычисленного хэша с хэшем объекта
        if (!expectedHash.equals(laws.getHashLaw())) {
            return false;
        }

        return true;
    }

    public static boolean isBuggetTrueLaws(Laws laws) throws IOException {
        // Проверка, что объект laws и его поля не равны null
        if (laws == null || laws.getPacketLawName() == null || laws.getHashLaw() == null || laws.getLaws() == null) {
            return false;
        }

        // Проверка, что в списке laws каждая строка содержит pub key и сумму через пробел
        for (String lawEntry : laws.getLaws()) {
            if (lawEntry == null || lawEntry.trim().isEmpty()) {
                return false;
            }

            String[] parts = lawEntry.split(" ");
            if (parts.length != 2) {
                return false;
            }

            String address = parts[0];
            String amountStr = parts[1];

            // Проверка, что адрес не пустой и не содержит пробелов
            if (address.isEmpty() || address.contains(" ")) {
                return false;
            }

            // Проверка, что сумма является корректным числом
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                return false;
            }

            // Проверка, что сумма >= MINIMUM (0.00000001)
            BigDecimal minimum = new BigDecimal("0.00000001");
            if (amount.compareTo(minimum) < 0) {
                return false;
            }

            // Проверка, что сумма имеет не меньше 8 десятичных знаков
            if (amount.scale() < 8) {
                return false;
            }

            // Проверка, что сумма учитывает только последние 8 знаков без округления
            BigDecimal truncatedAmount = amount.setScale(8, BigDecimal.ROUND_DOWN);
            if (amount.compareTo(truncatedAmount) != 0) {
                return false;
            }
        }

        // Создание копии объекта laws без поля hashLaw для корректного вычисления хэша
        Laws lawsForHash = new Laws();
        lawsForHash.setPacketLawName(laws.getPacketLawName());
        lawsForHash.setLaws(laws.getLaws());

        // Вычисление ожидаемого хэша
        String expectedHash = Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(lawsForHash));

        // Сравнение вычисленного хэша с хэшем объекта
        if (!expectedHash.equals(laws.getHashLaw())) {
            return false;
        }

        return true;
    }



    @Test
    public void testVotingWithFiveBallotsCombined() throws Exception {
        Map<String, Account> balances = new HashMap<>();
        Base58 base = new Base58();

        // Create 5 senders with specified balances
        List<Keys> keyPairs = new ArrayList<>();
        List<Account> senders = new ArrayList<>();

        int[] balancesArray = {10, 20, 30, 40, 50};
        for (int i = 0; i < 5; i++) {
            Keys keyPair = UtilsSecurity.generateKeyPair();
            keyPairs.add(keyPair);
            String pubkey = keyPair.getPubkey();
            Account account = new Account(pubkey, BigDecimal.valueOf(balancesArray[i]), BigDecimal.valueOf(balancesArray[i]), BigDecimal.valueOf(balancesArray[i]));
            senders.add(account);
            balances.put(pubkey, account);
        }

        // Define initial ballots (preferences) for each sender
        List<List<String>> initialBallotsPreferences = Arrays.asList(
                Arrays.asList("A", "B", "C", "D", "F"),                 // sender1
                Arrays.asList("B", "A", "D", "C", "E", "F"),            // sender2
                Arrays.asList("C", "B", "A", "D", "E", "F"),            // sender3
                Arrays.asList("D", "E", "F", "A", "B", "C"),            // sender4
                Arrays.asList("E", "D", "F", "B", "A", "C")             // sender5
        );

        // Create initial blocks with ballots
        List<Block> blocks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Account senderAccount = senders.get(i);
            String senderPubKey = senderAccount.getAccount();
            String senderPrivKey = keyPairs.get(i).getPrivkey();
            PrivateKey senderPrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(senderPrivKey));

            // Create a bulletin (ballot)
            Laws bulletin = new Laws();
            bulletin.setPacketLawName("RCV_BULLETIN");
            List<String> bulletinList = initialBallotsPreferences.get(i);
            bulletin.setLaws(bulletinList);

            // Create transaction
            DtoTransaction transaction = new DtoTransaction(senderPubKey, "bulletin" + (i + 1), 0.0, 0.0, bulletin, 0.0, VoteEnum.YES);

            // Sign the transaction
            byte[] transactionSign = UtilsSecurity.sign(senderPrivateKey, transaction.toSign());
            transaction.setSign(transactionSign);

            // Create a block and add the transaction
            Block block = new Block();
            block.setDtoTransactions(new ArrayList<>());
            block.getDtoTransactions().add(transaction);
            blocks.add(block);
        }

        // Now, let's suppose sender3 submits a new ballot later
        List<String> updatedBallotPreferencesSender3 = Arrays.asList("C", "D", "E", "B", "A", "F"); // Updated preferences

        // Create a new block with the updated ballot for sender3
        Account sender3Account = senders.get(2);
        String sender3PubKey = sender3Account.getAccount();
        String sender3PrivKey = keyPairs.get(2).getPrivkey();
        PrivateKey sender3PrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(sender3PrivKey));

        // Create a bulletin (updated ballot)
        Laws updatedBulletin = new Laws();
        updatedBulletin.setPacketLawName("RCV_BULLETIN");
        updatedBulletin.setLaws(updatedBallotPreferencesSender3);

        // Create transaction for updated ballot
        DtoTransaction updatedTransaction = new DtoTransaction(sender3PubKey, "updatedBulletin", 0.0, 0.0, updatedBulletin, 0.0, VoteEnum.YES);

        // Sign the transaction
        byte[] updatedTransactionSign = UtilsSecurity.sign(sender3PrivateKey, updatedTransaction.toSign());
        updatedTransaction.setSign(updatedTransactionSign);

        // Create a new block and add the updated transaction
        Block updatedBlock = new Block();
        updatedBlock.setDtoTransactions(new ArrayList<>());
        updatedBlock.getDtoTransactions().add(updatedTransaction);
        blocks.add(updatedBlock);

        // Prepare for voting calculation

        List<String> winners = new ArrayList<>();
        Map<String, List<Laws>> collectedBallots = new HashMap<>();
        int numberOfSeats = 3;

        // Collect ballots from all blocks (including the updated ballot)
        for (Block block : blocks) {
            calculateBuletin(balances, block, collectedBallots, false, numberOfSeats, winners);
        }

        // Now perform the final calculation to determine the winners
        calculateBuletin(balances, null, collectedBallots, true, numberOfSeats, winners);

        // Output the winners
        System.out.println("Winners: ");
        for (String winner : winners) {
            System.out.println(winner);
        }

        // Now, let's suppose we need to rollback the last ballot from sender3
        rollbackBuletin(Collections.singleton(sender3Account.getAccount()), collectedBallots);

        // Clear previous winners and recalculate
        winners.clear();
        calculateBuletin(balances, null, collectedBallots, true, numberOfSeats, winners);

        // Output the winners after rollback
        System.out.println("Winners after rollback: ");
        for (String winner : winners) {
            System.out.println(winner);
        }
        System.out.println("Senders: ");
        collectedBallots.entrySet().stream().forEach(t -> System.out.println(t.getKey()));
    }
    @Test
    public void testVotingWithFourteenBallotsCombined1() throws Exception {
        Map<String, Account> balances = new HashMap<>();
        Base58 base = new Base58();

        // Создание 14 отправителей с заданными балансами
        List<Keys> keyPairs = new ArrayList<>();
        List<Account> senders = new ArrayList<>();

        int[] balancesArray = {
                10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140
        };
        for (int i = 0; i < 14; i++) {
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

        // Получение публичных ключей отправителей (адресов)
        List<String> senderPubKeys = senders.stream()
                .map(Account::getAccount)
                .collect(Collectors.toList());

        // Определение sender3PubKey
        String sender3PubKey = senders.get(2).getAccount();

        // Определение начальных бюллетеней (предпочтений) для каждого отправителя
        List<List<String>> initialBallotsPreferences = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            List<String> preferences = new ArrayList<>(senderPubKeys);
            // Опционально: вращение предпочтений для разнообразия
            Collections.rotate(preferences, i);
            initialBallotsPreferences.add(preferences);
        }

        // Создание начальных блоков с бюллетенями
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            Account senderAccount = senders.get(i);
            String senderPubKey = senderAccount.getAccount();
            String senderPrivKey = keyPairs.get(i).getPrivkey();
            PrivateKey senderPrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(senderPrivKey));

            // Создание бюллетеня
            Laws bulletin = new Laws();
            bulletin.setPacketLawName("RCV_BULLETIN");
            List<String> bulletinList = initialBallotsPreferences.get(i);
            bulletin.setLaws(bulletinList);

            // Создание транзакции для бюллетеня
            DtoTransaction transaction = new DtoTransaction(
                    senderPubKey,
                    "bulletin" + (i + 1),
                    0.0,
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
            blocks.add(block);
        }

        // Предположим, что sender3 (индекс 2) отправляет новый бюллетень позже
        List<String> updatedBallotPreferencesSender3 = new ArrayList<>(senderPubKeys);
        Collections.rotate(updatedBallotPreferencesSender3, 3); // Пример изменения

        // Создание нового блока с обновлённым бюллетенем для sender3
        Account sender3Account = senders.get(2);
        String sender3PrivKey = keyPairs.get(2).getPrivkey();
        PrivateKey sender3PrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(sender3PrivKey));

        // Создание обновлённого бюллетеня
        Laws updatedBulletin = new Laws();
        updatedBulletin.setPacketLawName("RCV_BULLETIN");
        updatedBulletin.setLaws(updatedBallotPreferencesSender3);

        // Создание транзакции для обновлённого бюллетеня
        DtoTransaction updatedTransaction = new DtoTransaction(
                sender3PubKey,
                "updatedBulletin",
                0.0,
                0.0,
                updatedBulletin,
                0.0,
                VoteEnum.YES
        );
        byte[] updatedTransactionSign = UtilsSecurity.sign(sender3PrivateKey, updatedTransaction.toSign());
        updatedTransaction.setSign(updatedTransactionSign);


        // Создание нового блока и добавление обновлённой транзакции
        Block updatedBlock = new Block();
        updatedBlock.setDtoTransactions(new ArrayList<>());
        updatedBlock.getDtoTransactions().add(updatedTransaction);
        blocks.add(updatedBlock);

        // Подготовка к подсчёту голосов
        List<String> winners = new ArrayList<>();
        Map<String, List<Laws>> collectedBallots = new HashMap<>();
        int numberOfSeats = 7;

        // Сбор бюллетеней из всех блоков (включая обновлённый бюллетень)
        for (Block block : blocks) {
            calculateBuletin(balances, block, collectedBallots, false, numberOfSeats, winners);
        }

        // Финальный подсчёт для определения победителей
        calculateBuletin(balances, null, collectedBallots, true, numberOfSeats, winners);

        // Вывод победителей
        System.out.println("Winners: ");
        for (String winner : winners) {
            System.out.println(winner);
        }

        // Предположим, что нужно откатить последний бюллетень от sender3
        rollbackBuletin(Collections.singleton(sender3Account.getAccount()), collectedBallots);

        // Очистка предыдущих победителей и повторный подсчёт
        winners.clear();
        calculateBuletin(balances, null, collectedBallots, true, numberOfSeats, winners);

        System.out.println("----------------------------------------------------");
        // Вывод победителей после отката
        System.out.println("Winners after rollback: ");
        for (String winner : winners) {
            System.out.println(winner);
        }
        System.out.println("----------------------------------------------------");

        System.out.println("Senders: ");
        collectedBallots.entrySet().stream().forEach(t -> System.out.println(t.getKey()));

        // После выборов создаём закон "BUDGET" одним из победителей
        String budgetCreatorPubKey = winners.get(0); // Первый победитель создаёт закон
        Account budgetCreatorAccount = balances.get(budgetCreatorPubKey);
        String budgetCreatorPrivKey = keyPairs.get(senders.indexOf(budgetCreatorAccount)).getPrivkey();
        PrivateKey budgetCreatorPrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(budgetCreatorPrivKey));

        // Создание закона "BUDGET"
        List<String> budgetLawContent = new ArrayList<>();
        budgetLawContent.add("Budget allocation details...");
        Laws budgetLaw = new Laws("BUDGET", budgetLawContent);

        // Генерация случайных sendMoney записей
        List<String> sendMoney = generateRandomSendMoney(senderPubKeys, 1, 5, new BigDecimal("1000"));

        // Установка списка sendMoney в закон бюджета
        budgetLaw.setLaws(sendMoney);

        // Создание транзакции для закона "BUDGET"
        DtoTransaction budgetLawTransaction = new DtoTransaction(
                budgetCreatorPubKey,
                budgetLaw.getHashLaw(),
                0.0,
                0.0,
                budgetLaw,
                0.0,
                VoteEnum.YES
        );
        byte[] budgetLawTransactionSign = UtilsSecurity.sign(budgetCreatorPrivateKey, budgetLawTransaction.toSign());
        budgetLawTransaction.setSign(budgetLawTransactionSign);

        // Создание нового блока и добавление транзакции закона "BUDGET"
        Block budgetLawBlock = new Block();
        budgetLawBlock.setDtoTransactions(new ArrayList<>());
        budgetLawBlock.getDtoTransactions().add(budgetLawTransaction);
        blocks.add(budgetLawBlock);

        // Теперь победители голосуют за закон "BUDGET"
        Map<String, CurrentLawVotes> votes = new HashMap<>();
        List<Account> winnersAccounts = senders.stream()
                .filter(t -> winners.contains(t.getAccount()))
                .collect(Collectors.toList());

        for (Account winner : winnersAccounts) {
            String winnerPubKey = winner.getAccount();
            String winnerPrivKey = keyPairs.get(senders.indexOf(winner)).getPrivkey();
            PrivateKey winnerPrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(winnerPrivKey));

            Random random = new Random();
            // Победители голосуют ЗА или ПРОТИВ случайным образом
            VoteEnum voteEnum = random.nextBoolean() ? VoteEnum.YES : VoteEnum.NO;

            // Создание транзакции голосования
            DtoTransaction voteTransaction = new DtoTransaction(
                    winnerPubKey,
                    budgetLaw.getHashLaw(),
                    0.0,
                    0.0,
                    budgetLaw,
                    0.0,
                    voteEnum
            );
            byte[] voteTransactionSign = UtilsSecurity.sign(winnerPrivateKey, voteTransaction.toSign());
            voteTransaction.setSign(voteTransactionSign);

            // Добавление транзакции голосования в новый блок
            Block voteBlock = new Block();
            voteBlock.setDtoTransactions(new ArrayList<>());
            voteBlock.getDtoTransactions().add(voteTransaction);
            blocks.add(voteBlock);
        }

        // Подсчёт голосов от победителей
        votes.clear();
        for (Block block : blocks) {
            UtilsCurrentLaw.calculateVote(votes, winnersAccounts, block);
        }

        System.out.println("-----------------------------");
        System.out.println("Vote results:");

        // Инициализация аккаунта бюджета с достаточным балансом
        Account budget = new Account("BUDGET", BigDecimal.valueOf(1000), BigDecimal.ZERO, BigDecimal.ZERO);
        Map<String, Laws> budgets = new HashMap<>();
        budgets.put(budgetLaw.getHashLaw(), budgetLaw);

        for (Map.Entry<String, CurrentLawVotes> votesEntry : votes.entrySet()) {
            System.out.println("Law: " + votesEntry.getKey());
            System.out.println("Votes YES: " + votesEntry.getValue().getYES().size());
            System.out.println("Votes NO: " + votesEntry.getValue().getNO().size());

            if (votesEntry.getValue().getYES().size() > 3) {
                if (budgets.containsKey(votesEntry.getKey())) {
                    boolean success = sendFromBudget(budgets.get(votesEntry.getKey()), balances, budget);
                    if (success) {
                        System.out.println("Budget transfers executed successfully.");
                    } else {
                        System.out.println("Budget transfers failed due to insufficient funds or invalid data.");
                    }
                    rollbackFromBudget(budgets.get(votesEntry.getKey()), balances, budget);
                }
            }
        }
    }

    @Test
    public void testIsBuletinAndIsBuggetTrueLaws() throws Exception {
        // Создание тестовых данных

        // Создание списка публичных ключей для использования в тестах
        List<String> validPubKeys = Arrays.asList(
                "pubKey1", "pubKey2", "pubKey3", "pubKey4", "pubKey5",
                "pubKey6", "pubKey7", "pubKey8", "pubKey9", "pubKey10",
                "pubKey11", "pubKey12", "pubKey13", "pubKey14"
        );

        // 1. Тестирование isBuletinTrueLaws с валидным объектом Laws
        Laws validBuletin = new Laws();
        validBuletin.setPacketLawName("RCV_BULLETIN");
        validBuletin.setLaws(Arrays.asList("pubKey1", "pubKey2", "pubKey3"));
        validBuletin.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(validBuletin)));

        assertTrue(isBuletinTrueLaws(validBuletin), "Valid Buletin should return true");

        // 2. Тестирование isBuletinTrueLaws с некорректным хэшем
        Laws invalidHashBuletin = new Laws();
        invalidHashBuletin.setPacketLawName("RCV_BULLETIN");
        invalidHashBuletin.setLaws(Arrays.asList("pubKey1", "pubKey2", "pubKey3"));
        invalidHashBuletin.setHashLaw("invalidHash");

        assertFalse(isBuletinTrueLaws(invalidHashBuletin), "Buletin with invalid hash should return false");

        // 3. Тестирование isBuletinTrueLaws с null полями
        Laws nullFieldsBuletin = new Laws();
        // Не устанавливаем поля, оставляя их null

        assertFalse(isBuletinTrueLaws(nullFieldsBuletin), "Buletin with null fields should return false");

        // 4. Тестирование isBuletinTrueLaws с некорректным форматом в списке laws
        Laws invalidFormatBuletin = new Laws();
        invalidFormatBuletin.setPacketLawName("RCV_BULLETIN");
        invalidFormatBuletin.setLaws(Arrays.asList("pubKey1 invalid", "pubKey2", "pubKey3"));
        invalidFormatBuletin.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(invalidFormatBuletin)));

        assertFalse(isBuletinTrueLaws(invalidFormatBuletin), "Buletin with invalid format in laws should return false");

        // 5. Тестирование isBuggetTrueLaws с валидным объектом Laws
        Laws validBugget = new Laws();
        validBugget.setPacketLawName("BUDGET");
        validBugget.setLaws(Arrays.asList("pubKey1 100.00000001", "pubKey2 200.12345678"));
        validBugget.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(validBugget)));

        assertTrue(isBuggetTrueLaws(validBugget), "Valid Bugget should return true");

        // 6. Тестирование isBuggetTrueLaws с некорректным хэшем
        Laws invalidHashBugget = new Laws();
        invalidHashBugget.setPacketLawName("BUDGET");
        invalidHashBugget.setLaws(Arrays.asList("pubKey1 100.00000001", "pubKey2 200.12345678"));
        invalidHashBugget.setHashLaw("invalidHash");

        assertFalse(isBuggetTrueLaws(invalidHashBugget), "Bugget with invalid hash should return false");

        // 7. Тестирование isBuggetTrueLaws с null полями
        Laws nullFieldsBugget = new Laws();
        // Не устанавливаем поля, оставляя их null

        assertFalse(isBuggetTrueLaws(nullFieldsBugget), "Bugget with null fields should return false");

        // 8. Тестирование isBuggetTrueLaws с отсутствующей суммой
        Laws missingAmountBugget = new Laws();
        missingAmountBugget.setPacketLawName("BUDGET");
        missingAmountBugget.setLaws(Arrays.asList("pubKey1", "pubKey2 200.12345678"));
        missingAmountBugget.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(missingAmountBugget)));

        assertFalse(isBuggetTrueLaws(missingAmountBugget), "Bugget with missing amount should return false");

        // 9. Тестирование isBuggetTrueLaws с суммой меньше MINIMUM
        Laws lessThanMinimumBugget = new Laws();
        lessThanMinimumBugget.setPacketLawName("BUDGET");
        lessThanMinimumBugget.setLaws(Arrays.asList("pubKey1 0.00000000", "pubKey2 200.12345678"));
        lessThanMinimumBugget.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(lessThanMinimumBugget)));

        assertFalse(isBuggetTrueLaws(lessThanMinimumBugget), "Bugget with amount less than MINIMUM should return false");

        // 10. Тестирование isBuggetTrueLaws с суммой менее 8 десятичных знаков
        Laws lessDecimalBugget = new Laws();
        lessDecimalBugget.setPacketLawName("BUDGET");
        lessDecimalBugget.setLaws(Arrays.asList("pubKey1 100.1234", "pubKey2 200.12345678"));
        lessDecimalBugget.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(lessDecimalBugget)));

        assertFalse(isBuggetTrueLaws(lessDecimalBugget), "Bugget with amount having less than 8 decimal places should return false");

        // 11. Тестирование isBuggetTrueLaws с суммой более 8 десятичных знаков без округления
        Laws moreDecimalBugget = new Laws();
        moreDecimalBugget.setPacketLawName("BUDGET");
        moreDecimalBugget.setLaws(Arrays.asList("pubKey1 100.123456789", "pubKey2 200.12345678"));
        moreDecimalBugget.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(moreDecimalBugget)));

        assertFalse(isBuggetTrueLaws(moreDecimalBugget), "Bugget with amount having more than 8 decimal places without truncation should return false");

        // 12. Тестирование isBuggetTrueLaws с отрицательной суммой
        Laws negativeAmountBugget = new Laws();
        negativeAmountBugget.setPacketLawName("BUDGET");
        negativeAmountBugget.setLaws(Arrays.asList("pubKey1 -100.00000001", "pubKey2 200.12345678"));
        negativeAmountBugget.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(negativeAmountBugget)));

        assertFalse(isBuggetTrueLaws(negativeAmountBugget), "Bugget with negative amount should return false");
    }



    /**
     * Генерирует список записей sendMoney с случайными адресами и суммами.
     *
     * @param senderPubKeys Список публичных ключей отправителей.
     * @param minSenders    Минимальное количество отправителей для выбора.
     * @param maxSenders    Максимальное количество отправителей для выбора.
     * @param maxTotal      Максимальная общая сумма для распределения.
     * @return Список строк sendMoney в формате "address amount".
     */
    private List<String> generateRandomSendMoney(List<String> senderPubKeys, int minSenders, int maxSenders, BigDecimal maxTotal) {
        List<String> sendMoney = new ArrayList<>();
        Random random = new Random();

        // Определение количества отправителей для выбора
        int numberOfSenders = random.nextInt(maxSenders - minSenders + 1) + minSenders;

        // Перемешивание списка для обеспечения случайности
        List<String> shuffledSenders = new ArrayList<>(senderPubKeys);
        Collections.shuffle(shuffledSenders, random);

        // Выбор первых 'numberOfSenders' отправителей
        List<String> selectedSenders = shuffledSenders.subList(0, numberOfSenders);

        // Инициализация оставшейся суммы
        BigDecimal remainingAmount = maxTotal;

        for (int i = 0; i < selectedSenders.size(); i++) {
            String address = selectedSenders.get(i);

            // Для последнего отправителя назначаем оставшуюся сумму
            BigDecimal amount;
            if (i == selectedSenders.size() - 1) {
                amount = remainingAmount;
            } else {
                // Генерация случайной суммы между 0.00000001 и (remainingAmount / оставшиеся отправители)
                BigDecimal maxAmount = remainingAmount.divide(BigDecimal.valueOf(selectedSenders.size() - i), 8, BigDecimal.ROUND_DOWN);
                double randomDouble = 0.00000001 + (1000 - 0.00000001) * random.nextDouble();
                amount = BigDecimal.valueOf(randomDouble).setScale(8, BigDecimal.ROUND_DOWN);

                // Убедиться, что сумма не превышает оставшуюся
                if (amount.compareTo(remainingAmount) > 0) {
                    amount = remainingAmount;
                }

                remainingAmount = remainingAmount.subtract(amount);
            }

            // Форматирование суммы до 8 десятичных знаков
            amount = amount.setScale(8, BigDecimal.ROUND_DOWN);

            // Добавление в список sendMoney
            sendMoney.add(address + " " + amount.toPlainString());
        }

        return sendMoney;
    }

    public static boolean sendFromBudget(Laws budgetLaw, Map<String, Account> balances, Account budget){
        System.out.println("Sending money from budget");
        List<String> temp = budgetLaw.getLaws();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // First, validate all entries
        for (String s : temp) {
            String[] send = s.split(" ");

            // Check if the entry has exactly two parts: address and amount
            if (send.length != 2) {
                System.out.println("Invalid format: " + s + ". Expected 'address amount'.");
                return false;
            }

            String address = send[0];
            String amountStr = send[1];

            // Check if the address exists in balances
            if (!balances.containsKey(address)) {
                System.out.println("Invalid address: " + address);
                return false;
            }

            // Check if the amount is a valid number
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address);
                return false;
            }

            // Check if the amount is positive
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must be positive.");
                return false;
            }

            // Accumulate the total amount to be sent
            totalAmount = totalAmount.add(amount);
        }

        // Check if the total amount exceeds the budget's balance
        if (totalAmount.compareTo(budget.getDigitalDollarBalance()) > 0){
            System.out.println("Total amount to send (" + totalAmount + ") exceeds budget balance (" + budget.getDigitalDollarBalance() + ").");
            return false;
        }

        // All validations passed, perform the transfers
        for (String s : temp) {
            String[] send = s.split(" ");
            String address = send[0];
            BigDecimal amount = new BigDecimal(send[1]);

            Account account = balances.get(address);
            System.out.println("-----------------------");
            System.out.println("Account before send: " + account);

            // Update the recipient's balance
            account.setDigitalDollarBalance(account.getDigitalDollarBalance().add(amount));

            // Update the budget's balance
            budget.setDigitalDollarBalance(budget.getDigitalDollarBalance().subtract(amount));

            System.out.println("Account after send: " + account);
            System.out.println("-----------------------");
        }

        return true;
    }
    public static boolean rollbackFromBudget(Laws budgetLaw, Map<String, Account> balances, Account budget) {
        System.out.println("Rolling back money to budget");
        List<String> temp = budgetLaw.getLaws();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Сначала валидируем все записи
        for (String s : temp) {
            String[] send = s.split(" ");

            // Проверка, что запись содержит ровно два элемента: адрес и сумма
            if (send.length != 2) {
                System.out.println("Invalid format: " + s + ". Expected 'address amount'.");
                return false;
            }

            String address = send[0];
            String amountStr = send[1];

            // Проверка, что адрес существует в балансах
            if (!balances.containsKey(address)) {
                System.out.println("Invalid address: " + address);
                return false;
            }

            // Проверка, что сумма является валидным числом
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address);
                return false;
            }

            // Проверка, что сумма положительная
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must be positive.");
                return false;
            }

            // Проверка, что сумма имеет не меньше 8 десятичных знаков
            if (amount.scale() < 8) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must have at least 8 decimal places.");
                return false;
            }

            // Проверка, что сумма учитывает только последние 8 знаков без округления
            BigDecimal truncatedAmount = amount.setScale(8, BigDecimal.ROUND_DOWN);
            if (amount.compareTo(truncatedAmount) != 0) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must have exactly 8 decimal places without rounding.");
                return false;
            }

            // Проверка, что у аккаунта достаточно средств для снятия
            Account account = balances.get(address);
            if (account.getDigitalDollarBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient funds: " + address + " has " + account.getDigitalDollarBalance() + ", attempted to withdraw " + amount);
                return false;
            }

            // Накопление общей суммы для проверки после валидации всех записей
            totalAmount = totalAmount.add(amount);
        }

        // Все проверки пройдены, выполняем откат транзакций
        for (String s : temp) {
            String[] send = s.split(" ");
            String address = send[0];
            BigDecimal amount = new BigDecimal(send[1]);

            Account account = balances.get(address);
            System.out.println("-----------------------");
            System.out.println("Account before rollback: " + account);

            // Снятие средств с аккаунта
            account.setDigitalDollarBalance(account.getDigitalDollarBalance().subtract(amount));

            // Добавление средств в бюджет
            budget.setDigitalDollarBalance(budget.getDigitalDollarBalance().add(amount));

            System.out.println("Account after rollback: " + account);
            System.out.println("Budget after rollback: " + budget);
            System.out.println("-----------------------");
        }

        return true;
    }

    @Test
    public void testSendFromBudgetExceedsBalance() throws Exception {
        // Инициализация
        Map<String, Account> balances = new HashMap<>();
        Account budget = new Account("BUDGET", BigDecimal.valueOf(1000), BigDecimal.ZERO, BigDecimal.ZERO);
        balances.put("BUDGET", budget);
        List<String> sendMoney = Arrays.asList("address1 600", "address2 500"); // Сумма: 1100 > 1000

        Laws budgetLaw = new Laws("BUDGET", sendMoney);

        // Выполнение отправки
        boolean result = sendFromBudget(budgetLaw, balances, budget);

        // Проверка
        assertFalse(result);
        System.out.println("Test send exceeds balance: " + result);
    }


    // Adjusted calculateBuletin method
    public static void calculateBuletin(
            Map<String, Account> voters,
            Block block,
            Map<String, List<Laws>> collectedBallots,
            boolean isFinalCalculation,
            int numberOfDirectors,
            List<String> winners
    ) {
        if (block != null) {
            // Process transactions in the given block
            for (DtoTransaction transaction : block.getDtoTransactions()) {
                String sender = transaction.getSender();
                Laws laws = transaction.getLaws();

                // Collect ballots from transactions
                collectedBallots.computeIfAbsent(sender, k -> new ArrayList<>()).add(laws);
                System.out.println("Collected ballot from sender: " + sender);
            }
        }

        if (isFinalCalculation) {
            // Process only the latest ballot from each sender
            Map<String, Laws> latestBallots = new HashMap<>();

            for (Map.Entry<String, List<Laws>> entry : collectedBallots.entrySet()) {
                String sender = entry.getKey();
                List<Laws> ballotsList = entry.getValue();
                // Get the latest ballot for the sender
                Laws latestBallot = ballotsList.get(ballotsList.size() - 1);
                latestBallots.put(sender, latestBallot);
            }

            // Now call performSTVCounting using the latestBallots
            performSTVCounting(voters, latestBallots, numberOfDirectors, winners);
        }
    }

    // STV counting method with Droop quota and implementation of options a, b, and d
    private static void performSTVCounting(Map<String, Account> balances, Map<String, Laws> ballots, int numberOfSeats, List<String> winners) {
        Map<String, BigDecimal> candidateVotes = new HashMap<>();
        Set<String> eliminatedCandidates = new HashSet<>();
        Set<String> electedCandidates = new HashSet<>();

        // Initialize candidate votes
        Set<String> allCandidates = new HashSet<>();
        for (Laws ballot : ballots.values()) {
            allCandidates.addAll(ballot.getLaws());
        }
        for (String candidate : allCandidates) {
            candidateVotes.put(candidate, BigDecimal.ZERO);
        }

        int totalSeats = numberOfSeats;
        BigDecimal totalVotes = balances.values().stream()
                .map(Account::getDigitalStockBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate Droop quota
        BigDecimal quota = totalVotes.divide(BigDecimal.valueOf(numberOfSeats + 1), MathContext.DECIMAL128).add(BigDecimal.ONE).setScale(0, RoundingMode.FLOOR);
        System.out.println("Quota is: " + quota);

        // Copy of original balances to reset weights each round
        Map<String, BigDecimal> originalBalances = new HashMap<>();
        for (Map.Entry<String, Account> entry : balances.entrySet()) {
            originalBalances.put(entry.getKey(), entry.getValue().getDigitalStockBalance());
        }

        while (electedCandidates.size() < totalSeats && !allCandidates.isEmpty()) {
            // Reset candidate votes
            for (String candidate : candidateVotes.keySet()) {
                candidateVotes.put(candidate, BigDecimal.ZERO);
            }

            // Count votes based on current ballots
            for (Map.Entry<String, Laws> entry : ballots.entrySet()) {
                String sender = entry.getKey();
                Laws ballot = entry.getValue();
                BigDecimal weight = balances.get(sender).getDigitalStockBalance();

                // Find the highest-ranked candidate who is not eliminated or elected
                for (String preference : ballot.getLaws()) {
                    if (!eliminatedCandidates.contains(preference) && !electedCandidates.contains(preference)) {
                        candidateVotes.put(preference, candidateVotes.get(preference).add(weight));
                        break;
                    }
                }
            }

            // Check if any candidate meets the quota
            boolean candidateElectedThisRound = false;
            for (String candidate : new HashSet<>(candidateVotes.keySet())) {
                if (electedCandidates.contains(candidate) || eliminatedCandidates.contains(candidate)) {
                    continue;
                }
                BigDecimal votes = candidateVotes.get(candidate);
                System.out.println("Candidate " + candidate + " has " + votes + " votes.");
                if (votes.compareTo(quota) >= 0) {
                    System.out.println("Candidate " + candidate + " is elected.");
                    electedCandidates.add(candidate);
                    winners.add(candidate);
                    candidateElectedThisRound = true;

                    // Calculate surplus
                    BigDecimal surplus = votes.subtract(quota);
                    if (surplus.compareTo(BigDecimal.ZERO) > 0) {
                        // Redistribute surplus votes
                        redistributeSurplus(balances, ballots, candidate, surplus, votes, eliminatedCandidates, electedCandidates);
                    }

                    // Remove the elected candidate from allCandidates
                    allCandidates.remove(candidate);

                    // Since a candidate was elected, restart the counting process
                    break;
                }
            }

            if (candidateElectedThisRound) {
                continue; // Go back to counting with updated ballots
            }

            // Option a: If the number of remaining candidates equals the number of unfilled seats, declare them elected
            int remainingCandidates = 0;
            for (String candidate : allCandidates) {
                if (!electedCandidates.contains(candidate) && !eliminatedCandidates.contains(candidate)) {
                    remainingCandidates++;
                }
            }
            int remainingSeats = totalSeats - electedCandidates.size();
            if (remainingCandidates == remainingSeats) {
                System.out.println("The number of remaining candidates equals the number of unfilled seats. Electing remaining candidates:");
                for (String candidate : allCandidates) {
                    if (!electedCandidates.contains(candidate) && !eliminatedCandidates.contains(candidate)) {
                        System.out.println("Candidate " + candidate + " is elected automatically.");
                        electedCandidates.add(candidate);
                        winners.add(candidate);
                    }
                }
                break; // All seats are filled
            }

            // Option b: Exclude the candidate with the least votes
            String candidateToEliminate = null;
            BigDecimal leastVotes = null;
            for (String candidate : candidateVotes.keySet()) {
                if (eliminatedCandidates.contains(candidate) || electedCandidates.contains(candidate)) {
                    continue;
                }
                BigDecimal votes = candidateVotes.get(candidate);
                if (leastVotes == null || votes.compareTo(leastVotes) < 0) {
                    leastVotes = votes;
                    candidateToEliminate = candidate;
                }
            }
            if (candidateToEliminate != null) {
                System.out.println("Eliminating candidate " + candidateToEliminate + " with " + leastVotes + " votes.");
                eliminatedCandidates.add(candidateToEliminate);
                allCandidates.remove(candidateToEliminate);

                // Redistribute votes of the eliminated candidate
                redistributeEliminatedCandidateVotes(balances, ballots, candidateToEliminate, eliminatedCandidates, electedCandidates, candidateVotes);
            } else {
                // Option d: If no candidates can be eliminated, elect candidates with the highest votes
                System.out.println("No more candidates can be eliminated. Electing candidates with highest votes.");
                List<Map.Entry<String, BigDecimal>> remainingCandidatesList = new ArrayList<>();
                for (Map.Entry<String, BigDecimal> entry : candidateVotes.entrySet()) {
                    String candidate = entry.getKey();
                    if (!electedCandidates.contains(candidate) && !eliminatedCandidates.contains(candidate)) {
                        remainingCandidatesList.add(entry);
                    }
                }
                // Sort candidates by votes descending
                remainingCandidatesList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
                for (Map.Entry<String, BigDecimal> entry : remainingCandidatesList) {
                    if (electedCandidates.size() < totalSeats) {
                        String candidate = entry.getKey();
                        System.out.println("Candidate " + candidate + " is elected based on highest votes.");
                        electedCandidates.add(candidate);
                        winners.add(candidate);
                    } else {
                        break;
                    }
                }
                break; // All seats are filled
            }
        }

        // Reset balances to original after counting
        for (Map.Entry<String, Account> entry : balances.entrySet()) {
            entry.getValue().setDigitalStockBalance(originalBalances.get(entry.getKey()));
        }
    }

    // Method to redistribute surplus votes proportionally
    private static void redistributeSurplus(Map<String, Account> balances, Map<String, Laws> ballots, String electedCandidate, BigDecimal surplus, BigDecimal totalVotesForCandidate, Set<String> eliminatedCandidates, Set<String> electedCandidates) {
        // Map to store each voter's contribution to the elected candidate
        Map<String, BigDecimal> voterContributions = new HashMap<>();

        // Calculate each voter's contribution
        for (Map.Entry<String, Laws> entry : ballots.entrySet()) {
            String sender = entry.getKey();
            Laws ballot = entry.getValue();
            Account voterAccount = balances.get(sender);
            BigDecimal weight = voterAccount.getDigitalStockBalance();

            // Check if this ballot contributed to the elected candidate
            boolean contributed = false;
            for (String preference : ballot.getLaws()) {
                if (!eliminatedCandidates.contains(preference)) {
                    if (preference.equals(electedCandidate)) {
                        contributed = true;
                    }
                    break; // Stop at the first non-eliminated candidate
                }
            }

            if (contributed) {
                voterContributions.put(sender, weight);
            }
        }

        // Calculate transfer value (proportional)
        BigDecimal transferValue = surplus.divide(totalVotesForCandidate, MathContext.DECIMAL128);

        // Adjust the weights of the contributing voters
        for (Map.Entry<String, BigDecimal> entry : voterContributions.entrySet()) {
            String sender = entry.getKey();
            BigDecimal weight = entry.getValue();
            Account voterAccount = balances.get(sender);

            // Calculate the surplus portion to subtract
            BigDecimal surplusPortion = weight.multiply(transferValue);

            // Update the voter's weight
            BigDecimal newWeight = voterAccount.getDigitalStockBalance().subtract(surplusPortion);
            voterAccount.setDigitalStockBalance(newWeight);

            // Ensure weight doesn't go negative
            if (voterAccount.getDigitalStockBalance().compareTo(BigDecimal.ZERO) < 0) {
                voterAccount.setDigitalStockBalance(BigDecimal.ZERO);
            }
        }
    }

    // Method to redistribute votes from an eliminated candidate
    private static void redistributeEliminatedCandidateVotes(Map<String, Account> balances, Map<String, Laws> ballots, String eliminatedCandidate, Set<String> eliminatedCandidates, Set<String> electedCandidates, Map<String, BigDecimal> candidateVotes) {
        // Collect voters who voted for the eliminated candidate
        for (Map.Entry<String, Laws> entry : ballots.entrySet()) {
            String sender = entry.getKey();
            Laws ballot = entry.getValue();
            Account voterAccount = balances.get(sender);
            BigDecimal weight = voterAccount.getDigitalStockBalance();

            // Check if this ballot's next preference is the eliminated candidate
            boolean foundEliminatedCandidate = false;
            for (String preference : ballot.getLaws()) {
                if (electedCandidates.contains(preference)) {
                    continue;
                }
                if (eliminatedCandidates.contains(preference)) {
                    if (preference.equals(eliminatedCandidate)) {
                        foundEliminatedCandidate = true;
                    }
                    continue;
                }
                if (foundEliminatedCandidate) {
                    // Transfer voter's weight to the next preference
                    candidateVotes.put(preference, candidateVotes.getOrDefault(preference, BigDecimal.ZERO).add(weight));
                    break;
                }
                if (preference.equals(eliminatedCandidate)) {
                    foundEliminatedCandidate = true;
                }
            }
        }
    }

    // Method to rollback the latest ballots from specific senders
    public static void rollbackBuletin(Set<String> sendersToRollback, Map<String, List<Laws>> collectedBallots) {
        for (String sender : sendersToRollback) {
            List<Laws> ballotsList = collectedBallots.get(sender);
            if (ballotsList != null && !ballotsList.isEmpty()) {
                // Remove the latest ballot
                ballotsList.remove(ballotsList.size() - 1);
                System.out.println("Rolled back latest ballot from sender: " + sender);
            }
        }
    }

}
