package International_Trade_Union.utils;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.InfoDemerageMoney;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;

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
