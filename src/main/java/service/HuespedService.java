package service;

import model.Huesped;
import repository.HuespedRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/** SRP: solo contiene la lógica de negocio de huéspedes. */
public class HuespedService {
    private static final Pattern CORREO = Pattern.compile(
            "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$");
    private static final Pattern DOCUMENTO = Pattern.compile("^[A-Za-z0-9]{8,15}$");

    private final HuespedRepository repository;

    public HuespedService(HuespedRepository repository) {
        this.repository = repository;
    }

    public boolean registrar(String nombre, String documento, String email, String telefono) {
        validar(nombre, documento, email, telefono);
        Huesped huesped = new Huesped();
        completar(huesped, nombre, documento, email, telefono);
        huesped.setFechaRegistro(LocalDate.now());
        return repository.registrar(huesped);
    }

    public boolean actualizar(int id, String nombre, String documento, String email, String telefono) {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione un huésped válido.");
        }
        validar(nombre, documento, email, telefono);
        Huesped huesped = repository.buscarPorId(id);
        if (huesped == null) {
            return false;
        }
        completar(huesped, nombre, documento, email, telefono);
        return repository.actualizar(huesped);
    }

    public boolean eliminar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Seleccione un huésped válido.");
        }
        return repository.eliminar(id);
    }

    public List<Huesped> listar() { return repository.listar(); }
    public Huesped buscar(int id) { return repository.buscarPorId(id); }

    private void completar(Huesped huesped, String nombre, String documento, String email, String telefono) {
        huesped.setNombre(nombre.trim());
        huesped.setDocumento(documento.trim());
        huesped.setEmail(email.trim().toLowerCase(Locale.ROOT));
        huesped.setTelefono(telefono == null ? "" : telefono.trim());
    }

    private void validar(String nombre, String documento, String email, String telefono) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del huésped es obligatorio.");
        }
        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre no debe superar 100 caracteres.");
        }
        if (documento == null || !DOCUMENTO.matcher(documento.trim()).matches()) {
            throw new IllegalArgumentException("El documento debe tener entre 8 y 15 caracteres alfanuméricos.");
        }
        if (email == null || email.isBlank() || !CORREO.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Ingrese un correo válido.");
        }
        if (telefono != null && telefono.trim().length() > 20) {
            throw new IllegalArgumentException("El teléfono no debe superar 20 caracteres.");
        }
    }
}
