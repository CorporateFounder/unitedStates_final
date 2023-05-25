# HOW_IS_THE_STRATEGIC КАК УТВЕРЖДАЕТСЯ СТРАТЕГИЧЕСКИЙ ПЛАН. 
Стратегический план утверждает Совет Директоров, стратегический план может быть действующим 
только в единственном экземпляре. Чтобы Стратегический План был действующим, нужно остаток голосов 
Совета Директоров 15 или больше. Способ голосования VOTE_ONE. 

Совет Директоров в любой момент может отменить Стратегический План. Стратегический План действует 
пока количество голосов 15 или больше. Стратегический План может включать в себя общее направление 
Корпорации, а также что нужно реализовать. 

Участок кода который показывает как утверждается Стратегический План. 
class LawsController: method currentLaw: 

````
        //план утверждается только палатой Советом Директоров
      List<CurrentLawVotesEndBalance> planFourYears = current.stream()
               .filter(t->!directors.contains(t.getPackageName()))
               .filter(t->Seting.STRATEGIC_PLAN.equals(t.getPackageName()))
               .filter(t->!directors.isCabinets(t.getPackageName()))
               .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)
               .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())
               .limit(1)
               .collect(Collectors.toList());

````

[Выход на главную](../documentation/documentationRus.md)