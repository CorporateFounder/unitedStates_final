package International_Trade_Union.controllers.WebController;

import International_Trade_Union.entity.InfoDificultyBlockchain;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilUrl;
import International_Trade_Union.utils.UtilsJson;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;

@RestController
public class StorageController {
    /**
     * Returns the global size of the blockchain.
     * http://194.87.236.238:80/size
     *
     * Возвращает глобальный размер блокчейна.
     * http://194.87.236.238:80/size*/
    @GetMapping("/globalSize")
    @ResponseBody
    public int globalSize(){

        String address = "http://194.87.236.238:80";
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            address = s;
        }
        String sizeStr = "-1";
        try {
            sizeStr = UtilUrl.readJsonFromUrl(address + "/size");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }catch (IOException | JSONException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }

        return Integer.valueOf(sizeStr);
    }

    /**
     * Returns the repository version.
     * If the version of the local wallet does not match the version of the global version,
     * that storage is not allowed to be mined.
     * http://194.87.236.238:80/version
     *
     * Возвращает версию хранилища.
     * Если версия локального кошелька не совпадает с версией глобальной версией,
     * то хранилище не позволить добывать.
     * http://194.87.236.238:80/version */
    @GetMapping("/globalVersion")
    @ResponseBody
    public int globalVersion(){
        String address = "http://194.87.236.238:80";
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            address = s;
        }
        String versionStr = "-1";
        try {
            versionStr = UtilUrl.readJsonFromUrl(address + "/version");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give version global server");
            versionStr = "-1";
        }catch (IOException | JSONException e){
            System.out.println("," +
                    "you can't give version global server");
            versionStr = "-1";
        }
        return Integer.valueOf(versionStr);
    }

    /**
     * Returns the current block difficulty that the store accepts.
     * Also returns the complexity of the entire blockchain.
     * http://194.87.236.238:80/difficultyBlockchain
     *
     * Возвращает текущую сложность блока которую принимает хранилище.
     * Также Возвращает сложность всего блокчейна.
     * http://194.87.236.238:80/difficultyBlockchain */
    @GetMapping("/difficulty")
    @ResponseBody
    public InfoDificultyBlockchain infoDificultyBlockchain(){
        InfoDificultyBlockchain infoDificultyBlockchain = new InfoDificultyBlockchain(-1, -1);
        String difficultOneBlock =  ":";
        String difficultAllBlockchain = ":";

        String address = "http://194.87.236.238:80";
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            address = s;
        }
        try {
            String json = UtilUrl.readJsonFromUrl(address + "/difficultyBlockchain");
            infoDificultyBlockchain = UtilsJson.jsonToInfoDifficulty(json);

        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give difficulty global server");
            return infoDificultyBlockchain;

        }catch (IOException | JSONException e){
            System.out.println("," +
                    "you can't give difficulty global server");
            return infoDificultyBlockchain;
        }
        return infoDificultyBlockchain;
    }
}
