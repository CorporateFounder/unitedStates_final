# КАК ИЗБИРАЮТСЯ ЗАКОНЫ. 

## Утверждение закона
_____

## УСТАВ
Ни один закон не имеет обратной силы. Ни один закон не должен нарушать действующий устав или противоречит 
другим действующим законам. Если есть противотечение между несколькими законами из одного пакета законов, 
то действующим является тот который в списке находится выше по индексу. Пример: пакет по продаже алкоголя 
закон под индексом 3 противоречит закону из индекса 17, в данном случае закон под индексом три будет действующим, 
так как он более высокая по статусу. 
В случае если есть два и более принятых пакета законов, которые действуют, но противоречат друг другу,
то CORPORATE_COUNCIL_OF_REFEREES должны решить противоречие между ними с помощью прецедентного права.

Если закон суммарно действует 12 и более лет, то он должен быть как действующий и его можно отменить,
только таким же действующим законом который принял после, или верховным судом (CORPORATE_COUNCIL_OF_REFEREES)
который должен отменить его, если он противоречит уставу или с помощью прецедента.


Если есть Государства или Частные юрисдикции, которые входят в состав данного союза,
то должно быть сформировано сенат, с каждой страны должны избираться 5 сенаторов. 
Также избираться каждый кандидат должен по системе такой системе.
VOTE = N - 1. Где каждый человек имеет VOTE голосов, которые он может отдать как
ЗА, так и ПРОТИВ. Также он может распределить эти голоса между несколькими кандидатами
на сенат, или отдать одному. N - это количество Сенаторов от страны. Таким образом,
если каждая страна должна предоставить 5 сенаторов, то каждый гражданин в этой 
стране имеет 4 голоса 5-1=4. 
Далее для каждого кандидата должен быть подсчитан рейтинг, по формуле голоса отданные 
ЗА данного кандидата, минус голоса ПРОТИВ = результат РЕЙТИНГ. 
5 с наибольшим количеством рейтинга, становятся сенаторами от этого государства или частной юрисдикции.
Если в составе есть три и более государства, то любой закон на территории государств, также
должен утверждаться не только механизмами сети, но и сенатом.


Есть минимальные требования которые должны соблюдать все члены данного союза (Если это государство 
или частная юрисдикция)
1. Все участники должны вести торговлю между собой только в данной криптовалюте (доллары или акции)
2. Ни один участник данного союза не должен инициировать агрессию членам данного союза.
3. Члены союза не должны иметь права навязывать друг другу форму управления.
4. Все члены союза должны признавать данный устав как самый главный закон и законы также принятые 
советом директоров и сенатом.
5. Все граждане данного союза должны иметь права свободно пересекать границы членов данного союза.
6. Не должны применяться протекционистские меры против граждан членов данного союза и самих членов союза.


Все законы делятся на несколько групп.
1. Обычные законы
2. Стратегический План
3. Назначаемые должности Законодательной властью
4. Законы, которые создают новые должности. Данные должности утверждаются только Законодательной Властью.
5. Поправки в Устав
6. Сам устав


### ОБЫЧНЫЕ ЗАКОНЫ
Чтобы утвердить обычные законы, 
1. название пакета закона не должно совпадать с выделенными ключевыми словами.
2. Закон должен получить больше 1 голоса по системе подсчета описанной [VOTE_STOCK](../charter/VOTE_STOCK.md)
3. Должен получить рейтинг 10 или больше голосов от членов совета директоров по системе подсчета описанной в [ONE_VOTE](../charter/ONE_VOTE.md)
4. Если основатель наложил вето на закон,
то чтобы закон прошел в обход основателя, нужно 2 или больше голоса от
совета судей (рейтинг) (ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES) по системе подсчета голосов 
[ONE_VOTE](../charter/ONE_VOTE.md)


Пример кода в LawsController current law:
````
      //законы должны быть одобрены всеми.
        List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> !Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||
                        t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS &&
                                t.getVotesCorporateCouncilOfReferees() > Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());



````

### СТРАТЕГИЧЕСКИЙ ПЛАН.
Стратегический план является общим планом для всей сети и утверждается аналогично обычному закону,
но есть некоторые отличия от обычных законов.
1. Пакет стратегического плана должен называться STRATEGIC_PLAN
2. Все планы которые прошли одобрение, сортируется от наибольшего к наименьшему по количеству голосов,
полученных от Совета Директоров.
3. После Сортировки отбираются только один ПЛАН с наибольшим количеством голосов полученных от акций.

````
 //план утверждается всеми
        List<CurrentLawVotesEndBalance> planFourYears = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.STRATEGIC_PLAN.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());
````

### ДОЛЖНОСТИ КОТОРЫЕ НАЗНАЧАЮТСЯ ТОЛЬКО ЗАКОНОДАТЕЛЬНОЙ ВЛАСТЬЮ
Есть должности которые назначаются только Законодательной властью и таким должностям относиться
Генеральный Исполнительный Директор. Данная должность аналогична премьер-министру и является
Исполнительной Властью в данной системе.
Каждая такая должность может быть ограничена количеством, которое определено в данной системе
для данной должности. Пример: Генеральный Исполнительный Директор есть только одно место.
Избирается аналогично как ***стратегический план*** 
Но количество определяется для каждой должности отдельно.
Если основатель наложил вето на кандидата на данную должность,
но его должен также одобрить совет судей и он должен получить 2 или более голосов.
По системе [ONE_VOTE](../charter/ONE_VOTE.md)
````
     //позиции созданные всеми участниками
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
        //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //позиции избираемые только всеми участниками
        List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()
                .filter(t -> directors.isofficeOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t ->
                        t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||
                        t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


````

Также есть должности которые создаются с помощью законов, данные должности утверждаются тоже Законодательной властью.
Для каждой такой должности только одно место, для каждого названия. 
Название таких пакетов начинается с ADD_DIRECTOR_.
С обязательным нижним подчеркиванием.

````
//добавляет законы, которые создают новые должности утверждается всеми
        List<CurrentLawVotesEndBalance> addDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
````

### ПОПРАВКИ В УСТАВ
Чтобы внести поправки в устав, нужно чтобы пакет закона должен называться AMENDMENT_TO_THE_CHARTER.
Должно пройти не менее четырех недель после голосования чтобы поправка была легитимной.
Для того чтобы поправка считалась действующей
1. Нужно чтобы 35% или больше голосов получила от Совета Акционеров системой подсчета [ONE_VOTE](../charter/ONE_VOTE.md).
   1. Нужно, чтобы получить рейтинг 40.2 или больше голосов от совета директоров [ONE_VOTE](../charter/ONE_VOTE.md).
2. Нужно, чтобы получить 5 или больше голосов от Законодательной Власти Корпоративных Верховных Судей.



````
       //внедрение поправок в устав
        List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT
                        && t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT
                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());

````

### САМ УСТАВ.
Первый устав утверждается основателем и он является действующим, голос основателя для утверждения
устава никогда не имеет срока годности.
Название пакета устава начинается с CHARTER_ORIGINAL и название исходного кода CHARTER_ORIGINAL_CODE.
Эти два пакета и являются целостным уставом, но в первую очередь, исходный код не должен противоречить
принципам описанным в CHARTER_ORIGINAL.
````
//устав всегда действующий он подписан основателем
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t->t.getFounderVote()>=1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());

        //ИСХОДНЫЙ КОД СОЗДАННЫЙ ОСНОВАТЕЛЕМ
        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL_CODE = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t->t.getFounderVote()>=1)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .limit(1)
                .collect(Collectors.toList());
````
[Возврат на главную](../documentation/documentationRus.md)