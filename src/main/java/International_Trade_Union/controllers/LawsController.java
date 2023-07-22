package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.model.Mining;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.*;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class LawsController {


    @GetMapping("detail-laws")
    public String details(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();

        return "detail-laws";
    }

    //TODO реализовать голосвание


    /**Отображается в браузере, позволяет увидеть содержимое пакета законов, список действующих законов*/
    @GetMapping("/detail-laws-current/{addressLaw}")
    public String lawsDetail(@PathVariable(value = "addressLaw") String addressLaw, RedirectAttributes redirectAttrs) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        System.out.println("LawsController detail-laws-current/{addressLaw}: " + addressLaw);
        redirectAttrs.addAttribute("title", "detail laws");
        //Seting.ORIGINAL_CURRENT_FEDERAL_LAWS_FILE
        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        List<String> currntLaws = new ArrayList<>();
        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : lawEligibleForParliamentaryApprovals) {
            int i = 0;
            if (lawEligibleForParliamentaryApproval.getLaws().getHashLaw().equals(addressLaw)) {

                for (String str : lawEligibleForParliamentaryApproval.getLaws().getLaws()) {

                    currntLaws.add("" + i + ": " + str);
                    ++i;
                }

            }
        }

        currntLaws.forEach(System.out::println);
        redirectAttrs.addFlashAttribute("laws", currntLaws);
        return "redirect:/detail-laws";
    }

    /**Отображается в браузере, показывает содержимое пакета законов, из  списка всех законов*/
    @GetMapping("/detail-laws-all/{addressLaw}")
    public String lawsDetailAll(@PathVariable(value = "addressLaw") String addressLaw, RedirectAttributes redirectAttrs) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        System.out.println("LawsController /detail-laws-all/{addressLaw}: " + addressLaw);
        redirectAttrs.addAttribute("title", "detail laws");
        //ORIGINAL_ALL_CORPORATION_LAWS_FILE
        List<Laws> laws = UtilsLaws.readLineLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);
        List<String> allLaws = new ArrayList<>();
        for (Laws laws1 : laws) {
            int i = 0;
            if (laws1.getHashLaw().equals(addressLaw)) {

                for (String s : laws1.getLaws()) {
                    allLaws.add("" + i + ": " + s);
                    ++i;
                }

            }
        }

        redirectAttrs.addFlashAttribute("laws", allLaws);
        return "redirect:/detail-laws";
    }


    @GetMapping("/sanction")
    public String sanction(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
        return "sanction";
    }

    @PostMapping("/sanction")
    public String sanction(
            @RequestParam
            String sender,
            String recipient,
            Double stock,
            Double reward,
            String password,
            RedirectAttributes redirectAttrs

    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();

        Laws laws =  new Laws();
        laws.setLaws(new ArrayList<>());
        laws.setHashLaw("");
        laws.setPacketLawName("");
        DtoTransaction dtoTransaction = new DtoTransaction(
                sender,
                recipient,
                0.0,
                stock,
                laws,
                reward,
                VoteEnum.NO);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
        System.out.println("Main Controller: new transaction: vote: " + VoteEnum.NO);
        redirectAttrs.addFlashAttribute("title", "sending result!!!");
        redirectAttrs.addFlashAttribute("sender", sender);
        redirectAttrs.addFlashAttribute("recipient", recipient);
        redirectAttrs.addFlashAttribute("dollar", 0.0);
        redirectAttrs.addFlashAttribute("stock", stock);
        redirectAttrs.addFlashAttribute("reward", reward);
        redirectAttrs.addFlashAttribute("vote", VoteEnum.NO);
        dtoTransaction.setSign(sign);
        Directors directors = new Directors();
        if(dtoTransaction.verify()){

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t->t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if(corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)){
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
            System.out.println("dto MainController: " + dtoTransaction);

            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s +"/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if(BasisController.getExcludedAddresses().contains(url)){
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                }catch (Exception e){
                    System.out.println("exception discover: " + original);

                }
            }



        }

        else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";
    }
    /**Голосование учитывает голоса как акций, так и голоса избраных представителей*/
    @GetMapping("/voting")
    public String lawVoting() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
        return "voting";
    }
    @PostMapping("/voting")
    public String lawVoting(
            @RequestParam
            String sender,
            String recipient,
            Double reward,
            String vote,
            String password,
            RedirectAttributes redirectAttrs

    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();
        vote = vote.toUpperCase(Locale.ROOT);
        Laws laws =  new Laws();
        laws.setLaws(new ArrayList<>());
        laws.setHashLaw("");
        laws.setPacketLawName("");
        System.out.println("LawController: Voting: " + VoteEnum.valueOf(vote));
        DtoTransaction dtoTransaction = new DtoTransaction(
                sender,
                recipient,
                0.0,
                0.0,
                laws,
                reward,
                VoteEnum.valueOf(vote));
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
        System.out.println("Main Controller: new transaction: vote: " + vote);
        redirectAttrs.addFlashAttribute("title", "sending result!!!");
        redirectAttrs.addFlashAttribute("sender", sender);
        redirectAttrs.addFlashAttribute("recipient", recipient);
        redirectAttrs.addFlashAttribute("dollar", 0.0);
        redirectAttrs.addFlashAttribute("stock", 0.0);
        redirectAttrs.addFlashAttribute("reward", reward);
        redirectAttrs.addFlashAttribute("vote", vote);
        dtoTransaction.setSign(sign);
        Directors directors = new Directors();
        if(dtoTransaction.verify()){

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t->t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if(corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)){
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
            System.out.println("dto MainController: " + dtoTransaction);

            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s +"/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if(BasisController.getExcludedAddresses().contains(url)){
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                }catch (Exception e){
                    System.out.println("exception discover: " + original);

                }
            }



        }

        else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";
    }
    /**Отображается в браузере, список все действующих законов*/
    @GetMapping("/current-laws")
    public String currentLaw(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Directors directors = new Directors();
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
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


        //убрать появление всех бюджет и эмиссий из отображения в действующих законах
        current = current.stream()
                .filter(t->!t.getPackageName().equals(Seting.EMISSION) ||
                        t.getPackageName().equals(Seting.BUDGET))
                .collect(Collectors.toList());

//        избранные фракции
        List<CurrentLawVotesEndBalance> electedFraction = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.FRACTION.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.FRACTION.toString()).getCount())
                .collect(Collectors.toList());
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
        List<CurrentLawVotesEndBalance> createdByFraction = current.stream()
                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
        //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByFraction) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //позиции избираемые только всеми участниками
        List<CurrentLawVotesEndBalance> electedByFractions = current.stream()
                .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


        //групируем по списку
        Map<String, List<CurrentLawVotesEndBalance>> group = electedFraction.stream()
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
        List<CurrentLawVotesEndBalance> electedByGeneralExecutiveDirector = electedByFractions.stream()
                .filter(t -> directors.isElectedCEO(t.getPackageName()))
                .filter(t -> NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString().equals(t.getPackageName()))
                .filter(t -> t.getVoteGeneralExecutiveDirector() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_GENERAL_EXECUTIVE_DIRECTOR)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVoteGeneralExecutiveDirector))
                .collect(Collectors.toList());

        //голос верховного судьи
        List<CurrentLawVotesEndBalance> electedByHightJudge = electedByCorporateCouncilOfReferees.stream()
                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                .filter(t -> t.getVoteHightJudge() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_HIGHT_JUDGE)
                .collect(Collectors.toList());


        //законы должны быть одобрены всеми.
        List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t->!Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t->!Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());



        //внедрение поправок в устав
        List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t-> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT
                && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());

        //добавляет законы, которые создают новые должности утверждается всеми
        List<CurrentLawVotesEndBalance> addDirectors = current.stream()
                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());

        //план утверждается всеми
        List<CurrentLawVotesEndBalance> planFourYears = current.stream()
                .filter(t->!directors.contains(t.getPackageName()))
                .filter(t->Seting.STRATEGIC_PLAN.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());


        //устав всегда действующий он подписан основателем
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t->t.getFounderVote()>=1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());

        //ИСХОДНЫЙ КОД СОЗДАННЫЙ ОСНОВАТЕЛЕМ
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL_CODE = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t->t.getFounderVote()>=1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());


        int startBlock = 23660;
        int finishBlock = 23670;
        if(blockchain.sizeBlockhain() > finishBlock){
            List<Block> blocksCharter = blockchain.subBlock(startBlock, finishBlock);
            //учитывает отрезок блоков для выяснения подлиности устава
            List<CurrentLawVotesEndBalance> charterBlocks = UtilsGovernment.filtersVotes(
                    lawEligibleForParliamentaryApprovals,
                    balances,
                    boardOfShareholders,
                    blocksCharter,
                    Seting.LAW_YEAR_VOTE
            );
            List<CurrentLawVotesEndBalance> charterCheckBlock = charterBlocks.stream()
                    .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                    .filter(t->!directors.isCabinets(t.getPackageName()))
                    .filter(t->t.getFounderVote()>=1)
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                    .limit(1)
                    .collect(Collectors.toList());

            CHARTER_ORIGINAL.addAll(charterCheckBlock);


            List<CurrentLawVotesEndBalance> charterOriginalCode = charterBlocks.stream()
                    .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                    .filter(t->!directors.isCabinets(t.getPackageName()))
                    .filter(t->t.getFounderVote()>=1)
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                    .limit(1)
                    .collect(Collectors.toList());

            CHARTER_ORIGINAL_CODE.addAll(charterOriginalCode);
        }


        for (Map.Entry<Director, List<CurrentLawVotesEndBalance>> higherSpecialPositionsListMap : original_group.entrySet()) {
            current.addAll(higherSpecialPositionsListMap.getValue());
        }


        current = new ArrayList<>();
        current.addAll(addDirectors);

        current.addAll(electedFraction);
        current.addAll(planFourYears);

        current.addAll(electedByStockCorporateCouncilOfReferees);
        current.addAll(electedByFractions);
        current.addAll(electedByCorporateCouncilOfReferees);
        current.addAll(electedByGeneralExecutiveDirector);
        current.addAll(electedByHightJudge);
        current.addAll(notEnoughVotes);
        current.addAll(CHARTER_ORIGINAL);
        current.addAll(CHARTER_ORIGINAL_CODE);
        current.addAll(chapter_amendment);
        current = current.stream()
                .filter(UtilsUse.distinctByKey(CurrentLawVotesEndBalance::getAddressLaw))
                .collect(Collectors.toList());
        System.out.println("notEnoughVotes: " + notEnoughVotes);

        model.addAttribute("title", "How the current laws are made is described in the charter." +
                " ");
        model.addAttribute("currentLaw", current);
        return "current-laws";
    }

    /**Отображается в браузере список всех принятых бюджетов и эмиссий*/
    @GetMapping("/budget_end_emission")
    public String allBudgetEndEmission(Model model) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
        int index = BasisController.getBlockchainSize();

        int day = index % Seting.LAW_MONTH_VOTE;
        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account Budget = balances.get(Seting.BUDGET);
        if(Budget == null)
            Budget = new Account(Seting.BUDGET, 0, 0);
        model.addAttribute("dollar", Budget.getDigitalDollarBalance());
        model.addAttribute("stock", Budget.getDigitalStockBalance());
        model.addAttribute("emission", Seting.EMISSION_BUDGET);
        model.addAttribute("day", day);
        List<CurrentLawVotesEndBalance> current =
                UtilsCurrentLawVotesEndBalance.readLine(Seting.CURRENT_BUDGET_END_EMISSION);
        model.addAttribute("title", "adopted budgets and emissions");
        model.addAttribute("currentLaw", current);
        return "budget_end_emission.html";
    }

    @GetMapping("/budget_end_emission_15_day")
    public String budgetEndEmissision15day(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
        Directors directors = new Directors();
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

         Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        //считывать баланс


        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //получить совет акционеров из файла
        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockchain.getBlockchainList(), Seting.BOARDS_BLOCK);

        //подсчитать голоса за все проголосованные заканы
        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                blockchain.getBlockchainList(),
                Seting.LAW_MONTH_VOTE);


        List<CurrentLawVotesEndBalance> budget = current.stream()
                .filter(t->t.getPackageName().equals(Seting.BUDGET))
                .collect(Collectors.toList());

        List<CurrentLawVotesEndBalance> emission = current.stream()
                .filter(t->t.getPackageName().equals(Seting.EMISSION))
                .collect(Collectors.toList());

        budget.addAll(emission);
        model.addAttribute("title", "budgets and issues for which you can vote now.");
        model.addAttribute("currentLaw", budget);
        return "budget_end_emission_15_day";

    }
    /**Отображается в браузере, список всех пакета законов*/
    @GetMapping("/all-laws")
    public String allLaws(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        Map<String, Account> balances = new HashMap<>();
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);


        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockchain.getBlockchainList(), Seting.BOARDS_BLOCK);


        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        //данные для отображения


        Directors directors = new Directors();
        //TODO доработать оптимизацию
        //TODO избавиться от find position в данном методе
        //TODO доработать оптимизацию
        //TODO избавиться от find position в данном методе
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
            } else if (higherSpecialPositions.isElectedByStocks()) {
                fIndPositonHelperDataMap.put(higherSpecialPositions,
                        new FIndPositonHelperData(higherSpecialPositions, true, true, false, false, false));

            }

        }


        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                blockchain.getBlockchainList(),
                Seting.LAW_YEAR_VOTE);



        current = current.stream().distinct().collect(Collectors.toList());

        current = current.stream().sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());
        model.addAttribute("allLaws", current);
        return "all-laws";
    }


    /**Создать новую должность*/
    @GetMapping("/add_position")
    public String addPostion(Model model){
        model.addAttribute("title", "Create a new position");
        return "add_position";
    }

    @PostMapping("/add_position")
    public String addPosition( @RequestParam String sender,
                               @RequestParam String reward,
                               @RequestParam String nameLaw,
                               @RequestParam String[] laws,
                               @RequestParam String password,
                               RedirectAttributes redirectAttrs) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
        nameLaw = Seting.ADD_DIRECTOR + nameLaw;
        String[] lawsAdd = new String[laws.length];
        for (int i = 0; i < laws.length; i++) {
            lawsAdd[i] = Seting.ADD_DIRECTOR + laws[i];
        }
        laws = lawsAdd;

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

        Directors directors = new Directors();
        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<Director> enumPosition = directors.getDirectors();
            List<String> corporateSeniorPositions = enumPosition.stream()
                    .map(t->t.getName())
                    .collect(Collectors.toList());

            if (corporateSeniorPositions.contains(law.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, law)) {
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }

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

    /**Отображается в браузере, позволяет создать новый пакет законов*/
    @GetMapping("/create-law")
    public String createLawsShow(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        BasisController.resolve();
        model.addAttribute("title", "create law");
        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account Budget = balances.get(Seting.BUDGET);
        if(Budget == null)
            Budget = new Account(Seting.BUDGET, 0, 0);
        model.addAttribute("dollar", Budget.getDigitalDollarBalance());
        model.addAttribute("stock", Budget.getDigitalStockBalance());
        model.addAttribute("emission", Seting.EMISSION_BUDGET);
        return "create-law";
    }


    @RequestMapping(value = "/create-law", method = RequestMethod.POST, params = "action=/send")
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

        Directors directors = new Directors();
        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<Director> enumPosition = directors.getDirectors();
            List<String> corporateSeniorPositions = enumPosition.stream()
                    .map(t->t.getName())
                    .collect(Collectors.toList());

            if (corporateSeniorPositions.contains(law.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, law)) {
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }

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
