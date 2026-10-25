package controller;

import dao.ConsumoDAO;
import dao.HabitacionDAO;
import dao.HuespedDAO;
import dao.ReservaDAO;
import dao.ServicioDAO;
import dao.UsuarioDAO;
import model.ConsumoServicio;
import model.Habitacion;
import model.Huesped;
import model.Reserva;
import model.Servicio;
import patterns.behavioral.observer.AuditoriaReservaObserver;
import patterns.behavioral.observer.NotificacionHuespedObserver;
import patterns.behavioral.observer.ReservaEventManager;
import patterns.behavioral.strategy.TarifaSelector;
import patterns.creational.factory.ReporteFactory;
import patterns.creational.factory.TipoReporte;
import patterns.structural.adapter.PagoExternoAdapter;
import patterns.structural.adapter.ServicioPagoExterno;
import patterns.structural.facade.ReservaFacade;
import repository.ConsumoRepository;
import repository.HabitacionRepository;
import repository.HuespedRepository;
import repository.ReservaRepository;
import repository.ServicioRepository;
import repository.UsuarioRepository;
import service.AuthService;
import service.HabitacionService;
import service.HuespedService;
import service.ServicioHotelService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * GRASP CONTROLLER: recibe las operaciones del sistema desde la interfaz Swing
 * y traduce las excepciones de negocio en mensajes para el usuario.
 */
public class SistemaController {

    private final AuthService authService;
    private final HuespedService huespedService;
    private final HabitacionService habitacionService;
    private final ServicioHotelService servicioService;
    private final ReservaController reservaController;
    private final ReporteFactory reporteFactory;
    private String ultimoMensaje = "";

    /** Composition Root: conecta implementaciones concretas con abstracciones (DIP). */
    public SistemaController() {
        UsuarioRepository usuarioRepository = new UsuarioDAO();
        HuespedRepository huespedRepository = new HuespedDAO();
        HabitacionRepository habitacionRepository = new HabitacionDAO();
        ServicioRepository servicioRepository = new ServicioDAO();
        ReservaRepository reservaRepository = new ReservaDAO();
        ConsumoRepository consumoRepository = new ConsumoDAO();

        ReservaEventManager eventos = new ReservaEventManager();
        eventos.suscribir(new AuditoriaReservaObserver());
        eventos.suscribir(new NotificacionHuespedObserver(huespedRepository));

        ReservaFacade reservaFacade = new ReservaFacade(
                huespedRepository,
                habitacionRepository,
                servicioRepository,
                reservaRepository,
                consumoRepository,
                new TarifaSelector(),
                new PagoExternoAdapter(new ServicioPagoExterno()),
                eventos
        );

        authService = new AuthService(usuarioRepository);
        huespedService = new HuespedService(huespedRepository);
        habitacionService = new HabitacionService(habitacionRepository);
        servicioService = new ServicioHotelService(servicioRepository);
        reservaController = new ReservaController(reservaFacade);
        reporteFactory = new ReporteFactory(huespedRepository, habitacionRepository, reservaRepository);
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    // ---------- Autenticación ----------

    public boolean iniciarSesion(String usuario, String contrasena) {
        return ejecutar(() -> authService.iniciarSesion(usuario, contrasena), "Sesión iniciada correctamente.");
    }

    // ---------- Huéspedes ----------

    public boolean registrarHuesped(String nombre, String documento, String email, String telefono) {
        return ejecutar(() -> huespedService.registrar(nombre, documento, email, telefono),
                "Huésped registrado correctamente.");
    }

    public boolean actualizarHuesped(int id, String nombre, String documento, String email, String telefono) {
        return ejecutar(() -> huespedService.actualizar(id, nombre, documento, email, telefono),
                "Huésped actualizado correctamente.");
    }

    public boolean eliminarHuesped(int id) {
        return ejecutar(() -> huespedService.eliminar(id), "Huésped eliminado correctamente.");
    }

    public List<Huesped> listarHuespedes() {
        return consultar(huespedService::listar, Collections.emptyList());
    }

    // ---------- Habitaciones ----------

    public boolean registrarHabitacion(String numero, String tipo, int capacidad, BigDecimal precio) {
        return ejecutar(() -> habitacionService.registrar(numero, tipo, capacidad, precio),
                "Habitación registrada correctamente.");
    }

    public boolean actualizarHabitacion(int id, String numero, String tipo, int capacidad, BigDecimal precio) {
        return ejecutar(() -> habitacionService.actualizar(id, numero, tipo, capacidad, precio),
                "Habitación actualizada correctamente.");
    }

    public boolean eliminarHabitacion(int id) {
        return ejecutar(() -> habitacionService.eliminar(id), "Habitación eliminada correctamente.");
    }

    public List<Habitacion> listarHabitaciones() {
        return consultar(habitacionService::listar, Collections.emptyList());
    }

    public List<String> tiposHabitacion() {
        return HabitacionService.TIPOS;
    }

    // ---------- Servicios adicionales ----------

    public boolean registrarServicio(String nombre, BigDecimal precio) {
        return ejecutar(() -> servicioService.registrar(nombre, precio), "Servicio registrado correctamente.");
    }

    public boolean actualizarServicio(int id, String nombre, BigDecimal precio) {
        return ejecutar(() -> servicioService.actualizar(id, nombre, precio), "Servicio actualizado correctamente.");
    }

    public boolean eliminarServicio(int id) {
        return ejecutar(() -> servicioService.eliminar(id), "Servicio eliminado correctamente.");
    }

    public List<Servicio> listarServicios() {
        return consultar(servicioService::listar, Collections.emptyList());
    }

    // ---------- Reservas ----------

    public boolean registrarReserva(int idHuesped, int idHabitacion, LocalDate ingreso,
                                    LocalDate salida, String temporada) {
        return ejecutar(() -> {
            Reserva reserva = reservaController.registrar(idHuesped, idHabitacion, ingreso, salida, temporada);
            ultimoMensajeExito("Reserva #" + reserva.getIdReserva() + " registrada. Hospedaje: S/ "
                    + reserva.getCostoHospedaje().toPlainString());
            return true;
        }, null);
    }

    public boolean confirmarReserva(int idReserva) {
        return ejecutar(() -> {
            String operacion = reservaController.confirmar(idReserva);
            ultimoMensajeExito("Reserva confirmada. Pago aprobado: " + operacion);
            return true;
        }, null);
    }

    public boolean checkIn(int idReserva) {
        return ejecutar(() -> reservaController.checkIn(idReserva), "Check-in registrado correctamente.");
    }

    public boolean checkOut(int idReserva) {
        return ejecutar(() -> {
            String operacion = reservaController.checkOut(idReserva);
            ultimoMensajeExito("Check-out registrado. Cobro de servicios: " + operacion);
            return true;
        }, null);
    }

    public boolean cancelarReserva(int idReserva) {
        return ejecutar(() -> reservaController.cancelar(idReserva), "Reserva cancelada correctamente.");
    }

    public boolean eliminarReserva(int idReserva) {
        return ejecutar(() -> reservaController.eliminar(idReserva), "Reserva eliminada correctamente.");
    }

    public List<Reserva> listarReservas() {
        return consultar(reservaController::listar, Collections.emptyList());
    }

    public Reserva buscarReserva(int idReserva) {
        return consultar(() -> reservaController.buscar(idReserva), null);
    }

    public List<String> temporadas() {
        return consultar(reservaController::temporadas, List.of("REGULAR"));
    }

    public boolean agregarConsumo(int idReserva, int idServicio, int cantidad) {
        return ejecutar(() -> reservaController.agregarConsumo(idReserva, idServicio, cantidad),
                "Servicio agregado y total recalculado.");
    }

    public boolean eliminarConsumo(int idConsumo) {
        return ejecutar(() -> reservaController.eliminarConsumo(idConsumo),
                "Servicio retirado y total recalculado.");
    }

    public List<ConsumoServicio> listarConsumos(int idReserva) {
        return consultar(() -> reservaController.listarConsumos(idReserva), Collections.emptyList());
    }

    // ---------- Reportes (Factory Method) ----------

    public String generarReporteHuesped(int idHuesped) {
        String reporte = consultar(() -> reporteFactory.crear(TipoReporte.HUESPED).generar(idHuesped), null);
        return reporte != null ? reporte : "No se pudo generar el reporte: " + ultimoMensaje;
    }

    public String generarReporteOcupacion() {
        String reporte = consultar(() -> reporteFactory.crear(TipoReporte.OCUPACION_GENERAL).generar(null), null);
        return reporte != null ? reporte : "No se pudo generar el reporte: " + ultimoMensaje;
    }

    // ---------- Manejo uniforme de errores ----------

    private void ultimoMensajeExito(String mensaje) {
        ultimoMensaje = mensaje;
    }

    private boolean ejecutar(Operacion operacion, String mensajeExito) {
        try {
            boolean resultado = operacion.ejecutar();
            if (mensajeExito != null) {
                ultimoMensaje = resultado ? mensajeExito : "La operación no pudo completarse.";
            }
            return resultado;
        } catch (RuntimeException e) {
            ultimoMensaje = mensajeSeguro(e);
            return false;
        }
    }

    private <T> T consultar(Supplier<T> consulta, T valorAlternativo) {
        try {
            T resultado = consulta.get();
            ultimoMensaje = "";
            return resultado;
        } catch (RuntimeException e) {
            ultimoMensaje = mensajeSeguro(e);
            return valorAlternativo;
        }
    }

    private String mensajeSeguro(RuntimeException e) {
        String mensaje = e.getMessage();
        return mensaje == null || mensaje.isBlank()
                ? "Ocurrió un error inesperado al procesar la operación."
                : mensaje;
    }

    @FunctionalInterface
    private interface Operacion {
        boolean ejecutar();
    }
}
