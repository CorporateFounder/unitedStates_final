package International_Trade_Union.controllers;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.network.AllTransactions;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class TransactionController {
    @RequestMapping(method = RequestMethod.POST, value = "/addTransaction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public  void add(@RequestBody DtoTransaction data) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        System.out.println("add transaction: " + data);
        AllTransactions.getInstance();
        AllTransactions.addTransaction(data);
        System.out.println("TransactionController: add: " + AllTransactions.getInstance().size());
    }

}
