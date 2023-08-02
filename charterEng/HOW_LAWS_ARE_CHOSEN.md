# HOW THE LAWS ARE CHOSEN.

## Approval of the law
_____
## CHARTER
No law is retroactive. No law shall violate the existing statute or be inconsistent with
other applicable laws. If there is a contradiction between several laws from one set of laws,
then the current one is the one that is higher in the index in the list. Example: alcohol sales package
the law under index 3 contradicts the law from index 17, in this case the law under index three will be valid,
because he is higher in status.
In the event of a conflict between several laws in force, the Judiciary shall give priority to those laws
which were adopted earlier, but should be taken into account precisely from the last date of adoption of the law.
The law is in force as long as it satisfies the conditions for the adoption of the law and as soon as the condition
violated, the law loses its force until the law is re-adopted.
In the voting of all laws, only votes cast in the last year are taken into account.

If there are States or Private Jurisdictions that are part of this union,
then a House of Representatives should be formed. The House of Representatives must be elected
as from multi-member districts, where each state must have at least one candidate, but
for every 40,000 voters, one candidate. Also, each candidate must be elected according to such a system.
Each voter has v = n - 1 votes, where n is the number of candidates for that constituency.
Each voter can vote YES or NO by casting their votes for candidates.
Further, for all participants, all the votes given for him are summed up according to the formula YES - NO,
the result is the rating. Places shall be allocated to the persons with the highest number of
rating. If there are states or private jurisdictions in the Union, then no law is
in force until it is also approved by the House of Representatives.

There are minimum requirements that all members of this union must comply with (If this state
or private jurisdiction)
1. All participants must trade among themselves only in this cryptocurrency (dollars or stocks)
2. No member of this alliance should initiate aggression against the members of this alliance.
3. Union members should not have the right to impose a form of government on each other (an exception is the method
   election of members of the House of Representatives, and other members must be observers that they were elected
   according to the rules described. The House of Representatives shall be elected as described in this system,
   citizens of these jurisdictions).
4. All members of the union must recognize this charter as the most important law and the laws also adopted
   factions and the House of Representatives.
5. All citizens of this union should have the right to freely cross the borders of the members of this union.
6. Protectionist measures should not be applied against the citizens of the members of this union and the members of the union themselves.



All laws are divided into several groups.
1. Ordinary laws
2. Strategic Plan
3. Appointed by the Legislature
4. Laws that create new positions. These positions are approved only by the Legislative Power.
5. Amendments to the Charter
6. The charter itself



NOTHING removes the vote from the candidate when voting.
### REGULAR LAWS
To establish ordinary laws,
1. The name of the package of law should not match the highlighted keywords.
2. The law must receive more than 1 vote according to the scoring system described by [VOTE_STOCK](../charterEng/VOTE_STOCK.md)
3. Must receive 15% or more votes from factions according to the scoring system described in [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)
4. If the founder vetoed the law, you need to get 15% or more votes from factions in the system
   count described in [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md) and 2 or more votes from
   Council of Judges (ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES) according to the vote counting system
   [ONE_VOTE](../charterEng/ONE_VOTE.md)


Sample code in LawsController current law:
````
      //laws must be approved by everyone.
         //laws must be approved by everyone.
         List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()
                 .filter(t -> !directors.contains(t.getPackageName()))
                 .filter(t->!Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                 .filter(t->!directors.isCabinets(t.getPackageName()))
                 .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                 .filter(t->!Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                 .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||
                 t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS &&
                         t.getVotesCorporateCouncilOfReferees() > Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());


````
### STRATEGIC PLAN.
The strategic plan is the general plan for the entire network and is approved in the same way as an ordinary law,
but there are some differences from ordinary laws.
1. The strategic plan package should be called STRATEGIC_PLAN
2. All plans that have been approved are sorted from highest to lowest by the number of votes,
   received from the Board of Directors.
3. After Sorting, only one PLAN with the most votes received from shares is selected.

````
//the plan is approved by everyone
         List<CurrentLawVotesEndBalance> planFourYears = current.stream()
                 .filter(t->!directors.contains(t.getPackageName()))
                 .filter(t->Seting.STRATEGIC_PLAN.equals(t.getPackageName()))
                 .filter(t->!directors.isCabinets(t.getPackageName()))
                 .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                       
                       
                         && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                 .limit(1)
                 .collect(Collectors.toList());
````


### POSTS THAT ARE APPOINTED ONLY BY THE LEGISLATIVE AUTHORITY
There are positions that are appointed only by the Legislature and such positions include
General Executive Director. This position is similar to the Prime Minister and is
Executive Power in this system.
Each such position can be limited to the number that is defined in this system.
for this position. Example: There is only one CEO position.
Elected in the same way as ***strategic plan***
But the number is determined for each position separately.
If the founder has vetoed a candidate for this position,
but it must also be approved by the council of judges and must receive 2 or more votes.
By system [ONE_VOTE](../charterEng/ONE_VOTE.md)
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

There are also positions that are created with the help of laws, these positions are also approved by the Legislative power.
For each such position, there is only one seat for each title.
The name of such packages starts with ADD_DIRECTOR_.
With the obligatory underscore.

### CHARTER AMENDMENTS
To amend the charter, the law package must be named AMENDMENT_TO_THE_CHARTER.
For an amendment to be valid
1. It is necessary that 35% or more of the votes received from the Council of Shareholders by the counting system [ONE_VOTE](../charterEng/VOTE_FRACTION.md).
2. Need to get 15% or more votes from factions by counting system [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md).
3. Need to get 5 or more votes from the Legislative Branch of the Corporate Chief Justices.



````
    //introduction of amendments to the charter
         List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()
                 .filter(t -> !directors.contains(t.getPackageName()))
                 .filter(t-> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                 .filter(t->!directors.isCabinets(t.getPackageName()))
                 .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT
                 && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS
                 && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());

````

### SAM CHARTER.
The first charter is approved by the founder and it is valid, the vote of the founder for approval
The charter never has an expiration date.
The charter package name starts with CHARTER_ORIGINAL and the source code name is CHARTER_ORIGINAL_CODE.
These two packages are a holistic charter, but in the first place, the source code must not contradict
the principles described in CHARTER_ORIGINAL.
````
// the charter is always valid, it is signed by the founder
         List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL = current.stream()
                 .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                 .filter(t->!directors.isCabinets(t.getPackageName()))
                 .filter(t->t.getFounderVote()>=1)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                 .limit(1)
                 .collect(Collectors.toList());

         // SOURCE CODE CREATED BY THE FOUNDER
         List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL_CODE = current.stream()
                 .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                 .filter(t->!directors.isCabinets(t.getPackageName()))
                 .filter(t->t.getFounderVote()>=1)
                 .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                 .limit(1)
                 .collect(Collectors.toList());
````
[Return to main page](../documentation/documentationEng.md)