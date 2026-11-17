import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Estilo {
    public static final Color CARTA_ALIADA = Color.decode("#F3F2C9");
    public static final Color CARTA_RIVAL = Color.decode("#CFE2F3");
    public static final Color CARTA_CLICK = Color.decode("#CE840E");
    public static final Color BORDE = Color.decode("#1E1E1E");

    public static final Color ACCION = Color.decode("#ECF4F8");
    public static final Color ACCION_CLICK = Color.decode("#6d7071");
    public static final Color PANEL_CONTEXTUAL = Color.decode("#F3F2C9");

    public static Font fuente(float size) {
        return new Font("Lexend", Font.BOLD, (int) size);
    }

    public static JButton crearCartaTablero(String texto, boolean esAliado) {
        Color base = esAliado ? CARTA_ALIADA : CARTA_RIVAL;
        return configurarBoton(texto, base, CARTA_CLICK, BORDE);
    }

    public static JButton crearBotonAccion(String texto) {
        return configurarBoton(texto, ACCION, ACCION_CLICK, BORDE);
    }

    private static JButton configurarBoton(String texto, Color colorNormal, Color colorClick, Color colorBorde) {
        JButton boton = new JButton(texto);
        boton.setBackground(colorNormal);
        boton.setForeground(Color.BLACK);
        boton.setFont(fuente(13f));
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