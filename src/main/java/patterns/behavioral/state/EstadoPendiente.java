package patterns.behavioral.state;

import model.Reserva;

public class EstadoPendiente implements EstadoReserva {
    @Override
    public String getNombre() {
        return "PENDIENTE";
    }

    @Override
    public void confirmar(Reserva reserva) {
        reserva.cambiarEstado(new EstadoConfirmada());
    }

    @Override
    public void checkIn(Reserva reserva) {
        throw new IllegalStateException("La reserva debe confirmarse (pagarse) antes del check-in.");
    }

    @Override
    public void checkOut(Reserva reserva) {
        throw new IllegalStateException("No se puede hacer check-out de una reserva pendiente.");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new EstadoCancelada());
    }

    @Override
    public boolean permiteConsumos() {
        return false;
    }
}
