package International_Trade_Union.model;


import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;

import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.*;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.UtilsLaws;
import International_Trade_Union.vote.VoteEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.setings.Seting.*;

@Component
public class Mining {
    @Autowired
    BlockService blockService;

    @PostConstruct
    public void init() {
        Blockchain.setBlockService(blockService);

    }

    private static int customDiff = Seting.V34_MIN_DIFF;
    public static boolean miningIsObsolete = false;
    private static volatile boolean isMiningStop = false;

    public static Blockchain getBlockchain(String filename, BlockchainFactoryEnum factoryEnum) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        List<Block> blocks = UtilsBlock.readLineObject(filename);
        Blockchain blockchain = null;
        blockchain = BLockchainFactory.getBlockchain(factoryEnum);

        if (blocks.size() != 0) {
            blocks = blocks.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
            blockchain.setBlockchainList(blocks);
        }
        return blockchain;
    }

    public static int getCustomDiff() {
        return customDiff;
    }

    public static void setCustomDiff(int customDiff) {
        customDiff = customDiff < Seting.V34_MIN_DIFF ? Seting.V34_MIN_DIFF : customDiff;
        Mining.customDiff = customDiff;
    }

    public static boolean isIsMiningStop() {
        return isMiningStop;
    }

    public static synchronized void setIsMiningStop(boolean isMiningStop) {

        Mining.isMiningStop = isMiningStop;
    }

    public Map<String, Account> getBalances(String filename, Blockchain blockchain, Map<String, Account> balances, List<String> signs) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException, CloneNotSupportedException {
        //start test




        balances = UtilsAccountToEntityAccount.entityAccountsToMapAccounts(blockService.findAllAccounts());
        if (balances == null) {
            balances = new HashMap<>();
        }

        Block block;
        if (blockchain != null && blockchain.sizeBlockhain() > 0) {
            block = blockchain.getBlock(blockchain.sizeBlockhain() - 1);
            balances = UtilsBalance.calculateBalance(balances, block, signs, new ArrayList<>());
            //test
            Map<String, Laws> allLaws = new HashMap<>();

            allLaws = UtilsLaws.getLaws(blockchain.subBlock(blockchain.sizeBlockhain() - Seting.LAW_MONTH_VOTE, blockchain.sizeBlockhain()), Seting.ORIGINAL_ALL_CORPORATION_LAWS_FILE, allLaws);


            //получает все созданные когда либо законы


            //возвращает все законы с голосами проголосовавшими за них
            List<LawEligibleForParliamentaryApproval> allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances, Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
        }


        return balances;
    }

    public static void deleteFiles(String fileDelit) {
        UtilsFileSaveRead.deleteAllFiles(fileDelit);
    }


    public static Block miningDay(Account minner, List<Block> blockchain, long blockGenerationInterval, int DIFFICULTY_ADJUSTMENT_INTERVAL, List<DtoTransaction> transactionList, Map<String, Account> balances, long index) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Directors directors = new Directors();
        System.out.println("index: " + index);
        //получение транзакций с сети
        List<DtoTransaction> listTransactions = transactionList;
        Base base = new Base58();
        transactionList = transactionList.stream().sorted(Comparator.comparing(t->base.encode(t.getSign()))).collect(Collectors.toList());
        //определение валидных транзакций
        List<DtoTransaction> forAdd = new ArrayList<>();

        //проверяет целостность транзакции, что они подписаны правильно
        cicle:
        for (DtoTransaction transaction : listTransactions) {
            try {
                if (transaction.verify()) {

                    Account account = balances.get(transaction.getSender());
                    if (account == null) {
                        System.out.println("minerAccount null");
                        continue cicle;
                    }

                    if (index > Seting.ALGORITM_MINING) {
                        double digitalDollar = transaction.getDigitalDollar();
                        double digitalStock = transaction.getDigitalStockBalance();
                        double digitalBonus = transaction.getBonusForMiner();
                        if (!UtilsUse.isTransactionValid(BigDecimal.valueOf(digitalDollar))) {
                            System.out.println("the number dollar of decimal places exceeds ." + Seting.SENDING_DECIMAL_PLACES);
                            continue;
                        }
                        if (!UtilsUse.isTransactionValid(BigDecimal.valueOf(digitalStock))) {
                            System.out.println("the number stock of decimal places exceeds ." + Seting.SENDING_DECIMAL_PLACES);
                            continue;
                        }
                        if (!UtilsUse.isTransactionValid(BigDecimal.valueOf(digitalBonus))) {
                            System.out.println("the number bonus of decimal places exceeds ." + Seting.SENDING_DECIMAL_PLACES);
                            continue;
                        }
                    }

                    try {
                        forAdd = UtilsUse.balanceTransaction(forAdd, balances, index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //NAME_LAW_ADDRESS_START если адресс  означает правила выбранные сетью
                    if (transaction.getCustomer().startsWith(Seting.NAME_LAW_ADDRESS_START) && !balances.containsKey(transaction.getCustomer())) {
                        //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
                        //отправитель совпадает с законом
//                    List<Director> enumPosition = directors.getDirectors();
                        List<String> corporateSeniorPositions = directors.getDirectors().stream().map(t -> t.getName()).collect(Collectors.toList());
                        System.out.println("LawsController: create_law: " + transaction.getLaws().getPacketLawName() + "contains: " + corporateSeniorPositions.contains(transaction.getLaws().getPacketLawName()));
                        if (corporateSeniorPositions.contains(transaction.getLaws().getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(transaction.getSender(), transaction.getLaws())) {
                            System.out.println("if your create special corporate position, you need " + "sender to be equals with first law: now its wrong");
                            continue cicle;
                        }
                    }
                    if (transaction.getLaws() == null) {
                        System.out.println("law cannot to be null: ");
                        continue cicle;
                    }

                    if (account != null) {
                        if (transaction.getSender().equals(Seting.BASIS_ADDRESS)) {
                            System.out.println("only this miner can input basis adress in this block");
                            continue cicle;
                        }
                        if (transaction.getCustomer().equals(Seting.BASIS_ADDRESS)) {
                            System.out.println("basis address canot to be customer(recipient)");
                            continue cicle;
                        }


                        if (transaction.getVoteEnum().equals(VoteEnum.STAKING) || transaction.getVoteEnum().equals(VoteEnum.YES) || transaction.getVoteEnum().equals(VoteEnum.NO)) {
                            BigDecimal transactionDigitalDollar = BigDecimal.valueOf(transaction.getDigitalDollar());
                            BigDecimal transactionBonusForMiner = BigDecimal.valueOf(transaction.getBonusForMiner());
                            BigDecimal totalTransactionAmount = transactionDigitalDollar.add(transactionBonusForMiner);

                            if (account.getDigitalDollarBalance().compareTo(totalTransactionAmount) < 0) {
                                System.out.println("sender don't have digital dollar: " + account.getAccount() + " balance: " + account.getDigitalDollarBalance());
                                System.out.println("digital dollar for send: " + totalTransactionAmount);
                                continue;
                            }
                        }

                        BigDecimal transactionDigitalStockBalance = BigDecimal.valueOf(transaction.getDigitalStockBalance());
                        if (account.getDigitalStockBalance().compareTo(transactionDigitalStockBalance) < 0) {
                            System.out.println("sender don't have digital reputation: " + account.getAccount() + " balance: " + account.getDigitalStockBalance());
                            System.out.println("digital reputation for send: " + transactionDigitalStockBalance);
                            continue;
                        }
                        forAdd.add(transaction);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }

        long difficulty = UtilsBlock.difficulty(blockchain, blockGenerationInterval, DIFFICULTY_ADJUSTMENT_INTERVAL);
        if (index >= Seting.V34_NEW_ALGO) {
            difficulty = Mining.customDiff;
        }

        Block prevBlock = blockchain.get(blockchain.size() - 1);


        //доход майнера
        double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
        double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;

        //доход основателя
        double founderReward = Seting.DIGITAL_DOLLAR_FOUNDER_REWARDS_BEFORE;
        double founderDigigtalReputationReward = Seting.DIGITAL_REPUTATION_FOUNDER_REWARDS_BEFORE;
        if (index > Seting.CHECK_UPDATING_VERSION) {
            if (difficulty >= 8) {
                founderReward = difficulty;
                founderDigigtalReputationReward = difficulty;
            } else {
                founderReward = 8;
                founderDigigtalReputationReward = 8;
            }

        }
        if (index > Seting.CHECK_UPDATING_VERSION) {
            minerRewards = difficulty * Seting.MONEY;
            digitalReputationForMiner = difficulty * Seting.MONEY;
            minerRewards += index % 2 == 0 ? 0 : 1;
            digitalReputationForMiner += index % 2 == 0 ? 0 : 1;
        }

        if (index > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX && index < Seting.V34_NEW_ALGO) {
            long money = (index - Seting.V28_CHANGE_ALGORITH_DIFF_INDEX) / (576 * Seting.YEAR);
            money = (long) (Seting.MULTIPLIER - money);
            money = money < 1 ? 1 : money;


            double G = UtilsUse.blocksReward(forAdd, prevBlock.getDtoTransactions(), index);
            minerRewards = (Seting.V28_REWARD + G) * money;
            digitalReputationForMiner = (Seting.V28_REWARD + G) * money;
            founderReward = minerRewards / Seting.DOLLAR;
            founderDigigtalReputationReward = digitalReputationForMiner / Seting.STOCK;
        }
        if (index >= Seting.V34_NEW_ALGO) {
            long money = (index - Seting.V28_CHANGE_ALGORITH_DIFF_INDEX)
                    / (576 * Seting.YEAR);
            money = (long) (Seting.MULTIPLIER - money);
            money = money < 1 ? 1 : money;
            double moneyFromDif = 0;
            double G = UtilsUse.blocksReward(forAdd, prevBlock.getDtoTransactions(), index);
            if (index > Seting.ALGORITM_MINING ) {
                moneyFromDif = (difficulty - DIFFICULT_MONEY) / 2;
                moneyFromDif = moneyFromDif > 0 ? moneyFromDif : 0;
            }

            minerRewards = (Seting.V28_REWARD + G + (difficulty * Seting.V34_MINING_REWARD) + moneyFromDif) * money;
            digitalReputationForMiner = (Seting.V28_REWARD + G + (difficulty * Seting.V34_MINING_REWARD) + moneyFromDif) * money;

            if(index > ALGORITM_MINING_2){
                minerRewards += moneyFromDif * (MULT + G);
                digitalReputationForMiner += moneyFromDif * (MULT + G);
            }

            founderReward = minerRewards / Seting.DOLLAR;
            founderDigigtalReputationReward = digitalReputationForMiner / Seting.STOCK;


            if (BasisController.getBlockchainSize() > Seting.START_BLOCK_DECIMAL_PLACES && index <= ALGORITM_MINING) {
                minerRewards = UtilsUse.round(minerRewards, Seting.DECIMAL_PLACES);
                digitalReputationForMiner = UtilsUse.round(digitalReputationForMiner, Seting.DECIMAL_PLACES);
                founderReward = UtilsUse.round(founderReward, Seting.DECIMAL_PLACES);
                founderDigigtalReputationReward = UtilsUse.round(founderDigigtalReputationReward, Seting.DECIMAL_PLACES);
            }
            if (index > ALGORITM_MINING) {
                minerRewards = UtilsUse.round(minerRewards, SENDING_DECIMAL_PLACES);
                digitalReputationForMiner = UtilsUse.round(digitalReputationForMiner, SENDING_DECIMAL_PLACES);
                founderReward = UtilsUse.round(founderReward, SENDING_DECIMAL_PLACES);
                founderDigigtalReputationReward = UtilsUse.round(founderDigigtalReputationReward, SENDING_DECIMAL_PLACES);
            }
        }


        //суммирует все вознаграждения майнеров
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(Seting.BASIS_PASSWORD));
        double sumRewards = forAdd.stream().collect(Collectors.summingDouble(DtoTransaction::getBonusForMiner));




        String addressFounrder = Blockchain.indexFromFile(0, Seting.ORIGINAL_BLOCKCHAIN_FILE).getFounderAddress();
        if (!addressFounrder.equals(prevBlock.getFounderAddress())) {
            System.out.println("wrong founder address: ");
            return null;
        }
        //вознаграждение основателя
        DtoTransaction founderRew = new DtoTransaction(Seting.BASIS_ADDRESS, addressFounrder, founderReward, founderDigigtalReputationReward, new Laws(), 0.0, VoteEnum.YES);
        byte[] signFounder = UtilsSecurity.sign(privateKey, founderRew.toSign());

        founderRew.setSign(signFounder);


        forAdd.add(founderRew);


        //здесь должна быть создана динамическая модель
        //определение сложности и создание блока


        System.out.println("Mining: miningBlock: difficulty: " + difficulty + " index: " + index);


        if (index == Seting.SPECIAL_BLOCK_FORK && minner.getAccount().equals(Seting.FORK_ADDRESS_SPECIAL)) {
            minerRewards = SPECIAL_FORK_BALANCE;
            digitalReputationForMiner = SPECIAL_FORK_BALANCE;
        }

        //вознаграждения майнера
        DtoTransaction minerRew = new DtoTransaction(Seting.BASIS_ADDRESS, minner.getAccount(), minerRewards, digitalReputationForMiner, new Laws(), sumRewards, VoteEnum.YES);


        //подписывает
        byte[] signGold = UtilsSecurity.sign(privateKey, minerRew.toSign());
        minerRew.setSign(signGold);


        forAdd.add(minerRew);

        forAdd = forAdd.stream().filter(UtilsUse.distinctByKeyString(t -> {
            try {
                return t.getSign() != null ? base.encode(t.getSign()) : null;
            } catch (Exception e) {
                e.printStackTrace();
                return null; // или другое значение по умолчанию
            }
        })).collect(Collectors.toList());

        forAdd = forAdd.stream().filter(t -> t != null).collect(Collectors.toList());
        forAdd = forAdd.stream().sorted(Comparator.comparing(t->base.encode(t.getSign()))).collect(Collectors.toList());
        Block block = new Block(forAdd, prevBlock.getHashBlock(), minner.getAccount(), addressFounrder, difficulty, index);


        return block;
    }
}
