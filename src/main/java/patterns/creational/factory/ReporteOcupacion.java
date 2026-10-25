package patterns.creational.factory;

import model.Habitacion;
import model.Reserva;
import repository.HabitacionRepository;
import repository.ReservaRepository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Resumen general de ocupación e ingresos del hotel. */
public class ReporteOcupacion implements Reporte {

    private static final Set<String> ESTADOS_COBRADOS = Set.of("CONFIRMADA", "EN_CURSO", "FINALIZADA");

    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;

    public ReporteOcupacion(HabitacionRepository habitacionRepository, ReservaRepository reservaRepository) {
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public String generar(Integer idReferencia) {
        List<Habitacion> habitaciones = habitacionRepository.listar();
        List<Reserva> reservas = reservaRepository.listar();

        Map<String, Integer> porEstado = new LinkedHashMap<>();
        for (String estado : List.of("PENDIENTE", "CONFIRMADA", "EN_CURSO", "FINALIZADA", "CANCELADA")) {
            porEstado.put(estado, 0);
        }
        Map<Integer, Integer> porHabitacion = new LinkedHashMap<>();
        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal hospedaje = BigDecimal.ZERO;
        long noches = 0;

        for (Reserva r : reservas) {
            porEstado.merge(r.getEstado(), 1, Integer::sum);
            if (ESTADOS_COBRADOS.contains(r.getEstado())) {
                ingresos = ingresos.add(r.getTotal());
                hospedaje = hospedaje.add(r.getCostoHospedaje());
                noches += r.calcularNoches();
                porHabitacion.merge(r.getIdHabitacion(), 1, Integer::sum);
            }
        }

        long ocupadas = reservas.stream().filter(r -> "EN_CURSO".equals(r.getEstado())).count();

        StringBuilder sb = new StringBuilder();
        sb.append("===== REPORTE GENERAL DE OCUPACIÓN =====\n");
        sb.append("Habitaciones registradas: ").append(habitaciones.size()).append('\n');
        sb.append("Habitaciones ocupadas ahora: ").append(ocupadas).append('\n');
        sb.append("Reservas registradas: ").append(reservas.size()).append("\n\n");
        sb.append("Reservas por estado:\n");
        porEstado.forEach((estado, cantidad) -> sb.append("  ").append(estado).append(": ").append(cantidad).append('\n'));

        sb.append("\nReservas cobradas por habitación:\n");
        for (Habitacion h : habitaciones) {
            sb.append(String.format("  Hab. %s (%s): %d%n", h.getNumero(), h.getTipo(),
                    porHabitacion.getOrDefault(h.getIdHabitacion(), 0)));
        }

        sb.append("\nNoches vendidas: ").append(noches).append('\n');
        sb.append("Ingresos por hospedaje: S/ ").append(hospedaje.setScale(2).toPlainString()).append('\n');
        sb.append("Ingresos por servicios: S/ ").append(ingresos.subtract(hospedaje).setScale(2).toPlainString()).append('\n');
        sb.append("INGRESOS TOTALES: S/ ").append(ingresos.setScale(2).toPlainString()).append('\n');
        return sb.toString();
    }
}
