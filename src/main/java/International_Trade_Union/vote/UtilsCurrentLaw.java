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
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilsCurrentLaw {
    //подсчет по штучно баланса
    public static Map<String, CurrentLawVotes> calculateVote(Map<String, CurrentLawVotes> votes, List<Account> governments, Block block) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();
        List<String> signs = new ArrayList<>();
        for (int j = 0; j < block.getDtoTransactions().size(); j++) {
            DtoTransaction transaction = block.getDtoTransactions().get(j);
            if(signs.contains(transaction.getSign())){
                System.out.println("this transaction signature has already been used and is not valid");
                continue;
            }
            else {
                signs.add(transaction.toSign());
//                System.out.println("we added new sign transaction");
            }
            if (transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                System.out.println("law balance cannot be sender");
                continue;
            }
            if (transaction.verify() && transaction.getCustomer().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                for (Account account : governments) {
                    //основатель не может участвовать в голосовании
                    //!block.getFounderAddress().equals(transaction.getSender())
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
                        }
                        else if(transaction.getVoteEnum().equals(VoteEnum.REMOVE_YOUR_VOICE)){
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



    //возвращает списки позиций
    public static Map<Director, List<String>> findPositions(

            Map<String, Account> balances,
            Blockchain blockchain,
            List<LawEligibleForParliamentaryApproval> allGovernment,
            List<Account> BoardOfShareholders,
            Map<Director, FIndPositonHelperData> fIndPositonHelperData

    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        //список должностей
        Directors directors = new Directors();
        Map<Director, List<LawEligibleForParliamentaryApproval>> positionsListMap = new HashMap<>();
        //добавление всех должностей
        for (Director higherSpecialPositions : directors.getDirectors()) {
            positionsListMap.put(higherSpecialPositions, UtilsLaws.getPossions(allGovernment, higherSpecialPositions));
        }
        //список законов с голосами
        Map<Director, List<CurrentLawVotesEndBalance>> curentLawVotesEndBalance = new HashMap<>();
        for (Map.Entry<Director, List<LawEligibleForParliamentaryApproval>> corp : positionsListMap.entrySet()) {
            //убрать повторяющиеся должности из списка.
            corp.setValue(corp.getValue().stream()
//                    .filter(t-> finalBoardOfShareholders.contains(new Account(t.getLaws().getLaws().get(0), 0, 0)))
                    .distinct().collect(Collectors.toList()));

            //получить баланс и голоса для действующих законов
            curentLawVotesEndBalance.put(corp.getKey(), UtilsGovernment.filters(corp.getValue(), balances, BoardOfShareholders,
                    blockchain.getBlockchainList(), Seting.POSITION_YEAR_VOTE));
            List<CurrentLawVotesEndBalance> temporary;
            if (fIndPositonHelperData.get(corp.getKey()).isElectedWithStock()) {


                //отобрать голоса выше лимита
                curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey()));
                List<CurrentLawVotesEndBalance> electedByStock =
                        curentLawVotesEndBalance.get(corp.getKey())
                                .stream()
                                .filter(t->directors.isElectedByStocks(t.getPackageName()))
                                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                                .limit(corp.getKey().getCount())
                                .collect(Collectors.toList());

                System.out.println("UtilsCurrentLaw: findPostion: ");
                System.out.println("*******************************");
                electedByStock.stream().forEach(System.out::println);
                System.out.println("*******************************");

                //отобрать то количество которое соответсвтвует данной должности
                temporary = electedByStock;

            } else {

                curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey()));
                //отобрать то количество которое соответсвтвует данной должности
                //избираемые премьер министром
                if(fIndPositonHelperData.get(corp.getKey()).isElectedWithPrimeMinister()){
                    //отобрать голоса выше лимита
                    curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey()));
                    List<CurrentLawVotesEndBalance> electedByPrimeMinister =
                            curentLawVotesEndBalance.get(corp.getKey())
                                    .stream()
                                    .filter(t->directors.isElectedCEO(t.getPackageName()))
                                    .filter(t -> t.getVoteGeneralExecutiveDirector() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_GENERAL_EXECUTIVE_DIRECTOR)
                                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVoteGeneralExecutiveDirector).reversed())
                                    .collect(Collectors.toList());


                    //отобрать то количество которое соответсвтвует данной должности
                    temporary = electedByPrimeMinister;
                }
                //избираемые палатой представителей
                else if(fIndPositonHelperData.get(corp.getKey()).isElectedWithHousOfRepresentativies()){
                    //отобрать голоса выше лимита
                    curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey()));
                    List<CurrentLawVotesEndBalance> electedByHouseOfRepresentatives =
                            curentLawVotesEndBalance.get(corp.getKey())
                                    .stream()
                                    .filter(t->directors.isElectedByBoardOfDirectors(t.getPackageName()))
                                    .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)
                                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())
                                    .collect(Collectors.toList());


                    //отобрать то количество которое соответсвтвует данной должности
                    temporary = electedByHouseOfRepresentatives;



                }
                //избираемые палатаой верховных судей
                else if(fIndPositonHelperData.get(corp.getKey()).isElectedWithChamberOfHightJudjes()){
                    //отобрать голоса выше лимита
                    curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey()));
                    List<CurrentLawVotesEndBalance> electedByChamberOfSupremeJudges =
                            curentLawVotesEndBalance.get(corp.getKey())
                                    .stream()
                                    .filter(t->directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                                    .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees).reversed())
                                    .collect(Collectors.toList());


                    //отобрать то количество которое соответсвтвует данной должности
                    temporary = electedByChamberOfSupremeJudges;

                }
                else {
                    temporary = curentLawVotesEndBalance.get(corp.getKey()).stream()

                            .collect(Collectors.toList());
                }

            }

            temporary = temporary.stream().distinct().collect(Collectors.toList());

            curentLawVotesEndBalance.put(corp.getKey(), temporary);
        }


        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);


        Map<Director, List<String>> currentPossitions = new HashMap<>();
        for (Map.Entry<Director, FIndPositonHelperData> fIndPositonHelperData1 : fIndPositonHelperData.entrySet()) {
            List<CurrentLawVotesEndBalance> position = curentLawVotesEndBalance.get(fIndPositonHelperData1.getKey());
            //список адресов на данную позицию, пример члена палаты представителей
            List<String> currntAddress = new ArrayList<>();
            for (CurrentLawVotesEndBalance address : position) {
                for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : lawEligibleForParliamentaryApprovals) {


                    if (lawEligibleForParliamentaryApproval.getLaws().getHashLaw().equals(address.getAddressLaw())) {

                        currntAddress.add(lawEligibleForParliamentaryApproval.getLaws().getLaws().get(0));

                    }
                }

            }
            currentPossitions.put(fIndPositonHelperData1.getKey(), currntAddress);

        }


        return currentPossitions;
    }

    //найти членов палаты представителей
    //возвращает список позиций
    public static List<String> findPosition(
            Map<String, Account> balances,
            Blockchain blockchain,
            List<LawEligibleForParliamentaryApproval> allGovernment,
            List<Account> BoardOfShareholders,
            Directors positions,
            boolean withLimit

    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        //список должностей

        Map<Director, List<LawEligibleForParliamentaryApproval>> positionsListMap = new HashMap<>();
        //добавление всех должностей
        for (Director higherSpecialPositions : positions.getDirectors()) {
            positionsListMap.put(higherSpecialPositions, UtilsLaws.getPossions(allGovernment, higherSpecialPositions));
        }
        //список законов с голосами
        Map<Director, List<CurrentLawVotesEndBalance>> curentLawVotesEndBalance = new HashMap<>();
        for (Map.Entry<Director, List<LawEligibleForParliamentaryApproval>> corp : positionsListMap.entrySet()) {
            //убрать повторяющиеся должности из списка.
            corp.setValue(corp.getValue().stream()
//                    .filter(t-> finalBoardOfShareholders.contains(new Account(t.getLaws().getLaws().get(0), 0, 0)))
                    .distinct().collect(Collectors.toList()));

            //получить баланс и голоса для действующих законов
            curentLawVotesEndBalance.put(corp.getKey(), UtilsGovernment.filters(corp.getValue(), balances, BoardOfShareholders,
                    blockchain.getBlockchainList(), Seting.POSITION_YEAR_VOTE));
            List<CurrentLawVotesEndBalance> temporary;
            if (withLimit) {
                //отобрать голоса выше лимита
                curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey())
                        .stream().filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                        .collect(Collectors.toList()));

                //отобрать то количество которое соответсвтвует данной должности
                temporary = curentLawVotesEndBalance.get(corp.getKey()).stream()
                        .filter(t -> !t.getPackageName().equals(corp.getKey()))
                        .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                        .limit(corp.getKey().getCount()).collect(Collectors.toList());
            } else {
                //отобрать то количество которое соответсвтвует данной должности
                temporary = curentLawVotesEndBalance.get(corp.getKey()).stream()
                        .filter(t -> !t.getPackageName().equals(corp.getKey()))
                        .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                        .collect(Collectors.toList());
            }


            curentLawVotesEndBalance.put(corp.getKey(), temporary);
        }


        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //список адресов на данную позицию, пример члена палаты представителей
        List<String> currntAddress = new ArrayList<>();

        List<CurrentLawVotesEndBalance> position = curentLawVotesEndBalance.get(positions);

        for (CurrentLawVotesEndBalance address : position) {
            for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : lawEligibleForParliamentaryApprovals) {


                if (lawEligibleForParliamentaryApproval.getLaws().getHashLaw().equals(address.getAddressLaw())) {

                    currntAddress.add(lawEligibleForParliamentaryApproval.getLaws().getLaws().get(0));

                }
            }
        }


        return currntAddress;
    }
}
