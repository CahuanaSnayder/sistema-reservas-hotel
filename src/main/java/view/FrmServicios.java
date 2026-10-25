package view;

import controller.SistemaController;
import model.Servicio;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrmServicios extends FrmCatalogoBase {

    private static final long serialVersionUID = 1L;

    private JTextField txtNombre;
    private JTextField txtPrecio;

    public FrmServicios(SistemaController controller) {
        super(controller, "Servicios Adicionales", new String[]{"ID", "Servicio", "Precio (S/)"});
    }

    @Override
    protected Map<String, JComponent> definirCampos() {
        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Nombre del servicio:", txtNombre);
        campos.put("Precio (S/):", txtPrecio);
        return campos;
    }

    @Override
    protected boolean registrarEnControlador() {
        BigDecimal precio = leerPrecio();
        return precio != null && controller.registrarServicio(txtNombre.getText(), precio);
    }

    @Override
    protected boolean actualizarEnControlador(int id) {
        BigDecimal precio = leerPrecio();
        return precio != null && controller.actualizarServicio(id, txtNombre.getText(), precio);
    }

    @Override
    protected boolean eliminarEnControlador(int id) {
        return controller.eliminarServicio(id);
    }

    @Override
    protected void llenarTabla() {
        for (Servicio s : controller.listarServicios()) {
            modelo.addRow(new Object[]{s.getIdServicio(), s.getNombre(), s.getPrecio().toPlainString()});
        }
    }

    @Override
    protected void seleccionarFila(int fila) {
        txtNombre.setText(texto(fila, 1));
        txtPrecio.setText(texto(fila, 2));
    }

    @Override
    protected void limpiarCamposPropios() {
        txtNombre.setText("");
        txtPrecio.setText("");
    }

    private BigDecimal leerPrecio() {
        try {
            return new BigDecimal(txtPrecio.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un precio válido.");
            return null;
        }
    }
}
