package unitted_states_of_mankind;

import International_Trade_Union.utils.UtilUrl;
import org.json.JSONException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@SpringBootTest
public class TestConductorController {

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
