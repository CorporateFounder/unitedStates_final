# GENERAL_EXECUTIVE_DIRECTOR General Executive Director
This Director coordinates the actions of the other senior directors to implement the strategic plan or
the tasks assigned to it by the laws in force.
All powers must be given to him through existing laws.
This is the highest position elected by the Corporation and is essentially the analogue of the prime minister.

## How the CEO is elected
This director is elected by the Legislature
3. Fractions must give 15% or more votes using the method [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)
4. Network participants must give more than one vote using the [VOTE_STOCK](../charterEng/VOTE_STOCK.md) method
5. Next comes the sorting from the highest to the lowest received votes from the shares and
6. One account with the largest number of votes received from factions is selected.
7. If the founder vetoed this candidate, then you need to get
   Faction votes 15% or more according to the system [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)
   and votes of the council of judges 2 or more votes according to the system [ONE_VOTE](../charterEng/ONE_VOTE.md)

````
   //positions created by all participants
         List<CurrentLawVotesEndBalance> createdByFraction = current.stream()
                 .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                 .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                 .collect(Collectors.toList());
         //adding positions created by the board of directors
         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByFraction) {
             directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
         }

         //positions elected only by all participants
         List<CurrentLawVotesEndBalance> electedByFractions = current.stream()
                 .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                 .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||
                         t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                 .collect(Collectors.toList());

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
                 int hightJudgesVotes = 0;
                 int founderVote = 0;
                 double fraction = 0;

                 //count special votes for laws
                 vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());
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
// if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){
// if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){
// houseOfRepresentativies.add(currentLawVotesEndBalance.getLaws().get(0));
// }
//
// }
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
                 if(currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE
                 && currentLawVotesEndBalance.getFractionVote() >= 0 ||
                 currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && currentLawVotesEndBalance.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES){
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





     //excluding the House of Representatives
     public static List<CurrentLawVotesEndBalance> filters(List<LawEligibleForParliamentaryApproval> approvalList, Map<String, Account> balances,
                                                           List<Account> BoardOfShareholders, List<Block> blocks, int limitBlocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {
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

         for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {
             if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {
                 String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();
                 String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();
                 List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();
                 double vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votes(balances, yesAverage, noAverage);

                 CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(address, packageName, vote, 0, 0, 0, 0, 0, 0, 0, laws);
                 current.add(currentLawVotesEndBalance);

             }
         }
         return current;
     }
````

[Exit to home](../documentationEng/documentationEng.md)