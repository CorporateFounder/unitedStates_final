# майнинг блоков

## КАК НАЧАТЬ МАЙНИНГ
Прежде чем приступить к добыче блоков, вы
вам нужно установить адрес майнера, на который будет добыт блок.
После того как вы установили свой адрес в качестве майнера, есть два варианта.



### ВАРИАНТ 1
Нажмите кнопку старт, для остановки добычи нажмите на главную страницу и появиться 
страница, где будет кнопка стоп.
![блок майнинга](../screenshots/mineEng.png)


### ВАРИАНТ 2.
вызов http://localhost:8082/mine автоматически запускает майнинг.


### РАПРЕДЕЛЕНИЕ НАГРУЗКИ МЕЖДУ МНОЖЕСТВОМ КОМПЮТЕРОВ.
Представим что у вас есть два компьютера и хотите чтобы они добывали
блоки, если просто включить на двух компьютерах, то они будут
перебирать оба с 0 до бесконечности.

Вы можете на одном компьютере начать перебирать с 0, а на втором с 5000 тысяч,
таким образом второй будет проверять чуть раньше. Или 10000 тысяч.
Вы должны сами подобрать оптимальные числа.

Но число не должно быть равно или выше максимального значения.

Сложность блокчейна адаптируется аналогично биткойну, но адаптация происходит
раз в 12 часов (примерно) 288 блоков.
Каждый блок дает (difficulty * 30 ) + odd = где odd 0 если индекс блока четный и 1 если не четный

Текущий блокчейн не только самый длинный, но и нулей в нем должно быть больше.

Этот метод подсчитывает количество нулей в блокчейне и текущий блокчейн, причем не только самый длинный,
но и тот, в котором больше всего нулей
````
      src/main/java/utils/UtilsUse.java

       // подсчитываем количество последовательных нулей в хеше
      общедоступный статический длинный hashCount (строковый хэш) {
          длинный счет = 0;
          for (int i = 0; i < hash.length(); i++) {
              if(hash.charAt(i) == '0') count++;
              иначе вернуть счетчик;
          }
          количество возвратов;
      }
````
после 53391 блока подсчет происходит по количеству нулей в битах
````
 public static boolean meetsDifficulty(byte[] hash, int difficulty) {
   
    int zeroBits = countLeadingZeroBits(hash);
    return zeroBits >= difficulty;
  }
    public static int countLeadingZeroBits(byte[] hash) {
        int bitLength = hash.length * 8;
        BitSet bits = BitSet.valueOf(hash);

        int count = 0;
        while (count < bitLength && !bits.get(count)) {
            count++;
        }

        return count;
    }
  ````

Класс отвечает за извлечение

````
      src/main/java/International_Trade_Union/controllers/BasisController.java
````

метод нажатия кнопки вызывает кнопку /mine

````
      /**Начать майнинг, начать майнинг*/
      @GetMapping("/моя")
      публичная синхронизированная шахта String (модель модели) выдает NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException, CloneNotSupportedException {
          Строковый текст = "";
          // найти адреса
          найтиАдрес();
          отправитьАдрес();

          //собирает классный список балансов из файла расположенного по пути Setting.ORIGINAL_BALANCE_FILE
          Map<String, Account> balances = SaveBalances.readLineObject(Setting.ORIGINAL_BALANCE_FILE);
          //собирает объект блокчейна из файла
          Блокчейн = Майнинг.getBlockchain(
                  Настройка.ORIGINAL_BLOCKCHAIN_FILE,
                  BlockchainFactoryEnum.ORIGINAL);

          //если блокчейн работает, то продолжаем
          если (!blockchain.validatedBlockchain()) {
              text = "неправильная цепочка: неправильная цепочка, майнинг остановлен";
              model.addAttribute("текст", текст);
          }

          //Перед майнингом нового блока он сначала ищет в сети самый длинный блокчейн
          разрешение_конфликтов();

          //если размер блокчейна меньше или равен единице, сохраняем блок генезиса в файл
          длинный индекс = blockchain.sizeBlockchain();
          если (blockchain.sizeBlockchain () <= 1) {
              //сохраняем генезис блока
              если (blockchain.sizeBlockchain () == 1) {
                  UtilsBlock.saveBLock(blockchain.getBlock(0), Setting.ORIGINAL_BLOCKCHAIN_FILE);
              }

              // получаем список балансов из файла
              балансы = Mining.getBalances(Setting.ORIGINAL_BALANCE_FILE, блокчейн, балансы);
              //удаляем старые файлы баланса
              Mining.deleteFiles(Setting.ORIGINAL_BALANCE_FILE);
              // сохранить остатки
              SaveBalances.saveBalances(балансы, Настройка.ORIGINAL_BALANCE_FILE);

          }
          //скачиваем список балансов из файла
          балансы = SaveBalances.readLineObject(Setting.ORIGINAL_BALANCE_FILE);

          //получаем аккаунт майнера
          Майнер аккаунта = balances.get(User.getUserAddress());
          если (майнер == ноль) {
              //если баланса в блокчейне не было, то баланс нулевой
              майнер = новая учетная запись (User.getUserAddress(), 0, 0);
          }

          //транзакции которые мы добавили в блок и теперь их нужно удалить из файла в папке resources/transactions
          Список<DtoTransaction> временныйDtoList = AllTransactions.getInstance();

          //каждые три для очистки файлов в папке resources/sendedTransaction этой папки
          //сохраняет уже добавленные в блокчейн транзакции, чтобы не добавлять повторно в
          //транзакции уже добавлены в блок
          ВсеТранзакции.очиститьВсеОтправленныеТранзакции(индекс);
          AllTransactions.clearUsedTransaction(AllTransactions.getInsanceSended());
          System.out.println("BasisController: запустить мой:");

          // Сам процесс майнинга
          //DIFFICULTY_ADJUSTMENT_INTERVAL, как часто происходит коррекция
          //BLOCK_GENERATION_INTERVAL как часто должен находить блок
          //temporaryDtoList добавляет транзакции в блок
          Блочный блок = Mining.miningDay(
                  шахтер,
                  блокчейн,
                  Настройка.BLOCK_GENERATION_INTERVAL,
                  Настройка.DIFFICULTY_ADJUSTMENT_INTERVAL,
                  временныйDtoList,
                  балансы,
                  индекс
          );
          System.out.println("BasisController: закончить мой:");
          //сохраняем отправленную транзакцию
          AllTransactions.addSendedTransaction(temporaryDtoList);

          int diff = Настройка.DIFFICULTY_ADJUSTMENT_INTERVAL;
          // Проверка блока
          List<Block> testingValidationsBlock = null;

          если (blockchain.sizeBlockchain() > diff) {

              testingValidationsBlock = blockchain.subBlock(blockchain.sizeBlockhain() - diff, blockchain.sizeBlockhain());
          } еще {
              testingValidationsBlock = blockchain.clone();
          }
          если (testingValidationsBlock.size() > 1) {
              логическое значение validationTesting = UtilsBlock.validationOneBlock(
                      blockchain.genesisBlock().getFounderAddress(),
                      testingValidationsBlock.get(testingValidationsBlock.size() - 1),
                      блокировать,
                      Настройка.BLOCK_GENERATION_INTERVAL,
                      разница,
                      блок проверки валидации);

              если (проверка проверки == ложь) {
                  System.out.println("неверный блок проверки: " + validationTesting);
                  System.out.println("Индексный блок: " + block.getIndex());
                  текст = "неправильная проверка";
              }
              testingValidationsBlock.add(block.clone());
          }

          // сохранить блок
          blockchain.addBlock(блок);
          UtilsBlock.saveBLock(блок, Настройка.ORIGINAL_BLOCKCHAIN_FILE);

          // пересчет после извлечения
          балансы = Mining.getBalances(Setting.ORIGINAL_BALANCE_FILE, блокчейн, балансы);
          Mining.deleteFiles(Setting.ORIGINAL_BALANCE_FILE);
          SaveBalances.saveBalances(балансы, Настройка.ORIGINAL_BALANCE_FILE);

          //получение и отображение законов, и сохранение новых законов
          //и изменение существующих законов
          Map<String, Laws> allLaws = UtilsLaws.getLaws(blockchain.getBlockchainList(), Setting.ORIGINAL_ALL_CORPORATION_LAWS_FILE);

          //возвращает все законы с балансом
          List<LawEligibleForParliamentaryApproval> allLawsWithBalance = UtilsLaws.getCurrentLaws(allLaws, balances, Setting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
          //удаляем устаревшие законы
          Mining.deleteFiles(Setting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);
          UtilsLaws.saveCurrentsLaws(allLawsWithBalance, Setting.ORIGINAL_ALL_CORPORATION_LAWS_WITH_BALANCE_FILE);

          // отправляем фактический блокчейн
          sendAllBlocksToStorage(blockchain.getBlockchainList());

          text = "успех: блок успешно добыт";
          model.addAttribute("текст", текст);
          вернуть "перенаправление:/майнинг";

      }
````

Вот код майнинга

````
      src/main/java/International_Trade_Union/model/Mining.java
````

сам метод

````
      публичный статический блок miningDay(
      аккаунт майнер,
      блокчейн,
      длинный блокGenerationInterval,
      интервал DIFFICULTY_ADJUSTMENT_INTERVAL,
      List<DtoTransaction> список транзакций,
      Map<String, Account> балансы,
      длинный индекс
      ) выдает IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
      Директора директоров = новые директора();
      //прием транзакций из сети
      List<DtoTransaction> listTransactions = список транзакций;

          //определение допустимых транзакций
          List<DtoTransaction> forAdd = new ArrayList<>();

          // проверяет целостность транзакций, правильно ли они подписаны
          цикл:
          for (транзакция DtoTransaction: listTransactions) {
              если (транзакция.проверить()) {
            
                  Учетная запись = balances.get(transaction.getSender());
                  если (счет == ноль) {
                      System.out.println ("minerAccount null");
                      продолжить цикл;
                  }
                  //NAME_LAW_ADDRESS_START, если адрес означает правила, выбранные сетью
                  if(transaction.getCustomer().startsWith(Setting.NAME_LAW_ADDRESS_START) && !balances.containsKey(transaction.getCustomer())){
                      //если название закона совпадает с корпоративными позициями, то закон действует только тогда, когда
                      //отправитель соответствует закону
      // Список<Директор> enumPosition = Directors.getDirectors();
      List<String> CorporateSeniorPositions = Directors.getDirectors().stream()
      .map(t->t.getName()).collect(Collectors.toList());
      System.out.println("LawsController: create_law: " + transaction.getLaws().getPacketLawName() + "contains:" + CorporateSeniorPositions.contains(transaction.getLaws().getPacketLawName()));
      if(corporateSeniorPositions.contains(transaction.getLaws().getPacketLawName())
      && !UtilsGovernment.checkPostionSenderEqualsLaw(transaction.getSender(), transaction.getLaws())){
      System.out.println("если выесть особая корпоративная позиция, вам нужна "+
      "отправитель равен первому закону: теперь это неправильно");
      продолжить цикл;
      }
      }
      если (транзакция.getLaws () == ноль) {
      System.out.println("закон не может быть нулевым: ");
      продолжить цикл;
      }

                  если (учетная запись != ноль) {
                      если(транзакция.getSender().equals(Setting.BASIS_ADDRESS)){
                          System.out.println("только этот майнер может ввести базовый адрес в этот блок");
                          продолжить цикл;
                      }
                      if(transaction.getCustomer().equals(Setting.BASIS_ADDRESS)){
                          System.out.println("базовый адрес не может быть клиентом(получателем)");
                          продолжить цикл;
                      }

                      if( account.getDigitalDollarBalance() < transaction.getDigitalDollar() + transaction.getBonusForMiner()){
                          System.out.println("У отправителя нет цифрового доллара: " + account.getAccount() + "баланс:" + account.getDigitalDollarBalance() );
                          System.out.println("цифровой доллар для отправки: " + (transaction.getDigitalDollar() + transaction.getBonusForMiner()));
                          продолжить цикл;
                      }
                      если (account.getDigitalStockBalance() < transaction.getDigitalStockBalance()){
                          System.out.println("у отправителя нет цифровой репутации: " + account.getAccount() + " balance: " + account.getDigitalStockBalance());
                          System.out.println("цифровая репутация для отправки: " + (transaction.getDigitalDollar() + transaction.getBonusForMiner()));
                          продолжить цикл;
                      }
                      если(транзакция.getSender().equals(transaction.getCustomer())) ){
                          System.out.println("отправитель и конечный получатель равен " + transaction.getSender() + " : получатель: " + transaction.getCustomer());
                          продолжить цикл;
                      }
                      forAdd.add (транзакция);
                  }

              }
          }


          //доход майнера
        double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
        double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;

        //доход основателя
        double founderReward = Seting.DIGITAL_DOLLAR_FOUNDER_REWARDS_BEFORE;
        double founderDigigtalReputationReward = Seting.DIGITAL_REPUTATION_FOUNDER_REWARDS_BEFORE;

        Base base = new Base58();

        //суммирует все вознаграждения майнеров
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(Seting.BASIS_PASSWORD));
        double sumRewards = forAdd.stream().collect(Collectors.summingDouble(DtoTransaction::getBonusForMiner));




        //вознаграждение основателя
        DtoTransaction founderRew = new DtoTransaction(Seting.BASIS_ADDRESS, blockchain.getADDRESS_FOUNDER(),
                founderReward, founderDigigtalReputationReward, new Laws(), 0.0, VoteEnum.YES);
        byte[] signFounder = UtilsSecurity.sign(privateKey, founderRew.toSign());

        founderRew.setSign(signFounder);



        forAdd.add(founderRew);


        //здесь должна быть создана динамическая модель
        //определение сложности и создание блока

        int difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), blockGenerationInterval, DIFFICULTY_ADJUSTMENT_INTERVAL);
        BasisController.setDifficultExpected(difficulty);
        System.out.println("Mining: miningBlock: difficulty: " + difficulty + " index: " + index);

        if(index > Seting.CHECK_DIFFICULTY_BLOCK_2) {
            minerRewards = difficulty * Seting.MONEY;
            digitalReputationForMiner= difficulty * Seting.MONEY;
            minerRewards += index%2 == 0 ? 0 : 1;
            digitalReputationForMiner += index%2 == 0 ? 0 : 1;
        }

        //вознаграждения майнера
        DtoTransaction minerRew = new DtoTransaction(Seting.BASIS_ADDRESS, minner.getAccount(),
                minerRewards, digitalReputationForMiner, new Laws(), sumRewards, VoteEnum.YES );


        forAdd.add(minerRew);
        //подписывает
        byte[] signGold = UtilsSecurity.sign(privateKey, minerRew.toSign());
        minerRew.setSign(signGold);
        //blockchain.getHashBlock(blockchain.sizeBlockhain() - 1)
        Block block = new Block(
                forAdd,
                blockchain.getHashBlock(blockchain.sizeBlockhain() - 1),
                minner.getAccount(),
                blockchain.getADDRESS_FOUNDER(),
                difficulty,
                index);


       return block;
      }
````


подключается к внешним серверам в поисках длинного блокчейна,
Принимая его, если он находит более современный блокчейн, он загружает и заменяет свой собственный блокчейн.

````
      @GetMapping("/узлы/разрешить")
      public synchronized void resolve_conflicts() выдает NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
      Блокчейн временныйБлокчейн = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
      Блокчейн bigBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
      Блокчейн = Майнинг.getBlockchain(
      Настройка.ORIGINAL_BLOCKCHAIN_FILE,
      BlockchainFactoryEnum.ORIGINAL);
      интервал блоков_текущий_размер =blockchain.sizeBlockchain();
      длинный hashCountZeroTemporary = 0;
      длинный hashCountZeroBigBlockchain = 0;
      EntityChain entityChain = ноль;

          длинный hashCountZeroAll = 0;
          //счет хэша начинается с нуля все
          for (блокировка блока: blockchain.getBlockchainList()) {
              hashCountZeroAll += UtilsUse.hashCount(block.getHashBlock());
          }
          // список всех хостов
          Set<String> nodesAll = getNodes();
      // nodesAll.addAll(Setting.ORIGINAL_ADDRESSES_BLOCKCHAIN_STORAGE);
      System.out.println("BasisController: разрешение: размер: " + getNodes().size());
      for (Строка s: nodesAll) {
      System.out.println("BasisController: resove: address: " + s);
      Строка временный_сон = ноль;

              если (BasisController.getExcludedAddresses().contains(s)) {
                  System.out.println("это ваш адрес или исключенный адрес: " + s);
                  продолжать;
              }
              пытаться {
                  //загружает блокчейн с других адресов
                  Строковый адрес = s + "/chain";
                  System.out.println("BasisController:разрешить конфликты: адрес: " + s + "/size");
                  Строка sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                  Целочисленный размер = Целочисленное значениеOf(sizeStr);
                  //если размер текущего блокчейна меньше размера других хостов
                  если (размер > block_current_size) {
                      System.out.println("размер по адресу: " + s + " больше чем: " + size + ":blocks_current_size " + blocks_current_size);
                      //Алгоритм запуска теста
                      SubBlockchainEntity subBlockchainEntity = новый SubBlockchainEntity (текущий_размер блоков, размер);
                      Строка subBlockchainJson = UtilsJson.objToStringJson(subBlockchainEntity);

                      List<Block> emptyList = new ArrayList<>();

                      List<Block> subBlocks = UtilsJson.jsonToListBLock(UtilUrl.getObject(subBlockchainJson, s + "/sub-blocks"));
                      пустой список.добавить все (подблоки);
                      emptyList.addAll (blockchain.getBlockchainList ());

                      emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                      временныйBlockchain.setBlockchainList (пустой список);
                      если (!temporaryBlockchain.validatedBlockchain()) {
                          System.out.println("первый алгоритм не сработал");
                          пустой список = новый ArrayList<>();
                          пустой список.добавить все (подблоки);
                          for (int i = blockchain.sizeBlockchain() - 1; i > 0; i--) {
                              //загружаем блок по индексу
                              Блочный блок = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(i), s + "/block"));
                              если (!blockchain.getBlock(i).getHashBlock().equals(block.getHashBlock())) {
                                  пустой список.добавить (блок);
                              } еще {
                                  пустой список.добавить (блок);
                                  emptyList.addAll (blockchain.getBlockchainList (). SubList (0, i));
                                  emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                                  временныйBlockchain.setBlockchainList (пустой список);
                                  перерыв;
                              }
                          }
                      }
                      если (!temporaryBlockchain.validatedBlockchain()) {
                          System.out.println("второй алгоритм не сработал");
                          временный json = UtilUrl.readJsonFromUrl (адрес);
                          entityChain = UtilsJson.jsonToEntityChain (temporaryjson);
                          временныйBlockchain.setBlockchainList(
                                  entityChain.getBlocks().stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList()));
                      }
                  } еще {
                      System.out.println("BasisController: resove: size less: " + size + " address: " + address);
                      продолжать;
                  }
              } поймать (IOException e) {
                  System.out.println("BasisController: resolve_conflicts: Ошибка: " + s);
                  продолжать;
              }

              //если загруженный блокчейн правильный, то заменяем переменную bigBlockchain
              если (temporaryBlockchain.validatedBlockchain()) {
                  for (Блокировать блок: временноBlockchain.getBlockchainList()) {
                      hashCountZeroTemporary += UtilsUse.hashCount(block.getHashBlock());
                  }

                  если (blocks_current_size < временныйBlockchain.sizeBlockhain() && hashCountZeroAll < hashCountZeroTemporary) {
                      blocks_current_size = временныйBlockchain.siзеБлокчейн();
                      большойблокчейн = временныйблокчейн;
                      hashCountZeroBigBlockchain = hashCountZeroTemporary;
                  }
                  hashCountZeroTemporary = 0;
              }

          }

          //если самый длинный блокчейн больше, чем локальный блокчейн
          //сервер, но заменить на локальном сервере скачанный

          if (bigBlockchain.sizeBlockhain() > blockchain.sizeBlockhain() && hashCountZeroBigBlockchain > hashCountZeroAll) {

              Блокчейн = большой Блокчейн;
              UtilsBlock.deleteFiles();
              addBlock(bigBlockchain.getBlockchainList(), BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL));
              System.out.println("BasisController: разрешение: размер bigblockchain: " + bigBlockchain.sizeBlockchain());

          }
      }
````

Поиск хэша исходит из класса BLock

````
      источник/основной/java/entity/блокчейн/блок/Block.java
````

Сам поиск по хешу, как аргумент какой сложности надо найти,
сама сложность определяется динамически

````
      public String findHash(int hashCoplexity) выдает IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
      если (!verifyesTransSign()){
      бросить новое исключение NotValidTransactionException();
      }

          this.randomNumberProof = 0;
          Строковый хэш = "";
          пока (правда) {
              это.randomNumberProof++;
              Блок BlockForHash = новый BlockForHash(this.dtoTransactions,
                      this.previousHash, this.minerAddress, this.founderAddress,
                      this.randomNumberProof, this.minerRewards, this.hashCompexity, this.timestamp, this.index);
              хеш = block.hashForTransaction();
              if(UtilsUse.hashComplexity(hash.substring(0, hashCoplexity), hashCoplexity))
              {
                  перерыв;
              }

          }
          хэш возврата;
      }
````

Если количество нулей совпадает с ожидаемым, то поиск прекращается.

````
       src/main/java/utils/UtilsUse.java
````

Класс этого метода, который проверяет, совпадает ли хэш с количеством нулей

````
      открытый статический логический hashComplexity (строковый литерал, int hashComplexity) {

          Строковое регулярное выражение = "^[0]{" + Integer.toString(hashComplexity) + "}";
          Шаблон шаблона = Pattern.compile (регулярное выражение);
          Сопоставитель сопоставитель = шаблон.сопоставитель(литерал);
          вернуть matcher.find();
      }
````

BASIS_ADDRESS предназначен для отправки вознаграждения майнеру и основателю каждый раз, когда майнер
хочет добыть блок, вставляет адрес основателя и майнера,

````
      Метод src/main/java/model/Mining.java: miningDay()
````

````
      //доход майнера
        double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
        double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;

        //доход основателя
        double founderReward = Seting.DIGITAL_DOLLAR_FOUNDER_REWARDS_BEFORE;
        double founderDigigtalReputationReward = Seting.DIGITAL_REPUTATION_FOUNDER_REWARDS_BEFORE;

        Base base = new Base58();

        //суммирует все вознаграждения майнеров
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(Seting.BASIS_PASSWORD));
        double sumRewards = forAdd.stream().collect(Collectors.summingDouble(DtoTransaction::getBonusForMiner));




        //вознаграждение основателя
        DtoTransaction founderRew = new DtoTransaction(Seting.BASIS_ADDRESS, blockchain.getADDRESS_FOUNDER(),
                founderReward, founderDigigtalReputationReward, new Laws(), 0.0, VoteEnum.YES);
        byte[] signFounder = UtilsSecurity.sign(privateKey, founderRew.toSign());

        founderRew.setSign(signFounder);



        forAdd.add(founderRew);


        //здесь должна быть создана динамическая модель
        //определение сложности и создание блока

        int difficulty = UtilsBlock.difficulty(blockchain.getBlockchainList(), blockGenerationInterval, DIFFICULTY_ADJUSTMENT_INTERVAL);
        BasisController.setDifficultExpected(difficulty);
        System.out.println("Mining: miningBlock: difficulty: " + difficulty + " index: " + index);

        if(index > Seting.CHECK_DIFFICULTY_BLOCK_2) {
            minerRewards = difficulty * Seting.MONEY;
            digitalReputationForMiner= difficulty * Seting.MONEY;
            minerRewards += index%2 == 0 ? 0 : 1;
            digitalReputationForMiner += index%2 == 0 ? 0 : 1;
        }

        //вознаграждения майнера
        DtoTransaction minerRew = new DtoTransaction(Seting.BASIS_ADDRESS, minner.getAccount(),
                minerRewards, digitalReputationForMiner, new Laws(), sumRewards, VoteEnum.YES );


        forAdd.add(minerRew);
        //подписывает
        byte[] signGold = UtilsSecurity.sign(privateKey, minerRew.toSign());
        minerRew.setSign(signGold);
        //blockchain.getHashBlock(blockchain.sizeBlockhain() - 1)
        Block block = new Block(
                forAdd,
                blockchain.getHashBlock(blockchain.sizeBlockhain() - 1),
                minner.getAccount(),
                blockchain.getADDRESS_FOUNDER(),
                difficulty,
                index);


       return block;
````

[вернуться домой](./documentationRus.md)