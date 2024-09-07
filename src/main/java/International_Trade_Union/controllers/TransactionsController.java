package International_Trade_Union.controllers;

import International_Trade_Union.entity.DataForTransaction;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBalance;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import International_Trade_Union.utils.UtilsFileSaveRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);

    }

    private static int fromBlock = 0;
    private static int toBlock = BasisController.getBlockchainSize();
    private static boolean isSentTransactions = true;

    @RequestMapping(method = RequestMethod.GET, value = "/transactions")
    public String transactions(Model model) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        if (BasisController.isUpdating() || BasisController.isMining()) {
            return "redirect:/processUpdating";
        }

        String account = UtilsFileSaveRead.read(Seting.ORIGINAL_ACCOUNT);
        System.out.println("BlockchainCheckController: checkValidation: size: " + BasisController.getBlockchainSize());

        int blockchainSize = BasisController.getBlockchainSize();

        // Ограничиваем количество блоков до 100
        if (toBlock - fromBlock > 100) {
            fromBlock = Math.max(toBlock - 100, 0);
        }

        // Проверяем границы
        if (toBlock > blockchainSize) {
            toBlock = blockchainSize;
        }

        if (fromBlock < 0) {
            fromBlock = 0;
        }

        if (fromBlock >= toBlock) {
            fromBlock = Math.max(0, toBlock - 100);
        }

        if(fromBlock == 0 && toBlock == 0){
            fromBlock = BasisController.getBlockchainSize() -100;
            toBlock = BasisController.getBlockchainSize();
        }
        // Загружаем блоки сразу, от fromBlock до toBlock
        List<Block> blocks = UtilsBlockToEntityBlock.entityBlocksToBlocks(blockService.findBySpecialIndexBetween(fromBlock, toBlock));
        List<DataForTransaction> transactions = new ArrayList<>();

        for (Block block : blocks) {
            List<DtoTransaction> temporary = block.getDtoTransactions();
            for (DtoTransaction dtoTransaction : temporary) {
                if (isSentTransactions && dtoTransaction.getSender().equals(account)) {
                    DataForTransaction transaction = new DataForTransaction(
                            (int) block.getIndex(),
                            dtoTransaction.getSender(),
                            dtoTransaction.getCustomer(),
                            dtoTransaction.getDigitalDollar(),
                            dtoTransaction.getDigitalStockBalance(),
                            dtoTransaction.getBonusForMiner(),
                            dtoTransaction.getVoteEnum().toString()
                    );
                    transactions.add(transaction);
                } else if (!isSentTransactions && dtoTransaction.getCustomer().equals(account)) {
                    DataForTransaction transaction = new DataForTransaction(
                            (int)block.getIndex(),
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
                .sorted(Comparator.comparing(DataForTransaction::getNumberBlock).reversed())
                .collect(Collectors.toList());

        // Добавляем атрибуты в модель
        model.addAttribute("title", "transactions account");
        model.addAttribute("transactions", transactions);
        model.addAttribute("fromBlock", fromBlock);
        model.addAttribute("toBlock", toBlock);
        model.addAttribute("size", BasisController.getBlockchainSize());
        return "transactions";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transactions")
    public String transactions(@RequestParam int from, int to,
                               @RequestParam boolean isSent) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        System.out.println("from: " + from);
        System.out.println("to: " + to);
        int blockchainSize = BasisController.getBlockchainSize();

        // Ограничиваем количество блоков до 100
        if (to - from > 100) {
            from = Math.max(to - 100, 0);
        }

        if (to > blockchainSize) {
            to = blockchainSize - 1;
        }

        fromBlock = from;
        toBlock = to;
        isSentTransactions = isSent;

        return "redirect:/transactions";
    }
}
