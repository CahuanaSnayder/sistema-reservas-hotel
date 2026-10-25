import config.ConexionPostgres;
import model.ConsumoServicio;
import model.Habitacion;
import model.Huesped;
import model.Reserva;
import model.Servicio;
import patterns.behavioral.observer.ReservaEventManager;
import patterns.behavioral.strategy.TarifaSelector;
import patterns.behavioral.strategy.TarifaTemporadaAlta;
import patterns.behavioral.strategy.TarifaTemporadaBaja;
import patterns.creational.builder.ReservaBuilder;
import patterns.creational.factory.ReporteFactory;
import patterns.creational.factory.ReporteHuesped;
import patterns.creational.factory.ReporteOcupacion;
import patterns.creational.factory.TipoReporte;
import patterns.structural.adapter.PagoExternoAdapter;
import patterns.structural.adapter.ServicioPagoExterno;
import patterns.structural.facade.ReservaFacade;
import repository.ConsumoRepository;
import repository.HabitacionRepository;
import repository.HuespedRepository;
import repository.ReservaRepository;
import repository.ServicioRepository;
import util.PasswordUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pruebas sin dependencias externas (repositorios en memoria) para validar la lógica principal. */
public class PruebasLogica {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 1);

    public static void main(String[] args) {
        probarSingleton();
        probarStrategy();
        probarBuilder();
        probarAdapter();
        probarEstados();
        probarObserver();
        probarFlujoCompleto();
        probarCruceDeFechas();
        probarReportes();
        probarContrasenas();
        System.out.println("PRUEBAS OK: Singleton, Builder, Factory Method, Adapter, Facade, "
                + "Observer, State, Strategy, GRASP y validaciones.");
    }

    private static void probarSingleton() {
        verificar(ConexionPostgres.getInstancia() == ConexionPostgres.getInstancia(),
                "Singleton debe devolver siempre la misma instancia.");
    }

    private static void probarStrategy() {
        BigDecimal precio = new BigDecimal("200.00");
        verificar(new TarifaTemporadaBaja().calcular(precio, 2).compareTo(new BigDecimal("340.00")) == 0,
                "Temporada baja debe aplicar 15 % de descuento.");
        verificar(new TarifaTemporadaAlta().calcular(precio, 2).compareTo(new BigDecimal("500.00")) == 0,
                "Temporada alta debe aplicar 25 % de recargo.");
        verificar(new TarifaSelector().seleccionar("regular").getTemporada().equals("REGULAR"),
                "El selector debe encontrar la estrategia regular.");
    }

    private static void probarBuilder() {
        Reserva reserva = new ReservaBuilder().paraHuesped(1).enHabitacion(1)
                .desde(HOY).hasta(HOY.plusDays(3)).construir();
        verificar(reserva.calcularNoches() == 3, "La reserva debe calcular 3 noches.");
        verificar("PENDIENTE".equals(reserva.getEstado()), "Una reserva nueva debe estar pendiente.");
        esperarError(() -> new ReservaBuilder().paraHuesped(1).enHabitacion(1)
                .desde(HOY).hasta(HOY).construir(), "El Builder debe rechazar fechas inválidas.");
    }

    private static void probarAdapter() {
        long[] centimos = {0};
        ServicioPagoExterno externo = new ServicioPagoExterno() {
            @Override
            public String cobrar(String referencia, long montoCentimos, String moneda) {
                centimos[0] = montoCentimos;
                return "TXN-PRUEBA";
            }
        };
        Reserva reserva = new Reserva();
        reserva.setIdReserva(7);
        String codigo = new PagoExternoAdapter(externo).procesarPago(reserva, new BigDecimal("150.50"), "Hospedaje");
        verificar("TXN-PRUEBA".equals(codigo) && centimos[0] == 15050,
                "El Adapter debe convertir soles a céntimos para el proveedor externo.");
    }

    private static void probarEstados() {
        Reserva reserva = new Reserva();
        esperarError(reserva::checkIn, "No se debe hacer check-in sin confirmar.");
        reserva.confirmar();
        verificar(reserva.permiteConsumos(), "Una reserva confirmada admite consumos.");
        reserva.checkIn();
        esperarError(reserva::cancelar, "No se debe cancelar una estadía en curso.");
        reserva.checkOut();
        verificar("FINALIZADA".equals(reserva.getEstado()), "Tras el check-out la reserva queda finalizada.");
        verificar(!reserva.permiteConsumos(), "Una reserva finalizada no admite consumos.");
    }

    private static void probarObserver() {
        ReservaEventManager eventos = new ReservaEventManager();
        int[] llamadas = {0};
        eventos.suscribir((r, e) -> llamadas[0]++);
        eventos.suscribir((r, e) -> { throw new IllegalStateException("fallo simulado"); });
        eventos.suscribir((r, e) -> llamadas[0]++);
        eventos.notificar(new Reserva(), "evento");
        verificar(llamadas[0] == 2, "Un observador con error no debe impedir que los demás se ejecuten.");
    }

    private static void probarFlujoCompleto() {
        Memoria m = new Memoria();
        List<String> pagos = new ArrayList<>();
        ReservaFacade facade = m.facade((reserva, monto, concepto) -> {
            pagos.add(concepto + ":" + monto.toPlainString());
            return "TXN-" + pagos.size();
        });

        Reserva reserva = facade.registrarReserva(1, 1, HOY, HOY.plusDays(2), "ALTA");
        verificar(reserva.getCostoHospedaje().compareTo(new BigDecimal("450.00")) == 0,
                "2 noches x S/ 180 en temporada alta = S/ 450.00");

        esperarError(() -> facade.agregarConsumo(reserva.getIdReserva(), 1, 1),
                "No se deben agregar servicios a una reserva pendiente.");

        facade.confirmarReserva(reserva.getIdReserva());
        facade.registrarCheckIn(reserva.getIdReserva());
        facade.agregarConsumo(reserva.getIdReserva(), 1, 2);
        Reserva actual = facade.buscarReserva(reserva.getIdReserva());
        verificar(actual.getTotal().compareTo(new BigDecimal("520.00")) == 0,
                "Total = 450 de hospedaje + 2 x 35 de desayuno = 520.00");

        facade.registrarCheckOut(reserva.getIdReserva());
        verificar("FINALIZADA".equals(facade.buscarReserva(reserva.getIdReserva()).getEstado()),
                "La reserva debe quedar finalizada.");
        verificar(pagos.equals(List.of("Hospedaje:450.00", "Servicios adicionales:70.00")),
                "Se cobra el hospedaje al confirmar y los servicios al hacer check-out.");
    }

    private static void probarCruceDeFechas() {
        Memoria m = new Memoria();
        ReservaFacade facade = m.facade((r, monto, c) -> "TXN");
        facade.registrarReserva(1, 1, HOY, HOY.plusDays(3), "REGULAR");
        esperarError(() -> facade.registrarReserva(1, 1, HOY.plusDays(2), HOY.plusDays(4), "REGULAR"),
                "No se debe reservar una habitación ocupada en esas fechas.");
        facade.registrarReserva(1, 1, HOY.plusDays(3), HOY.plusDays(5), "REGULAR");
    }

    private static void probarReportes() {
        Memoria m = new Memoria();
        ReporteFactory factory = new ReporteFactory(m.huespedes, m.habitaciones, m.reservas);
        verificar(factory.crear(TipoReporte.HUESPED) instanceof ReporteHuesped,
                "Factory debe crear el reporte de huésped.");
        verificar(factory.crear(TipoReporte.OCUPACION_GENERAL) instanceof ReporteOcupacion,
                "Factory debe crear el reporte de ocupación.");
        verificar(factory.crear(TipoReporte.OCUPACION_GENERAL).generar(null).contains("INGRESOS TOTALES"),
                "El reporte general debe mostrar los ingresos.");
    }

    private static void probarContrasenas() {
        String hash = PasswordUtil.hash("admin");
        verificar(PasswordUtil.esHash(hash) && PasswordUtil.verificar("admin", hash),
                "La contraseña debe verificarse contra su hash PBKDF2.");
        verificar(!PasswordUtil.verificar("otra", hash), "Una contraseña incorrecta no debe validar.");
    }

    // ---------- Utilidades ----------

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
        System.out.println("  ✔ " + mensaje);
    }

    private static void esperarError(Runnable accion, String mensaje) {
        try {
            accion.run();
        } catch (RuntimeException esperado) {
            System.out.println("  ✔ " + mensaje);
            return;
        }
        throw new AssertionError(mensaje);
    }

    /** Repositorios en memoria para probar sin PostgreSQL. */
    private static final class Memoria {
        final Map<Integer, Huesped> datosHuespedes = new LinkedHashMap<>();
        final Map<Integer, Habitacion> datosHabitaciones = new LinkedHashMap<>();
        final Map<Integer, Servicio> datosServicios = new LinkedHashMap<>();
        final Map<Integer, Reserva> datosReservas = new LinkedHashMap<>();
        final Map<Integer, ConsumoServicio> datosConsumos = new LinkedHashMap<>();

        Memoria() {
            datosHuespedes.put(1, new Huesped(1, "Rosa Quispe", "45879632", "rosa@correo.com", "956321478"));
            datosHabitaciones.put(1, new Habitacion(1, "201", "DOBLE", 2, new BigDecimal("180.00")));
            datosServicios.put(1, new Servicio(1, "Desayuno buffet", new BigDecimal("35.00")));
        }

        final HuespedRepository huespedes = new HuespedRepository() {
            public boolean registrar(Huesped h) { datosHuespedes.put(h.getIdHuesped(), h); return true; }
            public List<Huesped> listar() { return new ArrayList<>(datosHuespedes.values()); }
            public Huesped buscarPorId(int id) { return datosHuespedes.get(id); }
            public boolean actualizar(Huesped h) { return datosHuespedes.put(h.getIdHuesped(), h) != null; }
            public boolean eliminar(int id) { return datosHuespedes.remove(id) != null; }
        };

        final HabitacionRepository habitaciones = new HabitacionRepository() {
            public boolean registrar(Habitacion h) { datosHabitaciones.put(h.getIdHabitacion(), h); return true; }
            public List<Habitacion> listar() { return new ArrayList<>(datosHabitaciones.values()); }
            public Habitacion buscarPorId(int id) { return datosHabitaciones.get(id); }
            public boolean actualizar(Habitacion h) { return datosHabitaciones.put(h.getIdHabitacion(), h) != null; }
            public boolean eliminar(int id) { return datosHabitaciones.remove(id) != null; }
        };

        final ServicioRepository servicios = new ServicioRepository() {
            public boolean registrar(Servicio s) { datosServicios.put(s.getIdServicio(), s); return true; }
            public List<Servicio> listar() { return new ArrayList<>(datosServicios.values()); }
            public Servicio buscarPorId(int id) { return datosServicios.get(id); }
            public boolean actualizar(Servicio s) { return datosServicios.put(s.getIdServicio(), s) != null; }
            public boolean eliminar(int id) { return datosServicios.remove(id) != null; }
        };

        final ReservaRepository reservas = new ReservaRepository() {
            public boolean registrar(Reserva r) {
                r.setIdReserva(datosReservas.size() + 1);
                datosReservas.put(r.getIdReserva(), r);
                return true;
            }
            public List<Reserva> listar() { return new ArrayList<>(datosReservas.values()); }
            public List<Reserva> listarPorHuesped(int id) {
                return datosReservas.values().stream().filter(r -> r.getIdHuesped() == id).toList();
            }
            public Reserva buscarPorId(int id) { return datosReservas.get(id); }
            public boolean actualizarEstado(Reserva r) { return datosReservas.put(r.getIdReserva(), r) != null; }
            public boolean eliminar(int id) { return datosReservas.remove(id) != null; }
            public boolean existeCruce(int idHab, LocalDate ingreso, LocalDate salida) {
                return datosReservas.values().stream().anyMatch(r -> r.getIdHabitacion() == idHab
                        && List.of("PENDIENTE", "CONFIRMADA", "EN_CURSO").contains(r.getEstado())
                        && r.getFechaIngreso().isBefore(salida) && r.getFechaSalida().isAfter(ingreso));
            }
        };

        final ConsumoRepository consumos = new ConsumoRepository() {
            public List<ConsumoServicio> listarPorReserva(int id) {
                return datosConsumos.values().stream().filter(c -> c.getIdReserva() == id).toList();
            }
            public ConsumoServicio buscarPorId(int id) { return datosConsumos.get(id); }
            public boolean registrarYActualizarTotal(Reserva r, ConsumoServicio c) {
                c.setIdConsumo(datosConsumos.size() + 1);
                datosConsumos.put(c.getIdConsumo(), c);
                r.calcularTotal(listarPorReserva(r.getIdReserva()));
                datosReservas.put(r.getIdReserva(), r);
                return true;
            }
            public boolean eliminarYActualizarTotal(Reserva r, int idConsumo) {
                datosConsumos.remove(idConsumo);
                r.calcularTotal(listarPorReserva(r.getIdReserva()));
                datosReservas.put(r.getIdReserva(), r);
                return true;
            }
        };

        ReservaFacade facade(patterns.structural.adapter.PasarelaPago pasarela) {
            return new ReservaFacade(huespedes, habitaciones, servicios, reservas, consumos,
                    new TarifaSelector(), pasarela, new ReservaEventManager());
        }
    }
}
