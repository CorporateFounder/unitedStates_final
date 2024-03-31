package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.model.Mining;
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

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GovernmentController {

    @Autowired
    BlockService blockService;


    /**Отображает в браузере список действующих должностей.
     * Displays a list of current positions in the browser*/
    @GetMapping("/governments")
    public String corporateSeniorpositions(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }


        Directors directors = new Directors();
//        Blockchain blockchain = Mining.getBlockchain(
//                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                BlockchainFactoryEnum.ORIGINAL);

        List<Block> blocksList = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                blockService.findBySpecialIndexBetween(
                        BasisController.getBlockchainSize() - Seting.LAW_YEAR_VOTE,
                        BasisController.getBlockchainSize() -1
                )
        );

        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
//        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());


        //Считывает из файла идентификационный номер закона, а так же его баланс. Закон так же может
        //выглядеть как баланс.
        //Reads the law identification number from the file, as well as its balance. The law can also
        //look like balance.
        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //получить совет акционеров из файла.
        //obtain shareholder advice from file.
        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blocksList, Seting.BOARDS_BLOCK);

        //фильтрует должности по типам голосования и как их избирают.
        //filters positions by voting type and how they are elected.
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

        //Для каждого пакета включая должности, отбирает список всех адресов которые полосовали за или против,
        //за определенный период LAW_YEAR_VOTE.
        //For each package including positions, selects a list of all addresses that were striped for or against,
        //for a certain period LAW_YEAR_VOTE.
        Map<String, CurrentLawVotes> votesMap = new HashMap<>();
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        long from = 0;
        long to = BasisController.getBlockchainSize();

        if (BasisController.getBlockchainSize() > Seting.LAW_YEAR_VOTE) {
            from = BasisController.getBlockchainSize() - Seting.LAW_YEAR_VOTE;
        }
        List<Block> list = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(from, to));
        for (Block block : list) {
            UtilsCurrentLaw.calculateVote(votesMap, accounts, block);
        }
//        for (long i = from; i < to; i += 10000) {
//            List<Block> list = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(i, Math.min(to, i + 1000)));
//            for (Block block : list) {
//                votesMap = UtilsCurrentLaw.calculateVote(votesMap, accounts, block);
//            }
//        }
        //подсчитывает голоса для каждого закона или кандидата.
        //counts votes for each law or candidate.
        List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotes(
                lawEligibleForParliamentaryApprovals,
                balances,
                boardOfShareholders,
                votesMap);



        //здесь утверждается действующий совет директоров.
        //the current board of directors is approved here.
        List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())
                .collect(Collectors.toList());



        //здесь утверждается совет корпоративных судей.
        //the council of corporate judges is approved here.
        List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))
                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())
                .collect(Collectors.toList());



        //здесь утверждаются новые должности, которые назначаются советом директоров.
        //new positions are approved here and appointed by the board of directors.
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES
                        || t.getVotesBoardOfDirectors() > Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_VOTE
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .collect(Collectors.toList());
        //добавляются новые должности, которые были утверждены советом директоров.
        //new positions are added that have been approved by the board of directors.
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //Эти должности назначаются советом директоров, если же основатель наложил вето, то должен так же утвердить
        //совет корпоративных судей.
        //These positions are appointed by the board of directors, but if the founder vetoes, he must also approve
        //council of corporate judges.
        List<CurrentLawVotesEndBalance> electedByBoardOfShareholders = current.stream()
                .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES
                || t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_VOTE
                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


        //группируем по списку. //group by list.
        Map<String, List<CurrentLawVotesEndBalance>> group = electedByBoardOfShareholders.stream()
                .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));

        Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();

        //оставляем то количество которое описано в данной должности.
        //we leave the quantity described in this post.
        for (Map.Entry<String, List<CurrentLawVotesEndBalance>> stringListEntry : group.entrySet()) {
            List<CurrentLawVotesEndBalance> temporary = stringListEntry.getValue();
            temporary = temporary.stream()
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes))
                    .limit(directors.getDirector(stringListEntry.getKey()).getCount())
                    .collect(Collectors.toList());
            original_group.put(directors.getDirector(stringListEntry.getKey()), temporary);
        }



        //позиции избираемые советом корпоративных верховных судей.
        //positions elected by the council of corporate supreme judges.
        List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList());


        //добавляет законы, которые создают новые должности утверждается советом директоров.
        //adds laws that create new positions approved by the board of directors.
        List<CurrentLawVotesEndBalance> addDirectors = current.stream()
                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES
                        || t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
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
        current = current.stream()
                .filter(UtilsUse.distinctByKey(CurrentLawVotesEndBalance::getAddressLaw))
                .filter(t->directors.contains(t.getPackageName()))
                .collect(Collectors.toList());


        model.addAttribute("title", "How the current laws are made is described in the charter." +
                " ");
        model.addAttribute("currentLaw", current);

        return "governments";
    }


    /**Позволяет подать на должность в системе и получить идентификационный номер.
     * Allows you to apply for a position in the system and receive an identification number.**/
    @GetMapping("/create-position")
    public String createPositionShow(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        Map<String, Account> balances = new HashMap<>();
        //считывать баланс
//        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
         balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());


        Directors directors = new Directors();

        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);


        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockchain.getBlockchainList(), Seting.BOARDS_BLOCK);

        //подсчет происходит с базы данных, таким образом вычисления происходят быст
        Map<String, CurrentLawVotes> votesMap = new HashMap<>();
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        long from = 0;
        long to = BasisController.getBlockchainSize();

        if (BasisController.getBlockchainSize() > Seting.LAW_YEAR_VOTE) {
            from = BasisController.getBlockchainSize() - Seting.LAW_YEAR_VOTE;
        }
        List<Block> list = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(from, to));
        for (Block block : list) {
            UtilsCurrentLaw.calculateVote(votesMap, accounts, block);
        }

//        for (long i = from; i < to; i += 10000) {
//            List<Block> list = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(i, Math.min(to, i + 1000)));
//            for (Block block : list) {
//                votesMap = UtilsCurrentLaw.calculateVote(votesMap, accounts, block);
//            }
//        }

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
        redirectAttrs.addFlashAttribute("dollar", 0.0);
        redirectAttrs.addFlashAttribute("stock", 0.0);
        redirectAttrs.addFlashAttribute("reward", rewardD);
        redirectAttrs.addFlashAttribute("vote", "YES");
        dtoTransaction.setSign(sign);
        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", encoded);

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
