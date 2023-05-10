package unitted_states_of_mankind.entityTest.blockchainTest;



import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsBlock;
import International_Trade_Union.utils.UtilsFileSaveRead;

import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlockchainRead {
    public static Blockchain getBlockchain() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
        List<Block> blocks = UtilsBlock.readLineObject(
                Seting.TEST_BLOCKCHAIN_SAVED);
        Blockchain blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.TEST);
        blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
        blockchain.setBlockchainList(blocks);
        return blockchain;
    }

    public static void deleteFiles(
            String blockchainFile,
            String lastBlockFile,
            String blockchainBalance,
            String indexFile,
            String federalFile,
            String allLaws,
            String currentLaws,
            String fileVote,
            String allLawsWithBalance){
        //удаление файлов
        UtilsFileSaveRead.deleteAllFiles(blockchainFile);
        UtilsFileSaveRead.deleteAllFiles(lastBlockFile);
        UtilsFileSaveRead.deleteAllFiles(blockchainBalance);
//        Seting.INDEX_TEST
        File indexDirectory = new File(indexFile);
        System.out.println("indexDirectory: " + indexDirectory.getParent());
        UtilsFileSaveRead.deleteAllFiles(indexDirectory.getParent());
        File govermnentDirectory = new File(federalFile);
        UtilsFileSaveRead.deleteAllFiles(govermnentDirectory.getParent());
        UtilsFileSaveRead.deleteAllFiles(allLaws);
        UtilsFileSaveRead.deleteAllFiles(currentLaws);
        UtilsFileSaveRead.deleteAllFiles(fileVote);
        UtilsFileSaveRead.deleteAllFiles(allLawsWithBalance);
    }
    public static Blockchain getBlockchainSave(int block, int accounts,  BlockchainFactoryEnum factoryEnum, int difficultyAdjustmentInterval, long blockGenenarionInterval, boolean saveAll, double randomDollar, double randomPower) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidAlgorithmParameterException {
        GenerateBlockchainFileTest.sendDollar = randomDollar;
        GenerateBlockchainFileTest.sendPower = randomPower;
        if(saveAll == false){
           deleteFiles(Seting.TEST_TEMPORARY_BLOCKCHAIN,
                   Seting.TEST_LAST_BLOCK_TEMPORARY,
                   Seting.TEST_BLOCKCHAIN_BALANCES_TEMPORARY,
                   Seting.TEST_INDEX_TEMPORARY,
                   Seting.TEST_FEDERAL_GOVERNMENT_TEMPORARY,
                   Seting.TEST_FEDERAL_LAWS_TEMPORARY,
                   Seting.TEST_CURRENT_LAWS_TEMPORARY,
                   Seting.TEST_FEDERAL_VOTE_TEMPORARY,
                   Seting.TEST_ALL_FEDERAL_LAWS_WITH_BALANCE_TEMPORARY);
       }else {
           deleteFiles(Seting.TEST_BLOCKCHAIN_SAVED,
                   Seting.TEST_LAST_BLOCK,
                   Seting.TEST_BLOCKCHAIN_BALANCES,
                   Seting.INDEX_TEST,
                   Seting.TEST_FEDERAL_GOVERNMENT,
                   Seting.TEST_FEDERAL_LAWS,
                   Seting.TEST_CURRENT_LAWS,
                   Seting.TEST_FEDERAL_VOTE,
                   Seting.TEST_ALL_FEDERAL_LAWS_WITH_BALANCE_FILE);
       }


        int transactions = 3 ;
       if(saveAll == false){
           GenerateBlockchainFileTest.savedBlock(
                   Seting.TEST_TEMPORARY_BLOCKCHAIN,
                   Seting.TEST_BLOCKCHAIN_BALANCES_TEMPORARY,
                   Seting.TEST_FEDERAL_GOVERNMENT_TEMPORARY,
                   Seting.TEST_FEDERAL_LAWS_TEMPORARY,
                   Seting.TEST_INDEX_TEMPORARY,
                   Seting.TEST_FEDERAL_VOTE_TEMPORARY,
                   Seting.TEST_ALL_FEDERAL_LAWS_WITH_BALANCE_TEMPORARY,
                   block,
                   accounts,
                   transactions,
                   factoryEnum,
                   difficultyAdjustmentInterval,
                   blockGenenarionInterval);
       }
       else {


           GenerateBlockchainFileTest.savedBlock(
                   Seting.TEST_BLOCKCHAIN_SAVED,
                   Seting.TEST_BLOCKCHAIN_BALANCES,
                   Seting.TEST_FEDERAL_GOVERNMENT,
                   Seting.TEST_FEDERAL_LAWS,
                   Seting.INDEX_TEST,
                   Seting.TEST_FEDERAL_VOTE,
                   Seting.TEST_ALL_FEDERAL_LAWS_WITH_BALANCE_FILE,
                   block,
                   accounts,
                   transactions,
                   factoryEnum,
                   difficultyAdjustmentInterval,
                   blockGenenarionInterval
           );
       }


        List<Block> blocks  = null;
        Blockchain blockchain = BLockchainFactory.getBlockchain(factoryEnum);
       if (saveAll == false){
           blocks = UtilsBlock.readLineObject(Seting.TEST_TEMPORARY_BLOCKCHAIN);
       }
       else {
           blocks = UtilsBlock.readLineObject(Seting.TEST_BLOCKCHAIN_SAVED);
       }

        blockchain.setBlockchainList(blocks);
       blockchain.setBLOCK_GENERATION_INTERVAL(blockGenenarionInterval);
       blockchain.setDIFFICULTY_ADJUSTMENT_INTERVAL(difficultyAdjustmentInterval);

        if(saveAll == false){
            deleteFiles(Seting.TEST_TEMPORARY_BLOCKCHAIN,
                    Seting.TEST_LAST_BLOCK_TEMPORARY,
                    Seting.TEST_BLOCKCHAIN_BALANCES_TEMPORARY,
                    Seting.TEST_INDEX_TEMPORARY,
                    Seting.TEST_FEDERAL_GOVERNMENT_TEMPORARY,
                    Seting.TEST_FEDERAL_LAWS_TEMPORARY,
                    Seting.TEST_CURRENT_LAWS_TEMPORARY,
                    Seting.TEST_FEDERAL_VOTE_TEMPORARY,
                    Seting.TEST_ALL_FEDERAL_LAWS_WITH_BALANCE_TEMPORARY);
        }


        return blockchain;
    }

    /** blocks, accounts, factoryEnum, difficulty, generation interval, deleted all*/
    public static Blockchain getBlockchain(int block,
                                           int accounts,
                                           BlockchainFactoryEnum factoryEnum,
                                           int difficultyAdjustmentInterval, long blockGenenarionInterval) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return getBlockchainSave(block, accounts, factoryEnum, difficultyAdjustmentInterval, blockGenenarionInterval, true, 10.0, 10.0);

    }

}
