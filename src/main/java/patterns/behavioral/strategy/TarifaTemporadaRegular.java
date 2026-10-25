package patterns.behavioral.strategy;

import java.math.BigDecimal;

/** Temporada regular: se cobra el precio por noche sin recargos. */
public class TarifaTemporadaRegular extends TarifaBase {
    @Override
    public String getTemporada() {
        return "REGULAR";
    }

    @Override
    protected BigDecimal factor() {
        return BigDecimal.ONE;
    }
}
