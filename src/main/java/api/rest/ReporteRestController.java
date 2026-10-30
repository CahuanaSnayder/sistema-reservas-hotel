package api.rest;

import api.rest.Solicitudes.Mensaje;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import patterns.creational.factory.ReporteFactory;
import patterns.creational.factory.TipoReporte;

/** Los reportes se crean con el Factory Method; la API solo indica el tipo. */
@RestController
@RequestMapping("/api/reportes")
public class ReporteRestController {

    private final ReporteFactory factory;

    public ReporteRestController(ReporteFactory factory) {
        this.factory = factory;
    }

    @GetMapping("/huesped/{idHuesped}")
    public Mensaje huesped(@PathVariable int idHuesped) {
        return new Mensaje(factory.crear(TipoReporte.HUESPED).generar(idHuesped));
    }

    @GetMapping("/ocupacion")
    public Mensaje ocupacion() {
        return new Mensaje(factory.crear(TipoReporte.OCUPACION_GENERAL).generar(null));
    }
}
