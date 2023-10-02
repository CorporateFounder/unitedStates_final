package International_Trade_Union.entity.repository;

import International_Trade_Union.entity.entities.EntityBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EntityBlockRepository extends JpaRepository<EntityBlock, Long> {
        List<EntityBlock> findAll();
        List<EntityBlock> findAllByIdBetween(long from, long to);
        EntityBlock findById(long id);
}
