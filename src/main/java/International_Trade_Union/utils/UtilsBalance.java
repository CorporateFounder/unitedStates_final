package International_Trade_Union.utils;



import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.model.Account;
import International_Trade_Union.vote.VoteEnum;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;


public class UtilsBalance {

    //подсчет по штучно баланса
    public  static Map<String, Account> calculateBalance(Map<String, Account> balances, Block block) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {


        double percent = Seting.ANNUAL_MAINTENANCE_FREE_DIGITAL_DOLLAR_YEAR / Seting.HALF_YEAR;
        double digitalReputationPercent = Seting.ANNUAL_MAINTENANCE_FREE_DIGITAL_STOCK_YEAR / Seting.HALF_YEAR;
            int i = (int) block.getIndex();


            for (int j = 0; j < block.getDtoTransactions().size(); j++) {
                int BasisSendCount = 0;


                DtoTransaction transaction = block.getDtoTransactions().get(j);

                if(transaction.getSender().startsWith(Seting.NAME_LAW_ADDRESS_START)){
                    System.out.println("law balance cannot be sender");
                    continue;
                }
                if (transaction.verify()) {
                    if(transaction.getSender().equals(Seting.BASIS_ADDRESS))
                        BasisSendCount++;


                    Account sender = getBalance(transaction.getSender(), balances);
                    Account customer = getBalance(transaction.getCustomer(), balances);

                    boolean sendTrue = true;
                    if(sender.getAccount().equals(Seting.BASIS_ADDRESS) && BasisSendCount > 2){
                        System.out.println("Basis address can send only two the base address can send no more than two times per block:" + Seting.BASIS_ADDRESS);
                        continue;
                    }

                    double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
                    double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;



                    if(sender.getAccount().equals(Seting.BASIS_ADDRESS) ){
                       if(i > 1 && (transaction.getDigitalDollar() > minerRewards || transaction.getDigitalStockBalance() > digitalReputationForMiner )){
                           System.out.println("rewards cannot be upper than " + minerRewards);
                           continue;
                       }
                        if(!customer.getAccount().equals(block.getFounderAddress()) && !customer.getAccount().equals(block.getMinerAddress())){
                            System.out.println("Basis address can send only to founder or miner");
                            continue;
                        }
                    }
                    sendTrue = UtilsBalance.sendMoney(sender, customer, transaction.getDigitalDollar(), transaction.getDigitalStockBalance(), transaction.getBonusForMiner(), transaction.getVoteEnum());

                    //если транзация валидная то записать данн иыезменения в баланс
                    if(sendTrue){
                        balances.put(sender.getAccount(), sender);
                        balances.put(customer.getAccount(), customer);
                    }

                }

            }


        if (i != 0 && i / Seting.COUNT_BLOCK_IN_DAY % (Seting.YEAR / Seting.HALF_YEAR) == 0.0) {

            for (Map.Entry<String, Account> changeBalance : balances.entrySet()) {
                Account change = changeBalance.getValue();
                change.setDigitalStockBalance(change.getDigitalStockBalance() - UtilsUse.countPercents(change.getDigitalStockBalance(), digitalReputationPercent));
                change.setDigitalDollarBalance(change.getDigitalDollarBalance() - UtilsUse.countPercents(change.getDigitalDollarBalance(), percent));
            }
        }


        return balances;

    }
    //подсчет целиком баланса
    public static Map<String, Account> calculateBalances(List<Block> blocks) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> balances = new HashMap<>();
        for (Block block :  blocks) {
            calculateBalance(balances, block);
        }

        return balances;

    }



    public static Account getBalance(String address, Map<String, Account> balances) {
        if (balances.containsKey(address)) {
            return balances.get(address);
        } else {
            Account account = new Account(address, 0.0, 0.0);
            return account;
        }
    }


    public static Account findAccount(Blockchain blockList, String address) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Map<String, Account> accountMap = calculateBalances(blockList.getBlockchainList());
        Account account = accountMap.get(address);
        return account != null? account: new Account(address, 0.0, 0.0);
    }

    public static boolean sendMoney(Account senderAddress, Account recipientAddress, double digitalDollar, double digitalReputation, double minerRewards) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException {
        return sendMoney(senderAddress, recipientAddress, digitalDollar, digitalReputation, minerRewards, VoteEnum.YES);
    }

    public static boolean sendMoney(Account senderAddress, Account recipientAddress, double digitalDollar, double digitalReputation, double minerRewards, VoteEnum voteEnum) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, IOException, SignatureException, InvalidKeyException {
        double remnantDigitalDollar = 0.0;
        double remnantDigitalReputation = 0.0;
        boolean sendTrue = true;
        if (senderAddress.getAccount().equals(recipientAddress.getAccount())) {
            System.out.println("sender %s, recipient %s cannot be equals! Error!".format(senderAddress.getAccount(), recipientAddress.getAccount()));
            sendTrue = false;
        }

        remnantDigitalDollar = senderAddress.getDigitalDollarBalance();
        remnantDigitalReputation = senderAddress.getDigitalStockBalance();

        if (!senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
            if(remnantDigitalDollar < digitalDollar + minerRewards){
                sendTrue = false;
            }
            else if(remnantDigitalReputation < digitalReputation){
                    System.out.printf("sender power %f, les than powerSend:  %f\n",
                            senderAddress.getDigitalStockBalance(), digitalReputation);
                    sendTrue = false;

            } else if (recipientAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {
                System.out.println("Basis canot to be recipient;");
                sendTrue = false;
            } else {

                senderAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance() - digitalDollar);
                senderAddress.setDigitalStockBalance(senderAddress.getDigitalStockBalance() - digitalReputation);
                recipientAddress.setDigitalDollarBalance(recipientAddress.getDigitalDollarBalance() + digitalDollar);
                //сделано чтобы можно было увеличить или отнять власть
                if (voteEnum.equals(VoteEnum.YES)) {
                    recipientAddress.setDigitalStockBalance(recipientAddress.getDigitalStockBalance() + digitalReputation);
                } else if (voteEnum.equals(VoteEnum.NO)) {
                    //политика сдерживания.
                    recipientAddress.setDigitalStockBalance(recipientAddress.getDigitalStockBalance() - digitalReputation);
                }

            }


        }  else if (senderAddress.getAccount().equals(Seting.BASIS_ADDRESS)) {

            recipientAddress.setDigitalDollarBalance(recipientAddress.getDigitalDollarBalance() + digitalDollar);
            recipientAddress.setDigitalStockBalance(recipientAddress.getDigitalStockBalance() + digitalReputation);

        }
        return sendTrue;
    }
}
