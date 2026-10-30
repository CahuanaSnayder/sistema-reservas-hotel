package api.rest;

import api.rest.Solicitudes.HabitacionDatos;
import api.rest.Solicitudes.Mensaje;
import model.Habitacion;
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
import service.HabitacionService;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionRestController {

    private final HabitacionService service;

    public HabitacionRestController(HabitacionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Habitacion> listar() {
        return service.listar();
    }

    @GetMapping("/tipos")
    public List<String> tipos() {
        return HabitacionService.TIPOS;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensaje registrar(@RequestBody HabitacionDatos d) {
        return Respuestas.exito(service.registrar(d.numero(), d.tipo(), d.capacidad(), d.precioNoche()),
                "Habitación registrada correctamente.");
    }

    @PutMapping("/{id}")
    public Mensaje actualizar(@PathVariable int id, @RequestBody HabitacionDatos d) {
        return Respuestas.exito(service.actualizar(id, d.numero(), d.tipo(), d.capacidad(), d.precioNoche()),
                "Habitación actualizada correctamente.");
    }

    @DeleteMapping("/{id}")
    public Mensaje eliminar(@PathVariable int id) {
        return Respuestas.exito(service.eliminar(id), "Habitación eliminada correctamente.");
    }
}
