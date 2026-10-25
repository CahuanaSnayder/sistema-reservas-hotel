package service;

import model.Usuario;
import repository.UsuarioRepository;
import util.PasswordUtil;

/** SRP: solo contiene la lógica de autenticación. */
public class AuthService {
    private final UsuarioRepository repository;

    public AuthService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public boolean iniciarSesion(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Ingrese usuario y contraseña.");
        }
        Usuario encontrado = repository.buscarPorUsuario(usuario.trim());
        if (encontrado == null || !PasswordUtil.verificar(contrasena, encontrado.getContrasena())) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        }
        if (!PasswordUtil.esHash(encontrado.getContrasena())) {
            repository.actualizarContrasena(encontrado.getIdUsuario(), PasswordUtil.hash(contrasena));
        }
        return true;
    }
}
