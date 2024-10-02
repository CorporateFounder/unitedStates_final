package International_Trade_Union.utils;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.EntityChain;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.*;
import International_Trade_Union.model.comparator.HostEndDataShortBComparator;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.UtilsLaws;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static International_Trade_Union.controllers.BasisController.getNodes;
import static International_Trade_Union.controllers.BasisController.utilsMethod;
import static International_Trade_Union.setings.Seting.RANDOM_HOSTS;
import static International_Trade_Union.utils.UtilsBalance.calculateBalance;
import static International_Trade_Union.utils.UtilsBalance.rollbackCalculateBalance;

@Component
public class UtilsResolving {
    @Autowired
    BlockService blockService;

    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);
    }


    //механизм обновления, обновляет локальный блокчейн, если у него меньше bigRandom
    public int resolve3() {
        //переходит в режим обновления, что значит другие вкладки не будут открываться, если здесь true,
        BasisController.setUpdating(true);

        //передает в доступ к базе данных в h2,
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);

        //удаляет файлы, которые хранить заблокированные хосты
        if (BasisController.getBlockchainSize() % Seting.DELETED_FILE_BLOCKED_HOST == 0) {
            Mining.deleteFiles(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
        }

        int bigSize = 0;
        try {
            System.out.println(" :start resolve");
            utilsMethod();

            //local blockchain size
            //размер локального блокчейна
            int blocks_current_size = BasisController.getBlockchainSize();

            EntityChain entityChain = null;
            System.out.println(" resolve3:local size: " + blocks_current_size);


            System.out.println(":resolve3: size nodes: " + getNodes().size());
            //goes through all hosts (repositories) in search of the most up-to-date blockchain
            //проходит по всем хостам(хранилищам) в поисках самого актуального блокчейна

            //сортирует по приоритетности блокчейны
            Map<HostEndDataShortB, List<Block>> tempBestBlock = new HashMap<>();
            Set<String> nodesAll = getNodes();
            List<HostEndDataShortB> sortPriorityHost = sortPriorityHost(nodesAll);
            Set<String> newAddress = newHostsLoop(sortPriorityHost.stream().map(t -> t.getHost()).collect(Collectors.toSet()));
            newAddress.remove(nodesAll);
            //обновляет список хостов, если количество новых хостов больше 0
            if (newAddress.size() > 0) {
                Mining.deleteFiles(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
            }
            for (String s : newAddress) {
                UtilsAllAddresses.putHost(s);
            }


            //Проходит по всем хостам, в поисках, где количество big random больше
            //P.S. на кошельке подключается только к своему хосту и обновляется только у него
            hostContinue:
            for (HostEndDataShortB hostEndDataShortB : sortPriorityHost) {
                String s = hostEndDataShortB.getHost();
                String server = UtilsFileSaveRead.read(Seting.YOUR_SERVER);


                if (!server.isBlank() || !server.isEmpty()) {
                    Seting.ORIGINAL_ADDRESSES.removeAll(Seting.ORIGINAL_ADDRESSES);
                    Seting.ORIGINAL_ADDRESSES.add(server);
                    s = server;
                }

                //именно здесь ограничивает кошелек одним сервером
                for (String s1 : Seting.ORIGINAL_ADDRESSES) {
                    s = s1;
                }


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


                    //здесь устанавливает самый длинный блокчейн.
                    if (size > bigSize) {
                        bigSize = size;

                    }

                    //скачиваем мета данные с сервера
                    String jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                    System.out.println("jsonGlobalData: " + jsonGlobalData);
                    //если мета данные сервера пустные, то он пропускает этот сервер
                    if (jsonGlobalData == null || jsonGlobalData.isEmpty() || jsonGlobalData.isBlank()) {
                        System.out.println("*********************************************************");
                        System.out.println("jsonGlobalData: error: " + jsonGlobalData);
                        System.out.println("*********************************************************");
                        continue;
                    }
                    //мета данные с сервера
                    DataShortBlockchainInformation global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
//
                    System.out.println("resolve3 size: " + size + " blocks_current_size: " + blocks_current_size);

                    if (isBig(BasisController.getShortDataBlockchain(), global)) {
                        System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                        //Test start algorithm
                        //600 последних блоков, для подсчета сложности, для последнего блока.
                        List<Block> lastDiff = new ArrayList<>();
                        SubBlockchainEntity subBlockchainEntity = null;
                        String subBlockchainJson = null;

                        Map<String, Account> balances = new HashMap<>();
                        Map<String, Account> tempBalances = new HashMap<>();


                        //if the local one lags behind the global one by more than PORTION_DOWNLOAD (500 blocks), then you need to download in portions from the storage
                        //если локальный отстает от глобального больше чем PORTION_DOWNLOAD (500 блоков), то нужно скачивать порциями из хранилища
                        if (size - blocks_current_size > Seting.PORTION_DOWNLOAD) {
                            boolean downloadPortion = true;
                            int finish = blocks_current_size + Seting.PORTION_DOWNLOAD;
                            int start = blocks_current_size;

                            //while the difference in the size of the local blockchain is greater than from the host, it will continue to download in portions to download the entire blockchain
                            //пока разница размера локального блокчейна больше чем с хоста будет продолжаться скачивать порциями, чтобы скачать весь блокчейн
                            stop:
                            while (downloadPortion) {


                                boolean local_size_upper = false;
                                //TODO возможно это решит проблему, если блоки будут равны
                                //если высота одинаковая, или локальная blocks_current_size меньше глобальной size,
                                //то сработает метод helpResolve4
                                if (blocks_current_size == size) {
                                    //здесь говориться, с какого блока по какой блок скачивать.
                                    subBlockchainEntity = new SubBlockchainEntity(blocks_current_size - 1, size);

                                }

                                //Если локальный сервер высота больше, но ценность ниже чем у глобального
                                //срабатывает метод helpResolve5
                                if (size < blocks_current_size) {
                                    subBlockchainEntity = new SubBlockchainEntity(size - Seting.IS_BIG_DIFFERENT, size);
                                    System.out.println("subBlockchainEntity: size < blocks_current_size: " + subBlockchainEntity);
                                    local_size_upper = true;
                                }


                                subBlockchainEntity = new SubBlockchainEntity(start, finish);

                                System.out.println("1:shortDataBlockchain:  " + BasisController.getShortDataBlockchain());
                                System.out.println("1:sublockchainEntity: " + subBlockchainEntity);
                                subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                System.out.println("1:sublockchainJson: " + subBlockchainJson);
                                //скачивает блоки с сервера порциями.
                                String str = UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks");
                                if (str.isEmpty() || str.isBlank()) {
                                    System.out.println("-------------------------------------");
                                    System.out.println("sublocks:  str: empty " + str);
                                    System.out.println("-------------------------------------");
                                    continue;
                                }
                                List<Block> subBlocks = UtilsJson.jsonToListBLock(str);
                                //если порция блоков пустая, то пропускается.
                                if (subBlocks.isEmpty() || subBlocks.size() == 0) {
                                    System.out.println("-------------------------------------");
                                    System.out.println("sublocks: " + subBlocks.size());
                                    System.out.println("-------------------------------------");
                                    continue;
                                }
                                System.out.println("1:download sub block: " + subBlocks.size());
                                System.out.println("1: host: " + s);

                                //Если количество оставшихся блоков на сервере меньше 500, то срабатывает,
                                //а при этом сервер нам заявлял что на его сервере больше 500 блоков, то этот
                                //сервер блокируется, и пропускается. Срабатывает система безопасности.
                                if (Seting.IS_SECURITY == true && subBlocks.size() < Seting.PORTION_DOWNLOAD) {
                                    System.out.println("Blocked host: " + subBlocks.size());
                                    //TODO записывать сюда заблокированные хосты
                                    UtilsAllAddresses.saveAllAddresses(hostEndDataShortB.getHost(), Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
                                    continue hostContinue;
                                }

                                System.out.println("1. subBlocks: subBLock size - 1:" + (subBlocks.size() - 1));
                                System.out.println("1. finish: subBlocks: subBLock getIndex() + Seting.PORTION_DOWNLOAD + 1:" + (subBlocks.get(subBlocks.size() - 1).getIndex() + Seting.PORTION_DOWNLOAD + 1));
                                System.out.println("1. start: subBlocks: subBLockstart = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1;:" + (subBlocks.get(subBlocks.size() - 1).getIndex() + 1));

                                finish = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + Seting.PORTION_DOWNLOAD + 1;
                                start = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1;

                                //получает список балансов из базы данных h2, локально по списку адресов в блоке.
                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                //это аналогичный баланс, только используется для проверки.
                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));


                                //устаревшый участок кода, где ранее сложность вычислялось алгоритмом.
                                if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY && BasisController.getBlockchainSize() < Seting.V34_NEW_ALGO) {
                                    lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                            blockService.findBySpecialIndexBetween(
                                                    (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                    BasisController.getPrevBlock().getIndex() + 1
                                            )
                                    );
                                }


                                //класс мета данных блокчейна.
                                DataShortBlockchainInformation temp = new DataShortBlockchainInformation();

                                //загружает баланс всех счетов для текущего блокчейна.
                                List<String> sign = new ArrayList<>();
                                if (BasisController.getBlockchainSize() > 1) {
                                    //проверяет скаченные блоки на целостность
                                    //checks downloaded blocks for integrity
                                    Map<String, Account> balanceForValid = UtilsUse.balancesClone(balances);
                                    temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign, balanceForValid, new ArrayList<>());
                                    System.out.println("prevBlock: " + BasisController.getPrevBlock().getIndex());
                                } else {

                                    //если высота 1 или меньше, он инициализирует базовый блокчейн, генезис блоком для всех
                                    Block genesis = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":6.5E7,\"digitalStockBalance\":6.5E7,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIDDW9fKvwUY0aXpvamxOU6pypicO3eCqEVM9LDFrIpjIAiEA81Zh7yCBbJOLrAzx4mg5HS0hMdqvB0obO2CZARczmfY=\"}],\"previousHash\":\"0234a350f4d56ae45c5ece57b08c54496f372bc570bd83a465fb6d2d85531479\",\"minerAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685942742706,\"index\":1,\"hashBlock\":\"08b1e6634457a40d3481e76ebd377e76322706e4ea27013b773686f7df8f8a4c\"}");
                                    Block firstBlock = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":400.0,\"digitalStockBalance\":400.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDfQ3TAOyuWi4NGr0hNuXjqzxDCL0U8DzwAmedSOw9eiwIgdRlZwmudMZJZURMtgmOwpT+wk569jo/Ok/fAGv0x/NE=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDV9MbTMPl/dWBTfc87rMcRBBKcNZsGtkuRx1pdzGrSKQIhALOFpX81JEyFCC8uQ//bZkSW9CaOODSgOaMkYgTHn5HC\"}],\"previousHash\":\"08b1e6634457a40d3481e76ebd377e76322706e4ea27013b773686f7df8f8a4c\",\"minerAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":4,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685942784960,\"index\":1,\"hashBlock\":\"06b932aadd602056b0fb7294ef693535009cb3ba54581f32fd4aa1d93108703f\"}");

                                    if (!subBlocks.get(0).equals(genesis) || !subBlocks.get(1).equals(firstBlock)) {
                                        System.out.println("error basis block: ");
                                        System.out.println("genesis: " + subBlocks.get(0).getIndex());
                                        System.out.println("first: " + subBlocks.get(1).getIndex());
                                        System.out.println("temp: " + temp);
                                    }

                                    //записывает блоки в базу данных
                                    boolean save = addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE, new ArrayList<>());
                                    temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    if (!temp.isValidation()) {
                                        System.out.println("error validation: " + temp);
                                    }
                                    //производит запись мета данных.
                                    if (save) {
                                        BasisController.setShortDataBlockchain(temp);
                                        BasisController.setBlockchainSize((int) temp.getSize());
                                        BasisController.setBlockchainValid(temp.isValidation());

                                        EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                                        BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));
                                        System.out.println("prevBlock: " + BasisController.getPrevBlock().getIndex() + " shortDataBlockchain: " + BasisController.getShortDataBlockchain());
                                        String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                                        UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
//
                                    }
                                    continue hostContinue;

                                }


                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                                System.out.println("1: temp: " + temp);
                                System.out.println("1: blockchainsize: " + BasisController.getBlockchainSize());
                                System.out.println("1: sublocks: size: " + subBlocks.size());
                                System.out.println("1: shortDataBlockchain: " + BasisController.getShortDataBlockchain());
                                System.out.println("1: host: " + s);

                                //получение мета данных с сервера
                                jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                                if (jsonGlobalData == null || jsonGlobalData.isEmpty() || jsonGlobalData.isBlank()) {
                                    System.out.println("*********************************************************");
                                    System.out.println(" 2 jsonGlobalData: error: " + jsonGlobalData);
                                    System.out.println("*********************************************************");
                                    continue;
                                }
                                System.out.println("2: jsonGlobalData: " + jsonGlobalData);
                                global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
                                temp = new DataShortBlockchainInformation();
                                sign = new ArrayList<>();

                                //получает баланс с базы данных h2, локально из счетов в блоке.
                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                sign = new ArrayList<>();
                                temp = new DataShortBlockchainInformation();
                                Map<String, Account> balanceForValidation = UtilsUse.balancesClone(balances);

                                //вычисляет мета данные, и делает проверку целостности блокчейна, если блокчейн правильный то это записывается в мета данных
                                temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign, balanceForValidation, new ArrayList<>());

                                //получает баланс с базы данных h2, локально из счетов в блоке.
                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                sign = new ArrayList<>();
                                if (!local_size_upper) {
                                    System.out.println("===========================");
                                    System.out.println("!local_size_upper: " + !local_size_upper);
                                    System.out.println("===========================");
                                    //метод так же вычисляет мета данные и записывает в локальную базу данных как сами блоки,
                                    //так подсчитывает балансы счетов.
                                    temp = helpResolve(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, false);

                                }

                                if (local_size_upper) {
                                    System.out.println("===========================");
                                    System.out.println("local_size_upper: " + local_size_upper);
                                    System.out.println("===========================");
                                    //метод так же вычисляет мета данные и записывает в локальную базу данных как сами блоки,
                                    //так подсчитывает балансы счетов.
                                    temp = helpResolve(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, false);
                                }

                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                                //если скачанный блокчейн не валидный, то не добавляет в блокчейн, возвращает -10
                                if (BasisController.getBlockchainSize() > 1 && !temp.isValidation()) {
                                    System.out.println("error resolve 2 in portion upper > 500");
                                    return -10;
                                }


                                //если количество новых блоков, относительно локального блокчейна меньше 500,
                                //то скачать эти блоки и прекратить попытки скачивания с данного узла.
                                //if the number of new blocks relative to the local blockchain is less than 500,
                                //then download these blocks and stop trying to download from this node.
                                if (size - BasisController.getPrevBlock().getIndex() < Seting.PORTION_DOWNLOAD) {


                                    local_size_upper = false;
                                    //срабатывает helpResolve4 если высота локальная и глобальная одинаковая,
                                    //или если локальная меньше.
                                    if (blocks_current_size == size) {
                                        subBlockchainEntity = new SubBlockchainEntity(blocks_current_size - 1, size);

                                    }
                                    //срабатывает helpResolve5 если локальная имеет большую высоту, но меньшую ценность.
                                    if (size < blocks_current_size) {
                                        subBlockchainEntity = new SubBlockchainEntity(size - Seting.IS_BIG_DIFFERENT, size);
                                        System.out.println("subBlockchainEntity: size < blocks_current_size: " + subBlockchainEntity);
                                        local_size_upper = true;
                                    }

                                    downloadPortion = false;
                                    finish = size;
                                    subBlockchainEntity = new SubBlockchainEntity(start, finish);
                                    System.out.println("2:sublockchainEntity: " + subBlockchainEntity);
                                    subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                    System.out.println("2:sublockchainJson: " + subBlockchainJson);
                                    str = UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks");
                                    if (str.isEmpty() || str.isBlank()) {
                                        System.out.println("-------------------------------------");
                                        System.out.println("sublocks:  str: empty " + str);
                                        System.out.println("-------------------------------------");
                                        continue;
                                    }
                                    subBlocks = UtilsJson.jsonToListBLock(str);

                                    if (subBlocks == null || subBlocks.isEmpty() || subBlocks.size() == 0) {
                                        System.out.println("-------------------------------------");
                                        System.out.println("sublocks: " + subBlocks.size());
                                        System.out.println("-------------------------------------");
                                        continue;
                                    }
                                    System.out.println("2:download sub block: " + subBlocks.size());
                                    System.out.println("2: host: " + s);

                                    if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY && BasisController.getBlockchainSize() < Seting.V34_NEW_ALGO) {
                                        lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                                blockService.findBySpecialIndexBetween(
                                                        (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                        BasisController.getPrevBlock().getIndex() + 1
                                                )
                                        );
                                    }


                                    jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                                    if (jsonGlobalData == null || jsonGlobalData.isEmpty() || jsonGlobalData.isBlank()) {
                                        System.out.println("*********************************************************");
                                        System.out.println(" 2 jsonGlobalData: error: " + jsonGlobalData);
                                        System.out.println("*********************************************************");
                                        continue;
                                    }
                                    System.out.println("2: jsonGlobalData: " + jsonGlobalData);
                                    global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);

                                    //получение баланса из базы данных локально h2, на базе адресов в списке блоков subBlocks
                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    sign = new ArrayList<>();
                                    temp = new DataShortBlockchainInformation();
                                    balanceForValidation = UtilsUse.balancesClone(balances);
                                    //подсчет мета данных и проверка целостности блокчейна, скачиваемого из сервера.
                                    temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign, balanceForValidation, new ArrayList<>());


                                    System.out.println("2: temp: " + temp);
                                    System.out.println("2: blockchainsize: " + BasisController.getBlockchainSize());
                                    System.out.println("2: sublocks: " + subBlocks.size());
                                    System.out.println("2: host: " + s);

                                    temp = new DataShortBlockchainInformation();
                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    sign = new ArrayList<>();
                                    if (!local_size_upper) {
                                        System.out.println("===========================");
                                        System.out.println("!local_size_upper: " + !local_size_upper);
                                        System.out.println("===========================");
                                        temp = helpResolve(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);

                                    }

                                    if (local_size_upper) {
                                        System.out.println("===========================");
                                        System.out.println("local_size_upper: " + local_size_upper);
                                        System.out.println("===========================");
                                        temp = helpResolve(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);
                                    }


                                    if (Seting.IS_SECURITY && BasisController.getBlockchainSize() > 1 && !temp.isValidation()) {
                                        //TODO добавить хост в заблокированный файл

                                        System.out.println("-------------------------------------------------");
                                        System.out.println("Blocked host: ");
                                        System.out.println("expected host: " + hostEndDataShortB.getDataShortBlockchainInformation());
                                        System.out.println("host: " + hostEndDataShortB.getHost());

                                        System.out.println("-------------------------------------------------");
                                        UtilsAllAddresses.saveAllAddresses(hostEndDataShortB.getHost(), Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
                                        continue;
                                    }

                                }
                            }
                        } else {

                            //этот блок срабатывает изначально если порция была меньше 500.
                            //Весь код схожий аналогично предыдущим блокам.
                            subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);

                            boolean different_value = false;
                            boolean local_size_upper = false;
                            //TODO возможно это решит проблему, если блоки будут равны
                            if (blocks_current_size == size) {
                                subBlockchainEntity = new SubBlockchainEntity(blocks_current_size - 1, size);

                            }
                            if (size < blocks_current_size) {
                                subBlockchainEntity = new SubBlockchainEntity(size - Seting.IS_BIG_DIFFERENT, size);
                                System.out.println("subBlockchainEntity: size < blocks_current_size: " + subBlockchainEntity);
                                local_size_upper = true;
                            }


                            subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);


                            String str = UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks");
                            if (str.isEmpty() || str.isBlank()) {
                                System.out.println("-------------------------------------");
                                System.out.println("sublocks:  str: empty " + str);
                                System.out.println("-------------------------------------");
                                continue;
                            }
                            List<Block> subBlocks = UtilsJson.jsonToListBLock(str);

                            if (subBlocks == null || subBlocks.isEmpty() || subBlocks.size() == 0) {
                                System.out.println("-------------------------------------");
                                System.out.println("sublocks: " + subBlocks.size());
                                System.out.println("-------------------------------------");
                                continue;
                            }
                            System.out.println("3:download sub block: " + subBlocks.size());


                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                            Map<String, Account> validationBalance = UtilsUse.balancesClone(tempBalances);

                            if (tempBalances == null || tempBalances.isEmpty()) {
                                continue;
                            }
                            List<String> sign = new ArrayList<>();

                            if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY && BasisController.getBlockchainSize() < Seting.V34_NEW_ALGO) {
                                lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                        blockService.findBySpecialIndexBetween(
                                                (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                BasisController.getPrevBlock().getIndex() + 1
                                        )
                                );
                            }

                            DataShortBlockchainInformation temp = new DataShortBlockchainInformation();
                            temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign, validationBalance, new ArrayList<>());

                            jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                            if (jsonGlobalData == null || jsonGlobalData.isEmpty() || jsonGlobalData.isBlank()) {
                                System.out.println("*********************************************************");
                                System.out.println("jsonGlobalData: error: " + jsonGlobalData);
                                System.out.println("*********************************************************");
                                continue;
                            }
                            System.out.println("3: jsonGlobalData: " + jsonGlobalData);
                            global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);


                            System.out.println("3: temp: " + temp);
                            System.out.println("3: blockchainsize: " + BasisController.getBlockchainSize());
                            System.out.println("3: sublocks: " + subBlocks.size());


                            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                            sign = new ArrayList<>();
                            if (!local_size_upper) {
                                System.out.println("===========================");
                                System.out.println("!local_size_upper: " + !local_size_upper);
                                System.out.println("===========================");
                                temp = helpResolve(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);

                            }

                            if (local_size_upper) {
                                System.out.println("===========================");
                                System.out.println("local_size_upper: " + local_size_upper);
                                System.out.println("===========================");
                                temp = helpResolve(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);
                            }


                            if (!temp.isValidation()) {
                                continue;
                            }

                        }
                        System.out.println("size temporaryBlockchain: ");
                        System.out.println("resolve: temporaryBlockchain: ");
                    } else {
                        System.out.println(":BasisController: resove: size less: " + size + " address: " + s);
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    continue;
                }
            }

        } finally {

            BasisController.setUpdating(false);
            if (BasisController.getBlockchainSize() > bigSize) {
                return 1;
            } else if (BasisController.getBlockchainSize() < bigSize) {
                return -1;
            } else {
                return 0;
            }

        }


    }

    /**
     * метод в кошельке обновляется если ценность кошелька отличается от сервера
     */
    public boolean isBig(
            DataShortBlockchainInformation actual,
            DataShortBlockchainInformation global) {
        if (global.getBigRandomNumber() != actual.getBigRandomNumber())
            return true;
        else return false;

    }


    public boolean isSmall(DataShortBlockchainInformation expected, DataShortBlockchainInformation actual) {
        if (
                actual.getSize() < expected.getSize()
                        || actual.getBigRandomNumber() < expected.getBigRandomNumber()
        ) {
            return true;
        }
        return false;
    }




    /**
     * метод срабатывает если высотка локального блокчейна меньше или равно относительно глобального сервера
     */
    public DataShortBlockchainInformation helpResolve(DataShortBlockchainInformation temp,
                                                      DataShortBlockchainInformation global,
                                                      String s,
                                                      List<Block> lastDiff,
                                                      Map<String, Account> tempBalances,
                                                      List<String> sign,
                                                      Map<String, Account> balances,
                                                      List<Block> subBlocks,
                                                      boolean checking)
            throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, SignatureException,
            InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        //TODO сначала найти блок откуда начинается ответление и докуда

        Map<String, Account> tempBalance = UtilsUse.balancesClone(tempBalances);

        //если мета данные полученные не правильны, то он делает дополнительную проверку,
        //чтобы выяснить, какие блоки стоит удалить из локальной цепочки и откатить, чтобы
        //откатить блоки, мета данные и баланс, и перейти на более ценную ветку.
        if (BasisController.getShortDataBlockchain().getSize() > 1 && !temp.isValidation()) {
            System.out.println("__________________________________________________________");

            List<Block> emptyList = new ArrayList<>();
            List<Block> different = new ArrayList<>();

            int lastBlockIndex = (int) (global.getSize() - 1);
            int currentIndex = lastBlockIndex;
            //TODO тестовая версия, мы проверяем если блокчейн ценее, но при этом меньше
            if (global.getSize() < BasisController.getBlockchainSize()) {
                List<EntityBlock> entityBlocks = blockService.findBySpecialIndexBetween(global.getSize(), BasisController.getBlockchainSize() - 1);
                different.addAll(UtilsBlockToEntityBlock.entityBlocksToBlocks(entityBlocks));
            }
            try {

                //здесь скачиваются блоки порциями, чтобы вычислить точку пересечения, когда они разделились
                stop:
                while (currentIndex >= 0) {

                    int startIndex = Math.max(currentIndex - 499, 0);
                    int endIndex = currentIndex;


                    SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(startIndex, endIndex);
                    String subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);

                    List<Block> blockList = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                    System.out.println("subBlockchainEntity: " + subBlockchainEntity);

                    blockList = blockList.stream().sorted(Comparator.comparing(Block::getIndex).reversed()).collect(Collectors.toList());
                    for (Block block : blockList) {
                        System.out.println("helpResolve4: block index: " + block.getIndex());

                        //Если блок имеет индекс выше высоты локального, он автоматически добавляется в список.
                        //Если в локальном уже есть блок с таким индексом, они оба добавляются в свои списки.
                        //Список emptyList это блоки для сохранения блоков из цепочки глобального блокчейна
                        //Список different это блоки которые нужно удалить из локального блокчейна.
                        if (block.getIndex() > BasisController.getBlockchainSize() - 1) {
                            System.out.println("helpResolve4 :download blocks: " + block.getIndex() +
                                    " your block : " + (BasisController.getBlockchainSize()) + ":waiting need download blocks: " + (block.getIndex() - BasisController.getBlockchainSize())
                                    + " host: " + s);
                            emptyList.add(block);
                        } else if (!blockService.findBySpecialIndex(block.getIndex()).getHashBlock().equals(block.getHashBlock())) {
                            emptyList.add(block);
                            different.add(UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(block.getIndex())));
                            System.out.println("********************************");
                            System.out.println(":dowdnload block index: " + block.getIndex());
                            System.out.println(":block original index: " + blockService.findBySpecialIndex(block.getIndex()).getIndex());
                            System.out.println(":block from index: " + block.getIndex());
                        } else {
                            // Останавливаем итерацию, т.к. дальнейшие блоки будут идентичными
                            break stop;
                        }
                    }

                    // Обновляем индекс для следующей итерации
                    currentIndex = startIndex - 1;
                }

            } catch (Exception e) {
                System.out.println("******************************");
                System.out.println("connecting exeption helpresolve4: address: " + s);
                System.out.println("******************************");
                return temp;
            }

            for (int i = 0; i < different.size(); i++) {
                System.out.println("different: " + different.get(i).getIndex());
                MyLogger.saveLog("different: " + different.get(i).getIndex());
            }
            if (emptyList.isEmpty()) {
                return temp;
            }
            System.out.println("different: ");
            if (different.isEmpty() && emptyList.isEmpty()) {
                return temp;
            }

            //получаем баланс всех счетов из локальной базы h2, на основе счетов которые присутствуют в списке блоков.
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(different, blockService)));
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(emptyList, blockService)));
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(lastDiff, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(different, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(emptyList, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(lastDiff, blockService)));

            System.out.println("shortDataBlockchain: " + BasisController.getShortDataBlockchain());
            System.out.println("rollback temp: " + temp);

            //сортируем блоки по возрастанию
            different = different.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            Block tempPrevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(different.get(0).getIndex() - 1));

            //откатываем мета данные
            temp = Blockchain.rollBackShortCheck(different, BasisController.getShortDataBlockchain(), tempBalance);
            //баланс для валидации мета данных, этот список баланса используется для проверки, достаточно ли было денег у отправителей
            //во время отправки денег.
            Map<String, Account> balanceForValidation = UtilsUse.balancesClone(balances);

            for (int i = different.size() - 1; i >= 0; i--) {
                Block block = different.get(i);

                //откатывает баланс на основе блоков.
                balanceForValidation = rollbackCalculateBalance(balanceForValidation, block);

            }

            //здесь подсчитывает целостность мета данных, а также целостность блокчейна, так же balanceForValidation используется
            //для проверки достаточно ли баланса было у отправителей в блоках.
            Base base = new Base58();
            List<String> signaturesNotTakenIntoAccount = new ArrayList<>();
            for (Block block : different) {
                List<String> tempSign = block.getDtoTransactions().stream().map(t->base.encode(t.getSign())).collect(Collectors.toList());
                signaturesNotTakenIntoAccount.addAll(tempSign);
            }

            for (Block block : emptyList) {
                List<Block> tempList = new ArrayList<>();
                tempList.add(block);
                temp = Blockchain.shortCheck(tempPrevBlock, tempList, temp, lastDiff, tempBalance, sign, balanceForValidation, signaturesNotTakenIntoAccount);
                tempPrevBlock = block;
            }

            //блокирует хосты серверов, которые намерено или случайно соврали о своей ценности.
            if (Seting.IS_SECURITY == true && checking && isSmall(global, temp)) {
                String expectedJson = "global: " + UtilsJson.objToStringJson(global);
                String actualJson = "actual: " + UtilsJson.objToStringJson(temp);
                UtilsFileSaveRead.save(expectedJson + "\n " + actualJson, Seting.ERROR_FILE, true);

                UtilsAllAddresses.saveAllAddresses(s, Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);

                temp.setValidation(false);
                return temp;
            }


            System.out.println("after rollback: " + temp);
            //если после перерасчета, подтвердиться что другая ветка более ценная
            // и она целая, то включается этот блок, который записывает окончательно новую цепочку,
            //удаляя при этом блоки, где произошло ответвление цепочек.
            if (temp.isValidation()) {
                System.out.println("------------------------------------------");
                System.out.println("rollback");
                try {
                    boolean save;
                    boolean result = true;
                    //метод rollBackAddBlock3 откатывает баланс локальный, если он правильно выполнил,
                    //то он вернет список балансов которые откатились.
                    Map<String, Account> tempBalanc = rollBackAddBlock3(different, emptyList, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                    if (tempBalanc.size() > 0) {
                        //если метод rollBackAddBlock3 откатил балансы, то он сохранит этот список балансов с изменениями,
                        //а так же список блоков.
                        result = addBlock3(emptyList, tempBalanc, Seting.ORIGINAL_BLOCKCHAIN_FILE, signaturesNotTakenIntoAccount);

                    } else {
                        result = false;
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    }

                    //если сохранение произошло успешно, то мета данные так же записываются успешно
                    if (result) {
                        BasisController.setShortDataBlockchain(temp);
                        BasisController.setBlockchainSize((int) temp.getSize());
                        BasisController.setBlockchainValid(temp.isValidation());
                        EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                        BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));
                        String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                        UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                        return temp;
                    }


                    System.out.println("------------------------------------------");
                    System.out.println("emptyList start index: " + emptyList.get(0).getIndex());
                    System.out.println("emptyList finish index: " + emptyList.get(emptyList.size() - 1).getIndex());
                    System.out.println("==========================================");
                    System.out.println("different start index: " + different.get(0).getIndex());
                    System.out.println("different finish index: " + different.get(different.size() - 1).getIndex());
                    System.out.println("------------------------------------------");
                } catch (Exception e) {
                    System.out.println("******************************");
                    System.out.println("elpresolve4: address: " + s);
                    e.printStackTrace();
                    System.out.println("******************************");
                }
                System.out.println("------------------------------------------");
                System.out.println("helpResolve4: temp: " + temp);
                System.out.println("------------------------------------------");
            } else {
                return temp;
            }


        } else if (BasisController.getShortDataBlockchain().getSize() > 1 && temp.isValidation()) {
            //вызывает методы, для сохранения списка блоков в текущий блокчейн,
            //так же записывает в базу h2, делает перерасчет всех балансов,
            //и так же их записывает, а так же записывает другие данные.
            //Этот блок сохраняет не достающие блоки, если изначально не было разделение веток,
            //но при этом высота глобального блокчейна была выше и тем саммым ценее.

            //TODO проверка теперь будет происходит уже сразу и при скачивании.
            if (Seting.IS_SECURITY == true && checking && isSmall(global, temp)) {
                System.out.println("host: " + s);
                String expectedJson = "global: " + UtilsJson.objToStringJson(global);
                String actualJson = "actual: " + UtilsJson.objToStringJson(temp);
                UtilsFileSaveRead.save(expectedJson + "\n " + actualJson, Seting.ERROR_FILE, true);

                UtilsAllAddresses.saveAllAddresses(s, Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
                temp.setValidation(false);
                return temp;
            }

            boolean save = addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE, new ArrayList<>());

            if (save) {
                BasisController.setShortDataBlockchain(temp);
                BasisController.setBlockchainSize((int) temp.getSize());
                BasisController.setBlockchainValid(temp.isValidation());

                EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));

                String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                return temp;
            }
        }


        System.out.println("__________________________________________________________");

        return temp;
    }


    @Transactional
    public Map<String, Account> rollBackAddBlock3(List<Block> deleteBlocks, List<Block> saveBlocks, Map<String, Account> balances, String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {

        MyLogger.saveLog("start: rollBackAddBlock3");
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        //сортирует блоки по индексу возрастания.
        deleteBlocks = deleteBlocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());

        //список блоков, которые нужно удалить из файла блокчейна, так как блокчейн храниться в двух экземплярах
        //как в базе данных h2, так и в файле.
        long threshold = deleteBlocks.get(0).getIndex();
        if (threshold <= 0) {

            return new HashMap<>();
        }

        //получает на базе индекса блока, с какого файла, начать удалять все последующие файлы
        //каждый файл имеет индекс по возрастанию, и размер не больше 2 мегабайт.
        File file = Blockchain.indexNameFileBlock((int) threshold, filename);

        if (file == null) {

            return new HashMap<>();
        }

        List<Block> tempBlock = Collections.synchronizedList(new ArrayList<>());
        try (Stream<String> lines = Files.lines(file.toPath())) {
            List<Block> finalTempBlock = tempBlock;
            lines.parallel().forEach(line -> {
                try {
                    Block block = UtilsJson.jsonToBLock(line);
                    if (block.getIndex() < threshold) {
                        finalTempBlock.add(block);
                    }
                } catch (JsonProcessingException e) {
                }
            });
        }

        try {
            //откатывает баланс, исходя из блоков предназначенных для удаления.
            for (int i = deleteBlocks.size() - 1; i >= 0; i--) {
                Block block = deleteBlocks.get(i);
                MyLogger.saveLog("----------------------------------");
                MyLogger.saveLog("rollBackAddBlock3 index: " + block.getIndex());
                MyLogger.saveLog("balance before: " + balances);
                balances = rollbackCalculateBalance(balances, block);
                MyLogger.saveLog("after before: " + balances);
                MyLogger.saveLog("----------------------------------");
            }


            //записывает новый баланс, уже сделавший откат
            blockService.saveAccountAllF(UtilsAccountToEntityAccount.accountsToEntityAccounts(balances));
            //удаляет блоки из базы данных h2,
            blockService.deleteEntityBlocksAndRelatedData(threshold);

        } catch (Throwable e) {
            MyLogger.saveLog("error tournament: ", e);
            MyLogger.saveLog("error rollback4: tempBlock index 0: " + tempBlock.get(0).getIndex());
            MyLogger.saveLog("error rollback4: saveBlocks index 0: " + saveBlocks.get(0).getIndex());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new HashMap<>();
        }


        allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        java.sql.Timestamp actualTime = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());

        Blockchain.deleteFileBlockchain(Integer.parseInt(file.getName().replace(".txt", "")), Seting.ORIGINAL_BLOCKCHAIN_FILE);
        tempBlock = tempBlock.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());

        //фиксирует в файле блоки которые остались, то есть он записывает в файл без перерасчета блоки в файловую систему
        UtilsBlock.saveBlocks(tempBlock, filename);


        MyLogger.saveLog("rollBackAddBlock3 finish");

        //возвращает баланс который откатился.
        return balances;
    }


    /**
     * rewrites the blockchain into files and into the h2 database. From here they are called
     * * methods that calculate balance and other calculations.
     * производит перезапись блокчейна в файлы и в базу h2. Отсюда вызываются
     * методы которые, вычисляют баланс и другие вычисления.
     */


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean addBlock3(List<Block> originalBlocks, Map<String, Account> balances, String filename, List<String> signaturesNotTakenIntoAccount) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        java.sql.Timestamp lastIndex = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());
        UtilsBalance.setBlockService(blockService);
        Blockchain.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);
        List<EntityBlock> list = new ArrayList<>();
        List<String> signs = new ArrayList<>();
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        originalBlocks = originalBlocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());

        Map<String, Account> tempBalances = UtilsUse.balancesClone(balances);
        long start = UtilsTime.getUniversalTimestamp();


        for (Block block : originalBlocks) {
            System.out.println(" :BasisController: addBlock3: blockchain is being updated: index" + block.getIndex());

            EntityBlock entityBlock = UtilsBlockToEntityBlock.blockToEntityBlock(block);
            list.add(entityBlock);
            //посчитывает баланс на основе блока
            balances =  calculateBalance(balances, block, signs, signaturesNotTakenIntoAccount);
        }
        list = list.stream().sorted(Comparator.comparing(EntityBlock::getSpecialIndex)).collect(Collectors.toList());

        long finish = UtilsTime.getUniversalTimestamp();
        System.out.println("UtilsResolving: addBlock3: for: time different: " + UtilsTime.differentMillSecondTime(start, finish));
        try {


            //находит счета, которые изменились после перерасчета
            tempBalances = UtilsUse.differentAccount(tempBalances, balances);
            //из базы данных вытаскивает счета которые изменились во время перерасчета
            List<EntityAccount> accountList = blockService.findByAccountIn(tempBalances);
            //объединяет два типа, счетов, счета из базы данных изменяются в соответствии с изменениями, но сохраняется ID
            accountList = UtilsUse.mergeAccounts(tempBalances, accountList);

            start = UtilsTime.getUniversalTimestamp();
            //записывает в базу данных счета
            blockService.saveAccountAllF(accountList);
            //записывает блоки в базу данных
            blockService.saveAllBLockF(list);
            finish = UtilsTime.getUniversalTimestamp();
        } catch (Exception e) {

            String stackerror = "";
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                stackerror += stackTraceElement.toString() + "\n";
            }
            MyLogger.saveLog("stackerror: " + stackerror);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;

        }

        System.out.println("UtilsResolving: addBlock3: time save accounts: " + UtilsTime.differentMillSecondTime(start, finish));
        System.out.println("UtilsResolving: addBlock3: total different balance: " + tempBalances.size());
        System.out.println("UtilsResolving: addBlock3: total original balance: " + balances.size());

        //записывает блок в базу данных

        UtilsBlock.saveBlocks(originalBlocks, filename);
        allLaws = UtilsLaws.getLaws(originalBlocks, Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE, allLaws);
        allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        Mining.deleteFiles(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

        java.sql.Timestamp actualTime = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());
        Long result = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.MILLIS);
        System.out.println("addBlock 3: time: result: " + result);
        System.out.println(":BasisController: addBlock3: finish: " + originalBlocks.size());
        return true;
    }
    public List<HostEndDataShortB> sortPriorityHostOriginal(Set<String> hosts) throws IOException, JSONException {
        List<HostEndDataShortB> list = new ArrayList<>();
        for (String s : hosts) {
            try {
                String jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                System.out.println("jsonGlobalData: " + jsonGlobalData);
                DataShortBlockchainInformation global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
                if (global.isValidation()) {
                    HostEndDataShortB dataShortB = new HostEndDataShortB(s, global);
                    list.add(dataShortB);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }


        }

        //сортировать здесь.
        // сортировка
        Collections.sort(list, new HostEndDataShortBComparator());
        return list;
    }

    /**
     * Записывает Блоки и баланс во временный файл.
     */
    public List<HostEndDataShortB> sortPriorityHost(Set<String> hosts) {

        // Добавляем ORIGINAL_ADDRESSES к входящему набору хостов
        Set<String> modifiedHosts = new HashSet<>(hosts);
        modifiedHosts.addAll(Seting.ORIGINAL_ADDRESSES);

        // Отбираем случайные 10 хостов
        Set<String> selectedHosts = modifiedHosts.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        listHost -> {
                            Collections.shuffle(listHost);
                            return listHost.stream().limit(RANDOM_HOSTS).collect(Collectors.toSet());
                        }
                ));


        List<CompletableFuture<HostEndDataShortB>> futures = new ArrayList<>(); // Список для хранения CompletableFuture

        // Вывод информации о начале метода
        System.out.println("start: sortPriorityHost: " + selectedHosts);

        // Перебираем все хосты
        for (String host : selectedHosts) {
            // Создаем CompletableFuture для каждого хоста
            CompletableFuture<HostEndDataShortB> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // Вызов метода для получения данных из источника
                    DataShortBlockchainInformation global = fetchDataShortBlockchainInformation(host);
                    // Если данные действительны, создаем объект HostEndDataShortB
                    if (global != null && global.isValidation()) {
                        return new HostEndDataShortB(host, global);
                    }
                } catch (IOException | JSONException e) {
                    // Перехват и логирование ошибки
                    logError("Error while retrieving data for host: " + host, e);

                }
                return null;
            });

            // Добавление CompletableFuture в список
            futures.add(future);
        }

        // Получение CompletableFuture, которые будут завершены
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // Создание CompletableFuture для обработки завершенных результатов
        CompletableFuture<List<HostEndDataShortB>> allComplete = allFutures.thenApplyAsync(result -> {
            // Получение результатов из CompletableFuture, фильтрация недействительных результатов и сборка в список
            return futures.stream()
                    .map(CompletableFuture::join)
                    .filter(result1 -> result1 != null)
                    .collect(Collectors.toList());
        });

        // Получение итогового списка
        List<HostEndDataShortB> resultList = allComplete.join();

        // Сортировка списка по приоритету
        Collections.sort(resultList, new HostEndDataShortBComparator());

        // Вывод информации о завершении метода
        System.out.println("finish: sortPriorityHost: " + resultList);

        resultList = resultList.stream().filter(UtilsUse.distinctByKey(HostEndDataShortB::getHost)).collect(Collectors.toList());
        // Возвращение итогового списка
        return resultList;
    }

    // Метод для получения данных из источника
    private DataShortBlockchainInformation fetchDataShortBlockchainInformation(String host) throws IOException, JSONException {
        // Загрузка JSON данных с URL
        String jsonGlobalData = UtilUrl.readJsonFromUrl(host + "/datashort");
        // Вывод загруженных данных
        System.out.println("jsonGlobalData: " + jsonGlobalData);
        // Преобразование JSON данных в объект
        return UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
    }

    //скачивает хосты из других узлов
    public Set<String> newHosts(String host) throws JSONException, IOException {
        String addresses = UtilUrl.readJsonFromUrl(host + "/getNodes");
        // Вывод загруженных данных
        System.out.println("jsonGlobalData: " + addresses);
        // Преобразование JSON данных в объект
        return UtilsJson.jsonToSetAddresses(addresses);
    }

    public Set<String> newHostsLoop(Set<String> hosts) throws JSONException, IOException {
        Set<String> addresses = new HashSet<>();
        for (String s : hosts) {
            addresses.addAll(newHosts(s));
        }
        return addresses;
    }

    // Метод для логирования ошибки
    private void logError(String message, Exception e) {
        // Вывод ошибки и сообщения
        System.out.println("-----------------------------------");
        System.out.println("Ошибка: " + message);
        // Вывод стека вызовов исключения
        if (e != null) {
            e.printStackTrace();
        }
        // Завершение логирования
        System.out.println("-----------------------------------");
    }

}

