#LEGISLATURE.
Power consists of 3 groups in this system.
1. Board of Shareholders
2. Fractions
3. Independent members of the network.

All participants must participate in the vote for the law adopted by the system to be valid
(The only exception is the Board of Shareholders, since the Board of Shareholders participates
only in the approval of amendments to the Charter).
For all votes, only votes cast in the last year count.
All members may hold multiple positions from different groups, but may not
hold more than one position in the same category.
Example: One account can be both ***Independent Network Member*** and ***Be like a faction***
and ***Member of the Board of Shareholders***, but one account cannot occupy several seats in fractions
or in the Board of Shareholders.

It is the votes from the Shares that are taken into account when electing Fractions and Corporate Judges
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

## Fractions
Fractions are elected in the same way as corporate judges, their number is 200 seats.
The peculiarity of factions is that their votes are equal to the share of support relative to other factions.
When we say faction, we always mean a legal or natural person who, on behalf of
of his group votes and because of this, one account may have more votes than when voting judges.
This is how faction votes are counted [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)

## Independent Network Members.
All network members who have shares and are not included in the first three categories listed above,
are ***independent members of the network***. The votes of each such participant are equal to
to the number of shares at the moment and is described in detail in [VOTE_STOCK](../charterEng/VOTE_STOCK.md).

[Exit to home](../documentationEng/documentationEng.md)