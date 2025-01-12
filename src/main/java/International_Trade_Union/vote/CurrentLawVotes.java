package International_Trade_Union.vote;

import International_Trade_Union.utils.UtilsUse;
import lombok.Data;
import International_Trade_Union.model.Account;
import International_Trade_Union.setings.Seting;
import org.apache.tomcat.util.net.jsse.JSSEUtil;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class CurrentLawVotes {

    private long indexCreateLaw;
    private String whoCreate;
    private String addressLaw;
    private Map<String, Long> YES;
    private Map<String, Long> NO;


    public CurrentLawVotes() {
    }

    public CurrentLawVotes(String addressLaw,  Map<String, Long> YES, Map<String, Long> NO, long indexCreateLaw) {
        this.addressLaw = addressLaw;
        this.YES = YES;
        this.NO = NO;
        this.indexCreateLaw = indexCreateLaw;
    }

    public int voteDirector(Map<String, Account> balances,
                            List<String> governments
    ) {
        int yes = 0;
        List<String> addressGovernment = governments;
        for (Map.Entry<String, Long> s : YES.entrySet()) {
            if (addressGovernment.contains(s.getKey())) {
                yes += Seting.VOTE_GOVERNMENT;
            }

        }
        return yes;
    }


    //подсчет голосов для палат
    public int voteGovernment(
            Map<String, Account> balances,
            List<String> governments

    ) {
        int yes = 0;
        int no = 0;

        List<String> addressGovernment = governments;
        for (Map.Entry<String, Long> s : YES.entrySet()) {
            if (addressGovernment.contains(s.getKey())) {
                yes += Seting.VOTE_GOVERNMENT;
            }

        }
        for (Map.Entry<String, Long> s : NO.entrySet()) {
            if (addressGovernment.contains(s.getKey())) {
                no += Seting.VOTE_GOVERNMENT;
            }

        }


        return yes - no;

    }
    public int voteGovernmentNo(
            Map<String, Account> balances,
            List<String> governments

    ) {
        int yes = 0;
        int no = 0;

        List<String> addressGovernment = governments;
        for (Map.Entry<String, Long> s : YES.entrySet()) {
            if (addressGovernment.contains(s.getKey())) {
                yes += Seting.VOTE_GOVERNMENT;
            }

        }
        for (Map.Entry<String, Long> s : NO.entrySet()) {
            if (addressGovernment.contains(s.getKey())) {
                no += Seting.VOTE_GOVERNMENT;
            }

        }


        return no;

    }
    public int voteGovernmentYes(
            Map<String, Account> balances,
            List<String> governments

    ) {
        int yes = 0;
        int no = 0;

        List<String> addressGovernment = governments;
        for (Map.Entry<String, Long> s : YES.entrySet()) {
            if (addressGovernment.contains(s.getKey())) {
                yes += Seting.VOTE_GOVERNMENT;
            }

        }
        for (Map.Entry<String, Long> s : NO.entrySet()) {
            if (addressGovernment.contains(s.getKey())) {
                no += Seting.VOTE_GOVERNMENT;
            }

        }


        return yes;

    }



    public int stakingPointVoting(Map<String, Account> balances, List<String> governments) {
        int yes = 0;
        int no = 0;

        for (Map.Entry<String, Long> s : YES.entrySet()) {

            if (governments.contains(s.getKey())) {
                yes += UtilsUse.calculateScore(balances.get(s.getKey()).getDigitalStakingBalance().doubleValue(), 1);
            }
        }
        //
        for (Map.Entry<String, Long> s : NO.entrySet()) {
            if (governments.contains(s.getKey())) {
                no += UtilsUse.calculateScore(balances.get(s.getKey()).getDigitalStakingBalance().doubleValue(), 1);
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
        for (Map.Entry<String, Long> s : YES.entrySet()) {

            yes += balances.get(s.getKey()).getDigitalStakingBalance().doubleValue() + balances.get(s.getKey()).getDigitalDollarBalance().doubleValue();

        }
        //
        for (Map.Entry<String, Long> s : NO.entrySet()) {

            no += balances.get(s.getKey()).getDigitalStakingBalance().doubleValue() + balances.get(s.getKey()).getDigitalDollarBalance().doubleValue();

        }


        return yes - no;
    }

    //для избрания должностных лиц
    public double votes(Map<String, Account> balances,
                        Map<String, Integer> yesAverage, Map<String, Integer> noAverage) {
        double yes = 0.0;
        double no = 0.0;
        for (Map.Entry<String, Long> s : YES.entrySet()) {
            yes += balances.get(s.getKey()).getDigitalStakingBalance().doubleValue() ;

        }
        for (Map.Entry<String, Long> s : NO.entrySet()) {

            no += balances.get(s).getDigitalStakingBalance().doubleValue() ;

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

        for (Map.Entry<String, Long> s : YES.entrySet()) {
            if (fractions.containsKey(s.getKey())) {
                yes += (fractions.get(s.getKey())/sum) * Seting.HUNDRED_PERCENT;
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

        for (Map.Entry<String, Long> s : YES.entrySet()) {
            if (fractions.containsKey(s.getKey())) {
                yes = (fractions.get(s.getKey())/sum) * Seting.HUNDRED_PERCENT;
                directorsVote.add(new Vote(s.getKey(), yes));
            }

        }


        return directorsVote;
    }
}
