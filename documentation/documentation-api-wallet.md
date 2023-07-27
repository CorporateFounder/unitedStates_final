# Документация апи кошелька
В качестве визуальной части используется bootstrap и thymeleaf

## home.html (main page)

```` html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}"/>
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.2/dist/css/bootstrap.min.css">
</head>
<body>

<header th:insert="blocks/header :: header "></header>
<div class="container mt 5">
    <h1 th:text="${title}"></h1>
</div>

<div  class="alert alert-info mt-2">
    <h4 th:text="'miner address: '+${account.account}"/>
    <p th:text="'digital dollar: '+${#numbers.formatDecimal(account.digitalDollarBalance, 1, 'COMMA', 16, 'POINT')}"/>
    <p th:text="'digital stock: '+${#numbers.formatDecimal(account.digitalStockBalance, 1, 'COMMA', 16, 'POINT')}"/>
    <p th:text="': '+${info}"/>
    <p th:text="': '+${info2}"/>
    <p th:text="': '+${demerage}"/>


</div>
<div  class="alert alert-info mt-2">
    <h4 th:text="'blockchain local size: ' + ${size}"/>
    <h4 th:text="'blockchain global size: ' + ${globalSize}"/>
    <h4 th:text="'local version: ' + ${version}"/>
    <h4 th:text="'global version: ' + ${global_version}"/>
    <h4 th:text="'local validation: ' + ${validation}"/>
    <h4 th:text="'the current difficulty of one block.: '+${difficultOneBlock}"/>
    <h4 th:text="'the current complexity of the blockchain.: '+${difficultAllBlockchain}"/>

</div>

<div class="container mt-5 mb-5">
    <form action="/" method="post">
        <input type="text" name="sender" placeholder="enter the sender's address" class="form-control"><br>
        <input type="text" name="recipient" placeholder="enter the recipient's address" class="form-control"><br>
        <input type="text" name="dollar" placeholder="enter the number of digital dollars to send" class="form-control"><br>
        <input type="text" name="stock" placeholder="enter the number of digital shares to send" class="form-control"><br>
        <input type="text" name="reward" placeholder="miner reward" class="form-control"><br>


        <textarea name="password" placeholder="enter private key" th:rows="8" th:cols="120"></textarea><br>
        <button type="submit" class="btn btn-success">Send money</button><br>
    </form>


</div>

<div class="container mt-5 mb-5">
    <form action="/resolving" method="get">
        <button type="submit" class="btn btn-success">Update blockchain</button><br>
    </form>
</div>
<div class="container mt-5 mb-5">
    <form action="/sendBlocks" method="get">
        <button type="submit" class="btn btn-success">send blockchain</button><br>
    </form>
</div>

<footer th:insert="blocks/footer :: footer"></footer>
</body>
</html>
````

Что добавляется в Model 
````
//Заголовок
model.addAttribute("title", "Corporation International Trade Union.");
//Object Account 
//account.account строка адресса
<h4 th:text="'miner address: '+${account.account}"/>
//строка баланса в долларах account.digitalDollarBalance
    <p th:text="'digital dollar: '+${#numbers.formatDecimal(account.digitalDollarBalance, 1, 'COMMA', 16, 'POINT')}"/>
    //строка баланса в акциях account.digitalStockBalance
    <p th:text="'digital stock: '+${#numbers.formatDecimal(account.digitalStockBalance, 1, 'COMMA', 16, 'POINT')}"/>
    // model.addAttribute("info", "In this system, a year is 360 days, and a day is 576 blocks.");

     <p th:text="': '+${info}"/>
    <p th:text="': '+${info2}"/>
    //показывает дни, на 180 дне снимается со всех счетов деньги.
    <p th:text="': '+${demerage}"/>
    //локальный размер
     <h4 th:text="'blockchain local size: ' + ${size}"/>
     //размер на хранилище
    <h4 th:text="'blockchain global size: ' + ${globalSize}"/>
    //версия локального блокчейна
    <h4 th:text="'local version: ' + ${version}"/>
    версия глобального блокчейна
    <h4 th:text="'global version: ' + ${global_version}"/>
    //является ли локальный блокчейн целым
    <h4 th:text="'local validation: ' + ${validation}"/>
    //сложность одного блока
    <h4 th:text="'the current difficulty of one block.: '+${difficultOneBlock}"/>
    //сумма сложностей всех блоков
    <h4 th:text="'the current complexity of the blockchain.: '+${difficultAllBlockchain}"/>
````
````
 @GetMapping("/")
    public String home(Model model) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        if(BasisController.isUpdating() || BasisController.isMining()){
            return "redirect:/processUpdating";
        }
        String sizeStr = "-1";
        try {
            sizeStr = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/size");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }catch (SocketException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give size global server");
            sizeStr = "-1";
        }
        Integer sizeG = Integer.valueOf(sizeStr);
        globalSize = sizeG;
        model.addAttribute("title", "Corporation International Trade Union.");
        if(sizeStr.isEmpty() || sizeStr.isBlank() || sizeStr.equals("-1")){
            model.addAttribute("globalSize", "global server data was not received because the server was unable to connect: " + sizeStr);
        }else {
            model.addAttribute("globalSize", Integer.toString(globalSize));
        }


        String versionStr = "-1";
        try {
            versionStr = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/version");
        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give version global server");
            versionStr = "-1";
        }catch (SocketException e){
            System.out.println("," +
                    "you can't give version global server");
            versionStr = "-1";
        }

        InfoDificultyBlockchain infoDificultyBlockchain = new InfoDificultyBlockchain(-1, -1);
        String difficultOneBlock =  ":";
        String difficultAllBlockchain = ":";
        try {
            String json = UtilUrl.readJsonFromUrl("http://194.87.236.238:80" + "/difficultyBlockchain");
             infoDificultyBlockchain = UtilsJson.jsonToInfoDifficulty(json);

        }catch (NoRouteToHostException e){
            System.out.println("home page you cannot connect to global server," +
                    "you can't give difficulty global server");

        }catch (SocketException e){
            System.out.println("," +
                    "you can't give difficulty global server");
        }

        difficultOneBlock = Long.toString(infoDificultyBlockchain.getDiffultyOneBlock());
        difficultAllBlockchain = Long.toString(infoDificultyBlockchain.getDifficultyAllBlockchain());


        model.addAttribute("global_version", versionStr);
        model.addAttribute("difficultOneBlock",difficultOneBlock);
        model.addAttribute("difficultAllBlockchain",  difficultAllBlockchain);

        Blockchain blockchain = Mining.getBlockchain(
                Seting.ORIGINAL_BLOCKCHAIN_FILE,
                BlockchainFactoryEnum.ORIGINAL);
        int size = blockchain.sizeBlockhain();
        Map<String, Account> balances = new HashMap<>();

        model.addAttribute("size", size);
        model.addAttribute("version", Seting.VERSION);
        boolean validation = blockchain.validatedBlockchain();

        if(validation == false){
            System.out.println("deleted blockchain files");

//            System.exit(1);
            UtilsBlock.deleteFiles();
        }
        model.addAttribute("validation", validation);


        //догрузить блокчейн
        balances = SaveBalances.readLineObject(Seting.ORIGINAL_BALANCE_FILE);


        Account account = UtilsBalance.getBalance(User.getUserAddress(), balances);
        model.addAttribute("account", account);

        //дата сколько осталось до уничтожения монет
        Block block =  blockchain.getBlock(blockchain.sizeBlockhain()-1);
        model.addAttribute("info", "In this system, a year is 360 days, and a day is 576 blocks.");
        model.addAttribute("info2", "every 180 days, 0.2% of digital dollars and 0.4% of digital shares" +
                " are withdrawn from the account.");
        model.addAttribute("demerage", "now is the day: ");
        if(block.getIndex() != 0){
           int day = (int) (block.getIndex() / Seting.COUNT_BLOCK_IN_DAY % (Seting.YEAR / Seting.HALF_YEAR));
            model.addAttribute("demerage", "now is the day: " + day);
        }

        return "home";
    }
````

Отправить деньги 

````html
<div class="container mt-5 mb-5">
    <form action="/" method="post">
        <input type="text" name="sender" placeholder="enter the sender's address" class="form-control"><br>
        <input type="text" name="recipient" placeholder="enter the recipient's address" class="form-control"><br>
        <input type="text" name="dollar" placeholder="enter the number of digital dollars to send" class="form-control"><br>
        <input type="text" name="stock" placeholder="enter the number of digital shares to send" class="form-control"><br>
        <input type="text" name="reward" placeholder="miner reward" class="form-control"><br>


        <textarea name="password" placeholder="enter private key" th:rows="8" th:cols="120"></textarea><br>
        <button type="submit" class="btn btn-success">Send money</button><br>
    </form>


</div>
````

````
  @PostMapping("/")
    public String new_transaction(
            @RequestParam  String sender,
            @RequestParam  String recipient,
                                   Double dollar,
                                   Double stock,
                                   Double reward,

                                  @RequestParam  String password,
                                  RedirectAttributes redirectAttrs) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Base base = new Base58();

        if(dollar == null)
            dollar = 0.0;

        if(stock == null)
            stock = 0.0;

        if(reward == null)
            reward = 0.0;

        Laws laws =  new Laws();
        laws.setLaws(new ArrayList<>());
        laws.setHashLaw("");
        laws.setPacketLawName("");
        DtoTransaction dtoTransaction = new DtoTransaction(
                sender,
                recipient,
                dollar,
                stock,
                laws,
                reward,
                VoteEnum.YES);
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(password));
        byte[] sign = UtilsSecurity.sign(privateKey, dtoTransaction.toSign());
        System.out.println("Main Controller: new transaction: vote: " +VoteEnum.YES);
        redirectAttrs.addFlashAttribute("title", "sending result!!!");
        redirectAttrs.addFlashAttribute("sender", sender);
        redirectAttrs.addFlashAttribute("recipient", recipient);
        redirectAttrs.addFlashAttribute("dollar", dollar);
        redirectAttrs.addFlashAttribute("stock", stock);
        redirectAttrs.addFlashAttribute("reward", reward);
        redirectAttrs.addFlashAttribute("vote", VoteEnum.YES);
        dtoTransaction.setSign(sign);
        Directors directors = new Directors();
        if(dtoTransaction.verify()){

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
           String str =  base.encode(dtoTransaction.getSign());
            System.out.println("sign: " + str);
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



        }

        else
            redirectAttrs.addFlashAttribute("sending", "wrong transaction");
        return "redirect:/result-sending";
    }


````
[возврат на главную](./documentationRus.md)
