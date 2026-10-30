package api.rest;

import api.rest.Solicitudes.HuespedDatos;
import api.rest.Solicitudes.Mensaje;
import model.Huesped;
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
import service.HuespedService;

import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
public class HuespedRestController {

    private final HuespedService service;

    public HuespedRestController(HuespedService service) {
        this.service = service;
    }

    @GetMapping
    public List<Huesped> listar() {
        return service.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensaje registrar(@RequestBody HuespedDatos d) {
        return Respuestas.exito(service.registrar(d.nombre(), d.documento(), d.email(), d.telefono()),
                "Huésped registrado correctamente.");
    }

    @PutMapping("/{id}")
    public Mensaje actualizar(@PathVariable int id, @RequestBody HuespedDatos d) {
        return Respuestas.exito(service.actualizar(id, d.nombre(), d.documento(), d.email(), d.telefono()),
                "Huésped actualizado correctamente.");
    }

    @DeleteMapping("/{id}")
    public Mensaje eliminar(@PathVariable int id) {
        return Respuestas.exito(service.eliminar(id), "Huésped eliminado correctamente.");
    }
}
