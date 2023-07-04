# Транзакция

## Как отправить транзакцию
Как отправить деньги

войти на http://localhost:8082/
Введите адрес отправителя, адрес получателя, сколько цифровых
долларов, которые вы хотите отправить, сколько цифровых акций вы хотите отправить,
вознаграждение майнера

Перед отправкой обновите локальный блокчейн, но актуальный.
Перед голосованием и другими действиями вы можете обновить блокчейн,
но перед голосованием не стоит, так как сумма не высылается.
Также, чтобы видеть текущие позиции, стоит обновить блокчейн.
Перед майнингом происходит автоматически.
Для этого нужно нажать кнопку ***обновить блокчейн*** в главном меню и в самом низу
![обновить блокчейн](../screenshots/update_blockchainEng.png)

И введите пароль, затем нажмите кнопку отправить деньги
![транзакция](../скриншоты/send-moneyEng.png)

на локальном хосте: 8082/
необходимо хранить данные в
- ввод адреса отправителя открытый ключ отправителя
- ввод адреса получателя открытый ключ получателя
- введите цифровой доллар, чтобы отправить сумму цифровых долларов для отправки
- введите цифровой запас, чтобы отправить количество цифрового запаса для отправки
- отправить вознаграждение для майнера

- вводите пароль, сохраняйте приватный ключ
- и нажмите отправить деньги

## Из чего состоит класс транзакции

````
      src/main/java/entity/blockchain/DtoTransaction/DtoTransaction.java
````

Конструктор транзакций.
- отправитель (отправитель)
- заказчик (получатель)
- digitalDollar (цифровой доллар)
- digitalStock (цифровые акции)
- законы (Пакет законов)
- BonusMiner (награда майнерам)
- VoteEnum (голос отправителя, который может быть ДА или НЕТ)
- подпись (подпись отправителя)


проверяет целостность транзакции, чтобы транзакция была подписана правильно
Метод находится в классе DtoTransaction.java.
````
      public boolean verify() выдает IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
      Базовая база = новая Base58();
      byte[]pub = base.decode(отправитель);
      BCECPublicKey publicKey = (BCECPublicKey) UtilsSecurity.decodeKey(pub);
      // PublicKey publicKey = UtilsSecurity.publicByteToPublicKey(pub);
      Строка sha = отправитель + клиент + цифровой доллар + цифровой баланс акций + законы + бонус для майнера;
      ша = UtilsUse.sha256hash(sha);
      if(sender.isBlank() || customer.isBlank() || digitalDollar < 0 || digitalStockBalance < 0 || bonusForMiner < 0 || law == null){
      System.out.println("неверный dto отправитель транзакции или клиент пустой? или доллар, репутация или вознаграждение меньше 0");
      вернуть ложь;
      }
      если (Настройка.BASIS_ADDRESS.equals(publicKey))
      вернуть истину;
      return UtilsSecurity.verify(sha, sign, publicKey);
      }
````

создает sha256 для подписи

````
      общедоступная строка toSign () {
      Строка sha = отправитель + клиент + цифровой доллар + цифровой баланс акций + законы + бонус для майнера;
      вернуть UtilsUse.sha256hash(sha);
      }
````

делает строку json из объекта

````
      public String jsonString() выдает IOException {
      вернуть UtilsJson.objToStringJson (это);
      }
````


## КАК ПРОИСХОДИТ ДОСТАВКА
После того, как вы нажали кнопку отправки
метод в контроллере класса MainController.java будет работать

````
      расположен в src/main/java/controllers/MainController.java
````

метод

````
      @PostMapping("/")
      публичная строка new_transaction(
      @RequestParam Отправитель строки,
      @RequestParam Строковый получатель,
      двойной доллар,
      двойной запас,
      двойная награда,
      @RequestParam Строковый пароль,
      redirectAttributes redirectAttrs)
````

во-первых, он проверяет, правильно ли подписана транзакция

````
      если (dtoTransaction.verify())
````

если чек правильно подписан
то сначала проверяется, не является ли эта сделка созданием закона,
если это и в то же время создание закона, который создает офис
затем он проверяет, совпадает ли адрес отправителя с первой строкой юридического пакета,
если проверка проходит, то транзакция отправляется в сеть
````
        //если название закона совпадает с корпоративными позициями, то закон действует только тогда, когда
              //отправитель соответствует закону
              List<String> CorporateSeniorPositions = Directors.getDirectors().stream()
                      .map(t->t.getName()).collect(Collectors.toList());
              System.out.println("LawsController: create_law: " + law.getPacketLawName() + "contains:" + CorporateSeniorPositions.contains(laws.getPacketLawName()));
              if(corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(отправитель, законы)){
                  redirectAttrs.addFlashAttribute("отправка", "неправильная транзакция: позиция должна быть равна отправке");
                  вернуть "перенаправление:/отправка результата";
              }
              redirectAttrs.addFlashAttribute («отправка», «успешно»);
              System.out.println("dto MainController: " + dtoTransaction);
````


если это просто транзакция, то транзакция просто отправляется в сеть

````

               AllTransactions.addTransaction(dtoТяга);
              Строка jsonDto = UtilsJson.objToStringJson(dtoTransaction);
              for (String s : Setting.ORIGINAL_ADDRESSES) {

                  Исходная строка = s;
                  URL-адрес строки = s +"/addTransaction";
                  //если адрес совпадает с внутренним хостом, то не отправлять на себя
                  если (BasisController.getExcludedAddresses (). Содержит (url)) {
                      System.out.println("MainController: это ваш адрес или исключенный адрес: " + url);
                      продолжать;
                  }
                  пытаться {
                      //отправляем в сеть
                      UtilUrl.sendPost (jsonDto, URL);

                  }поймать (Исключение e){
                      System.out.println("обнаружение исключения: " + оригинал);

                  }
              }

````

Класс AllTransaction.java сохраняет в файлах транзакции, уже добавленные в блок,
и транзакции, которые еще не были обработаны этим локальным сервером.

Класс делится на

````
      источник/главная/java/сеть/AllTransaction.java
````

Метод, добавляющий транзакцию в файл

````
        общедоступная статическая синхронизированная пустота addTransaction (транзакция DtoTransaction) выдает IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

          экземпляр = получить экземпляр ();
          instance.add (транзакция);
          Mining.deleteFiles(Setting.ORGINAL_ALL_TRANSACTION_FILE);
          //принимает только уникальные транзакции
          instance = instance.stream().filter(UtilsUse.distinctByKey(DtoTransaction::toSign)).collect(Collectors.toList());
          for (DtoTransaction dtoTransaction: экземпляр) {
              UtilsTransaction.saveAllTransaction(dtoTransaction, Setting.ORGINAL_ALL_TRANSACTION_FILE);
          }


      }
````

Класс UtilUrl.java отвечает за отправку
располагается

````
      src/main/java/utils/UtilUrl.java
````

Способ отправки

````
      public static int sendPost (String jsonObject, String requestStr) выдает IOException {
          внутренний ответ;
          URL-адрес = новый URL-адрес (requestStr);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      // соединение.connect();
      conn.setReadTimeout (10000);
      conn.setConnectTimeout (15000);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json; utf-8");
      conn.setRequestProperty("Принять", "приложение/json");
      conn.setDoOutput (истина);



          try(OutputStream outputStream = conn.getOutputStream()) {
              байт [] ввод = jsonObject.getBytes ("utf-8");
              outputStream.write(вход, 0, input.length);
               ответ = conn.getResponseCode();

          }


          соединять();
          ответный ответ;
      }
````

[Вернуться на главную](./documentationRus.md)