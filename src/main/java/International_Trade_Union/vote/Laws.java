package International_Trade_Union.vote;


import lombok.Data;
import International_Trade_Union.setings.Seting;
import International_Trade_Union.utils.UtilsJson;
import International_Trade_Union.utils.UtilsUse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Data
public class Laws {
    //название пакета закона
    String packetLawName;
    List<String> laws;
    String hashLaw;

    public Laws() {
    }

    public Laws(String packetLawName, List<String> laws) throws IOException {
        this.packetLawName = packetLawName;
        this.laws = laws;
        this.hashLaw = Seting.NAME_LAW_ADDRESS_START + UtilsUse.sha256hash(UtilsJson.objToStringJson(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Laws)) return false;
        Laws laws = (Laws) o;
        if(getHashLaw() == null && laws.getHashLaw() == null
        && getPacketLawName() == null && laws.getPacketLawName() == null
        && getLaws() == null && laws.getLaws() == null){
            return true;
        }
        return getHashLaw().equals(laws.getHashLaw());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getHashLaw());
    }
}
