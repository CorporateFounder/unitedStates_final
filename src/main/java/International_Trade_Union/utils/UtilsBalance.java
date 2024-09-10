package International_Trade_Union.utils;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;

import International_Trade_Union.model.Account;
import International_Trade_Union.model.MyLogger;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.setings.Seting.SPECIAL_FORK_BALANCE;
//wallet

public class UtilsBalance {
    private static BlockService blockService;

    public static long INDEX = 0;
    public static BlockService getBlockService() {
        return blockService;
    }

    public static void setBlockService(BlockService blockService) {
        UtilsBalance.blockService = blockService;
    }

    /**
     * Возвращает баланс обратно, нужно когда есть множество веток.
     */
    public static Map<String, Account> rollbackCalculateBalance(
            Map<String, Account> balances,
            Block block
    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();
        System.out.println("start rollbackCalculateBalance: index: " + block.getIndex());
        int i = (int) block.getIndex();
        List<DtoTransaction> transactions = block.getDtoTransactions();
        transactions = transactions.stream()
                .sorted(Comparator.comparing(t -> base.encode(t.getSign())))
                .collect(Collectors.toList());


        int BasisSendCount = 0;
        for (int j = 0; j < transactions.size(); j++) {

            DtoTransaction transaction = transactions.get(j);
            if (transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                System.out.println("law balance cannot be sender");
                continue;
            }
            if (transaction.verify()) {
                if (transaction.getSender().equals(Seting.BASIS_ADDRESS))
                    BasisSendCount++;


                Account sender = getBalance(transaction.getSender(), balances);
                Account customer = getBalance(transaction.getCustomer(), balances);

                boolean sendTrue = true;
                if (sender.getAccount().equals(Seting.BASIS_ADDRESS) && BasisSendCount > 2) {
                    System.out.println("Basis address can send only two the base address can send no more than two times per block:" + Seting.BASIS_ADDRESS);
                    continue;
                }

                double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
                double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;

                if (block.getIndex() > Seting.CHECK_UPDATING_VERSION) {
                    minerRewards = block.getHashCompexity() * Seting.MONEY;
                    digitalReputationForMiner = block.getHashCompexity() * Seting.MONEY;
                    minerRewards += block.getIndex() % 2 == 0 ? 0 : 1;
                    digitalReputationForMiner += block.getIndex() % 2 == 0 ? 0 : 1;
                }

                if (block.getIndex() > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX && block.getIndex() < Seting.V34_NEW_ALGO) {
                    minerRewards = 261;
                    digitalReputationForMiner = 261;
                } else if (block.getIndex() >= Seting.V34_NEW_ALGO && block.getIndex() <= Seting.ALGORITM_MINING) {
                    minerRewards = 1500;
                    digitalReputationForMiner = 1500;
                }else if(block.getIndex() > Seting.NEW_ALGO_MINING){
                    minerRewards = 12000;
                    digitalReputationForMiner = 12000;
                }

                if (block.getIndex() == Seting.SPECIAL_BLOCK_FORK && block.getMinerAddress().equals(Seting.FORK_ADDRESS_SPECIAL)) {
                    minerRewards = SPECIAL_FORK_BALANCE;
                    digitalReputationForMiner = SPECIAL_FORK_BALANCE;
                }


                if (sender.getAccount().equals(Seting.BASIS_ADDRESS)) {
                    if (i > 1 && (transaction.getDigitalDollar() > minerRewards || transaction.getDigitalStockBalance() > digitalReputationForMiner)) {
                        System.out.println("rewards cannot be upper than " + minerRewards);
                        System.out.println("rewards cannot be upper than " + digitalReputationForMiner);
                        System.out.println("rewards dollar: " + transaction.getDigitalDollar());
                        System.out.println("rewards stock: " + transaction.getDigitalStockBalance());
                        continue;
                    }
                    if (!customer.getAccount().equals(block.getFounderAddress()) && !customer.getAccount().equals(block.getMinerAddress())) {
                        System.out.println("Basis address can send only to founder or miner");
                        continue;
                    }
                }

                BigDecimal digitalDollar = null;
                BigDecimal digitalStock = null;
                BigDecimal mine = null;

//                if(block.getIndex() > Seting.FROM_STRING_DOUBLE){
//                    digitalDollar = new BigDecimal(Double.toString(transaction.getDigitalDollar()));
//                    digitalStock = new BigDecimal(Double.toString(transaction.getDigitalStockBalance()));
//                    mine = new BigDecimal(Double.toString(transaction.getBonusForMiner()));
//                    UtilsBalance.INDEX = block.getIndex();
//                    sendTrue = UtilsBalance.rollBackSendMoneyNew(
//                            sender,
//                            customer,
//                            digitalDollar,
//                            digitalStock,
//                            mine,
//                            transaction.getVoteEnum()
//                    );
//                }else {
                    digitalDollar = BigDecimal.valueOf(transaction.getDigitalDollar());
                    digitalStock = BigDecimal.valueOf(transaction.getDigitalStockBalance());
                    mine = BigDecimal.valueOf(transaction.getBonusForMiner());

                    sendTrue = UtilsBalance.rollBackSendMoney(
                            sender,
                            customer,
                            digitalDollar,
                            digitalStock,
                            mine,
                            transaction.getVoteEnum()
                    );
//                }





                //если транзация валидная то записать данн иыезменения в баланс
                if (sendTrue) {
                    balances.put(sender.getAccount(), sender);
                    balances.put(customer.getAccount(), customer);
                }

            }

        }


        System.out.println("finish calculateBalance");
        return balances;

    }

    /**
     * Подсчитывает баланс счетов
     */
    public static Map<String, Account> calculateBalance(
            Map<String, Account> balances,
            Block block,
            List<String> sign) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        Base base = new Base58();
        System.out.println("calculateBalance: index: " + block.getIndex());
        if(balances == null || balances.size() == 0){
            MyLogger.saveLog("balances null: " + balances + " index block: " + block.getIndex());
        }

        int i = (int) block.getIndex();

        List<DtoTransaction> transactions = block.getDtoTransactions();
        transactions = transactions.stream()
                .sorted(Comparator.comparing(t -> base.encode(t.getSign())))
                .collect(Collectors.toList());


        int BasisSendCount = 0;
        for (int j = 0; j < transactions.size(); j++) {


            DtoTransaction transaction = transactions.get(j);
            if (blockService != null) {
                if (blockService.existsBySign(transaction.getSign())) {
                    MyLogger.saveLog("this transaction signature has already been used and is not valid from db: index: " + block.getIndex() + " signature: " + base.encode(transaction.getSign()));
                    System.out.println("this transaction signature has already been used and is not valid from db");
                    continue;
                }else {
                    if(sign.contains(base.encode(transaction.getSign()))){
                        System.out.println("this transaction signature has already been used and is not valid from list");
                        continue;
                    }else {
                        sign.add(base.encode(transaction.getSign()));
                    }
                }
            }

            if (transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                System.out.println("law balance cannot be sender");
                continue;
            }
            Account sender = getBalance(transaction.getSender(), balances);
            Account customer = getBalance(transaction.getCustomer(), balances);
            if (transaction.verify()) {
                if (transaction.getSender().equals(Seting.BASIS_ADDRESS)) {
                    BasisSendCount++;
                    if (sender.getAccount().equals(Seting.BASIS_ADDRESS) && BasisSendCount > 2) {
                        MyLogger.saveLog("Basis address can send only two the base address can send no more than two times per block:");
                        System.out.println("Basis address can send only two the base address can send no more than two times per block:" + Seting.BASIS_ADDRESS);
                        continue;
                    }
                }


                boolean sendTrue = true;


                double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
                double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;

                if (block.getIndex() > Seting.CHECK_UPDATING_VERSION) {
                    minerRewards = block.getHashCompexity() * Seting.MONEY;
                    digitalReputationForMiner = block.getHashCompexity() * Seting.MONEY;
                    minerRewards += block.getIndex() % 2 == 0 ? 0 : 1;
                    digitalReputationForMiner += block.getIndex() % 2 == 0 ? 0 : 1;
                }

                if (block.getIndex() > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX && block.getIndex() < Seting.V34_NEW_ALGO) {
                    minerRewards = 261;
                    digitalReputationForMiner = 261;
                } else if (block.getIndex() >= Seting.V34_NEW_ALGO && block.getIndex() <= Seting.ALGORITM_MINING) {
                    minerRewards = 1500;
                    digitalReputationForMiner = 1500;
                }else if(block.getIndex() > Seting.NEW_ALGO_MINING){
                    minerRewards = 12000;
                    digitalReputationForMiner = 12000;
                }

                if (block.getIndex() == Seting.SPECIAL_BLOCK_FORK && block.getMinerAddress().equals(Seting.FORK_ADDRESS_SPECIAL)) {
                    minerRewards = SPECIAL_FORK_BALANCE;
                    digitalReputationForMiner = SPECIAL_FORK_BALANCE;
                }


                if (sender.getAccount().equals(Seting.BASIS_ADDRESS)) {
                    if (i > 1 && (transaction.getDigitalDollar() > minerRewards || transaction.getDigitalStockBalance() > digitalReputationForMiner)) {
                        MyLogger.saveLog("rewards cannot be upper than " + minerRewards);
                        MyLogger.saveLog("rewards dollar: " + transaction.getDigitalDollar());
                        MyLogger.saveLog("rewards stock: " + transaction.getDigitalStockBalance());
                        System.out.println("rewards cannot be upper than " + minerRewards);
                        System.out.println("rewards cannot be upper than " + digitalReputationForMiner);
                        System.out.println("rewards dollar: " + transaction.getDigitalDollar());
                        System.out.println("rewards stock: " + transaction.getDigitalStockBalance());
                        continue;
                    }
                    if (!customer.getAccount().equals(block.getFounderAddress()) && !customer.getAccount().equals(block.getMinerAddress())) {
                        System.out.println("Basis address can send only to founder or miner");
                        MyLogger.saveLog("Basis address can send only to founder or miner");
                        continue;
                    }
                }
                BigDecimal digitalDollar = null;
                BigDecimal digitalStock = null;
                BigDecimal mine = null;


                    digitalDollar = BigDecimal.valueOf(transaction.getDigitalDollar());
                    digitalStock = BigDecimal.valueOf(transaction.getDigitalStockBalance());
                    mine = BigDecimal.valueOf(transaction.getBonusForMiner());
                    sendTrue = UtilsBalance.sendMoney(
                            sender,
                            customer,
                            digitalDollar,
                            digitalStock,
                            mine,
                            transaction.getVoteEnum()

                    );

                //если транзация валидная то записать данн иыезменения в баланс
                if (sendTrue) {
                    balances.put(sender.getAccount(), sender);
                    balances.put(customer.getAccount(), customer);


                }

            }

        }



//        System.out.println("finish calculateBalance");
        return balances;

    }

    //подсчет целиком баланса
    public static Map<String, Account> calculateBalances(List<Block> blocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = new HashMap<>();
        List<String> signs = new ArrayList<>();
        Map<String, Laws> allLaws = new HashMap<>();
        List<LawEligibleForParliamentaryApproval> allLawsWithBalance = new ArrayList<>();
        for (Block block : blocks) {
            calculateBalance(balances, block, signs);
        }

        return balances;

    }


    public static Account getBalance(String address, Map<String, Account> balances) {
        if (balances.containsKey(address)) {
            return balances.get(address);
        } else {
            Account account = new Account(address, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            return account;
        }
    }


    public static Account findAccount(Blockchain blockList, String address) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> accountMap = calculateBalances(blockList.getBlockchainList());
        Account account = accountMap.get(address);
        return account != null ? account : new Account(address, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static boolean sendMoney(Account senderAddress, Account recipientAddress, double digitalDollar, double digitalReputation, double minerRewards, long index) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {

        return sendMoney(senderAddress, recipientAddress, BigDecimal.valueOf(digitalDollar), BigDecimal.valueOf(digitalReputation), BigDecimal.valueOf(minerRewards), VoteEnum.YES);


    }

    /**
     * Переписывает баланс, от отправителя к получателю
     */


    public static boolean sendMoney(Account senderAddress, Account recipientAddress, BigDecimal digitalDollar, BigDecimal digitalStock, BigDecimal minerRewards, VoteEnum voteEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        BigDecimal senderDigitalDollar = senderAddress.getDigitalDollarBalance();
        BigDecimal senderDigitalStock = senderAddress.getDigitalStockBalance();
        BigDecimal senderDigitalStaking = senderAddress.getDigitalStakingBalance();
        BigDecimal recipientDigitalDollar = recipientAddress.getDigitalDollarBalance();
        BigDecimal recipientDigitalStock = recipientAddress.getDigitalStockBalance();

        boolean sendTrue = true;


        if (!senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            if (senderDigitalStock.compareTo(digitalStock) < 0) {
                System.out.println("less stock");
                MyLogger.saveLog("less stock:senderDigitalStock " + senderDigitalStock + " digitalStock " + digitalStock + " sender: " + senderAddress );
                sendTrue = false;
            } else if (recipientAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
                System.out.println("Basis cannot be recipient");
                MyLogger.saveLog("Basis cannot be recipient: " );
                sendTrue = false;
            } else if (voteEnum.equals(VoteEnum.YES) || voteEnum.equals(VoteEnum.NO)) {
                if (senderAddress.getAccount().equals(recipientAddress.getAccount())) {
                    System.out.printf("sender %s, recipient %s cannot be equals! Error!%n", senderAddress.getAccount(), recipientAddress.getAccount());
                    MyLogger.saveLog(String.format("sender %s, recipient %s cannot be equals! Error!%n", senderAddress.getAccount(), recipientAddress.getAccount()));
                    sendTrue = false;
                    return sendTrue;
                }
                if (senderDigitalDollar.compareTo(digitalDollar.add(minerRewards)) < 0) {
                    System.out.println("less dollar");
                    sendTrue = false;
                    return sendTrue;
                }

                senderAddress.setDigitalDollarBalance(senderDigitalDollar.subtract(digitalDollar));
                senderAddress.setDigitalStockBalance(senderDigitalStock.subtract(digitalStock));
                recipientAddress.setDigitalDollarBalance(recipientDigitalDollar.add(digitalDollar));

                if (voteEnum.equals(VoteEnum.YES)) {
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock.add(digitalStock));
                } else if (voteEnum.equals(VoteEnum.NO)) {
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock.subtract(digitalStock));
                }

            } else if (voteEnum.equals(VoteEnum.STAKING)) {
                System.out.println("STAKING: ");
                if (senderDigitalDollar.compareTo(digitalDollar.add(minerRewards)) < 0) {
                    System.out.println("less dollar");
                    System.out.println("sender: " + senderAddress);
                    System.out.println("minerRewards: " + minerRewards);

                    sendTrue = false;
                    return sendTrue;
                }


                senderAddress.setDigitalDollarBalance(senderDigitalDollar.subtract(digitalDollar));
                senderAddress.setDigitalStakingBalance(senderDigitalStaking.add(digitalDollar));
                recipientAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance());
                recipientAddress.setDigitalStakingBalance(senderAddress.getDigitalStakingBalance());
            } else if (voteEnum.equals(VoteEnum.UNSTAKING)) {
                System.out.println("UNSTAKING");
                if (senderDigitalStaking.compareTo(digitalDollar.add(minerRewards)) < 0) {
                    System.out.println("less staking");
                    sendTrue = false;
                    return sendTrue;
                }
                senderAddress.setDigitalDollarBalance(senderDigitalDollar.add(digitalDollar));
                senderAddress.setDigitalStakingBalance(senderDigitalStaking.subtract(digitalDollar));
                recipientAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance());
                recipientAddress.setDigitalStakingBalance(senderAddress.getDigitalStakingBalance());
            }

        } else if (senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            recipientAddress.setDigitalDollarBalance(recipientDigitalDollar.add(digitalDollar));
            recipientAddress.setDigitalStockBalance(recipientDigitalStock.add(digitalStock));
        }

        return sendTrue;
    }

    public static boolean rollBackSendMoney(Account senderAddress, Account recipientAddress, BigDecimal digitalDollar, BigDecimal digitalStock, BigDecimal minerRewards, VoteEnum voteEnum) {
        BigDecimal senderDigitalDollar = senderAddress.getDigitalDollarBalance();
        BigDecimal senderDigitalStock = senderAddress.getDigitalStockBalance();
        BigDecimal senderDigitalStaking = senderAddress.getDigitalStakingBalance();
        BigDecimal recipientDigitalDollar = recipientAddress.getDigitalDollarBalance();
        BigDecimal recipientDigitalStock = recipientAddress.getDigitalStockBalance();
        BigDecimal recipientDigitalStaking = recipientAddress.getDigitalStakingBalance();

        boolean sendTrue = true;

        if (!senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            if (voteEnum.equals(VoteEnum.YES) || voteEnum.equals(VoteEnum.NO)) {

                if (senderAddress.getAccount().equals(recipientAddress.getAccount())) {
                    System.out.printf("sender %s, recipient %s cannot be equals! Error!%n", senderAddress.getAccount(), recipientAddress.getAccount());
                    sendTrue = false;
                    return sendTrue;
                }
                senderAddress.setDigitalDollarBalance(senderDigitalDollar.add(digitalDollar));
                senderAddress.setDigitalStockBalance(senderDigitalStock.add(digitalStock));
                recipientAddress.setDigitalDollarBalance(recipientDigitalDollar.subtract(digitalDollar));


                if (voteEnum.equals(VoteEnum.YES)) {
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock.subtract(digitalStock));
                } else if (voteEnum.equals(VoteEnum.NO)) {
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock.add(digitalStock));
                }

            } else if (voteEnum.equals(VoteEnum.STAKING)) {
                System.out.println("STAKING: ");
                if (senderDigitalStaking.compareTo(digitalDollar.add(minerRewards)) < 0) {
                    System.out.println("less dollar");
                    System.out.println("sender: " + senderAddress);
                    System.out.println("minerRewards: " + minerRewards);

                    sendTrue = false;
                    return sendTrue;
                }
                senderAddress.setDigitalDollarBalance(senderDigitalDollar.add(digitalDollar));
                senderAddress.setDigitalStakingBalance(senderDigitalStaking.subtract(digitalDollar));
                recipientAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance());
                recipientAddress.setDigitalStakingBalance(senderAddress.getDigitalStakingBalance());
            } else if (voteEnum.equals(VoteEnum.UNSTAKING)) {
                System.out.println("UNSTAKING");

                if (senderDigitalDollar.compareTo(digitalDollar.add(minerRewards)) < 0) {
                    System.out.println("less dollar");
                    System.out.println("sender: " + senderAddress);
                    System.out.println("minerRewards: " + minerRewards);

                    sendTrue = false;
                    return sendTrue;
                }
                senderAddress.setDigitalDollarBalance(senderDigitalDollar.subtract(digitalDollar));
                senderAddress.setDigitalStakingBalance(senderDigitalStaking.add(digitalDollar));
                recipientAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance());
                recipientAddress.setDigitalStakingBalance(senderAddress.getDigitalStakingBalance());
            }

        } else if (senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            System.out.println("BASIS_ADDRESS");
            System.out.println(" recipientAddress: before " + recipientAddress);
            recipientAddress.setDigitalDollarBalance(recipientDigitalDollar.subtract(digitalDollar));
            recipientAddress.setDigitalStockBalance(recipientDigitalStock.subtract(digitalStock));
            System.out.println(" recipientAddress: after " + recipientAddress);
        }

        System.out.println("sendTrue: " + sendTrue);
        return sendTrue;
    }





}
