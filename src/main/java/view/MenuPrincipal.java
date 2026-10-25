package view;

import controller.SistemaController;
import util.UIEstilos;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.function.Supplier;

@SuppressWarnings("this-escape")
public class MenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private transient final SistemaController controller;

    public MenuPrincipal(SistemaController controller, String usuario) {
        this.controller = controller;
        setTitle("Hotel Oasis Ica - Menú principal");
        setSize(560, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIEstilos.COLOR_FONDO);

        add(UIEstilos.crearEncabezado("Sistema de Reservas de Hotel"), BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(3, 2, 14, 14));
        opciones.setBackground(UIEstilos.COLOR_FONDO);
        opciones.setBorder(new EmptyBorder(25, 40, 15, 40));

        opciones.add(crearOpcion("Huéspedes", () -> new FrmHuespedes(controller)));
        opciones.add(crearOpcion("Habitaciones", () -> new FrmHabitaciones(controller)));
        opciones.add(crearOpcion("Servicios adicionales", () -> new FrmServicios(controller)));
        opciones.add(crearOpcion("Reservas", () -> new FrmReservas(controller)));
        opciones.add(crearOpcion("Reportes", () -> new FrmReportes(controller)));

        JButton btnSalir = new JButton("Cerrar sesión");
        UIEstilos.estilizarBotonPeligro(btnSalir);
        btnSalir.addActionListener(e -> {
            new FrmLogin().setVisible(true);
            dispose();
        });
        opciones.add(btnSalir);
        add(opciones, BorderLayout.CENTER);

        JLabel pie = new JLabel("Usuario conectado: " + usuario, SwingConstants.CENTER);
        pie.setFont(UIEstilos.FUENTE_SUBTITULO);
        pie.setForeground(UIEstilos.COLOR_TEXTO_SUAVE);
        pie.setBorder(new EmptyBorder(0, 0, 12, 0));
        add(pie, BorderLayout.SOUTH);
    }

    private JButton crearOpcion(String texto, Supplier<JFrame> ventana) {
        JButton boton = new JButton(texto);
        UIEstilos.estilizarBotonPrimario(boton);
        boton.addActionListener(e -> ventana.get().setVisible(true));
        return boton;
    }
}
