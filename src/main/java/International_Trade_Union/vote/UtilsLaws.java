package International_Trade_Union.vote;

import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import com.fasterxml.jackson.core.JsonProcessingException;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.model.Account;
import International_Trade_Union.utils.UtilsFileSaveRead;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsUse;


import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

public class UtilsLaws {
    public static void saveLaws(List<Laws> laws, String filename) throws IOException {
        int fileLimit = Seting.SIZE_FILE_LIMIT * 1024 * 1024;
        File folder = new File(filename);
        List<String> files = new ArrayList<>();

        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file.getAbsolutePath());
            }
        }

        files = files.stream().sorted().collect(Collectors.toList());
        int count = 0;
        String nextFile = files.isEmpty() ? filename + "0.txt" : files.get(files.size() - 1);

        if (new File(nextFile).length() >= fileLimit) {
            count = Integer.parseInt(nextFile.replaceAll("[^\\d]", "")) + 1;
            nextFile = filename + count + ".txt";
        }

        // Обработка IOException внутри лямбды
        List<String> jsons = laws.stream().map(law -> {
            try {
                return UtilsJson.objToStringJson(law);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());

        UtilsFileSaveRead.saves(jsons, nextFile, true);
    }

    public static void saveCurrentsLaws(List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals, String filename) throws IOException {
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
        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : lawEligibleForParliamentaryApprovals) {
            String json = UtilsJson.objToStringJson(lawEligibleForParliamentaryApproval);
            jsons.add(json);
        }

//        String json = UtilsJson.objToStringJson(minerAccount);
//        UtilsFileSaveRead.save(json + "\n", nextFile);
        UtilsFileSaveRead.saves(jsons, nextFile, true);
    }

   public static List<Laws> readLineLaws(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
    List<Laws> laws = new ArrayList<>();
    File folder = new File(filename);

    for (File fileEntry : folder.listFiles()) {
        if (!fileEntry.isDirectory()) {
            List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
            for (String s : list) {
                laws.add(UtilsJson.jsonToLaw(s));
            }
        }
    }

    // Sort laws once after reading
    return laws.stream().sorted(Comparator.comparing(Laws::getPacketLawName)).collect(Collectors.toList());
}
    public static List<LawEligibleForParliamentaryApproval> readLineCurrentLaws(String filename) throws JsonProcessingException {
        List<LawEligibleForParliamentaryApproval> laws = new ArrayList<>();
        File folder = new File(filename);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("is directory " + fileEntry.getAbsolutePath());
            } else {
                List<String> list = UtilsFileSaveRead.reads(fileEntry.getAbsolutePath());
                for (String s : list) {

                    LawEligibleForParliamentaryApproval laws1 = UtilsJson.jsonToCurrentLaw(s);
                    laws.add(laws1);
                }

            }
        }

        return laws;
    }


    //возвращает все счета законов
    public static List<Account> allPackegeLaws(Map<String, Account> balances) {
        List<Account> laws = new ArrayList<>();
        laws = balances.entrySet()
                .stream()
                .map(t -> t.getValue())
                .filter(t -> t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))
                .collect(Collectors.toList());
        return laws;
    }



    //возвращает пакет законов и их счета
    public static Map<String, Laws> getPackageLaws(Block block, Map<String, Laws> laws) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
//        Map<String, Laws> laws = new HashMap<>();

            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                if (dtoTransaction.verify()) {
                    if (dtoTransaction.getCustomer().startsWith(Seting.NAME_LAW_ADDRESS_START) && dtoTransaction.getBonusForMiner() >= Seting.COST_LAW) {
                        if(dtoTransaction.getLaws() != null && !laws.containsKey(dtoTransaction.getCustomer())){
                            laws.put(dtoTransaction.getCustomer(), dtoTransaction.getLaws());
                        }

                    }
                }


        }
        return laws;
    }


    /**Удаляет законы, которые были из не актуальной ветки.*/
    public static Map<String, Laws> rollBackLaws(
            Block block,
            String fileLaws,
            Map<String, Laws> lawsMap
    ) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Laws> lawsForSave = new ArrayList<>();
        List<Laws> lawsFromFile = readLineLaws(fileLaws);
//        Map<String, Laws> lawsMap = new HashMap<>();
        File file = new File(fileLaws);
        List<Laws> lawsList = new ArrayList<>();
        if (file.exists()) {
            lawsList = readLineLaws(fileLaws);
        }

        Map<String, Laws> laws = new HashMap<>();
//        lawsMap = getPackageLaws(block, laws);
        lawsMap.putAll(getPackageLaws(block, laws));

        for (Map.Entry<String, Laws> map : lawsMap.entrySet()) {
            if (!lawsList.contains(map.getValue())) {
                if( map.getValue() != null &&
                        map.getValue().packetLawName != null&&
                        map.getValue().getLaws() != null
                        && !map.getValue().getHashLaw().isEmpty()
                        && (map.getValue().getLaws().size() > 0)){

                    lawsForSave.add(map.getValue());
                }

            }

        }

        lawsFromFile.removeAll(lawsForSave);
        saveLaws(lawsFromFile, fileLaws);

        return lawsMap;
    }


    /**возвращает список всех законов, как действующих, так и не действующих, если закон новый то автоматически сохраняет его*/
    public static Map<String, Laws> getLaws(List<Block> blocks, String fileLaws, Map<String, Laws> lawsMap) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Laws> lawsForSave = new ArrayList<>();
        File file = new File(fileLaws);
        List<Laws> lawsList = new ArrayList<>();

        if (file.exists()) {
            lawsList = readLineLaws(fileLaws);
        }

        for (Block block : blocks) {
            Map<String, Laws> laws = new HashMap<>();
            lawsMap.putAll(getPackageLaws(block, laws));
        }

        for (Map.Entry<String, Laws> map : lawsMap.entrySet()) {
            Laws law = map.getValue();
            if (law != null && law.packetLawName != null && law.getLaws() != null && !law.getHashLaw().isEmpty() && !lawsList.contains(law)) {
                if (!law.getLaws().isEmpty()) {
                    lawsForSave.add(law);
                }
            }
        }

        saveLaws(lawsForSave, fileLaws);

        return lawsMap;
    }

    public static List<LawEligibleForParliamentaryApproval> getCurrentLaws(Map<String, Laws> lawsMap, Map<String, Account> balances, String fileCurrentLaws) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        List<Account> lawsBalances = allPackegeLaws(balances);


        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals = new ArrayList<>();
        LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval = null;
        File file = new File(fileCurrentLaws);
        if (file.exists()) {
            lawEligibleForParliamentaryApprovals = readLineCurrentLaws(fileCurrentLaws);
        }

        for (LawEligibleForParliamentaryApproval eligibleForParliamentaryApproval : lawEligibleForParliamentaryApprovals) {
            if(lawsBalances.contains(eligibleForParliamentaryApproval.getAccount())){
                eligibleForParliamentaryApproval.getAccount().
                        setDigitalStockBalance(balances.get(eligibleForParliamentaryApproval.getAccount().getAccount()).getDigitalStockBalance());
                eligibleForParliamentaryApproval.getAccount().
                        setDigitalDollarBalance(balances.get(eligibleForParliamentaryApproval.getAccount().getAccount()).getDigitalDollarBalance());
            }

        }


        List<LawEligibleForParliamentaryApproval> temporary = new ArrayList<>();
        for (Account account : lawsBalances) {
            lawEligibleForParliamentaryApproval = new LawEligibleForParliamentaryApproval(account, lawsMap.get(account.getAccount()));
            temporary.add(lawEligibleForParliamentaryApproval);
        }


        lawEligibleForParliamentaryApprovals.addAll(temporary);


        lawEligibleForParliamentaryApprovals = lawEligibleForParliamentaryApprovals.stream()
                .filter(t->Objects.nonNull(t.getLaws()))
                .filter(t->Objects.nonNull(t.getAccount()))
                .filter(t->Objects.nonNull(t.getLaws().getHashLaw()))
                .filter(t->Objects.nonNull(t.getLaws().getLaws()))
                .filter(t->Objects.nonNull(t.getName()))
                .filter(t->Objects.nonNull(t.getLaws().getPacketLawName()))
                .filter(t->t != null).
                filter(UtilsUse.distinctByKey(LawEligibleForParliamentaryApproval::getName)).collect(Collectors.toList());

        return lawEligibleForParliamentaryApprovals;

    }




    //отбирает позицию вакансий
    public static List<LawEligibleForParliamentaryApproval> getPossions(List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals, Director higherSpecialPositions){

        List<LawEligibleForParliamentaryApproval> temporary = new ArrayList<>();
        temporary = lawEligibleForParliamentaryApprovals.stream()
                .filter(t->Objects.nonNull(t))
                .filter(t->Objects.nonNull(t.getLaws()))
                .filter(t->Objects.nonNull(t.getLaws().getLaws()))
                .filter(t->Objects.nonNull(t.getLaws().getPacketLawName()))
                .filter(t->Objects.nonNull(t.getName()))
                .filter(t->Objects.nonNull(t.getLaws().getHashLaw()))
                .sorted((f1, f2) -> Double.compare(f2.getAccount().getDigitalStockBalance().doubleValue(), f1.getAccount().getDigitalStockBalance().doubleValue()))
                .filter(t-> t.getLaws().getPacketLawName().equals(higherSpecialPositions.getName()))
                .limit(higherSpecialPositions.getCount())
                .collect(Collectors.toList());
        return temporary;

    }


    //удаляет из списка не лигитимные должности
    public static List<LawEligibleForParliamentaryApproval> deletePossions(List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals, List<LawEligibleForParliamentaryApproval> forDeleted, Directors higherSpecialPositions){
        List<LawEligibleForParliamentaryApproval> temporary = new ArrayList<>();
        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : forDeleted) {
            if(lawEligibleForParliamentaryApproval != null &&
                    lawEligibleForParliamentaryApproval.getLaws() != null &&
                    lawEligibleForParliamentaryApproval.getLaws().getPacketLawName() != null &&
                    lawEligibleForParliamentaryApproval.getLaws().getHashLaw() != null &&
                    lawEligibleForParliamentaryApproval.getLaws().getHashLaw() != null &&
                    lawEligibleForParliamentaryApproval.getLaws().getPacketLawName().equals(higherSpecialPositions.getNames())){
                if(lawEligibleForParliamentaryApprovals.contains(lawEligibleForParliamentaryApproval)){
                    temporary.add(lawEligibleForParliamentaryApproval);
                }else {
                    continue;
                }
            }
            temporary.add(lawEligibleForParliamentaryApproval);
        }
        return temporary;
    }

    public static void saveLaw(Laws laws, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        int fileLimit = Seting.SIZE_FILE_LIMIT * 1024 * 1024;

        //папка чтобы проверить есть ли
        File folder = new File(filename);
        List<String> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file.getAbsolutePath());
            }
        }

        int count = 0;
        files = files.stream().sorted().collect(Collectors.toList());
        String nextFile = "";

        if (files.size() > 0) {
            nextFile = files.get(files.size() - 1);

            count = Integer.parseInt(nextFile.replaceAll("[^\\d]", ""));


        }

        File file = new File(nextFile);

        if (file.length() >= fileLimit) {
            count++;

        }

        nextFile = filename + count + ".txt";

        String json = UtilsJson.objToStringJson(laws);
        UtilsFileSaveRead.save(json + "\n", nextFile);

    }
    //проверяет валидность закона
    public static boolean isValidHashLaw(Laws laws) throws IOException {
        String hash = laws.getHashLaw();
        String hashLaw = Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(laws));
        System.out.println("UtilsLaw: isValidHashLaw: " + hash.equals(hashLaw));
        System.out.println("hash: " + hash);
        System.out.println("hashLaw: " + hashLaw);
        return hash.equals(hashLaw);
    }

    public static void saveCurrentLaw(LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval, String filename) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        int fileLimit = Seting.SIZE_FILE_LIMIT * 1024 * 1024;

        //папка чтобы проверить есть ли
        File folder = new File(filename);
        List<String> files = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                files.add(file.getAbsolutePath());
            }
        }

        int count = 0;
        files = files.stream().sorted().collect(Collectors.toList());
        String nextFile = "";

        if (files.size() > 0) {
            nextFile = files.get(files.size() - 1);

            count = Integer.parseInt(nextFile.replaceAll("[^\\d]", ""));


        }

        File file = new File(nextFile);

        if (file.length() >= fileLimit) {
            count++;

        }

        nextFile = filename + count + ".txt";

        String json = UtilsJson.objToStringJson(lawEligibleForParliamentaryApproval);
        UtilsFileSaveRead.save(json + "\n", nextFile);

    }


    public static List<Account> allPackageLaws(List<Account> accounts){
        List<Account> currentLaws = accounts.stream()
                .filter(t -> t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))
                .collect(Collectors.toList());
        return currentLaws;
    }

}
