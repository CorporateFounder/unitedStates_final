package International_Trade_Union.simulation;



import International_Trade_Union.model.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddapterAccountSimulationToAccount {
    public static List<Account> getAccounts(List<AccountSimulation> accountSimulations){
        List<Account> accounts = new ArrayList<>();

        for (AccountSimulation accountSimulation :accountSimulations) {

            Account account = new Account(accountSimulation.getPublicKey(), BigDecimal.valueOf( accountSimulation.getDigitalDollarBalance()));
            accounts.add(account);
        }
        return accounts;
    }
}
