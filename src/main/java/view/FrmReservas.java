package view;

import controller.SistemaController;
import model.Habitacion;
import model.Huesped;
import model.Reserva;
import util.ModeloTablaNoEditable;
import util.UIEstilos;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

@SuppressWarnings("this-escape")
public class FrmReservas extends JFrame {

    private static final long serialVersionUID = 1L;

    private transient final SistemaController controller;

    private final JComboBox<Huesped> cboHuesped = new JComboBox<>();
    private final JComboBox<Habitacion> cboHabitacion = new JComboBox<>();
    private final JTextField txtIngreso = new JTextField(LocalDate.now().toString());
    private final JTextField txtSalida = new JTextField(LocalDate.now().plusDays(1).toString());
    private final JComboBox<String> cboTemporada = new JComboBox<>();

    private final DefaultTableModel modelo = new ModeloTablaNoEditable();
    private final JTable tabla = new JTable(modelo);

    public FrmReservas(SistemaController controller) {
        this.controller = controller;
        setTitle("Gestión de Reservas");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIEstilos.COLOR_FONDO);

        // ------- Formulario de nueva reserva -------
        JPanel panelDatos = new JPanel(new GridLayout(3, 4, 8, 8));
        panelDatos.setBackground(UIEstilos.COLOR_FONDO);
        panelDatos.setBorder(new EmptyBorder(15, 20, 10, 20));
        UIEstilos.estilizarCampoTexto(txtIngreso);
        UIEstilos.estilizarCampoTexto(txtSalida);
        controller.temporadas().forEach(cboTemporada::addItem);
        cboTemporada.setSelectedItem("REGULAR");

        panelDatos.add(UIEstilos.crearEtiqueta("Huésped:"));
        panelDatos.add(cboHuesped);
        panelDatos.add(UIEstilos.crearEtiqueta("Habitación:"));
        panelDatos.add(cboHabitacion);
        panelDatos.add(UIEstilos.crearEtiqueta("Ingreso (AAAA-MM-DD):"));
        panelDatos.add(txtIngreso);
        panelDatos.add(UIEstilos.crearEtiqueta("Salida (AAAA-MM-DD):"));
        panelDatos.add(txtSalida);
        panelDatos.add(UIEstilos.crearEtiqueta("Temporada:"));
        panelDatos.add(cboTemporada);
        JButton btnRegistrar = new JButton("Registrar reserva");
        UIEstilos.estilizarBotonPrimario(btnRegistrar);
        btnRegistrar.addActionListener(e -> registrar());
        panelDatos.add(new javax.swing.JLabel(""));
        panelDatos.add(btnRegistrar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(UIEstilos.crearEncabezado("Gestión de Reservas"), BorderLayout.NORTH);
        panelSuperior.add(panelDatos, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        // ------- Tabla -------
        for (String columna : new String[]{"ID", "Huésped", "Habitación", "Ingreso", "Salida",
                "Noches", "Temporada", "Hospedaje (S/)", "Total (S/)", "Estado"}) {
            modelo.addColumn(columna);
        }
        UIEstilos.estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(0, 20, 0, 20));
        add(scroll, BorderLayout.CENTER);

        // ------- Acciones del ciclo de vida (patrón State) -------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        panelBotones.setBackground(UIEstilos.COLOR_FONDO);
        panelBotones.add(accion("Confirmar y pagar", true, controller::confirmarReserva));
        panelBotones.add(accion("Check-in", true, controller::checkIn));
        panelBotones.add(accion("Check-out", true, controller::checkOut));
        panelBotones.add(accion("Cancelar", false, controller::cancelarReserva));

        JButton btnServicios = new JButton("Servicios / consumos");
        UIEstilos.estilizarBotonSecundario(btnServicios);
        btnServicios.addActionListener(e -> abrirConsumos());
        panelBotones.add(btnServicios);

        JButton btnEliminar = new JButton("Eliminar");
        UIEstilos.estilizarBotonPeligro(btnEliminar);
        btnEliminar.addActionListener(e -> eliminar());
        panelBotones.add(btnEliminar);

        JButton btnRefrescar = new JButton("Actualizar lista");
        UIEstilos.estilizarBotonSecundario(btnRefrescar);
        btnRefrescar.addActionListener(e -> cargarDatos());
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);

        cargarDatos();
    }

    private void registrar() {
        Huesped huesped = (Huesped) cboHuesped.getSelectedItem();
        Habitacion habitacion = (Habitacion) cboHabitacion.getSelectedItem();
        if (huesped == null || habitacion == null) {
            JOptionPane.showMessageDialog(this, "Registre primero huéspedes y habitaciones.");
            return;
        }
        LocalDate ingreso;
        LocalDate salida;
        try {
            ingreso = LocalDate.parse(txtIngreso.getText().trim());
            salida = LocalDate.parse(txtSalida.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Las fechas deben tener el formato AAAA-MM-DD.");
            return;
        }
        boolean exito = controller.registrarReserva(huesped.getIdHuesped(), habitacion.getIdHabitacion(),
                ingreso, salida, (String) cboTemporada.getSelectedItem());
        JOptionPane.showMessageDialog(this, controller.getUltimoMensaje());
        if (exito) {
            cargarTabla();
        }
    }

    private JButton accion(String texto, boolean primario, IntPredicate operacion) {
        JButton boton = new JButton(texto);
        if (primario) {
            UIEstilos.estilizarBotonPrimario(boton);
        } else {
            UIEstilos.estilizarBotonSecundario(boton);
        }
        boton.addActionListener(e -> {
            int id = idSeleccionado();
            if (id > 0) {
                operacion.test(id);
                JOptionPane.showMessageDialog(this, controller.getUltimoMensaje());
                cargarTabla();
            }
        });
        return boton;
    }

    private void eliminar() {
        int id = idSeleccionado();
        if (id > 0 && JOptionPane.showConfirmDialog(this, "¿Eliminar la reserva #" + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            controller.eliminarReserva(id);
            JOptionPane.showMessageDialog(this, controller.getUltimoMensaje());
            cargarTabla();
        }
    }

    private void abrirConsumos() {
        int id = idSeleccionado();
        if (id > 0) {
            FrmConsumos ventana = new FrmConsumos(controller, id);
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    cargarTabla();
                }
            });
            ventana.setVisible(true);
        }
    }

    private int idSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva de la tabla.");
            return -1;
        }
        return Integer.parseInt(modelo.getValueAt(fila, 0).toString());
    }

    private void cargarDatos() {
        cboHuesped.removeAllItems();
        controller.listarHuespedes().forEach(cboHuesped::addItem);
        cboHabitacion.removeAllItems();
        controller.listarHabitaciones().forEach(cboHabitacion::addItem);
        cargarTabla();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        Map<Integer, String> huespedes = new HashMap<>();
        controller.listarHuespedes().forEach(h -> huespedes.put(h.getIdHuesped(), h.getNombre()));
        Map<Integer, String> habitaciones = new HashMap<>();
        controller.listarHabitaciones().forEach(h -> habitaciones.put(h.getIdHabitacion(), h.getNumero()));

        List<Reserva> reservas = controller.listarReservas();
        for (Reserva r : reservas) {
            modelo.addRow(new Object[]{
                    r.getIdReserva(),
                    huespedes.getOrDefault(r.getIdHuesped(), "#" + r.getIdHuesped()),
                    habitaciones.getOrDefault(r.getIdHabitacion(), "#" + r.getIdHabitacion()),
                    r.getFechaIngreso(),
                    r.getFechaSalida(),
                    r.calcularNoches(),
                    r.getTemporada(),
                    r.getCostoHospedaje().toPlainString(),
                    r.getTotal().toPlainString(),
                    r.getEstado()
            });
        }
    }
}
