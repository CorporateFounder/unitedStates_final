package International_Trade_Union.utils;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.EntityChain;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.repository.EntityAccountRepository;
import International_Trade_Union.entity.repository.EntityBlockRepository;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.HostEndDataShortB;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.comparator.HostEndDataShortBComparator;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.UtilsLaws;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static International_Trade_Union.controllers.BasisController.getNodes;
import static International_Trade_Union.controllers.BasisController.utilsMethod;
import static International_Trade_Union.setings.Seting.RANDOM_HOSTS;
import static International_Trade_Union.utils.UtilsBalance.calculateBalance;
import static International_Trade_Union.utils.UtilsBalance.rollbackCalculateBalance;

@Component
public class UtilsResolving {
    @Autowired
    BlockService blockService;
    @Autowired
    EntityAccountRepository entityAccountRepository;
    @Autowired
    EntityBlockRepository entityBlockRepository;


    public int resolve3() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        BasisController.setUpdating(true);
        UtilsBalance.setBlockService(blockService);
        Blockchain.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);

        //удаляет файлы которые хранять заблокированные хосты
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
            Set<String> nodesAll = getNodes();
            //сортирует по приоритетности блокчейны
            Map<HostEndDataShortB, List<Block>> tempBestBlock = new HashMap<>();

            List<HostEndDataShortB> sortPriorityHost = sortPriorityHost(nodesAll);
            Set<String> newAddress = newHostsLoop(sortPriorityHost.stream().map(t -> t.getHost()).collect(Collectors.toSet()));
            newAddress.remove(nodesAll);

            for (String s : newAddress) {
                UtilsAllAddresses.putHost(s);
            }


            hostContinue:
            for (HostEndDataShortB hostEndDataShortB : sortPriorityHost) {
                String s = hostEndDataShortB.getHost();
                String server = UtilsFileSaveRead.read(Seting.YOUR_SERVER);


                if(!server.isBlank() || !server.isEmpty()){
                    Seting.ORIGINAL_ADDRESSES.removeAll(Seting.ORIGINAL_ADDRESSES);
                    Seting.ORIGINAL_ADDRESSES.add(server);
                    s = server;
                }
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


                    String jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                    System.out.println("jsonGlobalData: " + jsonGlobalData);
                    DataShortBlockchainInformation global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
//
                    //if the size from the storage is larger than on the local server, start checking
                    //если размер с хранилища больше чем на локальном сервере, начать проверку
                    System.out.println("resolve3 size: " + size + " blocks_current_size: " + blocks_current_size);

                    if (isBig(BasisController.getShortDataBlockchain(), global)) {
                        System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                        //Test start algorithm
                        //600 последних блоков, для подсчета сложности, для последнего блока.
                        List<Block> lastDiff = new ArrayList<>();
                        SubBlockchainEntity subBlockchainEntity = null;
                        String subBlockchainJson = null;
//                        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
//                        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                        Map<String, Account> balances = new HashMap<>();
//                        Map<String, Account> tempBalances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
//                        Map<String, Account> tempBalances = UtilsUse.balancesClone(balances);
//                        Map<String, Account> tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
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
                                //здесь говориться, с какого блока по какой блок скачивать.

                                boolean different_value = false;
                                boolean local_size_upper = false;
                                //TODO возможно это решит проблему, если блоки будут равны
                                if (blocks_current_size == size) {
                                    subBlockchainEntity = new SubBlockchainEntity(blocks_current_size - 1, size);
                                    different_value = true;
                                }
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
                                List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                System.out.println("1:download sub block: " + subBlocks.size());
                                System.out.println("2: host: " + s);
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
                                start = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1; //вот здесь возможно сделать + 2


//                                balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
//                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));

                                //вычисляет сложность блока, для текущего блока, на основе предыдущих блоков.
                                //select a block class for the current block, based on previous blocks.

                                if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY && BasisController.getBlockchainSize() < Seting.V34_NEW_ALGO) {
                                    lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                            blockService.findBySpecialIndexBetween(
                                                    (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                    BasisController.getPrevBlock().getIndex() + 1
                                            )
                                    );
                                }

                                if (Seting.IS_SECURITY == true && subBlocks.size() < Seting.PORTION_DOWNLOAD) {
                                    System.out.println("Blocked host: size block:" + subBlocks.size());
                                    //TODO записывать сюда заблокированные хосты
                                    UtilsAllAddresses.saveAllAddresses(hostEndDataShortB.getHost(), Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
                                    continue hostContinue;

                                }
                                //класс мета данных блокчейна.
                                DataShortBlockchainInformation temp = new DataShortBlockchainInformation();

                                //загружает баланс всех счетов для текущего блокчейна.
                                List<String> sign = new ArrayList<>();
                                if (BasisController.getBlockchainSize() > 1) {
                                    //проверяет скаченные блоки на целостность
                                    //checks downloaded blocks for integrity
                                    temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
                                    System.out.println("prevBlock: " + BasisController.getPrevBlock().getIndex());
                                } else {

                                    Block genesis = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":6.5E7,\"digitalStockBalance\":6.5E7,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIDDW9fKvwUY0aXpvamxOU6pypicO3eCqEVM9LDFrIpjIAiEA81Zh7yCBbJOLrAzx4mg5HS0hMdqvB0obO2CZARczmfY=\"}],\"previousHash\":\"0234a350f4d56ae45c5ece57b08c54496f372bc570bd83a465fb6d2d85531479\",\"minerAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685942742706,\"index\":1,\"hashBlock\":\"08b1e6634457a40d3481e76ebd377e76322706e4ea27013b773686f7df8f8a4c\"}");
                                    Block firstBlock = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":400.0,\"digitalStockBalance\":400.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDfQ3TAOyuWi4NGr0hNuXjqzxDCL0U8DzwAmedSOw9eiwIgdRlZwmudMZJZURMtgmOwpT+wk569jo/Ok/fAGv0x/NE=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDV9MbTMPl/dWBTfc87rMcRBBKcNZsGtkuRx1pdzGrSKQIhALOFpX81JEyFCC8uQ//bZkSW9CaOODSgOaMkYgTHn5HC\"}],\"previousHash\":\"08b1e6634457a40d3481e76ebd377e76322706e4ea27013b773686f7df8f8a4c\",\"minerAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":4,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685942784960,\"index\":1,\"hashBlock\":\"06b932aadd602056b0fb7294ef693535009cb3ba54581f32fd4aa1d93108703f\"}");

                                    if (!subBlocks.get(0).equals(genesis) || !subBlocks.get(1).equals(firstBlock)) {
                                        System.out.println("error basis block: ");
                                        System.out.println("genesis: " + subBlocks.get(0).getIndex());
                                        System.out.println("first: " + subBlocks.get(1).getIndex());
                                        System.out.println("temp: " + temp);
                                    }
                                    addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    if (!temp.isValidation()) {
                                        System.out.println("error validation: " + temp);
                                    }

                                    BasisController.setShortDataBlockchain(temp);
                                    BasisController.setBlockchainSize((int) temp.getSize());
                                    BasisController.setBlockchainValid(temp.isValidation());

                                    EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                                    BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));
                                    System.out.println("prevBlock: " + BasisController.getPrevBlock().getIndex() + " shortDataBlockchain: " + BasisController.getShortDataBlockchain());
                                    String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                                    UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
//                                    continue;
                                    continue hostContinue;

                                }


                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                                System.out.println("1: temp: " + temp);
                                System.out.println("1: blockchainsize: " + BasisController.getBlockchainSize());
                                System.out.println("1: sublocks: size: " + subBlocks.size());
                                System.out.println("1: shortDataBlockchain: " + BasisController.getShortDataBlockchain());
                                System.out.println("1: blockService count: " + blockService.sizeBlock());
                                System.out.println("1: host: " + s);



                                jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                                System.out.println("2: jsonGlobalData: " + jsonGlobalData);
                                global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
                                temp = new DataShortBlockchainInformation();
                                sign = new ArrayList<>();
                                temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);


//                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                sign = new ArrayList<>();
                                temp = new DataShortBlockchainInformation();
                                temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
//                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                sign = new ArrayList<>();
                                if (!local_size_upper){
                                    System.out.println("===========================");
                                    System.out.println("!local_size_upper: " + !local_size_upper);
                                    System.out.println("===========================");
                                    temp = helpResolve4(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, false);

                                }

                                if(local_size_upper){
                                    System.out.println("===========================");
                                    System.out.println("local_size_upper: " + local_size_upper);
                                    System.out.println("===========================");
                                    temp = helpResolve5(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, false);
                                }

                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                                //если скачанный блокчейн не валидный, то не добавляет в блокчейн, возвращает -10
                                if (BasisController.getBlockchainSize() > 1 && !temp.isValidation()) {
                                    System.out.println("error resolve 2 in portion upper > 500");
                                    return -10;
                                }


//                                if (Seting.IS_SECURITY && BasisController.getBlockchainSize() > 1 && !temp.isValidation()) {
//                                    return -10;
//                                }

                                //добавляет мета данные блокчейна в static переменную, как так
                                //уже эти мета данные являются актуальными.
                                //adds capacitor metadata to a static variable like so
                                //this metadata is already relevant.
                                BasisController.setShortDataBlockchain(temp);
                                BasisController.setBlockchainSize((int) temp.getSize());
                                BasisController.setBlockchainValid(temp.isValidation());


                                //получить последний блок из базы данных.
                                //get the last block from the database.
                                EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                                //последний блок в локальном сервере.
                                //last block in the local server.
                                BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));
                                System.out.println("prevBlock: " + BasisController.getPrevBlock().getIndex() + " shortDataBlockchain: " + BasisController.getShortDataBlockchain());
                                String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                                //сохранить мета данные блокчейна.
                                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                                //если количество новых блоков, относительно локального блокчейна меньше 500,
                                //то скачать эти блоки и прекратить попытки скачивания с данного узла.
                                //if the number of new blocks relative to the local blockchain is less than 500,
                                //then download these blocks and stop trying to download from this node.
                                if (size - BasisController.getPrevBlock().getIndex() < Seting.PORTION_DOWNLOAD) {

                                     different_value = false;
                                     local_size_upper = false;
                                    //TODO возможно это решит проблему, если блоки будут равны
                                    if (blocks_current_size == size) {
                                        subBlockchainEntity = new SubBlockchainEntity(blocks_current_size - 1, size);
                                        different_value = true;
                                    }
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
                                    subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                    System.out.println("2:download sub block: " + subBlocks.size());
                                    System.out.println("2: host: " + s);

//                                    balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                                    if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY && BasisController.getBlockchainSize() < Seting.V34_NEW_ALGO) {
                                        lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                                blockService.findBySpecialIndexBetween(
                                                        (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                        BasisController.getPrevBlock().getIndex() + 1
                                                )
                                        );
                                    }

//                                    if (BasisController.getBlockchainSize() > 1) {
//                                        temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
//                                    }

                                    jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                                    System.out.println("2: jsonGlobalData: " + jsonGlobalData);
                                    global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
//                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    sign = new ArrayList<>();
                                    temp = new DataShortBlockchainInformation();
                                    temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
//                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    sign = new ArrayList<>();


                                    System.out.println("2: temp: " + temp);
                                    System.out.println("2: blockchainsize: " + BasisController.getBlockchainSize());
                                    System.out.println("2: sublocks: " + subBlocks.size());
                                    System.out.println("2: host: " + s);


//                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    sign = new ArrayList<>();
                                    temp = new DataShortBlockchainInformation();
                                    temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
//                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                                    sign = new ArrayList<>();
                                    if (!local_size_upper){
                                        System.out.println("===========================");
                                        System.out.println("!local_size_upper: " + !local_size_upper);
                                        System.out.println("===========================");
                                        temp = helpResolve4(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);

                                    }

                                    if(local_size_upper){
                                        System.out.println("===========================");
                                        System.out.println("local_size_upper: " + local_size_upper);
                                        System.out.println("===========================");
                                        temp = helpResolve5(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);
                                    }

                                    if (Seting.IS_SECURITY && BasisController.getBlockchainSize() > 1 && !temp.isValidation()) {
                                        UtilsFileSaveRead.save("-----------------------------------------------: " , Seting.ERROR_FILE, true);
                                        //TODO добавить хост в заблокированный файл
                                        System.out.println("-------------------------------------------------");
                                        System.out.println("Blocked host: ");
                                        System.out.println("expected host: " + hostEndDataShortB.getDataShortBlockchainInformation());
                                        System.out.println("host: " + hostEndDataShortB.getHost());
                                        System.out.println("-------------------------------------------------");
                                        UtilsAllAddresses.saveAllAddresses(hostEndDataShortB.getHost(), Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
                                        continue ;
                                    }

                                    BasisController.setShortDataBlockchain(temp);
                                    BasisController.setBlockchainSize((int) temp.getSize());
                                    BasisController.setBlockchainValid(temp.isValidation());

                                    tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                                    BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));

                                    json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                                    UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                                }
                            }
                        } else {

                            //здесь нужно проверить
                            //If the difference is not greater than PORTION_DOWNLOAD, then downloads once a portion of this difference
                            //Если разница не больше PORTION_DOWNLOAD, то скачивает один раз порцию эту разницу
                            subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);

                            boolean different_value = false;
                            boolean local_size_upper = false;
                            //TODO возможно это решит проблему, если блоки будут равны
                            if (blocks_current_size == size) {
                                subBlockchainEntity = new SubBlockchainEntity(blocks_current_size - 1, size);
                                different_value = true;
                            }
                            if (size < blocks_current_size) {
                                subBlockchainEntity = new SubBlockchainEntity(size - Seting.IS_BIG_DIFFERENT, size);
                                System.out.println("subBlockchainEntity: size < blocks_current_size: " + subBlockchainEntity);
                                local_size_upper = true;
                            }


                            subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);


                            List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                            System.out.println("3:download sub block: " + subBlocks.size());
//                            tempBalances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
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
                            temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);

                            jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                            System.out.println("3: jsonGlobalData: " + jsonGlobalData);
                            global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
                            DataShortBlockchainInformation anotherCheck = null;



                            System.out.println("3: temp: " + temp);
                            System.out.println("3: blockchainsize: " + BasisController.getBlockchainSize());
                            System.out.println("3: sublocks: " + subBlocks.size());


//                            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                            sign = new ArrayList<>();
                            temp = new DataShortBlockchainInformation();
                            temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
//                            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
//                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(subBlocks, blockService));
                            sign = new ArrayList<>();
                            if (!local_size_upper){
                                System.out.println("===========================");
                                System.out.println("!local_size_upper: " + !local_size_upper);
                                System.out.println("===========================");
                                temp = helpResolve4(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);

                            }

                            if(local_size_upper){
                                System.out.println("===========================");
                                System.out.println("local_size_upper: " + local_size_upper);
                                System.out.println("===========================");
                                temp = helpResolve5(temp, global, s, lastDiff, tempBalances, sign, balances, subBlocks, true);
                            }


                            if (  !temp.isValidation()) {
                                UtilsFileSaveRead.save("-----------------------------------------------: " , Seting.ERROR_FILE, true);
                                //TODO добавить хост в заблокированный файл
                                System.out.println("-------------------------------------------------");
                                System.out.println("Blocked host: ");
                                System.out.println("expected host: " + hostEndDataShortB.getDataShortBlockchainInformation());
                                System.out.println("actual host: " + anotherCheck);
                                System.out.println("host: " + hostEndDataShortB.getHost());
                                System.out.println("-------------------------------------------------");
                                UtilsAllAddresses.saveAllAddresses(hostEndDataShortB.getHost(), Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);
                                UtilsFileSaveRead.save("anotherCheck: " + anotherCheck+ "\n", Seting.ERROR_FILE, true);
                                UtilsFileSaveRead.save("temp: " + anotherCheck+ "\n", Seting.ERROR_FILE, true);
                                UtilsFileSaveRead.save("-----------------------------------------------: " , Seting.ERROR_FILE, true);

                                continue ;
                            }

                            BasisController.setShortDataBlockchain(temp);
                            BasisController.setBlockchainSize((int) temp.getSize());
                            BasisController.setBlockchainValid(temp.isValidation());

                            EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                            BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));

                            String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                            UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
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

    //TODO возникает ошибка, потому что высота одинаковая, но при этом big random разный.
    //TODO нужно сделать еще один if внутри него поместить все if этого метода
    //TODO или он должен удалять свой блок и добавлять другой блок
    //TODO тестовая версия
    public boolean isBig(
            DataShortBlockchainInformation actual,
            DataShortBlockchainInformation global) {
//        if (global.getSize() >= actual.getSize() - Seting.IS_BIG_DIFFERENT && global.getBigRandomNumber() > actual.getBigRandomNumber()) {
//            return true;
//        }
//        return false;
        if(global.getBigRandomNumber() != actual.getBigRandomNumber())
            return true;
        else return false;

    }


    public boolean isSmall(DataShortBlockchainInformation expected, DataShortBlockchainInformation actual) {
        if (
                actual.getSize() < expected.getSize()
                        || actual.getBigRandomNumber() < expected.getBigRandomNumber()
                        || actual.getTransactions() < expected.getTransactions()

        ) {
            return true;
        }
        return false;
    }

    public DataShortBlockchainInformation check(DataShortBlockchainInformation temp,
                                                DataShortBlockchainInformation global,
                                                String s,
                                                List<Block> lastDiff,
                                                Map<String, Account> tempBalances,
                                                List<String> sign) throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> tempBalance = UtilsUse.balancesClone(tempBalances);
        if (BasisController.getShortDataBlockchain().getSize() > 1 && !temp.isValidation()) {
            System.out.println("__________________________________________________________");

            List<Block> emptyList = new ArrayList<>();
            List<Block> different = new ArrayList<>();

            int lastBlockIndex = (int) (global.getSize() - 1);
            int currentIndex = lastBlockIndex;



            stop:
            while (currentIndex >= 0) {

                int startIndex = Math.max(currentIndex - 499, 0);
                int endIndex = currentIndex;


                SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(startIndex, endIndex);
                String subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                List<Block> blockList = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                System.out.println("check subBlockchainEntity: " + subBlockchainEntity);
                blockList = blockList.stream().sorted(Comparator.comparing(Block::getIndex).reversed()).collect(Collectors.toList());
                for (Block block : blockList) {
                    System.out.println("helpResolve4: block index: " + block.getIndex());

                    if (block.getIndex() > BasisController.getBlockchainSize() - 1) {
                        System.out.println("check :download blocks: " + block.getIndex() +
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
//                        emptyList.add(block);
//                        different.add(UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(block.getIndex())));

                        break stop;
                    }
                }

                // Обновляем индекс для следующей итерации
                currentIndex = startIndex - 1;
            }

            if(different.isEmpty() && emptyList.isEmpty()){
                return temp;
            }
            System.out.println("different: " + different.size());
            System.out.println("emptyList: " + emptyList.size());

            System.out.println("check: shortDataBlockchain: " + BasisController.getShortDataBlockchain());
            System.out.println("check temp: " + temp);


            different = different.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            Block tempPrevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(different.get(0).getIndex() - 1));
            temp = Blockchain.rollBackShortCheck(different, BasisController.getShortDataBlockchain(), tempBalance, sign);



            for (Block block : emptyList) {
                List<Block> tempList = new ArrayList<>();
                tempList.add(block);
                temp = Blockchain.shortCheck(tempPrevBlock, tempList, temp, lastDiff, tempBalance, sign);
                tempPrevBlock = block;
//                System.out.println("check: " + block.getIndex());
//                System.out.println("check: temp " + temp);
            }

        }
//        System.out.println("rollback temp: " + temp);

        return temp;
    }


    public DataShortBlockchainInformation check2(DataShortBlockchainInformation temp,
                                                 DataShortBlockchainInformation global,
                                                 String s,
                                                 List<Block> lastDiff,
                                                 Map<String, Account> tempBalances,
                                                 List<String> sign) throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> tempBalance = UtilsUse.balancesClone(tempBalances);
        if (BasisController.getShortDataBlockchain().getSize() > 1 && !temp.isValidation()) {
            System.out.println("__________________________________________________________");

            List<Block> emptyList = new ArrayList<>();
            List<Block> different = new ArrayList<>();

            int lastBlockIndex = (int) (global.getSize() - 1);
            int currentIndex = lastBlockIndex;

            UtilsFileSaveRead.save("lastBlockIndex: " + lastBlockIndex+ "\n", Seting.ERROR_FILE, true);
            UtilsFileSaveRead.save("currentIndex: " + currentIndex+ "\n", Seting.ERROR_FILE, true);

            //TODO тестовая версия, мы проверяем если блокчейн ценее, но при этом меньше
            if (global.getSize() < BasisController.getBlockchainSize()) {
                List<EntityBlock> entityBlocks = blockService.findBySpecialIndexBetween(global.getSize(), BasisController.getBlockchainSize()-1);
                different.addAll(UtilsBlockToEntityBlock.entityBlocksToBlocks(entityBlocks));

            }

            UtilsFileSaveRead.save("different: 1: " + different+ "\n", Seting.ERROR_FILE, true);
            stop:
            while (currentIndex >= 0) {

                int startIndex = Math.max(currentIndex - 499, 0);
                int endIndex = currentIndex;

                SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(startIndex, endIndex);
                String subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                List<Block> blockList = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                System.out.println("check2 subBlockchainEntity: " + subBlockchainEntity);
                blockList = blockList.stream().sorted(Comparator.comparing(Block::getIndex).reversed()).collect(Collectors.toList());

                for (Block block : blockList) {
                    System.out.println("check2: block index: " + block.getIndex());

                    if (block.getIndex() > BasisController.getBlockchainSize() - 1) {
                        System.out.println("check2 :download blocks: " + block.getIndex() +
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

                        break stop;
                    }
                }

                // Обновляем индекс для следующей итерации
                currentIndex = startIndex - 1;
            }

            if(different.isEmpty() && emptyList.isEmpty()){
                return temp;
            }



            System.out.println("shortDataBlockchain: " + BasisController.getShortDataBlockchain());
            System.out.println("check 2: rollback temp: " + temp);

            different = different.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());

            UtilsFileSaveRead.save("check2 different: 2: " + different + "\n", Seting.ERROR_FILE, true);
            UtilsFileSaveRead.save("check2 emptyList: 2: " + emptyList+ "\n", Seting.ERROR_FILE, true);
            Block tempPrevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(different.get(0).getIndex() - 1));
            temp = Blockchain.rollBackShortCheck(different, BasisController.getShortDataBlockchain(),  tempBalance, sign);

            if (!emptyList.isEmpty()) {

                for (Block block : emptyList) {
                    List<Block> tempList = new ArrayList<>();
                    tempList.add(block);
                    temp = Blockchain.shortCheck(tempPrevBlock, tempList, temp, lastDiff, tempBalance, sign);
                    tempPrevBlock = block;
                }
            }


        }
        return temp;
    }

    public DataShortBlockchainInformation helpResolve5(DataShortBlockchainInformation temp,
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
        if (BasisController.getShortDataBlockchain().getSize() > 1 && !temp.isValidation()) {
            System.out.println("__________________________________________________________");

            List<Block> emptyList = new ArrayList<>();
            List<Block> different = new ArrayList<>();

            int lastBlockIndex = (int) (global.getSize() - 1);
            int currentIndex = lastBlockIndex;

            //TODO тестовая версия, мы проверяем если блокчейн ценее, но при этом меньше
            if (global.getSize() < BasisController.getBlockchainSize()) {
                List<EntityBlock> entityBlocks = blockService.findBySpecialIndexBetween(global.getSize(), BasisController.getBlockchainSize()-1);
                different.addAll(UtilsBlockToEntityBlock.entityBlocksToBlocks(entityBlocks));
            }

            try {


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
                        System.out.println("helpResolve5: block index: " + block.getIndex());

                        if (block.getIndex() > BasisController.getBlockchainSize() - 1) {
                            System.out.println("helpResolve5 :download blocks: " + block.getIndex() +
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


                            break stop;
                        }
                    }

                    // Обновляем индекс для следующей итерации
                    currentIndex = startIndex - 1;
                }

            }catch (Exception e){
                System.out.println("******************************");
                System.out.println("connecting exeption helpresolve5: address: " + s);
                System.out.println("******************************");
                return temp;
            }
            if(different.isEmpty() && emptyList.isEmpty()){
                return temp;
            }

            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(different, blockService)));
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(emptyList, blockService)));
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(lastDiff, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(different, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(emptyList, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(lastDiff, blockService)));


            System.out.println("shortDataBlockchain: " + BasisController.getShortDataBlockchain());
            System.out.println("rollback temp: " + temp);

            different = different.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            Block tempPrevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(different.get(0).getIndex() - 1));
            temp = Blockchain.rollBackShortCheck( different, BasisController.getShortDataBlockchain(), tempBalance, sign);

            if (!emptyList.isEmpty()) {


                for (Block block : emptyList) {
                    List<Block> tempList = new ArrayList<>();
                    tempList.add(block);
                    temp = Blockchain.shortCheck(tempPrevBlock, tempList, temp, lastDiff, tempBalance, sign);
                    tempPrevBlock = block;
                }
            }

            //TODO проверка теперь будет происходит уже сразу и при скачивании.
            if (Seting.IS_SECURITY == true && checking && isSmall(global, temp)){
                temp.setValidation(false);
                return temp;
            }
            if(!temp.isValidation()){
                return temp;
            }



            System.out.println("after rollback: " + temp);
            if (temp.isValidation()) {
                System.out.println("------------------------------------------");
                System.out.println("rollback 5");
                try {
                    System.out.println("before roll back emptyList: " + emptyList);
                    rollBackAddBlock4(different, emptyList,  balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);


                    System.out.println("------------------------------------------");
                    System.out.println("emptyList: " + emptyList);

                    System.out.println("==========================================");
                    System.out.println("different start index: " + different.get(0).getIndex());
                    System.out.println("different finish index: " + different.get(different.size() - 1).getIndex());
                    System.out.println("------------------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("------------------------------------------");
                System.out.println("helpResolve5: temp: " + temp);
                System.out.println("------------------------------------------");
            } else {

                return temp;
            }


        } else if (BasisController.getShortDataBlockchain().getSize() > 1 && temp.isValidation()) {
            //вызывает методы, для сохранения списка блоков в текущий блокчейн,
            //так же записывает в базу h2, делает перерасчет всех балансов,
            //и так же их записывает, а так же записывает другие данные.

            //TODO проверка теперь будет происходит уже сразу и при скачивании.
            if (Seting.IS_SECURITY == true && checking && isSmall(global, temp)){
                temp.setValidation(false);
                return temp;
            }
            if(!temp.isValidation()){
                return temp;
            }

            addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }


        System.out.println("__________________________________________________________");
        return temp;
    }

    public DataShortBlockchainInformation helpResolve4(DataShortBlockchainInformation temp,
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
        if (BasisController.getShortDataBlockchain().getSize() > 1 && !temp.isValidation()) {
            System.out.println("__________________________________________________________");

            List<Block> emptyList = new ArrayList<>();
            List<Block> different = new ArrayList<>();

            int lastBlockIndex = (int) (global.getSize() - 1);
            int currentIndex = lastBlockIndex;

            try {

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
            }catch (Exception e){
                System.out.println("******************************");
                System.out.println("connecting exeption helpresolve4: address: " + s);
                System.out.println("******************************");
                return temp;
            }
            System.out.println("different: ");
            if(different.isEmpty() && emptyList.isEmpty()) {
                return temp;
            }

            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(different, blockService)));
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(emptyList, blockService)));
            balances.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(lastDiff, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(different, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(emptyList, blockService)));
            tempBalance.putAll(UtilsAccountToEntityAccount.entityAccountsToMapAccounts(UtilsUse.accounts(lastDiff, blockService)));

            System.out.println("shortDataBlockchain: " + BasisController.getShortDataBlockchain());
            System.out.println("rollback temp: " + temp);

            different = different.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            Block tempPrevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(different.get(0).getIndex() - 1));
            temp = Blockchain.rollBackShortCheck( different, BasisController.getShortDataBlockchain(),  tempBalance, sign);

            //TODO проверить если данные совпадают с ожидаемыми global, то произвести запись


            for (Block block : emptyList) {
                List<Block> tempList = new ArrayList<>();
                tempList.add(block);
                temp = Blockchain.shortCheck(tempPrevBlock, tempList, temp, lastDiff, tempBalance, sign);
                tempPrevBlock = block;
            }

            //TODO проверка теперь будет происходит уже сразу и при скачивании.
            if (Seting.IS_SECURITY == true && checking && isSmall(global, temp)){
                temp.setValidation(false);
                return temp;
            }
            if(!temp.isValidation()){
                return temp;
            }

            System.out.println("after rollback: " + temp);
            if (temp.isValidation()) {
                System.out.println("------------------------------------------");
                System.out.println("rollback");
                try {
                    rollBackAddBlock3(different, emptyList, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);


                    System.out.println("------------------------------------------");
                    System.out.println("emptyList start index: " + emptyList.get(0).getIndex());
                    System.out.println("emptyList finish index: " + emptyList.get(emptyList.size() - 1).getIndex());
                    System.out.println("==========================================");
                    System.out.println("different start index: " + different.get(0).getIndex());
                    System.out.println("different finish index: " + different.get(different.size() - 1).getIndex());
                    System.out.println("------------------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("------------------------------------------");
                System.out.println("helpResolve4: temp: " + temp);
                System.out.println("------------------------------------------");
            } else {

                return temp;
            }


        }
        else if (BasisController.getShortDataBlockchain().getSize() > 1 && temp.isValidation()) {
            //вызывает методы, для сохранения списка блоков в текущий блокчейн,
            //так же записывает в базу h2, делает перерасчет всех балансов,
            //и так же их записывает, а так же записывает другие данные.

            //TODO проверка теперь будет происходит уже сразу и при скачивании.
            if (Seting.IS_SECURITY == true && checking && isSmall(global, temp)){
                temp.setValidation(false);
                return temp;
            }
            if(!temp.isValidation()){
                return temp;
            }

            addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }


        System.out.println("__________________________________________________________");
        return temp;
    }


    public DataShortBlockchainInformation helpResolve3(DataShortBlockchainInformation temp,
                                                       DataShortBlockchainInformation global,
                                                       String s,
                                                       List<Block> lastDiff,
                                                       Map<String, Account> tempBalances,
                                                       List<String> sign,
                                                       Map<String, Account> balances,

                                                       List<Block> subBlocks) throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        //TODO сначала найти блок откуда начинается ответление и докуда

        Map<String, Account> tempBalance = UtilsUse.balancesClone(tempBalances);
        if (BasisController.getShortDataBlockchain().getSize() > 1 && !temp.isValidation()) {
            System.out.println("__________________________________________________________");

            List<Block> emptyList = new ArrayList<>();
            List<Block> different = new ArrayList<>();


            for (int i = (int) (global.getSize() - 1); i >= 0; i--) {

                Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(i), s + "/block"));

                System.out.println("helpResolve3: block index: " + block.getIndex());
                if (i > BasisController.getBlockchainSize() - 1) {
                    System.out.println(":download blocks: " + block.getIndex() +
                            " your block : " + (BasisController.getBlockchainSize()) + ":waiting need download blocks: " + (block.getIndex() - BasisController.getBlockchainSize()));
                    emptyList.add(block);

                } else if (!blockService.findBySpecialIndex(i).getHashBlock().equals(block.getHashBlock())) {
                    emptyList.add(block);
                    different.add(UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(i)));
                    System.out.println("********************************");
                    System.out.println(":dowdnload block index: " + i);
                    System.out.println(":block original index: " + blockService.findBySpecialIndex(i).getIndex());
                    System.out.println(":block from index: " + block.getIndex());

                } else {
//                    emptyList.add(block);
//                    different.add(UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(i)));

                    break;
                }
            }
            System.out.println("different: ");


            System.out.println("shortDataBlockchain: " + BasisController.getShortDataBlockchain());
            System.out.println("rollback temp: " + temp);

            different = different.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            Block tempPrevBlock = UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(different.get(0).getIndex() - 1));
            temp = Blockchain.rollBackShortCheck(different, BasisController.getShortDataBlockchain(),  tempBalance, sign);

            for (Block block : emptyList) {
                List<Block> tempList = new ArrayList<>();
                tempList.add(block);
                temp = Blockchain.shortCheck(tempPrevBlock, tempList, temp, lastDiff, tempBalance, sign);
                tempPrevBlock = block;
            }

            System.out.println("after rollback: " + temp);
            if (temp.isValidation()) {
                System.out.println("------------------------------------------");
                System.out.println("rollback");
                try {
                    rollBackAddBlock3(different, emptyList, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);


                    System.out.println("------------------------------------------");
                    System.out.println("emptyList start index: " + emptyList.get(0).getIndex());
                    System.out.println("emptyList finish index: " + emptyList.get(emptyList.size() - 1).getIndex());
                    System.out.println("==========================================");
                    System.out.println("different start index: " + different.get(0).getIndex());
                    System.out.println("different finish index: " + different.get(different.size() - 1).getIndex());
                    System.out.println("------------------------------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("------------------------------------------");
                System.out.println("helpResolve3: temp: " + temp);
                System.out.println("------------------------------------------");
            } else {

                return temp;
            }


        } else if (BasisController.getShortDataBlockchain().getSize() > 1 && temp.isValidation()) {
            //вызывает методы, для сохранения списка блоков в текущий блокчейн,
            //так же записывает в базу h2, делает перерасчет всех балансов,
            //и так же их записывает, а так же записывает другие данные.
            addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        }


        System.out.println("__________________________________________________________");
        return temp;
    }

    @Transactional
    public void rollBackAddBlock4(List<Block> deleteBlocks, List<Block> saveBlocks, Map<String, Account> balances, String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        java.sql.Timestamp lastIndex = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());


        List<String> signs = new ArrayList<>();
        //пакет законов.
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        //сначала узнаем название файла, где есть первый блок для удаления из файла
        System.out.println("str 876: deleteBlocks: " + deleteBlocks);
        File file = Blockchain.indexNameFileBlock((int) deleteBlocks.get(0).getIndex(), filename);
        //потом берем список блоков из этого файл
        System.out.println("rollBackAddBlock3: file: " + file.getAbsolutePath());
        List<String> tempList = UtilsFileSaveRead.reads(file.getAbsolutePath());
        List<Block> tempBlock = new ArrayList<>();
        for (String s : tempList) {

            Block block = UtilsJson.jsonToBLock(s);
            tempBlock.add(block);
        }
        //потом удаляем из этого списка блоки, которые не должны быть в файле.
        tempBlock = tempBlock.stream()
                .filter(t -> t.getIndex() < deleteBlocks.get(0)
                        .getIndex())
                .sorted(Comparator.comparing(Block::getIndex))
                .collect(Collectors.toList());

        //TODO здесь мы должны удалить все файлы идущие после этого файла,

        System.out.println("rollBackAddBlock4: delete: " + file.getName());
        Blockchain.deleteFileBlockchain(Integer.parseInt(file.getName().replace(".txt", "")), Seting.ORIGINAL_BLOCKCHAIN_FILE);
        System.out.println("rollBackAddBlock4: delete finish: " + file.getName());

        long threshold = deleteBlocks.get(0).getIndex();

        //для удаления баланса
        Map<String, Account> tempBalances = UtilsUse.balancesClone(balances);


        for (int i = deleteBlocks.size() - 1; i >= 0; i--) {
            Block block = deleteBlocks.get(i);
            System.out.println("rollBackAddBlock4 :BasisController: addBlock3: blockchain is being updated: index" + block.getIndex());

            //возвращаем деньги на счета и аннулируем добытые монеты в неверной ветке
            balances = rollbackCalculateBalance(balances, block, signs);

            //Аннулирует законы из списка законов, которые из неправильной ветки.
            allLaws = UtilsLaws.rollBackLaws(block, Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE, allLaws);

        }

        tempBalances = UtilsUse.differentAccount(tempBalances, balances);
        List<EntityAccount> accountList = blockService.findByAccountIn(balances);
        accountList = UtilsUse.mergeAccounts(tempBalances, accountList);


        long startTime = UtilsTime.getUniversalTimestamp();
        blockService.saveAccountAllF(accountList);
        long finishTime = UtilsTime.getUniversalTimestamp();

        System.out.println("UtilsResolving: rollBackAddBlock4: time save accounts: " + UtilsTime.differentMillSecondTime(startTime, finishTime));
        System.out.println("UtilsResolving: rollBackAddBlock4: total different balance: " + tempBalances.size());
        System.out.println("UtilsResolving: rollBackAddBlock4: total original balance: " + balances.size());
        //Удаляет блоки из неправильной ветки.
//        BlockService.removeAllBlock(list);
        blockService.deleteEntityBlocksAndRelatedData(threshold);


        //возвращает все законы с балансом,
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

        int tempIndexTest = (int) tempBlock.get(0).getIndex();
        int tempIndexTest2 = (int) tempBlock.get(tempBlock.size() - 1).getIndex();

        tempBlock = tempBlock.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
        for (Block block : tempBlock) {
            UtilsBlock.saveBLock(block, filename);
        }


        System.out.println("addBlock 3: time: result: " + result);
        System.out.println(":BasisController: addBlock3: finish: " + deleteBlocks.size());
        System.out.println("deleteBlocks: index: start: " + deleteBlocks.get(deleteBlocks.size() - 1).getIndex());
        System.out.println("tempBlock: index: start: " + tempIndexTest);
        System.out.println("tempBlock: index: finish: " + tempIndexTest2);

        System.out.println("balances size: " + balances.size());


        if (!saveBlocks.isEmpty())
            addBlock3(saveBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);

    }




    @Transactional
    public void rollBackAddBlock3(List<Block> deleteBlocks, List<Block> saveBlocks, Map<String, Account> balances, String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        java.sql.Timestamp lastIndex = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());


        List<String> signs = new ArrayList<>();
        //пакет законов.
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        //сначала узнаем название файла, где есть первый блок для удаления из файла
        File file = Blockchain.indexNameFileBlock((int) deleteBlocks.get(0).getIndex(), filename);
        //потом берем список блоков из этого файл
        System.out.println("rollBackAddBlock3: file: " + file.getAbsolutePath());
        List<String> tempList = UtilsFileSaveRead.reads(file.getAbsolutePath());
        List<Block> tempBlock = new ArrayList<>();
        for (String s : tempList) {

            Block block = UtilsJson.jsonToBLock(s);
            tempBlock.add(block);
        }
        //потом удаляем из этого списка блоки, которые не должны быть в файле.
        tempBlock = tempBlock.stream()
                .filter(t -> t.getIndex() < deleteBlocks.get(0).getIndex()).collect(Collectors.toList());

        //TODO здесь мы должны удалить все файлы идущие после этого файла,

        System.out.println("rollBackAddBlock3: delete: " + file.getName());
        Blockchain.deleteFileBlockchain(Integer.parseInt(file.getName().replace(".txt", "")), Seting.ORIGINAL_BLOCKCHAIN_FILE);
        System.out.println("rollBackAddBlock3: delete finish: " + file.getName());

        long threshold = deleteBlocks.get(0).getIndex();

        //для удаления баланса
        Map<String, Account> tempBalances = UtilsUse.balancesClone(balances);


        for (int i = deleteBlocks.size() - 1; i >= 0; i--) {
            Block block = deleteBlocks.get(i);
            System.out.println("rollBackAddBlock3 :BasisController: addBlock3: blockchain is being updated: index" + block.getIndex());

            //возвращаем деньги на счета и аннулируем добытые монеты в неверной ветке
            balances = rollbackCalculateBalance(balances, block, signs);

            //Аннулирует законы из списка законов, которые из неправильной ветки.
            allLaws = UtilsLaws.rollBackLaws(block, Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE, allLaws);

        }

        tempBalances = UtilsUse.differentAccount(tempBalances, balances);
        List<EntityAccount> accountList = blockService.findByAccountIn(balances);
        accountList = UtilsUse.mergeAccounts(tempBalances, accountList);


        long startTime = UtilsTime.getUniversalTimestamp();
        blockService.saveAccountAllF(accountList);
        long finishTime = UtilsTime.getUniversalTimestamp();

        System.out.println("UtilsResolving: rollBackAddBlock3: time save accounts: " + UtilsTime.differentMillSecondTime(startTime, finishTime));
        System.out.println("UtilsResolving: rollBackAddBlock3: total different balance: " + tempBalances.size());
        System.out.println("UtilsResolving: rollBackAddBlock3: total original balance: " + balances.size());
        //Удаляет блоки из неправильной ветки.
//        BlockService.removeAllBlock(list);
        blockService.deleteEntityBlocksAndRelatedData(threshold);


        //возвращает все законы с балансом,
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

        int tempIndexTest = (int) tempBlock.get(0).getIndex();
        int tempIndexTest2 = (int) tempBlock.get(tempBlock.size() - 1).getIndex();

        tempBlock = tempBlock.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
        for (Block block : tempBlock) {
            UtilsBlock.saveBLock(block, filename);
        }


        System.out.println("addBlock 3: time: result: " + result);
        System.out.println(":BasisController: addBlock3: finish: " + deleteBlocks.size());
        System.out.println("deleteBlocks: index: start: " + deleteBlocks.get(deleteBlocks.size() - 1).getIndex());
        System.out.println("tempBlock: index: start: " + tempIndexTest);
        System.out.println("tempBlock: index: finish: " + tempIndexTest2);

        System.out.println("balances size: " + balances.size());

//        for (int start = 0; start < saveBlocks.size(); start += Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
//            int end = Math.min(saveBlocks.size(), start + Seting.PORTION_BLOCK_TO_COMPLEXCITY);
//            List<Block> batch = saveBlocks.subList(start, end);
//            addBlock3(batch, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
//        }

        addBlock3(saveBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);

    }


    public int resovle2() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        BasisController.setUpdating(true);
        int bigSize = 0;
        try {
            System.out.println(" :start resolve");
            utilsMethod();

//            blockchainSize = (int) shortDataBlockchain.getSize();

            //local blockchain size
            //размер локального блокчейна
            int blocks_current_size = BasisController.getBlockchainSize();

            System.out.println(" resolve2:local size: " + blocks_current_size);

            //адреса узлов.
            Set<String> nodesAll = getNodes();
            //сортирует по приоритетности блокчейны
            List<HostEndDataShortB> sortPriorityHost = sortPriorityHost(nodesAll);
            System.out.println(":resolve2: size nodes: " + getNodes().size());
            //goes through all hosts (repositories) in search of the most up-to-date blockchain
            //проходит по всем хостам(хранилищам) в поисках самого актуального блокчейна
            for (HostEndDataShortB hostEndDataShortB : sortPriorityHost) {
                String s = hostEndDataShortB.getHost();
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
                    //if the size from the storage is larger than on the local server, start checking
                    //если размер с хранилища больше чем на локальном сервере, начать проверку
                    System.out.println("resolve2 size: " + size + " blocks_current_size: " + blocks_current_size);
                    String jsonGlobalData = UtilUrl.readJsonFromUrl(s + "/datashort");
                    System.out.println("jsonGlobalData: " + jsonGlobalData);
                    DataShortBlockchainInformation global = UtilsJson.jsonToDataShortBlockchainInformation(jsonGlobalData);
                    if (isBig(BasisController.getShortDataBlockchain(), global)) {
                        System.out.println(":size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                        //Test start algorithm
                        //600 последних блоков, для подсчета сложности, для последнего блока.
                        List<Block> lastDiff = new ArrayList<>();
                        SubBlockchainEntity subBlockchainEntity = null;
                        String subBlockchainJson = null;
//                        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                        Map<String, Account> balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
//                        Map<String, Account> tempBalances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                        Map<String, Account> tempBalances = UtilsUse.balancesClone(balances);

                        //if the local one lags behind the global one by more than PORTION_DOWNLOAD (500 blocks), then you need to download in portions from the storage
                        //если локальный отстает от глобального больше чем PORTION_DOWNLOAD (500 блоков), то нужно скачивать порциями из хранилища
                        if (size - blocks_current_size > Seting.PORTION_DOWNLOAD) {
                            boolean downloadPortion = true;
                            int finish = blocks_current_size + Seting.PORTION_DOWNLOAD;
                            int start = blocks_current_size;
                            //while the difference in the size of the local blockchain is greater than from the host, it will continue to download in portions to download the entire blockchain
                            //пока разница размера локального блокчейна больше чем с хоста будет продолжаться скачивать порциями, чтобы скачать весь блокчейн
                            while (downloadPortion) {
                                //здесь говориться, с какого блока по какой блок скачивать.
                                subBlockchainEntity = new SubBlockchainEntity(start, finish);

                                System.out.println("1:shortDataBlockchain:  " + BasisController.getShortDataBlockchain());
                                System.out.println("1:sublockchainEntity: " + subBlockchainEntity);
                                subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                System.out.println("1:sublockchainJson: " + subBlockchainJson);
                                List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                System.out.println("1:download sub block: " + subBlocks.size());

                                finish = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + Seting.PORTION_DOWNLOAD + 1;
                                start = (int) subBlocks.get(subBlocks.size() - 1).getIndex() + 1; //вот здесь возможно сделать + 2


//                                balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                                balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

                                //вычисляет сложность блока, для текущего блока, на основе предыдущих блоков.
                                //select a block class for the current block, based on previous blocks.
                                if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
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
                                    temp = Blockchain.shortCheck(
                                            BasisController.getPrevBlock(),
                                            subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
                                    System.out.println("prevBlock: " + BasisController.getPrevBlock().getIndex());
                                }


                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                                System.out.println("1: temp: " + temp);
                                System.out.println("1: blockchainsize: " + BasisController.getBlockchainSize());
                                System.out.println("1: sublocks: " + subBlocks.size());
                                System.out.println("1: shortDataBlockchain: " + BasisController.getShortDataBlockchain());
                                System.out.println("1: blockService count: " + blockService.sizeBlock());

                                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
                                //если скачанный блокчейн не валидный, то не добавляет в блокчейн, возвращает -10
                                if (BasisController.getBlockchainSize() > 1 && !temp.isValidation()) {
                                    System.out.println("error resolve 2 in portion upper > 500");
                                    return -10;
                                }

                                //вызывает методы, для сохранения списка блоков в текущий блокчейн,
                                //так же записывает в базу h2, делает перерасчет всех балансов,
                                //и так же их записывает, а так же записывает другие данные.
                                addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);

                                if (!temp.isValidation()) {
                                    System.out.println("check all file");
                                    //проверить целостность блокчейна всего на кошельке
                                    //check the integrity of the blockchain of everything on the wallet
                                    temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                }

                                //добавляет мета данные блокчейна в static переменную, как так
                                //уже эти мета данные являются актуальными.
                                //adds capacitor metadata to a static variable like so
                                //this metadata is already relevant.
                                BasisController.setShortDataBlockchain(temp);
                                //размер блокчейна в кошельке.
                                //the size of the blockchain in the wallet.

                                BasisController.setBlockchainSize((int) BasisController.getShortDataBlockchain().getSize());
                                //валидность блокчейна в кошельке.
                                //validity of the blockchain in the wallet.
                                BasisController.setBlockchainValid(BasisController.getShortDataBlockchain().isValidation());
//
                                //получить последний блок из базы данных.
                                //get the last block from the database.
                                EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                                //последний блок в локальном сервере.
                                //last block in the local server.
                                BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));
                                System.out.println("prevBlock: " + BasisController.getPrevBlock().getIndex() + " shortDataBlockchain: " + BasisController.getShortDataBlockchain());
                                String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                                //сохранить мета данные блокчейна.
                                UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                                //если количество новых блоков, относительно локального блокчейна меньше 500,
                                //то скачать эти блоки и прекратить попытки скачивания с данного узла.
                                //if the number of new blocks relative to the local blockchain is less than 500,
                                //then download these blocks and stop trying to download from this node.
                                if (size - BasisController.getPrevBlock().getIndex() < Seting.PORTION_DOWNLOAD) {
                                    downloadPortion = false;
                                    finish = size;
                                    subBlockchainEntity = new SubBlockchainEntity(start, finish);
                                    System.out.println("2:sublockchainEntity: " + subBlockchainEntity);
                                    subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
                                    System.out.println("2:sublockchainJson: " + subBlockchainJson);
                                    subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                                    System.out.println("2:download sub block: " + subBlocks.size());

//                                    balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                                    balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                                    if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                        lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                                blockService.findBySpecialIndexBetween(
                                                        (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                        BasisController.getPrevBlock().getIndex() + 1
                                                )
                                        );
                                    }

                                    if (BasisController.getBlockchainSize() > 1) {
                                        temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
                                    }


                                    System.out.println("2: temp: " + temp);
                                    System.out.println("2: blockchainsize: " + BasisController.getBlockchainSize());
                                    System.out.println("2: sublocks: " + subBlocks.size());

                                    if (BasisController.getBlockchainSize() > 1 && !temp.isValidation()) {
                                        return -10;
                                    }

                                    //метод, который сохраняет скачанные порции блока, в блокчейн
                                    addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    if (!temp.isValidation()) {
                                        temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                                    }
                                    BasisController.setShortDataBlockchain(temp);
                                    BasisController.setBlockchainSize((int) BasisController.getShortDataBlockchain().getSize());
                                    BasisController.setBlockchainValid(BasisController.getShortDataBlockchain().isValidation());
//
                                    tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                                    BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));

                                    json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                                    UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                                }
                            }
                        } else {

                            //здесь нужно проверить
                            //If the difference is not greater than PORTION_DOWNLOAD, then downloads once a portion of this difference
                            //Если разница не больше PORTION_DOWNLOAD, то скачивает один раз порцию эту разницу
                            subBlockchainEntity = new SubBlockchainEntity(blocks_current_size, size);

                            subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);


                            List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                            System.out.println("3:download sub block: " + subBlocks.size());
//                            tempBalances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
                            tempBalances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
                            List<String> sign = new ArrayList<>();

                            if (BasisController.getBlockchainSize() > Seting.PORTION_BLOCK_TO_COMPLEXCITY) {
                                lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                                        blockService.findBySpecialIndexBetween(
                                                (BasisController.getPrevBlock().getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                                                BasisController.getPrevBlock().getIndex() + 1
                                        )
                                );
                            }

                            DataShortBlockchainInformation temp = new DataShortBlockchainInformation();
                            if (BasisController.getBlockchainSize() > 1) {
                                temp = Blockchain.shortCheck(BasisController.getPrevBlock(), subBlocks, BasisController.getShortDataBlockchain(), lastDiff, tempBalances, sign);
                            }

                            System.out.println("3: temp: " + temp);
                            System.out.println("3: blockchainsize: " + BasisController.getBlockchainSize());
                            System.out.println("3: sublocks: " + subBlocks.size());

                            if (temp.getSize() > 1 && !temp.isValidation()) {
                                System.out.println("error resolve 2 in portion upper < 500");

                                return -10;
                            }

                            addBlock3(subBlocks, balances, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            if (!temp.isValidation()) {
                                System.out.println("check all file");
                                temp = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            }

                            BasisController.setShortDataBlockchain(temp);
                            BasisController.setBlockchainSize((int) BasisController.getShortDataBlockchain().getSize());
                            BasisController.setBlockchainValid(BasisController.getShortDataBlockchain().isValidation());

//                            prevBlock = Blockchain.indexFromFile(blockchainSize - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);
                            EntityBlock tempBlock = blockService.findBySpecialIndex(BasisController.getBlockchainSize() - 1);
                            BasisController.setPrevBlock(UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock));

                            String json = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                            UtilsFileSaveRead.save(json, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
                        }
                        System.out.println("size temporaryBlockchain: ");
                        System.out.println("resolve: temporaryBlockchain: ");
                    } else {
                        System.out.println(":BasisController: resove: size less: " + size + " address: " + s);
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("BasisController: resove2: " + e.getMessage());
                    continue;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
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
     * rewrites the blockchain into files and into the h2 database. From here they are called
     * * methods that calculate balance and other calculations.
     * производит перезапись блокчейна в файлы и в базу h2. Отсюда вызываются
     * методы которые, вычисляют баланс и другие вычисления.
     */


    @Transactional
    public void addBlock3(List<Block> originalBlocks, Map<String, Account> balances, String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        java.sql.Timestamp lastIndex = new java.sql.Timestamp(UtilsTime.getUniversalTimestamp());
        UtilsBalance.setBlockService(blockService);
        Blockchain.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);
        List<EntityBlock> list = new ArrayList<>();
        List<String> signs = new ArrayList<>();
        //пакет законов.
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();

        originalBlocks = originalBlocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());

        Map<String, Account> tempBalances = UtilsUse.balancesClone(balances);
        long start = UtilsTime.getUniversalTimestamp();
        for (Block block : originalBlocks) {
            System.out.println(" :BasisController: addBlock3: blockchain is being updated: index" + block.getIndex());
            //записывает блок в файл.
            UtilsBlock.saveBLock(block, filename);
            EntityBlock entityBlock = UtilsBlockToEntityBlock.blockToEntityBlock(block);
            list.add(entityBlock);

            //вычисляет баланс исходя из блока.
            calculateBalance(balances, block, signs);

            //сохраняет новые законы в файл
            allLaws = UtilsLaws.getLaws(block, Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE, allLaws);

        }

        long finish = UtilsTime.getUniversalTimestamp();
        System.out.println("UtilsResolving: addBlock3: for: time different: " + UtilsTime.differentMillSecondTime(start, finish));

        //записывает в базу h2,
//        BlockService.saveAllBlock(list);
        blockService.saveAllBLockF(list);


        tempBalances = UtilsUse.differentAccount(tempBalances, balances);
        List<EntityAccount> accountList = blockService.findByAccountIn(tempBalances);
        accountList = UtilsUse.mergeAccounts(tempBalances, accountList);


        start = UtilsTime.getUniversalTimestamp();
        blockService.saveAccountAllF(accountList);
        finish = UtilsTime.getUniversalTimestamp();

        System.out.println("UtilsResolving: addBlock3: time save accounts: " + UtilsTime.differentMillSecondTime(start, finish));
        System.out.println("UtilsResolving: addBlock3: total different balance: " + tempBalances.size());
        System.out.println("UtilsResolving: addBlock3: total original balance: " + balances.size());

//        //удаляет старый файл балансов
//        Mining.deleteFiles(Seting.ORIGINAL_BALANCE_FILE);
//        //записывает актуальный файл балансов.
//        SaveBalances.saveBalances(balances, Seting.ORIGINAL_BALANCE_FILE);


        //возвращает все законы с балансом,
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

    public boolean isRightDSB(DataShortBlockchainInformation actual, DataShortBlockchainInformation expected) {
        boolean result = true;
        if (actual.isValidation() != expected.isValidation()
                || actual.getSize() < expected.getSize()
                || actual.getTransactions() < expected.getTransactions()
                || actual.getStaking() < expected.getStaking()
                || actual.getBigRandomNumber() < expected.getBigRandomNumber()
                || actual.getHashCount() < expected.getHashCount()) {
            result = false;
        }
        return result;
    }

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

