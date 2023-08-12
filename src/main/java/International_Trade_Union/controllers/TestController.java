package International_Trade_Union.controllers;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.DataShortBlockchainInformation;
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
        Blockchain blockchain =  Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);


        int classicDiff = UtilsBlock.difficulty(blockchain.getBlockchainList(),
                Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL );


        System.out.println("classicDiff: " + classicDiff);
        System.out.println("*****************************************************************");
        System.out.println("validation: " + blockchain.validatedBlockchain());

        System.out.println("*****************************************************************");
        DataShortBlockchainInformation dataShortBlockchainInformation =
                Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
        System.out.println("dataShortBlockchainInformation: " + dataShortBlockchainInformation);
        System.out.println("*****************************************************************");






        System.out.println("*****************************************************************");
        int block_size = 2;



        List<Block> tempBLocks = blockchain.getBlockchainList()
                .subList(blockchain.sizeBlockhain()-300-block_size,
                        blockchain.sizeBlockhain()-300);


        List<Block> lastBlocks = blockchain.getBlockchainList()
                .subList((int) (tempBLocks.get(0).getIndex()-Seting.PORTION_BLOCK_TO_COMPLEXCITY-block_size),
                        (int) (tempBLocks.get(0).getIndex()-block_size));
        System.out.println("last: " + lastBlocks.get(lastBlocks.size()-1).getIndex());
        System.out.println("temp: " + tempBLocks.get(0).getIndex());
        Block prevBlock = blockchain.getBlock((int) (tempBLocks.get(0).getIndex()-1));


        dataShortBlockchainInformation.setSize(dataShortBlockchainInformation.
                getSize()-block_size);
        DataShortBlockchainInformation shortData = Blockchain.shortCheck(prevBlock, tempBLocks,
                dataShortBlockchainInformation, lastBlocks);


        System.out.println("shortData: " + shortData);
        System.out.println("*****************************************************************");


        return 0;
    }

    @GetMapping("/testBlock1")
    @ResponseBody
    public String testBlock1() throws CloneNotSupportedException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, ParseException {
        int size = 37000;
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
        long time = blocks.get(blocks.size()-1200).getTimestamp().getTime();
        for (int i = 0; i < 1200; i++) {

            diff= UtilsBlock.difficulty(blocks,
                    Seting.BLOCK_GENERATION_INTERVAL, Seting.DIFFICULTY_ADJUSTMENT_INTERVAL);
            blocks.remove(blocks.size()-1);
            if(diff > 5){
                System.out.println("diff: " + diff + " index: "+ blocks.get(blocks.size()-1).getIndex());
                break;
            }
            if(time > blocks.get(i).getTimestamp().getTime()){
                System.out.println("wrong time: " + i);
                System.out.println("prev time: " + new Date(time));
                System.out.println("actul time: " + new Date(blocks.get(i).getTimestamp().getTime()));
            }
        }


        System.out.println("******************");
        System.out.println("diff " + diff);
        System.out.println("size: " + size);
        Date current = new Date(System.currentTimeMillis());
        OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
        Timestamp test = Timestamp.valueOf( OffsetDateTime.now( ZoneOffset.UTC ).atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        Date utc = new Date(test.getTime());
        System.out.println("current: " + current + " utc: " + utc);
        return "0";
    }
}
