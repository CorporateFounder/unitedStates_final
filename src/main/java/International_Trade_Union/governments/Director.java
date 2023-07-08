package International_Trade_Union.governments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    //количество юридических или физических лиц в данной должности
    //количество юридических или физических лиц в данной должности
    private String name;
    private  int count;

    private boolean electedByCEO;
    private boolean electedByFractions;


    private boolean electedByCorporateCouncilOfReferees;
    private boolean electedByStocks;
    private boolean officeOfDirectors;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Director)) return false;
        Director director = (Director) o;
        return getName().equals(director.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public boolean isAppointedByTheGovernment(){
        if(electedByCEO || electedByFractions || electedByCorporateCouncilOfReferees)
            return true;
        else return false;
    }
    public int getCount() {
        return count;
    }




}
