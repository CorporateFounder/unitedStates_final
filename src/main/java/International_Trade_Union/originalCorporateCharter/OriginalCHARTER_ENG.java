package International_Trade_Union.originalCorporateCharter;

public interface OriginalCHARTER_ENG {
    String POWERS_OF_THE_BOARD_OF_DIRECTORS = " Powers of the Board of Directors. " +
            " The Board of Directors may approve invoices submitted for positions on the Directors list. " +
            " Also Laws package names start with ADD_DIRECTOR are packages that contain " +
            " list of new directors who should manage new product lines. Data " +
            " Laws can only be approved by the Board of Directors and from there they will take a list of laws, where " +
            " every line that starts with ADD_DIRECTOR will be added to the Directors list as a new " +
            "positions to apply for." +
            " The package that starts with BUDGET is the budget and can only be approved by the Board of Directors. " +
            "There can be only one effective budget." +
            " The Board of Directors also approves the strategic plan STRATEGIC_PLAN. Only " +
            "one strategic plan." +
            " The board of directors also participates in the approval of laws (rules on which should operate " +
            " all members of the corporation), and also participates in the approval of the implementation of amendments to the charter AMENDMENT_TO_THE_CHARTER." +
            "" +
            " The Council has the right to set and collect commission from sales within the platforms owned by the Corporation " +
            " of the International Trade Union, provided that this commission will not exceed twenty percent (20%). " +
            "All fees must be allocated to expenses that are established by the budget. " +
            " Also, a source of income is the sale of their goods and services, for this there are Office Directors who are elected" +
            " by the board of directors and they must sell the products of the Corporation of the International Trade Union. " +
            "" ;


    //rules for laws
    //TODO
    String HOW_LAWS_ARE_CHOSEN = "HOW LAWS ARE CHOSEN" +
            "No law has retroactive effect. No law should violate the current charter or contradict " +
            " other laws in force. If there is a conflict between several laws from one set of laws, " +
            " then the current one is the one that is higher in the index. Example: a package for the sale of alcohol " +
            "the law under index 3 contradicts the law from index 17, in this case the law under index three will be valid, since it is higher in status. " +
            "if the laws contradict from different packages, then the package that received more votes is valid" +
            " from the Board of Shareholders, if there is parity, then the one that received more votes from the Board of Directors, if here too " +
            "there is parity, then this dispute must be decided by the Chief Justice, if he also did not determine which of the two packages" +
            "where the laws contradict each other, the laws of one of the packages are more valid, then the priority becomes " +
            "the one that began to operate earlier, the countdown is determined precisely from the last moment of entry into force. " +
            " All ordinary laws are valid if they are voted in this way ONE_VOTE by the Board of Shareholders, the Board of Directors and possibly " +
            "The Chief Justice. For a law to be valid, it must receive equal to or more than 100 of the remaining votes of the Board of Shareholders," +
            " equal to or greater than 15 remaining votes of the Board of Directors and One vote of the Chief Justice, but if the Chief Justice did not vote or voted against " +
            " then you can override the veto of the supreme judge by receiving 200 or more of the remainder of the votes of the Board of Shareholders and 30 or more of the remainder of the votes of the Board of Directors. " +
            " " +
            "A law is valid as long as it matches the number of votes as described above. Every time someone loses their office" +
            "also lost are all his votes for all the laws that he voted." +
            "" +
            " Example code in LawsController current law:" +
            " //laws that don't get enough votes that can only pass if the supreme judge approves\n" +
            " List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()\n" +
            " .filter(t -> !directors.contains(t.getPackageName()))\n" +
            " .filter(t->!Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            " .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            " .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            " .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS)\n" +
            " .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)\n" +
            " .filter(t -> t.getVoteHightJudge() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_HIGHT_JUDGE)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            " //laws that have received enough votes and do not require the approval of the supreme judge\n" +
            " List<CurrentLawVotesEndBalance> powerfulVotes = current.stream()\n" +
            " .filter(t -> !directors.contains(t.getPackageName()))\n" +
            " .filter(t-> !Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            " .filter(t-> !directors.isCabinets(t.getPackageName()))\n" +
            " .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            " .filter(t -> t.getVotesBoardOfShareholders() >= (Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS * Seting.POWERFUL_VOTE))\n" +
            " .filter(t -> t.getVotesBoardOfDirectors() >= (Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS * Seting.POWERFUL_VOTE))\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList()); ";

    String HOW_THE_BOARD_OF_DIRECTORS_IS_ELECTED = "How the Board of Directors is elected." +
            "" +
            " The board of directors consists of 301 BOARD_OF_DIRECTORS accounts. " +
            " Each member of the network can apply for the position of the board of directors by creating a package of law, where " +
            " the package name BOARD_OF_DIRECTORS and the sender's account must match the account specified " +
            " in the first line of the law that is contained in the list of this package. " +
            " The 301 account with the most remaining votes wins the position. " +
            " The cost of filing to create a law (position) is worth five digital dollars (5) as a reward to the earner. " +
            " The voting process is described in VOTE_STOCK " +
            "" +
            " Code example: LawController: method currentLaw: " +
            "code section responsible for the election of the board of directors" +
            " //minimum number of positive votes for the law to be valid, \n" +
            " //positions elected by shares of members of the Board of Directors\n" +
            " List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()\n" +
            " .filter(t -> directors.isElectedByStocks(t.getPackageName()))\n" +
            " .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))\n" +
            " .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            " .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())\n" +
            " .collect(Collectors.toList()); ";

    String POWERS_OF_THE_BOARD_OF_SHAREHOLDERS = " Powers of the board of shareholders. " +
            "The Council of Shareholders Participates in the approval of the Laws (rules that all members of this Corporation must comply with). " +
            " Also, the Board of Shareholders participates in the approval of amendments to the charter of the Corporation of the International Trade Union AMENDMENT_TO_THE_CHARTER. " +
            " The Board of Shareholders may also participate in voting when electing candidates CORPORATE_COUNCIL_OF_REFEREES and BOARD_OF_DIRECTORS " +
            " using these rules to vote for VOTE_STOCK candidates. " +
            "";

    String HOW_SHAREHOLDERS_BOARD_IS_ELECTED = " HOW SHAREHOLDERS ARE ELECTED. " +
            " The Board of Shareholders consists of one thousand five hundred accounts (1500) with the largest number of shares, but only those accounts are taken into account " +
            " from whose activity more than a year has not passed. formula: the current year is one year, and if the account was active in this range, it " +
            " is taken into account. All accounts are sorted in descending order of the number of digital shares, and 1500 accounts with the largest number of " +
            " shares. Recalculation occurs every block. " +
            "" +
            " An example of a section of code how the Board of Shareholders is elected: " +
            " class UtilsGovernment method findBoardOfShareholders: " +
            " //determining the board of shareholders\n" +
            " public static List<Account> findBoardOfShareholders(Map<String, Account> balances, List<Block> blocks, int limit) {\n" +
            "List<Block> minersHaveMoreStock = null;\n" +
            " if (blocks.size() > limit) {\n" +
            " minersHaveMoreStock = blocks.subList(blocks.size() - limit, blocks.size());\n" +
            " } else {\n" +
            "minersHaveMoreStock = blocks;\n" +
            "}\n" +
            " List<Account> boardAccounts = minersHaveMoreStock.stream().map(\n" +
            " t -> new Account(t.getMinerAddress(), 0, 0))\n" +
            " .collect(Collectors.toList());\n" +
            "\n" +
            " for (Block block : minersHaveMoreStock) {\n" +
            " for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {\n" +
            " boardAccounts.add(new Account(dtoTransaction.getSender(), 0, 0));\n" +
            "}\n" +
            "\n" +
            "}\n" +
            "\n" +
            "\n" +
            " CompareObject compareObject = new CompareObject();\n" +
            "\n" +
            " List<Account> boardOfShareholders = balances.entrySet().stream()\n" +
            " .filter(t -> boardAccounts.contains(t.getValue()))\n" +
            " .map(t -> t.getValue()).collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            "boardOfShareholders = boardOfShareholders\n" +
            ".stream()\n" +
            " .filter(t -> !t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))\n" +
            " .filter(t -> t.getDigitalStockBalance() > 0)\n" +
            " .sorted(Comparator.comparing(Account::getDigitalStockBalance).reversed())\n" +
            " .collect(Collectors.toList());\n" +
            "\n" +
            "boardOfShareholders = boardOfShareholders\n" +
            ".stream()\n" +
            " .limit(Setting.BOARD_OF_SHAREHOLDERS)\n" +
            " .collect(Collectors.toList());\n" +
            "\n" +
            " return boardOfShareholders;\n" +
            "}";

    String VOTE_STOCK = "How shares vote. " +
            " All shares held by the account are equal to the same number of votes. " +
            " every time someone makes a transaction to an account that is a packet address that starts with " +
            " LIBER he votes for this package. Only those votes from which more than four years have not passed are taken into account. " +
            " if the transaction was made VoteEnum.YES then this account receives votes for according to the formula " +
            " yesV = number of votes equal to the sender's shares." +
            " yesN = how many laws this account voted for with VoteEnum.YES" +
            " resultYES = yesV / math.pow(yesN, 3). Example: A score voted for three scores that start with LIBER," +
            "100 shares in the account means 100 votes. 100 / math.pow(3, 3) = 3.7 means each account gets 3.7 votes." +
            "" +
            " if the transaction was made with VoteEnum.NO " +
            " then the same formula is used, but now all bills for which he voted against are taken into account " +
            "Example the same account voted for two scores against, it has the same one hundred shares." +
            " resultNO = noV / math.pow(noN, 3) = 100/ math.pow(2,3) = 12.5 means every score he voted for, " +
            "against will get 12.5 votes against." +
            " then each score counts all the votes given to it FOR (VoteEnum.YES) and AGAINST (VoteEnum.NO). " +
            " Then this formula is used remainder = resultYES - resultNO. " +
            "First, these positions are selected for all accounts that received more than or equal to fourteen thousand" +
            "four hundred votes of the remainder (14400) remainder >= 14400." +
            " Further, all accounts are sorted in descending order by remainder and from there the number of accounts on " +
            " these positions, as much as it is stipulated in this position. Example: " +
            " For the Board of Directors, that's the 301 accounts with the most balances. " +
            " " +
            "At any time you can change your voice, but only to the opposite, which means if" +
            " you voted for the candidate YES then you can only change to NO and vice versa. " +
            "There is no limit to how many times you can change your voice." +
            "With every block there is a recalculation of votes, if you lose your shares, your candidates" +
            " also lose their votes. This measure is specifically implemented so that elected positions " +
            "were interested in seeing you prosper." +
            "Only CORPORATE_COUNCIL_OF_REFEREES and BOARD_OF_DIRECTORS are elected this way" +
            " Only the last transaction given for each account counts if you haven't updated your vote, " +
            "then after four years it is annulled.";

    String CODE_VOTE_STOCK = " class CurrentLawVotes method: votesLaw " +
            " public double votesLaw(Map<String, Account> balances,\n" +
            " Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {\n" +
            "double yes = 0.0;\n" +
            "double no = 0.0;\n" +
            "\n" +
            " //\n" +
            " for (String s : YES) {\n" +
            "\n" +
            "int count = 1;\n" +
            "count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;\n" +
            " yes += balances.get(s).getDigitalStockBalance() / Math.pow(count, Seting.POWERING_FOR_VOTING);\n" +
            "\n" +
            "}\n" +
            " //\n" +
            " for (String s : NO) {\n" +
            "int count = 1;\n" +
            " count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;\n" +
            " no += balances.get(s).getDigitalStockBalance() / Math.pow(count, Seting.POWERING_FOR_VOTING);\n" +
            "\n" +
            "}\n" +
            "\n" +
            "\n" +
            "return yes - no;\n" +
            "}";

    String POWERS_OF_DIRECTORS_IN_THE_OFFICE = " POWERS OF DIRECTORS IN THE OFFICE " +
            "Office directors are senior directors who are directors of their divisions." +
            " The powers of each director must be described by the laws in force. But each director must manage " +
            " only their division. Coordination of all directors should be led by the General Executive Director GENERAL_EXECUTIVE_DIRECTOR. " +
            " " +
            " Board of Directors, CORPORATE_COUNCIL_OF_REFEREES, HIGH_JUDGE, Board of Shareholders and GENERAL_EXECUTIVE_DIRECTOR can be either " +
            "individuals and legal entities, but one account will be counted as one vote. ";

    String HOW_OFFICE_DIRECTORS_ARE_CHOSEN = " HOW OFFICE DIRECTORS ARE CHOSEN. " +
            " All Directors of the office, these are the top directors who manage their divisions, are elected only by the Board of Directors. " +
            " Each member of the network can apply for the position of supreme director by creating a law with a package name that matches the allowed " +
            " positions, where the address of the sender of this transaction must match the first line from the list of laws in this package. " +
            "The cost of the law is five digital dollars as a reward to the earner." +
            " the score with the most remaining votes wins the position. " +
            " The voting mechanism is described by ONE_VOTE. " +
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
            "" +
            " When these positions are voted, count as one score = one vote " +
            " (CORPORATE_COUNCIL_OF_REFEREES, BOARD_OF_DIRECTORS, GENERAL_EXECUTIVE_DIRECTOR, HIGH_JUDGE and Board of Shareholders). " +
            " every score that starts with LIBER counts all votes FOR (VoteEnum.YES) and AGAINST (VoteEnum.NO) for it " +
            "further subtracted from FOR - AGAINST = if the balances are above the threshold, then it becomes the current law, but if the position is elected" +
            "then after that it is sorted from largest to smallest and the largest number that is described for this position is selected. " +
            "Votes are recalculated every block. After voting, the vote can only be changed to the opposite." +
            " There are no restrictions on the number of times you can change your vote. Only those votes that are given by accounts are taken into account " +
            "in their position, for example, if the account has ceased to be in the Board of Directors, his vote as a " +
            " The Board of Directors does not and will not count in the voting. All votes are valid until the accounts " +
            " those who voted are in their positions. Only those votes from which no more than " +
            " four years, but each participant can at any time renew his vote. ";

    String CODE_VOTE_ONE = " CODE class CurrentLawVotes: method voteGovernment " +
            "" +
            "public int voteGovernment(\n" +
            " Map<String, Account> balances,\n" +
            "List<String>governments\n" +
            "\n" +
            " ) {\n" +
            "int yes = 0;\n" +
            "int no = 0;\n" +
            "\n" +
            " List<String> addressGovernment = governments;\n" +
            " for (String s : YES) {\n" +
            " if (addressGovernment.contains(s)) {\n" +
            " yes += Seting.VOTE_GOVERNMENT;\n" +
            "}\n" +
            "\n" +
            "}\n" +
            " for (String s : NO) {\n" +
            " if (addressGovernment.contains(s)) {\n" +
            "no += Seting.VOTE_GOVERNMENT;\n" +
            "}\n" +
            "\n" +
            "}\n" +
            "\n" +
            "\n" +
            "return yes - no;\n" +
            "\n" +
            "}";

    String MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES = " MECHANISM REDUCING THE NUMBER OF SHARES. " +
            " Every time one account sends a digital share to another account, but uses VoteEnum.NO, the account " +
            "the recipient's digital shares are reduced by the amount sent by the share sender." +
            " Example account A sent to account B 100 digital shares with VoteEnum.NO, then account A and account B both lose 100 " +
            " digital shares. This measure is needed so that there is a mechanism to remove the Board of Shareholders from office and also allows you to lower the votes " +
            " destructive accounts, since the number of votes is equal to the number of shares in the Election of the Board of Directors and " +
            " when electing CORPORATE_COUNCIL_OF_REFEREES. " +
            " This mechanism is valid only for digital shares and only if the sender sent with the sign " +
            "VoteEnum.NO";

    String WHO_HAS_THE_RIGHT_TO_CREATE_LAWS = "Create Laws in Cryptocurrency International Trade Union Corporations have the right" +
            " all network members who have at least five digital dollars. " +
            " To create a law through the cryptocurrency mechanism of the International Trade Union Corporation " +
            " It is necessary to create an object of the Laws class inside this cryptocurrency, where packetLawName is the name of the law package. " +
            " List<String> laws - is a list of laws, String hashLaw - is the address of this package of laws and starts with LIBER. " +
            " In order for a law to be included in the pool of laws, you need to create a transaction where the recipient is the hashLaw of this law and the reward " +
            " miner equals five digital dollars (5) of this cryptocurrency. After that, as the law gets into the block, it will be in the pool " +
            "laws and it will be possible to vote for him." +
            " The number of lines in a package of laws can be as many as needed and there are no restrictions. ";

    String POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES = " POWERS OF THE CORPORATE COUNCIL OF JUDGES. " +
            "Approves the Chief Justice." +
            " Participates in voting on the implementation of amendments. " +
            " " +
            "The judicial power of the Corporation of the International Trade Union belongs" +
            "to one Supreme Court and such inferior courts as the Corporation International" +
            "The Merchant Union may issue and establish from time to time." +
            " Judges of both supreme and inferior courts hold their offices in good conduct and " +
            "receive remuneration for their services within the stipulated time. " +
            "Judicial power extends to all cases of law and justice," +
            " including those initiated by members to challenge the misappropriation of funds, " +
            "arising under these Articles, the laws of the International Trade Union Corporation and treaties," +
            "prisoners or who will be imprisoned according to their authority." +
            "To disputes," +
            "in which the International Trade Union will be party to a dispute between two or more members of the network." +
            "No trial should be secret, but justice should be administered openly and free of charge, completely and without delay," +
            "and every person shall have legal protection against harm done to life, liberty or property." +
            " Supreme Court CORPORATE_COUNCIL_OF_REFEREES and Chief Justice HIGH_JUDGE ";

    String HOW_THE_CORPORATE_BOARD_OF_JUDGES_IS_ELECTED = " HOW THE CORPORATE BOARD OF JUDGES IS ELECTED. " +
            "CORPORATE_COUNCIL_OF_REFEREES" +
            " consists of 55 accounts. " +
            " each member of the network can apply for the CORPORATE_COUNCIL_OF_REFEREES position by creating a law package, where " +
            " the name of the package CORPORATE_COUNCIL_OF_REFEREES and the sender's account must match the account specified " +
            " in the first line of the law that is contained in the list of this package " +
            "The 55th score with the most remaining votes wins the position." +
            " The cost of filing to create a law (position) is worth five digital dollars (5) as a reward to the earner. " +
            " The voting process is described in VOTE_STOCK " +
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

    String HOW_THE_CHIEF_JUDGE_IS_CHOSEN = " HOW HIGH_JUDGE IS CHOSEN. " +
            "The Chief Justice is elected by CORPORATE_COUNCIL_OF_REFEREES." +
            " Each member of the network can apply for the position of Chief Justice by creating a law with a package name that matches the allowed " +
            " position, where the address of the sender of this transaction must match the first line from the list of laws in this package. " +
            "The cost of the law is five digital dollars as a reward to the earner." +
            " the score with the most remaining votes wins the position. " +
            " The voting mechanism is described by ONE_VOTE. " +
            "" +
            " Code example as stated by the Chief Justice. class LawsController: method currentLaw. Code snippet " +
            " //positions elected by the board of corporate chief judges\n" +
            " List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()\n" +
            " .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))\n" +
            " .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList()); ";

    String POWERS_OF_THE_CHIEF_JUDGE = " POWERS OF THE CHIP JUDGE. " +
            "The Chief Justice participates in the approval of laws, as well as" +
            " can participate in resolving disputes within network members like CORPORATE_COUNCIL_OF_REFEREES, " +
            " but his vote is higher than that of CORPORATE_COUNCIL_OF_REFEREES. ";

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

    String HOW_THE_BUDGET_IS_APPROVED = " HOW IS THE BUDGET APPROVED. " +
            "There can be only one effective budget. Only the Board of Directors approves the budget." +
            " To approve the budget, you need to get the method described in VOTE_ONE 15 or more votes. " +
            "the process itself goes like this: " +
            " 1. First, all packages of laws are selected where the name of the package matches BUDGET. " +
            "2. Next, all the packages that received 15 or more votes are selected. " +
            " 3. Then all these packages are sorted in descending order, with the most votes. " +
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


    String HOW_IS_THE_STRATEGIC = " AS THE STRATEGIC PLAN IS APPROVED. " +
            " The strategic plan is approved by the Board of Directors, the strategic plan may be valid " +
            "only in a single copy. For the Strategic Plan to be valid, a balance of votes is needed" +
            " Board of Directors 15 or more. Voting method VOTE_ONE. " +
            " " +
            " The Board of Directors can cancel the Strategic Plan at any time. The Strategic Plan is in effect " +
            " as long as the number of votes is 15 or more. The Strategic Plan may include a general direction " +
            "Corporations, and what needs to be implemented." +
            "" +
            "a section of code that shows how the Strategic Plan is being approved." +
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

    String HOW_NEW_POSITIONS_ARE_ADDED = " HOW ARE NEW POSITIONS ADDED. " +
            "In this way, only top management, subordinates of each leader are added" +
            " are hired without the use of the blockchain, they can be hired by the director himself, or otherwise as " +
            " described by applicable laws. Top management is added to the list of class Directors. " +
            "all newly added positions are valid as long as their laws that create these positions" +
            " work. Only the Board of Directors can add new positions. " +
            " Every package of laws that starts with ADD_DIRECTOR is defined as adding a position. " +
            " the list of laws that are inside this package are positions if the name is in " +
            " line starts with ADD_DIRECTOR. Example: package name ADD_DIRECTOR_PACKAGE" +
            " the name of the first third and fourth line ADD_DIRECTOR_FIRST ADD_DIRECTOR_THIRD " +
            " ADD_DIRECTOR_FOUR this will create three positions for the position. " +
            " But if the line starts with ADD_DIRECTOR, then the job title must be large " +
            " letters and underscores, also there should be only one position in one line " +
            " and no more words. Those lines where there is no position added are used to describe " +
            " permissions of the added positions. " +
            " In order for new positions to be added to the list, the Board of Directors must vote by the " +
            "VOTE_ONE 15 or more votes." +
            "Once new positions are created, each member of the network will be able to apply for these positions." +
            "" +
            " an example of a piece of code that creates new posts. " +
            " class LawsController: method currentLaw: " +
            "" +
            "\n" +
            " //adds laws that create new directorships\n" +
            " List<CurrentLawVotesEndBalance> addDirectors = current.stream()\n" +
            " .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            " .filter(t->t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS)\n" +
            " .collect(Collectors.toList());";

    String PROPERTY_OF_THE_CORPORATION = " PROPERTY OF THE CORPORATION. " +
            "All property owned by the International Trade Union Corporation cannot be sold without a valid law," +
            " where the sale process will be described and at what price the property will be sold. The account of the founder, and the account of other participants is not " +
            "corporate account, the Board of Directors should create a separate account that will be budgeted and managed only by members of current members" +
            " Board of directors. ";

    String INTERNET_STORE_DIRECTOR = " This director is engaged in the development, promotion and management of an online store, which should " +
            " sell goods for a digital dollar or digital shares. Detailed authority must be given either through applicable laws or " +
            " issued by the CEO or the Board of Directors. " +
            "The name of the Store must be determined by either the Board of Directors or the Chief Executive Officer.";

    String GENERAL_EXECUTIVE_DIRECTOR = " This Director coordinates the actions of other senior directors to implement the strategic plan or " +
            "the tasks assigned to him by the laws in force. All powers must be issued to him through the laws in force.";

    String DIRECTOR_OF_THE_DIGITAL_EXCHANGE = "This Director is the development, promotion and management of the Internet exchange. The authority must be " +
            "issued to him either by applicable law or by the Board of Directors or by the Chief Executive Officer.";

    String DIRECTOR_OF_DIGITAL_BANK = "This Director is in charge of Internet Banking. All powers must be issued either by applicable law or by the Board of Directors" +
            "or the Chief Executive Officer.";

    String DIRECTOR_OF_THE_COMMERCIAL_COURT = " The director of a private commercial court must provide leadership to a private court, all powers " +
            "must be Issued either by applicable law or by the Board of Directors or the Chief Executive Officer.";

    String MEDIA_DIRECTOR = " This Director directs the media, all powers must be issued by applicable laws, or by the Board of Directors or " +
            "CEO.";

    String DIRECTOR_OF_THE_DEVELOPMENT_OF_THE_IMPLEMENTATION_OF_CRYPTOCURRENCY_TECHNOLOGIES_OF_THE_INTERNATIONAL_TRADE_UNION_CORPORATION = "" +
            " This Director manages the development and implementation of new code in this cryptocurrency, all powers must be issued only through " +
            " current laws, but may also be issued by either the Board of Directors if the current laws have given such powers to the Board of Directors. " +
            " Also, no code change should be contrary to the current charter or applicable laws. Also, powers can be issued" +
            " by the Chief Executive Officer, if such authority is granted to the Chief Executive Officer by applicable law. ";

    String EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE = " EXPLANATION WHY MONEY DEMURAGE IS USED HERE.." +
            " Negative rates are now used in many countries, this encourages money holders when the price is excessively high, " +
            " saturate the market with money. The amount of money mined for each block is 200 digital dollars and 200 digital shares, " +
            "also 2% of each mining reward to the founder, which is 4 digital dollars and 4 digital Shares for each block mining. " +
            " Here it is used as the Theories of Silvio Gesell, as well as the school of monetarism (in a modified form " +
            " with Silvio Gesell, the negative rate was 1% per month, which would just kill the economy, " +
            "under monetarism, the growth of the money supply should have been proportional to the growth of GDP, but since " +
            " in this system it is not possible to calculate the real GDP growth, I set a fixed growth, also if the money growth " +
            " will be equal to GDP, there is a high probability of Hyperinflation. Money must be hard in order to " +
            " business could predict its long-term investments and from monetarism, only that part was taken that " +
            "the money supply should grow linearly, but in general there is a mix of different economic schools, including the Austrian one" +
            " economic school.). With a negative rate of 0.1% every six months for digital " +
            " dollars and 0.2% for digital stocks, we avoid the consequences of a severe economic crisis for this currency. " +
            " Such a mechanism creates a price corridor, where the lower limit of the value of these digital currencies is the total number of digital currencies issued " +
            " dollars and digital stocks, and the upper limit is the real value. As soon as the value becomes higher than the real value, " +
            " it becomes more profitable for holders to sell digital dollars and digital shares at inflated prices, thereby saturating the market with money " +
            "and creating a correction in the market." +
            "" +
            "The main source of monetary crises is between rapid changes in commodity prices and slow changes in wages." +
            " Example: Imagine that the value of the currency has risen sharply by 30%, it becomes more profitable for holders not to invest money, since " +
            "revenues from holding currency are higher than what is now paid for more expensive employees, because of this, the money stops" +
            "to invest, people do not receive wages, which leads to the fact that a huge number of goods are not sold," +
            "which leads to the fact that some manufacturers go bankrupt and lay off many workers, which further reduces wages" +
            "pay the rest, as the labor market becomes surplus. Which in turn causes even more fear among the holders" +
            "money to invest and this process continues until the moment when the value of money begins to decline due to the fact" +
            "that the total number of production chains has shrunk and so have the goods."+
             " Example: Let's imagine that we had inflation and the value of money fell by 40% within a month, the cost of goods rises sharply, " +
                     " but wages have not risen, so many goods will not be bought, resulting in the closure of production chains, " +
                     "which, in turn, due to an excess of workers in the labor market, reduces wages, which also further reduces" +
                     " the number of goods sold. The first case A deflationary spiral occurs due to a sharp reduction in money in the market, the second " +
                     "The case of stagflation often occurs when a sharply excess amount of money enters the market. " +
                     "" +
                     "So that such crises do not arise, in this cryptocurrency, money grows in the same predictable amount. " +
                     "204 (4 - reward to the founder, 200 - reward to the earner)" +
                     " digital dollars and shares per block, there are about 576 blocks per day. And the negative rate adjusts the cost of coins every six months. " +
                     " It is also forbidden to use fractional reserve banking for these coins, since their number grows linearly, and " +
                     "will not be able to cover the debts incurred due to fractional reserve banking, due to absence due to a lack of " +
                     " cash, because with fractional reserve banking, the growth in debt will be much higher than this protocol will create money. " +
                     " also if you increase the money supply by changing the settings, and making the growth of the money supply much higher, it can cause hyperinflation or " +
                     "even galloping inflation. If it is necessary to increase the growth of the money supply, this should only happen through amendments, " +
                     " while keeping the founder's reward percentage at two percent. And the mining per block should not increase more than 5% for " +
                     "twenty years, each subsequent increase that may be made must pass at least twenty years through amendments," +
                     " and no more than 5% per block from the reward of the last block. (Example: if we changed " +
                     "through the amendments, then the extraction should not be higher than 210 coins, but each subsequent one will be no more than five percent of the last." +
                     "Thus, the next increase made through the amendments will be 220.5 coins. But this amendment will be made only after twenty" +
                     " years after the first correction for production change) " +
                     "" +
                     " If the money supply is deficient, if the number of mined coins has not been changed through the amendment, you can add a few " +
                     "additional zeros after the decimal point, so it will simply increase the value of the coins, without increasing the total money supply." +
                     "" +
                     " Negative rates should not be higher than 0.5% per annum and lower than 0.2% per annum. Negative rates can only be changed through amendments. " +
                     " ";

    String FREEDOM_OF_SPEECH = "No authority of this corporation or entity shall prohibit free practice" +
            " any religion; or restrict freedom of speech, conscience, or the press; " +
            "or the right of people to peacefully assemble or associate with each other, or not associate with each other, and " +
            " apply to the management of the Corporation of the International Trade Union and to this corporation with a request for satisfaction of complaints; " +
            " or violate the right to the fruits of one's labor or " +
            " the right to a peaceful life of one's choice. Freedoms of speech and conscience include the freedom to contribute to " +
            " political campaigns or nominations for corporate office and shall be interpreted as " +
            "applying equally to any means of communication. ";

    String RIGHTS = "All members of the network must respect the Natural Human Rights and not violate them." +
            " The presumption of innocence must also be respected and each member of the network must have the right to an honest independent " +
            "litigation. Each participant has the right to a lawyer or to be his own lawyer." +
            " International Trade Union Corporation should not regulate the cost of goods and services of network members who " +
            " sell through this platform. Also, the Corporation should not ban individual brands on its site, but may " +
            "prohibit the sale of entire groups of goods that fall within the characteristics described by applicable laws, if " +
            "this ban does not violate Natural Human Rights. As a source of rights, countries recognized as democratic can be taken as a precedent" +
            "countries." +
            "One of the natural inalienable human rights is the right to life," +
            "freedom, security, property, physical and mental integrity, personal dignity, personal and family secrets, etc. " +
            "" +
            "No law in force should be interpreted in such a way as to violate the natural rights of man." +
            " The Corporate Supreme Court may use precedents as judgments, as long as those decisions do not contradict " +
            "the current charter and current laws." +
            " The Corporate Supreme Court can create precedents similar to countries with case law, but apply " +
            "You can if these precedents do not violate the current charter or current laws of the Corporation of the International Trade Union.";


}
