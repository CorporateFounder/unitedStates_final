package International_Trade_Union.entity.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "sliding_window_balances")
public class SlidingWindowBalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "window_key", nullable = false)
    private Long windowKey;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "digital_dollar_balance", nullable = false)
    private BigDecimal digitalDollarBalance;

    @Column(name = "digital_stock_balance", nullable = false)
    private BigDecimal digitalStockBalance;

    @Column(name = "digital_staking_balance", nullable = false)
    private BigDecimal digitalStakingBalance;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWindowKey() {
        return windowKey;
    }

    public void setWindowKey(Long windowKey) {
        this.windowKey = windowKey;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getDigitalDollarBalance() {
        return digitalDollarBalance;
    }

    public void setDigitalDollarBalance(BigDecimal digitalDollarBalance) {
        this.digitalDollarBalance = digitalDollarBalance;
    }

    public BigDecimal getDigitalStockBalance() {
        return digitalStockBalance;
    }

    public void setDigitalStockBalance(BigDecimal digitalStockBalance) {
        this.digitalStockBalance = digitalStockBalance;
    }

    public BigDecimal getDigitalStakingBalance() {
        return digitalStakingBalance;
    }

    public void setDigitalStakingBalance(BigDecimal digitalStakingBalance) {
        this.digitalStakingBalance = digitalStakingBalance;
    }
}
