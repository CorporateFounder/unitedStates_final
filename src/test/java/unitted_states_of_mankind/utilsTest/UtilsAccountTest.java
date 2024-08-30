package unitted_states_of_mankind.utilsTest;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilAccounts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UtilsAccountTest {
    private static final double delta = 0.0000000001;



    @Test
    public void getAllRemnantUpperLevel(){

        List<Account> accounts = new ArrayList<>();
        double balance = 50;
        double limit = 49.0;
        accounts.add(new Account("first", BigDecimal.valueOf(balance)));
        accounts.add(new Account("second", BigDecimal.valueOf(balance)));
        accounts.add(new Account("third",BigDecimal.valueOf(25)));

        int expected = 2;
        //акаунты которые больше лимита
        List<Account> accountList = UtilAccounts.allAccountsRemnantUpperLimit( accounts,limit);
        Assert.assertEquals(expected,accountList.size());
    }

    @Test
    public void serchAccount(){
        List<Account> accounts = new ArrayList<>();
        double balance = 50;
        accounts.add(new Account("first", BigDecimal.valueOf(balance)));
        accounts.add(new Account("second", BigDecimal.valueOf(balance)));
        accounts.add(new Account("third", BigDecimal.valueOf(25)));
        Account account = UtilAccounts.serchAccountByAddress(accounts,"second");
        Assert.assertEquals("second", account.getAccount());
    }

}
