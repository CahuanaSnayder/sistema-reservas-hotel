package repository;

import model.ConsumoServicio;
import model.Reserva;

import java.util.List;

/* Operaciones atómicas que modifican los consumos y el total de la reserva. */
public interface ConsumoRepository {
    List<ConsumoServicio> listarPorReserva(int idReserva);
    ConsumoServicio buscarPorId(int idConsumo);
    boolean registrarYActualizarTotal(Reserva reserva, ConsumoServicio consumo);
    boolean eliminarYActualizarTotal(Reserva reserva, int idConsumo);
}
