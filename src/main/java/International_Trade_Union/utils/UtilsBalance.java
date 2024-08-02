package International_Trade_Union.utils;


import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.LawEligibleForParliamentaryApproval;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static International_Trade_Union.setings.Seting.SPECIAL_FORK_BALANCE;
//wallet

public class UtilsBalance {
    private static BlockService blockService;

    public static BlockService getBlockService() {
        return blockService;
    }

    public static void setBlockService(BlockService blockService) {
        UtilsBalance.blockService = blockService;
    }

    /**Возвращает баланс обратно, нужно когда есть множество веток.*/
    public static Map<String, Account> rollbackCalculateBalance(
            Map<String, Account> balances,
            Block block
    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();
        System.out.println("start rollbackCalculateBalance: index: " + block.getIndex());
        int i = (int) block.getIndex();

        int BasisSendCount = 0;
        for (int j = 0; j < block.getDtoTransactions().size(); j++) {

            DtoTransaction transaction = block.getDtoTransactions().get(j);
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
                } else if (block.getIndex() >= Seting.V34_NEW_ALGO) {
                    minerRewards = 1500;
                    digitalReputationForMiner = 1500;
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
                sendTrue = UtilsBalance.rollBackSendMoney(
                        sender,
                        customer,
                        transaction.getDigitalDollar(),
                        transaction.getDigitalStockBalance(),
                        transaction.getBonusForMiner(),
                        transaction.getVoteEnum());


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
    /**Подсчитывает баланс счетов*/
    public static Map<String, Account> calculateBalance(
            Map<String, Account> balances,
            Block block,
            List<String> sign) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        Base base = new Base58();
        System.out.println("calculateBalance: index: " + block.getIndex());
        int i = (int) block.getIndex();

        int BasisSendCount = 0;
        for (int j = 0; j < block.getDtoTransactions().size(); j++) {



            DtoTransaction transaction = block.getDtoTransactions().get(j);
            if(blockService != null){
                if(blockService.existsBySign(transaction.getSign())){
                    System.out.println("this transaction signature has already been used and is not valid from db");
                    continue;
                }
            }
            if (sign.contains(base.encode(transaction.getSign()))) {
                System.out.println("this transaction signature has already been used and is not valid");
                continue;
            } else {
//                    System.out.println("we added new sign transaction");
                sign.add(base.encode(transaction.getSign()));
            }

            if (transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)) {
                System.out.println("law balance cannot be sender");
                continue;
            }
            Account sender = getBalance(transaction.getSender(), balances);
            Account customer = getBalance(transaction.getCustomer(), balances);
            if (transaction.verify()) {
                if (transaction.getSender().equals(Seting.BASIS_ADDRESS)){
                    BasisSendCount++;
                    if (sender.getAccount().equals(Seting.BASIS_ADDRESS) && BasisSendCount > 2) {
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
                } else if (block.getIndex() >= Seting.V34_NEW_ALGO) {
                    minerRewards = 1500;
                    digitalReputationForMiner = 1500;
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
                sendTrue = UtilsBalance.sendMoney(
                        sender,
                        customer,
                        transaction.getDigitalDollar(),
                        transaction.getDigitalStockBalance(),
                        transaction.getBonusForMiner(),
                        transaction.getVoteEnum());


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
            Account account = new Account(address, 0.0, 0.0, 0.0);
            return account;
        }
    }


    public static Account findAccount(Blockchain blockList, String address) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> accountMap = calculateBalances(blockList.getBlockchainList());
        Account account = accountMap.get(address);
        return account != null ? account : new Account(address, 0.0, 0.0, 0.0);
    }

    public static boolean sendMoney(Account senderAddress, Account recipientAddress, double digitalDollar, double digitalReputation, double minerRewards) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return sendMoney(senderAddress, recipientAddress, digitalDollar, digitalReputation, minerRewards, VoteEnum.YES);
    }

    /**Переписывает баланс, от отправителя к получателю*/
    public static boolean sendMoney(Account senderAddress, Account recipientAddress, double digitalDollar, double digitalStock, double minerRewards, VoteEnum voteEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        double senderDigitalDollar = senderAddress.getDigitalDollarBalance();
        double  senderDigitalStock = senderAddress.getDigitalStockBalance();
        double senderDigitalStaking = senderAddress.getDigitalStakingBalance();
        double recipientDigitalDollar = recipientAddress.getDigitalDollarBalance();
        double recipientDigitalStock = recipientAddress.getDigitalStockBalance();
        double recipientDigitalStaking = recipientAddress.getDigitalStakingBalance();
        boolean sendTrue = true;


        if(BasisController.getBlockchainSize() > Seting.START_BLOCK_DECIMAL_PLACES){
            senderDigitalDollar = UtilsUse.round(senderDigitalDollar,  Seting.DECIMAL_PLACES);
            senderDigitalStock = UtilsUse.round(senderDigitalStock,  Seting.DECIMAL_PLACES);
            senderDigitalStaking = UtilsUse.round(senderDigitalStaking,  Seting.DECIMAL_PLACES);

            recipientDigitalDollar = UtilsUse.round(recipientDigitalDollar,  Seting.DECIMAL_PLACES);
            recipientDigitalStock = UtilsUse.round(recipientDigitalStock,  Seting.DECIMAL_PLACES);
            recipientDigitalStaking = UtilsUse.round(recipientDigitalStaking,  Seting.DECIMAL_PLACES);
            digitalDollar = UtilsUse.round(digitalDollar,  Seting.DECIMAL_PLACES);
            digitalStock = UtilsUse.round(digitalStock,  Seting.DECIMAL_PLACES);
            minerRewards = UtilsUse.round(minerRewards,  Seting.DECIMAL_PLACES);
        }
        if (!senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            if (senderDigitalStock < digitalStock) {
                System.out.println("less stock");
                sendTrue = false;

            } else if (recipientAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
                System.out.println("Basis canot to be recipient;");
                sendTrue = false;
            } else if((voteEnum.equals(VoteEnum.YES)  || voteEnum.equals(VoteEnum.NO))){
                if (senderAddress.getAccount().equals(recipientAddress.getAccount())) {
                    System.out.println("sender %s, recipient %s cannot be equals! Error!".format(senderAddress.getAccount(), recipientAddress.getAccount()));
                    sendTrue = false;
                    return sendTrue;
                }
                if (senderDigitalDollar < digitalDollar + minerRewards) {
                    System.out.println("less dollar");
                    sendTrue = false;
                    return sendTrue;
                }

                senderAddress.setDigitalDollarBalance(senderDigitalDollar - digitalDollar);
                senderAddress.setDigitalStockBalance(senderDigitalStock - digitalStock);
                recipientAddress.setDigitalDollarBalance(recipientDigitalDollar + digitalDollar);
                //сделано чтобы можно было увеличить или отнять власть
                if (voteEnum.equals(VoteEnum.YES)) {
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock + digitalStock);
                } else if (voteEnum.equals(VoteEnum.NO)) {
                    //политика сдерживания.
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock - digitalStock);
                }


            }
            else if (voteEnum.equals(VoteEnum.STAKING)) {
                System.out.println("STAKING: ");
                if (senderDigitalDollar < digitalDollar + minerRewards) {
                    System.out.println("less dollar");
                    sendTrue = false;
                    return sendTrue;
                }
                senderAddress.setDigitalDollarBalance(senderDigitalDollar - digitalDollar);
                senderAddress.setDigitalStakingBalance(senderDigitalStaking + digitalDollar);
            }
            else if (voteEnum.equals(VoteEnum.UNSTAKING)) {
                System.out.println("UNSTAKING");
                if (senderDigitalStaking < digitalDollar) {
                    System.out.println("less staking");
                    sendTrue = false;
                    return sendTrue;
                }
                senderAddress.setDigitalDollarBalance(senderDigitalDollar + digitalDollar);
                senderAddress.setDigitalStakingBalance(senderDigitalStaking - digitalDollar);
            }


        } else if (senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {

            recipientAddress.setDigitalDollarBalance(recipientDigitalDollar + digitalDollar);
            recipientAddress.setDigitalStockBalance(recipientDigitalStock + digitalStock);

        }
        return sendTrue;
    }

    /**Откатывает транзакции, чтобы мы могли переключиться в новую ветку.*/
    public static boolean rollBackSendMoney(
            Account senderAddress,
            Account recipientAddress,
            double digitalDollar, double digitalStock, double minerRewards, VoteEnum voteEnum){
        double senderDigitalDollar = senderAddress.getDigitalDollarBalance();
        double  senderDigitalStock = senderAddress.getDigitalStockBalance();
        double senderDigitalStaking = senderAddress.getDigitalStakingBalance();
        double recipientDigitalDollar = recipientAddress.getDigitalDollarBalance();
        double recipientDigitalStock = recipientAddress.getDigitalStockBalance();
        double recipientDigitalStaking = recipientAddress.getDigitalStakingBalance();

        boolean sendTrue = true;
        if(BasisController.getBlockchainSize() > Seting.START_BLOCK_DECIMAL_PLACES){
            senderDigitalDollar = UtilsUse.round(senderDigitalDollar,  Seting.DECIMAL_PLACES);
            senderDigitalStock = UtilsUse.round(senderDigitalStock,  Seting.DECIMAL_PLACES);
            senderDigitalStaking = UtilsUse.round(senderDigitalStaking,  Seting.DECIMAL_PLACES);

            recipientDigitalDollar = UtilsUse.round(recipientDigitalDollar,  Seting.DECIMAL_PLACES);
            recipientDigitalStock = UtilsUse.round(recipientDigitalStock,  Seting.DECIMAL_PLACES);
            recipientDigitalStaking = UtilsUse.round(recipientDigitalStaking,  Seting.DECIMAL_PLACES);
            digitalDollar = UtilsUse.round(digitalDollar,  Seting.DECIMAL_PLACES);
            digitalStock = UtilsUse.round(digitalStock,  Seting.DECIMAL_PLACES);
            minerRewards = UtilsUse.round(minerRewards,  Seting.DECIMAL_PLACES);
        }

        if (!senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
                if((voteEnum.equals(VoteEnum.YES)  || voteEnum.equals(VoteEnum.NO))){
//

                senderAddress.setDigitalDollarBalance(senderDigitalDollar + digitalDollar);
                senderAddress.setDigitalStockBalance(senderDigitalStock + digitalStock);
                recipientAddress.setDigitalDollarBalance(recipientDigitalDollar - digitalDollar);
                //сделано чтобы можно было увеличить или отнять власть
                if (voteEnum.equals(VoteEnum.YES)) {
                    System.out.println("YES");
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock - digitalStock);
                } else if (voteEnum.equals(VoteEnum.NO)) {
                    System.out.println("NO");
                    //политика сдерживания.
                    recipientAddress.setDigitalStockBalance(recipientDigitalStock + digitalStock);
                }


            }
            else if (voteEnum.equals(VoteEnum.STAKING)) {
                System.out.println("STAKING: ");

                senderAddress.setDigitalDollarBalance(senderDigitalDollar + digitalDollar);
                senderAddress.setDigitalStakingBalance(senderDigitalStaking - digitalDollar);
            }
            else if (voteEnum.equals(VoteEnum.UNSTAKING)) {
                System.out.println("UNSTAKING");

                senderAddress.setDigitalDollarBalance(senderDigitalDollar - digitalDollar);
                senderAddress.setDigitalStakingBalance(senderDigitalStaking + digitalDollar);
            }


        } else if (senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            System.out.println("BASIS_ADDRESS");
            System.out.println(" recipientAddress: before " + recipientAddress);
            recipientAddress.setDigitalDollarBalance(recipientDigitalDollar - digitalDollar);
            recipientAddress.setDigitalStockBalance(recipientDigitalStock - digitalStock);
            System.out.println(" recipientAddress: after " + recipientAddress);

        }
        System.out.println("sendTrue: " + sendTrue);
        return sendTrue;
    }





}
