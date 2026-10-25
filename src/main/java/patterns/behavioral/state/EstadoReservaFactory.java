package patterns.behavioral.state;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/** Reconstruye el estado guardado en PostgreSQL mediante un registro (sin switch). */
public final class EstadoReservaFactory {

    private static final Map<String, Supplier<EstadoReserva>> ESTADOS = Map.of(
            "PENDIENTE", EstadoPendiente::new,
            "CONFIRMADA", EstadoConfirmada::new,
            "EN_CURSO", EstadoEnCurso::new,
            "FINALIZADA", EstadoFinalizada::new,
            "CANCELADA", EstadoCancelada::new
    );

    private EstadoReservaFactory() {
    }

    public static EstadoReserva desdeNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return new EstadoPendiente();
        }
        Supplier<EstadoReserva> estado = ESTADOS.get(nombre.trim().toUpperCase(Locale.ROOT));
        if (estado == null) {
            throw new IllegalArgumentException("Estado de reserva no reconocido: " + nombre);
        }
        return estado.get();
    }
}
