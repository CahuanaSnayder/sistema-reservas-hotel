package api.rest;

import api.rest.ManejadorErrores.OperacionFallidaException;
import api.rest.Solicitudes.Mensaje;

/** Convierte el boolean que devuelven los servicios en una respuesta o un error. */
final class Respuestas {

    private Respuestas() {
    }

    static Mensaje exito(boolean resultado, String mensajeExito) {
        if (!resultado) {
            throw new OperacionFallidaException("La operación no pudo completarse. Verifique que el registro exista.");
        }
        return new Mensaje(mensajeExito);
    }
}
