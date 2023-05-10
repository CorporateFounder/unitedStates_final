package unitted_states_of_mankind.utilsTest;

import International_Trade_Union.vote.Laws;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.*;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Keys;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilAccounts;
import International_Trade_Union.utils.UtilsBalance;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;
import unitted_states_of_mankind.entityTest.blockchainTest.BlockchainRead;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//TODO высянить почему не списывается правильно долги
@SpringBootTest
public class UtilsBalanceTest {
    private static final double delta = 0.0000000001;
    private static Blockchain blockchain;

    static {

        try {
            blockchain  = BlockchainRead.getBlockchain();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }


    @Before
    public void loadBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeyException {
        blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        List<Block> blocks = UtilsBlock.readLineObject(Seting.TEST_BLOCKCHAIN_SAVED);
        blockchain.setBlockchainList(blocks);
    }

    @After
    public void resetBlockchain() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        List<Block> blocks = UtilsBlock.readLineObject(Seting.TEST_BLOCKCHAIN_SAVED);
        blockchain.setBlockchainList(blocks);
    }






    @Test
    public void findAcountTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);


        blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);

        List<Block> blocks = UtilsBlock.readLineObject(Seting.TEST_BLOCKCHAIN_SAVED);
        blockchain.setBlockchainList(blocks);
        Block block = blockchain.getBlock(1);

        System.out.println("blockchain: "+blockchain);

        Account account =  UtilsBalance.findAccount(blockchain, block.getFounderAddress());
        System.out.println( "minerAccount: "+ account);
        Account afterMining = UtilsBalance.findAccount(blockchain, Seting.ADDRESS_FOUNDER_TEST);
        System.out.println("afterMining: " + afterMining);
        System.out.println("address founder: " + Seting.ADDRESS_FOUNDER_TEST);
        String addressExpected = Seting.ADDRESS_FOUNDER_TEST;
        Assert.assertEquals(addressExpected, account.getAccount());


    }








    //TODO исправить sendTest установив баланс отправилтеля
    @Test
    public void SendTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        Base base = new Base58();
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        Keys keyPair = UtilsSecurity.generateKeyPair();
        byte[] senderPub = base.decode(keyPair.getPubkey());
        byte[] senderPriv = base.decode(keyPair.getPrivkey());

        keyPair = UtilsSecurity.generateKeyPair();
        byte[] recipientPub = base.decode(keyPair.getPubkey());


        PrivateKey senderPrivateKey = UtilsSecurity.privateBytToPrivateKey(senderPriv);

        DtoTransaction dtoTransaction = new DtoTransaction(base.encode(senderPub),
                base.encode(recipientPub),
                100.0,
                5,
                new Laws(),
                0.0,
                VoteEnum.YES);
        byte[] sign = UtilsSecurity.sign(senderPrivateKey, dtoTransaction.toSign());
        dtoTransaction.setSign(sign);

        List<DtoTransaction> transactions = new ArrayList<>();
        transactions.add(dtoTransaction);


        Block block = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), Seting.ADDRESS_FOUNDER_TEST, Seting.ADDRESS_FOUNDER_TEST,  Seting.HASH_COMPLEXITY_GENESIS, blockchain.sizeBlockhain());

        blockchain.addBlock(block);

        Account sender = UtilsBalance.findAccount(blockchain, base.encode(senderPub));
        Account recipient = UtilsBalance.findAccount(blockchain, base.encode(recipientPub));
        System.out.println("sender before: " + sender);
        System.out.println("recipient before: " + recipient);
        UtilsBalance.sendMoney(sender, recipient, dtoTransaction.getDigitalDollar(),dtoTransaction.getDigitalStockBalance(),  dtoTransaction.getBonusForMiner());
        System.out.println("sender after: " + sender);
        System.out.println("recipient after: " + recipient);
    }
}


