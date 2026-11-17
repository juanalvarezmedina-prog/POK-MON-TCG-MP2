import javax.swing.*;
import java.awt.*;
import helper_classes.*;
public class Estilo {
    public static final Color CARTA = Color.decode("#E5A700");
    public static final Color CARTA_CLICK = Color.decode("#CE840E");
    public static final Color BORDE = Color.decode("#3d364a");

    public static final Color ACCION = Color.decode("#ECF4F8");
    public static final Color ACCION_CLICK = Color.decode("#6d7071");
    public static final Color BORDE_ACCION = Color.decode("#262627");

    public static Font fuente(float size) {
        return new Font("Lexend", Font.BOLD, (int) size);
    }

    public static JButton crearCarta(String texto, int ancho, int alto) {
    JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(ancho, alto));
        boton.setBackground(CARTA);
        boton.setForeground(Color.BLACK);
        boton.setFont(fuente(14f));
        boton.setBorder(new RoundedBorder(4, BORDE, 1));
        boton.setFocusPainted(false);
        OnClickEventHelper.setOnClickColor(boton, CARTA_CLICK, CARTA);
        return boton;
    }

     public static JButton crearBotonAccion(String texto, int ancho, int alto) {
    JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(ancho, alto));
        boton.setBackground(ACCION);
        boton.setForeground(Color.BLACK);
        boton.setFont(fuente(14f));
        boton.setBorder(new RoundedBorder(4, BORDE_ACCION, 1));
        boton.setFocusPainted(false);
        OnClickEventHelper.setOnClickColor(boton, ACCION_CLICK, ACCION);
        return boton;
    }

    public static JLabel crearTexto(String texto, int ancho, int alto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setPreferredSize(new Dimension(ancho, alto));
        etiqueta.setFont(fuente(14f));
        return etiqueta;
    }

}
