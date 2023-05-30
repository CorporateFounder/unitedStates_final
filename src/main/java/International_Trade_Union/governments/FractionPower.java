package International_Trade_Union.governments;

import lombok.Data;

import java.util.List;

@Data
public class FractionPower {
    public FractionPower() {
    }

    public FractionPower(String addressLaw,
                         double votes,
                         int votesCorporateCouncilOfReferees,
                         int votesBoardOfDirectors,
                         int votesBoardOfShareholders,
                         int voteGeneralExecutiveDirector,
                         int voteHightJudge,
                         int founderVote,
                         String packageName,
                         List<String> laws,
                         double fractionVote,
                         double fractionPower) {
        this.addressLaw = addressLaw;
        this.votes = votes;
        this.votesCorporateCouncilOfReferees = votesCorporateCouncilOfReferees;
        this.votesBoardOfDirectors = votesBoardOfDirectors;
        this.votesBoardOfShareholders = votesBoardOfShareholders;
        this.voteGeneralExecutiveDirector = voteGeneralExecutiveDirector;
        this.voteHightJudge = voteHightJudge;
        this.founderVote = founderVote;
        this.packageName = packageName;
        this.laws = laws;
        this.fractionVote = fractionVote;
        this.fractionPower = fractionPower;

    }

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
    private double fractionPower;

}
