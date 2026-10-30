package api.rest;

import api.rest.Solicitudes.Login;
import api.rest.Solicitudes.Mensaje;
import api.seguridad.SesionInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;

    public AuthRestController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mensaje login(@RequestBody Login datos, HttpServletRequest request) {
        authService.iniciarSesion(datos.usuario(), datos.contrasena());
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate(); // evita la fijación de sesión
        }
        request.getSession(true).setAttribute(SesionInterceptor.ATRIBUTO_USUARIO, datos.usuario().trim());
        return new Mensaje("Sesión iniciada correctamente.");
    }

    @PostMapping("/logout")
    public Mensaje logout(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        return new Mensaje("Sesión cerrada.");
    }

    @GetMapping("/sesion")
    public ResponseEntity<Mensaje> sesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        Object usuario = sesion == null ? null : sesion.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO);
        return usuario == null
                ? ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("Debe iniciar sesión."))
                : ResponseEntity.ok(new Mensaje(usuario.toString()));
    }
}
