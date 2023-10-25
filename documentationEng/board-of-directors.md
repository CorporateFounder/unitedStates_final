# Board of Directors.
The Board of Directors consists of the 201 highest rated accounts,
This house has the same powers as the House of Representatives in the United States.
The 201 highest ranking accounts are elected to that position.
Elected by the [VOTE_STOCK](../charter/VOTE_STOCK.md) algorithm


public static List<CurrentLawVotesEndBalance> filtersVotes(
List<LawEligibleForParliamentaryApproval> approvalList,
Map<String, Account> balances,
List<Account> BoardOfShareholders,
Map<String, CurrentLawVotes> votesMap
) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
//current laws whose votes are greater ORIGINAL_LIMIT_MIN_VOTE
List<CurrentLawVotesEndBalance> current = new ArrayList<>();
// Map<String, CurrentLawVotes> votesMap = null;
List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());
// if (blocks.size() > limitBlocks) {
// votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));
// } else {
// votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);
// }



         //calculate the average number of times he voted for
         Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);
         //calculate the average number of times he voted against
         Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);


         //count votes for regular laws and position laws
         for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {
             if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {
                 String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();
                 String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();
                 List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();
                 double vote = 0;
                 int supremeVotes = 0;
                 int boafdOfShareholderVotes = 0;
                 int boardOfDirectors = 0;
                 int primeMinisterVotes = 0;
                 int hightJudgesVotes = 0;
                 int founderVote = 0;
                 double fraction = 0;

                 //for laws we count special votes
                 vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);
                 List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());
                 boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);

                 List<String> founder = List.of(Setting.ADDRESS_FOUNDER);
                 founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);
                 CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(
                         address,
                         packageName,
                         vote,
                         supremeVotes,
                         boardOfDirectors,
                         boafdOfShareholderVotes,
                         primeMinisterVotes,
                         hightJudgesVotes,
                         founderVote,
                         fraction
                         laws);
                 current.add(currentLawVotesEndBalance);

             }
         }

// List<String> houseOfRepresentativies = new ArrayList<>();
List<String> chamberOfSumpremeJudges = new ArrayList<>();
List<String> boardOfDirectors = new ArrayList<>();

         for (CurrentLawVotesEndBalance currentLawVotesEndBalance: current) {
             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){
                 if(currentLawVotesEndBalance.getVotes() >= Setting.ORIGINAL_LIMIT_MIN_VOTE){
                     boardOfDirectors.add(currentLawVotesEndBalance.getLaws().get(0));
                 }

             }
             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString())){
                 if(currentLawVotesEndBalance.getVotes() >= Setting.ORIGINAL_LIMIT_MIN_VOTE){
                     chamberOfSumpremeJudges.add(currentLawVotesEndBalance.getLaws().get(0));
                 }

             }




         }


         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
             if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){


                 double vote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).votesLaw(balances, yesAverage, noAverage);int supremeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, chamberOfSumpremeJudges);
                 int boardOfDirectorsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, boardOfDirectors);

                 currentLawVotesEndBalance.setVotes(vote);
                 currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);
                 currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);
                 currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);
             }

         }

         //the General Executive Director is being scrutinized
         List<String> primeMinister = new ArrayList<>();
         List<String> hightJudge = new ArrayList<>();
         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {
             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){
                 if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Setting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                 && currentLawVotesEndBalance.getVotes() >= Setting.ALL_STOCK_VOTE
                 ){
                     primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));
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
[Exit to main page](../documentation/documentationEng.md)