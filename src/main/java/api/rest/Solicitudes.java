package api.rest;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Cuerpos JSON que recibe la API. */
public final class Solicitudes {

    private Solicitudes() {
    }

    public record Login(String usuario, String contrasena) { }

    public record HuespedDatos(String nombre, String documento, String email, String telefono) { }

    public record HabitacionDatos(String numero, String tipo, int capacidad, BigDecimal precioNoche) { }

    public record ServicioDatos(String nombre, BigDecimal precio) { }

    public record ReservaDatos(int idHuesped, int idHabitacion, LocalDate fechaIngreso,
                               LocalDate fechaSalida, String temporada) { }

    public record ConsumoDatos(int idServicio, int cantidad) { }

    /** Respuesta estándar de las operaciones: un mensaje para mostrar al usuario. */
    public record Mensaje(String mensaje) { }
}
