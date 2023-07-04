# ONE_VOTE (One Voice)

When these positions are voted count as one score = one vote
(CORPORATE_COUNCIL_OF_REFEREES-Council of Corporate Judges,
GENERAL_EXECUTIVE_DIRECTOR-General Executive Director,
HIGH_JUDGE - Supreme Judge and Board of Shareholders).
Each score that starts with LIBER counts all votes FOR (VoteEnum.YES) and AGAINST (VoteEnum.NO) for it
further deducted from FOR - AGAINST = if the balances are above the threshold, then it becomes the current law. But if a position is elected,
then after that it is sorted from largest to smallest and the largest number that is described for this position is selected.
Recalculation of votes occurs every block.

After voting, the vote can only be changed to the opposite one.
There is no limit on the number of times you can change your vote. Only those votes that are given by accounts are taken into account
in his position, for example, if the account ceased to be in CORPORATE_COUNCIL_OF_REFEREES, his vote as
CORPORATE_COUNCIL_OF_REFEREES does not count and will not count in voting. All votes are valid until the bills
voters are in their positions. Only those votes from which no more than
four years, but each participant may at any time renew their vote.

______

CODE class CurrentLawVotes: method voteGovernment

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
[back to home](../documentationEng/documentationEng.md)