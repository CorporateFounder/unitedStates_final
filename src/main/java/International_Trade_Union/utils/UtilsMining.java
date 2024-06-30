package International_Trade_Union.utils;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.SendBlocksEndInfo;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.services.BlockService;
import International_Trade_Union.governments.Directors;
import International_Trade_Union.governments.UtilsGovernment;
import International_Trade_Union.model.Account;
import International_Trade_Union.model.Mining;
import International_Trade_Union.model.User;
import International_Trade_Union.network.AllTransactions;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.base.Base;
import International_Trade_Union.utils.base.Base58;
import International_Trade_Union.vote.Laws;
import International_Trade_Union.vote.VoteEnum;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static International_Trade_Union.controllers.BasisController.findAddresses;
import static International_Trade_Union.controllers.BasisController.getNodes;
import static International_Trade_Union.setings.Seting.SPECIAL_FORK_BALANCE;

public class UtilsMining {
    @Autowired
    BlockService blockService;

    @Autowired
    Mining mining;
    public static Block miningDay(
            Account minner,
            List<Block> blockchain,
            long blockGenerationInterval,
            int DIFFICULTY_ADJUSTMENT_INTERVAL,
            List<DtoTransaction> transactionList,
            Map<String, Account> balances,
            long index
    ) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, NoSuchProviderException, InvalidKeyException {
        Directors directors = new Directors();
        //получение транзакций с сети
        List<DtoTransaction> listTransactions = transactionList;

        //определение валидных транзакций
        List<DtoTransaction> forAdd = new ArrayList<>();

        //проверяет целостность транзакции, что они подписаны правильно
        cicle:
        for (DtoTransaction transaction : listTransactions) {
            try {
                if (transaction.verify()) {

                    Account account = balances.get(transaction.getSender());
                    if (account == null) {
                        System.out.println("minerAccount null");
                        continue cicle;
                    }
                    //NAME_LAW_ADDRESS_START если адресс  означает правила выбранные сетью
                    if (transaction.getCustomer().startsWith(Seting.NAME_LAW_ADDRESS_START) && !balances.containsKey(transaction.getCustomer())) {
                        //если в названия закона совпадает с корпоративными должностями, то закон является действительным только когда
                        //отправитель совпадает с законом
//                    List<Director> enumPosition = directors.getDirectors();
                        List<String> corporateSeniorPositions = directors.getDirectors().stream()
                                .map(t -> t.getName()).collect(Collectors.toList());
                        System.out.println("LawsController: create_law: " + transaction.getLaws().getPacketLawName()
                                + "contains: " + corporateSeniorPositions.contains(transaction.getLaws().getPacketLawName()));
                        if (corporateSeniorPositions.contains(transaction.getLaws().getPacketLawName())
                                && !UtilsGovernment.checkPostionSenderEqualsLaw(transaction.getSender(), transaction.getLaws())) {
                            System.out.println("if your create special corporate position, you need " +
                                    "sender to be equals with first law: now its wrong");
                            continue cicle;
                        }
                    }
                    if (transaction.getLaws() == null) {
                        System.out.println("law cannot to be null: ");
                        continue cicle;
                    }

                    if (account != null) {
                        if (transaction.getSender().equals(Seting.BASIS_ADDRESS)) {
                            System.out.println("only this miner can input basis adress in this block");
                            continue cicle;
                        }
                        if (transaction.getCustomer().equals(Seting.BASIS_ADDRESS)) {
                            System.out.println("basis address canot to be customer(recipient)");
                            continue cicle;
                        }

                        if(transaction.getVoteEnum().equals(VoteEnum.STAKING)
                                || transaction.getVoteEnum().equals(VoteEnum.YES)
                                || transaction.getVoteEnum().equals(VoteEnum.NO) ){
                            if (account.getDigitalDollarBalance() < transaction.getDigitalDollar() + transaction.getBonusForMiner()) {
                                System.out.println("sender don't have digital dollar: " + account.getAccount() + " balance: " + account.getDigitalDollarBalance());
                                System.out.println("digital dollar for send: " + (transaction.getDigitalDollar() + transaction.getBonusForMiner()));
                                continue cicle;
                            }
                        }

                        if (account.getDigitalStockBalance() < transaction.getDigitalStockBalance()) {
                            System.out.println("sender don't have digital reputation: " + account.getAccount() + " balance: " + account.getDigitalStockBalance());
                            System.out.println("digital reputation for send: " + (transaction.getDigitalDollar() + transaction.getBonusForMiner()));
                            continue cicle;
                        }

                        forAdd.add(transaction);
                    }

                }
            }catch (IOException e){
                e.printStackTrace();
                continue;
            }
        }

        long difficulty = UtilsBlock.difficulty(blockchain, blockGenerationInterval, DIFFICULTY_ADJUSTMENT_INTERVAL);
        if(index >= Seting.V34_NEW_ALGO){
            difficulty = Mining.getCustomDiff();
        }

        Block prevBlock = blockchain.get(blockchain.size()-1);


        //доход майнера
        double minerRewards = Seting.DIGITAL_DOLLAR_REWARDS_BEFORE;
        double digitalReputationForMiner = Seting.DIGITAL_STOCK_REWARDS_BEFORE;

        //доход основателя
        double founderReward = Seting.DIGITAL_DOLLAR_FOUNDER_REWARDS_BEFORE;
        double founderDigigtalReputationReward = Seting.DIGITAL_REPUTATION_FOUNDER_REWARDS_BEFORE;
        if(index > Seting.CHECK_UPDATING_VERSION){
            if(difficulty >= 8){
                founderReward = difficulty;
                founderDigigtalReputationReward = difficulty;
            }
            else {
                founderReward = 8;
                founderDigigtalReputationReward = 8;
            }

        }
        if(index > Seting.CHECK_UPDATING_VERSION) {
            minerRewards = difficulty * Seting.MONEY;
            digitalReputationForMiner= difficulty * Seting.MONEY;
            minerRewards += index%2 == 0 ? 0 : 1;
            digitalReputationForMiner += index%2 == 0 ? 0 : 1;
        }

        if(index > Seting.V28_CHANGE_ALGORITH_DIFF_INDEX && index < Seting.V34_NEW_ALGO){
            long money = (index - Seting.V28_CHANGE_ALGORITH_DIFF_INDEX)
                    / (576 * Seting.YEAR);
            money = (long) (Seting.MULTIPLIER - money);
            money = money < 1 ? 1: money;


            double G = UtilsUse.blocksReward(forAdd, prevBlock.getDtoTransactions());
            minerRewards = (Seting.V28_REWARD + G) * money;
            digitalReputationForMiner = (Seting.V28_REWARD + G) * money;
            founderReward = minerRewards/Seting.DOLLAR;
            founderDigigtalReputationReward = digitalReputationForMiner/Seting.STOCK;
        }
        if( index >= Seting.V34_NEW_ALGO){
            long money = (index - Seting.V28_CHANGE_ALGORITH_DIFF_INDEX)
                    / (576 * Seting.YEAR);
            money = (long) (Seting.MULTIPLIER - money);
            money = money < 1 ? 1: money;


            double G = UtilsUse.blocksReward(forAdd, prevBlock.getDtoTransactions());
            minerRewards = (Seting.V28_REWARD + G + (difficulty * Seting.V34_MINING_REWARD)) * money;
            digitalReputationForMiner = (Seting.V28_REWARD + G + (difficulty * Seting.V34_MINING_REWARD))* money;
            founderReward = minerRewards/Seting.DOLLAR;
            founderDigigtalReputationReward = digitalReputationForMiner/Seting.STOCK;

            if(BasisController.getBlockchainSize() > Seting.START_BLOCK_DECIMAL_PLACES){
                minerRewards = UtilsUse.round(minerRewards, Seting.DECIMAL_PLACES);
                digitalReputationForMiner = UtilsUse.round(digitalReputationForMiner, Seting.DECIMAL_PLACES);
                founderReward = UtilsUse.round(founderReward, Seting.DECIMAL_PLACES);
                founderDigigtalReputationReward = UtilsUse.round(founderDigigtalReputationReward, Seting.DECIMAL_PLACES);
            }
        }
        Base base = new Base58();

        //суммирует все вознаграждения майнеров
        PrivateKey privateKey = UtilsSecurity.privateBytToPrivateKey(base.decode(Seting.BASIS_PASSWORD));
        double sumRewards = forAdd.stream().collect(Collectors.summingDouble(DtoTransaction::getBonusForMiner));




        String addressFounrder = Blockchain.indexFromFile(0, Seting.ORIGINAL_BLOCKCHAIN_FILE).getFounderAddress();
        if(!addressFounrder.equals(prevBlock.getFounderAddress())) {
            System.out.println("wrong founder address: " );
            return null;
        }
        //вознаграждение основателя
        DtoTransaction founderRew = new DtoTransaction(Seting.BASIS_ADDRESS, addressFounrder,
                founderReward, founderDigigtalReputationReward, new Laws(), 0.0, VoteEnum.YES);
        byte[] signFounder = UtilsSecurity.sign(privateKey, founderRew.toSign());

        founderRew.setSign(signFounder);



        forAdd.add(founderRew);


        //здесь должна быть создана динамическая модель
        //определение сложности и создание блока



        System.out.println("Mining: miningBlock: difficulty: " + difficulty + " index: " + index);



        if(index == Seting.SPECIAL_BLOCK_FORK && minner.getAccount().equals(Seting.FORK_ADDRESS_SPECIAL)){
            minerRewards = SPECIAL_FORK_BALANCE;
            digitalReputationForMiner = SPECIAL_FORK_BALANCE;
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
                prevBlock.getHashBlock(),
                minner.getAccount(),
                addressFounrder,
                difficulty,
                index);


        return block;
    }
    public static int sendAllBlocksToStorage(List<Block> blocks) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, NoSuchProviderException, InvalidKeyException {

        System.out.println(new Date() + ":BasisController: sendAllBlocksToStorage: start: ");
        int bigsize = 0;
        int blocks_current_size = (int) blocks.get(blocks.size() - 1).getIndex() + 1;
        //отправка блокчейна на хранилище блокчейна
        System.out.println(":BasisController: sendAllBlocksToStorage: ");
        getNodes().stream().forEach(System.out::println);
        for (String s : getNodes()) {

            System.out.println(":trying to connect to the server send block: " + s + ": timeout 45 seconds");

            if (BasisController.getExcludedAddresses().contains(s)) {
                System.out.println(":its your address or excluded address: " + s);
                continue;
            }

            try {
                System.out.println(":BasisController:resolve conflicts: address: " + s + "/size");
                String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                Integer size = 0;
                if (Integer.valueOf(sizeStr) > 0)
                    size = Integer.valueOf(sizeStr);
                System.out.println(":BasisController: send: local size: " + blocks_current_size + " global size: " + size);
                if (size > blocks_current_size) {
                    System.out.println(":your local chain less: current: " + blocks_current_size + " global: " + size);
                    return -1;
                }
//                List<Block> fromToTempBlock = blocks.subList(size, blocks_current_size);
                List<Block> fromToTempBlock = new ArrayList<>();
                fromToTempBlock.addAll(blocks);
                SendBlocksEndInfo infoBlocks = new SendBlocksEndInfo(Seting.VERSION, fromToTempBlock);
                String jsonFromTo = UtilsJson.objToStringJson(infoBlocks);
                //if the current blockchain is larger than the storage, then
                //send current blockchain send to storage
                //если блокчейн текущей больше чем в хранилище, то
                //отправить текущий блокчейн отправить в хранилище
                if (size < blocks_current_size) {
                    if (bigsize < size) {
                        bigsize = size;
                    }
                    int response = -1;
                    //Test start algorithm
                    String originalF = s;
                    System.out.println(":send resolve_from_to_block");
                    String urlFrom = s + "/nodes/resolve_from_to_block";
                    try {
                        response = UtilUrl.sendPost(jsonFromTo, urlFrom);
                        System.out.println(":CONFLICT TREE, IN GLOBAL DIFFERENT TREE " + HttpStatus.CONFLICT.value());
                        System.out.println(":GOOD: SUCCESS  " + HttpStatus.OK.value());
                        System.out.println(":FAIL BAD BLOCKCHAIN: " + HttpStatus.EXPECTATION_FAILED.value());
                        System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                        System.out.println(":response: " + response);
                    } catch (Exception e) {
                        System.out.println(":exception resolve_from_to_block: " + originalF);

                    }
                    System.out.println(":CONFLICT TREE, IN GLOBAL DIFFERENT TREE: " + HttpStatus.CONFLICT.value());
                    System.out.println(":GOOD SUCCESS: " + HttpStatus.OK.value());
                    System.out.println(":FAIL BAD BLOCKHAIN: " + HttpStatus.EXPECTATION_FAILED.value());
                    System.out.println(":CONFLICT VERSION: " + HttpStatus.FAILED_DEPENDENCY.value());
                    System.out.println(":NAME CONFLICT: " + HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println("two miner addresses cannot be consecutive: " + HttpStatus.NOT_ACCEPTABLE.value());
                    System.out.println("PARITY ERROR" + HttpStatus.LOCKED);
                    System.out.println("Test version: If the index is even, then the stock balance must also be even; if the index is not even, all can mining"
                            + HttpStatus.LOCKED.value());
                    System.out.println("BLOCK HAS CHEATER ADDRESS: " + HttpStatus.SEE_OTHER);
                    System.out.println(":response: " + response);

                    System.out.println(":BasisController: sendAllBlocksStorage: response: " + response);


                    //there is an up-to-date branch on the global server, download it and delete the obsolete branch.
                    //на глобальном сервере есть актуальная ветка, скачать ее и удалить устревшую ветку.
//                    if (response == HttpStatus.CONFLICT.value()) {
//                        System.out.println(":BasisController: sendAllBlocksStorage: start deleted 50 blocks:");
//                        System.out.println(":size before delete: " + blockchainSize);
//                       Blockchain blockchain1 = Mining.getBlockchain(
//                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                                BlockchainFactoryEnum.ORIGINAL);
//                        List<Block> temporary = blockchain1.subBlock(0, blockchainSize - Seting.DELETED_PORTION);
//                        UtilsBlock.deleteFiles();
//                        blockchain1.setBlockchainList(temporary);
//                        UtilsBlock.saveBlocks(blockchain1.getBlockchainList(), Seting.ORIGINAL_BLOCKCHAIN_FILE);
//                        blockchain1 = Mining.getBlockchain(
//                                Seting.ORIGINAL_BLOCKCHAIN_FILE,
//                                BlockchainFactoryEnum.ORIGINAL);
//
//                        shortDataBlockchain = Blockchain.checkFromFile(Seting.ORIGINAL_BLOCKCHAIN_FILE);
//                        blockchainSize = (int) shortDataBlockchain.getSize();
//                        blockchainValid = shortDataBlockchain.isValidation();
//
//                        UtilsBlock.deleteFiles();
//                        addBlock(blockchain1.getBlockchainList());
//                        System.out.println(":size after delete: " + blockchainSize);
//
//
//                        int result = resolve();
//                        System.out.println(":resolve: updated: " + result);
//                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
                continue;

            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

        }
        if (BasisController.getBlockchainSize() > bigsize) {
            return 1;
        } else if (BasisController.getBlockchainSize() < bigsize) {
            return -1;
        } else if (BasisController.getBlockchainSize() == bigsize) {
            return 0;
        } else {
            return -4;
        }
    }
}
