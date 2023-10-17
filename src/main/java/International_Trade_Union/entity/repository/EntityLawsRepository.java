package International_Trade_Union.entity.repository;

import International_Trade_Union.entity.entities.EntityLaws;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityLawsRepository extends JpaRepository<EntityLaws, Long> {
}
