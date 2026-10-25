package patterns.behavioral.observer;

import model.Reserva;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** PATRÓN DE COMPORTAMIENTO: OBSERVER. Sujeto que notifica los eventos de una reserva. */
public class ReservaEventManager {
    private static final Logger LOGGER = Logger.getLogger(ReservaEventManager.class.getName());

    private final List<ReservaObserver> observadores = new ArrayList<>();

    public void suscribir(ReservaObserver observador) {
        observadores.add(Objects.requireNonNull(observador, "El observador es obligatorio."));
    }

    public void desuscribir(ReservaObserver observador) {
        observadores.remove(observador);
    }

    public void notificar(Reserva reserva, String evento) {
        for (ReservaObserver observador : List.copyOf(observadores)) {
            try {
                observador.actualizar(reserva, evento);
            } catch (RuntimeException e) {
                LOGGER.log(Level.WARNING,
                        "No se pudo ejecutar el observador "
                                + observador.getClass().getSimpleName(), e);
            }
        }
    }
}
