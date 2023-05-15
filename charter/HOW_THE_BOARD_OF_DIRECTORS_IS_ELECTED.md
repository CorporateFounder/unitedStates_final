# Как избирается Совет Директоров.


_____

## Код и устав
Совет директоров состоит из 301 счетов BOARD_OF_DIRECTORS. 
Каждый участник сети может подать на должность совета директоров, создав пакет закона, где 
название пакета BOARD_OF_DIRECTORS и счет отправителя должен совпадать счетом который указан 
в первой строке закона, который содержится в списке данного пакета. 
301 счет с наибольшим количеством остатка голосов получает должность. 
Стоимость подачи на создание закона(должность) стоит пять цифровых долларов (5) в качестве вознаграждения добытчику. 

Процесс голосования описан в VOTE_STOCK 

Пример кода: LawController: method currentLaw: 
участок кода отвечающий за избрание совета директоров 

````
  //минимальное значение количество положительных голосов для того чтобы закон действовал, 
        //позиции избираемые акциями членов Совета Директоров
        List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()
               .filter(t -> directors.isElectedByStocks(t.getPackageName()))
               .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))
               .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
               .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
               .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())
               .collect(Collectors.toList());
````

[Возврат на главную](../readme.md)