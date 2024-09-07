package International_Trade_Union.controllers;

import International_Trade_Union.entity.*;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.vote.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.utils.UtilsBalance.*;

@Controller
public class BasisController {

    private BlockService blockService;
    @Autowired
    public BasisController(BlockService blockService) {
        this.blockService = blockService;
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);
        initializeBlockchain();
    }



    @Autowired
    Mining miningS;
    @Autowired
    UtilsResolving utilsResolving;


    private static double minDollarRewards = 0;
    private static Block prevBlock = null;
    private static Account minerShow = null;
    private static long difficultExpected = 0;

    private static boolean mining = false;
    private static boolean updating = false;
    private static DataShortBlockchainInformation shortDataBlockchain = null;

    private static int blockchainSize = 0;
    private static boolean blockchainValid = false;
    private static Set<String> excludedAddresses = new HashSet<>();

    public BlockService getBlockService() {
        return blockService;
    }

    public void setBlockService(BlockService blockService) {
        this.blockService = blockService;
    }

    public static void setPrevBlock(Block prevBlock) {
        BasisController.prevBlock = prevBlock;
    }

    public static void setMining(boolean mining) {
        BasisController.mining = mining;
    }

    public static void setUpdating(boolean updating) {
        BasisController.updating = updating;
    }

    public static DataShortBlockchainInformation getShortDataBlockchain() {
        return shortDataBlockchain;
    }

    public static void setShortDataBlockchain(DataShortBlockchainInformation shortDataBlockchain) {
        BasisController.shortDataBlockchain = shortDataBlockchain;
    }

    public static void setBlockchainSize(int blockchainSize) {
        BasisController.blockchainSize = blockchainSize;
    }

    public static boolean isBlockchainValid() {
        return blockchainValid;
    }

    public static void setBlockchainValid(boolean blockchainValid) {
        BasisController.blockchainValid = blockchainValid;
    }

    /**
     * Возвращает последний блок который есть в локальном блокчейне.
     * Returns the last block that exists in the local room.
     */
    public static Block getPrevBlock() {
        return prevBlock;
    }

    public static long getDifficultExpected() {
        return difficultExpected;
    }

    public static void setDifficultExpected(long difficultExpected) {
        BasisController.difficultExpected = difficultExpected;
    }

    public static Account getMinerShow() {
        return minerShow;
    }

    public static double getMinDollarRewards() {
        return minDollarRewards;
    }

    public static void setMinDollarRewards(double minDollarRewards) {
        BasisController.minDollarRewards = minDollarRewards;
    }

    public static void setMinerShow(Account minerShow) {
        BasisController.minerShow = minerShow;
    }

    public static int getBlockchainSize() {
        return blockchainSize;
    }

    /**
     * informs if mining is happening now. информирует, происходит ли сейчас майнинг.
     */
    public static boolean isMining() {
        return mining;
    }

    /**
     * Informs whether the files are currently being updated. информирует, происходит ли сейчас обновление файлов.
     */
    public static boolean isUpdating() {
        return updating;
    }

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null)
            return null;
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
        return servletRequest;
    }

    /**
     * Используется в будущем, для подключения к разным серверам.
     * Used in the future to connect to different servers.
     */

    public static Set<String> getExcludedAddresses() {

        HttpServletRequest request = getCurrentRequest();
        if (request == null)
            return excludedAddresses;

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();


        String localaddress = scheme + "://" + serverName + ":" + serverPort;

        excludedAddresses.add(localaddress);
        return excludedAddresses;
    }

    public static void setExcludedAddresses(Set<String> excludedAddresses) {
        BasisController.excludedAddresses = excludedAddresses;
    }

    private static Set<String> nodes = new HashSet<>();
//    private static Nodes nodes = new Nodes();

    public static void setNodes(Set<String> nodes) {
        BasisController.nodes = nodes;
    }

    /**
     * returns a list of hosts to which you can connect automatically.
     * возвращает список хостов которому можно подключиться автоматически.
     */
    public static Set<String> getNodes() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        nodes = new HashSet<>();

        Set<String> temporary = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);


        nodes.addAll(temporary);

        nodes = nodes.stream().map(t -> t.replaceAll("\"", "")).collect(Collectors.toSet());
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        //removes blocked hosts. удаляет заблокированные хосты
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);

        //adds preset hosts. добавляет предустановные хосты
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);

        if (Seting.IS_TEST) {
            nodes = new HashSet<>();
            nodes.add("http://localhost:8083");
        }
        return nodes;
    }

    /**
     * similar to getNodes. аналогичен getNodes
     */
    @GetMapping("/getNodes")
    public Set<String> getAllNodes() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Set<String> temporary = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        nodes.addAll(temporary);
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
//        nodes = nodes.stream().filter(t -> t.startsWith("\""))
//                .collect(Collectors.toSet());
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);
        nodes = nodes.stream().map(t -> t.replaceAll("\"", "")).collect(Collectors.toSet());
        return nodes;
    }

    /**
     * Возвращает действующий блокчейн. Returns a valid blockchain
     */


    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);
        initializeBlockchain();
    }

    private void initializeBlockchain() {
        try {
            // Инициализация ресурсов
            UtilsCreatedDirectory.createPackages();

            // Загрузка блокчейна из файла
            String json = UtilsFileSaveRead.read(Seting.TEMPORARY_BLOCKCHAIN_FILE);
            if (!json.isEmpty() || !json.isBlank()) {
                shortDataBlockchain = UtilsJson.jsonToDataShortBlockchainInformation(json);
            } else {
                shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                json = UtilsJson.objToStringJson(shortDataBlockchain);
                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
            }
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();
            prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        } catch ( IOException  e) {
            MyLogger.saveLog("initializeBlockchain: ", e);
            throw new RuntimeException(e);
        }
    }


    public BasisController() {
    }

    /**
     * Returns an EntityChain which stores the size of the blockchain and the list of blocks
     * Возвращает EntityChain который, хранит в себе размер блокчейна и список блоков
     */


    /**
     * returns the size of the local blockchain
     * возвращает размер локального блокчейна
     */
    @GetMapping("/size")
    @ResponseBody
    public Integer sizeBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        utilsMethod();
        System.out.println(":sizeBlockchain: " + shortDataBlockchain);
        return blockchainSize;
    }

    /**
     * Returns a list of blocks from to to,
     * Возвращает список блоков ОТ до ДО,
     */
    @PostMapping("/sub-blocks")
    @ResponseBody
    public List<Block> subBlocks(@RequestBody SubBlockchainEntity entity) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        utilsMethod();
        return Blockchain.subFromFile(entity.getStart(), entity.getFinish(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }

    /**
     * Returns a block by index
     * Возвращает блок по индексу
     */
    @PostMapping("/block")
    @ResponseBody
    public Block getBlock(@RequestBody Integer index) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        utilsMethod();

        //        return blockchain.getBlock(index);
        return Blockchain.indexFromFile(index, Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }

    /**
     * connects to the storage and updates its internal blockchain.
     * подключается к хранилищу и обновляет свой внутренний блокчейн
     */
    @GetMapping("/nodes/resolve")
    public synchronized int resolve_conflicts() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        System.out.println(":resolve_conflicts");


//        int result = resovle2();
        int result = utilsResolving.resolve3();
        while (true) {
            result = utilsResolving.resolve3();
            if (result >= 0) {
                break;
            }
        }
        return result;
    }

    @RequestMapping("/resolving")
    public String resolving() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        resolve_conflicts();
        return "redirect:/";
    }


    //String jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
//                                System.out.println("jsonGlobalData: " + jsonGlobalData);
//                                DataShortBlockchainInformation global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
//                                temp = helpResolve3(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks);


    /**
     * TODO Устарел и нигде не используется. TODO Deprecated and not used anywhere.
     */

    public void addBlock2(List<Block> originalBlocks, Map<String, Account> balances) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {


        System.out.println(" addBlock2 start: ");

        List<String> signs = new ArrayList<>();
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        List<EntityBlock> entityBlocks = new ArrayList<>();
        //write a new blockchain from scratch to the resources folder
        //записать с нуля новый блокчейн в папку resources
        for (Block block : originalBlocks) {
            System.out.println(" :BasisController: addBlock2: blockchain is being updated: ");
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);
            EntityBlock entityBlock = UtilsBlockToEntityBlock.blockToEntityBlock(block);
            entityBlocks.add(entityBlock);
            calculateBalance(balances, block, signs);


        }
        blockService.saveAllBlock(entityBlocks);
        List<EntityAccount> entityBalances = UtilsAccountToEntityAccount
                .accountsToEntityAccounts(balances);
        blockService.saveAccountAll(entityBalances);


        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);

        //возвращает все законы с балансом
        allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances,
                Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //removal of obsolete laws
        //удаление устаревших законов
        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        //rewriting all existing laws
        //перезапись всех действующих законов
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);


        System.out.println(":BasisController: addBlock2: finish: " + originalBlocks.size());
    }


    /**
     * Если вы предполагаете что некоторые файлы повреждены, то после вызова данного метода, происходить
     * перерасчет всех файлов для всего блокчейна.
     * If you assume that some files are damaged, then after calling this method,
     * * recalculation of all files for the entire blockchain.
     */

    public void getBlock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        int size = 0;
        long startTime = UtilsTime.getUniversalTimestamp() / 1000;
        System.out.println("start get a block");
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        System.out.println("start delete file get block:");
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_BALANCE_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.BALANCE_REPORT_ON_DESTROYED_COINS);
        UtilsFileSaveRead.deleteAllFiles(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        UtilsFileSaveRead.deleteAllFiles(Seting.CURRENT_BUDGET_END_EMISSION);

//        UtilsFileSaveRead.deleteAllFiles(Seting.H2_DB);
//        UtilsCreatedDirectory.createPackage(Seting.H2_DB);
//        UtilsCreatedDirectory.createPackage(Seting.H2_DB+"db.mv.db");

        UtilsFileSaveRead.deleteFile(Seting.TEMPORARY_BLOCKCHAIN_FILE);
        System.out.println("finish delete file get block:");
        blockService.deletedAll();
        System.out.println("finish delete db get block:");
        List<Block> list = new ArrayList<>();

        Map<String, Account> balances = new HashMap<>();
        Map<String, Account> tempBalances = new HashMap<>();
        List<String> sign = new ArrayList<>();
        while (true) {
            if (size + Seting.PORTION_DOWNLOAD < blockchain.sizeBlockhain()) {
                list = blockchain.subBlock(size, size + Seting.PORTION_DOWNLOAD);
            } else {
                list = blockchain.subBlock(size, blockchain.sizeBlockhain());
            }

            List<Block> lastDiff = new ArrayList<>();
            if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY && BasisController.getBlockchainSize() < Seting.V34_NEW_ALGO) {
                lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                        blockService.findBySpecialIndexBetween(
                                (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                BasisController.getPrevBlock().getIndex() + 1
                        )
                );
            }

            list = list.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(list, blockService));
            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(list, blockService));
            DataShortBlockchainInformation temp = new DataShortBlockchainInformation();
            if (BasisController.getBlockchainSize() > 1){
                Map<String, Account> balanceForValidation = UtilsUse.balancesClone(balances);
                temp = Blockchain.shortCheck(BasisController.getPrevBlock(), list, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign, balanceForValidation);

                SlidingWindowManager windowManager = SlidingWindowManager.loadInstance(Seting.SLIDING_WINDOWS_BALANCE);
                boolean result = utilsResolving.addBlock3(list, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE, windowManager);
                if (result) {
                    BasisController.setShortDataBlockchain(temp);
                    BasisController.setBlockchainSize((int) temp.getSize());
                    BasisController.setBlockchainValid(temp.isValidation());

                    EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                    BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));

                    String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                    UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                }
            }else {
                SlidingWindowManager windowManager =  SlidingWindowManager.loadInstance(Seting.SLIDING_WINDOWS_BALANCE);
                boolean result = utilsResolving.addBlock3(list, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE, windowManager);
                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(list, blockService));
                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(list, blockService));
                if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY && BasisController.getBlockchainSize() < Seting.V34_NEW_ALGO) {
                    lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                            blockService.findBySpecialIndexBetween(
                                    (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                    BasisController.getPrevBlock().getIndex() + 1
                            )
                    );
                }
                Map<String, Account> balanceForValidation = UtilsUse.balancesClone(balances);
                temp = Blockchain.shortCheck(BasisController.getPrevBlock(), list, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign, balanceForValidation);

                BasisController.setShortDataBlockchain(temp);
                BasisController.setBlockchainSize((int) temp.getSize());
                BasisController.setBlockchainValid(temp.isValidation());

                EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));

                String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
            }

            if (list.size() < Seting.PORTION_DOWNLOAD) {
                break;
            }

            Block block = list.get(list.size() - 1);
            size = (int) block.getIndex() + 1;  // Обновляем размер для следующей итерации
        }

//
//        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
//        blockchainSize = (int) shortDataBlockchain.getSize();
//        blockchainValid = shortDataBlockchain.isValidation();
//        String json = UtilsJson.objToStringJson(shortDataBlockchain);
//        UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

        long finishTime = UtilsTime.getUniversalTimestamp() / 1000;
        System.out.println("time: result time: " + UtilsTime.timeToString(finishTime - startTime));
        System.out.println("finish get a block");
    }

    /**
     * overwrites the current blockchain in the resources folder.
     * производит перезапись текущего блокчейна в папку ресурсы
     */
    @GetMapping("/addBlock")
    @ResponseBody
    public ResponseEntity getBLock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        System.out.println("start recalculating blockchain");
        getBlock();
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * get a block. добыть блок
     */
    @GetMapping("/miningblock")
    public synchronized ResponseEntity minings() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        mining();
        return new ResponseEntity("OK", HttpStatus.OK);
    }


    /**
     * get a block. добыть блок
     */
    @GetMapping("/process-mining")
    public synchronized String proccessMining(Model model, Integer number) throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        mining();
        model.addAttribute("title", "mining proccess");
        return "redirect:/process-mining";
    }


    /**
     * blockchain update. обновление блокчейна
     */


    /**
     * Registers a new external host. Регистрирует новый внешний хост
     */
    @RequestMapping(method = RequestMethod.POST, value = "/nodes/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public static synchronized void register_node(@RequestBody AddressUrl urlAddrress) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


        for (String s : BasisController.getNodes()) {
            String original = s;
            String url = s + "/nodes/register";

//            try {
//                UtilUrl.sendPost(urlAddrress.getAddress(), url);
//                sendAddress();
//
//
//            } catch (Exception e) {
//                System.out.println(":BasisController: register node: wrong node: " + original);
//                BasisController.getNodes().remove(original);
//                continue;
//            }
        }

        Set<String> nodes = BasisController.getNodes();
        nodes = nodes.stream()
                .map(t -> t.replaceAll("\"", ""))
                .map(t -> t.replaceAll("\\\\", ""))
                .collect(Collectors.toSet());
        nodes.add(urlAddrress.getAddress());
        BasisController.setNodes(nodes);

        Mining.deleteFiles(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        nodes.stream().forEach(t -> {
            try {
                UtilsAllAddresses.saveAllAddresses(t, Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            } catch (NoSuchProviderException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

    }


    /**
     * connects to other nodes and takes their lists of hosts that are stored by them,
     * and keeps these lists at home. (currently partially disabled).
     * подключается к другим узлам и у них берет их списки хостов, которые хранятся у них,
     * и сохраняет эти списки у себя. (на данный момент частично отключен).
     */

    @GetMapping("/findAddresses")
    public static void findAddresses() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            Set<String> addressesSet = new HashSet<>();
            try {
                register_node(new AddressUrl(s));
                System.out.println(":start download addressses");
                System.out.println("trying to connect to the server:" + s + " timeout 45 seconds");
//                String addresses = UtilUrl.readJsonFromUrl(s + "/getDiscoveryAddresses");
//                addressesSet = UtilsJson.jsonToSetAddresses(addresses);
                System.out.println(":finish download addreses");
            } catch (IOException e) {
                System.out.println(":BasisController: findAddress: error");
                continue;
            }

        }

    }

    /**
     * Starts an automatic mining cycle, the cycle will go 2000 steps
     * Запускает автоматический цикл майнинга, цикл будет идти 2000 шагов
     */
    @GetMapping("/moreMining")
    public void moreMining() throws JSONException, IOException {
        for (int i = 1; i < 2000; i++) {
            System.out.println("block generate i: " + i);
            UtilUrl.readJsonFromUrl("http://localhost:8082/mine");


        }
    }


    /**
     * Sends its list of hosts to other nodes and tries to automatically register with them
     * Отправляет свой список хостов, другим узлам, и пытается автоматически регистрировать у них
     */
    public static void sendAddress() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        //лист временный для отправки аддресов

        for (String s : Seting.ORIGINAL_ADDRESSES) {
            System.out.println(new Date() + ":start send addreses");
            String original = s;
            String url = s + "/nodes/register";

            if (BasisController.getExcludedAddresses().contains(url)) {
                System.out.println(":MainController: its your address or excluded address: " + url);
                continue;
            }
            try {
                for (String s1 : BasisController.getNodes()) {

                    System.out.println(":trying to connect to the server: send addreses: " + s + ": timeout 45 seconds");
                    AddressUrl addressUrl = new AddressUrl(s1);
                    String json = UtilsJson.objToStringJson(addressUrl);
                    UtilUrl.sendPost(json, url);

                }
            } catch (Exception e) {
                System.out.println(":BasisController: sendAddress: wronge node: " + original + " address: " + s);

                continue;
            }


        }
        System.out.println(":finish send addressess");
    }


    /**
     * Sends a list of blocks to central stores (example: http://194.87.236.238:82)
     * Отправляет список блоков в центральные хранилища (пример: http://194.87.236.238:82)
     */
    public int sendAllBlocksToStorage(List<Block> blocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        System.out.println(new Date() + ":BasisController: sendAllBlocksToStorage: start: ");
        int bigsize = 0;
        int blocks_current_size = (int) blocks.get(blocks.size() - 1).getIndex() + 1;
        //отправка блокчейна на хранилище блокчейна
        System.out.println(":BasisController: sendAllBlocksToStorage: ");
        Set<String> nodesAll = getNodes();

        List<HostEndDataShortB> sortPriorityHost = utilsResolving.sortPriorityHost(nodesAll);

        getNodes().stream().forEach(System.out::println);
        for (HostEndDataShortB hostEndDataShortB : sortPriorityHost) {
            String s = hostEndDataShortB.getHost();
            String server = UtilsFileSaveRead.read(Seting.YOUR_SERVER);
            if (!server.isBlank() || !server.isEmpty()) {
                Seting.ORIGINAL_ADDRESSES.removeAll(Seting.ORIGINAL_ADDRESSES);
                Seting.ORIGINAL_ADDRESSES.add(server);
                s = server;
            }
            for (String s1 : Seting.ORIGINAL_ADDRESSES) {
                s = s1;
            }


            System.out.println(":trying to connect to the server send block: " + s + ": timeout 45 seconds");

            if (BasisController.getExcludedAddresses().contains(s)) {
                System.out.println(":its your address or excluded address: " + s);
                continue;
            }

            try {
                System.out.println(":BasisController:resolve conflicts: address: " + s + "/size");
                String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                Integer size = 0;
                if (Integer.valueOf(sizeStr) > 0)
                    size = Integer.valueOf(sizeStr);
                System.out.println(":BasisController: send: local size: " + blocks_current_size + " global size: " + size);
                if (size > blocks_current_size) {
                    System.out.println(":your local chain less: current: " + blocks_current_size + " global: " + size);
                    return -1;
                }
//                List<Block> fromToTempBlock = blocks.subList(size, blocks_current_size);
                List<Block> fromToTempBlock = new ArrayList<>();
                fromToTempBlock.addAll(blocks);
                SendBlocksEndInfo infoBlocks = new SendBlocksEndInfo(Seting.VERSION, fromToTempBlock);
                String jsonFromTo = UtilsJson.objToStringJson(infoBlocks);
                //if the current blockchain is larger than the storage, then
                //send current blockchain send to storage
                //если блокчейн текущей больше чем в хранилище, то
                //отправить текущий блокчейн отправить в хранилище
                if (size < blocks_current_size) {
                    if (bigsize < size) {
                        bigsize = size;
                    }
                    int response = -1;
                    //Test start algorithm
                    String originalF = s;
                    System.out.println(":send resolve_from_to_block");
                    String urlFrom = s + "/nodes/resolve_from_to_block";
                    try {
                        response = UtilUrl.sendPost(jsonFromTo, urlFrom);
                        System.out.println(":CONFLICT TREE, IN GLOBAL DIFFERENT TREE " + HttpStatus.CONFLICT.value());
                        System.out.println(":GOOD: SUCCESS  " + HttpStatus.OK.value());
                        System.out.println(":FAIL BAD BLOCKCHAIN: " + HttpStatus.EXPECTATION_FAILED.value());
                        System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                        System.out.println(":response: " + response + " address: " + s);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(":exception resolve_from_to_block: " + originalF);
                        continue;
                    }
                    System.out.println(":CONFLICT TREE, IN GLOBAL DIFFERENT TREE: " + HttpStatus.CONFLICT.value());
                    System.out.println(":GOOD SUCCESS: " + HttpStatus.OK.value());
                    System.out.println(":FAIL BAD BLOCKHAIN: " + HttpStatus.EXPECTATION_FAILED.value());
                    System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                    System.out.println(":NAME CONFLICT: " + HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println("two miner addresses cannot be consecutive: " + HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println("PARITY ERROR" + HttpStatus.LOCKED);
                    System.out.println("Test version: If the index is even, then the stock balance must also be even; if the index is not even, all can mining"
                            + HttpStatus.LOCKED.value());
                    System.out.println("BLOCK HAS CHEATER ADDRESS: " + HttpStatus.SEE_OTHER);
                    System.out.println(":response: " + response + " address: " + s);

                    System.out.println(":BasisController: sendAllBlocksStorage: response: " + response + " address: " + s);


                }


            } catch (JSONException e) {
                e.printStackTrace();
                continue;

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

        }
        if (blockchainSize > bigsize) {
            return 1;
        } else if (blockchainSize < bigsize) {
            return -1;
        } else if (blockchainSize == bigsize) {
            return 0;
        } else {
            return -4;
        }
    }

    /**
     * mine every 576 blocks. добывать каждые 576 блоков
     */
    @GetMapping("/constantMining")
    public String alwaysMining() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {

        while (true) {
            try {
                mining();
                if (Mining.isIsMiningStop()) {
                    System.out.println("production stopped");
                    Mining.setIsMiningStop(false);
                    break;
                }

            } catch (IllegalArgumentException e) {
                System.out.println("BasisisController: constantMining find error:");
                continue;
            } catch (IOException e) {
                System.out.println("BasisisController: constantMining find error: ");
                continue;
            }
        }
        return "redirect:/mining";
    }


    /**
     * Добывает блок и отправляет на сервер.
     * Mines a block and sends it to the server.
     */

    public synchronized String mining() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining = true;
        try {
            String server = UtilsFileSaveRead.read(Seting.YOUR_SERVER);
            if (!server.isBlank() || !server.isEmpty()) {
                Seting.ORIGINAL_ADDRESSES.removeAll(Seting.ORIGINAL_ADDRESSES);
                Seting.ORIGINAL_ADDRESSES.add(server);
            }
            findAddresses();
            resolve_conflicts();

            List<Block> tempBlockchain;

            tempBlockchain = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                    blockService.findBySpecialIndexBetween(
                            (blockchainSize) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                            blockchainSize - 1
                    )
            );

            Block prevBlock = tempBlockchain.get(tempBlockchain.size() - 1);
            long index = prevBlock.getIndex() + 1;
//            Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
            Map<String, Account> balances = new HashMap<>();

            EntityAccount entityAccount = blockService.findByAccount(User.getUserAddress());
            if(entityAccount == null){
                entityAccount = new EntityAccount(User.getUserAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
            Account miner =UtilsAccountToEntityAccount.entityAccountToAccount(entityAccount);
            minerShow = miner;

            String address = "http://194.87.236.238:80";
            for (String s : Seting.ORIGINAL_ADDRESSES) {
                address = s;
            }


            String sizeStr = "-1";
            try {
                sizeStr = UtilUrl.readJsonFromUrl(address + "/size");
            } catch (NoRouteToHostException e) {
                System.out.println("home page you cannot connect to global server," +
                        "you can't give size global server");
                sizeStr = "-1";
            } catch (SocketException e) {
                System.out.println("home page you cannot connect to global server," +
                        "you can't give size global server");
                sizeStr = "-1";
            }
            Integer sizeG = Integer.valueOf(sizeStr);
            String text = "";
            //нахождение адрессов


            if (blockchainSize % 288 == 0) {
                System.out.println("clear storage transaction because is old");
                AllTransactions.clearAllTransaction();
            }
            //считывает все балансы из файла.
            //reads all balances from a file.
//            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
//


            String json = UtilsFileSaveRead.read(Seting.TEMPORARY_BLOCKCHAIN_FILE);
            if (json != null && !json.isEmpty()) {
                shortDataBlockchain = UtilsJson.jsonToDataShortBlockchainInformation(json);
                blockchainSize = (int) shortDataBlockchain.getSize();
                blockchainValid = shortDataBlockchain.isValidation();
            }


            if (shortDataBlockchain == null) {
                System.out.println("shortBlockchainInformation null");
                shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                String stringJson = UtilsJson.objToStringJson(shortDataBlockchain);
                UtilsFileSaveRead.save(stringJson, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                shortDataBlockchain = UtilsJson.jsonToDataShortBlockchainInformation(json);
                blockchainSize = (int) shortDataBlockchain.getSize();
                blockchainValid = shortDataBlockchain.isValidation();
            }

            if (blockchainSize <= 1) {
                System.out.println("save genesis block");
                Blockchain blockchain1 = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
                //сохранение генезис блока. preservation of the genesis of the block.
                if (blockchainSize == 1) {
                    UtilsBlock.saveBLock(blockchain1.genesisBlock(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
                }

//                //получить список балансов из файла. get a list of balances from a file.
//                List<String> signs = new ArrayList<>();
//                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
//                balances = miningS.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain1, balances, signs);
//                //удалить старые файлы баланса. delete old balance files.
//                Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
//
//                //сохранить балансы. maintain balances.
//                SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);
//

            }
            //скачать список балансов из файла. download a list of balances from a file.
            System.out.println("BasisController: minining: read list balance");
//            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
//            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

            //получить счет майнера. get the miner's account.

            EntityAccount tempAccount = blockService.findByAccount(User.getUserAddress());
            if(tempAccount == null){
                tempAccount = new EntityAccount(User.getUserAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
            miner = UtilsAccountToEntityAccount.entityAccountToAccount(tempAccount);
            minerShow = miner;
            System.out.println("BasisController: mining: account miner: " + miner);
            if (miner == null) {
                //если в блокчейне не было баланса майнера, то баланс равен нулю.
                //if there was no miner balance in the blockchain, then the balance is zero.
                miner = new Account(User.getUserAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }

            //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions.
            //transactions that we added to the block and now need to be deleted from the file in the resources/transactions folder.
            List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();

            List<Block> temp
                    = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                    blockService.findBySpecialIndexBetween(
                            (blockchainSize) - Seting.CHECK_DTO,
                            blockchainSize - 1
                    )
            );
            //удаляет транзакции которые были ранее уже добавлены в блок.
            //deletes transactions that were previously added to the block.
            temporaryDtoList = UtilsBlock.validDto(temp, temporaryDtoList);
            //TODO убирает транзакции которые уже были добавлены в блокчейн
            temporaryDtoList = temporaryDtoList.stream()
                    .filter(t -> !blockService.existsBySign(t.getSign()))
                    .collect(Collectors.toList());


            //отказ от транзакций которые меньше данного вознаграждения, если количество транзакций на блок выше 1000.
            //rejection of transactions that are less than this reward, if the number of transactions per block is above 1000.
            temporaryDtoList = UtilsTransaction.reward(temporaryDtoList, minDollarRewards);

            //раз в три для очищает файлы в папке resources/sendedTransaction данная папка
            //хранит уже добавленные в блокчейн транзакции, чтобы повторно не добавлять в
            //блок уже добавленные транзакции.
            //every three times to clear files in the resources/sendedTransaction folder, this folder
            //stores transactions already added to the blockchain so as not to be added to it again
            //block of already added transactions.
            AllTransactions.clearUsedTransaction(AllTransactions.getInsanceSended());
            System.out.println("BasisController: start mine:");

            //Сам процесс Майнинга
            //DIFFICULTY_ADJUSTMENT_INTERVAL как часто происходит коррекция
            //BLOCK_GENERATION_INTERVAL как часто должен находить блок
            //temporaryDtoList добавляет транзакции в блок.
            //The mining process itself
            //DIFFICULTY_ADJUSTMENT_INTERVAL how often the correction occurs
            //BLOCK_GENERATION_INTERVAL how often the block should be found
            //temporaryDtoList adds transactions to the block.
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findByDtoAccounts(temporaryDtoList)));
            Block block = Mining.miningDay(
                    miner,
                    tempBlockchain,
                    Seting.BLOCK_GENERATION_INTERVAL,
                    Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                    temporaryDtoList,
                    balances,
                    index
            );

            //синхронизируется с глобальным сервером и если в глобальном сервере,
            //появился более актуальный блок, то прекращает добывать.
            //synchronizes with the global server and if in the global server,
            //a more relevant block appears, it stops mining.
            if (Mining.miningIsObsolete) {
                Mining.miningIsObsolete = false;
                System.out.println("This block has already been mined, we start a new cycle");
                return "ok";

            }
            //останавливает майнинг.
            // stops mining.
            if (Mining.isIsMiningStop()) {
                System.out.println("mining will be stopped");
                return "ok";
            }
            System.out.println("BasisController: finish mine:" + block.getIndex());

            //каждые 288 блоков происходит регулировка сложности.
            //every 288 blocks the difficulty is adjusted.
            int diff = Seting.DIFFICULTY_ADJUSTMENT_INTERVAL;
            List<Block> testingValidationsBlock = null;

            if (tempBlockchain.size() > diff) {
                testingValidationsBlock = Blockchain.clone(
                        tempBlockchain.size() - diff,
                        temp.size(), tempBlockchain
                );

            } else {
                testingValidationsBlock = Blockchain.clone(
                        0, tempBlockchain.size(), tempBlockchain
                );
            }

            String addresFounder = blockService.findBySpecialIndex(0).getFounderAddress();
            if (!block.getFounderAddress().equals(addresFounder)) {
                System.out.println("wrong address founder: ");
            }
            //проверяет блок на валидность, соответствует ли блок требованиям.
            //checks the block for validity, whether the block meets the requirements.
            List<Block> blocks = new ArrayList<>();
            blocks.add(prevBlock);
            blocks.add(block);
            Map<String,Account> balance = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(blocks, blockService));
            if (testingValidationsBlock.size() > 1) {
                boolean validationTesting = UtilsBlock.validationOneBlock(
                        addresFounder,
                        testingValidationsBlock.get(testingValidationsBlock.size() - 1),
                        block,
                        testingValidationsBlock,
                        blockService,
                        balance);

                if (validationTesting == false) {
                    System.out.println("wrong validation block: " + validationTesting);
                    System.out.println("index block: " + block.getIndex());
                    text = "wrong validation";

                }
                testingValidationsBlock.add(block.clone());
            }


            System.out.println("block to send: " + block.getIndex());
            List<Block> sends = new ArrayList<>();
            sends.add(block);
            System.out.println("hash: " + block.getHashBlock());
            //отправляет блоки на узел или узлы.
            //sends blocks to a node or nodes.
            sendAllBlocksToStorage(sends);


            //отправить адресса
//        sendAddress();
            text = "success: блок успешно добыт";

        } finally {
            mining = false;
            Mining.miningIsObsolete = false;
        }
        return "ok";
    }

    /**
     * Стартует добычу, начинает майнинг.
     * Mining starts, mining starts.
     */
    @GetMapping("/mine")
    public synchronized String mine(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining();

        return "redirect:/mining";

    }


    /**
     * Отображает информационное табло во время майнинга или обновления.
     * Displays an information board during mining or updating.
     */
    @GetMapping("/processUpdating")
    public String processUpdating(Model model) {
        model.addAttribute("isMining", isMining());
        model.addAttribute("isUpdating", isUpdating());
        if (minerShow == null) {
            model.addAttribute("address", "balance has not loaded yet");
            model.addAttribute("dollar", "balance has not loaded yet");
            model.addAttribute("stock", "balance has not loaded yet");
            model.addAttribute("stop", Mining.isIsMiningStop());

        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.################");
            String dollar = decimalFormat.format(minerShow.getDigitalDollarBalance());
            String stock = decimalFormat.format(minerShow.getDigitalStockBalance());
            model.addAttribute("address", minerShow.getAccount());
            model.addAttribute("dollar", dollar);
            model.addAttribute("stock", stock);
            model.addAttribute("stop", Mining.isIsMiningStop());
            model.addAttribute("expectedDifficult", difficultExpected);

        }

        if (difficultExpected == 0) {
            model.addAttribute("expectedDifficult", "");
        } else {
            model.addAttribute("expectedDifficult", difficultExpected);
        }


        return "processUpdating";
    }

    /**
     * Отображает информационное табло и прекращает майнинг.
     * Displays an information board and stops mining.
     */
    @GetMapping("/stopMining")
    public String stopMining(RedirectAttributes model) {
        //Отключает майнинг.
        //Disables mining.
        Mining.setIsMiningStop(true);
        model.addAttribute("isMining", isMining());
        model.addAttribute("isUpdating", isUpdating());
        if (minerShow == null) {
            model.addAttribute("address", "balance has not loaded yet");
            model.addAttribute("dollar", "balance has not loaded yet");
            model.addAttribute("stock", "balance has not loaded yet");

            model.addAttribute("stop", Mining.isIsMiningStop() + " mining will be stopped, as file updates will stop. You can click on the main page, if mining is disabled then you will see it.");
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("#.################");
            String dollar = decimalFormat.format(minerShow.getDigitalDollarBalance());
            String stock = decimalFormat.format(minerShow.getDigitalStockBalance());
            model.addAttribute("address", minerShow.getAccount());
            model.addAttribute("dollar", dollar);
            model.addAttribute("stock", stock);

            model.addAttribute("stop", Mining.isIsMiningStop() + " mining will be stopped, as file updates will stop. You can click on the main page, if mining is disabled then you will see it.");

        }

        if (difficultExpected == 0) {
            model.addAttribute("expectedDifficult", "");
        } else {
            model.addAttribute("expectedDifficult", difficultExpected);
        }
        return "redirect:/processUpdating";
    }


    /**
     * Инициализирует мета данные о внутреннем блокчейне. Включая размер, блокчейна, целость и общую сложность.
     * Initializes meta data about the internal blockchain. Including size, blockchain, integrity and overall complexity.
     */
    public static boolean utilsMethod() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        boolean result = false;
        if (shortDataBlockchain.getSize() == 0
                || !shortDataBlockchain.isValidation()
                || shortDataBlockchain.getHashCount() == 0
                || prevBlock == null) {

            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);


            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();

            result = true;
        }
        return result;
    }


    @GetMapping("/testCalculate")
    @ResponseBody
    public String testResolving() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        utilsResolving.resolve3();
        String server = UtilsFileSaveRead.read(Seting.YOUR_SERVER);
        String s = "http://194.87.236.238:82";

        for (String s1 : Seting.ORIGINAL_ADDRESSES) {
            s = s1;
        }
        if (!server.isBlank() || !server.isEmpty()) {
            Seting.ORIGINAL_ADDRESSES.removeAll(Seting.ORIGINAL_ADDRESSES);
            Seting.ORIGINAL_ADDRESSES.add(server);
            s = server;

        }


        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Memory, used object remains balances: " + (afterMemory - beforeMemory) + "bytes");
        Map<String, Account> accounts = UtilsJson.balances(UtilUrl.readJsonFromUrl(s + "/addresses"));


        System.out.println("=========================================");
        System.out.println("all balance in server "+balances.size());
        System.out.println("=========================================");
        System.out.println("all balance in local "+accounts.size());

        System.out.println("=========================================");
        System.out.println("=========================================");
        Map<String, Account> result = UtilsUse.differentAccount(balances, accounts);
        result = result.entrySet().stream()
                .filter(t -> t.getValue().getDigitalStakingBalance() != null
                        && t.getValue().getDigitalStockBalance() != null
                        && t.getValue().getDigitalDollarBalance() != null
                        && (t.getValue().getDigitalStakingBalance().compareTo(BigDecimal.ZERO) != 0
                        || t.getValue().getDigitalStockBalance().compareTo(BigDecimal.ZERO) != 0
                        || t.getValue().getDigitalDollarBalance().compareTo(BigDecimal.ZERO) != 0))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println("wrong account: " + result.size());
        System.out.println(result);
        System.out.println("=========================================");
        balances =  balances.entrySet().stream()
                .filter(t -> t.getValue().getDigitalStakingBalance() != null
                        && t.getValue().getDigitalStockBalance() != null
                        && t.getValue().getDigitalDollarBalance() != null
                        && (t.getValue().getDigitalStakingBalance().compareTo(BigDecimal.ZERO) != 0
                        || t.getValue().getDigitalStockBalance().compareTo(BigDecimal.ZERO) != 0
                        || t.getValue().getDigitalDollarBalance().compareTo(BigDecimal.ZERO) != 0))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
            System.out.println(balances.get(accountEntry.getKey()));
        }

        System.out.println("=========================================");
        accounts = accounts.entrySet().stream().filter(t -> t.getValue().getDigitalStakingBalance() != null
                        && t.getValue().getDigitalStockBalance() != null
                        && t.getValue().getDigitalDollarBalance() != null
                        && (t.getValue().getDigitalStakingBalance().compareTo(BigDecimal.ZERO) != 0
                        || t.getValue().getDigitalStockBalance().compareTo(BigDecimal.ZERO) != 0
                        || t.getValue().getDigitalDollarBalance().compareTo(BigDecimal.ZERO) != 0))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
            System.out.println(accounts.get(accountEntry.getKey()));
        }


        return "result: " + (balances.equals(accounts) && result.size() == 0);

    }

    @GetMapping("/showAccounts")
    @ResponseBody
    public List<Map<String, Account>> showAccounts() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        List<Map<String, Account>> maps = new ArrayList<>();
        utilsResolving.resolve3();
        String server = UtilsFileSaveRead.read(Seting.YOUR_SERVER);
        String s = "http://194.87.236.238:82";

        for (String s1 : Seting.ORIGINAL_ADDRESSES) {
            s = s1;
        }
        if (!server.isBlank() || !server.isEmpty()) {
            Seting.ORIGINAL_ADDRESSES.removeAll(Seting.ORIGINAL_ADDRESSES);
            Seting.ORIGINAL_ADDRESSES.add(server);
            s = server;

        }


        long beforeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
        long afterMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Memory, used object remains balances: " + (afterMemory - beforeMemory) + "bytes");
        Map<String, Account> accounts = UtilsJson.balances(UtilUrl.readJsonFromUrl(s + "/addresses"));


        System.out.println("=========================================");
        System.out.println("all balance in server "+balances.size());
        balances.entrySet().stream().forEach(t-> System.out.println(t.getValue()));
        System.out.println("=========================================");
        System.out.println("all balance in local "+accounts.size());
        accounts.entrySet().stream().forEach(t-> System.out.println(t.getValue()));
        maps.add(balances);
        maps.add(accounts);

        return maps;
    }
    @GetMapping("/status")
    @ResponseBody
    public String status() {
        String result = "";
        try {

            String strBlockchainSize = "blockchainSize: " + getBlockchainSize() + "\n";

            String blockFromDb =
                    "blockFromDb: " + String.valueOf(blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1))
                            + "\n";
            String blockFromFile = "*********************************\nblockFromFile: " + Blockchain.indexFromFileBing(BasisController.blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE)
                    + "\n";


            result = strBlockchainSize + blockFromDb + blockFromFile;

            result += "**********************************************\n";

            result += "**********************************************\n";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


        return result;
    }

    @GetMapping("/testJson")
    @ResponseBody
    public String testJson() throws IOException {
        return "";
    }
}


