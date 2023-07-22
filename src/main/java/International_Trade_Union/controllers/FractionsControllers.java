package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.governments.*;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.SaveBalances;
import International_Trade_Union.vote.CurrentLawVotesEndBalance;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.UtilsLaws;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FractionsControllers {
    @GetMapping("/fractions")
    public String fraction(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();


        Directors directors = new Directors();
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //получить совет акционеров из файла
        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockchain.getBlockchainList(), Seting.BOARDS_BLOCK);


        //TODO доработать оптимизацию
        //TODO избавиться от find position в данном методе
        //отфильтровать по типам голосов
        Map<Director, FIndPositonHelperData> fIndPositonHelperDataMap = new HashMap<>();
        for (Director higherSpecialPositions : directors.getDirectors()) {
            if (higherSpecialPositions.isElectedByCEO()) {
                fIndPositonHelperDataMap.put(higherSpecialPositions,
                        new FIndPositonHelperData(higherSpecialPositions, false, false, true, false, false));
            } else if (higherSpecialPositions.isElectedByFractions()) {
                fIndPositonHelperDataMap.put(higherSpecialPositions,
                        new FIndPositonHelperData(higherSpecialPositions, false, false, false, true, false));
            } else if (higherSpecialPositions.isElectedByCorporateCouncilOfReferees()) {
                fIndPositonHelperDataMap.put(higherSpecialPositions,
                        new FIndPositonHelperData(higherSpecialPositions, false, false, false, false, true));
            } else {
                fIndPositonHelperDataMap.put(higherSpecialPositions,
                        new FIndPositonHelperData(higherSpecialPositions, true, true, false, false, false));

            }

        }
        //подсчитать голоса за все проголосованные заканы
        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                blockchain.getBlockchainList(),
                Seting.LAW_YEAR_VOTE);





//        избранные фракции
        List<CurrentLawVotesEndBalance> electedFraction = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.FRACTION.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.FRACTION.toString()).getCount())
                .collect(Collectors.toList());

        List<FractionPower> fractionPowerArrayList = new ArrayList<>();
        double sum = 0;
        sum = current.stream().filter(t->directors.isElectedByStocks(t.getPackageName()))
                .filter(t-> t.getPackageName().equals(NamePOSITION.FRACTION.toString()))
                .filter(t->t.getVotes()>= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .mapToDouble(t->t.getVotes()).sum();

        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : electedFraction) {
            FractionPower fractionPower = new FractionPower();
            fractionPower.setAddressLaw(currentLawVotesEndBalance.getAddressLaw());
            fractionPower.setVotes(currentLawVotesEndBalance.getVotes());
            fractionPower.setVotesCorporateCouncilOfReferees(currentLawVotesEndBalance.getVotesCorporateCouncilOfReferees());
            fractionPower.setVotesBoardOfDirectors(0);
            fractionPower.setVotesBoardOfShareholders(currentLawVotesEndBalance.getVotesBoardOfShareholders());
            fractionPower.setVoteGeneralExecutiveDirector(currentLawVotesEndBalance.getVoteGeneralExecutiveDirector());
            fractionPower.setVoteHightJudge(currentLawVotesEndBalance.getVoteHightJudge());
            fractionPower.setFounderVote(currentLawVotesEndBalance.getFounderVote());
            fractionPower.setPackageName(currentLawVotesEndBalance.getPackageName());
            fractionPower.setLaws(currentLawVotesEndBalance.getLaws());
            fractionPower.setFractionVote(currentLawVotesEndBalance.getFractionVote());
            fractionPower.setFractionPower((currentLawVotesEndBalance.getVotes()/sum) * Seting.HUNDRED_PERCENT);
            fractionPowerArrayList.add(fractionPower);
        }




        model.addAttribute("title", "How the current laws are made is described in the charter." +
                " ");
        model.addAttribute("currentLaw", fractionPowerArrayList);
        return "fractions";
    }
}
