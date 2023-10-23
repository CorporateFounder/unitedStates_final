package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityDtoTransaction;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.CreateAccount;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ConductorController {

    @Autowired
    BlockService blockService;
    /**
     * created account
     * Params: nothing
     * Body: nothing
     */
    @GetMapping("/keys")
    public Map<String, String> keys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        return CreateAccount.create();
    }


    /***/
    @GetMapping("/updating")
    @ResponseBody
    public Integer updating() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return BasisController.resolve();
    }

    /**
     * get find end get account
     */
    @GetMapping("/account")
    public Account account(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account account = UtilsBalance.getBalance(address, balances);

        return account;
    }

    /**
     * get dollar balance
     */
    @GetMapping("/dollar")
    public Double dollar(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account account = UtilsBalance.getBalance(address, balances);
        return account.getDigitalDollarBalance();
    }

    /**
     * get stock balance
     */
    @GetMapping("/stock")
    public Double stock(@RequestParam String address) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);
        Account account = UtilsBalance.getBalance(address, balances);
        return account.getDigitalStockBalance();
    }

    /**
     * send dollar or stock (if return wrong-its not sending if return sign its success)
     */
    @GetMapping("/sendCoin")
    public String send(@RequestParam String sender,
                       @RequestParam String recipient,
                       @RequestParam Double dollar,
                       @RequestParam Double stock,
                       @RequestParam Double reward,
                       @RequestParam String password) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        Base base = new Base58();
        String result = "wrong";
        if (dollar == null)
            dollar = 0.0;

        if (stock == null)
            stock = 0.0;

        if (reward == null)
            reward = 0.0;

        Laws laws = new Laws();
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
        System.out.println("Main Controller: new transaction: vote: " + VoteEnum.YES);
        dtoTransaction.setSign(sign);
        Directors directors = new Directors();
        System.out.println("sender: " + sender);
        System.out.println("recipient: " + recipient);
        System.out.println("dollar: " + dollar + ": class: " + dollar.getClass());
        System.out.println("stock: " + stock + ": class: " + stock.getClass());
        System.out.println("reward: " + reward + ": class: " + reward.getClass());
        System.out.println("password: " + password);
        System.out.println("sign: " + dtoTransaction.toSign());
        System.out.println("verify: " + dtoTransaction.verify());

        if (dtoTransaction.verify()) {

            //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t -> t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if (corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)) {
                System.out.println("sending" + "wrong transaction: Position to be equals whith send");
                return result;
            }
            result = dtoTransaction.toSign();

            String str = base.encode(dtoTransaction.getSign());
            System.out.println("sign: " + str);
            AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s + "/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if (BasisController.getExcludedAddresses().contains(url)) {
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
            return result;
        return result;

    }

    /**
     * whether the transaction was added to the blockchain, find with sign
     */
    @GetMapping("/isTransactionAdd")
    public Boolean isTransactionGet(@RequestParam String sign) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        boolean result = false;
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        for (Block block : blockchain.getBlockchainList()) {
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                if (dtoTransaction.toSign().equals(sign)) {
                    result = true;
                    return result;
                }
            }

        }
        return result;
    }

//    /**find block from index*/
//    @GetMapping("/block")
//    public Block block(@RequestParam int index) throws JsonProcessingException {
//        return Blockchain.indexFromFile(index, Seting.ORIGINAL_BLOCKCHAIN_FILE);
//    }

    /**find block from hash*/
    @GetMapping("/blockHash")
    public Block blockFromHash(@RequestParam String hash) throws JsonProcessingException {
        return Blockchain.hashFromFile(hash, Seting.ORIGINAL_BLOCKCHAIN_FILE);
    }



    @GetMapping("/conductorBlock")
    @ResponseBody
    public Block  block(@RequestParam Integer index) throws IOException {
        if(index < 0 ){
            index = 0;
        }
        if(index > BasisController.getBlockchainSize() -1){
            index = BasisController.getBlockchainSize() - 1;
        }
        return UtilsBlockToEntityBlock.entityBlockToBlock(
                BlockService.findBySpecialIndex(index)
        );
    }

    @GetMapping("/conductorHashTran")
    @ResponseBody
    public DtoTransaction transaction(@RequestParam String hash) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<EntityDtoTransaction> entityDtoTransactions =
                BlockService.findAllDto();
        EntityDtoTransaction entityDtoTransaction = null;
        try {
             entityDtoTransaction = BlockService.findBySign(hash);

            if(entityDtoTransaction == null){
                return null;
            }
        }catch (Exception e){
           return null;
        }

        return UtilsBlockToEntityBlock.entityToDto(entityDtoTransaction);
    }

}
