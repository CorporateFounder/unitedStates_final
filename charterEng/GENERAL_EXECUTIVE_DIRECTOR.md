# GENERAL_EXECUTIVE_DIRECTOR General Executive Director
This Director coordinates the actions of other senior directors to implement the strategic plan or
tasks assigned to it by current laws.
All powers must be given to him through existing laws.
This is the highest position elected by the Corporation and is essentially equivalent to the Prime Minister.

## How the General Executive Director is elected
This director is elected by the Legislature
1. the candidate must receive a rating from BOARD_OF_DIRECTORS of 10 or more [ONE_VOTE](../charterEng/ONE_VOTE.md)
2. Network participants must give more than one vote using the [VOTE_STOCK](../charterEng/VOTE_STOCK.md) method
3. Next, sorting occurs from the largest to the smallest votes received from the shares and
4. One account with the largest number of votes received from BOARD_OF_DIRECTORS is selected.
5. If the founder vetoed a given candidate, then you need to get
   votes BOARD_OF_DIRECTORS 10 or more according to the [ONE_VOTE](../charterEng/ONE_VOTE.md) system
   and votes of the council of judges 2 or more votes according to the [ONE_VOTE](../charterEng/ONE_VOTE.md) system

````
     List<String> hightJudge = new ArrayList<>();
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE
                ){
                    primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));
                }
            }
        }
````

[Exit to home](../documentationEng/documentationEng.md)