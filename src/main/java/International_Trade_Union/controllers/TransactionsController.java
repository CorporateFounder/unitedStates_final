package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DataForTransaction;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import International_Trade_Union.utils.UtilsFileSaveRead;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TransactionsController {

   @Autowired
    BlockService blockService;

    private static int fromBlock = 0;
    private static int toBlock = BasisController.getBlockchainSize();
    private static boolean isSentTransactions = true;

    @RequestMapping(method = RequestMethod.GET, value = "/transactions")
    public String transactions(Model model) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }



        String account = UtilsFileSaveRead.read(Seting.ORIGINAL_ACCOUNT);
        System.out.println("BlockchainCheckController: checkValidation: size: " + BasisController.getBlockchainSize());
        List<DataForTransaction> transactions = new ArrayList<>();
        if(toBlock > BasisController.getBlockchainSize()){
            toBlock = BasisController.getBlockchainSize();
        }
        if(fromBlock < 0)
            fromBlock = 0;

        if(fromBlock >= toBlock){
            fromBlock = 0;
            toBlock = BasisController.getBlockchainSize()-1;
        }

        for (int i = fromBlock; i < toBlock ; i++) {
            List<DtoTransaction> temporary =  UtilsBlockToEntityBlock.entityBlockToBlock(blockService.findBySpecialIndex(i)).getDtoTransactions();
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
        transactions = transactions.stream()
                .sorted(Comparator.comparing(DataForTransaction::getNumberBlock).reversed()).collect(Collectors.toList());
        model.addAttribute("title", "transactions account");
        model.addAttribute("transactions", transactions);
        return "transactions";

    }

    @RequestMapping(method = RequestMethod.POST, value = "/transactions")
    public String transactions(@RequestParam int from, int to,
                               @RequestParam boolean isSent) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {


        System.out.println("from: " + from);
        System.out.println("to: " + to);
        if(to > BasisController.getBlockchainSize()){
            to = BasisController.getBlockchainSize()-1;
        }
        System.out.println("isSent: " + isSent);
        fromBlock = from;
        toBlock = to;
        isSentTransactions = isSent;
        return "redirect:/transactions";
    }
}
