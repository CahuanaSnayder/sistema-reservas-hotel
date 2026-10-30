package api.rest;

import api.rest.ManejadorErrores.OperacionFallidaException;
import api.rest.Solicitudes.ConsumoDatos;
import api.rest.Solicitudes.Mensaje;
import api.rest.Solicitudes.ReservaDatos;
import model.ConsumoServicio;
import model.Reserva;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import patterns.structural.facade.ReservaFacade;

import java.util.List;

/** Expone el ciclo de vida de la reserva delegando en el Facade (igual que ReservaController). */
@RestController
@RequestMapping("/api")
public class ReservaRestController {

    private final ReservaFacade facade;

    public ReservaRestController(ReservaFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/reservas")
    public List<Reserva> listar() {
        return facade.listarReservas();
    }

    @GetMapping("/reservas/temporadas")
    public List<String> temporadas() {
        return facade.temporadas();
    }

    @GetMapping("/reservas/{id}")
    public Reserva buscar(@PathVariable int id) {
        Reserva reserva = facade.buscarReserva(id);
        if (reserva == null) {
            throw new OperacionFallidaException("La reserva no existe.");
        }
        return reserva;
    }

    @PostMapping("/reservas")
    @ResponseStatus(HttpStatus.CREATED)
    public Mensaje registrar(@RequestBody ReservaDatos d) {
        Reserva reserva = facade.registrarReserva(d.idHuesped(), d.idHabitacion(),
                d.fechaIngreso(), d.fechaSalida(), d.temporada());
        return new Mensaje("Reserva #" + reserva.getIdReserva() + " registrada. Hospedaje: S/ "
                + reserva.getCostoHospedaje().toPlainString());
    }

    @PostMapping("/reservas/{id}/confirmar")
    public Mensaje confirmar(@PathVariable int id) {
        return new Mensaje("Reserva confirmada. Pago aprobado: " + facade.confirmarReserva(id));
    }

    @PostMapping("/reservas/{id}/check-in")
    public Mensaje checkIn(@PathVariable int id) {
        return Respuestas.exito(facade.registrarCheckIn(id), "Check-in registrado correctamente.");
    }

    @PostMapping("/reservas/{id}/check-out")
    public Mensaje checkOut(@PathVariable int id) {
        return new Mensaje("Check-out registrado. Cobro de servicios: " + facade.registrarCheckOut(id));
    }

    @PostMapping("/reservas/{id}/cancelar")
    public Mensaje cancelar(@PathVariable int id) {
        return Respuestas.exito(facade.cancelarReserva(id), "Reserva cancelada correctamente.");
    }

    @DeleteMapping("/reservas/{id}")
    public Mensaje eliminar(@PathVariable int id) {
        return Respuestas.exito(facade.eliminarReserva(id), "Reserva eliminada correctamente.");
    }

    // ---------- Consumos de servicios adicionales ----------

    @GetMapping("/reservas/{id}/consumos")
    public List<ConsumoServicio> listarConsumos(@PathVariable int id) {
        return facade.listarConsumos(id);
    }

    @PostMapping("/reservas/{id}/consumos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mensaje agregarConsumo(@PathVariable int id, @RequestBody ConsumoDatos d) {
        return Respuestas.exito(facade.agregarConsumo(id, d.idServicio(), d.cantidad()),
                "Servicio agregado y total recalculado.");
    }

    @DeleteMapping("/consumos/{idConsumo}")
    public Mensaje eliminarConsumo(@PathVariable int idConsumo) {
        return Respuestas.exito(facade.eliminarConsumo(idConsumo), "Servicio retirado y total recalculado.");
    }
}
