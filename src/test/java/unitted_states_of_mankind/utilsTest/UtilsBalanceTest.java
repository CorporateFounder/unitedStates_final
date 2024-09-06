package unitted_states_of_mankind.utilsTest;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.model.SlidingWindowManager;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.*;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;


//TODO высянить почему не списывается правильно долги
@SpringBootTest
public class UtilsBalanceTest {

    @Test
    public void eqaulsSenderCustomer() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Block> blocks = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
        Base base = new Base58();
        List<String> sign = new ArrayList<>();
        for (Block block : blocks) {
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                if (sign.contains(base.encode(dtoTransaction.getSign()))) {
                    System.out.println("wrong transaction");
                } else {
                    sign.add(base.encode(dtoTransaction.getSign()));
                }
            }

        }

    }

    @Test
    public void testActualBalance() throws IOException {
        Map<String, Account> basis = new HashMap<>();
        String account = "oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt";

        double balance = 50;
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":27.84,\"digitalStockBalance\":27.84,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCcZyLbrnsa87hJqaU3OUZQjSE8RMFxwLygASeoSWFVbwIhAMkAUoVO6eA1W0XCNbenWG1QNDhHbDbAemXbihdR6LP1\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"digitalDollar\":278.4,\"digitalStockBalance\":278.4,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCID14260/Ua87OO04+XdkgvQgB47uwiX6lvh6w4OeYmCpAiBYs/0Tr4AKrtuxd12KcGZFbuPinZI3z1yhy1qpsxDhTw==\"}],\"previousHash\":\"b2940121c2044812023a8410960472dca940416842000cb0d02e4cf180001010\",\"minerAddress\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":280000736430912,\"minerRewards\":0.0,\"hashCompexity\":23,\"timestamp\":1722808037000,\"index\":289095,\"hashBlock\":\"2634cb92441012208a0c42c44499008820b0084030d5840450d1200132e46a99\"}");
        List<DtoTransaction> dtoTransactions = block.getDtoTransactions();
        System.out.println("size: " + block.getDtoTransactions().size());
        String resipient = dtoTransactions.get(0).getCustomer();
        String resipient2 = dtoTransactions.get(1).getCustomer();
        dtoTransactions.get(0).setSender("oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt");
        dtoTransactions.get(1).setSender("oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt");

        basis.put(account, new Account(account, BigDecimal.valueOf(balance), BigDecimal.valueOf(balance), BigDecimal.valueOf(balance)));
        basis.put(resipient, new Account(account, BigDecimal.valueOf(balance), BigDecimal.valueOf(balance), BigDecimal.valueOf(balance)));
        basis.put(resipient2, new Account(account, BigDecimal.valueOf(balance), BigDecimal.valueOf(balance), BigDecimal.valueOf(balance)));


        List<DtoTransaction> result = balanceTransaction(dtoTransactions, basis);
        System.out.println("result: " + result.size());
    }

    public List<DtoTransaction> balanceTransaction(List<DtoTransaction> transactions, Map<String, Account> basis) throws IOException {
        List<DtoTransaction> dtoTransactions = new ArrayList<>();
        Map<String, Account> balances = basis;

        for (DtoTransaction transaction : transactions) {

            // Check if both digital dollar and digital stock are below the minimum
            boolean result = false;
            if (balances.containsKey(transaction.getSender())) {
                Account sender = balances.get(transaction.getSender());
                Account customer = balances.get(transaction.getCustomer());
                BigDecimal transactionDigitalDollar = BigDecimal.valueOf(transaction.getDigitalDollar());
                BigDecimal transactionDigitalStock = BigDecimal.valueOf(transaction.getDigitalStockBalance());

                BigDecimal transactionBonusForMiner = BigDecimal.valueOf(transaction.getBonusForMiner());

                if (sender.getDigitalDollarBalance().compareTo(transactionDigitalDollar.add(transactionBonusForMiner)) >= 0) {
                    dtoTransactions.add(transaction);
                    result = true;
                } else if (sender.getDigitalStockBalance().compareTo(transactionDigitalStock.add(transactionBonusForMiner)) >= 0 && transaction.getVoteEnum().equals(VoteEnum.YES)) {
                    dtoTransactions.add(transaction);
                    result = true;
                } else if (sender.getDigitalStockBalance().compareTo(transactionDigitalStock.add(transactionBonusForMiner)) >= 0 && transaction.getVoteEnum().equals(VoteEnum.NO)) {
                    dtoTransactions.add(transaction);
                    result = true;
                } else if (sender.getDigitalDollarBalance().compareTo(transactionDigitalDollar.add(transactionBonusForMiner)) >= 0 && transaction.getVoteEnum().equals(VoteEnum.STAKING)) {
                    dtoTransactions.add(transaction);
                    result = true;
                } else if (sender.getDigitalStakingBalance().compareTo(transactionDigitalDollar.add(transactionBonusForMiner)) >= 0 && transaction.getVoteEnum().equals(VoteEnum.UNSTAKING)) {
                    dtoTransactions.add(transaction);
                    result = true;
                }
                try {
                    if (result == true) {
                        boolean sendtrue = UtilsBalance.sendMoney(
                                sender,
                                customer,
                                BigDecimal.valueOf(transaction.getDigitalDollar()),
                                BigDecimal.valueOf(transaction.getDigitalStockBalance()),
                                BigDecimal.valueOf(transaction.getBonusForMiner()),
                                transaction.getVoteEnum());
                        if (sendtrue) {
                            balances.put(sender.getAccount(), sender);
                            balances.put(customer.getAccount(), customer);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return dtoTransactions;
    }

    @Test
    public void wrongTransaction() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Block> blocks = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
        for (Block block : blocks) {
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                if (dtoTransaction.getVoteEnum().equals(VoteEnum.STAKING)
                        || dtoTransaction.getVoteEnum().equals(VoteEnum.UNSTAKING)) {
                    if (!dtoTransaction.getSender().equals(dtoTransaction.getCustomer())) {
                        System.out.println("dto: " + dtoTransaction);
                        Assert.assertTrue(false);
                    }
                    if (dtoTransaction.getDigitalDollar() < 0 || dtoTransaction.getDigitalStockBalance() < 0
                            || dtoTransaction.getBonusForMiner() < 0) {
                        System.out.println("wrong: dto");
                    }
                }
            }

        }

    }

    @Test
    public void testSignFromFile() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<String> signs = new ArrayList<>();
        List<Block> blocks = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
        Base base = new Base58();
        Set<String> uniqueSigns = new HashSet<>();
        List<String> duplicateSigns = new ArrayList<>();

        for (Block block : blocks) {
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                String encodedSign = base.encode(dtoTransaction.getSign());
                if (!uniqueSigns.add(encodedSign)) {
                    // If the sign is already in the set, it's a duplicate.
                    duplicateSigns.add(encodedSign);
                }
                signs.add(encodedSign);
            }
        }

        // Print out duplicate signatures
        if (!duplicateSigns.isEmpty()) {
            System.out.println("Duplicate signatures found:");
            for (String sign : duplicateSigns) {
                System.out.println(sign);
            }
        } else {
            System.out.println("No duplicate signatures found.");
        }

        // Optionally, you can assert that no duplicates were found
        Assert.assertTrue("Duplicate signatures detected!", duplicateSigns.isEmpty());


    }

    @Test
    public void rollbackBalanceFromFile1() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Map<String, Account> balance = new HashMap<>();
        List<Block> blocks = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
        BigDecimal dollar = BigDecimal.valueOf(20000000);
        BigDecimal stock = BigDecimal.valueOf(20000000);
        BigDecimal staking = BigDecimal.valueOf(20000000);

        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());

        for (Block block : blocks) {
            Account miner = new Account(block.getMinerAddress(), dollar, stock, staking);
            Account founder = new Account(block.getFounderAddress(), dollar, stock, staking);
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                Account sender = new Account(dtoTransaction.getSender(), dollar, stock, staking);
                Account customer = new Account(dtoTransaction.getCustomer(), dollar, stock, staking);
                balance.put(sender.getAccount(), sender);
                balance.put(customer.getAccount(), customer);
            }

            balance.put(miner.getAccount(), miner);
            balance.put(founder.getAccount(), founder);
        }


        List<String> sign = new ArrayList<>();
        System.out.println("index first: " + blocks.get(0).getIndex() + " index last: " + blocks.get(blocks.size() - 1).getIndex());
        for (Block block : blocks) {

            Map<String, Account> cloneBalance = UtilsUse.balancesClone(balance);
            balance = UtilsBalance.calculateBalance(balance, block, sign);
            balance = UtilsBalance.rollbackCalculateBalance(balance, block);

            Map<String, Account> result = UtilsUse.differentAccount(cloneBalance, balance);
            Assert.assertTrue(result.size() == 0);
            if (result.size() > 0) {
                System.out.println("==============================");
                System.out.println("block: " + block.getIndex());
                System.out.println("result: " + result);


                System.out.println("-------------------------");
                System.out.println("block: " + block);
                System.out.println("==============================");

                System.out.println("=========================================");
                for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
                    Account temp = balance.get(accountEntry.getKey());
                    if (temp == null) {
                        System.out.println("balance: null: " + accountEntry.getKey());
                    }
                    System.out.println(temp);
                }

                System.out.println("=========================================");
                for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
                    Account temp = cloneBalance.get(accountEntry.getKey());
                    if (temp == null) {
                        System.out.println("cloneBalance: null: " + accountEntry.getKey());
                    }
                    System.out.println(temp);
                }
            }
        }

        System.out.println("index first: " + blocks.get(0).getIndex() + " index last: " + blocks.get(blocks.size() - 1).getIndex());

    }

    @Test
    public void rollbackBalanceFromFile() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Map<String, Account> balance = new HashMap<>();
        List<Block> blocks = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
        BigDecimal dollar = BigDecimal.valueOf(20000);
        BigDecimal stock = BigDecimal.valueOf(20000);
        BigDecimal staking = BigDecimal.valueOf(20000);
        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());

        for (Block block : blocks) {

            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                Account sender = new Account(dtoTransaction.getSender(), dollar, stock, staking);
                Account customer = new Account(dtoTransaction.getCustomer(), dollar, stock, staking);
                balance.put(sender.getAccount(), sender);
                balance.put(customer.getAccount(), customer);

            }

        }

//        balance.put(test,new Account(test, dollar, stock, staking));
        Map<String, Account> cloneBalance = UtilsUse.balancesClone(balance);

        List<String> sign = new ArrayList<>();
        for (Block block : blocks) {
            balance = UtilsBalance.calculateBalance(balance, block, sign);
        }

        //повторный подсчет
        Map<String, Account> cloneReapete = UtilsUse.balancesClone(balance);

        System.out.println("---------------------------------------------------------------------");
        System.out.println("before");
        System.out.println("---------------------------------------------------------------------");
        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex).reversed()).collect(Collectors.toList());
        for (Block block : blocks) {
            balance = UtilsBalance.rollbackCalculateBalance(balance, block);
        }

        Map<String, Account> result = UtilsUse.differentAccount(cloneBalance, balance);

        sign = new ArrayList<>();
        for (Block block : blocks) {
            balance = UtilsBalance.calculateBalance(balance, block, sign);
        }

        Map<String, Account> result2 = UtilsUse.differentAccount(cloneReapete, balance);

        //true test completed

        System.out.println("result:  " + result.size());
        System.out.println("result2:  " + result2.size());
        System.out.println("result2: " + result2);
        System.out.println("size: balance: " + balance.size());
        System.out.println("size: cloneBalance: " + cloneBalance.size());
        System.out.println("=========================================");
        for (Map.Entry<String, Account> accountEntry : result2.entrySet()) {
            Account temp = balance.get(accountEntry.getKey());
            if (temp == null) {
                System.out.println("balance: null: " + accountEntry.getKey());
            }
            System.out.println(temp);
        }

        System.out.println("=========================================");
        for (Map.Entry<String, Account> accountEntry : result2.entrySet()) {
            Account temp = cloneBalance.get(accountEntry.getKey());
            if (temp == null) {
                System.out.println("cloneBalance: null: " + accountEntry.getKey());
            }
            System.out.println(temp);
        }
        System.out.println("size: balance: " + balance.size());
        System.out.println("size: cloneBalance: " + cloneBalance.size());

        System.out.println("==============================================");
        for (Map.Entry<String, Account> accountEntry : result2.entrySet()) {
            Account temp = cloneReapete.get(accountEntry.getKey());
            if (temp == null) {
                System.out.println("cloneReapete: null: " + accountEntry.getKey());
            }
            System.out.println(temp);
        }
        System.out.println("size: balance: " + balance.size());
        System.out.println("size: cloneBalance: " + cloneBalance.size());
        System.out.println("size: cloneReapete: " + cloneReapete.size());

        System.out.println("==============================================");
        System.out.println("size: result: " + result.size());
        Assert.assertTrue(result.size() == 0);
        Assert.assertTrue(result2.size() == 0);

    }


    @Test
    public void rollbackBalanceFr() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Map<String, Account> balance = new HashMap<>();
        System.out.println("start:");


        List<Block> list = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
        System.out.println("size: " + list.size());
        list = list.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
        BigDecimal dollar = BigDecimal.valueOf(100000);
        BigDecimal stock = BigDecimal.valueOf(100000);
        BigDecimal staking = BigDecimal.valueOf(100000);
        for (Block temp : list) {
            for (DtoTransaction dtoTransaction : temp.getDtoTransactions()) {
                Account sender = new Account(dtoTransaction.getSender(), dollar, stock, staking);
                Account customer = new Account(dtoTransaction.getCustomer(), dollar, stock, staking);
                balance.put(sender.getAccount(), sender);
                balance.put(customer.getAccount(), customer);
            }

        }

        // Replace the HashMap with a LinkedHashMap that has a size limit for the sliding window
        Map<String, Account> clone = UtilsUse.balancesClone(balance);
        for (Block temp : list) {

            UtilsBalance.calculateBalance(balance, temp, new ArrayList<>());
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.println("before");

        System.out.println("---------------------------------------------------------------------");

        list = list.stream().sorted(Comparator.comparing(Block::getIndex).reversed()).collect(Collectors.toList());

        Map<String, Account> cloneWindow = UtilsUse.balancesClone(balance);
        for (Block temp : list) {

                UtilsBalance.rollbackCalculateBalance(balance, temp);

        }

        System.out.println("---------------------------------------------------------------------");
        System.out.println("after");
        Map<String, Account> result = UtilsUse.differentAccount(clone, balance);
        Map<String, Account> result2 = UtilsUse.differentAccount(clone, cloneWindow);
        System.out.println("result size: " + result.size());
        System.out.println("result2 size: " + result2.size());
        Long index = list.get(list.size() - 5).getIndex();

        //true test completed
        Assert.assertTrue(result.size() == 0);
        Assert.assertTrue(result.size() == 0);
//        Assert.assertTrue(windows.getWindow(index) != null);
        System.out.println(result);
        System.out.println("=========================================");
        for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
            System.out.println(balance.get(accountEntry.getKey()));
        }

        System.out.println("=========================================");
        for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
            System.out.println(clone.get(accountEntry.getKey()));
        }
    }

    @Test
    public void testRollBackShort() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIC8rnPDb8ltwajdu4nY1BvyJboPn2fc/9uQC1XWom1mxAiEAiq9tDEUDlV+lEngDuJmpCnKHbt5aEbjIkFccktn6P0A=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIFKIFafSxvF0B3cAoAhfX4vtPXmWwGajaGGaHfUL11pWAiEA4thPgp8MtgJ3IwQm2xsA+/JxY5nCus6j38tFwfayBGQ=\"}],\"previousHash\":\"4aa09101b5164c401482da008285070428020011141087600204014132aa0ac2\",\"minerAddress\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12345686317122146,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722857830000,\"index\":289335,\"hashBlock\":\"8aa912e6301406109a489e80064105ccaa040b9005100044222094380e218441\"}\n");
        Block block1 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":27.84,\"digitalStockBalance\":27.84,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDJviFDIUD158qpEBHqMrGNWbovELMNyUS0JGJtaGlTmwIhAPLg5M+VTUCk43D8ivjXchG4ANfe3upwy95c2cRL73Ti\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"digitalDollar\":278.4,\"digitalStockBalance\":278.4,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIDXRQON/BI4ugxRZoqX34lONq3ebbouqRfEUNche9xW4AiAcjEcsR+ouwFMaw+DGA5kObDnQYprFhGKIPaV4iZgD4Q==\"}],\"previousHash\":\"8aa912e6301406109a489e80064105ccaa040b9005100044222094380e218441\",\"minerAddress\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":20000374584817,\"minerRewards\":0.0,\"hashCompexity\":23,\"timestamp\":1722858134000,\"index\":289336,\"hashBlock\":\"8780088c200953628469005799200900381ce680390e05060000480170f226c2\"}\n");
        Block block2 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIGwz9GjU5VSwGlZSpP5SiacI2bl/YVNija4MjDpp/ezNAiEAoZzhAZMhku/yCDu5x1l04qLCjxwZ60RzaNGnofX3YdU=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCcyNZSFJMLKC1zGf9QBeNQKDRR/GE8jHviSz69/zxMggIgUtzeY0oWOCs+jnzkBi65ZpWVWV2P6eaflLfAc3p+1ow=\"}],\"previousHash\":\"8780088c200953628469005799200900381ce680390e05060000480170f226c2\",\"minerAddress\":\"nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12345706449312701,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722858326000,\"index\":289337,\"hashBlock\":\"409645836d020b805a500604100ab711b24198320076484440308804a481c000\"}\n");
        Block block3 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICvD1Siol5v/41W8WZmfshIhPiZByDRDi9lBR3CbsHx1AiEAvs5UEWJ4ql2VuA8HlAJKe16NJ1kQYeoTXIaRl34eqkc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD+RGY4EFFYAseK29n4e9nuFOCgk84dJNWrPTS6D4C0SwIhAONs065JgjFsQ/p5wuvDstb31DsQBgx4bDxrsgfgAoQr\"}],\"previousHash\":\"409645836d020b805a500604100ab711b24198320076484440308804a481c000\",\"minerAddress\":\"oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12345693289400101,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722858875000,\"index\":289338,\"hashBlock\":\"000c1c9034c011ca8224768040018050acc8d5115e0e620d011000c430883168\"}\n");
        List<Block> blocks = new ArrayList<>();
        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        Block prev = block;
        Map<String, Account> balance = new HashMap<>();
        balance.put("++++", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("oi4M59ViufpL1QnQK1XQ8LyTEraLZrCZ2VKHvwUTFCxt", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("nugMm4zpoQdfgBGuXrk57EPFAe4EPSg1MQ44YTLdjKfg", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        balance.put("2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL", new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        DataShortBlockchainInformation dataShortBlockchainInformation = new DataShortBlockchainInformation();
        dataShortBlockchainInformation.setValidation(true);
        dataShortBlockchainInformation.setSize(10);
        dataShortBlockchainInformation.setBigRandomNumber(10);
        dataShortBlockchainInformation.setStaking(BigDecimal.valueOf(10.0000000001));
        dataShortBlockchainInformation.setTransactions(10);
        dataShortBlockchainInformation.setHashCount(10);
        DataShortBlockchainInformation original = dataShortBlockchainInformation.clone();
        System.out.println("dataL " + dataShortBlockchainInformation);
        List<String> sign = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            List<Block> temp = new ArrayList<>();
            temp.add(blocks.get(i));
            dataShortBlockchainInformation = Blockchain.shortCheck(prev, temp, dataShortBlockchainInformation, new ArrayList<>(), balance, sign);
            prev = blocks.get(i);
        }


        System.out.println("dataL " + dataShortBlockchainInformation);
        for (int i = 0; i < blocks.size(); i++) {
            List<Block> temp = new ArrayList<>();
            temp.add(blocks.get(i));
            dataShortBlockchainInformation = Blockchain.rollBackShortCheck(temp, dataShortBlockchainInformation, balance, sign);
            prev = blocks.get(i);
        }
        System.out.println("dataL " + dataShortBlockchainInformation);

        //true test completed
        Assert.assertTrue(original.equals(dataShortBlockchainInformation));
        System.out.println("equals: " + original.equals(dataShortBlockchainInformation));
    }


    //TODO исправить sendTest установив баланс отправилтеля
    @Test
    public void SendTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException, CloneNotSupportedException {


        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":14.5,\"digitalStockBalance\":14.5,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCkBX4IBVOq/2mPsOTWIn2N8TPfS5FItHqrEqhuU/YgHAIgKmdMlLXLM4sfnWh0aO0ezTZeInUdc/dh7SDF1qOKPAI=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"digitalDollar\":145.0,\"digitalStockBalance\":145.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":3.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD8iQWqgDwsxDV3ew0yZeUDS+xnI2iNjuNHak4MccU1WgIhAOP1cKFcZfrJ6Oek4UH6cX9FcIBYaA3OwAItctWny1k7\"}],\"previousHash\":\"904045a20888b4581d08c9587242b740cb12603802c440104f10063236603202\",\"minerAddress\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":947262,\"minerRewards\":0.0,\"hashCompexity\":16,\"timestamp\":1704736749000,\"index\":167479,\"hashBlock\":\"185ac49d8c2320b030d4ac8454000b2a21423a00816a042c5403470032439188\"}");
        DtoTransaction transaction = block.getDtoTransactions().get(1);
        transaction.setBonusForMiner(3);
        transaction.setSender("2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG");
        transaction.setCustomer("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43");

        long index = 284984 + 5000;
        System.out.println("index: " + index);
        Account sender = new Account(transaction.getSender(), BigDecimal.valueOf(1000.00000000000001), BigDecimal.valueOf(1000.00000000001), BigDecimal.ZERO);
        Account miner = new Account(block.getMinerAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        Account recipient = new Account(transaction.getCustomer(), BigDecimal.valueOf(1000.00000000000001), BigDecimal.valueOf(1000.00000000001), BigDecimal.valueOf(0.0));
        Account testSender = sender.clone();
        Account testRecipietnt = recipient.clone();
        Account testMiner = miner.clone();
        System.out.println("sender before: " + sender);
        System.out.println("recipient before: " + recipient);
        System.out.println("minier before: " + miner);
        UtilsBalance.sendMoney(sender, recipient, BigDecimal.valueOf(transaction.getDigitalDollar()), BigDecimal.valueOf(transaction.getDigitalStockBalance()), BigDecimal.valueOf(transaction.getBonusForMiner()), VoteEnum.YES);
        System.out.println("sender after: " + sender);
        System.out.println("recipient after: " + recipient);
        System.out.println("minier after: " + miner);
        System.out.println("bonus for miner: " + block.getDtoTransactions().get(1).getBonusForMiner());

        System.out.println("roll back");
        UtilsBalance.rollBackSendMoney(sender, recipient, BigDecimal.valueOf(transaction.getDigitalDollar()), BigDecimal.valueOf(transaction.getDigitalStockBalance()), BigDecimal.valueOf(transaction.getBonusForMiner()), VoteEnum.YES);
        System.out.println("sender after: " + sender);
        System.out.println("recipient after: " + recipient);
        System.out.println("minier after: " + miner);


        //true test completed
        Assert.assertTrue(testMiner.equals(miner) && testSender.equals(sender) && testRecipietnt.equals(recipient));
        System.out.println("equals: " + (testMiner.equals(miner) && testSender.equals(sender) && testRecipietnt.equals(recipient)));
    }

    @Test
    public void testBalanceDiscrepancy() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {

        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":0.00000001,\"digitalStockBalance\":0.00000001,\"laws\":{\"packetLawName\":null,\"laws\":null},\"bonusForMiner\":0.00000001,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCkBX4IBVOq/2mPsOTWIn2N8TPfS5FItHqrEqhuU/YgHAIgKmdMlLXLM4sfnWh0aO0ezTZeInUdc/dh7SDF1qOKPAI=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"digitalDollar\":0.00000001,\"digitalStockBalance\":0.00000001,\"laws\":{\"packetLawName\":null,\"laws\":null},\"bonusForMiner\":0.00000001,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD8iQWqgDwsxDV3ew0yZeUDS+xnI2iNjuNHak4MccU1WgIhAOP1cKFcZfrJ6Oek4UH6cX9FcIBYaA3OwAItctWny1k7\"}],\"previousHash\":\"904045a20888b4581d08c9587242b740cb12603802c440104f10063236603202\",\"minerAddress\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":947262,\"minerRewards\":0.0,\"hashCompexity\":16,\"timestamp\":1704736749000,\"index\":167479,\"hashBlock\":\"185ac49d8c2320b030d4ac8454000b2a21423a00816a042c5403470032439188\"}");

        DtoTransaction transaction = block.getDtoTransactions().get(1);

        // Setup initial accounts
        Account sender = new Account(transaction.getSender(), BigDecimal.valueOf(1000.00000001), BigDecimal.valueOf(1000.00000001), BigDecimal.ZERO);
        Account recipient = new Account(transaction.getCustomer(), BigDecimal.valueOf(1000.00000001), BigDecimal.valueOf(1000.00000001), BigDecimal.ZERO);
        Account miner = new Account(block.getMinerAddress(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        // Clone initial states for comparison after rollback
        Account originalSender = sender.clone();
        Account originalRecipient = recipient.clone();
        Account originalMiner = miner.clone();

        // Perform the money transfer
        UtilsBalance.sendMoney(sender, recipient, BigDecimal.valueOf(transaction.getDigitalDollar()), BigDecimal.valueOf(transaction.getDigitalStockBalance()), BigDecimal.valueOf(transaction.getBonusForMiner()), VoteEnum.YES);

        // Rollback the transaction
        UtilsBalance.rollBackSendMoney(sender, recipient, BigDecimal.valueOf(transaction.getDigitalDollar()), BigDecimal.valueOf(transaction.getDigitalStockBalance()), BigDecimal.valueOf(transaction.getBonusForMiner()), VoteEnum.YES);

        // Compare the states after rollback
        boolean senderEqual = originalSender.equals(sender);
        boolean recipientEqual = originalRecipient.equals(recipient);
        boolean minerEqual = originalMiner.equals(miner);

        System.out.println("Sender before and after rollback match: " + senderEqual);
        System.out.println("Recipient before and after rollback match: " + recipientEqual);
        System.out.println("Miner before and after rollback match: " + minerEqual);

        // Assert that balances should match the original state
        Assert.assertTrue("Discrepancy detected in sender balance!", senderEqual);
        Assert.assertTrue("Discrepancy detected in recipient balance!", recipientEqual);
        Assert.assertTrue("Discrepancy detected in miner balance!", minerEqual);
    }

    @Test
    public void testMultipleTransactionsAndRollbacks() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        BigDecimal digitalDollar = new BigDecimal(Double.toString(1000000000.00000000));
        Account sender = new Account("sender", digitalDollar, digitalDollar, BigDecimal.ZERO);
        Account recipient = new Account("recipient", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        BigDecimal transactionAmount = BigDecimal.valueOf(0.10001111);
        BigDecimal minerRewards = BigDecimal.valueOf(0.00000111);
        int round = 1000000;
        // Perform multiple transactions
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.sendMoney(sender, recipient, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
            Assert.assertTrue("Transaction failed", success);
        }

        // Store intermediate balances
        BigDecimal senderBalanceAfterSend = sender.getDigitalDollarBalance();
        BigDecimal recipientBalanceAfterSend = recipient.getDigitalDollarBalance();

        // Perform rollbacks
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.rollBackSendMoney(sender, recipient, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
            Assert.assertTrue("Rollback failed", success);
        }

        System.out.println("sender: " + sender.getDigitalDollarBalance());
        System.out.println("digital: " + digitalDollar);
        System.out.println("recipient: " + recipient);
        // Check final balances using compareTo to ignore scale differences
        Assert.assertTrue("Sender balance incorrect after rollbacks",
                digitalDollar.compareTo(sender.getDigitalDollarBalance()) == 0);
        Assert.assertTrue("Recipient balance incorrect after rollbacks",
                BigDecimal.ZERO.compareTo(recipient.getDigitalDollarBalance()) == 0);

        // Print intermediate and final balances for analysis
        System.out.println("Sender balance after sends: " + senderBalanceAfterSend);
        System.out.println("Recipient balance after sends: " + recipientBalanceAfterSend);
        System.out.println("Sender final balance: " + sender.getDigitalDollarBalance());
        System.out.println("Recipient final balance: " + recipient.getDigitalDollarBalance());
    }

    @Test
    public void testMultipleTransactionsAndRollbacksStaking() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        BigDecimal digitalDollar = new BigDecimal(Double.toString(1000000000.00000000));
        Account sender = new Account("sender", digitalDollar, digitalDollar, digitalDollar);
        Account recipient = new Account("recipient", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        BigDecimal transactionAmount = BigDecimal.valueOf(0.10001111);
        BigDecimal minerRewards = BigDecimal.valueOf(0.00000111);
        int round = 1000000;
        // Perform multiple transactions
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.sendMoney(sender, sender, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
//            Assert.assertTrue("Transaction failed", success);
        }

        // Store intermediate balances
        BigDecimal senderBalanceAfterSend = sender.getDigitalDollarBalance();
        BigDecimal recipientBalanceAfterSend = sender.getDigitalDollarBalance();

        // Perform rollbacks
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.rollBackSendMoney(sender, sender, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
//            Assert.assertTrue("Rollback failed", success);
        }

        System.out.println("sender: " + sender);
        System.out.println("digital: " + digitalDollar);

        // Check final balances using compareTo to ignore scale differences
        Assert.assertTrue("Sender balance incorrect after rollbacks",
                digitalDollar.compareTo(sender.getDigitalDollarBalance()) == 0);
        Assert.assertTrue("Sender staking balance incorrect after rollbacks",
                digitalDollar.compareTo(sender.getDigitalStakingBalance()) == 0);

        // Print intermediate and final balances for analysis
        System.out.println("Sender balance after sends: " + senderBalanceAfterSend);
        System.out.println("Recipient balance after sends: " + recipientBalanceAfterSend);
        System.out.println("Sender final balance: " + sender.getDigitalDollarBalance());
        System.out.println("Recipient final balance: " + recipient.getDigitalDollarBalance());

    }


    @Test
    public void testMultipleTransactionsAndRollbacksUnstaking() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        BigDecimal digitalDollar = new BigDecimal(Double.toString(1000000000.00000000));
        Account sender = new Account("sender", digitalDollar, digitalDollar, digitalDollar);
        Account recipient = new Account("recipient", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        BigDecimal transactionAmount = BigDecimal.valueOf(0.10001111);
        BigDecimal minerRewards = BigDecimal.valueOf(0.00000111);
        int round = 1000000;
        // Perform multiple transactions
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.sendMoney(sender, sender, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
//            Assert.assertTrue("Transaction failed", success);
        }

        // Store intermediate balances
        BigDecimal senderBalanceAfterSend = sender.getDigitalDollarBalance();
        BigDecimal recipientBalanceAfterSend = sender.getDigitalDollarBalance();

        // Perform rollbacks
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.rollBackSendMoney(sender, sender, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
//            Assert.assertTrue("Rollback failed", success);
        }

        System.out.println("sender: " + sender);
        System.out.println("digital: " + digitalDollar);

        // Check final balances using compareTo to ignore scale differences
        Assert.assertTrue("Sender balance incorrect after rollbacks",
                digitalDollar.compareTo(sender.getDigitalDollarBalance()) == 0);
        Assert.assertTrue("Sender staking balance incorrect after rollbacks",
                digitalDollar.compareTo(sender.getDigitalStakingBalance()) == 0);

        // Print intermediate and final balances for analysis
        System.out.println("Sender balance after sends: " + senderBalanceAfterSend);
        System.out.println("Recipient balance after sends: " + recipientBalanceAfterSend);
        System.out.println("Sender final balance: " + sender.getDigitalDollarBalance());
        System.out.println("Recipient final balance: " + recipient.getDigitalDollarBalance());

    }

    @Test
    public void testMultipleTransactionsAndRollbacksMining() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        BigDecimal digitalDollar = new BigDecimal(Double.toString(0));
        Account sender = new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", digitalDollar, digitalDollar, digitalDollar);
        Account recipient = new Account("recipient", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        BigDecimal transactionAmount = BigDecimal.valueOf(0.10001111);
        BigDecimal minerRewards = BigDecimal.valueOf(0.00000111);
        int round = 1000000;
        // Perform multiple transactions
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.sendMoney(sender, recipient, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
//            Assert.assertTrue("Transaction failed", success);
        }

        // Store intermediate balances
        BigDecimal senderBalanceAfterSend = sender.getDigitalDollarBalance();
        BigDecimal recipientBalanceAfterSend = recipient.getDigitalDollarBalance();

        // Perform rollbacks
        for (int i = 0; i < round; i++) {
            boolean success = UtilsBalance.rollBackSendMoney(sender, recipient, transactionAmount, transactionAmount, minerRewards, VoteEnum.YES);
//            Assert.assertTrue("Rollback failed", success);
        }

        System.out.println("sender: " + sender);
        System.out.println("recipient: " + recipient);
        System.out.println("digital: " + digitalDollar);

        // Check final balances using compareTo to ignore scale differences
        Assert.assertTrue("Sender balance incorrect after rollbacks",
                digitalDollar.compareTo(sender.getDigitalDollarBalance()) == 0);
        Assert.assertTrue("Sender staking balance incorrect after rollbacks",
                BigDecimal.ZERO.compareTo(recipient.getDigitalStakingBalance()) == 0);

        // Print intermediate and final balances for analysis
        System.out.println("Sender balance after sends: " + senderBalanceAfterSend);
        System.out.println("Recipient balance after sends: " + recipientBalanceAfterSend);
        System.out.println("Sender final balance: " + sender.getDigitalDollarBalance());
        System.out.println("Recipient final balance: " + recipient.getDigitalDollarBalance());

    }


}


