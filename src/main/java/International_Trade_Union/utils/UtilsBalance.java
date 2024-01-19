package International_Trade_Union.utils;


import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.InfoDemerageMoney;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.model.FIndPositonHelperData;
import International_Trade_Union.model.User;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.model.Account;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.*;


import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.stream.Collectors;

import static International_Trade_Union.setings.Seting.SPECIAL_FORK_BALANCE;
//wallet

public class UtilsBalance {

    //подсчет по штучно баланса
    public static Map<String, Account> calculateBalance(
            Map<String, Account> balances,
            Block block,
            List<String> sign) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        Base base = new Base58();
        System.out.println("start calculateBalance: index: " + block.getIndex());
        int i = (int) block.getIndex();


        for (int j = 0; j < block.getDtoTransactions().size(); j++) {
            int BasisSendCount = 0;


            DtoTransaction transaction = block.getDtoTransactions().get(j);
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
                    minerRewards = 850;
                    digitalReputationForMiner = 850;
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
                        transaction.getVoteEnum(),
                        block.getIndex());
                Account miner = balances.get(block.getMinerAddress());
                miner = miner == null?
                        new Account(block.getMinerAddress(),
                                0,
                                0,
                                0,
                                0): miner;
                miner.setEpoch(block.getIndex());
                balances.put(miner.getAccount(), miner);

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
            Account account = new Account(address, 0.0, 0.0, 0, 0);
            return account;
        }
    }


    public static Account findAccount(Blockchain blockList, String address) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> accountMap = calculateBalances(blockList.getBlockchainList());
        Account account = accountMap.get(address);
        return account != null ? account : new Account(address, 0.0, 0.0, 0, 0);
    }

    public static boolean sendMoney(Account senderAddress, Account recipientAddress, double digitalDollar, double digitalReputation, double minerRewards) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return sendMoney(senderAddress, recipientAddress, digitalDollar, digitalReputation, minerRewards, VoteEnum.YES, 0);
    }

    public static boolean sendMoney(Account senderAddress, Account recipientAddress, double digitalDollar, double digitalStock, double minerRewards, VoteEnum voteEnum, long index) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        double remnantDigitalDollar = 0.0;
        double remnantDigitalStock = 0.0;
        double remnantDigitalStaking = 0.0;
        boolean sendTrue = true;

        remnantDigitalDollar = senderAddress.getDigitalDollarBalance();
        remnantDigitalStock = senderAddress.getDigitalStockBalance();
        remnantDigitalStaking = senderAddress.getDigitalStakingBalance();

        if (!senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            if (remnantDigitalDollar < digitalDollar + minerRewards) {
                System.out.println("less dollar");
                sendTrue = false;
            } else if (remnantDigitalStock < digitalStock) {
                System.out.println("less stock");
                sendTrue = false;

            } else if (voteEnum.equals(VoteEnum.UNSTAKING) && remnantDigitalStaking < digitalDollar) {
                System.out.println("less staking");
                sendTrue = false;
            } else if (recipientAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
                System.out.println("Basis canot to be recipient;");
                sendTrue = false;
            } else if((voteEnum.equals(VoteEnum.YES)  || voteEnum.equals(VoteEnum.NO)) &&
                        !senderAddress.getAccount().equals(recipientAddress.getAccount())){

                senderAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance() - digitalDollar);
                senderAddress.setDigitalStockBalance(senderAddress.getDigitalStockBalance() - digitalStock);
                recipientAddress.setDigitalDollarBalance(recipientAddress.getDigitalDollarBalance() + digitalDollar);
                //сделано чтобы можно было увеличить или отнять власть
                if (voteEnum.equals(VoteEnum.YES)) {
                    recipientAddress.setDigitalStockBalance(recipientAddress.getDigitalStockBalance() + digitalStock);
                } else if (voteEnum.equals(VoteEnum.NO)) {
                    //политика сдерживания.
                    recipientAddress.setDigitalStockBalance(recipientAddress.getDigitalStockBalance() - digitalStock);
                }


            } else if (voteEnum.equals(VoteEnum.STAKING)) {
                System.out.println("STAKING: ");
                senderAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance() - digitalDollar);
                senderAddress.setDigitalStakingBalance(senderAddress.getDigitalStakingBalance() + digitalDollar);
                senderAddress.setEpoch(index);
            } else if (voteEnum.equals(VoteEnum.UNSTAKING)) {
                System.out.println("UNSTAKING");
                senderAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance() + digitalDollar);
                senderAddress.setDigitalStakingBalance(senderAddress.getDigitalStakingBalance() - digitalDollar);
                senderAddress.setEpoch(index);
            }


        } else if (senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {

            recipientAddress.setDigitalDollarBalance(recipientAddress.getDigitalDollarBalance() + digitalDollar);
            recipientAddress.setDigitalStockBalance(recipientAddress.getDigitalStockBalance() + digitalStock);

        }
        return sendTrue;
    }
}
