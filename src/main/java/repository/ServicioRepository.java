package repository;

import model.Servicio;

import java.util.List;

public interface ServicioRepository {
    boolean registrar(Servicio servicio);
    List<Servicio> listar();
    Servicio buscarPorId(int idServicio);
    boolean actualizar(Servicio servicio);
    boolean eliminar(int idServicio);
}
