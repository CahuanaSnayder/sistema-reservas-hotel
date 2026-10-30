package api.rest;

import api.rest.Solicitudes.Mensaje;
import api.rest.Solicitudes.ServicioDatos;
import model.Servicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import service.ServicioHotelService;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioRestController {

    private final ServicioHotelService service;

    public ServicioRestController(ServicioHotelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Servicio> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensaje registrar(@RequestBody ServicioDatos d) {
        return Respuestas.exito(service.registrar(d.nombre(), d.precio()), "Servicio registrado correctamente.");
    }

    @PutMapping("/{id}")
    public Mensaje actualizar(@PathVariable int id, @RequestBody ServicioDatos d) {
        return Respuestas.exito(service.actualizar(id, d.nombre(), d.precio()), "Servicio actualizado correctamente.");
    }

    @DeleteMapping("/{id}")
    public Mensaje eliminar(@PathVariable int id) {
        return Respuestas.exito(service.eliminar(id), "Servicio eliminado correctamente.");
    }
}
