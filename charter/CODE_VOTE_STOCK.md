# CODE_VOTE_STOCK (Код голосования с помощью акций)

````
//код находится в классе class CurrentLawVotes method: votesLaw 
public double votesLaw(Map<String, Account> balances,
     Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;
       
              
       for (String s : YES) {

            int count = 1;
          count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;
          yes += balances.get(s).getDigitalStockBalance() / Math.pow(count, Seting.POWERING_FOR_VOTING);

       }
        
        for (String s : NO) {
           int count = 1;
            count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;
           no += balances.get(s).getDigitalStockBalance() / Math.pow(count, Seting.POWERING_FOR_VOTING);

        }


        return yes - no;
   } 

````

[возврат на главную](../readme.md)