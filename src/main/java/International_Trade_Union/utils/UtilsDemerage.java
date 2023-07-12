package International_Trade_Union.utils;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.InfoDemerageMoney;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
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

public class UtilsDemerage {
    public static void saveDemarege(InfoDemerageMoney infoDemerageMoney, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
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

        String json = UtilsJson.objToStringJson(infoDemerageMoney);
        UtilsFileSaveRead.save(json + "\n", nextFile);

    }
    public static List<InfoDemerageMoney> readDemerage(String nameFile) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {

        List<InfoDemerageMoney> list = new ArrayList<>();

        File folder = new File(nameFile);

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                InfoDemerageMoney infoDemerageMoney = UtilsJson.jsonInfoDemerageMoney(UtilsFileSaveRead.read(fileEntry.getAbsolutePath()));
                list.add(infoDemerageMoney);
            }
        }



        return list;
    }

}
