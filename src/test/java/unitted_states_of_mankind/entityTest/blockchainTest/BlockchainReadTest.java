package unitted_states_of_mankind.entityTest.blockchainTest;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class BlockchainReadTest {
    private static double delta = 0.0000000001;

    @Test
//    @Ignore
    public void getBlockchainTest() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        int blocks = (int) (Seting.COUNT_BLOCK_IN_DAY * 1);
        Blockchain blockchainWithoutSave = BlockchainRead.getBlockchain(
                blocks,
                20,
                BlockchainFactoryEnum.TEST,
                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST,
                Seting.BLOCK_GENERATION_INTERVAL_TEST);

        Blockchain blockchainWithSave = BlockchainRead.getBlockchainSave(
                blocks,
                100,
                BlockchainFactoryEnum.TEST,
                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST,
                Seting.BLOCK_GENERATION_INTERVAL_TEST,
                true,
                10.0,
                10.0);

        System.out.println("blockchainwithoutSave size: " + blockchainWithoutSave.sizeBlockhain());
        System.out.println("blockchainwitSave size: " + blockchainWithSave.sizeBlockhain());
        Blockchain afterLoad = BlockchainRead.getBlockchain();
        System.out.println("blockchainwtithoutsave validation: " + blockchainWithoutSave.validatedBlockchain());
        System.out.println("blockchainWithsave validation: " + blockchainWithSave.validatedBlockchain());
        System.out.println("after load validation: " + afterLoad.validatedBlockchain());

//        Assert.assertTrue(blockchainWithoutSave.sizeBlockhain() == blockchainWithSave.sizeBlockhain());
        Assert.assertTrue(blockchainWithoutSave.validatedBlockchain());
        Assert.assertTrue(blockchainWithSave.validatedBlockchain());
        Assert.assertTrue(afterLoad.validatedBlockchain());

    }

    @Test
//    @Ignore
    public  void equalsBalanceFromFileEndReadTest() throws InvalidAlgorithmParameterException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        int blocks = (int) 144;
        Blockchain blockchainWithSave = BlockchainRead.getBlockchainSave(
                blocks,
                5,
                BlockchainFactoryEnum.TEST,
                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST,
                Seting.BLOCK_GENERATION_INTERVAL_TEST,
                true,
                10.0,
                4.0);

        Map<String, Account> balanceWithSave = new HashMap<>();
        List<String> signs = new ArrayList<>();
        for (Block block : blockchainWithSave.getBlockchainList()) {
            balanceWithSave = UtilsBalance.calculateBalance(balanceWithSave, block, signs);
        }
        Map<String, Account> calculatesTest = UtilsBalance.calculateBalances(blockchainWithSave.getBlockchainList());

        Map<String, Account> balanceFromFile = SaveBalances.readLineObject(Seting.TEST_BLOCKCHAIN_BALANCES);

        List<Block> blocks1 = UtilsBlock.readLineObject(Seting.TEST_BLOCKCHAIN_SAVED);
        Map<String, Account> balance = UtilsBalance.calculateBalances(blocks1);


        List<Account> accountList = balanceWithSave.entrySet().stream().map(t-> t.getValue()).collect(Collectors.toList());
        double allBalance = UtilAccounts.getAllBalance(accountList);

        List<Account> accountListFromFile = balanceFromFile.entrySet().stream().map(t->t.getValue()).collect(Collectors.toList());
        double allBalanceFromFile = UtilAccounts.getAllBalance(accountListFromFile);

        List<Account> balanceFromBlocchainFile = balance.entrySet().stream().map(t->t.getValue()).collect(Collectors.toList());
        double allBalanceFromBlockchainFile = UtilAccounts.getAllBalance(balanceFromBlocchainFile);


        System.out.println("\nbalanceWithSave: ");
        balanceWithSave.entrySet().forEach(System.out::println);
        System.out.println("\nbalanceFromFile: ");
        balanceFromFile.entrySet().forEach(System.out::println);
        System.out.println("\ncalculateBalances: ");
        calculatesTest.entrySet().forEach(System.out::println);
        System.out.println("\ncalculateBalancesBlockchainFile: ");
        balance.entrySet().forEach(System.out::println);

        List<Account> accountListCalculateBalance = calculatesTest.entrySet().stream().map(t->t.getValue()).collect(Collectors.toList());

        double allBalanceCalculateBalances = UtilAccounts.getAllBalance(accountListCalculateBalance);

        System.out.println("calculatebalances: " + new BigDecimal(allBalanceCalculateBalances));
        System.out.println("all ballance: " + new BigDecimal(allBalance));
        System.out.println("from file: " + new BigDecimal(allBalanceFromFile));
        System.out.println("from blockchainFile: " + new BigDecimal(allBalanceFromBlockchainFile));

        Assert.assertEquals(balanceWithSave, balance);
        Assert.assertEquals(balanceWithSave, calculatesTest);
        Assert.assertEquals(balanceWithSave, balanceFromFile);


    }



}
