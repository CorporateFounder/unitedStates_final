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
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;

public class Hybrid {

    int assembly = 31;
    int directors = 5;
    //вероятность быть выбраным в асамблею
    double probability = 0.3;
    /**
     * Elects 5 directors using Approval Voting among the top patrons.
     *
     * @param block        The block containing transactions to process.
     * @param transactions A map to store the maximum transaction for each sender.
     * @param isFinish     Indicates whether to perform the final tally.
     * @return A list of elected directors if isFinish is true; otherwise, an empty list.
     * @throws Exception If an error occurs during processing.
     */
    public List<String> electDirectors(Block block, Map<String, DtoTransaction> transactions, boolean isFinish) throws Exception {
        // Process the block
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            if ("BUDGET".equals(transaction.getCustomer())) {
                String sender = transaction.getSender();

                // Update the maximum amount for each sender
                transactions.merge(sender, transaction, (existingTx, newTx) ->
                        existingTx.getDigitalDollar() < newTx.getDigitalDollar() ? newTx : existingTx
                );
            }
        }

        if (!isFinish) {
            // Continue collecting data, return an empty list
            return Collections.emptyList();
        } else {
            // Final tally and elect directors

            // Step 1: Select top patrons based on maximum amount
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
                        // Deterministic order for equal amounts
                        try {
                            long e1Deterministic = hashToLong(e1.getKey());
                            long e2Deterministic = hashToLong(e2.getKey());
                            return Long.compare(e1Deterministic, e2Deterministic);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException("SHA-256 unavailable", ex);
                        }
                    })
                    .limit(500)
                    .collect(Collectors.toList());

            Set<String> topSenders = sortedTransactions.stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            // Step 2: Collect ballots from top senders
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

            // Step 3: Tally votes using Approval Voting
            Map<String, Integer> candidateVotes = new HashMap<>();
            for (Laws ballot : validBallots.values()) {
                for (String candidate : ballot.getLaws()) {
                    candidateVotes.put(candidate, candidateVotes.getOrDefault(candidate, 0) + 1);
                }
            }

            // Step 4: Elect 5 directors based on the highest votes
            int numberOfDirectors = directors;
            List<String> electedDirectors = candidateVotes.entrySet()
                    .stream()
                    .sorted((e1, e2) -> {
                        int compareByVotes = Integer.compare(e2.getValue(), e1.getValue());
                        if (compareByVotes != 0) {
                            return compareByVotes;
                        }
                        // Deterministic order for equal votes
                        try {
                            long e1Deterministic = hashToLong(e1.getKey());
                            long e2Deterministic = hashToLong(e2.getKey());
                            return Long.compare(e1Deterministic, e2Deterministic);
                        } catch (NoSuchAlgorithmException ex) {
                            throw new RuntimeException("SHA-256 unavailable", ex);
                        }
                    })
                    .limit(numberOfDirectors)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            return electedDirectors;
        }
    }

    /**
     * Randomly selects 31 assembly members from the top patrons, ensuring no single member has a selection probability higher than 30%.
     *
     * @param block        The block containing transactions.
     * @param transactions A map of transactions with the sender's public key as the key.
     * @param isFinish     Indicates whether to finalize processing and select members.
     * @return A list of selected assembly members.
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is unavailable.
     */
    public List<String> selectAssemblyMembers(Block block, Map<String, DtoTransaction> transactions, boolean isFinish) throws NoSuchAlgorithmException {
        // Process the block
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            if ("BUDGET".equals(transaction.getCustomer())) {
                String sender = transaction.getSender();

                // Update the maximum amount for each sender
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
                            throw new RuntimeException("SHA-256 algorithm unavailable", ex);
                        }
                    })
                    .collect(Collectors.toList());

            // Limit to top patrons (e.g., top 500)
            List<String> topPatrons = sortedSenders.stream()
                    .limit(500)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            // Modify the selection to ensure maximum probability does not exceed 30%
            double maxProbability = probability; // 30%
            List<String> selectedMembers = weightedDeterministicSelection(
                    topPatrons,
                    senderMaxContributions,
                    assembly,
                    blockHash,
                    maxProbability
            );

            return selectedMembers;
        }
    }

    /**
     * Converts a string to a deterministic long value using SHA-256.
     *
     * @param input Input string.
     * @return Long value derived from the hash.
     * @throws NoSuchAlgorithmException If SHA-256 is unavailable.
     */
    private long hashToLong(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        ByteBuffer buffer = ByteBuffer.wrap(hashBytes);
        return buffer.getLong();
    }

    /**
     * Validates if the provided Laws object is a valid approval ballot.
     *
     * @param laws Laws object to validate.
     * @return True if the ballot is valid, otherwise False.
     * @throws IOException If an error occurs during processing.
     */
    public static boolean isValidApprovalBulletin(Laws laws) throws IOException {
        // Check if Laws object and its fields are not null
        if (laws == null || laws.getPacketLawName() == null || laws.getHashLaw() == null || laws.getLaws() == null) {
            return false;
        }

        // Check if PacketLawName equals "APPROVAL_BULLETIN"
        if (!"APPROVAL_BULLETIN".equals(laws.getPacketLawName())) {
            return false;
        }

        // Check if the list of laws contains no more than 10 entries
        if (laws.getLaws().size() > 10) {
            return false;
        }

        // Check if each entry in laws is a valid public key without spaces
        for (String lawEntry : laws.getLaws()) {
            if (lawEntry == null || lawEntry.trim().isEmpty() || lawEntry.contains(" ")) {
                return false;
            }
        }

        // Verify hash
        Laws lawsForHash = new Laws();
        lawsForHash.setPacketLawName(laws.getPacketLawName());
        lawsForHash.setLaws(laws.getLaws());

        String expectedHash = Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(lawsForHash));

        return expectedHash.equals(laws.getHashLaw());
    }

    /**
     * Weighted deterministic selection ensuring no member has a selection probability higher than maxProbability.
     *
     * @param eligibleSenders       List of eligible senders.
     * @param senderContributions   Map of senders and their maximum contributions.
     * @param count                 Number of members to select.
     * @param blockHash             Block hash for deterministic randomness.
     * @param maxIndividualProbability Maximum individual selection probability.
     * @return List of selected assembly members.
     * @throws NoSuchAlgorithmException If SHA-256 is unavailable.
     */
    private List<String> weightedDeterministicSelection(
            List<String> eligibleSenders,
            Map<String, Double> senderContributions,
            int count,
            String blockHash,
            double maxIndividualProbability
    ) throws NoSuchAlgorithmException {
        long seed = hashToLong(blockHash);
        Random random = new Random(seed);

        Map<String, Double> weights = new HashMap<>();
        double totalContribution = senderContributions.values().stream().mapToDouble(Double::doubleValue).sum();

        // Calculate weights with maximum individual probability
        double totalWeight = 0.0;
        for (String sender : eligibleSenders) {
            double contribution = senderContributions.getOrDefault(sender, 0.0);
            double weight = contribution / totalContribution;
            weight = Math.min(weight, maxIndividualProbability / count); // Ensure max probability
            weights.put(sender, weight);
            totalWeight += weight;
        }

        // Normalize weights
        for (String sender : weights.keySet()) {
            double normalizedWeight = weights.get(sender) / totalWeight;
            weights.put(sender, normalizedWeight);
        }

        List<String> selectedMembers = new ArrayList<>();
        Set<String> selectedSet = new HashSet<>();
        while (selectedMembers.size() < count && selectedSet.size() < eligibleSenders.size()) {
            double target = random.nextDouble();
            double cumulativeWeight = 0.0;

            for (String sender : eligibleSenders) {
                if (selectedSet.contains(sender)) continue;
                cumulativeWeight += weights.get(sender);
                if (cumulativeWeight >= target) {
                    selectedMembers.add(sender);
                    selectedSet.add(sender);
                    break;
                }
            }
        }

        return selectedMembers;
    }

    /**
     * Test method demonstrating both upper and lower house selection.
     *
     * @throws Exception If an error occurs during processing.
     */
    @Test
    public void testHybridSelection() throws Exception {
        Map<String, Account> balances = new HashMap<>();
        Base58 base = new Base58();
        Map<String, DtoTransaction> transactions = new HashMap<>();
        Map<String, DtoTransaction> transactionMap = new HashMap<>();

        // Create senders with specified balances
        List<Keys> keyPairs = new ArrayList<>();
        List<Account> senders = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();

        int numberOfSenders = 600; // Total number of senders
        int[] balancesArray = new int[numberOfSenders];
        int money = 10;
        for (int i = 0; i < numberOfSenders; i++) {
            balancesArray[i] = money;
            money += 10;
        }

        // Initialize senders and their balances
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

        // Create ballots and blocks
        for (int i = 0; i < numberOfSenders; i++) {
            Account senderAccount = senders.get(i);
            String senderPubKey = senderAccount.getAccount();
            String senderPrivKey = keyPairs.get(i).getPrivkey();
            PrivateKey senderPrivateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(senderPrivKey));

            // Create ballot (up to 10 candidates)
            Laws bulletin = new Laws();
            bulletin.setPacketLawName("APPROVAL_BULLETIN");
            List<String> candidates = getRandomCandidates(senders, 10);
            bulletin.setLaws(candidates);
            bulletin.setHashLaw(Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(bulletin)));

            // Create transaction with the sent amount (DigitalDollar)
            double transactionAmount = balancesArray[i] * 1.5; // Example amount
            DtoTransaction transaction = new DtoTransaction(
                    senderPubKey,
                    "BUDGET",
                    transactionAmount,
                    0.0,
                    bulletin,
                    0.0,
                    VoteEnum.YES
            );

            // Sign transaction
            byte[] transactionSign = UtilsSecurity.sign(senderPrivateKey, transaction.toSign());
            transaction.setSign(transactionSign);

            // Create block and add transaction
            Block block = new Block();
            block.setDtoTransactions(new ArrayList<>());
            block.getDtoTransactions().add(transaction);
            block.setHashBlock(UtilsUse.sha256hash(Integer.toString(i)));
            blocks.add(block);
        }

        // Process blocks incrementally
        List<String> electedDirectors = new ArrayList<>();
        List<String> selectedAssemblyMembers = new ArrayList<>();
        List<String> selectedAssemblyV2 = new ArrayList<>();
        List<String> selectedAssemblyV2part2 = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            boolean isFinish = (i == blocks.size() - 1);

            // Elect directors (upper house)
            List<String> directorResult = electDirectors(blocks.get(i), transactions, isFinish);
            if (isFinish) {
                electedDirectors = directorResult;
            }

            // Select assembly members (lower house)
            List<String> assemblyResult = selectAssemblyMembers(blocks.get(i), transactions, isFinish);
            if (isFinish) {
                selectedAssemblyMembers = assemblyResult;
            }
            processBlock(blocks.get(i), transactionMap);
            if(isFinish)
                selectedAssemblyV2 = selectWinners(transactionMap, blocks.get(i) , assembly, probability);

            if(isFinish)
                selectedAssemblyV2part2 = selectWinners(transactionMap, blocks.get(i-1) , assembly, probability);
        }

        // Output elected directors
        System.out.println("Elected Directors (Upper House):");
        for (String director : electedDirectors) {
            System.out.println(director);
        }

        // Output selected assembly members
        System.out.println("\nSelected Assembly Members (Lower House):");
        for (String member : selectedAssemblyMembers) {
            System.out.println(member);
        }
        // Output selected assembly members v2
        System.out.println("\nSelected Assembly Members (Lower House) V2:");
        for (String member : selectedAssemblyV2) {
            System.out.println(member);
        }
        // Output selected assembly members v2
        System.out.println("\nSelected Assembly Members (Lower House) V2 part2:");
        for (String member : selectedAssemblyV2part2) {
            System.out.println(member);
        }
    }

    /**
     * Generates a list of random public keys of candidates.
     *
     * @param senders List of all senders.
     * @param max     Maximum number of candidates to select.
     * @return List of public keys of candidates.
     */
    private List<String> getRandomCandidates(List<Account> senders, int max) {
        List<String> candidates = senders.stream()
                .map(Account::getAccount)
                .collect(Collectors.toList());

        Collections.shuffle(candidates);
        return candidates.stream().limit(max).collect(Collectors.toList());
    }


    //тестовые версии
    /**
     * Обрабатывает список блоков и создает карту транзакций.
     *
     * @param blocks Список блоков для обработки.
     * @return Карта транзакций с публичным ключом отправителя в качестве ключа.
     */
    /**
     * Обрабатывает один блок и обновляет карту транзакций.
     *
     * @param block        Блок для обработки.
     * @param transactions Карта транзакций, которую нужно обновить.
     */
    public void processBlock(Block block, Map<String, DtoTransaction> transactions) {
        for (DtoTransaction transaction : block.getDtoTransactions()) {
            if ("BUDGET".equals(transaction.getCustomer())) {
                String sender = transaction.getSender();

                // Обновляем максимальную сумму для каждого отправителя
                transactions.merge(sender, transaction, (existingTx, newTx) ->
                        existingTx.getDigitalDollar() < newTx.getDigitalDollar() ? newTx : existingTx
                );
            }
        }
    }

    /**
     * Случайно выбирает победителей из транзакций на основе хэша блока.
     *
     * @param transactions    Карта транзакций с публичным ключом отправителя в качестве ключа.
     * @param block           Блок, содержащий хэш для случайности.
     * @param assemblySize    Количество членов ассамблеи для выбора.
     * @param maxProbability  Максимальная индивидуальная вероятность выбора.
     * @return Список выбранных членов ассамблеи.
     * @throws NoSuchAlgorithmException Если SHA-256 недоступен.
     */
    public List<String> selectWinners(Map<String, DtoTransaction> transactions, Block block, int assemblySize, double maxProbability) throws NoSuchAlgorithmException {
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
                        throw new RuntimeException("SHA-256 недоступен", ex);
                    }
                })
                .collect(Collectors.toList());

        // Ограничиваем список топовыми участниками (например, топ-500)
        List<String> topPatrons = sortedSenders.stream()
                .limit(500)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Используем метод для случайного выбора победителей
        List<String> selectedMembers = weightedDeterministicSelection(
                topPatrons,
                senderMaxContributions,
                assemblySize,
                blockHash,
                maxProbability
        );

        return selectedMembers;
    }

}
