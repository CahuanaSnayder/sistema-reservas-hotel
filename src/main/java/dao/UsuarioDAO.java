package dao;

import config.ConexionPostgres;
import exception.DataAccessException;
import model.Usuario;
import repository.UsuarioRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO implements UsuarioRepository {

    @Override
    public Usuario buscarPorUsuario(String usuario) {
        String sql = "SELECT id_usuario,usuario,contrasena FROM usuarios WHERE usuario=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Usuario(rs.getInt("id_usuario"), rs.getString("usuario"),
                        rs.getString("contrasena"));
            }
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo consultar el usuario.", e);
        }
    }

    @Override
    public boolean actualizarContrasena(int idUsuario, String contrasenaHash) {
        String sql = "UPDATE usuarios SET contrasena=? WHERE id_usuario=?";
        try (Connection con = ConexionPostgres.getInstancia().conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, contrasenaHash);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("No se pudo actualizar la contraseña.", e);
        }
    }
}
