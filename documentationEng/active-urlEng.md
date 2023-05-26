# Активные url


## Локальные url


- http://localhost:8082/mine создает блок
    - ***@GetMapping("/mine")
      public synchronized ResponseEntity<String> mine()***
- http://localhost:8082/chain
    - ***@GetMapping("/chain")
      @ResponseBody
      public EntityChain full_chain()*** возвращает блокчейн ввиду класса ***EntityChain***
- http://localhost:8082/sub-blocks
    - возвращает список блоков в диапазоне от start до finish
      ***@PostMapping("/sub-blocks")
      @ResponseBody
      public List<Block> subBlocks(@RequestBody SubBlockchainEntity entity)***
    - В качестве аргумента принимает json object class ***SubBlockchainEntity.java***
    - class находится в ***src/main/java/International_Trade_Union/entity/SubBlockchainEntity.java***
- http://localhost:8082/block
    - возвращает блок по индексу  ***@PostMapping("/block")
      @ResponseBody
      public Block getBlock(@RequestBody Integer index)***
- http://localhost:8082/nodes/resolve
    - подключается к внешним хостам,
      и если находит более длинный блокчейн, чем в локальном, скачивает перезаписывает
      все данные.
      ***@GetMapping("/nodes/resolve")
      public synchronized void resolve_conflicts()***
- http://localhost:8082/nodes/register
    - Регистрирует новый внешний хост.
      ***@RequestMapping(method = RequestMethod.POST, value = "/nodes/register", consumes = MediaType.APPLICATION_JSON_VALUE)
      public synchronized void register_node(@RequestBody AddressUrl urlAddrress)***
    - В качестве Аргумента берет класс AddressUrl
- http://localhost:8082/addBlock
    - Пересохраняет весь список блоков, предварительно заново пересчитывав,
      баланс, голоса и другие данные, так же все эти данные заново сохраняет
      ***@GetMapping("/addBlock")
      public boolean getBLock()***
- http://localhost:8082/getNodes
  Возвращяет список хостов, сохраненных на локальном сервере
  ***@GetMapping("/getNodes")
  public Set<String> getAllNodes()***
- http://localhost:8082/moreMining
    - Запускает автоматический цикл майнинга, цикл будет идти 2000 шагов.
      ***@GetMapping("/moreMining")
      public void moreMining()***
- http://localhost:8082/checkValidation
  Проверяет целостность блокчейна
  ***@GetMapping("/checkValidation")
  public boolean checkValidation()***
- http://localhost:8082/addTransaction
  добавляет транзакцию в список не добавленных транзакций в блок,
  сохраняется внутренний файловую систему.
  *** @RequestMapping(method = RequestMethod.POST, value = "/addTransaction", consumes = MediaType.APPLICATION_JSON_VALUE)
  public  void add(@RequestBody DtoTransaction data)***


## Внешние url
внешние url аналогичны локальным, но начинаются
с http://194.87.236.238:80
и там отсутствует http://194.87.236.238:80/mine
так же там есть дополнительный метод
http://194.87.236.238:80/getTransactions он возвращяет список
***@GetMapping("/getTransactions")
public List<DtoTransaction> getTransaction()***
находиться DtoTransaction в
***src/main/java/International_Trade_Union/entity/DtoTransaction/DtoTransaction.java***


Отправляет с локального хоста во внешнее хранилище, весь блокчейн
***@PostMapping("/nodes/resolve_all_blocks")
public synchronized ResponseEntity<String>resolve_blocks_conflict(@RequestBody List<Block> blocks)***
***http://194.87.236.238:80/nodes/resolve_all_blocks***

Отправляет с локального хоста во внешнее хранилище от выбранного индекса, до выбранного индекса
***@PostMapping("/nodes/resolve_from_to_block")
public synchronized ResponseEntity<String> resolve_conflict(@RequestBody List<Block> blocks)***
***http://194.87.236.238:80/nodes/resolve_from_to_block***
````java

@JsonAutoDetect
@Data
public class DtoTransaction {
  private String sender;
  private String customer;
  private double digitalDollar;
  private double digitalStockBalance;
  private Laws laws;
  private double bonusForMiner;
  private VoteEnum voteEnum;
  private byte[] sign;



  public DtoTransaction(String sender, String customer, double digitalDollar, double digitalStockBalance, Laws laws, double bonusForMiner, VoteEnum voteEnum) {
    this.sender = sender;
    this.customer = customer;
    this.digitalDollar = digitalDollar;
    this.digitalStockBalance = digitalStockBalance;
    this.laws = laws;
    this.bonusForMiner = bonusForMiner;
    this.voteEnum = voteEnum;
  }

  public DtoTransaction() {
  }

  //TODO возможно стоит перевести проверку подписи в отдельный utils, под вопросом!!
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

  public String toSign(){
    String sha = sender + customer + digitalDollar + digitalStockBalance + laws + bonusForMiner;
    return UtilsUse.sha256hash(sha);
  }

//    public String hashForBlock() throws IOException {
//        return UtilsUse.sha256hash(jsonString());
//    }

  public String jsonString() throws IOException {
    return UtilsJson.objToStringJson(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DtoTransaction)) return false;
    DtoTransaction that = (DtoTransaction) o;
    return Double.compare(that.getDigitalDollar(), getDigitalDollar()) == 0 && Double.compare(that.getDigitalStockBalance(), getDigitalStockBalance()) == 0 && Double.compare(that.getBonusForMiner(), getBonusForMiner()) == 0 && getSender().equals(that.getSender()) && getCustomer().equals(that.getCustomer()) && getLaws().equals(that.getLaws()) && getVoteEnum() == that.getVoteEnum() && Arrays.equals(getSign(), that.getSign());
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(getSender(), getCustomer(), getDigitalDollar(), getDigitalStockBalance(), getLaws(), getBonusForMiner(), getVoteEnum());
    result = 31 * result + Arrays.hashCode(getSign());
    return result;
  }
}

````


класс EntityChain

***src/main/java/International_Trade_Union/entity/EntityChain.java***
````java

    @Data
    public class EntityChain {

    private int size;
    private List<Block> blocks;

    public EntityChain() {
    }

    public EntityChain(int sizeBlockhain, List<Block> blockchainList) {
    this.size = sizeBlockhain;
    this.blocks = blockchainList;
    }
  }
````

***src/main/java/International_Trade_Union/entity/SubBlockchainEntity***

````java

@Data
public class SubBlockchainEntity {
    private int start;
    private int finish;

    public SubBlockchainEntity(int start, int finish) {
        this.start = start;
        this.finish = finish;
    }

    public SubBlockchainEntity() {
    }
}

````

***src/main/java/International_Trade_Union/entity/AddressUrl***
````java
  @Data
public class AddressUrl {
  private String address;

  public AddressUrl() {
  }

  public AddressUrl(String address) {
    this.address = address;
  }
}
  ````
[Возврат на главную](./documentationEng.md)