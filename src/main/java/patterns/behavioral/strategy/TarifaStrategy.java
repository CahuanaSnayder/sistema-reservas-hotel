package patterns.behavioral.strategy;

import java.math.BigDecimal;

/** PATRÓN DE COMPORTAMIENTO: STRATEGY. Cada temporada define su propia regla de precio. */
public interface TarifaStrategy {
    String getTemporada();

    BigDecimal calcular(BigDecimal precioNoche, long noches);
}
