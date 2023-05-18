package International_Trade_Union.originalCorporateCharter;

public interface OriginalCHARTER_ENG {
    //Added
    String POWERS_OF_THE_BOARD_OF_DIRECTORS = " The Board of Directors can approve bills that have applied for positions on the Directors List.\n" +
            "Also Laws package names start with ADD_DIRECTOR, are packages that contain a list of new directors.\n" +
            "This list should drive new product lines. \n" +
            "\n" +
            "These Laws can only be approved by the Board of Directors, and from there they will take a list of laws, where \n" +
            "every line that starts with ADD_DIRECTOR will be added to the Directors list as a new \n" +
            "positions. List of DIRECTORS that can be applied. \n" +
            "\n" +
            "A package that starts with BUDGET is a budget and can only be approved by the Board of Directors\n" +
            "There can only be one effective budget.\n" +
            "\n" +
            "The board of directors also approves the strategic plan STRATEGIC_PLAN. Only \n can be valid" +
            "one strategic plan.\n" +
            "\n" +
            "The board of directors is also involved in the approval of laws (the rules by which \n must operate" +
            " all members of the corporation), and also participates in the approval of the implementation of amendments to the charter AMENDMENT_TO_THE_CHARTER.\n" +
            "\n" +
            "The Board has the right to set and collect commissions from sales within the Corporation's platforms\n" +
            " of the International Trade Union, provided that this commission will not exceed twenty percent (20%).\n" +
            "All fees must be allocated to expenses that are set in the budget.\n" +
            " Also, a source of income is the sale of their goods and services, for this there are Office Directors who are elected \n" +
            "the board of directors and they must sell the products of the Corporation of the International Trade Union." ;

    String HOW_LAWS_ARE_CHOSEN = "No law is retroactive. No law must violate the current bylaws or contradict \n" +
            "other applicable laws. If there is a conflict between several laws from the same package of laws, \n" +
            "the current one is the one that is higher in the index. Example: a package for the sale of alcohol \n" +
            "the law at index 3 contradicts the law at index 17, in this case the law at index three will be valid,\n" +
            "because he's more senior. \n" +
            "\n" +
            "If laws conflict from different packages, then the valid package is the one with the most votes\n" +
            "from the Board of Shareholders, if there is parity, then the one that received more votes from the Board of Directors, if here \n" +
            "there is parity, then this dispute should be decided by the Supreme Justice, if he also did not determine which of the two packages\n" +
            "where laws conflict with each other, laws of one of the packages are more valid, then \n takes precedence" +
            "the one that took effect earlier, the countdown is determined precisely from the last moment of entry into force.\n" +
            "\n" +
            "All ordinary laws are valid if voted in this way ONE_VOTE by the Board of Shareholders, the Board of Directors and possibly \n" +
            "High Justice. For a law to be valid, it must receive equal to or more than 100 of the remaining votes of the Board of Shareholders,\n" +
            "equal to or greater than 15 remaining votes of the Board of Directors and One vote of the Chief Justice, but if the Chief Justice did not vote or voted against\n" +
            "then it is possible to override the veto of the supreme judge by obtaining 200 or more of the remainder of the votes of the Board of Shareholders and 30 or more of the remainder of the votes of the Board of Directors.\n" +
            "\n" +
            "or if the law received more than 100k remainder votes as described by VOTE_STOCK is also valid and will be\n" +
            "act while retaining more than one hundred thousand votes.\n" +
            "A law is valid as long as it matches the votes as described above. Every time someone loses their office \n" +
            "also loses all his votes for all the laws he voted, but not lost votes received through shares.\n";

    //Added
    String HOW_THE_BOARD_OF_DIRECTORS_IS_ELECTED = "How the Board of Directors is elected." +
            "" +
            " The board of directors consists of 301 BOARD_OF_DIRECTORS accounts. " +
            " Each member of the network can apply for the position of the board of directors by creating a package of law, where " +
            " the package name BOARD_OF_DIRECTORS and the sender's account must match the account specified " +
            " in the first line of the law that is contained in the list of this package. " +
            " The 301 account with the most remaining votes wins the position. " +
            " The cost of filing to create a law (position) is worth five digital dollars (5) as a reward to the earner. " +
            " The voting process is described in VOTE_STOCK ";

    //Added
    String POWERS_OF_THE_BOARD_OF_SHAREHOLDERS = " OriginalCHARTER.POWERS_OF_THE_BOARD_OF_SHAREHOLDERS: Board powers.\n" +
            "The Board of Shareholders Participates in the approval of the Laws (network rules that all members of this Corporation must comply with).\n" +
            "Also, the Board of Shareholders participates in the approval of amendments to the charter of the International Trade Union Corporation AMENDMENT_TO_THE_CHARTER.\n" +
            "The Board of Shareholders can also participate in voting when electing candidates CORPORATE_COUNCIL_OF_REFEREES and BOARD_OF_DIRECTORS using\n" +
            "these rules are for voting for VOTE_STOCK candidates. ";

    //added
    String HOW_SHAREHOLDERS_BOARD_IS_ELECTED = " OriginalCHARTER.HOW_SHAREHOLDERS_BOARD_IS_ELECTED: HOW SHAREHOLDERS ARE ELECTED. \n" +
            "The Board of Shareholders consists of one thousand five hundred accounts (1500) with the largest number of shares,\n" +
            "but only those accounts whose activity has not been more than a year old are taken into account (Activity is mining or sending money, creating a law, sending fines,\n" +
            "Applying for a position and creating a new position).\n" +
            "formula: current year is one year, and if the account was active in this range, it is taken into account.\n" +
            "All accounts are sorted in descending order of the number of digital shares, and the 1500 accounts with the most shares are selected. Recalculation occurs every block.\n" +
            "An example of a section of code how the Board of Shareholders is elected:" +
            "" +
            "//определение совета акционеров\n" +
            "    public static List<Account> findBoardOfShareholders(Map<String, Account> balances, List<Block> blocks, int limit) {\n" +
            "        List<Block> minersHaveMoreStock = null;\n" +
            "        if (blocks.size() > limit) {\n" +
            "            minersHaveMoreStock = blocks.subList(blocks.size() - limit, blocks.size());\n" +
            "        } else {\n" +
            "            minersHaveMoreStock = blocks;\n" +
            "        }\n" +
            "        List<Account> boardAccounts = minersHaveMoreStock.stream().map(\n" +
            "                        t -> new Account(t.getMinerAddress(), 0, 0))\n" +
            "                .collect(Collectors.toList());\n" +
            "\n" +
            "        for (Block block : minersHaveMoreStock) {\n" +
            "            for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {\n" +
            "                boardAccounts.add(new Account(dtoTransaction.getSender(), 0, 0));\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "\n" +
            "\n" +
            "        CompareObject compareObject = new CompareObject();\n" +
            "\n" +
            "        List<Account> boardOfShareholders = balances.entrySet().stream()\n" +
            "                .filter(t -> boardAccounts.contains(t.getValue()))\n" +
            "                .map(t -> t.getValue()).collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            "        boardOfShareholders = boardOfShareholders\n" +
            "                .stream()\n" +
            "                .filter(t -> !t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))\n" +
            "                .filter(t -> t.getDigitalStockBalance() > 0)\n" +
            "                .sorted(Comparator.comparing(Account::getDigitalStockBalance).reversed())\n" +
            "                .collect(Collectors.toList());\n" +
            "\n" +
            "        boardOfShareholders = boardOfShareholders\n" +
            "                .stream()\n" +
            "                .limit(Seting.BOARD_OF_SHAREHOLDERS)\n" +
            "                .collect(Collectors.toList());\n" +
            "\n" +
            "        return boardOfShareholders;\n" +
            "    }";

    //Added
    String VOTE_STOCK = "How shares are voted. All shares an account owns count for the same number of votes. every time someone makes a transaction to an account that is a package address that starts with LIBER, they vote for that package. Only those are counted votes less than four years old If the transaction was made VoteEnum.YES then this account receives votes for according to the formula yesV = number of votes equal to the number of shares of the sender yesN = how many laws this account voted for with VoteEnum.YES resultYES = yesV / yesN).Example: an account voted for three accounts that start with LIBER, there are 100 shares in the account, so 100 votes. 100 / 3 = 33.3, so each account will receive 33.3 votes.\n" +
            "\n" +
            "if the transaction was made with VoteEnum.NO then the same formula is used, but now all accounts for which he voted against are taken into account example the same account voted for two against, he has the same hundred shares. resultNO = noV / noN = 50 = 50 means each account for which he voted against will receive 50 votes against.next, each account counts and sums up all the votes given to it FOR (VoteEnum.YES) and AGAINST (VoteEnum.NO).Then use this formula remainder = resultYES - resultNO.first these positions, all accounts that received more than or equal to one vote remainder (0) remainder >= 1. Then all accounts are sorted in descending order by remainder and from there the number of accounts for these positions is selected, as specified in this position.For the Board of Directors, this is 301 the account with the most balance.\n" +
            "\n" +
            "At any time, you can change your vote, but only to the opposite one, which means if you voted for a YES candidate, you can only change it to NO and vice versa. The number of times you can change your vote is not limited. With each block, the votes are recalculated, if you lose your shares, your candidates also lose their votes This measure is specifically implemented so that elected positions have a stake in you to prosper This way only CORPORATE_COUNCIL_OF_REFEREES and BOARD_OF_DIRECTORS are elected Only the last transaction given for each account is counted if you did not update your vote, then after four years it will be annulled. 100,000 votes are needed to approve the Law";

    //Дописан
    String CODE_VOTE_STOCK = " class CurrentLawVotes method: votesLaw " +
            " public double votesLaw(Map<String, Account> balances,\n" +
            "                           Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {\n" +
            "        double yes = 0.0;\n" +
            "        double no = 0.0;\n" +
            "\n" +
            "        //\n" +
            "        for (String s : YES) {\n" +
            "\n" +
            "            int count = 1;\n" +
            "            count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;\n" +
            "            yes += balances.get(s).getDigitalStockBalance() /count;" +
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
            "    } ";

    String POWERS_OF_THE_CABINET_OF_DIRECTORS = "Powers of the Cabinet of Directors. \n" +
            "Cabinet directors are senior directors who are directors of their divisions. \n" +
            "The powers of each director must be described by the laws in force. But each director must govern\n" +
            "only his own division (example: MEDIA_DIRECTOR manages the media and its powers\n" +
            "applies to the media only. Coordination of all directors should be led by the GENERAL_EXECUTIVE_DIRECTOR.\n" +
            "\n" +
            "Board of Directors, CORPORATE_COUNCIL_OF_REFEREES, HIGH_JUDGE, Board of Shareholders and GENERAL_EXECUTIVE_DIRECTOR can be either \n" +
            "individuals and legal entities, but one account will be counted as one vote.";

    String HOW_CABINET_DIRECTORS_ARE_CHOSEN = "All Cabinet Directors are the top directors who manage their divisions\n" +
            "elected only by the Board of Directors. \n" +
            "Each member of the network can apply for the position of supreme director by creating a law with a package name that matches the allowed \n" +
            "positions, where the address of the sender of this transaction must match the first line from the list of laws in this package. \n" +
            "The cost of the law is five digital dollars as a reward to the earner.\n" +
            "The account with the most remaining votes wins the position.\n" +
            "The voting mechanism is described by ONE_VOTE. \n" +
            "To be elected by the board of directors, the position must receive at least 15 votes (the remainder of the votes). " +
            "" +
            " An example of a piece of code how positions are elected class LawsController: method currentLaw: " +
            "" +
            " //positions created by the board of directors\n" +
            " List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()\n" +
            " .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            " .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)\n" +
            " .collect(Collectors.toList());\n" +
            " //adding positions created by the board of directors\n" +
            " for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {\n" +
            "directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());\n" +
            "}\n" +
            "\n" +
            " //positions elected only by the board of directors\n" +
            " List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()\n" +
            " .filter(t -> directors.isElectedByBoardOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            " .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            " .collect(Collectors.toList());";

    String ONE_VOTE = "ONE VOTE" +
            "When these positions are voted, counts as one count = one vote \n" +
            "(CORPORATE_COUNCIL_OF_REFEREES-Council of Corporate Judges,\n" +
            "BOARD_OF_DIRECTORS-Board of Directors, GENERAL_EXECUTIVE_DIRECTOR-General Executive Director,\n" +
            "HIGH_JUDGE - Supreme Judge and Board of Shareholders.\n" +
            "Every score that starts with LIBER counts all votes FOR (VoteEnum.YES) and AGAINST (VoteEnum.NO) for it\n" +
            "further deducted from FOR - AGAINST = if the balances are above the threshold, then it becomes the current law. But if the office is elected,\n" +
            "then it sorts from largest to smallest and selects the largest number that is described for this position.\n" +
            "Votes are recalculated every block.\n" +
            "\n" +
            "After voting, the vote can only be changed to the opposite one.\n" +
            "There is no limit on the number of times you can change your vote. Only those votes given by accounts \n" +
            "in their position, for example, if the account ceases to be in the Board of Directors, his vote is as \n" +
            "Board of Directors does not, and will not, count in voting. All votes are valid as long as accounts \n" +
            "those who voted are in their positions. Only those votes from which no more than \n have passed are also taken into account" +
            "four years, but each participant may at any time renew his vote. ";

    String CODE_VOTE_ONE = " CODE class CurrentLawVotes: method voteGovernment " +
            "public int voteGovernment(\n" +
            "            Map<String, Account> balances,\n" +
            "            List<String> governments) {\n" +
            "       int yes = 0;\n" +
            "        int no = 0;\n" +
            "\n" +
            "       List<String> addressGovernment = governments;\n" +
            "      for (String s : YES) {\n" +
            "            if (addressGovernment.contains(s)) {\n" +
            "                yes += Seting.VOTE_GOVERNMENT;\n" +
            "           }\n" +
            "\n" +
            "        }\n" +
            "        for (String s : NO) {\n" +
            "          if (addressGovernment.contains(s)) {\n" +
            "                no += Seting.VOTE_GOVERNMENT;\n" +
            "           }\n" +
            "        }\n" +
            "       return yes - no;\n" +
            "   }  ";

    String MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES = " MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES"+
             "Every time one account sends a digital share to another account but uses VoteEnum.NO, the account \n" +
                     "the recipient's digital shares are reduced by the amount sent by the share sender.\n" +
                     "Example account A sent to account B 100 digital shares with VoteEnum.NO, then account A and account B both lose 100 \n" +
                     "digital shares. This measure is needed so that there is a mechanism to remove the Board of Shareholders from the post and also allows you to reduce votes \n" +
                     "destructive accounts, since the number of votes is equal to the number of shares in the Election of the Board of Directors and \n" +
                     "when elected CORPORATE_COUNCIL_OF_REFEREES. \n" +
                     "This mechanism only works on digital shares and only if the sender sent with the \n sign" +
                     "VoteEnum.NO.";

    //Added
    String POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES = " POWERS OF THE CORPORATE COUNCIL OF JUDGES. \n" +
            "Approves the Chief Justice.\n" +
            "Participates in the voting on the implementation of amendments. \n" +
            "\n" +
            "The judicial power of the International Trade Union Corporation is\n" +
            "one Supreme Court and such inferior courts as Corporation International\n" +
            "The Merchant Alliance may issue and establish from time to time.\n" +
            "Judges of both superior and inferior courts hold their positions, with good behavior and \n" +
            "they receive remuneration for their services in a timely manner.\n" +
            "Remuneration must be given from the budget established by law.\n" +
            "Judicial power extends to all matters of law and justice,\n" +
            "including those initiated by members to challenge misappropriation of funds,\n" +
            "arising in accordance with these Articles, the laws of the International Trade Union Corporation and treaties,\n" +
            "prisoners or who will be imprisoned according to their authority.\n" +
            "Controversy, \n" +
            "in which the International Trade Union will be party to a dispute between two or more members of the network.\n" +
            "No trial shall be secret, but justice shall be administered openly and free of charge, fully and without delay,\n" +
            "and every person shall have legal protection against injury to life, liberty, or property.\n" +
            "Supreme Court CORPORATE_COUNCIL_OF_REFEREES and Chief Justice HIGH_JUDGE.";

    //Added
    String HOW_THE_CORPORATE_BOARD_OF_JUDGES_IS_ELECTED = " HOW THE CORPORATE BOARD OF JUDGES IS ELECTED.\n" +
            "\n" +
            "CORPORATE_COUNCIL_OF_REFEREES consists of 55 accounts.\n" +
            "Each network member can apply for CORPORATE_COUNCIL_OF_REFEREES,\n" +
            "by creating a law package where the package name CORPORATE_COUNCIL_OF_REFEREES and the sender's account must match\n" +
            "the account that is listed in the first line of the law that is listed in this package\n" +
            "The 55th score with the most remaining votes wins the position.\n" +
            "\n" +
            "The cost of filing for the creation of a law (position) is worth five digital dollars (5) as a reward to the earner.\n" +
            "The voting process is described in VOTE_STOCK. " +
            "" +
            " Code snippet example: class LawsController: method currentLaw: " +
            " //minimum number of positive votes for the law to be valid,\n" +
            " //positions elected by shares CORPORATE_COUNCIL_OF_REFEREES\n" +
            " List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()\n" +
            " .filter(t -> directors.isElectedByStocks(t.getPackageName()))\n" +
            " .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))\n" +
            " .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            " .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())\n" +
            " .collect(Collectors.toList());\n";

    String HOW_THE_CHIEF_JUDGE_IS_CHOSEN = " HOW HIGH_JUDGE IS CHOSEN.\n" +
            "The Chief Justice is elected by CORPORATE_COUNCIL_OF_REFEREES. \n" +
            "Each member of the network can apply for the position of Chief Justice by creating a law with a package name that matches the allowed \n" +
            "position, where the address of the sender of this transaction must match the first line from the list of laws in this package. \n" +
            "The cost of the law is five digital dollars as a reward to the earner.\n" +
            "The account with the most remaining votes wins the position.\n" +
            "The voting mechanism is described by ONE_VOTE. \n" +
            "\n" +
            "Code example as stated by the Chief Justice. class LawsController: method currentLaw. Code snippet " +
            " //positions elected by the board of corporate chief judges\n" +
            " List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()\n" +
            " .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))\n" +
            " .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList()); ";

    //Added
    String POWERS_OF_THE_CHIEF_JUDGE = " POWERS OF THE CHIP JUDGE.\n" +
            "The Chief Justice participates in the approval of laws, as well as \n" +
            "can participate in resolving disputes within network members, just like CORPORATE_COUNCIL_OF_REFEREES,\n" +
            "but his vote is higher than that of CORPORATE_COUNCIL_OF_REFEREES. ";


    //Added
    String HOW_IS_THE_PROCESS_OF_AMENDING_THE_CHARTER = " HOW DOES THE PROCESS FOR AMENDING THE CHARTER " +
            " To amend, you need to create a law with the package name AMENDMENT_TO_THE_CHARTER, " +
            "then this law must be voted on by the method described in VOTE_ONE" +
            " Board of Shareholders and the balance of votes must be equal to or higher than 300 members, " +
            " the Board of Directors must also vote and the balance of votes must be 60 or more, " +
            " should also vote corporate chief judges (CORPORATE_COUNCIL_OF_REFEREES) and " +
            " The remainder of the votes must be equal to or greater than 5. " +
            "But the amendments should not concern the way in which the rules of existing laws are established, and also" +
            " election of the Board of Directors, Board of Shareholders, General Executive Director, " +
            " Council of Corporate Judges and Chief Justice. Amendments may change the code if the rules are preserved " +
            "election of current positions (including voting rules), laws and money mining (mining digital dollars and digital stocks)," +
            "No amendment should give any of the above positions more power." +
            "Also, the amendments must not infringe on Natural Human Rights." +
            " " +
            " Code example. class LawsController: method currentLaw: code section that approves current amendments" +
            " //introduction of amendments to the charter\n" +
            " List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()\n" +
            " .filter(t -> !directors.contains(t.getPackageName()))\n" +
            " .filter(t->Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            " .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            " .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT)\n" +
            " .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT)\n" +
            " .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed()).collect(Collectors.toList());\n ";

    //Added
    String HOW_THE_BUDGET_IS_APPROVED = " HOW IS THE BUDGET APPROVED. " +
            "There can be only one effective budget. Only the Board of Directors approves the budget.\n" +
            "To approve the budget, you need to get 15 or more votes by the method described in VOTE_ONE.\n" +
            "The process itself goes like this: \n" +
            "1. First, all packages of laws where the package name matches BUDGET are selected. \n" +
            "2. Next, all packages that received 15 or more votes are selected. \n" +
            "3. Next, all these packages are sorted in descending order, with the most votes. \n" +
            "4. Then the very first one with the most votes is selected." +
            "" +
            " Budget approving code example. class LawsController: method: currentLaw. " +
            " //the budget is approved only by the board of directors.\n" +
            " List<CurrentLawVotesEndBalance>budjet = current.stream()\n" +
            " .filter(t-> !directors.contains(t.getPackageName()))\n" +
            " .filter(t->Seting.BUDGET.equals(t.getPackageName()))\n" +
            " .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            " .filter(t-> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            ".limit(1)\n" +
            " .collect(Collectors.toList());";

    //Added
    String HOW_IS_THE_STRATEGIC = "The strategic plan is approved by the Board of Directors, the strategic plan may be valid\n" +
            "only in a single copy. For the Strategic Plan to be valid, the balance of votes \n" +
            "Board of Directors 15 or more. Voting method VOTE_ONE. \n" +
            "\n" +
            "The Board of Directors may cancel the Strategic Plan at any time. The Strategic Plan is in effect \n" +
            "as long as the number of votes is 15 or more. The Strategic Plan may include a general direction \n" +
            "Corporations and what needs to be implemented.\n" +
            "\n" +
            "Section of code that shows how the Strategic Plan is being approved." +
            " class LawsController: method currentLaw: " +
            "\n" +
            " //the plan is approved only by the chamber Board of Directors\n" +
            " List<CurrentLawVotesEndBalance> planFourYears = current.stream()\n" +
            " .filter(t->!directors.contains(t.getPackageName()))\n" +
            " .filter(t->Seting.STRATEGIC_PLAN.equals(t.getPackageName()))\n" +
            " .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            " .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            ".limit(1)\n" +
            " .collect(Collectors.toList());";

    String HOW_NEW_POSITIONS_ARE_ADDED = "Only top management, subordinates of each leader are added this way \n" +
            "they are hired without the use of the blockchain, they can be hired by the director himself, or otherwise as \n" +
            "described by applicable laws. Top management is added to the class Directors list. \n" +
            "All newly added positions are in effect as long as their laws that create those positions \n" +
            "work.\n" +
            "\n" +
            "Only the Board of Directors can add new positions.\n" +
            "Each package of laws that starts with ADD_DIRECTOR is defined as adding a position.\n" +
            "The list of laws that are inside this package are positions if the name is in \n" +
            "line starts with ADD_DIRECTOR. \n" +
            "\n" +
            "Example: package name ADD_DIRECTOR_PACKAGE\n" +
            "name of the first third and fourth line ADD_DIRECTOR_FIRST ADD_DIRECTOR_THIRD \n" +
            "ADD_DIRECTOR_FOUR will create three positions for this position. \n" +
            "\n" +
            "But if the line starts with ADD_DIRECTOR, then the job title must be large \n" +
            "letters and underscores, also there should be only one position per line \n" +
            "no more words. \n" +
            "\n" +
            "Those lines where there is no position added (ADD_DIRECTOR) are used to describe \n" +
            "permissions for added positions.\n" +
            "\n" +
            "In order for new positions to be added to the list, the Board of Directors must vote using the \n method" +
            "VOTE_ONE 15 or more votes. \n" +
            "Once new positions are created, each member of the network will be able to apply for these positions." +
            " class LawsController: method currentLaw: " +
            "" +
            "\n" +
            " //adds laws that create new directorships\n" +
            " List<CurrentLawVotesEndBalance> addDirectors = current.stream()\n" +
            " .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            " .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)\n" +
            " .collect(Collectors.toList());";

    //Added
    String PROPERTY_OF_THE_CORPORATION = " PROPERTY OF THE CORPORATION. " +
            "All property owned by the International Trade Union Corporation,\n" +
            "cannot be sold without a valid law,\n" +
            "where the sale process will be described and at what price the property will be sold.\n" +
            "The founder's account and the other members' accounts are not \n" +
            "corporate account, the Board of Directors should create a separate account which\n" +
            "will be budgeted and managed only by members of the current members of the Board of Directors.";

    //Added
    String INTERNET_STORE_DIRECTOR = "Web Store Director\n" +
            "This director is engaged in the development, promotion and management of an online store in which they must \n" +
            "sell goods for a digital dollar or digital shares. \n" +
            "Detailed powers must be given either through existing laws or \n" +
            "issued by the CEO or the Board of Directors.\n" +
            "The name of the Store must be determined by either the Board of Directors or the Chief Executive Officer.";

    //Added
    String GENERAL_EXECUTIVE_DIRECTOR = "General Executive Director\n" +
            "This Director coordinates the actions of the other senior directors to implement the strategic plan or \n" +
            "the tasks assigned to him by the laws in force.\n" +
            "All powers must be given to him through existing laws.\n" +
            "This is the highest office elected by the Corporation and is in essence an analogue of the prime minister.";

    //Added
    String DIRECTOR_OF_THE_DIGITAL_EXCHANGE = "Digital Exchange Director\n" +
            "This Director is engaged in the development, promotion and management of the Internet exchange.\n" +
            "Permissions must be granted to him or the laws in force,\n" +
            "or the Board of Directors, or the Chief Executive Officer.";

    //Added
    String DIRECTOR_OF_DIGITAL_BANK = "Digital Bank Director\n" +
            "This Director is in charge of Internet Banking. All powers must be issued \n" +
            "or applicable laws, or the Board of Directors, or the Chief Executive Officer.";

    //Added
    String DIRECTOR_OF_THE_COMMERCIAL_COURT = " Private Commercial Court Director \n" +
            "Must provide guidance to private courts, all powers\n" +
            "must be Issued either by current laws or by the Board of Directors,\n" +
            "or CEO.\n" +
            "\n" +
            "In these courts, judges can be members of the Council of Chief Justices (CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "So is the Chief Justice or lower judges elected by CORPORATE_COUNCIL_OF_REFEREES.";

    //Added
    String MEDIA_DIRECTOR = "Media Director\n" +
            "This Director is in charge of the media, all powers must be issued by the laws in force,\n" +
            "or, the Board of Directors or the CEO.\n" +
            "\n" +
            "This Director is in charge of directly owned media\n" +
            "Corporations of the International Trade Union.";

    //Added
    String DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION =
            " This Director manages the development and implementation of new code in this cryptocurrency, all powers must be issued only through " +
                    " current laws, but may also be issued by either the Board of Directors if the current laws have given such powers to the Board of Directors. " +
                    " Also, no code change should be contrary to the current charter or applicable laws. Also, powers can be issued" +
                    " by the Chief Executive Officer, if such authority is granted to the Chief Executive Officer by applicable law. ";



    //Added
    String EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE = " EXPLANATION WHY MONEY DEMURAGE IS USED HERE\n" +
            "Negative rates are now in use in many countries, a measure that encourages money holders when the price is too high,\n" +
            "saturate the market with money.\n" +
            "The amount of money mined per block is 200 digital dollars and 200 digital shares,\n" +
            "also 2% of each mining reward to the founder, which is 4 digital dollars and 4 digital Shares for each block mined.\n" +
            "Here it is used as the Theories of Silvio Gesell, as well as schools of monetarism in a modified form.\n" +
            "\n" +
            "For Silvio Gezel, the negative rate was 1% per month, which would just kill the economy,\n" +
            "under monetarism, growth in the money supply should have been proportional to GDP growth, but since \n" +
            "this system can't calculate real GDP growth, I set fixed growth, also if monetary growth \n" +
            "will be equal to GDP, there is a high probability of Hyperinflation, since GDP does not always reflect real economic growth. \n" +
            "Money must be solid so that a business can predict its long-term investments and from monetarism, only the part that \n is taken" +
            "the money supply should grow linearly, but in general there is a mix of different economic schools, including the Austrian School of Economics.\n" +
            "\n" +
            "With a negative rate of 0.1% every six months for digital dollars and 0.2% for digital stocks, we avoid the consequences of a severe economic crisis for this currency.\n" +
            "\n" +
            "Such a mechanism creates a price corridor where the lower limit of the value of these digital currencies is the total number of issued digital currencies\n" +
            "dollars and digital stocks, and the upper limit is the real value. As soon as the value gets higher than the real value,\n" +
            "it becomes more profitable for holders to sell digital dollars and digital shares at inflated prices, thereby saturating the market with money\n" +
            "and creating a correction in the market. \n" +
            "\n" +
            "The main source of monetary crises is rapid changes in commodity prices and slow changes in wages.\n" +
            "Example: Let's imagine that the value of the currency has risen sharply by 30%, it becomes more profitable for holders not to invest money, since \n" +
            "revenue from holding currency is higher than paying more expensive employees now, because of that, the money stops \n" +
            "to invest. People don't receive more wages, which leads to the fact that a huge number of goods are not sold,\n" +
            "and this leads to the fact that some manufacturers go bankrupt and lay off a lot of workers, which further reduces wages\n" +
            "pay the rest, as the labor market becomes surplus. \n" +
            "\n" +
            "Which, in turn, makes money holders even more afraid to invest, and this process continues until the moment \n" +
            "until the value of money starts to decline as the total number of production chains has shrunk and commodities have shrunk as well.\n" +
            "\"\n" +
            "Example: Let's imagine that we have inflation and the value of money has fallen by 40% within a month, the cost of goods rises sharply,\n" +
            "but wages have not risen, so many goods will not be bought, resulting in the closure of production chains,\n" +
            "which in turn, due to the surplus of workers in the labor market, reduces wages, which in turn also reduces \n even more" +
            "number of items sold.\n" +
            "The first case of a deflationary spiral occurs due to a sharp reduction in money in the market, the second \n" +
            "The case of stagflation often occurs when there is a sharp excess of money entering the market.\n" +
            "And these two phenomena are two sides of the same coin, in one case we get a deflationary spiral in the other\n" +
            "stagflation.\n" +
            "\n" +
            "In order to avoid such crises, in this cryptocurrency, money grows in the same predictable amount. \n" +
            "204 (4 - founder reward, 200 - earner reward) \n" +
            "digital dollars and shares per block, there are about 576 blocks per day. And the negative rate adjusts the cost of coins every six months. \n" +
            "It is also forbidden to use fractional reserve for these coins, as their number grows linearly, and \n" +
            "will not be able to cover debts incurred due to fractional reserve banking, due to lack due to lack \n" +
            "cash, as with frequentIn personal banking, debt growth will be much higher than this protocol will create money. \n" +
            "\n" +
            "Also, if you increase the money supply by changing the settings, and making the money supply increase much higher, it may cause hyperinflation or \n" +
            "even galloping inflation. If money growth is to be increased, it should only be through adjustments,\n" +
            "keeping the founder's reward percentage at 2%. And mining per block shouldn't increase more than 5% for \n" +
            "ten years, each subsequent increase that may be made must pass at least ten years through amendments,\n" +
            "and no more than 5% per block of the last block reward. (Example: if we changed \n" +
            "through amendments, then the extraction should not exceed 210 coins, but each subsequent one will be no more than five percent of the last. \n" +
            "So the next increase made through the amendments will be 220.5 coins. But this amendment will only be made after ten\n" +
            "years after the first change in production) \n" +
            "\n" +
            "If there is a lack of money supply, if the number of mined coins has not been changed through the amendment, you can add a few \n" +
            "additional zeros after the decimal point, so it will just increase the value of the coins, without increasing the total money supply.\n" +
            "\n" +
            "Negative rates should not be higher than 0.5% per annum and lower than 0.2% per annum. Negative rates can only be changed through amendments.";

    //Added
    String FREEDOM_OF_SPEECH = "No authority of this corporation or entity shall prohibit free practice\n" +
            "any religion; or restrict freedom of speech, conscience, or the press\n" +
            "or the right of people to peacefully assemble or associate with each other, or not associate with each other, and \n" +
            "to apply to the management of the Corporation of the International Trade Union and to this corporation with a petition for satisfaction of complaints; \" +\n" +
            "or violate the right to the fruits of one's labor, or \n" +
            "the right to a peaceful life of one's choice. \n" +
            "Freedom of speech and conscience includes the freedom to contribute to \n" +
            "political campaigns or nominations for corporate office and should be interpreted as \n" +
            "applying equally to any means of communication. ";

    String RIGHTS = " Natural Rights\n" +
            "All members of the network must respect the Natural Human Rights and not violate them. \n" +
            "\"The presumption of innocence must also be respected and every member of the network must have the right to an honest independent \n" +
            "trial.\n" +
            "Each member has the right to have an attorney or be their own attorney.\n" +
            "\n" +
            "The International Trade Union Corporation should not regulate the cost of goods and services of network members who \n" +
            "sell through this platform.\n" +
            "Also, the Corporation should not ban individual brands on its site, but it can \n" +
            "prohibit the sale of entire groups of goods that fall within the characteristics described by current laws, if \n" +
            "this prohibition does not violate Natural Human Rights. You can use \n as a source of rights" +
            "as a precedent Countries recognized as democratic countries. \n" +
            "\n" +
            "A detailed list is at the United Nations (UN)\n" +
            "\n" +
            "Right to life\n" +
            "The right to liberty and security of person\n" +
            "Right to privacy\n" +
            "The right to determine and indicate one's nationality\n" +
            "The right to use one's native language\n" +
            "The right to freedom of movement and choice of place of stay and residence\n" +
            "The right to freedom of conscience\n" +
            "\n" +
            "Freedom of thought and speech\n" +
            "Freedom of Information\n" +
            "The right to form public associations\n" +
            "The right to hold public events\n" +
            "The right to participate in the management of the affairs of the Corporation of the International Trade Union\n" +
            "The right to appeal to the bodies of the Corporation of the International Trade Union and local authorities." +
            "One of the natural inalienable rights of a person is the right to life," +
            "freedom, security, property, physical and mental integrity, personal dignity, personal and family secrets, etc. " +
            "" +
            "No law in force should be interpreted in such a way as to violate the natural rights of man." +
            " The Corporate Supreme Court may use precedents as judgments, as long as those decisions do not contradict " +
            "the current charter and current laws." +
            " The Corporate Supreme Court can create precedents similar to countries with case law, but apply " +
            "You can if these precedents do not violate the current charter or current laws of the Corporation of the International Trade Union.";

}
