package unitted_states_of_mankind.utilsTest;

import International_Trade_Union.utils.*;
import International_Trade_Union.vote.Laws;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.*;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Keys;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.VoteEnum;
import unitted_states_of_mankind.entityTest.blockchainTest.BlockchainRead;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//TODO высянить почему не списывается правильно долги
@SpringBootTest
public class UtilsBalanceTest {








    //TODO исправить sendTest установив баланс отправилтеля
    @Test
    public void SendTest() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {


        Block block = UtilsJson.jsonToBLock("{\"dtoTransactions\":[{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"digitalDollar\":14.5,\"digitalStockBalance\":14.5,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEUCIQCkBX4IBVOq/2mPsOTWIn2N8TPfS5FItHqrEqhuU/YgHAIgKmdMlLXLM4sfnWh0aO0ezTZeInUdc/dh7SDF1qOKPAI=\"},{\"sender\":\"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\",\"customer\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"digitalDollar\":145.0,\"digitalStockBalance\":145.0,\"laws\":{\"packetLawName\":null,\"laws\":null,\"hashLaw\":null},\"bonusForMiner\":0.0,\"voteEnum\":\"YES\",\"sign\":\"MEYCIQD8iQWqgDwsxDV3ew0yZeUDS+xnI2iNjuNHak4MccU1WgIhAOP1cKFcZfrJ6Oek4UH6cX9FcIBYaA3OwAItctWny1k7\"}],\"previousHash\":\"904045a20888b4581d08c9587242b740cb12603802c440104f10063236603202\",\"minerAddress\":\"2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG\",\"founderAddress\":\"nNifuwmFZr7fnV1zvmpiyQDV5z7ETWvqR6GSeqeHTY43\",\"randomNumberProof\":947262,\"minerRewards\":0.0,\"hashCompexity\":16,\"timestamp\":1704736749000,\"index\":167479,\"hashBlock\":\"185ac49d8c2320b030d4ac8454000b2a21423a00816a042c5403470032439188\"}");


        Account sender = new Account("faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ", 100, 0, 0);

        Account recipient = new Account("2A8vxijdyY5ST1WhLQan3N1P6wSdzBDo9VmEFhck9bArG", 100, 0, 0);
        System.out.println("sender before: " + sender);
        System.out.println("recipient before: " + recipient);
        UtilsBalance.sendMoney(sender, recipient, block.getDtoTransactions().get(1).getDigitalDollar(),block.getDtoTransactions().get(1).getDigitalStockBalance(),  block.getDtoTransactions().get(1).getBonusForMiner());
        System.out.println("sender after: " + sender);
        System.out.println("recipient after: " + recipient);
        System.out.println("roll back");
        UtilsBalance.rollBackSendMoney(sender, recipient, block.getDtoTransactions().get(1).getDigitalDollar(), block.getDtoTransactions().get(1).getDigitalStockBalance(), block.getDtoTransactions().get(1).getBonusForMiner(), VoteEnum.YES );
        System.out.println("sender after: " + sender);
        System.out.println("recipient after: " + recipient);
    }
}


