# VOTE_STOCK (How shares are voted.)

How shares are voted.
1. The number of shares is equal to the number of votes.
2. Your votes are recounted every block and if you lose your shares,
   or increase your shares, your cast votes also change
   according to the number of shares.
3. For each law that you voted, for this law, all
   FOR and AGAINST and after that AGAINST is taken away from FOR and this is the rating of the law.

______

````
//code is in class class CurrentLawVotes method: votesLaw
public double votesLaw(Map<String, Account> balances,
                           Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;


        //
        for (String s : YES) {

            yes += balances.get(s).getDigitalStockBalance();
        }
        //
        for (String s : NO) {
            no += balances.get(s).getDigitalStockBalance();

        }


        return yes - no;
    }
````

[back to home](../documentationEng/documentationEng.md)