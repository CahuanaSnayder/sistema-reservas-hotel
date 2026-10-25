package patterns.behavioral.state;

import model.Reserva;

public class EstadoEnCurso implements EstadoReserva {
    @Override
    public String getNombre() {
        return "EN_CURSO";
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new IllegalStateException("La reserva ya fue confirmada y está en curso.");
    }

    @Override
    public void checkIn(Reserva reserva) {
        throw new IllegalStateException("El huésped ya realizó el check-in.");
    }

    @Override
    public void checkOut(Reserva reserva) {
        reserva.cambiarEstado(new EstadoFinalizada());
    }

    @Override
    public void cancelar(Reserva reserva) {
        throw new IllegalStateException("No se puede cancelar una estadía en curso; realice el check-out.");
    }

    @Override
    public boolean permiteConsumos() {
        return true;
    }
}
