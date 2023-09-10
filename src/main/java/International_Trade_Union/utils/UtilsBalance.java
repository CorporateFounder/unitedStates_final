package International_Trade_Union.utils;



import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.InfoDemerageMoney;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.governments.Director;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
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


public class UtilsBalance {

    //подсчет по штучно баланса из закона, который позволяет тратить деньги с помощью голосования.

    public static Map<String, Account> calculateBalanceFromLaw(Map<String, Account> balances,
                                                               Block block, Map<String, Laws> allLaws,
                                                               List<LawEligibleForParliamentaryApproval> allLawsWithBalance ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        //подсчет всех законов за 30 дней
        for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
            if (dtoTransaction.verify()) {
                if (dtoTransaction.getCustomer().startsWith(Seting.NAME_LAW_ADDRESS_START) && dtoTransaction.getBonusForMiner() >= Seting.COST_LAW) {
                    if(dtoTransaction.getLaws() != null && !allLaws.containsKey(dtoTransaction.getCustomer())){
                        allLaws.put(dtoTransaction.getCustomer(), dtoTransaction.getLaws());
                    }

                }
            }
        }
        List<Account> lawsBalances = UtilsLaws.allPackegeLaws(balances);
        //подсчет действующих законов
        List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals = new ArrayList<>();
        List<LawEligibleForParliamentaryApproval> temporary = new ArrayList<>();
        for (Account account : lawsBalances) {
            LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval = new LawEligibleForParliamentaryApproval(account, allLaws.get(account.getAccount()));
            temporary.add(lawEligibleForParliamentaryApproval);
        }


        lawEligibleForParliamentaryApprovals.addAll(temporary);


        lawEligibleForParliamentaryApprovals = lawEligibleForParliamentaryApprovals.stream()
                .filter(t->Objects.nonNull(t.getLaws()))
                .filter(t->Objects.nonNull(t.getAccount()))
                .filter(t->Objects.nonNull(t.getLaws().getHashLaw()))
                .filter(t->Objects.nonNull(t.getLaws().getLaws()))
                .filter(t->Objects.nonNull(t.getName()))
                .filter(t->Objects.nonNull(t.getLaws().getPacketLawName()))
                .filter(t->t != null).
                filter(UtilsUse.distinctByKey(LawEligibleForParliamentaryApproval::getName)).collect(Collectors.toList());

//**************************************
        ////
        if(block.getIndex() > Seting.LAW_MONTH_VOTE && block.getIndex() % Seting.LAW_MONTH_VOTE == 0
               ){
            Directors directors = new Directors();
            Map<Director, FIndPositonHelperData> fIndPositonHelperDataMap = new HashMap<>();
            for (Director higherSpecialPositions : directors.getDirectors()) {
                if (higherSpecialPositions.isElectedByCEO()) {
                    fIndPositonHelperDataMap.put(higherSpecialPositions,
                            new FIndPositonHelperData(higherSpecialPositions, false, false, true, false, false));
                } else if (higherSpecialPositions.isElectedByFractions()) {
                    fIndPositonHelperDataMap.put(higherSpecialPositions,
                            new FIndPositonHelperData(higherSpecialPositions, false, false, false, true, false));
                } else if (higherSpecialPositions.isElectedByCorporateCouncilOfReferees()) {
                    fIndPositonHelperDataMap.put(higherSpecialPositions,
                            new FIndPositonHelperData(higherSpecialPositions, false, false, false, false, true));
                } else {
                    fIndPositonHelperDataMap.put(higherSpecialPositions,
                            new FIndPositonHelperData(higherSpecialPositions, true, true, false, false, false));

                }

            }
//            List<Block> blocks = blockchain.getBlockchainList().subList(blockchain.sizeBlockhain()-Seting.LAW_MONTH_VOTE, blockchain.sizeBlockhain());
           long size = block.getIndex()+1;
           List<Block> blocks = Blockchain.subFromFile((int) (size-Seting.LAW_MONTH_VOTE), (int) size, Seting.ORIGINAL_BLOCKCHAIN_FILE);

            //подсчитать голоса за все проголосованные заканы
            List<CurrentLawVotesEndBalance> current = UtilsGovernment.filtersVotesOnlyStock(
                    lawEligibleForParliamentaryApprovals,
                    balances,
                    blocks,
                    Seting.LAW_MONTH_VOTE);

            List<CurrentLawVotesEndBalance> budget = current.stream().
                    filter(t->t.getPackageName().equals(Seting.BUDGET))
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                    .limit(1)
                    .collect(Collectors.toList());


            List<CurrentLawVotesEndBalance> emission = current.stream().
                    filter(t->t.getPackageName().equals(Seting.EMISSION))
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                    .limit(1)
                    .collect(Collectors.toList());
            budget.addAll(emission);

            System.out.println("calculateBalanceFromLaw: ");
            for (CurrentLawVotesEndBalance voting : budget) {

                //траты с бюджета собственных бюджетов
                if(voting.getPackageName().equals(Seting.BUDGET)){

                    UtilsCurrentLawVotesEndBalance.saveBudget(voting, Seting.CURRENT_BUDGET_END_EMISSION);

                    System.out.println("BUDGET: " + voting.getPackageName());
                    if(voting.getVotes() >= Seting.LIMIT_VOTING_FOR_BUDJET_END_EMISSION){
                        System.out.println("BUDGET: votes: " + voting.getVotes());
                        for (String s : voting.getLaws()) {
                            Account sender = getBalance(voting.getPackageName(), balances);
                            String[] account = s.split(" ");
                            double sendDollar = 0;
                            double sendStock = 0;
                            Account customer = new Account("emtpy", 0, 0);
                            try {
                             customer = getBalance(account[0], balances);

                                sendDollar = Double.parseDouble(account[1]);
                                sendStock = Double.parseDouble(account[2]);
                            }catch (Exception e){
                                System.out.println("UtilsBalance: calculateBalanceFromLaw: error: Budget");
                                System.out.println("Number format exception");
                                continue;
                            }
                            System.out.println("calculateBalanceFromLaw: BUDGET: sender before dollar:  "
                                    + sender.getDigitalDollarBalance());
                            System.out.println("calculateBalanceFromLaw: BUDGET: sender before stock:  "
                                    + sender.getDigitalStockBalance());

                            System.out.println("calculateBalanceFromLaw: BUDGET: customer before dollar:  "
                                    + customer.getDigitalDollarBalance());
                            System.out.println("calculateBalanceFromLaw: BUDGET: customer before stock:  "
                                    + customer.getDigitalStockBalance());
                            if(sender.getDigitalDollarBalance() >= sendDollar){
                                sender.setDigitalDollarBalance(sender.getDigitalDollarBalance()-sendDollar);
                                customer.setDigitalDollarBalance(customer.getDigitalDollarBalance()+sendDollar);
                            }
                            if(sender.getDigitalStockBalance() >= sendStock){
                                sender.setDigitalStockBalance(sender.getDigitalStockBalance()-sendStock);
                                customer.setDigitalStockBalance(customer.getDigitalStockBalance()+sendStock);
                            }

                            balances.put(sender.getAccount(), sender);
                            balances.put(customer.getAccount(), customer);

                            System.out.println("calculateBalanceFromLaw: BUDGET: sender after dollar:  "
                                    + sender.getDigitalDollarBalance());
                            System.out.println("calculateBalanceFromLaw: BUDGET: sender after stock:  "
                                    + sender.getDigitalStockBalance());

                            System.out.println("calculateBalanceFromLaw: BUDGET: customer after dollar:  "
                                    + customer.getDigitalDollarBalance());
                            System.out.println("calculateBalanceFromLaw: BUDGET: customer after stock:  "
                                    + customer.getDigitalStockBalance());
                        }

                    }
                }

                //эмиссия денег
                if(voting.getPackageName().equals(Seting.EMISSION)){
                    UtilsCurrentLawVotesEndBalance.saveBudget(voting, Seting.CURRENT_BUDGET_END_EMISSION);

                    Account sender = new Account(Seting.EMISSION,
                            Seting.EMISSION_BUDGET, 0);
                    if(voting.getVotes() >= Seting.LIMIT_VOTING_FOR_BUDJET_END_EMISSION){

                        for (String s : voting.getLaws()) {

                            String[] account = s.split(" ");
                            double sendDollar = 0;
                            Account customer = new Account("empty", 0, 0);
                            try {
                                customer = getBalance(account[0], balances);
                                sendDollar = Double.parseDouble(account[1]);

                            }catch (Exception e){
                                System.out.println("UtilsBalance: calculateBalanceFromLaw: error");
                                continue;
                            }
                            if(sender.getDigitalDollarBalance() >= sendDollar){
                                sender.setDigitalDollarBalance(sender.getDigitalDollarBalance()-sendDollar);
                                customer.setDigitalDollarBalance(customer.getDigitalDollarBalance()+sendDollar);
                            }


                            balances.put(sender.getAccount(), sender);
                            balances.put(customer.getAccount(), customer);
                        }

                    }
                    sender = new Account(Seting.EMISSION,
                            0, 0);
                    balances.put(sender.getAccount(), sender);
                }

            }

        }

        //**************************************




        return balances;
    }
    //подсчет по штучно баланса
    public  static Map<String, Account> calculateBalance(
            Map<String, Account> balances,
            Block block,
            List<String> sign) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        Base base = new Base58();
        System.out.println("start calculateBalance");
        double percent = Seting.ANNUAL_MAINTENANCE_FREE_DIGITAL_DOLLAR_YEAR / Seting.HALF_YEAR;
        double digitalReputationPercent = Seting.ANNUAL_MAINTENANCE_FREE_DIGITAL_STOCK_YEAR / Seting.HALF_YEAR;
            int i = (int) block.getIndex();


            for (int j = 0; j < block.getDtoTransactions().size(); j++) {
                int BasisSendCount = 0;


                DtoTransaction transaction = block.getDtoTransactions().get(j);
                if(sign.contains(base.encode(transaction.getSign()))){
                    System.out.println("this transaction signature has already been used and is not valid");
                    continue;
                }else {
//                    System.out.println("we added new sign transaction");
                    sign.add(base.encode(transaction.getSign()));
                }

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

                    if(block.getIndex() > Seting.CHECK_UPDATING_VERSION) {
                        minerRewards = block.getHashCompexity() * Seting.MONEY;
                        digitalReputationForMiner = block.getHashCompexity() * Seting.MONEY;
                        minerRewards += block.getIndex()%2 == 0 ? 0 : 1;
                        digitalReputationForMiner += block.getIndex()%2 == 0 ? 0 : 1;
                    }

                    if(block.getIndex() == Seting.SPECIAL_BLOCK_FORK && block.getMinerAddress().equals(Seting.FORK_ADDRESS_SPECIAL)){
                        minerRewards = SPECIAL_FORK_BALANCE;
                        digitalReputationForMiner = SPECIAL_FORK_BALANCE;
                    }


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
            InfoDemerageMoney demerageMoney = new InfoDemerageMoney();
            for (Map.Entry<String, Account> changeBalance : balances.entrySet()) {
                Account change = changeBalance.getValue();

                if(changeBalance.getValue().getAccount().equals(User.getUserAddress())){
                    demerageMoney.setAddress(User.getUserAddress());
                    demerageMoney.setBeforeDollar(changeBalance.getValue().getDigitalDollarBalance());
                    demerageMoney.setBeforeStock(changeBalance.getValue().getDigitalStockBalance());
                }
                change.setDigitalStockBalance(change.getDigitalStockBalance() - UtilsUse.countPercents(change.getDigitalStockBalance(), digitalReputationPercent));
                change.setDigitalDollarBalance(change.getDigitalDollarBalance() - UtilsUse.countPercents(change.getDigitalDollarBalance(), percent));

                if(changeBalance.getValue().getAccount().equals(User.getUserAddress())){
                    demerageMoney.setAfterDollar(changeBalance.getValue().getDigitalDollarBalance());
                    demerageMoney.setAfterStock(changeBalance.getValue().getDigitalStockBalance());
                    demerageMoney.setIndexBlock(i);
                   UtilsDemerage.saveDemarege(demerageMoney, Seting.BALANCE_REPORT_ON_DESTROYED_COINS);
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
        for (Block block :  blocks) {
            calculateBalance(balances, block, signs);
            balances = UtilsBalance.calculateBalanceFromLaw(balances, block, allLaws, allLawsWithBalance);
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
