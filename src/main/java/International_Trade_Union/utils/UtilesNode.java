package International_Trade_Union.utils;

import International_Trade_Union.config.BLockchainFactory;
import International_Trade_Union.config.BlockchainFactoryEnum;
import International_Trade_Union.controllers.BasisController;
import International_Trade_Union.entity.EntityChain;
import International_Trade_Union.entity.SubBlockchainEntity;
import International_Trade_Union.entity.blockchain.Blockchain;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.model.Mining;
import International_Trade_Union.setings.Seting;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static International_Trade_Union.controllers.BasisController.addBlock;

public class UtilesNode {
    public static ResponseEntity<String> updates(boolean blockchainValid,
                            int blockchainSize,
                            Blockchain blockchain,
                            Set<String> nodes) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, SignatureException, NoSuchProviderException, InvalidKeyException, JSONException {
        System.out.println("start resolve");
        Blockchain temporaryBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        Blockchain bigBlockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        if(blockchainValid == false || blockchainSize == 0){
            blockchain = Mining.getBlockchain(
                    Seting.ORIGINAL_BLOCKCHAIN_FILE,
                    BlockchainFactoryEnum.ORIGINAL);
        }

        if(blockchainValid== false){
            System.out.println("you have wrong blockchain end deleted this: ");
            UtilsBlock.deleteFiles();
            blockchain = BLockchainFactory.getBlockchain(BlockchainFactoryEnum.ORIGINAL);
        }
        int blocks_current_size = blockchainSize;
        long hashCountZeroTemporary = 0;
        long hashCountZeroBigBlockchain = 0;
        EntityChain entityChain = null;
        System.out.println("resolve_conflicts: blocks_current_size: " + blocks_current_size);
        long hashCountZeroAll = 0;
        //count hash start with zero all
        for (Block block : blockchain.getBlockchainList()) {
            hashCountZeroAll += UtilsUse.hashCount(block.getHashBlock());
        }

        Set<String> nodesAll = nodes;
//        nodesAll.addAll(Seting.ORIGINAL_ADDRESSES_BLOCKCHAIN_STORAGE);
        System.out.println("BasisController: resolve_conflicts: size nodes: " + nodes.size());
        for (String s : nodesAll) {
            System.out.println("while resolve_conflicts: node address: " + s);
            String temporaryjson = null;

            if (BasisController.getExcludedAddresses().contains(s)) {
                System.out.println("its your address or excluded address: " + s);
                continue;
            }
            try {
                if(s.contains("localhost") || s.contains("127.0.0.1"))
                    continue;
                String address = s + "/chain";
                System.out.println("resolve_conflicts: start /size");
                System.out.println("BasisController:resolve conflicts: address: " + s + "/size");
                String sizeStr = UtilUrl.readJsonFromUrl(s + "/size");
                Integer size = Integer.valueOf(sizeStr);
                System.out.println("resolve_conflicts: finish /size: " + size);

                if (size >= blocks_current_size) {
                    System.out.println("size from address: " + s + " upper than: " + size + ":blocks_current_size " + blocks_current_size);
                    //Test start algorithm

                    List<Block> emptyList = new ArrayList<>();


                    ;
                    emptyList.addAll(blockchain.getBlockchainList());

                    emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                    temporaryBlockchain.setBlockchainList(emptyList);

                        System.out.println("first algorithm not worked");
                        emptyList = new ArrayList<>();

                        for (int i = size - 1; i > 0; i--) {
                            Block block = UtilsJson.jsonToBLock(UtilUrl.getObject(UtilsJson.objToStringJson(i), s + "/block"));
                            if(i > blockchain.sizeBlockhain()){
                                emptyList.add(block);
                            }
                            else if (!blockchain.getBlock(i).getHashBlock().equals(block.getHashBlock())) {
                                emptyList.add(block);
                            } else {
                                emptyList.add(block);
                                emptyList.addAll(blockchain.getBlockchainList().subList(0, i));
                                emptyList = emptyList.stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList());
                                temporaryBlockchain.setBlockchainList(emptyList);
                                break;
                            }
                        }

                    if (!temporaryBlockchain.validatedBlockchain()) {
                        System.out.println("second algorith not worked");
                        temporaryjson = UtilUrl.readJsonFromUrl(address);
                        entityChain = UtilsJson.jsonToEntityChain(temporaryjson);
                        temporaryBlockchain.setBlockchainList(
                                entityChain.getBlocks().stream().sorted(Comparator.comparing(Block::getIndex)).collect(Collectors.toList()));
                    }
                } else {
                    System.out.println("BasisController: resove: size less: " + size + " address: " + address);
                    continue;
                }
            } catch (IOException e) {

//                e.printStackTrace();
                System.out.println("BasisController: resolve_conflicts: connect refused Error: " + s);
                continue;
            }


            if (temporaryBlockchain.validatedBlockchain()) {
                for (Block block : temporaryBlockchain.getBlockchainList()) {
                    hashCountZeroTemporary += UtilsUse.hashCount(block.getHashBlock());
                }

                if (blocks_current_size < temporaryBlockchain.sizeBlockhain() && hashCountZeroAll < hashCountZeroTemporary) {
                    blocks_current_size = temporaryBlockchain.sizeBlockhain();
                    bigBlockchain = temporaryBlockchain;
                    hashCountZeroBigBlockchain = hashCountZeroTemporary;
                }
                hashCountZeroTemporary = 0;
            }

        }


        if (bigBlockchain.sizeBlockhain() > blockchainSize && hashCountZeroBigBlockchain > hashCountZeroAll) {

            blockchain = bigBlockchain;
            UtilsBlock.deleteFiles();
            addBlock(bigBlockchain.getBlockchainList());
            System.out.println("BasisController: resolve: bigblockchain size: " + bigBlockchain.sizeBlockhain());

        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
