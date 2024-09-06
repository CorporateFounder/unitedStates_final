package International_Trade_Union.entity.repository;

import International_Trade_Union.entity.entities.SlidingWindowBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlidingWindowBalanceRepository extends JpaRepository<SlidingWindowBalanceEntity, Long> {

    // Существующие методы
    List<SlidingWindowBalanceEntity> findByWindowKey(Long windowKey);

    SlidingWindowBalanceEntity findByWindowKeyAndAccountId(Long windowKey, String accountId);

    void deleteByWindowKey(Long windowKey);

    void deleteTopByOrderByWindowKeyAsc();

    // Поиск всех записей по accountId, чтобы можно было находить старые балансы
    List<SlidingWindowBalanceEntity> findByAccountIdOrderByWindowKeyAsc(String accountId);

    // Новый метод для поиска по windowKey и списку accountId
    List<SlidingWindowBalanceEntity> findByWindowKeyAndAccountIdIn(Long windowKey, List<String> accountIds);
}
