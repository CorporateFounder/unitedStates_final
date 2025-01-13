package International_Trade_Union.controllers;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.*;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.*;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.controllers.BasisController.getNodes;
import static International_Trade_Union.setings.Seting.VETO_THRESHOLD;


@RestController
public class ConductorController {

    @Autowired
    BlockService blockService;
    @Autowired
    UtilsResolving utilsResolving;

    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);

    }
    /**
     * создает новую пару ключ и пароль.
     * creates a new key and password pair.
     * (from local host)
     */
    @GetMapping("/keys")
    public Map<String, String> keys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return CreateAccount.create();
    }


    /**скачать актуальный блокчейн.
     * download the current blockchain.
     * (update local from global node)*/
    @GetMapping("/updating")
    @ResponseBody
    public synchronized Integer updating() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating()){
            return -2;
        }

        return utilsResolving.resolve3();
    }

    /**
     *получить полный баланс адреса, по публичному ключу адреса.
     * get the full balance of the address using the public key of the address.
     * (from local host)
     */
    @GetMapping("/account")
    @ResponseBody
    public Account account(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
//        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);


        EntityAccount entityAccount  = new EntityAccount();
        try {
            entityAccount = blockService.findByAccount(address);
        }catch (Exception e){
            MyLogger.saveLog("account address: " + e.getMessage());
            return new Account();
        }
//        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

        Account account = UtilsAccountToEntityAccount.entityAccountToAccount(entityAccount);

        return account;
    }

    /**
     * get dollar balance
     * (from local host)
     */
    @GetMapping("/dollar")
    @ResponseBody
    public Double dollar(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
//        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);


        EntityAccount entityAccount  = new EntityAccount();
        try {
            entityAccount = blockService.findByAccount(address);
        }catch (Exception e){
            MyLogger.saveLog("account address: " + e.getMessage());
            return Double.valueOf(-1);
        }
//        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

        Account account = UtilsAccountToEntityAccount.entityAccountToAccount(entityAccount);


        return UtilsUse.round(account.getDigitalDollarBalance(), Seting.SENDING_DECIMAL_PLACES).doubleValue();
    }

    /**
     * get stock balance
     * (from local host)
     */
    @GetMapping("/stock")
    @ResponseBody
    public Double stock(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        EntityAccount entityAccount  = new EntityAccount();
        try {
            entityAccount = blockService.findByAccount(address);
        }catch (Exception e){
            MyLogger.saveLog("account address: " + e.getMessage());
            return Double.valueOf(-1);
        }
//        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

        Account account = UtilsAccountToEntityAccount.entityAccountToAccount(entityAccount);


        return UtilsUse.round(account.getDigitalStockBalance(), Seting.SENDING_DECIMAL_PLACES).doubleValue();

    }

    /**
     * send dollar or stock (if return wrong-its not sending, if return sign its success)
     * (send to global node)
     */
    @GetMapping("/sendCoin")
    @ResponseBody
    public SendCoinResult send(@RequestParam String sender,
                           @RequestParam String recipient,
                           @RequestParam(defaultValue = "0.0") Double dollar,
                           @RequestParam(defaultValue = "0.0") Double stock,
                           @RequestParam(defaultValue = "0.0") Double reward,
                           @RequestParam String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
    Base base = new Base58();
    SendCoinResult result = new SendCoinResult(); // Initialize the result
        // Check if the password contains only valid Base58 characters
        if (!password.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+")) {
            System.out.println("Password contains invalid Base58 characters");
            return result;
        }
        dollar = UtilsUse.truncateAndRound(BigDecimal.valueOf(dollar)).doubleValue();
        stock = UtilsUse.truncateAndRound(BigDecimal.valueOf(stock)).doubleValue();
        reward = UtilsUse.truncateAndRound(BigDecimal.valueOf(reward)).doubleValue();

        reward = 0.0;
    if (dollar == null || dollar <  Seting.MINIMUM_2) dollar = 0.0;
    if (stock == null || stock <  Seting.MINIMUM_2) stock = 0.0;
    if (reward == null || reward <  Seting.MINIMUM_2) reward = 0.0;



        Laws laws = new Laws();
    laws.setLaws(new ArrayList<>());
    laws.setHashLaw("");
    laws.setPacketLawName("");
    DtoTransaction dtoTransaction = new DtoTransaction(
            sender,
            recipient,
            dollar,
            stock,
            laws,
            reward,
            VoteEnum.YES);
        PrivateKey privateKey;
        byte[] sign ;
    try {
        privateKey= UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
    }catch (IOException e){
        return result;
    }

        Account senderAccount = null;
        try{
            senderAccount = UtilsAccountToEntityAccount.entityAccountToAccount(blockService.findByAccount(sender));

        }catch (Exception e){
            result.put("wrong balance", "FAILED: " + e);
            System.out.println(result);
            return result;
        }
        if(!"success".equals(UtilsUse.checkSendBalance(senderAccount, dtoTransaction))){
            String str = UtilsUse.checkSendBalance(senderAccount, dtoTransaction);
            result.put("wrong balance", "FAILED: " + str);
            System.out.println(result);
            return result;

        }
    System.out.println("Main Controller: new transaction: vote: " + VoteEnum.YES);
    dtoTransaction.setSign(sign);
    Directors directors = new Directors();
    System.out.println("sender: " + sender);
    System.out.println("recipient: " + recipient);
    System.out.println("dollar: " + dollar + ": class: " + dollar.getClass());
    System.out.println("stock: " + stock + ": class: " + stock.getClass());
    System.out.println("reward: " + reward + ": class: " + reward.getClass());
    System.out.println("password: " + password);
    try {
        System.out.println("sign: " + base.encode(dtoTransaction.getSign()));
        System.out.println("verify: " + dtoTransaction.verify());

    }catch (Exception e){
        return result;
    }

    if (dtoTransaction.verify()) {

        List<String> corporateSeniorPositions = directors.getDirectors().stream()
                .map(t -> t.getName()).collect(Collectors.toList());
        System.out.println("LawsController: create_law: " + laws.getPacketLawName() + " contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
        if (corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)) {
            System.out.println("sending wrong transaction: Position to be equals with send");
            return result;
        }
        try {
            result.setSign(base.encode(dtoTransaction.getSign()));
            result.setDtoTransaction(dtoTransaction);

        }catch (Exception e){
            return new SendCoinResult();
        }

        String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
        Set<String> nodesAll = getNodes();
        List<HostEndDataShortB> sortPriorityHost = utilsResolving.sortPriorityHost(nodesAll);

        for (HostEndDataShortB hostEndDataShortB : sortPriorityHost) {
            String original = hostEndDataShortB.getHost();
            String url = hostEndDataShortB.getHost() + "/addTransaction";
            if (BasisController.getExcludedAddresses().contains(url)) {
                System.out.println("MainController: its your address or excluded address: " + url);
                continue;
            }
            try {
                // Send to network
                int responseCode = UtilUrl.sendPost(jsonDto, url);
                if (responseCode == 200) {
                    result.put(hostEndDataShortB.getHost(), "SENDED");
                } else {
                    result.put(hostEndDataShortB.getHost(), "FAILED: " + responseCode);
                }
            } catch (Exception e) {
                System.out.println("exception discover: " + original);
                result.put(hostEndDataShortB.getHost(), "SERVER DID NOT RESPOND");
            }
        }
    } else {
        return result;
    }
    return result;
}


    /**
     * whether the transaction was added to the blockchain, find with sign
     * (check from local host)
     */
    @GetMapping("/isTransactionAdd")
    public Boolean isTransactionGet(@RequestParam String sign) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        Base base = new Base58();
        return blockService.existsBySign(base.decode(sign));


    }

    @PostMapping("/TransactionAddBase")
    public DtoTransaction TransactionGetBase(@RequestBody SignRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        try {

            // Удаление всех пробелов из строки Base64
            String sanitizedSign = request.getSign().replaceAll("\\s+", "");
            // Декодирование строки Base64 в байты

            // Преобразование байтов в строку Base58
            String base58Sign =sanitizedSign;
            // Проверка наличия в базе данных по строке Base58
            EntityDtoTransaction entityDtoTransaction = blockService.findBySign(base58Sign);
            if(entityDtoTransaction == null)
                return null;
            return UtilsBlockToEntityBlock.entityToDto(entityDtoTransaction);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid Base64 input", e);
        }
    }

    private final Base58 base58 = new Base58();
    @PostMapping("/isTransactionAddBase64")
    public Boolean isTransactionGetBase64(@RequestBody SignRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        try {

            // Удаление всех пробелов из строки Base64
            String sanitizedSign = request.getSign().replaceAll("\\s+", "");
            // Декодирование строки Base64 в байты
            byte[] decodedBytes = Base64.getDecoder().decode(sanitizedSign);
            // Преобразование байтов в строку Base58
            String base58Sign = base58.encode(decodedBytes);
            // Проверка наличия в базе данных по строке Base58
            if(base58Sign == null)
                return false;
            return blockService.findBySign(base58Sign) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @PostMapping("/TransactionAddBase64")
    public DtoTransaction TransactionGetBase64(@RequestBody SignRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        try {

            // Удаление всех пробелов из строки Base64
            String sanitizedSign = request.getSign().replaceAll("\\s+", "");
            // Декодирование строки Base64 в байты
            byte[] decodedBytes = Base64.getDecoder().decode(sanitizedSign);
            // Преобразование байтов в строку Base58
            String base58Sign = base58.encode(decodedBytes);
            // Проверка наличия в базе данных по строке Base58
            EntityDtoTransaction entityDtoTransaction = blockService.findBySign(base58Sign);
            if(entityDtoTransaction == null)
                return null;
            return UtilsBlockToEntityBlock.entityToDto(entityDtoTransaction);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }


    /**find block from hash
     * (from local host)*/
    @GetMapping("/blockHash")
    @ResponseBody
    public Block blockFromHash(@RequestParam String hash) throws IOException {


//        return Blockchain.hashFromFile(hash, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        Block block = UtilsBlockToEntityBlock.entityBlockToBlock(
                blockService.findByHashBlock(hash)
        );
        return block;
    }

    @PostMapping ("/findBlocksFromSign58")
    public List<Block> findBlocksFromSign58(@RequestBody SignRequest reques){
        try {
            List<EntityBlock> blocks = blockService.findBlocksByTransactionSign(reques.getSign());
            List<Block> blocks1 = UtilsBlockToEntityBlock.entityBlocksToBlocks(blocks);
            return blocks1;
        }catch (Exception e){

            return new ArrayList<>();
        }

    }

    @PostMapping ("/findBlocksFromSign64")
    public List<Block> findBlocksFromSign64(@RequestBody SignRequest reques){
        try {

            byte[] bytes = Base64.getDecoder().decode(reques.getSign());
            String sign = base58.encode(bytes);
            List<EntityBlock> blocks = blockService.findBlocksByTransactionSign(sign);
            List<Block> blocks1 = UtilsBlockToEntityBlock.entityBlocksToBlocks(blocks);
            return blocks1;
        }catch (Exception e){

            return new ArrayList<>();
        }
    }
    /**найти блок по индексу.
     * find a block by index.
     * (from local host)*/
    @GetMapping("/conductorBlock")
    @ResponseBody
    public Block  block(@RequestParam Integer index) throws IOException {


        if(index < 0 ){
            index = 0;
        }
        if(index > BasisController.getBlockchainSize() -1){
            index = BasisController.getBlockchainSize() - 1;
        }
        return UtilsBlockToEntityBlock.entityBlockToBlock(
                blockService.findBySpecialIndex(index)
        );
    }
    /***
     * находит транзакцию по подписи.
     * finds a transaction by signature.
     * (from local host)
     */

    @GetMapping("/conductorHashTran")
    @ResponseBody
    public DtoTransaction transaction(@RequestParam String hash) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        List<EntityDtoTransaction> entityDtoTransactions =
                blockService.findAllDto();
        EntityDtoTransaction entityDtoTransaction = null;
        try {
             entityDtoTransaction = blockService.findBySign(hash);

            if(entityDtoTransaction == null){
                return null;
            }
        }catch (Exception e){
           return null;
        }

        return UtilsBlockToEntityBlock.entityToDto(entityDtoTransaction);
    }
    /**
     * Получить список транзакций для адреса отправителя, от определенного блока до определенного блока
     */
    @GetMapping("/senderTransactions")
    @ResponseBody
    public List<DtoTransaction> senderTransactions(
            @RequestParam String address,
            @RequestParam int from,
            @RequestParam int to
    ) {
        try {


            if (to - from > 500) {
                to = from + 500;
            }
            return blockService.findBySender(address, from, to);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получить список транзакций для адреса получателя, от определенного блока до определенного блока
     */

    @GetMapping("/customerTransactions")
    @ResponseBody
    public List<DtoTransaction> customerTransactions(
            @RequestParam String address,
            @RequestParam int from,
            @RequestParam int to
    ) {
        try {

            if (to - from > 500) {
                to = from + 500;
            }
            return blockService.findByCustomer(address, from, to);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * количество отправленных транзакций от данного адреса
     */
    @GetMapping("/senderCountDto")
    @ResponseBody
    public long countSenderTransaction(
            @RequestParam String address
    ) {
        try {

            return blockService.countSenderTransaction(address);

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * количество полученных транзакций от данного адреса
     */

    @GetMapping("/customerCountDto")
    @ResponseBody
    public long countCustomerTransaction(
            @RequestParam String address
    ) {
        try {

            return blockService.countCustomerTransaction(address);

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * Получить все балансы данного адреса
     */
    @GetMapping("/addresses")
    @ResponseBody
    public Map<String, Account> addresses() {
        Map<String, Account> accountMap = new HashMap<>();
        try {

            accountMap = UtilsAccountToEntityAccount
                    .entityAccountsToMapAccounts(blockService.findAllAccounts());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accountMap;
    }




    @GetMapping("/getBlocksBySenderInRange")
    public List<EntityBlock> getBlocksBySender(
            @RequestParam long from,
            @RequestParam long to,
            @RequestParam String sender) {
        try {
            return blockService.findBlocksBySpecialIndexRangeAndSender(from, to, sender);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/findBlocksByCustomerInRange")
    public List<EntityBlock> getBlocksByCustomer(
            @RequestParam long from,
            @RequestParam long to,
            @RequestParam String customer) {
        try {
            return blockService.findBlocksBySpecialIndexRangeAndCustomer(from, to, customer);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/current-laws-body")
    @ResponseBody
    public List<CurrentLawVotesEndBalance> currentLawVotesEndBalances() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        if (BasisController.isUpdating() || BasisController.isMining()) {
            return new ArrayList<>();
        }
        utilsResolving.resolve3();

        //получает список должностей
        Directors directors = new Directors();


        Map<String, Account> balances = new HashMap<>();
        //извлекает весь список балансов из базы данных.
        balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

        //извлекает из файла список объектов.
        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =
                UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //получить активных участников за фиксированный период.
//        List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockService, Seting.BOARDS_BLOCK);
        List<Account> boardOfShareholders = new ArrayList<>();


        //подсчет происходит с блоками, полученными порциями из базы данных.
        Map<String, CurrentLawVotes> votesMap = new HashMap<>();
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());

        VoteMapAndLastIndex voteMapAndLastIndex = new VoteMapAndLastIndex();
        String json = UtilsFileSaveRead.read(Seting.SLIDING_WINDOWS_VOTING);
        if (json != null && !json.isBlank())
            voteMapAndLastIndex = (VoteMapAndLastIndex) UtilsJson.jsonToClass(json, VoteMapAndLastIndex.class);
        if (voteMapAndLastIndex == null)
            voteMapAndLastIndex = new VoteMapAndLastIndex();
        boolean isSave = false;
        if (voteMapAndLastIndex.getFinishIndex()+1  < BasisController.getBlockchainSize() - 10) {
            isSave = true;
        }
        UtilsCurrentLaw.processBlocksWithWindow(voteMapAndLastIndex, accounts, BasisController.getBlockchainSize() - 10, blockService);

        if (isSave) {
            System.out.println("vote: " + voteMapAndLastIndex.getFinishIndex());
            System.out.println("size: " +BasisController.getBlockchainSize());
            System.out.println("size2: " +(BasisController.getBlockchainSize() - 10));
            UtilsFileSaveRead.save(UtilsJson.objToStringJson(voteMapAndLastIndex), Seting.SLIDING_WINDOWS_VOTING, false);
        }
        votesMap = voteMapAndLastIndex.getVotesMap();

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
                .peek(t -> t.setValid(t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE))
                .filter(CurrentLawVotesEndBalance::isValid)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())
                .collect(Collectors.toList());

        //здесь отображается избранный совет судей, на основе правил.
        List<CurrentLawVotesEndBalance> CORPORATE_COUNCIL_OF_REFEREES = current.stream()
                .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))
                .peek(t -> t.setValid(t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE))
                .filter(CurrentLawVotesEndBalance::isValid)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())
                .collect(Collectors.toList());


        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .peek(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    boolean firstCondition = t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean secondCondition = (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean additionalCondition = t.getVotesCorporateCouncilOfRefereesNo() < VETO_THRESHOLD;

                    boolean isValid = (firstCondition || secondCondition) && additionalCondition;
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтрация только валидных объектов
                .collect(Collectors.toList());

        //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //позиции избираемые советом директоров
        List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()
                .filter(t -> directors.isofficeOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .peek(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    boolean firstCondition = t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean secondCondition = (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean additionalCondition = t.getVotesCorporateCouncilOfRefereesNo() < VETO_THRESHOLD;

                    boolean isValid = (firstCondition || secondCondition) && additionalCondition;
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
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


        List<CurrentLawVotesEndBalance> electedByGeneralExecutiveDirector = electedByBoardOfDirectors.stream()
                .filter(t -> directors.isElectedCEO(t.getPackageName()))
                .filter(t -> NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString().equals(t.getPackageName()))
                .peek(t -> {
                    boolean isValid = t.getVoteGeneralExecutiveDirector() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_GENERAL_EXECUTIVE_DIRECTOR;
                    t.setValid(isValid); // Устанавливаем поле isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVoteGeneralExecutiveDirector))
                .collect(Collectors.toList());


        List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> !Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .peek(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    boolean firstCondition = t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean secondCondition = (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean additionalCondition = t.getVotesCorporateCouncilOfRefereesNo() < VETO_THRESHOLD;

                    boolean isValid = (firstCondition || secondCondition) && additionalCondition;
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


        List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .peek(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    boolean firstCondition = t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT;
                    boolean secondCondition = (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT;
                    boolean additionalCondition = t.getVotesCorporateCouncilOfRefereesNo() < VETO_THRESHOLD;

                    boolean isValid = (firstCondition || secondCondition) && additionalCondition;
                    t.setValid(isValid); // Установка поля isValid
                })

                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());

        List<String> positions = directors.getDirectors().stream().map(t->t.getName()).collect(Collectors.toList());
        // Добавляет законы, которые создают новые должности, утверждается всеми
        List<CurrentLawVotesEndBalance> addDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .peek(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    boolean firstCondition = t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean secondCondition = (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean additionalCondition = t.getVotesCorporateCouncilOfRefereesNo() < VETO_THRESHOLD;

                    boolean isValid = (firstCondition || secondCondition) && additionalCondition;
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Оставляем только валидные объекты
                .collect(Collectors.toList());

        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }
        positions.addAll(directors.getNames());
        positions = positions.stream().distinct().collect(Collectors.toList());

        // Определение позиций, которые требуют соответствия отправителя и первой строки
        // Поскольку все позиции требуют соответствия, senderMatchPositions = positions
        List<String> senderMatchPositions = new ArrayList<>(positions);

        // План утверждается всеми
        List<CurrentLawVotesEndBalance> planFourYears = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.STRATEGIC_PLAN.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .peek(t -> {
                    double totalFractionsRating = t.getFractionsRaiting().values().stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();

                    boolean firstCondition = t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean secondCondition = (t.getVotes() / totalFractionsRating) * Seting.HUNDRED_PERCENT
                            >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_PERCENT;
                    boolean additionalCondition = t.getVotesCorporateCouncilOfRefereesNo() < VETO_THRESHOLD;

                    boolean isValid = (firstCondition || secondCondition) && additionalCondition;
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());


        //устав всегда действующий он подписан основателем
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .peek(t -> {
                    boolean isValid = t.getFounderVote() >= 1; // Проверка условия для валидации
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());


        //ИСХОДНЫЙ КОД СОЗДАННЫЙ ОСНОВАТЕЛЕМ
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL_CODE = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .peek(t -> {
                    boolean isValid = t.getFounderVote() >= 1; // Проверка условия для валидации
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());


        List<CurrentLawVotesEndBalance> charterCheckBlock = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .peek(t -> {
                    boolean isValid = t.getFounderVote() >= 1; // Условие валидации
                    t.setValid(isValid); // Установка значения поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());


        CHARTER_ORIGINAL.addAll(charterCheckBlock);


        List<CurrentLawVotesEndBalance> charterOriginalCode = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .peek(t -> {
                    boolean isValid = t.getFounderVote() >= 1; // Условие валидации
                    t.setValid(isValid); // Установка поля isValid
                })
                .filter(CurrentLawVotesEndBalance::isValid) // Фильтруем только валидные объекты
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());




        CHARTER_ORIGINAL_CODE.addAll(charterOriginalCode);
        for (Map.Entry<Director, List<CurrentLawVotesEndBalance>> higherSpecialPositionsListMap : original_group.entrySet()) {
            current.addAll(higherSpecialPositionsListMap.getValue());
        }

        current.addAll(CORPORATE_COUNCIL_OF_REFEREES);
        current.addAll(addDirectors);

        current.addAll(electedBoardOfDirectors);
        current.addAll(planFourYears);

        current.addAll(electedByBoardOfDirectors);
        current.addAll(electedByGeneralExecutiveDirector);
        current.addAll(notEnoughVotes);
        current.addAll(CHARTER_ORIGINAL);
        current.addAll(CHARTER_ORIGINAL_CODE);
        current.addAll(chapter_amendment);
        current = current.stream()
                .filter(UtilsUse.distinctByKey(CurrentLawVotesEndBalance::getAddressLaw))
                .collect(Collectors.toList());

        List<String> finalPositions = positions;
        current.forEach(item -> {
            boolean isInPositions = finalPositions.contains(item.getPackageName());
            item.setPosition(isInPositions);
        });




        return current;
    }

}
