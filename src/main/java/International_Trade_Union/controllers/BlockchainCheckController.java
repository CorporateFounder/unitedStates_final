package International_Trade_Union.controllers;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBalance;
import International_Trade_Union.utils.UtilsBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class BlockchainCheckController {

    @Autowired
    BlockService blockService;
    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);
        UtilsBalance.setBlockService(blockService);
        UtilsBlock.setBlockService(blockService);

    }
    /**Проверяет целостность блокчейна из файла.
     * Verifies the integrity of the blockchain from a file.*/
    @GetMapping("/checkValidation")
    public boolean checkValidation() throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        DataShortBlockchainInformation data = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        boolean check = data.isValidation();
        if(check == false){
            System.out.println("deleted blockchain files");
        }

        System.out.println("check: " + check);
        return check;
    }
}
