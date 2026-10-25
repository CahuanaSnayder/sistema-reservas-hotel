package patterns.structural.facade;

import model.ConsumoServicio;
import model.Habitacion;
import model.Reserva;
import model.Servicio;
import patterns.behavioral.observer.ReservaEventManager;
import patterns.behavioral.strategy.TarifaSelector;
import patterns.behavioral.strategy.TarifaStrategy;
import patterns.creational.builder.ReservaBuilder;
import patterns.structural.adapter.PasarelaPago;
import repository.ConsumoRepository;
import repository.HabitacionRepository;
import repository.HuespedRepository;
import repository.ReservaRepository;
import repository.ServicioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * PATRÓN ESTRUCTURAL: FACADE.
 * Ofrece una API simple para el proceso de reserva y oculta la coordinación entre
 * repositorios, Builder, Strategy, State, Adapter (pagos) y Observer (eventos).
 */
public class ReservaFacade {

    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;
    private final ServicioRepository servicioRepository;
    private final ReservaRepository reservaRepository;
    private final ConsumoRepository consumoRepository;
    private final TarifaSelector tarifas;
    private final PasarelaPago pasarelaPago;
    private final ReservaEventManager eventos;

    public ReservaFacade(HuespedRepository huespedRepository,
                         HabitacionRepository habitacionRepository,
                         ServicioRepository servicioRepository,
                         ReservaRepository reservaRepository,
                         ConsumoRepository consumoRepository,
                         TarifaSelector tarifas,
                         PasarelaPago pasarelaPago,
                         ReservaEventManager eventos) {
        this.huespedRepository = huespedRepository;
        this.habitacionRepository = habitacionRepository;
        this.servicioRepository = servicioRepository;
        this.reservaRepository = reservaRepository;
        this.consumoRepository = consumoRepository;
        this.tarifas = tarifas;
        this.pasarelaPago = pasarelaPago;
        this.eventos = eventos;
    }

    public Reserva registrarReserva(int idHuesped, int idHabitacion, LocalDate ingreso,
                                    LocalDate salida, String temporada) {
        if (idHuesped <= 0 || huespedRepository.buscarPorId(idHuesped) == null) {
            throw new IllegalArgumentException("El huésped seleccionado no existe.");
        }
        Habitacion habitacion = idHabitacion <= 0 ? null : habitacionRepository.buscarPorId(idHabitacion);
        if (habitacion == null) {
            throw new IllegalArgumentException("La habitación seleccionada no existe.");
        }

        Reserva reserva = new ReservaBuilder()
                .paraHuesped(idHuesped)
                .enHabitacion(idHabitacion)
                .desde(ingreso)
                .hasta(salida)
                .enTemporada(temporada)
                .conEstado("PENDIENTE")
                .construir();

        if (reservaRepository.existeCruce(idHabitacion, ingreso, salida)) {
            throw new IllegalStateException("La habitación " + habitacion.getNumero()
                    + " ya está reservada en esas fechas.");
        }

        TarifaStrategy tarifa = tarifas.seleccionar(temporada);
        reserva.calcularHospedaje(habitacion.getPrecioNoche(), tarifa);

        if (!reservaRepository.registrar(reserva)) {
            throw new IllegalStateException("No se pudo registrar la reserva.");
        }
        eventos.notificar(reserva, "Reserva registrada por " + reserva.calcularNoches()
                + " noche(s) en temporada " + tarifa.getTemporada());
        return reserva;
    }

    /** Cobra el hospedaje mediante la pasarela externa y confirma la reserva. */
    public String confirmarReserva(int idReserva) {
        Reserva reserva = obtenerReserva(idReserva);
        reserva.confirmar();
        String operacion = pasarelaPago.procesarPago(reserva, reserva.getCostoHospedaje(), "Hospedaje");
        actualizarEstado(reserva, "Reserva confirmada. Pago de hospedaje " + operacion);
        return operacion;
    }

    public boolean registrarCheckIn(int idReserva) {
        Reserva reserva = obtenerReserva(idReserva);
        reserva.checkIn();
        return actualizarEstado(reserva, "Check-in realizado. ¡Bienvenido(a)!");
    }

    /** Cobra los consumos pendientes (si los hay) y finaliza la estadía. */
    public String registrarCheckOut(int idReserva) {
        Reserva reserva = obtenerReserva(idReserva);
        reserva.checkOut();
        BigDecimal consumos = reserva.calcularConsumos();
        String operacion = consumos.signum() > 0
                ? pasarelaPago.procesarPago(reserva, consumos, "Servicios adicionales")
                : "SIN CONSUMOS";
        actualizarEstado(reserva, "Check-out realizado. Cobro de servicios: " + operacion);
        return operacion;
    }

    public boolean cancelarReserva(int idReserva) {
        Reserva reserva = obtenerReserva(idReserva);
        reserva.cancelar();
        return actualizarEstado(reserva, "Reserva cancelada");
    }

    public boolean eliminarReserva(int idReserva) {
        Reserva reserva = obtenerReserva(idReserva);
        if (!"PENDIENTE".equals(reserva.getEstado()) && !"CANCELADA".equals(reserva.getEstado())) {
            throw new IllegalStateException("Solo se pueden eliminar reservas pendientes o canceladas.");
        }
        return reservaRepository.eliminar(idReserva);
    }

    public boolean agregarConsumo(int idReserva, int idServicio, int cantidad) {
        Reserva reserva = obtenerReserva(idReserva);
        if (!reserva.permiteConsumos()) {
            throw new IllegalStateException("Solo se registran servicios en reservas confirmadas o en curso.");
        }
        Servicio servicio = servicioRepository.buscarPorId(idServicio);
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio seleccionado no existe.");
        }
        ConsumoServicio consumo = ConsumoServicio.crear(idReserva, servicio, cantidad);
        boolean guardado = consumoRepository.registrarYActualizarTotal(reserva, consumo);
        if (guardado) {
            eventos.notificar(reserva, "Servicio agregado: " + servicio.getNombre() + " x" + cantidad);
        }
        return guardado;
    }

    public boolean eliminarConsumo(int idConsumo) {
        if (idConsumo <= 0) {
            throw new IllegalArgumentException("Seleccione un consumo válido.");
        }
        ConsumoServicio consumo = consumoRepository.buscarPorId(idConsumo);
        if (consumo == null) {
            return false;
        }
        Reserva reserva = obtenerReserva(consumo.getIdReserva());
        if (!reserva.permiteConsumos()) {
            throw new IllegalStateException("Solo se modifican servicios en reservas confirmadas o en curso.");
        }
        boolean eliminado = consumoRepository.eliminarYActualizarTotal(reserva, idConsumo);
        if (eliminado) {
            eventos.notificar(reserva, "Servicio retirado de la cuenta");
        }
        return eliminado;
    }

    public List<Reserva> listarReservas() { return reservaRepository.listar(); }
    public Reserva buscarReserva(int idReserva) { return reservaRepository.buscarPorId(idReserva); }
    public List<ConsumoServicio> listarConsumos(int idReserva) { return consumoRepository.listarPorReserva(idReserva); }
    public List<String> temporadas() { return tarifas.temporadasDisponibles(); }

    private Reserva obtenerReserva(int idReserva) {
        if (idReserva <= 0) {
            throw new IllegalArgumentException("Seleccione una reserva válida.");
        }
        Reserva reserva = reservaRepository.buscarPorId(idReserva);
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no existe.");
        }
        return reserva;
    }

    private boolean actualizarEstado(Reserva reserva, String evento) {
        boolean actualizado = reservaRepository.actualizarEstado(reserva);
        if (actualizado) {
            eventos.notificar(reserva, evento);
        }
        return actualizado;
    }
}
