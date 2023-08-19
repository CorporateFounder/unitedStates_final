package International_Trade_Union.controllers;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.SaveBalances;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsTransaction;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

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
        Blockchain tempblockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);

        //транзакции которые мы добавили в блок и теперь нужно удалить из файла, в папке resources/transactions
        List<DtoTransaction> temporaryDtoList = AllTransactions.getInstance();
        System.out.println("temporaryDtoList: " + temporaryDtoList);
        System.out.println("********************************************************");
        //отказ от дублирующих транзакций
        temporaryDtoList = UtilsBlock.validDto(tempblockchain.getBlockchainList(), temporaryDtoList);
        System.out.println("temporaryDtoList: " + temporaryDtoList);
        System.out.println("*********************************************************");
        //отказ от транзакций которые меньше данного вознаграждения
        temporaryDtoList = UtilsTransaction.reward(temporaryDtoList, 0);
        System.out.println("**********************************************************");

        //раз в три для очищяет файлы в папке resources/sendedTransaction данная папка
        //хранит уже добавленые в блокчейн транзации, чтобы повторно не добавлять в
        //в блок уже добавленные транзакции

        AllTransactions.clearUsedTransaction(AllTransactions.getInsanceSended());
        return 0;
    }

    @GetMapping("/testBlock1")
    @ResponseBody
    public String testBlock1() throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, ParseException {
        int size = 37903;
        while (true){
            if(size % 288 == 0){

                break;
            }
            size++;

        }
        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        List<Block> blocks = blockchain.getBlockchainList();
        System.out.println("blockchain size: " + blockchain.sizeBlockhain());
        int diff = 0;

        for (int i = 0; i < 600; i++) {

            diff= UtilsBlock.difficulty(blocks,
                    Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
            blocks.remove(blocks.size()-1);
            if(diff > 5){
                System.out.println("diff: " + diff + " index: "+ blocks.get(blocks.size()-1).getIndex());
                break;
            }

        }



        System.out.println("******************");
        System.out.println("diff " + diff);
        System.out.println("size: " + size);

        return "0";
    }

    private static final int TARGET_BLOCK_TIME = 150; // 150 секунд
    private static final int RETARGET_INTERVAL = 288; // 288 блоков

    public static int getDifficulty(List<Block> blockchain) {
        Block latestBlock = blockchain.get(blockchain.size() - 1);
        long timeSinceLastBlock = latestBlock.getTimestamp().getTime() - blockchain.get(blockchain.size() - RETARGET_INTERVAL - 1).getTimestamp().getTime();
        long expectedBlocksPerSecond = RETARGET_INTERVAL / TARGET_BLOCK_TIME;
        int difficulty = (int) Math.pow(2, (timeSinceLastBlock / expectedBlocksPerSecond) - 1);
        return difficulty;
    }
}
