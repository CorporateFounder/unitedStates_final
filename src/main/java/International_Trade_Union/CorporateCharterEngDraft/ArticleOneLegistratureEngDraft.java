package International_Trade_Union.CorporateCharterEngDraft;
/**устарел(недействителен), является черновиком, но его хеш используется в генезис блоке.
 Но с помощью новых законов можно добавить те части которые будут интересны и сделать их действительным законом, если
 данные части не противоречат действующему уставу и действующим законам.
 */
public interface ArticleOneLegistratureEngDraft {
    String SECTION_1 = "All legislative authority granted here is vested in the Corporation " +
            "International Trade Union, which consists of the Board of Shareholders and HigherSpecialPositions and all other members" +
            "who are entitled to vote in accordance with these Statutes." +
            " All laws are created through the International Trade Union Corporation's cryptocurrency mechanism as described here " +
            "rules." +
            "All the powers granted by this Charter, as well as existing laws that do not contradict this charter" +
            "are legitimate." +
            "The only advantage of the board of directors over the rest of the participants in this system and its" +
            "citizens, is only the fact that they have 10% more votes. And also the right to introduce amendments" +
            "as described in this statute";

    String CHAPTER_2 = "The Board of Shareholders consists of one thousand accounts(1000)," +
            "accounts with the most digital stocks digitalStockBalance." +
            "But this list is selected from all accounts that were active at least once, i.e. senders or miners during" +
            "of one year (the current year is one year, and if the activity was within this range.), but" +
            "if more than a year has passed since the last activity, then this account is not taken into account in the selection" +
            "of the Shareholders' Council. This is necessary so that if an account is suddenly lost, then this does not affect the system, since " +
            "only active members and only active members can be Shareholders." +
            "Recalculation of members of the Board of Shareholders occurs every block and is determined by each block." +
            " when voting through the mechanism of this cryptocurrency, their votes (digital shares) are multiplied by a multiplier of 1.10 " +
            "which gives them ten percent more" +
            "votes in the creation of the law within the framework of this" +
            "cryptocurrencies (when voting through the mechanism of this cryptocurrency)" +
            "Absolutely any person (Legal, Physical, Digital Person-Artificial Intelligence, etc.)" +
            " may be a member of the Board of Shareholders (regardless of citizenship, nationality, gender, etc. or any " +
            "other signs) or even any state can act as a member of the Board of Shareholders." +
            "" +
            "Algorithm for Election of the Board of Shareholders:" +
            " //determining the board of shareholders\n" +
            " public static List<Account> findBoardOfShareholders(Map<String, Account> balances, List<Block> blocks, int limit) {\n" +
            "List<Block> minersHaveMoreStock = null;\n" +
            " if(blocks.size() > limit){\n" +
            " minersHaveMoreStock = blocks.subList(blocks.size() - limit, blocks.size());\n" +
            "}\n" +
            "else {\n" +
            "minersHaveMoreStock = blocks;\n" +
            "}\n" +
            " List<Account> boardAccounts = minersHaveMoreStock.stream().map(\n" +
            " t->new Account(t.getMinerAddress(), 0, 0))\n" +
            " .collect(Collectors.toList());\n" +
            "\n" +
            " for (Block block : minersHaveMoreStock) {\n" +
            " for (DtoTransaction dtoTransaction : block.getDtoTransactions()) {\n" +
            " boardAccounts.add(new Account(dtoTransaction.getSender(), 0, 0));\n" +
            "}\n" +
            "}\n" +

            " CompareObject compareObject = new CompareObject();\n" +
            " List<Account> boardOfShareholders = balances.entrySet().stream()\n" +
            " .filter(t->boardAccounts.contains(t.getValue()))\n" +
            " .map(t -> t.getValue()).collect(Collectors.toList());\n" +
            "boardOfShareholders = boardOfShareholders\n" +
            ".stream()\n" +
            " .filter(t -> !t.getAccount().startsWith(Seting.NAME_LAW_ADDRESS_START))\n" +
            " .filter(t -> t.getDigitalStockBalance() > 0)\n" +
            " .sorted((f1, f2) -> {return compareObject.compare(f1, f2);})\n" +
            " .limit(Setting.BOARD_OF_SHAREHOLDERS)\n" +
            " .collect(Collectors.toList());\n" +
            " return boardOfShareholders;\n" +
            "}" +
            "" +
            "boardOfShareholdersController is sorted in descending order" +
            " //sort in descending order\n" +
            " boardOfShareholders = boardOfShareholders.stream().sorted((f1, f2)\n" +
            " -> Double.compare(f2.getDigitalStockBalance(), f1.getDigitalStockBalance())).collect(Collectors.toList());\n";

    String CHAPTER_3 = "Any International Trade Union Corporation cryptocurrency account can create laws through the mechanism of this cryptocurrency." +
            "The Cost of Making Law" +
            "one hundred and forty-four digital dollars (144) as a reward to the miner (the cost may be reduced through current law)." +
            "The law created through the mechanism of this cryptocurrency" +
            " is valid as long as " +
            "he was voted" +
            "five million one hundred and eighty-four thousand votes or more. The voting mechanism is as follows: Votes that voted FOR minus votes that voted" +
            "AGAINST and" +
            "the remainder must be greater than 5184000. This measure is intended not to" +
            "votes against were lost (Example: if we take the classic mechanism of voting by shares," +
            "then if one participant has 51%, then the remaining 49% of the votes will not be taken into account." +
            "since no matter how the rest vote, it will not play a role. But in this one" +
            " system subtracting from FOR - AGAINST > THRESHOLD, we get a situation where each vote " +
            "important.). For normal accounts, the number of votes is equal to the number of digital shares at the moment, " +
            "for members of the Board of Shareholders" +
            " The number of votes is equal to the number of digital shares at the moment, multiplied by a multiplier of one and ten hundredths" +
            " percent (1.10)." +
            " Which gives members of the Board of Shareholders 10% more votes than they own digital shares. " +
            "Each block is recalculating all the votes of those who voted for and against. As long as the number of votes is more than 5,184,000, the law is" +
            " valid. ";

    String CHAPTER_4 = " The right to create laws through the mechanism of this cryptocurrency, " +
            "as well as the right to vote for laws through the mechanism of this cryptocurrency have all legal entities and individuals who have " +
            " an account in this cryptocurrency and satisfying the conditions for creating and voting in " +
            "to this cryptocurrency of the International Trade Union Corporation." +
            "Only those votes that are less than three years old for laws and no more than three years old for HigherSpecialPositions count," +
            " but you can vote again to " +
            "will update the voice. This measure is necessary for the security of the system, if suddenly the account was lost, then this voice will not work forever," +
            "But only a certain period of time." +
            "it also allows you to re-elect important officials and fight against lobbying for bad laws because bad laws" +
            "do not get re-elected.";

    String CHAPTER_5 = "HigherSpecialPositions These special positions are senior positions. All positions are " +
            "described in this" +
            " Bylaws are valid. (CORPORATE_SUPREME_COURT(18), DEPARTMENT_OF_CORPORATE_JOURNALISTS(15)," +
            " BOARD_OF_DIRECTORS(16), MARKETING_DEPARTMENT(5), DEPARTMENT_OF_STRATEGIC_DEVELOPMENT_AND_PLANNING(5), PR_DEPARTMENT(5)," +
            " DEPARTMENT_OF_FINANCE(5),DEPARTMENT_OF_HUMAN_RESOURCES(5))." +
            "To create new special top positions you need" +
            "Create a law with a new job title, determine how many leadership positions will be created for that job." +
            "And also to determine its powers. This position and all its powers will be valid." +
            "as long as the law that created this position is in effect. " +
            "In the event that the law on the office has become invalid" +
            ", then this position loses all its powers. If again this law is not renewed or a similar law is created" +
            "for a canceled position. The positions that are described in the charter are always valid, but their powers may" +
            "change depending on the laws in force that describe these positions" +
            "How is a law passed that creates a new position and is not repealed within four weeks of its adoption, " +
            "All miners must add this position to HigherSpecialPositions." +
            " ";

    String CHAPTER_6 =
            " To take the Special position HigherSpecialPositions you need to create a law with the name of the package which " +
                    " coincides with the name of the current position. The address of the sender (creator) must be entered as the first line of the law " +
                    "of this law. The address of the creator must match the address that is written in this law." +
                    " The second line can be filled with contact details or useful information about who is applying for " +
                    " this position. The account will take office as soon as this package of law is approved with those " +
                    "the same conditions as for all laws in this cryptocurrency and he will be in office until" +
                    "his application package for this position will be valid.";



    String CHAPTER_7 = "In the case of several existing laws that contradict each other" +
            ", Chief Justice (appointed through the CORPORATE_SUPREME_COURT cryptocurrency)" +
            "should give priority to those laws," +
            " who at the time of the trial has the largest number of votes, exceeding 5% " +
            " from dissenting votes, but if " +
            "there is no such superiority, then this issue must be resolved either with the help of a precedent that" +
            "will remain in effect until there is a change in the number of votes or until the supreme judges who set the precedent" +
            "are in their positions." +

            " The precedent is created by the vote of the supreme judges who, by a majority vote, determine what in the case " +
            "as long as several conflicting laws are in effect and there is no 5% majority, then the court accepts that one" +
            "or another law will be higher, over another, as long as parity in votes for the law is maintained and as long as the judges" +
            "who voted for this law as the current and higher, of the contradictory but adopted through the mechanism" +
            " of this cryptocurrency. If the composition of the judges has changed, those who voted for the law, then if the " +
            "the parity of conflicting laws must be accepted again with the help of a precedent which of them is higher than the others" +
            "contradictory." ;

    String CHAPTER_8 = "To create law through the International Trade Union Corporation's cryptocurrency mechanism" +
            " Inside this cryptocurrency, Create an object of the Laws class, where packetLawName- is the name of the law package." +
            " List<String> laws - is a list of laws, String hashLaw - is the address of this package of laws and starts with LIBER." +
            " In order for a law to be included in the pool of laws, you need to create a transaction where the recipient is the hashLaw of this law and the reward " +
            " miner is equal to one hundred and forty-four digital dollars of this cryptocurrency. After that, as the law gets into the block, it will be in the pool " +
            "laws and it will be possible to vote for him.";

    String CHAPTER_9 = "when sending \"digitalStockBalance\" to any balance, with VoteEnum.NO" +
            "reduces the recipient's balance by the amount sent by the sender," +
            "This operation is needed to be able to lower the voices of the destructive members of the Corporation of the International Trade Union" +
            "and also to remove a specific bill from the Board of Shareholders.," +
            "or reduce the political power of a particular account,\n" +
            "\n" +
            "when sending \"digitalStockBalance\" to any balance, with VoteEnum.YES\n" +
            "increases the recipient's balance by the amount sent by the sender," +
            "This operation is needed" +
            " to add a specific account to the Board of Shareholders, or increase " +
            "the political power of a particular account.";

    String CHAPTER_10 = "All individuals may hold special senior positions." +
            "HigherSpecialPositions." +
            "Exemplary job code is described in GovernmentController." +
            "Each Special Position has a limited number." +
            " One account can take one place in the pool of one position, but " +
            "One account can hold several positions if they are different and have different powers." +
            " (Example: CORPORATE_SUPREME_COURT(18),BOARD_OF_DIRECTORS(16) the same account can borrow one" +
            " positions in each such group, but the account cannot occupy for example more than two positions in the position BOARD_OF_DIRECTORS" +
            "or other special position)." +
            "The requirements for the position are the same as for the current law, but there is a difference, an approximate algorithm is described here:" +
            "1.creates a list of all balances." +
            "2. create a list of all laws." +
            "3. create lists named HigherSpecialPositions." +
            "THIS CODE CAN BE IMPROVED IF THE OPERATING PRINCIPLES ARE KEEPED, BUT THE IMPROVEMENT MUST BE" +
            "FIRST CREATED IN THE VIEW OF THE CURRENT LAW, WHERE IT IS INDICATED WHAT CODE AND HOW THIS CODE WILL ACT" +
            " //selects the position of vacancies\n" +
            " //selects the position of vacancies\n" +
            " class UtilsLaws public static List<LawEligibleForParliamentaryApproval> getPossions(List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals, HigherSpecialPositions corporateSeniorPositions){\n" +
            "\n" +
            " List<LawEligibleForParliamentaryApproval> temporary = new ArrayList<>();\n" +
            "temporary = lawEligibleForParliamentaryApprovals.stream()\n" +
            " .filter(t->Objects.nonNull(t))\n" +
            " .filter(t->Objects.nonNull(t.getLaws()))\n" +
            " .filter(t->Objects.nonNull(t.getLaws().getLaws()))\n" +
            " .filter(t->Objects.nonNull(t.getLaws().getPacketLawName()))\n" +
            " .filter(t->Objects.nonNull(t.getName()))\n" +
            " .filter(t->Objects.nonNull(t.getLaws().getHashLaw()))\n" +
            " .sorted((f1, f2) -> Double.compare(f2.getAccount().getDigitalStockBalance(), f1.getAccount().getDigitalStockBalance()))\n" +
            " .filter(t-> t.getLaws().getPacketLawName().equals(corporateSeniorPositions.name()))\n" +
            " .limit(corporateSeniorPositions.getCount())\n" +
            " .collect(Collectors.toList());\n" +
            "return temporary;\n" +
            "\n" +
            "}" +
            "," +
            "4. Get a list of shareholders." +
            "5. Make sure the package—the law that acts as an office is valid, in the same way as ordinary laws." +
            "6. Next, sort each current position of the law in descending order, and select from them with the largest number" +
            "Votes equal to the number for this position." +
            " //select votes above the limit\n" +
            "curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey())\n" +
            " .stream().filter(t-> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            " .collect(Collectors.toList()));\n" +
            "\n" +
            " //select the number that corresponds to this position\n" +
            " List<CurrentLawVotesEndBalance> temporary = curentLawVotesEndBalance.get(corp.getKey()).stream().filter(\n" +
            "t->!t.getPackageName().equals(corp.getKey()))\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            " .limit(corp.getKey().getCount()).collect(Collectors.toList());\n" +
            "\n" +
            "currentLawVotesEndBalance.put(corp.getKey(), temporary);" +
            "" +
            "Any person can vote for participants applying for positions, but each person can apply for a position only for himself" +
            "when applying for this position, the bill creates a law and the first line of the law is the same as the sender's bill." +
            "once a bill is on the list, everyone can vote for that bill, which approves the score specified" +
            "there for this position. Current positions are selected in this way:" +
            "GovernmentController corporateSeniorpositions" +
            " Blockchain blockchain = Mining.getBlockchain(\n" +
            " Seting.ORIGINAL_BLOCKCHAIN_FILE,\n" +
            "BlockchainFactoryEnum.ORIGINAL);\n" +
            "\n" +
            " //Getting balance\n" +
            " Map<String, Account> balances = new HashMap<>();\n" +
            "\n" +
            "balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);\n" +
            "\n" +
            " //Finding a position\n" +
            " List<LawEligibleForParliamentaryApproval> allGovernment =\n" +
            "UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);\n" +
            "\n" +
            " //list of positions\n" +
            " Map<HigherSpecialPositions, List<LawEligibleForParliamentaryApproval>> positionsListMap = new HashMap<>();\n" +
            " //adding all posts\n" +
            " for (HigherSpecialPositions corporateSeniorPositions : HigherSpecialPositions.values()) {\n" +
            "positionsListMap.put(corporateSeniorPositions, UtilsLaws.getPossions(allGovernment, corporateSeniorPositions));\n" +
            "}\n" +
            "\n" +
            "\n" +
            " //list of shareholders\n" +
            " List<Account> BoardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockchain.getBlockchainList(), Seting.BOARDS_BLOCK);\n" +
            "\n" +
            " //constant list of shareholders\n" +
            " List<Account> finalBoardOfShareholders = BoardOfShareholders;\n" +
            "\n" +
            " //list of laws with votes\n" +
            " Map<HigherSpecialPositions, List<CurrentLawVotesEndBalance>> curentLawVotesEndBalance = new HashMap<>();\n" +
            "\n" +
            " for (Map.Entry<HigherSpecialPositions, List<LawEligibleForParliamentaryApproval>> corp :positionsListMap.entrySet()) {\n" +
            " //remove duplicate posts from the list.\n" +
            " .distinct().collect(Collectors.toList()));\n" +
            "\n" +
            " //get balance and votes for existing laws\n" +
            " curentLawVotesEndBalance.put(corp.getKey(),UtilsGovernment.filters(corp.getValue(), balances, BoardOfShareholders,\n" +
            " blockchain.getBlockchainList(), Seting.POSITION_YEAR_VOTE));\n" +
            "\n" +
            " //select votes above the limit\n" +
            "curentLawVotesEndBalance.put(corp.getKey(), curentLawVotesEndBalance.get(corp.getKey())\n" +
            " .stream().filter(t-> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            " .collect(Collectors.toList()));\n" +
            "\n" +
            " //select the number that corresponds to this position\n" +
            " List<CurrentLawVotesEndBalance> temporary = curentLawVotesEndBalance.get(corp.getKey()).stream().filter(\n" +
            "t->!t.getPackageName().equals(corp.getKey()))\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            " .limit(corp.getKey().getCount()).collect(Collectors.toList());\n" +
            "\n" +
            "curentLawVotesEndBalance.put(corp.getKey(), temporary);\n" +
            "}\n" +
            "\n" +
            "\n" +
            "\n" +
            "model.addAttribute(\"show\", curentLawVotesEndBalance);\n" +
            "\n" +
            " model.addAttribute(\"title\", \"current guidance\");\n" +
            "\n" +
            "return \"/governments\";";

    String CHAPTER_11 = "To vote for any law created through the mechanism of this cryptocurrency," +
            "it is enough to put the address of the law as a recipient and as a vote" +
            "send VoteEnum YES or NO as soon as your vote hits the sender's vote will be equal to the number of digitalStockBalance on this " +
            "moment, if the sender is a member of the Board of Shareholders, then the vote will be equal to the number of digital reputation at the moment multiplied by " +
            " 1.10, which will give 10% more votes. Votes for the law are recalculated every block " +
            "Algorithm for counting votes:" +
            "" +
            "classLawController: " +
            " public String currentLaw(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {\n" +
            "\n" +
            " Blockchain blockchain = Mining.getBlockchain(\n" +
            " Seting.ORIGINAL_BLOCKCHAIN_FILE,\n" +
            "BlockchainFactoryEnum.ORIGINAL);\n" +
            "\n" +
            " Map<String, Account> balances = new HashMap<>();\n" +
            " //read balance\n" +
            "balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);\n" +
            "\n" +
            " List<LawEligibleForParliamentaryApproval> lawEligibleForParliamentaryApprovals =\n" +
            "UtilsLaws.readLineCurrentLaws(Seting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);\n" +
            "\n" +
            "\n" +
            " List<Account> boardOfShareholders = UtilsGovernment.findBoardOfShareholders(balances, blockchain.getBlockchainList(), Seting.BOARDS_BLOCK);\n" +
            "\n" +
            "\n" +
            "\n" +
            " //data to display\n" +
            " List<CurrentLawVotesEndBalance> current = UtilsGovernment.filters(lawEligibleForParliamentaryApprovals, balances, boardOfShareholders,\n" +
            " blockchain.getBlockchainList(), Seting.LAW_YEAR_VOTE);\n" +
            "// Seting.ORIGINAL_LIMIT_MIN_VOTE\n" +
            " //minimum number of positive votes for the law to be valid,\n" +
            "// int ORIGINAL_LIMIT_MIN_VOTE = (int) (200 * Seting.COUNT_BLOCK_IN_DAY * 45); count_block_in_day = 144\n" +
            " current = current.stream().filter(t-> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            " model.addAttribute(\"title\", \"current law: current laws are laws that have more than this: \" + Seting.ORIGINAL_LIMIT_MIN_VOTE + \" the number of votes.\");\n" +
            "model.addAttribute(\"currentLaw\", current);\n" +
            " return \"current-laws\";\n" +
            "}" +
            "\n" +
            "\n" +
            " //data to display. Actual laws are defined here\n" +
            " List<CurrentLawVotesEndBalance> current = UtilsGovernment.filters(lawEligibleForParliamentaryApprovals, balances, boardOfShareholders);\n" +
            "// Seting.ORIGINAL_LIMIT_MIN_VOTE\n" +
            " //minimum number of positive votes for the law to be valid,\n" +
            "// int ORIGINAL_LIMIT_MIN_VOTE = (int) (200 * Seting.COUNT_BLOCK_IN_DAY * 45); count_block_in_day = 144\n" +
            " current = current.stream().filter(t-> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            " .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            " model.addAttribute(\"title\", \"current law: current laws are laws that have more than this: \" + Seting.ORIGINAL_LIMIT_MIN_VOTE + \" the number of votes.\");\n" +
            "model.addAttribute(\"currentLaw\", current);\n" +
            " return \"current-laws\";\n" +
            "}" +
            "" +
            "ClassUtilsGovernment:" +
            " public static List<CurrentLawVotesEndBalance> filters(List<LawEligibleForParliamentaryApproval> approvalList, Map<String, Account> balances, List<Account> BoardOfShareholders, List<Block> blocks, int limitBlocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {\n" +
            " //current laws whose votes are greater than ORIGINAL_LIMIT_MIN_VOTE\n" +
            " List<CurrentLawVotesEndBalance> current = new ArrayList<>();\n" +
            " Map<String, CurrentLawVotes> votesMap = null;\n" +
            " List<Account> accounts = balances.entrySet().stream().map(t->t.getValue()).collect(Collectors.toList());\n" +
            " if(blocks.size() > limitBlocks){\n" +
            "votesMap = UtilsCurrentLaw.calculateVotes(accounts,blocks.subList(blocks.size() - limitBlocks, blocks.size()));\n" +
            " }else {\n" +
            "votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);\n" +
            "}\n" +
            "\n" +
            " for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {\n" +
            " if(votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())){\n" +
            " String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();\n" +
            " String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();\n" +
            " double vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votes(balances, BoardOfShareholders);\n" +
            "\n" +
            " CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(address, packageName, vote);\n" +
            "current.add(currentLawVotesEndBalance);\n" +
            "\n" +
            "}\n" +
            "}\n" +
            "return current;\n" +
            "}" +
            "" +
            "class CurrentLawVotes: voting mechanism" +
            "public double votes(Map<String, Account> balances, List<Account> pubChumbers){\n" +
            "double yes = 0.0;\n" +
            "double no = 0.0;\n" +
            " List<String> addressGovernment = pubChumbers.stream().map(t->t.getAccount()).collect(Collectors.toList());\n" +
            " for (String s : YES) {\n" +
            " if(addressGovernment.contains(s)){\n" +
            " yes += balances.get(s).getDigitalStockBalance() * Seting.POWER_SHAREHOLDER;\n" +
            " }else {\n" +
            " yes += balances.get(s).getDigitalStockBalance();\n" +
            "}\n" +
            "\n" +
            "}\n" +
            " for (String s : NO) {\n" +
            " if(addressGovernment.contains(s)){\n" +
            " no += balances.get(s).getDigitalStockBalance() * Seting.POWER_SHAREHOLDER;\n" +
            " }else {\n" +
            " no += balances.get(s).getDigitalStockBalance();\n" +
            "}\n" +
            "\n" +
            "}\n" +
            "\n" +
            "\n" +
            "return yes - no;\n" +
            "}";


    String CHAPTER_12 = "Any person or entity can mine, and all mined by mining " +
            "Digital dollars and digital stocks belong to the miner, just as the founder's reward belongs to the founder." +
            "For one Block Miner receives 200 digital dollars and 200 digital reputation, the founder's reward is 2% rounded" +
            "up to an integer.";

    String CHAPTER_13 = "Any positions that the International Trade Union may need, including departments and other structures" +
            "must be approved by the laws of the International Trade Union, as well as their powers." +
            "Any powers delegated to the Board of Shareholders, Special Positions or positions otherwise created," +
            "are in effect while the laws created through this cryptocurrency that determine these powers";

    String CHAPTER_14 = " (Applies to Special Positions and Board of Shareholders only)" +
            "Any remuneration of the Special Positions and the Board of Shareholders shall be determined by the laws in force." +
            "And be determined how they should receive, but due to the fact that this currency has a limited amount, then from the budget " +
            "for this Corporation, the remuneration should not be fixed, but percentage. (Example: Instead of a salary of 30 coins, " +
            "determine what percentage this amount takes from the budget and issue exactly the percentage, taking into account inflation and deflation, at the moment" +
            "issuing." +
            "example number two: The salary of a judge at the time of hiring was 20 coins, but at that time it was 0.003% of income" +
            "for this corporation, then monthly it will receive not 20 coins, but 0.003%, but this percentage.)" +
            "This protection is made for this reason, for example, bitcoin at the dawn of its formation was only a few cents." +
            "but as development progressed, the cost grew, if such a measure is not carried out, then the salary of one employee can grow by several" +
            "hundreds of times, which will lead to the bankruptcy of this Corporation." +
            "";
    String CHAPTER_15 = "Any cash or other money issued by the International Trade Union Corporation," +
            "must be 100% backed by a digital dollar or a digital share of that cryptocurrency, at a fixed rate." +
            "But the rate should be corrected every ten years, taking into account inflationary and deflationary processes, as well as market conditions." +
            "Who has the right to issue money backed by this cryptocurrency and correct" +
            "the course must be determined by the laws in force" +
            "and created special positions.";

    String CHAPTER_16 = "The International Trade Union Corporation has the right to set and collect commissions, duties (when " +
            "condition not higher than 20%);" +
            "Also a source of income for this corporation" +
            "can be the sale of their goods or services, as well as other sources of income (for example, the sale of subscriptions, membership fees, etc.)" +
            "the purpose of such measures is the issuance of dividends to shareholders and investors, financing the expenses of this Corporation," +
            "First of all" +
            "in investing in the development of its products and ecosystems, infrastructure development" +
            "of this Corporation, the maintenance of personnel, the payment of debts, it is also possible the maintenance of social and socially significant projects," +
            "such as affordable medicine, education (for the benefit of the entire common society), etc. on the territory of the Corporation of the International Trade Union." +
            " To promote and develop the general welfare throughout the territory of the Corporation of the International Trade Union - " +
            "problems that cannot be solved at the regional or local level, as well as problems that cannot be solved" +
            "Private small investments. Using economies of scale, you can reduce the cost of developing and implementing advanced technologies" +
            "as well as socially significant products and services on the territory of the Corporation of the International Trade Union, but any measures" +
            "which the International Trade Union Corporation decides to implement, including those listed above, must be approved by the current" +
            "by law, or delegated by law in force to the Board of Directors or other special higher offices" +
            "HigherSpecialPositions. "+
            ", all expenses of this Corporation shall be determined either by the board of directors or by applicable laws or special" +
            "current positions" +
            "to whom, through the laws in force, these powers have been transferred." +
            " " +
            "to whom these powers have been issued by the laws in force" +
            "All purposes of the International Trade Union Corporation must be determined by or through" +
            "the laws in force or by the board of directors or special officers whom, through the laws in force, them" +
            "These powers have been transferred. Infrastructure development for" +
            "conducting trade and providing services to members of the Corporation of the International Trade Union," +
            "and it is also possible to develop public and social benefits in the territory, if necessary and defined as goals, as" +
            "described above or any other purpose defined as described above in" +
            "Corporations of the International Trade Union." +
            "(An example of public and social goods, the production and sale of life-saving medicines at a low price, or the creation of parks, public transport," +
            "maintenance of sewers, etc." +
            "\n" +
            "Borrow money from the International Trade Union Corporation;\n" +
            "\n" +
            "To regulate trade with foreign countries and similar corporations, and provided that this provision is not " +
            "permits to regulate or prohibit any " +
            "non-commercial activity or any commercial activity, " +
            "which is limited to one part of the territory of the International Corporation" +
            "Trade Union, regardless of its effect outside the territory; but the Corporation of the International Trade Union" +
            "has the right to regulate harmful emissions between the territories controlled by the Corporation" +
            "International Trade Union, whatever their source;\n" +
            "\n" +
            "Establish uniform rules for naturalization and uniform bankruptcy laws throughout the territory of the International Corporation" +
            "The Trade Union, provided this is not construed as sanctioning legislation," +
            "forbidding entry to the territory of the Corporation of the International Trade Union of any person," +
            " who enters for peaceful, impregnable reasons, and who does not suffer from a contagious disease;\n" +
            "\n" +
            "Issue money and other currencies one hundred percent backed by the International Trade Union Corporation's cryptocurrency," +
            "regulate their value and foreign coinage, provided it doesn't" +
            "construed as permitting the conversion of any currency into legal tender; and establishing a standard of weights and measures;\n" +
            "\n" +
            "To provide for punishment for counterfeiting securities and the current coin or currency of the Corporation of the International Trade Union;\n" +
            "\n" +
            "Establish post offices and post roads on condition" +
            "that it won't beinterpreted as allowing for the establishment of some sort of postal monopoly;\n" +
            "\n" +
            "To promote the progress of science and the useful arts by granting to authors and inventors for a period not exceeding" +
            "28 years exclusive right to their respective works and discoveries;\n" +
            "\n" +

            "All applicable laws apply in the territory owned by the International Trade Union Corporation and to all members regardless of territory." +

            "The International Trade Union Corporation shall not impose on the members of the International Trade Union Corporation" +
            "or its political subdivision any obligations" +
            "or the obligation to make expenses, if such expenses will not be" +
            "completely reimbursed by the International Trade Union Corporation; Congress also must not bet" +
            "any conditions regarding the spending or receipt of the allocated funds," +
            "requiring members of the International Trade Union Corporation" +
            "or its political subdivision passing a law or regulation," +
            "restricting the freedoms of its citizens or otherwise affecting any powers (Since members can" +
            "to be also legal entities, this allows you to protect citizens of other companies, corporations, states from such actions.)," +
            " not within the competence of the Corporation of the International Trade Union. If a member refuses the allocated funds " +
            "with the conditions for their spending, the amount of appropriations, " +
            "proportionately distributed by the population of this member (If it is some kind of company, corporation, or state, etc.)," +
            "paid to the member as a lump-sum grant," +
            "which is spent on the general purposes of appropriations.";

    String CHAPTER_17 = "The habeas corpus privilege cannot be suspended," +
            "Except in cases where public safety may require it." +
            "No arrest bill or ex post facto law, whether civil or criminal, can be passed." +
            "The International Trade Union Corporation shall not make laws imposing or levying taxes on income," +
            "gifts or property," +
            " direct or poll taxes or taxes on aggregate consumption or expenditure; " +
            " but the International Trade Union Corporation has the right to charge a flat commission on the sale of goods or " +
            " services if this commission " +
            " no more than twenty percent (20%). " +
            "Any introduction or increase in a commission, duty, levy or excise requires that a law be made which will" +
            " act and which will describe this change." +

            "No regulation of trade or income should be given preference" +
            "to one member of the Corporation of the International Trade Union, among others.:" +
            "and ships or other modes of transport bound for or from one State(Members) or tribal lands," +
            "not required to call at ports. , clear, or pay duties in another." +

            "No money can be taken from the treasury, but due to appropriations," +
            "made by law, all such appropriations shall expire in two years;" +
            "and a regular report and report on the receipts and expenditures of all the Corporation of the International Trade Union of money" +
            "should be published from time to time, and be available to the Board of Shareholders. (This measure is needed to attract more people to this corporation," +
            "and make the management's actions transparent.)" +


            "The International Trade Union Corporation also cannot make any laws that do not apply" +
            "to himself or to his own members.";

    String CHAPTER_18 = "All accounts are subject to a negative rate. The negative rate for the digital dollar is 0.10%" +
            " every half year, and the negative rate for digital stocks is 0.2% every half year. " +
            " This part of the code is specified in class UtilsBalance calculateBalance:" +
            " if (i != 0 && i / Seting.COUNT_BLOCK_IN_DAY % (Seting.YEAR / Seting.HALF_YEAR) == 0.0) {\n" +
            "\n" +
            " for (Map.Entry<String, Account> changeBalance : balances.entrySet()) {\n" +
            " Account change = changeBalance.getValue();\n" +
            " change.setDigitalStockBalance(change.getDigitalStockBalance() - UtilsUse.countPercents(change.getDigitalStockBalance(), digitalReputationPercent));\n" +
            " change.setDigitalDollarBalance(change.getDigitalDollarBalance() - UtilsUse.countPercents(change.getDigitalDollarBalance(), percent));\n" +
            "}\n" +
            "}" +
            "" +
            "This measure was taken to avoid crises similar to the Great Depression, as well as to be ineffective and outdated" +
            " laws were easily removed from the system, and in order to motivate shareholders to get the support of society, a negative rate does not " +
            "it's too much to overvalue as a currency," +
            "So the value of the shares, which does not create situations like in other cryptocurrencies, where there is a very high volatility." +
            " This measure should not be changed by any amendments. (Explanation: When the digital dollar is subject to a negative rate of 0.10% every six months," +
            "this creates a price channel, where the lower limit of the coin is determined by its cost and the total amount in circulation, and the upper limit" +
            "is determined by the real value, as soon as the price becomes higher than the real market price, it becomes more profitable for participants to quickly sell this one" +
            "currency, and thereby saturate the market with money, this creates frequent market corrections, which does not create conditions for large collapses." +
            "But the effect will be noticeable the more active participants in the network, and the longer the system exists.)";

    String CHAPTER_19 = "Any law can be either canceled or renewed. If the law satisfies the condition of being in force" +
            "of the law, it is considered valid even if it was repealed not long ago, also if the law does not satisfy the conditions," +
            "it is considered repealed by law.";

    String CHAPTER_20 = "Settings" +
            "// value is used to calculate percentages\n" +
            "int HUNDRED_PERCENT = 100;\n" +
            " // value is used as year constant,\n" +
            " // there is no leap year in this system\n" +
            "int YEAR = 360;\n" +
            "\n" +
            " //delete sent transactions\n" +
            " String DELETED_SENDED_TRANSACTION_TIME = \"PT96H\";\n" +
            "\n" +
            "\n" +
            " //for what period of the last blocks should be taken into account for the selection of shareholders.\n" +
            " //Shareholders can only be those with the largest amount of balance\n" +
            " //senders and miners.\n" +
            " int BOARDS_BLOCK = (int) (Seting.COUNT_BLOCK_IN_DAY * YEAR);\n" +
            "\n" +
            " //Government vote multiplier for general account one share one vote, for members of parliament one reputation * 1.10\n" +
            "double POWER_SHAREHOLDER = 1.10;\n" +
            "\n" +
            " //minimum number of positive votes for the law to be valid,\n" +
            " int ORIGINAL_LIMIT_MIN_VOTE = (int) (200 * Seting.COUNT_BLOCK_IN_DAY * 45);\n" +
            "\n" +
            " // percentage that the founder receives from the production\n" +
            "Double FOUNDERS_REWARD = 2.0;\n" +
            "\n" +
            " //address for send rewards\n" +
            " String BASIS_ADDRESS = \"faErFrDnBhfSfNnj1hYjxydKNH28cRw1PBwDQEXH3QsJ\";\n" +

            "\n" +
            " //difficulty correction every n blocks\n" +
            " int DIFFICULTY_ADJUSTMENT_INTERVAL = (int) (Seting.COUNT_BLOCK_IN_DAY / 2);\n" +
            "int DIFFICULTY_ADJUSTMENT_INTERVAL_TEST = 10;\n" +
            "\n" +
            " long BLOCK_GENERATION_INTERVAL = Seting.BLOCK_TIME * 1000;// after Seting.BLOCK_TIME\n" +
            "long BLOCK_GENERATION_INTERVAL_TEST = 0 * 1000;\n" +
            "\n" +
            "long INTERVAL_TARGET = 600000;\n" +
            "long INTERVAL_TARGET_TEST = 25000;\n" +
            "\n" +
            " // maintenance fee every 6 months 0.4 /HALF_YEAR = 0.1, 0.4/HALF_YEAR = 0.2\n" +
            "Double ANNUAL_MAINTENANCE_FREE_DIGITAL_DOLLAR_YEAR = 0.2;\n" +
            " //negative rate for a digital stock\n" +
            "double ANNUAL_MAINTENANCE_FREE_DIGITAL_STOCK_YEAR = 0.4;\n" +
            " //every how many months to shoot\n" +
            "int HALF_YEAR = 2;\n" +
            "\n" +
            " //the cost of creating a law\n" +
            "double COST_LAW = 144;\n" +
            " //where does the law packet address begin\n" +
            " //corporation for short\n" +
            " String NAME_LAW_ADDRESS_START = \"LIBER\";\n" +
            "\n" +
            "int HASH_COMPLEXITY_GENESIS = 1;\n" +
            "\n" +
            " //shareholders' council\n" +
            "int BOARD_OF_SHAREHOLDERS = 1000;\n" +
            "\n" +
            "\n" +
            " // address of the founder: here will be my address. Now a stub\n" +
            " String ADDRESS_FOUNDER_TEST = \"stExZb8ifLfnFoq4JJuTifpAcscegATH8znhwW26zyTa\";\n" +
            " String ADDRESS_FOUNDER = \"stExZb8ifLfnFoq4JJuTifpAcscegATH8znhwW26zyTa\";\n" +
            "\n" +
            " String CORPORATE_CHARTER = International_Trade_Union.CorporateCharter.CorporateCharter.getAllConstitution() + \"\\n\" + CorporateCharter.getAllConstitution() + AboutUsDraft.getAboutUs()\n" +
            " + AboutUsEngDraft.getAboutUs();\n" +
            "\n" +
            " //founder's initial amount\n" +
            "Double FOUNDERS_REMUNERATION_DIGITAL_DOLLAR = 30000000.0;\n" +
            "double FOUNDERS_REMNUNERATION_DIGITAL_STOCK = 30000000.0;\n" +
            "\n" +
            "\n" +
            " // how many seconds are there in a day\n" +
            "int DAY_SECOND = 86400;\n" +
            "\n" +
            " // how many seconds each block is mined\n" +
            "int BLOCK_TIME = 150;\n" +
            "\n" +
            "\n" +
            " //how many blocks are mined per day\n" +
            "double COUNT_BLOCK_IN_DAY = (DAY_SECOND / BLOCK_TIME);\n" +
            "\n" +
            " //count of votes for the position in years\n" +
            " int POSITION_YEAR_VOTE = (int) Seting.COUNT_BLOCK_IN_DAY * YEAR * 3;\n" +
            " //count of votes for laws in years\n" +
            " int LAW_YEAR_VOTE = (int) Seting.COUNT_BLOCK_IN_DAY * YEAR * 3;\n" +
            "double DIGITAL_DOLLAR_REWARDS_BEFORE = 200.0;\n" +
            "double DIGITAL_STOCK_REWARDS_BEFORE = 200.0;\n" +
            " double DIGITAL_DOLLAR_FOUNDER_REWARDS_BEFORE = Math.round(UtilsUse.countPercents(Seting.DIGITAL_DOLLAR_REWARDS_BEFORE, Seting.FOUNDERS_REWARD));\n" +
            " double DIGITAL_REPUTATION_FOUNDER_REWARDS_BEFORE = Math.round(UtilsUse.countPercents(Seting.DIGITAL_STOCK_REWARDS_BEFORE, Seting.FOUNDERS_REWARD));";
}
