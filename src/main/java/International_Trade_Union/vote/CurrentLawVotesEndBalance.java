package International_Trade_Union.vote;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class CurrentLawVotesEndBalance {
    private String addressLaw;
    private double votes;
    //CORPORATE_COUNCIL_OF_REFEREES
    private int votesCorporateCouncilOfRefereesNo;

    //BOARD_OF_DIRECTORS
    private int votesBoardOfDirectors;
    private int votesBoardOfShareholders;

    //GENERAL_EXECUTIVE_DIRECTOR
    private int voteGeneralExecutiveDirector;
    private int voteHightJudge;

    private int founderVote;
    private String packageName;
    private List<String> laws;
    private double fractionVote;
    private List<Vote> directorsVote;
    Map<String, Double> fractionsRaiting = new HashMap<>();
    private boolean isValid;
    private double sum;
    private double percentDirectDemocracy;
    private long indexCreateLaw;
    String whoCreate;
    private boolean isPosition;
    private int votesCorporateCouncilOfRefereesYes;


    public CurrentLawVotesEndBalance() {
    }

    public CurrentLawVotesEndBalance
            (String addressLaw,
             String packageName,
             double votes,
             int votesCorporateCouncilOfRefereesNo,
             int votesBoardOfDirectors,
             int votesBoardOfShareholders,
             int voteGeneralExecutiveDirector,
             int voteHightJudge,
                    int founderVote,
             double fractionVote,
             List<String> laws,
             List<Vote> directorsVote,
             Map<String, Double> fractionsRaiting,
             boolean isValid,
             double sum,
             double percentDirectDemocracy,
             long indexCreateLaw,
             String whoCreate,
             int votesCorporateCouncilOfRefereesYes) {
        this.addressLaw = addressLaw;
        this.packageName = packageName;
        this.votesCorporateCouncilOfRefereesNo = votesCorporateCouncilOfRefereesNo;
        this.votesBoardOfShareholders = votesBoardOfShareholders;
        this.votesBoardOfDirectors = votesBoardOfDirectors;
        this.voteGeneralExecutiveDirector = voteGeneralExecutiveDirector;
        this.voteHightJudge = voteHightJudge;
        this.founderVote = founderVote;
        this.fractionVote = fractionVote;

        this.votes = votes;
        this.laws = laws;
        this.directorsVote = directorsVote;
        this.fractionsRaiting = fractionsRaiting;
        this.isValid = isValid;
        this.sum = sum;
        this.percentDirectDemocracy = percentDirectDemocracy;
        this.indexCreateLaw = indexCreateLaw;
        this.whoCreate = whoCreate;
        this.votesCorporateCouncilOfRefereesYes = votesCorporateCouncilOfRefereesYes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentLawVotesEndBalance)) return false;
        CurrentLawVotesEndBalance that = (CurrentLawVotesEndBalance) o;
        return getAddressLaw().equals(that.getAddressLaw());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddressLaw());
    }

    public double sumDirectorsVote(){
        return directorsVote.stream().mapToDouble(t->t.getVote()).sum();
    }
}
