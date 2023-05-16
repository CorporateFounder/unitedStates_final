# HOW_IS_THE_PROCESS_OF_AMENDING_THE_CHARTER КАК ПРОИСХОДИТ ПРОЦЕСС ВНЕСЕНИЯ ПОПРАВОК В УСТАВ. 
Для внесения поправок, нужно создать закон с названием пакета AMENDMENT_TO_THE_CHARTER, 
дальше за этот закон должны проголосовать методом описанным в VOTE_ONE 
Совет Акционеров и остаток голосов должен быть равен или выше 300 участников, 
также должны проголосовать Совет Директоров и остаток голосов должен быть 60 или больше, 
также должны проголосовать корпоративные верховные судьи (CORPORATE_COUNCIL_OF_REFEREES) и 
остаток голосов должен быть равен или больше 5. 

Но поправки не должны касаться способа установления правил действующих законов, а также 
избрания Совета Директоров, Совета Акционеров, Генерального Исполнительного Директора, 
Совета Корпоративных Судей и Верховного Судьи. Поправки могут изменять код, если сохраняются правила 
избрания действующих должностей (включая правил голосования), законов и добычи денег (добыча цифровых долларов и цифровых акции),
Ни одна поправка не должна наделять из выше перечисленных должностей большей властью. 
Также поправки не должны ущемлять Естественные Права Человека. 

Пример кода. class LawsController: method currentLaw: участок кода утверждающий действующие поправки

````
            //внедрение поправок в устав
                List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t-> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t->!directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT)
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT)
                .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed()).collect(Collectors.toList());
````
[Выход на главную](../readme.md)
