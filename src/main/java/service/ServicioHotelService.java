package service;

import model.Servicio;
import repository.ServicioRepository;

import java.math.BigDecimal;
import java.util.List;

/** SRP: solo contiene la lógica de negocio del catálogo de servicios adicionales. */
public class ServicioHotelService {
    private final ServicioRepository repository;

    public ServicioHotelService(ServicioRepository repository) {
        this.repository = repository;
    }

    public boolean registrar(String nombre, BigDecimal precio) {
        validar(nombre);
        return repository.registrar(new Servicio(0, nombre.trim(), precio));
    }

    public boolean actualizar(int id, String nombre, BigDecimal precio) {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione un servicio válido.");
        }
        validar(nombre);
        if (repository.buscarPorId(id) == null) {
            return false;
        }
        return repository.actualizar(new Servicio(id, nombre.trim(), precio));
    }

    public boolean eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione un servicio válido.");
        }
        return repository.eliminar(id);
    }

    public List<Servicio> listar() { return repository.listar(); }

    private void validar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre no debe superar 100 caracteres.");
        }
    }
}
