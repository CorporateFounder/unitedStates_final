package International_Trade_Union.entity.repository;

import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.vote.VoteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface EntityBlockRepository extends JpaRepository<EntityBlock, Long> {

        List<EntityBlock> findAllByIdBetween(long from, long to);
        List<EntityBlock> findBySpecialIndexBetween(long from, long to);
        EntityBlock findBySpecialIndex(long specialIndex);
        EntityBlock findById(long id);

        EntityBlock findByHashBlock(String hashBlock);


        @Transactional
        @Modifying
        void deleteAllBySpecialIndexGreaterThanEqual(Long threshold);


        @Query("SELECT b FROM EntityBlock b JOIN b.dtoTransactions t WHERE t.sign = :sign")
        List<EntityBlock> findBlocksByTransactionSign(String sign);

}
