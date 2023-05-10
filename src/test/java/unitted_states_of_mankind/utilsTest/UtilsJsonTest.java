package unitted_states_of_mankind.utilsTest;




import International_Trade_Union.vote.Laws;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.model.Account;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
public class UtilsJsonTest {
    private Account sender = new Account("germes",10000.0);
    private Account customer = new Account("mercury",10000.0);

    @Test
    public void objToStringJson() throws IOException {
        Laws laws = new Laws("test", new ArrayList<>());

        DtoTransaction expected = new DtoTransaction(
                sender.getAccount(),  customer.getAccount(),10000.0, 5000,
                laws, 1.0, VoteEnum.YES);
        try{
           String strObj = UtilsJson.objToStringJson(expected);
           DtoTransaction afterjson =  UtilsJson.jsonToDtoTransaction(strObj);
            Assert.assertEquals(expected, afterjson);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
