# HOW_THE_BUDGET_IS_APPROVED КАК УТВЕРЖДАЕТСЯ БЮДЖЕТ.

Действующий бюджет может быть только один. Бюджет утверждает только Совет Директоров. 
Для утверждения бюджета нужно получить методом описанным в VOTE_ONE 15 и больше голосов. 
Сам процесс происходит так: 
1. Сначала отбираются все пакеты законов, где название пакета совпадает с BUDGET. 
2. Дальше отбираются все пакеты которые остаток голосов получили 15 или больше. 
3. Дальше все эти пакеты сортируются по убыванию, с наибольшим количеством голосов. 
4. Дальше отбирается самый первый с наибольшим количеством голосов. 


````
   Пример кода утверждающий бюджет. class LawsController: method: currentLaw. 
   //бюджет утверждается только советом директоров.
       List<CurrentLawVotesEndBalance> budjet = current.stream()
               .filter(t-> !directors.contains(t.getPackageName()))
               .filter(t->Seting.BUDGET.equals(t.getPackageName()))
               .filter(t->!directors.isCabinets(t.getPackageName()))
               .filter(t-> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)
               .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())
               .limit(1)
               .collect(Collectors.toList());
````

[Выход на главную](../readme.md)