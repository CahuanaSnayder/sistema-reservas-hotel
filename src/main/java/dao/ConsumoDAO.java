package dao;

import config.ConexionPostgres;
import exception.DataAccessException;
import model.ConsumoServicio;
import model.Reserva;
import repository.ConsumoRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Registra y elimina consumos recalculando el total de la reserva en una sola transacción. */
public class ConsumoDAO implements ConsumoRepository {

    private static final String COLUMNAS = "id_consumo,id_reserva,id_servicio,cantidad,subtotal";

    @Override
    public List<ConsumoServicio> listarPorReserva(int idReserva) {
        try (Connection con = ConexionPostgres.getInstancia().conectar()) {
            return listarPorReserva(con, idReserva);
        } catch (SQLException e) {
            throw new DataAccessException("No se pudieron listar los consumos.", e);
        }
    }

    @Override
    public ConsumoServicio buscarPorId(int idConsumo) {
        String sql = "SELECT " + COLUMNAS + " FROM consumos_servicio WHERE id_consumo=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idConsumo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo buscar el consumo.", e);
        }
    }

    @Override
    public boolean registrarYActualizarTotal(Reserva reserva, ConsumoServicio consumo) {
        String insertar = "INSERT INTO consumos_servicio(id_reserva,id_servicio,cantidad,subtotal) VALUES(?,?,?,?)";
        try (Connection con = ConexionPostgres.getInstancia().conectar()) {
            con.setAutoCommit(false);
            try {
                bloquearReserva(con, reserva.getIdReserva());
                try (PreparedStatement ps = con.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, consumo.getIdReserva());
                    ps.setInt(2, consumo.getIdServicio());
                    ps.setInt(3, consumo.getCantidad());
                    ps.setBigDecimal(4, consumo.getSubtotal());
                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            consumo.setIdConsumo(keys.getInt(1));
                        }
                    }
                }
                actualizarTotal(con, reserva);
                con.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                rollbackSilencioso(con);
                throw e;
            } finally {
                restaurarAutoCommit(con);
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo registrar el consumo.", e);
        }
    }

    @Override
    public boolean eliminarYActualizarTotal(Reserva reserva, int idConsumo) {
        String eliminar = "DELETE FROM consumos_servicio WHERE id_consumo=? AND id_reserva=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar()) {
            con.setAutoCommit(false);
            try {
                bloquearReserva(con, reserva.getIdReserva());
                try (PreparedStatement ps = con.prepareStatement(eliminar)) {
                    ps.setInt(1, idConsumo);
                    ps.setInt(2, reserva.getIdReserva());
                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return false;
                    }
                }
                actualizarTotal(con, reserva);
                con.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                rollbackSilencioso(con);
                throw e;
            } finally {
                restaurarAutoCommit(con);
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo eliminar el consumo.", e);
        }
    }

    private void bloquearReserva(Connection con, int idReserva) throws SQLException {
        String sql = "SELECT id_reserva FROM reservas WHERE id_reserva=? FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("La reserva ya no existe.");
                }
            }
        }
    }

    private void actualizarTotal(Connection con, Reserva reserva) throws SQLException {
        BigDecimal total = reserva.calcularTotal(listarPorReserva(con, reserva.getIdReserva()));
        try (PreparedStatement ps = con.prepareStatement("UPDATE reservas SET total=? WHERE id_reserva=?")) {
            ps.setBigDecimal(1, total);
            ps.setInt(2, reserva.getIdReserva());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("La reserva ya no existe.");
            }
        }
    }

    private List<ConsumoServicio> listarPorReserva(Connection con, int idReserva) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM consumos_servicio WHERE id_reserva=? ORDER BY id_consumo";
        List<ConsumoServicio> consumos = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consumos.add(mapear(rs));
                }
            }
        }
        return consumos;
    }

    private ConsumoServicio mapear(ResultSet rs) throws SQLException {
        ConsumoServicio consumo = new ConsumoServicio();
        consumo.setIdConsumo(rs.getInt("id_consumo"));
        consumo.setIdReserva(rs.getInt("id_reserva"));
        consumo.setIdServicio(rs.getInt("id_servicio"));
        consumo.setCantidad(rs.getInt("cantidad"));
        consumo.setSubtotal(rs.getBigDecimal("subtotal"));
        return consumo;
    }

    private void rollbackSilencioso(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ignored) {
            // Se conserva la excepción original.
        }
    }

    private void restaurarAutoCommit(Connection con) {
        try {
            con.setAutoCommit(true);
        } catch (SQLException ignored) {
            // La conexión se cerrará inmediatamente.
        }
    }
}
