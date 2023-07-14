package International_Trade_Union.statistics;

import International_Trade_Union.setings.Seting;
import lombok.Data;


public enum Periud {
    DAY((int)Seting.COUNT_BLOCK_IN_DAY),
    WEEK(DAY.periud * 7),
    MONTH(DAY.periud * 30),
    YEAR(DAY.periud * 360);

    private int periud;

    Periud(int periud) {
        this.periud = periud;
    }

    public int getPeriud() {
        return periud;
    }
}
