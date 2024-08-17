package International_Trade_Union.entity.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private String account;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0000000000")
    @Column(precision = 30, scale = 10)
    private BigDecimal digitalDollarBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0000000000")
    @Column(precision = 30, scale = 10)
    private BigDecimal digitalStockBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0000000000")
    @Column(precision = 30, scale = 10)
    private BigDecimal digitalStakingBalance;

    public EntityAccount() {
    }

    public EntityAccount(String account,
                         BigDecimal digitalDollarBalance,
                         BigDecimal digitalStockBalance,
                         BigDecimal digitalStakingBalance) {
        this.account = account;
        this.digitalDollarBalance = digitalDollarBalance.setScale(10, RoundingMode.HALF_UP);
        this.digitalStockBalance = digitalStockBalance.setScale(10, RoundingMode.HALF_UP);
        this.digitalStakingBalance = digitalStakingBalance.setScale(10, RoundingMode.HALF_UP);
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