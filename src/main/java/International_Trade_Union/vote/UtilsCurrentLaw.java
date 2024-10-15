package International_Trade_Union.vote;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilsCurrentLaw {

    public static void calculateBuletin(Map<String, Account> voters, Block block, Map<String, BigDecimal> candidateVotes, Set<String> processedSenders, List<String> signs, boolean isFinalCalculation, int numberOfDirectors, List<String> winners) {
        Base base = new Base58();

        // Process transactions in the given block
        for (int i = 0; i < block.getDtoTransactions().size(); i++) {
            DtoTransaction transaction = block.getDtoTransactions().get(i);

            // Check if the signature has already been used
            if (signs.contains(transaction.getSign())) {
                System.out.println("This transaction signature has already been used and is not valid: sender: "
                        + transaction.getSender() + " customer: " + transaction.getCustomer());
                continue;
            } else {
                signs.add(base.encode(transaction.getSign()));
            }

            // Check if the sender is a law balance
            if (transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                System.out.println("Law balance cannot be sender");
                continue;
            }

            // Check if the packet law name is RCV_BULLETIN
            if (transaction.getLaws().getPacketLawName() != null &&
                    !transaction.getLaws().getPacketLawName().isEmpty() &&
                    transaction.getLaws().getPacketLawName().equals(Seting.RCV_BULLETIN)) {
                // Only process transactions for senders that haven't been fully processed
                if (!processedSenders.contains(transaction.getSender())) {
                    processedSenders.add(transaction.getSender());
                    Account voterAccount = voters.get(transaction.getSender());
                    if (voterAccount == null) {
                        System.out.println("Voter account not found for sender: " + transaction.getSender());
                        continue;
                    }

                    BigDecimal voterBalance = voterAccount.getDigitalStakingBalance();
                    List<String> candidateRatings = transaction.getLaws().getLaws();

                    // Apply RCV voting where each line represents a candidate in order of preference
                    for (int rank = 0; rank < candidateRatings.size(); rank++) {
                        String candidate = candidateRatings.get(rank);
                        BigDecimal weightedScore = voterBalance.divide(BigDecimal.valueOf(rank + 1), RoundingMode.HALF_UP);
                        candidateVotes.put(candidate, candidateVotes.getOrDefault(candidate, BigDecimal.ZERO).add(weightedScore));
                        System.out.println("Added " + weightedScore + " votes to candidate " + candidate);
                    }
                } else {
                    System.out.println("Sender already processed: " + transaction.getSender());
                }
            } else {
                System.out.println("Packet law name is not RCV_BULLETIN or is empty for sender: " + transaction.getSender());
            }
        }

        // If this is the final calculation, determine the winners using the revised RCV and Sainte-Laguë method
        if (isFinalCalculation) {
            // Run multiple rounds until all director positions are filled
            while (winners.size() < numberOfDirectors) {
                // Select candidate with the most votes in the current round
                Optional<Map.Entry<String, BigDecimal>> winnerEntry = candidateVotes.entrySet().stream()
                        .filter(entry -> !winners.contains(entry.getKey()))
                        .max(Map.Entry.comparingByValue());

                if (winnerEntry.isPresent()) {
                    String winner = winnerEntry.get().getKey();
                    winners.add(winner);
                    System.out.println("Winner selected: " + winner);

                    // Reduce the influence of votes for the next preferences in ballots where the winner was selected
                    for (Map.Entry<String, Account> entry : voters.entrySet()) {
                        Account voterAccount = entry.getValue();
                        List<String> candidateRatings = new ArrayList<>();
                        for (DtoTransaction transaction : block.getDtoTransactions()) {
                            if (transaction.getSender().equals(entry.getKey()) && transaction.getLaws() != null
                                    && transaction.getLaws().getPacketLawName() != null
                                    && transaction.getLaws().getPacketLawName().equals(Seting.RCV_BULLETIN)) {
                                candidateRatings = transaction.getLaws().getLaws();
                                break;
                            }
                        }

                        BigDecimal voterBalance = voterAccount.getDigitalStakingBalance();
                        boolean foundWinner = false;
                        int divisor = 1; // Sainte-Laguë divisor starts from 1

                        for (int rank = 0; rank < candidateRatings.size(); rank++) {
                            String candidate = candidateRatings.get(rank);

                            if (candidate.equals(winner)) {
                                // This candidate is the winner of the current round
                                foundWinner = true;
                                continue;
                            }

                            BigDecimal weightedScore = voterBalance.divide(BigDecimal.valueOf(rank + 1), RoundingMode.HALF_UP);

                            // If we have already found a winner from this ballot, reduce the influence for the next candidate using Sainte-Laguë divisor
                            if (foundWinner) {
                                divisor += 2; // Increment divisor as per Sainte-Laguë method (1, 3, 5, ...)
                                weightedScore = weightedScore.divide(BigDecimal.valueOf(divisor), RoundingMode.HALF_UP);
                            }

                            candidateVotes.put(candidate, candidateVotes.getOrDefault(candidate, BigDecimal.ZERO).add(weightedScore));
                            System.out.println((foundWinner ? "Reduced " : "Full ") + "weight score for candidate " + candidate + " after winner selection with divisor " + divisor);
                        }
                    }

                    // After determining a winner, re-evaluate remaining candidates based on next preferences to resolve ties
                    resolveTies(candidateVotes, winners);
                } else {
                    System.out.println("No more candidates to select as winner.");
                    break;
                }
            }
        }
    }

    private static void resolveTies(Map<String, BigDecimal> candidateVotes, List<String> winners) {
        // Find candidates with tied votes
        List<Map.Entry<String, BigDecimal>> tiedCandidates = candidateVotes.entrySet().stream()
                .filter(entry -> !winners.contains(entry.getKey()))
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Resolve ties using next preferences
        for (Map.Entry<String, BigDecimal> candidate : tiedCandidates) {
            // Logic to determine the candidate with the next highest preference in tied situations
            // Currently selects based on the highest remaining preference in ballots where a tie exists
            BigDecimal adjustedVote = candidate.getValue().add(BigDecimal.ONE); // Adjust for the tie-breaker
            candidateVotes.put(candidate.getKey(), adjustedVote);
            System.out.println("Adjusted vote for candidate " + candidate.getKey() + " to resolve tie.");
        }
    }

    /*
     * Voting Procedure for Director Election with Incremental Block Processing and RCV Voting
     *
     * 1. Data Collection:
     *    - Blocks are processed one at a time.
     *    - Transactions from each block are processed incrementally and votes are counted accordingly.
     *    - Only unique signatures are considered valid, to prevent duplicate voting.
     *    - Transactions originating from law balance accounts are ignored.
     *    - Only transactions with a law packet name of RCV_BULLETIN are considered for voting.
     *
     * 2. Candidate Identification:
     *    - The system extracts the candidates for each valid transaction and keeps track of the latest transactions for each sender.
     *    - Processed senders are tracked in `processedSenders` to avoid reprocessing the same sender.
     *
     * 3. Vote Initialization and Accumulation (RCV Voting):
     *    - Votes are accumulated incrementally as blocks are processed.
     *    - Each candidate is ranked in order of preference by the voter.
     *    - The voter's staking balance is divided by the rank to calculate the weighted vote for each candidate.
     *
     * 4. Final Calculation (Optional):
     *    - When `isFinalCalculation` is set to `true`, the system runs multiple voting rounds to fill all director positions.
     *    - In each round, the candidate with the most votes is selected as a winner.
     *    - Once a candidate wins, subsequent candidates in the voter's ballot are given reduced voting power using the Sainte-Laguë method.
     *    - This ensures that the influence of voters whose candidates have already won is reduced in subsequent rounds, creating a more proportional distribution.
     *
     * 5. Vote Redistribution:
     *    - For voters whose preferred candidate won a round, the votes for subsequent candidates are reduced using a decreasing divisor (1, 3, 5, etc.).
     *    - This redistribution continues until all director positions are filled.
     *
     * 6. Tie Resolution:
     *    - If there are candidates with tied votes, the system will re-evaluate the next preferences in the ballots to resolve the ties.
     *    - Adjusts the tied candidate's votes slightly to resolve the tie based on preference ranking.
     *
     * 7. Final Winner Selection:
     *    - Once the number of directors to be elected is reached, the system stops.
     *    - The winning candidates are collected and returned as a list of accounts.
     *
     * Summary:
     * - This voting system combines elements of RCV Voting and the Sainte-Laguë method to ensure a fair and proportional distribution of director positions.
     * - Votes are initially given full weight, but reduced in subsequent rounds if a voter's higher-ranked candidate has already won.
     * - This ensures that the influence of voters is balanced, preventing any single voter from having disproportionate influence over multiple positions.
     */
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
                            currentLawVotes.setYES(new HashSet<>());
                            currentLawVotes.setNO(new HashSet<>());

                            votes.put(transaction.getCustomer(), currentLawVotes);
                        }

                        if (transaction.getVoteEnum().equals(VoteEnum.YES)) {

                            currentLawVotes.getYES().add(transaction.getSender());
                            currentLawVotes.getNO().remove(transaction.getSender());

                        } else if (transaction.getVoteEnum().equals(VoteEnum.NO)) {
                            currentLawVotes.getNO().add(transaction.getSender());
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
            for (String yesVoteAddress : current.getValue().getYES()) {
                if (voteAverage.containsKey(yesVoteAddress)) {
                    int count = voteAverage.get(yesVoteAddress);
                    voteAverage.put(yesVoteAddress, count + 1);
                } else {
                    int count = 1;
                    voteAverage.put(yesVoteAddress, count);
                }
            }

        }
        return voteAverage;
    }

    //подсчитывает голоса No
    public static Map<String, Integer> calculateAverageVotesNo(Map<String, CurrentLawVotes> votesMap) {
        Map<String, Integer> voteAverage = new HashMap<>();
        for (Map.Entry<String, CurrentLawVotes> current : votesMap.entrySet()) {
            for (String yesVoteAddress : current.getValue().getNO()) {
                if (voteAverage.containsKey(yesVoteAddress)) {
                    int count = voteAverage.get(yesVoteAddress);
                    voteAverage.put(yesVoteAddress, count + 1);
                } else {
                    int count = 1;
                    voteAverage.put(yesVoteAddress, count);
                }
            }

        }
        return voteAverage;
    }


}
