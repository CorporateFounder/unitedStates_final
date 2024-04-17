package International_Trade_Union.utils;

import International_Trade_Union.setings.Seting;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;

public class UtilsStorage {
    public static int getSize()  {
        String address = "http://194.87.236.238:82";
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            address = s;
        }

        String sizeStr = "-1";
        try {
            String server = UtilsFileSaveRead.read(Seting.YOUR_SERVER);
            if (!server.isEmpty() && !server.isBlank()){
                address = server;
            }
            sizeStr = UtilUrl.readJsonFromUrl(address+ "/size");
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
