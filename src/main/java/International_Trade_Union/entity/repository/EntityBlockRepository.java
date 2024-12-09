package International_Trade_Union.entity.repository;

import International_Trade_Union.entity.entities.EntityBlock;
import International_Trade_Union.vote.VoteEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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

        // Существующие методы...

        @Query("SELECT DISTINCT b FROM EntityBlock b JOIN b.dtoTransactions t WHERE b.specialIndex BETWEEN :from AND :to AND t.sender = :sender")
        List<EntityBlock> findBlocksBySpecialIndexRangeAndSender(@Param("from") long from, @Param("to") long to, @Param("sender") String sender);

        @Query("SELECT DISTINCT b FROM EntityBlock b JOIN b.dtoTransactions t WHERE b.specialIndex BETWEEN :from AND :to AND t.customer = :customer")
        List<EntityBlock> findBlocksBySpecialIndexRangeAndCustomer(@Param("from") long from, @Param("to") long to, @Param("customer") String customer);

        @Query("SELECT b.hashCompexity, COUNT(b.hashCompexity) as frequency " +
                "FROM EntityBlock b WHERE b.id BETWEEN :startRange AND :endRange " +
                "GROUP BY b.hashCompexity " +
                "ORDER BY frequency DESC, b.hashCompexity ASC")
        List<Object[]> findHashComplexityModeInRange(@Param("startRange") long startRange, @Param("endRange") long endRange);


        @Query("SELECT b.hashCompexity FROM EntityBlock b WHERE b.id BETWEEN :startRange AND :endRange")
        List<Long> findHashComplexitiesInRange(@Param("startRange") long startRange, @Param("endRange") long endRange);

}
