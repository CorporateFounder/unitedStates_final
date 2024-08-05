package International_Trade_Union.utils;



import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;

import java.util.ArrayList;
import java.util.List;

public class UtilAccounts {

//    поиск аккаунта по адрессу
    public static Account serchAccountByAddress(List<Account> accountList, String address){
        Account result = null;
        for (Account account : accountList) {
            if(account.getAccount().equals(address)){
                result = account;
//                System.out.println(String.format("find minerAccount: %s, address %s", minerAccount.getAccount(), address));
                return result;
            }
        }
        return result;
    }
//    возвращает список счетов чья наличность больше n
    public static List<Account> allAccountsRemnantUpperLimit(
             List<Account> accountList, double bottomLineMoney){
        List<Account> accounts = new ArrayList<>();

                for (Account account : accountList) {

                    if(account.getDigitalDollarBalance().doubleValue()>bottomLineMoney)
                        accounts.add(account);
                }


            return accounts;
    }

//    подсчитывает общий баланс всех участников
    public static double getAllBalance( List<Account> accountList){
        double allBalance = 0.0;

            for (Account account : accountList) {
                if(!account.getAccount().equals(Seting.BASIS_ADDRESS))
                    allBalance+=account.getDigitalDollarBalance().doubleValue();
            }


        return allBalance;
    }




}
