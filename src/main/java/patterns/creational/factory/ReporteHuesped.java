package patterns.creational.factory;

import model.Habitacion;
import model.Huesped;
import model.Reserva;
import repository.HabitacionRepository;
import repository.HuespedRepository;
import repository.ReservaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/** Historial de estadías y gasto acumulado de un huésped. */
public class ReporteHuesped implements Reporte {

    private static final Set<String> ESTADOS_COBRADOS = Set.of("CONFIRMADA", "EN_CURSO", "FINALIZADA");

    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;

    public ReporteHuesped(HuespedRepository huespedRepository,
                          HabitacionRepository habitacionRepository,
                          ReservaRepository reservaRepository) {
        this.huespedRepository = huespedRepository;
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public String generar(Integer idHuesped) {
        if (idHuesped == null || idHuesped <= 0) {
            throw new IllegalArgumentException("Seleccione un huésped válido.");
        }
        Huesped huesped = huespedRepository.buscarPorId(idHuesped);
        if (huesped == null) {
            throw new IllegalArgumentException("El huésped no existe.");
        }

        List<Reserva> reservas = reservaRepository.listarPorHuesped(idHuesped);
        StringBuilder sb = new StringBuilder();
        sb.append("===== REPORTE DE HUÉSPED =====\n");
        sb.append("Nombre: ").append(huesped.getNombre()).append('\n');
        sb.append("Documento: ").append(huesped.getDocumento()).append('\n');
        sb.append("Correo: ").append(huesped.getEmail()).append('\n');
        sb.append("Teléfono: ").append(huesped.getTelefono()).append("\n\n");
        sb.append("Reservas registradas: ").append(reservas.size()).append('\n');

        BigDecimal gastado = BigDecimal.ZERO;
        long noches = 0;
        for (Reserva r : reservas) {
            Habitacion hab = habitacionRepository.buscarPorId(r.getIdHabitacion());
            sb.append(String.format("  #%d | Hab. %s | %s al %s | %s | S/ %s%n",
                    r.getIdReserva(), hab == null ? "?" : hab.getNumero(),
                    r.getFechaIngreso(), r.getFechaSalida(), r.getEstado(), r.getTotal().toPlainString()));
            if (ESTADOS_COBRADOS.contains(r.getEstado())) {
                gastado = gastado.add(r.getTotal());
                noches += r.calcularNoches();
            }
        }
        sb.append("\nNoches hospedadas/confirmadas: ").append(noches).append('\n');
        sb.append("Total gastado: S/ ").append(gastado.setScale(2).toPlainString()).append('\n');
        sb.append(noches >= 5 ? "Categoría: HUÉSPED FRECUENTE\n" : "Categoría: HUÉSPED REGULAR\n");
        return sb.toString();
    }
}
