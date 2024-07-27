package unitted_states_of_mankind;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.DtoTransaction.MerkleTree;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.exception.NotValidTransactionException;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.HostEndDataShortB;
import International_Trade_Union.model.LiteVersionWiner;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.comparator.HostEndDataShortBComparator;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.spring5.processor.SpringTextareaFieldTagProcessor;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static International_Trade_Union.setings.Seting.*;
import static International_Trade_Union.utils.BlockchainDifficulty.*;
import static International_Trade_Union.utils.UtilsBalance.rollbackCalculateBalance;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class Testing {

    /**  if (this.index > Seting.NEW_ALGO_MINING) {
     MerkleTree merkleTree = new MerkleTree(this.getDtoTransactions());
     String hash = merkleTree.getRoot() + this.previousHash + this.minerAddress + this.founderAddress
     + this.randomNumberProof + this.minerRewards + this.hashCompexity + this.timestamp +
     this.index;
     return UtilsUse.sha256hash(hash);
     } else {
     return UtilsUse.sha256hash(jsonString());

     }*/

    @Test
    public void hash() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":15.08,\"digitalStockBalance\":15.08,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIAY4A4l9zf3rXYfBgiC03w/hBHK12NeYiCLmKdAixpetAiA2vgNu7ISp2pUXBV1CzpmaXVjWtnC1adP3RX1CLk83jQ==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"BUDGET\",\"digitalDollar\":150.8,\"digitalStockBalance\":150.8,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQC1n/T7dfrqiZeCwIPm7SFFkinuLGwb8X0mDtd3czg6vQIgW80E8Y2XGV4A+iIrnLpTQUvcZL/Y44R+5X8G6SVXzpU=\"}],\"previousHash\":\"8c80ca00407d817730148cc23231905db0214c2844a41944051910ef99955ed2\",\"minerAddress\":\"BUDGET\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":7205759403799190,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1721807561000,\"index\":284495,\"hashBlock\":\"08801a989a5d21028802c4a8ce841532b706482468b4810520977d59030842b3\"}\n");
        System.out.println(block.getHashBlock().equals(block.hashForTransaction()));
        System.out.println("index: " + block.getIndex());
        System.out.println("hash1: " + block.getHashBlock());
        System.out.println("hash2: " + block.hashForTransaction());
        String firstPart = UtilsUse.firstPartHash(block);
        String secondPart = UtilsUse.secondPartHash(block);
        String hash3 = UtilsUse.finalHash(firstPart, secondPart, block.getRandomNumberProof());
        String hash4 = UtilsUse.hashMining(block, block.getRandomNumberProof());
        System.out.println("hash3: " + hash3);
        System.out.println("hash4: " + hash4);
        DtoTransaction dtoTransaction = block.getDtoTransactions().get(0);
        String json = UtilsJson.objToStringJson(dtoTransaction);
        System.out.println("json: " + json);

    }

    @Test
    public  void calculateScore(){

        double sumBalance = 1;
        double count = 6;
        int diff = 18;
        int result = 4;
        double transactionSumPoints = UtilsUse.calculateScore(sumBalance / count, 1);
        double transactionCountPoints = count * 0.5;
        long score = UtilsUse.calculateScore(4400000, 1);

        double maxTransactionSumPoints = (diff - 17 + score) * 0.6;
        if (transactionSumPoints > maxTransactionSumPoints) {
            transactionSumPoints = maxTransactionSumPoints;
        }

        double sum = transactionCountPoints + transactionSumPoints;
        System.out.println("score: " + score);
        result = (int) ( (result + (diff * 30)) + score + sum);
        System.out.println("result: " + result);
    }

    @Test
    public void test() throws JsonProcessingException {
        Block previusblock = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIGJgO67IRfp9WRBqfyO32iwIma2rIxXUa0Ma96FvcILuAiEAx/z75ESJ6k8VZ5f/R60sfViBxKOeQB/KQb6c1HMjFfg=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICKmTeszZPOMuH7uEXTeN6RyfK8siKe7Y8v+pWao5jwwAiEAt8vUpDbUI6uYpeD1Rcuud0n8ji2hkRnkaOE9cEp601M=\"}],\"previousHash\":\"0c0004662e9240e6199c09640a04a8801830b22760a40a841005489458033512\",\"minerAddress\":\"28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":2702159777149640,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718494423000,\"index\":268767,\"hashBlock\":\"901c41d0442d443530581010888680d049018174505004345041d2c92c61028a\"}");
        Block thisBlock = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.68,\"digitalStockBalance\":26.68,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCgvClMkSz4HdvyMbVLrYKu1QDfA+g2ocl2xif57jxkbQIhAN02wvGyJzz8YLpYvDY8neK73Fa1kPdY1ZkLViVilgoN\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"zraD2LuUah83KXTtYP33pwbUuS62dmp21CxPYXDNGWqW\",\"digitalDollar\":266.8,\"digitalStockBalance\":266.8,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIE2jCPp6U7sXVeoUGUE9lZPjb6NznXByPRVRKT9AK2iWAiEA+1gAOZfwqBs0mJtDrEY9juuadtdqB7kFXXd+4CYpr+A=\"}],\"previousHash\":\"901c41d0442d443530581010888680d049018174505004345041d2c92c61028a\",\"minerAddress\":\"zraD2LuUah83KXTtYP33pwbUuS62dmp21CxPYXDNGWqW\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":14411518811790413,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718494610000,\"index\":268768,\"hashBlock\":\"e880d0625192a181108048c0108c43004180498a6a2a42500248309270f29203\"}\n");
        Account account = new Account("28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az", 266.79999999999995, 266.79999999999995, 0);
        Account account1 = new Account("zraD2LuUah83KXTtYP33pwbUuS62dmp21CxPYXDNGWqW", 266.8, 266.8, 0);

    }
    @Test
    public void testReadHost() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        String url = "E://resources/poolAddress/";
        Set<String> strings = UtilsAllAddresses.readLineObject(url);
        List<HostEndDataShortB> list = new ArrayList<>();

        System.out.println("before: " + strings);
       strings = strings.stream().map(t->t.replaceAll("\"", "")).collect(Collectors.toSet());

        System.out.println("after: " + strings);




        //сортировать здесь.
        // сортировка


    }
    @Test
    public void testHost() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
       String url = "http://localhost:8085/putNode";
       Set<String> strings = BasisController.getNodes();
        System.out.println(strings);



       MyHost myHost = new MyHost("http://localhost:8083/putNode", "server", "key");
       String json = UtilsJson.objToStringJson(myHost);


        UtilUrl.sendPost(json, url );

    }

    @Test
    public void TestHostComparator(){
        List<HostEndDataShortB> hostEndDataShortBS = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int size = random.nextInt(10);
            int hashCount = random.nextInt(10);
            double staking = random.nextDouble(10);
            int trancaction = random.nextInt(10);
            int big = i;
            if(i >=7){
                big = 7;
            }
            DataShortBlockchainInformation temp = new DataShortBlockchainInformation(size, true, hashCount, staking, trancaction, big);
            HostEndDataShortB tempHost = new HostEndDataShortB("host: " + i, temp);
            hostEndDataShortBS.add(tempHost);
        }
        System.out.println("before list: " );
        hostEndDataShortBS.forEach(System.out::println);
        System.out.println("____________________________________");
        Collections.sort(hostEndDataShortBS, new HostEndDataShortBComparator());
        System.out.println("after list: ");
        hostEndDataShortBS.forEach(System.out::println);
    }
    @Test
    public void random() throws JsonProcessingException {
        long score = 0;
        int number = 0;

        for (int i = 0; i < 1000; i++) {
            String json = "{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":14.5,\"digitalStockBalance\":14.5,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCICJUELZJqVWgIOxaPc8gOJPf6Iq9WDX0QCdTDiHXTP92AiEAkUfXC5Eouj64J7WPXkjhxoWXTw+AY/yU/SQ05+R7VRk=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"25giJad2YEEJqecPsbLkiHBp3KgVpCipENzK1rNZuPbYg\",\"digitalDollar\":145.0,\"digitalStockBalance\":145.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCJSq59MSO/2B9w84dCVxmWRggxLFB1u2e1+m001mvTSAIhAMKnH3VGhgBO0mDdvr5eim6xX4J9E/jO3fvQWgSvzTwK\"}],\"previousHash\":\"a56a0e519e1630a1c0580481430aa00621001001020810200805830387440837\",\"minerAddress\":\"25giJad2YEEJqecPsbLkiHBp3KgVpCipENzK1rNZuPbYg\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":24319438452809543,\"minerRewards\":0.0,\"hashCompexity\":26,\"timestamp\":1704555268000,\"index\":167337,\"hashBlock\":\"48135472000c814010085824170a4a0f80520c62788310154480885018c1c803\"}";
            List<Block> blocks = new ArrayList<Block>();

            blocks.add(UtilsJson.jsonToBLock(json));
            blocks.get(0).setHashBlock("4111780951498215c031c98cc201b4690208828c3c844660104bacdc11040909");
            blocks.get(0).setPreviousHash("41c408c41718500200306b0200a10a040280e08f4d2844f30258a23eb9715490");
            Account miner = new Account("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", 0, 0, 1000);
             number = UtilsUse.bigRandomWinner(blocks.get(0),  miner);
             score = UtilsUse.calculateScore(5242881, 10);
        }

        System.out.println("score: " + score);
        System.out.println("number: " + number);

//        System.out.println("pow: " + Math.pow(19, 1.5));

    }


    @Test
    public void getBlock() throws IOException {
        String s = "http://localhost:8083";
        int index = 0;
        Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(index), s + "/block"));
        System.out.println("block: " + block);
    }

    @Test
    public void sub() throws IOException {
        String str = "{\"start\":167337,\"finish\":167338}";
        String s = " http://194.87.236.238:82";
        SubBlockchainEntity subBlockchainEntity = (SubBlockchainEntity) UtilsJson.jsonToListBLock(str, SubBlockchainEntity.class);
        List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(str, s + "/sub-blocks"));
        System.out.println(subBlocks.size());
    }

    @Test
    public void TestHashBlock() throws IOException {
        final int numThreads = Runtime.getRuntime().availableProcessors() - 2;
        final int numThreads2 = Runtime.getRuntime().availableProcessors();
        System.out.println(numThreads);
        System.out.println(numThreads2);
    }
    public static void main(String[] args) throws JsonProcessingException {
        String actualJ = "{\"dtoTransactions\":[{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":138.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIDoHB7j6VJr3G6IOmZpVFd4DtouNNYgVB/Uhjx2Fwd5PAiEAyMu+wNd6y8wugT3Ki8BtLPmS5+93HfCb60p03L5dvTE=\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":20.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIGhhp8zYarceDMl15uZGcggP8yR7PWduOkpA6PX5od8eAiAMB1LaXsNfxE/s9mccnb54Eon3JL26/1CeVtZSLcbYvQ==\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":3.0,\"digitalStockBalance\":1.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDfCZ9t3s+cRtPSKj7yvBIj2G33sGnOKWlesm+Z9WuVKgIgfM/MfJ4r4B/zSfb6Ky/A+bTVo9s1G+Rb1LZQMFaexRA=\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":1.0,\"digitalStockBalance\":1.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDZxb1O8Q+s9W90iZrWwSZiRhCIKik6/oaUAQ/EvpUO3wIhAPN0ZrZzuuTI3jyMXEWeYVHKarh75+kCwEgu6WDwNDcp\"},{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":1.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":1.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDbPQc4eN0RNwnJKsIKVUN7twNqD9DARORJKIyy/e2fcwIgVjaNcJlBPwlpaIWfGyUjZZkkSZgcV4yTUjHGtD3kyzo=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":13.0,\"digitalStockBalance\":13.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQDDOJXTnc5Gg6EMpkeK6rqmK/53chJORZHZQ4yANboy7gIhAOlVnGwv4ylPF9wn8JUg6OUwDczgj2p64dXr870vMISG\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"BUDGET\",\"digitalDollar\":130.0,\"digitalStockBalance\":130.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":4.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQC8AJYMNWwko4ybq2rm2OrZHHgOai6Aze6VMiUsJ9eqkQIgdpRmZETcUlBwZc51UMUE/O/b0v94za+6tR2kbBPcvdo=\"}],\"previousHash\":\"00000777a26bed8b58d10f344d94bd3048348b66cce633223aff1b39efd204b7\",\"minerAddress\":\"BUDGET\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":515012,\"minerRewards\":8.0,\"hashCompexity\":1,\"timestamp\":1702731467000,\"index\":127488,\"hashBlock\":\"00000caf287b044cec2445d666528697af51a95e079bf09a8c8e4abf5813ee77\"}";
        String prevJ = "{\"dtoTransactions\":[{\"sender\":\"rDqx8hhZRzNm6xxvL1GL5aWyYoQRKVdjEHqDo5PY2nbM\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":145.0,\"digitalStockBalance\":0.0,\"laws\":{\"packetLawName\":\"\",\"laws\":[],\"hashLaw\":\"\"},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQCTFqeYDMUThwkeM4D2tueoUc5Vz2PqoHt4wvSZHWJ7PgIhAO4kKiHYEcZcbbmZT54WVQ9GazDvovTdo3gtQFYX+GOb\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":13.0,\"digitalStockBalance\":13.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDfMGbzuDX/CzVlybjFgR48xpKKCg7NKj4SkayKiyQxxQIgCO1RDGyWBYAO61jk2cZ8tQweqtFTqvkYzBnQ62HSu+Y=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"BUDGET\",\"digitalDollar\":130.0,\"digitalStockBalance\":130.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCID8bfgSNZPlip+WSHSLrVB8VGlp81QWdTXgkXeUde8ikAiEAi2d7UjxFifMVA/uZvOigPvZ00HoSrvcp5sfL9AhMlYg=\"}],\"previousHash\":\"000005b2203e97829d6cfb01b36f4ae9e9fb50ff7a608d0532d4da702a968e7b\",\"minerAddress\":\"BUDGET\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":425566,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1702731065000,\"index\":127487,\"hashBlock\":\"00000777a26bed8b58d10f344d94bd3048348b66cce633223aff1b39efd204b7\"}";
        Block actual = UtilsJson.jsonToBLock(actualJ);
        Block prev = UtilsJson.jsonToBLock(prevJ);
       double G =  UtilsUse.blocksReward(actual.getDtoTransactions(), prev.getDtoTransactions());
        System.out.println("G: " + G);
        double reward = (5+G) * 26;
        reward = reward/ DOLLAR;
        System.out.println("Reward: " + reward);
    }




    private static volatile boolean blockFound = false;
    private static volatile String foundHash = "-";

    @Test
    public void testFindBlock() {
        String hash = "fdgfgdg";

        //перебирает число nonce чтобы найти хеш
        int randomNumberProof = 0;
        //используется для определения кто-нибудь уже успел добыть блок.

        String target = MiningUtils.calculateTarget(1);
        while (true) {

            //перебирает число nonce чтобы найти хеш


//            hash = UtilsUse.sha256hash("hello" + randomNumberProof);
            hash = MiningUtils.calculateHash("hello" + randomNumberProof);

            System.out.println("random: " + randomNumberProof);

            //если true, то прекращаем майнинг. Правильный блок найден
//            if (UtilsUse.chooseComplexity(hash, 1, 122499)) {
//                System.out.println("block found: hash: " + hash);
//                break;
//            }//если true, то прекращаем майнинг. Правильный блок найден
//            String target = BlockchainDifficulty.calculateTarget(1);
            //33290382
            if (MiningUtils.isValidHash(hash, target)) {
                System.out.println("block found: hash: " + hash);
                break;
            }
            randomNumberProof++;
        }
        System.out.println("hash: " + hash);
    }



    @Test
    public void allCoundDollar() {
        int reward = 5;
        int mult = 29;
        double sum = 113000000;
        for (int i = 0; i < mult; i++) {
            int step = mult - i;
            step = step < 1 ? 1 : step;
            sum += (reward * step) * 576 * 365;
        }
        System.out.printf("sum %f.00000000\n", sum);
        double min = 5 * 576 * 365;
        double max = 8 * 576 * 365;
        System.out.printf("min %f.00000000\n", min);
        System.out.printf("max %f.00000000\n", max);

    }

    @Test
    public void testReward() {
        int V28 = 1;
        int index = 7072;
        int diff = 8;
        int reductionPeriod = 576 * 14;
        if (index > V28) {
            int step1 = index - V28;
            int step2 = reductionPeriod;
            int step3 = step1 / step2;
            long money = (index - V28)
                    / (576 * 14);


            System.out.println("step1: " + step1);
            System.out.println("step2: " + step2);
            System.out.println("step3: " + step3);

            money = (long) (Seting.MONEY - money);
            System.out.println("result: " + (index - V28));
            System.out.println();
            System.out.println("money: " + money);
            money = money < 1 ? 1 : money;
            System.out.println("diff: " + (diff * money));
            System.out.println("money: " + money);
            System.out.println("index: " + index);


        }
        displayBlockCountdown(V28, index, 14);
    }

    public static void displayBlockCountdown(int v28, int index, int periodInDays) {
        int blocksPerDay = 576;

        if (index > v28) {
            // Вычисляем, сколько блоков прошло с последнего снижения
            int blocksSinceReduction = (index - v28) % (blocksPerDay * periodInDays);

            // Оставшиеся блоки до следующего снижения
            int blocksRemaining = (blocksPerDay * periodInDays) - blocksSinceReduction;


            System.out.println("Blocks remaining: " + blocksRemaining);

        }
    }



    public static int countLeadingZeroBits(byte[] hash) {
        int bitLength = hash.length * 8;
        BitSet bits = BitSet.valueOf(hash);

        int count = 0;
        while (count < bitLength && !bits.get(count)) {
            count++;
        }

        return count;
    }

    @Test
    public void testCountLeadingZeroBits() {
        int nonce = 0;
        int diff = 5;
        Timestamp lastIndex = new Timestamp(UtilsTime.getUniversalTimestamp());
        String hash = "";
        while (true) {
            String text = "hello world" + nonce;
            hash = UtilsUse.sha256hash(text);
            if (countLeadingZeroBits(hash.getBytes()) == diff) {
                break;
            }
            nonce++;
        }
        System.out.println("check: " + (countLeadingZeroBits(hash.getBytes()) == diff));
        System.out.println("hash: " + hash);

        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());


        Long result = actualTime.toInstant().until(lastIndex.toInstant(), ChronoUnit.SECONDS);
        System.out.println("result: " + result);
    }

    @Test
    public void testChangeDiff() {
        int index = 151833;
        while (true) {
            if (index % 288 == 0) {
                break;
            }
            index++;
        }
        System.out.println("index: " + index);
    }


    @Test
    public void testServer() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        int finish = 0 + Seting.PORTION_DOWNLOAD;
        int start = 0;

        SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(start, finish);


        System.out.println("1:sublockchainEntity: " + subBlockchainEntity);
        String subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);
        System.out.println("1:sublockchainJson: " + subBlockchainJson);
        String localhost = "http://localhost:8083";
        String server = "http://194.87.236.238:80";
        List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, server + "/sub-blocks"));
        List<Block> subBlocks1 = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, localhost + "/sub-blocks"));
        List<Block> fromFile = UtilsBlock.readLineObject("C://resources/blockchain");
        Block from = fromFile.get(0);

        System.out.println("******************************************");
        Block one = subBlocks.get(0);
        System.out.println(one);

        System.out.println("******************************************");
        Block two = subBlocks1.get(0);
        System.out.println(two);
        System.out.println("******************************************");
        System.out.println(one.equals(two));
        System.out.println("hash: " + one.getHashBlock().equals(two.getHashBlock()));
        System.out.println("getPreviousHash: " + one.getPreviousHash().equals(two.getPreviousHash()));
        System.out.println("getMinerAddress: " + one.getMinerAddress().equals(two.getMinerAddress()));
        System.out.println("getFounderAddress: " + one.getFounderAddress().equals(two.getFounderAddress()));
        System.out.println("getDtoTransactions: " + one.getDtoTransactions().equals(two.getDtoTransactions()));
        System.out.println("getIndex: " + (one.getIndex() == two.getIndex()));

        System.out.println("from: " + from.getTimestamp());
        System.out.println("one: " + one.getTimestamp());
        System.out.println("two: " + two.getTimestamp());

        System.out.println(one.getHashBlock().equals(one.hashForTransaction()));
        System.out.println(two.getHashBlock().equals(two.hashForTransaction()));


//        List<Block> subBlocks = UtilsBlock.readLineObject("C://resources/blockchain/");
        System.out.println("1:download sub block: " + subBlocks.size());
        Block prev = null;
        for (int i = 0; i < subBlocks.size(); i++) {
            if (prev == null) {
                prev = subBlocks.get(i);
                if (!prev.getHashBlock().equals(prev.hashForTransaction())) {
                    System.out.printf("wrong hash genesis: index: %d, actual %s, expected %s\n"
                            , prev.getIndex(), prev.getHashBlock(), prev.hashForTransaction());
                }
                continue;
            }
            if (!subBlocks.get(i).getHashBlock().equals(subBlocks.get(i).hashForTransaction())) {
                System.out.printf("wrong hash: index: %d, actual %s, expected %s\n"
                        , subBlocks.get(i).getIndex(), subBlocks.get(i).getHashBlock(), subBlocks.get(i).hashForTransaction());
            }
            System.out.println();
        }


    }

    @Test
    public void entityBalance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        List<Account> accountList = balances.entrySet().stream()
                .map(t -> t.getValue())
                .collect(Collectors.toList());

        List<EntityAccount> entityAccounts = UtilsAccountToEntityAccount.accountsToEntityAccounts(balances);
        List<Account> testAccount = UtilsAccountToEntityAccount.EntityAccountToAccount(entityAccounts);
        assertEquals(testAccount, accountList);

    }

    @Test
    public void entityBlock() throws IOException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"27MkHGZZnYkNtQMevRqBfAU2Pnu7LJEWC61AzMvAC31V3\",\"digitalDollar\":400.0,\"digitalStockBalance\":400.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIEHrC1uypFUgsXM/Z6yN/AM1qb+Q545RiU5FGFoFVvnGAiEA/adtMyE6ffnsOEVlXl+rbx2NstboFVEoY4D0EZuXfow=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIDXMAVvYsJLJLDVGm0bIfJhd58Jzv2OKrAlWVWH6mWlOAiACDeYNeQlkupje6M53315qV2W5VVHLLW+6nvh3zi32sw==\"}],\"previousHash\":\"00da347d3b3d4c9fc5c826fcff7b06569b673c63fc1afdaf7b9075b175f772be\",\"minerAddress\":\"27MkHGZZnYkNtQMevRqBfAU2Pnu7LJEWC61AzMvAC31V3\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":12,\"minerRewards\":0.0,\"hashCompexity\":1,\"timestamp\":1685946378876,\"index\":20,\"hashBlock\":\"039bf7b8b0be69125b93b26018df6b342d7c64305b6405cfa3b4813dae2bc682\"}");
        EntityBlock entityBlock = UtilsBlockToEntityBlock.blockToEntityBlock(block);
        System.out.println("***************************************");

        System.out.println(block);
        System.out.println("***************************************");

        Block testBlock = UtilsBlockToEntityBlock.entityBlockToBlock(entityBlock);
        System.out.println(testBlock);

        System.out.println("getHashCompexity: " + (block.getHashCompexity() == testBlock.getHashCompexity()));
        System.out.println("getHashBlock: " + (block.getHashBlock().equals(testBlock.getHashBlock())));
        System.out.println("getMinerRewards: " + (block.getMinerRewards() == testBlock.getMinerRewards()));
        System.out.println("getFounderAddress: " + (block.getFounderAddress().equals(testBlock.getFounderAddress())));
        System.out.println("getMinerAddress: " + (block.getMinerAddress().equals(testBlock.getMinerAddress())));
        System.out.println("getPreviousHash: " + (block.getPreviousHash().equals(testBlock.getPreviousHash())));
        System.out.println("getIndex: " + (block.getIndex() == testBlock.getIndex()));
        System.out.println("getTimestamp: " + (block.getTimestamp().equals(testBlock.getTimestamp())));
        System.out.println("getRandomNumberProof: " + (block.getRandomNumberProof() == testBlock.getRandomNumberProof()));
        for (int i = 0; i < testBlock.getDtoTransactions().size(); i++) {
            DtoTransaction transaction = block.getDtoTransactions().get(i);
            DtoTransaction transaction1 = testBlock.getDtoTransactions().get(i);
            System.out.println("getCustomer: " + (transaction.getCustomer().equals(transaction1.getCustomer())));
            System.out.println("getSender: " + (transaction.getSender().equals(transaction1.getSender())));
            System.out.println("getBonusForMiner: " + (transaction.getBonusForMiner() == transaction1.getBonusForMiner()));
            System.out.println("getDigitalDollar: " + (transaction.getDigitalDollar() == transaction1.getDigitalDollar()));
            System.out.println("getDigitalStockBalance: " + (transaction.getDigitalStockBalance() == transaction1.getDigitalStockBalance()));
            System.out.println("getVoteEnum: " + (transaction.getVoteEnum().equals(transaction1.getVoteEnum())));
            System.out.println("getSign: " + (transaction.getSign().equals(transaction1.getSign())));

            System.out.println("transaction.getLaws(): " + transaction.getLaws());
            System.out.println("transaction1.getLaws(): " + transaction1.getLaws());

            if (transaction.getLaws().getHashLaw() == null && transaction1.getLaws().getHashLaw() == null) {
                System.out.println("getHashLaw true");
            }
            if (transaction.getLaws().getLaws() == null && transaction1.getLaws().getLaws() == null) {
                System.out.println("getLaws true");
            }


        }

//        testBlock.getDtoTransactions().get(0).getLaws().setPacketLawName("32");
        assertEquals(testBlock, block);


    }

    @Test
    public void multipleFindHash() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        System.out.println("find hash method");
        int randomNumberProofStatic = 0;
        int differrentNumber = 0;
        int INCREMENT_VALUE = 100000;
        int hashCoplexity = 2;
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            System.out.println(":i: " + i);
            int finalDifferrentNumber = differrentNumber;
            Thread thread = new Thread(() -> {
                long nonce = randomNumberProofStatic + finalDifferrentNumber;
                String tempHash = "";
                int size = UtilsStorage.getSize();
                Timestamp previus = new Timestamp(UtilsTime.getUniversalTimestamp());
                String nameThread = Thread.currentThread().getName();
                while (!blockFound) {


//                    System.out.printf("\tTrying %d to find a block: ThreadName %s:\n ", nonce , nameThread);
                    Instant instant1 = Instant.ofEpochMilli(UtilsTime.getUniversalTimestamp());
                    Instant instant2 = previus.toInstant();

                    Duration duration = Duration.between(instant1, instant2);
                    long seconds = duration.getSeconds();

                    tempHash = UtilsUse.sha256hash("hello: " + nonce);


                    if (seconds > 10 || seconds < -10) {
                        long milliseconds = instant1.toEpochMilli();
                        previus = new Timestamp(milliseconds);
                        previus.setTime(milliseconds);

                        //проверяет устаревание майнинга, если устарел - прекращает майнинг

//                        int tempSize = UtilsStorage.getSize();
//                        if (size < tempSize) {
//                            Mining.miningIsObsolete = true;
//                            System.out.println("someone mined a block before you, the search for this block is no longer relevant and outdated: " + tempHash);
//
//                            synchronized (Block.class) {
//                                if (!blockFound) {
//                                    blockFound = true;
//                                    Mining.miningIsObsolete = true;
//                                    foundHash = tempHash;
//                                }
//                            }
//                            System.out.println("Block found: hash: " + tempHash);
//                            break;
//
//                        }

                    }

                    //если true, то прекращаем майнинг
//                    if (Mining.isIsMiningStop()) {
//                        System.out.println("mining will be stopped");
//
//                        synchronized (Block.class) {
//                            if (!blockFound) {
//                                blockFound = true;
//                                Mining.miningIsObsolete = true;
//                                foundHash = tempHash;
//                            }
//                        }
//                        System.out.println("Block found: hash: " + tempHash);
//
//                        foundHash= tempHash;
//                        break;
//
//                    }
//

                    String target = BlockchainDifficulty.calculateTarget(hashCoplexity);
                    BigInteger bigTarget = BlockchainDifficulty.calculateTargetV30(hashCoplexity);
                    //если true, то прекращаем майнинг. Правильный блок найден
                    if (UtilsUse.chooseComplexity(tempHash, hashCoplexity, 30001, target, bigTarget)) {
                        System.out.println("block found: hash: " + tempHash);
                        synchronized (Testing.class) {
                            if (!blockFound) {
                                blockFound = true;
//                                Mining.miningIsObsolete = true;
                                foundHash = tempHash;
                            }
                        }
                        System.out.println("Block found: hash: " + tempHash);
                        foundHash = tempHash;
                        break;
                    }
                    nonce++;
                }

            });

            differrentNumber += INCREMENT_VALUE;
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("foundhash: " + foundHash);
    }


    @Test
    public void testRollback() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIGUmscRx3/ry3+JCXEn5IUPvztkp8wssWVncXrboTT7WAiBzoushgN/njGCBu2giDT4BdPlt6lWk1ng6eDG3O6+10A==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIHSBd0UTPOBcYLp8CgcK0GDLTNWdLQjKXoX7RDIjCP5cAiBkVtlG037xou6eTEK3EnbhdmpDDwClemoUGKB3c2CoJg==\"}],\"previousHash\":\"010568240909052200811b000a049ac25a8df8ee44204046e0070c38dc108608\",\"minerAddress\":\"2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":5717820000077086321,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355281000,\"index\":268086,\"hashBlock\":\"08b04c22294ca9e3291d5168c2163ba000a0400900060e20452230480640909c\"}\n");
        Block block1 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEQCIQCq3OPKoLDRTMn39QX0Dw3j5aHkMFyB8pqGLfhUaDrFhwIfFrLYsXiNAQJpB9B8N0EL5Ft40nkf9npaOqODMlo8jQ==\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDmb7a4gSr4HA/xOxWDxfLlhrBeWKoxKDuKYWdw4HlfDwIgBEJ4RQjbg4XRIG4VoxNpkljQ+K0dy7WZVPxbcnONlFg=\"}],\"previousHash\":\"08b04c22294ca9e3291d5168c2163ba000a0400900060e20452230480640909c\",\"minerAddress\":\"28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":3602879713466940,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355436000,\"index\":268087,\"hashBlock\":\"c0811412a835915404c01202540128a201a0052518c06d460600225a7432963a\"}\n");
        Block block2 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIEgT2vk/wOxjFlYJoADR8JtApy5pp28NrHRk4rDmD1kbAiEArjA9mpnk4LZJ2LqRBeq/kJwKanJJzqoX63CkmIws7UY=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCYGK9AwF24/oxhJsRRsu+GmkySWWtlBoqnUPsRpvYIJwIgGnOwcYPdq0UD+txIgM2B759XufOt7FjFGkE6qpqYz8w=\"}],\"previousHash\":\"c0811412a835915404c01202540128a201a0052518c06d460600225a7432963a\",\"minerAddress\":\"s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":17113678609889273,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355643000,\"index\":268088,\"hashBlock\":\"c8021823126b424287805002cb14ae4fb40022a8948842606592105218104040\"}\n");
        Block block3 = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":26.679999999999996,\"digitalStockBalance\":26.679999999999996,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQC/hnEtsAP4E6wMTvhu0ejO2nWVh5KoP2IwPywj3vP8NQIgMTplpc0CXXHQbCNtzrh2rgeYb6MTcnc2cBEHMi9rxZc=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf\",\"digitalDollar\":266.79999999999995,\"digitalStockBalance\":266.79999999999995,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQDyzfwiWbYCpVy3lp4fjVLp0fzAUPUtwbSZgtU7AVuycAIgSFqsdYAtrhBDbT0zn3RVYoUfEjDtziqmYj8ILvjgqcc=\"}],\"previousHash\":\"c8021823126b424287805002cb14ae4fb40022a8948842606592105218104040\",\"minerAddress\":\"hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":17113678593776157,\"minerRewards\":0.0,\"hashCompexity\":21,\"timestamp\":1718355820000,\"index\":268089,\"hashBlock\":\"88262801c521884304084066a1000646a11400084ed2b1642a1d2a42a4328516\"}\n");
        List<Block> deleteBlocks = new ArrayList<>();
        deleteBlocks.add(block);
        deleteBlocks.add(block1);
        deleteBlocks.add(block2);
        deleteBlocks.add(block3);

        Map<String, Account> balances = new HashMap<>();
        double founderBalance = 26.679999999999996 * 4;
        double minerBalance = 266.79999999999995;
        balances.put("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", new Account("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43", founderBalance, founderBalance , 0.0));
        balances.put("2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL", new Account("2B2JAxRi6Uf12PGG6bYkkQKKNGC46zFr2dRhLXxFM4VwL", minerBalance, minerBalance , 0.0));
        balances.put("28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az", new Account("28QDe5813uR6iFsPxVY5p4naQUcXL3Tv4dKF7i1J3b7Az", minerBalance, minerBalance , 0.0));
        balances.put("s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq", new Account("s66m816Dakw1zs1MBXjJr5UuVABnAgA8ahPxvXNyAWyq", minerBalance, minerBalance , 0.0));
        balances.put("hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf", new Account("hK6XwB7LxM5wkqbHV3WTpjhZG1TstggsxqEWMw8FnbGf", minerBalance, minerBalance , 0.0));


        for (int i = deleteBlocks.size() - 1; i >= 0; i--) {
            Block temp = deleteBlocks.get(i);
            System.out.println("rollBackAddBlock4 :BasisController: addBlock3: blockchain is being updated: index" + temp.getIndex());

            balances = rollbackCalculateBalance(balances, temp);
        }
        System.out.println("===========================================");
        balances.entrySet().stream().forEach(System.out::println);
        System.out.println("===========================================");
        double balance = balances.get("nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43").getDigitalDollarBalance();

        System.out.printf("balance: %.16f", balance);
        System.out.printf("round: %.16f" , round(balance, 10));

    }


    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Test
    public void generateOriginalBlocks() throws IOException, JSONException, InterruptedException {

        for (int i = 1; i < 2000; i++) {

            System.out.println("block generate i: " + i);
            try {
                UtilUrl.readJsonFromUrl("http://localhost:8082/mine");
            } catch (IOException e) {
                System.out.println("error test mining");
                continue;
            }

        }

    }

    @Test
    public void testSorted() {
        // Изначальное значение хэш рейта
        long hashRate = 1; // Начинаем с 1 H/s

        // Вычисление SHA-256 хешей
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

            // Пример вычисления хеша строки "Hello, World!"
            String input = "Hello, World!";
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;

            // Увеличиваем хэш рейт до тех пор, пока время выполнения цикла не превысит одну секунду
            while (elapsedTime < 1000) {
                long numberOfHashes = hashRate;

                for (long i = 0; i < numberOfHashes; i++) {
                    sha256.digest(inputBytes);
                }

                long endTime = System.currentTimeMillis();
                elapsedTime = endTime - startTime;

                // Увеличиваем хэш рейт в 10 раз для следующей итерации
                hashRate *= 2;
            }

            System.out.println("Количество хешей SHA-256, которые может перебирать один поток процессора в одну секунду: " + (hashRate / 10));
            System.out.println("Время, затраченное на вычисление хешей: " + elapsedTime + " миллисекунд");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void testLimitedMoney() {
        int block = 0;
        double digitalDollarMining = 400;
        double digitalStockMining = 400;
        double dollarPercent = 0.2;
        double stockPercent = 0.4;
        double digitalDollarAccount = 0;
        double digitalStockAccount = 0;
        int year = 360 * 600;

        for (int i = 0; i < year; i++) {
            for (int j = 0; j < 576; j++) {
                block++;
                if (block % (180 * 576) == 0) {
                    digitalDollarAccount = digitalStockAccount - digitalDollarAccount * dollarPercent / 100;
                    digitalStockAccount = digitalStockAccount - digitalStockAccount * stockPercent / 100;
                }

                digitalDollarAccount += digitalDollarMining;
                digitalStockAccount += digitalStockMining;
            }
//            if(i%360 == 2){
//                digitalDollarMining = digitalDollarMining/2;
//                digitalStockAccount/= 2;
//            }
            if (digitalDollarAccount < 56000000000.0 && digitalDollarAccount > 5100000000.0) {
                //при таких настройках, верхняя граница, должна быть достигнута к 334 году
                //денежная масса больше не будет выше пять миллиардов сто сорок миллионов
                System.out.println("block: " + block + " index: " + i + " year: " + (i % 360));
                break;
            }
        }

        System.out.printf("digital dollar balance: %f\n", digitalDollarAccount);
        System.out.printf("digital stock balance: %f\n", digitalStockAccount);
    }


    @Test
    public void testHashDifficulty() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        String hash = "8c41b1ddc22ba3402ed0489952ba45161b279abb3b3b8ebc6bede062a21f83ff";
//        hash = bytesToBinary(hash.getBytes());
//        int count = countLeadingZeros(hash);
//       printBitSet(hash.getBytes());
//       BlockchainDifficulty.printBinary(hash.getBytes());
//        System.out.println(count);
        System.out.println("**********************************************");
        String hello = "папрарпарыек546нгу авпвкпкккккккккккккккккккккуке";
        int nonce = 0;
        String testHash = "";
        Timestamp actualTime = new Timestamp(UtilsTime.getUniversalTimestamp());


        int diffInStr = 8;
        int difficulty = 2;
        while (true) {

            testHash = UtilsUse.sha256hash(hello + nonce);


            hash = testHash;
//            hash = bytesToBinary(testHash.getBytes());

//            String binary = bytesToBinary(hash.getBytes());
//            int leadingZeros = countLeadingZeros(binary);
            int leadingZeros = countLeadingZeroBits(hash.getBytes());
            boolean countInHash = UtilsUse.hashComplexity(hash, diffInStr);
//            boolean countInHash = true;
            boolean test = leadingZeros >= difficulty && countInHash;
//            boolean test = countInHash;

            if (test) {

                break;
            }
            nonce++;

        }
        Timestamp last = new Timestamp(UtilsTime.getUniversalTimestamp());
        Long result = actualTime.toInstant().until(last.toInstant(), ChronoUnit.SECONDS);
        System.out.println("different time: " + result);
        System.out.println("***********************************");
//        printBitSet(testHash.getBytes());

        System.out.println("***********************************");
        BlockchainDifficulty.printBinary(hash.getBytes());
        System.out.println("hash: " + hash);
    }

    @Test
    public void Account() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> originals = SaveBalances.readLineObject("C://strategy1/balance/");
        Account account = originals.get("hzhq1LUk3qCcNyrTGE5pSRrRsYf3HkdSmeu5jap1JUnx");
        System.out.println(account);
    }

    @Test
    public void testHash() throws IOException {
        String json = "{\"dtoTransactions\":[],\"previousHash\":\"previous\",\"minerAddress\":\"mineAdres\",\"founderAddress\":\"founder\",\"randomNumberProof\":1,\"minerRewards\":0.0,\"hashCompexity\":2,\"timestamp\":1694088872091,\"index\":53392,\"hashBlock\":\"8c59604158f2f4ce093e7bef8ae46fc471071d95f24221e85b8b589dcff32a13\"}";
        String wrong = "{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"beACedNewaJU6BMFN8CrMeoRNArUHYEYTqZjQJCmNmRC\",\"digitalDollar\":400.0,\"digitalStockBalance\":400.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIHJezsfngLx2etBXHGA0eTMZOHZ0RGDL32HyJ4Qiv+ArAiEA2LellhnxyDwHUZ/8hz5KgEexhsWRg32k/Q2cd5UJbGo=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":8.0,\"digitalStockBalance\":8.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCUL3TvVodxb6aHhtfHEPcbxps20nqylU0ksaLq1XJb8AIgMnfMkbAPH8tUpw9LML5JtwLY0o1bXqr3JAuHQazGeB0=\"}],\"previousHash\":\"000006e63b836e089c34bf8d7cb4bf024feb67b3c70e03e6f24bc2fc0b6637d0\",\"minerAddress\":\"beACedNewaJU6BMFN8CrMeoRNArUHYEYTqZjQJCmNmRC\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":0,\"minerRewards\":0.0,\"hashCompexity\":5,\"timestamp\":1690488334985,\"index\":24281,\"hashBlock\":\"000000b0bef5b152f5ac9d68a9185e94de3e6a7368e4a2cb7b5e4d4fe2c36208\"}";
        Block block = UtilsJson.jsonToBLock(json);
        Block wrongHash = UtilsJson.jsonToBLock(wrong);
        assertEquals(block.getHashBlock(), block.hashForTransaction());
        assertNotEquals(wrongHash.getHashBlock(), wrongHash.hashForTransaction());
    }


    @Test
    public void testBalance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> originals = SaveBalances.readLineObject("C://strategy1/balance/");
        Map<String, Account> forks = SaveBalances.readLineObject("C://strategy2/balance/");
        Map<String, Account> balance = SaveBalances.readLineObject("C://resources/balance/");
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
                    Account account = new Account(arr[0], dollar, stock, 0);
                    cheaterAccount.put(arr[0], account);
                } else {
                    String address = arr[0];

                    try {
                        double dollar = Double.valueOf(arr[1]);
                        double stock = Double.valueOf(arr[2]);
//                        System.out.println("dollar: " + dollar + " stock: " + stock + " index: " + arr[3]);
                        Account account = new Account(address, dollar, stock, 0);
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

                Account account = new Account(address, resultDollar, resultStock, 0);
                differents.put(address, account);
            } else {
                double originalDollar = original.getValue().getDigitalDollarBalance();
                double originalStock = original.getValue().getDigitalStockBalance();
                Account account = new Account(address, originalDollar, originalStock, 0);
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
                Account account = new Account(address, resultDollar, resultStock, 0);
                afterFork.put(address, account);
            } else {
                double originalDollar = different.getValue().getDigitalDollarBalance();
                double originalStock = different.getValue().getDigitalStockBalance();
                Account account = new Account(address, originalDollar, originalStock,0 );
                afterFork.put(address, account);
            }
        }
        for (Map.Entry<String, Account> different : differents.entrySet()) {
            String address = different.getKey();
            if (cheaterAccount.containsKey(address)) {
                double dollar = different.getValue().getDigitalDollarBalance();
                double stock = different.getValue().getDigitalStockBalance();
                dollar = dollar - cheaterAccount.get(address).getDigitalDollarBalance();
                System.out.println("====================");
                System.out.println("cheter: " + cheaterAccount.get(address).getDigitalDollarBalance());
                stock = stock - cheaterAccount.get(address).getDigitalStockBalance();
                if (dollar < 0)
                    dollar = 0;
                if (stock < 0)
                    stock = 0;

//                different.getValue().setDigitalStockBalance(dollar);
//                different.getValue().setDigitalStockBalance(stock);
                differents.get(address).setDigitalDollarBalance(dollar);
                differents.get(address).setDigitalDollarBalance(stock);
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

        List<Account> forkAccount = forks.entrySet().stream().map(t -> t.getValue())
                .collect(Collectors.toList());

        double forkSumDollar = forkAccount.stream().mapToDouble(t -> t.getDigitalDollarBalance()).sum();
        double forkSumStock = forkAccount.stream().mapToDouble(t -> t.getDigitalStockBalance()).sum();

        System.out.printf("dollar: original: %f after fork: %f: \n", originalSumDollar, afterForkSumDollar);
        System.out.printf("stock: original: %f after fork: %f: \n", originalSumStock, afterForkSumsStock);
        System.out.printf(" different dollar: %f: \n", (originalSumDollar - afterForkSumDollar));
        System.out.printf(" different stock: %f: \n", (originalSumStock - afterForkSumsStock));
        System.out.printf("different fork dollar: %f: \n ", (originalSumDollar - forkSumDollar));
        System.out.printf("different fork stock: %f: \n ", (originalSumStock - forkSumStock));
        System.out.printf("dollar: %f: stock %f: \n ", forkSumDollar, forkSumStock);

        String address = "tjghGks15LdppYYvZKwb79w6wU2NwgpEeq5Rktj7smHH";
        System.out.println("****************************");
        System.out.println(address + ":" + cheaterAccount.containsKey(address));
        System.out.println("different balance: " + differents.get(address));
        System.out.println("orginal: " + originals.get(address));
        System.out.println("balance: " + balance.get(address));
        if (forks != null) {
            System.out.println("fork: " + forks.get(address));
            System.out.println("chetaer Account: " + cheaterAccount.get(address));
//        Account fork = forks.get(address);
//        Account different = differents.get(address);
//        double dollar = fork.getDigitalDollarBalance() - different.getDigitalDollarBalance();
//        double stock = fork.getDigitalStockBalance() - different.getDigitalStockBalance();
//        System.out.println("dollar: " + dollar);
//        System.out.println("stock: " + stock);
        }

    }


    @Test
    public void TestChangeDiff() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, InterruptedException {
        Timestamp first = Timestamp.from(Instant.now());
        Thread.sleep(60000);
        Timestamp second = Timestamp.from(Instant.now());
        long result = second.toInstant().until(first.toInstant(), ChronoUnit.MINUTES);
        Long result2 = first.toInstant().until(second.toInstant(), ChronoUnit.MINUTES);
        System.out.println("result1 " + result);
        System.out.println("result2: " + result2);

    }
}
