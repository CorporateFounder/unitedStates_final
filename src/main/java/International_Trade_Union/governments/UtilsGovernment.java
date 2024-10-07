package International_Trade_Union.governments;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.vote.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilsGovernment {

    /**чтобы pubkey был зарегистрирован в качестве кандидата, нужно чтобы адрес отправителя,
     * совпадал с первой строкой созданного им пакета законов. Данный метод проверяет это.
     * In order for pubkey to be registered as a candidate, the sender's address must be
     *       * coincided with the first line of the package of laws he created.
     *       This method checks for this.
     *       */
    public static boolean checkPostionSenderEqualsLaw(String addressSender, Laws laws) {
        Directors directors = new Directors();
        List<Director> enumPosition = directors.getDirectors();
        List<String> corporateSeniorPositions = enumPosition.stream().map(t->t.getName()).collect(Collectors.toList());
        if (corporateSeniorPositions.contains(laws.getPacketLawName())) {
            if (laws.getLaws().get(0) != null && addressSender.equals(laws.getLaws().get(0))) {
                System.out.println("UtilsBogernment: checkPostionSenderEqualsLaw: " + laws.getLaws().get(0) + " true:");
                return true;
            } else {
                System.out.println("UtilsBogernment: checkPostionSenderEqualsLaw: " + laws.getLaws().get(0) + " false:");
                return false;
            }
        }
        System.out.println("UtilsBogernment: checkPostionSenderEqualsLaw: " + laws.getLaws().get(0) + " not position:");
        return true;
    }


    /**Совет акционеров состоит из 1500 счетов с наибольшим количеством монет акций, но при этом в течение
     * последнего года они совершали хотя бы одну транзакцию.
     * The Board of Shareholders consists of 1,500 accounts with the largest number of share coins, but during
     *       * they have made at least one transaction in the last year.*/
    public static List<Account> findBoardOfShareholders(Map<String, Account> balances, List<Block> blocks, int limit) {
        List<Block> minersHaveMoreStock = null;
        if (blocks.size() > limit) {
            minersHaveMoreStock = blocks.subList(blocks.size() - limit, blocks.size());
        } else {
            minersHaveMoreStock = blocks;
        }
        List<Account> boardAccounts = minersHaveMoreStock.stream().map(
                        t -> new Account(t.getMinerAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO))
                .collect(Collectors.toList());

        for (Block block : minersHaveMoreStock) {

            System.out.println("calculating board of shareholder: index:  " + block.getIndex());
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                boardAccounts.add(new Account(dtoTransaction.getSender(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
            }
        }



        List<Account> boardOfShareholders = balances.entrySet().stream()
                .filter(t -> boardAccounts.contains(t.getValue()))
                .map(t -> t.getValue()).collect(Collectors.toList());

        boardOfShareholders = boardOfShareholders
                .stream()
                .filter(t -> !t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))
                .filter(t -> t.getDigitalDollarBalance().doubleValue() + t.getDigitalStakingBalance().doubleValue() > 0)
                .sorted(Comparator.comparing(Account::getDigitalDollarBalance).reversed())
                .collect(Collectors.toList());

        return boardOfShareholders;
    }
    //выбрать случайным образом limit избирателей.
    public static List<Account> drawingOfLotsByVoters(List<Account> candidates, Block block, int limit) {
        try {
            // Sort candidates by account to ensure reproducibility
            List<Account> sortedCandidates = new ArrayList<>(candidates);
            sortedCandidates.sort(Comparator.comparing(Account::getAccount));

            BigDecimal maxProbability = new BigDecimal("0.15"); // Max probability set to 15%

            List<BigDecimal> balances = sortedCandidates.stream()
                    .map(a -> a.getDigitalStakingBalance().add(a.getDigitalDollarBalance()))
                    .collect(Collectors.toList());

            BigDecimal totalBalance = balances.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal maxBalance = Collections.max(balances);
            BigDecimal minBalance = Collections.min(balances);

            // Calculate Gini coefficient
            BigDecimal giniCoefficient = calculateGiniCoefficient(balances);

            Map<Account, BigDecimal> weights = new LinkedHashMap<>();
            BigDecimal totalWeight = BigDecimal.ZERO;

            for (int i = 0; i < sortedCandidates.size(); i++) {
                Account account = sortedCandidates.get(i);
                BigDecimal balance = balances.get(i);

                // Use logarithmic scaling to handle large numbers and prevent overflow
                BigDecimal logBalance = BigDecimal.valueOf(Math.log1p(balance.doubleValue()));
                BigDecimal logMaxBalance = BigDecimal.valueOf(Math.log1p(maxBalance.doubleValue()));

                // Normalized log balance (between 0 and 1)
                BigDecimal normalizedLogBalance = logBalance.divide(logMaxBalance, 10, RoundingMode.HALF_UP);

                // Apply diminishing returns using a sigmoid-like function with better scaling
                BigDecimal diminishingFactor = BigDecimal.ONE.divide(
                        BigDecimal.ONE.add(BigDecimal.valueOf(Math.exp(normalizedLogBalance.doubleValue() * 5 - 2.5))),
                        10, RoundingMode.HALF_UP
                );

                // Calculate "one account, one person" incentive with adjusted bell curve
                BigDecimal oneAccountIncentive = calculateOneAccountIncentive(balance, minBalance, maxBalance, giniCoefficient);

                // Combine factors
                BigDecimal weight = diminishingFactor.multiply(oneAccountIncentive);

                // Set minimum weight to ensure fairness
                BigDecimal minWeight = new BigDecimal("0.001");
                weight = weight.max(minWeight);

                weights.put(account, weight);
                totalWeight = totalWeight.add(weight);
            }

            // Normalize weights with maximum probability cap
            BigDecimal normalizationFactor = BigDecimal.ONE.divide(totalWeight, 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(limit))
                    .min(maxProbability);
            for (Account account : weights.keySet()) {
                BigDecimal normalizedWeight = weights.get(account).multiply(normalizationFactor);
                weights.put(account, normalizedWeight);
            }

            // Selection process with reproducible randomness
            Set<Account> selectedAccounts = new LinkedHashSet<>();
            for (int seat = 0; seat < limit && selectedAccounts.size() < sortedCandidates.size(); seat++) {
                String combinedHash = block.getHashBlock() + seat;
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(combinedHash.getBytes(StandardCharsets.UTF_8));
                ByteBuffer buffer = ByteBuffer.wrap(digest);
                long seed = buffer.getLong();
                Random deterministicRandom = new Random(seed);

                BigDecimal randomValue = BigDecimal.valueOf(deterministicRandom.nextDouble());
                BigDecimal cumulativeProbability = BigDecimal.ZERO;

                for (Account account : weights.keySet()) {
                    cumulativeProbability = cumulativeProbability.add(weights.get(account));
                    if (cumulativeProbability.compareTo(randomValue) > 0) {
                        if (!selectedAccounts.contains(account)) {
                            selectedAccounts.add(account);
                        }
                        break;
                    }
                }
            }

            return new ArrayList<>(selectedAccounts);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static BigDecimal calculateOneAccountIncentive(BigDecimal balance, BigDecimal minBalance, BigDecimal maxBalance, BigDecimal giniCoefficient) {
        // Normalize balance to a value between 0 and 1
        BigDecimal normalizedBalance = balance.subtract(minBalance).divide(maxBalance.subtract(minBalance), 10, RoundingMode.HALF_UP);

        // Apply a broader bell curve function to favor a wider range of mid-range balances
        BigDecimal bellCurve = BigDecimal.valueOf(Math.exp(-Math.pow(normalizedBalance.doubleValue() - 0.5, 2) / 0.1));

        // Adjust incentive based on Gini coefficient to promote equality
        BigDecimal giniAdjustment = BigDecimal.ONE.subtract(giniCoefficient);

        return bellCurve.multiply(giniAdjustment).add(BigDecimal.valueOf(0.1));
    }

    private static BigDecimal calculateGiniCoefficient(List<BigDecimal> balances) {
        Collections.sort(balances);
        int n = balances.size();
        BigDecimal sumOfDifferences = BigDecimal.ZERO;
        BigDecimal sumOfBalances = balances.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        for (int i = 0; i < n; i++) {
            sumOfDifferences = sumOfDifferences.add(
                    new BigDecimal(2 * i - n + 1).multiply(balances.get(i))
            );
        }

        return sumOfDifferences.divide(new BigDecimal(n * n).multiply(sumOfBalances), 10, RoundingMode.HALF_UP);
    }
    /**Здесь происходит подсчет всех голосов за определенный период, за кандидатов и законы.
     * Here, all votes for a certain period, for candidates and laws are counted.*/
    public static List<CurrentLawVotesEndBalance> filtersVotes(
            List<LawEligibleForParliamentaryApproval> approvalList,
            Map<String, Account> balances,
            List<Account> BoardOfShareholders,
            Map<String, CurrentLawVotes> votesMap
    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        //действующие законы чьи голоса больше ORIGINAL_LIMIT_MIN_VOTE
        List<CurrentLawVotesEndBalance> current = new ArrayList<>();


        //подсчитать средннее количество раз сколько он проголосовал за
        Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);
        //подсчитать среднее количество раз сколько он проголосовал против
        Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);


        int index = 0;
        //подсчитываем голоса для для обычных законов и законов позиций
        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {
            System.out.println("calculate governments: index: " + index );
            index++;

            if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {
                String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();
                String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();
                List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();
                double vote = 0;
                int supremeVotes = 0;
                int boafdOfShareholderVotes = 0;
                int boardOfDirectors = 0;
                int CeoVotes = 0;
                int hightJudgesVotes = 0;
                int founderVote = 0;
                double fraction = 0;
                List<Vote> directorsVote = new ArrayList<>();

                //для законов подсчитываем специальные голоса
                vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);
                List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());
                boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);

                List<String> founder = List.of(Seting.ADDRESS_FOUNDER);
                founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);
                CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(
                        address,
                        packageName,
                        vote,
                        supremeVotes,
                        boardOfDirectors,
                        boafdOfShareholderVotes,
                        CeoVotes,
                        hightJudgesVotes,
                        founderVote,
                        fraction,
                        laws,
                        directorsVote);
                current.add(currentLawVotesEndBalance);

            }
        }


        List<String> corporateCouncilOfReferees = new ArrayList<>();
        List<String> boardOfDirectors = new ArrayList<>();
        Map<String, Double> fractions = new HashMap<>();

        for (CurrentLawVotesEndBalance currentLawVotesEndBalance: current) {
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){
                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
                    boardOfDirectors.add(currentLawVotesEndBalance.getLaws().get(0));
                }

            }
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString())){
                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
                    corporateCouncilOfReferees.add(currentLawVotesEndBalance.getLaws().get(0));
                }

            }


            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){
                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
                    fractions.put(currentLawVotesEndBalance.getLaws().get(0), currentLawVotesEndBalance.getVotes());
                }
            }

        }


        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){


                double vote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).votesLaw(balances, yesAverage, noAverage);
                int supremeVotes  = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, corporateCouncilOfReferees);
                int boardOfDirectorsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteDirector(balances, boardOfDirectors);
                double boardOfDirectorsVotesPR = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteFractions(fractions);
                List<Vote> directorsVote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).directorsVote(fractions);
                currentLawVotesEndBalance.setVotes(vote);
                currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);
                currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);
                currentLawVotesEndBalance.setFractionVote(boardOfDirectorsVotesPR);
                currentLawVotesEndBalance.setDirectorsVote(directorsVote);

            }
            System.out.println("UtilsGovernment: currentLawVotesEndBalance: " + currentLawVotesEndBalance);

        }

        //избирается Генеральный исполнительный директор
        List<String> primeMinister = new ArrayList<>();
        List<String> hightJudge = new ArrayList<>();
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                if(currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                 ||
                        currentLawVotesEndBalance.getVotesBoardOfDirectors() > Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_VOTE

                ){
                    primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));
                }
            }
        }

        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){
                int primeMinisterVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, primeMinister);
                int hightJudgeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, hightJudge);

                currentLawVotesEndBalance.setVoteGeneralExecutiveDirector(primeMinisterVotes);
                currentLawVotesEndBalance.setVoteHightJudge(hightJudgeVotes);
            }

        }

        return current;

    }





    /**TODO устарела и не используется.
     * TODO is obsolete and no longer used. */
    public static List<CurrentLawVotesEndBalance> filters(List<LawEligibleForParliamentaryApproval> approvalList, Map<String, Account> balances,
                                                          List<Account> BoardOfShareholders, List<Block> blocks, int limitBlocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        //действующие законы чьи голоса больше ORIGINAL_LIMIT_MIN_VOTE
        List<CurrentLawVotesEndBalance> current = new ArrayList<>();
        Map<String, CurrentLawVotes> votesMap = null;
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        if (blocks.size() > limitBlocks) {
            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));
        } else {
            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);
        }

        //подсчитать средннее количество раз сколько он проголосовал за
        Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);
        //подсчитать среднее количество раз сколько он проголосовал против
        Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);

        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {
            if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {
                String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();
                String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();
                List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();
                double vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votes(balances, yesAverage, noAverage);

                CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(address, packageName, vote, 0, 0, 0, 0, 0, 0, 0,  laws, new ArrayList<>());
                current.add(currentLawVotesEndBalance);

            }
        }
        return current;
    }

    public static List<CurrentLawVotesEndBalance>filtersVotesOnlyStock(
            List<LawEligibleForParliamentaryApproval> approvalList,
            Map<String, Account> balances,
            List<Block> blocks,
            int limitBlocks
    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        List<CurrentLawVotesEndBalance> current = new ArrayList<>();
        Map<String, CurrentLawVotes> votesMap = null;
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        if (blocks.size() > limitBlocks) {
            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));
        } else {
            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);
        }

        //подсчитать средннее количество раз сколько он проголосовал за
        Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);
        //подсчитать среднее количество раз сколько он проголосовал против
        Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);


        //подсчитываем голоса для для обычных законов и законов позиций
        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {
            if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {
                String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();
                String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();
                List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();
                double vote = 0;
                int supremeVotes = 0;
                int boafdOfShareholderVotes = 0;
                int houseOfRepresentativiesVotes = 0;
                int primeMinisterVotes = 0;
                int hightJudgesVotes = 0;
                int founderVote = 0;
                double fraction = 0;
                List<Vote> directorsVote = new ArrayList<>();

                //для законов подсчитываем специальные голоса
                vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);

                List<String> founder = List.of(Seting.ADDRESS_FOUNDER);
                founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);
                CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(
                        address,
                        packageName,
                        vote,
                        supremeVotes,
                        houseOfRepresentativiesVotes,
                        boafdOfShareholderVotes,
                        primeMinisterVotes,
                        hightJudgesVotes,
                        founderVote,
                        fraction,
                        laws,
                        directorsVote);
                current.add(currentLawVotesEndBalance);

            }
        }


        return current;

    }
}
