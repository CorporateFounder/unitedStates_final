package International_Trade_Union.utils;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SendBlocksEndInfo;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.User;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static International_Trade_Union.controllers.BasisController.findAddresses;
import static International_Trade_Union.controllers.BasisController.getNodes;

public class UtilsMining {
    @Autowired
    BlockService blockService;

    @Autowired
    Mining mining;
    public  synchronized String mining(UtilsResolving utilsResolving) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {

        BasisController.setMining(true);
        try {
            findAddresses();
            utilsResolving.resolve3();

            List<Block> tempBlockchain;

            tempBlockchain = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                    blockService.findBySpecialIndexBetween(
                            (BasisController.getBlockchainSize()) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                            BasisController.getBlockchainSize() - 1
                    )
            );

            Block prevBlock = tempBlockchain.get(tempBlockchain.size() - 1);
            long index = prevBlock.getIndex() + 1;
            Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
            Account miner = balances.get(User.getUserAddress());

            BasisController.setMinerShow(miner);

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


            if (BasisController.getBlockchainSize() % 288 == 0) {
                System.out.println("clear storage transaction because is old");
                AllTransactions.clearAllTransaction();
            }
            //считывает все балансы из файла.
            //reads all balances from a file.
            balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);

            String json = UtilsFileSaveRead.read(Seting.TEMPORARY_BLOCKCHAIN_FILE);
            if (json != null && !json.isEmpty()) {
                BasisController.setShortDataBlockchain(UtilsJson.jsonToDataShortBlockchainInformation(json));
                BasisController.setBlockchainSize((int) BasisController.getShortDataBlockchain().getSize());
                BasisController.setBlockchainValid(BasisController.getShortDataBlockchain().isValidation());
            }


            if (BasisController.getShortDataBlockchain() == null) {
                System.out.println("shortBlockchainInformation null");
                BasisController.setShortDataBlockchain(Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE));
                String stringJson = UtilsJson.objToStringJson(BasisController.getShortDataBlockchain());
                UtilsFileSaveRead.save(stringJson, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);

                BasisController.setShortDataBlockchain(UtilsJson.jsonToDataShortBlockchainInformation(json));
                BasisController.setBlockchainSize((int) BasisController.getShortDataBlockchain().getSize());
                BasisController.setBlockchainValid(BasisController.getShortDataBlockchain().isValidation());
            }

            if (BasisController.getBlockchainSize() <= 1) {
                System.out.println("save genesis block");
                Blockchain blockchain1 = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
                //сохранение генезис блока. preservation of the genesis of the block.
                if (BasisController.getBlockchainSize() == 1) {
                    UtilsBlock.saveBLock(blockchain1.genesisBlock(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
                }

                //получить список балансов из файла. get a list of balances from a file.
                List<String> signs = new ArrayList<>();
                balances = mining.getBalances(Seting.ORIGINAL_BALANCE_FILE, blockchain1, balances, signs);
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

            BasisController.setMinerShow(miner);
            System.out.println("BasisController: mining: account miner: " + miner);
            if (miner == null) {
                //если в блокчейне не было баланса майнера, то баланс равен нулю.
                //if there was no miner balance in the blockchain, then the balance is zero.
                miner = new Account(User.getUserAddress(), 0, 0, 0);
            }

            //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions.
            //transactions that we added to the block and now need to be deleted from the file in the resources/transactions folder.
            List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();

            List<Block> temp
                    = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                    blockService.findBySpecialIndexBetween(
                            (BasisController.getBlockchainSize()) - Seting.CHECK_DTO,
                            BasisController.getBlockchainSize() - 1
                    )
            );
            //удаляет транзакции которые были ранее уже добавлены в блок.
            //deletes transactions that were previously added to the block.
            temporaryDtoList = UtilsBlock.validDto(temp, temporaryDtoList);

            //отказ от транзакций которые меньше данного вознаграждения, если количество транзакций на блок выше 1000.
            //rejection of transactions that are less than this reward, if the number of transactions per block is above 1000.
            temporaryDtoList = UtilsTransaction.reward(temporaryDtoList, BasisController.getMinDollarRewards());

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

            String addresFounder = blockService.findBySpecialIndex(0).getFounderAddress();
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
            BasisController.setMining(false);
            Mining.miningIsObsolete = false;
        }
        return "ok";
    }
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
        if (BasisController.getBlockchainSize() > bigsize) {
            return 1;
        } else if (BasisController.getBlockchainSize() < bigsize) {
            return -1;
        } else if (BasisController.getBlockchainSize() == bigsize) {
            return 0;
        } else {
            return -4;
        }
    }
}
