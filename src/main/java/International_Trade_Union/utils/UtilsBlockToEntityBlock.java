package International_Trade_Union.utils;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.entities.EntityDtoTransaction;
import International_Trade_Union.entity.entities.EntityLaws;
import International_Trade_Union.vote.Laws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UtilsBlockToEntityBlock {
    public static EntityBlock blockToEntityBlock(Block block) {
        List<EntityDtoTransaction> entityDtoTransactions =
                dtoTransactionToEntity(block.getDtoTransactions());
        EntityBlock entityBlock = new EntityBlock(
                entityDtoTransactions,
                block.getPreviousHash(),
                block.getMinerAddress(),
                block.getFounderAddress(),
                block.getRandomNumberProof(),
                block.getMinerRewards(),
                block.getHashCompexity(),
                block.getTimestamp(),
                block.getIndex(),
                block.getHashBlock()
        );

        for (EntityDtoTransaction dtoTransaction : entityDtoTransactions) {
            dtoTransaction.setEntityBlock(entityBlock);
        }

        return entityBlock;
    }

    public static List<EntityDtoTransaction> dtoTransactionToEntity(List<DtoTransaction> list) {
        List<EntityDtoTransaction> entityDtoTransactions = new ArrayList<>();
        for (DtoTransaction transaction : list) {
            EntityLaws entityLaws = lawsToEntity(transaction.getLaws());
            EntityDtoTransaction entityDtoTransaction = new EntityDtoTransaction(
                    transaction.getSender(),
                    transaction.getCustomer(),
                    transaction.getDigitalDollar(),
                    transaction.getDigitalStockBalance(),
                    entityLaws,
                    transaction.getBonusForMiner(),
                    transaction.getVoteEnum(),
                    transaction.getSign()
            );
            entityDtoTransaction.setEntityBlock(null); // Поле будет заполнено при преобразовании блока
            entityDtoTransactions.add(entityDtoTransaction);
        }

        return entityDtoTransactions;
    }

    public static EntityLaws lawsToEntity(Laws laws) {

        EntityLaws entityLaws = new EntityLaws(
                laws.getPacketLawName(),
                laws.getLaws(),
                laws.getHashLaw()
        );
        return entityLaws;
    }



    public static List<Block> entityBlocksToBlocks(List<EntityBlock> entityBlocks) throws IOException {
        List<Block> blocks = new ArrayList<>();
        for (EntityBlock entityBlock : entityBlocks) {
            Block block = entityBlockToBlock(entityBlock);
            blocks.add(block);
        }
        return blocks;

    }
    public static Block entityBlockToBlock(EntityBlock entityBlock) throws IOException {
        List<DtoTransaction> dtoTransactions = entityDtoTransactionToDtoTransaction(
                entityBlock.getDtoTransactions()
        );
        Block block = new Block(
                dtoTransactions,
                entityBlock.getPreviousHash(),
                entityBlock.getMinerAddress(),
                entityBlock.getFounderAddress(),
                entityBlock.getRandomNumberProof(),
                entityBlock.getMinerRewards(),
                entityBlock.getHashCompexity(),
                entityBlock.getTimestamp(),
                entityBlock.getIndex(),
                entityBlock.getHashBlock()
        );
        return block;
    }
    public static List<DtoTransaction> entityDtoTransactionToDtoTransaction(
            List<EntityDtoTransaction> entityDtoTransactions
    ) throws IOException {
        List<DtoTransaction> dtoTransactions = new ArrayList<>();


        for (EntityDtoTransaction entityDtoTransaction : entityDtoTransactions) {
            Laws laws = entityLawsToLaws(entityDtoTransaction.getEntityLaws());
            DtoTransaction dtoTransaction = new DtoTransaction(
                    entityDtoTransaction.getSender(),
                    entityDtoTransaction.getCustomer(),
                    entityDtoTransaction.getDigitalDollar(),
                    entityDtoTransaction.getDigitalStockBalance(),
                    laws,
                    entityDtoTransaction.getBonusForMiner(),
                    entityDtoTransaction.getVoteEnum()
            );
            dtoTransaction.setSign(entityDtoTransaction.getSign());

            dtoTransactions.add(dtoTransaction);

        }

        return dtoTransactions;
    }
    public static Laws entityLawsToLaws(EntityLaws entityLaws) throws IOException {
        String name = null;
        List<String> strings = null;
        String hash = null;

        if(entityLaws == null){
            return new Laws();
        }

        if(entityLaws.getPacketLawName() != null){
            System.out.println("getPacketLawName: ");
            name = entityLaws.getPacketLawName();
        }

        if(entityLaws.getLaws() != null){
            System.out.println("getLaws");
            strings = entityLaws.getLaws();
        }

        if(entityLaws.getHashLaw() != null){
            System.out.println("getHashLaw");
            hash = entityLaws.getHashLaw();
        }
        Laws laws = new Laws(
               name,
                strings);
        laws.setHashLaw(hash);


        return laws;
    }

}