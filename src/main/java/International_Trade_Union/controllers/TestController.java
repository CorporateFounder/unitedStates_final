package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.entities.EntityDtoTransaction;
import International_Trade_Union.entity.repository.EntityAccountRepository;
import International_Trade_Union.entity.repository.EntityBlockRepository;
import International_Trade_Union.entity.repository.EntityDtoTransactionRepository;
import International_Trade_Union.entity.repository.EntityLawsRepository;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TestController {

    @Autowired
    EntityBlockRepository entityBlockRepository;
    @Autowired
    EntityDtoTransactionRepository entityDtoTransactionRepository;
    @Autowired
    EntityLawsRepository entityLawsRepository;
    @Autowired
    EntityAccountRepository entityAccountRepository;

    @Autowired
    BlockService blockService;

    public String send(@RequestParam String sender,
                       @RequestParam String recipient,
                       @RequestParam Double dollar,
                       @RequestParam Double stock,
                       @RequestParam Double reward,
                       @RequestParam String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        Base base = new Base58();
        String result = "wrong";
        if (dollar == null)
            dollar = 0.0;

        if (stock == null)
            stock = 0.0;

        if (reward == null)
            reward = 0.0;

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
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
        System.out.println("Main Controller: new transaction: vote: " + VoteEnum.YES);
        dtoTransaction.setSign(sign);
        Directors directors = new Directors();
        System.out.println("sender: " + sender);
        System.out.println("recipient: " + recipient);
        System.out.println("dollar: " + dollar + ": class: " + dollar.getClass());
        System.out.println("stock: " + stock + ": class: " + stock.getClass());
        System.out.println("reward: " + reward + ": class: " + reward.getClass());
        System.out.println("password: " + password);
        System.out.println("sign: " + dtoTransaction.toSign());
        System.out.println("verify: " + dtoTransaction.verify());

        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t -> t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if (corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)) {
                System.out.println("sending" + "wrong transaction: Position to be equals whith send");
                return result;
            }
            result = dtoTransaction.toSign();

            String str = base.encode(dtoTransaction.getSign());
            System.out.println("sign: " + str);
            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s + "/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if (BasisController.getExcludedAddresses().contains(url)) {
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                } catch (Exception e) {
                    System.out.println("exception discover: " + original);

                }
            }


        } else
            return result;
        return result;

    }

    @GetMapping("/testFindById")
    @ResponseBody
    public boolean testFindById() throws IOException {
        int size = BasisController.getBlockchainSize();
        Block block = Blockchain.indexFromFile(size - 1, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        EntityBlock tempBlock = entityBlockRepository.findById(size);
        Block testBlock = UtilsBlockToEntityBlock.entityBlockToBlock(tempBlock);
        System.out.println("***********************************************");
        System.out.println(testBlock);
        System.out.println("***********************************************");
        System.out.println(block);
        return block.equals(testBlock);
    }

    @GetMapping("/testSendBlock")
    @ResponseBody
    public boolean sendTest() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        System.out.println("test send Block ");
        for (int i = 1; i < BasisController.getBlockchainSize() ; i++) {
            System.out.println("test send block " + i);
            List list = new ArrayList();
            list.add(UtilsBlockToEntityBlock.entityBlockToBlock(entityBlockRepository.findById(i)));
            BasisController.sendAllBlocksToStorage(list);
        }
        return true;
    }

    @GetMapping("/testSubBlock")
    @ResponseBody
    public boolean testSubBlock() throws IOException {
        int size = BasisController.getBlockchainSize();
        int startSize = BasisController.getBlockchainSize() - Seting.PORTION_BLOCK_TO_COMPLEXCITY;
        List<EntityBlock> entityBlocks =
                entityBlockRepository.findAllByIdBetween(startSize + 1, size );
        List<Block> blocksDb = UtilsBlockToEntityBlock.entityBlocksToBlocks(entityBlocks);
        List<Block> blocks = Blockchain.subFromFile(startSize, size, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        System.out.println("***********************************************************");
        System.out.println(blocksDb.get(0).getIndex() + " hash: " + blocksDb.get(0).getHashBlock());
        System.out.println(blocksDb.get(blocksDb.size()-1).getIndex() +
                " hash: " + blocksDb.get(blocksDb.size()-1).getHashBlock());

        System.out.println("***********************************************************");
        System.out.println(blocks.get(0).getIndex() + " hash: " + blocks.get(0).getHashBlock());
        System.out.println(blocks.get(blocks.size()-1).getIndex() +
                " hash: " + blocks.get(blocks.size()-1).getHashBlock());
        System.out.println("***********************************************************");
        System.out.println("blocks size: " + blocks.size());
        System.out.println("blocksDb size: " + blocksDb.size());

        Block prevBlock = BasisController.getPrevBlock();
        List<Block> tempBlockchain = Blockchain.subFromFile(
                (int) ((prevBlock.getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY),
                (int) (prevBlock.getIndex() + 1), Seting.ORIGINAL_BLOCKCHAIN_FILE
        );
        List<Block> tempBlockchain2 =UtilsBlockToEntityBlock.entityBlocksToBlocks(
                BlockService.findAllByIdBetween(
                        (prevBlock.getIndex()+ 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY + 1,
                        prevBlock.getIndex() + 1
                )
        );
        System.out.println("***********************************************************");
        System.out.println("tempblochain size: " + tempBlockchain.get(tempBlockchain.size()-1).getIndex());
        System.out.println("tempblochain1 size : " + tempBlockchain2.get(tempBlockchain2.size()-1).getIndex());
        System.out.println("tempblochain: " + tempBlockchain.get(0).getIndex());
        System.out.println("tempblochain1: " + tempBlockchain2.get(0).getIndex());

        System.out.println("***********************************************************");

        return blocks.equals(blocksDb);
    }

    @GetMapping("/sendReward")
    @ResponseBody
    public void testBalance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> originals = SaveBalances.readLineObject("C://strategy1/balance/");
        Map<String, Account> forks = SaveBalances.readLineObject("C://strategy2/balance/");
        Map<String, Account> differents = new HashMap<>();


        Map<String, Account> cheaterAccount = new HashMap<>();
        Map<String, Integer> countAddressCheater = new HashMap<>();
        BufferedReader reader = null;
        try {
            // Открываем файл для чтения
            reader = new BufferedReader(new FileReader("C://cheater miner/1.txt"));

            String line;
            // Читаем файл построчно, пока не достигнем конца файла (null)
            while ((line = reader.readLine()) != null) {
                // Обрабатываем текущую строку
                String[] arr = line.split(":");
                if (cheaterAccount.containsKey(arr[0])) {
                    double dollar = Double.valueOf(arr[1]) + cheaterAccount.get(arr[0]).getDigitalDollarBalance();
                    double stock = Double.valueOf(arr[2]) + cheaterAccount.get(arr[0]).getDigitalStockBalance();
                    ;
                    Account account = new Account(arr[0], dollar, stock);
                    cheaterAccount.put(arr[0], account);
                } else {
                    String address = arr[0];

                    try {
                        double dollar = Double.valueOf(arr[1]);
                        double stock = Double.valueOf(arr[2]);
//                        System.out.println("dollar: " + dollar + " stock: " + stock + " index: " + arr[3]);
                        Account account = new Account(address, dollar, stock);
                        cheaterAccount.put(address, account);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }

                if (countAddressCheater.containsKey(arr[0])) {
                    int count = countAddressCheater.get(arr[0]) + 1;
                    countAddressCheater.put(arr[0], count);
                } else {
                    countAddressCheater.put(arr[0], 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Закрываем BufferedReader после использования
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for (Map.Entry<String, Account> cheater : cheaterAccount.entrySet()) {
            System.out.println("cheater: " + cheater);
        }
        for (Map.Entry<String, Integer> countAccount : countAddressCheater.entrySet()) {
            System.out.println("chear how much: " + countAccount);
        }


        //----------------------------------------------------------------------
        for (Map.Entry<String, Account> original : originals.entrySet()) {
            String address = original.getKey();
            if (forks.containsKey(address)) {
                double originalDollar = original.getValue().getDigitalDollarBalance();
                double originalStock = original.getValue().getDigitalStockBalance();
                double forkDollar = forks.get(address).getDigitalDollarBalance();
                double forkStock = forks.get(address).getDigitalStockBalance();
                double resultDollar = originalDollar - forkDollar;
                double resultStock = originalStock - forkStock;
                if (resultDollar < 0) {
                    resultDollar = 0;
                    System.out.println("dollar original: " + originalDollar + " fork: " + forkDollar);
                }


                if (resultStock < 0) {
                    resultStock = 0;
                    System.out.println("stock original: " + originalStock + " fork: " + forkStock);
                }

                Account account = new Account(address, resultDollar, resultStock);
                differents.put(address, account);
            } else {
                double originalDollar = original.getValue().getDigitalDollarBalance();
                double originalStock = original.getValue().getDigitalStockBalance();
                Account account = new Account(address, originalDollar, originalStock);
                differents.put(address, account);
            }
        }

        HashMap<String, Account> afterFork = new HashMap<>();
        for (Map.Entry<String, Account> different : differents.entrySet()) {
            String address = different.getKey();
            if (forks.containsKey(address)) {
                double originalDollar = different.getValue().getDigitalDollarBalance();
                double originalStock = different.getValue().getDigitalStockBalance();
                double forkDollar = forks.get(address).getDigitalDollarBalance();
                double forkStock = forks.get(address).getDigitalStockBalance();

                double resultDollar = forkDollar + originalDollar;
                double resultStock = forkStock + originalStock;
                Account account = new Account(address, resultDollar, resultStock);
                afterFork.put(address, account);
            } else {
                double originalDollar = different.getValue().getDigitalDollarBalance();
                double originalStock = different.getValue().getDigitalStockBalance();
                Account account = new Account(address, originalDollar, originalStock);
                afterFork.put(address, account);
            }
        }
        for (Map.Entry<String, Account> different : differents.entrySet()) {
            String address = different.getKey();
            if (cheaterAccount.containsKey(address)) {
                double dollar = different.getValue().getDigitalDollarBalance();
                double stock = different.getValue().getDigitalStockBalance();
                dollar = dollar - cheaterAccount.get(address).getDigitalDollarBalance();
                stock = stock - cheaterAccount.get(address).getDigitalStockBalance();
                if (dollar < 0)
                    dollar = 0;
                if (stock < 0)
                    stock = 0;

                differents.get(address).setDigitalDollarBalance(dollar);
                differents.get(address).setDigitalStockBalance(stock);
            }
        }


        List<Account> originalAccounts = originals.entrySet().stream()
                .map(t -> t.getValue()).collect(Collectors.toList());
        List<Account> afterForkAccounts = afterFork.entrySet().stream()
                .map(t -> t.getValue())
                .collect(Collectors.toList());
        List<Account> differentAcount = differents.entrySet().stream()
                .map(t -> t.getValue()).collect(Collectors.toList());


        double originalSumDollar = originalAccounts.stream().mapToDouble(t -> t.getDigitalDollarBalance()).sum();
        double originalSumStock = originalAccounts.stream().mapToDouble(t -> t.getDigitalStockBalance()).sum();
        double afterForkSumDollar = afterForkAccounts.stream().mapToDouble(t -> t.getDigitalDollarBalance()).sum();
        double afterForkSumsStock = afterForkAccounts.stream().mapToDouble(t -> t.getDigitalStockBalance()).sum();

        System.out.printf("dollar: original: %f fork: %f: \n", originalSumDollar, afterForkSumDollar);
        System.out.printf("stock: original: %f fork: %f: \n", originalSumStock, afterForkSumsStock);
        System.out.printf(" different dollar: %f: \n", (originalSumDollar - afterForkSumDollar));
        System.out.printf(" different stock: %f: \n", (originalSumStock - afterForkSumsStock));

        System.out.println("rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM: " + cheaterAccount.containsKey("rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM"));
        System.out.println("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43: " + cheaterAccount.containsKey("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43"));
        System.out.println("23o5AEqbbRnmkiggoWbix2dRwtpULVnFLba6eZLkq5Udw: " + cheaterAccount.containsKey("23o5AEqbbRnmkiggoWbix2dRwtpULVnFLba6eZLkq5Udw"));
        System.out.println("25Ybc6xyHoCS6KnFc6ezb4QHq4hKVRJ5hSmjJDtoLcyK2: " + cheaterAccount.containsKey("25Ybc6xyHoCS6KnFc6ezb4QHq4hKVRJ5hSmjJDtoLcyK2    "));


        String sender = "";
        String password = "";


        for (Map.Entry<String, Account> send : differents.entrySet()) {
            String recipient = send.getKey();
            double dollar = send.getValue().getDigitalDollarBalance();
            double stock = send.getValue().getDigitalStockBalance();
            send(sender, recipient, dollar, stock, 0.0, password);
        }

    }

    @GetMapping("/give")
    @ResponseBody
    public boolean GiveOriginalBlocks() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        List<Block> addBlocks = new ArrayList<>();
        List<Block> blocks = blockchain.getBlockchainList();
        UtilsBlock.deleteFiles();
        for (Block block : blocks) {
            if (block.getIndex() > 0 && block.getHashBlock().equals(block.hashForTransaction())) {
                break;
            }
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        }

        return true;
    }

    @GetMapping("test2")
    @ResponseBody
    public SubBlockchainEntity test() {
        SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(0, 10);
        return subBlockchainEntity;
    }

    @GetMapping("/testBlock")
    @ResponseBody
    public boolean testBlock() throws IOException, CloneNotSupportedException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Block prevBlock = Blockchain.indexFromFile(600, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        List<Block> original = Blockchain.subFromFile(
                (int) (prevBlock.getIndex() - Seting.PORTION_BLOCK_TO_COMPLEXCITY),
                (int) (prevBlock.getIndex() + 1),
                Seting.ORIGINAL_BLOCKCHAIN_FILE
        );

        List<Block> lastDiff = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                BlockService.findAllByIdBetween(
                        (prevBlock.getIndex() + 1) - Seting.PORTION_BLOCK_TO_COMPLEXCITY,
                        prevBlock.getIndex() + 1
                )
        );
        System.out.println("**************************************************************");
        original.forEach(t -> System.out.printf("index: %d, hash %s\n",
                t.getIndex(), t.getHashBlock()));
        System.out.println("**************************************************************");
        lastDiff.forEach(t -> System.out.printf("index: %d, hash %s\n",
                t.getIndex(), t.getHashBlock()));
        System.out.println("***************************************************************");

        return lastDiff.equals(original);
    }

    @GetMapping("/testBlock1")
    @ResponseBody
    public String testBlock1() throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, ParseException {
        int size = 37903;
        while (true) {
            if (size % 288 == 0) {

                break;
            }
            size++;

        }
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        List<Block> blocks = blockchain.getBlockchainList();
        System.out.println("blockchain size: " + blockchain.sizeBlockhain());
        int diff = 0;

        for (int i = 0; i < 600; i++) {

            diff = UtilsBlock.difficulty(blocks,
                    Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
            blocks.remove(blocks.size() - 1);
            if (diff > 5) {
                System.out.println("diff: " + diff + " index: " + blocks.get(blocks.size() - 1).getIndex());
                break;
            }

        }


        System.out.println("******************");
        System.out.println("diff " + diff);
        System.out.println("size: " + size);

        return "0";
    }

    private static final int TARGET_BLOCK_TIME = 150; // 150 секунд
    private static final int RETARGET_INTERVAL = 288; // 288 блоков

    public static int getDifficulty(List<Block> blockchain) {
        Block latestBlock = blockchain.get(blockchain.size() - 1);
        long timeSinceLastBlock = latestBlock.getTimestamp().getTime() - blockchain.get(blockchain.size() - RETARGET_INTERVAL - 1).getTimestamp().getTime();
        long expectedBlocksPerSecond = RETARGET_INTERVAL / TARGET_BLOCK_TIME;
        int difficulty = (int) Math.pow(2, (timeSinceLastBlock / expectedBlocksPerSecond) - 1);
        return difficulty;
    }

    @GetMapping("/findCheater")
    public void findCheater() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain tempblockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        for (Block block : tempblockchain.getBlockchainList()) {
            if (!block.getHashBlock().equals(block.hashForTransaction())) {
                for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                    if (dtoTransaction.getSender().equals(Seting.BASIS_ADDRESS) &&
                            dtoTransaction.getCustomer().equals(block.getMinerAddress())) {
                        System.out.printf("cheater address: %s, dollar: %f, stock %f, index %d\n",
                                block.getMinerAddress(),
                                dtoTransaction.getDigitalDollar(),
                                dtoTransaction.getDigitalStockBalance(),
                                block.getIndex());
                    }
                }

            }
        }

    }

    @GetMapping("/testSaveBlockToDb")
    @ResponseBody
    public boolean testSaveDb() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        Block block = Blockchain.indexFromFile(10, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        EntityBlock entityBlock =
                UtilsBlockToEntityBlock.blockToEntityBlock(block);
        System.out.println("entity block: " + entityBlock.getIndex());
        entityBlockRepository.save(entityBlock);
        block = Blockchain.indexFromFile(11, Seting.ORIGINAL_BLOCKCHAIN_FILE);
        entityBlock =
                UtilsBlockToEntityBlock.blockToEntityBlock(block);
        System.out.println("entity block: " + entityBlock.getIndex());

//        entityBlockRepository.save(entityBlock);
        BlockService.saveBlock(entityBlock);
        return true;
    }

    @GetMapping("/testShowBlockDb")
    @ResponseBody
    public List<EntityBlock> testShowDb() {
//        List<EntityBlock> entityBlocks =
//                entityBlockRepository.findAll();

        List<EntityBlock> entityBlocks =
                BlockService.findAll();
        return entityBlocks;
    }

    @GetMapping("/testSizeBlockDb")
    @ResponseBody
    public long testSize() {

        long size = BasisController.getBlockchainSize();
        long sizeDb = BlockService.countBlock();
        System.out.println("size: " + size);
        System.out.println("sizeDb: " + sizeDb);
        return sizeDb;
    }

    @GetMapping("/testDeleteFiles")
    @ResponseBody
    public boolean testDeleteFiles() {
        UtilsBlock.deleteFiles();
        return true;
    }

    @GetMapping("/testCheckEqualsBlock")
    @ResponseBody
    public boolean checkBlock() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        List<EntityBlock> entityBlocks = entityBlockRepository.findAll();
        List<Block> testBlocks = UtilsBlockToEntityBlock.entityBlocksToBlocks(entityBlocks);
        Blockchain blockchain1 = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        List<Block> blocks = blockchain1.getBlockchainList();

//        EntityBlock entityBlock = entityBlockRepository.findById(2220);
        EntityBlock entityBlock = entityBlockRepository.findById(45909);
//        Block originalBlock = blocks.get(2219);
        Block originalBlock = blocks.get(45908);
//        originalBlock.getDtoTransactions().get(0).getLaws().setPacketLawName("wrong test");
        Block testBlock = UtilsBlockToEntityBlock.entityBlockToBlock(entityBlock);
        System.out.println("__________________________________________");
        System.out.println("originalBlock getHashBlock: " + originalBlock.getHashBlock());
        System.out.println("entityBlock getHashBlock: " + entityBlock.getHashBlock());
        System.out.println("testBlock getHashBlock: " + testBlock.getHashBlock());
        System.out.println("__________________________________________");

        System.out.println("originalBlock getPreviousHash: " + originalBlock.getPreviousHash());
        System.out.println("entityBlock getPreviousHash: " + entityBlock.getPreviousHash());
        System.out.println("testBlock getPreviousHash: " + testBlock.getPreviousHash());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getHashCompexity: " + originalBlock.getHashCompexity());
        System.out.println("entityBlock getHashCompexity: " + entityBlock.getHashCompexity());
        System.out.println("testBlock getHashCompexity: " + testBlock.getHashCompexity());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getFounderAddress: " + originalBlock.getFounderAddress());
        System.out.println("entityBlock getFounderAddress: " + entityBlock.getFounderAddress());
        System.out.println("testBlock getFounderAddress: " + testBlock.getFounderAddress());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getMinerAddress: " + originalBlock.getMinerAddress());
        System.out.println("entityBlock getMinerAddress: " + entityBlock.getMinerAddress());
        System.out.println("testBlock getMinerAddress: " + testBlock.getMinerAddress());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getMinerRewards: " + originalBlock.getMinerRewards());
        System.out.println("entityBlock getMinerRewards: " + entityBlock.getMinerRewards());
        System.out.println("testBlock getMinerRewards: " + testBlock.getMinerRewards());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getMinerRewards: " + originalBlock.getMinerRewards());
        System.out.println("entityBlock getMinerRewards: " + entityBlock.getMinerRewards());
        System.out.println("testBlock getMinerRewards: " + testBlock.getMinerRewards());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getIndex: " + originalBlock.getIndex());
        System.out.println("entityBlock getIndex: " + entityBlock.getIndex());
        System.out.println("testBlock getIndex: " + testBlock.getIndex());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getRandomNumberProof: " + originalBlock.getRandomNumberProof());
        System.out.println("entityBlock getRandomNumberProof: " + entityBlock.getRandomNumberProof());
        System.out.println("testBlock getRandomNumberProof: " + testBlock.getRandomNumberProof());
        System.out.println("__________________________________________");
        System.out.println("originalBlock getTimestamp: " + originalBlock.getTimestamp());
        System.out.println("entityBlock getTimestamp: " + entityBlock.getTimestamp());
        System.out.println("testBlock getTimestamp: " + testBlock.getTimestamp());

        for (int j = 0; j < blocks.size(); j++) {
            entityBlock = entityBlockRepository.findById(j+1);
            testBlock = UtilsBlockToEntityBlock.entityBlockToBlock(entityBlock);
            Block block = blocks.get(j);
            System.out.println("block: index: " + block.getIndex() + " testBlock: " +
                    testBlock.getIndex() + " entityBlock: " + entityBlock.getIndex());

            if(!block.equals(testBlock)){
                System.out.println("wrong: ");
                System.out.println("block:\n " + block);
                System.out.println("--------------------------");
                System.out.println("testBlock:\n" + testBlock);
                break;
            }
        }
//        System.out.println("__________________________________________");
       return UtilsBlockToEntityBlock.compareLists(blocks, testBlocks);
    }


    @GetMapping("/testCheckBalance")
    @ResponseBody
    public boolean checkBalance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balnces = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        List<Account> original = balnces.entrySet().stream()
                .map(t -> t.getValue()).collect(Collectors.toList());

        boolean result = true;
        List<EntityAccount> temp = entityAccountRepository.findAll();
        List<Account> db = UtilsAccountToEntityAccount.EntityAccountToAccount(temp);
        System.out.println("*********************************************************");
        for (Account account : db) {
            if (balnces.containsKey(account.getAccount())) {
                System.out.println("------------------------------------------");
                Account account1 = balnces.get(account.getAccount());
                System.out.printf("db: %s, dollar %f, stock %f\n", account.getAccount(),
                        account.getDigitalDollarBalance(), account.getDigitalStockBalance());
                System.out.printf("original: %s, dollar %f, stock %f\n", account1.getAccount(),
                        account1.getDigitalDollarBalance(), account1.getDigitalStockBalance());

                if (account.getDigitalDollarBalance() == account1.getDigitalDollarBalance() &&
                        account.getDigitalStockBalance() == account1.getDigitalStockBalance()) {
                    System.out.println("true");
                } else {
                    System.out.println("false");
                    result = false;
                }
                System.out.println("------------------------------------------");
            } else {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++");
                System.out.printf("db: %s, dollar %f, stock %f\n", account.getAccount(),
                        account.getDigitalDollarBalance(), account.getDigitalStockBalance());
                System.out.println("++++++++++++++++++++++++++++++++++++++++++");
            }
        }

        System.out.println("*********************************************************");

        System.out.println("size db: " + db.size());
        System.out.println("size original: " + original.size());


        return result;
    }


}
