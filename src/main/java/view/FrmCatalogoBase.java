package view;

import controller.SistemaController;
import util.ModeloTablaNoEditable;
import util.UIEstilos;

import javax.swing.JButton;
import javax.swing.JComponent;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Plantilla común para los mantenimientos (huéspedes, habitaciones, servicios):
 * arma el formulario, la tabla y los botones; cada subclase define sus campos y acciones.
 */
@SuppressWarnings("this-escape")
public abstract class FrmCatalogoBase extends JFrame {

    private static final long serialVersionUID = 1L;

    protected transient final SistemaController controller;
    protected final JTextField txtId = new JTextField();
    protected final DefaultTableModel modelo = new ModeloTablaNoEditable();
    protected final JTable tabla = new JTable(modelo);

    protected FrmCatalogoBase(SistemaController controller, String titulo, String[] columnas) {
        this.controller = controller;
        setTitle(titulo);
        setSize(760, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIEstilos.COLOR_FONDO);

        txtId.setEditable(false);
        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("ID:", txtId);
        campos.putAll(definirCampos());

        JPanel panelDatos = new JPanel(new GridLayout(campos.size(), 2, 8, 8));
        panelDatos.setBackground(UIEstilos.COLOR_FONDO);
        panelDatos.setBorder(new EmptyBorder(15, 20, 10, 20));
        campos.forEach((etiqueta, campo) -> {
            if (campo instanceof JTextField texto) {
                UIEstilos.estilizarCampoTexto(texto);
            }
            panelDatos.add(UIEstilos.crearEtiqueta(etiqueta));
            panelDatos.add(campo);
        });

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(UIEstilos.crearEncabezado(titulo), BorderLayout.NORTH);
        panelSuperior.add(panelDatos, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        for (String columna : columnas) {
            modelo.addColumn(columna);
        }
        UIEstilos.estilizarTabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(0, 20, 0, 20));
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(UIEstilos.COLOR_FONDO);
        panelBotones.add(boton("Registrar", true, this::registrar));
        panelBotones.add(boton("Actualizar", true, () -> conSeleccion(this::actualizar)));
        JButton btnEliminar = new JButton("Eliminar");
        UIEstilos.estilizarBotonPeligro(btnEliminar);
        btnEliminar.addActionListener(e -> conSeleccion(this::confirmarEliminacion));
        panelBotones.add(btnEliminar);
        panelBotones.add(boton("Listar", false, this::cargarTabla));
        panelBotones.add(boton("Limpiar", false, this::limpiarCampos));
        add(panelBotones, BorderLayout.SOUTH);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila != -1) {
                    txtId.setText(String.valueOf(modelo.getValueAt(fila, 0)));
                    seleccionarFila(fila);
                }
            }
        });

        cargarTabla();
    }

    protected abstract Map<String, JComponent> definirCampos();

    protected abstract boolean registrarEnControlador();

    protected abstract boolean actualizarEnControlador(int id);

    protected abstract boolean eliminarEnControlador(int id);

    protected abstract void llenarTabla();

    protected abstract void seleccionarFila(int fila);

    protected abstract void limpiarCamposPropios();

    protected String texto(int fila, int columna) {
        Object valor = modelo.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void registrar() {
        mostrarResultado(registrarEnControlador());
    }

    private void actualizar(int id) {
        mostrarResultado(actualizarEnControlador(id));
    }

    private void confirmarEliminacion(int id) {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar el registro seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            mostrarResultado(eliminarEnControlador(id));
        }
    }

    private void mostrarResultado(boolean exito) {
        JOptionPane.showMessageDialog(this, controller.getUltimoMensaje());
        if (exito) {
            limpiarCampos();
            cargarTabla();
        }
    }

    private void conSeleccion(java.util.function.IntConsumer accion) {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla.");
            return;
        }
        accion.accept(Integer.parseInt(txtId.getText()));
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        llenarTabla();
    }

    private void limpiarCampos() {
        txtId.setText("");
        limpiarCamposPropios();
        tabla.clearSelection();
    }

    private JButton boton(String texto, boolean primario, Runnable accion) {
        JButton boton = new JButton(texto);
        if (primario) {
            UIEstilos.estilizarBotonPrimario(boton);
        } else {
            UIEstilos.estilizarBotonSecundario(boton);
        }
        boton.addActionListener(e -> accion.run());
        return boton;
    }
}
