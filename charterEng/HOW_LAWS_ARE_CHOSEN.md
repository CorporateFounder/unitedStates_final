# HOW LAWS ARE ELECTED.

## Approval of the law
_____

## CHARTER
No law has retroactive effect. No law shall violate any existing statute or be contrary to
other applicable laws. If there is a contradiction between several laws from one package of laws,
then the active one is the one that is higher in the list by index. Example: alcohol package
the law under index 3 contradicts the law under index 17, in this case the law under index three will be valid,
because he has a higher status.
If there are two or more adopted packages of laws that are valid but contradict each other,
then CORPORATE_COUNCIL_OF_REFEREES must resolve the conflict between them using case law.

If the law has been in force for a total of 12 years or more, then it must remain in force and can be repealed,
only by the same current law that was adopted after, or by the Supreme Court (CORPORATE_COUNCIL_OF_REFEREES)
who shall revoke it if it is contrary to the statute or by precedent.


If there are States or Private Jurisdictions that are part of this union,
then a senate should be formed, 5 senators should be elected from each country.
Also, each candidate must be elected according to such a system.
VOTE = N - 1. Where each person has a VOTE of votes that he can cast as
FOR AND AGAINST. He can also distribute these votes among several candidates
to the Senate, or give it to one. N is the number of Senators from the country. Thus,
if each country must provide 5 senators, then every citizen in that
the country has 4 votes 5-1=4.
Next, the rating must be calculated for each candidate, using the formula for the votes cast
FOR a given candidate, minus votes AGAINST = RATING result.
The 5 with the most rankings become senators from that state or private jurisdiction.
If there are three or more states, then any law on the territory of the states also
must be approved not only by the network mechanisms, but also by the Senate.


There are minimum requirements that all members of a given union must comply with (If this is a state
or private jurisdiction)
1. All participants must trade with each other only in this cryptocurrency (dollars or shares)
2. No member of this union should initiate aggression against members of this union.
3. Members of the union should not have the right to impose a form of management on each other.
4. All members of the union must recognize this charter as the most important law and laws also adopted
   board of directors and senate.
5. All citizens of a given union must have the right to freely cross the borders of members of this union.
6. Protectionist measures should not be applied against citizens of members of a given union and the members of the union themselves.


All laws are divided into several groups.
1. Ordinary laws
2. Strategic Plan
3. Appointed positions by the Legislature
4. Laws that create new positions. These positions are approved only by the Legislative Branch.
5. Amendments to the Charter
6. The charter itself


### ORDINARY LAWS
To establish ordinary laws,
1. The name of the law package should not coincide with the highlighted keywords.
2. The law must receive more than 1 vote according to the counting system described by [VOTE_STOCK](../charter/VOTE_STOCK.md)
3. Must receive a rating of 10 or more votes from members of the board of directors according to the scoring system described in [ONE_VOTE](../charter/ONE_VOTE.md)
4. If the founder vetoed the law,
   then for the law to bypass the founder, you need 2 or more votes from
   Council of Judges (rating) (ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES) according to the vote counting system
   [ONE_VOTE](../charter/ONE_VOTE.md)


Example code in LawsController current law:
````
       //laws must be approved by everyone.
         List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()
                 .filter(t -> !directors.contains(t.getPackageName()))
                 .filter(t -> !Setting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                 .filter(t -> !directors.isCabinets(t.getPackageName()))
                 .filter(t -> !Setting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))
                 .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))
                 .filter(t -> t.getVotesBoardOfDirectors() >= Setting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                         && t.getVotes() >= Setting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||
                         t.getVotesBoardOfDirectors() >= Setting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS &&
                                 t.getVotesCorporateCouncilOfReferees() > Setting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
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
 //план утверждается всеми
        List<CurrentLawVotesEndBalance> planFourYears = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.STRATEGIC_PLAN.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
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
     //позиции созданные всеми участниками
        List<CurrentLawVotesEndBalance> createdByBoardOfDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
        //добавление позиций созданных советом директоров
        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByBoardOfDirectors) {
            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());
        }

        //позиции избираемые только всеми участниками
        List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()
                .filter(t -> directors.isofficeOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))
                .filter(t ->
                        t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||
                        t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)
                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())
                .collect(Collectors.toList());


````

There are also positions that are created with the help of laws, these positions are also approved by the Legislative power.
For each such position, there is only one seat for each title.
The name of such packages starts with ADD_DIRECTOR_.
With the obligatory underscore.

````
//добавляет законы, которые создают новые должности утверждается всеми
        List<CurrentLawVotesEndBalance> addDirectors = current.stream()
                .filter(t -> t.getPackageName().startsWith(Seting.ADD_DIRECTOR))
                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS
                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)
                .collect(Collectors.toList());
````

### AMENDMENTS TO THE CHARTER
To amend the charter, the law package must be named AMENDMENT_TO_THE_CHARTER.
At least four weeks must pass after the vote for the amendment to be legitimate.
For an amendment to be considered valid
1. It is necessary that 35% or more votes are received from the Board of Shareholders by the [ONE_VOTE](../charter/ONE_VOTE.md) counting system.
   1. It is necessary to receive a rating of 40.2 or more votes from the board of directors [ONE_VOTE](../charter/ONE_VOTE.md).
2. Required to obtain 5 or more votes from the Legislative Branch of the Corporate Supreme Judges.

````
     //внедрение поправок в устав
        List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()
                .filter(t -> !directors.contains(t.getPackageName()))
                .filter(t -> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))
                .filter(t -> !directors.isCabinets(t.getPackageName()))
                .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT
                        && t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS_AMENDMENT
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