package International_Trade_Union.controllers.WebController;

import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsMining;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

@RestController
public class WebBasisController {

    /**If it returns true, then the file system is currently being updated.
     *If true, the buttons should be disabled.
     * localhost:8082/isUpdating
     *
     * Если возвращает true, значит сейчас происходит обновление файловой системы.
     * Если true, кнопки должны быть отключены.
     * localhost:8082/isUpdating */
    @GetMapping("/isUpdating")
    @ResponseBody
    public boolean isUpdating(){
        return BasisController.isUpdating();
    }

    /**
     *If it returns true, then mining is in progress.
     *While true, the buttons should be disabled.
     *localhost:8082/isMining
     *
     * Если возвращает true, значит сейчас происходит майнинг.
     * Пока true, кнопки должны быть отключены.
     * localhost:8082/isMining*/
    @GetMapping("/isMining")
    @ResponseBody
    public boolean isMining(){
        return BasisController.isMining();
    }

    /**
     * Returns the miner's account balance during mining, and is only used
     * when mining occurs.
     * When isMining is true to not interrupt mining, you can see the balance
     * Here.
     * localhost:8082/minerShow
     *
     * Возвращает аккаунт баланс майнера во время майнинга, и используется только
     * когда, происходит добыча.
     * Когда isMining true, чтобы не прерывать добычу, вы можете увидеть баланс
     * здесь.
     * localhost:8082/minerShow */
    @GetMapping("/minerShow")
    @ResponseBody
    public Account minerShow(){
        return BasisController.getMinerShow();
    }




    /**
     * The size of the local blockchain.
     * localhost:8082/size
     * Размер локального блокчейна.
     * localhost:8082/size*/
    @GetMapping("/localSize")
    @ResponseBody
    public int localSize() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        return blockchain.sizeBlockhain();
    }

}
