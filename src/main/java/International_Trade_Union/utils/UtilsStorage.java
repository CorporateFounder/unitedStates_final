package International_Trade_Union.utils;

import org.json.JSONException;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;

public class UtilsStorage {
    public static int getSize(){
        String sizeStr = "-1";
        try {
            sizeStr = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/size");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }catch (IOException | JSONException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }
        Integer sizeG = Integer.valueOf(sizeStr);
        return sizeG;
    }
}
