package dao;

import config.ConexionPostgres;
import exception.DataAccessException;
import model.Huesped;
import repository.HuespedRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementación PostgreSQL del contrato HuespedRepository. */
public class HuespedDAO implements HuespedRepository {

    private static final String COLUMNAS = "id_huesped,nombre,documento,email,telefono,fecha_registro";

    @Override
    public boolean registrar(Huesped huesped) {
        String sql = "INSERT INTO huespedes(nombre,documento,email,telefono,fecha_registro) VALUES(?,?,?,?,?)";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, huesped.getNombre());
            ps.setString(2, huesped.getDocumento());
            ps.setString(3, huesped.getEmail());
            ps.setString(4, huesped.getTelefono());
            ps.setDate(5, Date.valueOf(huesped.getFechaRegistro()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DataAccessException("Ya existe un huésped con ese documento o correo.", e);
            }
            throw new DataAccessException("No se pudo registrar el huésped.", e);
        }
    }

    @Override
    public List<Huesped> listar() {
        List<Huesped> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM huespedes ORDER BY id_huesped";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron listar los huéspedes.", e);
        }
    }

    @Override
    public Huesped buscarPorId(int idHuesped) {
        String sql = "SELECT " + COLUMNAS + " FROM huespedes WHERE id_huesped=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHuesped);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar el huésped.", e);
        }
    }

    @Override
    public boolean actualizar(Huesped huesped) {
        String sql = "UPDATE huespedes SET nombre=?, documento=?, email=?, telefono=? WHERE id_huesped=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, huesped.getNombre());
            ps.setString(2, huesped.getDocumento());
            ps.setString(3, huesped.getEmail());
            ps.setString(4, huesped.getTelefono());
            ps.setInt(5, huesped.getIdHuesped());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new DataAccessException("Ya existe otro huésped con ese documento o correo.", e);
            }
            throw new DataAccessException("No se pudo actualizar el huésped.", e);
        }
    }

    @Override
    public boolean eliminar(int idHuesped) {
        String sql = "DELETE FROM huespedes WHERE id_huesped=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHuesped);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new DataAccessException(
                        "No se puede eliminar el huésped porque tiene reservas registradas.", e);
            }
            throw new DataAccessException("No se pudo eliminar el huésped.", e);
        }
    }

    private Huesped mapear(ResultSet rs) throws SQLException {
        Huesped huesped = new Huesped();
        huesped.setIdHuesped(rs.getInt("id_huesped"));
        huesped.setNombre(rs.getString("nombre"));
        huesped.setDocumento(rs.getString("documento"));
        huesped.setEmail(rs.getString("email"));
        huesped.setTelefono(rs.getString("telefono"));
        Date fecha = rs.getDate("fecha_registro");
        huesped.setFechaRegistro(fecha == null ? null : fecha.toLocalDate());
        return huesped;
    }
}
