package unitted_states_of_mankind;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.controllers.MainController;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.NamePOSITION;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilUrl;
import International_Trade_Union.utils.UtilsFileSaveRead;
import org.json.JSONException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class Testing {


    @Test
    public void generateOriginalBlocks() throws IOException, JSONException, InterruptedException {
        List<String> addresses = new ArrayList<>();
        //случайным образом выбирает из адрессов
        Random random = new Random();
        addresses.add("22a5XcurUDGGhJ3JncMnRS4Ka8LDRf7tpb6YJMjvTJFZr");
        addresses.add("stExZb8ifLfnFoq4JJuTifpAcscegATH8znhwW26zyTa");



        for (int i = 1; i < 2000; i++) {
            int size = 0;

            while (size <= 0)
                size = random.nextInt() * size;

            String address = addresses.get(size);
            UtilsFileSaveRead.save(address, Seting.ORIGINAL_ACCOUNT, false);
            System.out.println("block generate i: " + i);
            try {
                UtilUrl.readJsonFromUrl("http://localhost:8082/mine");
            }catch (IOException e){
                System.out.println("error test mining");
                continue;
            }

        }

    }

    @Test
    public void testSorted(){
        Directors directors = new Directors();
        System.out.println(directors.isElectedByBoardOfDirectors(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString()));
    }

    @Test
    public void sendBlocks() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        BasisController.sendAllBlocksToStorage(blockchain.getBlockchainList());
    }

    @Test
    public void testLimitedMoney(){
        int block = 0;
        double digitalDollarMining = 200;
        double digitalStockMining = 200;
        double dollarPercent = 0.1;
        double stockPercent = 0.2;
        double digitalDollarAccount = 0;
        double digitalStockAccount = 0;
        int year = 360 * 600;

        for (int i = 0; i < year; i++) {
            for (int j = 0; j < 576; j++) {
                block++;
                if(block % (180 * 576) == 0){
                    digitalDollarAccount = digitalStockAccount - digitalDollarAccount * dollarPercent /100;
                    digitalStockAccount = digitalStockAccount - digitalStockAccount * stockPercent /100;
                }

                digitalDollarAccount += digitalDollarMining;
                digitalStockAccount += digitalStockMining;
            }
//            if(i%360 == 2){
//                digitalDollarMining = digitalDollarMining/2;
//                digitalStockAccount/= 2;
//            }
            if(digitalDollarAccount < 56000000000.0 && digitalDollarAccount > 5100000000.0){
                //при таких настройках, верхняя граница, должна быть достигнута к 334 году
                //денежная масса больше не будет выше пять миллиардов сто сорок миллионов
                System.out.println("block: " + block + " index: " + i + " year: " + (i%360));
                break;
            }
        }

        System.out.printf("digital dollar balance: %f\n", digitalDollarAccount);
        System.out.printf("digital stock balance: %f\n", digitalStockAccount);
    }

    @Test
    public void addblock() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        BasisController.addBlock(blockchain.getBlockchainList(), blockchain);
    }
}
