# POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES Judicial Power.
Approves the Chief Justice.
Participates in the voting on the introduction of amendments.

The judicial power of the International Trade Union Corporation is vested in
one Supreme Court and such inferior courts as the Corporation International
The Merchant Union may issue and establish from time to time.
Judges of both the supreme and inferior courts hold their offices, with good conduct and
in due time receive remuneration for their services.
Remuneration must be given from the budget established by laws.
Judicial power extends to all cases of law and justice,
including those initiated by members to challenge the misappropriation of funds,
arising under these Articles, the laws of the International Trade Union Corporation and treaties,
imprisoned or to be imprisoned according to their authority.
to controversy,
in which the International Trade Union will be party to a dispute between two or more members of the network.
No judgment shall be secret, but justice shall be administered openly and free of charge, completely and without delay,
and every person shall have legal protection against injury to life, liberty, or property.
Supreme Court CORPORATE_COUNCIL_OF_REFEREES and Chief Justice HIGH_JUDGE.

## How the Corporate Council of Judges is elected.
The Corporate Council of Judges consists of 55 accounts and is elected by the Network Members,
with the scoring system described in VOTE_STOCK, similar to Board of Directors and Factions.
The 55 accounts that received the most votes are selected.
![stock votes](../screenshots/stock_vote.png)
````
//minimum value for the number of positive votes for the law to be valid,
         //positions elected by shares CORPORATE_COUNCIL_OF_REFEREES
         List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()
                 .filter(t -> directors.isElectedByStocks(t.getPackageName()))
                 .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))
                 .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                 .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())
                 .collect(Collectors.toList());
````

Each score of such a judge is equal to one vote, similar to [ONE_VOTE](../charterEng/ONE_VOTE.md)

[Exit to home](../documentationEng/documentationEng.md)