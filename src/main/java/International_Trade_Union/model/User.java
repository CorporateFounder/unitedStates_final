package International_Trade_Union.model;

import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsFileSaveRead;

import java.io.FileNotFoundException;


public class User {
    private static String addressMiner="";

    public static void setUserAddress(String addressMiner) {
//        System.out.println("change minerAccount: " + addressMiner);
//        UtilsFileSaveRead.save(addressMiner, Seting.ORIGINAL_ACCOUNT, false);
        User.addressMiner = addressMiner;
    }

    public static String getUserAddress() throws FileNotFoundException {
        addressMiner = UtilsFileSaveRead.read(Seting.ORIGINAL_ACCOUNT);
        System.out.println("user: " + addressMiner);
        if(addressMiner.isEmpty() || addressMiner == null)
            addressMiner = "empty ";
        return addressMiner;
    }
}
