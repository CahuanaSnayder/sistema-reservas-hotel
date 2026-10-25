package view;

import controller.SistemaController;
import model.Huesped;
import util.UIEstilos;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

@SuppressWarnings("this-escape")
public class FrmReportes extends JFrame {

    private static final long serialVersionUID = 1L;

    public FrmReportes(SistemaController controller) {
        setTitle("Reportes del Hotel");
        setSize(720, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIEstilos.COLOR_FONDO);

        JComboBox<Huesped> cboHuesped = new JComboBox<>();
        controller.listarHuespedes().forEach(cboHuesped::addItem);
        JTextArea txtReporte = new JTextArea();
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        txtReporte.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnHuesped = new JButton("Reporte por huésped");
        JButton btnGeneral = new JButton("Reporte general de ocupación");
        UIEstilos.estilizarBotonPrimario(btnHuesped);
        UIEstilos.estilizarBotonPrimario(btnGeneral);

        // La vista solo pide el reporte; ReporteFactory decide la clase concreta.
        btnHuesped.addActionListener(e -> {
            Huesped huesped = (Huesped) cboHuesped.getSelectedItem();
            txtReporte.setText(huesped == null
                    ? "No hay huéspedes registrados."
                    : controller.generarReporteHuesped(huesped.getIdHuesped()));
        });
        btnGeneral.addActionListener(e -> txtReporte.setText(controller.generarReporteOcupacion()));

        JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelOpciones.setBackground(UIEstilos.COLOR_FONDO);
        panelOpciones.add(UIEstilos.crearEtiqueta("Huésped:"));
        panelOpciones.add(cboHuesped);
        panelOpciones.add(btnHuesped);
        panelOpciones.add(btnGeneral);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(UIEstilos.crearEncabezado("Reportes"), BorderLayout.NORTH);
        panelSuperior.add(panelOpciones, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(txtReporte);
        scroll.setBorder(new EmptyBorder(0, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);
    }
}
