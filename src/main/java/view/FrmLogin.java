package view;

import controller.SistemaController;
import util.UIEstilos;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

@SuppressWarnings("this-escape")
public class FrmLogin extends JFrame {

    private static final long serialVersionUID = 1L;

    private transient final SistemaController controller = new SistemaController();

    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtContrasena = new JPasswordField();

    public FrmLogin() {
        setTitle("Hotel - Iniciar sesión");
        setSize(420, 330);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIEstilos.COLOR_FONDO);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setBackground(UIEstilos.COLOR_PRIMARIO);
        encabezado.setBorder(new EmptyBorder(14, 10, 14, 10));
        JLabel titulo = new JLabel("HOTEL OASIS ICA", SwingConstants.CENTER);
        titulo.setFont(UIEstilos.FUENTE_TITULO);
        titulo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("Sistema de Reservas", SwingConstants.CENTER);
        subtitulo.setFont(UIEstilos.FUENTE_SUBTITULO);
        subtitulo.setForeground(UIEstilos.COLOR_SECUNDARIO);
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        add(encabezado, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridLayout(4, 1, 6, 6));
        formulario.setBackground(UIEstilos.COLOR_FONDO);
        formulario.setBorder(new EmptyBorder(20, 50, 10, 50));
        UIEstilos.estilizarCampoTexto(txtUsuario);
        UIEstilos.estilizarCampoTexto(txtContrasena);
        formulario.add(UIEstilos.crearEtiqueta("Usuario:"));
        formulario.add(txtUsuario);
        formulario.add(UIEstilos.crearEtiqueta("Contraseña:"));
        formulario.add(txtContrasena);
        add(formulario, BorderLayout.CENTER);

        JButton btnIngresar = new JButton("Ingresar");
        UIEstilos.estilizarBotonPrimario(btnIngresar);
        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(UIEstilos.COLOR_FONDO);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panelBoton.add(btnIngresar);
        add(panelBoton, BorderLayout.SOUTH);

        btnIngresar.addActionListener(e -> ingresar());
        txtContrasena.addActionListener(e -> ingresar());
        getRootPane().setDefaultButton(btnIngresar);
    }

    private void ingresar() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());
        if (controller.iniciarSesion(usuario, contrasena)) {
            new MenuPrincipal(controller, usuario.trim()).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, controller.getUltimoMensaje(),
                    "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            txtContrasena.setText("");
        }
    }
}
