package unitted_states_of_mankind.entityTest.blockchainTest.blockTest;


import International_Trade_Union.vote.Laws;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Keys;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.simulation.AccountSimulation;
import International_Trade_Union.simulation.GenerateAccountsSimulation;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.UtilsUse;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;
import unitted_states_of_mankind.networkTest.TransactionsTest;

import java.sql.Timestamp;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class BlockTest {
    private static List<DtoTransaction> dtoTransactionList;

    private static Blockchain blockchain;
    private static final double delta = 0.0000000001;

    static {
        try {
            blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void beforeClass() throws
            InvalidAlgorithmParameterException,
            NoSuchAlgorithmException,
            NoSuchProviderException,
            IOException,
            SignatureException,
            InvalidKeyException, InvalidKeySpecException {
        Base base = new Base58();

        Keys pair = UtilsSecurity.generateKeyPair();
        double gold = 0.0;
        double power = 0.0;
        DtoTransaction transaction = new DtoTransaction( Seting.ADDRESS_FOUNDER_TEST,
                Seting.ADDRESS_FOUNDER_TEST,
                gold,
               power,
                null,
                0.0,
                VoteEnum.YES);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(pair.getPrivkey()));
        byte[] sign = UtilsSecurity.sign(privateKey, transaction.toSign());
        transaction.setSign(sign);

         dtoTransactionList = new ArrayList<>();
    }

    @Test
    public void blockHashTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException, InvalidKeySpecException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        Keys pair = UtilsSecurity.generateKeyPair();


        Base base = new Base58();
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(pair.getPrivkey()));
        DtoTransaction dtoTransaction = new DtoTransaction(pair.getPubkey() , Seting.ADDRESS_FOUNDER_TEST,
                1.0, 0.5,new Laws(),  0.0, VoteEnum.YES);
        byte[] signByte = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
        dtoTransaction.setSign(signByte);

        List<DtoTransaction> transactions = new ArrayList<>();
        transactions.add(dtoTransaction);
        Block genesisBlock = blockchain.genesisBlock();

        Block block = new Block(transactions, genesisBlock.getHashBlock(), pair.getPubkey(), Seting.ADDRESS_FOUNDER_TEST, Seting.HASH_COMPLEXITY_GENESIS, blockchain.sizeBlockhain());
        System.out.println("block: " + block.getHashBlock());
        Assert.assertTrue(UtilsUse.hashComplexity(block.getHashBlock(), Seting.HASH_COMPLEXITY_GENESIS));
    }

    @Test
    public void minerRewardsTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        Keys pair = UtilsSecurity.generateKeyPair();


        Base base = new Base58();
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(pair.getPrivkey()));
        DtoTransaction dtoTransaction = new DtoTransaction(pair.getPubkey() , Seting.ADDRESS_FOUNDER_TEST,
                1.0,1.0, new Laws(), 10.0, VoteEnum.YES);
        DtoTransaction dtoTransaction1 = new DtoTransaction(pair.getPubkey() , Seting.ADDRESS_FOUNDER_TEST,
                1.0, 1.0,new Laws(), 20.0, VoteEnum.YES);
        byte[] signByte = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
        byte[] signByte1 = UtilsSecurity.sign(privateKey, dtoTransaction1.toSign());

        dtoTransaction.setSign(signByte);
        dtoTransaction1.setSign(signByte1);

        List<DtoTransaction> transactions = new ArrayList<>();
        transactions.add(dtoTransaction);
        transactions.add(dtoTransaction1);
        Block genesisBlock = blockchain.genesisBlock();

        Block block = new Block(transactions, genesisBlock.getHashBlock(), pair.getPubkey(), Seting.ADDRESS_FOUNDER_TEST, Seting.HASH_COMPLEXITY_GENESIS, blockchain.sizeBlockhain());
        System.out.println("minerRewards: " + block.getMinerRewards());
        System.out.println("block: " + block);

        Assert.assertTrue(30==block.getMinerRewards());
//        Assert.assertEquals(30.0, block.getMinerRewards(), delta);

    }

    @Test
    public  void randomTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        List<AccountSimulation> list = GenerateAccountsSimulation.accountSimulations(1);
        Timestamp before = new Timestamp(System.currentTimeMillis());
//
//        Block block = SomeTransactionsConfigSim.someTransactions(blockchain, 1,list, Seting.BLOCK_GENERATION_INTERVAL_TEST, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST);
        TransactionsTest transactionsTest = new TransactionsTest();
       List<DtoTransaction> transactions = transactionsTest.getTransactions();
        int difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
        Block block = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), list.get(0).getPublicKey(), Seting.ADDRESS_FOUNDER_TEST, difficulty, blockchain.sizeBlockhain());

        Timestamp after = new Timestamp(System.currentTimeMillis());
        long different = after.getTime() - before.getTime();
        Timestamp timestamp = new Timestamp(different);
        System.out.println("random: " + block.getRandomNumberProof() + " : " + timestamp);

    }


}
