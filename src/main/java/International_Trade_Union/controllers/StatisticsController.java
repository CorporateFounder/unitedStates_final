package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.statistics.Periud;
import International_Trade_Union.statistics.Statistic;
import International_Trade_Union.statistics.UtilsStatistics;
import International_Trade_Union.utils.UtilsBalance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatisticsController {
    private static Periud periud = International_Trade_Union.statistics.Periud.DAY;

    @PostMapping("/statistics")
    public String setStatistics(@RequestParam String periudStr, RedirectAttributes redirectAttrs) {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }

        periud = Periud.valueOf(periudStr);
        redirectAttrs.addAttribute("title statistics: " + periud);
        return "redirect:/statistics";
    }

    @GetMapping("/statistics")
    public String getStatistics(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        System.out.println("start statistics method");
        Map<String, Account> balances = new HashMap<>();
        List<Statistic> statistics = new ArrayList<>();
        List<String> signs = new ArrayList<>();
        for (Block block : blockchain.getBlockchainList()) {

            balances = UtilsBalance.calculateBalance(balances, block, signs);
            if (block.getIndex() > 576 && block.getIndex() % periud.getPeriud() == 0) {
                System.out.println("per index: " + block.getIndex() + ":"
                +(block.getIndex()-periud.getPeriud()) + ": " + block.getIndex()+1);
                List<Block> tempBlocks = blockchain.subBlock(
                        (int) (block.getIndex()- periud.getPeriud()),
                        (int) block.getIndex()+1);
                Statistic statistic = UtilsStatistics.statistic(tempBlocks, balances, periud);
                statistics.add(statistic);
            }
        }

        statistics = statistics.stream()
                .sorted(Comparator.comparing(Statistic::getIndexBlock))
                .collect(Collectors.toList());
        model.addAttribute("title statistics: " + periud);
        model.addAttribute("statistics", statistics);
        return "statistics";
    }
}
