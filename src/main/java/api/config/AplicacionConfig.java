package api.config;

import dao.ConsumoDAO;
import dao.HabitacionDAO;
import dao.HuespedDAO;
import dao.ReservaDAO;
import dao.ServicioDAO;
import dao.UsuarioDAO;
import patterns.behavioral.observer.AuditoriaReservaObserver;
import patterns.behavioral.observer.NotificacionHuespedObserver;
import patterns.behavioral.observer.ReservaEventManager;
import patterns.behavioral.strategy.TarifaSelector;
import patterns.creational.factory.ReporteFactory;
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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition Root de la versión web (equivalente al constructor de SistemaController):
 * conecta los DAO concretos con las abstracciones y reutiliza la misma lógica de negocio.
 */
@Configuration
public class AplicacionConfig {

    @Bean UsuarioRepository usuarioRepository() { return new UsuarioDAO(); }
    @Bean HuespedRepository huespedRepository() { return new HuespedDAO(); }
    @Bean HabitacionRepository habitacionRepository() { return new HabitacionDAO(); }
    @Bean ServicioRepository servicioRepository() { return new ServicioDAO(); }
    @Bean ReservaRepository reservaRepository() { return new ReservaDAO(); }
    @Bean ConsumoRepository consumoRepository() { return new ConsumoDAO(); }

    @Bean
    ReservaEventManager reservaEventManager(HuespedRepository huespedRepository) {
        ReservaEventManager eventos = new ReservaEventManager();
        eventos.suscribir(new AuditoriaReservaObserver());
        eventos.suscribir(new NotificacionHuespedObserver(huespedRepository));
        return eventos;
    }

    @Bean
    ReservaFacade reservaFacade(HuespedRepository huespedRepository,
                                HabitacionRepository habitacionRepository,
                                ServicioRepository servicioRepository,
                                ReservaRepository reservaRepository,
                                ConsumoRepository consumoRepository,
                                ReservaEventManager eventos) {
        return new ReservaFacade(
                huespedRepository,
                habitacionRepository,
                servicioRepository,
                reservaRepository,
                consumoRepository,
                new TarifaSelector(),
                new PagoExternoAdapter(new ServicioPagoExterno()),
                eventos
        );
    }

    @Bean AuthService authService(UsuarioRepository r) { return new AuthService(r); }
    @Bean HuespedService huespedService(HuespedRepository r) { return new HuespedService(r); }
    @Bean HabitacionService habitacionService(HabitacionRepository r) { return new HabitacionService(r); }
    @Bean ServicioHotelService servicioHotelService(ServicioRepository r) { return new ServicioHotelService(r); }

    @Bean
    ReporteFactory reporteFactory(HuespedRepository huespedRepository,
                                  HabitacionRepository habitacionRepository,
                                  ReservaRepository reservaRepository) {
        return new ReporteFactory(huespedRepository, habitacionRepository, reservaRepository);
    }
}
