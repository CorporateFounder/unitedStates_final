# String HOW_THE_CORPORATE_BOARD_OF_JUDGES_IS_ELECTED КАК ИЗБИРАЮТСЯ КОРПОРАТИВНЫЙ СОВЕТ СУДЕЙ.

CORPORATE_COUNCIL_OF_REFEREES состоит из 55 счетов.
Каждый участник сети может подать на должность CORPORATE_COUNCIL_OF_REFEREES,
создав пакет закона, где название пакета CORPORATE_COUNCIL_OF_REFEREES и счет отправителя должен совпадать
счетом который указан в первой строке закона который содержится в списке данного пакета
55 счет с наибольшим количеством остатка голосов получает должность.

Стоимость подачи на создание закона(должность) стоит пять цифровых долларов (5) в качестве вознаграждения добытчику.
Процесс голосования описан в VOTE_STOCK.

----

    Пример участка кода: class LawsController: method currentLaw: 
            //минимальное значение количество положительных голосов для того чтобы закон действовал,
            //позиции избираемые акциями CORPORATE_COUNCIL_OF_REFEREES
                   List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()
                    .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                      .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))
                        .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                          .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
    .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())
    .collect(Collectors.toList());

----

[Выход на главное](../documentation/documentationRus.md)