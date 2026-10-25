package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Servicio adicional consumido por el huésped durante su reserva. */
public final class ConsumoServicio {

    private int idConsumo;
    private int idReserva;
    private int idServicio;
    private int cantidad;
    private BigDecimal subtotal = BigDecimal.ZERO;

    public ConsumoServicio() {
    }

    /** GRASP Creator: el consumo se crea con los datos del servicio que lo origina. */
    public static ConsumoServicio crear(int idReserva, Servicio servicio, int cantidad) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio es obligatorio.");
        }
        ConsumoServicio consumo = new ConsumoServicio();
        consumo.setIdReserva(idReserva);
        consumo.setIdServicio(servicio.getIdServicio());
        consumo.setCantidad(cantidad);
        consumo.setSubtotal(consumo.calcularSubtotal(servicio.getPrecio()));
        return consumo;
    }

    /** GRASP Information Expert: el consumo conoce su cantidad y calcula su subtotal. */
    public BigDecimal calcularSubtotal(BigDecimal precioUnitario) {
        if (precioUnitario == null || precioUnitario.signum() <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero.");
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int getIdConsumo() { return idConsumo; }
    public void setIdConsumo(int idConsumo) { this.idConsumo = idConsumo; }
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() { return subtotal; }

    public void setSubtotal(BigDecimal subtotal) {
        if (subtotal == null || subtotal.signum() < 0) {
            throw new IllegalArgumentException("El subtotal no puede ser negativo.");
        }
        this.subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
    }
}
