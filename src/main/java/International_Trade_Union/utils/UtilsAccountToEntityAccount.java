package International_Trade_Union.utils;

import International_Trade_Union.entity.entities.EntityAccount;
import International_Trade_Union.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilsAccountToEntityAccount {
    public static EntityAccount account(Account account){
        EntityAccount entityAccount = new EntityAccount(
                account.getAccount(),
                account.getDigitalDollarBalance(),
                account.getDigitalStockBalance(),
                account.getDigitalStakingBalance());
        return entityAccount;
    }

    public static Account entityAccountToAccount(EntityAccount entityAccount){
        Account account = new Account(
                entityAccount.getAccount(),
                entityAccount.getDigitalDollarBalance(),
                entityAccount.getDigitalStockBalance(),
                entityAccount.getDigitalStakingBalance());
        return account;
    }

    public static List<EntityAccount> accountsToEntityAccounts(Map<String, Account> accountMap){
        List<Account> accounts = accountMap.entrySet().stream().map(
                t->t.getValue()
        ).collect(Collectors.toList());
        return accountsToEntityAccounts(accounts);
    }
    public static List<EntityAccount> accountsToEntityAccounts(List<Account> accounts){
        List<EntityAccount> entityAccounts = new ArrayList<>();
        for (Account account : accounts) {
            EntityAccount entityAccount = account(account);
            entityAccounts.add(entityAccount);
        }
        return entityAccounts;
    }

    public static List<Account>EntityAccountToAccount(List<EntityAccount> entityAccounts){
        List<Account> accounts = new ArrayList<>();
        for (EntityAccount entityAccount : entityAccounts) {
            Account account = entityAccountToAccount(entityAccount);
            accounts.add(account);
        }
        return accounts;
    }
}
