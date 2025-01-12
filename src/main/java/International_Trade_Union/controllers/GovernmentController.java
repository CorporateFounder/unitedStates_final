package International_Trade_Union.controllers;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GovernmentController {

    @Autowired
    BlockService blockService;
    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);

    }


    /**Отображает в браузере список действующих должностей.
     * Displays a list of current positions in the browser*/
    @GetMapping("/governments")
    public String corporateSeniorpositions(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        if (BasisController.isUpdating() || BasisController.isMining()) {
            return "redirect:/processUpdating";
        }

        Directors directors = new Directors();
        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        List<Account> boardOfShareholders = new ArrayList<>();
        Map<String, CurrentLawVotes> votesMap = new HashMap<>();
        List<Account> accounts = balances.values().stream().toList();
        UtilsCurrentLaw.processBlocks(votesMap, accounts, BasisController.getBlockchainSize(), blockService);

        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                votesMap);

        // Утверждение совета директоров
        List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())
                .collect(Collectors.toList());

        // Утверждение совета судей
        List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())
                .collect(Collectors.toList());

        // Утверждение новых должностей
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();
                    return t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                            || (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                })
                .collect(Collectors.toList());
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        // Назначение должностей советом директоров и судей
        List<CurrentLawVotesEndBalance> electedByBoardOfShareholders = current.stream()
                .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();
                    return t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                            || (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                })
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());

        // Группировка результатов
        Map<String, List<CurrentLawVotesEndBalance>> group = electedByBoardOfShareholders.stream()
                .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));
        Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();
        for (Map.Entry<String, List<CurrentLawVotesEndBalance>> entry : group.entrySet()) {
            List<CurrentLawVotesEndBalance> temporary = entry.getValue().stream()
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes))
                    .limit(directors.getDirector(entry.getKey()).getCount())
                    .collect(Collectors.toList());
            original_group.put(directors.getDirector(entry.getKey()), temporary);
        }

        // Итоговое объединение результатов
        current = new ArrayList<>();
        current.addAll(createdByBoardOfDirectors);
        current.addAll(electedByStockBoardOfDirectors);
        current.addAll(electedByStockCorporateCouncilOfReferees);
        current.addAll(electedByBoardOfShareholders);
        current = current.stream()
                .filter(UtilsUse.distinctByKey(CurrentLawVotesEndBalance::getAddressLaw))
                .filter(t -> directors.contains(t.getPackageName()))
                .collect(Collectors.toList());

        model.addAttribute("title", "Corporate senior positions and approvals.");
        model.addAttribute("currentLaw", current);

        return "governments";
    }


    /**Позволяет подать на должность в системе и получить идентификационный номер.
     * Allows you to apply for a position in the system and receive an identification number.**/
    @GetMapping("/create-position")
    public String createPositionShow(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
//        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
         balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());


        Directors directors = new Directors();

        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);


//        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockService, Seting.BOARDS_BLOCK);
        List<Account> boardOfShareholders = new ArrayList<>();

        //подсчет происходит с базы данных, таким образом вычисления происходят быст
        Map<String, CurrentLawVotes> votesMap = new HashMap<>();
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        UtilsCurrentLaw.processBlocks(votesMap, accounts, BasisController.getBlockchainSize(), blockService);


        //подсчитать голоса за все проголосованные законы.
        //count the votes for all voted laws.
        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                votesMap);


        List<String> positions = directors.getDirectors().stream().map(t->t.getName()).collect(Collectors.toList());
        //positions are approved by the board of directors.
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t->
                        t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
        //добавление позиций созданных советом директоров
        //adding positions created by the board of directors
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }


        positions.addAll(directors.getNames());
        positions = positions.stream().distinct().collect(Collectors.toList());
        model.addAttribute("positions", positions);
        return "create-position";
    }

    /**Позволяет подать на существующие должности и получить идентификационный номер.
     * Allows you to apply for existing positions and receive an identification number.*/
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
        redirectAttrs.addFlashAttribute("dollar", Seting.MIN_SENDING_FOR_LAW);
        redirectAttrs.addFlashAttribute("stock", 0.0);
        redirectAttrs.addFlashAttribute("reward", rewardD);
        redirectAttrs.addFlashAttribute("vote", "YES");
        dtoTransaction.setSign(sign);
//        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", base.encode(dtoTransaction.getSign()));

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
