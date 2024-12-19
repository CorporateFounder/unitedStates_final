package International_Trade_Union.controllers;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MineController {
    private static int computers = 1;
    @Autowired
    BlockService blockService;

    /**
     * Отправляет в страницу майнинга.
     * Sends to the mining page.
     */
    @RequestMapping("/mining")
    public String miming(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if (BasisController.isUpdating() || BasisController.isMining()) {
            return "redirect:/processUpdating";
        }

        model.addAttribute("title", "Corporation International Trade Union.");
        model.addAttribute("customDiff", Mining.getCustomDiff());
        return "mining";
    }

    @PostMapping("/customDiff")
    public String changeDiff(@RequestParam String customDiff) {
        Mining.setCustomDiff(Integer.valueOf(customDiff));
        return "redirect:/mining";
    }

    @PostMapping("/staking")
    public String staking(@RequestParam
                          String miner,
                          Double dollar,
                          @RequestParam(defaultValue = "0.0") Double reward,
                          String password,
                          RedirectAttributes redirectAttrs) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, SignatureException, IOException, InvalidKeyException {
        System.out.println("start staking controller");

        System.out.println("start post /miningTransaction");
        Base base = new Base58();

        dollar = UtilsUse.truncateAndRound(BigDecimal.valueOf(dollar)).doubleValue();
        reward = UtilsUse.truncateAndRound(BigDecimal.valueOf(reward)).doubleValue();
        reward = 0.0;
        if (dollar == null || dollar <  Seting.MINIMUM_2)
            dollar = 0.0;



        Laws laws = new Laws();
        laws.setLaws(new ArrayList<>());
        laws.setHashLaw("");
        laws.setPacketLawName("");


        DtoTransaction dtoTransaction = new DtoTransaction(
                miner,
                miner,
                dollar,
                0.0,
                laws,
                reward,
                VoteEnum.STAKING);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());


        redirectAttrs.addFlashAttribute("title", "sending result!!!");
        redirectAttrs.addFlashAttribute("sender", miner);
        redirectAttrs.addFlashAttribute("recipient", miner);
        redirectAttrs.addFlashAttribute("dollar", dollar);
        redirectAttrs.addFlashAttribute("stock", 0.0);
        redirectAttrs.addFlashAttribute("reward", reward);
        redirectAttrs.addFlashAttribute("vote", VoteEnum.STAKING);

        dtoTransaction.setSign(sign);
//        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", base.encode(dtoTransaction.getSign()));
        Directors directors = new Directors();
        if (dtoTransaction.verify()) {
            Account senderAccount = null;
            try{
                senderAccount = UtilsAccountToEntityAccount.entityAccountToAccount(blockService.findByAccount(miner));

            }catch (Exception e){
                redirectAttrs.addFlashAttribute("sending", "wrong transaction, address");
            }
            if(!"success".equals(UtilsUse.checkSendBalance(senderAccount, dtoTransaction))){
                String str = UtilsUse.checkSendBalance(senderAccount, dtoTransaction);
                redirectAttrs.addFlashAttribute("sending", str);

                return "redirect:/result-sending";
            }

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным, только когда
            //отправитель совпадает с законом.
            //if the title of the law coincides with corporate positions, then the law is valid only when
            //sender matches the law.
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t -> t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if (corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(miner, laws)) {
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
            String str = base.encode(dtoTransaction.getSign());
            System.out.println("sign: " + str);
            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s + "/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if (!Seting.IS_TEST && BasisController.getExcludedAddresses().contains(url)) {
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                } catch (Exception e) {
                    System.out.println("exception discover: " + original);

                }
            }


        } else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";


    }

    @PostMapping("/unstaking")
    public String unstaking(@RequestParam
                            String miner,
                            Double dollar,
                            @RequestParam(defaultValue = "0.0") Double reward,
                            String password,
                            RedirectAttributes redirectAttrs) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        System.out.println("start staking controller");

        System.out.println("start post /miningTransaction");
        Base base = new Base58();

        dollar = UtilsUse.truncateAndRound(BigDecimal.valueOf(dollar)).doubleValue();
        reward = UtilsUse.truncateAndRound(BigDecimal.valueOf(reward)).doubleValue();

        reward = 0.0;
        if (dollar == null || dollar < Seting.MINIMUM_2)
            dollar = 0.0;



        Laws laws = new Laws();
        laws.setLaws(new ArrayList<>());
        laws.setHashLaw("");
        laws.setPacketLawName("");
        DtoTransaction dtoTransaction = new DtoTransaction(
                miner,
                miner,
                dollar,
                0.0,
                laws,
                reward,
                VoteEnum.UNSTAKING);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());


        redirectAttrs.addFlashAttribute("title", "sending result!!!");
        redirectAttrs.addFlashAttribute("sender", miner);
        redirectAttrs.addFlashAttribute("recipient", miner);
        redirectAttrs.addFlashAttribute("dollar", dollar);
        redirectAttrs.addFlashAttribute("stock", 0.0);
        redirectAttrs.addFlashAttribute("reward", reward);
        redirectAttrs.addFlashAttribute("vote", VoteEnum.UNSTAKING);

        dtoTransaction.setSign(sign);
//        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", base.encode(dtoTransaction.getSign()));
        Directors directors = new Directors();
        if (dtoTransaction.verify()) {
            Account senderAccount = null;
            try{
                senderAccount = UtilsAccountToEntityAccount.entityAccountToAccount(blockService.findByAccount(miner));

            }catch (Exception e){
                redirectAttrs.addFlashAttribute("sending", "wrong transaction, address");
            }
            if(!"success".equals(UtilsUse.checkSendBalance(senderAccount, dtoTransaction))){
                String str = UtilsUse.checkSendBalance(senderAccount, dtoTransaction);
                redirectAttrs.addFlashAttribute("sending", str);

                return "redirect:/result-sending";
            }


            //если в названия закона совпадает с корпоративными должностями, то закон является действительным, только когда
            //отправитель совпадает с законом.
            //if the title of the law coincides with corporate positions, then the law is valid only when
            //sender matches the law.
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t -> t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if (corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(miner, laws)) {
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
            String str = base.encode(dtoTransaction.getSign());
            System.out.println("sign: " + str);
            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s + "/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if (!Seting.IS_TEST && BasisController.getExcludedAddresses().contains(url)) {
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                } catch (Exception e) {
                    System.out.println("exception discover: " + original);

                }
            }

        } else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";

    }

}
