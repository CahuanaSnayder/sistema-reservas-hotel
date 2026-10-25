package patterns.behavioral.strategy;

import java.util.List;
import java.util.Locale;

/** Selecciona la estrategia de tarifa según el nombre de la temporada. */
public class TarifaSelector {

    private final List<TarifaStrategy> estrategias;

    public TarifaSelector() {
        this(List.of(new TarifaTemporadaBaja(), new TarifaTemporadaRegular(), new TarifaTemporadaAlta()));
    }

    public TarifaSelector(List<TarifaStrategy> estrategias) {
        this.estrategias = List.copyOf(estrategias);
    }

    public TarifaStrategy seleccionar(String temporada) {
        String buscada = temporada == null ? "REGULAR" : temporada.trim().toUpperCase(Locale.ROOT);
        return estrategias.stream()
                .filter(e -> e.getTemporada().equals(buscada))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Temporada no reconocida: " + temporada));
    }

    public List<String> temporadasDisponibles() {
        return estrategias.stream().map(TarifaStrategy::getTemporada).toList();
    }
}
