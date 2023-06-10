package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DataForTransaction;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsFileSaveRead;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TransactionsController {
    private static Blockchain blockchain;
    static {
        try {
            blockchain= Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private static int fromBlock = 0;
    private static int toBlock = blockchain.sizeBlockhain();
    private static boolean isSentTransactions = true;

    @RequestMapping(method = RequestMethod.GET, value = "/transactions")
    public String transactions(Model model) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        blockchain= Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        String account = UtilsFileSaveRead.read(Seting.ORIGINAL_ACCOUNT);
        System.out.println("BlockchainCheckController: checkValidation: size: " + blockchain.sizeBlockhain());
        List<DataForTransaction> transactions = new ArrayList<>();
        if(toBlock > blockchain.sizeBlockhain()){
            toBlock = blockchain.sizeBlockhain();
        }
        if(fromBlock < 0)
            fromBlock = 0;

        if(fromBlock >= toBlock){
            fromBlock = 0;
            toBlock = blockchain.sizeBlockhain();
        }

        for (int i = fromBlock; i < toBlock ; i++) {
            List<DtoTransaction> temporary =  blockchain.getBlock(i).getDtoTransactions();
            for (DtoTransaction dtoTransaction : temporary) {
                if(isSentTransactions && dtoTransaction.getSender().equals(account)){
                    DataForTransaction transaction = new DataForTransaction(
                            i,
                            dtoTransaction.getSender(),
                            dtoTransaction.getCustomer(),
                            dtoTransaction.getDigitalDollar(),
                            dtoTransaction.getDigitalStockBalance(),
                            dtoTransaction.getBonusForMiner(),
                            dtoTransaction.getVoteEnum().toString()

                    );

                    transactions.add(transaction);
                }
                else if(!isSentTransactions && dtoTransaction.getCustomer().equals(account)){
                    DataForTransaction transaction = new DataForTransaction(
                            i,

                            dtoTransaction.getSender(),
                            dtoTransaction.getCustomer(),
                            dtoTransaction.getDigitalDollar(),
                            dtoTransaction.getDigitalStockBalance(),
                            dtoTransaction.getBonusForMiner(),
                            dtoTransaction.getVoteEnum().toString()
                    );
                    transactions.add(transaction);
                }
            }

        }

        model.addAttribute("title", "transactions account");
        model.addAttribute("transactions", transactions);
        return "transactions";

    }

    @RequestMapping(method = RequestMethod.POST, value = "/transactions")
    public String transactions(@RequestParam int from, int to,
                               @RequestParam boolean isSent) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        blockchain= Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        System.out.println("from: " + from);
        System.out.println("to: " + to);
        System.out.println("isSent: " + isSent);
        fromBlock = from;
        toBlock = to;
        isSentTransactions = isSent;
        return "redirect:/transactions";
    }
}
