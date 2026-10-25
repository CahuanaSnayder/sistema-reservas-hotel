package service;

import model.Habitacion;
import repository.HabitacionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** SRP: solo contiene la lógica de negocio de habitaciones. */
public class HabitacionService {
    public static final List<String> TIPOS = List.of("SIMPLE", "DOBLE", "MATRIMONIAL", "SUITE");

    private final HabitacionRepository repository;

    public HabitacionService(HabitacionRepository repository) {
        this.repository = repository;
    }

    public boolean registrar(String numero, String tipo, int capacidad, BigDecimal precioNoche) {
        Habitacion habitacion = crearValida(0, numero, tipo, capacidad, precioNoche);
        return repository.registrar(habitacion);
    }

    public boolean actualizar(int id, String numero, String tipo, int capacidad, BigDecimal precioNoche) {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione una habitación válida.");
        }
        if (repository.buscarPorId(id) == null) {
            return false;
        }
        return repository.actualizar(crearValida(id, numero, tipo, capacidad, precioNoche));
    }

    public boolean eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione una habitación válida.");
        }
        return repository.eliminar(id);
    }

    public List<Habitacion> listar() { return repository.listar(); }
    public Habitacion buscar(int id) { return repository.buscarPorId(id); }

    private Habitacion crearValida(int id, String numero, String tipo, int capacidad, BigDecimal precio) {
        if (numero == null || numero.isBlank() || numero.trim().length() > 10) {
            throw new IllegalArgumentException("El número de habitación es obligatorio (máx. 10 caracteres).");
        }
        String tipoNormalizado = tipo == null ? "" : tipo.trim().toUpperCase(Locale.ROOT);
        if (!Set.copyOf(TIPOS).contains(tipoNormalizado)) {
            throw new IllegalArgumentException("Tipo de habitación no válido.");
        }
        if (capacidad <= 0 || capacidad > 10) {
            throw new IllegalArgumentException("La capacidad debe estar entre 1 y 10 personas.");
        }
        return new Habitacion(id, numero.trim(), tipoNormalizado, capacidad, precio);
    }
}
