package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.CreateAccount;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.User;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.SaveBalances;
import International_Trade_Union.utils.UtilsBalance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@Controller
public class AccountController {
    @GetMapping("keys")
    public Map<String, String> keys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return CreateAccount.create();
    }

    @GetMapping("account")
    public Account account(@RequestBody String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
         Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account account = UtilsBalance.getBalance(address, balances);
        return account;
    }

    @GetMapping("dollar")
    public double dollar(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account account = UtilsBalance.getBalance(address, balances);
        return account.getDigitalDollarBalance();
    }

    @GetMapping("stock")
    public double stock(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account account = UtilsBalance.getBalance(address, balances);
        return account.getDigitalStockBalance();
    }
}
