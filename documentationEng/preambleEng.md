# Brief description of the cryptocurrency

This cryptocurrency is a whole eco-system that allows not only to exchange
monetary units, but also in this system an electoral system is implemented,
allowing you to elect your representatives who will promote your interests in this network.
It also allows you to create rules (laws) for which your representatives will vote, and all
network members will instantly be able to see what decisions have been made.

The system also implements direct democracy, which allows you to directly vote for the rules of the network (laws).


Also, this coin implements the Demurrage mechanism, 0.1% every six months, a digital dollar and
0.2% for a digital share.
Demurrage is a service fee, this mechanism is used in many countries,
and it's called a negative rate, but the most direct application was
The first practical application of Gesell's views was an experiment in 1932 in the Austrian town of Wörgl with a population of 3,000 people.

This measure provides frequent course corrections, which allows this System to be as
crisis-resistant and do not depreciate quickly.

There are two types of currencies in the system - digital dollars and digital stocks,
576 blocks can be mined per day, each block gives 200 coins
each type.
In this package they are
***src/main/java/International_Trade_Union/utils/UtilsBlock.java***

Difficulty is determined by the formula and is corrected every half a day.
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
- Board of Directors [BOARD_OF_DIRECTORS]
- Board of Shareholders [BOARD_OF_SHAREHOLDERS]
- Council of Corporate Judges [CORPORATE_COUNCIL_OF_REFEREES]
- General Executive Director [GENERAL_EXECUTIVE_DIRECTOR]
- High Judge [HIGH_JUDGE]
- Online Store Director [INTERNET_STORE_DIRECTOR]
- Director of the Internet Exchange [DIRECTOR_OF_THE_DIGITAL_EXCHANGE]
- Internet bank director [DIRECTOR_OF_DIGITAL_BANK]
- Director of Private Judges [DIRECTOR_OF_THE_COMMERCIAL_COURT]
- Media Director [MEDIA_DIRECTOR]
- DIRECTOR FOR THE DEVELOPMENT OF THE IMPLEMENTATION OF CRYPTOCURRENCY TECHNOLOGIES OF THE INTERNATIONAL TRADE UNION CORPORATION
    - [DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION]

[back to home](./documentationEng.md)