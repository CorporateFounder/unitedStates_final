package unitted_states_of_mankind.utilsTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.model.Account;
import International_Trade_Union.utils.UtilsBalance;
import unitted_states_of_mankind.entityTest.blockchainTest.BlockchainRead;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class UtisBalanceEqualsAccountMethodsTest {
    private static Blockchain blockchain;
    private static final double delta = 0.0000000001;
    static {
        try {
            blockchain = BlockchainRead.getBlockchain();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
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

    @Test
    public void balanceTest() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        System.out.println("size: " + blockchain.sizeBlockhain());

        String address = "";
        for (int i = 0; i < blockchain.sizeBlockhain(); i++) {
            List<DtoTransaction> transactionList = blockchain.getBlockchainList().get(i).getDtoTransactions();
            if (transactionList.size() >0)
                address = transactionList.get(0).getSender();

            if(address.length() > 0 && !address.isEmpty())break;
        }

        Account account = UtilsBalance.findAccount(blockchain, address);
        double expected = account.getDigitalDollarBalance();
        Map<String, Account> balances = UtilsBalance.calculateBalances(blockchain.getBlockchainList());
        Account resultUtilsMethod = UtilsBalance.getBalance(address, balances);
//        Assert.assertTrue(expected.compareTo(resultUtilsMethod.getGoldBalance()) == 0);
        Assert.assertEquals(expected, resultUtilsMethod.getDigitalDollarBalance(), delta);
    }



}
