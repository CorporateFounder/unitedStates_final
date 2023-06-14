package International_Trade_Union.originalCorporateCharter;

public interface OriginalCHARTER_ENG {
    String HOW_LAWS_ARE_CHOSEN_1 = "# HOW THE LAWS ARE CHOSEN.\n" +
            "\n" +
            "## Approval of the law\n" +
            "_____\n" +
            "\n" +
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
            "In the voting of all laws, only the votes given in the last four years are taken into account.\n" +
            "All laws are divided into several groups.\n" +
            "1. Ordinary laws\n" +
            "2. Strategic Plan\n" +
            "3. Budget\n" +
            "4. Appointed by the Legislature\n" +
            "6. Laws that create new positions. These positions are approved only by the Legislative Power.\n" +
            "7. Amendments to the Charter\n" +
            "8. The charter itself\n" +
            "\n" +
            "NOTHING removes the vote from the candidate when voting.\n" +
            "### REGULAR LAWS\n" +
            "To establish ordinary laws,\n" +
            "1. The name of the package of law should not match the highlighted keywords.\n" +
            "2. The law must receive more than 1 vote according to the scoring system described by [VOTE_STOCK](../charter/VOTE_STOCK.md)\n" +
            "3. Must receive 10 or more votes of the Board of Directors according to the scoring system described in [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "\n" +
            "\n" +
            "Sample code in LawsController current law:\n" +
            "````\n" +
            "      //laws must be approved by everyone.\n" +
            "         List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()\n" +
            "                 .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                 .filter(t->!Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            "                 .filter(t->!Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))\n" +
            "                 .filter(t ->\n" +
            "                  t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "   \n" +
            "\n" +
            "````\n" +
            "\n" +
            "### STRATEGIC PLAN.\n" +
            "The strategic plan is the general plan for the entire network and is approved in the same way as an ordinary law,\n" +
            "but there are some differences from ordinary laws.\n" +
            "1. The strategic plan package should be called STRATEGIC_PLAN\n" +
            "2. All plans that have been approved are sorted from highest to lowest by the number of votes,\n" +
            "   received from the Board of Directors.\n" +
            "3. After Sorting, only one PLAN with the highest number of votes received from the Board of Directors is selected.\n" +
            "\n" +
            "````\n" +
            "//the plan is approved by everyone\n" +
            "         List<CurrentLawVotesEndBalance> planFourYears = current.stream()\n" +
            "                 .filter(t->!directors.contains(t.getPackageName()))\n" +
            "                 .filter(t->Seting.STRATEGIC_PLAN.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                       \n" +
            "                       \n" +
            "                         && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            "                 .limit(1)\n" +
            "                 .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "### BUDGET\n" +
            "The budget is the overall cost for the system and is approved similarly to the Strategic Plan,\n" +
            "but the name of the package should be BUDGET and it is also in a single copy.\n" +
            "\n" +
            "````\n" +
            "  //budget approved by everyone\n" +
            "         List<CurrentLawVotesEndBalance> budjet = current.stream()\n" +
            "                 .filter(t-> !directors.contains(t.getPackageName()))\n" +
            "                 .filter(t->Seting.BUDGET.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t->\n" +
            "                         t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                       \n" +
            "                       \n" +
            "                         && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            "                 .limit(1)\n" +
            "                 .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "### POSTS THAT ARE APPOINTED ONLY BY THE LEGISLATIVE AUTHORITY\n" +
            "There are positions that are appointed only by the Legislature and such positions include\n" +
            "General Executive Director. This position is similar to the Prime Minister and is\n" +
            "Ispoexercising power in this system.\n" +
            "Each such position can be limited to the number that is defined in this system.\n" +
            "for this position. Example: There is only one CEO position.\n" +
            "Elected in the same way as ***strategic plan*** and ***budget***.\n" +
            "But the number is determined for each position separately.\n" +
            "````\n" +
            "   //positions elected only by all participants\n" +
            "         List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()\n" +
            "                 .filter(t -> directors.isElectedByBoardOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                \n" +
            "              \n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            "                 .collect(Collectors.toList());\n" +
            "                \n" +
            "                   //group by list\n" +
            "         Map<String, List<CurrentLawVotesEndBalance>> group = electedByBoardOfDirectors.stream()\n" +
            "                 .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));\n" +
            "\n" +
            "         Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();\n" +
            "\n" +
            "         // leave the amount that is described in this post\n" +
            "         for (Map.Entry<String, List<CurrentLawVotesEndBalance>> stringListEntry : group.entrySet()) {\n" +
            "             List<CurrentLawVotesEndBalance> temporary = stringListEntry.getValue();\n" +
            "             temporary = temporary.stream()\n" +
            "                     .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors))\n" +
            "                     .limit(directors.getDirector(stringListEntry.getKey()).getCount())\n" +
            "                     .collect(Collectors.toList());\n" +
            "             original_group.put(directors.getDirector(stringListEntry.getKey()), temporary);\n" +
            "         }\n" +
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
            "1. It is necessary that 20% or more of the votes received from the Council of Shareholders by the counting system [ONE_VOTE](../charter/ONE_VOTE.md).\n" +
            "2. Need to get 20% or more votes from the Board of Directors by the [ONE_VOTE] counting system (../charter/ONE_VOTE.md).\n" +
            "3. Need to get 5 or more votes from the Legislative Branch of the Corporate Chief Justices.\n" +
            "\n" +
            "\n" +
            "![Charter amendments](../screenshots/amendment-chapter.png)\n" +
            "````\n" +
            "    //introduction of amendments to the charter\n" +
            "         List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()\n" +
            "                 .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                 .filter(t-> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                 .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT\n" +
            "                 && t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT\n" +
            "                 && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT\n" +
            "                )\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed()).collect(Collectors.toList());\n" +
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
            "                 .filter(t->!directors.isCabinets(t.getPackageName())).filter(t->t.getFounderVote()>=1)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .limit(1)\n" +
            "                 .collect(Collectors.toList());\n" +
            "````\n" +
            "[Return to main page](../documentation/documentationEng.md)";
    String VOTE_STOCK_2 = "# VOTE_STOCK (How shares are voted.)\n" +
            "\n" +
            "How shares are voted.\n" +
            "All shares held by the account are equal to the same number of votes.\n" +
            "Every time someone makes a transaction to the account, is the packet address that starts with\n" +
            "LIBER he votes on this package. Only those votes from which no more than four years have passed are taken into account.\n" +
            "If the transaction was made ***VoteEnum.YES,*** then this account receives votes ***for***, formula\n" +
            "yesV = number of votes equal to the sender's shares.\n" +
            "yesN = how many laws this account voted for with VoteEnum.YES\n" +
            "resultYES = yesV / yesN). Example: an account voted for three accounts that start with LIBER,\n" +
            "100 shares, 100 votes. 100 / 3 = 33.3 means each account will receive 33.3 votes.\n" +
            "\n" +
            "If the transaction was made with VoteEnum.NO,\n" +
            "then the same formula is used, but now all bills for which he voted against are taken into account\n" +
            "example the same account voted for two accounts against, it has the same one hundred shares.\n" +
            "resultNO = noV / noN = 50 = 50 means every bill he voted for,\n" +
            "against will receive 50 votes against.\n" +
            "Further, each score that starts with LIBER counts and sums up all the votes given to it ***FOR*** (VoteEnum.YES)\n" +
            "and ***NO*** (VoteEnum.NO).\n" +
            "Then this formula is used remainder = resultYES - resultNO.\n" +
            "It is this result that is displayed as votes cast.\n" +
            "At any time you can change your voice, but only to the opposite, which means if\n" +
            "If you voted for a YES candidate then you can only change to NO and back.\n" +
            "The number of times you can change your voice is not limited.\n" +
            "With each block there is a recalculation of votes, if you lose your shares, your candidates\n" +
            "also lose their votes. This measure is specifically implemented so that elected positions\n" +
            "were interested in the fact that those who vote for them prospered and did not lose their shares.\n" +
            "Only CORPORATE_COUNCIL_OF_REFEREES and BOARD_OF_DIRECTORS are elected this way\n" +
            "Only the last transaction given for each account counts, unless you have updated your vote,\n" +
            "then after four years it will be cancelled.\n" +
            "100,000 votes are needed to approve the law\n" +
            "\n" +
            "______\n" +
            "\n" +
            "````\n" +
            "//code is in class class CurrentLawVotes method: votesLaw\n" +
            "public double votesLaw(Map<String, Account> balances,\n" +
            "      Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {\n" +
            "         double yes = 0.0;\n" +
            "         double no = 0.0;\n" +
            "       \n" +
            "              \n" +
            "        for (String s : YES) {\n" +
            "\n" +
            "             int count = 1;\n" +
            "           count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;\n" +
            "           yes += balances.get(s).getDigitalStockBalance() / count;\n" +
            "\n" +
            "        }\n" +
            "        \n" +
            "         for (String s : NO) {\n" +
            "            int count = 1;\n" +
            "             count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;\n" +
            "            no += balances.get(s).getDigitalStockBalance() / count);\n" +
            "\n" +
            "         }\n" +
            "\n" +
            "\n" +
            "         return yes - no;\n" +
            "    }\n" +
            "\n" +
            "````\n" +
            "\n" +
            "[back to home](../documentationEng/documentationEng.md)";

    String ONE_VOTE_3 = "# ONE_VOTE (One Voice)\n" +
            "\n" +
            "When these positions are voted count as one score = one vote\n" +
            "(CORPORATE_COUNCIL_OF_REFEREES-Council of Corporate Judges,\n" +
            "BOARD_OF_DIRECTORS-Board of Directors, GENERAL_EXECUTIVE_DIRECTOR-General Executive Director,\n" +
            "HIGH_JUDGE - Supreme Judge and Board of Shareholders).\n" +
            "Each score that starts with LIBER counts all votes FOR (VoteEnum.YES) and AGAINST (VoteEnum.NO) for it\n" +
            "further deducted from FOR - AGAINST = if the balances are above the threshold, then it becomes the current law. But if a position is elected,\n" +
            "then after that it is sorted from largest to smallest and the largest number that is described for this position is selected.\n" +
            "Recalculation of votes occurs every block.\n" +
            "\n" +
            "After voting, the vote can only be changed to the opposite one.\n" +
            "There is no limit on the number of times you can change your vote. Only those votes that are given by accounts are taken into account\n" +
            "in office, for example, if the account ceases to be on the Board of Directors, his vote as\n" +
            "The Board of Directors does not, and will not, count in voting. All votes are valid until the bills\n" +
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
            "\n" +
            "[back to home](../documentationEng/documentationEng.md)";

    String VOTE_FRACTION_4 = "#VOTE_FRACTION\n" +
            "This voting system is used only for factions.\n" +
            "First, 100 factions are selected that have become legitimate.\n" +
            "Then all the votes given to 100 selected factions are summed up.\n" +
            "After that, the share of each fraction from the total amount is determined.\n" +
            "votes cast for this faction.\n" +
            "The number of votes of each faction is equal to its percentage shares.\n" +
            "Thus, if a faction has 23% of the votes of all votes, out of\n" +
            "100 factions, then her vote is equal to 23%.\n" +
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

    String Penalty_mechanism_5 = "# Penalty mechanism\n" +
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
            "destructive accounts, since the number of votes is equal to the number of shares in the Election of the Board of Directors and\n" +
            "when electing CORPORATE_COUNCIL_OF_REFEREES.\n" +
            "This mechanism only works on digital shares and only if the sender has made a transaction with\n" +
            "VoteEnum.NO.\n" +
            "\n" +
            "[exit to home](../documentationEng/documentationEng.md)";

    String WHO_HAS_THE_RIGHT_TO_CREATE_LAWS_6 ="# WHO_HAS_THE_RIGHT_TO_CREATE_LAWS Who has the right to create laws\n" +
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

    String POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES_7 = "# POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES Judicial Power.\n" +
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

    String HOW_THE_CHIEF_JUDGE_IS_CHOSEN_8 = "# String HOW_THE_CHIEF_JUDGE_IS_CHOSEN HOW HIGH_JUDGE IS CHOSEN.\n" +
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

    String PROPERTY_OF_THE_CORPORATION_9 = "# PROPERTY_OF_THE_CORPORATION PROPERTY OF THE CORPORATION.\n" +
            "All property owned by the International Trade Union Corporation,\n" +
            "cannot be sold without a valid law,\n" +
            "where the sale process will be described and at what price the property will be sold.\n" +
            "The founder's account, and the accounts of other members are not\n" +
            "corporate account, the Board of Directors must create a separate account which\n" +
            "will be budgeted and managed only by members of the current members of the Board of Directors.\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";

    String GENERAL_EXECUTIVE_DIRECTOR_10 = "# GENERAL_EXECUTIVE_DIRECTOR General Executive Director\n" +
            "This Director coordinates the actions of the other senior directors to implement the strategic plan or\n" +
            "the tasks assigned to it by the laws in force.\n" +
            "All powers must be given to him through existing laws.\n" +
            "This is the highest position elected by the Corporation and is essentially the analogue of the prime minister.\n" +
            "\n" +
            "## How the CEO is elected\n" +
            "This director is elected by the Legislature\n" +
            "1. The Board of Directors must give more than 10 or more votes using the [ONE_VOTE](../charterEng/ONE_VOTE.md) method\n" +
            "2. The Board of Shareholders must give more than 10 or more votes using the [ONE_VOTE](../charterEng/ONE_VOTE.md) method\n" +
            "3. Fractions must give 10% or more votes using the [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md) method\n" +
            "4. Network participants must give more than one vote using the [VOTE_STOCK](../charterEng/VOTE_STOCK.md) method\n" +
            "5. Next comes the sorting from highest to lowest received votes from the Board of Directors and\n" +
            "6. One account with the most votes from the Board of Directors is selected\n" +
            "\n" +
            "````\n" +
            "  //positions elected only by all participants\n" +
            "         List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()\n" +
            "                 .filter(t -> directors.isElectedByBoardOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                 .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                 && t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS\n" +
            "                 && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            "                 .collect(Collectors.toList());\n" +
            "                \n" +
            "                  //group by list\n" +
            "         Map<String, List<CurrentLawVotesEndBalance>> group = electedByBoardOfDirectors.stream()\n" +
            "                 .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));\n" +
            "\n" +
            "         Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();\n" +
            "\n" +
            "         // leave the amount that is described in this post\n" +
            "         for (Map.Entry<String, List<CurrentLawVotesEndBalance>> stringListEntry : group.entrySet()) {\n" +
            "             List<CurrentLawVotesEndBalance> temporary = stringListEntry.getValue();\n" +
            "             temporary = temporary.stream()\n" +
            "                     .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors))\n" +
            "                     .limit(directors.getDirector(stringListEntry.getKey()).getCount())\n" +
            "                     .collect(Collectors.toList());\n" +
            "             original_group.put(directors.getDirector(stringListEntry.getKey()), temporary);\n" +
            "         }\n" +
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
            "                 int-highhtJudgesVotes = 0;\n" +
            "                 int founderVote = 0;\n" +
            "                 double fraction = 0;\n" +
            "\n" +
            "                 //count special votes for laws\n" +
            "                 vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);\n" +
            "                 List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());\n" +
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
            "             if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){\n" +
            "                 if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                     houseOfRepresentativies.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                 }\n" +
            "\n" +
            "             }\n" +
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
            "                 if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                 && currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                 && currentLawVotesEndBalance.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS\n" +
            "                 && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE){\n" +
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
            "````\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";

    String EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE_11 = "# EXPLANATION WHY MONEY DEMURAGE IS USED HERE\n" +
            "How does the negative rate work here? Every six months we burn 0.1% of digital dollars from all accounts\n" +
            "and 0.2% of digital shares, which allows us to keep the money supply unchanged, preventing it from growing,\n" +
            "for the new money created and the money destroyed come into parity.\n" +
            "The negative rate is now applied in many countries, this measure stimulates money holders when the price is excessively high,\n" +
            "saturate the market with money.\n" +
            "The amount of money mined for each block is 400 digital dollars and 400 digital shares,\n" +
            "also 2% of each mining reward to the founder, which is 4 digital dollars and 4 digital Shares for each block mining.\n" +
            "Here it is used as the Theories of Silvio Gesell, as well as the school of monetarism in a modified form.\n" +
            "\n" +
            "With Silvio Gezel, the negative rate was 1% per month, which would just kill the economy,\n" +
            "under monetarism, the growth of the money supply had to be proportional to the growth of GDP, but since in\n" +
            "this system fails to calculate the real GDP growth, I set a fixed growth, also if the monetary growth\n" +
            "will equal GDP, there is a high probability of Hyperinflation, since GDP does not always reflect real economic growth.\n" +
            "Money must be solid so that a business can predict its long-term investments and from monetarism, only the part that\n" +
            "the money supply should grow linearly, but in general there is a mix of different economic schools, including the Austrian School of Economics.\n" +
            "\n" +
            "With a negative rate of 0.1% every six months for digital dollars and 0.2% for digital stocks, we avoid the consequences of a severe economic crisis for this currency.\n" +
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
            "Which, in turn, causes even more fear among money holders to invest, and this process continues until the moment when\n" +
            "until the value of money starts to decline due to the fact that the total number of production chains has decreased and goods have also decreased.\n" +
            "\"\n" +
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
            "408 (8 - founder's reward, 400 - earner's reward)\n" +
            "digital dollars and shares per block, about 576 blocks per day. A negative rate adjusts the value of coins every six months.\n" +
            "It is also forbidden to use fractional reserve banking for these coins, as their number grows linearly, and\n" +
            "will not be able to cover the debts incurred due to fractional reserve banking, due to lack of\n" +
            "cash, since with fractional reserve banking, the increase in debt will be much higher than this protocol will create money.\n" +
            "\n" +
            "Also, if you increase the money supply by changing the settings, and making the money supply increase much higher, it can cause hyperinflation or\n" +
            "even galloping inflation. If it is necessary to increase the growth of the money supply, this should only happen through amendments,\n" +
            "keeping the founder's remuneration percentage at two percent. And mining per block should not increase more than 5% for\n" +
            "ten years, each subsequent increase that may be made must take place at least tenyears through amendments,\n" +
            "and no more than 5% per block from the reward of the last block. (Example: if we change\n" +
            "through the amendments, then the extraction should not be higher than 420 coins, but each following will be no more than five percent of the last.\n" +
            "Thus, the next increase made through the amendments will be 440.10 coins. But this amendment will be introduced only in ten\n" +
            "years after the first production adjustment)\n" +
            "\n" +
            "With a lack of money supply, if the number of mined coins has not been changed through an amendment, you can add a few\n" +
            "additional zeros after the decimal point, so it will simply increase the value of the coins, without increasing the total money supply.\n" +
            "\n" +
            "Negative rates should not be higher than 0.5% per annum and lower than 0.2% per annum. Negative rates can only be changed through amendments.\n" +
            "\n" +
            "[Exit to home](../documentationEng/documentationEng.md)";

    String FREEDOM_OF_SPEECH_12 = "FREEDOM_OF_SPEECH The right to free speech\n" +
            "No body of this corporation or entity shall prohibit free practice any religion; or restrict freedom of speech, conscience or the press or the right of people to peacefully assemble or associate with one another, or not associate with one another, and apply to the management of the Corporation of the International Trade Union and to this corporation with a petition for satisfaction of complaints; + or violate the right to the fruits of one's labor or the right to a peaceful life of their choice. Freedoms of speech and conscience include the freedom to contribute to political campaigns or nominations for corporate office and shall be construed as extending equally to any means of communication.";

    String RIGHTS_13 = "RIGHTS Natural Rights\n" +
            "All members of the network must respect the Natural Human Rights and not violate them. \"The presumption of innocence must also be respected and every member of the network must have the right to a fair and independent trial. Each participant has the right to a lawyer or to be his own lawyer.\n" +
            "\n" +
            "The International Trade Union Corporation shall not regulate the cost of goods and services of network members that sell through this platform. Also, the Corporation should not ban individual brands on its site, but may prohibit the sale of entire groups of goods that fall within the characteristics described by applicable laws, if this prohibition does not violate Natural Human Rights. As a source of rights, you can take as a precedent Countries recognized as democratic countries.\n" +
            "\n" +
            "A detailed list is at the United Nations (UN)\n" +
            "\n" +
            "The right to live Right to liberty and security of person Right to privacy The right to determine and indicate one's nationality The right to use one's native language The right to freedom of movement and choice of place of stay and residence Right to freedom of conscience\n" +
            "\n" +
            "Freedom of thought and speech Freedom of Information The right to form public associations The right to hold public events The right to participate in the management of the affairs of the Corporation of the International Trade Union The right to appeal to the bodies of the Corporation of the International Trade Union and local governments.";
    String LEGISLATURE_14 = "#LEGISLATURE.\n" +
            "Power consists of 4 groups in this system.\n" +
            "1. Board of Shareholders\n" +
            "2. Board of Directors\n" +
            "3. Independent members of the network.\n" +
            "\n" +
            "All participants must participate in the vote for the law adopted by the system to be valid\n" +
            "(The only exception is the Board of Shareholders, since the Board of Shareholders participates\n" +
            "only in the approval of amendments to the Charter).\n" +
            "For all votes, only votes cast in the last four years count.\n" +
            "All members may hold multiple positions from different groups, but may not\n" +
            "hold more than one position in the same category.\n" +
            "Example: One account can be both ***Member of the Network*** and ***Member of the Board of Directors***\n" +
            "and ***Member of the Board of Shareholders***, but one account cannot hold multiple seats on the Board of Directors\n" +
            "or in the Board of Shareholders.\n" +
            "\n" +
            "It is this part of the vote that is taken into account when electing the Board of Directors and Fractions.\n" +
            "![stock_vote](../screenshots/stock_vote.png)\n" +
            "## Board of Shareholders\n" +
            "The Board of Shareholders is automatically appointed by the system.\n" +
            "The Board of Shareholders consists of 1500 accounts with the largest number of shares,\n" +
            "but only those accounts are selected that have either been mining in the last year,\n" +
            "either sent digital dollars or digital shares, or participated in voting.\n" +
            "A member of one Board of Shareholders has one vote. One score equals one vote.\n" +
            "The voting system described in [ONE_VOTE](../charter/ONE_VOTE.md) is used\n" +
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
            "## Board of Directors\n" +
            "The Board of Directors is elected by the members of the network.\n" +
            "The Board of Directors consists of 601 accounts that received the most votes\n" +
            "according to the system described in [VOTE_STOCK](../charter/VOTE_STOCK.md). Each score is equal to one vote, described\n" +
            "in [ONE_VOTE](../charter/ONE_VOTE.md).\n" +
            "\n" +
            "````\n" +
            "  //minimum value for the number of positive votes for the law to be valid,\n" +
            "         //positions elected by shares of the board of directors\n" +
            "         List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()\n" +
            "                 .filter(t -> directors.isElectedByStocks(t.getPackageName()))\n" +
            "                 .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))\n" +
            "                 .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            "                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                 .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())\n" +
            "                 .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "### How to apply for a board position\n" +
            "First you need to go to the tab in ***apply for a position*** Select BOARD_OF_DIRECTORS\n" +
            "and fill in all the lines with the required data.\n" +
            "![apply_board_of_directors](../screenshots/apply_board_or_directors.png)\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "## Independent Network Members.\n" +
            "All network members who have shares and are not included in the first three categories listed above,\n" +
            "are ***independent members of the network***. The votes of each such participant are equal to\n" +
            "to the number of shares at the moment and is described in detail in [VOTE_STOCK](../charter/VOTE_STOCK.md).\n" +
            "\n" +
            "\n" +
            "[Exit to home](../documentation/documentationEng.md)";




}
