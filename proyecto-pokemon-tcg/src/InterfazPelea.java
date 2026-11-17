import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
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

    // Barras superiores (izquierda y derecha para no chocar con la mano rival) y registro
    private JLabel lblInfoIzquierda;
    private JLabel lblInfoDerecha;
    private JTextArea txtRegistro;

    // Cartas del tablero
    private JButton activo, banco1, banco2;
    private JButton activoRival, banco1Rival, banco2Rival;

    private JButton btnTerminarTurno, btnAdjuntarEnergia, btnRendirse;

    // Panel desplegable del Pokémon Activo (Izquierda)
    private JPanel panelOpcionesActivo;
    private JButton btnAtacar, btnRetirar, btnEvolucionar;

    // Panel de Zoom al hacer clic en una carta de la mano
    private JPanel panelZoomCarta;
    private JLabel lblTituloZoom;
    private JTextArea txtDetalleZoom;
    private JButton btnJugarCarta, btnCerrarZoom;

    // Listas visuales de botones en mano
    private final List<JButton> manoAliada = new ArrayList<>();
    private final List<JButton> manoRival = new ArrayList<>();

    // Estado de selección actual
    private Carta cartaZoomSeleccionada;
    private boolean esperandoObjetivoEnergia = false;

    public InterfazPelea(Partida partida) {
        this.partida = partida;

        setTitle("Batalla Pokémon TCG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        setResizable(false);

        lienzo = new JLayeredPane();
        lienzo.setLayout(null);
        lienzo.setOpaque(true);
        lienzo.setBackground(Color.decode("#888888"));
        add(lienzo);

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

    private void inicializarInfoYRegistro() {
        lblInfoIzquierda = new JLabel("", SwingConstants.CENTER);
        lblInfoIzquierda.setFont(Estilo.fuente(13f));
        lblInfoIzquierda.setOpaque(true);
        lblInfoIzquierda.setBackground(Estilo.ACCION);
        lblInfoIzquierda.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));
        lblInfoIzquierda.setBounds(20, 12, 410, 36);
        lienzo.add(lblInfoIzquierda, JLayeredPane.DEFAULT_LAYER);

        lblInfoDerecha = new JLabel("", SwingConstants.CENTER);
        lblInfoDerecha.setFont(Estilo.fuente(13f));
        lblInfoDerecha.setOpaque(true);
        lblInfoDerecha.setBackground(Estilo.ACCION);
        lblInfoDerecha.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));
        lblInfoDerecha.setBounds(840, 12, 410, 36);
        lienzo.add(lblInfoDerecha, JLayeredPane.DEFAULT_LAYER);

        txtRegistro = new JTextArea();
        txtRegistro.setEditable(false);
        txtRegistro.setLineWrap(true);
        txtRegistro.setWrapStyleWord(true);
        txtRegistro.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JScrollPane scrollRegistro = new JScrollPane(txtRegistro);
        scrollRegistro.setBorder(BorderFactory.createTitledBorder("Registro de Batalla"));
        scrollRegistro.setBackground(Estilo.ACCION);
        scrollRegistro.setBounds(20, 62, 290, 185);
        lienzo.add(scrollRegistro, JLayeredPane.DEFAULT_LAYER);
    }

    private void inicializarTablero() {
        // Rival (Arriba - Azul claro)
        banco1Rival = Estilo.crearCartaTablero("Banca", false);
        banco2Rival = Estilo.crearCartaTablero("Banca", false);
        activoRival = Estilo.crearCartaTablero("Activo", false);

        banco1Rival.setBounds(430, 95, 120, 145);
        banco2Rival.setBounds(690, 95, 120, 145);
        activoRival.setBounds(560, 195, 120, 145);

        // Aliado (Abajo - Crema)
        activo = Estilo.crearCartaTablero("Activo", true);
        banco1 = Estilo.crearCartaTablero("Banca", true);
        banco2 = Estilo.crearCartaTablero("Banca", true);

        activo.setBounds(560, 360, 120, 145);
        banco1.setBounds(430, 455, 120, 145);
        banco2.setBounds(690, 455, 120, 145);

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
        lienzo.add(activo, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco1, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(banco2, JLayeredPane.DEFAULT_LAYER);
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
            JOptionPane.showMessageDialog(this,
                    formatearDetalleEnJuego(seleccionado),
                    seleccionado.getCarta().getNombre(),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void inicializarBotonesFijos() {
        btnTerminarTurno = Estilo.crearBotonAccion("Terminar Turno");
        btnTerminarTurno.setBounds(960, 180, 220, 50);

        btnRendirse = Estilo.crearBotonAccion("Rendirse");
        btnRendirse.setBackground(Color.decode("#EF9A9A"));
        btnRendirse.setBounds(960, 110, 220, 50);

        btnAdjuntarEnergia = Estilo.crearBotonAccion("Adjuntar Energía");
        btnAdjuntarEnergia.setBackground(Estilo.CARTA_ALIADA);
        btnAdjuntarEnergia.setBounds(960, 520, 220, 50);

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

        lienzo.add(btnTerminarTurno, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(btnAdjuntarEnergia, JLayeredPane.DEFAULT_LAYER);
        lienzo.add(btnRendirse, JLayeredPane.DEFAULT_LAYER);

    }

    private void inicializarPanelActivo() {
        panelOpcionesActivo = new JPanel();
        panelOpcionesActivo.setLayout(new GridLayout(3, 1, 10, 12));
        panelOpcionesActivo.setBackground(Estilo.PANEL_CONTEXTUAL);
        panelOpcionesActivo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panelOpcionesActivo.setBounds(90, 265, 280, 220);
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
        panelZoomCarta = new JPanel();
        panelZoomCarta.setLayout(null);
        panelZoomCarta.setBackground(Color.decode("#FFF59D"));
        panelZoomCarta.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 3));
        panelZoomCarta.setBounds(920, 240, 280, 345);
        panelZoomCarta.setVisible(false);

        lblTituloZoom = new JLabel("Carta Seleccionada", SwingConstants.CENTER);
        lblTituloZoom.setFont(Estilo.fuente(15f));
        lblTituloZoom.setBounds(10, 10, 260, 28);

        txtDetalleZoom = new JTextArea();
        txtDetalleZoom.setEditable(false);
        txtDetalleZoom.setLineWrap(true);
        txtDetalleZoom.setWrapStyleWord(true);
        txtDetalleZoom.setOpaque(false);
        txtDetalleZoom.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane scrollDetalle = new JScrollPane(txtDetalleZoom);
        scrollDetalle.setOpaque(false);
        scrollDetalle.getViewport().setOpaque(false);
        scrollDetalle.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        scrollDetalle.setBounds(12, 42, 256, 195);

        btnJugarCarta = Estilo.crearBotonAccion("Jugar Carta");
        btnJugarCarta.setBounds(40, 245, 200, 40);
        btnJugarCarta.addActionListener(e -> {
            panelZoomCarta.setVisible(false);
            accionJugarCartaDeMano();
        });

        btnCerrarZoom = Estilo.crearBotonAccion("Cerrar");
        btnCerrarZoom.setBounds(40, 293, 200, 35);
        btnCerrarZoom.addActionListener(e -> panelZoomCarta.setVisible(false));

        panelZoomCarta.add(lblTituloZoom);
        panelZoomCarta.add(scrollDetalle);
        panelZoomCarta.add(btnJugarCarta);
        panelZoomCarta.add(btnCerrarZoom);

        lienzo.add(panelZoomCarta, JLayeredPane.MODAL_LAYER);
    }

    // --- PREPARACIÓN INICIAL ---
    private void prepararActivosIniciales() {
        JOptionPane.showMessageDialog(this,
            partida.getJugadorActual().getNombre() + " empieza la partida.",
            "Sorteo inicial",
            JOptionPane.INFORMATION_MESSAGE);

    elegirActivoInicialPara(partida.getJugadorActual());
    elegirActivoInicialPara(partida.getRival());
    partida.iniciarPrimerTurno();

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

    public String accionRendirse() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas rendirte? Esto terminará la partida.",
                "Confirmar Rendición",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String ganador = partida.getRival().getNombre();
            partida.rendirse();
            actualizarPerspectiva();
            JOptionPane.showMessageDialog(this, "Te has rendido. " + ganador + " gana la partida.", "Fin de Partida", JOptionPane.INFORMATION_MESSAGE);
            return ganador;
        }
        return null;
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
        actualizarSlotPokemon(activoRival, campoRival.getActivo(), "Activo Rival");
        List<PokemonEnJuego> bancoR = campoRival.getBanco();
        actualizarSlotPokemon(banco1Rival, bancoR.size() > 0 ? bancoR.get(0) : null, "Banca Rival");
        actualizarSlotPokemon(banco2Rival, bancoR.size() > 1 ? bancoR.get(1) : null, "Banca Rival");

        Campo campoPropio = actual.getCampo();
        actualizarSlotPokemon(activo, campoPropio.getActivo(), "Activo");
        List<PokemonEnJuego> bancoP = campoPropio.getBanco();
        actualizarSlotPokemon(banco1, bancoP.size() > 0 ? bancoP.get(0) : null, "Banca");
        actualizarSlotPokemon(banco2, bancoP.size() > 1 ? bancoP.get(1) : null, "Banca");

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

    private void actualizarSlotPokemon(JButton boton, PokemonEnJuego p, String textoVacio) {
        if (p == null) {
            boton.setText("<html><center>" + textoVacio + "<br><small>(Vacío)</small></center></html>");
            return;
        }
        Pokemon carta = p.getCarta();
        String etapa = "Básico";
        if (carta instanceof PokemonEvolucion evo) {
            etapa = evo.getEtapa().toString();
        } else if (carta instanceof PokemonEx) {
            etapa = "EX";
        }

        String energiasStr = formatearEnergias(p.getEnergias());
        boton.setText("<html><center><b>" + carta.getNombre() + "</b><br>"
                + "<small>" + etapa + " | " + carta.getTipo() + "</small><br>"
                + "PS: " + p.getPsActuales() + "/" + carta.getPsMaximos() + "<br>"
                + "<small>" + energiasStr + "</small></center></html>");
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
                    sb.append("<br>");
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
        JButton carta = new JButton("<html><div style='width:85px; text-align:center;'>" + c.getNombre() + "</div></html>");
        carta.setVerticalAlignment(SwingConstants.TOP);
        carta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDE, 2),
                BorderFactory.createEmptyBorder(10, 4, 4, 4)));
        carta.setBackground(Color.decode("#FFEB3B"));
        carta.setFont(Estilo.fuente(11f));
        carta.setFocusPainted(false);

        carta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                carta.setLocation(carta.getX(), 580);
                lienzo.moveToFront(carta);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                carta.setLocation(carta.getX(), 615);
                reordenarCapasManoAliada();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                panelOpcionesActivo.setVisible(false);
                cartaZoomSeleccionada = c;
                lblTituloZoom.setText(c.getNombre());
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
        int anchoCarta = 115;
        int altoCarta = 155;
        int solapamiento = manoAliada.size() <= 7 ? 105 : 75;
        int anchoTotal = (manoAliada.size() - 1) * solapamiento + anchoCarta;
        int inicioX = (1280 - anchoTotal) / 2;

        for (int i = 0; i < manoAliada.size(); i++) {
            JButton c = manoAliada.get(i);
            c.setBounds(inicioX + (i * solapamiento), 615, anchoCarta, altoCarta);
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
        JButton cartaRival = new JButton("RIVAL");
        cartaRival.setVerticalAlignment(SwingConstants.BOTTOM);
        cartaRival.setBackground(Color.decode("#F48FB1"));
        cartaRival.setEnabled(false);
        cartaRival.setBorder(BorderFactory.createLineBorder(Estilo.BORDE, 2));

        manoRival.add(cartaRival);
        lienzo.add(cartaRival, JLayeredPane.PALETTE_LAYER);

        int anchoCarta = 90;
        int altoCarta = 110;
        int solapamiento = 50;
        int anchoTotal = (manoRival.size() - 1) * solapamiento + anchoCarta;
        int inicioX = (1280 - anchoTotal) / 2;

        for (int i = 0; i < manoRival.size(); i++) {
            manoRival.get(i).setBounds(inicioX + (i * solapamiento), -45, anchoCarta, altoCarta);
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