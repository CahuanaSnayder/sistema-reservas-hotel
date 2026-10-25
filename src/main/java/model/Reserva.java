package model;

import patterns.behavioral.state.EstadoPendiente;
import patterns.behavioral.state.EstadoReserva;
import patterns.behavioral.state.EstadoReservaFactory;
import patterns.behavioral.strategy.TarifaStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class Reserva {

    private int idReserva;
    private int idHuesped;
    private int idHabitacion;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private String temporada = "REGULAR";
    private BigDecimal costoHospedaje = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private LocalDate fechaRegistro = LocalDate.now();
    private EstadoReserva estadoActual = new EstadoPendiente();

    public Reserva() {
    }

    // ---------- GRASP Information Expert ----------

    /** La reserva conoce sus fechas, por eso calcula sus noches. */
    public long calcularNoches() {
        if (fechaIngreso == null || fechaSalida == null) {
            throw new IllegalStateException("La reserva debe tener fecha de ingreso y salida.");
        }
        return ChronoUnit.DAYS.between(fechaIngreso, fechaSalida);
    }

    /** Calcula el hospedaje delegando la regla de precio en la estrategia de temporada. */
    public BigDecimal calcularHospedaje(BigDecimal precioNoche, TarifaStrategy tarifa) {
        if (tarifa == null) {
            throw new IllegalArgumentException("La tarifa es obligatoria.");
        }
        costoHospedaje = tarifa.calcular(precioNoche, calcularNoches())
                .setScale(2, RoundingMode.HALF_UP);
        total = costoHospedaje;
        return costoHospedaje;
    }

    /** Total = hospedaje + consumos de servicios adicionales. */
    public BigDecimal calcularTotal(List<ConsumoServicio> consumos) {
        if (consumos == null) {
            throw new IllegalArgumentException("La lista de consumos es obligatoria.");
        }
        total = consumos.stream()
                .map(ConsumoServicio::getSubtotal)
                .reduce(costoHospedaje, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return total;
    }

    public BigDecimal calcularConsumos() {
        return total.subtract(costoHospedaje).setScale(2, RoundingMode.HALF_UP);
    }

    // ---------- Patrón State: delega el comportamiento en el estado actual ----------

    public void confirmar() { estadoActual.confirmar(this); }
    public void checkIn() { estadoActual.checkIn(this); }
    public void checkOut() { estadoActual.checkOut(this); }
    public void cancelar() { estadoActual.cancelar(this); }
    public boolean permiteConsumos() { return estadoActual.permiteConsumos(); }

    public void cambiarEstado(EstadoReserva nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio.");
        }
        this.estadoActual = nuevoEstado;
    }

    public String getEstado() { return estadoActual.getNombre(); }
    public void setEstado(String estado) { this.estadoActual = EstadoReservaFactory.desdeNombre(estado); }

    // ---------- Getters y setters ----------

    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public int getIdHuesped() { return idHuesped; }
    public void setIdHuesped(int idHuesped) { this.idHuesped = idHuesped; }
    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public String getTemporada() { return temporada; }
    public void setTemporada(String temporada) { this.temporada = temporada; }
    public BigDecimal getCostoHospedaje() { return costoHospedaje; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public void setCostoHospedaje(BigDecimal costoHospedaje) {
        if (costoHospedaje == null || costoHospedaje.signum() < 0) {
            throw new IllegalArgumentException("El costo de hospedaje no puede ser negativo.");
        }
        this.costoHospedaje = costoHospedaje.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() { return total; }

    public void setTotal(BigDecimal total) {
        if (total == null || total.signum() < 0) {
            throw new IllegalArgumentException("El total no puede ser negativo.");
        }
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Reserva #" + idReserva + " - Huésped " + idHuesped + " - Hab. " + idHabitacion
                + " (S/ " + total.toPlainString() + ") - " + getEstado();
    }
}
