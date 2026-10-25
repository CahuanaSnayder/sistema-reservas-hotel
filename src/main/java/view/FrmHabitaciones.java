package view;

import controller.SistemaController;
import model.Habitacion;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrmHabitaciones extends FrmCatalogoBase {

    private static final long serialVersionUID = 1L;

    private JTextField txtNumero;
    private JComboBox<String> cboTipo;
    private JTextField txtCapacidad;
    private JTextField txtPrecio;

    public FrmHabitaciones(SistemaController controller) {
        super(controller, "Gestión de Habitaciones",
                new String[]{"ID", "Número", "Tipo", "Capacidad", "Precio por noche (S/)"});
    }

    @Override
    protected Map<String, JComponent> definirCampos() {
        txtNumero = new JTextField();
        cboTipo = new JComboBox<>(controller.tiposHabitacion().toArray(new String[0]));
        txtCapacidad = new JTextField();
        txtPrecio = new JTextField();
        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Número:", txtNumero);
        campos.put("Tipo:", cboTipo);
        campos.put("Capacidad (personas):", txtCapacidad);
        campos.put("Precio por noche (S/):", txtPrecio);
        return campos;
    }

    @Override
    protected boolean registrarEnControlador() {
        try {
            return controller.registrarHabitacion(txtNumero.getText(), (String) cboTipo.getSelectedItem(),
                    Integer.parseInt(txtCapacidad.getText().trim()), new BigDecimal(txtPrecio.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacidad y precio deben ser números válidos.");
            return false;
        }
    }

    @Override
    protected boolean actualizarEnControlador(int id) {
        try {
            return controller.actualizarHabitacion(id, txtNumero.getText(), (String) cboTipo.getSelectedItem(),
                    Integer.parseInt(txtCapacidad.getText().trim()), new BigDecimal(txtPrecio.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacidad y precio deben ser números válidos.");
            return false;
        }
    }

    @Override
    protected boolean eliminarEnControlador(int id) {
        return controller.eliminarHabitacion(id);
    }

    @Override
    protected void llenarTabla() {
        for (Habitacion h : controller.listarHabitaciones()) {
            modelo.addRow(new Object[]{h.getIdHabitacion(), h.getNumero(), h.getTipo(),
                    h.getCapacidad(), h.getPrecioNoche().toPlainString()});
        }
    }

    @Override
    protected void seleccionarFila(int fila) {
        txtNumero.setText(texto(fila, 1));
        cboTipo.setSelectedItem(texto(fila, 2));
        txtCapacidad.setText(texto(fila, 3));
        txtPrecio.setText(texto(fila, 4));
    }

    @Override
    protected void limpiarCamposPropios() {
        txtNumero.setText("");
        cboTipo.setSelectedIndex(0);
        txtCapacidad.setText("");
        txtPrecio.setText("");
    }
}
