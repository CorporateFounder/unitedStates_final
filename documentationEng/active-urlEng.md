# Active urls


## Local urls


- http://localhost:8082/sub-blocks
    - returns a list of blocks in the range from start to finish
      ***@PostMapping("/sub-blocks")
      @ResponseBody
      public List<Block> subBlocks(@RequestBody SubBlockchainEntity entity)***
    - Takes json object class ***SubBlockchainEntity.java*** as an argument
    - class is in ***src/main/java/International_Trade_Union/entity/SubBlockchainEntity.java***
- http://localhost:8082/block
    - returns a block by index ***@PostMapping("/block")
      @ResponseBody
      public Block getBlock(@RequestBody Integer index)***
- http://localhost:8082/nodes/resolve
    - connects to external hosts,
      and if it finds a longer blockchain than in the local one, it downloads and overwrites
      all data.
      ***@GetMapping("/nodes/resolve")
      public synchronized void resolve_conflicts()***
- http://localhost:8082/nodes/register
    - Registers a new external host.
      ***@RequestMapping(method = RequestMethod.POST, value = "/nodes/register", consumes = MediaType.APPLICATION_JSON_VALUE)
      public synchronized void register_node(@RequestBody AddressUrl urlAddrress)***
    - Takes the AddressUrl class as an Argument
- http://localhost:8082/addBlock
    - saves recalculating the entire list of blocks, after recalculating,
      balance, votes and other data, it also saves all this data again
      ***@GetMapping("/addBlock")
      public boolean getBLock()***
- http://localhost:8082/getNodes
  Returns a list of hosts stored on the local server
  ***@GetMapping("/getNodes")
  public Set<String> getAllNodes()***
- http://localhost:8082/moreMining
    - Starts an automatic mining cycle, the cycle will go 2000 steps.
      ***@GetMapping("/moreMining")
      public void moreMining()***
- http://localhost:8082/checkValidation
  Verifies the integrity of the blockchain
  ***@GetMapping("/checkValidation")
  public boolean checkValidation()***
- http://localhost:8082/addTransaction
  adds a transaction to the list of transactions not added to the block,
  the internal file system is saved.
  *** @RequestMapping(method = RequestMethod.POST, value = "/addTransaction", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void add(@RequestBody DtoTransaction data)***


## External urls
external urls are similar to local ones, but start
from http://194.87.236.238:80
and there is no http://194.87.236.238:80/mine
there is also an additional method
http://194.87.236.238:80/getTransactions it returns a list
***@GetMapping("/getTransactions")
public List<DtoTransaction> getTransaction()***
be DtoTransaction in
***src/main/java/International_Trade_Union/entity/DtoTransaction/DtoTransaction.java***


Sends from localhost to external storage, entire blockchain
***@PostMapping("/nodes/resolve_all_blocks")
public synchronized ResponseEntity<String>resolve_blocks_conflict(@RequestBody List<Block> blocks)***
***http://194.87.236.238:80/nodes/resolve_all_blocks***

Sends from localhost to external storage from the selected index, to the selected index
***@PostMapping("/nodes/resolve_from_to_block")
public synchronized ResponseEntity<String> resolve_conflict(@RequestBody List<Block> blocks)***
***http://194.87.236.238:80/nodes/resolve_from_to_block***



get pub end priv key
http://localhost:8082/keys

Update local blockchain
http://localhost:8082/updating

Get balance address
http://localhost:8082/account

Get dollar balance
http://localhost:8082/dollar

Get stock balance
http://localhost:8082/stock

Send amount
http://localhost:8082/sendCoin

@GetMapping("/sendCoin")
public String send(@RequestParam String sender,
@RequestParam String recipient,
@RequestParam Double dollar,
@RequestParam Double stock,
@RequestParam Double reward,
@RequestParam String password)

Get block by index
@GetMapping("/conductorBlock")
@ResponseBody
public Block block(@RequestParam Integer index)

Get a transaction by its hash
@GetMapping("/conductorHashTran")
@ResponseBody
public DtoTransaction transaction(@RequestParam String hash)
````java

@JsonAutoDetect
@Data
public class DtoTransaction {
   private String sender;
   private String customer;
   private double digitalDollar;
   private double digitalStockBalance;
   private law laws;
   private double bonusForMiner;
   private VoteEnum voteEnum;
   private byte[]sign;



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

   //TODO perhaps it is worth transferring the signature check to a separate utils, questionable!!
   public boolean verify() throws IOException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
     Base base = new Base58();
     byte[]pub = base.decode(sender);
     BCECPublicKey publicKey = (BCECPublicKey) UtilsSecurity.decodeKey(pub);
// PublicKey publicKey = UtilsSecurity.publicByteToPublicKey(pub);
     String sha = sender + customer + digitalDollar + digitalStockBalance + laws + bonusForMiner;
     sha = UtilsUse.sha256hash(sha);
     if(sender.isBlank() || customer.isBlank() || digitalDollar < 0 || digitalStockBalance < 0 || bonusForMiner < 0|| laws == null){
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

// public String hashForBlock() throws IOException {
// return UtilsUse.sha256hash(jsonString());
// }

   public String jsonString() throws IOException {
     return UtilsJson.objToStringJson(this);
   }

   @Override
   public boolean equals(Object o) {
     if (this == o) return true;
     if (!(o instanceof DtoTransaction)) return false;
     DtoTransaction that = (DtoTransaction) o;
     return Double.compare(that.getDigitalDollar(), getDigitalDollar()) == 0 && Double.compare(that.getDigitalStockBalance(), getDigitalStockBalance()) == 0 && Double.compare(that.getBonusForMiner(), getBonusForMiner() ) == 0 && getSender().equals(that.getSender()) && getCustomer().equals(that.getCustomer()) && getLaws().equals(that.getLaws()) && getVoteEnum() == that .getVoteEnum() && Arrays.equals(getSign(), that.getSign());
   }

   @Override
   public int hashCode() {
     int result = Objects.hash(getSender(), getCustomer(), getDigitalDollar(), getDigitalStockBalance(), getLaws(), getBonusForMiner(), getVoteEnum());
     result = 31 * result + Arrays.hashCode(getSign());
     return result;
   }
}

````


EntityChain class

***src/main/java/International_Trade_Union/entity/EntityChain.java***
````java

     @Data
     public class EntityChain {

     private int size;
     private List<Block> blocks;

     public EntityChain() {
     }

     public EntityChain(int sizeBlockchain, List<Block> blockchainList) {
     this.size = sizeBlockchain;
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
     this address = address;
   }
}
   ````
[Return to main page](./documentationEng.md)