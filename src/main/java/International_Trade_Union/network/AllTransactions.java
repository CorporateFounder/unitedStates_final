package International_Trade_Union.network;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AllTransactions {
    private static List<DtoTransaction> instance = new ArrayList<>();
    //все транзакции которые уже добавлены в блок, нужно чтобы повторно
    //не добавлялись в блок если они скачены с дисковери.
    private static List<DtoTransaction> sendedTransaction = new ArrayList<>();

    public static List<DtoTransaction> readFrom() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return UtilsTransaction.readLineObject(Seting.ORGINAL_ALL_TRANSACTION_FILE);
    }

    public static synchronized List<DtoTransaction> getInstance() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if (instance == null) {
            instance = new ArrayList<>();
        }
        instance = new ArrayList<>();

        //считываем с пула транзакции из дисковери.
        for (String s : Seting.ORIGINAL_ADDRESSES) {
            try {
                String json = UtilUrl.readJsonFromUrl(s + "/getTransactions");
                List<DtoTransaction> list = UtilsJson.jsonToDtoTransactionList(json);

                instance.addAll(list);


            } catch (IOException | JSONException e) {

                System.out.println("AllTransaction: getInstance: Error");
                continue;
            }


        }

        instance.addAll(UtilsTransaction.readLineObject(Seting.ORGINAL_ALL_TRANSACTION_FILE));
        instance = instance.stream().distinct().collect(Collectors.toList());

        sendedTransaction = getInsanceSended();

        instance.removeAll(sendedTransaction);
        return instance;
    }

    public static synchronized void clearAllTransaction() {
        instance = new ArrayList<>();
        Mining.deleteFiles(Seting.ORGINAL_ALL_TRANSACTION_FILE);
    }

    public static synchronized void clearAllSendedTransaction(long index) {
        if(index % (Seting.COUNT_BLOCK_IN_DAY * Seting.DAY_DELETED_SENDED_FILE) == 0){
            sendedTransaction = new ArrayList<>();
            Mining.deleteFiles(Seting.ORIGINAL_ALL_SENDED_TRANSACTION_FILE);
            System.out.println("clear delete sended transaction");
        }

    }

    public static synchronized void clearUsedTransaction(List<DtoTransaction> transactions) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        instance = getInstance();
        List<DtoTransaction> temporaryDto = new ArrayList<>();

        instance = temporaryDto;
        instance.removeAll(transactions);
        Mining.deleteFiles(Seting.ORGINAL_ALL_TRANSACTION_FILE);
        for (DtoTransaction dtoTransaction : instance) {

            UtilsTransaction.saveAllTransaction(dtoTransaction, Seting.ORGINAL_ALL_TRANSACTION_FILE);
        }


    }

    public static synchronized void addTransaction(DtoTransaction transaction) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        instance = getInstance();
        instance.add(transaction);
        Mining.deleteFiles(Seting.ORGINAL_ALL_TRANSACTION_FILE);
        instance = instance.stream().filter(UtilsUse.distinctByKey(DtoTransaction::toSign)).collect(Collectors.toList());
        for (DtoTransaction dtoTransaction : instance) {
            UtilsTransaction.saveAllTransaction(dtoTransaction, Seting.ORGINAL_ALL_TRANSACTION_FILE);
        }


    }

    public static synchronized void addSendedTransaction(List<DtoTransaction> transactions) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        sendedTransaction = getInsanceSended();
        sendedTransaction.addAll(transactions);

        Mining.deleteFiles(Seting.ORIGINAL_ALL_SENDED_TRANSACTION_FILE);
        for (DtoTransaction dtoTransaction : sendedTransaction) {
            UtilsTransaction.saveAllTransaction(dtoTransaction, Seting.ORIGINAL_ALL_SENDED_TRANSACTION_FILE);
        }

        System.out.println("AllTransaction: addSendedTransaction: " + sendedTransaction.size());

    }

    public static List<DtoTransaction> getInsanceSended() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        if (sendedTransaction == null) {
            sendedTransaction = new ArrayList<>();
        }
        sendedTransaction = UtilsTransaction.readLineObject(Seting.ORIGINAL_ALL_SENDED_TRANSACTION_FILE);
        sendedTransaction = sendedTransaction.stream().distinct().collect(Collectors.toList());
        return sendedTransaction;
    }

}
