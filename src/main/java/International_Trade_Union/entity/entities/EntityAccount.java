package International_Trade_Union.entity.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class EntityAccount {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00000000")
    @Column(precision = 30, scale = 8)
    private BigDecimal digitalDollarBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00000000")
    @Column(precision = 30, scale = 8)
    private BigDecimal digitalStockBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00000000")
    @Column(precision = 30, scale = 8)
    private BigDecimal digitalStakingBalance;

    public EntityAccount() {
    }

    public EntityAccount(String account,
                         BigDecimal digitalDollarBalance,
                         BigDecimal digitalStockBalance,
                         BigDecimal digitalStakingBalance) {
        this.account = account;
        this.digitalDollarBalance = digitalDollarBalance.setScale(8, RoundingMode.DOWN);
        this.digitalStockBalance = digitalStockBalance.setScale(8, RoundingMode.DOWN);
        this.digitalStakingBalance = digitalStakingBalance.setScale(8, RoundingMode.DOWN);
    }

    @PrePersist
    @PreUpdate
    public void trimDecimalValues() {
        if (digitalDollarBalance != null) {
            digitalDollarBalance = digitalDollarBalance.setScale(8, RoundingMode.DOWN);
        }
        if (digitalStockBalance != null) {
            digitalStockBalance = digitalStockBalance.setScale(8, RoundingMode.DOWN);
        }
        if (digitalStakingBalance != null) {
            digitalStakingBalance = digitalStakingBalance.setScale(8, RoundingMode.DOWN);
        }
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
