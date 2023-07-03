package International_Trade_Union.controllers;

import International_Trade_Union.about_us_engDraft.AboutUsEngDraft;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Mining;
import International_Trade_Union.originalCorporateCharter.OriginalPreamble;
import International_Trade_Union.originalCorporateCharter.OriginalPreambleEng;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.User;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;


import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.http.WebSocket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private static int globalSize = 0;

    public static int getGlobalSize() {
        return globalSize;
    }

    public static void setGlobalSize(int globalSize) {
        MainController.globalSize = globalSize;
    }

    static {
       try {
           UtilsCreatedDirectory.createPackages();
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }
    @GetMapping("/")
    public String home(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        String sizeStr = "-1";
        try {
            sizeStr = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/size");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }catch (SocketException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }
        Integer sizeG = Integer.valueOf(sizeStr);
        globalSize = sizeG;
        model.addAttribute("title", "Corporation International Trade Union.");
        if(sizeStr.isEmpty() || sizeStr.isBlank() || sizeStr.equals("-1")){
            model.addAttribute("globalSize", "global server data was not received because the server was unable to connect: " + sizeStr);
        }else {
            model.addAttribute("globalSize", Integer.toString(globalSize));
        }


        String versionStr = "-1";
        try {
            versionStr = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/version");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give version global server");
            versionStr = "-1";
        }catch (SocketException e){
            System.out.println("," +
                    "you can't give version global server");
            versionStr = "-1";
        }

        model.addAttribute("global_version", versionStr);

        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        int size = blockchain.sizeBlockhain();
        Map<String, Account> balances = new HashMap<>();

        model.addAttribute("size", size);
        model.addAttribute("version", Seting.VERSION);
        boolean validation = blockchain.validatedBlockchain();

        if(validation == false){
            System.out.println("deleted blockchain files");

//            System.exit(1);
            UtilsBlock.deleteFiles();
        }
        model.addAttribute("validation", validation);


        //догрузить блокчейн
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);


        Account account = UtilsBalance.getBalance(User.getUserAddress(), balances);
        model.addAttribute("account", account);

        return "home";
    }


    @PostMapping("/setMinner")
    public String setMinnerAddress(@RequestParam(value = "setMinner") String setMinner, RedirectAttributes redirectAttrs){
        System.out.println("MainController:  " + setMinner);
        UtilsFileSaveRead.save(setMinner, Seting.ORIGINAL_ACCOUNT, false);
        return "redirect:/seting";
    }



    @GetMapping("about")
    public String aboutUs(Model model){
        model.addAttribute("title", "ABOUT US");
        model.addAttribute("eng", OriginalPreambleEng.ARTICLE_0);
        model.addAttribute("rus", OriginalPreamble.ARTICLE_0);
        return "about";
    }
    @GetMapping("result-sending")
    public String resultSending(Model model){

        return "result-sending";
    }

    @PostMapping("/")
    public String new_transaction(
            @RequestParam  String sender,
            @RequestParam  String recipient,
                                   Double dollar,
                                   Double stock,
                                   Double reward,

                                  @RequestParam  String password,
                                  RedirectAttributes redirectAttrs) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();

        if(dollar == null)
            dollar = 0.0;

        if(stock == null)
            stock = 0.0;

        if(reward == null)
            reward = 0.0;

        Laws laws =  new Laws();
        laws.setLaws(new ArrayList<>());
        laws.setHashLaw("");
        laws.setPacketLawName("");
        DtoTransaction dtoTransaction = new DtoTransaction(
                sender,
                recipient,
                dollar,
                stock,
                laws,
                reward,
                VoteEnum.YES);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
        System.out.println("Main Controller: new transaction: vote: " +VoteEnum.YES);
        redirectAttrs.addFlashAttribute("title", "sending result!!!");
        redirectAttrs.addFlashAttribute("sender", sender);
        redirectAttrs.addFlashAttribute("recipient", recipient);
        redirectAttrs.addFlashAttribute("dollar", dollar);
        redirectAttrs.addFlashAttribute("stock", stock);
        redirectAttrs.addFlashAttribute("reward", reward);
        redirectAttrs.addFlashAttribute("vote", VoteEnum.YES);
        dtoTransaction.setSign(sign);
        Directors directors = new Directors();
        if(dtoTransaction.verify()){

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t->t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if(corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)){
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
           String str =  base.encode(dtoTransaction.getSign());
            System.out.println("sign: " + str);
            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s +"/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if(BasisController.getExcludedAddresses().contains(url)){
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть
                    UtilUrl.sendPost(jsonDto, url);

                }catch (Exception e){
                    System.out.println("exception discover: " + original);

                }
            }



        }

        else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";
    }


    @GetMapping("/seting")
    public String seting(Model model){
        model.addAttribute("title", "Settings");
        return "seting";
    }


}
