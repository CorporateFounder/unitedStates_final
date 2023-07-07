# VOTE_STOCK (How shares are voted.)
How shares are voted.
1. The number of shares is equal to the number of votes.
2. Your votes are recounted every block and if you lose your shares,
   or increase your shares, your cast votes also change
   according to the number of shares.
3. For each law that you voted, for this law, all
   FOR and AGAINST and after that with FOR minus AGAINST and this is the rating of the law.
4. Your votes are divided separately for all the laws that you voted FOR and separately AGAINST
   Example: you have 100 shares and you voted FOR one candidate and for one law,
   you also voted AGAINST two candidates and two laws.
   Now each of your candidates and the law for which you voted FOR will receive 50 votes.
   and for which you voted AGAINST will receive 25 votes AGAINST.
   the formula is simple FOR/number of laws and AGAINST/number of laws you are against.

______

````
public double votesLaw(Map<String, Account> balances,
                           Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;


        //
        for (String s : YES) {

            int count = 1;
            count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;
            yes += balances.get(s).getDigitalStockBalance() / count;

        }
        //
        for (String s : NO) {
            int count = 1;
            count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;
            no += balances.get(s).getDigitalStockBalance() / count;

        }


        return yes - no;
    }
````

[back to home](../documentationEng/documentationEng.md)