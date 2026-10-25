package patterns.behavioral.state;

import model.Reserva;

public class EstadoFinalizada implements EstadoReserva {
    @Override
    public String getNombre() {
        return "FINALIZADA";
    }

    @Override
    public void confirmar(Reserva reserva) {
        throw new IllegalStateException("Una reserva finalizada no puede confirmarse.");
    }

    @Override
    public void checkIn(Reserva reserva) {
        throw new IllegalStateException("Una reserva finalizada no admite check-in.");
    }

    @Override
    public void checkOut(Reserva reserva) {
        throw new IllegalStateException("La reserva ya se encuentra finalizada.");
    }

    @Override
    public void cancelar(Reserva reserva) {
        throw new IllegalStateException("Una reserva finalizada no puede cancelarse.");
    }

    @Override
    public boolean permiteConsumos() {
        return false;
    }
}
