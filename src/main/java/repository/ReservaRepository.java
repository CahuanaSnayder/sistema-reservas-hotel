package repository;

import model.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository {
    boolean registrar(Reserva reserva);
    List<Reserva> listar();
    List<Reserva> listarPorHuesped(int idHuesped);
    Reserva buscarPorId(int idReserva);
    boolean actualizarEstado(Reserva reserva);
    boolean eliminar(int idReserva);

    /** Indica si la habitación ya tiene una reserva activa que se cruza con las fechas dadas. */
    boolean existeCruce(int idHabitacion, LocalDate ingreso, LocalDate salida);
}
