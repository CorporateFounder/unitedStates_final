package International_Trade_Union.vote;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class VoteMapAndLastIndex {
    private Map<String, CurrentLawVotes> votesMap;
    private long startIndex;
    private long finishIndex;

    public VoteMapAndLastIndex() {
        votesMap = new HashMap<>();
    }

    public VoteMapAndLastIndex(Map<String, CurrentLawVotes> votesMap, long startIndex, long finishIndex) {
        this.votesMap = votesMap;
        this.startIndex = startIndex;
        this.finishIndex = finishIndex;
    }
}
