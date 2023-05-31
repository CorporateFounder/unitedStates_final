# VOTE_STOCK (How shares are voted.)

How shares are voted.
All shares held by the account are equal to the same number of votes.
Every time someone makes a transaction to the account, is the packet address that starts with
LIBER he votes on this package. Only those votes from which no more than four years have passed are taken into account.
If the transaction was made ***VoteEnum.YES,*** then this account receives votes ***for***, formula
yesV = number of votes equal to the sender's shares.
yesN = how many laws this account voted for with VoteEnum.YES
resultYES = yesV / yesN). Example: an account voted for three accounts that start with LIBER,
100 shares, 100 votes. 100 / 3 = 33.3 means each account will receive 33.3 votes.

If the transaction was made with VoteEnum.NO,
then the same formula is used, but now all bills for which he voted against are taken into account
example the same account voted for two accounts against, it has the same one hundred shares.
resultNO = noV / noN = 50 = 50 means every bill he voted for,
against will receive 50 votes against.
Further, each score that starts with LIBER counts and sums up all the votes given to it ***FOR*** (VoteEnum.YES)
and ***NO*** (VoteEnum.NO).
Then this formula is used remainder = resultYES - resultNO.
It is this result that is displayed as votes cast.
At any time you can change your voice, but only to the opposite, which means if
If you voted for a YES candidate then you can only change to NO and back.
The number of times you can change your voice is not limited.
With each block there is a recalculation of votes, if you lose your shares, your candidates
also lose their votes. This measure is specifically implemented so that elected positions
were interested in the fact that those who vote for them prospered and did not lose their shares.
Only CORPORATE_COUNCIL_OF_REFEREES and BOARD_OF_DIRECTORS are elected this way
Only the last transaction given for each account counts, unless you have updated your vote,
then after four years it will be cancelled.
100,000 votes are needed to approve the law

______

````
//code is in class class CurrentLawVotes method: votesLaw
public double votesLaw(Map<String, Account> balances,
      Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
         double yes = 0.0;
         double no = 0.0;
       
              
        for (String s : YES) {

             int count = 1;
           count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;
           yes += balances.get(s).getDigitalStockBalance() / count;

        }
        
         for (String s : NO) {
            int count = 1;
             count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;
            no += balances.get(s).getDigitalStockBalance() / count);

         }


         return yes - no;
    }

````

[back to home](../documentationEng/documentationEng.md)