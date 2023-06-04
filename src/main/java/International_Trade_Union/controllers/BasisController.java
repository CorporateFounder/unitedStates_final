package International_Trade_Union.controllers;

import International_Trade_Union.entity.AddressUrl;
import International_Trade_Union.entity.SubBlockchainEntity;
import org.json.JSONException;

import org.springframework.http.MediaType;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.EntityChain;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.User;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.vote.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BasisController {
    private static Blockchain blockchain;

    private static Set<String> excludedAddresses = new HashSet<>();

    public static HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null)
            return null;
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
        return servletRequest;
    }


    public static Set<String> getExcludedAddresses() {
        HttpServletRequest request = getCurrentRequest();
        if(request == null)
            return excludedAddresses;

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();  // includes leading forward slash

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

    /**Возвращает список хостов*/
    public static Set<String> getNodes() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        nodes = new HashSet<>();

        Set<String> temporary = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);


        nodes.addAll(temporary);


        nodes = nodes.stream()
                .filter(t -> !t.isBlank())
                .filter(t -> t.startsWith("\""))
                .collect(Collectors.toSet());
        nodes = nodes.stream().map(t -> t.replaceAll("\"", "")).collect(Collectors.toSet());
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
        return nodes;
    }


    /**Возвращяет действующий блокчейн*/
    public static Blockchain getBlockchain() {
        return blockchain;
    }

    public static synchronized void setBlockchain(Blockchain blockchain) {
        BasisController.blockchain = blockchain;
    }


    static {
        try {
            blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    public BasisController() {
    }

    //TODO если вы прервали mine, то перед следующим вызовом перезапустите сервер и вызовите /addBlock перед mine
    //TODO if you interrupted mine, restart the server before next call and call /addBlock before mine
    //TODO иначе будет расождение в файле балансов
    //TODO otherwise there will be a discrepancy in the balance file



    @GetMapping("constantMining")
    public void alwaysMining() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        for (int i = 0; i < 100 ; i++) {
            mining();
        }
    }
    public synchronized String mining() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        String text = "";
        //нахождение адрессов
        findAddresses();
        sendAddress();

        //собирает класс список балансов из файла расположенного по пути Seting.ORIGINAL_BALANCE_FILE
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        //собирает объект блокчейн из файла
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        //если блокчейн работает то продолжить
        if (!blockchain.validatedBlockchain()) {
            text = "wrong chain: неправильный блокчейн, добыча прекращена";
//            model.addAttribute("text", text);
            return "wrong blockchain";
        }

        //Прежде чем добыть новый блок сначала в сети ищет самый длинный блокчейн
        resolve_conflicts();

        //если размер блокчейна меньше или равно единице, сохранить в файл генезис блок
        long index = blockchain.sizeBlockhain();
        if (blockchain.sizeBlockhain() <= 1) {
            //сохранение генезис блока
            if (blockchain.sizeBlockhain() == 1) {
                UtilsBlock.saveBLock(blockchain.getBlock(0), Seting.ORIGINAL_BLOCKCHAIN_FILE);
            }

            //получить список балансов из файла
            balances = Mining.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain, balances);
            //удалить старые файлы баланса
            Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
            //сохранить балансы
            SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);

        }
        //скачать список балансов из файла
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

        //получить счет майнера
        Account miner = balances.get(User.getUserAddress());
        if (miner == null) {
            //если в блокчейне не было баланса, то баланс равен нулю
            miner = new Account(User.getUserAddress(), 0, 0);
        }

        //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions
        List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();

        //раз в три для очищяет файлы в папке resources/sendedTransaction данная папка
        //хранит уже добавленые в блокчейн транзации, чтобы повторно не добавлять в
        //в блок уже добавленные транзакции
//        AllTransactions.clearAllSendedTransaction(index);

        AllTransactions.clearUsedTransaction(AllTransactions.getInsanceSended());
        System.out.println("BasisController: start mine:");



        //Сам процесс Майнинга
        //DIFFICULTY_ADJUSTMENT_INTERVAL как часто происходит коррекция
        //BLOCK_GENERATION_INTERVAL как часто должен находить блок
        //temporaryDtoList добавляет транзакции в блок
        Block block = Mining.miningDay(
                miner,
                blockchain,
                Seting.BLOCK_GENERATION_INTERVAL,
                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL,
                temporaryDtoList,
                balances,
                index
        );
        System.out.println("BasisController: finish mine:");
        //save sended transaction
        //сохранить уже добавленные в блок транзакции,
        //чтобы избежать повторного добавления
        AllTransactions.addSendedTransaction(temporaryDtoList);

        //нужна для корректировки сложности
        int diff = Seting.DIFFICULTY_ADJUSTMENT_INTERVAL;
        //Тестирование блока
        List<Block> testingValidationsBlock = null;

        if (blockchain.sizeBlockhain() > diff) {

            testingValidationsBlock = blockchain.subBlock(blockchain.sizeBlockhain() - diff, blockchain.sizeBlockhain());
        } else {
            testingValidationsBlock = blockchain.clone();
        }
        //проверяет последние 288 блоков на валидность.
        if (testingValidationsBlock.size() > 1) {
            boolean validationTesting = UtilsBlock.validationOneBlock(
                    blockchain.genesisBlock().getFounderAddress(),
                    testingValidationsBlock.get(testingValidationsBlock.size() - 1),
                    block,
                    Seting.BLOCK_GENERATION_INTERVAL,
                    diff,
                    testingValidationsBlock);

            if (validationTesting == false) {
                System.out.println("wrong validation block: " + validationTesting);
                System.out.println("index block: " + block.getIndex());
                text = "wrong validation";
            }
            testingValidationsBlock.add(block.clone());
        }

        //добавляет последний блок в блокчейн
        blockchain.addBlock(block);
        //сохраняет последний блок в файл
        UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        //перерасчет балансов, подсчитывает какие изменения произошли в балансах
        balances = Mining.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain, balances);
        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
        //сохраняет в файл уже заново посчитанные балансы.
        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);

        //получает все созданные когда либо законы
        Map<String, Laws> allLaws = UtilsLaws.getLaws(blockchain.getBlockchainList(), Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);

        //возвращает все законы с голосами проголосовавшими за них
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        //удаление устаревних законов
        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        //записывает все законы в файл с их голосами.
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        //отправляет блокчейн во внешние сервера
        sendAllBlocksToStorage(blockchain.getBlockchainList());

        text = "success: блок успешно добыт";
//        model.addAttribute("text", text);
        return "ok";
    }

    /**Стартует добычу, начинает майнинг*/
    @GetMapping("/mine")
    public synchronized String mine(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining();
        return "redirect:/mining";

    }
    @GetMapping("/miningblock")
    public synchronized ResponseEntity minings() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        mining();
        return new ResponseEntity("OK", HttpStatus.OK);
    }


    /**Возвращает EntityChain который хранит в себе размер блокчейна и список блоков*/
    @GetMapping("/chain")
    @ResponseBody
    public EntityChain full_chain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return new EntityChain(blockchain.sizeBlockhain(), blockchain.getBlockchainList());
    }

    /**возвращяет размер локального блокчейна*/
    @GetMapping("/size")
    @ResponseBody
    public Integer sizeBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return blockchain.sizeBlockhain();
    }


    /**Возвращает список блоков ОТ до ДО,*/
    @PostMapping("/sub-blocks")
    @ResponseBody
    public List<Block> subBlocks(@RequestBody SubBlockchainEntity entity) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return blockchain.getBlockchainList().subList(entity.getStart(), entity.getFinish());
    }

    /**Возвращяет блок по индексу*/
    @PostMapping("/block")
    @ResponseBody
    public Block getBlock(@RequestBody Integer index) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return blockchain.getBlock(index);
    }

    //TODO нужно чтобы передавался каждый раз не весь блокчейн а часть, как реализованно в биткоин
    //TODO is necessary so that not the entire blockchain is transmitted each time, but a part, as implemented in bitcoin
    //TODO need to optimization because now not best

    @RequestMapping("/resolving")
    public String resolving() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        resolve_conflicts();
        return "redirect:/";
    }
    /**соединяется к внешним хостам, и скачивает самый длинный блокчейн,
     * если, локальный блокчейн, меньше других */
    @GetMapping("/nodes/resolve")
    public synchronized ResponseEntity resolve_conflicts() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        Blockchain temporaryBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        Blockchain bigBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        int blocks_current_size = blockchain.sizeBlockhain();
        long hashCountZeroTemporary = 0;
        long hashCountZeroBigBlockchain = 0;
        EntityChain entityChain = null;

        long hashCountZeroAll = 0;
        //count hash start with zero all
        for (Block block : blockchain.getBlockchainList()) {
            hashCountZeroAll += UtilsUse.hashCount(block.getHashBlock());
        }

        Set<String> nodesAll = getNodes();
//        nodesAll.addAll(Seting.ORIGINAL_ADDRESSES_BLOCKCHAIN_STORAGE);
        System.out.println("BasisController: resolve: size: " + getNodes().size());
        for (String s : nodesAll) {
            System.out.println("BasisController: resove: address: " + s);
            String temporaryjson = null;

            if (BasisController.getExcludedAddresses().contains(s)) {
                System.out.println("its your address or excluded address: " + s);
                continue;
            }
            try {
                if(s.contains("localhost") || s.contains("127.0.0.1"))
                    continue;
                String address = s + "/chain";
                System.out.println("BasisController:resolve conflicts: address: " + s + "/size");
                String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                Integer size = Integer.valueOf(sizeStr);
                if (size > blocks_current_size) {
                    System.out.println("size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                    //Test start algorithm
                    SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);
                    String subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);

                    List<Block> emptyList = new ArrayList<>();


                    List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                    emptyList.addAll(subBlocks);
                    emptyList.addAll(blockchain.getBlockchainList());

                    emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                    temporaryBlockchain.setBlockchainList(emptyList);
                    if (!temporaryBlockchain.validatedBlockchain()) {
                        System.out.println("first algorithm not worked");
                        emptyList = new ArrayList<>();
                        emptyList.addAll(subBlocks);
                        for (int i = blockchain.sizeBlockhain() - 1; i > 0; i--) {
                            Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(i), s + "/block"));
                            if (!blockchain.getBlock(i).getHashBlock().equals(block.getHashBlock())) {
                                emptyList.add(block);
                            } else {
                                emptyList.add(block);
                                emptyList.addAll(blockchain.getBlockchainList().subList(0, i));
                                emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                                temporaryBlockchain.setBlockchainList(emptyList);
                                break;
                            }
                        }
                    }
                    if (!temporaryBlockchain.validatedBlockchain()) {
                        System.out.println("second algorith not worked");
                        temporaryjson = UtilUrl.readJsonFromUrl(address);
                        entityChain = UtilsJson.jsonToEntityChain(temporaryjson);
                        temporaryBlockchain.setBlockchainList(
                                entityChain.getBlocks().stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList()));
                    }
                } else {
                    System.out.println("BasisController: resove: size less: " + size + " address: " + address);
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("BasisController: resolve_conflicts: Error: " + s);
                continue;
            }


            if (temporaryBlockchain.validatedBlockchain()) {
                for (Block block : temporaryBlockchain.getBlockchainList()) {
                    hashCountZeroTemporary += UtilsUse.hashCount(block.getHashBlock());
                }

                if (blocks_current_size < temporaryBlockchain.sizeBlockhain() && hashCountZeroAll < hashCountZeroTemporary) {
                    blocks_current_size = temporaryBlockchain.sizeBlockhain();
                    bigBlockchain = temporaryBlockchain;
                    hashCountZeroBigBlockchain = hashCountZeroTemporary;
                }
                hashCountZeroTemporary = 0;
            }

        }


        if (bigBlockchain.sizeBlockhain() > blockchain.sizeBlockhain() && hashCountZeroBigBlockchain > hashCountZeroAll) {

            blockchain = bigBlockchain;
            UtilsBlock.deleteFiles();
            addBlock(bigBlockchain.getBlockchainList(), BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL));
            System.out.println("BasisController: resolve: bigblockchain size: " + bigBlockchain.sizeBlockhain());

        }
        return new ResponseEntity(HttpStatus.OK);
    }


    /**
     * Перезаписывает весь список блоков, и делает перерасчет баланса, а также других данных
     * таких как голоса, совет акционеров и т.д. заново записывает в файлы
     */

    public static void addBlock(List<Block> orignalBlocks, Blockchain blockchain) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        Map<String, Account> balances = new HashMap<>();
        Blockchain temporaryForValidation = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        temporaryForValidation.setBlockchainList(orignalBlocks);
        UtilsBlock.deleteFiles();
        System.out.println("addBlock start");
        for (Block block : orignalBlocks) {
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }

        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        //перерасчет после добычи
        balances = UtilsBalance.calculateBalances(blockchain.getBlockchainList());
        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);


        //получение и отображение законов, а также сохранение новых законов
        //и изменение действующих законов
        Map<String, Laws> allLaws = UtilsLaws.getLaws(blockchain.getBlockchainList(), Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);


        //возвращает все законы с балансом
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        //удаление устаревних законов
        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        System.out.println("BasisController: addBlock: finish");
    }

    /**Регистрирует новый внешний хост*/
    @RequestMapping(method = RequestMethod.POST, value = "/nodes/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public synchronized void register_node(@RequestBody AddressUrl urlAddrress) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


        for (String s : BasisController.getNodes()) {
            String original = s;
            String url = s + "/nodes/register";

            try {
                UtilUrl.sendPost(urlAddrress.getAddress(), url);
                sendAddress();


            } catch (Exception e) {
                System.out.println("BasisController: register node: wrong node: " + original);
                BasisController.getNodes().remove(original);
                continue;
            }
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

    //TODO если происходить майнинг, то он возвращает false, пока не прекратиться майнинг.
    //TODO if mining occurs, it returns false until mining stops.
    /** выззывает метод addBlock который перезаписывает весь список блоков, и другие данные*/
    @GetMapping("/addBlock")
    public ResponseEntity getBLock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        UtilsBlock.deleteFiles();
        addBlock(blockchain.getBlockchainList(), BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL));
        return new ResponseEntity(HttpStatus.OK);
    }

    /**Возвращяет список хостов, сохраненных на локальном сервере*/
    @GetMapping("/getNodes")
    public Set<String> getAllNodes() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Set<String> temporary = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        nodes.addAll(temporary);
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
        nodes = nodes.stream().filter(t -> t.startsWith("\""))
                .collect(Collectors.toSet());
        return nodes;
    }

    /**подключается к другим узлам и у них берет их списки хостов, которые храняться у них,
     *  и сохраняет эти списки у себя*/
    @GetMapping("/findAddresses")
    public void findAddresses() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            Set<String> addressesSet = new HashSet<>();
            try {
                String addresses = UtilUrl.readJsonFromUrl(s + "/getDiscoveryAddresses");
                addressesSet = UtilsJson.jsonToSetAddresses(addresses);
            } catch (IOException e) {
                System.out.println("BasisController: findAddress: error");
                continue;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            for (String s1 : addressesSet) {

                register_node(new AddressUrl(s1));
            }

        }

    }

    /**Запускает автоматический цикл майнинга, цикл будет идти 2000 шагов*/
    @GetMapping("/moreMining")
    public void moreMining() throws JSONException, IOException {
        for (int i = 1; i < 2000; i++) {
            System.out.println("block generate i: " + i);
            UtilUrl.readJsonFromUrl("http://localhost:8082/mine");


        }
    }


    /**Отправляет свой список хостов, другим узлам, и пытается автоматически регистрировать у них*/
    public static void sendAddress() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        //лист временный для отправки аддресов

        for (String s : Seting.ORIGINAL_ADDRESSES) {

            String original = s;
            String url = s + "/nodes/register";

            if (BasisController.getExcludedAddresses().contains(url)) {
                System.out.println("MainController: its your address or excluded address: " + url);
                continue;
            }
            try {
                for (String s1 : BasisController.getNodes()) {


                    AddressUrl addressUrl = new AddressUrl(s1);
                    String json = UtilsJson.objToStringJson(addressUrl);
                    UtilUrl.sendPost(json, url);
                }
            } catch (Exception e) {
                System.out.println("BasisController: sendAddress: wronge node: " + original);

                continue;
            }


        }
    }

    //должен отправлять блокчейн в хранилище блокчейна
    /**Отправляет список блоков в центральные хранилища (пример: http://194.87.236.238:80)*/
    public static void sendAllBlocksToStorage(List<Block> blocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        String jsonDto;
        System.out.println("BasisController: sendAllBlocksToStorage: start: ");
        try {
            jsonDto = UtilsJson.objToStringJson(blocks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int blocks_current_size = blocks.size();
        //отправка блокчейна на хранилище блокчейна
        System.out.println("BasisController: sendAllBlocksToStorage: ");
        getNodes().stream().forEach(System.out::println);
        for (String s : getNodes()) {


            if (BasisController.getExcludedAddresses().contains(s)) {
                System.out.println("its your address or excluded address: " + s);
                continue;
            }

            try {
                System.out.println("BasisController:resolve conflicts: address: " + s + "/size");
                String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                Integer size =  0;
                if(Integer.valueOf(sizeStr) > 0)
                    size = Integer.valueOf(sizeStr);
                System.out.println("BasisController: send size: " + size);
                List<Block> fromToTempBlock = blocks.subList(size, blocks.size());
                String jsonFromTo = UtilsJson.objToStringJson(fromToTempBlock);
                //если блокчейн текущей больше чем в хранилище, то
                //отправить текущий блокчейн отправить в хранилище
                if (size < blocks_current_size) {
                    int response = 0;
                    //Test start algorithm
                    String originalF = s;
                    String urlFrom = s + "/nodes/resolve_from_to_block";
                    try {
                        response = UtilUrl.sendPost(jsonFromTo, urlFrom);
                    }catch (Exception e){
                        System.out.println("exception discover: " + originalF);
                        continue;
                    }

                    System.out.println("BasisController: sendAllBlocksStorage: response: " + response);

                    if(response != 0){
                        System.out.println("BasisController: sendAllBlocks: need change all: " + response);
                        //Test start algorithm
                        String original = s;
                        String url = s + "/nodes/resolve_all_blocks";
                        try {
                            UtilUrl.sendPost(jsonDto, url);

                        }catch (Exception e){
                            System.out.println("exception discover: " + original);
                            continue;

                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

        }

    }
}


