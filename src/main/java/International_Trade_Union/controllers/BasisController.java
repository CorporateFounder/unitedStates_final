package International_Trade_Union.controllers;

import International_Trade_Union.entity.*;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import org.json.JSONException;

import org.springframework.http.MediaType;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
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

import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BasisController {
    private static Account minerShow = null;
    private static boolean mining = false;
    private static boolean updating = false;
    private static DataShortBlockchainInformation shortDataBlockchain = null;
    private static Blockchain tempBlockchain;
    private static Blockchain blockchain;
    private static int blockchainSize = 0;
    private static boolean blockchainValid = false;
    private static Set<String> excludedAddresses = new HashSet<>();

    public static int getBlockchainSize() {
        return blockchainSize;
    }

    public static boolean isMining() {
        return mining;
    }

    public static boolean isUpdating() {
        return updating;
    }

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
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
        return nodes;
    }

    @GetMapping("/getNodes")
    public Set<String> getAllNodes() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Set<String> temporary = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        nodes.addAll(temporary);
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
        nodes = nodes.stream().filter(t -> t.startsWith("\""))
                .collect(Collectors.toSet());
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);
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
        try {  UtilsCreatedDirectory.createPackages();
            blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
            blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();



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

    /**Возвращает EntityChain который хранит в себе размер блокчейна и список блоков*/
    @GetMapping("/chain")
    @ResponseBody
    public EntityChain full_chain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(blockchainValid == false || blockchainSize == 0){
            blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();
        }

        return new EntityChain(blockchainSize, blockchain.getBlockchainList());
    }

    /**возвращяет размер локального блокчейна*/
    @GetMapping("/size")
    @ResponseBody
    public Integer sizeBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

           blockchain = Mining.getBlockchain(
                   Seting.ORIGINAL_BLOCKCHAIN_FILE,
                   BlockchainFactoryEnum.ORIGINAL);
           shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
           blockchainSize = (int) shortDataBlockchain.getSize();
           blockchainValid = shortDataBlockchain.isValidation();
        System.out.println(":sizeBlockchain: " + shortDataBlockchain);

        return blockchainSize;
    }

    /**Возвращает список блоков ОТ до ДО,*/
    @PostMapping("/sub-blocks")
    @ResponseBody
    public List<Block> subBlocks(@RequestBody SubBlockchainEntity entity) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(blockchainValid == false || blockchainSize == 0){
            blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
        }

//        return blockchain.getBlockchainList().subList(entity.getStart(), entity.getFinish());
        return Blockchain.subFromFile(entity.getStart(), entity.getFinish(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }
    /**Возвращяет блок по индексу*/
    @PostMapping("/block")
    @ResponseBody
    public Block getBlock(@RequestBody Integer index) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
       if(blockchainValid == false || blockchainSize == 0){
           blockchain = Mining.getBlockchain(
                   Seting.ORIGINAL_BLOCKCHAIN_FILE,
                   BlockchainFactoryEnum.ORIGINAL);
       }

//        return blockchain.getBlock(index);
        return Blockchain.indexFromFile(index, Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }
    @GetMapping("/nodes/resolve")
    public synchronized int resolve_conflicts() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        System.out.println(":resolve_conflicts");
        return resolve();
    }
   public static int resolve() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
       updating = true;
       try {
           System.out.println(" :start resolve");
           Blockchain temporaryBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
           Blockchain bigBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
           if (blockchainValid == false || blockchainSize == 0) {
               blockchain = Mining.getBlockchain(
                       Seting.ORIGINAL_BLOCKCHAIN_FILE,
                       BlockchainFactoryEnum.ORIGINAL);
               shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
               blockchainSize = (int) shortDataBlockchain.getSize();
               blockchainValid = shortDataBlockchain.isValidation();
           }

           int bigSize = 0;
           int blocks_current_size = blockchainSize;
           long hashCountZeroTemporary = 0;
           long hashCountZeroBigBlockchain = 0;
           EntityChain entityChain = null;
           System.out.println(" :resolve_conflicts: blocks_current_size: " + blocks_current_size);
           long hashCountZeroAll = 0;
           //count hash start with zero all

           hashCountZeroAll = shortDataBlockchain.getHashCount();

           Set<String> nodesAll = getNodes();

           System.out.println(":BasisController: resolve_conflicts: size nodes: " + getNodes().size());
           for (String s : nodesAll) {
               System.out.println(":while resolve_conflicts: node address: " + s);
               String temporaryjson = null;

               if (BasisController.getExcludedAddresses().contains(s)) {
                   System.out.println(":its your address or excluded address: " + s);
                   continue;
               }
               try {
                   if (s.contains("localhost") || s.contains("127.0.0.1"))
                       continue;
                   ;


                   System.out.println("start:BasisController:resolve conflicts: address: " + s + "/size");
                   String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                   Integer size = Integer.valueOf(sizeStr);
                   MainController.setGlobalSize(size);
                   System.out.println(" :resolve_conflicts: finish /size: " + size);
                   if (size > blocks_current_size) {

                       System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                       //Test start algorithm
                       List<Block> emptyList = new ArrayList<>();
                       SubBlockchainEntity subBlockchainEntity = null;
                       String subBlockchainJson = null;
                       if (size - blocks_current_size > Seting.PORTION_DOWNLOAD) {
                           boolean downloadPortion = true;
                           int finish = blocks_current_size + Seting.PORTION_DOWNLOAD;
                           int start = blocks_current_size;
                           while (downloadPortion) {

                               subBlockchainEntity = new SubBlockchainEntity(start, finish);

                               System.out.println("downloadPortion: " + subBlockchainEntity.getStart() +
                                       ": " + subBlockchainEntity.getFinish());
                               subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                               List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                               finish = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + Seting.PORTION_DOWNLOAD;
                               start = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1;
                               emptyList.addAll(subBlocks);
                               System.out.println("subblocks: " + subBlocks.get(0).getIndex() + ":"
                                       + subBlocks.get(subBlocks.size() - 1).getIndex());
                               if (size - emptyList.get(emptyList.size() - 1).getIndex() < Seting.PORTION_DOWNLOAD) {
                                   downloadPortion = false;
                                   finish = size;
                                   subBlockchainEntity = new SubBlockchainEntity(start, finish);
                                   subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                   subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                   System.out.println("subblocks: " + subBlocks.get(0).getIndex() + ":"
                                           + subBlocks.get(subBlocks.size() - 1).getIndex());
                                   emptyList.addAll(subBlocks);
                               }
                           }
                       } else {
                           subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);
                           subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                           System.out.println(":download sub block: " + subBlockchainJson);
                           List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                           emptyList.addAll(subBlocks);
                           System.out.println("subblocks: " + subBlocks.get(0).getIndex() + ":"
                                   + subBlocks.get(subBlocks.size() - 1).getIndex());
                           System.out.println("blocks_current_size: " + blocks_current_size);
                           System.out.println("sub: " + subBlocks.get(0).getIndex() + ":" + subBlocks.get(0).getHashBlock() + ":"
                                   + "prevHash: " + subBlocks.get(0).getPreviousHash());
                       }


                       if (blocks_current_size > 0) {
                           System.out.println("sub: from 0 " + ":" + blocks_current_size);
                           List<Block> temp = blockchain.subBlock(0, blocks_current_size);

                           emptyList.addAll(temp);
                       }


                       emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                       temporaryBlockchain.setBlockchainList(emptyList);


                       System.out.println("size temporaryBlockchain: " + temporaryBlockchain.sizeBlockhain());
                       System.out.println("resolve: temporaryBlockchain: " + temporaryBlockchain.validatedBlockchain());
                       if (!temporaryBlockchain.validatedBlockchain()) {
                           System.out.println(":download blocks");
                           emptyList = new ArrayList<>();

                           for (int i = size - 1; i > 0; i--) {

                               Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(i), s + "/block"));

                               System.out.println("block index: " + block.getIndex());
                               if (i > blocks_current_size - 1) {
                                   System.out.println(":download blocks: " + block.getIndex() +
                                           " your block : " + (blocks_current_size) + ":wating need downoad blocks: " + (block.getIndex() - blocks_current_size));
                                   emptyList.add(block);
                               } else if (!blockchain.getBlock(i).getHashBlock().equals(block.getHashBlock())) {
                                   emptyList.add(block);
                                   System.out.println("********************************");
                                   System.out.println(":dowdnload block index: " + i);
                                   System.out.println(":block original index: " + blockchain.getBlock(i).getIndex());
                                   System.out.println(":block from index: " + block.getIndex());
                                   System.out.println("---------------------------------");
                               } else {
                                   emptyList.add(block);

                                   if (i != 0) {
                                       System.out.println("portion:sub: " + 0 + " : " + i + " block index: " + block.getIndex());
                                       emptyList.addAll(blockchain.subBlock(0, i));
                                   }

                                   emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                                   temporaryBlockchain.setBlockchainList(emptyList);
                                   System.out.println("<><><<><><><>><><><><><><><<>><><><><>");
                                   System.out.println(":resolve_conflicts: temporaryBlockchain: " + temporaryBlockchain.validatedBlockchain());
                                   System.out.println(":dowdnload block index: " + i);
                                   System.out.println(":block original index: " + blockchain.getBlock(i).getIndex());
                                   System.out.println(":block from index: " + block.getIndex());
                                   System.out.println("<><><<><><><>><><><><><><><<>><><><><>");
                                   break;
                               }
                           }
                       }
                   } else {
                       System.out.println(":BasisController: resove: size less: " + size + " address: " + s);
                       continue;
                   }
               } catch (IOException e) {

//                e.printStackTrace();
                   System.out.println(":BasisController: resolve_conflicts: connect refused Error: " + s);
                   continue;
               } catch (JSONException e) {
                   throw new RuntimeException(e);
               } catch (CloneNotSupportedException e) {
                   throw new RuntimeException(e);
               }


               if (temporaryBlockchain.validatedBlockchain()) {
                   if (bigSize < temporaryBlockchain.sizeBlockhain()) {
                       bigSize = temporaryBlockchain.sizeBlockhain();
                   }
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

           System.out.println("bigBlockchain: " + bigBlockchain.validatedBlockchain() + " : " + bigBlockchain.sizeBlockhain());
           if (bigBlockchain.validatedBlockchain() && bigBlockchain.sizeBlockhain() > blockchainSize && hashCountZeroBigBlockchain > hashCountZeroAll) {
               System.out.println("resolve start addBlock start: ");
               blockchain = bigBlockchain;
               UtilsBlock.deleteFiles();
               addBlock(bigBlockchain.getBlockchainList());
               System.out.println(":BasisController: resolve: bigblockchain size: " + bigBlockchain.sizeBlockhain());
               System.out.println(":BasisController: resolve: validation bigblochain: " + bigBlockchain.validatedBlockchain());

               if (blockchainSize > bigSize) {
                   return 1;
               } else if (blockchainSize < bigSize) {
                   return -1;
               } else {
                   return 0;
               }
           }
       }finally {
           updating = false;
       }
       return -4;

   }

    public static void addBlock(List<Block> orignalBlocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        Map<String, Account> balances = new HashMap<>();
        Blockchain temporaryForValidation = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        temporaryForValidation.setBlockchainList(orignalBlocks);
        UtilsBlock.deleteFiles();
        System.out.println(" addBlock start: ");
        for (Block block : orignalBlocks) {
            System.out.println(" :BasisController: addBlock: blockchain is being updated: ");
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }

        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        blockchainSize = (int) shortDataBlockchain.getSize();
        blockchainValid = shortDataBlockchain.isValidation();

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

//        balances = UtilsBalance.calculateBalances(blockchain.getBlockchainList());
//        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
//        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);
        System.out.println(":BasisController: addBlock: finish");
    }
    @GetMapping("/addBlock")
    public ResponseEntity getBLock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        UtilsBlock.deleteFiles();
        addBlock(blockchain.getBlockchainList());
        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        blockchainSize = (int) shortDataBlockchain.getSize();
        blockchainValid = shortDataBlockchain.isValidation();
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/miningblock")
    public synchronized ResponseEntity minings() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        mining();
        return new ResponseEntity("OK", HttpStatus.OK);
    }


    @GetMapping("/process-mining")
    public synchronized String proccessMining(Model model, Integer number) throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        mining();
        model.addAttribute("title","mining proccess");
        return "redirect:/process-mining";
    }



    @RequestMapping("/resolving")
    public String resolving() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        resolve_conflicts();
        return "redirect:/";
    }
    /**соединяется к внешним хостам, и скачивает самый длинный блокчейн,
     * если, локальный блокчейн, меньше других */


    @RequestMapping("/sendBlocks")
    public String sending() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(blockchainValid == false || blockchainSize == 0){
            blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();
        }
        System.out.println("sendBlocks: size: " + blockchain.sizeBlockhain());
        sendAllBlocksToStorage(blockchain.getBlockchainList());
        return "redirect:/";
    }
    /**
     * Перезаписывает весь список блоков, и делает перерасчет баланса, а также других данных
     * таких как голоса, совет акционеров и т.д. заново записывает в файлы
     */


    /**Регистрирует новый внешний хост*/
    @RequestMapping(method = RequestMethod.POST, value = "/nodes/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public synchronized void register_node(@RequestBody AddressUrl urlAddrress) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


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

    //TODO если происходить майнинг, то он возвращает false, пока не прекратиться майнинг.
    //TODO if mining occurs, it returns false until mining stops.
    /** выззывает метод addBlock который перезаписывает весь список блоков, и другие данные*/

    /**Возвращяет список хостов, сохраненных на локальном сервере*/

    /**подключается к другим узлам и у них берет их списки хостов, которые храняться у них,
     *  и сохраняет эти списки у себя*/
    @GetMapping("/findAddresses")
    public void findAddresses() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            Set<String> addressesSet = new HashSet<>();
            try {
                register_node(new AddressUrl(s));
                System.out.println(":start download addressses");
                System.out.println("trying to connect to the server:"+ s +" timeout 45 seconds");
//                String addresses = UtilUrl.readJsonFromUrl(s + "/getDiscoveryAddresses");
//                addressesSet = UtilsJson.jsonToSetAddresses(addresses);
                System.out.println(":finish download addreses");
            } catch (IOException e) {
                System.out.println(":BasisController: findAddress: error");
                continue;
            }


//            for (String s1 : addressesSet) {
//
//                register_node(new AddressUrl(s1));
//            }

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
            System.out.println(new Date()+":start send addreses");
            String original = s;
            String url = s + "/nodes/register";

            if (BasisController.getExcludedAddresses().contains(url)) {
                System.out.println(":MainController: its your address or excluded address: " + url);
                continue;
            }
            try {
                for (String s1 : BasisController.getNodes()) {

                    System.out.println(":trying to connect to the server: send addreses: " + s+": timeout 45 seconds");
                    AddressUrl addressUrl = new AddressUrl(s1);
                    String json = UtilsJson.objToStringJson(addressUrl);
                    UtilUrl.sendPost(json, url);
                }
            } catch (Exception e) {
                System.out.println(":BasisController: sendAddress: wronge node: " + original);

                continue;
            }


        }
        System.out.println(":finish send addressess");
    }

    //должен отправлять блокчейн в хранилище блокчейна
    /**Отправляет список блоков в центральные хранилища (пример: http://194.87.236.238:80)*/

    //должен отправлять блокчейн в хранилище блокчейна
    /**Отправляет список блоков в центральные хранилища (пример: http://194.87.236.238:80)*/
    public static int sendAllBlocksToStorage(List<Block> blocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        System.out.println(new Date()+":BasisController: sendAllBlocksToStorage: start: ");
        int bigsize = 0;
        int blocks_current_size = blocks.size();
        //отправка блокчейна на хранилище блокчейна
        System.out.println(":BasisController: sendAllBlocksToStorage: ");
        getNodes().stream().forEach(System.out::println);
        for (String s : getNodes()) {

            System.out.println(":trying to connect to the server send block: " +s+": timeout 45 seconds");

            if (BasisController.getExcludedAddresses().contains(s)) {
                System.out.println(":its your address or excluded address: " + s);
                continue;
            }

            try {
                System.out.println(":BasisController:resolve conflicts: address: " + s + "/size");
                String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                Integer size =  0;
                if(Integer.valueOf(sizeStr) > 0)
                    size = Integer.valueOf(sizeStr);
                System.out.println(":BasisController: send: local size: " + blocks_current_size + " global size: " + size);
                if(size > blocks.size()){
                    System.out.println(":your local chain less");
                    return -1;
                }
                List<Block> fromToTempBlock = blocks.subList(size, blocks.size());
                SendBlocksEndInfo infoBlocks = new SendBlocksEndInfo(Seting.VERSION, fromToTempBlock);
                String jsonFromTo = UtilsJson.objToStringJson(infoBlocks);
                //если блокчейн текущей больше чем в хранилище, то
                //отправить текущий блокчейн отправить в хранилище
                if (size < blocks_current_size) {
                    if(bigsize < size){
                        bigsize = size;
                    }
                    int response = -1;
                    //Test start algorithm
                    String originalF = s;
                    System.out.println(":send resolve_from_to_block");
                    String urlFrom = s + "/nodes/resolve_from_to_block";
                    try {
                        response = UtilUrl.sendPost(jsonFromTo, urlFrom);
                        System.out.println(":CONFLICT: " + HttpStatus.CONFLICT.value());
                        System.out.println(":GOOD: " + HttpStatus.OK.value());
                        System.out.println(":FAIL: " + HttpStatus.EXPECTATION_FAILED.value());
                        System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                        System.out.println(":response: " + response);
                    }catch (Exception e){
                        System.out.println(":exception resolve_from_to_block: " + originalF);

                    }
                    System.out.println(":CONFLICT: " + HttpStatus.CONFLICT.value());
                    System.out.println(":GOOD: " + HttpStatus.OK.value());
                    System.out.println(":FAIL: " + HttpStatus.EXPECTATION_FAILED.value());
                    System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                    System.out.println(":response: " + response);

                    System.out.println(":BasisController: sendAllBlocksStorage: response: " + response);
                    if(response == HttpStatus.CONFLICT.value()){
                        System.out.println(":BasisController: sendAllBlocksStorage: start deleted 50 blocks:" );
                        System.out.println(":size before delete: " + blockchainSize);
                        blockchain = Mining.getBlockchain(
                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                                BlockchainFactoryEnum.ORIGINAL);
                        List<Block> temporary = blockchain.subBlock(0, blockchainSize-Seting.DELETED_PORTION);
                        UtilsBlock.deleteFiles();
                        blockchain.setBlockchainList(temporary);
                        UtilsBlock.saveBlocks(blockchain.getBlockchainList(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
                        blockchain = Mining.getBlockchain(
                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                                BlockchainFactoryEnum.ORIGINAL);

                        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                        blockchainSize = (int) shortDataBlockchain.getSize();
                        blockchainValid = shortDataBlockchain.isValidation();

                        UtilsBlock.deleteFiles();
                        addBlock(blockchain.getBlockchainList());
                        System.out.println(":size after delete: " + blockchainSize);


                       int result = resolve();
                        System.out.println(":resolve: updated: " + result);
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
                continue;

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

        }
        if(blockchainSize > bigsize){
            return 1;
        }else if(blockchainSize < bigsize){
            return -1;
        }else if(blockchainSize == bigsize){
            return 0;
        }else {
            return -4;
        }
    }
    @GetMapping("/constantMining")
    public String alwaysMining() throws JSONException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {

        for (int i = 0; i < 576 ; i++) {
            try {
                mining();
            }
            catch (IllegalArgumentException e){
                System.out.println("BasisisController: constantMining find error:");
                continue;
            }catch (IOException e){
                System.out.println("BasisisController: constantMining find error: ");
                continue;
            }
        }
        return "redirect:/mining";
    }

    public synchronized String mining() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining = true;
        try {
            if (tempBlockchain == null ||
                    tempBlockchain.getBlockchainList() == null
                    || tempBlockchain.getBlockchainList().isEmpty()) {
                tempBlockchain = Mining.getBlockchain(
                        Seting.ORIGINAL_BLOCKCHAIN_FILE,
                        BlockchainFactoryEnum.ORIGINAL);

            }

            String sizeStr = "-1";
            try {
                sizeStr = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/size");
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
            findAddresses();
            if (blockchainSize % 5 == 0
                    || tempBlockchain.getBlock(tempBlockchain.sizeBlockhain() - 1).getIndex() % 5 == 0
                    || sizeG % 5 == 0
            )
                resolve_conflicts();


            if (blockchainSize % (576 * 2) == 0) {
                System.out.println("clear storage transaction because is old");
                AllTransactions.clearAllTransaction();
            }
            //собирает класс список балансов из файла расположенного по пути Seting.ORIGINAL_BALANCE_FILE
            Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
            //собирает объект блокчейн из файла

            blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
            shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();


            //если блокчейн работает то продолжить
            if (!tempBlockchain.validatedBlockchain()) {
                text = "wrong chain: неправильный блокчейн, добыча прекращена";
//            model.addAttribute("text", text);
                return "wrong blockchain";
            }


            //если размер блокчейна меньше или равно единице, сохранить в файл генезис блок
            long index = tempBlockchain.sizeBlockhain();
            if (blockchain.sizeBlockhain() <= 1) {
                System.out.println("save genesis block");
                //сохранение генезис блока
                if (blockchain.sizeBlockhain() == 1) {
                    UtilsBlock.saveBLock(blockchain.genesisBlock(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
                }

                //получить список балансов из файла
                List<String> signs = new ArrayList<>();
                balances = Mining.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain, balances, signs);
                //удалить старые файлы баланса
                Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);

                //сохранить балансы
                SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);
                blockchain = Mining.getBlockchain(
                        Seting.ORIGINAL_BLOCKCHAIN_FILE,
                        BlockchainFactoryEnum.ORIGINAL);


            }
            //скачать список балансов из файла
            System.out.println("BasisController: minining: read list balance");
            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

            //получить счет майнера

            Account miner = balances.get(User.getUserAddress());
            minerShow = miner;
            System.out.println("BasisController: mining: account miner: " + miner);
            if (miner == null) {
                //если в блокчейне не было баланса, то баланс равен нулю
                miner = new Account(User.getUserAddress(), 0, 0);
            }

            //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions
            List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();
            //отказ от дублирующих транзакций
            temporaryDtoList = UtilsBlock.validDto(tempBlockchain.getBlockchainList(), temporaryDtoList);


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
                    tempBlockchain,
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

            if (tempBlockchain.sizeBlockhain() > diff) {

                testingValidationsBlock = tempBlockchain.subBlock(tempBlockchain.sizeBlockhain() - diff, tempBlockchain.sizeBlockhain());
            } else {
                testingValidationsBlock = tempBlockchain.clone();
            }
            //проверяет последние 288 блоков на валидность.
            if (testingValidationsBlock.size() > 1) {
                boolean validationTesting = UtilsBlock.validationOneBlock(
                        tempBlockchain.genesisBlock().getFounderAddress(),
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
            tempBlockchain.addBlock(block);
            //сохраняет последний блок в файл
//        UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);

            //перерасчет балансов, подсчитывает какие изменения произошли в балансах
//        List<String> signs = new ArrayList<>();
//        balances = Mining.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain, balances, signs);
//        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
            //сохраняет в файл уже заново посчитанные балансы.
//        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);

//        получает все созданные когда либо законы
//        Map<String, Laws> allLaws = UtilsLaws.getLaws(blockchain.getBlockchainList(), Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);

            //возвращает все законы с голосами проголосовавшими за них
//        List<LawEligibleForParliamentaryApproval> allLawsWithBalance =
//                UtilsLaws.getCurrentLaws(allLaws, balances, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
            //удаление устаревних законов
//        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
            //записывает все законы в файл с их голосами.
//        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

            //отправляет блокчейн во внешние сервера
            if (blockchainSize % 5 == 0
                    || tempBlockchain.getBlock(tempBlockchain.sizeBlockhain() - 1).getIndex() % 5 == 0
                    || sizeG % 5 == 0) {
                sendAllBlocksToStorage(tempBlockchain.getBlockchainList());
                tempBlockchain.setBlockchainList(new ArrayList<>());
            }


            //отправить адресса
//        sendAddress();
            text = "success: блок успешно добыт";
//        model.addAttribute("text", text);
        }finally {
            mining = false;
        }
        return "ok";
    }

    /**Стартует добычу, начинает майнинг*/
    @GetMapping("/mine")
    public synchronized String mine(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining();

        return "redirect:/mining";

    }



    @GetMapping("/testBlock")
    @ResponseBody
    public boolean testBlock() throws IOException, CloneNotSupportedException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        blockchainSize = (int) shortDataBlockchain.getSize();
        blockchainValid = shortDataBlockchain.isValidation();

        return true;
//        list.add(blockList);

    }
    @GetMapping("testBlock1")
    @ResponseBody
    public Integer testBlock1() throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        InfoDemerageMoney demerageMoney = new InfoDemerageMoney();
        demerageMoney.setAddress(User.getUserAddress());
        demerageMoney.setBeforeDollar(100);
        demerageMoney.setBeforeStock(100);
        demerageMoney.setAfterDollar(50);
        demerageMoney.setAfterStock(50);
        demerageMoney.setIndexBlock(1);
        UtilsDemerage.saveDemarege(demerageMoney, Seting.BALANCE_REPORT_ON_DESTROYED_COINS);
        return 0;
    }

    @GetMapping("/processUpdating")
    public String processUpdating(Model model){
        model.addAttribute("isMining", isMining());
        model.addAttribute("isUpdating", isUpdating());
        if(minerShow == null){
            model.addAttribute("address", "balance has not loaded yet");
            model.addAttribute("dollar", "balance has not loaded yet");
            model.addAttribute("stock", "balance has not loaded yet");
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.################");
        String dollar = decimalFormat.format(minerShow.getDigitalDollarBalance());
        String stock = decimalFormat.format(minerShow.getDigitalStockBalance());
        model.addAttribute("address", minerShow.getAccount());
        model.addAttribute("dollar",dollar);
        model.addAttribute("stock", stock);

        return "processUpdating";
    }
}


