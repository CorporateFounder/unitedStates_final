package International_Trade_Union.originalCorporateCharter;

public interface OriginalCHARTER_ENG {
    String LAW_1 = "# HOW THE LAWS ARE CHOSEN.\n" +
            "\n" +
            "## Approval of the law\n" +
            "_____\n" +
            "## CHARTER\n" +
            "No law is retroactive. No law shall violate the existing statute or be inconsistent with\n" +
            "other applicable laws. If there is a contradiction between several laws from one set of laws,\n" +
            "then the current one is the one that is higher in the index in the list. Example: alcohol sales package\n" +
            "the law under index 3 contradicts the law from index 17, in this case the law under index three will be valid,\n" +
            "because he is higher in status.\n" +
            "In the event of a conflict between several laws in force, the Judiciary shall give priority to those laws\n" +
            "which were adopted earlier, but should be taken into account precisely from the last date of adoption of the law.\n" +
            "The law is in force as long as it satisfies the conditions for the adoption of the law and as soon as the condition\n" +
            "violated, the law loses its force until the law is re-adopted.\n" +
            "In the voting of all laws, only votes cast in the last year are taken into account.\n" +
            "\n" +
            "If there are States or Private Jurisdictions that are part of this union,\n" +
            "then a House of Representatives should be formed. The House of Representatives must be elected\n" +
            "as from multi-member districts, where each state must have at least one candidate, but\n" +
            "for every 40,000 voters, one candidate. Also, each candidate must be elected according to such a system.\n" +
            "Each voter has v = n - 1 votes, where n is the number of candidates for that constituency.\n" +
            "Each voter can vote YES or NO by casting their votes for candidates.\n" +
            "Further, for all participants, all the votes given for him are summed up according to the formula YES - NO,\n" +
            "the result is the rating. Places shall be allocated to the persons with the highest number of\n" +
            "rating. If there are states or private jurisdictions in the Union, then no law is\n" +
            "in force until it is also approved by the House of Representatives.\n" +
            "\n" +
            "There are minimum requirements that all members of this union must comply with (If this state\n" +
            "or private jurisdiction)\n" +
            "1. All participants must trade among themselves only in this cryptocurrency (dollars or stocks)\n" +
            "2. No member of this alliance should initiate aggression against the members of this alliance.\n" +
            "3. Union members should not have the right to impose a form of government on each other (an exception is the method\n" +
            "   election of members of the House of Representatives, and other members must be observers that they were elected\n" +
            "   according to the rules described. The House of Representatives shall be elected as described in this system,\n" +
            "   citizens of these jurisdictions).\n" +
            "4. All members of the union must recognize this charter as the most important law and the laws also adopted\n" +
            "   factions and the House of Representatives.\n" +
            "5. All citizens of this union should have the right to freely cross the borders of the members of this union.\n" +
            "6. Protectionist measures should not be applied against the citizens of the members of this union and the members of the union themselves.\n" +
            "\n" +
            "\n" +
            "\n" +
            "All laws are divided into several groups.\n" +
            "1. Ordinary laws\n" +
            "2. Strategic Plan\n" +
            "3. Appointed by the Legislature\n" +
            "4. Laws that create new positions. These positions are approved only by the Legislative Power.\n" +
            "5. Amendments to the Charter\n" +
            "6. The charter itself\n" +
            "\n" +
            "\n" +
            "\n" +
            "NOTHING removes the vote from the candidate when voting.\n" +
            "### REGULAR LAWS\n" +
            "To establish ordinary laws,\n" +
            "1. The name of the package of law should not match the highlighted keywords.\n" +
            "2. The law must receive more than 1 vote according to the scoring system described by [VOTE_STOCK](../charterEng/VOTE_STOCK.md)\n" +
            "3. Must receive 15% or more votes from factions according to the scoring system described in [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)\n" +
            "4. If the founder vetoed the law, you need to get 15% or more votes from factions in the system\n" +
            "   count described in [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md) and 2 or more votes from\n" +
            "   Council of Judges (ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES) according to the vote counting system\n" +
            "   [ONE_VOTE](../charterEng/ONE_VOTE.md)\n" +
            "\n" +
            "\n" +
            "Sample code in LawsController current law:\n" +
            "````\n" +
            "      //laws must be approved by everyone.\n" +
            "         //laws must be approved by everyone.\n" +
            "         List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()\n" +
            "                 .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                 .filter(t->!Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            "                 .filter(t->!Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))\n" +
            "                 .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                 t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS &&\n" +
            "                         t.getVotesCorporateCouncilOfReferees() > Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            "````\n" +
            "### STRATEGIC PLAN.\n" +
            "The strategic plan is the general plan for the entire network and is approved in the same way as an ordinary law,\n" +
            "but there are some differences from ordinary laws.\n" +
            "1. The strategic plan package should be called STRATEGIC_PLAN\n" +
            "2. All plans that have been approved are sorted from highest to lowest by the number of votes,\n" +
            "   received from the Board of Directors.\n" +
            "3. After Sorting, only one PLAN with the most votes received from shares is selected.\n" +
            "\n" +
            "````\n" +
            "//the plan is approved by everyone\n" +
            "         List<CurrentLawVotesEndBalance> planFourYears = current.stream()\n" +
            "                 .filter(t->!directors.contains(t.getPackageName()))\n" +
            "                 .filter(t->Seting.STRATEGIC_PLAN.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                       \n" +
            "                       \n" +
            "                         && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .limit(1)\n" +
            "                 .collect(Collectors.toList());\n" +
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
            "   //positions created by all participants\n" +
            "         List<CurrentLawVotesEndBalance> createdByFraction = current.stream()\n" +
            "                 .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            "                 .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                 .collect(Collectors.toList());\n" +
            "         //adding positions created by the board of directors\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByFraction) {\n" +
            "             directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());\n" +
            "         }\n" +
            "\n" +
            "         //positions elected only by all participants\n" +
            "         List<CurrentLawVotesEndBalance> electedByFractions = current.stream()\n" +
            "                 .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                         t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .collect(Collectors.toList());\n" +
            "\n" +
            "````\n" +
            "\n" +
            "There are also positions that are created with the help of laws, these positions are also approved by the Legislative power.\n" +
            "For each such position, there is only one seat for each title.\n" +
            "The name of such packages starts with ADD_DIRECTOR_.\n" +
            "With the obligatory underscore.\n" +
            "\n" +
            "### CHARTER AMENDMENTS\n" +
            "To amend the charter, the law package must be named AMENDMENT_TO_THE_CHARTER.\n" +
            "For an amendment to be valid\n" +
            "1. It is necessary that 35% or more of the votes received from the Council of Shareholders by the counting system [ONE_VOTE](../charterEng/VOTE_FRACTION.md).\n" +
            "2. Need to get 15% or more votes from factions by counting system [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md).\n" +
            "3. Need to get 5 or more votes from the Legislative Branch of the Corporate Chief Justices.\n" +
            "\n" +
            "\n" +
            "\n" +
            "````\n" +
            "    //introduction of amendments to the charter\n" +
            "         List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()\n" +
            "                 .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                 .filter(t-> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT\n" +
            "                 && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
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
    String LAW_4 = "# FAVORITE_FRACTION\n" +
            "The faction is extracted like the chief judges, 200 scores received by the maximum number of votes\n" +
            "from a unique electoral one, as previously and an observed share equal to one vote of the described\n" +
            "in VOTE_STOCK\n" +
            "\n" +
            "#VOTE_FRACTION\n" +
            "This voting system is used only for factions.\n" +
            "First, 200 factions are selected that have become legitimate.\n" +
            "Then all the votes given to 200 selected factions are summed up.\n" +
            "After that, the share of each fraction from the total amount is determined.\n" +
            "votes cast for this faction.\n" +
            "The number of votes of each faction is equal to its percentage shares.\n" +
            "Thus, if a faction has 23% of the votes of all votes, out of\n" +
            "200 factions, then her vote is equal to 23%.\n" +
            "On behalf of the factions, the leaders always act and because of this it is\n" +
            "First of all, the leader system. Identical factions with ideological\n" +
            "system here can be represented by different leaders, even\n" +
            "if they are from the same community.\n" +
            "\n" +
            "Then every time a faction votes for laws,\n" +
            "that start with LIBER (VoteEnum.YES) or (VoteEnum.NO).\n" +
            "This law counts all the votes given *** for ***\n" +
            "and *** against ***, after which it is subtracted from *** for *** - *** against ***.\n" +
            "This result is displayed as a percentage.\n" +
            "\n" +
            "````\n" +
            "  //faction voice\n" +
            "     public double voteFractions(Map<String, Double> fractions){\n" +
            "         double yes = 0;\n" +
            "         double no = 0;\n" +
            "         double sum = fractions.entrySet().stream()\n" +
            "                 .map(t->t.getValue())\n" +
            "                 .collect(Collectors.toList())\n" +
            "                 .stream().reduce(0.0, Double::sum);\n" +
            "\n" +
            "         for (String s : YES) {\n" +
            "             if (fractions.containsKey(s)) {\n" +
            "                 yes += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;\n" +
            "             }\n" +
            "\n" +
            "         }\n" +
            "         for (String s : NO) {\n" +
            "             if (fractions.containsKey(s)) {\n" +
            "                 no += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;\n" +
            "             }\n" +
            "\n" +
            "         }\n" +
            "         return yes - no;\n" +
            "\n" +
            "     }\n" +
            "\n" +
            "````\n" +
            "\n" +
            "[Return to main page](../documentationEng/documentationEng.md)";
    String LAW_5 = "# Penalty mechanism\n" +
            "\n" +
            "You make a transaction in which you lose this amount of shares, but\n" +
            "and the account to which the penalty is directed loses such an amount of shares.\n" +
            "\n" +
            "Valid for digital dollars only.\n" +
            "![Lead fine](../screenshots/lead_a_fine_eng.png)\n" +
            "______\n" +
            "\n" +
            "## MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES MECHANISM FOR REDUCING THE NUMBER OF SHARES. Entering penalties.\n" +
            "Every time one account sends a digital share to another account but uses VoteEnum.NO, the account\n" +
            "recipient's digital shares are reduced by the amount sent by the share sender.\n" +
            "Example account A sent to account B 100 digital shares with VoteEnum.NO, then account A and account B both lose 100\n" +
            "digital shares. This measure is needed so that there is a mechanism to dismiss the Board of Shareholders and also allows you to lower your votes\n" +
            "destructive accounts, since the number of votes is equal to the number of shares,\n" +
            "when electing CORPORATE_COUNCIL_OF_REFEREES, Fractions and other positions that are elected by shares.\n" +
            "This mechanism only works on digital shares and only if the sender has made a transaction with\n" +
            "VoteEnum.NO.\n" +
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
            "Approves the Chief Justice.\n" +
            "Participates in the voting on the introduction of amendments.\n" +
            "\n" +
            "The judicial power of the International Trade Union Corporation is vested in\n" +
            "one Supreme Court and such inferior courts as the Corporation International\n" +
            "The Merchant Union may issue and establish from time to time.\n" +
            "Judges of both the supreme and inferior courts hold their offices, with good conduct and\n" +
            "in due time receive remuneration for their services.\n" +
            "Remuneration must be given from the budget established by laws.\n" +
            "Judicial power extends to all cases of law and justice,\n" +
            "including those initiated by members to challenge the misappropriation of funds,\n" +
            "arising under these Articles, the laws of the International Trade Union Corporation and treaties,\n" +
            "imprisoned or to be imprisoned according to their authority.\n" +
            "to controversy,\n" +
            "in which the International Trade Union will be party to a dispute between two or more members of the network.\n" +
            "No judgment shall be secret, but justice shall be administered openly and free of charge, completely and without delay,\n" +
            "and every person shall have legal protection against injury to life, liberty, or property.\n" +
            "Supreme Court CORPORATE_COUNCIL_OF_REFEREES and Chief Justice HIGH_JUDGE.\n" +
            "\n" +
            "## How the Corporate Council of Judges is elected.\n" +
            "The Corporate Council of Judges consists of 55 accounts and is elected by the Network Members,\n" +
            "with the scoring system described in VOTE_STOCK, similar to Board of Directors and Factions.\n" +
            "The 55 accounts that received the most votes are selected.\n" +
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
    String LAW_8 = "# String HOW_THE_CHIEF_JUDGE_IS_CHOSEN HOW HIGH_JUDGE IS CHOSEN.\n" +
            "The Chief Justice is elected by CORPORATE_COUNCIL_OF_REFEREES.\n" +
            "Each member of the network can apply for the position of Chief Justice by creating a law called\n" +
            "package that matches HIGH_JUDGE\n" +
            "position, where the address of the sender of this transaction must match the first line from the list of laws of this package.\n" +
            "The cost of the law is five digital dollars as a reward to the earner.\n" +
            "The account with the most remaining votes receives the position.\n" +
            "The voting mechanism is described in [ONE_VOTE](../charterEng/ONE_VOTE.md).\n" +
            "Elects the Chief Justice, Corporate Council of Judges. (CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "Sample code as stated by the supreme judge. Class LawsController: method currentLaw. Code section\n" +
            "\n" +
            "````\n" +
            "       //positions elected by the board of corporate chief judges\n" +
            "       List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()\n" +
            "                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))\n" +
            "                .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "## Powers of Chief Justice\n" +
            "Chief Justice\n" +
            "can participate in resolving disputes within network members, like CORPORATE_COUNCIL_OF_REFEREES,\n" +
            "but his vote is higher than that of CORPORATE_COUNCIL_OF_REFEREES.\n" +
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
            "This Director coordinates the actions of the other senior directors to implement the strategic plan or\n" +
            "the tasks assigned to it by the laws in force.\n" +
            "All powers must be given to him through existing laws.\n" +
            "This is the highest position elected by the Corporation and is essentially the analogue of the prime minister.\n" +
            "\n" +
            "## How the CEO is elected\n" +
            "This director is elected by the Legislature\n" +
            "3. Fractions must give 15% or more votes using the method [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)\n" +
            "4. Network participants must give more than one vote using the [VOTE_STOCK](../charterEng/VOTE_STOCK.md) method\n" +
            "5. Next comes the sorting from the highest to the lowest received votes from the shares and\n" +
            "6. One account with the largest number of votes received from factions is selected.\n" +
            "7. If the founder vetoed this candidate, then you need to get\n" +
            "   Faction votes 15% or more according to the system [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)\n" +
            "   and votes of the council of judges 2 or more votes according to the system [ONE_VOTE](../charterEng/ONE_VOTE.md)\n" +
            "\n" +
            "````\n" +
            "   //positions created by all participants\n" +
            "         List<CurrentLawVotesEndBalance> createdByFraction = current.stream()\n" +
            "                 .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            "                 .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                 .collect(Collectors.toList());\n" +
            "         //adding positions created by the board of directors\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByFraction) {\n" +
            "             directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());\n" +
            "         }\n" +
            "\n" +
            "         //positions elected only by all participants\n" +
            "         List<CurrentLawVotesEndBalance> electedByFractions = current.stream()\n" +
            "                 .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                         t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .collect(Collectors.toList());\n" +
            "\n" +
            "````\n" +
            "\n" +
            "````\n" +
            "  public static List<CurrentLawVotesEndBalance> filtersVotes(\n" +
            "             List<LawEligibleForParliamentaryApproval> approvalList,\n" +
            "             Map<String, Account> balances,\n" +
            "             List<Account> BoardOfShareholders,\n" +
            "             List<Block> blocks,\n" +
            "             int limitBlocks\n" +
            "     ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {\n" +
            "         //acting laws whose votes are greater than ORIGINAL_LIMIT_MIN_VOTE\n" +
            "         List<CurrentLawVotesEndBalance> current = new ArrayList<>();\n" +
            "         Map<String, CurrentLawVotes> votesMap = null;\n" +
            "         List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());\n" +
            "         if (blocks.size() > limitBlocks) {\n" +
            "             votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));\n" +
            "         } else {\n" +
            "             votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);\n" +
            "         }\n" +
            "\n" +
            "         //calculate the average number of times he voted for\n" +
            "         Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);\n" +
            "         //calculate the average number of times he downvoted\n" +
            "         Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);\n" +
            "\n" +
            "\n" +
            "         //count the votes for the normal laws and laws of positions\n" +
            "         for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {\n" +
            "             if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {\n" +
            "                 String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();\n" +
            "                 String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();\n" +
            "                 List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();\n" +
            "                 double vote = 0;\n" +
            "                 int supremeVotes = 0;\n" +
            "                 int boafdOfShareholderVotes = 0;\n" +
            "                 int houseOfRepresentativiesVotes = 0;\n" +
            "                 int primeMinisterVotes = 0;\n" +
            "                 int hightJudgesVotes = 0;\n" +
            "                 int founderVote = 0;\n" +
            "                 double fraction = 0;\n" +
            "\n" +
            "                 //count special votes for laws\n" +
            "                 vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());\n" +
            "                 boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);\n" +
            "\n" +
            "                 List<String> founder = List.of(Seting.ADDRESS_FOUNDER);\n" +
            "                 founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);\n" +
            "                 CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(\n" +
            "                         address,\n" +
            "                         packagename,\n" +
            "                         vote,\n" +
            "                         supremeVotes,\n" +
            "                         houseOfRepresentativesVotes,\n" +
            "                         boafdOfShareholderVotes,\n" +
            "                         primeMinisterVotes,\n" +
            "                         hightJudgesVotes,\n" +
            "                         founderVote,\n" +
            "                         fraction,\n" +
            "                         laws);\n" +
            "                 current.add(currentLawVotesEndBalance);\n" +
            "\n" +
            "             }\n" +
            "         }\n" +
            "\n" +
            "         List<String> houseOfRepresentativies = new ArrayList<>();\n" +
            "         List<String> chamberOfSumpremeJudges = new ArrayList<>();\n" +
            "         Map<String, Double> fractions = new HashMap<>();\n" +
            "\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance: current) {\n" +
            "// if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){\n" +
            "// if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "// houseOfRepresentativies.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "// }\n" +
            "//\n" +
            "// }\n" +
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                     chamberOfSumpremeJudges.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                 }\n" +
            "\n" +
            "             }\n" +
            "\n" +
            "\n" +
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.FRACTION.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                     fractions.put(currentLawVotesEndBalance.getLaws().get(0), currentLawVotesEndBalance.getVotes());\n" +
            "                 }\n" +
            "             }\n" +
            "\n" +
            "         }\n" +
            "\n" +
            "\n" +
            "\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "             if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){\n" +
            "\n" +
            "\n" +
            "                 double vote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).votesLaw(balances, yesAverage, noAverage);\n" +
            "                 int supremeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, chamberOfSumpremeJudges);\n" +
            "                 int houseOfRepresentativiesVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, houseOfRepresentativies);\n" +
            "                 double fractionsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteFractions(fractions);\n" +
            "\n" +
            "                 currentLawVotesEndBalance.setVotes(vote);\n" +
            "                 currentLawVotesEndBalance.setVotesBoardOfDirectors(houseOfRepresentativiesVotes);\n" +
            "                 currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);\n" +
            "                 currentLawVotesEndBalance.setFractionVote(fractionsVotes);\n" +
            "             }\n" +
            "\n" +
            "         }\n" +
            "\n" +
            "         //examines the CEO\n" +
            "         List<String> primeMinister = new ArrayList<>();\n" +
            "         List<String> hightJudge = new ArrayList<>();\n" +
            "         for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE\n" +
            "                 && currentLawVotesEndBalance.getFractionVote() >= 0 ||\n" +
            "                 currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && currentLawVotesEndBalance.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES){\n" +
            "                     primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                 }\n" +
            "             }\n" +
            "\n" +
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.HIGH_JUDGE.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES){\n" +
            "                     hightJudge.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                 }\n" +
            "             }\n" +
            "         }\n" +
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
            "\n" +
            "         return current;\n" +
            "\n" +
            "     }\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "     //excluding the House of Representatives\n" +
            "     public static List<CurrentLawVotesEndBalance> filters(List<LawEligibleForParliamentaryApproval> approvalList, Map<String, Account> balances,\n" +
            "                                                           List<Account> BoardOfShareholders, List<Block> blocks, int limitBlocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {\n" +
            "         //acting laws whose votes are greater than ORIGINAL_LIMIT_MIN_VOTE\n" +
            "         List<CurrentLawVotesEndBalance> current = new ArrayList<>();\n" +
            "         Map<String, CurrentLawVotes> votesMap = null;\n" +
            "         List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());\n" +
            "         if (blocks.size() > limitBlocks) {\n" +
            "             votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));\n" +
            "         } else {\n" +
            "             votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);\n" +
            "         }\n" +
            "\n" +
            "         //calculate the average number of times he voted for\n" +
            "         Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);\n" +
            "         //calculate the average number of times he downvoted\n" +
            "         Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);\n" +
            "\n" +
            "         for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {\n" +
            "             if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {\n" +
            "                 String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();\n" +
            "                 String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();\n" +
            "                 List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();\n" +
            "                 double vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votes(balances, yesAverage, noAverage);\n" +
            "\n" +
            "                 CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(address, packageName, vote, 0, 0, 0, 0, 0, 0, 0, laws);\n" +
            "                 current.add(currentLawVotesEndBalance);\n" +
            "\n" +
            "             }\n" +
            "         }\n" +
            "         return current;\n" +
            "     }\n" +
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
            "All members of the network must respect the Natural Human Rights and not violate them.\n" +
            "\"The presumption of innocence must also be respected and every member of the network must have the right to a fair and independent\n" +
            "trial.\n" +
            "Each participant has the right to a lawyer or to be his own lawyer.\n" +
            "\n" +
            "The International Trade Union Corporation shall not regulate the cost of goods and services of network members that\n" +
            "sell through this platform.\n" +
            "Also, the Corporation should not ban individual brands on its site, but may\n" +
            "prohibit the sale of entire groups of goods that fall within the characteristics described by applicable laws, if\n" +
            "this prohibition does not violate Natural Human Rights. As a source of rights, you can take\n" +
            "as a precedent Countries recognized as democratic countries.\n" +
            "\n" +
            "A detailed list is at the United Nations (UN)\n" +
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
            "Freedom of Information\n" +
            "The right to form public associations\n" +
            "The right to hold public events\n" +
            "The right to participate in the management of the affairs of the Corporation of the International Trade Union\n" +
            "The right to appeal to the bodies of the Corporation of the International Trade Union and local governments.\n" +
            "\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_14 = "#LEGISLATURE.\n" +
            "Power consists of 3 groups in this system.\n" +
            "1. Board of Shareholders\n" +
            "2. Fractions\n" +
            "3. Independent members of the network.\n" +
            "\n" +
            "All participants must participate in the vote for the law adopted by the system to be valid\n" +
            "(The only exception is the Board of Shareholders, since the Board of Shareholders participates\n" +
            "only in the approval of amendments to the Charter).\n" +
            "For all votes, only votes cast in the last year count.\n" +
            "All members may hold multiple positions from different groups, but may not\n" +
            "hold more than one position in the same category.\n" +
            "Example: One account can be both ***Independent Network Member*** and ***Be like a faction***\n" +
            "and ***Member of the Board of Shareholders***, but one account cannot occupy several seats in fractions\n" +
            "or in the Board of Shareholders.\n" +
            "\n" +
            "It is the votes from the Shares that are taken into account when electing Fractions and Corporate Judges\n" +
            "## Board of Shareholders\n" +
            "The Board of Shareholders is automatically appointed by the system.\n" +
            "The Board of Shareholders consists of 1500 accounts with the largest number of shares,\n" +
            "but only those accounts are selected that have either been mining in the last year,\n" +
            "either sent digital dollars or digital shares, or participated in voting.\n" +
            "A member of one Board of Shareholders has one vote. One score equals one vote.\n" +
            "The voting system described in [ONE_VOTE](../charterEng/ONE_VOTE.md) is used\n" +
            "\n" +
            "````\n" +
            "   //determining the board of shareholders\n" +
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
            "## Fractions\n" +
            "Fractions are elected in the same way as corporate judges, their number is 200 seats.\n" +
            "The peculiarity of factions is that their votes are equal to the share of support relative to other factions.\n" +
            "When we say faction, we always mean a legal or natural person who, on behalf of\n" +
            "of his group votes and because of this, one account may have more votes than when voting judges.\n" +
            "This is how faction votes are counted [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)\n" +
            "\n" +
            "## Independent Network Members.\n" +
            "All network members who have shares and are not included in the first three categories listed above,\n" +
            "are ***independent members of the network***. The votes of each such participant are equal to\n" +
            "to the number of shares at the moment and is described in detail in [VOTE_STOCK](../charterEng/VOTE_STOCK.md).\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";
    String LAW_15 = "# BUDGET AND EMISSION.\n" +
            "\n" +
            "## BUDGET\n" +
            "The company's budget is the BUDGET address. You can both send money and withdraw money to this address.\n" +
            "How to withdraw money from this account.\n" +
            "1. First, a Law (Document) is created with the name budget and in this package,\n" +
            "   in the form of lists, addresses should be written and, separated by a space, the amount of the digital dollar and digital shares.\n" +
            "2. Next, the participants vote for one of these packages using the VOTE_STOCK method.\n" +
            "3. Packages are taken into account, from which the time of creation of these laws has not gone more than 15 days.\n" +
            "4. The one package that receives the most votes becomes valid, and\n" +
            "   amounts are withdrawn from it (only those packages in which at least 300,000 votes are taken into account).\n" +
            "5. You can spend from the budget balance once every 15 days. A digital year is 360 days, and there are 576 blocks in one day.\n" +
            "\n" +
            "\n" +
            "## ISSUE\n" +
            "The issue allows you to create up to 25 thousand digital dollars every fifteen days.\n" +
            "An issue is created in the same way as a budget, with the only difference that the name of the package must\n" +
            "be EMISSION.\n" +
            "\n" +
            "The budget and Emission are primarily intended for spending public goods and developing the system.\n" +
            "This includes judges and other employees.\n" +
            "Also, you cannot spend more than the amount from the budget than it is, and you cannot spend\n" +
            "more than 25 thousand digital dollars for issue in one document.\n" +
            "Every 15 days for the issue of no more than one hundred thousand digital dollars.\n";

}
