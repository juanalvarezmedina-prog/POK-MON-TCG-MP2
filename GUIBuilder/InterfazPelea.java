import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class InterfazPelea extends JFrame {

    private JLayeredPane lienzo;
    
    // Cartas del tablero
    private JButton activo, banco1, banco2;
    private JButton activoRival, banco1Rival, banco2Rival;

    // Botones fijos a la derecha
    private JButton btnTerminarTurno, btnAdjuntarEnergia;

    // Panel desplegable del Pokémon Activo (Izquierda)
    private JPanel panelOpcionesActivo;
    private JButton btnAtacar, btnRetirar, btnEvolucionar;

    // Panel de Zoom al hacer clic en una carta de la mano
    private JPanel panelZoomCarta;
    private JLabel lblTituloZoom;
    private JButton btnJugarCarta, btnCerrarZoom;

    // Listas dinámicas para las manos
    private List<JButton> manoAliada = new ArrayList<>();
    private List<JButton> manoRival = new ArrayList<>();

    public InterfazPelea() {
        setTitle("Batalla Pokémon TCG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        lienzo = new JLayeredPane();
        lienzo.setLayout(null);
        lienzo.setOpaque(true);
        lienzo.setBackground(Color.decode("#888888")); // Fondo gris del boceto
        add(lienzo);

        inicializarTablero();
        inicializarBotonesFijos();
        inicializarPanelActivo();
        inicializarPanelZoom();

        // Simulación inicial: 5 cartas para cada jugador
        for (int i = 1; i <= 5; i++) {
            agregarCartaManoAliada("Pikachu #" + i);
            agregarCartaManoRival();
        }

        // Si se hace clic en el fondo gris vacío, se cierran los paneles desplegables
        lienzo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panelOpcionesActivo.setVisible(false);
                panelZoomCarta.setVisible(false);
            }
        });

        setVisible(true);
    }

    private void inicializarTablero() {
        // Rival (Arriba - Azul claro)
        banco1Rival = Estilo.crearCartaTablero("Banca", false);
        banco2Rival = Estilo.crearCartaTablero("Banca", false);
        activoRival = Estilo.crearCartaTablero("Activo", false);

        banco1Rival.setBounds(430, 120, 120, 155);
        banco2Rival.setBounds(690, 120, 120, 155);
        activoRival.setBounds(560, 225, 120, 155);

        // Aliado (Abajo - Crema)
        activo = Estilo.crearCartaTablero("Activo", true);
        banco1 = Estilo.crearCartaTablero("Banca", true);
        banco2 = Estilo.crearCartaTablero("Banca", true);

        activo.setBounds(560, 400, 120, 155);
        banco1.setBounds(430, 505, 120, 155);
        banco2.setBounds(690, 505, 120, 155);

        // Evento al hacer clic en el Pokémon Activo: abre/cierra el menú lateral
        activo.addActionListener(e -> {
            panelZoomCarta.setVisible(false);
            panelOpcionesActivo.setVisible(!panelOpcionesActivo.isVisible());
        });

        lienzo.add(banco1Rival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco2Rival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(activoRival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(activo, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco1, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco2, JLayeredPane.DEFAULT_LAYER);
    }

    private void inicializarBotonesFijos() {
        btnTerminarTurno = Estilo.crearBotonAccion("Terminar Turno");
        btnTerminarTurno.setBounds(960, 240, 220, 55);

        btnAdjuntarEnergia = Estilo.crearBotonAccion("Adjuntar Energía");
        btnAdjuntarEnergia.setBackground(Estilo.CARTA_ALIADA);
        btnAdjuntarEnergia.setBounds(960, 550, 220, 55);

        // Ejemplo de prueba: al terminar turno, roba una carta para probar el dinamismo
        btnTerminarTurno.addActionListener(e -> {
            agregarCartaManoAliada("Nueva Carta");
            agregarCartaManoRival();
        });

        lienzo.add(btnTerminarTurno, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(btnAdjuntarEnergia, JLayeredPane.DEFAULT_LAYER);
    }

    private void inicializarPanelActivo() {
        panelOpcionesActivo = new JPanel();
        panelOpcionesActivo.setLayout(new GridLayout(3, 1, 10, 12));
        panelOpcionesActivo.setBackground(Estilo.PANEL_CONTEXTUAL);
        panelOpcionesActivo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelOpcionesActivo.setBounds(90, 260, 280, 220);
        panelOpcionesActivo.setVisible(false); // Oculto hasta hacer clic en Activo

        btnAtacar = Estilo.crearBotonAccion("Atacar");
        btnRetirar = Estilo.crearBotonAccion("Retirar Pokémon");
        btnEvolucionar = Estilo.crearBotonAccion("Evolucionar");

        panelOpcionesActivo.add(btnAtacar);
        panelOpcionesActivo.add(btnRetirar);
        panelOpcionesActivo.add(btnEvolucionar);

        // Lo colocamos en la capa POPUP_LAYER para que flote sobre el tablero
        lienzo.add(panelOpcionesActivo, JLayeredPane.POPUP_LAYER);
    }

    private void inicializarPanelZoom() {
        panelZoomCarta = new JPanel();
        panelZoomCarta.setLayout(null);
        panelZoomCarta.setBackground(Color.decode("#FFF59D"));
        panelZoomCarta.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 3));
        panelZoomCarta.setBounds(880, 280, 260, 360);
        panelZoomCarta.setVisible(false);

        lblTituloZoom = new JLabel("Carta Seleccionada", SwingConstants.CENTER);
        lblTituloZoom.setFont(Estilo.fuente(16f));
        lblTituloZoom.setBounds(10, 15, 240, 30);

        btnJugarCarta = Estilo.crearBotonAccion("Jugar Carta");
        btnJugarCarta.setBounds(30, 250, 200, 40);

        btnCerrarZoom = Estilo.crearBotonAccion("Cerrar");
        btnCerrarZoom.setBounds(30, 300, 200, 35);
        btnCerrarZoom.addActionListener(e -> panelZoomCarta.setVisible(false));

        panelZoomCarta.add(lblTituloZoom);
        panelZoomCarta.add(btnJugarCarta);
        panelZoomCarta.add(btnCerrarZoom);

        lienzo.add(panelZoomCarta, JLayeredPane.MODAL_LAYER);
    }

    // --- GESTIÓN DINÁMICA DE LA MANO DEL JUGADOR ---
    public void agregarCartaManoAliada(String nombreCarta) {
        JButton carta = new JButton(nombreCarta);
        carta.setBackground(Color.decode("#FFEB3B"));
        carta.setFont(Estilo.fuente(12f));
        carta.setFocusPainted(false);
        carta.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));

        // Efecto Hover (elevación) y Zoom al hacer clic
        carta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Sube 35 píxeles y pasa al frente
                carta.setLocation(carta.getX(), 635);
                lienzo.moveToFront(carta);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Vuelve a su posición normal abajo
                carta.setLocation(carta.getX(), 670);
                reordenarCapasManoAliada();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                panelOpcionesActivo.setVisible(false);
                lblTituloZoom.setText(nombreCarta);
                panelZoomCarta.setVisible(true);
                lienzo.moveToFront(panelZoomCarta);
            }
        });

        manoAliada.add(carta);
        lienzo.add(carta, JLayeredPane.PALETTE_LAYER);
        actualizarPosicionesManoAliada();
    }

    private void actualizarPosicionesManoAliada() {
        int anchoCarta = 110;
        int altoCarta = 150;
        int solapamiento = 65; // Qué tanto se monta una carta sobre otra
        int anchoTotal = (manoAliada.size() - 1) * solapamiento + anchoCarta;
        int inicioX = (1280 - anchoTotal) / 2; // Centrado automático

        for (int i = 0; i < manoAliada.size(); i++) {
            JButton c = manoAliada.get(i);
            c.setBounds(inicioX + (i * solapamiento), 670, anchoCarta, altoCarta);
        }
        reordenarCapasManoAliada();
    }

    private void reordenarCapasManoAliada() {
        for (int i = 0; i < manoAliada.size(); i++) {
            lienzo.setLayer(manoAliada.get(i), JLayeredPane.PALETTE_LAYER + i);
        }
    }

    // --- GESTIÓN DINÁMICA DE LA MANO DEL RIVAL (Reverso arriba) ---
    public void agregarCartaManoRival() {
        JButton cartaRival = new JButton("RIVAL");
        cartaRival.setBackground(Color.decode("#F48FB1")); // Tono rosado/lila del reverso del boceto
        cartaRival.setEnabled(false); // El jugador no puede hacer clic en las cartas del rival
        cartaRival.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));

        manoRival.add(cartaRival);
        lienzo.add(cartaRival, JLayeredPane.PALETTE_LAYER);

        int anchoCarta = 100;
        int altoCarta = 140;
        int solapamiento = 60;
        int anchoTotal = (manoRival.size() - 1) * solapamiento + anchoCarta;
        int inicioX = (1280 - anchoTotal) / 2;

        for (int i = 0; i < manoRival.size(); i++) {
            manoRival.get(i).setBounds(inicioX + (i * solapamiento), -45, anchoCarta, altoCarta);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InterfazPelea::new);
    }
}