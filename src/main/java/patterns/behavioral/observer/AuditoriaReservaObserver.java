package patterns.behavioral.observer;

import model.Reserva;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditoriaReservaObserver implements ReservaObserver {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void actualizar(Reserva reserva, String evento) {
        System.out.printf("[AUDITORÍA %s] Reserva %d: %s. Estado=%s Total=S/ %s%n",
                LocalDateTime.now().format(FORMATO), reserva.getIdReserva(), evento,
                reserva.getEstado(), reserva.getTotal().toPlainString());
    }
}
