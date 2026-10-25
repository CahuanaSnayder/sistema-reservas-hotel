package patterns.structural.adapter;

import model.Reserva;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** PATRÓN ESTRUCTURAL: ADAPTER. Traduce procesarPago(...) a cobrar(...) del proveedor. */
public class PagoExternoAdapter implements PasarelaPago {

    private static final String MONEDA = "PEN";

    private final ServicioPagoExterno servicioExterno;

    public PagoExternoAdapter(ServicioPagoExterno servicioExterno) {
        this.servicioExterno = servicioExterno;
    }

    @Override
    public String procesarPago(Reserva reserva, BigDecimal monto, String concepto) {
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero.");
        }
        long centimos = monto.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
        String referencia = "RESERVA-" + reserva.getIdReserva() + " " + concepto;
        return servicioExterno.cobrar(referencia, centimos, MONEDA);
    }
}
