# ONE_VOTE (Один Голос)

Когда голосуют данные должности, учитывается как один счет = один голос  
(CORPORATE_COUNCIL_OF_REFEREES-Совет Корпоративных Судей, 
 GENERAL_EXECUTIVE_DIRECTOR-Генеральный Исполнительный Директор,
HIGH_JUDGE-Верховный Судья и Совет Акционеров). 
Каждый счет который начинается с LIBER, учитывает все голоса ЗА (VoteEnum.YES) и ПРОТИВ (VoteEnum.NO) за него 
дальше отнимается от ЗА - ПРОТИВ = если остатков выше порога, то он становиться действующим законом. Но если избирается должности, 
то после сортируется от наибольшего к наименьшим и отбираются то количество наибольших, которое описано для данной должности. 
Перерасчет голосов происходит каждый блок. 

После голосования голос можно поменять только на противоположный. 
Ограничений на количество сколько раз можно поменять свой голос нет. Учитываются только те голоса которые даны счетами 
находящимися в своей должности, к примеру если счет перестал быть в CORPORATE_COUNCIL_OF_REFEREES, его голос как в качестве
CORPORATE_COUNCIL_OF_REFEREES не учитывается, и не будет учитываться в голосовании. Все голоса действуют, пока счета 
проголосовавшие находятся в своих должностях. Учитываются также только те голоса, от которых прошло не более 
четырех лет, но каждый участник, может в любой момент времени обновить свой голос. 

______

КОД class CurrentLawVotes: method voteGovernment

````
public int voteGovernment(
            Map<String, Account> balances,
            List<String> governments) {
       int yes = 0;
        int no = 0;

       List<String> addressGovernment = governments;
      for (String s : YES) {
            if (addressGovernment.contains(s)) {
                yes += Seting.VOTE_GOVERNMENT;
           }

        }
        for (String s : NO) {
          if (addressGovernment.contains(s)) {
                no += Seting.VOTE_GOVERNMENT;
           }
        }
       return yes - no;
   } 

````

[возврат на главную](../documentation/documentationRus.md)