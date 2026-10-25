package patterns.behavioral.state;

import model.Reserva;

public class EstadoConfirmada implements EstadoReserva {
    @Override
    public String getNombre() {
        return "CONFIRMADA";
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new IllegalStateException("La reserva ya se encuentra confirmada.");
    }

    @Override
    public void checkIn(Reserva reserva) {
        reserva.cambiarEstado(new EstadoEnCurso());
    }

    @Override
    public void checkOut(Reserva reserva) {
        throw new IllegalStateException("El huésped debe hacer check-in antes del check-out.");
    }

    @Override
    public void cancelar(Reserva reserva) {
        reserva.cambiarEstado(new EstadoCancelada());
    }

    @Override
    public boolean permiteConsumos() {
        return true;
    }
}
