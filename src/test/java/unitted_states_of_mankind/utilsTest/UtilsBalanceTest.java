package unitted_states_of_mankind.utilsTest;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
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

//    @Test
//    public void testSendFromFile() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
//        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"24ED8T8aJ9FXGP4fG7ru72bwWYVK5d4iT3LAknponv91P\",\"customer\":\"jcqrb3nW1kYLAEYcuzfKFrCeoX2WCTnXq4sH2Rr3fQrE\",\"digitalDollar\":2.12121212,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCVKkJvDTNbLrY1kO7ifnMYtTE8flcQtPuvDWF8dbhfXAIhAPcPfc6p9ndTFKxabc8VuilJCqFJuWF0yEY0OyDI8O4o\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":37.12,\"digitalStockBalance\":37.12,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDhsV/G30Cf2m1G65xwdGzZHPvvC6TOR7cBHc5pJmwdjgIhALjhQ+PGzzuXcjRAMJB6hC90AWrbVcCmQykadicnjfN7\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"pRUbAvZouvUMCBrvjJK8yLWysUg4Fui6xsvuNfuVKH37\",\"digitalDollar\":371.2,\"digitalStockBalance\":371.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQC/q0uAHVtvb8XBUQNZl9gGGMQ7nQHftCMFkpF2nvQPoAIhALjYSBx2BK9IV/3xoC60QjlLpPaHLaS9hiBEVLHWtflM\"}],\"previousHash\":\"1801831c443a8260509a62534b721f2404102c501021190c150b004009215040\",\"minerAddress\":\"pRUbAvZouvUMCBrvjJK8yLWysUg4Fui6xsvuNfuVKH37\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":66666702298206244,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722863748000,\"index\":289360,\"hashBlock\":\"b834080e46398110ca0101c5001cc0122200bd682c420e00534252a004311c40\"}");
//        for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
//            System.out.println("sender: " + dtoTransaction.getSender());
//            System.out.println("custome: " + dtoTransaction.getCustomer());
//        }
//    }
//
//    @Test
//    public void rollbackBalanceFromFile1() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
//        Map<String, Account> balance = new HashMap<>();
//        List<Block> blocks = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
//        BigDecimal dollar = BigDecimal.valueOf(20000000);
//        BigDecimal stock = BigDecimal.valueOf(20000000);
//        BigDecimal staking = BigDecimal.valueOf(20000000);
//
//        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
//
//        for (Block block: blocks ) {
//            Account miner = new Account(block.getMinerAddress(), dollar, stock, staking);
//            Account founder = new Account(block.getFounderAddress(), dollar, stock, staking);
//            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
//                Account sender = new Account(dtoTransaction.getSender(), dollar, stock, staking);
//                Account customer = new Account(dtoTransaction.getCustomer(), dollar, stock, staking);
//                balance.put(sender.getAccount(), sender);
//                balance.put(customer.getAccount(), customer);
//            }
//
//            balance.put(miner.getAccount(), miner);
//            balance.put(founder.getAccount(), founder);
//        }
//
//
//        List<String> sign = new ArrayList<>();
//        System.out.println("index first: " + blocks.get(0).getIndex() + " index last: " + blocks.get(blocks.size()-1).getIndex());
//        for (Block block : blocks) {
//
//            Map<String, Account> cloneBalance = UtilsUse.balancesClone(balance);
//            balance =  UtilsBalance.calculateBalance(balance, block, sign);
//            balance = UtilsBalance.rollbackCalculateBalance(balance, block );
//
//            Map<String, Account> result = UtilsUse.differentAccount(cloneBalance, balance);
//            if(result.size() > 0){
//                System.out.println("==============================");
//                System.out.println("block: " + block.getIndex());
//                System.out.println("result: " + result);
//
//
//                System.out.println("-------------------------");
//                System.out.println("block: " + block);
//                System.out.println("==============================");
//
//                System.out.println("=========================================");
//                for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
//                    Account temp = balance.get(accountEntry.getKey());
//                    if(temp == null){
//                        System.out.println("balance: null: " + accountEntry.getKey());
//                    }
//                    System.out.println(temp);
//                }
//
//                System.out.println("=========================================");
//                for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
//                    Account temp = cloneBalance.get(accountEntry.getKey());
//                    if(temp == null){
//                        System.out.println("cloneBalance: null: " + accountEntry.getKey());
//                    }
//                    System.out.println(temp);
//                }
//            }
//        }
//
//        System.out.println("index first: " + blocks.get(0).getIndex() + " index last: " + blocks.get(blocks.size()-1).getIndex());
//
//    }
//    @Test
//    public void rollbackBalanceFromFile() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
//        Map<String, Account> balance = new HashMap<>();
//         List<Block> blocks = UtilsBlock.readLineObject("C:\\strategy3\\blockchain\\");
//         BigDecimal dollar = BigDecimal.valueOf(20000000);
//         BigDecimal stock = BigDecimal.valueOf(20000000);
//         BigDecimal staking = BigDecimal.valueOf(20000000);
//        String test = "2A5gxBWfuYzaTM9f2FhVBFYDPRYY4N1PZSc2v8AT8AE37";
//        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
//
//        for (Block block: blocks ) {
//
//            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
//                Account sender = new Account(dtoTransaction.getSender(), dollar, stock, staking);
//                Account customer = new Account(dtoTransaction.getCustomer(), dollar, stock, staking);
//                balance.put(sender.getAccount(), sender);
//                balance.put(customer.getAccount(), customer);
//                if(sender.equals(test) || customer.equals(test)){
//                    System.out.println("index: " + block.getIndex());
//                    System.out.println("dto: " + dtoTransaction);
//                }
//
//            }
//
//        }
//
////        balance.put(test,new Account(test, dollar, stock, staking));
//        Map<String, Account> cloneBalance = UtilsUse.balancesClone(balance);
//
//        for (Block block : blocks) {
//            List<String> sign = new ArrayList<>();
//          balance =  UtilsBalance.calculateBalance(balance, block, sign);
//        }
//
//        System.out.println("---------------------------------------------------------------------");
//        System.out.println("before");
//        System.out.println("---------------------------------------------------------------------");
//        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex).reversed()).collect(Collectors.toList());
//        for (Block block : blocks) {
//            balance = UtilsBalance.rollbackCalculateBalance(balance, block );
//        }
//        Map<String, Account> result = UtilsUse.differentAccount(cloneBalance, balance);
//
//        System.out.println("size: balance: " + balance.size());
//        System.out.println("size: cloneBalance: " + cloneBalance.size());
//        System.out.println("=========================================");
//        for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
//            Account temp = balance.get(accountEntry.getKey());
//            if(temp == null){
//                System.out.println("balance: null: " + accountEntry.getKey());
//            }
//            System.out.println(temp);
//        }
//
//        System.out.println("=========================================");
//        for (Map.Entry<String, Account> accountEntry : result.entrySet()) {
//            Account temp = cloneBalance.get(accountEntry.getKey());
//            if(temp == null){
//                System.out.println("cloneBalance: null: " + accountEntry.getKey());
//            }
//            System.out.println(temp);
//        }
//        System.out.println("size: balance: " + balance.size());
//        System.out.println("size: cloneBalance: " + cloneBalance.size());
//
//
//    }

    @Test
    public void rollbackBalance() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Map<String, Account> balance = new HashMap<>();
         System.out.println("start:");



        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":27.84,\"digitalStockBalance\":27.84,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCcZyLbrnsa87hJqaU3OUZQjSE8RMFxwLygASeoSWFVbwIhAMkAUoVO6eA1W0XCNbenWG1QNDhHbDbAemXbihdR6LP1\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"digitalDollar\":278.4,\"digitalStockBalance\":278.4,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCID14260/Ua87OO04+XdkgvQgB47uwiX6lvh6w4OeYmCpAiBYs/0Tr4AKrtuxd12KcGZFbuPinZI3z1yhy1qpsxDhTw==\"}],\"previousHash\":\"b2940121c2044812023a8410960472dca940416842000cb0d02e4cf180001010\",\"minerAddress\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":280000736430912,\"minerRewards\":0.0,\"hashCompexity\":23,\"timestamp\":1722808037000,\"index\":289095,\"hashBlock\":\"2634cb92441012208a0c42c44499008820b0084030d5840450d1200132e46a99\"}");
        Block block1 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIAglYv20BF/UOMrBYRzorJ6r/WVyxo0Nv+4IgYNfechUAiAsObVSgbuE2JfhxHYfntmPdhELCIgm7pC3QFdvTEkm3Q==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2AGJkhkndQ5xpu2WNbHUvkqMFWzH8T4oM8BBDv4Z1atBG\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIGH2iaoX091VvkZILL1fjkCLpHUSZLO863p+YKQt6RZ9AiEA/kaxW3J8vKUv2A+Z/pZOc0dTvbrL68dDau+bxrdq3Ng=\"}],\"previousHash\":\"580d3002b8360c64079c4b4010014238c18128400a0234807121101415f28402\",\"minerAddress\":\"2AGJkhkndQ5xpu2WNbHUvkqMFWzH8T4oM8BBDv4Z1atBG\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":66666686231897366,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722807915000,\"index\":289094,\"hashBlock\":\"b2940121c2044812023a8410960472dca940416842000cb0d02e4cf180001010\"}");
        Block block2 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDpMs8D5bVwdNc06FBUIMiQZipPlKpKyqm+EZDKjaDeagIhAJ7Q04q3Is/Mww47GHOzXq5q8tPcx2kRqYqhZIOz4gbb\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"dk6rTxZvBg3viTdLKJxYoJLY9vphi2Y9jyktRd957Brr\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCjuda8qAhoixBN4uCtXQsyJpy/+J7UstFVgTqTURlNhQIgbJjiMWYumqooI5qwkiNehgXPkH5DDDpsYgI5P4eHKmQ=\"}],\"previousHash\":\"1c290d0358040032000100030d05134d3406c58042b355036001e39ce8808440\",\"minerAddress\":\"dk6rTxZvBg3viTdLKJxYoJLY9vphi2Y9jyktRd957Brr\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":66666709669985331,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722807669000,\"index\":289093,\"hashBlock\":\"580d3002b8360c64079c4b4010014238c18128400a0234807121101415f28402\"}");
        Block block3 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":28.42,\"digitalStockBalance\":28.42,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIGu7WS/ESw2n+vm/gnonxAwKA6ZkYlMrpeQHwAOJHvcSAiEAlQkzyr3ZIl4now4z1nJ3a/d/0H5d294/E/yfzk83Yqk=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2AGJkhkndQ5xpu2WNbHUvkqMFWzH8T4oM8BBDv4Z1atBG\",\"digitalDollar\":284.2,\"digitalStockBalance\":284.2,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIHceH2nPVLQDexy3WFXkhRArf0BpbF8DvWaV7ei4u0u/AiBA5kTj/BodFpaxn25Gfmym6fH3MU76OuvZBlqYGNjyWA==\"}],\"previousHash\":\"149011107915000e15005200218c0c04c746846880232110ad3d4c0905812709\",\"minerAddress\":\"2AGJkhkndQ5xpu2WNbHUvkqMFWzH8T4oM8BBDv4Z1atBG\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":66666723790448917,\"minerRewards\":0.0,\"hashCompexity\":24,\"timestamp\":1722807516000,\"index\":289092,\"hashBlock\":\"1c290d0358040032000100030d05134d3406c58042b355036001e39ce8808440\"}");
        Block block4 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"qDWgdtEBzbSGs7AdUVLYEiBP5Bs7nb3m3L8GaBGgbSEP\",\"customer\":\"qDWgdtEBzbSGs7AdUVLYEiBP5Bs7nb3m3L8GaBGgbSEP\",\"digitalDollar\":20.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"STAKING\",\"sign\":\"MEUCIQD7Ip1+zcWX/M9mQ4ZZ1fPJJs+hYDrf0+Mgis4iutGnCAIgMHJNSubTpEqZfQY+ItLEqgIWgM99dDbYmDKiRMVV9cw=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":36.54,\"digitalStockBalance\":36.54,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIHmSqX84NymL9PBWGxEU6xoqRdVZSvyBkbUsAGGauYDfAiAwA8OyCzGAL8bZBaxBtFEJH+XuRY9Fr/RGay4snLb1kQ==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"digitalDollar\":365.4,\"digitalStockBalance\":365.4,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDlKDBERZvJtlfOCGh/bQDpZKq6zJnSd0RraGKwCdKpZQIgHl6KVqIeyEfqGIjovBbl1kLZ2YEcFR7VmQvTYcchqSU=\"}],\"previousHash\":\"0bc0600403e87340502d218246051c1120030310228478a0992c019100b76040\",\"minerAddress\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":40000239573575,\"minerRewards\":0.0,\"hashCompexity\":23,\"timestamp\":1722807285000,\"index\":289091,\"hashBlock\":\"149011107915000e15005200218c0c04c746846880232110ad3d4c0905812709\"}");
        List<Block> list = new ArrayList<>();
        list.add(block4);
        list.add(block3);
        list.add(block2);
        list.add(block1);
        list.add(block);
        BigDecimal dollar = BigDecimal.valueOf(10);
        BigDecimal stock = BigDecimal.valueOf(10);
        BigDecimal staking = BigDecimal.valueOf(10);
        for (Block temp : list) {
            for (DtoTransaction dtoTransaction : temp.getDtoTransactions()) {
                Account sender = new Account(dtoTransaction.getSender(), dollar, stock, staking);
                Account customer = new Account(dtoTransaction.getCustomer(), dollar, stock, staking);
                balance.put(sender.getAccount(), sender);
                balance.put(customer.getAccount(), customer);
            }

        }

        Map<String, Account> clone = UtilsUse.balancesClone(balance);

        UtilsBalance.calculateBalance(balance, block4, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block3, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block2, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block1, new ArrayList<>());
        UtilsBalance.calculateBalance(balance, block, new ArrayList<>());
        System.out.println("---------------------------------------------------------------------");
        System.out.println("before");

        System.out.println("---------------------------------------------------------------------");
        UtilsBalance.rollbackCalculateBalance(balance, block );
        UtilsBalance.rollbackCalculateBalance(balance, block1);
        UtilsBalance.rollbackCalculateBalance(balance, block2);
        UtilsBalance.rollbackCalculateBalance(balance, block3);
        UtilsBalance.rollbackCalculateBalance(balance, block4);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("after");
       Map<String, Account> result = UtilsUse.differentAccount(clone, balance);
        System.out.println("result size: " + result.size());
//        Assert.assertTrue(result.size() == 0);

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

        Assert.assertTrue(testMiner.equals(miner) && testSender.equals(sender) && testRecipietnt.equals(recipient));
        System.out.println("equals: " + (testMiner.equals(miner) && testSender.equals(sender) && testRecipietnt.equals(recipient)));
    }


}


