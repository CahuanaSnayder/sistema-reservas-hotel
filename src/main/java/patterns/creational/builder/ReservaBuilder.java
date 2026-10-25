package patterns.creational.builder;

import model.Reserva;

import java.time.LocalDate;

/** PATRÓN CREACIONAL: BUILDER. Construye una reserva válida paso a paso. */
public class ReservaBuilder {

    private int idReserva;
    private Integer idHuesped;
    private Integer idHabitacion;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private String temporada = "REGULAR";
    private String estado = "PENDIENTE";

    public ReservaBuilder conId(int idReserva) {
        this.idReserva = idReserva;
        return this;
    }

    public ReservaBuilder paraHuesped(int idHuesped) {
        this.idHuesped = idHuesped;
        return this;
    }

    public ReservaBuilder enHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
        return this;
    }

    public ReservaBuilder desde(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
        return this;
    }

    public ReservaBuilder hasta(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
        return this;
    }

    public ReservaBuilder enTemporada(String temporada) {
        this.temporada = temporada;
        return this;
    }

    public ReservaBuilder conEstado(String estado) {
        this.estado = estado;
        return this;
    }

    public Reserva construir() {
        if (idHuesped == null || idHuesped <= 0) {
            throw new IllegalStateException("Debe indicar un huésped válido.");
        }
        if (idHabitacion == null || idHabitacion <= 0) {
            throw new IllegalStateException("Debe indicar una habitación válida.");
        }
        if (fechaIngreso == null || fechaSalida == null) {
            throw new IllegalStateException("Las fechas de ingreso y salida son obligatorias.");
        }
        if (!fechaSalida.isAfter(fechaIngreso)) {
            throw new IllegalStateException("La fecha de salida debe ser posterior a la de ingreso.");
        }
        if (temporada == null || temporada.isBlank()) {
            throw new IllegalStateException("La temporada es obligatoria.");
        }

        Reserva reserva = new Reserva();
        reserva.setIdReserva(idReserva);
        reserva.setIdHuesped(idHuesped);
        reserva.setIdHabitacion(idHabitacion);
        reserva.setFechaIngreso(fechaIngreso);
        reserva.setFechaSalida(fechaSalida);
        reserva.setTemporada(temporada);
        reserva.setEstado(estado);
        reserva.setFechaRegistro(LocalDate.now());
        return reserva;
    }
}
