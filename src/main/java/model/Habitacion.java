package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Habitacion {

    private int idHabitacion;
    private String numero;
    private String tipo;
    private int capacidad;
    private BigDecimal precioNoche = BigDecimal.ZERO;

    public Habitacion() {
    }

    public Habitacion(int idHabitacion, String numero, String tipo, int capacidad, BigDecimal precioNoche) {
        this.idHabitacion = idHabitacion;
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        setPrecioNoche(precioNoche);
    }

    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public BigDecimal getPrecioNoche() { return precioNoche; }

    public void setPrecioNoche(BigDecimal precioNoche) {
        if (precioNoche == null || precioNoche.signum() <= 0) {
            throw new IllegalArgumentException("El precio por noche debe ser mayor que cero.");
        }
        this.precioNoche = precioNoche.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Hab. " + numero + " - " + tipo + " (S/ " + precioNoche.toPlainString() + ")";
    }
}
