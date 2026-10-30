'use strict';

// ======================================================================
//  Cliente de la API REST
// ======================================================================

const api = {
    async pedir(metodo, ruta, cuerpo) {
        const opciones = { method: metodo, headers: {}, credentials: 'same-origin' };
        if (cuerpo !== undefined) {
            opciones.headers['Content-Type'] = 'application/json';
            opciones.body = JSON.stringify(cuerpo);
        }
        let respuesta;
        try {
            respuesta = await fetch('/api' + ruta, opciones);
        } catch {
            throw new Error('No se pudo conectar con el servidor.');
        }
        const datos = await respuesta.json().catch(() => null);
        if (respuesta.status === 401 && !ruta.startsWith('/auth')) {
            mostrarLogin();
        }
        if (!respuesta.ok) {
            throw new Error(datos?.mensaje || 'Error ' + respuesta.status);
        }
        return datos;
    },
    get: (ruta) => api.pedir('GET', ruta),
    post: (ruta, cuerpo) => api.pedir('POST', ruta, cuerpo ?? {}),
    put: (ruta, cuerpo) => api.pedir('PUT', ruta, cuerpo),
    eliminar: (ruta) => api.pedir('DELETE', ruta),
};

// ======================================================================
//  Utilidades de interfaz
// ======================================================================

/** Crea un elemento; los textos se asignan con textContent (sin inyección de HTML). */
function el(etiqueta, atributos = {}, ...hijos) {
    const nodo = document.createElement(etiqueta);
    for (const [clave, valor] of Object.entries(atributos)) {
        if (valor === undefined || valor === null || valor === false) continue;
        if (clave === 'class') nodo.className = valor;
        else if (clave.startsWith('on')) nodo.addEventListener(clave.slice(2), valor);
        else nodo.setAttribute(clave, valor === true ? '' : valor);
    }
    for (const hijo of hijos.flat()) {
        if (hijo === null || hijo === undefined || hijo === false) continue;
        nodo.append(hijo instanceof Node ? hijo : document.createTextNode(String(hijo)));
    }
    return nodo;
}

const soles = (valor) => 'S/ ' + Number(valor ?? 0).toFixed(2);

let temporizadorToast;
function aviso(mensaje, tipo = 'exito') {
    const toast = document.getElementById('toast');
    toast.textContent = mensaje;
    toast.className = 'toast visible ' + tipo;
    clearTimeout(temporizadorToast);
    temporizadorToast = setTimeout(() => toast.classList.remove('visible'), 3500);
}

/** Ejecuta una acción de la API mostrando su mensaje y refrescando la vista. */
async function accion(promesa, alTerminar) {
    try {
        const r = await promesa;
        aviso(r?.mensaje || 'Operación realizada.');
        if (alTerminar) await alTerminar();
    } catch (e) {
        aviso(e.message, 'error');
    }
}

function tabla(columnas, filas, vacio = 'No hay registros.') {
    if (!filas.length) return el('div', { class: 'vacio' }, vacio);
    return el('div', { class: 'tabla-scroll' },
        el('table', {},
            el('thead', {}, el('tr', {}, columnas.map(c => el('th', { class: c.num ? 'num' : null }, c.titulo)))),
            el('tbody', {}, filas.map(f => el('tr', {}, columnas.map(c => {
                const valor = c.valor(f);
                return el('td', { class: c.num ? 'num' : c.acciones ? 'acciones-celda' : null }, valor);
            }))))
        )
    );
}

// ---------- Diálogo de formulario genérico ----------

/**
 * Abre un formulario modal. `campos`: [{nombre, etiqueta, tipo, opciones, paso, min, requerido}].
 * `guardar(valores)` debe devolver una promesa; si falla, el error se muestra en el diálogo.
 */
function abrirFormulario(titulo, campos, valoresIniciales, guardar) {
    const dialogo = document.getElementById('dialogo');
    const form = document.getElementById('form-dialogo');
    const contenedor = document.getElementById('dialogo-campos');
    const error = document.getElementById('dialogo-error');

    document.getElementById('dialogo-titulo').textContent = titulo;
    error.textContent = '';
    contenedor.replaceChildren(...campos.map(c => {
        const valor = valoresIniciales?.[c.nombre] ?? '';
        const control = c.tipo === 'select'
            ? el('select', { name: c.nombre, required: c.requerido !== false },
                c.opciones.map(o => {
                    const op = typeof o === 'object' ? o : { valor: o, texto: o };
                    return el('option', { value: op.valor, selected: String(op.valor) === String(valor) }, op.texto);
                }))
            : el('input', {
                name: c.nombre, type: c.tipo || 'text', value: valor, step: c.paso, min: c.min,
                required: c.requerido !== false,
            });
        return el('label', {}, c.etiqueta, control);
    }));

    form.onsubmit = async (evento) => {
        evento.preventDefault();
        const valores = {};
        for (const c of campos) {
            const crudo = form.elements[c.nombre].value.trim();
            valores[c.nombre] = c.tipo === 'number' && crudo !== '' ? Number(crudo) : crudo;
        }
        try {
            await guardar(valores);
            dialogo.close();
        } catch (e) {
            error.textContent = e.message;
        }
    };
    document.getElementById('dialogo-cancelar').onclick = () => dialogo.close();
    dialogo.showModal();
}

// ======================================================================
//  Catálogos (huéspedes, habitaciones, servicios)
// ======================================================================

const catalogos = {
    huespedes: {
        titulo: 'Huéspedes', singular: 'huésped', ruta: '/huespedes', id: 'idHuesped',
        campos: () => [
            { nombre: 'nombre', etiqueta: 'Nombre completo' },
            { nombre: 'documento', etiqueta: 'Documento (8–15 caracteres)' },
            { nombre: 'email', etiqueta: 'Correo', tipo: 'email' },
            { nombre: 'telefono', etiqueta: 'Teléfono', requerido: false },
        ],
        columnas: [
            { titulo: 'ID', valor: h => h.idHuesped },
            { titulo: 'Nombre', valor: h => h.nombre },
            { titulo: 'Documento', valor: h => h.documento },
            { titulo: 'Correo', valor: h => h.email },
            { titulo: 'Teléfono', valor: h => h.telefono },
        ],
    },
    habitaciones: {
        titulo: 'Habitaciones', singular: 'habitación', ruta: '/habitaciones', id: 'idHabitacion',
        campos: async () => [
            { nombre: 'numero', etiqueta: 'Número' },
            { nombre: 'tipo', etiqueta: 'Tipo', tipo: 'select', opciones: await api.get('/habitaciones/tipos') },
            { nombre: 'capacidad', etiqueta: 'Capacidad (personas)', tipo: 'number', min: 1 },
            { nombre: 'precioNoche', etiqueta: 'Precio por noche (S/)', tipo: 'number', paso: '0.01', min: 0 },
        ],
        columnas: [
            { titulo: 'ID', valor: h => h.idHabitacion },
            { titulo: 'Número', valor: h => h.numero },
            { titulo: 'Tipo', valor: h => h.tipo },
            { titulo: 'Capacidad', valor: h => h.capacidad, num: true },
            { titulo: 'Precio / noche', valor: h => soles(h.precioNoche), num: true },
        ],
    },
    servicios: {
        titulo: 'Servicios adicionales', singular: 'servicio', ruta: '/servicios', id: 'idServicio',
        campos: () => [
            { nombre: 'nombre', etiqueta: 'Nombre' },
            { nombre: 'precio', etiqueta: 'Precio (S/)', tipo: 'number', paso: '0.01', min: 0 },
        ],
        columnas: [
            { titulo: 'ID', valor: s => s.idServicio },
            { titulo: 'Nombre', valor: s => s.nombre },
            { titulo: 'Precio', valor: s => soles(s.precio), num: true },
        ],
    },
};

async function vistaCatalogo(clave) {
    const cfg = catalogos[clave];
    const contenido = document.getElementById('contenido');
    const recargar = () => vistaCatalogo(clave);

    const editar = async (registro) => {
        const campos = await cfg.campos();
        const nuevo = !registro;
        abrirFormulario((nuevo ? 'Registrar ' : 'Editar ') + cfg.singular, campos, registro, async (valores) => {
            const r = nuevo
                ? await api.post(cfg.ruta, valores)
                : await api.put(cfg.ruta + '/' + registro[cfg.id], valores);
            aviso(r.mensaje);
            await recargar();
        });
    };

    const registros = await api.get(cfg.ruta);
    const columnas = [...cfg.columnas, {
        titulo: 'Acciones', acciones: true, valor: r => [
            el('button', { class: 'btn chico', onclick: () => editar(r) }, 'Editar'),
            el('button', {
                class: 'btn chico peligro', onclick: () => {
                    if (confirm('¿Eliminar este ' + cfg.singular + '?')) {
                        accion(api.eliminar(cfg.ruta + '/' + r[cfg.id]), recargar);
                    }
                },
            }, 'Eliminar'),
        ],
    }];

    contenido.replaceChildren(
        el('div', { class: 'encabezado' },
            el('h2', {}, cfg.titulo),
            el('button', { class: 'btn primario', onclick: () => editar(null) }, '+ Nuevo ' + cfg.singular)),
        el('div', { class: 'tarjeta' }, tabla(columnas, registros))
    );
}

// ======================================================================
//  Reservas (ciclo de vida con el patrón State + consumos)
// ======================================================================

/** Acciones permitidas según el estado de la reserva (refleja las clases Estado*). */
const accionesPorEstado = {
    PENDIENTE: ['confirmar', 'cancelar', 'eliminar'],
    CONFIRMADA: ['check-in', 'cancelar', 'consumos'],
    EN_CURSO: ['check-out', 'consumos'],
    FINALIZADA: ['consumos'],
    CANCELADA: ['eliminar'],
};

let reservaSeleccionada = null;

async function vistaReservas() {
    const contenido = document.getElementById('contenido');
    const [reservas, huespedes, habitaciones, servicios, temporadas] = await Promise.all([
        api.get('/reservas'), api.get('/huespedes'), api.get('/habitaciones'),
        api.get('/servicios'), api.get('/reservas/temporadas'),
    ]);
    const nombreHuesped = id => huespedes.find(h => h.idHuesped === id)?.nombre ?? '#' + id;
    const numeroHabitacion = id => habitaciones.find(h => h.idHabitacion === id)?.numero ?? '#' + id;

    const nuevaReserva = () => {
        if (!huespedes.length || !habitaciones.length) {
            aviso('Primero registre al menos un huésped y una habitación.', 'error');
            return;
        }
        const hoy = new Date().toISOString().slice(0, 10);
        abrirFormulario('Nueva reserva', [
            { nombre: 'idHuesped', etiqueta: 'Huésped', tipo: 'select',
              opciones: huespedes.map(h => ({ valor: h.idHuesped, texto: h.nombre + ' (' + h.documento + ')' })) },
            { nombre: 'idHabitacion', etiqueta: 'Habitación', tipo: 'select',
              opciones: habitaciones.map(h => ({
                  valor: h.idHabitacion, texto: 'Hab. ' + h.numero + ' – ' + h.tipo + ' (' + soles(h.precioNoche) + ')',
              })) },
            { nombre: 'fechaIngreso', etiqueta: 'Fecha de ingreso', tipo: 'date' },
            { nombre: 'fechaSalida', etiqueta: 'Fecha de salida', tipo: 'date' },
            { nombre: 'temporada', etiqueta: 'Temporada', tipo: 'select', opciones: temporadas },
        ], { fechaIngreso: hoy, temporada: 'REGULAR' }, async (v) => {
            const r = await api.post('/reservas', {
                ...v, idHuesped: Number(v.idHuesped), idHabitacion: Number(v.idHabitacion),
            });
            aviso(r.mensaje);
            await vistaReservas();
        });
    };

    const botones = (r) => (accionesPorEstado[r.estado] || []).map(a => {
        const id = r.idReserva;
        switch (a) {
            case 'confirmar':
                return el('button', { class: 'btn chico', onclick: () => accion(api.post(`/reservas/${id}/confirmar`), vistaReservas) }, 'Confirmar y cobrar');
            case 'check-in':
                return el('button', { class: 'btn chico', onclick: () => accion(api.post(`/reservas/${id}/check-in`), vistaReservas) }, 'Check-in');
            case 'check-out':
                return el('button', { class: 'btn chico', onclick: () => accion(api.post(`/reservas/${id}/check-out`), vistaReservas) }, 'Check-out');
            case 'cancelar':
                return el('button', {
                    class: 'btn chico peligro',
                    onclick: () => confirm('¿Cancelar la reserva #' + id + '?') && accion(api.post(`/reservas/${id}/cancelar`), vistaReservas),
                }, 'Cancelar');
            case 'eliminar':
                return el('button', {
                    class: 'btn chico peligro',
                    onclick: () => confirm('¿Eliminar la reserva #' + id + '?') && accion(api.eliminar(`/reservas/${id}`), () => {
                        if (reservaSeleccionada === id) reservaSeleccionada = null;
                        return vistaReservas();
                    }),
                }, 'Eliminar');
            case 'consumos':
                return el('button', {
                    class: 'btn chico', onclick: () => { reservaSeleccionada = id; vistaReservas(); },
                }, 'Consumos');
        }
    });

    const columnas = [
        { titulo: '#', valor: r => r.idReserva },
        { titulo: 'Huésped', valor: r => nombreHuesped(r.idHuesped) },
        { titulo: 'Hab.', valor: r => numeroHabitacion(r.idHabitacion) },
        { titulo: 'Ingreso', valor: r => r.fechaIngreso },
        { titulo: 'Salida', valor: r => r.fechaSalida },
        { titulo: 'Temporada', valor: r => r.temporada },
        { titulo: 'Hospedaje', valor: r => soles(r.costoHospedaje), num: true },
        { titulo: 'Total', valor: r => soles(r.total), num: true },
        { titulo: 'Estado', valor: r => el('span', { class: 'estado ' + r.estado }, r.estado.replace('_', ' ')) },
        { titulo: 'Acciones', acciones: true, valor: botones },
    ];

    const reserva = reservas.find(r => r.idReserva === reservaSeleccionada);
    const panelConsumos = reserva ? await panelDeConsumos(reserva, servicios) : null;

    contenido.replaceChildren(
        el('div', { class: 'encabezado' },
            el('h2', {}, 'Reservas'),
            el('button', { class: 'btn primario', onclick: nuevaReserva }, '+ Nueva reserva')),
        el('div', { class: 'tarjeta' }, tabla(columnas, reservas, 'Aún no hay reservas registradas.')),
        panelConsumos
    );
}

async function panelDeConsumos(reserva, servicios) {
    const consumos = await api.get(`/reservas/${reserva.idReserva}/consumos`);
    const nombreServicio = id => servicios.find(s => s.idServicio === id)?.nombre ?? '#' + id;
    const editable = reserva.estado === 'CONFIRMADA' || reserva.estado === 'EN_CURSO';

    const columnas = [
        { titulo: 'Servicio', valor: c => nombreServicio(c.idServicio) },
        { titulo: 'Cantidad', valor: c => c.cantidad, num: true },
        { titulo: 'Subtotal', valor: c => soles(c.subtotal), num: true },
    ];
    if (editable) {
        columnas.push({
            titulo: '', acciones: true, valor: c => el('button', {
                class: 'btn chico peligro',
                onclick: () => accion(api.eliminar(`/consumos/${c.idConsumo}`), vistaReservas),
            }, 'Quitar'),
        });
    }

    let formulario = null;
    if (editable && servicios.length) {
        const selector = el('select', { name: 'idServicio' },
            servicios.map(s => el('option', { value: s.idServicio }, s.nombre + ' – ' + soles(s.precio))));
        const cantidad = el('input', { name: 'cantidad', type: 'number', min: 1, value: 1, required: true });
        formulario = el('form', {
            class: 'fila-form', onsubmit: (e) => {
                e.preventDefault();
                accion(api.post(`/reservas/${reserva.idReserva}/consumos`, {
                    idServicio: Number(selector.value), cantidad: Number(cantidad.value),
                }), vistaReservas);
            },
        },
            el('label', {}, 'Servicio', selector),
            el('label', {}, 'Cantidad', cantidad),
            el('button', { type: 'submit', class: 'btn primario' }, 'Agregar'));
    }

    return el('div', { class: 'tarjeta' },
        el('div', { class: 'tarjeta-cuerpo' },
            el('div', { class: 'encabezado' },
                el('h3', {}, `Consumos de la reserva #${reserva.idReserva} · Total ${soles(reserva.total)}`),
                el('button', { class: 'btn chico', onclick: () => { reservaSeleccionada = null; vistaReservas(); } }, 'Cerrar')),
            formulario),
        tabla(columnas, consumos, 'Sin consumos registrados.'));
}

// ======================================================================
//  Reportes (Factory Method)
// ======================================================================

async function vistaReportes() {
    const contenido = document.getElementById('contenido');
    const huespedes = await api.get('/huespedes');
    const salida = el('pre', { class: 'reporte' }, 'Seleccione un reporte para generarlo.');

    const generar = async (ruta) => {
        salida.textContent = 'Generando…';
        try {
            salida.textContent = (await api.get(ruta)).mensaje;
        } catch (e) {
            salida.textContent = 'No se pudo generar el reporte: ' + e.message;
        }
    };

    const selector = el('select', {},
        huespedes.map(h => el('option', { value: h.idHuesped }, h.nombre + ' (' + h.documento + ')')));

    contenido.replaceChildren(
        el('div', { class: 'encabezado' }, el('h2', {}, 'Reportes')),
        el('div', { class: 'tarjeta' }, el('div', { class: 'tarjeta-cuerpo' },
            el('div', { class: 'fila-form' },
                el('label', {}, 'Historial de huésped', selector),
                el('button', {
                    class: 'btn', disabled: !huespedes.length,
                    onclick: () => generar('/reportes/huesped/' + selector.value),
                }, 'Generar reporte de huésped'),
                el('button', { class: 'btn primario', onclick: () => generar('/reportes/ocupacion') },
                    'Reporte de ocupación general')))),
        el('div', { class: 'tarjeta' }, el('div', { class: 'tarjeta-cuerpo' }, salida))
    );
}

// ======================================================================
//  Navegación y sesión
// ======================================================================

const vistas = {
    reservas: vistaReservas,
    huespedes: () => vistaCatalogo('huespedes'),
    habitaciones: () => vistaCatalogo('habitaciones'),
    servicios: () => vistaCatalogo('servicios'),
    reportes: vistaReportes,
};

async function irA(seccion) {
    document.querySelectorAll('#menu button').forEach(b =>
        b.classList.toggle('activo', b.dataset.seccion === seccion));
    try {
        await vistas[seccion]();
    } catch (e) {
        document.getElementById('contenido').replaceChildren(
            el('div', { class: 'tarjeta' }, el('div', { class: 'vacio' }, e.message)));
    }
}

function mostrarLogin() {
    document.getElementById('vista-app').hidden = true;
    document.getElementById('vista-login').hidden = false;
    document.querySelector('#form-login input[name=usuario]').focus();
}

function mostrarApp(usuario) {
    document.getElementById('nombre-usuario').textContent = usuario;
    document.getElementById('vista-login').hidden = true;
    document.getElementById('vista-app').hidden = false;
    irA('reservas');
}

document.getElementById('form-login').addEventListener('submit', async (e) => {
    e.preventDefault();
    const form = e.target;
    const error = document.getElementById('login-error');
    error.textContent = '';
    try {
        await api.post('/auth/login', { usuario: form.usuario.value, contrasena: form.contrasena.value });
        form.contrasena.value = '';
        mostrarApp(form.usuario.value.trim());
    } catch (err) {
        error.textContent = err.message;
    }
});

document.getElementById('btn-salir').addEventListener('click', async () => {
    await api.post('/auth/logout').catch(() => {});
    reservaSeleccionada = null;
    mostrarLogin();
});

document.getElementById('menu').addEventListener('click', (e) => {
    const seccion = e.target.closest('button')?.dataset.seccion;
    if (seccion) irA(seccion);
});

// Al cargar: si ya hay sesión, entra directo.
api.get('/auth/sesion')
    .then(r => mostrarApp(r.mensaje))
    .catch(() => mostrarLogin());
