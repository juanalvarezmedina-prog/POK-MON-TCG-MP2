import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class InterfazPelea extends JFrame {

    private final Partida partida;
    private final JLayeredPane lienzo;

    // Dimensiones dinámicas de pantalla
    private final int anchoPantalla;
    private final int altoPantalla;
    private int cardW;
    private int cardH;
    private int handCardW;
    private int handCardH;
    private int handY;

    // Fondo, reverso rival y barras superiores
    private JLabel lblFondo;
    private ImageIcon iconoReversoRival;
    private JLabel lblInfoIzquierda;
    private JLabel lblInfoDerecha;
    private JTextArea txtRegistro;

    // Cartas del tablero y sus etiquetas externas (abajo de cada recuadro)
    private JButton activo, banco1, banco2;
    private JLabel lblActivo, lblBanco1, lblBanco2;

    private JButton activoRival, banco1Rival, banco2Rival;
    private JLabel lblActivoRival, lblBanco1Rival, lblBanco2Rival;

    private JButton btnTerminarTurno, btnAdjuntarEnergia, btnRendirse;

    // Panel desplegable del Pokémon Activo (Izquierda)
    private JPanel panelOpcionesActivo;
    private JButton btnAtacar, btnRetirar, btnEvolucionar;

    // Panel de Zoom al hacer clic en una carta de la mano
    private JPanel panelZoomCarta;
    private JLabel lblTituloZoom;
    private JLabel lblImagenZoom;
    private JTextArea txtDetalleZoom;
    private JButton btnJugarCarta, btnCerrarZoom;
    private int zoomImgW;
    private int zoomImgH;

    // Listas visuales de botones en mano
    private final List<JButton> manoAliada = new ArrayList<>();
    private final List<JButton> manoRival = new ArrayList<>();

    // Estado de selección actual
    private Carta cartaZoomSeleccionada;
    private boolean esperandoObjetivoEnergia = false;

    public InterfazPelea(Partida partida) {
        this.partida = partida;

        Rectangle limites = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.anchoPantalla = Math.max(1280, limites.width);
        this.altoPantalla = Math.max(768, limites.height);

        setTitle("Batalla Pokémon TCG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(anchoPantalla, altoPantalla);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);

        lienzo = new JLayeredPane();
        lienzo.setLayout(null);
        lienzo.setOpaque(true);
        lienzo.setBackground(Color.decode("#888888"));
        add(lienzo);

        inicializarFondoYReverso();
        inicializarInfoYRegistro();
        inicializarTablero();
        inicializarBotonesFijos();
        inicializarPanelActivo();
        inicializarPanelZoom();

        lienzo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panelOpcionesActivo.setVisible(false);
                panelZoomCarta.setVisible(false);
                esperandoObjetivoEnergia = false;
                btnAdjuntarEnergia.setText("Adjuntar Energía");
            }
        });

        setVisible(true);

        prepararActivosIniciales();
        actualizarPerspectiva();
    }

    private void inicializarFondoYReverso() {
        Random rng = new Random();

        lblFondo = new JLabel();
        lblFondo.setBounds(0, 0, anchoPantalla, altoPantalla);

        String[] posiblesFondos = { "fondo1.png", "fondo2.png", "fondo3.png" };
        List<ImageIcon> fondosDisponibles = new ArrayList<>();
        for (String nombre : posiblesFondos) {
            ImageIcon icono = Estilo.cargarIcono("media/" + nombre, anchoPantalla, altoPantalla);
            if (icono != null) {
                fondosDisponibles.add(icono);
            }
        }
        if (!fondosDisponibles.isEmpty()) {
            lblFondo.setIcon(fondosDisponibles.get(rng.nextInt(fondosDisponibles.size())));
        }
        lienzo.add(lblFondo, JLayeredPane.FRAME_CONTENT_LAYER);

        String[] posiblesReversos = { "reverso.jpg", "reverso1.jpg", "reverso2.jpg" };
        List<ImageIcon> reversosDisponibles = new ArrayList<>();
        for (String nombre : posiblesReversos) {
            ImageIcon icono = Estilo.cargarIcono("media/" + nombre, 68, 96);
            if (icono != null) {
                reversosDisponibles.add(icono);
            }
        }
        if (!reversosDisponibles.isEmpty()) {
            iconoReversoRival = reversosDisponibles.get(rng.nextInt(reversosDisponibles.size()));
        }
    }

    private void inicializarInfoYRegistro() {
        int anchoBarra = (int) (anchoPantalla * 0.31);

        lblInfoIzquierda = new JLabel("", SwingConstants.CENTER);
        lblInfoIzquierda.setFont(Estilo.fuente(13f));
        lblInfoIzquierda.setOpaque(true);
        lblInfoIzquierda.setBackground(Estilo.ACCION);
        lblInfoIzquierda.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));
        lblInfoIzquierda.setBounds(20, 12, anchoBarra, 38);
        lienzo.add(lblInfoIzquierda, JLayeredPane.DEFAULT_LAYER);

        lblInfoDerecha = new JLabel("", SwingConstants.CENTER);
        lblInfoDerecha.setFont(Estilo.fuente(13f));
        lblInfoDerecha.setOpaque(true);
        lblInfoDerecha.setBackground(Estilo.ACCION);
        lblInfoDerecha.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));
        lblInfoDerecha.setBounds(anchoPantalla - anchoBarra - 20, 12, anchoBarra, 38);
        lienzo.add(lblInfoDerecha, JLayeredPane.DEFAULT_LAYER);

        txtRegistro = new JTextArea();
        txtRegistro.setEditable(false);
        txtRegistro.setLineWrap(true);
        txtRegistro.setWrapStyleWord(true);
        txtRegistro.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane scrollRegistro = new JScrollPane(txtRegistro);
        scrollRegistro.setBorder(BorderFactory.createTitledBorder("Registro de Batalla"));
        scrollRegistro.setBackground(Estilo.ACCION);
        scrollRegistro.setBounds(20, 62, (int) (anchoPantalla * 0.22), (int) (altoPantalla * 0.26));
        lienzo.add(scrollRegistro, JLayeredPane.DEFAULT_LAYER);
    }

    private void inicializarTablero() {
        // Mano más compacta abajo (15.5% del alto de pantalla)
        handCardH = (int) (altoPantalla * 0.155);
        handCardW = (int) (handCardH * 0.72);
        handY = altoPantalla - handCardH - 35;

        // Aprovechar el espacio liberado para agrandar Activos y Banca
        int altoEtiqueta = 30;
        int rivalBancoY = 66;
        int espacioDisponible = handY - rivalBancoY - (altoEtiqueta * 2) - 24;
        cardH = (int) (espacioDisponible / 2.32);
        cardW = (int) (cardH * 0.72);

        int centroX = anchoPantalla / 2;

        // Rival (Arriba)
        banco1Rival = crearBotonSlot("Banca", false);
        banco2Rival = crearBotonSlot("Banca", false);
        activoRival = crearBotonSlot("Activo", false);

        int rivalActivoY = rivalBancoY + (int) (cardH * 0.16);

        banco1Rival.setBounds(centroX - (int) (cardW * 1.82), rivalBancoY, cardW, cardH);
        banco2Rival.setBounds(centroX + (int) (cardW * 0.82), rivalBancoY, cardW, cardH);
        activoRival.setBounds(centroX - (cardW / 2), rivalActivoY, cardW, cardH);

        lblBanco1Rival = crearEtiquetaEstado(banco1Rival, altoEtiqueta);
        lblBanco2Rival = crearEtiquetaEstado(banco2Rival, altoEtiqueta);
        lblActivoRival = crearEtiquetaEstado(activoRival, altoEtiqueta);

        // Aliado (Abajo)
        activo = crearBotonSlot("Activo", true);
        banco1 = crearBotonSlot("Banca", true);
        banco2 = crearBotonSlot("Banca", true);

        int aliadoBancoY = handY - cardH - altoEtiqueta - 12;
        int aliadoActivoY = aliadoBancoY - (int) (cardH * 0.16);

        activo.setBounds(centroX - (cardW / 2), aliadoActivoY, cardW, cardH);
        banco1.setBounds(centroX - (int) (cardW * 1.82), aliadoBancoY, cardW, cardH);
        banco2.setBounds(centroX + (int) (cardW * 0.82), aliadoBancoY, cardW, cardH);

        lblActivo = crearEtiquetaEstado(activo, altoEtiqueta);
        lblBanco1 = crearEtiquetaEstado(banco1, altoEtiqueta);
        lblBanco2 = crearEtiquetaEstado(banco2, altoEtiqueta);

        activo.addActionListener(e -> {
            PokemonEnJuego pActivo = partida.getJugadorActual().getCampo().getActivo();
            if (pActivo == null) {
                return;
            }
            if (esperandoObjetivoEnergia) {
                esperandoObjetivoEnergia = false;
                btnAdjuntarEnergia.setText("Adjuntar Energía");
                ejecutarAccion(partida.adjuntarEnergia(pActivo));
                return;
            }
            panelZoomCarta.setVisible(false);
            panelOpcionesActivo.setVisible(!panelOpcionesActivo.isVisible());
            lienzo.moveToFront(panelOpcionesActivo);
        });

        banco1.addActionListener(e -> manejarClicBancoAliado(0));
        banco2.addActionListener(e -> manejarClicBancoAliado(1));

        lienzo.add(banco1Rival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco2Rival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(activoRival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(lblBanco1Rival, JLayeredPane.PALETTE_LAYER);
        lienzo.add(lblBanco2Rival, JLayeredPane.PALETTE_LAYER);
        lienzo.add(lblActivoRival, JLayeredPane.PALETTE_LAYER);

        lienzo.add(activo, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco1, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco2, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(lblActivo, JLayeredPane.PALETTE_LAYER);
        lienzo.add(lblBanco1, JLayeredPane.PALETTE_LAYER);
        lienzo.add(lblBanco2, JLayeredPane.PALETTE_LAYER);
    }

    private JButton crearBotonSlot(String texto, boolean esAliado) {
        JButton btn = Estilo.crearCartaTablero(texto, esAliado);
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    private JLabel crearEtiquetaEstado(JButton cartaBoton, int altoEtiqueta) {
        JLabel lbl = new JLabel("", SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(28, 28, 28, 220));
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));
        lbl.setBounds(
                cartaBoton.getX() - 8,
                cartaBoton.getY() + cartaBoton.getHeight() + 2,
                cartaBoton.getWidth() + 16,
                altoEtiqueta);
        lbl.setVisible(false);
        return lbl;
    }

    private void manejarClicBancoAliado(int indiceBanco) {
        List<PokemonEnJuego> banco = partida.getJugadorActual().getCampo().getBanco();
        if (indiceBanco >= banco.size()) {
            return;
        }
        PokemonEnJuego seleccionado = banco.get(indiceBanco);
        if (esperandoObjetivoEnergia) {
            esperandoObjetivoEnergia = false;
            btnAdjuntarEnergia.setText("Adjuntar Energía");
            ejecutarAccion(partida.adjuntarEnergia(seleccionado));
        } else {
            ImageIcon iconoDetalle = Estilo.cargarIcono(seleccionado.getCarta().getRutaImagen(), 150, 210);
            JOptionPane.showMessageDialog(this,
                    formatearDetalleEnJuego(seleccionado),
                    seleccionado.getCarta().getNombre(),
                    JOptionPane.INFORMATION_MESSAGE,
                    iconoDetalle);
        }
    }

    private void inicializarBotonesFijos() {
        int btnW = (int) (anchoPantalla * 0.16);
        int btnX = anchoPantalla - btnW - 35;

        btnRendirse = Estilo.crearBotonAccion("Rendirse");
        btnRendirse.setBackground(Color.decode("#EF9A9A"));
        btnRendirse.setBounds(btnX, 72, btnW, 46);

        btnTerminarTurno = Estilo.crearBotonAccion("Terminar Turno");
        btnTerminarTurno.setBounds(btnX, 128, btnW, 46);

        btnAdjuntarEnergia = Estilo.crearBotonAccion("Adjuntar Energía");
        btnAdjuntarEnergia.setBackground(Estilo.CARTA_ALIADA);
        btnAdjuntarEnergia.setBounds(btnX, handY - 56, btnW, 46);

        btnAdjuntarEnergia.addActionListener(e -> {
            panelOpcionesActivo.setVisible(false);
            panelZoomCarta.setVisible(false);

            Jugador actual = partida.getJugadorActual();
            if (actual.getEnergiaDisponible() == null) {
                JOptionPane.showMessageDialog(this, "Ya usaste la energía de zona de este turno.");
                return;
            }
            esperandoObjetivoEnergia = true;
            btnAdjuntarEnergia.setText("Elige tu Pokémon...");
            JOptionPane.showMessageDialog(this,
                    "Haz clic sobre tu Pokémon Activo o uno de tu Banca para adjuntar energía " + actual.getTipoZona() + ".");
        });

        btnRendirse.addActionListener(e -> accionRendirse());
        btnTerminarTurno.addActionListener(e -> pasarTurnoConAviso());

        lienzo.add(btnRendirse, JLayeredPane.DEFAULT_LAYER);
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
        panelOpcionesActivo.setBounds(30, (int) (altoPantalla * 0.36), (int) (anchoPantalla * 0.20), 215);
        panelOpcionesActivo.setVisible(false);

        btnAtacar = Estilo.crearBotonAccion("Atacar");
        btnRetirar = Estilo.crearBotonAccion("Retirar Pokémon");
        btnEvolucionar = Estilo.crearBotonAccion("Evolucionar");

        btnAtacar.addActionListener(e -> {
            panelOpcionesActivo.setVisible(false);
            accionAtacar();
        });

        btnRetirar.addActionListener(e -> {
            panelOpcionesActivo.setVisible(false);
            accionRetirar();
        });

        btnEvolucionar.addActionListener(e -> {
            panelOpcionesActivo.setVisible(false);
            accionEvolucionarDesdeTablero();
        });

        panelOpcionesActivo.add(btnAtacar);
        panelOpcionesActivo.add(btnRetirar);
        panelOpcionesActivo.add(btnEvolucionar);

        lienzo.add(panelOpcionesActivo, JLayeredPane.POPUP_LAYER);
    }

private void inicializarPanelZoom() {
        // Calcular el borde derecho de la banca y dejar margen libre
        int centroX = anchoPantalla / 2;
        int bordeDerechoBanca = centroX + (int) (cardW * 1.82);
        int margen = 18;

        int zoomX = bordeDerechoBanca + margen;
        int zoomW = anchoPantalla - zoomX - 20;
        int zoomH = Math.max(300, (int) (altoPantalla * 0.39));
        int zoomY = 182;

        panelZoomCarta = new JPanel();
        panelZoomCarta.setLayout(null);
        panelZoomCarta.setBackground(Color.decode("#FFF59D"));
        panelZoomCarta.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 3));
        panelZoomCarta.setBounds(zoomX, zoomY, zoomW, zoomH);
        panelZoomCarta.setVisible(false);

        lblTituloZoom = new JLabel("Carta Seleccionada", SwingConstants.CENTER);
        lblTituloZoom.setFont(Estilo.fuente(14f));
        lblTituloZoom.setBounds(8, 6, zoomW - 16, 24);

        zoomImgH = zoomH - 94;
        zoomImgW = (int) (zoomImgH * 0.72);

        lblImagenZoom = new JLabel("", SwingConstants.CENTER);
        lblImagenZoom.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));
        lblImagenZoom.setBounds(12, 34, zoomImgW, zoomImgH);

        txtDetalleZoom = new JTextArea();
        txtDetalleZoom.setEditable(false);
        txtDetalleZoom.setLineWrap(true);
        txtDetalleZoom.setWrapStyleWord(true);
        txtDetalleZoom.setOpaque(false);
        txtDetalleZoom.setFont(new Font("SansSerif", Font.PLAIN, 11));
        JScrollPane scrollDetalle = new JScrollPane(txtDetalleZoom);
        scrollDetalle.setOpaque(false);
        scrollDetalle.getViewport().setOpaque(false);
        scrollDetalle.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        scrollDetalle.setBounds(12 + zoomImgW + 8, 34, zoomW - zoomImgW - 30, zoomImgH);

        int mitadBtnW = (zoomW - 36) / 2;
        btnJugarCarta = Estilo.crearBotonAccion("Jugar Carta");
        btnJugarCarta.setBounds(12, zoomH - 48, mitadBtnW, 38);
        btnJugarCarta.addActionListener(e -> {
            panelZoomCarta.setVisible(false);
            accionJugarCartaDeMano();
        });

        btnCerrarZoom = Estilo.crearBotonAccion("Cerrar");
        btnCerrarZoom.setBounds(12 + mitadBtnW + 12, zoomH - 48, mitadBtnW, 38);
        btnCerrarZoom.addActionListener(e -> panelZoomCarta.setVisible(false));

        panelZoomCarta.add(lblTituloZoom);
        panelZoomCarta.add(lblImagenZoom);
        panelZoomCarta.add(scrollDetalle);
        panelZoomCarta.add(btnJugarCarta);
        panelZoomCarta.add(btnCerrarZoom);

        lienzo.add(panelZoomCarta, JLayeredPane.MODAL_LAYER);
    }

    // --- PREPARACIÓN INICIAL ---
    private void prepararActivosIniciales() {
        JOptionPane.showMessageDialog(this,
                "Se lanzó la moneda: " + partida.getJugadorActual().getNombre() + " empieza la partida.",
                "Sorteo inicial",
                JOptionPane.INFORMATION_MESSAGE);

        elegirActivoInicialPara(partida.getJugadorActual());
        elegirActivoInicialPara(partida.getRival());
        partida.iniciarPrimerTurno();
    }

    private void elegirActivoInicialPara(Jugador j) {
        List<Pokemon> basicos = new ArrayList<>();
        for (Carta c : j.getMano()) {
            if (c instanceof Pokemon p && p.esBasico()) {
                basicos.add(p);
            }
        }
        String[] nombres = new String[basicos.size()];
        for (int i = 0; i < basicos.size(); i++) {
            nombres[i] = basicos.get(i).getNombre() + " (" + basicos.get(i).getPsMaximos() + " PS)";
        }

        while (j.getCampo().getActivo() == null) {
            int sel = JOptionPane.showOptionDialog(
                    this,
                    j.getNombre() + ", elige tu Pokémon Básico Activo inicial:",
                    "Preparación - " + j.getNombre(),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    nombres,
                    nombres[0]);
            if (sel >= 0 && sel < basicos.size()) {
                partida.colocarActivoInicial(j, basicos.get(sel));
            }
        }
    }

    // --- ACCIONES DE JUEGO ---
    private void accionJugarCartaDeMano() {
        if (cartaZoomSeleccionada == null) {
            return;
        }

        if (cartaZoomSeleccionada instanceof Pokemon p && p.esBasico()) {
            ejecutarAccion(partida.colocarEnBanco(p));
            return;
        }

        if (cartaZoomSeleccionada instanceof PokemonEvolucion evo) {
            PokemonEnJuego objetivo = elegirPokemonPropioDialogo(
                    "¿Qué Pokémon deseas evolucionar a " + evo.getNombre() + "?");
            if (objetivo != null) {
                ejecutarAccion(partida.evolucionar(evo, objetivo));
            }
            return;
        }

        if (cartaZoomSeleccionada instanceof Entrenador entrenador) {
            PokemonEnJuego objetivo = null;
            List<PokemonEnJuego> posibles = entrenador.objetivosPosibles(partida.getJugadorActual());
            if (!posibles.isEmpty()) {
                String[] opciones = new String[posibles.size()];
                for (int i = 0; i < posibles.size(); i++) {
                    opciones[i] = posibles.get(i).getCarta().getNombre() + " (" + posibles.get(i).getPsActuales() + " PS)";
                }
                int sel = JOptionPane.showOptionDialog(
                        this,
                        "Elige el Pokémon objetivo para " + entrenador.getNombre() + ":",
                        entrenador.getNombre(),
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]);
                if (sel < 0) {
                    return;
                }
                objetivo = posibles.get(sel);
            }
            ejecutarAccion(partida.jugarEntrenador(entrenador, objetivo));
        }
    }

    private void accionAtacar() {
        PokemonEnJuego pActivo = partida.getJugadorActual().getCampo().getActivo();
        if (pActivo == null) {
            return;
        }
        List<Ataque> ataques = pActivo.getCarta().getAtaques();
        String[] opciones = new String[ataques.size()];
        for (int i = 0; i < ataques.size(); i++) {
            Ataque a = ataques.get(i);
            opciones[i] = a.getNombre() + " (" + a.getCosto() + " " + a.getTipoEnergia() + " - " + a.getDano() + " daño)";
        }
        int sel = JOptionPane.showOptionDialog(
                this,
                "Elige el ataque de " + pActivo.getCarta().getNombre() + ":",
                "Atacar",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
        if (sel >= 0) {
            String error = partida.atacar(ataques.get(sel));
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Acción no válida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (partida.getGanador() != null) {
                actualizarPerspectiva();
                return;
            }

            Jugador debeReemplazar = partida.getJugadorQueDebeReemplazar();
            if (debeReemplazar != null) {
                actualizarPerspectiva();
            }

            pasarTurnoConAviso();
        }
    }

    private void pasarTurnoConAviso() {
        panelOpcionesActivo.setVisible(false);
        panelZoomCarta.setVisible(false);
        esperandoObjetivoEnergia = false;
        btnAdjuntarEnergia.setText("Adjuntar Energía");

        String error = partida.terminarTurno();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "No se puede terminar turno", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (partida.getGanador() == null) {
            for (JButton b : manoAliada) {
                b.setVisible(false);
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Turno de " + partida.getJugadorActual().getNombre(),
                    "Cambio de turno",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        actualizarPerspectiva();
    }

    private void accionRetirar() {
        List<PokemonEnJuego> banco = partida.getJugadorActual().getCampo().getBanco();
        if (banco.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes Pokémon en la banca para intercambiar.");
            return;
        }
        String[] opciones = new String[banco.size()];
        for (int i = 0; i < banco.size(); i++) {
            opciones[i] = banco.get(i).getCarta().getNombre() + " (" + banco.get(i).getPsActuales() + " PS)";
        }
        int sel = JOptionPane.showOptionDialog(
                this,
                "¿Qué Pokémon de la banca pasará a ser Activo?",
                "Retirar Pokémon Activo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
        if (sel >= 0) {
            ejecutarAccion(partida.retirar(sel));
        }
    }

    private void accionRendirse() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas rendirte? Esto terminará la partida.",
                "Confirmar Rendición",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            partida.rendirse();
            actualizarPerspectiva();
        }
    }

    private void accionEvolucionarDesdeTablero() {
        List<PokemonEvolucion> evolucionesEnMano = new ArrayList<>();
        for (Carta c : partida.getJugadorActual().getMano()) {
            if (c instanceof PokemonEvolucion evo) {
                evolucionesEnMano.add(evo);
            }
        }
        if (evolucionesEnMano.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tienes cartas de evolución en tu mano.");
            return;
        }
        String[] nombresEvo = new String[evolucionesEnMano.size()];
        for (int i = 0; i < evolucionesEnMano.size(); i++) {
            PokemonEvolucion e = evolucionesEnMano.get(i);
            nombresEvo[i] = e.getNombre() + " (de " + e.getEvolucionaDe() + ")";
        }
        int selEvo = JOptionPane.showOptionDialog(
                this,
                "Elige la carta de evolución de tu mano:",
                "Evolucionar",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresEvo,
                nombresEvo[0]);
        if (selEvo < 0) {
            return;
        }

        PokemonEvolucion elegida = evolucionesEnMano.get(selEvo);
        PokemonEnJuego objetivo = elegirPokemonPropioDialogo(
                "¿Qué Pokémon deseas evolucionar a " + elegida.getNombre() + "?");
        if (objetivo != null) {
            ejecutarAccion(partida.evolucionar(elegida, objetivo));
        }
    }

    private PokemonEnJuego elegirPokemonPropioDialogo(String mensaje) {
        Campo campo = partida.getJugadorActual().getCampo();
        List<PokemonEnJuego> propios = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();

        if (campo.getActivo() != null) {
            propios.add(campo.getActivo());
            etiquetas.add("[Activo] " + campo.getActivo().getCarta().getNombre());
        }
        for (PokemonEnJuego p : campo.getBanco()) {
            propios.add(p);
            etiquetas.add("[Banca] " + p.getCarta().getNombre());
        }

        if (propios.isEmpty()) {
            return null;
        }

        int sel = JOptionPane.showOptionDialog(
                this,
                mensaje,
                "Seleccionar Pokémon",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                etiquetas.toArray(new String[0]),
                etiquetas.get(0));
        return (sel >= 0 && sel < propios.size()) ? propios.get(sel) : null;
    }

    private void ejecutarAccion(String error) {
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Acción no válida", JOptionPane.WARNING_MESSAGE);
        }
        actualizarPerspectiva();
    }

    // --- VOLTEO DE PERSPECTIVA Y REFRESCO DEL TABLERO ---
    public void actualizarPerspectiva() {
        Jugador actual = partida.getJugadorActual();
        Jugador rival = partida.getRival();

        Jugador debeReemplazar = partida.getJugadorQueDebeReemplazar();
        if (debeReemplazar != null && partida.getGanador() == null) {
            pedirReemplazoKO(debeReemplazar);
        }

        String zonaTexto = actual.getTipoZona() + (actual.getEnergiaDisponible() != null ? " (Disponible)" : " (Usada)");
        lblInfoIzquierda.setText(
                "Turno " + partida.getTurno() + "  |  " + actual.getNombre() + "  |  Zona: " + zonaTexto);
        lblInfoDerecha.setText(
                "Premios: " + actual.getPuntosPremio() + "/" + Partida.PUNTOS_PARA_GANAR
                + " (Rival: " + rival.getPuntosPremio() + "/" + Partida.PUNTOS_PARA_GANAR + ")"
                + "  |  Mazos: " + actual.getMazo().cantidadCartas() + " vs " + rival.getMazo().cantidadCartas());

        Campo campoRival = rival.getCampo();
        actualizarSlotPokemon(activoRival, lblActivoRival, campoRival.getActivo(), "Activo Rival");
        List<PokemonEnJuego> bancoR = campoRival.getBanco();
        actualizarSlotPokemon(banco1Rival, lblBanco1Rival, bancoR.size() > 0 ? bancoR.get(0) : null, "Banca Rival");
        actualizarSlotPokemon(banco2Rival, lblBanco2Rival, bancoR.size() > 1 ? bancoR.get(1) : null, "Banca Rival");

        Campo campoPropio = actual.getCampo();
        actualizarSlotPokemon(activo, lblActivo, campoPropio.getActivo(), "Activo");
        List<PokemonEnJuego> bancoP = campoPropio.getBanco();
        actualizarSlotPokemon(banco1, lblBanco1, bancoP.size() > 0 ? bancoP.get(0) : null, "Banca");
        actualizarSlotPokemon(banco2, lblBanco2, bancoP.size() > 1 ? bancoP.get(1) : null, "Banca");

        for (JButton b : manoAliada) {
            lienzo.remove(b);
        }
        for (JButton b : manoRival) {
            lienzo.remove(b);
        }
        manoAliada.clear();
        manoRival.clear();

        for (Carta c : actual.getMano()) {
            agregarCartaManoAliada(c);
        }
        for (int i = 0; i < rival.getMano().size(); i++) {
            agregarCartaManoRival();
        }

        StringBuilder sb = new StringBuilder();
        for (String evento : partida.getRegistro()) {
            sb.append("• ").append(evento).append("\n");
        }
        txtRegistro.setText(sb.toString());

        lienzo.revalidate();
        lienzo.repaint();

        if (partida.getGanador() != null) {
            mostrarFinDePartida();
        }
    }

    private void actualizarSlotPokemon(JButton boton, JLabel etiquetaAbajo, PokemonEnJuego p, String textoVacio) {
        if (p == null) {
            boton.setIcon(null);
            boton.setText("<html><center>" + textoVacio + "<br><small>(Vacío)</small></center></html>");
            etiquetaAbajo.setVisible(false);
            return;
        }
        Pokemon carta = p.getCarta();
        ImageIcon icono = Estilo.cargarIcono(carta.getRutaImagen(), cardW - 4, cardH - 4);
        if (icono != null) {
            boton.setIcon(icono);
            boton.setText("");
        } else {
            boton.setIcon(null);
            boton.setText(carta.getNombre());
        }

        String energiasStr = formatearEnergias(p.getEnergias());
        etiquetaAbajo.setText("<html><center>PS: " + p.getPsActuales() + "/" + carta.getPsMaximos()
                + "<br>" + energiasStr + "</center></html>");
        etiquetaAbajo.setVisible(true);
    }

    private String formatearEnergias(List<TipoEnergia> energias) {
        if (energias.isEmpty()) {
            return "Sin energías";
        }
        StringBuilder sb = new StringBuilder();
        for (TipoEnergia tipo : TipoEnergia.values()) {
            int cantidad = 0;
            for (TipoEnergia e : energias) {
                if (e == tipo) {
                    cantidad++;
                }
            }
            if (cantidad > 0) {
                if (sb.length() > 0) {
                    sb.append(" | ");
                }
                sb.append(tipo.name()).append(": ").append(cantidad);
            }
        }
        return sb.toString();
    }

    private String formatearDetalleEnJuego(PokemonEnJuego p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getCarta().obtenerInformacion()).append("\n");
        sb.append("PS actuales: ").append(p.getPsActuales()).append("/").append(p.getCarta().getPsMaximos()).append("\n");
        sb.append("Energías adjuntas: ").append(p.getEnergias().isEmpty() ? "ninguna" : p.getEnergias());
        return sb.toString();
    }

    // --- GESTIÓN DINÁMICA DE LA MANO DEL JUGADOR ---
    public void agregarCartaManoAliada(Carta c) {
        JButton carta = new JButton();
        carta.setMargin(new Insets(0, 0, 0, 0));
        ImageIcon icono = Estilo.cargarIcono(c.getRutaImagen(), handCardW - 4, handCardH - 4);
        if (icono != null) {
            carta.setIcon(icono);
        } else {
            carta.setText("<html><div style='width:85px; text-align:center;'>" + c.getNombre() + "</div></html>");
        }
        carta.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));
        carta.setBackground(Color.decode("#FFEB3B"));
        carta.setFocusPainted(false);

        carta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                carta.setLocation(carta.getX(), handY - 35);
                lienzo.moveToFront(carta);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                carta.setLocation(carta.getX(), handY);
                reordenarCapasManoAliada();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                panelOpcionesActivo.setVisible(false);
                cartaZoomSeleccionada = c;
                lblTituloZoom.setText(c.getNombre());
                lblImagenZoom.setIcon(Estilo.cargarIcono(c.getRutaImagen(), zoomImgW - 4, zoomImgH - 4));
                txtDetalleZoom.setText(c.obtenerInformacion());

                if (c instanceof Pokemon p && p.esBasico()) {
                    btnJugarCarta.setText("Colocar en Banca");
                } else if (c instanceof PokemonEvolucion) {
                    btnJugarCarta.setText("Evolucionar");
                } else {
                    btnJugarCarta.setText("Jugar Entrenador");
                }

                panelZoomCarta.setVisible(true);
                lienzo.moveToFront(panelZoomCarta);
            }
        });

        manoAliada.add(carta);
        lienzo.add(carta, JLayeredPane.PALETTE_LAYER);
        actualizarPosicionesManoAliada();
    }

    private void actualizarPosicionesManoAliada() {
        int solapamiento = manoAliada.size() <= 7
                ? (int) (handCardW * 0.92)
                : (int) (handCardW * 0.68);
        int anchoTotal = (manoAliada.size() - 1) * solapamiento + handCardW;
        int inicioX = (anchoPantalla - anchoTotal) / 2;

        for (int i = 0; i < manoAliada.size(); i++) {
            JButton c = manoAliada.get(i);
            c.setBounds(inicioX + (i * solapamiento), handY, handCardW, handCardH);
        }
        reordenarCapasManoAliada();
    }

    private void reordenarCapasManoAliada() {
        for (int i = 0; i < manoAliada.size(); i++) {
            lienzo.setLayer(manoAliada.get(i), JLayeredPane.PALETTE_LAYER + i);
        }
    }

    // --- GESTIÓN DINÁMICA DE LA MANO DEL RIVAL ---
    public void agregarCartaManoRival() {
        int anchoCarta = 72;
        int altoCarta = 100;

        JButton cartaRival = new JButton();
        cartaRival.setMargin(new Insets(0, 0, 0, 0));
        if (iconoReversoRival != null) {
            cartaRival.setIcon(iconoReversoRival);
            cartaRival.setDisabledIcon(iconoReversoRival);
        } else {
            cartaRival.setText("RIVAL");
            cartaRival.setVerticalAlignment(SwingConstants.BOTTOM);
        }
        cartaRival.setBackground(Color.decode("#F48FB1"));
        cartaRival.setEnabled(false);
        cartaRival.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));

        manoRival.add(cartaRival);
        lienzo.add(cartaRival, JLayeredPane.PALETTE_LAYER);

        int solapamiento = 44;
        int anchoTotal = (manoRival.size() - 1) * solapamiento + anchoCarta;
        int inicioX = (anchoPantalla - anchoTotal) / 2;

        for (int i = 0; i < manoRival.size(); i++) {
            manoRival.get(i).setBounds(inicioX + (i * solapamiento), -46, anchoCarta, altoCarta);
        }
    }

    private void pedirReemplazoKO(Jugador j) {
        List<PokemonEnJuego> banco = j.getCampo().getBanco();
        String[] opciones = new String[banco.size()];
        for (int i = 0; i < banco.size(); i++) {
            opciones[i] = banco.get(i).getCarta().getNombre() + " (" + banco.get(i).getPsActuales() + " PS)";
        }
        while (j.necesitaReemplazo()) {
            int sel = JOptionPane.showOptionDialog(
                    this,
                    j.getNombre() + ", tu Pokémon Activo fue derrotado. Elige un reemplazo de la banca:",
                    "Reemplazo obligatorio",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);
            if (sel >= 0 && sel < banco.size()) {
                partida.reemplazarActivo(sel);
            }
        }
    }

    private void mostrarFinDePartida() {
        String[] opciones = { "Jugar otra", "Salir" };
        int resp = JOptionPane.showOptionDialog(
                this,
                "¡" + partida.getGanador().getNombre() + " ha ganado el duelo!\nMotivo: " + partida.getMotivoVictoria(),
                "Fin de la Partida",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (resp == 0) {
            new Gui().setVisible(true);
            dispose();
        } else {
            System.exit(0);
        }
    }
}