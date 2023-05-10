package International_Trade_Union.vote;


import lombok.Data;
import International_Trade_Union.model.Account;

import java.util.Objects;

@Data
public class LawEligibleForParliamentaryApproval {
    public LawEligibleForParliamentaryApproval(Account account, Laws laws) {
        this.account = account;
        this.laws = laws;
        this.name = account.getAccount();
    }

    public LawEligibleForParliamentaryApproval() {
    }

    private String name;
    private Account account;
    private Laws laws;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LawEligibleForParliamentaryApproval)) return false;
        LawEligibleForParliamentaryApproval that = (LawEligibleForParliamentaryApproval) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getAccount(), that.getAccount()) && Objects.equals(getLaws(), that.getLaws());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAccount(), getLaws());
    }
}
