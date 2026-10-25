package patterns.behavioral.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Plantilla común: valida los datos y aplica el factor propio de cada temporada. */
public abstract class TarifaBase implements TarifaStrategy {

    protected abstract BigDecimal factor();

    @Override
    public BigDecimal calcular(BigDecimal precioNoche, long noches) {
        if (precioNoche == null || precioNoche.signum() <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor que cero.");
        }
        if (noches <= 0) {
            throw new IllegalArgumentException("La reserva debe tener al menos una noche.");
        }
        return precioNoche.multiply(BigDecimal.valueOf(noches))
                .multiply(factor())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
