package unitted_states_of_mankind.entityTest.blockchainTest;




import java.io.File;
import java.sql.Timestamp;


import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.simulation.AccountSimulation;
import International_Trade_Union.simulation.AddapterAccountSimulationToAccount;
import International_Trade_Union.simulation.GenerateAccountsSimulation;
import International_Trade_Union.utils.*;
import International_Trade_Union.vote.*;
import unitted_states_of_mankind.networkTest.TransactionsTest;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
public class

GenerateBlockchainFileTest {

    static AllTransactions transactions = new AllTransactions();
    static double sendDollar = 10.0;
    static double sendPower = 1.0;


    // нужная только для создания длиноого блокчейна для тестирования, после чего нужно ставить в игнор
    //полезные ссылки
    //двойная бугалтерия
    //https://gruyaume.medium.com/create-your-own-blockchain-using-python-double-entry-bookkeeping-and-transaction-fees-pt-4-1e399a9cc092
    //p2p сеть
    //https://dev.to/envoy_/learn-blockchains-by-building-one-in-python-2kb3

    public static void savedBlock(
            String fileBlockchain,
            String fileBlockchainBalance,
            String governmentfile,
            String fileLaws,
            String fileIndex,
            String fileVote,
            String fileAllLawsWithBalance,
            long year,
            int accounts,
            int transactions,
            BlockchainFactoryEnum factoryEnum,
            int difficultAdjastmentInterval,
            long blockGenerationInterval) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {


    }



    /*
            //подсчитывает и сохраняет голоса
            Map<String, CurrentLawVotes> votes = UtilsCurrentLaw.readLineVotes(fileVote);
            if(votes == null) votes = new HashMap<>();
            votes = UtilsCurrentLaw.calculateVote(votes, governments, block);
            Mining.deleteFiles(fileVote);
            UtilsCurrentLaw.saveVotes(votes, fileVote);*/




}
