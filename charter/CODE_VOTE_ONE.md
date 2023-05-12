# CODE_VOTE_ONE (Код Один голос)

КОД class CurrentLawVotes: method voteGovernment

````
public int voteGovernment(
            Map<String, Account> balances,
            List<String> governments) {
       int yes = 0;
        int no = 0;

       List<String> addressGovernment = governments;
      for (String s : YES) {
            if (addressGovernment.contains(s)) {
                yes += Seting.VOTE_GOVERNMENT;
           }

        }
        for (String s : NO) {
          if (addressGovernment.contains(s)) {
                no += Seting.VOTE_GOVERNMENT;
           }
        }
       return yes - no;
   } 

````

[возврат на главную](../readme.md)