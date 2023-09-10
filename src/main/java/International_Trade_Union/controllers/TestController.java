package International_Trade_Union.controllers;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.tomcat.jni.Socket.send;

@Controller
public class TestController {


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
                if(cheaterAccount.containsKey(arr[0])){
                    double dollar = Double.valueOf(arr[1]) + cheaterAccount.get(arr[0]).getDigitalDollarBalance();
                    double stock = Double.valueOf(arr[2])+ cheaterAccount.get(arr[0]).getDigitalStockBalance();;
                    Account account = new Account(arr[0], dollar, stock);
                    cheaterAccount.put(arr[0], account);
                }else {
                    String address = arr[0];

                    try {
                        double dollar = Double.valueOf(arr[1]);
                        double stock = Double.valueOf(arr[2]);
//                        System.out.println("dollar: " + dollar + " stock: " + stock + " index: " + arr[3]);
                        Account account = new Account(address, dollar, stock);
                        cheaterAccount.put(address, account);
                    }catch (ArrayIndexOutOfBoundsException e){
                        continue;
                    }catch (NumberFormatException e){
                        continue;
                    }
                }

                if(countAddressCheater.containsKey(arr[0])){
                    int count = countAddressCheater.get(arr[0]) + 1;
                    countAddressCheater.put(arr[0], count);
                }else {
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
            if(cheaterAccount.containsKey(address)){
                double dollar = different.getValue().getDigitalDollarBalance();
                double stock = different.getValue().getDigitalStockBalance();
                dollar = dollar - cheaterAccount.get(address).getDigitalDollarBalance();
                stock = stock - cheaterAccount.get(address).getDigitalStockBalance();
                if(dollar < 0)
                    dollar = 0;
                if(stock < 0)
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
                .map(t->t.getValue()).collect(Collectors.toList());


        double originalSumDollar = originalAccounts.stream().mapToDouble(t->t.getDigitalDollarBalance()).sum();
        double originalSumStock = originalAccounts.stream().mapToDouble(t->t.getDigitalStockBalance()).sum();
        double afterForkSumDollar = afterForkAccounts.stream().mapToDouble(t->t.getDigitalDollarBalance()).sum();
        double afterForkSumsStock = afterForkAccounts.stream().mapToDouble(t->t.getDigitalStockBalance()).sum();

        System.out.printf("dollar: original: %f fork: %f: \n", originalSumDollar, afterForkSumDollar);
        System.out.printf("stock: original: %f fork: %f: \n", originalSumStock, afterForkSumsStock);
        System.out.printf(" different dollar: %f: \n", (originalSumDollar-afterForkSumDollar));
        System.out.printf(" different stock: %f: \n", (originalSumStock-afterForkSumsStock));

        System.out.println("rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM: " + cheaterAccount.containsKey("rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM"));
        System.out.println("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43: " + cheaterAccount.containsKey("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43"));
        System.out.println("23o5AEqbbRnmkiggoWbix2dRwtpULVnFLba6eZLkq5Udw: " + cheaterAccount.containsKey("23o5AEqbbRnmkiggoWbix2dRwtpULVnFLba6eZLkq5Udw"));
        System.out.println("25Ybc6xyHoCS6KnFc6ezb4QHq4hKVRJ5hSmjJDtoLcyK2: " + cheaterAccount.containsKey("25Ybc6xyHoCS6KnFc6ezb4QHq4hKVRJ5hSmjJDtoLcyK2    "));
        String sender = "jQCqtL2VXmLznKcQQmiMdmf2JeNrsioG3njkHXNRX2Fo";
        String password = "";


        for (Map.Entry<String,Account> send : differents.entrySet()) {
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
            if(block.getIndex() > 0 && block.getHashBlock().equals(block.hashForTransaction())){
                break;
            }
            UtilsBlock.saveBLock(block, Seting.ORIGINAL_BLOCKCHAIN_FILE);

        }

        return true;
    }
    @GetMapping("test2")
    @ResponseBody
    public SubBlockchainEntity test(){
        SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(0, 10);
        return subBlockchainEntity;
    }

    @GetMapping("/testBlock")
    @ResponseBody
    public int testBlock() throws IOException, CloneNotSupportedException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain tempblockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions
        List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();
        System.out.println("temporaryDtoList: " + temporaryDtoList);
        System.out.println("********************************************************");
        //отказ от дублирующих транзакций
        temporaryDtoList = UtilsBlock.validDto(tempblockchain.getBlockchainList(), temporaryDtoList);
        System.out.println("temporaryDtoList: " + temporaryDtoList);
        System.out.println("*********************************************************");
        //отказ от транзакций которые меньше данного вознаграждения
        temporaryDtoList = UtilsTransaction.reward(temporaryDtoList, 0);
        System.out.println("**********************************************************");

        //раз в три для очищяет файлы в папке resources/sendedTransaction данная папка
        //хранит уже добавленые в блокчейн транзации, чтобы повторно не добавлять в
        //в блок уже добавленные транзакции

        AllTransactions.clearUsedTransaction(AllTransactions.getInsanceSended());
        return 0;
    }

    @GetMapping("/testBlock1")
    @ResponseBody
    public String testBlock1() throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, ParseException {
        int size = 37903;
        while (true){
            if(size % 288 == 0){

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

            diff= UtilsBlock.difficulty(blocks,
                    Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
            blocks.remove(blocks.size()-1);
            if(diff > 5){
                System.out.println("diff: " + diff + " index: "+ blocks.get(blocks.size()-1).getIndex());
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
}
