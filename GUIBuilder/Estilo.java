import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        return configurarBoton(texto, ancho, alto, CARTA, CARTA_CLICK, BORDE);
    }

    public static JButton crearBotonAccion(String texto, int ancho, int alto) {
        return configurarBoton(texto, ancho, alto, ACCION, ACCION_CLICK, BORDE_ACCION);
    }

    private static JButton configurarBoton(String texto, int ancho, int alto, Color colorNormal, Color colorClick, Color colorBorde) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(ancho, alto));
        boton.setBackground(colorNormal);
        boton.setForeground(Color.BLACK);
        boton.setFont(fuente(14f));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(colorBorde, 2));
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boton.setBackground(colorClick);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boton.setBackground(colorNormal);
            }
        });

        return boton;
    }
}