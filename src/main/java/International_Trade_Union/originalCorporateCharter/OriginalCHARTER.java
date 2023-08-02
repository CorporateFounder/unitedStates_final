package International_Trade_Union.originalCorporateCharter;

public interface OriginalCHARTER {
    String LAW_1 = "# КАК ИЗБИРАЮТСЯ ЗАКОНЫ. \n" +
            "\n" +
            "## Утверждение закона\n" +
            "_____\n" +
            "\n" +
            "## УСТАВ\n" +
            "Ни один закон не имеет обратной силы. Ни один закон не должен нарушать действующий устав или противоречит \n" +
            "другим действующим законам. Если есть противотечение между несколькими законами из одного пакета законов, \n" +
            "то действующим является тот который в списке находится выше по индексу. Пример: пакет по продаже алкоголя \n" +
            "закон под индексом 3 противоречит закону из индекса 17, в данном случае закон под индексом три будет действующим, \n" +
            "так как он более высокая по статусу. \n" +
            "В случае противоречия нескольких действующих Законов Судебная Власть должна отдавать приоритет, тем законам,\n" +
            "которые были приняты ранее, но учитывать должны именно с последней даты принятия закона.\n" +
            "Закон является Действующим, пока удовлетворяет условиям принятия закона и как только условие \n" +
            "нарушено, закон теряет свою силу, до повторного принятия закона.\n" +
            "В голосовании всех законов, учитываются только голоса отданные за последний год.\n" +
            "\n" +
            "Если есть Государства или Частные юрисдикции, которые входят в состав данного союза,\n" +
            "то должно быть сформировано палата представителей. Палата представителей должна избираться\n" +
            "как из многомандатных округов, где каждый штат должен иметь не меньше одного кандидата, но\n" +
            "на каждые 40 тысяч избирателей, по кандидату. Также избираться каждый кандидат должен по системе такой системе.\n" +
            "Каждый избиратель иметь v = n - 1 голосов, где n это количество кандидатов на данный округ. \n" +
            "Каждый избиратель может проголосовать ДА или НЕТ отдав свои голоса за кандидатов.\n" +
            "Дальше для всех участников суммируется все голоса отданные за него по формуле ДА - НЕТ,\n" +
            "результат является рейтинг. Места должны быть распределены лицам получившим наибольшее количество\n" +
            "рейтинга. Если Есть в составе Союза государства или частные юрисдикции, то ни один закон не является\n" +
            "действующим, пока не будет одобрен также палатой представителей.\n" +
            "\n" +
            "Есть минимальные требования которые должны соблюдать все члены данного союза (Если это государство \n" +
            "или частная юрисдикция)\n" +
            "1. Все участники должны вести торговлю между собой только в данной криптовалюте (доллары или акции)\n" +
            "2. Ни один участник данного союза не должен инициировать агрессию членам данного союза.\n" +
            "3. Члены союза не должны иметь права навязывать друг другу форму управления (исключением является способ\n" +
            "избрания членов палаты представителей, и другие участники должны быть наблюдателями, что их избирали\n" +
            "в соответствии описанным правилам. Палата представителей должна избираться как описано в данной системе,\n" +
            "гражданами данных юрисдикций).\n" +
            "4. Все члены союза должны признавать данный устав как самый главный закон и законы также принятые \n" +
            "фракциями и палатой представителей.\n" +
            "5. Все граждане данного союза должны иметь права свободно пересекать границы членов данного союза.\n" +
            "6. Не должны применяться протекционистские меры против граждан членов данного союза и самих членов союза.\n" +
            "\n" +
            "\n" +
            "\n" +
            "Все законы делятся на несколько групп.\n" +
            "1. Обычные законы\n" +
            "2. Стратегический План\n" +
            "3. Назначаемые должности Законодательной властью\n" +
            "4. Законы, которые создают новые должности. Данные должности утверждаются только Законодательной Властью.\n" +
            "5. Поправки в Устав\n" +
            "6. Сам устав\n" +
            "\n" +
            "\n" +
            "\n" +
            "NOTHING снимает голос с кандидата при голосовании.\n" +
            "### ОБЫЧНЫЕ ЗАКОНЫ\n" +
            "Чтобы утвердить обычные законы, \n" +
            "1. название пакета закона не должно совпадать с выделенными ключевыми словами.\n" +
            "2. Закон должен получить больше 1 голоса по системе подсчета описанной [VOTE_STOCK](../charter/VOTE_STOCK.md)\n" +
            "3. Должен получить 15% или больше голосов от фракций по системе подсчета описанной в [VOTE_FRACTION](../charter/VOTE_FRACTION.md)\n" +
            "4. Если основатель наложил вето на закон нужно получить 15% или больше голосов от фракций по системе\n" +
            "подсчета описанной в [VOTE_FRACTION](../charter/VOTE_FRACTION.md) и 2 или больше голоса от\n" +
            "совета судей (ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES) по системе подсчета голосов \n" +
            "[ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "\n" +
            "\n" +
            "Пример кода в LawsController current law:\n" +
            "````\n" +
            "     //законы должны быть одобрены всеми.\n" +
            "        //законы должны быть одобрены всеми.\n" +
            "        List<CurrentLawVotesEndBalance> notEnoughVotes = current.stream()\n" +
            "                .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                .filter(t->!Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t -> !Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            "                .filter(t->!Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))\n" +
            "                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS &&\n" +
            "                        t.getVotesCorporateCouncilOfReferees() > Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            "\n" +
            "````\n" +
            "\n" +
            "### СТРАТЕГИЧЕСКИЙ ПЛАН.\n" +
            "Стратегический план является общим планом для всей сети и утверждается аналогично обычному закону,\n" +
            "но есть некоторые отличия от обычных законов.\n" +
            "1. Пакет стратегического плана должен называться STRATEGIC_PLAN\n" +
            "2. Все планы которые прошли одобрение, сортируется от наибольшего к наименьшему по количеству голосов,\n" +
            "полученных от Совета Директоров.\n" +
            "3. После Сортировки отбираются только один ПЛАН с наибольшим количеством голосов полученных от акций.\n" +
            "\n" +
            "````\n" +
            "//план утверждается всеми\n" +
            "        List<CurrentLawVotesEndBalance> planFourYears = current.stream()\n" +
            "                .filter(t->!directors.contains(t.getPackageName()))\n" +
            "                .filter(t->Seting.STRATEGIC_PLAN.equals(t.getPackageName()))\n" +
            "                .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                       \n" +
            "                       \n" +
            "                        && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .limit(1)\n" +
            "                .collect(Collectors.toList());\n" +
            "````\n" +
            "\n" +
            "### ДОЛЖНОСТИ КОТОРЫЕ НАЗНАЧАЮТСЯ ТОЛЬКО ЗАКОНОДАТЕЛЬНОЙ ВЛАСТЬЮ\n" +
            "Есть должности которые назначаются только Законодательной властью и таким должностям относиться\n" +
            "Генеральный Исполнительный Директор. Данная должность аналогична премьер-министру и является\n" +
            "Исполнительной Властью в данной системе.\n" +
            "Каждая такая должность может быть ограничена количеством, которое определено в данной системе\n" +
            "для данной должности. Пример: Генеральный Исполнительный Директор есть только одно место.\n" +
            "Избирается аналогично как ***стратегический план*** \n" +
            "Но количество определяется для каждой должности отдельно.\n" +
            "Если основатель наложил вето на кандидата на данную должность,\n" +
            "но его должен также одобрить совет судей и он должен получить 2 или более голосов.\n" +
            "По системе [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "````\n" +
            "  //позиции созданные всеми участниками\n" +
            "        List<CurrentLawVotesEndBalance> createdByFraction = current.stream()\n" +
            "                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            "                .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                .collect(Collectors.toList());\n" +
            "        //добавление позиций созданных советом директоров\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByFraction) {\n" +
            "            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());\n" +
            "        }\n" +
            "\n" +
            "        //позиции избираемые только всеми участниками\n" +
            "        List<CurrentLawVotesEndBalance> electedByFractions = current.stream()\n" +
            "                .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                        t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS \n" +
            "                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .collect(Collectors.toList());\n" +
            "\n" +
            "````\n" +
            "\n" +
            "Также есть должности которые создаются с помощью законов, данные должности утверждаются тоже Законодательной властью.\n" +
            "Для каждой такой должности только одно место, для каждого названия. \n" +
            "Название таких пакетов начинается с ADD_DIRECTOR_.\n" +
            "С обязательным нижним подчеркиванием.\n" +
            "\n" +
            "### ПОПРАВКИ В УСТАВ\n" +
            "Чтобы внести поправки в устав, нужно чтобы пакет закона должен называться AMENDMENT_TO_THE_CHARTER.\n" +
            "Должно пройти не менее четырех недель после голосования чтобы поправка была легитимной.\n" +
            "Для того чтобы поправка считалась действующей\n" +
            "1. Нужно чтобы 35% или больше голосов получила от Совета Акционеров системой подсчета [ONE_VOTE](../charter/ONE_VOTE.md).\n" +
            "2. Нужно, чтобы получить 15% или больше голосов от фракций системой подсчета [VOTE_FRACTION](../charter/VOTE_FRACTION.md).\n" +
            "3. Нужно, чтобы получить 5 или больше голосов от Законодательной Власти Корпоративных Верховных Судей.\n" +
            "\n" +
            "\n" +
            "\n" +
            "````\n" +
            "   //внедрение поправок в устав\n" +
            "        List<CurrentLawVotesEndBalance> chapter_amendment = current.stream()\n" +
            "                .filter(t -> !directors.contains(t.getPackageName()))\n" +
            "                .filter(t-> Seting.AMENDMENT_TO_THE_CHARTER.equals(t.getPackageName()))\n" +
            "                .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t -> t.getVotesBoardOfShareholders() >= Seting.ORIGINAL_LIMIT_MINT_VOTE_BOARD_OF_SHAREHOLDERS_AMENDMENT\n" +
            "                && t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES_AMENDMENT)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed()).collect(Collectors.toList());\n" +
            "\n" +
            "````\n" +
            "\n" +
            "### САМ УСТАВ.\n" +
            "Первый устав утверждается основателем и он является действующим, голос основателя для утверждения\n" +
            "устава никогда не имеет срока годности.\n" +
            "Название пакета устава начинается с CHARTER_ORIGINAL и название исходного кода CHARTER_ORIGINAL_CODE.\n" +
            "Эти два пакета и являются целостным уставом, но в первую очередь, исходный код не должен противоречить\n" +
            "принципам описанным в CHARTER_ORIGINAL.\n" +
            "````\n" +
            "//устав всегда действующий он подписан основателем\n" +
            "        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL = current.stream()\n" +
            "                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_LAW_PACKAGE_NAME.equals(t.getPackageName()))\n" +
            "                .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t->t.getFounderVote()>=1)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .limit(1)\n" +
            "                .collect(Collectors.toList());\n" +
            "\n" +
            "        //ИСХОДНЫЙ КОД СОЗДАННЫЙ ОСНОВАТЕЛЕМ\n" +
            "        List<CurrentLawVotesEndBalance> CHARTER_ORIGINAL_CODE = current.stream()\n" +
            "                .filter(t -> !directors.contains(t.getPackageName()) && Seting.ORIGINAL_CHARTER_CURRENT_ALL_CODE.equals(t.getPackageName()))\n" +
            "                .filter(t->!directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t->t.getFounderVote()>=1)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .limit(1)\n" +
            "                .collect(Collectors.toList());\n" +
            "````\n" +
            "[Возврат на главную](../documentation/documentationRus.md)";

    String LAW_2 = "# VOTE_STOCK (Как с помощью акций происходит голосование.)\n" +
            "\n" +
            "Как с помощью акций происходит голосование. \n" +
            "1. Количество акций приравнивается количеству голосов.\n" +
            "2. Ваши голоса пересчитываются каждый блок и если вы теряете свои акции,\n" +
            "или увеличиваете ваши акции, то ваши отданные голоса также изменяются\n" +
            "в соответствии с количеством акций.\n" +
            "3. За каждый закон который вы проголосовали, для данного закона подсчитываются все \n" +
            "ЗА и ПРОТИВ и после чего с ЗА минус ПРОТИВ и это и есть рейтинг закона.\n" +
            "4. Ваши голоса делятся отдельно за все законы которые вы проголосовали ЗА и отдельно ПРОТИВ\n" +
            "Пример: у вас 100 акций и вы проголосовали ЗА одного кандидата и за один закон,\n" +
            "также вы проголосовали ПРОТИВ двух кандидатов и двух законов.\n" +
            "Теперь каждый ваш кандидат и закон за которых вы проголосовали ЗА-получат по 50 голосов.\n" +
            "а за которых вы проголосовали ПРОТИВ, получат по 25 голосов ПРОТИВ.\n" +
            "формула простая ЗА/количество законов и ПРОТИВ/количество законов против которых вы.\n" +
            "______\n" +
            "\n" +
            "````\n" +
            "public double votesLaw(Map<String, Account> balances,\n" +
            "                           Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {\n" +
            "        double yes = 0.0;\n" +
            "        double no = 0.0;\n" +
            "\n" +
            "\n" +
            "        //\n" +
            "        for (String s : YES) {\n" +
            "\n" +
            "            int count = 1;\n" +
            "            count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;\n" +
            "            yes += balances.get(s).getDigitalStockBalance() / count;\n" +
            "\n" +
            "        }\n" +
            "        //\n" +
            "        for (String s : NO) {\n" +
            "            int count = 1;\n" +
            "            count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;\n" +
            "            no += balances.get(s).getDigitalStockBalance() / count;\n" +
            "\n" +
            "        }\n" +
            "\n" +
            "\n" +
            "        return yes - no;\n" +
            "    }\n" +
            "````\n" +
            "\n" +
            "[возврат на главную](../documentation/documentationRus.md)";

    String LAW_3 = "# ONE_VOTE (Один Голос)\n" +
            "\n" +
            "Когда голосуют данные должности, учитывается как один счет = один голос  \n" +
            "(CORPORATE_COUNCIL_OF_REFEREES-Совет Корпоративных Судей, \n" +
            " GENERAL_EXECUTIVE_DIRECTOR-Генеральный Исполнительный Директор,\n" +
            "HIGH_JUDGE-Верховный Судья и Совет Акционеров). \n" +
            "Каждый счет который начинается с LIBER, учитывает все голоса ЗА (VoteEnum.YES) и ПРОТИВ (VoteEnum.NO) за него \n" +
            "дальше отнимается от ЗА - ПРОТИВ = если остатков выше порога, то он становиться действующим законом. Но если избирается должности, \n" +
            "то после сортируется от наибольшего к наименьшим и отбираются то количество наибольших, которое описано для данной должности. \n" +
            "Перерасчет голосов происходит каждый блок. \n" +
            "\n" +
            "После голосования голос можно поменять только на противоположный. \n" +
            "Ограничений на количество сколько раз можно поменять свой голос нет. Учитываются только те голоса которые даны счетами \n" +
            "находящимися в своей должности, к примеру если счет перестал быть в CORPORATE_COUNCIL_OF_REFEREES, его голос как в качестве\n" +
            "CORPORATE_COUNCIL_OF_REFEREES не учитывается, и не будет учитываться в голосовании. Все голоса действуют, пока счета \n" +
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
    String LAW_4 = "# ELECTED_FRACTION\n" +
            "Фракция избирается аналогично как и верховные судьи, 200 счетов получивших наибольшее количество голосов\n" +
            "от независимых избирателей, как ранее и говорилось одна акция равна одному голосу описанную\n" +
            "в VOTE_STOCK\n" +
            "\n" +
            "# VOTE_FRACTION \n" +
            "Данная система голосования используется только для фракций.\n" +
            "Сначала отбираются 200 фракций, которые стали легитимными.\n" +
            "Дальше суммируется все голоса отданные 200 отобранным фракциям.\n" +
            "После чего определяется доля каждой фракции от общих количество \n" +
            "голосов отданных за данную фракцию.\n" +
            "Количество голосов каждой фракции приравниваются ее доли в процентах.\n" +
            "Таким образом если фракция имеет 23% голосов от всех голосов, из\n" +
            "200 фракций, то ее голос приравнивается к 23%.\n" +
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
    String LAW_5 = "# Механизм штрафов\n" +
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
            "деструктивных счетов, так как количество голосов, равно количеству акций, \n" +
            "при избрании CORPORATE_COUNCIL_OF_REFEREES, Фракций и других должностей которые избираются акциями. \n" +
            "Данный механизм действует только на цифровые акции и только в том случае, что отправитель совершил транзакцию с\n" +
            "VoteEnum.NO.\n" +
            "\n" +
            "[выход на главную](../documentation/documentationRus.md)";
    String LAW_6 = "# WHO_HAS_THE_RIGHT_TO_CREATE_LAWS Кто имеет права создавать законы\n" +
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
    String LAW_7 = "# POWERS_OF_THE_CORPORATE_COUNCIL_OF_JUDGES  Судебная Власть. \n" +
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
    String LAW_8 = "#  String HOW_THE_CHIEF_JUDGE_IS_CHOSEN КАК ИЗБИРАЕТСЯ ВЕРХОВНЫЙ СУДЬЯ HIGH_JUDGE.\n" +
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
    String LAW_9 = "# PROPERTY_OF_THE_CORPORATION СОБСТВЕННОСТЬ КОРПОРАЦИИ.\n" +
            "Вся собственность которая принадлежит Корпорации Международного Торгового Союза,\n" +
            "не может быть продана без действующего закона, \n" +
            "где будет описан процесс продажи и по какой стоимости будет продана собственность.\n" +
            "Счет основателя, и счета других участников не является \n" +
            "счетом корпорации, Совет Директоров должен создать отдельный счет который\n" +
            "будет бюджетом и управляться только членами действующих членов Совета Директоров.\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)";
    String LAW_10 = "# GENERAL_EXECUTIVE_DIRECTOR Генеральный Исполнительный Директор\n" +
            "Данный Директор координирует действия остальных высших директоров для реализации стратегического плана или \n" +
            "поставленных перед ним задач действующими законами. \n" +
            "Все полномочия должны быть ему выданы через действующие законы. \n" +
            "Это самая высокая должность избираемая Корпорацией и является по своей сути аналогом премьер-министра.\n" +
            "\n" +
            "## Как избирается Генеральный Исполнительный Директор\n" +
            "Данный директор избирается Законодательной властью\n" +
            "3. Фракции должны дать 15% или больше голосов методом [VOTE_FRACTION](../charter/VOTE_FRACTION.md)\n" +
            "4. Участники сети должны дать больше одного голоса методом [VOTE_STOCK](../charter/VOTE_STOCK.md)\n" +
            "5. Дальше происходит сортировка от наибольшего к наименьшему полученных голосов от акций и\n" +
            "6. Отбирается один счет с наибольшим количеством голосов полученных от фракций.\n" +
            "7. Если основатель наложил вето на данного кандидата, тогда нужно получить \n" +
            "голоса фракций 15% или больше по системе [VOTE_FRACTION](../charter/VOTE_FRACTION.md)\n" +
            "и голоса совета судей 2 или больше голосов по системе [ONE_VOTE](../charter/ONE_VOTE.md)\n" +
            "\n" +
            "````\n" +
            "  //позиции созданные всеми участниками\n" +
            "        List<CurrentLawVotesEndBalance> createdByFraction = current.stream()\n" +
            "                .filter(t->t.getPackageName().startsWith(Seting.ADD_DIRECTOR))\n" +
            "                .filter(t->t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotes() >= Seting.ALL_STOCK_VOTE)\n" +
            "                .collect(Collectors.toList());\n" +
            "        //добавление позиций созданных советом директоров\n" +
            "        for (CurrentLawVotesEndBalance currentLawVotesEndBalance : createdByFraction) {\n" +
            "            directors.addAllByBoardOfDirectors(currentLawVotesEndBalance.getLaws());\n" +
            "        }\n" +
            "\n" +
            "        //позиции избираемые только всеми участниками\n" +
            "        List<CurrentLawVotesEndBalance> electedByFractions = current.stream()\n" +
            "                .filter(t -> directors.isElectedByFractions(t.getPackageName()) || directors.isCabinets(t.getPackageName()))\n" +
            "                .filter(t -> t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotes() >= Seting.ALL_STOCK_VOTE && t.getFounderVote() >= 0 ||\n" +
            "                        t.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && t.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES)\n" +
            "                .sorted(Comparator.comparing(CurrentLawVotesEndBalance::getVotes).reversed())\n" +
            "                .collect(Collectors.toList());\n" +
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
            "//            if(currentLawVotesEndBalance.getPackageName().equals(NamePOSITION.BOARD_OF_DIRECTORS.toString())){\n" +
            "//                if(currentLawVotesEndBalance.getVotes() >= Seting.ORIGINAL_LIMIT_MIN_VOTE){\n" +
            "//                    houseOfRepresentativies.add(currentLawVotesEndBalance.getLaws().get(0));\n" +
            "//                }\n" +
            "//\n" +
            "//            }\n" +
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
            "                if(currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && currentLawVotesEndBalance.getVotes() >= Seting.ALL_STOCK_VOTE\n" +
            "                && currentLawVotesEndBalance.getFractionVote() >= 0 ||\n" +
            "                currentLawVotesEndBalance.getFractionVote() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_FRACTIONS\n" +
            "                && currentLawVotesEndBalance.getVotesCorporateCouncilOfReferees() >= Seting.ORIGINAL_LIMIT_MIN_VOTE_CORPORATE_COUNCIL_OF_REFEREES){\n" +
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
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "    //без учета палаты представителей\n" +
            "    public static List<CurrentLawVotesEndBalance> filters(List<LawEligibleForParliamentaryApproval> approvalList, Map<String, Account> balances,\n" +
            "                                                          List<Account> BoardOfShareholders, List<Block> blocks, int limitBlocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {\n" +
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
            "        for (LawEligibleForParliamentaryApproval lawEligibleForParliamentaryApproval : approvalList) {\n" +
            "            if (votesMap.containsKey(lawEligibleForParliamentaryApproval.getLaws().getHashLaw())) {\n" +
            "                String address = lawEligibleForParliamentaryApproval.getLaws().getHashLaw();\n" +
            "                String packageName = lawEligibleForParliamentaryApproval.getLaws().getPacketLawName();\n" +
            "                List<String> laws = lawEligibleForParliamentaryApproval.getLaws().getLaws();\n" +
            "                double vote = votesMap.get(lawEligibleForParliamentaryApproval.getLaws().getHashLaw()).votes(balances, yesAverage, noAverage);\n" +
            "\n" +
            "                CurrentLawVotesEndBalance currentLawVotesEndBalance = new CurrentLawVotesEndBalance(address, packageName, vote, 0, 0, 0, 0, 0, 0, 0,  laws);\n" +
            "                current.add(currentLawVotesEndBalance);\n" +
            "\n" +
            "            }\n" +
            "        }\n" +
            "        return current;\n" +
            "    }\n" +
            "````\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)\n\n";
    String LAW_11 = "# ОБЯСНЕНИЕ ПОЧЕМУ ЗДЕСЬ ИСПОЛЬЗУЕТСЯ  ДЕМЕРЕДЖ ДЕНЕГ\n" +
            "Как работает отрицательная ставка здесь? Мы каждые пол года сжигаем со всех счетов 0.1% цифровых долларов\n" +
            "и 0.2% цифровых акций, что позволяет нам сохранить денежную массу не изменой, не давая ей расти,\n" +
            "так как новые создаваемые деньги и уничтожаемые деньги приходят в паритет.\n" +
            "Отрицательная ставка сейчас применяется во множестве стран, данная мера стимулирует держателей денег, когда цена чрезмерно завышена, \n" +
            "насыщать рынок деньгами. \n" +
            "Количество добываемых денег за каждый блок составляет 400 цифровых долларов и 400 цифровых акций, \n" +
            "также 2% от каждой добычи вознаграждение основателю, что составляет 4 цифровых доллара и 4 цифровых Акций при каждой добыче блока. \n" +
            "Здесь используется как Теории Сильвио Гезеля, а также школы монетаризма в измененном виде.\n" +
            "\n" +
            "У Сильвио Гезеля, отрицательная ставка составляла 1% в месяц, что просто убило бы экономику,\n" +
            "при монетаризме рост, денежной массы должен был быть пропорционален росту ВВП, но так как в\n" +
            "данной системе не получиться посчитать реальный рост ВВП, я установил фиксированный рост, также если денежный рост \n" +
            "будет равен ВВП, есть высокая вероятность Гиперинфляции, так как ВВП не всегда отражает реальный экономический рост. \n" +
            "Деньги должны быть твердые, чтобы бизнес мог прогнозировать свои долгосрочные вложения и от монетаризма, взята только та часть что \n" +
            "денежная масса должна расти линейно, но в целом здесь микс из разных экономических школ, включая Австрийскую экономическую школу. \n" +
            "\n" +
            "При отрицательной ставке 0.2% каждые пол года для цифровых долларов и 0.4% для цифровых акций мы избегаем последствий тяжелого экономического кризиса для данной валюты. \n" +
            "\n" +
            "Такой механизм создает коридор цен, где нижняя граница стоимости данных цифровых валют является общее количество выпущенных цифровых \n" +
            "долларов и цифровых акций, а верхняя граница является реальная стоимость. Так как только стоимость становиться выше реальной стоимости, \n" +
            "держателям выгодней становиться продавать цифровые доллары и цифровые акции, по завышенным ценам, тем самым насыщая рынок деньгами \n" +
            "и создавая коррекцию на рынке. \n" +
            "\n" +
            "Основным источником монетарных кризисов, является быстрыми изменениями цен на товары и медленным изменением заработных плат. \n" +
            "Пример: Представим что стоимость валюты резко подорожало на 30%, держателям выгодней становиться не инвестировать деньги, так как \n" +
            "доходы от удерживания валюты, выше чем теперь уже оплачивать более дорогих сотрудников, из-за того деньги перестают \n" +
            "инвестироваться. Люди не дополучают заработные платы, что приводит к тому, что огромное количество товаров не реализуется, \n" +
            "и это приводит к тому, часть производителей банкротится и увольняют множество рабочих, что еще больше снижает заработную \n" +
            "плату у оставшихся, так как становиться профицитный рынок труда. \n" +
            "\n" +
            "Что в свою очередь еще больше вызывает страх у держателей денег инвестировать и данный процесс продолжается до того момента, \n" +
            "пока стоимость денег не начинает сокращаться в связи с тем что общее количество производственных цепочек сократилось и также сократились товары. \n" +
            "            \"\n" +
            "Пример: Представим что у нас произошла инфляция и стоимость денег упала на 40% в течение месяца, стоимость товаров резко возрастает, \n" +
            "но заработные платы не выросли, таким образом множество товаров не будут куплены, что приводит к закрытию производственных цепочек, \n" +
            "что в свою очередь из-за избытка рабочих на рынке труда, снижает заработную плату, что также в свою очередь еще больше сокращает \n" +
            "количество проданных товаров.\n" +
            "Первый случай Дефляционная спираль возникает из-за резкого сокращения денег на рынке, второй \n" +
            "случай стагфляция чаще возникает когда на рынок поступает резко избыточное количество денег. \n" +
            "И это оба явления две стороны одной медали, в одном случае мы получаем дефляционную спираль в другом\n" +
            "стагфляцию.\n" +
            "            \n" +
            "Чтобы не возникали такие кризисы, в данной криптовалюте деньги прирастают в одинаковом предсказуемом количестве. \n" +
            "408 (8 - вознаграждение основателю, 400 - вознаграждение добытчику) \n" +
            "цифровых долларов и акций за блок, в сутках около 576 блоков. А отрицательная ставка корректирует стоимость монет каждые пол года. \n" +
            "Также запрещено использовать частичное банковское резервирование для данных монет, так как их количество растет линейно, и \n" +
            "не сможет покрыть долги возникшие из-за частичного банковского резервирования, в связи отсутствия с недостатком \n" +
            "наличности, так как при частичном банковском резервировании рост долгов будет намного выше, чем данный протокол будет создавать денег. \n" +
            "\n" +
            "Также если увеличить денежную массу изменив настройки, и сделав прирост денежной массы значительно выше, может вызвать гиперинфляцию или \n" +
            "даже галопирующую инфляцию. Если нужно будет увеличить прирост денежной массы это должно происходить только через внесения поправок, \n" +
            "сохраняя процент вознаграждения основателя в двух процентах. И добыча за блок не должна увеличиваться больше 5% в течение \n" +
            "десяти лет, каждое следующее увеличение которое может вноситься должно проходить не менее десяти лет через поправки, \n" +
            "и не более 5% за блок от вознаграждения последнего блока. (Пример: если мы изменили \n" +
            "через поправки, то добыча не должна быть выше 420 монет, но каждые следующие будет не больше пяти процентов от последнего. \n" +
            "Таким образом следующее увеличение внесенное через поправки составит 440.10 монет. Но Эту поправку внесут только через десять\n" +
            "лет после первой поправки по изменению добычи) \n" +
            "\n" +
            "При недостатке денежной массы, если не было изменено количество добываемых монет через поправку, можно добавить несколько \n" +
            "дополнительных нулей после запятой, таким образом это просто увеличит ценность монет, без увелечения общей выпущенной денежной массы.\n" +
            "\n" +
            "Отрицательные ставки не должны быть выше 1% годовых и ниже 0.2% годовых. Отрицательные ставки можно изменять только через внесения поправок. \n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)";
    String LAW_12 = "# FREEDOM_OF_SPEECH Право свободы слова \n" +
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
    String LAW_13 = "# RIGHTS Естественные Права\n" +
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
    String LAW_14 = "#ЗАКОНОДАТЕЛЬНАЯ ВЛАСТЬ.\n" +
            "Власть состоит из 3 групп в данной системе.\n" +
            "1. Совет Акционеров\n" +
            "2. Фракции\n" +
            "3. Независимые участники сети.\n" +
            "\n" +
            "Все участники должны участвовать в голосовании чтобы был действителен закон принятый системой\n" +
            "(Исключением является только совет Акционеров, так как совет Акционеров участвует \n" +
            "только в утверждении поправок в Устав).\n" +
            "Для всех голосов учитываются только голоса отданные за последние четыре года.\n" +
            "Все участники могут занимать несколько должностей из разных групп, но не могут\n" +
            "занимать в одной категории должностей несколько мест.\n" +
            "Пример: Один счет может быть как ***Независимым участником сети*** и ***Быть как фракцией***\n" +
            "и ***Членом Совета Акционеров***, но один счет не сможет занять несколько мест во фракциях\n" +
            "или в Совете Акционеров.\n" +
            "\n" +
            "Именно голоса от Акций учитываются при избрании Фракций и Корпоративных судей\n" +
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
            "## Фракции \n" +
            "Фракции избираются также как корпоративные судьи, их количество составляет 200 мест.\n" +
            "Особенность фракций в том что их голоса равны доле поддержки относительно других фракций.\n" +
            "Когда мы говорим фракция, мы всегда имеем лицо юридическое или физическое которое от имени\n" +
            "своей группы голосует и из-за этого один счет может иметь больше голосов чем при голосовании судей.\n" +
            "Так происходит подсчет голосов фракций [VOTE_FRACTION](../charter/VOTE_FRACTION.md)\n" +
            "\n" +
            "##  Независимые Участники сети.\n" +
            "Все участники сети которые имеют акции и не входят в первые три выше перечисленные категории,\n" +
            "являются ***независимыми участниками сети***. Голоса каждого такого участника приравнивается\n" +
            "к количеству акций на данный момент и детально описано в [VOTE_STOCK](../charter/VOTE_STOCK.md).\n" +
            "\n" +
            "\n" +
            "[Выход на главную](../documentation/documentationRus.md)\n" +
            "\n";
    String LAW_15 = "# БЮДЖЕТ И ЭМИССИЯ.\n" +
            "\n" +
            "## БЮДЖЕТ\n" +
            "Бюджет компании это адрес BUDGET. На данный адрес можно как отправлять деньги, так и снимать.\n" +
            "Как происходить снятие денег с этого счета.\n" +
            "1. Сначала создается Закон(Документ) с названием бюджет и в данном пакете,\n" +
            "в виде списков должны быть записаны адреса и через пробел сумма цифрового доллара и цифровых акций.\n" +
            "2. Дальше участники голосуют за один из этих пакетов методом VOTE_STOCK.\n" +
            "3. Учитываются пакеты, от которых не ушло время создания данных законов больше 15 дней.\n" +
            "4. Один пакет который получил наибольшее количество голосов становиться действующим, и с\n" +
            "него снимаются суммы (учитываются только те пакеты в которых не меньше 300000 голосов).\n" +
            "5. Тратить с баланса бюджета можно раз в 15 дней. Цифровой год это 360 суток, а в одних сутках 576 блоков.\n" +
            "\n" +
            "\n" +
            "## ЭМИССИЯ\n" +
            "Эмиссия позволяет создавать до 25 тысяч цифровых долларов каждые пятнадцать дней.\n" +
            "Эмиссия создается аналогично бюджету, с той лишь разницей, что название пакета должно\n" +
            "быть EMISSION.\n" +
            "\n" +
            "Бюджет и Эмиссия в первую очередь предназначены для трат общественных благ и развития системы.\n" +
            "Туда входят судьи, и другие сотрудники.\n" +
            "Также вы не можете потратить больше суммы из бюджета чем она есть и не можете потратить\n" +
            "больше 25 тысяч цифровых долларов для эмиссии в одном документе. \n" +
            "Каждые 15 дней для эмиссии не больше сто тысяч цифровых долларов.";


}
