package International_Trade_Union.vote;

import lombok.Data;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import org.apache.tomcat.util.net.jsse.JSSEUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CurrentLawVotes {
    private String addressLaw;
    private Set<String> YES;
    private Set<String> NO;


    public CurrentLawVotes() {
    }

    public CurrentLawVotes(String addressLaw, Set<String> YES, Set<String> NO) {
        this.addressLaw = addressLaw;
        this.YES = YES;
        this.NO = NO;
    }

    //голос фракции
    public double voteFractions(Map<String, Double> fractions){
        double yes = 0;
        double no = 0;
        double sum = fractions.entrySet().stream()
                .map(t->t.getValue())
                .collect(Collectors.toList())
                .stream().reduce(0.0, Double::sum);

        for (String s : YES) {
            if (fractions.containsKey(s)) {
                yes += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;
            }

        }
        for (String s : NO) {
            if (fractions.containsKey(s)) {
                no += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;
            }

        }
        return yes - no;

    }

    //подсчет голосов для палат
    public int voteGovernment(
            Map<String, Account> balances,
            List<String> governments

    ) {
        int yes = 0;
        int no = 0;

        List<String> addressGovernment = governments;
        for (String s : YES) {
            if (addressGovernment.contains(s)) {
                yes += Seting.VOTE_GOVERNMENT;
            }

        }
        for (String s : NO) {
            if (addressGovernment.contains(s)) {
                no += Seting.VOTE_GOVERNMENT;
            }

        }


        return yes - no;

    }

    //для избрания должностных лиц
    //для избрания должностных лиц
    public double votesLaw(Map<String, Account> balances,
                           Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;


        //
        for (String s : YES) {

            int count = 1;
            count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;
            yes += balances.get(s).getDigitalStockBalance() / count;

        }
        //
        for (String s : NO) {
            int count = 1;
            count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;
            no += balances.get(s).getDigitalStockBalance() / count;

        }


        return yes - no;
    }

    //для избрания должностных лиц
    public double votes(Map<String, Account> balances,
                        Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;
        for (String s : YES) {
            int count = 1;
            count = yesAverage.get(s) > 0 ? yesAverage.get(s) : 1;
            yes += balances.get(s).getDigitalStockBalance() / count;

        }
        for (String s : NO) {
            int count = 1;
            count = noAverage.get(s) > 0 ? noAverage.get(s) : 1;
            no += balances.get(s).getDigitalStockBalance() / count;

        }


        return yes - no;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentLawVotes)) return false;
        CurrentLawVotes that = (CurrentLawVotes) o;
        return getAddressLaw().equals(that.getAddressLaw());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddressLaw());
    }
}
