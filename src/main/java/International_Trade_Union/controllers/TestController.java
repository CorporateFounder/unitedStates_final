package International_Trade_Union.controllers;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBlock;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Controller
public class TestController {

    @GetMapping("test2")
    @ResponseBody
    public SubBlockchainEntity test(){
        SubBlockchainEntity subBlockchainEntity = new SubBlockchainEntity(0, 10);
        return subBlockchainEntity;
    }

    @GetMapping("/testBlock")
    @ResponseBody
    public int testBlock() throws IOException, CloneNotSupportedException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        int temp = 0;
        Blockchain blockchain  =  Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        for (int i = blockchain.sizeBlockhain()-1; i > 0 ; i--) {
            Block block = blockchain.getBlock(i);
            if( block.getIndex() % Seting.DIFFICULTY_ADJUSTMENT_INTERVAL == 0){
                temp = i;
                break;
            }
        }

        int diff =
                UtilsBlock.difficulty(blockchain.subBlock(temp - 600, temp+1),
                Seting.BLOCK_GENERATION_INTERVAL,
                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);


        return diff;
    }

    @GetMapping("/testBlock1")
    @ResponseBody
    public String testBlock1() throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        long timeUp = format.parse("2016/01/01 00:00:00").getTime();
        Blockchain blockchain =  Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
       Block current =  blockchain.getBlockchainList().get(blockchain.sizeBlockhain() -1);
       Block last = blockchain.getBlockchainList().get(blockchain.sizeBlockhain()- Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);


        long diff = current.getTimestamp().getTime() - last.getTimestamp().getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        StringBuilder sb = new StringBuilder();
        sb.append(diffDays + " дней, ");
        sb.append(diffHours + " часов, ");
        sb.append(diffMinutes + " минут, ");
        sb.append(diffSeconds + " секунд");
        return sb.toString();
    }
}
