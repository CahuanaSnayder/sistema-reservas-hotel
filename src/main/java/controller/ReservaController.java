package controller;

import model.ConsumoServicio;
import model.Reserva;
import patterns.structural.facade.ReservaFacade;

import java.time.LocalDate;
import java.util.List;

/** GRASP Controller: recibe las acciones de la vista de reservas y delega en el Facade. */
public class ReservaController {
    private final ReservaFacade facade;

    public ReservaController(ReservaFacade facade) {
        this.facade = facade;
    }

    public Reserva registrar(int idHuesped, int idHabitacion, LocalDate ingreso, LocalDate salida, String temporada) {
        return facade.registrarReserva(idHuesped, idHabitacion, ingreso, salida, temporada);
    }
    public String confirmar(int idReserva) { return facade.confirmarReserva(idReserva); }
    public boolean checkIn(int idReserva) { return facade.registrarCheckIn(idReserva); }
    public String checkOut(int idReserva) { return facade.registrarCheckOut(idReserva); }
    public boolean cancelar(int idReserva) { return facade.cancelarReserva(idReserva); }
    public boolean eliminar(int idReserva) { return facade.eliminarReserva(idReserva); }
    public boolean agregarConsumo(int idReserva, int idServicio, int cantidad) {
        return facade.agregarConsumo(idReserva, idServicio, cantidad);
    }
    public boolean eliminarConsumo(int idConsumo) { return facade.eliminarConsumo(idConsumo); }
    public List<Reserva> listar() { return facade.listarReservas(); }
    public Reserva buscar(int idReserva) { return facade.buscarReserva(idReserva); }
    public List<ConsumoServicio> listarConsumos(int idReserva) { return facade.listarConsumos(idReserva); }
    public List<String> temporadas() { return facade.temporadas(); }
}
