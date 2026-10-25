package repository;

import model.Habitacion;

import java.util.List;

public interface HabitacionRepository {
    boolean registrar(Habitacion habitacion);
    List<Habitacion> listar();
    Habitacion buscarPorId(int idHabitacion);
    boolean actualizar(Habitacion habitacion);
    boolean eliminar(int idHabitacion);
}
