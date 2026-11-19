import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

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

    // Fondo, reverso rival, caché de mini-iconos y barras superiores
    private JLabel lblFondo;
    private ImageIcon iconoReversoRival;
    private final Map<TipoEnergia, ImageIcon> miniIconosEnergia = new EnumMap<>(TipoEnergia.class);
    private JLabel lblInfoIzquierda;
    private JLabel lblInfoDerecha;
    private JTextArea txtRegistro;

    // Pilas visuales de los mazos
    private JLabel lblMazoRival;
    private JLabel lblMazoAliado;

    // Cartas del tablero y sus paneles externos (abajo de cada recuadro)
    private JButton activo, banco1, banco2;
    private JPanel pnlEstadoActivo, pnlEstadoBanco1, pnlEstadoBanco2;

    private JButton activoRival, banco1Rival, banco2Rival;
    private JPanel pnlEstadoActivoRival, pnlEstadoBanco1Rival, pnlEstadoBanco2Rival;

    private JButton btnTerminarTurno, btnAdjuntarEnergia, btnRendirse;

    // Panel desplegable del Pokémon Activo (Izquierda)
    private JPanel panelOpcionesActivo;
    private JButton btnAtacar, btnRetirar, btnEvolucionar;

    // Panel de Zoom lateral derecho
    private JPanel panelZoomCarta;
    private JLabel lblTituloZoom;
    private JLabel lblImagenZoom;
    private JTextArea txtDetalleZoom;
    private JButton btnJugarCarta, btnCerrarZoom;
    private int zoomImgW;
    private int zoomImgH;
    private int zoomW;
    private int zoomH;

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

        cargarMiniIconosEnergia();
        inicializarFondoYReverso();
        inicializarInfoYRegistro();
        inicializarTablero();
        inicializarMazosVisuales();
        inicializarBotonesFijos();
        inicializarPanelActivo();
        inicializarPanelZoom();

        lienzo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panelOpcionesActivo.setVisible(false);
                panelZoomCarta.setVisible(false);
                if (esperandoObjetivoEnergia) {
                    esperandoObjetivoEnergia = false;
                    actualizarResaltadoEnergia();
                    actualizarIconoEnergiaDisponible(partida.getJugadorActual());
                }
            }
        });

        setVisible(true);

        prepararActivosIniciales();
        actualizarPerspectiva();
    }

    private void cargarMiniIconosEnergia() {
        for (TipoEnergia tipo : TipoEnergia.values()) {
            ImageIcon mini = Estilo.cargarIcono("media/" + tipo.name() + ".png", 14, 14);
            if (mini != null) {
                miniIconosEnergia.put(tipo, mini);
            }
        }
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
        handCardH = (int) (altoPantalla * 0.155);
        handCardW = (int) (handCardH * 0.72);
        handY = altoPantalla - handCardH - 35;

        int altoEtiqueta = 44;
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

        pnlEstadoBanco1Rival = crearPanelEstado(banco1Rival, altoEtiqueta);
        pnlEstadoBanco2Rival = crearPanelEstado(banco2Rival, altoEtiqueta);
        pnlEstadoActivoRival = crearPanelEstado(activoRival, altoEtiqueta);

        activoRival.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarZoomPokemonEnJuego(partida.getRival().getCampo().getActivo(), true);
            }
        });

        banco1Rival.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<PokemonEnJuego> bancoR = partida.getRival().getCampo().getBanco();
                if (bancoR.size() > 0) {
                    mostrarZoomPokemonEnJuego(bancoR.get(0), true);
                }
            }
        });

        banco2Rival.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<PokemonEnJuego> bancoR = partida.getRival().getCampo().getBanco();
                if (bancoR.size() > 1) {
                    mostrarZoomPokemonEnJuego(bancoR.get(1), true);
                }
            }
        });

        // Aliado (Abajo)
        activo = crearBotonSlot("Activo", true);
        banco1 = crearBotonSlot("Banca", true);
        banco2 = crearBotonSlot("Banca", true);

        int aliadoBancoY = handY - cardH - altoEtiqueta - 12;
        int aliadoActivoY = aliadoBancoY - (int) (cardH * 0.16);

        activo.setBounds(centroX - (cardW / 2), aliadoActivoY, cardW, cardH);
        banco1.setBounds(centroX - (int) (cardW * 1.82), aliadoBancoY, cardW, cardH);
        banco2.setBounds(centroX + (int) (cardW * 0.82), aliadoBancoY, cardW, cardH);

        pnlEstadoActivo = crearPanelEstado(activo, altoEtiqueta);
        pnlEstadoBanco1 = crearPanelEstado(banco1, altoEtiqueta);
        pnlEstadoBanco2 = crearPanelEstado(banco2, altoEtiqueta);

        activo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PokemonEnJuego pActivo = partida.getJugadorActual().getCampo().getActivo();
                if (pActivo == null) {
                    return;
                }
                if (esperandoObjetivoEnergia) {
                    esperandoObjetivoEnergia = false;
                    actualizarResaltadoEnergia();
                    ejecutarAccion(partida.adjuntarEnergia(pActivo));
                    return;
                }
                panelZoomCarta.setVisible(false);
                panelOpcionesActivo.setVisible(!panelOpcionesActivo.isVisible());
                lienzo.moveToFront(panelOpcionesActivo);
            }
        });

        banco1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarClicBancoAliado(0);
            }
        });

        banco2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manejarClicBancoAliado(1);
            }
        });

        lienzo.add(banco1Rival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco2Rival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(activoRival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(pnlEstadoBanco1Rival, JLayeredPane.PALETTE_LAYER);
        lienzo.add(pnlEstadoBanco2Rival, JLayeredPane.PALETTE_LAYER);
        lienzo.add(pnlEstadoActivoRival, JLayeredPane.PALETTE_LAYER);

        lienzo.add(activo, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco1, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco2, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(pnlEstadoActivo, JLayeredPane.PALETTE_LAYER);
        lienzo.add(pnlEstadoBanco1, JLayeredPane.PALETTE_LAYER);
        lienzo.add(pnlEstadoBanco2, JLayeredPane.PALETTE_LAYER);
    }

    private void inicializarMazosVisuales() {
        int mazoW = 68;
        int mazoH = 96;
        int mazoX = 28;

        lblMazoRival = crearEtiquetaMazo(mazoX, 62 + (int) (altoPantalla * 0.26) + 14, mazoW, mazoH);
        lblMazoAliado = crearEtiquetaMazo(mazoX, handY - 12, mazoW, mazoH);

        lienzo.add(lblMazoRival, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(lblMazoAliado, JLayeredPane.DEFAULT_LAYER);
    }

    private JLabel crearEtiquetaMazo(int x, int y, int w, int h) {
        JLabel lbl = new JLabel("", SwingConstants.CENTER);
        lbl.setBounds(x, y, w, h);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(30, 30, 30, 200));
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        lbl.setHorizontalTextPosition(SwingConstants.CENTER);
        lbl.setVerticalTextPosition(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));
        if (iconoReversoRival != null) {
            lbl.setIcon(iconoReversoRival);
        }
        return lbl;
    }

    private JButton crearBotonSlot(String texto, boolean esAliado) {
        JButton btn = Estilo.crearCartaTablero(texto, esAliado);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel crearPanelEstado(JButton cartaBoton, int altoPanel) {
        JPanel panel = new JPanel(new BorderLayout(0, 1));
        panel.setOpaque(true);
        panel.setBackground(new Color(24, 24, 24, 235));
        panel.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 1));
        panel.setBounds(
                cartaBoton.getX() - 8,
                cartaBoton.getY() + cartaBoton.getHeight() + 2,
                cartaBoton.getWidth() + 16,
                altoPanel);
        panel.setVisible(false);
        return panel;
    }

    private void manejarClicBancoAliado(int indiceBanco) {
        List<PokemonEnJuego> banco = partida.getJugadorActual().getCampo().getBanco();
        if (indiceBanco >= banco.size()) {
            return;
        }
        PokemonEnJuego seleccionado = banco.get(indiceBanco);
        if (esperandoObjetivoEnergia) {
            esperandoObjetivoEnergia = false;
            actualizarResaltadoEnergia();
            ejecutarAccion(partida.adjuntarEnergia(seleccionado));
        } else {
            mostrarZoomPokemonEnJuego(seleccionado, false);
        }
    }

    private void mostrarZoomPokemonEnJuego(PokemonEnJuego p, boolean esRival) {
        if (p == null) {
            return;
        }
        panelOpcionesActivo.setVisible(false);
        cartaZoomSeleccionada = null;

        String sufijo = esRival ? " (Rival)" : " (Tu Banca)";
        lblTituloZoom.setText(p.getCarta().getNombre() + sufijo);
        lblImagenZoom.setIcon(Estilo.cargarIcono(p.getCarta().getRutaImagen(), zoomImgW - 4, zoomImgH - 4));
        txtDetalleZoom.setText(formatearDetalleEnJuego(p));

        btnJugarCarta.setVisible(false);
        btnCerrarZoom.setBounds((zoomW - 140) / 2, zoomH - 48, 140, 38);

        panelZoomCarta.setVisible(true);
        lienzo.moveToFront(panelZoomCarta);
    }

    private void inicializarBotonesFijos() {
        int btnW = (int) (anchoPantalla * 0.16);
        int btnX = anchoPantalla - btnW - 35;

        btnRendirse = Estilo.crearBotonAccion("Rendirse");
        btnRendirse.setBackground(Color.decode("#EF9A9A"));
        btnRendirse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRendirse.setBounds(btnX, 72, btnW, 46);

        btnTerminarTurno = Estilo.crearBotonAccion("Terminar Turno");
        btnTerminarTurno.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTerminarTurno.setBounds(btnX, 128, btnW, 46);

        btnAdjuntarEnergia = Estilo.crearBotonAccion("Adjuntar Energía");
        btnAdjuntarEnergia.setBackground(Estilo.CARTA_ALIADA);
        btnAdjuntarEnergia.setIconTextGap(10);
        btnAdjuntarEnergia.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdjuntarEnergia.setBounds(btnX, handY - 56, btnW, 46);

        btnAdjuntarEnergia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelOpcionesActivo.setVisible(false);
                panelZoomCarta.setVisible(false);

                Jugador actual = partida.getJugadorActual();
                if (actual.getEnergiaDisponible() == null) {
                    JOptionPane.showMessageDialog(InterfazPelea.this, "Ya usaste la energía de zona de este turno.");
                    return;
                }
                esperandoObjetivoEnergia = !esperandoObjetivoEnergia;
                actualizarResaltadoEnergia();
                if (esperandoObjetivoEnergia) {
                    btnAdjuntarEnergia.setText("Elige tu Pokémon...");
                } else {
                    actualizarIconoEnergiaDisponible(actual);
                }
            }
        });

        btnRendirse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accionRendirse();
            }
        });

        btnTerminarTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasarTurnoConAviso();
            }
        });

        lienzo.add(btnRendirse, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(btnTerminarTurno, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(btnAdjuntarEnergia, JLayeredPane.DEFAULT_LAYER);
    }

    private void actualizarResaltadoEnergia() {
        Campo campo = partida.getJugadorActual().getCampo();
        List<PokemonEnJuego> banco = campo.getBanco();

        if (esperandoObjetivoEnergia) {
            javax.swing.border.Border bordeObjetivo = BorderFactory.createLineBorder(Color.decode("#00E676"), 4);
            if (campo.getActivo() != null) {
                activo.setBorder(bordeObjetivo);
            }
            if (banco.size() > 0) {
                banco1.setBorder(bordeObjetivo);
            }
            if (banco.size() > 1) {
                banco2.setBorder(bordeObjetivo);
            }
        } else {
            javax.swing.border.Border bordeNormal = BorderFactory.createLineBorder(Estilo.BORDE, 2);
            activo.setBorder(bordeNormal);
            banco1.setBorder(bordeNormal);
            banco2.setBorder(bordeNormal);
        }
    }

    private void actualizarIconoEnergiaDisponible(Jugador actual) {
        TipoEnergia disponible = actual.getEnergiaDisponible();
        if (disponible != null) {
            ImageIcon icono = Estilo.cargarIcono("media/" + disponible.name() + ".png", 28, 28);
            btnAdjuntarEnergia.setIcon(icono);
            btnAdjuntarEnergia.setBackground(Estilo.CARTA_ALIADA);
            if (!esperandoObjetivoEnergia) {
                btnAdjuntarEnergia.setText("Adjuntar Energía");
            }
        } else {
            btnAdjuntarEnergia.setIcon(null);
            btnAdjuntarEnergia.setBackground(Color.LIGHT_GRAY);
            btnAdjuntarEnergia.setText("Energía Usada");
        }
    }

    private void destellarImpactoRival() {
        activoRival.setBorder(BorderFactory.createLineBorder(Color.RED, 5));
        Timer timer = new Timer(320, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activoRival.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void inicializarPanelActivo() {
        panelOpcionesActivo = new JPanel();
        panelOpcionesActivo.setLayout(new GridLayout(3, 1, 10, 12));
        panelOpcionesActivo.setBackground(Estilo.PANEL_CONTEXTUAL);
        panelOpcionesActivo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelOpcionesActivo.setBounds(112, (int) (altoPantalla * 0.36), (int) (anchoPantalla * 0.18), 210);
        panelOpcionesActivo.setVisible(false);

        btnAtacar = Estilo.crearBotonAccion("Atacar");
        btnRetirar = Estilo.crearBotonAccion("Retirar Pokémon");
        btnEvolucionar = Estilo.crearBotonAccion("Evolucionar");

        btnAtacar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRetirar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEvolucionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnAtacar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelOpcionesActivo.setVisible(false);
                accionAtacar();
            }
        });

        btnRetirar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelOpcionesActivo.setVisible(false);
                accionRetirar();
            }
        });

        btnEvolucionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelOpcionesActivo.setVisible(false);
                accionEvolucionarDesdeTablero();
            }
        });

        panelOpcionesActivo.add(btnAtacar);
        panelOpcionesActivo.add(btnRetirar);
        panelOpcionesActivo.add(btnEvolucionar);

        lienzo.add(panelOpcionesActivo, JLayeredPane.POPUP_LAYER);
    }

    private void inicializarPanelZoom() {
        int centroX = anchoPantalla / 2;
        int bordeDerechoBanca = centroX + (int) (cardW * 1.82);
        int margen = 18;

        int zoomX = bordeDerechoBanca + margen;
        zoomW = anchoPantalla - zoomX - 20;
        zoomH = Math.max(300, (int) (altoPantalla * 0.39));
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
        btnJugarCarta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnJugarCarta.setBounds(12, zoomH - 48, mitadBtnW, 38);
        btnJugarCarta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelZoomCarta.setVisible(false);
                accionJugarCartaDeMano();
            }
        });

        btnCerrarZoom = Estilo.crearBotonAccion("Cerrar");
        btnCerrarZoom.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarZoom.setBounds(12 + mitadBtnW + 12, zoomH - 48, mitadBtnW, 38);
        btnCerrarZoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelZoomCarta.setVisible(false);
            }
        });

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

            destellarImpactoRival();

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
        actualizarResaltadoEnergia();

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
                + "  |  Premios Rival: " + rival.getPuntosPremio() + "/" + Partida.PUNTOS_PARA_GANAR);

        int cartasRival = rival.getMazo().cantidadCartas();
        int cartasPropias = actual.getMazo().cantidadCartas();
        lblMazoRival.setText("<html><center><span style='background-color:rgba(0,0,0,0.65); padding:2px 6px;'>" + cartasRival + "</span></center></html>");
        lblMazoRival.setToolTipText("Mazo de " + rival.getNombre() + ": " + cartasRival + " cartas");
        lblMazoAliado.setText("<html><center><span style='background-color:rgba(0,0,0,0.65); padding:2px 6px;'>" + cartasPropias + "</span></center></html>");
        lblMazoAliado.setToolTipText("Tu mazo: " + cartasPropias + " cartas");

        actualizarIconoEnergiaDisponible(actual);
        actualizarResaltadoEnergia();

        Campo campoRival = rival.getCampo();
        actualizarSlotPokemon(activoRival, pnlEstadoActivoRival, campoRival.getActivo(), "Activo Rival");
        List<PokemonEnJuego> bancoR = campoRival.getBanco();
        actualizarSlotPokemon(banco1Rival, pnlEstadoBanco1Rival, bancoR.size() > 0 ? bancoR.get(0) : null, "Banca Rival");
        actualizarSlotPokemon(banco2Rival, pnlEstadoBanco2Rival, bancoR.size() > 1 ? bancoR.get(1) : null, "Banca Rival");

        Campo campoPropio = actual.getCampo();
        actualizarSlotPokemon(activo, pnlEstadoActivo, campoPropio.getActivo(), "Activo");
        List<PokemonEnJuego> bancoP = campoPropio.getBanco();
        actualizarSlotPokemon(banco1, pnlEstadoBanco1, bancoP.size() > 0 ? bancoP.get(0) : null, "Banca");
        actualizarSlotPokemon(banco2, pnlEstadoBanco2, bancoP.size() > 1 ? bancoP.get(1) : null, "Banca");

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

    private void actualizarSlotPokemon(JButton boton, JPanel panelAbajo, PokemonEnJuego p, String textoVacio) {
        if (p == null) {
            boton.setIcon(null);
            boton.setText("<html><center>" + textoVacio + "<br><small>(Vacío)</small></center></html>");
            panelAbajo.setVisible(false);
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

        panelAbajo.removeAll();

        JPanel bloqueVida = new JPanel(new BorderLayout(0, 1));
        bloqueVida.setOpaque(false);

        int psAct = p.getPsActuales();
        int psMax = carta.getPsMaximos();

        JLabel lblPs = new JLabel("PS: " + psAct + "/" + psMax, SwingConstants.CENTER);
        lblPs.setForeground(Color.WHITE);
        lblPs.setFont(new Font("SansSerif", Font.BOLD, 10));
        bloqueVida.add(lblPs, BorderLayout.NORTH);

        JProgressBar barraVida = new JProgressBar(0, psMax);
        barraVida.setValue(psAct);
        barraVida.setPreferredSize(new Dimension(panelAbajo.getWidth() - 10, 6));
        barraVida.setBackground(new Color(60, 60, 60));
        barraVida.setBorderPainted(false);

        double ratio = (psMax > 0) ? ((double) psAct / psMax) : 0;
        if (ratio > 0.5) {
            barraVida.setForeground(Color.decode("#00E676"));
        } else if (ratio > 0.25) {
            barraVida.setForeground(Color.decode("#FFD600"));
        } else {
            barraVida.setForeground(Color.decode("#FF1744"));
        }
        bloqueVida.add(barraVida, BorderLayout.SOUTH);

        panelAbajo.add(bloqueVida, BorderLayout.NORTH);

        JPanel filaEnergias = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 1));
        filaEnergias.setOpaque(false);

        List<TipoEnergia> energias = p.getEnergias();
        if (energias.isEmpty()) {
            JLabel lblSin = new JLabel("Sin energías");
            lblSin.setForeground(Color.LIGHT_GRAY);
            lblSin.setFont(new Font("SansSerif", Font.PLAIN, 9));
            filaEnergias.add(lblSin);
        } else {
            for (TipoEnergia e : energias) {
                ImageIcon mini = miniIconosEnergia.get(e);
                if (mini != null) {
                    JLabel lblIcono = new JLabel(mini);
                    lblIcono.setToolTipText(e.name());
                    filaEnergias.add(lblIcono);
                } else {
                    JLabel lblTxt = new JLabel(e.name().substring(0, 1));
                    lblTxt.setForeground(Color.YELLOW);
                    lblTxt.setFont(new Font("SansSerif", Font.BOLD, 10));
                    filaEnergias.add(lblTxt);
                }
            }
        }

        panelAbajo.add(filaEnergias, BorderLayout.CENTER);
        panelAbajo.revalidate();
        panelAbajo.repaint();
        panelAbajo.setVisible(true);
    }

    private String formatearDetalleEnJuego(PokemonEnJuego p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getCarta().obtenerInformacion()).append("\n");
        sb.append("PS actuales: ").append(p.getPsActuales()).append("/").append(p.getCarta().getPsMaximos()).append("\n");
        sb.append("Energías adjuntas: ").append(p.getEnergias().isEmpty() ? "ninguna" : p.getEnergias());
        return sb.toString();
    }

    // --- GESTIÓN DINÁMICA DE LA MANO DEL JUGADOR ---
    public void agregarCartaManoAliada(final Carta c) {
        final JButton carta = new JButton();
        carta.setMargin(new Insets(0, 0, 0, 0));
        carta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

                int mitadBtnW = (zoomW - 36) / 2;
                btnJugarCarta.setBounds(12, zoomH - 48, mitadBtnW, 38);
                btnCerrarZoom.setBounds(12 + mitadBtnW + 12, zoomH - 48, mitadBtnW, 38);
                btnJugarCarta.setVisible(true);

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

