# GENERAL_EXECUTIVE_DIRECTOR General Executive Director
This Director coordinates the actions of the other senior directors to implement the strategic plan or
the tasks assigned to it by the laws in force.
All powers must be given to him through existing laws.
This is the highest position elected by the Corporation and is essentially the analogue of the prime minister.

## How the CEO is elected
This director is elected by the Legislature
1. The Board of Directors must give more than 10 or more votes using the [ONE_VOTE](../charterEng/ONE_VOTE.md) method
2. The Board of Shareholders must give more than 10 or more votes using the [ONE_VOTE](../charterEng/ONE_VOTE.md) method
3. Fractions must give 10% or more votes using the [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md) method
4. Network participants must give more than one vote using the [VOTE_STOCK](../charterEng/VOTE_STOCK.md) method
5. Next comes the sorting from highest to lowest received votes from the Board of Directors and
6. One account with the most votes from the Board of Directors is selected

````
  //positions elected only by all participants
         List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()
                 .filter(t -> directors.isElectedByBoardOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                 .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                 && t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS
                 && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())
                 .collect(Collectors.toList());
                
                  //group by list
         Map<String, List<CurrentLawVotesEndBalance>> group = electedByBoardOfDirectors.stream()
                 .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));

         Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();

         // leave the amount that is described in this post
         for (Map.Entry<String, List<CurrentLawVotesEndBalance>> stringListEntry : group.entrySet()) {
             List<CurrentLawVotesEndBalance> temporary = stringListEntry.getValue();
             temporary = temporary.stream()
                     .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors))
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
         //acting laws whose votes are greater than ORIGINAL_LIMIT_MIN_VOTE
         List<CurrentLawVotesEndBalance> current = new ArrayList<>();
         Map<String, CurrentLawVotes> votesMap = null;
         List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
         if (blocks.size() > limitBlocks) {
             votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));
         } else {
             votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);
         }

         //calculate the average number of times he voted for
         Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);
         //calculate the average number of times he downvoted
         Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);


         //count the votes for the normal laws and laws of positions
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
                 int-highhtJudgesVotes = 0;
                 int founderVote = 0;
                 double fraction = 0;

                 //count special votes for laws
                 vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);
                 List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());
                 boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);

                 List<String> founder = List.of(Seting.ADDRESS_FOUNDER);
                 founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);
                 CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(
                         address,
                         packagename,
                         vote,
                         supremeVotes,
                         houseOfRepresentativesVotes,
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
             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){
                 if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
                     houseOfRepresentativies.add(currentLawVotesEndBalance.getLaws().get(0));
                 }

             }
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
                 int supremeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, chamberOfSumpremeJudges);
                 int houseOfRepresentativiesVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, houseOfRepresentativies);
                 double fractionsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteFractions(fractions);

                 currentLawVotesEndBalance.setVotes(vote);
                 currentLawVotesEndBalance.setVotesBoardOfDirectors(houseOfRepresentativiesVotes);
                 currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);
                 currentLawVotesEndBalance.setFractionVote(fractionsVotes);
             }

         }

         //examines the CEO
         List<String> primeMinister = new ArrayList<>();
         List<String> hightJudge = new ArrayList<>();
         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                 if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                 && currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && currentLawVotesEndBalance.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS
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

[Exit to home](../documentationEng/documentationEng.md)