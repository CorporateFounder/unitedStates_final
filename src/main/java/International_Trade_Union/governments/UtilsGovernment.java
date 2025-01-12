package International_Trade_Union.governments;


import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
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

    /**
     * чтобы pubkey был зарегистрирован в качестве кандидата, нужно чтобы адрес отправителя,
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


    /**
     * Отобрать счета которые были активны за последние n дней
     */
    /**
     * Отбирает счета, которые были активны за последние n блоков, обрабатывая блоки партиями по 500 штук.
     *
     * @param balances     карта балансов аккаунтов
     * @param blockService сервис для доступа к блокам
     * @param limit        максимальное количество блоков для обработки (например, 800000)
     * @return список акционеров
     * @throws IOException если произошла ошибка при обработке блоков
     */
    public static List<Account> findBoardOfShareholders(Map<String, Account> balances, BlockService blockService, int limit) throws IOException {
        // Инициализация набора для хранения уникальных адресов
        Set<String> boardAccountAddresses = new HashSet<>();

        // Определение диапазона блоков для обработки
        long blockchainSize = BasisController.getBlockchainSize();
        long from = 0;
        long to = blockchainSize - 1; // Предполагаем, что индексация блоков начинается с 0

        if (blockchainSize > Seting.LAW_HALF_VOTE) {
            from = blockchainSize - Seting.LAW_HALF_VOTE;
        }

        final int BATCH_SIZE = 500;
        long currentFrom = from;
        long currentTo;

        long processedBlocks = 0;

        while (currentFrom <= to && processedBlocks < limit) {
            try {
                // Вычисляем конец текущей партии
                currentTo = Math.min(currentFrom + BATCH_SIZE - 1, to);

                // Извлекаем текущую партию блоков из базы данных
                List<Block> batchBlocks = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                        blockService.findBySpecialIndexBetween(currentFrom, currentTo)
                );

                for (Block block : batchBlocks) {
                    // Добавляем адрес майнера блока
                    boardAccountAddresses.add(block.getMinerAddress());

                    // Добавляем адреса отправителей транзакций блока
                    for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                        boardAccountAddresses.add(dtoTransaction.getSender());
                    }

                    processedBlocks++;

                    // Проверка достижения лимита
                    if (processedBlocks >= limit) {
                        break;
                    }
                }

                // Обновляем индексы для следующей партии
                currentFrom += BATCH_SIZE;

                // Логирование прогресса (опционально)
                System.out.println("Извлечено блоков до индекса: " + currentTo + ". Обработано блоков: " + processedBlocks);
            } catch (Exception e) {
                // Логирование ошибки и прекращение обработки
                System.err.println("Ошибка при извлечении блоков с " + currentFrom + " по " + (currentFrom + BATCH_SIZE - 1));
                e.printStackTrace();
                throw new IOException("Ошибка при обработке блоков.", e);
            }
        }

        // Фильтрация балансов на основе собранных адресов
        List<Account> boardOfShareholders = balances.values().stream()
                .filter(account -> boardAccountAddresses.contains(account.getAccount()))
                .filter(account -> !account.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))
                .filter(account -> account.getDigitalStockBalance().doubleValue() > 0)
                .sorted(Comparator.comparing(Account::getDigitalStockBalance).reversed())
                .limit(Seting.BOARD_OF_SHAREHOLDERS)
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

    /**
     * Здесь происходит подсчет всех голосов за определенный период, за кандидатов и законы.
     * Here, all votes for a certain period, for candidates and laws are counted.
     */
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
            System.out.println("calculate governments: index: " + index);
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
                boolean isValid = false;
                List<Vote> directorsVote = new ArrayList<>();
                Map<String, Double> fractionsRaiting = new HashMap<>();
                double sum = 0;
                double percentDirectDemocracy  = 0;
                int votesCorporateCouncilOfRefereesYes = 0;

                //для законов подсчитываем специальные голоса
                vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);
                List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());
                boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);

                String whoCreate = "";
                Long indexCreateLaw = 0L;
                if(votesMap.containsKey(address)){
                    indexCreateLaw = votesMap.get(address).getIndexCreateLaw();
                    whoCreate = votesMap.get(address).getWhoCreate();
                }
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
                        directorsVote,
                        fractionsRaiting,
                        isValid,
                        sum,
                        percentDirectDemocracy,
                        indexCreateLaw,
                        whoCreate,
                        votesCorporateCouncilOfRefereesYes);
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
                int supremeVotes  = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernmentNo(balances, corporateCouncilOfReferees);
                int votesCorporateCouncilOfRefereesYes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernmentYes(balances, corporateCouncilOfReferees);
                int boardOfDirectorsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteDirector(balances, boardOfDirectors);
                double boardOfDirectorsVotesPR = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteFractions(fractions);
                List<Vote> directorsVote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).directorsVote(fractions);
                currentLawVotesEndBalance.setVotes(vote);
                currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);
                currentLawVotesEndBalance.setVotesCorporateCouncilOfRefereesNo(supremeVotes);
                currentLawVotesEndBalance.setFractionVote(boardOfDirectorsVotesPR);
                currentLawVotesEndBalance.setDirectorsVote(directorsVote);
                currentLawVotesEndBalance.setFractionsRaiting(fractions);
                currentLawVotesEndBalance.setVotesCorporateCouncilOfRefereesYes(votesCorporateCouncilOfRefereesYes);
                double sum = fractions.entrySet().stream()
                        .map(t->t.getValue())
                        .collect(Collectors.toList())
                        .stream().reduce(0.0, Double::sum);
                currentLawVotesEndBalance.setSum(sum);
                currentLawVotesEndBalance.setPercentDirectDemocracy(currentLawVotesEndBalance.getVotes() / sum * Seting.HUNDRED_PERCENT);


            }
        }

        //избирается Генеральный исполнительный директор
        List<String> primeMinister = new ArrayList<>();
        List<String> hightJudge = new ArrayList<>();
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                if(currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                 ||
                        currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_VOTE

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

}
