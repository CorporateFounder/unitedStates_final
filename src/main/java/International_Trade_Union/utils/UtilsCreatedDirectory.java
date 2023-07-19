package International_Trade_Union.utils;

import International_Trade_Union.setings.Seting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UtilsCreatedDirectory {
    public static void createPackages() throws IOException {
        List<String> files = new ArrayList<>();

        files.add(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        files.add(Seting.ORIGINAL_BALANCE_FILE);

        files.add(Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);

        files.add(Seting.ORIGINAL_CORPORATE_VOTE_FILE);
        files.add(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        files.add(Seting.ORGINAL_ALL_TRANSACTION_FILE);
        files.add(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        files.add(Seting.ORIGINAL_CORPORATE_VOTE_FILE);
        files.add(Seting.ORIGINAL_POOL_URL_ADDRESS_FILE);
        files.add(Seting.ORIGINAL_ALL_SENDED_TRANSACTION_FILE);
        files.add(Seting.ORIGINAL_POOL_URL_ADDRESS_BLOCKED_FILE);

        files.add(Seting.ORIGINAL_ACCOUNT);
        files.add(Seting.ORIGINAL_BOARD_0F_SHAREHOLDERS_FILE);
        files.add(Seting.TEMPORARY_BLOCKCHAIN_FILE);
        files.add(Seting.BALANCE_REPORT_ON_DESTROYED_COINS);
        files.add(Seting.CURRENT_BUDGET_END_EMISSION);

        for (String s : files) {
            // specify an abstract pathname in the File object
            File f = new File(s);
                if(!s.contains(".txt") && !f.exists() ){

                    System.out.println("is directory: " + Files.isDirectory(Paths.get(s)) + " : " + s);
                    Files.createDirectories(f.toPath());



                } else if (!f.exists()) {
                    Files.createDirectories(Paths.get(s).getParent());
                    Files.createFile(Paths.get(s));


                }


        }


    }
}
