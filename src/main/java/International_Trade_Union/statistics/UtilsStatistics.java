package International_Trade_Union.statistics;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsUse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilsStatistics {
    public static Statistic statistic(List<Block> list, Map<String, Account> balances, Periud periud){
        int indexBlock = (int) list.get(list.size()-1).getIndex();
        int thisPeriud = indexBlock / periud.getPeriud();
        double allDigitalDollar = 0;
        double allDigitalStock = 0;

        double velocity_of_money_dollar = 0;
        double velocity_of_money_dollar_model2 = 0;

        double velocity_of_money_stock = 0;
        double velocity_of_money_stock_model2 = 0;

        int transactions = 0;
        int countDollarTransactions = 0;
        int countStocktransactions = 0;

        double sumTransactionsDollar = 0;
        double sumTransactionsStock = 0;
        double medianDollar = 0;
        double medianStock = 0;
        int uniqueMinerDaySize = 0;
        System.out.println("start method Utils statistic: ");
        double sumDollarSender = 0;
        double sumStockSender = 0;
        double medianBalanceDollar = 0;
        double medianBalanceStock = 0;

                System.out.println("calculate all digital dollars end stocks");
        for (Map.Entry<String, Account> accountEntry : balances.entrySet()) {
            allDigitalDollar += accountEntry.getValue().getDigitalDollarBalance();
            allDigitalStock += accountEntry.getValue().getDigitalStockBalance();
        }

        List<DtoTransaction> allTransactions = new ArrayList<>();
        List<String> uniqueMinersInDay = new ArrayList<>();

        List<Double> dollarsList = new ArrayList<>();
        List<Double> stocksList = new ArrayList<>();

        System.out.println("calculate transactions dollar end stock");
        for (Block block : list) {
            uniqueMinersInDay.add(block.getMinerAddress());
            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {

                if(!dtoTransaction.getSender().equals(Seting.BASIS_ADDRESS)){
                  allTransactions.add(dtoTransaction);
                  sumTransactionsDollar += dtoTransaction.getDigitalDollar();
                  sumTransactionsStock += dtoTransaction.getDigitalStockBalance();

                  if(dtoTransaction.getDigitalDollar() > 0){
                      dollarsList.add(dtoTransaction.getDigitalDollar());
                      sumDollarSender += balances.get(dtoTransaction.getSender())
                              .getDigitalDollarBalance();
                  }

                  if(dtoTransaction.getDigitalStockBalance() > 0){
                      stocksList.add(dtoTransaction.getDigitalStockBalance());
                      sumStockSender += balances.get(dtoTransaction.getSender())
                              .getDigitalStockBalance();
                  }

                }
            }

        }

        //все транзакции в сутки
        System.out.println("transactions size: ");
        transactions = allTransactions.size();
        System.out.println("uniqueMinersInDay");
        uniqueMinersInDay = uniqueMinersInDay.stream().distinct().collect(Collectors.toList());
        //уникальные адреса майнеров в сутки
        uniqueMinerDaySize = uniqueMinersInDay.size();

        dollarsList = dollarsList.stream().sorted().collect(Collectors.toList());
        stocksList = stocksList.stream().sorted().collect(Collectors.toList());


        System.out.println("calculate medians dollar");
        if(dollarsList.size() > 3)
            medianDollar = UtilsUse.median(dollarsList);
        System.out.println("medianDollar: " + medianDollar);
        System.out.println("calculate medians stock");
        if(stocksList.size() > 3)
             medianStock = UtilsUse.median(stocksList);
        System.out.println("medianStock: " + medianStock);

        //скорость обращения денег
        //сумма всех счетов отправителей за периуд
        if(medianDollar > 0 && dollarsList.size() > 3){
            velocity_of_money_dollar = (medianDollar * dollarsList.size())/ allDigitalDollar;
            velocity_of_money_dollar_model2 = (medianDollar * dollarsList.size())/ sumDollarSender;
        }

        if(medianStock > 0 && stocksList.size() > 3){
            velocity_of_money_stock = (medianStock * stocksList.size())/ allDigitalStock;
            velocity_of_money_stock_model2 = (medianStock * stocksList.size())/sumStockSender;
        }


        List<Double> medianBalanceDollarList = balances.entrySet()
                .stream()
                .filter(t->t.getValue().getDigitalDollarBalance()>0)
                .map(t->t.getValue().getDigitalDollarBalance())
                .collect(Collectors.toList());

        List<Double>medinBalanceStockList = balances.entrySet()
                .stream()
                .filter(t->t.getValue().getDigitalStockBalance() >0)
                .map(t->t.getValue().getDigitalStockBalance())
                .collect(Collectors.toList());

            medianBalanceDollar = UtilsUse.median(medianBalanceDollarList);
            medianBalanceStock = UtilsUse.median(medinBalanceStockList);

        int accountsSize = (int) balances.entrySet().stream()
                .map(t->t.getValue().getDigitalDollarBalance() > 0 || t.getValue().getDigitalStockBalance() > 0)
                .count();
        Statistic statistic = new Statistic(
                indexBlock,
                thisPeriud,
                allDigitalDollar,
                allDigitalStock,
                velocity_of_money_dollar,
                velocity_of_money_dollar_model2,
                velocity_of_money_stock,
                velocity_of_money_stock_model2,
                transactions,
                dollarsList.size(),
                stocksList.size(),
                sumTransactionsDollar,
                sumTransactionsStock,
                medianDollar,
                medianStock,
                uniqueMinerDaySize,
                accountsSize,
                medianBalanceDollar,
                medianBalanceStock

        );
        return statistic;
    }

}
