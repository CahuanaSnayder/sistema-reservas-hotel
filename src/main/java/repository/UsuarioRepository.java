package repository;

import model.Usuario;

public interface UsuarioRepository {
    Usuario buscarPorUsuario(String usuario);
    boolean actualizarContrasena(int idUsuario, String contrasenaHash);
}
