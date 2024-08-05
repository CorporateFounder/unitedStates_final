package International_Trade_Union.governments;


import International_Trade_Union.model.Account;

public class CompareObject {

    public int compare(Account o1, Account o2) {
        double x1 = o1.getDigitalStockBalance().doubleValue();
        double x2 = o2.getDigitalStockBalance().doubleValue();
        int power = Double.compare(x1, x2);
        if (power != 0)
            return power;
        double gold1 = o1.getDigitalDollarBalance().doubleValue();
        double gold2 = o2.getDigitalDollarBalance().doubleValue();
        int gold = Double.compare(gold1, gold2);
        if(gold != 0)
            return gold;
        double first = x1 + gold1;
        double second = x2 + gold2;
        return Double.compare(first, second);
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
