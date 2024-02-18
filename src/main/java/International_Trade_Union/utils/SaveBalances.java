package International_Trade_Union.utils;



import International_Trade_Union.setings.Seting;
import International_Trade_Union.model.Account;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaveBalances {

    public static void saveBalances(Map<String, Account> balances, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        int fileLimit = Seting.SIZE_FILE_LIMIT * 1024 * 1024;

        //папка чтобы проверить есть ли
        File folder = new File(filename);
        List<String> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if(!file.isDirectory()){
                files.add(file.getAbsolutePath());
            }
        }

        int count = 0;
        files = files.stream().sorted().collect(Collectors.toList());
        String nextFile = "";

        if (files.size() > 0) {
            nextFile = files.get(files.size()-1);

            count = Integer.parseInt(nextFile.replaceAll("[^\\d]", ""));


        }

        File file = new File(nextFile);

        if(file.length() >= fileLimit){
            count++;

        }

        nextFile = filename + count + ".txt";

        List<String> jsons = new ArrayList<>();
        for (Map.Entry<String, Account> stringAccountEntry : balances.entrySet()) {
            String json = UtilsJson.objToStringJson(stringAccountEntry.getValue());
            jsons.add(json);
        }

//        String json = UtilsJson.objToStringJson(minerAccount);
//        UtilsFileSaveRead.save(json + "\n", nextFile);
        UtilsFileSaveRead.saves(jsons, nextFile, true);

    }

    public static Map<String, Account> readLineObject(String filename ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Account> accounts = new ArrayList<>();
        File folder = new File(filename);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                   Account account = UtilsJson.jsonToAccount(s);
                    accounts.add(account);
                }

            }
        }
        Map<String, Account> balances = accounts
                .stream()
                .collect(Collectors.toMap(Account::getAccount, t->t, (v1, v2) -> v1));
        return balances;
    }

    //написал SuprTrupr
    //writed SuprTrupr
    // Method for incrementally updating balances in the file
    public static void updateBalance(String account, double newBalance, String filename) throws IOException {
        File file = new File(filename);
        File tempFile = new File("temp.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String line;
        boolean accountUpdated = false;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":"); // Assuming the line format is "account:balance"
            String currentAccount = parts[0].trim();
            double balance = Double.parseDouble(parts[1].trim());

            if (currentAccount.equals(account)) {
                // Update the balance for the specific account
                writer.write(account + ":" + newBalance);
                writer.newLine();
                accountUpdated = true;
            } else {
                // Keep the current balance for other accounts
                writer.write(line);
                writer.newLine();
            }
        }

        // If the account wasn't found, add a new line for the account with the new balance
        if (!accountUpdated) {
            writer.write(account + ":" + newBalance);
            writer.newLine();
        }

        writer.close();
        reader.close();

        // Rename the temporary file to the original file name
        tempFile.renameTo(file);
    }

}
