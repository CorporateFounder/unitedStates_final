package International_Trade_Union.vote;

import lombok.Data;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import org.apache.tomcat.util.net.jsse.JSSEUtil;

import java.util.*;
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

            yes += balances.get(s).getDigitalStakingBalance();

        }
        //
        for (String s : NO) {

            no += balances.get(s).getDigitalStakingBalance();

        }


        return yes - no;
    }

    //для избрания должностных лиц
    public double votes(Map<String, Account> balances,
                        Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;
        for (String s : YES) {
            yes += balances.get(s).getDigitalStakingBalance() ;

        }
        for (String s : NO) {

            no += balances.get(s).getDigitalStakingBalance() ;

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

    //голос фракции
    public double voteFractions(Map<String, Double> fractions){
        double yes = 0;
//        double no = 0;
        double sum = fractions.entrySet().stream()
                .map(t->t.getValue())
                .collect(Collectors.toList())
                .stream().reduce(0.0, Double::sum);

        for (String s : YES) {
            if (fractions.containsKey(s)) {
                yes += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;
            }

        }
//        for (String s : NO) {
//            if (fractions.containsKey(s)) {
//                no += (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;
//            }
//
//        }
//        return yes - no;

        return yes;
    }

    public List<Vote> directorsVote(Map<String, Double> fractions){
        List<Vote> directorsVote = new ArrayList<>();
        double yes = 0;

        double sum = fractions.entrySet().stream()
                .map(t->t.getValue())
                .collect(Collectors.toList())
                .stream().reduce(0.0, Double::sum);

        for (String s : YES) {
            if (fractions.containsKey(s)) {
                yes = (fractions.get(s)/sum) * Seting.HUNDRED_PERCENT;
                directorsVote.add(new Vote(s, yes));
            }

        }


        return directorsVote;
    }
}
