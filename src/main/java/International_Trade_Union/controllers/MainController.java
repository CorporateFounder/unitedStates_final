package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.InfoDificultyBlockchain;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.originalCorporateCharter.OriginalPreamble;
import International_Trade_Union.setings.originalCorporateCharter.OriginalPreambleEng;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
    @Autowired
    BasisController basisController;
    private static DataShortBlockchainInformation shortBlockchainInformation;
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
           String json = UtilsFileSaveRead.read(Seting.TEMPORARY_BLOCKCHAIN_FILE);
           if (json != null && !json.isEmpty())
            shortBlockchainInformation = UtilsJson.jsonToDataShortBlockchainInformation(json);
           if(shortBlockchainInformation == null){
               System.out.println("shortBlockchainInformation null");
               shortBlockchainInformation = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
               String stringJson = UtilsJson.objToStringJson(shortBlockchainInformation);
               UtilsFileSaveRead.save(stringJson, Seting.TEMPORARY_BLOCKCHAIN_FILE, false);
           }
       } catch (Exception e) {
           try {
               BasisController.resolve_conflicts();
           } catch (NoSuchAlgorithmException ex) {
               throw new RuntimeException(ex);
           } catch (InvalidKeySpecException ex) {
               throw new RuntimeException(ex);
           } catch (IOException ex) {
               throw new RuntimeException(ex);
           } catch (SignatureException ex) {
               throw new RuntimeException(ex);
           } catch (NoSuchProviderException ex) {
               throw new RuntimeException(ex);
           } catch (InvalidKeyException ex) {
               throw new RuntimeException(ex);
           } catch (JSONException ex) {
               throw new RuntimeException(ex);
           }
           throw new RuntimeException(e);
       }
    }
    @GetMapping("/")
    public String home(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }

        String address = "http://194.87.236.238:80";
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            address = s;
        }
        System.out.println("address: " + address);

        String stringShort = UtilsFileSaveRead.read(Seting.TEMPORARY_BLOCKCHAIN_FILE);
    if(stringShort != null && !stringShort.isEmpty())
        shortBlockchainInformation = UtilsJson.jsonToDataShortBlockchainInformation(stringShort);
        String sizeStr = "-1";
        try {
            sizeStr = UtilUrl.readJsonFromUrl(address + "/size");
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
            versionStr = UtilUrl.readJsonFromUrl(address + "/version");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give version global server");
            versionStr = "-1";
        }catch (SocketException e){
            System.out.println("," +
                    "you can't give version global server");
            versionStr = "-1";
        }

        InfoDificultyBlockchain infoDificultyBlockchain = new InfoDificultyBlockchain(-1, -1);
        String difficultOneBlock =  ":";
        String difficultAllBlockchain = ":";
        try {
            String json = UtilUrl.readJsonFromUrl(address + "/difficultyBlockchain");
             infoDificultyBlockchain = UtilsJson.jsonToInfoDifficulty(json);

        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give difficulty global server");

        }catch (SocketException e){
            System.out.println("," +
                    "you can't give difficulty global server");
        }

        difficultOneBlock = Long.toString(infoDificultyBlockchain.getDiffultyOneBlock());
        difficultAllBlockchain = Long.toString(infoDificultyBlockchain.getDifficultyAllBlockchain());


        model.addAttribute("global_version", versionStr);
        model.addAttribute("difficultOneBlock",difficultOneBlock);
        model.addAttribute("difficultAllBlockchain",  difficultAllBlockchain);
        Blockchain blockchain = null;
        int size = 0;
        boolean validation = false;
        Block block =  null;
        if(shortBlockchainInformation == null ||
         shortBlockchainInformation.isValidation() == false){
             blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
            size = blockchain.sizeBlockhain();
            validation = blockchain.validatedBlockchain();
            blockchain.getBlock(blockchain.sizeBlockhain()-1);
        }else {
            size = (int) shortBlockchainInformation.getSize();
            validation = shortBlockchainInformation.isValidation();
            if(size > 1)
                block = Blockchain.indexFromFile(size-1, Seting.ORIGINAL_BLOCKCHAIN_FILE);
            else {
                blockchain = Mining.getBlockchain(
                        Seting.ORIGINAL_BLOCKCHAIN_FILE,
                        BlockchainFactoryEnum.ORIGINAL);
                block =  blockchain.getBlock(blockchain.sizeBlockhain()-1);
            }

        }



        Map<String, Account> balances = new HashMap<>();

        model.addAttribute("size", size);
        model.addAttribute("version", Seting.VERSION);


        if(validation == false){
            System.out.println("deleted blockchain files");
            System.out.println("**************************************");
            System.out.println("wrong blockchain");
            System.out.println("**************************************");

//            System.exit(1);
//            UtilsBlock.deleteFiles();
        }
        model.addAttribute("validation", validation);


        //догрузить блокчейн
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);


        Account account = UtilsBalance.getBalance(User.getUserAddress(), balances);
        model.addAttribute("account", account);

        //дата сколько осталось до уничтожения монет

        model.addAttribute("info", "In this system, a year is 360 days, and a day is 576 blocks.");
        model.addAttribute("info2", "every 180 days, 0.2% of digital dollars and 0.4% of digital shares" +
                " are withdrawn from the account.");
        model.addAttribute("demerage", "now is the day: ");
        if(block.getIndex() != 0){
           int day = (int) (block.getIndex() / Seting.COUNT_BLOCK_IN_DAY % (Seting.YEAR / Seting.HALF_YEAR));
            model.addAttribute("demerage", "now is the day: " + day);
        }
//
//        Block.setMultiThread(true);

        return "home";
    }



    @PostMapping("/setPool")
    public String setPool(@RequestParam(value = "setPool") String setPool){
       int number = 10;

        try{
          number = Integer.valueOf(setPool);
          Block.setThreadCount(number);
       }catch(Exception e) {
            e.printStackTrace();
        }
        return "redirect:/seting";
    }
    @PostMapping("/setMinner")
    public String setMinnerAddress(@RequestParam(value = "setMinner") String setMinner, RedirectAttributes redirectAttrs){


        System.out.println("MainController:  " + setMinner);
        UtilsFileSaveRead.save(setMinner, Seting.ORIGINAL_ACCOUNT, false);
        return "redirect:/seting";
    }
    @PostMapping ("/setMinDollarRewards")
    public String setMinDollarRewards(@RequestParam(value = "reward") String reward){
        double number = 0;


        try{
            number = Double.valueOf(reward);
            BasisController.setMinDollarRewards(number);
        }catch(Exception e) {
            e.printStackTrace();
        }

        return "redirect:/seting";
    }


    @PostMapping("/changeMultiThread")
    public String changeMultiThread(@RequestParam("thread") boolean thread) {
        // Здесь можно добавить логику для изменения состояния многопоточности
        // Например, установить значение переменной или выполнить другие действия в зависимости от значения параметра "reward"

        // Возвращение перенаправления на главную страницу или другой URL
        Block.setMultiThread(thread);
        return "redirect:/seting"; // Замените "/" на ваш URL назначения
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

    //"@PostMapping("/") - its spring mapping
    //@RequestParam - its parametrs in url - example http://localhost:80?age=18&color=red;
    //RedirectAttributes = its instrument redirect parametr from one htmlt to senod
    //model = its instrument to binding
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
        String encoded = Base64.getEncoder().encodeToString(dtoTransaction.getSign());
        redirectAttrs.addFlashAttribute("sign", encoded);
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
//        model.addAttribute("reward", "the reward that
//        the miner must receive in order to add a transaction.: " +
//        BasisController.getMinDollarRewards());
        System.out.println("pool: " + Block.getThreadCount());
        System.out.println("multithread: " + Block.isMultiThread());

        model.addAttribute("minerRew", BasisController.getMinDollarRewards());
        model.addAttribute("pool",Block.getThreadCount());
        model.addAttribute("multithread", Block.isMultiThread());
        long startMiningNumber = Block.getRandomNumberProofStatic();
        if(Block.isMultiThread()){
            startMiningNumber += Block.getThreadCount() * Block.getIncrementValue();
        }
        model.addAttribute("number", startMiningNumber);
        return "seting";
    }


}
