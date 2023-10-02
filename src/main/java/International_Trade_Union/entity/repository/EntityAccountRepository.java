package International_Trade_Union.entity.repository;

import International_Trade_Union.entity.entities.EntityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityAccountRepository extends JpaRepository<EntityAccount, Long> {
    EntityAccount findByAccount(String account);
}
