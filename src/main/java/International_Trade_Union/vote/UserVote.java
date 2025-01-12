package International_Trade_Union.vote;

public class UserVote {
    private String vote; // "YES", "NO", или "NONE"
    private Long index;

    public UserVote(String vote, Long index) {
        this.vote = vote;
        this.index = index;
    }

    public String getVote() {
        return vote;
    }

    public Long getIndex() {
        return index;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public void setIndex(Long index) {
        this.index = index;
    }
}
