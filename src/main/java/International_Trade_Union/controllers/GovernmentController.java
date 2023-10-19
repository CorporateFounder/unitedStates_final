package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.vote.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GovernmentController {

    //TODO если происходит майнинг почему то происходят ошибки, и если вызвать данный метод, то может
    //TODO прерываться сам процесс майнинга
    //TODO if mining occurs for some reason, errors are observed, and this method appears, then it can
    //TODO interrupt the mining process itself

    /**Отображает в браузере список действующих должностей*/
    @GetMapping("/governments")
    public String corporateSeniorpositions(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }


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
            } else if (higherSpecialPositions.isElectedByBoardOfDirectors()) {
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




//        минимальное значение количество положительных голосов для того чтобы закон действовал,
//        позиции избираемые акциями совета директоров
        List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())
                .collect(Collectors.toList());


//        List<CurrentLawVotesEndBalance> electedFraction = current.stream()
//                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
//                .filter(t -> t.getPackageName().equals(NamePOSITION.FRACTION.toString()))
//                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
//                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
//                .limit(directors.getDirector(NamePOSITION.FRACTION.toString()).getCount())
//                .collect(Collectors.toList());


        //минимальное значение количество положительных голосов для того чтобы закон действовал,
        //позиции избираемые акциями CORPORATE_COUNCIL_OF_REFEREES
        List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())
                .collect(Collectors.toList());



        //позиции созданные всеми участниками
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
        //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //позиции избираемые только всеми участниками
        List<CurrentLawVotesEndBalance> electedByBoardOfShareholders = current.stream()
                .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0
                || t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


        //групируем по списку
        Map<String, List<CurrentLawVotesEndBalance>> group = electedByBoardOfShareholders.stream()
                .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));

        Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();

        //оставляем то количество которое описано в данной должности
        for (Map.Entry<String, List<CurrentLawVotesEndBalance>> stringListEntry : group.entrySet()) {
            List<CurrentLawVotesEndBalance> temporary = stringListEntry.getValue();
            temporary = temporary.stream()
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes))
                    .limit(directors.getDirector(stringListEntry.getKey()).getCount())
                    .collect(Collectors.toList());
            original_group.put(directors.getDirector(stringListEntry.getKey()), temporary);
        }



        //позиции избираемые советом корпоративных верховных судей
        List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList());


        //избираемые GENERAL_EXECUTIVE_DIRECTOR
        List<CurrentLawVotesEndBalance> electedByGeneralExecutiveDirector = electedByBoardOfShareholders.stream()
                .filter(t -> directors.isElectedCEO(t.getPackageName()))
                .filter(t -> NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString().equals(t.getPackageName()))
                .filter(t -> t.getVoteGeneralExecutiveDirector() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_GENERAL_EXECUTIVE_DIRECTOR)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes))
                .collect(Collectors.toList());

        //голос верховного судьи
        List<CurrentLawVotesEndBalance> electedByHightJudge = electedByCorporateCouncilOfReferees.stream()
                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                .filter(t -> t.getVoteHightJudge() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_HIGHT_JUDGE)
                .collect(Collectors.toList());



        //добавляет законы, которые создают новые должности утверждается всеми
        List<CurrentLawVotesEndBalance> addDirectors = current.stream()
                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());





        for (Map.Entry<Director, List<CurrentLawVotesEndBalance>> higherSpecialPositionsListMap : original_group.entrySet()) {
            current.addAll(higherSpecialPositionsListMap.getValue());
        }


        current = new ArrayList<>();
        current.addAll(addDirectors);
        current.addAll(electedByStockBoardOfDirectors);
        current.addAll(electedByStockCorporateCouncilOfReferees);
        current.addAll(electedByBoardOfShareholders);
        current.addAll(electedByCorporateCouncilOfReferees);
        current.addAll(electedByGeneralExecutiveDirector);
        current.addAll(electedByHightJudge);
        current = current.stream()
                .filter(UtilsUse.distinctByKey(CurrentLawVotesEndBalance::getAddressLaw))
                .filter(t->directors.contains(t.getPackageName()))
                .collect(Collectors.toList());


        model.addAttribute("title", "How the current laws are made is described in the charter." +
                " ");
        model.addAttribute("currentLaw", current);

        return "governments";
    }

    @GetMapping("/create-position")
    public String createPositionShow(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

        Directors directors = new Directors();

        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);


        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockchain.getBlockchainList(), Seting.BOARDS_BLOCK);

        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                blockchain.getBlockchainList(),
                Seting.LAW_YEAR_VOTE);


        List<String> positions = directors.getDirectors().stream().map(t->t.getName()).collect(Collectors.toList());
        //позиции утвержденные всеми
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t->
                         t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS
                        && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
        //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }


        positions.addAll(directors.getNames());
        positions = positions.stream().distinct().collect(Collectors.toList());
        model.addAttribute("positions", positions);
        return "create-position";
    }

    /**Отображается в браузере, позволяет создавать новые должности*/
    @RequestMapping(value = "/create-position", method = RequestMethod.POST, params = "action=/send")
    public String createLaw(Model model,
                            @RequestParam String sender,
                            @RequestParam String reward,
                            @RequestParam String nameLaw,
                            @RequestParam String[] laws,
                            @RequestParam String password,
                            RedirectAttributes redirectAttrs) throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {


        Laws law = new Laws(nameLaw, Arrays.asList(laws));
        Base base = new Base58();

        Double rewardD = Double.parseDouble(reward);


        DtoTransaction dtoTransaction = new DtoTransaction(
                sender,
                law.getHashLaw(),
                0.0,
                0.0,
                law,
                rewardD,
                VoteEnum.valueOf("YES"));
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());

        redirectAttrs.addFlashAttribute("title", "sending result!!!");
        redirectAttrs.addFlashAttribute("sender", sender);

        redirectAttrs.addFlashAttribute("recipient", law.getHashLaw());
        redirectAttrs.addFlashAttribute("dollar", 0.0);
        redirectAttrs.addFlashAttribute("stock", 0.0);
        redirectAttrs.addFlashAttribute("reward", rewardD);
        redirectAttrs.addFlashAttribute("vote", "YES");
        dtoTransaction.setSign(sign);

        if (dtoTransaction.verify() && UtilsGovernment.checkPostionSenderEqualsLaw(sender, law)) {
            redirectAttrs.addFlashAttribute("sending", "success");
            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);

            for (String s : Seting.ORIGINAL_ADDRESSES) {
                String original = s;
                String url = s + "/addTransaction";
                if (BasisController.getExcludedAddresses().contains(url)) {
                    System.out.println("its your address or excluded address: " + url);
                    continue;
                }
                try {
                    UtilUrl.sendPost(jsonDto, url);

                } catch (Exception e) {
                    System.out.println("exception discovery: " + original);

                }
            }
        } else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");

        return "redirect:/result-sending";

    }
}
