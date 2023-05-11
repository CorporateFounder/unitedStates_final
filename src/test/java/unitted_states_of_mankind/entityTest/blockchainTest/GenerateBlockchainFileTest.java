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
        //TODO generate File Test
        List<AccountSimulation> list = GenerateAccountsSimulation.accountSimulations(accounts);



        Random random = new Random();
        Blockchain blockchain = BLockchainFactory.getBlockchain(factoryEnum);


        Map<String, Account> balances = new HashMap<>();
        for (int i = 0; i < year; i++) {

            TransactionsTest transactionsTest = new TransactionsTest(transactions, list);
            transactionsTest.setSendDollarRandom(sendDollar);
            transactionsTest.setSendPowerRandom(sendPower);

            List<Account> miners = AddapterAccountSimulationToAccount.getAccounts(list);
            Account miner = miners.get(random.nextInt(miners.size()));


            blockchain = Mining.getBlockchain(
                    fileBlockchain,
                    BlockchainFactoryEnum.TEST);


            long index = 0;
            File indexF = new File(fileIndex);
            if (indexF.length() > 0){
                index = Long.parseLong(UtilsFileSaveRead.read(fileIndex));
            }else {
                //сохранение генезис блока
                if (blockchain.sizeBlockhain() == 1) {
                    UtilsBlock.saveBLock(blockchain.getBlock(0), fileBlockchain);
                }

                balances = Mining.getBalances(fileBlockchainBalance, blockchain, balances);
                Mining.deleteFiles(fileBlockchainBalance);
                SaveBalances.saveBalances(balances, fileBlockchainBalance);
            }
            balances = SaveBalances.readLineObject(fileBlockchainBalance);

            Block block = Mining.miningDay(
                    miner,
                    blockchain,
                    blockGenerationInterval,
                    difficultAdjastmentInterval,
                    transactionsTest.getTransactions(),
                    balances,
                    index
            );

            System.out.println("index: "  + index);
            index++;


            UtilsFileSaveRead.save(String.valueOf(index), fileIndex, false);
            int diff = difficultAdjastmentInterval;
            //Тестирование блока
            List<Block> testingValidationsBlock = null;
            if(blockchain.sizeBlockhain() > diff){
                testingValidationsBlock = blockchain.getBlockchainList().subList(blockchain.sizeBlockhain() - diff, blockchain.sizeBlockhain());
            }
            else {
                testingValidationsBlock = blockchain.getBlockchainList();
            }
            if (testingValidationsBlock.size() > 1) {
                boolean validationTesting = UtilsBlock.validationOneBlock(
                        blockchain.genesisBlock().getFounderAddress(),
                        testingValidationsBlock.get(testingValidationsBlock.size() - 1),
                        block,
                        blockGenerationInterval,
                        diff,
                        testingValidationsBlock);
                testingValidationsBlock.add(block);

                if (validationTesting == false) {
                    System.out.println("wrong validation block: " + validationTesting);
                    System.out.println("index block: " + block.getIndex());
                    return;
                }
            }


            //сохранение блока
            blockchain.addBlock(block);
            UtilsBlock.saveBLock(block, fileBlockchain);


            //перерасчет после добычи
            balances = Mining.getBalances(fileBlockchainBalance, blockchain, balances);
            Mining.deleteFiles(fileBlockchainBalance);
            SaveBalances.saveBalances(balances, fileBlockchainBalance);

            //получение и отображение законов, а также сохранение новых законов
            //и изменение действующих законов
            Map<String, Laws> allLaws = UtilsLaws.getLaws(blockchain.getBlockchainList(), fileLaws);

            //возвращает все законы с балансом
            List<LawEligibleForParliamentaryApproval> allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances, fileAllLawsWithBalance);
            //удаление устаревних законов
            Mining.deleteFiles(fileAllLawsWithBalance);
            UtilsLaws.saveCurrentsLaws(allLawsWithBalance, fileAllLawsWithBalance);

//            List<Account> governments = new ArrayList<>();
//            governments = UtilsGovernment.findBoardOfShareholders(balances);

//
//            //подсчитывает и сохраняет голоса
//            Map<String, CurrentLawVotes> votes = UtilsCurrentLaw.readLineVotes(fileVote);
//            if(votes == null) votes = new HashMap<>();
//            //засчитывать все акаунты чей баланс выше нуля
//            List<Account> accounts1 = balances.entrySet().stream().map(t->t.getValue()).collect(Collectors.toList());
//            accounts1 = accounts1.stream().filter(t->t.getDigitalStockBalance() > 0).collect(Collectors.toList());
//            votes = UtilsCurrentLaw.calculateVote(votes, accounts1, block);
//            Mining.deleteFiles(fileVote);
//            UtilsCurrentLaw.saveVotes(votes, fileVote);




//            //сохранение власти
//            if (governments.size() > 0) {
//                File governmentDirectory = new File(governmentfile);
//                Mining.deleteFiles(governmentDirectory.getParent());
//                UtilsFileSaveRead.save(UtilsJson.objToStringJson(governments), governmentfile);
//            }



        }


        sendDollar = 50.0;
        sendPower = 10.0;

    }



    /*
            //подсчитывает и сохраняет голоса
            Map<String, CurrentLawVotes> votes = UtilsCurrentLaw.readLineVotes(fileVote);
            if(votes == null) votes = new HashMap<>();
            votes = UtilsCurrentLaw.calculateVote(votes, governments, block);
            Mining.deleteFiles(fileVote);
            UtilsCurrentLaw.saveVotes(votes, fileVote);*/

    @Test
//    @Ignore
    public void saveMiningDayTest() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {

        int year = (int) (Seting.COUNT_BLOCK_IN_DAY * 181);
        int accounts = 301;
        Timestamp before = new Timestamp(System.currentTimeMillis());
        BlockchainRead.getBlockchainSave(year, accounts, BlockchainFactoryEnum.TEST,
                Seting.DIFFICULTY_ADJUSTMENT_INTERVAL_TEST, Seting.BLOCK_GENERATION_INTERVAL_TEST, true, 10.0, 10.0);
        Timestamp after = new Timestamp(System.currentTimeMillis());
        System.out.println("time save mining day: " + new Timestamp(after.getTime() - before.getTime()));

    }


}
