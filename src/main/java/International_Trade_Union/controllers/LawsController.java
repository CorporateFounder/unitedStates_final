package International_Trade_Union.controllers;

import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.model.FIndPositonHelperData;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class LawsController {
    @Autowired
    BlockService blockService;
    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);

    }

    /**Отображает детали пакета законов в браузере.
     * Displays the details of the law package in the browser.*/
    @GetMapping("detail-laws")
    public String details(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {

        return "detail-laws";
    }



    /**
     * Отображается в браузере, позволяет увидеть содержимое пакета законов, список действующих законов.
     * Displayed in the browser, allows you to see the contents of the package of laws, a list of current laws.
     */
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

    /**
     * Отображается в браузере, показывает содержимое пакета законов, из списка всех законов.
     * Displayed in the browser, shows the contents of the package of laws, from a list of all laws.
     */
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

        Laws laws = new Laws();
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
//        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", base.encode(dtoTransaction.getSign()));
        Directors directors = new Directors();
        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t -> t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if (corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)) {
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
            System.out.println("dto MainController: " + dtoTransaction);

            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s + "/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if (BasisController.getExcludedAddresses().contains(url)) {
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                } catch (Exception e) {
                    System.out.println("exception discover: " + original);

                }
            }


        } else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";
    }

    /**
     * Голосование учитывает голоса как акций, так и голоса избранных представителей.
     * Voting takes into account both share votes and votes of elected representatives.
     */
    @GetMapping("/voting")
    public String lawVoting() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {

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
        Laws laws = new Laws();
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
//        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", base.encode(dtoTransaction.getSign()));
        Directors directors = new Directors();
        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t -> t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if (corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)) {
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
            System.out.println("dto MainController: " + dtoTransaction);

            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s + "/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if (BasisController.getExcludedAddresses().contains(url)) {
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                } catch (Exception e) {
                    System.out.println("exception discover: " + original);

                }
            }


        } else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";
    }

    /**
     * Отображается в браузере, список все действующих законов.
     * Displayed in the browser, a list of all current laws.
     */
    @GetMapping("/current-laws")
    public String currentLaw(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        if (BasisController.isUpdating() || BasisController.isMining()) {
            return "redirect:/processUpdating";
        }

        //получает список должностей
        Directors directors = new Directors();

        //получает блоки из базы данных, за больший период (414720 блоков)
        List<Block> blocksList = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                blockService.findBySpecialIndexBetween(
                        BasisController.getBlockchainSize() - Seting.LAW_HALF_VOTE,
                        BasisController.getBlockchainSize() -1
                )
        );


        Map<String, Account> balances = new HashMap<>();
        //извлекает весь список балансов из базы данных.
        balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

        //извлекает из файла список объектов.
        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //получить активных участников за фиксированный период.
        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blocksList, Seting.BOARDS_BLOCK);


        //подсчет происходит с блоками, полученными порциями из базы данных.
        Map<String, CurrentLawVotes> votesMap = new HashMap<>();
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        long from = 0;
        long to = BasisController.getBlockchainSize();

        if (BasisController.getBlockchainSize() > Seting.LAW_HALF_VOTE) {
            from = BasisController.getBlockchainSize() - Seting.LAW_HALF_VOTE;
        }
        List<Block> list = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(from, to));
        for (Block block : list) {
            UtilsCurrentLaw.calculateVote(votesMap, accounts, block);
        }


        //подсчитать голоса за все проголосованные законы (здесь происходит подсчет, на основе монет)
        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                votesMap);



        //убрать появление всех бюджет и эмиссий из отображения в действующих законах
        current = current.stream()
                .filter(t -> !t.getPackageName().equals(Seting.EMISSION) ||
                        t.getPackageName().equals(Seting.BUDGET))
                .collect(Collectors.toList());


        //здесь отображается избранный совет директоров, на основе правил.
        List<CurrentLawVotesEndBalance> electedBoardOfDirectors = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())
                .collect(Collectors.toList());

        //здесь отображается избранный совет судей, на основе правил.
        List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())
                .collect(Collectors.toList());


        //позиции утвержденные советом директоров
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                || t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_VOTE
               )
                .collect(Collectors.toList());
        //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //позиции избираемые советом директоров и судей
        List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()
                .filter(t -> directors.isofficeOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t ->

                        t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES
                       )
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


        //групируем по списку
        Map<String, List<CurrentLawVotesEndBalance>> group = electedBoardOfDirectors.stream()
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


        //позиции избираемые советом  судей
        List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList());


        //избираемые Гендиректором
        List<CurrentLawVotesEndBalance> electedByGeneralExecutiveDirector = electedByBoardOfDirectors.stream()
                .filter(t -> directors.isElectedCEO(t.getPackageName()))
                .filter(t -> NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString().equals(t.getPackageName()))
                .filter(t -> t.getVoteGeneralExecutiveDirector() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_GENERAL_EXECUTIVE_DIRECTOR)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVoteGeneralExecutiveDirector))
                .collect(Collectors.toList());

        //голос верховного судьи (на данный момент не используется)
        List<CurrentLawVotesEndBalance> electedByHightJudge = electedByCorporateCouncilOfReferees.stream()
                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                .filter(t -> t.getVoteHightJudge() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_HIGHT_JUDGE)
                .collect(Collectors.toList());


        //законы должны быть одобрены всеми.
        List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> !Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t->
                        t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES

                )
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());


        //внедрение поправок в устав
        List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT
                        ||
                         t.getFounderVote() >= 1 && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)

                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());

        //добавляет законы, которые создают новые должности утверждается всеми
        List<CurrentLawVotesEndBalance> addDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES
               )
                .collect(Collectors.toList());

        //план утверждается всеми
        List<CurrentLawVotesEndBalance> planFourYears = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.STRATEGIC_PLAN.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES
               )
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());


        //устав всегда действующий он подписан основателем
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFounderVote() >= 1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());

        //ИСХОДНЫЙ КОД СОЗДАННЫЙ ОСНОВАТЕЛЕМ
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL_CODE = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFounderVote() >= 1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());




        List<CurrentLawVotesEndBalance> charterCheckBlock = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFounderVote() >= 1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());

        CHARTER_ORIGINAL.addAll(charterCheckBlock);


        List<CurrentLawVotesEndBalance> charterOriginalCode = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFounderVote() >= 1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());



        CHARTER_ORIGINAL_CODE.addAll(charterOriginalCode);
        for (Map.Entry<Director, List<CurrentLawVotesEndBalance>> higherSpecialPositionsListMap : original_group.entrySet()) {
            current.addAll(higherSpecialPositionsListMap.getValue());
        }


        current = new ArrayList<>();
        current.addAll(addDirectors);

        current.addAll(electedBoardOfDirectors);
        current.addAll(planFourYears);

        current.addAll(electedByStockCorporateCouncilOfReferees);
        current.addAll(electedByBoardOfDirectors);
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


    /**
     * Отображается в браузере, список всех пакета законов.
     * Displayed in the browser, a list of all packages of laws.
     */
    @GetMapping("/all-laws")
    public String allLaws(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        if (BasisController.isUpdating() || BasisController.isMining()) {
            return "redirect:/processUpdating";
        }


//        Blockchain blockchain = Mining.getBlockchain(
//                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                BlockchainFactoryEnum.ORIGINAL);

        List<Block> blocksList = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                blockService.findBySpecialIndexBetween(
                        BasisController.getBlockchainSize() - Seting.LAW_HALF_VOTE,
                        BasisController.getBlockchainSize() -1
                )
        );
        Map<String, Account> balances = new HashMap<>();
//        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());



        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blocksList, Seting.BOARDS_BLOCK);


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
            } else if (higherSpecialPositions.isElectedByBoardOfDirectors()) {
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


        //подсчет происходит с базы данных, таким образом вычисления происходят быст
        Map<String, CurrentLawVotes> votesMap = new HashMap<>();
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        long from = 0;
        long to = BasisController.getBlockchainSize();

        if (BasisController.getBlockchainSize() > Seting.LAW_HALF_VOTE) {
            from = BasisController.getBlockchainSize() - Seting.LAW_HALF_VOTE;
        }
        List<Block> list = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(from, to));
        for (Block block : list) {
            UtilsCurrentLaw.calculateVote(votesMap, accounts, block);
        }

//        for (long i = from; i < to; i += 10000) {
//            List<Block> list = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(i, Math.min(to, i + 1000)));
//            for (Block block : list) {
//                UtilsCurrentLaw.calculateVote(votesMap, accounts, block);
//            }
//        }


        //подсчитать голоса за все проголосованные заканы
        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                votesMap);


        current = current.stream().distinct().collect(Collectors.toList());


        current = current.stream().sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());


        model.addAttribute("allLaws", current);
        return "all-laws";
    }


    /**
     * Создать новую должность, новые должности имеют только одно место. То есть на данную должность может избираться
     * только один из множества кандидатов.
     * Create a new position, new positions have only one location. That is, one can be elected to this position
     *       * only one of many candidates.
     */
    @GetMapping("/add_position")
    public String addPostion(Model model) {
        model.addAttribute("title", "Create a new position");
        return "add_position";
    }

    @PostMapping("/add_position")
    public String addPosition(@RequestParam String sender,
                              @RequestParam String reward,
                              @RequestParam String nameLaw,
                              @RequestParam String[] laws,
                              @RequestParam String password,
                              RedirectAttributes redirectAttrs) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

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
//        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", base.encode(dtoTransaction.getSign()));
        Directors directors = new Directors();
        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<Director> enumPosition = directors.getDirectors();
            List<String> corporateSeniorPositions = enumPosition.stream()
                    .map(t -> t.getName())
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

    /**
     * Отображается в браузере, позволяет создать новый пакет законов.
     * Displayed in the browser, allows you to create a new package of laws.
     */
    @GetMapping("/create-law")
    public String createLawsShow(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        model.addAttribute("title", "create law");
        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
//        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

        Account Budget = balances.get(Seting.BUDGET);
        if (Budget == null)
            Budget = new Account(Seting.BUDGET, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
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
//        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", base.encode(dtoTransaction.getSign()));
        Directors directors = new Directors();
        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<Director> enumPosition = directors.getDirectors();
            List<String> corporateSeniorPositions = enumPosition.stream()
                    .map(t -> t.getName())
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
