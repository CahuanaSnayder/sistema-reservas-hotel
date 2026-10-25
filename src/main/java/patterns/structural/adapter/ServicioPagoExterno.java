package patterns.structural.adapter;

import java.util.UUID;

/**
 * Servicio de pagos de un proveedor externo (Adaptee). Trabaja con montos en
 * céntimos y una referencia de texto, una firma distinta a la del sistema.
 */
public class ServicioPagoExterno {
    public String cobrar(String referencia, long montoCentimos, String moneda) {
        if (montoCentimos <= 0) {
            throw new IllegalArgumentException("El monto a cobrar debe ser mayor que cero.");
        }
        String codigo = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("[PASARELA EXTERNA] " + referencia + " | " + moneda + " "
                + (montoCentimos / 100.0) + " | Operación " + codigo);
        return codigo;
    }
}
