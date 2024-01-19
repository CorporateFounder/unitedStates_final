package International_Trade_Union.controllers;

import International_Trade_Union.entity.*;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.services.BlockService;
import org.json.JSONException;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.utils.UtilsBalance.calculateBalance;

@Controller
public class BasisController {
    @Autowired
    BlockService blockService;


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

    /**Возвращает последний блок который есть в локальном блокчейне.
     * Returns the last block that exists in the local room.*/
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

    /**Используется в будущем, для подключения к разным серверам.
     * Used in the future to connect to different servers.*/

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


        nodes = nodes.stream()
                .filter(t -> !t.isBlank())
                .filter(t -> t.startsWith("\""))
                .collect(Collectors.toSet());
        nodes = nodes.stream().map(t -> t.replaceAll("\"", "")).collect(Collectors.toSet());
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        //removes blocked hosts. удаляет заблокированные хосты
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);

        //adds preset hosts. добавляет предустановные хосты
        nodes.addAll(Seting.ORIGINAL_ADDRESSES);
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
        nodes = nodes.stream().filter(t -> t.startsWith("\""))
                .collect(Collectors.toSet());
        Set<String> bloked = UtilsAllAddresses.readLineObject(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        nodes.removeAll(bloked);
        nodes.removeAll(Seting.ORIGINAL_BLOCKED_ADDRESS);
        return nodes;
    }

    /**
     * Возвращает действующий блокчейн. Returns a valid blockchain
     */
    public static Blockchain getBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain1 = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return blockchain1;
    }
    static {
        try {
            //creates all resource folders to work with
            //создает все папки ресурсов для работы
            UtilsCreatedDirectory.createPackages();

            //downloads from a blockchain file
            //загрузки из файла бло

            //a shorthand object that stores information about the blockchain
            //сокращенный объект, который хранит информацию о блокчейне

            String json = UtilsFileSaveRead.read(Seting.TEMPORARY_BLOCKCHAIN_FILE);
            if(!json.isEmpty() || !json.isBlank()){
                shortDataBlockchain = UtilsJson.jsonToDataShortBlockchainInformation(json);

            }else {
                shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                 json = UtilsJson.objToStringJson(shortDataBlockchain);
                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
            }
            blockchainSize = (int) shortDataBlockchain.getSize();
            blockchainValid = shortDataBlockchain.isValidation();
            prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);




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

    /**
     * Returns an EntityChain which stores the size of the blockchain and the list of blocks
     * Возвращает EntityChain который, хранит в себе размер блокчейна и список блоков
     */
    @GetMapping("/chain")
    @ResponseBody
    public EntityChain full_chain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        utilsMethod();
        Blockchain blockchain1 = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return new EntityChain(blockchain1.sizeBlockhain(), blockchain1.getBlockchainList());
    }

    /**
     * returns the size of the local blockchain
     * возвращает размер локального блокчейна
     */
    @GetMapping("/size")
    @ResponseBody
    public Integer sizeBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
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
    public List<Block> subBlocks(@RequestBody SubBlockchainEntity entity) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        utilsMethod();
        return Blockchain.subFromFile(entity.getStart(), entity.getFinish(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }

    /**
     * Returns a block by index
     * Возвращает блок по индексу
     */
    @PostMapping("/block")
    @ResponseBody
    public Block getBlock(@RequestBody Integer index) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        utilsMethod();

        //        return blockchain.getBlock(index);
        return Blockchain.indexFromFile(index, Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }

    /**
     * connects to the storage and updates its internal blockchain.
     * подключается к хранилищу и обновляет свой внутренний блокчейн
     */
    @GetMapping("/nodes/resolve")
    public static synchronized int resolve_conflicts() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        System.out.println(":resolve_conflicts");

        int result = resovle2();
        return result;
    }


    /**Подключается к серверу и оттуда порциями по 500 блоков скачивает на локальный.
     * Так же производит расчеты, чтобы обновить внутренние файлы включая h2.
     * It connects to the server and from there downloads 500 blocks in portions to the local one.
     *       * Also performs calculations to update internal files including h2.*/
    public static int resovle2() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        updating = true;
        int bigSize = 0;
        try {
            System.out.println(" :start resolve");
            utilsMethod();


            //local blockchain size
            //размер локального блокчейна
            int blocks_current_size = blockchainSize;

            EntityChain entityChain = null;
            System.out.println(" resolve2:local size: " + blocks_current_size);

            Set<String> nodesAll = getNodes();
            System.out.println(":resolve2: size nodes: " + getNodes().size());
            //goes through all hosts (repositories) in search of the most up-to-date blockchain
            //проходит по всем хостам(хранилищам) в поисках самого актуального блокчейна
            for (String s : nodesAll) {
                //if the local address matches the host address, it skips
                //если локальный адрес совпадает с адресом хоста, он пропускает
                if (BasisController.getExcludedAddresses().contains(s)) {
                    System.out.println(":its your address or excluded address: " + s);
                    continue;
                }
                try {
                    //if the address is localhost, it skips
                    //если адрес локального хоста, он пропускает
                    if (Seting.IS_TEST == false && (s.contains("localhost") || s.contains("127.0.0.1")))
                        continue;
                    String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                    Integer size = Integer.valueOf(sizeStr);

                    if (size > bigSize) {
                        bigSize = size;
                    }
                    //if the size from the storage is larger than on the local server, start checking
                    //если размер с хранилища больше чем на локальном сервере, начать проверку
                    System.out.println("resolve2 size: " + size + " blocks_current_size: " + blocks_current_size);
                    if (size > blocks_current_size) {
                        System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                        //Test start algorithm
                        List<Block> lastDiff = new ArrayList<>();
                        SubBlockchainEntity subBlockchainEntity = null;
                        String subBlockchainJson = null;
                        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

                        //if the local one lags behind the global one by more than PORTION_DOWNLOAD, then you need to download in portions from the storage
                        //если локальный отстает от глобального больше чем PORTION_DOWNLOAD, то нужно скачивать порциями из хранилища
                        if (size - blocks_current_size > Seting.PORTION_DOWNLOAD) {
                            boolean downloadPortion = true;
                            int finish = blocks_current_size + Seting.PORTION_DOWNLOAD;
                            int start = blocks_current_size;
                            //while the difference in the size of the local blockchain is greater than from the host, it will continue to download in portions to download the entire blockchain
                            //пока разница размера локального блокчейна больше чем с хоста будет продожаться скачивать порциями, чтобы скачать весь блокчейн
                            while (downloadPortion) {

                                subBlockchainEntity = new SubBlockchainEntity(start, finish);

                                System.out.println("1:sublockchainEntity: " + subBlockchainEntity);
                                subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                System.out.println("1:sublockchainJson: " + subBlockchainJson);
                                List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                System.out.println("1:download sub block: " + subBlocks.size());

                                finish = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + Seting.PORTION_DOWNLOAD + 1;
                                start = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1; //вот здесь возможно сделать + 2


                                System.out.println("****************************************************");
                                System.out.println("size: "+subBlocks.size());
                                System.out.println("sub.get(0): " + subBlocks.get(0));
                                System.out.println("sub.get(1): " + subBlocks.get(1));
                                System.out.println("sub.get(size-1): " + subBlocks.get(subBlocks.size()-1));
                                System.out.println("****************************************************");
                                balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

                                //вычисляет сложность блока, для следующего блока, на основе предыдущего.
                                //calculates the block difficulty for the next block based on the previous one.
                                if (blockchainSize > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                    lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                            BlockService.findBySpecialIndexBetween(
                                                    (prevBlock.getIndex()+ 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY ,
                                                    prevBlock.getIndex() + 1
                                            )
                                    );
                                }

                                DataShortBlockchainInformation temp = new DataShortBlockchainInformation();
                                Map<String, Account> tempBalances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                                List<String> sign = new ArrayList<>();
                                if (blockchainSize > 1) {
                                    //проверяет скаченные блоки на целостность
                                    //checks downloaded blocks for integrity
                                    temp = Blockchain.shortCheck(prevBlock, subBlocks, shortDataBlockchain, lastDiff, tempBalances, sign);
                                }


                                System.out.println("temp: " + temp);
                                System.out.println("blockchainsize: " + blockchainSize);
                                System.out.println("sublocks: " + subBlocks.size());

                                if (blockchainSize > 1 && !temp.isValidation()) {
                                    System.out.println("error resolve 2 in portion upper > 500");
                                    return -10;
                                }
                                addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                if (!temp.isValidation()) {
                                    System.out.println("check all file");
                                    //проверить целостность блокчейна всего на кошельке
                                    //check the integrity of the blockchain of everything on the wallet
                                    temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                }

                                //сохраняет последний индекс, длину всего блокчейна и его валидность.
                                //saves the last index, the length of the entire blockchain and its validity.
                                shortDataBlockchain = temp;
                                //размер блокчейна в кошельке.
                                //the size of the blockchain in the wallet.
                                blockchainSize = (int) shortDataBlockchain.getSize();
                                //валидность блокчейна в кошельке.
                                //validity of the blockchain in the wallet.
                                blockchainValid = shortDataBlockchain.isValidation();
//
                                //получить последний блок из базы данных.
                                //get the last block from the database.
                                EntityBlock tempBlock = BlockService.findBySpecialIndex(blockchainSize-1);
                                //последний блок в локальном сервере.
                                //last block in the local server.
                                prevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock);
                                String json = UtilsJson.objToStringJson(shortDataBlockchain);
                                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                                //если количество новых блоков, относительно локального блокчейна меньше 500,
                                //то скачать эти блоки и прекратить попытки скачивания.
                                //if the number of new blocks relative to the local blockchain is less than 500,
                                //then download these blocks and stop trying to download.
                                if (size - prevBlock.getIndex() < Seting.PORTION_DOWNLOAD) {
                                    downloadPortion = false;
                                    finish = size;
                                    subBlockchainEntity = new SubBlockchainEntity(start, finish);
                                    System.out.println("2:sublockchainEntity: " + subBlockchainEntity);
                                    subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                    System.out.println("2:sublockchainJson: " + subBlockchainJson);
                                    subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                    System.out.println("2:download sub block: " + subBlocks.size());

                                    balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                                    if (blockchainSize > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                        lastDiff =UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                                BlockService.findBySpecialIndexBetween(
                                                        (prevBlock.getIndex()+ 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY ,
                                                        prevBlock.getIndex() + 1
                                                )
                                        );
                                    }

                                    if (blockchainSize > 1) {
                                        temp = Blockchain.shortCheck(prevBlock, subBlocks, shortDataBlockchain, lastDiff, tempBalances, sign);
                                    }


                                    System.out.println("temp: " + temp);
                                    System.out.println("blockchainsize: " + blockchainSize);
                                    System.out.println("sublocks: " + subBlocks.size());

                                    if (blockchainSize > 1 && !temp.isValidation()) {
                                        return -10;
                                    }

                                    //метод, который сохраняет скачанные порции блока, в блокчейн
                                    addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    if (!temp.isValidation()) {
                                        temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    }
                                    shortDataBlockchain = temp;
                                    blockchainSize = (int) shortDataBlockchain.getSize();
                                    blockchainValid = shortDataBlockchain.isValidation();
//                                    prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);
//
                                    tempBlock = BlockService.findBySpecialIndex(blockchainSize-1);
                                    prevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock);

                                    json = UtilsJson.objToStringJson(shortDataBlockchain);
                                    UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                                }
                            }
                        } else {

                            //здесь нужно проверить
                            //If the difference is not greater than PORTION_DOWNLOAD, then downloads once a portion of this difference
                            //Если разница не больше PORTION_DOWNLOAD, то скачивает один раз порцию эту разницу
                            subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);

                            System.out.println("3:sublockchainEntity: " + subBlockchainEntity);
                            subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);


                            System.out.println("3:sublockchainJson: " + subBlockchainJson);
                            List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                            System.out.println("3:download sub block: " + subBlocks.size());
                            System.out.println("prev block:" + prevBlock);
                            System.out.println("3: block " + subBlocks.get(0));
                            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                            List<String> sign = new ArrayList<>();
                            Map<String, Account> tempBalances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                            if (blockchainSize > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                        BlockService.findBySpecialIndexBetween(
                                                (prevBlock.getIndex()+ 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY ,
                                                prevBlock.getIndex() + 1
                                        )
                                );
                            }

                            DataShortBlockchainInformation temp = new DataShortBlockchainInformation();
                            if (blockchainSize > 1) {
                                temp = Blockchain.shortCheck(prevBlock, subBlocks, shortDataBlockchain, lastDiff, tempBalances,sign);
                            }

                            System.out.println("temp: " + temp);
                            System.out.println("blockchainsize: " + blockchainSize);
                            System.out.println("sublocks: " + subBlocks.size());

                            if (temp.getSize() > 1 && !temp.isValidation()) {
                                System.out.println("error resolve 2 in portion upper < 500");

                                return -10;
                            }
                            addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            if (!temp.isValidation()) {
                                System.out.println("check all file");
                                temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            }
                            shortDataBlockchain = temp;
                            blockchainSize = (int) shortDataBlockchain.getSize();
                            blockchainValid = shortDataBlockchain.isValidation();
//                            prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            EntityBlock tempBlock =BlockService.findBySpecialIndex(blockchainSize-1);
                            prevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock);

                            String json = UtilsJson.objToStringJson(shortDataBlockchain);
                            UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                        }
                        //if the local blockchain was originally greater than 0, then add part of the missing list of blocks to the list.
                        //если локальный блокчейн изначально был больше 0, то добавить в список часть недостающего списка блоков.
                        System.out.println("size temporaryBlockchain: ");
                        System.out.println("resolve: temporaryBlockchain: ");
                    } else {
                        System.out.println(":BasisController: resove: size less: " + size + " address: " + s);
                        continue;
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                    System.out.println("BasisController: resove2: " + e.getMessage());
                    continue;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }

        } finally {
            updating = false;
            if (blockchainSize > bigSize) {
                return 1;
            } else if (blockchainSize < bigSize) {
                return -1;
            } else {
                return 0;
            }

        }


    }



    /**TODO Устарел и нигде не используется. TODO Deprecated and not used anywhere.
     * Updates the blockchain. Connects to the storage host and downloads if the host has a more up-to-date blockchain.
     * Обновляет блокчейн. Подключается к хосту хранилища и скачивает, если в на хосте более актуальный блокчейн.
     */
    public static int resolve() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        updating = true;
        Blockchain blockchain1 = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        boolean isPortion = false;
        boolean isBigPortion = false;

        try {
            System.out.println(" :start resolve");
            Blockchain temporaryBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
            Blockchain bigBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
            utilsMethod();

            //size of the most recent long blockchain downloaded from hosts (storage)
            //размер самого актуального длинного блокчейна, скачанного из хостов (хранилище)
            int bigSize = 0;

            //local blockchain size
            //размер локального блокчейна
            int blocks_current_size = blockchainSize;

            //the sum of the complexity (all zeros) of the temporary blockchain, needed to select the most complex blockchain
            //сумма сложности (всех нулей) временного блокчейна, нужна чтобы отобрать самый сложный блокчейн
            long hashCountZeroTemporary = 0;

            //the sum of the complexity (all zeros) of the longest downloaded blockchain is needed to select the most complex blockchain
            //сумма сложности (всех нулей) самого длинного блокчейна из скачанных, нужна чтобы отобрать самый сложный блокчейн
            long hashCountZeroBigBlockchain = 0;

            EntityChain entityChain = null;
            System.out.println(" :resolve_conflicts: blocks_current_size: " + blockchainSize);

            //the sum of the complexity (all zeros) of the local blockchain
            //сумма сложности (всех нулей) локального блокчейна
            long hashCountZeroAll = 0;

            //get the total complexity of the local blockchain
            //получить общую сложность локального блокчейна
            hashCountZeroAll = shortDataBlockchain.getHashCount();

            Set<String> nodesAll = getNodes();

            System.out.println(":BasisController: resolve_conflicts: size nodes: " + getNodes().size());

            //goes through all hosts (repositories) in search of the most up-to-date blockchain
            //проходит по всем хостам(хранилищам) в поисках самого актуального блокчейна
            for (String s : nodesAll) {
                //if the local address matches the host address, it skips
                //если локальный адрес совпадает с адресом хоста, он пропускает
                if (BasisController.getExcludedAddresses().contains(s)) {
                    System.out.println(":its your address or excluded address: " + s);
                    continue;
                }
                try {
                    //if the address is localhost, it skips
                    //если адрес локального хоста, он пропускает
                    if (Seting.IS_TEST == false && (s.contains("localhost") || s.contains("127.0.0.1")))
                        continue;

                    System.out.println("start:BasisController:resolve conflicts: address: " + s + "/size");

                    String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                    Integer size = Integer.valueOf(sizeStr);

                    System.out.println(" :resolve_conflicts: finish /size: " + size);
                    //if the size from the storage is larger than on the local server, start checking
                    //если размер с хранилища больше чем на локальном сервере, начать проверку
                    if (size > blockchainSize) {

                        System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blockchainSize);
                        //Test start algorithm
                        List<Block> emptyList = new ArrayList<>();
                        SubBlockchainEntity subBlockchainEntity = null;
                        String subBlockchainJson = null;

                        //if the local one lags behind the global one by more than PORTION_DOWNLOAD, then you need to download in portions from the storage
                        //если локальный отстает от глобального больше чем PORTION_DOWNLOAD, то нужно скачивать порциями из хранилища
                        if (size - blockchainSize > Seting.PORTION_DOWNLOAD) {
                            boolean downloadPortion = true;
                            int finish = blockchainSize + Seting.PORTION_DOWNLOAD;
                            int start = blockchainSize;
                            //while the difference in the size of the local blockchain is greater than from the host, it will continue to download in portions to download the entire blockchain
                            //пока разница размера локального блокчейна больше чем с хоста будет продожаться скачивать порциями, чтобы скачать весь блокчейн
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

                                    emptyList.addAll(subBlocks);
                                }
                            }
                        } else {
                            //If the difference is not greater than PORTION_DOWNLOAD, then downloads once a portion of this difference
                            //Если разница не больше PORTION_DOWNLOAD, то скачивает один раз порцию эту разницу
                            subBlockchainEntity = new SubBlockchainEntity(blockchainSize, size);
                            subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);

                            System.out.println(":download sub block: " + subBlockchainJson);

                            List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                            emptyList.addAll(subBlocks);

                            System.out.println("subblocks: " + subBlocks.get(0).getIndex() + ":"
                                    + subBlocks.get(subBlocks.size() - 1).getIndex());
                            System.out.println("blocks_current_size: " + blockchainSize);
                            System.out.println("sub: " + subBlocks.get(0).getIndex() + ":" + subBlocks.get(0).getHashBlock() + ":"
                                    + "prevHash: " + subBlocks.get(0).getPreviousHash());
                        }

                        //if the local blockchain was originally greater than 0, then add part of the missing list of blocks to the list.
                        //если локальный блокчейн изначально был больше 0, то добавить в список часть недостающего списка блоков.
                        if (blockchainSize > 0) {
                            System.out.println("sub: from 0 " + ":" + blocks_current_size);
                            List<Block> temp = blockchain1.subBlock(0, blocks_current_size);

                            emptyList.addAll(temp);
                        }

                        emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                        temporaryBlockchain.setBlockchainList(emptyList);


                        System.out.println("size temporaryBlockchain: " + temporaryBlockchain.sizeBlockhain());
                        System.out.println("resolve: temporaryBlockchain: " + temporaryBlockchain.validatedBlockchain());

                        //if the global blockchain is larger but there is a branching in the blockchain, for example, the global size is 25,
                        // the local size is 20,
                        //but from block 15 they differ, then you need to remove all blocks from the local block from block 15
                        // and add 15-25 blocks from the global blockchain there
                        //если глобальный блокчейн больше но есть развлетление в блокчейне, к примеру глобальный размер 25,
                        // локальный 20,
                        //но с 15 блока они отличаются, то нужно удалить из локального с
                        // 15 все блоки и добавить туда 15-25 с глобального блокчейна

                        if (temporaryBlockchain.validatedBlockchain() && blockchainSize > 1) {
                            isPortion = true;
                        } else {
                            isPortion = false;
                        }
                        System.out.println("isPortion: " + isPortion);
                        if (!temporaryBlockchain.validatedBlockchain()) {
                            System.out.println(":download blocks");
                            emptyList = new ArrayList<>();

                            for (int i = size - 1; i > 0; i--) {

                                Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(i), s + "/block"));

                                System.out.println("block index: " + block.getIndex());
                                if (i > blockchainSize - 1) {
                                    System.out.println(":download blocks: " + block.getIndex() +
                                            " your block : " + (blockchainSize) + ":wating need downoad blocks: " + (block.getIndex() - blockchainSize));
                                    emptyList.add(block);
                                } else if (!blockchain1.getBlock(i).getHashBlock().equals(block.getHashBlock())) {
                                    emptyList.add(block);
                                    System.out.println("********************************");
                                    System.out.println(":dowdnload block index: " + i);
                                    System.out.println(":block original index: " + blockchain1.getBlock(i).getIndex());
                                    System.out.println(":block from index: " + block.getIndex());
                                    System.out.println("---------------------------------");
                                } else {
                                    emptyList.add(block);

                                    if (i != 0) {
                                        System.out.println("portion:sub: " + 0 + " : " + i + " block index: " + block.getIndex());
                                        emptyList.addAll(blockchain1.subBlock(0, i));
                                    }

                                    emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                                    temporaryBlockchain.setBlockchainList(emptyList);
                                    System.out.println("<><><<><><><>><><><><><><><<>><><><><>");
                                    System.out.println(":resolve_conflicts: temporaryBlockchain: " + temporaryBlockchain.validatedBlockchain());
                                    System.out.println(":dowdnload block index: " + i);
                                    System.out.println(":block original index: " + blockchain1.getBlock(i).getIndex());
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

                    System.out.println(":BasisController: resolve_conflicts: connect refused Error: " + s);
                    continue;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }

                //if the global blockchain is correct and it is larger than the longest previous temporary blockchain, then make it a contender as a future local blockchain
                //если глобальный блокчейн верный и он больше самого длиного предыдущего временного блокчейна, то сделать его претендентом в качестве будущего локального блокчейна
                if (temporaryBlockchain.validatedBlockchain()) {
                    if (bigSize < temporaryBlockchain.sizeBlockhain()) {
                        isBigPortion = isPortion;
                        bigSize = temporaryBlockchain.sizeBlockhain();
                    }
                    for (Block block : temporaryBlockchain.getBlockchainList()) {
                        hashCountZeroTemporary += UtilsUse.hashCount(block.getHashBlock(), block.getIndex());
                    }

                    if (blockchainSize < temporaryBlockchain.sizeBlockhain() && shortDataBlockchain.getHashCount() < hashCountZeroTemporary) {
                        blockchainSize = temporaryBlockchain.sizeBlockhain();
                        bigBlockchain = temporaryBlockchain;
                        hashCountZeroBigBlockchain = hashCountZeroTemporary;
                    }
                    hashCountZeroTemporary = 0;
                }

            }



            System.out.println("bigBlockchain: " + bigBlockchain.validatedBlockchain() + " : " + bigBlockchain.sizeBlockhain());
            //Only the blockchain that is not only the longest but also the most complex will be accepted.
            //Будет принять только тот блокчейн который является не только самым длинным, но и самым сложным.
            if (bigBlockchain.validatedBlockchain() && bigBlockchain.sizeBlockhain() > blockchainSize && hashCountZeroBigBlockchain > shortDataBlockchain.getHashCount()) {
                System.out.println("resolve start addBlock start: ");

                blockchain1 = bigBlockchain;
                if (isBigPortion) {
                    List<Block> temp = bigBlockchain.subBlock(blockchainSize, bigBlockchain.sizeBlockhain());
                    Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                    addBlock2(temp,
                            balances);
                    System.out.println("temp size: " + temp.size());

                } else {
                    BasisController.getBlock();
                }
                List<Block> temp = bigBlockchain.subBlock(blockchainSize, bigBlockchain.sizeBlockhain());

                System.out.println("size: " + blockchainSize);
                System.out.println(":BasisController: resolve: bigblockchain size: " + bigBlockchain.sizeBlockhain());
                System.out.println(":BasisController: resolve: validation bigblochain: " + bigBlockchain.validatedBlockchain());

                System.out.println("isPortion: " + isPortion + ":isBigPortion: " + isBigPortion + " size: " + temp.size());
                if (blockchainSize > bigSize) {
                    return 1;
                } else if (blockchainSize < bigSize) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        } finally {
            updating = false;
        }
        return -4;

    }

    /**
     * rewrites the blockchain into files and into the h2 database. From here they are called
     *       * methods that calculate balance and other calculations.
     * производит перезапись блокчейна в файлы и в базу h2. Отсюда вызываются
     * методы которые, вычисляют баланс и другие вычисления.
     */

    public static void addBlock3(List<Block> originalBlocks, Map<String, Account> balances, String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        java.sql.Timestamp lastIndex = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());

        List<EntityBlock> list = new ArrayList<>();
        List<String> signs = new ArrayList<>();
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        for (Block block : originalBlocks) {
            System.out.println(" :BasisController: addBlock3: blockchain is being updated: index" + block.getIndex());
            UtilsBlock.saveBLock(block, filename);
            EntityBlock entityBlock = UtilsBlockToEntityBlock.blockToEntityBlock(block);
            list.add(entityBlock);
            calculateBalance(balances, block, signs);


            //получение и отображение законов, а также сохранение новых законов
            //и изменение действующих законов
            allLaws = UtilsLaws.getLaws(block, Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE, allLaws);

        }
        BlockService.saveAllBlock(list);
        List<EntityAccount> entityBalances = UtilsAccountToEntityAccount
                .accountsToEntityAccounts(balances);
        BlockService.saveAccountAll(entityBalances);

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


        java.sql.Timestamp actualTime = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());


        Long result = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.MILLIS);
        System.out.println("addBlock 3: time: result: " + result);
        System.out.println(":BasisController: addBlock3: finish: " + originalBlocks.size());

    }


    /**TODO Устарел и нигде не используется. TODO Deprecated and not used anywhere.*/

    public static void addBlock2(List<Block> originalBlocks, Map<String, Account> balances) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {


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
        BlockService.saveAllBlock(entityBlocks);
        List<EntityAccount> entityBalances = UtilsAccountToEntityAccount
                .accountsToEntityAccounts(balances);
        BlockService.saveAccountAll(entityBalances);


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


    /**Если вы предполагаете что некоторые файлы повреждены, то после вызова данного метода, происходить
     * перерасчет всех файлов для всего блокчейна.
     * If you assume that some files are damaged, then after calling this method,
     *       * recalculation of all files for the entire blockchain.*/

    public static void getBlock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        int size = 0;

        System.out.println("start get a block");
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);


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
        BlockService.deletedAll();
        List<Block> list = new ArrayList<>();

        Map<String, Account> balances = new HashMap<>();

        while (true){
            if(size > Seting.PORTION_DOWNLOAD){
                list = blockchain.subBlock(size, Seting.PORTION_DOWNLOAD);

                addBlock3(list, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);

            }else {
                list = blockchain.subBlock(size, blockchain.sizeBlockhain());
                addBlock3(list, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                break;
            }
            Block block = list.get(list.size()-1);
            size = (int) (block.getIndex());

        }


        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        blockchainSize = (int) shortDataBlockchain.getSize();
        blockchainValid = shortDataBlockchain.isValidation();
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
        BasisController.getBlock();
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
    @RequestMapping("/resolving")
    public String resolving() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        resolve_conflicts();
        return "redirect:/";
    }


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
                System.out.println(":BasisController: sendAddress: wronge node: " + original);

                continue;
            }


        }
        System.out.println(":finish send addressess");
    }


    /**
     * Sends a list of blocks to central stores (example: http://194.87.236.238:82)
     * Отправляет список блоков в центральные хранилища (пример: http://194.87.236.238:82)
     */
    public static int sendAllBlocksToStorage(List<Block> blocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        System.out.println(new Date() + ":BasisController: sendAllBlocksToStorage: start: ");
        int bigsize = 0;
        int blocks_current_size = (int) blocks.get(blocks.size() - 1).getIndex() + 1;
        //отправка блокчейна на хранилище блокчейна
        System.out.println(":BasisController: sendAllBlocksToStorage: ");
        getNodes().stream().forEach(System.out::println);
        for (String s : getNodes()) {

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
                        System.out.println(":response: " + response);
                    } catch (Exception e) {
                        System.out.println(":exception resolve_from_to_block: " + originalF);

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
                    System.out.println(":response: " + response);

                    System.out.println(":BasisController: sendAllBlocksStorage: response: " + response);


                    //there is an up-to-date branch on the global server, download it and delete the obsolete branch.
                    //на глобальном сервере есть актуальная ветка, скачать ее и удалить устревшую ветку.
//                    if (response == HttpStatus.CONFLICT.value()) {
//                        System.out.println(":BasisController: sendAllBlocksStorage: start deleted 50 blocks:");
//                        System.out.println(":size before delete: " + blockchainSize);
//                       Blockchain blockchain1 = Mining.getBlockchain(
//                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                                BlockchainFactoryEnum.ORIGINAL);
//                        List<Block> temporary = blockchain1.subBlock(0, blockchainSize - Seting.DELETED_PORTION);
//                        UtilsBlock.deleteFiles();
//                        blockchain1.setBlockchainList(temporary);
//                        UtilsBlock.saveBlocks(blockchain1.getBlockchainList(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
//                        blockchain1 = Mining.getBlockchain(
//                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                                BlockchainFactoryEnum.ORIGINAL);
//
//                        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
//                        blockchainSize = (int) shortDataBlockchain.getSize();
//                        blockchainValid = shortDataBlockchain.isValidation();
//
//                        UtilsBlock.deleteFiles();
//                        addBlock(blockchain1.getBlockchainList());
//                        System.out.println(":size after delete: " + blockchainSize);
//
//
//                        int result = resolve();
//                        System.out.println(":resolve: updated: " + result);
//                    }


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


    /**Добывает блок и отправляет на сервер.
     * Mines a block and sends it to the server.*/

    public static synchronized String mining() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
        mining = true;
        try {
            findAddresses();
            resolve_conflicts();

            List<Block> tempBlockchain;

            tempBlockchain  = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                    BlockService.findBySpecialIndexBetween(
                            (blockchainSize) - Seting.PORTION_BLOCK_TO_COMPLEXCITY ,
                            blockchainSize-1
                    )
            );

            Block prevBlock = tempBlockchain.get(tempBlockchain.size()-1);
            long index = prevBlock.getIndex()+1;
            Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
            Account miner = balances.get(User.getUserAddress());
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
            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

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

                //получить список балансов из файла. get a list of balances from a file.
                List<String> signs = new ArrayList<>();
                balances = Mining.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain1, balances, signs);
                //удалить старые файлы баланса. delete old balance files.
                Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);

                //сохранить балансы. maintain balances.
                SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);
//

            }
            //скачать список балансов из файла. download a list of balances from a file.
            System.out.println("BasisController: minining: read list balance");
            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

            //получить счет майнера. get the miner's account.
            miner = balances.get(User.getUserAddress());
            minerShow = miner;
            System.out.println("BasisController: mining: account miner: " + miner);
            if (miner == null) {
                //если в блокчейне не было баланса майнера, то баланс равен нулю.
                //if there was no miner balance in the blockchain, then the balance is zero.
                miner = new Account(User.getUserAddress(), 0, 0, 0, 0);
            }

            //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions.
            //transactions that we added to the block and now need to be deleted from the file in the resources/transactions folder.
            List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();

            List<Block> temp
            =  UtilsBlockToEntityBlock.entityBlocksToBlocks(
                    BlockService.findBySpecialIndexBetween(
                            (blockchainSize) - Seting.CHECK_DTO,
                            blockchainSize-1
                    )
            );
            //удаляет транзакции которые были ранее уже добавлены в блок.
            //deletes transactions that were previously added to the block.
            temporaryDtoList = UtilsBlock.validDto(temp, temporaryDtoList);

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

            String addresFounder = BlockService.findBySpecialIndex(0).getFounderAddress();
            if (!block.getFounderAddress().equals(addresFounder)) {
                System.out.println("wrong address founder: ");
            }
            //проверяет блок на валидность, соответствует ли блок требованиям.
            //checks the block for validity, whether the block meets the requirements.
            if (testingValidationsBlock.size() > 1) {
                boolean validationTesting = UtilsBlock.validationOneBlock(
                        addresFounder,
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


    /**Отображает информационное табло во время майнинга или обновления.
     * Displays an information board during mining or updating.*/
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

    /**Отображает информационное табло и прекращает майнинг.
     * Displays an information board and stops mining.*/
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


    /**Инициализирует мета данные о внутреннем блокчейне. Включая размер, блокчейна, целость и общую сложность.
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


}


