package International_Trade_Union.utils;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UtilsTransaction {
    public static void saveAllTransaction(DtoTransaction dtoTransaction, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
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

        String json = UtilsJson.objToStringJson(dtoTransaction);
        UtilsFileSaveRead.save(json + "\n", nextFile);

    }

    public static List<DtoTransaction> readLineObject(String filename ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<DtoTransaction> dtoTransactions = new ArrayList<>();
        File folder = new File(filename);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {
                    try {
                        DtoTransaction dtoTransaction = UtilsJson.jsonToDtoTransaction(s);
                        dtoTransactions.add(dtoTransaction);
                    }catch (IOException e){
                        e.printStackTrace();
                        continue;
                    }

                }

            }
        }
        dtoTransactions = dtoTransactions
                .stream()
                .collect(Collectors.toList());

        return dtoTransactions;
    }


    /**Минимальное вознаграждение за добавление в блок транзакции долларов.
     * Если количество транзакций на блок превышает 1000, будут отбираться только
     * те транзакции которые дают вознаграждение майнеру за оплату транзакции
     * если он выше определенного порога.
     * Minimum reward for adding dollars to a transaction block.
     *      * If the number of transactions per block exceeds 1000, only
     *      * those transactions that give a reward to the miner for paying for the transaction
     *      * if it is above a certain threshold.*/
    public static List<DtoTransaction> reward(List<DtoTransaction> transactions, double minRewards){
       transactions = transactions.stream().sorted(Comparator.comparing(DtoTransaction::getDigitalDollar).reversed()).collect(Collectors.toList());
        if(transactions.size() > Seting.TRANSACTIONS_COUNT_MINIMUM){
            transactions = transactions.stream().filter(t->t.getDigitalDollar()> 0).collect(Collectors.toList());
        }
        return transactions;
    }

    public static void sendTransaction(DtoTransaction dtoTransaction){

    }
}
