package util;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class UIEstilos {

    public static final Color COLOR_PRIMARIO = new Color(14, 74, 92);
    public static final Color COLOR_PRIMARIO_OSCURO = new Color(9, 52, 66);
    public static final Color COLOR_SECUNDARIO = new Color(196, 154, 60);
    public static final Color COLOR_PELIGRO = new Color(166, 38, 38);
    public static final Color COLOR_FONDO = new Color(243, 246, 247);
    public static final Color COLOR_TARJETA = Color.WHITE;
    public static final Color COLOR_BORDE = new Color(205, 216, 220);
    public static final Color COLOR_TEXTO = new Color(45, 45, 45);
    public static final Color COLOR_TEXTO_SUAVE = new Color(100, 116, 122);

    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FUENTE_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_LABEL_NEGRITA = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_TABLA = new Font("Segoe UI", Font.PLAIN, 12);

    // Botón principal (registrar, guardar, generar, etc.)
    public static void estilizarBotonPrimario(JButton boton) {

        boton.setFont(FUENTE_BOTON);
        boton.setBackground(COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(8, 16, 8, 16));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

    }

    // Botón de peligro (eliminar)
    public static void estilizarBotonPeligro(JButton boton) {

        boton.setFont(FUENTE_BOTON);
        boton.setBackground(COLOR_PELIGRO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(8, 16, 8, 16));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

    }

    // Botón neutro (listar, actualizar, limpiar)
    public static void estilizarBotonSecundario(JButton boton) {

        boton.setFont(FUENTE_BOTON);
        boton.setBackground(new Color(225, 229, 235));
        boton.setForeground(COLOR_TEXTO);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(8, 16, 8, 16));
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

    }

    // Encabezado de ventana (titulo con fondo de color)
    public static JPanel crearEncabezado(String titulo) {

        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PRIMARIO);
        panel.setPreferredSize(new Dimension(100, 55));

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(FUENTE_TITULO);
        lbl.setForeground(Color.WHITE);

        panel.add(lbl);

        return panel;

    }

    public static JLabel crearEtiqueta(String texto) {

        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_LABEL_NEGRITA);
        lbl.setForeground(COLOR_TEXTO);

        return lbl;

    }

    public static void estilizarCampoTexto(JTextField campo) {

        campo.setFont(FUENTE_LABEL);
        campo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(COLOR_BORDE, 1),
                new EmptyBorder(4, 6, 4, 6)
        ));

    }

    public static void estilizarTabla(JTable tabla) {

        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(24);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setSelectionBackground(COLOR_PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setFont(FUENTE_LABEL_NEGRITA);
        tabla.getTableHeader().setBackground(new Color(220, 233, 237));
        tabla.getTableHeader().setForeground(COLOR_TEXTO);

    }

    public static EmptyBorder padding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

}
