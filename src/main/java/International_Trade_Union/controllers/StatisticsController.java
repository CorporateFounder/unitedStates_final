package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.statistics.Periud;
import International_Trade_Union.statistics.Statistic;
import International_Trade_Union.statistics.UtilsStatistics;
import International_Trade_Union.utils.UtilsAccountToEntityAccount;
import International_Trade_Union.utils.UtilsBalance;
import International_Trade_Union.utils.UtilsBlockToEntityBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;
/**Статистика. Statistic.*/

@Controller
public class StatisticsController {
    @Autowired
    BlockService blockService;
    private static Periud periud = International_Trade_Union.statistics.Periud.DAY;

    @PostMapping("/statistics")
    public String setStatistics(@RequestParam String periudStr, RedirectAttributes redirectAttrs) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {

        periud = Periud.valueOf(periudStr);
        redirectAttrs.addAttribute("title statistics: " + periud);
        return "redirect:/statistics";
    }

    @GetMapping("/statistics")
    public String getStatistics(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }



        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        Statistic prevStatistic = null;
        System.out.println("start statistics method");
        Map<String, Account> balances = new HashMap<>();
        List<Statistic> statistics = new ArrayList<>();
        List<String> signs = new ArrayList<>();
        for (Block block : blockchain.getBlockchainList()) {
            System.out.println("index: " + block.getIndex());
//            balances = UtilsBalance.calculateBalance(balances, block, signs);
            balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());

            if (block.getIndex() > 576 && block.getIndex() % periud.getPeriud() == 0) {
                System.out.println("per index: " + block.getIndex() + ":"
                +(block.getIndex()-periud.getPeriud()) + ": " + block.getIndex()+1);
                List<Block> tempBlocks = blockchain.subBlock(
                        (int) (block.getIndex()- periud.getPeriud()),
                        (int) block.getIndex()+1);
                Statistic statistic = UtilsStatistics.statistic(tempBlocks, balances, periud);
                if (prevStatistic == null || prevStatistic.getUniqueAddressBalanceUpperZero() == 0){
                    statistic.setGrowthAccountPercent(0);
                }else {
                    double percnet = ((double) statistic.getUniqueAddressBalanceUpperZero() -
                            prevStatistic.getUniqueAddressBalanceUpperZero()) /
                            (double) prevStatistic.getUniqueAddressBalanceUpperZero() * Seting.HUNDRED_PERCENT;
                    statistic.setGrowthAccountPercent(percnet);
                }
                statistics.add(statistic);

                prevStatistic = statistic;
            }
        }

        statistics = statistics.stream()
                .sorted(Comparator.comparing(Statistic::getIndexBlock))
                .collect(Collectors.toList());

        statistics = statistics.stream()
                .sorted(Comparator.comparing(Statistic::getIndexBlock).reversed())
                .collect(Collectors.toList());
        model.addAttribute("title statistics: " + periud);
        model.addAttribute("statistics", statistics);
        System.out.println("blockchain size: " + blockchain.getBlockchainList().size());
        return "statistics";
    }

    @GetMapping("/percent")
    @ResponseBody
    public List<String> percent() throws IOException {
        List<EntityBlock> entityBlocks = blockService.findBySpecialIndexBetween(BasisController.getBlockchainSize() - 576, BasisController.getBlockchainSize());

        Map<String, Integer> addressCountMap = new HashMap<>();
        Map<String, Long> addressComplexityMap = new HashMap<>();
        Map<String, Map<Long, Integer>> complexityFrequencyMap = new HashMap<>();

        for (EntityBlock block : entityBlocks) {
            String minerAddress = block.getMinerAddress();
            long blockComplexity = block.getHashCompexity();

            addressCountMap.put(minerAddress, addressCountMap.getOrDefault(minerAddress, 0) + 1);
            addressComplexityMap.put(minerAddress, addressComplexityMap.getOrDefault(minerAddress, 0L) + blockComplexity);

            Map<Long, Integer> frequencyMap = complexityFrequencyMap.getOrDefault(minerAddress, new HashMap<>());
            frequencyMap.put(blockComplexity, frequencyMap.getOrDefault(blockComplexity, 0) + 1);
            complexityFrequencyMap.put(minerAddress, frequencyMap);
        }

        int totalBlocks = entityBlocks.size();
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(addressCountMap.entrySet());

        entryList.sort((e1, e2) -> {
            double percentage1 = (e1.getValue() * 100.0) / totalBlocks;
            double percentage2 = (e2.getValue() * 100.0) / totalBlocks;
            return Double.compare(percentage2, percentage1);
        });

        List<String> result = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : entryList) {
            String address = entry.getKey();
            int count = entry.getValue();
            double percentage = (count * 100.0) / totalBlocks;
            long totalComplexity = addressComplexityMap.get(address);

            Map<Long, Integer> frequencyMap = complexityFrequencyMap.get(address);
            long modeComplexity = -1;
            int maxFrequency = 0;
            for (Map.Entry<Long, Integer> freqEntry : frequencyMap.entrySet()) {
                if (freqEntry.getValue() > maxFrequency) {
                    maxFrequency = freqEntry.getValue();
                    modeComplexity = freqEntry.getKey();
                }
            }

            result.add(String.format("address: %s - blocks: %d - percent: %.2f%% - difficult: %d (moda: %d)", address, count, percentage, totalComplexity, modeComplexity));
        }

        return result;
    }



}
