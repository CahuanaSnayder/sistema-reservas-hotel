package patterns.behavioral.observer;

import model.Reserva;

public interface ReservaObserver {
    void actualizar(Reserva reserva, String evento);
}
