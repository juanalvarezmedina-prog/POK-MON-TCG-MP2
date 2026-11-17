import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class Gui extends JFrame {
    private final JTextField txtJugador1;
    private final JComboBox<String> cmbMazo1;
    private final JButton btnArmar1;

    private final JTextField txtJugador2;
    private final JComboBox<String> cmbMazo2;
    private final JButton btnArmar2;

    private final JButton btnIniciar;

    private Mazo mazoPersonalizado1;
    private TipoEnergia zonaPersonalizada1;
    private List<String> nombresPersonalizados1;

    private Mazo mazoPersonalizado2;
    private TipoEnergia zonaPersonalizada2;
    private List<String> nombresPersonalizados2;

    public Gui() {
        setTitle("Pokémon TCG - Inicio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 280);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] mazos = CartaFactory.nombresMazos().toArray(new String[0]);

        // Jugador 1
        panel.add(new JLabel("Nombre Jugador 1:"));
        txtJugador1 = new JTextField();
        panel.add(txtJugador1);

        panel.add(new JLabel("Mazo Jugador 1:"));
        JPanel panelMazo1 = new JPanel(new BorderLayout(5, 0));
        cmbMazo1 = new JComboBox<>(mazos);
        btnArmar1 = new JButton("Armar mazo");
        btnArmar1.addActionListener(e -> abrirCreadorMazo(1));
        panelMazo1.add(cmbMazo1, BorderLayout.CENTER);
        panelMazo1.add(btnArmar1, BorderLayout.EAST);
        panel.add(panelMazo1);

        // Jugador 2
        panel.add(new JLabel("Nombre Jugador 2:"));
        txtJugador2 = new JTextField();
        panel.add(txtJugador2);

        panel.add(new JLabel("Mazo Jugador 2:"));
        JPanel panelMazo2 = new JPanel(new BorderLayout(5, 0));
        cmbMazo2 = new JComboBox<>(mazos);
        btnArmar2 = new JButton("Armar mazo");
        btnArmar2.addActionListener(e -> abrirCreadorMazo(2));
        panelMazo2.add(cmbMazo2, BorderLayout.CENTER);
        panelMazo2.add(btnArmar2, BorderLayout.EAST);
        panel.add(panelMazo2);

        // Botón Iniciar
        panel.add(new JLabel());
        btnIniciar = new JButton("Iniciar Partida");
        btnIniciar.addActionListener(e -> iniciarPartida());
        panel.add(btnIniciar);

        add(panel);
    }

    private void abrirCreadorMazo(int numJugador) {
        List<String> cartasPrevias = (numJugador == 1) ? nombresPersonalizados1 : nombresPersonalizados2;
        TipoEnergia zonaPrevia = (numJugador == 1) ? zonaPersonalizada1 : zonaPersonalizada2;
        String titulo = (cartasPrevias != null ? "Editar" : "Armar") + " Mazo - Jugador " + numJugador;

        JDialog dialogo = new JDialog(this, titulo, true);
        dialogo.setSize(760, 500);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));

        // Norte: Selección de zona de energía y contador
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelNorte.add(new JLabel("Zona de Energía:"));
        TipoEnergia[] zonas = { TipoEnergia.Fuego, TipoEnergia.Agua, TipoEnergia.Electrico, TipoEnergia.Psiquico };
        JComboBox<TipoEnergia> cmbZona = new JComboBox<>(zonas);
        if (zonaPrevia != null) {
            cmbZona.setSelectedItem(zonaPrevia);
        }
        panelNorte.add(cmbZona);

        JLabel lblEstado = new JLabel("Cartas: 0/20 | Básicos: 0/6");
        lblEstado.setFont(Estilo.fuente(13f));
        panelNorte.add(lblEstado);
        dialogo.add(panelNorte, BorderLayout.NORTH);

        // Lista izquierda: Cartas disponibles
        DefaultListModel<String> modeloDisponibles = new DefaultListModel<>();
        for (String nombre : CartaFactory.nombresDisponibles()) {
            modeloDisponibles.addElement(nombre);
        }
        JList<String> listaDisponibles = new JList<>(modeloDisponibles);
        listaDisponibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollIzq = new JScrollPane(listaDisponibles);
        scrollIzq.setBorder(BorderFactory.createTitledBorder("Cartas disponibles"));
        scrollIzq.setPreferredSize(new Dimension(220, 0));

        // Lista derecha: Cartas elegidas en el mazo (precargadas si ya existían)
        DefaultListModel<String> modeloElegidas = new DefaultListModel<>();
        if (cartasPrevias != null) {
            for (String nombre : cartasPrevias) {
                modeloElegidas.addElement(nombre);
            }
        }
        JList<String> listaElegidas = new JList<>(modeloElegidas);
        listaElegidas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollDer = new JScrollPane(listaElegidas);
        scrollDer.setBorder(BorderFactory.createTitledBorder("Tu mazo (20 cartas)"));
        scrollDer.setPreferredSize(new Dimension(220, 0));

        // Centro: Botones arriba + vista previa completa abajo
        JPanel panelCentro = new JPanel(new BorderLayout(8, 12));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 5, 10, 5));

        JPanel panelBotonesCentro = new JPanel(new GridLayout(3, 1, 6, 6));
        JButton btnAgregar = new JButton("Agregar >>");
        JButton btnQuitar = new JButton("<< Quitar");
        JButton btnVaciar = new JButton("Vaciar todo");
        panelBotonesCentro.add(btnAgregar);
        panelBotonesCentro.add(btnQuitar);
        panelBotonesCentro.add(btnVaciar);

        JTextArea txtInfoCarta = new JTextArea("Selecciona una carta para ver sus detalles.");
        txtInfoCarta.setEditable(false);
        txtInfoCarta.setLineWrap(true);
        txtInfoCarta.setWrapStyleWord(true);
        txtInfoCarta.setOpaque(false);
        txtInfoCarta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtInfoCarta.setBorder(BorderFactory.createTitledBorder("Detalle de la carta"));

        listaDisponibles.addListSelectionListener(e -> {
            String sel = listaDisponibles.getSelectedValue();
            if (sel != null) {
                Carta c = CartaFactory.crearCarta(sel);
                if (c != null) {
                    txtInfoCarta.setText(c.obtenerInformacion());
                }
            }
        });

        Runnable actualizarContador = () -> {
            int total = modeloElegidas.size();
            int basicos = 0;
            for (int i = 0; i < total; i++) {
                Carta c = CartaFactory.crearCarta(modeloElegidas.getElementAt(i));
                if (c instanceof Pokemon p && p.esBasico()) {
                    basicos++;
                }
            }
            lblEstado.setText("Cartas: " + total + "/20 | Básicos: " + basicos + "/6");
        };

        // Actualizar contador inicial por si se cargó un mazo previo
        actualizarContador.run();

        btnAgregar.addActionListener(e -> {
            String seleccionada = listaDisponibles.getSelectedValue();
            if (seleccionada == null) {
                return;
            }
            if (modeloElegidas.size() >= 20) {
                JOptionPane.showMessageDialog(dialogo, "El mazo ya tiene el máximo de 20 cartas.");
                return;
            }
            int copias = 0;
            for (int i = 0; i < modeloElegidas.size(); i++) {
                if (modeloElegidas.getElementAt(i).equalsIgnoreCase(seleccionada)) {
                    copias++;
                }
            }
            if (copias >= 2) {
                JOptionPane.showMessageDialog(dialogo, "Solo puedes incluir máximo 2 copias de " + seleccionada + ".");
                return;
            }
            modeloElegidas.addElement(seleccionada);
            actualizarContador.run();
        });

        btnQuitar.addActionListener(e -> {
            int idx = listaElegidas.getSelectedIndex();
            if (idx >= 0) {
                modeloElegidas.remove(idx);
                actualizarContador.run();
            }
        });

        btnVaciar.addActionListener(e -> {
            modeloElegidas.clear();
            actualizarContador.run();
        });

        panelCentro.add(panelBotonesCentro, BorderLayout.NORTH);
        panelCentro.add(txtInfoCarta, BorderLayout.CENTER);

        JPanel panelListas = new JPanel(new BorderLayout(8, 8));
        panelListas.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panelListas.add(scrollIzq, BorderLayout.WEST);
        panelListas.add(panelCentro, BorderLayout.CENTER);
        panelListas.add(scrollDer, BorderLayout.EAST);
        dialogo.add(panelListas, BorderLayout.CENTER);

        // Sur: Guardar o Cancelar
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar Mazo");

        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnGuardar.addActionListener(e -> {
            List<String> nombres = new ArrayList<>();
            for (int i = 0; i < modeloElegidas.size(); i++) {
                nombres.add(modeloElegidas.getElementAt(i));
            }
            Mazo nuevoMazo = CartaFactory.crearMazo(nombres);
            if (!nuevoMazo.esValido()) {
                JOptionPane.showMessageDialog(dialogo,
                        "El mazo no es válido.\nDebe tener exactamente 20 cartas, máximo 2 copias de cada una y mínimo 6 Pokémon básicos.",
                        "Mazo inválido",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            TipoEnergia zonaElegida = (TipoEnergia) cmbZona.getSelectedItem();
            if (numJugador == 1) {
                mazoPersonalizado1 = nuevoMazo;
                zonaPersonalizada1 = zonaElegida;
                nombresPersonalizados1 = nombres;
                btnArmar1.setText("Editar mazo");
                seleccionarOpcionPersonalizada(cmbMazo1);
            } else {
                mazoPersonalizado2 = nuevoMazo;
                zonaPersonalizada2 = zonaElegida;
                nombresPersonalizados2 = nombres;
                btnArmar2.setText("Editar mazo");
                seleccionarOpcionPersonalizada(cmbMazo2);
            }
            dialogo.dispose();
        });

        panelSur.add(btnCancelar);
        panelSur.add(btnGuardar);
        dialogo.add(panelSur, BorderLayout.SOUTH);

        dialogo.setVisible(true);
    }

    private void seleccionarOpcionPersonalizada(JComboBox<String> combo) {
        boolean existe = false;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if ("Personalizado".equals(combo.getItemAt(i))) {
                existe = true;
                break;
            }
        }
        if (!existe) {
            combo.addItem("Personalizado");
        }
        combo.setSelectedItem("Personalizado");
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

        String selMazo1 = (String) cmbMazo1.getSelectedItem();
        String selMazo2 = (String) cmbMazo2.getSelectedItem();

        Jugador j1 = "Personalizado".equals(selMazo1) && mazoPersonalizado1 != null
                ? new Jugador(nombre1, mazoPersonalizado1, zonaPersonalizada1)
                : CartaFactory.crearJugador(nombre1, selMazo1);

        Jugador j2 = "Personalizado".equals(selMazo2) && mazoPersonalizado2 != null
                ? new Jugador(nombre2, mazoPersonalizado2, zonaPersonalizada2)
                : CartaFactory.crearJugador(nombre2, selMazo2);

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

        new InterfazPelea(partida);
        dispose();
    }
}

