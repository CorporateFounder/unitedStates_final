package International_Trade_Union.entity.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class EntityAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    private String account;
    private double digitalDollarBalance;
    private double digitalStockBalance;

    public EntityAccount() {
    }

    public EntityAccount(String account, double digitalDollarBalance, double digitalStockBalance) {
        this.account = account;
        this.digitalDollarBalance = digitalDollarBalance;
        this.digitalStockBalance = digitalStockBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityAccount)) return false;
        EntityAccount that = (EntityAccount) o;
        return getAccount().equals(that.getAccount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccount());
    }
}
