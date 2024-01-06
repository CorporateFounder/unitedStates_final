package International_Trade_Union.governments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**Класс описывающий тип данных должность.
 * A class describing the data type position.*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    //название должности.
    //job title.
    private String name;
    //количество мест для данной должности.
    //number of places for this position.
    private  int count;

    //должность назначаемая ген директором.
    //position appointed by the general director.
    private boolean electedByCEO;
    //должность назначаемая советом директоров.
    //position appointed by the board of directors.
    private boolean electedByBoardOfDirectors;

    //должность назначаемая советом корпоративных судей.
    //a position appointed by a council of corporate judges.
    private boolean electedByCorporateCouncilOfReferees;

    //должность назначаемая избирателями с помощью акций.
    //position appointed by voters through shares.
    private boolean electedByStocks;

    //обозначает являться ли должность кабинетом министров.
    //denotes whether the position is a cabinet position.
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
    public int getCount() {
        return count;
    }

    public boolean isAppointedByTheGovernment(){
        if(electedByCEO || electedByBoardOfDirectors || electedByCorporateCouncilOfReferees)
            return true;
        else return false;
    }

}
