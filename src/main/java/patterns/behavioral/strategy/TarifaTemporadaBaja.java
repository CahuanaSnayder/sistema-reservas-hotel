package patterns.behavioral.strategy;

import java.math.BigDecimal;

/** Temporada baja: 15 % de descuento sobre la tarifa regular. */
public class TarifaTemporadaBaja extends TarifaBase {
    @Override
    public String getTemporada() {
        return "BAJA";
    }

    @Override
    protected BigDecimal factor() {
        return new BigDecimal("0.85");
    }
}
