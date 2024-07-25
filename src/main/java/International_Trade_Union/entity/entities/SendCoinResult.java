package International_Trade_Union.entity.entities;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SendCoinResult {
    DtoTransaction dtoTransaction;
    Map<String, String> statusServer;
    String sign;

    public SendCoinResult() {
        statusServer = new HashMap<>();
    }

    public void put(String str, String status){
        statusServer.put(str, status);
    }
}
