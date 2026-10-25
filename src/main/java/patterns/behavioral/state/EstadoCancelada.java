package patterns.behavioral.state;

import model.Reserva;

public class EstadoCancelada implements EstadoReserva {
    @Override
    public String getNombre() {
        return "CANCELADA";
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new IllegalStateException("Una reserva cancelada no puede confirmarse.");
    }

    @Override
    public void checkIn(Reserva reserva) {
        throw new IllegalStateException("Una reserva cancelada no admite check-in.");
    }

    @Override
    public void checkOut(Reserva reserva) {
        throw new IllegalStateException("Una reserva cancelada no admite check-out.");
    }

    @Override
    public void cancelar(Reserva reserva) {
        throw new IllegalStateException("La reserva ya se encuentra cancelada.");
    }

    @Override
    public boolean permiteConsumos() {
        return false;
    }
}
