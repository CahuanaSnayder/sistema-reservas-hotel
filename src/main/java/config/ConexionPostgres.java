package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PATRÓN CREACIONAL: SINGLETON.
 *
 * Centraliza la configuración de PostgreSQL. Los valores pueden definirse con
 * propiedades Java (-Ddb.url, -Ddb.user, -Ddb.password) o variables de entorno
 * (DB_URL, DB_USER, DB_PASSWORD).
 */
public final class ConexionPostgres {

    private static final String URL = obtenerConfiguracion(
            "db.url", "DB_URL", "jdbc:postgresql://localhost:5432/sistema_reservas_hotel");
    private static final String USER = obtenerConfiguracion(
            "db.user", "DB_USER", "postgres");
    private static final String PASSWORD = obtenerConfiguracion(
            "db.password", "DB_PASSWORD", "admin");

    private ConexionPostgres() {
    }

    private static class Holder {
        private static final ConexionPostgres INSTANCIA = new ConexionPostgres();
    }

    public static ConexionPostgres getInstancia() {
        return Holder.INSTANCIA;
    }

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void probarConexion() {
        try (Connection ignored = conectar()) {
            System.out.println("Conexión con PostgreSQL establecida.");
        } catch (SQLException e) {
            throw new exception.DataAccessException(
                    "No se pudo conectar a PostgreSQL. Verifique que la base "
                            + "'sistema_reservas_hotel' exista, que el script "
                            + "database/sistema_reservas_hotel.sql haya sido ejecutado "
                            + "y que las credenciales sean correctas.", e);
        }
    }

    private static String obtenerConfiguracion(String propiedad, String variableEntorno,
                                                String valorPredeterminado) {
        String valorPropiedad = System.getProperty(propiedad);
        if (valorPropiedad != null && !valorPropiedad.isBlank()) {
            return valorPropiedad.trim();
        }

        String valorEntorno = System.getenv(variableEntorno);
        if (valorEntorno != null && !valorEntorno.isBlank()) {
            return valorEntorno.trim();
        }
        return valorPredeterminado;
    }
}
