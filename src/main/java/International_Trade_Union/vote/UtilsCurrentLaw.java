package International_Trade_Union.vote;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilsCurrentLaw {
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


    //подсчет по штучно баланса
    public static Map<String, CurrentLawVotes> calculateVote(Map<String, CurrentLawVotes> votes, List<Account> voters, Block block) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();
        List<String> signs = new ArrayList<>();


        System.out.println("calculate voting: index: " + block.getIndex());
        for (int j = 0; j < block.getDtoTransactions().size(); j++) {

            DtoTransaction transaction = block.getDtoTransactions().get(j);


            if (signs.contains(transaction.getSign())) {
                System.out.println("this transaction signature has already been used and is not valid: sender: "
                        + transaction.getSender() + " customer: " + transaction.getCustomer());
                continue;
            } else {

                signs.add(base.encode(transaction.getSign()));
//                System.out.println("we added new sign transaction");
            }
            if (transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                System.out.println("law balance cannot be sender");
                continue;
            }
            if (transaction.verify() && transaction.getCustomer().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                for (Account account : voters) {

                    if (transaction.getSender().equals(account.getAccount())) {
                        CurrentLawVotes currentLawVotes = votes.get(transaction.getCustomer());

                        if (currentLawVotes == null) {
                            currentLawVotes = new CurrentLawVotes();
                            currentLawVotes.setAddressLaw(transaction.getCustomer());
                            currentLawVotes.setYES(new HashMap<>());
                            currentLawVotes.setNO(new HashMap<>());
                            currentLawVotes.setIndexCreateLaw(block.getIndex());
                            currentLawVotes.setWhoCreate(transaction.getSender());

                            votes.put(transaction.getCustomer(), currentLawVotes);
                        }

                        if (transaction.getVoteEnum().equals(VoteEnum.YES)) {

                            currentLawVotes.getYES().put(transaction.getSender(), block.getIndex());
                            currentLawVotes.getNO().remove(transaction.getSender());

                        } else if (transaction.getVoteEnum().equals(VoteEnum.NO)) {
                            currentLawVotes.getNO().put(transaction.getSender(), block.getIndex());
                            currentLawVotes.getYES().remove(transaction.getSender());
                        } else if (transaction.getVoteEnum().equals(VoteEnum.REMOVE_YOUR_VOICE)) {
                            currentLawVotes.getNO().remove(transaction.getSender());
                            currentLawVotes.getYES().remove(transaction.getSender());
                        }
                    }
                }

            }

        }


        return votes;

    }


    //подсчет целиком баланса
    public static Map<String, CurrentLawVotes> calculateVotes(List<Account> governments, List<Block> blocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, CurrentLawVotes> votes = new HashMap<>();
        for (Block block : blocks) {
            calculateVote(votes, governments, block);
        }

        return votes;

    }

    //возвращаяет усредненное количество голосов,
    //суть проста если есть один акаунт и он имеет 100 акций
    //и проголосовал за один закон то все сто акций будут для этого закона как сто голосов
    //если за два закона то 100/2 то есть если он на протяжении трех лет проголосовал
    //за n законов, то его голоса делятся на n.
    public static Map<String, Integer> calculateAverageVotesYes(Map<String, CurrentLawVotes> votesMap) {
        Map<String, Integer> voteAverage = new HashMap<>();
        for (Map.Entry<String, CurrentLawVotes> current : votesMap.entrySet()) {
            for (Map.Entry<String, Long> yesVoteAddress : current.getValue().getYES().entrySet()) {
                if (voteAverage.containsKey(yesVoteAddress)) {
                    int count = voteAverage.get(yesVoteAddress);
                    voteAverage.put(yesVoteAddress.getKey(), count + 1);
                } else {
                    int count = 1;
                    voteAverage.put(yesVoteAddress.getKey(), count);
                }
            }

        }
        return voteAverage;
    }

    //подсчитывает голоса No
    public static Map<String, Integer> calculateAverageVotesNo(Map<String, CurrentLawVotes> votesMap) {
        Map<String, Integer> voteAverage = new HashMap<>();
        for (Map.Entry<String, CurrentLawVotes> current : votesMap.entrySet()) {
            for (Map.Entry<String, Long> yesVoteAddress : current.getValue().getNO().entrySet()) {
                if (voteAverage.containsKey(yesVoteAddress)) {
                    int count = voteAverage.get(yesVoteAddress);
                    voteAverage.put(yesVoteAddress.getKey(), count + 1);
                } else {
                    int count = 1;
                    voteAverage.put(yesVoteAddress.getKey(), count);
                }
            }

        }
        return voteAverage;
    }
    public static VoteMapAndLastIndex processBlocksWithWindow(
            VoteMapAndLastIndex state,
            List<Account> voters,
            long size,
            BlockService blockService
    ) throws IOException {
        long newFrom = Math.max(0, size - Seting.LAW_HALF_VOTE);
        long oldFrom = state.getStartIndex();
        long oldTo = state.getFinishIndex();
        Map<String, CurrentLawVotes> votesMap = state.getVotesMap();

        // 1) Если новое "from" > старого, удаляем голоса из блоков, чей индекс < newFrom
        if (newFrom > oldFrom) {
            for (Map.Entry<String, CurrentLawVotes> e : votesMap.entrySet()) {
                CurrentLawVotes clv = e.getValue();
                // Убираем из YES / NO все записи, у которых номер блока < newFrom
                clv.getYES().entrySet().removeIf(x -> x.getValue() < newFrom);
                clv.getNO().entrySet().removeIf(x -> x.getValue() < newFrom);
            }
        }

        // 2) Обрабатываем новые блоки, если старый finishIndex меньше size
        long startIndexForNewBlocks = Math.max(newFrom, oldTo + 1);
        if (startIndexForNewBlocks < size) {
            final int BATCH_SIZE = 500;
            Runtime runtime = Runtime.getRuntime();
            for (long currentFrom = startIndexForNewBlocks; currentFrom < size; currentFrom += BATCH_SIZE) {
                long currentTo = Math.min(currentFrom + BATCH_SIZE, size);
                long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
                List<Block> batchBlocks = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                        blockService.findBySpecialIndexBetween(currentFrom, currentTo)
                );
                for (Block block : batchBlocks) {
                    try {
                        UtilsCurrentLaw.calculateVote(votesMap, voters, block);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                long afterMemory = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("Blocks processed up to: " + currentTo
                        + ", memory used: " + (afterMemory - beforeMemory) / 1024 + " KB");
            }
        }

        // 3) Обновляем состояние
        state.setVotesMap(votesMap);
        state.setStartIndex(newFrom);
        state.setFinishIndex(size - 1);

        return state;
    }


    //механизм для подсчета
    public static void processBlocks(Map<String, CurrentLawVotes> votesMap, List<Account> voters, long size, BlockService blockService) {
        long from = Math.max(0, size - Seting.LAW_HALF_VOTE);
        final int BATCH_SIZE = 500;
        Runtime runtime = Runtime.getRuntime();

        for (long currentFrom = from; currentFrom < size; currentFrom += BATCH_SIZE) {
            long currentTo = Math.min(currentFrom + BATCH_SIZE, size);
            try {
                // Измеряем память до операции
                long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

                // Извлечение и обработка блоков
                List<Block> batchBlocks = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                        blockService.findBySpecialIndexBetween(currentFrom, currentTo)
                );

                batchBlocks.forEach(block -> {
                    try {
                        UtilsCurrentLaw.calculateVote(votesMap, voters, block);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    } catch (SignatureException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchProviderException e) {
                        throw new RuntimeException(e);
                    } catch (InvalidKeyException e) {
                        throw new RuntimeException(e);
                    }
                });

                // Измеряем память после операции
                long afterMemory = runtime.totalMemory() - runtime.freeMemory();

                // Логируем использование памяти
                System.out.println("Blocks processed to index: " + currentFrom +
                        ", Memory used: " + (afterMemory - beforeMemory) / 1024 + " KB");
                System.out.println("processBlocks: votesMap.size: " + votesMap.size() + " voters.size: " + voters.size() );

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println("Обработка блоков завершена.");
    }
}
