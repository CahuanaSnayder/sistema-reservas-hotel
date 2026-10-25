package dao;

import config.ConexionPostgres;
import exception.DataAccessException;
import model.Reserva;
import repository.ReservaRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Implementación PostgreSQL del contrato ReservaRepository. */
public class ReservaDAO implements ReservaRepository {

    private static final String COLUMNAS = "id_reserva,id_huesped,id_habitacion,fecha_ingreso,"
            + "fecha_salida,temporada,costo_hospedaje,total,estado,fecha_registro";

    @Override
    public boolean registrar(Reserva reserva) {
        String sql = "INSERT INTO reservas(id_huesped,id_habitacion,fecha_ingreso,fecha_salida,"
                + "temporada,costo_hospedaje,total,estado,fecha_registro) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reserva.getIdHuesped());
            ps.setInt(2, reserva.getIdHabitacion());
            ps.setDate(3, Date.valueOf(reserva.getFechaIngreso()));
            ps.setDate(4, Date.valueOf(reserva.getFechaSalida()));
            ps.setString(5, reserva.getTemporada());
            ps.setBigDecimal(6, reserva.getCostoHospedaje());
            ps.setBigDecimal(7, reserva.getTotal());
            ps.setString(8, reserva.getEstado());
            ps.setDate(9, Date.valueOf(reserva.getFechaRegistro()));
            if (ps.executeUpdate() == 0) {
                return false;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    reserva.setIdReserva(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo registrar la reserva.", e);
        }
    }

    @Override
    public List<Reserva> listar() {
        String sql = "SELECT " + COLUMNAS + " FROM reservas ORDER BY id_reserva";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron listar las reservas.", e);
        }
    }

    @Override
    public List<Reserva> listarPorHuesped(int idHuesped) {
        String sql = "SELECT " + COLUMNAS + " FROM reservas WHERE id_huesped=? ORDER BY fecha_ingreso";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHuesped);
            return mapearLista(ps);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron listar las reservas del huésped.", e);
        }
    }

    @Override
    public Reserva buscarPorId(int idReserva) {
        String sql = "SELECT " + COLUMNAS + " FROM reservas WHERE id_reserva=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            List<Reserva> resultado = mapearLista(ps);
            return resultado.isEmpty() ? null : resultado.get(0);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar la reserva.", e);
        }
    }

    @Override
    public boolean actualizarEstado(Reserva reserva) {
        String sql = "UPDATE reservas SET estado=? WHERE id_reserva=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reserva.getEstado());
            ps.setInt(2, reserva.getIdReserva());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar el estado de la reserva.", e);
        }
    }

    @Override
    public boolean eliminar(int idReserva) {
        String sql = "DELETE FROM reservas WHERE id_reserva=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar la reserva.", e);
        }
    }

    @Override
    public boolean existeCruce(int idHabitacion, LocalDate ingreso, LocalDate salida) {
        String sql = "SELECT 1 FROM reservas WHERE id_habitacion=? "
                + "AND estado IN ('PENDIENTE','CONFIRMADA','EN_CURSO') "
                + "AND fecha_ingreso < ? AND fecha_salida > ? LIMIT 1";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setDate(2, Date.valueOf(salida));
            ps.setDate(3, Date.valueOf(ingreso));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo verificar la disponibilidad de la habitación.", e);
        }
    }

    private List<Reserva> mapearLista(PreparedStatement ps) throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdHuesped(rs.getInt("id_huesped"));
                reserva.setIdHabitacion(rs.getInt("id_habitacion"));
                reserva.setFechaIngreso(rs.getDate("fecha_ingreso").toLocalDate());
                reserva.setFechaSalida(rs.getDate("fecha_salida").toLocalDate());
                reserva.setTemporada(rs.getString("temporada"));
                reserva.setCostoHospedaje(rs.getBigDecimal("costo_hospedaje"));
                reserva.setTotal(rs.getBigDecimal("total"));
                reserva.setEstado(rs.getString("estado"));
                Date registro = rs.getDate("fecha_registro");
                reserva.setFechaRegistro(registro == null ? null : registro.toLocalDate());
                lista.add(reserva);
            }
        }
        return lista;
    }
}
