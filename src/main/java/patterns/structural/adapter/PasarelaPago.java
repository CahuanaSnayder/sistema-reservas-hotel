package patterns.structural.adapter;

import model.Reserva;

import java.math.BigDecimal;

/** Interfaz objetivo (Target) que usa el sistema para cobrar. */
public interface PasarelaPago {
    /** Devuelve el código de operación generado por el proveedor de pagos. */
    String procesarPago(Reserva reserva, BigDecimal monto, String concepto);
}
