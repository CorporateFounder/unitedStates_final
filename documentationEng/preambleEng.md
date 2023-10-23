# Brief description of cryptocurrency
A unique cryptocurrency that has a number of features,
such as:
1. Two unique coins, a digital dollar and digital shares.
2. A unique mechanism for limiting coins by burning co
   of all coin accounts in the amount of, 0.2% digital dollars and 0.4% digital
   shares every six months.
3. On average, 576 blocks are mined per day.
4. A unique mining algorithm, where the block is considered valid
   if the number of zeros in the hash string matches the current difficulty,
   and the number of zeros in the hash bits is equal to or greater than two.
5. Difficulty adapts every 288 blocks.
6. A unique electoral system that allows you to elect your representatives.
7. Three branches of government Legislative, Judicial and Executive.
8. Parliamentary Form of Government.
9. Unique Mining System the number of coins is equal to the difficulty multiplied by 30 (if
   the block index is even, if not even then we add +1 to this).
   Example: difficulty 9 a) even index 9*30=270, odd block index (9*30)+1= 271.
10. All laws and elected positions are valid for exactly 4 years and every four years you need
    re-vote.
11. A unique sanctions system where participants can donate their coins to
    another participant lost the same number of coins, but this mechanism only works
    for shares.

## Detailed description of each part

### 1. Two unique coins
For each block two types of coins are given, one coin is a dollar, the second is a share,
Shares are used in voting to elect officers, and in voting
for laws.

### 2. Every six months, coins are burned from all accounts
Every six months, coins are burned from all accounts in the amount
0.2% digital dollars and 0.4% digital stocks, which holds back
inflation and allows miners to mine coins in the same quantity.

### 3. Approximately 576 are mined per day
Approximately 576 blocks are mined per day, which allows a large number of
participants engage in mining for many participants. But the quantity is not strict
fixed, and each block is mined in approximately 150 seconds, and the difficulty
may rise or fall if production rose 2.7 times or fell 1.6 times

### 4. Unique mining algorithm
Unlike other cryptocurrencies, where they often use the number of zeros in
bits hash, double check is used here. 1. Number of zeros,
in the string, the hash of the block must match the complexity. 2. Also
Each block hash must contain 2 or more zeros in the bits.
This measure is made for more accurate protection against ASIC attacks.

### 5. Difficulty adapts dynamically.
Difficulty adjustment occurs every 288 blocks,
If a block is mined more than 2.7 times faster, then the difficulty
increases +1, if production drops by 1.6 times, then difficulty
drops to -1 if none of the conditions are met, then the difficulty
remains the same
````
public static int v2getAdjustedDifficultyMedian(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
         Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);
         // Median time from index 0 to 10 of blocks
         List<Long> adjustmentBlockTimes = new ArrayList<>();
         for (int i = 0; i < Math.min(DIFFICULTY_ADJUSTMENT_INTERVAL, blocks.size()); i++) {
             adjustmentBlockTimes.add(blocks.get(i).getTimestamp().getTime());
         }
         Collections.sort(adjustmentBlockTimes);
         long prevTime = adjustmentBlockTimes.get(adjustmentBlockTimes.size() / 2);

         // Includes the latestBlock time and the last 10 indices from blocks
         List<Long> latestBlockTimes = new ArrayList<>();
         latestBlockTimes.add(latestBlock.getTimestamp().getTime());
         for (int i = Math.max(blocks.size() - 30, 0); i < blocks.size(); i++) {
             latestBlockTimes.add(blocks.get(i).getTimestamp().getTime());
         }
         Collections.sort(latestBlockTimes);
         long latestTime = latestBlockTimes.get(latestBlockTimes.size() / 2);


         double percentGrow = 2.7;
         double percentDown = 1.6;


         long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
         long timeTaken = latestTime - prevTime;

         if(timeTaken < timeExpected / percentGrow){
             return prevAdjustmentBlock.getHashCompexity() + 1;
         }else if(timeTaken > timeExpected * percentDown){
             return prevAdjustmentBlock.getHashCompexity() - 1;
         }else {
             return prevAdjustmentBlock.getHashCompexity();
         }
     }
````


### 6, 7, 8. Electoral system and branches of government.
For the electoral system of its representatives, shares are used.
One share gives the right to vote one FOR and one AGAINST. Each participant can give
your votes both FOR and AGAINST a candidate or law.
You can also split your votes equally among several
participants. For each candidate, a rating is calculated, rating
this is the sum of all votes FOR minus all votes AGAINST, equals RATING.
All Government consists of three branches of government.

1. The legislative branch consists of the Parliament of the Council of Directors.
   their number is 201 accounts. One count equals one vote.
   For each law it is calculated as the rating received from the shares,
   and the rating received from a member of the board of directors.
   The law comes into force as soon as its rating from a stock is above 1,
   so his rating is above a certain level.
2. The executive branch is the General Executive Director,
   and other leadership positions. The entire executive branch is appointed
   Board of Directors.
3. The judiciary consists of 50 judges also elected by the community.

To elect the Board of Directors, 201 accounts with the largest
rating, but each account can only submit itself for this position.

At any time, any participant can change their voice to the opposite one.
Any voice is only valid for 4 years and needs to be renewed again,
so that he continues to act.

### 9. Unique mining system.
There are currently only two types of coin mining in the world,
1. Bitcoin and its successors, which reduce production every four
   year or other period.
2. dogecoin and its successors, which have a fixed number of coins.

This system has a unique production system, where production is determined from
complexity, which makes it possible to regulate emissions and attract more investment.
The number of coins for even blocks is equal to the difficulty multiplied by 30,
for odd numbers (difficulty multiplied by 30) + 1.
Thus, in even blocks, for example, if the difficulty is 9, it will be equal to
9*30=270, when the block index is not even (9*30)+1 = 271.
Why is such a measure needed?
To understand why it is needed, you first need to understand the disadvantages
other coins.

#### What disadvantages do type 1 coins have?
First of all, this reduces the incentive to invest in the coin,
since if mining profitability falls, then in order to remain profitable
the cost should increase in proportion to the reduction.
Now I’ll explain in more detail: the current price of Bitcoin (10/23/2023)
is equal to 30571.90, while the production per block is 6.25. Now let's imagine that
Every four years production decreases,
1. award 2024 3.125, cost-effective value 61143.8
2. award 2028 1.5625 cost effective 122287.6
3. award 2032 0.78125 cost effective 244575.2
4. award 2036 0.390625 cost effective 489150.4
5. award 2040 0.1953125 cost effective 978300.8

Thus, it is clear that after each extraction, to remain
cost-effective the cost must double or they will need
cut costs in half after every change.
This coin will contribute to the monopolization of the system
and degradation due to reduced investment. Since in some
moment, production will become absolutely unprofitable, and it will be
engage only large coin holders.
Due to the reduction in production, the incentive to invest is reduced both for large holders,
as well as new players.

#### What disadvantages do type 2 coins have?
There are advantages to having a fixed amount of loot, but
there are some disadvantages. Both excess inflation and
oligopoly and stagnation. Since the number of coins is fixed,
then at some point investing in equipment becomes
not profitable. Due to increasing complexity, the cost increases
costs, which will often lead to unspoken collusion when
large participants will not invest and as a result
society will not develop. But this also has a problem
with inflation, because when demand falls, so does complexity
falls more often, but the number of mined coins remains the same.
But in fairness it must be said that the second coin
more stable than the first and less subject to volatility.

#### Our coin.
In this coin we give an additional reward for increasing
difficulties, which in turn gives an incentive to participants to finance
into the equipment. We also determine demand depending on complexity,
the higher the complexity, the higher the demand, but if demand falls, so does the complexity
also falls, which leads to a decrease in production and reduces inflation.
And the difference between the income of even and odd blocks stimulates
further violate the conspiracy. Since no one wants to give to others
players have more advantage.
This mechanism also allows you to better regulate the exchange rate of the coin.

### 10. Laws and Positions
All participants can vote for both several participants and
for individuals. Each vote is counted only for the last four years.
For example, you have one hundred shares, which means you have one hundred votes FOR and
one hundred votes against. Example: You have 100 shares. There are 6 Candidates
board positions A) B) C) D) E) F) you want to support
A) and C) then by voting for them they receive 50 votes each,
votes divided by the number of candidates FOR. Similar
you want to vote against the remaining 4 at the same time,
and each of them will receive minus 25 votes, votes divided by the number
candidates AGAINST, so you voted for 6 candidates,
2 of them received +50, four of them -25.
Every law, like every position, has its ownoh hash, for which the participants
can cast votes and thus take part in the vote.

### 11. Sanctions
This system implements a sanctions mechanism, imagine
that there are participants who violate the rules of the network and
they are trying to use laws of a fairly radical type.
Let's imagine there are six participants with such views,
1. one centrist.
2. two left
3. two right
4. one radical.

Each participant has one hundred shares, thus
a radical may try to make a decision that everyone else
participants are not supportive. And then they decide that each of them
they are ready to lose twenty coins, but the radical also loses this
number of coins. Now imagine that each of them imposes sanctions,
against the radical, and all participants lose their coins, but the radical
thus the radical loses all his hundred shares. But others too
participants also lose their shares, 20 each.

  [back to home](./documentationEng.md)