package unitted_states_of_mankind;

import International_Trade_Union.controllers.AccountController;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilUrl;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsSecurity;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import org.json.JSONException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
public class TestAccountController {

    @Test
    @ResponseBody
    public void keysTest(){
        String json = "";
        try {
            json = UtilUrl.readJsonFromUrl("http://localhost:8082/keys");

        }catch (IOException | JSONException e){
            System.out.println("error test mining");

        }
        System.out.println(json);
    }

    @Test
    @ResponseBody
    public void accountTest(){
        String json = "";
        try {
            json = UtilUrl.readJsonFromUrl("http://localhost:8082/account");

        }catch (IOException | JSONException e){
            System.out.println("error test mining");

        }
        System.out.println(json);
    }

    @Test
    @ResponseBody
    public void dollarTest(){
        String json = "";
        try {
            json = UtilUrl.readJsonFromUrl("http://localhost:8082/dollar");

        }catch (IOException | JSONException e){
            System.out.println("error test mining");

        }
        System.out.println(json);
    }

    @Test
    @ResponseBody
    public void stockTest(){
        String json = "";
        try {
            json = UtilUrl.readJsonFromUrl("http://localhost:8082/stock");

        }catch (IOException | JSONException e){
            System.out.println("error test mining");

        }
        System.out.println(json);
    }

    @Test
    @ResponseBody
    public void sendCoinTest(){
        String json = "";
        try {
            json = UtilUrl.readJsonFromUrl("http://localhost:8082/sendCoin");

        }catch (IOException | JSONException e){
            System.out.println("error test mining");

        }
        System.out.println(json);
    }

    @Test
    @ResponseBody
    public void isTransactionAddTest() {
        String json = "";
        try {
            json = UtilUrl.readJsonFromUrl("http://localhost:8082/isTransactionAdd");

        } catch (IOException | JSONException e) {
            System.out.println("error test mining");

        }
        System.out.println(json);
    }

}
