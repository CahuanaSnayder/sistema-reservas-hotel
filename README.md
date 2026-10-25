# Sistema de Reservas de Hotel 🏨

Proyecto final del curso **Diseño de Patrones**. Aplicación de escritorio en **Java 17 + Swing** con persistencia en **PostgreSQL**, que gestiona huéspedes, habitaciones, servicios adicionales y el ciclo de vida completo de una reserva (registro → pago → check-in → consumos → check-out).

## 🎥 Video de presentación

[![Ver el video de presentación en YouTube](https://img.youtube.com/vi/_LJDWIChMV0/hqdefault.jpg)](https://youtu.be/_LJDWIChMV0)

▶️ **[Ver en YouTube](https://youtu.be/_LJDWIChMV0)**

## Patrones aplicados

| Tipo | Patrón | Clase(s) | Uso en el sistema |
|---|---|---|---|
| Creacional | **Singleton** | `config.ConexionPostgres` | Única configuración de conexión a PostgreSQL |
| Creacional | **Builder** | `ReservaBuilder` | Construye y valida una reserva paso a paso |
| Creacional | **Factory Method** | `ReporteFactory`, `ReporteHuesped`, `ReporteOcupacion` | Crea el reporte solicitado sin que la vista conozca la clase concreta |
| Estructural | **Adapter** | `PasarelaPago`, `PagoExternoAdapter`, `ServicioPagoExterno` | Adapta la pasarela de pagos externa (céntimos) a la interfaz del sistema (soles) |
| Estructural | **Facade** | `ReservaFacade` | API simple para todo el proceso de reserva |
| Comportamiento | **Observer** | `ReservaEventManager`, `AuditoriaReservaObserver`, `NotificacionHuespedObserver` | Auditoría y notificación al huésped en cada evento |
| Comportamiento | **State** | `EstadoReserva` + 5 estados | Pendiente → Confirmada → En curso → Finalizada / Cancelada |
| Comportamiento | **Strategy** | `TarifaStrategy`, `TarifaTemporadaBaja/Regular/Alta` | Cálculo del hospedaje según temporada (−15 %, 0 %, +25 %) |

También se aplican los principios **SOLID** (repositorios como interfaces + inyección por constructor) y **GRASP** (Information Expert, Creator, Controller).

## Arquitectura

```
view (Swing) → controller → service / ReservaFacade → repository (interfaces) → dao (JDBC) → PostgreSQL
                                     ↘ patterns (builder, factory, adapter, observer, state, strategy)
```

## Cómo ejecutar

1. Crear la base de datos en PostgreSQL:
   ```sql
   CREATE DATABASE sistema_reservas_hotel;
   ```
2. Ejecutar el script `database/sistema_reservas_hotel.sql` sobre esa base.
3. Revisar las credenciales en `config/ConexionPostgres.java` (por defecto `postgres` / `admin`) o definirlas con `-Ddb.url`, `-Ddb.user`, `-Ddb.password`.
4. Abrir el proyecto en IntelliJ IDEA / NetBeans como proyecto Maven y ejecutar `Main`.
5. Iniciar sesión con **admin / admin** (la contraseña se convierte a hash PBKDF2 en el primer ingreso).

## Pruebas

`src/test/java/PruebasLogica.java` valida los 8 patrones, los cálculos de tarifa y el flujo completo de una reserva usando repositorios en memoria (no requiere PostgreSQL). Se ejecuta como clase `main`.
