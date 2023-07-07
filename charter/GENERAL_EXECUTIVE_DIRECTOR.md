# GENERAL_EXECUTIVE_DIRECTOR Генеральный Исполнительный Директор
Данный Директор координирует действия остальных высших директоров для реализации стратегического плана или 
поставленных перед ним задач действующими законами. 
Все полномочия должны быть ему выданы через действующие законы. 
Это самая высокая должность избираемая Корпорацией и является по своей сути аналогом премьер-министра.

## Как избирается Генеральный Исполнительный Директор
Данный директор избирается Законодательной властью
3. Фракции должны дать 15% или больше голосов методом [VOTE_FRACTION](../charter/VOTE_FRACTION.md)
4. Участники сети должны дать больше одного голоса методом [VOTE_STOCK](../charter/VOTE_STOCK.md)
5. Дальше происходит сортировка от наибольшего к наименьшему полученных голосов от акций и
6. Отбирается один счет с наибольшим количеством голосов полученных от фракций.

````
  //позиции избираемые только всеми участниками
        List<CurrentLawVotesEndBalance> electedByFractions = current.stream()
                .filter(t -> directors.isElectedByBoardOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


        //групируем по списку
        Map<String, List<CurrentLawVotesEndBalance>> group = electedFraction.stream()
                .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));

        Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();

        //оставляем то количество которое описано в данной должности
        for (Map.Entry<String, List<CurrentLawVotesEndBalance>> stringListEntry : group.entrySet()) {
            List<CurrentLawVotesEndBalance> temporary = stringListEntry.getValue();
            temporary = temporary.stream()
                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes))
                    .limit(directors.getDirector(stringListEntry.getKey()).getCount())
                    .collect(Collectors.toList());
            original_group.put(directors.getDirector(stringListEntry.getKey()), temporary);
        }

````

````
 public static List<CurrentLawVotesEndBalance> filtersVotes(
            List<LawEligibleForParliamentaryApproval> approvalList,
            Map<String, Account> balances,
            List<Account> BoardOfShareholders,
            List<Block> blocks,
            int limitBlocks
    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        //действующие законы чьи голоса больше ORIGINAL_LIMIT_MIN_VOTE
        List<CurrentLawVotesEndBalance> current = new ArrayList<>();
        Map<String, CurrentLawVotes> votesMap = null;
        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
        if (blocks.size() > limitBlocks) {
            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));
        } else {
            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);
        }

        //подсчитать средннее количество раз сколько он проголосовал за
        Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);
        //подсчитать среднее количество раз сколько он проголосовал против
        Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);


        //подсчитываем голоса для для обычных законов и законов позиций
        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {
            if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {
                String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();
                String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();
                List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();
                double vote = 0;
                int supremeVotes = 0;
                int boafdOfShareholderVotes = 0;
                int houseOfRepresentativiesVotes = 0;
                int primeMinisterVotes = 0;
                int hightJudgesVotes = 0;
                int founderVote = 0;
                double fraction = 0;

                //для законов подсчитываем специальные голоса
                vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);
                List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());
                boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);

                List<String> founder = List.of(Seting.ADDRESS_FOUNDER);
                founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);
                CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(
                        address,
                        packageName,
                        vote,
                        supremeVotes,
                        houseOfRepresentativiesVotes,
                        boafdOfShareholderVotes,
                        primeMinisterVotes,
                        hightJudgesVotes,
                        founderVote,
                        fraction,
                        laws);
                current.add(currentLawVotesEndBalance);

            }
        }

        List<String> houseOfRepresentativies = new ArrayList<>();
        List<String> chamberOfSumpremeJudges = new ArrayList<>();
        Map<String, Double> fractions = new HashMap<>();

        for (CurrentLawVotesEndBalance currentLawVotesEndBalance: current) {
//            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){
//                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
//                    houseOfRepresentativies.add(currentLawVotesEndBalance.getLaws().get(0));
//                }
//
//            }
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString())){
                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
                    chamberOfSumpremeJudges.add(currentLawVotesEndBalance.getLaws().get(0));
                }

            }


            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.FRACTION.toString())){
                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
                    fractions.put(currentLawVotesEndBalance.getLaws().get(0), currentLawVotesEndBalance.getVotes());
                }
            }

        }



        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){


                double vote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).votesLaw(balances, yesAverage, noAverage);
                int supremeVotes  = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, chamberOfSumpremeJudges);
                int houseOfRepresentativiesVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, houseOfRepresentativies);
                double fractionsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteFractions(fractions);

                currentLawVotesEndBalance.setVotes(vote);
                currentLawVotesEndBalance.setVotesBoardOfDirectors(houseOfRepresentativiesVotes);
                currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);
                currentLawVotesEndBalance.setFractionVote(fractionsVotes);
            }

        }

        //изирается Генеральный исполнительный директор
        List<String> primeMinister = new ArrayList<>();
        List<String> hightJudge = new ArrayList<>();
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                if(currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE){
                    primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));
                }
            }

            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.HIGH_JUDGE.toString())){
                if(currentLawVotesEndBalance.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES){
                    hightJudge.add(currentLawVotesEndBalance.getLaws().get(0));
                }
            }
        }
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
            if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){
                int primeMinisterVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, primeMinister);
                int hightJudgeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, hightJudge);

                currentLawVotesEndBalance.setVoteGeneralExecutiveDirector(primeMinisterVotes);
                currentLawVotesEndBalance.setVoteHightJudge(hightJudgeVotes);
            }

        }


        return current;

    }
````

[Выход на главную](../documentation/documentationRus.md)
