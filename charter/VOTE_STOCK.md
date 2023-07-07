# VOTE_STOCK (Как с помощью акций происходит голосование.)

Как с помощью акций происходит голосование. 
1. Количество акций приравнивается количеству голосов.
2. Ваши голоса пересчитываются каждый блок и если вы теряете свои акции,
или увеличиваете ваши акции, то ваши отданные голоса также изменяются
в соответствии с количеством акций.
3. За каждый закон который вы проголосовали, для данного закона подсчитываются все 
ЗА и ПРОТИВ и после чего с ЗА отнимается ПРОТИВ и это и есть рейтинг закона.
______

````
//код находится в классе class CurrentLawVotes method: votesLaw 
public double votesLaw(Map<String, Account> balances,
                           Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;


        //
        for (String s : YES) {

            yes += balances.get(s).getDigitalStockBalance();
        }
        //
        for (String s : NO) {
            no += balances.get(s).getDigitalStockBalance();

        }


        return yes - no;
    }
````

[возврат на главную](../documentation/documentationRus.md)