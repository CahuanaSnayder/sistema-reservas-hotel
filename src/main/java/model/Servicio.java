package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Servicio {

    private int idServicio;
    private String nombre;
    private BigDecimal precio = BigDecimal.ZERO;

    public Servicio() {
    }

    public Servicio(int idServicio, String nombre, BigDecimal precio) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        setPrecio(precio);
    }

    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecio() { return precio; }

    public void setPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio del servicio debe ser mayor que cero.");
        }
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return idServicio + " - " + nombre + " (S/ " + precio.toPlainString() + ")";
    }
}
