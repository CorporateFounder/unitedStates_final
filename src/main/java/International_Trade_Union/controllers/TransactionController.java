package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsFileSaveRead;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TransactionController {

    /**Добавляет транзакцию во внутренний файл.
     * Adds a transaction to an internal file.*/
    @RequestMapping(method = RequestMethod.POST, value = "/addTransaction", consumes = MediaType.APPLICATION_JSON_VALUE)
    public  void add(@RequestBody DtoTransaction data) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        System.out.println("add transaction: " + data);
        AllTransactions.getInstance();
        AllTransactions.addTransaction(data);
        System.out.println("TransactionController: add: " + AllTransactions.getInstance().size());
    }


}
