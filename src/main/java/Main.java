import config.ConexionPostgres;
import view.FrmLogin;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println(" SISTEMA DE RESERVAS DE HOTEL ");
        System.out.println(" PostgreSQL + Patrones GoF y GRASP");
        System.out.println("======================================");

        aplicarApariencia();
        try {
            ConexionPostgres.getInstancia().probarConexion();
        } catch (RuntimeException e) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error de conexión con PostgreSQL",
                    JOptionPane.ERROR_MESSAGE
            ));
            return;
        }

        SwingUtilities.invokeLater(() -> new FrmLogin().setVisible(true));
    }

    private static void aplicarApariencia() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo aplicar Nimbus; se utilizará el estilo predeterminado.");
        }
    }
}
