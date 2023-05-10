package International_Trade_Union.model;

import International_Trade_Union.governments.Director;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FIndPositonHelperData {
    Director addressPosition;
    boolean withLimited;
    boolean electedWithStock;
    boolean electedWithPrimeMinister;
    boolean electedWithHousOfRepresentativies;
    boolean electedWithChamberOfHightJudjes;

}
