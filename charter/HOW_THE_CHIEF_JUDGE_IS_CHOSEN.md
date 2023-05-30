#  String HOW_THE_CHIEF_JUDGE_IS_CHOSEN КАК ИЗБИРАЕТСЯ ВЕРХОВНЫЙ СУДЬЯ HIGH_JUDGE.
Верховный Судья избирается CORPORATE_COUNCIL_OF_REFEREES. 
Каждый участник сети может подать на должность Верховного Судьи, создав закон, с названием 
пакета который совпадает с HIGH_JUDGE 
должностью, где адрес отправителя данной транзакции должен совпадать с первой строкой из списка законов данного пакета. 
Стоимость закона пять цифровых долларов в качестве вознаграждения добытчику.  
Счет с наибольшим количеством голосов остатка получает данную должность. 
Механизм голосования описан ONE_VOTE. 
Избирает Верховного Судью, Корпоративный Совет Судей. (CORPORATE_COUNCIL_OF_REFEREES) 
Пример кода как утверждается верховный судья. Class LawsController: method currentLaw. Участок кода 

````
      //позиции избираемые советом корпоративных верховных судей
      List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()
               .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
               .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
               .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList()); 
````

## Полномочия верховного судьи
Верховный судья
может участвовать в решении споров внутри членов сети, как и CORPORATE_COUNCIL_OF_REFEREES,
но его голос выше чем голос CORPORATE_COUNCIL_OF_REFEREES.

[Выход на главную](../documentation/documentationRus.md)