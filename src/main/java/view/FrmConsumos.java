package view;

import controller.SistemaController;
import model.ConsumoServicio;
import model.Reserva;
import model.Servicio;
import util.ModeloTablaNoEditable;
import util.UIEstilos;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

/** Cuenta de servicios adicionales consumidos durante una reserva. */
@SuppressWarnings("this-escape")
public class FrmConsumos extends JFrame {

    private static final long serialVersionUID = 1L;

    private transient final SistemaController controller;
    private final int idReserva;

    private final JComboBox<Servicio> cboServicio = new JComboBox<>();
    private final JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    private final JLabel lblResumen = new JLabel();
    private final DefaultTableModel modelo = new ModeloTablaNoEditable();
    private final JTable tabla = new JTable(modelo);

    public FrmConsumos(SistemaController controller, int idReserva) {
        this.controller = controller;
        this.idReserva = idReserva;
        setTitle("Servicios de la reserva #" + idReserva);
        setSize(680, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIEstilos.COLOR_FONDO);

        controller.listarServicios().forEach(cboServicio::addItem);

        JPanel panelAlta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        panelAlta.setBackground(UIEstilos.COLOR_FONDO);
        panelAlta.add(UIEstilos.crearEtiqueta("Servicio:"));
        panelAlta.add(cboServicio);
        panelAlta.add(UIEstilos.crearEtiqueta("Cantidad:"));
        panelAlta.add(spnCantidad);
        JButton btnAgregar = new JButton("Agregar");
        UIEstilos.estilizarBotonPrimario(btnAgregar);
        btnAgregar.addActionListener(e -> agregar());
        panelAlta.add(btnAgregar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(UIEstilos.crearEncabezado("Servicios de la reserva #" + idReserva), BorderLayout.NORTH);
        panelSuperior.add(panelAlta, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        for (String columna : new String[]{"ID", "Servicio", "Cantidad", "Subtotal (S/)"}) {
            modelo.addColumn(columna);
        }
        UIEstilos.estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(0, 20, 0, 20));
        add(scroll, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(UIEstilos.COLOR_FONDO);
        panelInferior.setBorder(new EmptyBorder(10, 20, 12, 20));
        lblResumen.setFont(UIEstilos.FUENTE_LABEL_NEGRITA);
        panelInferior.add(lblResumen, BorderLayout.CENTER);
        JButton btnQuitar = new JButton("Quitar servicio");
        UIEstilos.estilizarBotonPeligro(btnQuitar);
        btnQuitar.addActionListener(e -> quitar());
        panelInferior.add(btnQuitar, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);

        cargarTabla();
    }

    private void agregar() {
        Servicio servicio = (Servicio) cboServicio.getSelectedItem();
        if (servicio == null) {
            JOptionPane.showMessageDialog(this, "Registre primero servicios en el catálogo.");
            return;
        }
        controller.agregarConsumo(idReserva, servicio.getIdServicio(), (Integer) spnCantidad.getValue());
        JOptionPane.showMessageDialog(this, controller.getUltimoMensaje());
        cargarTabla();
    }

    private void quitar() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio de la tabla.");
            return;
        }
        controller.eliminarConsumo(Integer.parseInt(modelo.getValueAt(fila, 0).toString()));
        JOptionPane.showMessageDialog(this, controller.getUltimoMensaje());
        cargarTabla();
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        Map<Integer, String> nombres = new HashMap<>();
        controller.listarServicios().forEach(s -> nombres.put(s.getIdServicio(), s.getNombre()));
        for (ConsumoServicio c : controller.listarConsumos(idReserva)) {
            modelo.addRow(new Object[]{c.getIdConsumo(), nombres.getOrDefault(c.getIdServicio(), "?"),
                    c.getCantidad(), c.getSubtotal().toPlainString()});
        }
        Reserva reserva = controller.buscarReserva(idReserva);
        if (reserva != null) {
            lblResumen.setText("Estado: " + reserva.getEstado()
                    + "   |   Hospedaje: S/ " + reserva.getCostoHospedaje().toPlainString()
                    + "   |   TOTAL: S/ " + reserva.getTotal().toPlainString());
        }
    }
}
