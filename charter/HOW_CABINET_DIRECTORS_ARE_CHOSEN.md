# Как Избираются офисные Директора.

Все Директора Кабинета, это высшие директора которые управляют своими дивизионами,
избираются только Советом Директоров. 
Каждый участник сети может подать на должность высшего директора, создав закон, с названием пакета который совпадает с допустимыми 
должностями, где адрес отправителя данной транзакции должен совпадать с первой строкой из списка законов данного пакета. 
Стоимость закона пять цифровых долларов в качестве вознаграждения добытчику.  
Счет с наибольшим количеством голосов остатка получает данную должность. 
Механизм голосования описан ONE_VOTE. 
Чтобы быть избранным советом директоров, должность должна получить не меньше 15 голосов (остатка голосов).

Пример участка кода как избирается должности class LawsController: method currentLaw: 

````
    //позиции созданные советом директоров
       List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
               .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
               .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)
              .collect(Collectors.toList()); 
    //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //позиции избираемые только советом директоров
       List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()
                .filter(t -> directors.isElectedByBoardOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())
               .collect(Collectors.toList());
````

[возврат на главную](../readme.md)