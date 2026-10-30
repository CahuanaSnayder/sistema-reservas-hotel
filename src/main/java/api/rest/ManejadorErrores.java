package api.rest;

import api.rest.Solicitudes.Mensaje;
import exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;

/**
 * Traduce las excepciones de negocio a respuestas HTTP con un mensaje legible,
 * igual que SistemaController lo hace para la interfaz Swing.
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger LOG = LoggerFactory.getLogger(ManejadorErrores.class);

    /** Lanzada por los controladores cuando el servicio devuelve false. */
    public static class OperacionFallidaException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public OperacionFallidaException(String mensaje) {
            super(mensaje);
        }
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    ResponseEntity<Mensaje> reglaDeNegocio(RuntimeException e) {
        return respuesta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(OperacionFallidaException.class)
    ResponseEntity<Mensaje> operacionFallida(OperacionFallidaException e) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<Mensaje> datosInvalidos(Exception e) {
        return respuesta(HttpStatus.BAD_REQUEST, "Los datos enviados no tienen un formato válido.");
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<Mensaje> baseDeDatos(DataAccessException e) {
        // SQLState 23xxx = restricción violada (duplicado, registro en uso): conflicto esperado, no una caída.
        if (e.getCause() instanceof SQLException sql && sql.getSQLState() != null
                && sql.getSQLState().startsWith("23")) {
            LOG.info("Operación rechazada por la base de datos: {}", e.getMessage());
            return respuesta(HttpStatus.CONFLICT, e.getMessage());
        }
        LOG.error("Error de acceso a datos", e);
        return respuesta(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<Mensaje> inesperado(RuntimeException e) {
        LOG.error("Error inesperado", e);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado al procesar la operación.");
    }

    private ResponseEntity<Mensaje> respuesta(HttpStatus estado, String mensaje) {
        String texto = mensaje == null || mensaje.isBlank()
                ? "Ocurrió un error inesperado al procesar la operación."
                : mensaje;
        return ResponseEntity.status(estado).body(new Mensaje(texto));
    }
}
