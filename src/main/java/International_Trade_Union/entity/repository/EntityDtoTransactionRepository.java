package International_Trade_Union.entity.repository;

import International_Trade_Union.entity.entities.EntityDtoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityDtoTransactionRepository extends JpaRepository<EntityDtoTransaction, Long> {
    EntityDtoTransaction findById(long id);
    List<EntityDtoTransaction> findAll();
    EntityDtoTransaction findBySign(byte[] sign);
}
