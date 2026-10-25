package dao;

import config.ConexionPostgres;
import exception.DataAccessException;
import model.Servicio;
import repository.ServicioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementación PostgreSQL del contrato ServicioRepository. */
public class ServicioDAO implements ServicioRepository {

    @Override
    public boolean registrar(Servicio servicio) {
        String sql = "INSERT INTO servicios(nombre,precio) VALUES(?,?)";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setBigDecimal(2, servicio.getPrecio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo registrar el servicio.", e);
        }
    }

    @Override
    public List<Servicio> listar() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id_servicio,nombre,precio FROM servicios ORDER BY id_servicio";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron listar los servicios.", e);
        }
    }

    @Override
    public Servicio buscarPorId(int idServicio) {
        String sql = "SELECT id_servicio,nombre,precio FROM servicios WHERE id_servicio=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar el servicio.", e);
        }
    }

    @Override
    public boolean actualizar(Servicio servicio) {
        String sql = "UPDATE servicios SET nombre=?, precio=? WHERE id_servicio=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, servicio.getNombre());
            ps.setBigDecimal(2, servicio.getPrecio());
            ps.setInt(3, servicio.getIdServicio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el servicio.", e);
        }
    }

    @Override
    public boolean eliminar(int idServicio) {
        String sql = "DELETE FROM servicios WHERE id_servicio=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new DataAccessException(
                        "No se puede eliminar el servicio porque ya fue consumido en reservas.", e);
            }
            throw new DataAccessException("No se pudo eliminar el servicio.", e);
        }
    }

    private Servicio mapear(ResultSet rs) throws SQLException {
        return new Servicio(rs.getInt("id_servicio"), rs.getString("nombre"), rs.getBigDecimal("precio"));
    }
}
