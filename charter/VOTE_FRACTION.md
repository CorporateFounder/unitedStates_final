# VOTE_FRACTION 
Данная система голосования используется только для фракций.
Сначала отбираются 100 фракций, которые стали легитимными.
Дальше суммируется все голоса отданные 100 отобранным фракциям.
После чего определяется доля каждой фракции от общих количество 
голосов отданных за данную фракцию.
Количество голосов каждой фракции приравниваются ее доли в процентах.
Таким образом если фракция имеет 23% голосов от всех голосов, из
100 фракций, то ее голос приравнивается к 23%.

Дальше каждый раз когда фракция голосует за законы,
которые начинаются с LIBER (VoteEnum.YES) или (VoteEnum.NO).
У данного закона подсчитываются все голоса отданные ***за***
и ***против***, после чего отнимается от ***за*** - ***против***.
Именно этот результат отображается в процентах.

````
 //голос фракции
    public double voteFractions(Map<String, Double> fractions){
        double yes = 0;
        double no = 0;
        double sum = fractions.entrySet().stream()
                .map(t->t.getValue())
                .collect(Collectors.toList())
                .stream().reduce(0.0, Double::sum);

        for (String s : YES) {
            if (fractions.containsKey(s)) {
                yes += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;
            }

        }
        for (String s : NO) {
            if (fractions.containsKey(s)) {
                no += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;
            }

        }
        return yes - no;

    }

````

[Возврат на главную](../documentation/documentationRus.md)