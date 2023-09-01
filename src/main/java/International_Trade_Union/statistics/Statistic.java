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
    private double velocity_of_money_dollar_model2;
    private double velocity_of_money_stock;
    private double velocity_of_money_stock_model2;



    //количество транзакций в сутках
    private int transactions;
    private int countDollarTransactions;
    private int countStocktransactions;
    private double sumTransactionsDollar;
    private double sumTransactionsStock;
    private double medianTransactionsDollar;
    private double medianTransactionsStock;

    private int uniqueMinerInDays;
    private int uniqueAddressBalanceUpperZero;
    private double medianBalanceDollar;
    private double medianBalanceStock;
    private int difficultyMod;
    private double growthAccountPercent;

    public Statistic() {
    }

    public Statistic(int indexBlock,
                     int periud,
                     double allDigitalDollars,
                     double allDigitalStocks,
                     double velocity_of_money_dollar,
                     double velocity_of_money_dollar_model2,
                     double velocity_of_money_stock,
                     double velocity_of_money_stock_model2,
                     int transactions,
                     int countDollarTransactions,
                     int countStocktransactions,
                     double sumTransactionsDollar,
                     double sumTransactionsStock,
                     double medianTransactionsDollar,
                     double medianTransactionsStock,
                     int uniqueMinerInDays,
                     int uniqueAddressBalanceUpperZero,
                     double medianBalanceDollar,
                     double medianBalanceStock,
                     int difficultyMod) {
        this.indexBlock = indexBlock;
        this.periud = periud;
        this.allDigitalDollars = allDigitalDollars;
        this.allDigitalStocks = allDigitalStocks;
        this.velocity_of_money_dollar = velocity_of_money_dollar;
        this.velocity_of_money_dollar_model2 = velocity_of_money_dollar_model2;
        this.velocity_of_money_stock = velocity_of_money_stock;
        this.velocity_of_money_stock_model2 = velocity_of_money_stock_model2;
        this.transactions = transactions;
        this.countDollarTransactions = countDollarTransactions;
        this.countStocktransactions = countStocktransactions;
        this.sumTransactionsDollar = sumTransactionsDollar;
        this.sumTransactionsStock = sumTransactionsStock;
        this.medianTransactionsDollar = medianTransactionsDollar;
        this.medianTransactionsStock = medianTransactionsStock;
        this.uniqueMinerInDays = uniqueMinerInDays;
        this.uniqueAddressBalanceUpperZero = uniqueAddressBalanceUpperZero;
        this.medianBalanceDollar = medianBalanceDollar;
        this.medianBalanceStock = medianBalanceStock;
        this.difficultyMod = difficultyMod;
    }
}
