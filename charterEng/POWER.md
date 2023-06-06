#LEGISLATURE.
Power consists of 4 groups in this system.
1. Board of Shareholders
2. Board of Directors
3. Fractions
4. Independent members of the network.

All participants must participate in the vote for the law adopted by the system to be valid
(The only exception is the Board of Shareholders, since the Board of Shareholders participates
only in the approval of amendments to the Charter).
For all votes, only votes cast in the last four years count.
All members may hold multiple positions from different groups, but may not
hold more than one position in the same category.
Example: One account can be both ***Member of the Network*** and ***Member of the Board of Directors***
and ***Member of the Board of Shareholders***, but one account cannot hold multiple seats on the Board of Directors
or in the Board of Shareholders.

It is this part of the vote that is taken into account when electing the Board of Directors and Fractions.
![stock_vote](../screenshots/stock_vote.png)
## Board of Shareholders
The Board of Shareholders is automatically appointed by the system.
The Board of Shareholders consists of 1500 accounts with the largest number of shares,
but only those accounts are selected that have either been mining in the last year,
either sent digital dollars or digital shares, or participated in voting.
A member of one Board of Shareholders has one vote. One score equals one vote.
The voting system described in [ONE_VOTE](../charterEng/ONE_VOTE.md) is used

````
   //determining the board of shareholders
     public static List<Account> findBoardOfShareholders(Map<String, Account> balances, List<Block> blocks, int limit) {
         List<Block> minersHaveMoreStock = null;
         if (blocks.size() > limit) {
             minersHaveMoreStock = blocks.subList(blocks.size() - limit, blocks.size());
         } else {
             minersHaveMoreStock = blocks;
         }
         List<Account> boardAccounts = minersHaveMoreStock.stream().map(
                         t -> new Account(t.getMinerAddress(), 0, 0))
                 .collect(Collectors.toList());

         for (Block block : minersHaveMoreStock) {
             for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {
                 boardAccounts.add(new Account(dtoTransaction.getSender(), 0, 0));
             }

         }
````

## Board of Directors
The Board of Directors is elected by the members of the network.
The Board of Directors consists of 301 accounts that received the most votes
according to the system described in [VOTE_STOCK](../charterEng/VOTE_STOCK.md). Each score is equal to one vote, described
in [ONE_VOTE](../charterEng/ONE_VOTE.md).

````
  //minimum value for the number of positive votes for the law to be valid,
         //positions elected by shares of the board of directors
         List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()
                 .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                 .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))
                 .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                 .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())
                 .collect(Collectors.toList());
````

### How to apply for a board position
First you need to go to the tab in ***apply for a position*** Select BOARD_OF_DIRECTORS
and fill in all the lines with the required data.
![apply_board_of_directors](../screenshots/apply_board_or_directors.png)

## Fractions.
The factions are elected by the members of the network.
There are only 100 revenge for factions. One hundred with the most votes received by the system
described in [VOTE_STOCK](../charterEng/VOTE_STOCK.md) becomes a faction. The vote of each faction is equal to the share which
she received relatively 99 other factions. Each faction has a vote described in [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md).

````
//select factions
         List<CurrentLawVotesEndBalance> electedFraction = current.stream()
                 .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                 .filter(t -> t.getPackageName().equals(NamePOSITION.FRACTION.toString()))
                 .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                 .limit(directors.getDirector(NamePOSITION.FRACTION.toString()).getCount())
                 .collect(Collectors.toList());
````

### How to create a new faction
To create a faction, you need to follow the same procedure as for submitting to the board of directors.
![apply_fraction](../screenshots/apply_fraction.png)


## Independent Network Members.
All network members who have shares and are not included in the first three categories listed above,
are ***independent members of the network***. The votes of each such participant are equal to
to the number of shares at the moment and is described in detail in [VOTE_STOCK](../charterEng/VOTE_STOCK.md).


[Exit to home](../documentationEng/documentationEng.md)