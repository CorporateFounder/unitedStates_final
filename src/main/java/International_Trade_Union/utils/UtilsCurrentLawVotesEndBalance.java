package International_Trade_Union.utils;

import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.vote.CurrentLawVotesEndBalance;

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

public class UtilsCurrentLawVotesEndBalance {
    public static void saveBudget(CurrentLawVotesEndBalance currentLawVotesEndBalance, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
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

        String json = UtilsJson.objToStringJson(currentLawVotesEndBalance);
        UtilsFileSaveRead.save(json + "\n", nextFile);

    }

    public static List<CurrentLawVotesEndBalance> readLine(String filename ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<CurrentLawVotesEndBalance> currentLawVotesEndBalances = new ArrayList<>();
        File folder = new File(filename);

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {

                    CurrentLawVotesEndBalance currentLawVotesEndBalance = UtilsJson.jsonToCurrentLawVotesBalance(s);
                    currentLawVotesEndBalances.add(currentLawVotesEndBalance);
                }

            }
        }
        currentLawVotesEndBalances = currentLawVotesEndBalances
                .stream()
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());

        return currentLawVotesEndBalances;
    }
}
