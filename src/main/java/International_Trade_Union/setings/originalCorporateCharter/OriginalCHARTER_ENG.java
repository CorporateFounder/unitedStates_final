package International_Trade_Union.setings.originalCorporateCharter;

public interface OriginalCHARTER_ENG {
    String LAW_1 = "# HOW LAWS ARE ELECTED.\n" +
            "\n" +
            "## Approval of the law\n" +
            "_____\n" +
            "\n" +
            "## CHARTER\n" +
            "No law has retroactive effect. No law shall violate any existing statute or be contrary to\n" +
            "other applicable laws. If there is a contradiction between several laws from one package of laws,\n" +
            "then the active one is the one that is higher in the list by index. Example: alcohol package\n" +
            "the law under index 3 contradicts the law under index 17, in this case the law under index three will be valid,\n" +
            "because he has a higher status.\n" +
            "If there are two or more adopted packages of laws that are valid but contradict each other,\n" +
            "then CORPORATE_COUNCIL_OF_REFEREES must resolve the conflict between them using case law.\n" +
            "\n" +
            "If the law has been in force for a total of 12 years or more, then it must remain in force and can be repealed,\n" +
            "only by the same current law that was adopted after, or by the Supreme Court (CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "who shall revoke it if it is contrary to the statute or by precedent.\n" +
            "\n" +
            "\n" +
            "If there are States or Private Jurisdictions that are part of this union,\n" +
            "then a senate should be formed, 5 senators should be elected from each country.\n" +
            "Also, each candidate must be elected according to such a system.\n" +
            "VOTE = N - 1. Where each person has a VOTE of votes that he can cast as\n" +
            "FOR AND AGAINST. He can also distribute these votes among several candidates\n" +
            "to the Senate, or give it to one. N is the number of Senators from the country. Thus,\n" +
            "if each country must provide 5 senators, then every citizen in that\n" +
            "the country has 4 votes 5-1=4.\n" +
            "Next, the rating must be calculated for each candidate, using the formula for the votes cast\n" +
            "FOR a given candidate, minus votes AGAINST = RATING result.\n" +
            "The 5 with the most rankings become senators from that state or private jurisdiction.\n" +
            "If there are three or more states, then any law on the territory of the states also\n" +
            "must be approved not only by the network mechanisms, but also by the Senate.\n" +
            "\n" +
            "\n" +
            "There are minimum requirements that all members of a given union must comply with (If this is a state\n" +
            "or private jurisdiction)\n" +
            "1. All participants must trade with each other only in this cryptocurrency (dollars or shares)\n" +
            "2. No member of this union should initiate aggression against members of this union.\n" +
            "3. Members of the union should not have the right to impose a form of management on each other.\n" +
            "4. All members of the union must recognize this charter as the most important law and laws also adopted\n" +
            "   board of directors and senate.\n" +
            "5. All citizens of a given union must have the right to freely cross the borders of members of this union.\n" +
            "6. Protectionist measures should not be applied against citizens of members of a given union and the members of the union themselves.\n" +
            "\n" +
            "\n" +
            "All laws are divided into several groups.\n" +
            "1. Ordinary laws\n" +
            "2. Strategic Plan\n" +
            "3. Appointed positions by the Legislature\n" +
            "4. Laws that create new positions. These positions are approved only by the Legislative Branch.\n" +
            "5. Amendments to the Charter\n" +
            "6. The charter itself\n" +
            "\n" +
            "\n" +
            "### ORDINARY LAWS\n" +
            "To establish ordinary laws,\n" +
            "1. The name of the law package should not coincide with the highlighted keywords.\n" +
            "2. The law must receive more than 1 vote according to the counting system described by [VOTE_STOCK](../charter/VOTE_STOCK.md)\n" +
            "3. Must receive a rating of 10 or more votes from members of the board of directors according to the scoring system described in [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "4. If the founder vetoed the law,\n" +
            "   then for the law to bypass the founder, you need 2 or more votes from\n" +
            "   Council of Judges (rating) (ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES) according to the vote counting system\n" +
            "   [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "\n" +
            "\n" +
            "Example code in LawsController current law:\n" +
            "````\n" +
            "       //laws must be approved by everyone.\n" +
            "         List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()\n" +
            "                 .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                 .filter(t -> !Setting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                 .filter(t -> !directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> !Setting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            "                 .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))\n" +
            "                 .filter(t -> t.getVotesBoardOfDirectors() >= Setting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                         && t.getVotes() >= Setting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                         t.getVotesBoardOfDirectors() >= Setting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS &&\n" +
            "                                 t.getVotesCorporateCouncilOfReferees() > Setting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            "\n" +
            "````\n" +
            "\n" +
            "### STRATEGIC PLAN.\n" +
            "The strategic plan is the general plan for the entire network and is approved in the same way as an ordinary law,\n" +
            "but there are some differences from ordinary laws.\n" +
            "1. The strategic plan package should be called STRATEGIC_PLAN\n" +
            "2. All plans that have been approved are sorted from highest to lowest by the number of votes,\n" +
            "   received from the Board of Directors.\n" +
            "3. After Sorting, only one PLAN with the most votes received from shares is selected.\n" +
            "\n" +
            "````\n" +
            " //план утверждается всеми\n" +
            "        List<CurrentLawVotesEndBalance> planFourYears = current.stream()\n" +
            "                .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                .filter(t -> Seting.STRATEGIC_PLAN.equals(t.getPackageName()))\n" +
            "                .filter(t -> !directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .limit(1)\n" +
            "                .collect(Collectors.toList());\n" +
            "\n" +
            "````\n" +
            "\n" +
            "\n" +
            "### POSTS THAT ARE APPOINTED ONLY BY THE LEGISLATIVE AUTHORITY\n" +
            "There are positions that are appointed only by the Legislature and such positions include\n" +
            "General Executive Director. This position is similar to the Prime Minister and is\n" +
            "Executive Power in this system.\n" +
            "Each such position can be limited to the number that is defined in this system.\n" +
            "for this position. Example: There is only one CEO position.\n" +
            "Elected in the same way as ***strategic plan***\n" +
            "But the number is determined for each position separately.\n" +
            "If the founder has vetoed a candidate for this position,\n" +
            "but it must also be approved by the council of judges and must receive 2 or more votes.\n" +
            "By system [ONE_VOTE](../charterEng/ONE_VOTE.md)\n" +
            "````\n" +
            "     //позиции созданные всеми участниками\n" +
            "        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()\n" +
            "                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            "                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                .collect(Collectors.toList());\n" +
            "        //добавление позиций созданных советом директоров\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {\n" +
            "            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());\n" +
            "        }\n" +
            "\n" +
            "        //позиции избираемые только всеми участниками\n" +
            "        List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()\n" +
            "                .filter(t -> directors.isofficeOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t ->\n" +
            "                        t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                        t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            "````\n" +
            "\n" +
            "There are also positions that are created with the help of laws, these positions are also approved by the Legislative power.\n" +
            "For each such position, there is only one seat for each title.\n" +
            "The name of such packages starts with ADD_DIRECTOR_.\n" +
            "With the obligatory underscore.\n" +
            "\n" +
            "````\n" +
            "//добавляет законы, которые создают новые должности утверждается всеми\n" +
            "        List<CurrentLawVotesEndBalance> addDirectors = current.stream()\n" +
            "                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            "                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "### AMENDMENTS TO THE CHARTER\n" +
            "To amend the charter, the law package must be named AMENDMENT_TO_THE_CHARTER.\n" +
            "At least four weeks must pass after the vote for the amendment to be legitimate.\n" +
            "For an amendment to be considered valid\n" +
            "1. It is necessary that 35% or more votes are received from the Board of Shareholders by the [ONE_VOTE](../charter/ONE_VOTE.md) counting system.\n" +
            "   1. It is necessary to receive a rating of 40.2 or more votes from the board of directors [ONE_VOTE](../charter/ONE_VOTE.md).\n" +
            "2. Required to obtain 5 or more votes from the Legislative Branch of the Corporate Supreme Judges.\n" +
            "\n" +
            "````\n" +
            "     //внедрение поправок в устав\n" +
            "        List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()\n" +
            "                .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                .filter(t -> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                .filter(t -> !directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT\n" +
            "                        && t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT\n" +
            "                        && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            "````\n" +
            "\n" +
            "### SAM CHARTER.\n" +
            "The first charter is approved by the founder and it is valid, the vote of the founder for approval\n" +
            "The charter never has an expiration date.\n" +
            "The charter package name starts with CHARTER_ORIGINAL and the source code name is CHARTER_ORIGINAL_CODE.\n" +
            "These two packages are a holistic charter, but in the first place, the source code must not contradict\n" +
            "the principles described in CHARTER_ORIGINAL.\n" +
            "````\n" +
            "// the charter is always valid, it is signed by the founder\n" +
            "         List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL = current.stream()\n" +
            "                 .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t->t.getFounderVote()>=1)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .limit(1)\n" +
            "                 .collect(Collectors.toList());\n" +
            "\n" +
            "         // SOURCE CODE CREATED BY THE FOUNDER\n" +
            "         List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL_CODE = current.stream()\n" +
            "                 .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t->t.getFounderVote()>=1)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .limit(1)\n" +
            "                 .collect(Collectors.toList());\n" +
            "````\n" +
            "[Return to main page](../documentation/documentationEng.md)";

    String LAW_2 = "# VOTE_STOCK (How shares are voted.)\n" +
            "How shares are voted.\n" +
            "1. The number of shares is equal to the number of votes.\n" +
            "2. Your votes are recounted every block and if you lose your shares,\n" +
            "   or increase your shares, your cast votes also change\n" +
            "   according to the number of shares.\n" +
            "3. For each law that you voted, for this law, all\n" +
            "   FOR and AGAINST and after that with FOR minus AGAINST and this is the rating of the law.\n" +
            "4. Your votes are divided separately for all the laws that you voted FOR and separately AGAINST\n" +
            "   Example: you have 100 shares and you voted FOR one candidate and for one law,\n" +
            "   you also voted AGAINST two candidates and two laws.\n" +
            "   Now each of your candidates and the law for which you voted FOR will receive 50 votes.\n" +
            "   and for which you voted AGAINST will receive 25 votes AGAINST.\n" +
            "   the formula is simple FOR/number of laws and AGAINST/number of laws you are against.\n" +
            "\n" +
            "______\n" +
            "\n" +
            "````\n" +
            "public double votesLaw(Map<String, Account> balances,\n" +
            "                           Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {\n" +
            "        double yes = 0.0;\n" +
            "        double no = 0.0;\n" +
            "\n" +
            "\n" +
            "        //\n" +
            "        for (String s : YES) {\n" +
            "\n" +
            "            int count = 1;\n" +
            "            count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;\n" +
            "            yes += balances.get(s).getDigitalStockBalance() / count;\n" +
            "\n" +
            "        }\n" +
            "        //\n" +
            "        for (String s : NO) {\n" +
            "            int count = 1;\n" +
            "            count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;\n" +
            "            no += balances.get(s).getDigitalStockBalance() / count;\n" +
            "\n" +
            "        }\n" +
            "\n" +
            "\n" +
            "        return yes - no;\n" +
            "    }\n" +
            "````\n" +
            "\n" +
            "[back to home](../documentationEng/documentationEng.md)";
    String LAW_3 = "# ONE_VOTE (One Voice)\n" +
            "\n" +
            "When these positions are voted count as one score = one vote\n" +
            "(CORPORATE_COUNCIL_OF_REFEREES-Council of Corporate Judges,\n" +
            "GENERAL_EXECUTIVE_DIRECTOR-General Executive Director,\n" +
            "HIGH_JUDGE - Supreme Judge and Board of Shareholders).\n" +
            "Each score that starts with LIBER counts all votes FOR (VoteEnum.YES) and AGAINST (VoteEnum.NO) for it\n" +
            "further deducted from FOR - AGAINST = if the balances are above the threshold, then it becomes the current law. But if a position is elected,\n" +
            "then after that it is sorted from largest to smallest and the largest number that is described for this position is selected.\n" +
            "Recalculation of votes occurs every block.\n" +
            "\n" +
            "After voting, the vote can only be changed to the opposite one.\n" +
            "There is no limit on the number of times you can change your vote. Only those votes that are given by accounts are taken into account\n" +
            "in his position, for example, if the account ceased to be in CORPORATE_COUNCIL_OF_REFEREES, his vote as\n" +
            "CORPORATE_COUNCIL_OF_REFEREES does not count and will not count in voting. All votes are valid until the bills\n" +
            "voters are in their positions. Only those votes from which no more than\n" +
            "four years, but each participant may at any time renew their vote.\n" +
            "\n" +
            "______\n" +
            "\n" +
            "CODE class CurrentLawVotes: method voteGovernment\n" +
            "\n" +
            "````\n" +
            "public int voteGovernment(\n" +
            "             Map<String, Account> balances,\n" +
            "             List<String> governments) {\n" +
            "        int yes = 0;\n" +
            "         int no = 0;\n" +
            "\n" +
            "        List<String> addressGovernment = governments;\n" +
            "       for (String s : YES) {\n" +
            "             if (addressGovernment.contains(s)) {\n" +
            "                 yes += Seting.VOTE_GOVERNMENT;\n" +
            "            }\n" +
            "\n" +
            "         }\n" +
            "         for (String s : NO) {\n" +
            "           if (addressGovernment.contains(s)) {\n" +
            "                 no += Seting.VOTE_GOVERNMENT;\n" +
            "            }\n" +
            "         }\n" +
            "        return yes - no;\n" +
            "    }\n" +
            "\n" +
            "````\n" +
            "[back to home](../documentationEng/documentationEng.md)";
    String LAW_4 = "# Board of Directors.\n" +
            "The Board of Directors consists of the 201 highest rated accounts,\n" +
            "This house has the same powers as the House of Representatives in the United States.\n" +
            "The 201 highest ranking accounts are elected to that position.\n" +
            "Elected by the [VOTE_STOCK](../charter/VOTE_STOCK.md) algorithm\n" +
            "\n" +
            "\n" +
            "public static List<CurrentLawVotesEndBalance> filtersVotes(\n" +
            "List<LawEligibleForParliamentaryApproval> approvalList,\n" +
            "Map<String, Account> balances,\n" +
            "List<Account> BoardOfShareholders,\n" +
            "Map<String, CurrentLawVotes> votesMap\n" +
            ") throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {\n" +
            "//current laws whose votes are greater ORIGINAL_LIMIT_MIN_VOTE\n" +
            "List<CurrentLawVotesEndBalance> current = new ArrayList<>();\n" +
            "// Map<String, CurrentLawVotes> votesMap = null;\n" +
            "List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());\n" +
            "// if (blocks.size() > limitBlocks) {\n" +
            "// votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));\n" +
            "// } else {\n" +
            "// votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);\n" +
            "// }\n" +
            "\n" +
            "\n" +
            "\n" +
            "         //calculate the average number of times he voted for\n" +
            "         Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);\n" +
            "         //calculate the average number of times he voted against\n" +
            "         Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);\n" +
            "\n" +
            "\n" +
            "         //count votes for regular laws and position laws\n" +
            "         for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {\n" +
            "             if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {\n" +
            "                 String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();\n" +
            "                 String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();\n" +
            "                 List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();\n" +
            "                 double vote = 0;\n" +
            "                 int supremeVotes = 0;\n" +
            "                 int boafdOfShareholderVotes = 0;\n" +
            "                 int boardOfDirectors = 0;\n" +
            "                 int primeMinisterVotes = 0;\n" +
            "                 int hightJudgesVotes = 0;\n" +
            "                 int founderVote = 0;\n" +
            "                 double fraction = 0;\n" +
            "\n" +
            "                 //for laws we count special votes\n" +
            "                 vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);\n" +
            "                 List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());\n" +
            "                 boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);\n" +
            "\n" +
            "                 List<String> founder = List.of(Setting.ADDRESS_FOUNDER);\n" +
            "                 founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);\n" +
            "                 CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(\n" +
            "                         address,\n" +
            "                         packageName,\n" +
            "                         vote,\n" +
            "                         supremeVotes,\n" +
            "                         boardOfDirectors,\n" +
            "                         boafdOfShareholderVotes,\n" +
            "                         primeMinisterVotes,\n" +
            "                         hightJudgesVotes,\n" +
            "                         founderVote,\n" +
            "                         fraction\n" +
            "                         laws);\n" +
            "                 current.add(currentLawVotesEndBalance);\n" +
            "\n" +
            "             }\n" +
            "         }\n" +
            "\n" +
            "// List<String> houseOfRepresentativies = new ArrayList<>();\n" +
            "List<String> chamberOfSumpremeJudges = new ArrayList<>();\n" +
            "List<String> boardOfDirectors = new ArrayList<>();\n" +
            "\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance: current) {\n" +
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getVotes() >= Setting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                     boardOfDirectors.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                 }\n" +
            "\n" +
            "             }\n" +
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getVotes() >= Setting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                     chamberOfSumpremeJudges.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                 }\n" +
            "\n" +
            "             }\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "         }\n" +
            "\n" +
            "\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "             if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){\n" +
            "\n" +
            "\n" +
            "                 double vote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).votesLaw(balances, yesAverage, noAverage);int supremeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, chamberOfSumpremeJudges);\n" +
            "                 int boardOfDirectorsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, boardOfDirectors);\n" +
            "\n" +
            "                 currentLawVotesEndBalance.setVotes(vote);\n" +
            "                 currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);\n" +
            "                 currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);\n" +
            "                 currentLawVotesEndBalance.setVotesBoardOfDirectors(boardOfDirectorsVotes);\n" +
            "             }\n" +
            "\n" +
            "         }\n" +
            "\n" +
            "         //the General Executive Director is being scrutinized\n" +
            "         List<String> primeMinister = new ArrayList<>();\n" +
            "         List<String> hightJudge = new ArrayList<>();\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Setting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                 && currentLawVotesEndBalance.getVotes() >= Setting.ALL_STOCK_VOTE\n" +
            "                 ){\n" +
            "                     primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                 }\n" +
            "             }\n" +
            "         }\n" +
            "\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "             if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){\n" +
            "                 int primeMinisterVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, primeMinister);\n" +
            "                 int hightJudgeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, hightJudge);\n" +
            "\n" +
            "                 currentLawVotesEndBalance.setVoteGeneralExecutiveDirector(primeMinisterVotes);\n" +
            "                 currentLawVotesEndBalance.setVoteHightJudge(hightJudgeVotes);\n" +
            "             }\n" +
            "\n" +
            "         }\n" +
            "\n" +
            "         return current;\n" +
            "\n" +
            "     }\n" +
            "[Exit to main page](../documentation/documentationRus.md)";
    String LAW_5 = "# Penalty mechanism\n" +
            "\n" +
            "You make a transaction in which you lose this amount of shares, but\n" +
            "and the account to which the penalty is directed loses such an amount of shares.\n" +
            "\n" +
            "Valid for digital dollars only.\n" +
            "![Lead fine](../screenshots/lead_a_fine_eng.png)\n" +
            "______\n" +
            "\n" +
            "## MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES MECHANISM FOR REDUCING THE NUMBER OF SHARES. Entering fines.\n" +
            "Every time one account sends a digital share to another account but uses VoteEnum.NO, the account\n" +
            "The recipient's digital shares are reduced by the number sent by the sender of the shares.\n" +
            "Example account A sent 100 digital shares to account B with VoteEnum.NO, then account A and account B both lose 100\n" +
            "digital shares. This measure is needed to provide a mechanism for removing the Board of Shareholders from office and also allows for votes to be reduced\n" +
            "destructive accounts, since the number of votes is equal to the number of shares,\n" +
            "when electing CORPORATE_COUNCIL_OF_REFEREES, and other positions that are elected by shares.\n" +
            "This mechanism only applies to digital shares and only if the sender has completed a transaction with\n" +
            "VoteEnum.NO.\n" +
            "\n" +
            "[exit to home](../documentationEng/documentationEng.md)";
    String LAW_6 = "# WHO_HAS_THE_RIGHT_TO_CREATE_LAWS Who has the right to create laws\n" +
            "\n" +
            "String WHO_HAS_THE_RIGHT_TO_CREATE_LAWS = Who has the right to make laws.\n" +
            "Create Laws in Cryptocurrency International Trade Union Corporations Have the Rights\n" +
            "all network members who have at least five digital dollars.\n" +
            "To create law through the International Trade Union Corporation's cryptocurrency mechanism\n" +
            "It is necessary to create an object of the Laws class inside this cryptocurrency, where packetLawName is the name of the law package.\n" +
            "List<String> laws - is a list of laws, String hashLaw - is the address of this package of laws and starts with LIBER.\n" +
            "For a law to be included in the pool of laws, you need to create a transaction where the recipient is the hashLaw of this law and the reward\n" +
            "miner is equal to five digital dollars (5) of this cryptocurrency. After that, as the law gets into the block, it will be in the pool\n" +
            "laws and it will be possible to vote for it.\n" +
            "The number of lines in a package of laws can be as many as needed and there are no restrictions.\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_7 = "# POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES Judicial Power.\n" +
            "Participates in voting for the implementation of amendments.\n" +
            "\n" +
            "The judicial power of the International Trade Union Corporation belongs to\n" +
            "one Supreme Court (CORPORATE_COUNCIL_OF_REFEREES) and such subordinate courts as Corporation International\n" +
            "The Trade Union may from time to time issue and establish.\n" +
            "Judges of both the Supreme and Subordinate Courts hold office upon good behavior and\n" +
            "receive remuneration for their services in a timely manner.\n" +
            "Remuneration must be given from the budget established by law.\n" +
            "Judicial power extends to all matters according to law and equity,\n" +
            "including those initiated by members to challenge the illegal expenditure of funds,\n" +
            "arising in accordance with this Charter, the laws of the International Trade Union Corporation and treaties,\n" +
            "prisoners or who will be concluded in accordance with their authority.\n" +
            "To the controversy,\n" +
            "in which the International Trade Union will be a party to a dispute between two or more network participants.\n" +
            "No trial should be secret, but justice should be administered openly and freely, completely and promptly,\n" +
            "and every person shall have legal protection against injury to life, liberty, or property.\n" +
            "Supreme Court CORPORATE_COUNCIL_OF_REFEREES.\n" +
            "\n" +
            "## How the Corporate Council of Judges is elected.\n" +
            "The Corporate Council of Judges consists of 50 accounts and is elected by the Network Participants,\n" +
            "with the counting system described in VOTE_STOCK, similar to the Board of Directors.\n" +
            "The 50 accounts that received the most votes are selected.\n" +
            "![stock votes](../screenshots/stock_vote.png)\n" +
            "````\n" +
            "//minimum value for the number of positive votes for the law to be valid,\n" +
            "         //positions elected by shares CORPORATE_COUNCIL_OF_REFEREES\n" +
            "         List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()\n" +
            "                 .filter(t -> directors.isElectedByStocks(t.getPackageName()))\n" +
            "                 .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))\n" +
            "                 .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())\n" +
            "                 .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "Each score of such a judge is equal to one vote, similar to [ONE_VOTE](../charterEng/ONE_VOTE.md)\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";

    String LAW_9 = "# PROPERTY_OF_THE_CORPORATION PROPERTY OF THE CORPORATION.\n" +
            "All property owned by the International Trade Union Corporation,\n" +
            "cannot be sold without a valid law,\n" +
            "where the sale process will be described and at what price the property will be sold.\n" +
            "The founder's account, and the accounts of other members are not\n" +
            "corporation account, factions must create a separate account which\n" +
            "will be budgeted and managed only by members of the current factions.\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_10 = "# GENERAL_EXECUTIVE_DIRECTOR General Executive Director\n" +
            "This Director coordinates the actions of other senior directors to implement the strategic plan or\n" +
            "tasks assigned to it by current laws.\n" +
            "All powers must be given to him through existing laws.\n" +
            "This is the highest position elected by the Corporation and is essentially equivalent to the Prime Minister.\n" +
            "\n" +
            "## How the General Executive Director is elected\n" +
            "This director is elected by the Legislature\n" +
            "1. the candidate must receive a rating from BOARD_OF_DIRECTORS of 10 or more [ONE_VOTE](../charterEng/ONE_VOTE.md)\n" +
            "2. Network participants must give more than one vote using the [VOTE_STOCK](../charterEng/VOTE_STOCK.md) method\n" +
            "3. Next, sorting occurs from the largest to the smallest votes received from the shares and\n" +
            "4. One account with the largest number of votes received from BOARD_OF_DIRECTORS is selected.\n" +
            "5. If the founder vetoed a given candidate, then you need to get\n" +
            "   votes BOARD_OF_DIRECTORS 10 or more according to the [ONE_VOTE](../charterEng/ONE_VOTE.md) system\n" +
            "   and votes of the council of judges 2 or more votes according to the [ONE_VOTE](../charterEng/ONE_VOTE.md) system\n" +
            "\n" +
            "````\n" +
            "     List<String> hightJudge = new ArrayList<>();\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){\n" +
            "                if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE\n" +
            "                ){\n" +
            "                    primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "````\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_11 = "# EXPLANATION WHY MONEY DEMURAGE IS USED HERE\n" +
            "Imagine that you are a miner in one of two cryptocurrencies.\n" +
            "One of them halves the income of the cryptocurrency every four years per block.\n" +
            "Similar to bitcoin.\n" +
            "The second has a fixed income per block, which does not change like dogecoin.\n" +
            "If you are a cryptocurrency miner like Bitcoin, your costs are fixed.\n" +
            "You buy a farm, but there is a big problem if the income is halved,\n" +
            "then you must sell coins at twice the price to remain profitable.\n" +
            "Since if every four years production will be reduced, then in order for this\n" +
            "the system remains viable, the cost must double.\n" +
            "How do you not affect the market, you will be forced to reduce the size of the farm in order to\n" +
            "remain profitable, but this will also lead to a decrease in cost.\n" +
            "The number of blocks in bitcoin is limited to 144 blocks, which in any case will not allow\n" +
            "increase profitability and income from transactions cannot double every 4 years, how so\n" +
            "at what point will it become unprofitable to pay for small purchases at an inflated price, which\n" +
            "will reduce turnover.\n" +
            "In other words, for such a system to remain profitable, it is necessary that the cost\n" +
            "doubled every four years.\n" +
            "\n" +
            "If you are a miner of dogecoin, or similar coins, then you have another problem.\n" +
            "Such coins have the main problem of high inflation. Let's imagine such a situation,\n" +
            "a crisis arose and the cost of all coins fell a little, then mine with old equipment\n" +
            "became unprofitable. Then the miners reduce the equipment, but this reduces security,\n" +
            "which in turn reduces the value, but the coin continues to be issued in the same quantity.\n" +
            "Instead of reducing production, it continues to grow, which creates\n" +
            "even more problems.\n" +
            "\n" +
            "How my coin solves these problems.\n" +
            "1. Every six months, 0.2% of digital dollars and 0.4% of digital dollars are destroyed from all accounts.\n" +
            "   digital shares, which does not allow the money supply to grow and it should not be 10 billion\n" +
            "   dollars and 5 billion shares with a difficulty of 10.\n" +
            "2. Unique mining system, if the index is even then the income is equal to difficulty * 30,\n" +
            "3. If the index is not even, then (difficulty * 30) + 1.\n" +
            "4. if the index is even, then only those addresses whose share balance is even can be added to the blockchain.\n" +
            "5. if the index is not even, then everything.\n" +
            "6. difficulty is equal to the number of zeros in the block hash.\n" +
            "7. Two times the same address cannot add a block.\n" +
            "\n" +
            "Now, given all of the above parameters, we can conclude.\n" +
            "A negative rate stimulates the sale of coins, which creates a rate correction every\n" +
            "half a year and does not allow big falls. It also stimulates turnover and limits\n" +
            "mining of coins. How so the number of coins created and destroyed will be equal.\n" +
            "And the dynamic loot change system increases the loot when the difficulty\n" +
            "increases, which allows to meet demand and reduce production when demand falls.\n" +
            "This measure makes the coin rate more resistant to fluctuations.\n" +
            "\n" +
            "\n" +
            "With Silvio Gezel, the negative rate was 1% per month, which would just kill the economy,\n" +
            "under monetarism, the growth of the money supply had to be proportional to the growth of GDP, but since in\n" +
            "this system fails to calculate the real GDP growth, I set a fixed growth, also if the monetary growth\n" +
            "will equal GDP, there is a high probability of Hyperinflation, since GDP does not always reflect real economic growth.\n" +
            "Money must be hard so that a business can predict its long-term investments and from monetarism, only the part that\n" +
            "the money supply should grow linearly, but in general there is a mix of different economic schools, including the Austrian School of Economics.\n" +
            "\n" +
            "With a negative rate of 0.2% every six months for digital dollars and 0.4% for digital stocks, we avoid the consequences of a severe\n" +
            "economic crisis for this currency.\n" +
            "\n" +
            "Such a mechanism creates a price corridor where the lower limit of the value of these digital currencies is the total number of issued digital currencies.\n" +
            "dollars and digital stocks, and the upper limit is the real value. Since as soon as the value becomes higher than the real value,\n" +
            "it becomes more profitable for holders to sell digital dollars and digital shares at inflated prices, thereby saturating the market with money\n" +
            "and creating a correction in the market.\n" +
            "\n" +
            "The main source of monetary crises is rapid changes in commodity prices and slow changes in wages.\n" +
            "Example: Imagine that the value of the currency has risen sharply by 30%, it becomes more profitable for holders not to invest money, since\n" +
            "income from holding currency, higher than now pay more expensive employees, because of the fact that the money stops\n" +
            "invest. People do not receive wages, which leads to the fact that a huge number of goods are not sold,\n" +
            "and this leads to the fact that some manufacturers go bankrupt and lay off many workers, which further reduces wages.\n" +
            "wages from the rest, as the labor market becomes surplus.\n" +
            "\n" +
            "Which in turn causes even more fear among money holders to invest and this process continues until\n" +
            "\"until the value of money starts to decline as the total number of production chains has shrunk and commodities have also shrunk.\n" +
            "\n" +
            "Example: Let's imagine that we had inflation and the value of money fell by 40% within a month, the cost of goods increases sharply,\n" +
            "but wages have not risen, so a lot of goods will not be bought, which leads to the closure of production chains,\n" +
            "which, in turn, due to an excess of workers in the labor market, reduces wages, which also further reduces\n" +
            "the number of goods sold.\n" +
            "The first case A deflationary spiral occurs due to a sharp reduction in money in the market, the second\n" +
            "stagflation occurs more often when a sharply excess amount of money enters the market.\n" +
            "And these two phenomena are two sides of the same coin, in one case we get a deflationary spiral in the other\n" +
            "stagflation.\n" +
            "\n" +
            "To avoid such crises, in this cryptocurrency, money grows in the same predictable amount.\n" +
            "If the market has grown, then the demand for money will increase, which will attract more miners to mine this coin, which in turn\n" +
            "will increase the complexity and thereby increase the number of coins, according to the formula described above, if, on the contrary, demand has fallen,\n" +
            "the difficulty will drop and the loot will decrease, which will make the course more predictable. There are 576 blocks per day.\n" +
            "\n" +
            "A negative rate adjusts the value of coins every six months.\n" +
            "It is also forbidden to use fractional reserve banking for these coins, as their number grows in smaller numbers, and\n" +
            "will not be able to cover the debts incurred due to fractional reserve banking, due to lack of\n" +
            "cash, since with fractional reserve banking, the increase in debt will be much higher than this protocol will create money.\n" +
            "\n" +
            "Also, if you increase the money supply by changing the settings, and making the money supply increase much higher, it can cause hyperinflation or\n" +
            "even galloping inflation.\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_12 = "# FREEDOM_OF_SPEECH The right to free speech\n" +
            "No body of this corporation or entity shall prohibit free practice\n" +
            "any religion; or restrict freedom of speech, conscience or the press\n" +
            "or the right of people to peacefully assemble or associate with one another, or not associate with one another, and\n" +
            "apply to the management of the Corporation of the International Trade Union and to this corporation with a petition for satisfaction of complaints; +\n" +
            "or violate the right to the fruits of one's labor or\n" +
            "the right to a peaceful life of their choice.\n" +
            "Freedoms of speech and conscience include the freedom to contribute to\n" +
            "political campaigns or nominations for corporate office and shall be construed as\n" +
            "extending equally to any means of communication.\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_13 = "# RIGHTS Natural Rights\n" +
            "All members of the network must respect Natural Human Rights and not violate them.\n" +
            "The presumption of innocence must also be respected and each network participant must have the rights to fair, independent\n" +
            "trial.\n" +
            "Each participant has the right to a lawyer or to be his own lawyer.\n" +
            "In general, we also adhere to the same principles as stated in the US Bill of Rights.\n" +
            "\n" +
            "The International Trade Union Corporation should not regulate the cost of goods and services of network participants that\n" +
            "sold through this platform.\n" +
            "Also, the Corporation should not ban certain brands on its site, but may\n" +
            "prohibit the sale of entire groups of goods that fall within the characteristics described by current laws, if\n" +
            "this prohibition does not violate Natural Human Rights. You can take as a source of rights\n" +
            "as a precedent Countries recognized as democratic countries.\n" +
            "\n" +
            "A detailed list is available at the United Nations (UN)\n" +
            "\n" +
            "The right to live\n" +
            "Right to liberty and security of person\n" +
            "Right to privacy\n" +
            "The right to determine and indicate one's nationality\n" +
            "The right to use one's native language\n" +
            "The right to freedom of movement and choice of place of stay and residence\n" +
            "Right to freedom of conscience\n" +
            "\n" +
            "Freedom of thought and speech\n" +
            "Freedom of information\n" +
            "The right to create public associations\n" +
            "Right to hold public events\n" +
            "The right to participate in the management of the affairs of the International Trade Union Corporation\n" +
            "The right to appeal to the bodies of the International Trade Union Corporation and local governments.\n" +
            "\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_14 = "#LEGISLATURE.\n" +
            "Power consists of 3 groups in this system.\n" +
            "1. Council of Shareholders\n" +
            "2. Board of Directors\n" +
            "3. Independent network participants.\n" +
            "\n" +
            "All participants must participate in voting for the law adopted by the system to be valid\n" +
            "(The only exception is the Board of Shareholders, since the Board of Shareholders participates\n" +
            "only in approving amendments to the Charter).\n" +
            "For all votes, only votes cast within the last year are taken into account.\n" +
            "All members can hold multiple positions from different groups, but cannot\n" +
            "occupy several positions in one position category.\n" +
            "Example: One account can be both ***Independent Network Member*** and ***Be Like a Faction***\n" +
            "and ***Member of the Board of Shareholders***, but one account will not be able to take several seats in factions\n" +
            "or in the Board of Shareholders.\n" +
            "\n" +
            "It is the votes from the Shares that are taken into account when electing Factions and Corporate Judges\n" +
            "## Board of Shareholders\n" +
            "The Board of Shareholders is appointed automatically by the system.\n" +
            "The Board of Shareholders consists of 1,500 accounts with the largest number of shares,\n" +
            "but only those accounts are selected that have either been involved in mining in the last year,\n" +
            "either sent digital dollars or digital shares, or participated in voting.\n" +
            "A member of one Board of Shareholders has one vote. One count equals one vote.\n" +
            "The voting system described in [ONE_VOTE](../charterEng/ONE_VOTE.md) is used.\n" +
            "\n" +
            "````\n" +
            "   //definition of the shareholders' council\n" +
            "     public static List<Account> findBoardOfShareholders(Map<String, Account> balances, List<Block> blocks, int limit) {\n" +
            "         List<Block> minersHaveMoreStock = null;\n" +
            "         if (blocks.size() > limit) {\n" +
            "             minersHaveMoreStock = blocks.subList(blocks.size() - limit, blocks.size());\n" +
            "         } else {\n" +
            "             minersHaveMoreStock = blocks;\n" +
            "         }\n" +
            "         List<Account> boardAccounts = minersHaveMoreStock.stream().map(\n" +
            "                         t -> new Account(t.getMinerAddress(), 0, 0))\n" +
            "                 .collect(Collectors.toList());\n" +
            "\n" +
            "         for (Block block : minersHaveMoreStock) {\n" +
            "             for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {\n" +
            "                 boardAccounts.add(new Account(dtoTransaction.getSender(), 0, 0));\n" +
            "             }\n" +
            "\n" +
            "         }\n" +
            "````\n" +
            "\n" +
            "## Factions\n" +
            "The Board of Directors is also elected as corporate judges, their number is 201 seats.\n" +
            "The 201 accounts with the highest ratings become members of the board of directors.\n" +
            "This is how the votes of the Board of Directors are counted [ONE_VOTE](../charterEng/ONE_VOTE.md)\n" +
            "\n" +
            "## Independent Network Participants.\n" +
            "All network participants who have shares and are not included in the first three categories listed above,\n" +
            "are ***independent members of the network***. The votes of each such participant are equal to\n" +
            "to the number of shares at the moment and is described in detail in [VOTE_STOCK](../charterEng/VOTE_STOCK.md).\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";


}
