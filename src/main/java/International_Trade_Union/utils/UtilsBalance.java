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
import International_Trade_Union.vote.UtilsCurrentLaw;
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

import static International_Trade_Union.setings.Seting.*;
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
                    if (i > 1 && (transaction.getDigitalDollar() > minerRewards || transaction.getDigitalStockBalance() > digitalReputationForMiner) && block.getIndex() < MONEY_MILTON_FRIDMAN_INDEX) {
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
            List<String> sign,
            List<String> signaturesNotTakenIntoAccount) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

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



            //здесь идет проверка по подписи, сначала проверяется, была ли эта подпись в базе данных для
            //транзакции, если ее нет, то проверяется во временном списке, который был предоставлен
            //если нет, то в список добавляется эта подпись и продолжается процедура.
            DtoTransaction transaction = transactions.get(j);
            if (blockService != null && block.getIndex() <= CHECK_DUBLICATE_IN_DB_BLOCK) {
                if (blockService.existsBySign(transaction.getSign()) && !signaturesNotTakenIntoAccount.contains(base.encode(transaction.getSign()))) {
                    MyLogger.saveLog("this transaction signature has already been used and is not valid from db: index: " + block.getIndex() + " signature: " + base.encode(transaction.getSign()));
                    System.out.println("this transaction signature has already been used and is not valid from db");
                    continue;
                }else {
                    if(sign.contains(base.encode(transaction.getSign()))){
                        MyLogger.saveLog("this transaction signature has already been used and is not valid from signList: index: " + block.getIndex() + " signature: " + base.encode(transaction.getSign()));
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
            boolean verifyTransaction = transaction.verify();
            if (verifyTransaction == false){
                String json = UtilsJson.objToStringJson(transaction);
                MyLogger.saveLog("verifyTransaction failed" + verifyTransaction + "json: " + json + " index: "  + block.getIndex());
                DtoTransaction tempTransaction = UtilsJson.jsonToDtoTransaction(json);
                verifyTransaction = tempTransaction.verify();
                MyLogger.saveLog("repeat Transaction: verify" + verifyTransaction + "json: " + json + " index: "  + block.getIndex());
                for (int k = 0; k < 5; k++) {
                    json = UtilsJson.objToStringJson(transaction);
                    tempTransaction = UtilsJson.jsonToDtoTransaction(json);
                    verifyTransaction = tempTransaction.verify();
                    MyLogger.saveLog("repeat Transaction: verify" + verifyTransaction + "json: " + json + " index: "  + block.getIndex() + "reate: " + k);
                    if(verifyTransaction == true)
                        break;
                }
//
            }
            if (verifyTransaction) {
                //BASIS_ADDRESS это специальный адрес, который отправляет награду шахтеру и основателю в каждом
                //блоке должна быть 1 транзакция награда шахтеру, и 1 основателю.
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
                    if (i > 1 && (transaction.getDigitalDollar() > minerRewards || transaction.getDigitalStockBalance() > digitalReputationForMiner) && block.getIndex() < MONEY_MILTON_FRIDMAN_INDEX) {
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

                    //здесь идет подсчет баланса, если правильно, то потом это фиксируется.
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
            else {
                MyLogger.saveLog("------------------------------------");

                MyLogger.saveLog("wrong transaction calculateBalance: verify: " + verifyTransaction );
                MyLogger.saveLog("wrong transaction calculateBalance: verify repeat: " + transaction.verify());
                MyLogger.saveLog("wrong transaction calculateBalance: transaction: " + transaction);
                MyLogger.saveLog("wrong transaction calculateBalance: transaction json: " + UtilsJson.objToStringJson(transaction));
                MyLogger.saveLog("wrong transaction calculateBalance: block: " + block);
                String json = UtilsJson.objToStringJson(transaction);
                DtoTransaction transaction1 = UtilsJson.jsonToDtoTransaction(json);
                MyLogger.saveLog("verify after json: " + transaction1.verify());
                MyLogger.saveLog("verify after json: " + transaction1);
                MyLogger.saveLog("------------------------------------");
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
            calculateBalance(balances, block, signs, new ArrayList<>());
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

                senderAddress.setDigitalDollarBalance(senderDigitalDollar.add(digitalDollar));
                senderAddress.setDigitalStakingBalance(senderDigitalStaking.subtract(digitalDollar));
                recipientAddress.setDigitalDollarBalance(senderAddress.getDigitalDollarBalance());
                recipientAddress.setDigitalStakingBalance(senderAddress.getDigitalStakingBalance());
            } else if (voteEnum.equals(VoteEnum.UNSTAKING)) {

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

    public static boolean sendFromBudget(Laws budgetLaw, Map<String, Account> balances, Account budget){
        System.out.println("Sending money from budget");
        List<String> temp = budgetLaw.getLaws();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // First, validate all entries
        for (String s : temp) {
            String[] send = s.split(" ");

            // Check if the entry has exactly two parts: address and amount
            if (send.length != 2) {
                System.out.println("Invalid format: " + s + ". Expected 'address amount'.");
                return false;
            }

            String address = send[0];
            String amountStr = send[1];

            // Check if the address exists in balances
            if (!balances.containsKey(address)) {
                System.out.println("Invalid address: " + address);
                return false;
            }

            // Check if the amount is a valid number
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address);
                return false;
            }

            // Check if the amount is positive
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must be positive.");
                return false;
            }

            // Accumulate the total amount to be sent
            totalAmount = totalAmount.add(amount);
        }

        // Check if the total amount exceeds the budget's balance
        if (totalAmount.compareTo(budget.getDigitalDollarBalance()) > 0){
            System.out.println("Total amount to send (" + totalAmount + ") exceeds budget balance (" + budget.getDigitalDollarBalance() + ").");
            return false;
        }

        // All validations passed, perform the transfers
        for (String s : temp) {
            String[] send = s.split(" ");
            String address = send[0];
            BigDecimal amount = new BigDecimal(send[1]);

            Account account = balances.get(address);
            System.out.println("-----------------------");
            System.out.println("Account before send: " + account);

            // Update the recipient's balance
            account.setDigitalDollarBalance(account.getDigitalDollarBalance().add(amount));

            // Update the budget's balance
            budget.setDigitalDollarBalance(budget.getDigitalDollarBalance().subtract(amount));

            System.out.println("Account after send: " + account);
            System.out.println("-----------------------");
        }

        return true;
    }
    public static boolean rollbackFromBudget(Laws budgetLaw, Map<String, Account> balances, Account budget) {
        System.out.println("Rolling back money to budget");
        List<String> temp = budgetLaw.getLaws();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Сначала валидируем все записи
        for (String s : temp) {
            String[] send = s.split(" ");

            // Проверка, что запись содержит ровно два элемента: адрес и сумма
            if (send.length != 2) {
                System.out.println("Invalid format: " + s + ". Expected 'address amount'.");
                return false;
            }

            String address = send[0];
            String amountStr = send[1];

            // Проверка, что адрес существует в балансах
            if (!balances.containsKey(address)) {
                System.out.println("Invalid address: " + address);
                return false;
            }

            // Проверка, что сумма является валидным числом
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address);
                return false;
            }

            // Проверка, что сумма положительная
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must be positive.");
                return false;
            }

            // Проверка, что сумма имеет не меньше 8 десятичных знаков
            if (amount.scale() < 8) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must have at least 8 decimal places.");
                return false;
            }

            // Проверка, что сумма учитывает только последние 8 знаков без округления
            BigDecimal truncatedAmount = amount.setScale(8, BigDecimal.ROUND_DOWN);
            if (amount.compareTo(truncatedAmount) != 0) {
                System.out.println("Invalid amount: " + amountStr + " for address: " + address + ". Amount must have exactly 8 decimal places without rounding.");
                return false;
            }

            // Проверка, что у аккаунта достаточно средств для снятия
            Account account = balances.get(address);
            if (account.getDigitalDollarBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient funds: " + address + " has " + account.getDigitalDollarBalance() + ", attempted to withdraw " + amount);
                return false;
            }

            // Накопление общей суммы для проверки после валидации всех записей
            totalAmount = totalAmount.add(amount);
        }

        // Все проверки пройдены, выполняем откат транзакций
        for (String s : temp) {
            String[] send = s.split(" ");
            String address = send[0];
            BigDecimal amount = new BigDecimal(send[1]);

            Account account = balances.get(address);
            System.out.println("-----------------------");
            System.out.println("Account before rollback: " + account);

            // Снятие средств с аккаунта
            account.setDigitalDollarBalance(account.getDigitalDollarBalance().subtract(amount));

            // Добавление средств в бюджет
            budget.setDigitalDollarBalance(budget.getDigitalDollarBalance().add(amount));

            System.out.println("Account after rollback: " + account);
            System.out.println("Budget after rollback: " + budget);
            System.out.println("-----------------------");
        }

        return true;
    }


}
