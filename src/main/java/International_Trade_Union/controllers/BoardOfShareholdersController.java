package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Mining;
import International_Trade_Union.utils.SaveBalances;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class BoardOfShareholdersController {

    /**отображает список Совета Акционеров, отображается в браузере.
     * displays a list of the Board of Shareholders, displayed in the browser.*/
    @GetMapping("board-of-shareholders")
    public String boardOfShareHolders(Model model) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }


        List<Block> blocksList = UtilsBlockToEntityBlock.entityBlocksToBlocks(
                BlockService.findBySpecialIndexBetween(
                        BasisController.getBlockchainSize() - Seting.LAW_YEAR_VOTE,
                        BasisController.getBlockchainSize() -1
                )
        );

        //Получение баланса.
        //Get the balance.
        Map<String, Account> balances = new HashMap<>();
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        List<Account> boardOfShareholders = new ArrayList<>();
        boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blocksList, Seting.BOARDS_BLOCK);

        model.addAttribute("title", "Board of shareholders quantity: " + boardOfShareholders.size());
        model.addAttribute("boardOfShareholders", boardOfShareholders);
        return "board-of-shareholders";
    }


}
