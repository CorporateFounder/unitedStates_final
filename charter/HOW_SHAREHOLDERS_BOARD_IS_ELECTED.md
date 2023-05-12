# Как Избирается Совет Акционеров.

Совет Акционеров состоит из тысячи пятсот счетов (1500) с наибольшим количеством акций,
но учитываются только те счета от чьей активности не прошло больше года.
формула: текущий год - один год, и если счет был активен в этом диапазоне, он 
учитывается. Все счета сортируются по убыванию количества цифровых акций, и отбираются 1500 счетов с наибольшим количеством
акций. Перерасчет происходит Каждый блок. 

Пример участка кода как избирается Совет Акционеров: 
class UtilsGovernment method findBoardOfShareholders:
````
 //определение совета акционеров
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
       CompareObject compareObject = new CompareObject();
       List<Account> boardOfShareholders = balances.entrySet().stream()
                .filter(t -> boardAccounts.contains(t.getValue()))
                .map(t -> t.getValue()).collect(Collectors.toList());

        boardOfShareholders = boardOfShareholders
                .stream()
                .filter(t -> !t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))
                .filter(t -> t.getDigitalStockBalance() > 0)
                .sorted(Comparator.comparing(Account::getDigitalStockBalance).reversed())
                .collect(Collectors.toList());

        boardOfShareholders = boardOfShareholders
                .stream()
                .limit(Seting.BOARD_OF_SHAREHOLDERS)
               .collect(Collectors.toList());
        return boardOfShareholders;
   } 

````

[возврат на главную](../readme.md)