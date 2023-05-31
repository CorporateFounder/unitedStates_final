# String HOW_THE_CHIEF_JUDGE_IS_CHOSEN HOW HIGH_JUDGE IS CHOSEN.
The Chief Justice is elected by CORPORATE_COUNCIL_OF_REFEREES.
Each member of the network can apply for the position of Chief Justice by creating a law called
package that matches HIGH_JUDGE
position, where the address of the sender of this transaction must match the first line from the list of laws of this package.
The cost of the law is five digital dollars as a reward to the earner.
The account with the most remaining votes receives the position.
The voting mechanism is described in [ONE_VOTE](../charterEng/ONE_VOTE.md).
Elects the Chief Justice, Corporate Council of Judges. (CORPORATE_COUNCIL_OF_REFEREES)
Sample code as stated by the supreme judge. Class LawsController: method currentLaw. Code section

````
       //positions elected by the board of corporate chief judges
       List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()
                .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))
                .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList());
````

## Powers of Chief Justice
Chief Justice
can participate in resolving disputes within network members, like CORPORATE_COUNCIL_OF_REFEREES,
but his vote is higher than that of CORPORATE_COUNCIL_OF_REFEREES.

[Exit to home](../documentationEng/documentationEng.md)