#LEGISLATURE.
Power consists of 3 groups in this system.
1. Council of Shareholders
2. Board of Directors
3. Independent network participants.

All participants must participate in voting for the law adopted by the system to be valid
(The only exception is the Board of Shareholders, since the Board of Shareholders participates
only in approving amendments to the Charter).
For all votes, only votes cast within the last year are taken into account.
All members can hold multiple positions from different groups, but cannot
occupy several positions in one position category.
Example: One account can be both ***Independent Network Member*** and ***Be Like a Faction***
and ***Member of the Board of Shareholders***, but one account will not be able to take several seats in factions
or in the Board of Shareholders.

It is the votes from the Shares that are taken into account when electing Factions and Corporate Judges
## Board of Shareholders
The Board of Shareholders is appointed automatically by the system.
The Board of Shareholders consists of 1,500 accounts with the largest number of shares,
but only those accounts are selected that have either been involved in mining in the last year,
either sent digital dollars or digital shares, or participated in voting.
A member of one Board of Shareholders has one vote. One count equals one vote.
The voting system described in [ONE_VOTE](../charterEng/ONE_VOTE.md) is used.

````
   //definition of the shareholders' council
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

## Factions
The Board of Directors is also elected as corporate judges, their number is 201 seats.
The 201 accounts with the highest ratings become members of the board of directors.
This is how the votes of the Board of Directors are counted [ONE_VOTE](../charterEng/ONE_VOTE.md)

## Independent Network Participants.
All network participants who have shares and are not included in the first three categories listed above,
are ***independent members of the network***. The votes of each such participant are equal to
to the number of shares at the moment and is described in detail in [VOTE_STOCK](../charterEng/VOTE_STOCK.md).

[Exit to home](../documentationEng/documentationEng.md)