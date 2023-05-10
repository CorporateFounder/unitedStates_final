package International_Trade_Union.utils;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.setings.Seting;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UtilsAllAddresses {
    public static void saveAllAddresses(String adress, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
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

        String json = UtilsJson.objToStringJson(adress);
        UtilsFileSaveRead.save(json + "\n", nextFile);

    }

    public static Set<String> readLineObject(String filename ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Set<String> allAddresses = new HashSet<>();
        File folder = new File(filename);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                Set<String> list = UtilsFileSaveRead.readSet(fileEntry.getAbsolutePath())
                        .stream().collect(Collectors.toSet());
                for (String s : list) {
                    allAddresses.add(s);
                }

            }
        }

        allAddresses = allAddresses
                .stream()
                .collect(Collectors.toSet());


        return allAddresses;
    }

}
