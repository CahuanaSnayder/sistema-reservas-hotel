package view;

import controller.SistemaController;
import model.Huesped;

import javax.swing.JComponent;
import javax.swing.JTextField;
import java.util.LinkedHashMap;
import java.util.Map;

public class FrmHuespedes extends FrmCatalogoBase {

    private static final long serialVersionUID = 1L;

    private JTextField txtNombre;
    private JTextField txtDocumento;
    private JTextField txtEmail;
    private JTextField txtTelefono;

    public FrmHuespedes(SistemaController controller) {
        super(controller, "Gestión de Huéspedes",
                new String[]{"ID", "Nombre", "Documento", "Email", "Teléfono", "Fecha registro"});
    }

    @Override
    protected Map<String, JComponent> definirCampos() {
        txtNombre = new JTextField();
        txtDocumento = new JTextField();
        txtEmail = new JTextField();
        txtTelefono = new JTextField();
        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Nombre completo:", txtNombre);
        campos.put("DNI / Pasaporte:", txtDocumento);
        campos.put("Email:", txtEmail);
        campos.put("Teléfono:", txtTelefono);
        return campos;
    }

    @Override
    protected boolean registrarEnControlador() {
        return controller.registrarHuesped(txtNombre.getText(), txtDocumento.getText(),
                txtEmail.getText(), txtTelefono.getText());
    }

    @Override
    protected boolean actualizarEnControlador(int id) {
        return controller.actualizarHuesped(id, txtNombre.getText(), txtDocumento.getText(),
                txtEmail.getText(), txtTelefono.getText());
    }

    @Override
    protected boolean eliminarEnControlador(int id) {
        return controller.eliminarHuesped(id);
    }

    @Override
    protected void llenarTabla() {
        for (Huesped h : controller.listarHuespedes()) {
            modelo.addRow(new Object[]{h.getIdHuesped(), h.getNombre(), h.getDocumento(),
                    h.getEmail(), h.getTelefono(), h.getFechaRegistro()});
        }
    }

    @Override
    protected void seleccionarFila(int fila) {
        txtNombre.setText(texto(fila, 1));
        txtDocumento.setText(texto(fila, 2));
        txtEmail.setText(texto(fila, 3));
        txtTelefono.setText(texto(fila, 4));
    }

    @Override
    protected void limpiarCamposPropios() {
        txtNombre.setText("");
        txtDocumento.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
    }
}
