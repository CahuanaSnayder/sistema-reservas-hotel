package patterns.behavioral.strategy;

import java.math.BigDecimal;

/** Temporada alta (feriados, Vendimia, Fiestas Patrias): 25 % de recargo. */
public class TarifaTemporadaAlta extends TarifaBase {
    @Override
    public String getTemporada() {
        return "ALTA";
    }

    @Override
    protected BigDecimal factor() {
        return new BigDecimal("1.25");
    }
}
