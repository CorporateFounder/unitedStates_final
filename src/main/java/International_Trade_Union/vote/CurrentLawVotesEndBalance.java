package International_Trade_Union.vote;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class CurrentLawVotesEndBalance {
    private String addressLaw;
    private double votes;
    //CORPORATE_COUNCIL_OF_REFEREES
    private int votesCorporateCouncilOfReferees;

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

    public CurrentLawVotesEndBalance() {
    }

    public CurrentLawVotesEndBalance
            (String addressLaw,
             String packageName,
             double votes,
             int votesCorporateCouncilOfReferees,
             int votesBoardOfDirectors,
             int votesBoardOfShareholders,
             int voteGeneralExecutiveDirector,
             int voteHightJudge,
                    int founderVote,
             double fractionVote,
             List<String> laws) {
        this.addressLaw = addressLaw;
        this.packageName = packageName;
        this.votesCorporateCouncilOfReferees = votesCorporateCouncilOfReferees;
        this.votesBoardOfShareholders = votesBoardOfShareholders;
        this.votesBoardOfDirectors = votesBoardOfDirectors;
        this.voteGeneralExecutiveDirector = voteGeneralExecutiveDirector;
        this.voteHightJudge = voteHightJudge;
        this.founderVote = founderVote;
        this.fractionVote = fractionVote;

        this.votes = votes;
        this.laws = laws;

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
}
