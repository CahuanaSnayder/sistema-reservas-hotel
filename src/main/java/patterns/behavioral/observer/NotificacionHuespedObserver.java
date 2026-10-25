package patterns.behavioral.observer;

import model.Huesped;
import model.Reserva;
import repository.HuespedRepository;

/** Avisa al huésped por correo (simulado) cada vez que cambia su reserva. */
public class NotificacionHuespedObserver implements ReservaObserver {

    private final HuespedRepository huespedRepository;

    public NotificacionHuespedObserver(HuespedRepository huespedRepository) {
        this.huespedRepository = huespedRepository;
    }

    @Override
    public void actualizar(Reserva reserva, String evento) {
        Huesped huesped = huespedRepository.buscarPorId(reserva.getIdHuesped());
        if (huesped != null) {
            System.out.println("[NOTIFICACIÓN] Para: " + huesped.getEmail()
                    + " | Estimado(a) " + huesped.getNombre() + ": " + evento
                    + ". Estado actual: " + reserva.getEstado()
                    + ". Total: S/ " + reserva.getTotal().toPlainString());
        }
    }
}
