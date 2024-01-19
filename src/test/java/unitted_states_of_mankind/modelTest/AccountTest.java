package unitted_states_of_mankind.modelTest;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.model.Account;
import International_Trade_Union.simulation.AccountSimulation;
import International_Trade_Union.simulation.GenerateAccountsSimulation;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class AccountTest {
    private static double delta = 0.0000000001;


    @Test
    public void sendTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchProviderException, SignatureException, IOException, InvalidKeyException {
        double sendGold = 100.0;
        double sendStock = 200.0;
        List<AccountSimulation> accountSimulations = GenerateAccountsSimulation.accountSimulations(2);
        Account account = new Account(accountSimulations.get(0).getPublicKey(), 1000.0, 500, 0, 0);
        double expectedGold = account.getDigitalDollarBalance()-sendGold;
        double expectedPower = account.getDigitalStockBalance() - sendStock;
        double minerRewards = 0.0;
        Account account1 = new Account(accountSimulations.get(1).getPublicKey(), 0.0);
        DtoTransaction t = account.send(account1.getAccount(), accountSimulations.get(0).getPrivateKey(), sendGold,  sendStock, null, minerRewards, VoteEnum.YES);
        System.out.println("balance: " + account.getDigitalDollarBalance());
        Assert.assertTrue(expectedGold == account.getDigitalDollarBalance()-t.getDigitalDollar());
        Assert.assertTrue(expectedPower == account.getDigitalStockBalance() - t.getDigitalStockBalance());
//        Assert.assertEquals(String.valueOf(expected), minerAccount.getGoldBalance().subtract(t.getMoneySend())  , delta);

    }

    @Test
    public void accountsTest(){
        Account first = new Account("germes", 0.000000000000003);
        Account second = new Account("germes", 0.000000000000003);
        Assert.assertTrue(first.equals(second));

        Map<String, Account> firstmap = new HashMap<>();
        firstmap.put(first.getAccount(), first);
        firstmap.put(second.getAccount(), second);
        Map<String, Account> secondmap = new HashMap<>();
        secondmap.put(second.getAccount(), second);
        secondmap.put(first.getAccount(), first);

        Account find = firstmap.get(first.getAccount());
        System.out.println("find: " + find);
        System.out.println(firstmap.equals(secondmap));
    }


}
