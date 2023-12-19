package unitted_states_of_mankind.entityTest.blockchainTest;


import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.simulation.AccountSimulation;
import International_Trade_Union.simulation.GenerateAccountsSimulation;
import International_Trade_Union.utils.UtilsBlock;
import unitted_states_of_mankind.networkTest.TransactionsTest;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class BlockchainTest {
    private static Blockchain blockchain;

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

    @After
    public void resetBlockchain() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {

        blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
    }

    public BlockchainTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
    }


    @Test
    public void validatedBlockchainTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        List<AccountSimulation> list = GenerateAccountsSimulation.accountSimulations(100);
        System.out.println("listSize: " + list.size());
        TransactionsTest transactionsTest = new TransactionsTest(1, list);

        List<DtoTransaction> transactions = transactionsTest.getTransactions();
        long difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
        Block block = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), list.get(0).getPublicKey(), Seting.ADDRESS_FOUNDER_TEST, difficulty, blockchain.sizeBlockhain());

        blockchain.addBlock(block);

        transactions = transactionsTest.getTransactions();
         difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
         block = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), list.get(0).getPublicKey(), Seting.ADDRESS_FOUNDER_TEST, difficulty, blockchain.sizeBlockhain());

        blockchain.addBlock(block);
        System.out.println("size: " + blockchain.getBlockchainList().size());
        for (int i = 0; i < blockchain.sizeBlockhain(); i++) {
            System.out.println("blockchain hashes: " + blockchain.getHashBlock(i));
            System.out.println("previous hash: " + blockchain.getBlock(i).getPreviousHash());
        }
        Assert.assertTrue(blockchain.validatedBlockchain());

    }
    @Test
    public void valdatedBLockchainFileTest() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        List<Block> blocks = UtilsBlock.readLineObject(
                Seting.TEST_BLOCKCHAIN_SAVED);
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
        blockchain.setBlockchainList(blocks);
        System.out.println("blockchain size: " + blockchain.sizeBlockhain());

        Assert.assertTrue(blockchain.validatedBlockchain());
    }

    @Test
    public void notValidBlockchainTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<AccountSimulation> list = GenerateAccountsSimulation.accountSimulations(2);
        TransactionsTest transactionsTest = new TransactionsTest(1, list);
        List<DtoTransaction> transactions = transactionsTest.getTransactions();
        long difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
        Block block = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), list.get(0).getPublicKey(), Seting.ADDRESS_FOUNDER_TEST, difficulty, blockchain.sizeBlockhain());

//        Block block = SomeTransactionsConfigSim.someTransactions(blockchain, 1, list, Seting.BLOCK_GENERATION_INTERVAL_TEST, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST);
        blockchain.addBlock(block);

         transactions = transactionsTest.getTransactions();
         difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
         block = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), list.get(0).getPublicKey(), Seting.ADDRESS_FOUNDER_TEST, difficulty, blockchain.sizeBlockhain());

//        block = SomeTransactionsConfigSim.someTransactions(blockchain, 1, list, Seting.BLOCK_GENERATION_INTERVAL_TEST, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST);
        blockchain.addBlock(block);

        blockchain.getBlock(1).setHashBlock("wrong");
        Assert.assertTrue(!blockchain.validatedBlockchain());
    }

    @Test
    public void genesisTest() throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        Assert.assertTrue(blockchain.genesisBlock().verifyesTransSign());
    }

    @Test
    public void afterGenesisSizeTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        int size = 1;
        Assert.assertEquals(size, blockchain.getBlockchainList().size());
    }

    @Test
    public void showBlockChain() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        List<AccountSimulation> list = GenerateAccountsSimulation.accountSimulations(10);
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        TransactionsTest transactionsTest = new TransactionsTest(1, list);
        List<DtoTransaction> transactions = transactionsTest.getTransactions();
        long difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
        Block firstBlock = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), list.get(0).getPublicKey(), Seting.ADDRESS_FOUNDER_TEST, difficulty, blockchain.sizeBlockhain());

         transactions = transactionsTest.getTransactions();
         difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
        Block secondBlock = new Block(transactions, blockchain.getHashBlock(blockchain.sizeBlockhain()-1), list.get(0).getPublicKey(), Seting.ADDRESS_FOUNDER_TEST, difficulty, blockchain.sizeBlockhain());

        blockchain.addBlock(firstBlock);
        blockchain.addBlock(secondBlock);
        System.out.println("blockchain:\n ");
        System.out.println("genesis: " + blockchain.getBlockchainList().get(0).getHashBlock());
        for (Block block : blockchain.getBlockchainList()) {
            System.out.println("hash: " + block.getHashBlock());
        }

    }


}
