# Brief description of the cryptocurrency
## Why is the voting system implemented in this system.

We have seen a lot of cryptocurrencies that split due to small problems.
and instead of one coin, we get hundreds, which reduced the value of the coin, as well as
to capital losses.
Each of you knows what is in one of the best coins, and the founder of all cryptocurrency,
such as bitcoin, the reason for the split was simply the size of the block and from this was created
many coins. In this system, shareholders, and everyone who has shares are shareholders,
can solve such problems through voting and each decision will be valid only
1 year, and if this decision after 1 year is still relevant, then the participants
can easily support this decision again.
1. Direct Democracy allows you to vote for Laws and members directly. This
   a measure is needed when people have their own opinion on some specific issues.
2. Factions are your delegates and represent the share that shareholders support.
   Factions vote on the rules of the network.



## Brief description of the cryptocurrency
This cryptocurrency is unique, since the total money supply does not grow, but at the same time, the production of miners does not decrease.
How did we achieve this?! All cryptocurrencies in the world now use only two strategies and
I will give an example on the most successful currencies Bitcoin and Dogecoin.
1. Bitcoin, in order to limit the number of coins, reduces production by half every four years.
   But if the value does not double after each reduction, then many small players will go bankrupt,
   as the cost of production is maintained, and profits are reduced. transaction costs can't either
   grow, because if the cost is excessively high, then it makes no sense to acquire these coins.
2. Dogecoin removed production cuts, but this creates inflation as the money supply continues to grow,
   which causes problems.
3. My coin burns 0.2% of digital dollars and 0.4% of digital shares from all accounts every half a year,
   which allows miners to always mine 400 digital dollars and 400 digital shares for each block,
   nor does the money supply grow as extraction and destruction come into equilibrium.
4. You do not need to create a server with a white ip for mining. Since all local servers send
   their blocks to a global server that stores, updates and transmits the actual blockchain.
5. All your transactions automatically go to the global server and all miners automatically
   they take transactions and add them to the block, so the chances that your transfer will be added to the block,
   significantly higher.
6. Uses unique SHA-256 algorithm. In this system, the complexity is determined by the number
   zeros in the hash string when finding a block, but the actual blockchain is not only the longest blockchain,
   but also one where the sum of all zeros is greater than that of an alternative blockchain.
7. About 576 blocks are mined per day, which allows more to be mined.
8. Difficulty is adjusted every half a year.

***src/main/java/International_Trade_Union/utils/UtilsBlock.java***


````
  /**get complexity*/
     private static int getAdjustedDifficulty(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
         Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);

         long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
         long timeTaken = latestBlock.getTimestamp().getTime() - prevAdjustmentBlock.getTimestamp().getTime();

         if(timeTaken < timeExpected / 2){

             return prevAdjustmentBlock.getHashCompexity() + 1;
         }else if(timeTaken > timeExpected * 2){

             return prevAdjustmentBlock.getHashCompexity() - 1;
         } else {
             return prevAdjustmentBlock.getHashCompexity();
         }
     }
````

````
    /**determines the difficulty, since half a day adjusts the difficulty. There are 576 blocks per day.
  * each block is mined in approximately 2.3 minutes*/
     public static int difficulty(List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL ){

         //seconds how often blocks are created
         int difficulty = 1;
         Block latestBlock = blocks.get(blocks.size() -1);
         if(latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0){
         difficulty = getAdjustedDifficulty(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
         System.out.println("difficulty: change dificulty: " + difficulty);
         }
         else {
         difficulty = latestBlock.getHashCompexity();
         }

         return difficulty == 0? 1: difficulty;
         }

````

What positions are there?
- Fraction [FRACTION]
- Board of Shareholders [BOARD_OF_SHAREHOLDERS]
- Council of Corporate Judges [CORPORATE_COUNCIL_OF_REFEREES]
- General Executive Director [GENERAL_EXECUTIVE_DIRECTOR]
- High Judge [HIGH_JUDGE]
  [back to home](./documentationEng.md)