package patterns.creational.factory;

import repository.HabitacionRepository;
import repository.HuespedRepository;
import repository.ReservaRepository;

/** PATRÓN CREACIONAL: FACTORY METHOD. Decide qué reporte concreto crear. */
public class ReporteFactory {
    private final HuespedRepository huespedRepository;
    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;

    public ReporteFactory(HuespedRepository huespedRepository,
                          HabitacionRepository habitacionRepository,
                          ReservaRepository reservaRepository) {
        this.huespedRepository = huespedRepository;
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
    }

    public Reporte crear(TipoReporte tipo) {
        return switch (tipo) {
            case HUESPED -> new ReporteHuesped(huespedRepository, habitacionRepository, reservaRepository);
            case OCUPACION_GENERAL -> new ReporteOcupacion(habitacionRepository, reservaRepository);
        };
    }
}
