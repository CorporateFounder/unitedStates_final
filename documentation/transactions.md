# Транзакция 

## Как отправить транзакцию
Как отправить деньги, 

войдите в http://localhost:8082/
Введите адрес отправителя, адрес получателя, сколько цифровых 
долларов хотите отправить, сколько цифровых акций хотите отправить,
вознаграждение майнеру, выберите ваш голос YES(ДА) или NO(Нет), 
и введите пароль, после нажмите кнопку отправить деньги


в localhost:8082/ 
нужно вести данные в 
- input address sender публичный ключ отправителя
- input address recipient публичный ключ получателя
- input digital dollar to send сумму цифровых долларов для отправки
- input digital stock to send сумму цифрвых акций для отправки
- send reward for miner вознаграждение майнера
- выбрать YES или NO для голосования
- input password вести private key
- и нажать send money

## из чего состоит класс транзакция

````
    src/main/java/entity/blockchain/DtoTransaction/DtoTransaction.java
````

Конструктор
- sender (отправителя)
- customer (получателя)
- digitalDollar (цифрового доллара)
- digitalStock (цифровых акций)
- laws (Пакета законов)
- bonusMiner (вознаграждение майнера)
- VoteEnum (голоса отправителя, который может быть YES или NO)
- sign (подписи отправителя)


проверяет целостность транзакции, что транзакцию подписали правильно
Метод находится в классе DtoTransaction.java
````
    public boolean verify() throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
    Base base = new Base58();
    byte[] pub = base.decode(sender);
    BCECPublicKey publicKey = (BCECPublicKey) UtilsSecurity.decodeKey(pub);
    //        PublicKey publicKey = UtilsSecurity.publicByteToPublicKey(pub);
    String sha = sender + customer + digitalDollar + digitalStockBalance + laws + bonusForMiner;
    sha = UtilsUse.sha256hash(sha);
    if(sender.isBlank() || customer.isBlank() || digitalDollar < 0 || digitalStockBalance < 0 || bonusForMiner < 0 || laws == null){
    System.out.println("wrong dto transaction sender or customer blank? or dollar, reputation or reward less then 0");
    return false;
    }
    if(Seting.BASIS_ADDRESS.equals(publicKey))
    return true;
    return UtilsSecurity.verify(sha, sign, publicKey);
    }
````

создает sha256 для подписи

````
    public String toSign(){
    String sha = sender + customer + digitalDollar + digitalStockBalance + laws + bonusForMiner;
    return UtilsUse.sha256hash(sha);
    }
````

делает из объекта строку json

````
    public String jsonString() throws IOException {
    return UtilsJson.objToStringJson(this);
    }
````


## КАК ПРОИСХОДИТ ОТПРАВКА
после того как вы нажали кнопку send
сработает метод в контроллере класса MainController.java

````
    расположенного в src/main/java/controllers/MainController.java
````

метод

````
    @PostMapping("/")
    public String new_transaction(
    @RequestParam  String sender,
    @RequestParam  String recipient,
    Double dollar,
    Double stock,
    Double reward,
    @RequestParam  String vote,
    @RequestParam  String password,
    RedirectAttributes redirectAttrs)
````

сначала происходит проверка что транзакция правильно подписана

````
    if(dtoTransaction.verify())
````

если проверка правильно подписана
то сначала проверяется являться ли данная транзакция созданием закона,
если является и одновременно является созданием закона которая создает должность
то, проверяется, совпадает ли адрес отправителя с первой строкой пакета закона,
если проверка проходит то транзакция отправляется в сеть
````
      //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
            //отправитель совпадает с законом
            List<String> corporateSeniorPositions = directors.getDirectors().stream()
                    .map(t->t.getName()).collect(Collectors.toList());
            System.out.println("LawsController: create_law: " + laws.getPacketLawName() + "contains: " + corporateSeniorPositions.contains(laws.getPacketLawName()));
            if(corporateSeniorPositions.contains(laws.getPacketLawName()) && !UtilsGovernment.checkPostionSenderEqualsLaw(sender, laws)){
                redirectAttrs.addFlashAttribute("sending", "wrong transaction: Position to be equals whith send");
                return "redirect:/result-sending";
            }
            redirectAttrs.addFlashAttribute("sending", "success");
            System.out.println("dto MainController: " + dtoTransaction);
````


если это просто транзакция, то транзакция просто отправляется в сеть

````

             AllTransactions.addTransaction(dtoTransaction);
            String jsonDto = UtilsJson.objToStringJson(dtoTransaction);
            for (String s : Seting.ORIGINAL_ADDRESSES) {

                String original = s;
                String url = s +"/addTransaction";
                //если адресс совпадает с внутреним хостом, то не отправляет самому себе
                if(BasisController.getExcludedAddresses().contains(url)){
                    System.out.println("MainController: its your address or excluded address: " + url);
                    continue;
                }
                try {
                    //отправка в сеть 
                    UtilUrl.sendPost(jsonDto, url);

                }catch (Exception e){
                    System.out.println("exception discover: " + original);

                }
            }

````

класс AllTransaction.java хранит в файлах как уже добавленые в блок транзакции,
так и транзакции которые еще не добыты данным локальным сервером.

класс рассположен в 

````
    src/main/java/network/AllTransaction.java
````

метод который добавляет в файл транзакцию

````
      public static synchronized void addTransaction(DtoTransaction transaction) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {

        instance = getInstance();
        instance.add(transaction);
        Mining.deleteFiles(Seting.ORGINAL_ALL_TRANSACTION_FILE);
        //берет только уникальные транзакции
        instance = instance.stream().filter(UtilsUse.distinctByKey(DtoTransaction::toSign)).collect(Collectors.toList());
        for (DtoTransaction dtoTransaction : instance) {
            UtilsTransaction.saveAllTransaction(dtoTransaction, Seting.ORGINAL_ALL_TRANSACTION_FILE);
        }


    }
````

за отправку отвечает класс UtilUrl.java 
расположенный 

````
    src/main/java/utils/UtilUrl.java
````

метод отправки

````
    public static int sendPost(String jsonObject, String requestStr) throws IOException {
        int response;
        URL url = new URL(requestStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    //        conn.connect();
    conn.setReadTimeout(10000);
    conn.setConnectTimeout(15000);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; utf-8");
    conn.setRequestProperty("Accept", "application/json");
    conn.setDoOutput(true);



        try(OutputStream outputStream = conn.getOutputStream()) {
            byte[] input = jsonObject.getBytes("utf-8");
            outputStream.write(input, 0, input.length);
             response = conn.getResponseCode();

        }


        conn.connect();
        return response;
    }
````