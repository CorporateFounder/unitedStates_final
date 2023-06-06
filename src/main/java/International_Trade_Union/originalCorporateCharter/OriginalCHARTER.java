package International_Trade_Union.originalCorporateCharter;

public interface OriginalCHARTER {
    String HOW_LAWS_ARE_CHOSEN_1 = "# VOTE_FRACTION \n" +
            "Данная система голосования используется только для фракций.\n" +
            "Сначала отбираются 100 фракций, которые стали легитимными.\n" +
            "Дальше суммируется все голоса отданные 100 отобранным фракциям.\n" +
            "После чего определяется доля каждой фракции от общих количество \n" +
            "голосов отданных за данную фракцию.\n" +
            "Количество голосов каждой фракции приравниваются ее доли в процентах.\n" +
            "Таким образом если фракция имеет 23% голосов от всех голосов, из\n" +
            "100 фракций, то ее голос приравнивается к 23%.\n" +
            "От имени фракций, всегда выступают лидеры и из-за этого это \n" +
            "в первую очередь система лидеров. Одинаковые фракции с идеологической \n" +
            "системой здесь могут быть представлены разными лидерами, даже \n" +
            "если они из одного и того же сообщества.\n" +
            "\n" +
            "Дальше каждый раз когда фракция голосует за законы,\n" +
            "которые начинаются с LIBER (VoteEnum.YES) или (VoteEnum.NO).\n" +
            "У данного закона подсчитываются все голоса отданные ***за***\n" +
            "и ***против***, после чего отнимается от ***за*** - ***против***.\n" +
            "Именно этот результат отображается в процентах.\n" +
            "\n" +
            "````\n" +
            " //голос фракции\n" +
            "    public double voteFractions(Map<String, Double> fractions){\n" +
            "        double yes = 0;\n" +
            "        double no = 0;\n" +
            "        double sum = fractions.entrySet().stream()\n" +
            "                .map(t->t.getValue())\n" +
            "                .collect(Collectors.toList())\n" +
            "                .stream().reduce(0.0, Double::sum);\n" +
            "\n" +
            "        for (String s : YES) {\n" +
            "            if (fractions.containsKey(s)) {\n" +
            "                yes += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "        for (String s : NO) {\n" +
            "            if (fractions.containsKey(s)) {\n" +
            "                no += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "        return yes - no;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "````\n" +
            "\n" +
            "[Возврат на главную](../documentation/documentationRus.md)";

    String VOTE_STOCK_2 = "# VOTE_STOCK (Как с помощью акций происходит голосование.)\n" +
            "\n" +
            "Как с помощью акций происходит голосование. \n" +
            "Все акции которым счет владеет, приравниваются такому же количеству голосов. \n" +
            "Каждый раз когда кто-то делает транзакцию на счет, является адресом пакета который начинается с \n" +
            "LIBER он голосует по данному пакету. Учитываются только те голоса, с которых не прошло больше четырех лет. \n" +
            "Если транзакция была совершена ***VoteEnum.YES,*** то данный счет получает голоса ***за***, формуле \n" +
            "yesV = количество голосов равные количеству акций отправителя.\n" +
            "yesN = за сколько законов данный счет проголосовал с VoteEnum.YES\n" +
            "resultYES = yesV / yesN). Пример: счет проголосовал за три счета, которые начинаются с LIBER,\n" +
            "на счету сто акций, значит сто голосов. 100 / 3 = 33.3 значит каждый счет получит по 33.3 голоса. \n" +
            "\n" +
            "Если транзакция была совершена с VoteEnum.NO, \n" +
            "то используется такая же формула, но учитываются теперь все счета за которые он проголосовал против \n" +
            "пример тот же счет проголосовал за два счёта против, у него те же сто акций. \n" +
            "resultNO = noV / noN = 50 = 50 значит каждый счет за который он проголосовал, \n" +
            "против получит 50 голосов против. \n" +
            "Дальше каждый счет который начитается с LIBER подсчитывает и суммирует все отданные ему голоса ***ЗА*** (VoteEnum.YES)\n" +
            "и ***ПРОТИВ*** (VoteEnum.NO). \n" +
            "Потом используется данная формула remainder = resultYES - resultNO. \n" +
            "Именно этот результат и отображается как отданные голоса.\n" +
            "В любой момент можно изменить свой голос, но только на противоположный, что значит если \n" +
            "вы проголосовали за кандидата YES то вы можете изменить только на NO и обратно. \n" +
            "Количество раз сколько вы можете изменить свой голос не ограничено.\n" +
            "С каждым блоком происходит перерасчет голосов, если вы теряете свои акции, ваши кандидаты \n" +
            "также теряют свои голоса. Данная мера специально так реализовано чтобы избираемые должности \n" +
            "были заинтересованы в том чтобы те кто голосует за них, процветал и не теряли свои акции. \n" +
            "Таким способом избираются Только CORPORATE_COUNCIL_OF_REFEREES и BOARD_OF_DIRECTORS\n" +
            "Учитывается только последняя транзакция отданная за каждый счет, если вы не обновляли свой голос, \n" +
            "то по прошествии четырех лет он аннулируется.\n" +
            "Для Утверждения Закона нужно 100 тысяч голосов\n" +
            "\n" +
            "______\n" +
            "\n" +
            "````\n" +
            "//код находится в классе class CurrentLawVotes method: votesLaw \n" +
            "public double votesLaw(Map<String, Account> balances,\n" +
            "     Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {\n" +
            "        double yes = 0.0;\n" +
            "        double no = 0.0;\n" +
            "       \n" +
            "              \n" +
            "       for (String s : YES) {\n" +
            "\n" +
            "            int count = 1;\n" +
            "          count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;\n" +
            "          yes += balances.get(s).getDigitalStockBalance() / count;\n" +
            "\n" +
            "       }\n" +
            "        \n" +
            "        for (String s : NO) {\n" +
            "           int count = 1;\n" +
            "            count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;\n" +
            "           no += balances.get(s).getDigitalStockBalance() / count);\n" +
            "\n" +
            "        }\n" +
            "\n" +
            "\n" +
            "        return yes - no;\n" +
            "   } \n" +
            "\n" +
            "````\n" +
            "\n" +
            "[возврат на главную](../documentation/documentationRus.md)";

    String ONE_VOTE_3 = "# ONE_VOTE (Один Голос)\n" +
            "\n" +
            "Когда голосуют данные должности, учитывается как один счет = один голос  \n" +
            "(CORPORATE_COUNCIL_OF_REFEREES-Совет Корпоративных Судей, \n" +
            "BOARD_OF_DIRECTORS-Совет Директоров, GENERAL_EXECUTIVE_DIRECTOR-Генеральный Исполнительный Директор,\n" +
            "HIGH_JUDGE-Верховный Судья и Совет Акционеров). \n" +
            "Каждый счет который начинается с LIBER, учитывает все голоса ЗА (VoteEnum.YES) и ПРОТИВ (VoteEnum.NO) за него \n" +
            "дальше отнимается от ЗА - ПРОТИВ = если остатков выше порога, то он становиться действующим законом. Но если избирается должности, \n" +
            "то после сортируется от наибольшего к наименьшим и отбираются то количество наибольших, которое описано для данной должности. \n" +
            "Перерасчет голосов происходит каждый блок. \n" +
            "\n" +
            "После голосования голос можно поменять только на противоположный. \n" +
            "Ограничений на количество сколько раз можно поменять свой голос нет. Учитываются только те голоса которые даны счетами \n" +
            "находящимися в своей должности, к примеру если счет перестал быть в Совете Директор, его голос как в качестве \n" +
            "Совета Директоров не учитывается, и не будет учитываться в голосовании. Все голоса действуют, пока счета \n" +
            "проголосовавшие находятся в своих должностях. Учитываются также только те голоса, от которых прошло не более \n" +
            "четырех лет, но каждый участник, может в любой момент времени обновить свой голос. \n" +
            "\n" +
            "______\n" +
            "\n" +
            "КОД class CurrentLawVotes: method voteGovernment\n" +
            "\n" +
            "````\n" +
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
            "   } \n" +
            "\n" +
            "````\n" +
            "\n" +
            "[возврат на главную](../documentation/documentationRus.md)";

    String VOTE_FRACTION_4 = "# VOTE_FRACTION \n" +
            "Данная система голосования используется только для фракций.\n" +
            "Сначала отбираются 100 фракций, которые стали легитимными.\n" +
            "Дальше суммируется все голоса отданные 100 отобранным фракциям.\n" +
            "После чего определяется доля каждой фракции от общих количество \n" +
            "голосов отданных за данную фракцию.\n" +
            "Количество голосов каждой фракции приравниваются ее доли в процентах.\n" +
            "Таким образом если фракция имеет 23% голосов от всех голосов, из\n" +
            "100 фракций, то ее голос приравнивается к 23%.\n" +
            "От имени фракций, всегда выступают лидеры и из-за этого это \n" +
            "в первую очередь система лидеров. Одинаковые фракции с идеологической \n" +
            "системой здесь могут быть представлены разными лидерами, даже \n" +
            "если они из одного и того же сообщества.\n" +
            "\n" +
            "Дальше каждый раз когда фракция голосует за законы,\n" +
            "которые начинаются с LIBER (VoteEnum.YES) или (VoteEnum.NO).\n" +
            "У данного закона подсчитываются все голоса отданные ***за***\n" +
            "и ***против***, после чего отнимается от ***за*** - ***против***.\n" +
            "Именно этот результат отображается в процентах.\n" +
            "\n" +
            "````\n" +
            " //голос фракции\n" +
            "    public double voteFractions(Map<String, Double> fractions){\n" +
            "        double yes = 0;\n" +
            "        double no = 0;\n" +
            "        double sum = fractions.entrySet().stream()\n" +
            "                .map(t->t.getValue())\n" +
            "                .collect(Collectors.toList())\n" +
            "                .stream().reduce(0.0, Double::sum);\n" +
            "\n" +
            "        for (String s : YES) {\n" +
            "            if (fractions.containsKey(s)) {\n" +
            "                yes += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "        for (String s : NO) {\n" +
            "            if (fractions.containsKey(s)) {\n" +
            "                no += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "        return yes - no;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "````\n" +
            "\n" +
            "[Возврат на главную](../documentation/documentationRus.md)";

    String Penalty_mechanism_5 = "# Механизм штрафов\n" +
            "\n" +
            "Вы совершаете транзакцию, при которой, вы теряете данную сумму акций, но \n" +
            "и счет на который направлен штраф теряет такую сумму акций.\n" +
            "\n" +
            "Действует только на цифровые доллары.\n" +
            "![Вести штраф](../screenshots/lead_a_fine.png)\n" +
            "______\n" +
            "\n" +
            "## MECHANISM_FOR_REDUCING_THE_NUMBER_OF_SHARES  МЕХАНИЗМ СНИЖЕНИЯ КОЛИЧЕСТВА АКЦИЙ. Ввод штрафов. \n" +
            "Каждый раз когда один счет отправляет на другой счет цифровую акцию, но использует VoteEnum.NO, счет \n" +
            "цифровых акций получателя снижается на то количество которое отправил отправитель акций. \n" +
            "Пример счет А отправил на счет Б 100 цифровых акций с VoteEnum.NO, тогда счет А и счет Б оба теряют 100 \n" +
            "цифровых акций. Данная мера нужна чтобы был механизм снять с должности Совета акционеров и также позволяет снижать голоса \n" +
            "деструктивных счетов, так как количество голосов, равно количеству акций, при Избрании Совета Директоров и \n" +
            "при избрании CORPORATE_COUNCIL_OF_REFEREES. \n" +
            "Данный механизм действует только на цифровые акции и только в том случае, что отправитель совершил транзакцию с\n" +
            "VoteEnum.NO.\n" +
            "\n" +
            "[выход на главную](../documentation/documentationRus.md)";

    String WHO_HAS_THE_RIGHT_TO_CREATE_LAWS_6 = "# WHO_HAS_THE_RIGHT_TO_CREATE_LAWS Кто имеет права создавать законы\n" +
            "\n" +
            "String WHO_HAS_THE_RIGHT_TO_CREATE_LAWS = Кто имеет Права создавать законы.\n" +
            "Создавать законы в криптовалюте Корпорации Международный Торговый Союз имеют права \n" +
            "все участники сети, которые имеют минимум пять цифровых долларов. \n" +
            "Для создания закона через механизм криптовалюты Корпорации Международного торгового Союза \n" +
            "Нужно внутри данной криптовалюты Создать объект класса Laws, где packetLawName - является названием пакета законов. \n" +
            "List<String> laws - является списком законов, String hashLaw - является адресом данного пакета законов и начинается с LIBER. \n" +
            "Чтобы Закон попал в пул законов нужно создать транзакцию где получателем являться hashLaw данного закона и вознаграждение \n" +
            "майнера равно пять цифровых доллара (5)  данной криптовалюты. После этого как закон попадет в блок, он окажется в пуле \n" +
            "законов и за него можно будет голосовать. \n" +
            "Количество строк в пакете законов может быть столько, сколько понадобиться и нет никаких ограничений.\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)\n";

    String POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES_7 = "# POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES  Судебная Власть. \n" +
            "Утверждает Верховного судью.\n" +
            "Участвует в голосовании внедрения поправок. \n" +
            "\n" +
            "Судебная власть Корпорации Международного Торгового Союза принадлежит \n" +
            "одному Верховному суду и таким нижестоящим судам, которые Корпорация Международный \n" +
            "Торговый Союз может время от времени издавать и учреждать. \n" +
            "Судьи как верховных, так и нижестоящих судов занимают свои должности, при хорошем поведении и \n" +
            "в установленные сроки получают за свои услуги вознаграждение. \n" +
            "Вознаграждение должно даваться с бюджета, установленными законами.\n" +
            "Судебная власть распространяется на все дела по закону и справедливости, \n" +
            "в том числе инициированные членами для оспаривания незаконного расходования средств, \n" +
            "возникающего в соответствии с настоящем Уставом, законами Корпорации Международного Торгового Союза и договорами, \n" +
            "заключенными или которые будут заключены в соответствии с их авторитетом. \n" +
            "К спорам, \n" +
            "в которых Международный Торговый Союз будут стороной к разногласиям между двумя или более участников сети. \n" +
            "Ни один суд не должен быть тайным, но правосудие должно вершиться открыто и бесплатно, полностью и безотлагательно, \n" +
            "и каждый человек должен иметь правовую защиту от вреда, причиненного жизни, свободе или имуществу. \n" +
            "Верховный Суд CORPORATE_COUNCIL_OF_REFEREES и верховный судья HIGH_JUDGE.\n" +
            "\n" +
            "## Как избирается Корпоративный Совет Судей.\n" +
            "Корпоративный Совет судей состоит из 55 счетов и избирается Участниками сети,\n" +
            "с системой подсчета описанной в VOTE_STOCK, аналогично Совету Директоров и Фракциям.\n" +
            "Отбираются 55 счетов, которые получили наибольшее количество голосов.\n" +
            "![голоса акциями](../screenshots/stock_vote.png)\n" +
            "````\n" +
            "//минимальное значение количество положительных голосов для того чтобы закон действовал,\n" +
            "        //позиции избираемые акциями CORPORATE_COUNCIL_OF_REFEREES\n" +
            "        List<CurrentLawVotesEndBalance> electedByStockCorporateCouncilOfReferees = current.stream()\n" +
            "                .filter(t -> directors.isElectedByStocks(t.getPackageName()))\n" +
            "                .filter(t -> t.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()))\n" +
            "                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .limit(directors.getDirector(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString()).getCount())\n" +
            "                .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "Каждый счет такого судьи приравнивается одному голосу аналогично [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)";

    String HOW_THE_CHIEF_JUDGE_IS_CHOSEN_8 = "#  String HOW_THE_CHIEF_JUDGE_IS_CHOSEN КАК ИЗБИРАЕТСЯ ВЕРХОВНЫЙ СУДЬЯ HIGH_JUDGE.\n" +
            "Верховный Судья избирается CORPORATE_COUNCIL_OF_REFEREES. \n" +
            "Каждый участник сети может подать на должность Верховного Судьи, создав закон, с названием \n" +
            "пакета который совпадает с HIGH_JUDGE \n" +
            "должностью, где адрес отправителя данной транзакции должен совпадать с первой строкой из списка законов данного пакета. \n" +
            "Стоимость закона пять цифровых долларов в качестве вознаграждения добытчику.  \n" +
            "Счет с наибольшим количеством голосов остатка получает данную должность. \n" +
            "Механизм голосования описан [ONE_VOTE](../charter/ONE_VOTE.md). \n" +
            "Избирает Верховного Судью, Корпоративный Совет Судей. (CORPORATE_COUNCIL_OF_REFEREES) \n" +
            "Пример кода как утверждается верховный судья. Class LawsController: method currentLaw. Участок кода \n" +
            "\n" +
            "````\n" +
            "      //позиции избираемые советом корпоративных верховных судей\n" +
            "      List<CurrentLawVotesEndBalance> electedByCorporateCouncilOfReferees = current.stream()\n" +
            "               .filter(t -> directors.isElectedBYCorporateCouncilOfReferees(t.getPackageName()))\n" +
            "               .filter(t -> t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "               .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesCorporateCouncilOfReferees)).collect(Collectors.toList()); \n" +
            "````\n" +
            "\n" +
            "## Полномочия верховного судьи\n" +
            "Верховный судья\n" +
            "может участвовать в решении споров внутри членов сети, как и CORPORATE_COUNCIL_OF_REFEREES,\n" +
            "но его голос выше чем голос CORPORATE_COUNCIL_OF_REFEREES.\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)";

    String PROPERTY_OF_THE_CORPORATION_9 = "# PROPERTY_OF_THE_CORPORATION СОБСТВЕННОСТЬ КОРПОРАЦИИ.\n" +
            "Вся собственность которая принадлежит Корпорации Международного Торгового Союза,\n" +
            "не может быть продана без действующего закона, \n" +
            "где будет описан процесс продажи и по какой стоимости будет продана собственность.\n" +
            "Счет основателя, и счета других участников не является \n" +
            "счетом корпорации, Совет Директоров должен создать отдельный счет который\n" +
            "будет бюджетом и управляться только членами действующих членов Совета Директоров.\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)";

    String GENERAL_EXECUTIVE_DIRECTOR_10 = "# GENERAL_EXECUTIVE_DIRECTOR Генеральный Исполнительный Директор\n" +
            "Данный Директор координирует действия остальных высших директоров для реализации стратегического плана или \n" +
            "поставленных перед ним задач действующими законами. \n" +
            "Все полномочия должны быть ему выданы через действующие законы. \n" +
            "Это самая высокая должность избираемая Корпорацией и является по своей сути аналогом премьер-министра.\n" +
            "\n" +
            "## Как избирается Генеральный Исполнительный Директор\n" +
            "Данный директор избирается Законодательной властью\n" +
            "1. Совет Директоров должен дать больше 10 или больше голосов методом [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "2. Совет Акционеров должен дать больше 10 или больше голосов методом [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "3. Фракции должны дать 10% или больше голосов методом [VOTE_FRACTION](../charter/VOTE_FRACTION.md)\n" +
            "4. Участники сети должны дать больше одного голоса методом [VOTE_STOCK](../charter/VOTE_STOCK.md)\n" +
            "5. Дальше происходит сортировка от наибольшего к наименьшему полученых голосов от Совета Директоров и\n" +
            "6. Отбирается один счет с наибольшим количеством голосов от Совета Директоров\n" +
            "\n" +
            "````\n" +
            " //позиции избираемые только всеми участниками\n" +
            "        List<CurrentLawVotesEndBalance> electedByBoardOfDirectors = current.stream()\n" +
            "                .filter(t -> directors.isElectedByBoardOfDirectors(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t -> t.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                && t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS\n" +
            "                && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors).reversed())\n" +
            "                .collect(Collectors.toList());\n" +
            "                \n" +
            "                 //групируем по списку\n" +
            "        Map<String, List<CurrentLawVotesEndBalance>> group = electedByBoardOfDirectors.stream()\n" +
            "                .collect(Collectors.groupingBy(CurrentLawVotesEndBalance::getPackageName));\n" +
            "\n" +
            "        Map<Director, List<CurrentLawVotesEndBalance>> original_group = new HashMap<>();\n" +
            "\n" +
            "        //оставляем то количество которое описано в данной должности\n" +
            "        for (Map.Entry<String, List<CurrentLawVotesEndBalance>> stringListEntry : group.entrySet()) {\n" +
            "            List<CurrentLawVotesEndBalance> temporary = stringListEntry.getValue();\n" +
            "            temporary = temporary.stream()\n" +
            "                    .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotesBoardOfDirectors))\n" +
            "                    .limit(directors.getDirector(stringListEntry.getKey()).getCount())\n" +
            "                    .collect(Collectors.toList());\n" +
            "            original_group.put(directors.getDirector(stringListEntry.getKey()), temporary);\n" +
            "        }\n" +
            "\n" +
            "````\n" +
            "\n" +
            "````\n" +
            " public static List<CurrentLawVotesEndBalance> filtersVotes(\n" +
            "            List<LawEligibleForParliamentaryApproval> approvalList,\n" +
            "            Map<String, Account> balances,\n" +
            "            List<Account> BoardOfShareholders,\n" +
            "            List<Block> blocks,\n" +
            "            int limitBlocks\n" +
            "    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {\n" +
            "        //действующие законы чьи голоса больше ORIGINAL_LIMIT_MIN_VOTE\n" +
            "        List<CurrentLawVotesEndBalance> current = new ArrayList<>();\n" +
            "        Map<String, CurrentLawVotes> votesMap = null;\n" +
            "        List<Account> accounts = balances.entrySet().stream().map(t -> t.getValue()).collect(Collectors.toList());\n" +
            "        if (blocks.size() > limitBlocks) {\n" +
            "            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks.subList(blocks.size() - limitBlocks, blocks.size()));\n" +
            "        } else {\n" +
            "            votesMap = UtilsCurrentLaw.calculateVotes(accounts, blocks);\n" +
            "        }\n" +
            "\n" +
            "        //подсчитать средннее количество раз сколько он проголосовал за\n" +
            "        Map<String, Integer> yesAverage = UtilsCurrentLaw.calculateAverageVotesYes(votesMap);\n" +
            "        //подсчитать среднее количество раз сколько он проголосовал против\n" +
            "        Map<String, Integer> noAverage = UtilsCurrentLaw.calculateAverageVotesNo(votesMap);\n" +
            "\n" +
            "\n" +
            "        //подсчитываем голоса для для обычных законов и законов позиций\n" +
            "        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {\n" +
            "            if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {\n" +
            "                String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();\n" +
            "                String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();\n" +
            "                List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();\n" +
            "                double vote = 0;\n" +
            "                int supremeVotes = 0;\n" +
            "                int boafdOfShareholderVotes = 0;\n" +
            "                int houseOfRepresentativiesVotes = 0;\n" +
            "                int primeMinisterVotes = 0;\n" +
            "                int hightJudgesVotes = 0;\n" +
            "                int founderVote = 0;\n" +
            "                double fraction = 0;\n" +
            "\n" +
            "                //для законов подсчитываем специальные голоса\n" +
            "                vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votesLaw(balances, yesAverage, noAverage);\n" +
            "                List<String> boardOfShareholdersAddress = BoardOfShareholders.stream().map(t -> t.getAccount()).collect(Collectors.toList());\n" +
            "                boafdOfShareholderVotes = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, boardOfShareholdersAddress);\n" +
            "\n" +
            "                List<String> founder = List.of(Seting.ADDRESS_FOUNDER);\n" +
            "                founderVote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).voteGovernment(balances, founder);\n" +
            "                CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(\n" +
            "                        address,\n" +
            "                        packageName,\n" +
            "                        vote,\n" +
            "                        supremeVotes,\n" +
            "                        houseOfRepresentativiesVotes,\n" +
            "                        boafdOfShareholderVotes,\n" +
            "                        primeMinisterVotes,\n" +
            "                        hightJudgesVotes,\n" +
            "                        founderVote,\n" +
            "                        fraction,\n" +
            "                        laws);\n" +
            "                current.add(currentLawVotesEndBalance);\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        List<String> houseOfRepresentativies = new ArrayList<>();\n" +
            "        List<String> chamberOfSumpremeJudges = new ArrayList<>();\n" +
            "        Map<String, Double> fractions = new HashMap<>();\n" +
            "\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance: current) {\n" +
            "            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){\n" +
            "                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                    houseOfRepresentativies.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                }\n" +
            "\n" +
            "            }\n" +
            "            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.CORPORATE_COUNCIL_OF_REFEREES.toString())){\n" +
            "                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                    chamberOfSumpremeJudges.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                }\n" +
            "\n" +
            "            }\n" +
            "\n" +
            "\n" +
            "            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.FRACTION.toString())){\n" +
            "                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "                    fractions.put(currentLawVotesEndBalance.getLaws().get(0), currentLawVotesEndBalance.getVotes());\n" +
            "                }\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "\n" +
            "\n" +
            "\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "            if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){\n" +
            "\n" +
            "\n" +
            "                double vote = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).votesLaw(balances, yesAverage, noAverage);\n" +
            "                int supremeVotes  = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, chamberOfSumpremeJudges);\n" +
            "                int houseOfRepresentativiesVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, houseOfRepresentativies);\n" +
            "                double fractionsVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteFractions(fractions);\n" +
            "\n" +
            "                currentLawVotesEndBalance.setVotes(vote);\n" +
            "                currentLawVotesEndBalance.setVotesBoardOfDirectors(houseOfRepresentativiesVotes);\n" +
            "                currentLawVotesEndBalance.setVotesCorporateCouncilOfReferees(supremeVotes);\n" +
            "                currentLawVotesEndBalance.setFractionVote(fractionsVotes);\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "\n" +
            "        //изирается Генеральный исполнительный директор\n" +
            "        List<String> primeMinister = new ArrayList<>();\n" +
            "        List<String> hightJudge = new ArrayList<>();\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.GENERAL_EXECUTIVE_DIRECTOR.toString())){\n" +
            "                if(currentLawVotesEndBalance.getVotesBoardOfDirectors() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_DIRECTORS\n" +
            "                && currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && currentLawVotesEndBalance.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_BOARD_OF_SHAREHOLDERS\n" +
            "                && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE){\n" +
            "                    primeMinister.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                }\n" +
            "            }\n" +
            "\n" +
            "            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.HIGH_JUDGE.toString())){\n" +
            "                if(currentLawVotesEndBalance.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES){\n" +
            "                    hightJudge.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : current) {\n" +
            "            if(votesMap.containsKey(currentLawVotesEndBalance.getAddressLaw())){\n" +
            "                int primeMinisterVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, primeMinister);\n" +
            "                int hightJudgeVotes = votesMap.get(currentLawVotesEndBalance.getAddressLaw()).voteGovernment(balances, hightJudge);\n" +
            "\n" +
            "                currentLawVotesEndBalance.setVoteGeneralExecutiveDirector(primeMinisterVotes);\n" +
            "                currentLawVotesEndBalance.setVoteHightJudge(hightJudgeVotes);\n" +
            "            }\n" +
            "\n" +
            "        }\n" +
            "\n" +
            "\n" +
            "        return current;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "````\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)\n";

    String EXPLANATION_WHY_MONEY_DEMURAGE_IS_USED_HERE_11 = "without amendment will be 440.10 coins. But this amendment will be introduced only in ten\n" +
            "years after the first production adjustment)\n" +
            "\n" +
            "With a lack of money supply, if the number of mined coins has not been changed through an amendment, you can add a few\n" +
            "additional zeros after the decimal point, so it will simply increase the value of the coins, without increasing the total money supply.\n" +
            "\n" +
            "Negative rates should not be higher than 0.5% per annum and lower than 0.2% per annum. Negative rates can only be changed through amendments.";

    String FREEDOM_OF_SPEECH_12 = "# FREEDOM_OF_SPEECH Право свободы слова \n" +
            "Ни один орган данной корпорации или субъект не должен запрещать свободное исповедание \n" +
            "какой-либо религии; или ограничивать свободу слова, совести или печати\n" +
            "или право людей мирно собираться или объединяться друг с другом, или не объединяться друг с другом, и \n" +
            "обращаться к руководству Корпорации Международного Торгового Союза и к данной корпорации с ходатайством об удовлетворении жалоб; \" +\n" +
            "или нарушать право на плоды своего труда или \n" +
            "право на мирную жизнь по своему выбору. \n" +
            "Свободы слова и совести включают свободу вносить вклад в \n" +
            "политические кампании или кандидатуры на корпоративные должности и должны толковаться как \n" +
            "распространяющиеся в равной степени на любые средства коммуникации.\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)";

    String RIGHTS_13 = "# RIGHTS Естественные Права\n" +
            "Все члены сети, должны соблюдать Естественные Права Человека и не нарушать их. \n" +
            "\"Также должно соблюдаться презумпция невиновности и каждый участник сети должен иметь права на честное независимое \n" +
            "судебное разбирательство. \n" +
            "Каждый участник имеет права на адвоката или быть самому себе адвокатом.\n" +
            " \n" +
            "Корпорация Международный Торговый Союз не должна регулировать стоимость товаров и услуг участников сети, которые \n" +
            "продают через данную платформу. \n" +
            "Также Корпорация не должна запрещать отдельные бренды на своей площадке, но может \n" +
            "запрещать продавать целые группы товаров, которые попадают по характеристикам описанных действующими законами, если \n" +
            "этот запрет не нарушает Естественные Права Человека. В качестве источника прав можно брать \n" +
            "в качестве прецедента Страны признанные демократическими странами.  \n" +
            "\n" +
            "Детальный список есть в Организации Объединенных Наций (ООН)\n" +
            "\n" +
            "Право на жизнь\n" +
            "Право на свободу и личную неприкосновенность\n" +
            "Право на неприкосновенность частной жизни\n" +
            "Право определять и указывать свою национальную принадлежность\n" +
            "Право на пользование родным языком\n" +
            "Право на свободу передвижения и выбора места пребывания и жительства\n" +
            "Право на свободу совести\n" +
            "\n" +
            "Свобода мысли и слова\n" +
            "Свобода информации\n" +
            "Право на создание общественных объединений\n" +
            "Право на проведение публичных мероприятий\n" +
            "Право на участие в управлении делами Корпорации Международного Торгового Союза\n" +
            "Право на обращение в органы Корпорации Международного Торгового Союза и органы местного самоуправления.\n" +
            "\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)";

    String LEGISLATURE_14 = "#ЗАКОНОДАТЕЛЬНАЯ ВЛАСТЬ.\n" +
            "Власть состоит из 4 групп в данной системе.\n" +
            "1. Совет Акционеров\n" +
            "2. Совет Директоров\n" +
            "3. Фракции \n" +
            "4. Независимые участники сети.\n" +
            "\n" +
            "Все участники должны участвовать в голосовании чтобы был действителен закон принятый системой\n" +
            "(Исключением является только совет Акционеров, так как совет Акционеров участвует \n" +
            "только в утверждении поправок в Устав).\n" +
            "Для всех голосов учитываются только голоса отданные за последние четыре года.\n" +
            "Все участники могут занимать несколько должностей из разных групп, но не могут\n" +
            "занимать в одной категории должностей несколько мест.\n" +
            "Пример: Один счет может быть как ***Независимым участником сети*** и ***Членом Совета Директоров***\n" +
            "и ***Членом Совета Акционеров***, но один счет не сможет занять несколько мест в Совете Директоров\n" +
            "или в Совете Акционеров.\n" +
            "\n" +
            "Именно данная часть голоса учитывается при избрании Совета Директоров и Фракций.\n" +
            "![stock_vote](../screenshots/stock_vote.png)\n" +
            "## Совет Акционеров\n" +
            "Совет Акционеров назначается системой автоматически.\n" +
            "Совет Акционеров состоит из 1500 счетов с наибольшим количеством акций,\n" +
            "но отбираются только те счета, которые за последний год либо занимались добычей,\n" +
            "либо отправляли цифровые доллары или цифровые акции, или участвовали в голосовании.\n" +
            "Член одного Совета акционеров имеет один голос. Один счет равен одному голосу. \n" +
            "Используется система голосования описанная в [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "\n" +
            "````\n" +
            "  //определение совета акционеров\n" +
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
            "````\n" +
            "\n" +
            "## Совет Директоров \n" +
            "Совет директоров избирается участниками сети.\n" +
            "Совет Директоров состоит из 301 счетов которые получили наибольшее количество голосов\n" +
            "по системе описанной в [VOTE_STOCK](../charter/VOTE_STOCK.md). Каждый счет приравнивается одному голосу, описанному\n" +
            "в [ONE_VOTE](../charter/ONE_VOTE.md).\n" +
            "\n" +
            "````\n" +
            " //минимальное значение количество положительных голосов для того чтобы закон действовал,\n" +
            "        //позиции избираемые акциями совета директоров\n" +
            "        List<CurrentLawVotesEndBalance> electedByStockBoardOfDirectors = current.stream()\n" +
            "                .filter(t -> directors.isElectedByStocks(t.getPackageName()))\n" +
            "                .filter(t -> t.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString()))\n" +
            "                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .limit(directors.getDirector(NamePOSITION.BOARD_OF_DIRECTORS.toString()).getCount())\n" +
            "                .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "### Как подать на должность совета директоров\n" +
            "Сначала нужно зайти во вкладку в ***apply for a position*** Выбрать BOARD_OF_DIRECTORS\n" +
            "и заполнить все строки нужными данными.\n" +
            "![apply_board_of_directors](../screenshots/apply_board_or_directors.png)\n" +
            "\n" +
            "## Фракции.\n" +
            "Фракции избираются участниками сети. \n" +
            "Есть только 100 месть для фракций. Сто с наибольшим количеством голосов полученных по системе\n" +
            "описанной в [VOTE_STOCK](../charter/VOTE_STOCK.md) становиться фракцией. Голос каждой фракции приравнивается доли которую\n" +
            "она получила относительно 99 других фракций. Каждая фракция имеет голос описанный в [VOTE_FRACTION](../charter/VOTE_FRACTION.md).\n" +
            "\n" +
            "````\n" +
            "//избранные фракции\n" +
            "        List<CurrentLawVotesEndBalance> electedFraction = current.stream()\n" +
            "                .filter(t -> directors.isElectedByStocks(t.getPackageName()))\n" +
            "                .filter(t -> t.getPackageName().equals(NamePOSITION.FRACTION.toString()))\n" +
            "                .filter(t -> t.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .limit(directors.getDirector(NamePOSITION.FRACTION.toString()).getCount())\n" +
            "                .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "### Как создать новую фракцию\n" +
            "Для создания фракции нужно проделать ту же процедуру действий, что и для подачи на совет директоров.\n" +
            "![apply_fraction](../screenshots/apply_fraction.png)\n" +
            "\n" +
            "\n" +
            "##  Независимые Участники сети.\n" +
            "Все участники сети которые имеют акции и не входят в первые три выше перечисленные категории,\n" +
            "являются ***независимыми участниками сети***. Голоса каждого такого участника приравнивается\n" +
            "к количеству акций на данный момент и детально описано в [VOTE_STOCK](../charter/VOTE_STOCK.md).\n" +
            "\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)\n" +
            "\n";

    String FRACTION_15 = "# Создание фракции\n" +
            "## Как создаются фракции\n" +
            "Фракции создаются аналогично другим должностям, таким как Совет Директоров.\n" +
            "Нужно войти во вкладку ***apply for a position***, там выбрать \n" +
            "из выпадающего списка FRACTION. Дать вознаграждение 5 монет добытчику и\n" +
            "чтобы адреса отправителя и первой строки закона совпадали.\n" +
            "От имени фракций всегда выступают лидеры, таким образом вы всегда голосуете за лидера и\n" +
            "всегда может быть несколько идеологических идентичных фракций, которые возглавляют \n" +
            "разные лидеры. Вы должны воспринимать палату фракций как палату лидеров.\n" +
            "![apply_fraction](../screenshots/apply_fraction.png)\n" +
            "## В чем тогда отличие фракций.\n" +
            "Отличие фракций заключается в системе голосования, а именно когда отдает свой голос,\n" +
            "член Совета Директоров или член Совета Акционеров, то один счет приравнивается одному голосу.\n" +
            "В то же время голос фракции, равен доле голосов которые он получил.\n" +
            "Для этого суммируется голоса всех 100 фракций, и каждый потом определяется доля каждой фракции.\n" +
            "Пример: если ваша фракция получила, 23% доли, то голос будет равен 23%.\n" +
            "Детально прописано в [VOTE_FRACTION](../charterEng/VOTE_FRACTION.md)\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)\n";


}
