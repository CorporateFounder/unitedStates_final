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


        boolean hashOriginal = false;
        boolean hash = false;
        boolean nameOriginal = false;
        boolean name = false;
        boolean stringsOriginal = false;
        boolean strings = false;

        if(getHashLaw() == null ){
            hashOriginal = true;
        }
        if(laws.getHashLaw() == null ){
            hash = true;
        }
        if(getPacketLawName() == null ){
            nameOriginal = true;
        }
        if(laws.getPacketLawName() == null){
            name = true;
        }

        if(getLaws() == null){
            stringsOriginal = true;
        }
        if(laws.getLaws() == null){
            strings = true;
        }
        if(hashOriginal && hash && nameOriginal && name && stringsOriginal && strings){
            return true;
        }

        if(!hashOriginal ^ !hash || !nameOriginal ^ !name || !stringsOriginal ^ !strings){
            return false;
        }




        if(getHashLaw().equals(laws.getHashLaw())
                && getPacketLawName().equals(laws.getPacketLawName())
                && getLaws().equals(laws.getLaws())){
            return true;
        }else {
            return false;
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(getHashLaw());
    }
}
