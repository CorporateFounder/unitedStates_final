#  String HOW_THE_CHIEF_JUDGE_IS_CHOSEN КАК ИЗБИРАЕТСЯ ВЕРХОВНЫЙ СУДЬЯ HIGH_JUDGE.
Верховный Судья избирается CORPORATE_COUNCIL_OF_REFEREES. 
Каждый участник сети может подать на должность Верховного Судьи, создав закон, с названием пакета который совпадает с допустимым 
должностью, где адрес отправителя данной транзакции должен совпадать с первой строкой из списка законов данного пакета. 
стоимость закона пять цифровых долларов в качестве вознаграждения добытчику.  
счет с наибольшим количеством голосов остатка получает данную должность. 
Механизм голосования описан ONE_VOTE. 

Пример кода как утверждается верховный судья. class LawsController: method currentLaw. Участок кода 

````
      //позиции избираемые советом корпоративных верховных судей
      List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()
               .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
               .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
               .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList()); 
````

[Выход на главную](../documentation/documentationRus.md)