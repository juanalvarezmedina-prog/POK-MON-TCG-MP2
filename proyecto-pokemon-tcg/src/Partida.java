import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Partida {
    public static final int PUNTOS_PARA_GANAR = 3;

    private final Jugador[] jugadores;
    private final List<String> registro;
    private int turno;
    private int indiceJugadorActivo;
    private Jugador ganador;
    private String motivoVictoria;
    private final Random random = new Random();
    private int indicePrimerJugador;

    public Partida(Jugador j1, Jugador j2) {
        this.jugadores = new Jugador[] { j1, j2 };
        this.registro = new ArrayList<>();
        this.turno = 1;
        this.ganador = null;
        this.motivoVictoria = null;
        this.indicePrimerJugador = random.nextInt(2);
        this.indiceJugadorActivo = indicePrimerJugador;
    }

    public int getTurno() {
        return turno;
    }

    public Jugador getJugadorActual() {
        return jugadores[indiceJugadorActivo];
    }

    public Jugador getRival() {
        return jugadores[1 - indiceJugadorActivo];
    }

    public Jugador getGanador() {
        return ganador;
    }

    public String getMotivoVictoria() {
        return motivoVictoria;
    }

    public List<String> getRegistro() {
        return new ArrayList<>(registro);
    }

    private void registrar(String evento) {
        registro.add(evento);
    }

    // --- Preparación inicial ---

    public void prepararInicial() {
        indicePrimerJugador = random.nextInt(2);
        indiceJugadorActivo = indicePrimerJugador;
        registrar("El azar decidió: " + getJugadorActual().getNombre() + " empieza la partida.");

        for (Jugador j : jugadores) {
            j.getMazo().barajar();
            j.hacerMulligan();
            while (!j.tieneBasicoEnMano()) {
                registrar(j.getNombre() + " no tiene Pokémon básico: mulligan.");
                j.hacerMulligan();
            }
        }
    }

    public String colocarActivoInicial(Jugador jugador, Pokemon pokemon) {
        if (jugador == null || pokemon == null) {
            return "Debes elegir un Pokémon básico.";
        }
        if (!jugador.colocarActivoDesdeMano(pokemon, turno)) {
            return "No puedes colocar " + pokemon.getNombre() + " como Pokémon activo.";
        }
        registrar(jugador.getNombre() + " colocó a " + pokemon.getNombre() + " como Pokémon activo inicial.");
        return null;
    }

    public void iniciarPrimerTurno() {
        Jugador primero = getJugadorActual();
        primero.iniciarTurno();
        if (!primero.robarCarta()) {
            ganador = getRival();
            motivoVictoria = primero.getNombre() + " se quedó sin cartas para robar.";
            registrar(motivoVictoria);
        } else {
            registrar("Comienza el turno " + turno + " de " + primero.getNombre() + ".");
        }
    }

    // --- Acciones de turno ---

    public String adjuntarEnergia(PokemonEnJuego destino) {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        if (getJugadorQueDebeReemplazar() != null) {
            return "Primero se debe elegir un Pokémon activo de reemplazo.";
        }
        Jugador actual = getJugadorActual();
        TipoEnergia energia = actual.getEnergiaDisponible();
        String error = actual.adjuntarEnergiaDeZona(destino);
        if (error != null) {
            return error;
        }
        registrar(actual.getNombre() + " adjuntó energía " + energia + " a " + destino.getCarta().getNombre() + ".");
        return null;
    }

    public String jugarEntrenador(Entrenador carta, PokemonEnJuego objetivo) {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        if (getJugadorQueDebeReemplazar() != null) {
            return "Primero se debe elegir un Pokémon activo de reemplazo.";
        }
        if (carta == null) {
            return "Debes elegir una carta de entrenador.";
        }
        Jugador actual = getJugadorActual();
        Jugador rival = getRival();

        List<PokemonEnJuego> objetivos = carta.objetivosPosibles(actual);
        if (!objetivos.isEmpty() && objetivo == null) {
            return "Debes elegir un Pokémon objetivo para " + carta.getNombre() + ".";
        }

        String error = actual.jugarEntrenador(carta, rival, objetivo);
        if (error != null) {
            return error;
        }

        registrar(actual.getNombre() + " jugó " + carta.getNombre() + ":\n" + carta.obtenerInformacion());
        if (objetivo != null) {
            registrar("Objetivo: " + objetivo.getCarta().getNombre());
        }
        verificarVictoria();
        return null;
    }

    public String colocarEnBanco(Pokemon carta) {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        if (getJugadorQueDebeReemplazar() != null) {
            return "Primero se debe elegir un Pokémon activo de reemplazo.";
        }
        Jugador actual = getJugadorActual();
        String error = actual.colocarEnBanco(carta, turno);
        if (error != null) {
            return error;
        }
        registrar(actual.getNombre() + " colocó en el banco a " + carta.getNombre() + ".");
        return null;
    }

    public String retirar(int indiceBanco) {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        if (getJugadorQueDebeReemplazar() != null) {
            return "Primero se debe elegir un Pokémon activo de reemplazo.";
        }
        Jugador actual = getJugadorActual();
        PokemonEnJuego activoAnterior = actual.getCampo().getActivo();
        String nombreRetirado = "";
            if (activoAnterior != null) {
                nombreRetirado = activoAnterior.getCarta().getNombre();
            }
            
        String error = actual.retirarActivo(indiceBanco);
        if (error != null) {
            return error;
        }
        registrar(actual.getNombre() + " retiró a " + nombreRetirado + ". Ahora "
                + actual.getCampo().getActivo().getCarta().getNombre() + " es el Pokémon activo.");
        return null;
    }

    public String evolucionar(PokemonEvolucion carta, PokemonEnJuego objetivo) {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        if (getJugadorQueDebeReemplazar() != null) {
            return "Primero se debe elegir un Pokémon activo de reemplazo.";
        }
        if (carta == null || objetivo == null) {
            return "Debes elegir una carta de evolución y un Pokémon objetivo.";
        }
        Jugador actual = getJugadorActual();
        String nombreAntes = objetivo.getCarta().getNombre();

        String error = actual.evolucionar(carta, objetivo, turno);
        if (error != null) {
            return error;
        }
        registrar(actual.getNombre() + " evolucionó a " + nombreAntes + " en " + carta.getNombre() + ".");
        return null;
    }

    public String atacar(Ataque ataque) {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        if (getJugadorQueDebeReemplazar() != null) {
            return "Primero se debe elegir un Pokémon activo de reemplazo.";
        }
        if (turno == 1) {
            return "Nadie puede atacar en el turno 1.";
        }
        Jugador actual = getJugadorActual();
        Jugador rival = getRival();
        PokemonEnJuego atacante = actual.getCampo().getActivo();
        PokemonEnJuego defensor = rival.getCampo().getActivo();
        int psAntes = 0;
            if (defensor != null) {
                psAntes = defensor.getPsActuales();
        }
        

        String error = actual.atacar(rival, ataque);
        if (error != null) {
            return error;
        }

        int danoHecho = psAntes - defensor.getPsActuales();
        registrar(actual.getNombre() + " atacó: " + atacante.getCarta().getNombre() + " usó "
                + ataque.getNombre() + " y " + defensor.getCarta().getNombre() + " pierde " + danoHecho + " PS.");

        if (defensor.getCarta().esDebilA(atacante.getCarta())) {
            registrar("¡Es débil a ese tipo! (+" + Pokemon.BONO_DEBILIDAD + " de daño)");
        }
        if (defensor.estaDerrotado()) {
            registrar("¡" + defensor.getCarta().getNombre() + " fue derrotado! "
                    + actual.getNombre() + " gana " + defensor.getCarta().getPuntosPremio()
                    + " punto(s) de premio.");
        }

        verificarVictoria();
        return null;
    }

    public String terminarTurno() {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        if (getJugadorQueDebeReemplazar() != null) {
            return "Primero se debe elegir un Pokémon activo de reemplazo.";
        }

        Jugador anterior = getJugadorActual();
        registrar(anterior.getNombre() + " terminó su turno.");

        indiceJugadorActivo = 1 - indiceJugadorActivo;
        if (indiceJugadorActivo == indicePrimerJugador) {
            turno++;
        }

        Jugador siguiente = getJugadorActual();
        siguiente.iniciarTurno();
        if (!siguiente.robarCarta()) {
            ganador = getRival();
            motivoVictoria = siguiente.getNombre() + " no tiene cartas para robar.";
            registrar(motivoVictoria);
        } else {
            registrar("Turno " + turno + " - " + siguiente.getNombre() + ".");
        }
        return null;
    }

    public String rendirse() {
        if (ganador != null) {
            return "La partida ya terminó.";
        }
        Jugador actual = getJugadorActual();
        Jugador rival = getRival();
        ganador = rival;
        motivoVictoria = actual.getNombre() + " se rindió.";
        registrar(motivoVictoria);
        return null;
    }

    // --- Reemplazo tras KO ---

    public Jugador getJugadorQueDebeReemplazar() {
        for (Jugador j : jugadores) {
            if (j.necesitaReemplazo()) {
                return j;
            }
        }
        return null;
    }

    public String reemplazarActivo(int indiceBanco) {
        Jugador j = getJugadorQueDebeReemplazar();
        if (j == null) {
            return "Ningún jugador necesita reemplazar su Pokémon activo.";
        }
        if (!j.reemplazarActivo(indiceBanco)) {
            return "Debes elegir un Pokémon válido del banco.";
        }
        registrar(j.getNombre() + " puso a " + j.getCampo().getActivo().getCarta().getNombre()
                + " como Pokémon activo.");
        return null;
    }

    // --- Victoria ---

    public boolean verificarVictoria() {
        for (int i = 0; i < 2; i++) {
            Jugador j = jugadores[i];
            Jugador otro = jugadores[1 - i];
            if (j.getPuntosPremio() >= PUNTOS_PARA_GANAR) {
                ganador = j;
                motivoVictoria = j.getNombre() + " alcanzó " + PUNTOS_PARA_GANAR + " puntos de premio.";
                registrar(motivoVictoria);
                return true;
            }
            if (!otro.getCampo().tienePokemonEnJuego()) {
                ganador = j;
                motivoVictoria = otro.getNombre() + " se quedó sin Pokémon en juego.";
                registrar(motivoVictoria);
                return true;
            }
        }
        return false;
    }
}