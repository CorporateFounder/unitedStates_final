# GENERAL_EXECUTIVE_DIRECTOR Генеральный Исполнительный Директор
Данный Директор координирует действия остальных высших директоров для реализации стратегического плана или 
поставленных перед ним задач действующими законами. 
Все полномочия должны быть ему выданы через действующие законы. 
Это самая высокая должность избираемая Корпорацией и является по своей сути аналогом премьер-министра.

## Как избирается Генеральный Исполнительный Директор
Данный директор избирается Законодательной властью
1. кандидат должен получить рейтинг от BOARD_OF_DIRECTORS в размере от 10 или больше [ONE_VOTE](../charter/ONE_VOTE.md) 
2. Участники сети должны дать больше одного голоса методом [VOTE_STOCK](../charter/VOTE_STOCK.md)
3. Дальше происходит сортировка от наибольшего к наименьшему полученных голосов от акций и
4. Отбирается один счет с наибольшим количеством голосов полученных от BOARD_OF_DIRECTORS.
5. Если основатель наложил вето на данного кандидата, тогда нужно получить 
голоса BOARD_OF_DIRECTORS 10 или больше по системе [ONE_VOTE](../charter/ONE_VOTE.md)
и голоса совета судей 2 или больше голосов по системе [ONE_VOTE](../charter/ONE_VOTE.md)

````
  List<String> hightJudge = new ArrayList<>();
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE
                ){
                    primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));
                }
            }
        }
````

[Выход на главную](../documentation/documentationRus.md)
