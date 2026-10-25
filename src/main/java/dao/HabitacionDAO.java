package dao;

import config.ConexionPostgres;
import exception.DataAccessException;
import model.Habitacion;
import repository.HabitacionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementación PostgreSQL del contrato HabitacionRepository. */
public class HabitacionDAO implements HabitacionRepository {

    private static final String COLUMNAS = "id_habitacion,numero,tipo,capacidad,precio_noche";

    @Override
    public boolean registrar(Habitacion habitacion) {
        String sql = "INSERT INTO habitaciones(numero,tipo,capacidad,precio_noche) VALUES(?,?,?,?)";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, habitacion.getNumero());
            ps.setString(2, habitacion.getTipo());
            ps.setInt(3, habitacion.getCapacidad());
            ps.setBigDecimal(4, habitacion.getPrecioNoche());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DataAccessException("Ya existe una habitación con ese número.", e);
            }
            throw new DataAccessException("No se pudo registrar la habitación.", e);
        }
    }

    @Override
    public List<Habitacion> listar() {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM habitaciones ORDER BY numero";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron listar las habitaciones.", e);
        }
    }

    @Override
    public Habitacion buscarPorId(int idHabitacion) {
        String sql = "SELECT " + COLUMNAS + " FROM habitaciones WHERE id_habitacion=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar la habitación.", e);
        }
    }

    @Override
    public boolean actualizar(Habitacion habitacion) {
        String sql = "UPDATE habitaciones SET numero=?, tipo=?, capacidad=?, precio_noche=? WHERE id_habitacion=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, habitacion.getNumero());
            ps.setString(2, habitacion.getTipo());
            ps.setInt(3, habitacion.getCapacidad());
            ps.setBigDecimal(4, habitacion.getPrecioNoche());
            ps.setInt(5, habitacion.getIdHabitacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DataAccessException("Ya existe otra habitación con ese número.", e);
            }
            throw new DataAccessException("No se pudo actualizar la habitación.", e);
        }
    }

    @Override
    public boolean eliminar(int idHabitacion) {
        String sql = "DELETE FROM habitaciones WHERE id_habitacion=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new DataAccessException(
                        "No se puede eliminar la habitación porque tiene reservas registradas.", e);
            }
            throw new DataAccessException("No se pudo eliminar la habitación.", e);
        }
    }

    private Habitacion mapear(ResultSet rs) throws SQLException {
        return new Habitacion(
                rs.getInt("id_habitacion"),
                rs.getString("numero"),
                rs.getString("tipo"),
                rs.getInt("capacidad"),
                rs.getBigDecimal("precio_noche"));
    }
}
