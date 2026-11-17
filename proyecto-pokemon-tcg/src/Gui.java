import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Gui extends JFrame {
    private final JTextField txtJugador1;
    private final JComboBox<String> cmbMazo1;
    private final JTextField txtJugador2;
    private final JComboBox<String> cmbMazo2;
    private final JButton btnIniciar;

    public Gui() {
        setTitle("Pokémon TCG - Inicio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 260);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] mazos = CartaFactory.nombresMazos().toArray(new String[0]);

        panel.add(new JLabel("Nombre Jugador 1:"));
        txtJugador1 = new JTextField();
        panel.add(txtJugador1);

        panel.add(new JLabel("Mazo Jugador 1:"));
        cmbMazo1 = new JComboBox<>(mazos);
        panel.add(cmbMazo1);

        panel.add(new JLabel("Nombre Jugador 2:"));
        txtJugador2 = new JTextField();
        panel.add(txtJugador2);

        panel.add(new JLabel("Mazo Jugador 2:"));
        cmbMazo2 = new JComboBox<>(mazos);
        panel.add(cmbMazo2);

        panel.add(new JLabel());
        btnIniciar = new JButton("Iniciar Partida");
        panel.add(btnIniciar);

        btnIniciar.addActionListener(e -> iniciarPartida());

        add(panel);
    }

    private void iniciarPartida() {
        String nombre1 = txtJugador1.getText().trim();
        String nombre2 = txtJugador2.getText().trim();

        if (nombre1.isEmpty() || nombre2.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ambos jugadores deben ingresar su nombre.",
                    "Datos incompletos",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String mazo1 = (String) cmbMazo1.getSelectedItem();
        String mazo2 = (String) cmbMazo2.getSelectedItem();

        Jugador j1 = CartaFactory.crearJugador(nombre1, mazo1);
        Jugador j2 = CartaFactory.crearJugador(nombre2, mazo2);

        if (!j1.getMazo().esValido()) {
            JOptionPane.showMessageDialog(this,
                    "El mazo de " + nombre1 + " no es válido (20 cartas, máximo 2 copias y mínimo 6 básicos).",
                    "Mazo inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!j2.getMazo().esValido()) {
            JOptionPane.showMessageDialog(this,
                    "El mazo de " + nombre2 + " no es válido (20 cartas, máximo 2 copias y mínimo 6 básicos).",
                    "Mazo inválido",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Partida partida = new Partida(j1, j2);
        partida.prepararInicial();

        JOptionPane.showMessageDialog(this,
                "¡Partida lista entre " + j1.getNombre() + " y " + j2.getNombre() + "!");
    }
}