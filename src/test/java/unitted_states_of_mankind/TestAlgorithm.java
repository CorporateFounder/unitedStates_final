package unitted_states_of_mankind;


import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.AddressUrl;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.model.Keys;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class TestAlgorithm {
    //TODO каждый раз после как подписал стоит обязательно удалить пароль с privKey

    //сначала сгенерировать ключи
    //потом их добавить в Seting.FOunder address
    @Test
    public void GenererateKeys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Base base = new Base58();
        Keys keyPair = UtilsSecurity.generateKeyPair();
        String pubkey = keyPair.getPubkey();
        String privKey = keyPair.getPrivkey();
        System.out.println("pubKey: " + pubkey);
        System.out.println("privKey: " + privKey);

//        String str = null;
        String str2 = "f";
//        System.out.println(str.isEmpty());
        System.out.println(str2.isEmpty());

    }

    @Test
    public void tests() {
        Set<String> words = new HashSet<>();
        words.add("http:\\hello");
        words.add("http:\\fine");
        words.add("http:\\hello");
        System.out.println(words);
    }

    @Test
    public void generateOriginalBlocks() throws IOException, JSONException, InterruptedException {
        for (int i = 1; i < 2000; i++) {
            System.out.println("block generate i: " + i);
            UtilUrl.readJsonFromUrl("http://localhost:8082/mine");
            if(i % 100   == 0){
                UtilUrl.readJsonFromUrl("http://localhost:8084/nodes/resolve");
            }

        }

    }

    @Test
    public void sendAddress() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        AddressUrl addressUrl = new AddressUrl("http://localhost:8082");
        String jsonAddress = UtilsJson.objToStringJson(addressUrl);
        System.out.println("json: " + jsonAddress);
        BasisController.sendAddress();
    }

    @Test
    public void testCalculate() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<TestedBlock> A = new ArrayList<>();
        A.add(new TestedBlock(1, "A1"));
        A.add(new TestedBlock(2, "A2"));
        A.add(new TestedBlock(3, "A3"));
        A.add(new TestedBlock(4, "A4"));
        A.add(new TestedBlock(5, "A5"));
        A.add(new TestedBlock(6, "A6"));
        List<TestedBlock> B = new ArrayList<>();
        B.add(new TestedBlock(1, "A1"));
        B.add(new TestedBlock(2, "A2"));
        B.add(new TestedBlock(3, "A3"));
        B.add(new TestedBlock(4, "A4"));
        B.add(new TestedBlock(5, "A5"));
        B.add(new TestedBlock(6, "B6"));
        B.add(new TestedBlock(7, "B7"));
        B.add(new TestedBlock(8, "B8"));
        B.add(new TestedBlock(9, "B9"));

        String hashA = "";
        A = A.stream().sorted(Comparator.comparing(TestedBlock::getIndex)).collect(Collectors.toList());
        for (int i = 0; i < A.size(); i++) {
            hashA += A.get(i).getData();
        }
        String hashB = "";
        B = B.stream().sorted(Comparator.comparing(TestedBlock::getIndex)).collect(Collectors.toList());
        for (int i = 0; i < B.size(); i++) {
            hashB += B.get(i).getData();
        }


        hashA = UtilsUse.sha256hash(hashA);
        hashB = UtilsUse.sha256hash(hashB);

        System.out.println("hashA: " + hashA + " A: " + A);
        System.out.println("hashB: " + hashB + " B: " + B);

        List<TestedBlock> C = new ArrayList<>();

        System.out.printf("finish size: A:%d B:%d C:%d \n", A.size(), B.size(), C.size());
        C.addAll(A);
        C.addAll(B.subList(A.size(), B.size()));

        String hashC = "";
        C = C.stream().sorted(Comparator.comparing(TestedBlock::getIndex)).collect(Collectors.toList());
        for (int i = 0; i < C.size(); i++) {
            hashC += C.get(i).getData();
        }

        hashC = UtilsUse.sha256hash(hashC);
        System.out.printf("finish size: A:%d B:%d C:%d \n", A.size(), B.size(), C.size());
        System.out.println("hashC: " + hashC + " C: " + C);
        if (hashA.equals(hashC)){
            System.out.println("valid: ");
            System.out.println("hashA: " + hashA + " A: " + A);
            System.out.println("hashC: " + hashC + " C: " + C);
        }else {
            System.out.println("first algorithm not valid: start second algorithm");
            C = new ArrayList<>();
            C.addAll(B.subList(A.size(), B.size()));
            System.out.println("C: size: " + C.size());

            for (int i = A.size()-1; i > 0 ; i--) {
                if(!A.get(i).equals(B.get(i))){
                    C.add(B.get(i));

                }
                else {
                    C.add(B.get(i));
                    System.out.println("i: " + i);
                   C.addAll(A.subList(0, i));
                   break;
                }
            }

            C = C.stream().sorted(Comparator.comparing(TestedBlock::getIndex)).collect(Collectors.toList());
            hashC = "";
            for (int i = 0; i < C.size(); i++) {
                hashC += C.get(i).getData();
            }
            hashC = UtilsUse.sha256hash(hashC);
            System.out.printf("finish size: A:%d B:%d C:%d \n", A.size(), B.size(), C.size());
            System.out.println("finish: hashA: " + hashA + " A: " + A);
            System.out.println("finish: hashB: " + hashB + " B: " + B);
            System.out.println("finish: hashC: " + hashC + " C: " + C);
        }

    }

    @Test
    public void validTest() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        System.out.println("blockchain valid: " + blockchain.validatedBlockchain());
        Assert.assertTrue(blockchain.validatedBlockchain());
    }

    @Test
    public void TestSignCheck() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, IOException, InvalidKeyException {
        //доход майнера
        double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
        double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;

        //доход основателя
        double founderReward = Seting.DIGITAL_DOLLAR_FOUNDER_REWARDS_BEFORE;
        double founderDigigtalReputationReward = Seting.DIGITAL_REPUTATION_FOUNDER_REWARDS_BEFORE;

        Base base = new Base58();

        //суммирует все вознаграждения майнеров
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(Seting.BASIS_PASSWORD));
        double sumRewards =0.0;

        //вознаграждения майнера
        DtoTransaction minerRew = new DtoTransaction(Seting.BASIS_ADDRESS, Seting.ADDRESS_FOUNDER_TEST,
                minerRewards, digitalReputationForMiner, new Laws(), sumRewards, VoteEnum.YES );

        byte[] signGold = UtilsSecurity.sign(privateKey, minerRew.toSign());
        minerRew.setSign(signGold);

        //вознаграждение основателя
        DtoTransaction founderRew = new DtoTransaction(Seting.BASIS_ADDRESS, Seting.ADDRESS_FOUNDER_TEST,
                founderReward, founderDigigtalReputationReward, new Laws(), 0.0, VoteEnum.YES);
        byte[] signFounder = UtilsSecurity.sign(privateKey, founderRew.toSign());

        founderRew.setSign(signFounder);

        System.out.println(founderRew.verify());
        System.out.println(minerRew.verify());
        System.out.println("sign: " + signGold.toString());

    }

    @Test
    public void testCloneBlocks() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Blockchain blockchain =  Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        Blockchain blockchain1 = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        List<Block> temporary = new ArrayList<>();
        for (Block block : blockchain.getBlockchainList()) {
            temporary.add(block.clone());
        }
        temporary.get(temporary.size() - 1).setHashBlock("Hello");
        blockchain1.setBlockchainList(temporary);
        Assert.assertTrue(!blockchain1.equals(blockchain));

    }



}
