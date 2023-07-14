package International_Trade_Union.statistics;

import lombok.Data;

@Data
public class Statistic {
    private int indexBlock;
    private int periud;

    //все цифровые доллары
    private double allDigitalDollars;
    //все цифровые акции
    private double allDigitalStocks;

    //velocity of money
    //скорость обращения
    private double velocity_of_money_dollar;
    private double velocity_of_money_stock;



    //количество транзакций в сутках
    private int transactions;
    private double sumTransactionsDollar;
    private double sumTransactionsStock;
    private double medianTransactionsDollar;
    private double medianTransactionsStock;

    private int uniqueMinerInDays;
    //сколько добыто цифровых долларов за день


    public Statistic() {
    }

    public Statistic(int indexBlock,
                     int periud,
                     double allDigitalDollars,
                     double allDigitalStocks,
                     double velocity_of_money_dollar,
                     double velocity_of_money_stock,
                     int transactions,
                     double sumTransactionsDollar,
                     double sumTransactionsStock,
                     double medianTransactionsDollar,
                     double medianTransactionsStock,
                     int uniqueMinerInDays) {
        this.indexBlock = indexBlock;
        this.periud = periud;
        this.allDigitalDollars = allDigitalDollars;
        this.allDigitalStocks = allDigitalStocks;
        this.velocity_of_money_dollar = velocity_of_money_dollar;
        this.velocity_of_money_stock = velocity_of_money_stock;
        this.transactions = transactions;
        this.sumTransactionsDollar = sumTransactionsDollar;
        this.sumTransactionsStock = sumTransactionsStock;
        this.medianTransactionsDollar = medianTransactionsDollar;
        this.medianTransactionsStock = medianTransactionsStock;
        this.uniqueMinerInDays = uniqueMinerInDays;
    }
}
