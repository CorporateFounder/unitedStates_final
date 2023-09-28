package International_Trade_Union.utils;

import International_Trade_Union.entity.DtoTransaction.DtoTransaction;
import International_Trade_Union.entity.blockchain.block.Block;
import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.entity.entities.EntityDtoTransaction;
import International_Trade_Union.entity.entities.EntityLaws;
import International_Trade_Union.vote.Laws;

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
}