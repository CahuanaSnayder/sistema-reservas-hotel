package repository;

import model.Huesped;

import java.util.List;

/* Interface pequeña para cumplir ISP y permitir DIP. */
public interface HuespedRepository {
    boolean registrar(Huesped huesped);
    List<Huesped> listar();
    Huesped buscarPorId(int idHuesped);
    boolean actualizar(Huesped huesped);
    boolean eliminar(int idHuesped);
}
