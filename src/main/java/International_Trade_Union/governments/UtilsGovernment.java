package International_Trade_Union.governments;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.vote.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
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
                        t -> new Account(t.getMinerAddress(), 0, 0))
                .collect(Collectors.toList());

        for (Block block : minersHaveMoreStock) {
            System.out.println("calculating board of shareholder: index:  " + block.getIndex());
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                boardAccounts.add(new Account(dtoTransaction.getSender(), 0, 0));
            }

        }


        List<Account> boardOfShareholders = balances.entrySet().stream()
                .filter(t -> boardAccounts.contains(t.getValue()))
                .map(t -> t.getValue()).collect(Collectors.toList());


        boardOfShareholders = boardOfShareholders
                .stream()
                .filter(t -> !t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))
                .filter(t -> t.getDigitalStockBalance() > 0)
                .sorted(Comparator.comparing(Account::getDigitalStockBalance).reversed())
                .collect(Collectors.toList());

        boardOfShareholders = boardOfShareholders
                .stream()
                .limit(Seting.BOARD_OF_SHAREHOLDERS)
                .collect(Collectors.toList());

        return boardOfShareholders;
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
                        laws);
                current.add(currentLawVotesEndBalance);

            }
        }


        List<String> corporateCouncilOfReferees = new ArrayList<>();
        List<String> boardOfDirectors = new ArrayList<>();

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




        }


        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){


                double vote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).votesLaw(balances, yesAverage, noAverage);
                int supremeVotes  = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, corporateCouncilOfReferees);
                int boardOfDirectorsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, boardOfDirectors);

                currentLawVotesEndBalance.setVotes(vote);
                currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);
                currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);
                currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);
            }

        }

        //изирается Генеральный исполнительный директор
        List<String> primeMinister = new ArrayList<>();
        List<String> hightJudge = new ArrayList<>();
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE
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

                CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(address, packageName, vote, 0, 0, 0, 0, 0, 0, 0,  laws);
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
                        laws);
                current.add(currentLawVotesEndBalance);

            }
        }


        return current;

    }
}
