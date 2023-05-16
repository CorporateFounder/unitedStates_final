# Краткое описание криптовалюты

Данная криптовалюта является целой эко-системой, позволяющей не только обмениваться 
денежными единицами, но также в данной системе реализована избирательная система,
позволяющая избирать своих предстателей, которые будут продвигать ваши интересы в данной сети.
Также позволяет создавать правила(законы) за которые будут голосовать ваши представители, и все
участники сети мгновенно смогут видеть какие решения были приняты.

Также в системе реализована прямая демократия, позволяющая напрямую голосовать за правила сети(законы).


Также в данной монете реализован механизм Демереджа, 0.1% каждые пол года, цифрового доллара и
0.2% для цифровой акции.
Демередж - это плата за обслуживание, данный механизм применяется во многих странах, 
и там называется отрицательная ставка, но самое прямое применение было
Первым практическим применением взглядов Гезелля был эксперимент в 1932 году в австрийском городке Вёргль с населением 3000 человек.

Даная мера обеспечивает частую коррекцию курса, что позволяет данной Системе быть максимально 
устойчивым к кризису и быстро не обесцениваться.

В системе два типа валют цифровые доллары и цифровые акции, 
в сутках 576 блоков можно добыть, каждый блок дает по 200 монет 
каждого типа.
В данном пакете расположены они
***src/main/java/International_Trade_Union/utils/UtilsBlock.java***

Сложность определяется по формуле и коректируется раз в пол дня.
````
 /**получить сложность*/
    private static int getAdjustedDifficulty(Block latestBlock, List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int DIFFICULTY_ADJUSTMENT_INTERVAL){
        Block prevAdjustmentBlock = blocks.get(blocks.size() - DIFFICULTY_ADJUSTMENT_INTERVAL);

        long timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
        long timeTaken = latestBlock.getTimestamp().getTime() - prevAdjustmentBlock.getTimestamp().getTime();

        if(timeTaken < timeExpected / 2){

            return prevAdjustmentBlock.getHashCompexity() + 1;
        }else if(timeTaken > timeExpected * 2){

            return prevAdjustmentBlock.getHashCompexity() - 1;
        }else {
            return prevAdjustmentBlock.getHashCompexity();
        }
    }
````

````
   /**определяет сложность, раз пол дня корректирует сложность. В сутках 576 блоков. 
 * каждый блок добывается примерно 2.3 минуты*/
    public static int difficulty(List<Block> blocks, long BLOCK_GENERATION_INTERVAL, int  DIFFICULTY_ADJUSTMENT_INTERVAL ){

        //секунды как часто создается блоки
        int difficulty = 1;
        Block latestBlock = blocks.get(blocks.size() -1);
        if(latestBlock.getIndex() != 0 && latestBlock.getIndex() % DIFFICULTY_ADJUSTMENT_INTERVAL == 0){
        difficulty = getAdjustedDifficulty(latestBlock, blocks, BLOCK_GENERATION_INTERVAL, DIFFICULTY_ADJUSTMENT_INTERVAL);
        System.out.println("difficulty: change dificulty: " + difficulty);
        }
        else {
        difficulty =  latestBlock.getHashCompexity();
        }

        return difficulty == 0? 1: difficulty;
        }

````

Какие должности здесь есть,
- Совет Директоров [BOARD_OF_DIRECTORS]
- Совет Акционеров [BOARD_OF_SHAREHOLDERS]
- Совет Корпоративных Судей [CORPORATE_COUNCIL_OF_REFEREES]
- Генеральный Исполнительный Директор [GENERAL_EXECUTIVE_DIRECTOR]
- Верховный Судья [HIGH_JUDGE]
- Директор интернет Магазина [INTERNET_STORE_DIRECTOR]
- Директор интернет биржа [DIRECTOR_OF_THE_DIGITAL_EXCHANGE]
- Директор интернет банка [DIRECTOR_OF_DIGITAL_BANK]
- Директор частных судей [DIRECTOR_OF_THE_COMMERCIAL_COURT]
- Директор СМИ [MEDIA_DIRECTOR]
- ДИРЕКТОР ПО РАЗВИТИЮ ВНЕДРЕНИЯ КРИПТОВАЛЮТНЫХ ТЕХНОЛОГИЙ МЕЖДУНАРОДНОЙ ПРОФСОЮЗНОЙ КОРПОРАЦИИ 
  - [DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION]

[возврат на главную](../readme.md)