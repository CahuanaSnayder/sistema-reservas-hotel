package patterns.behavioral.state;

import model.Reserva;

/** PATRÓN DE COMPORTAMIENTO: STATE. Contrato común de todos los estados de una reserva. */
public interface EstadoReserva {
    String getNombre();

    void confirmar(Reserva reserva);

    void checkIn(Reserva reserva);

    void checkOut(Reserva reserva);

    void cancelar(Reserva reserva);

    boolean permiteConsumos();
}
