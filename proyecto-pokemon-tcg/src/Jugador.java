import java.util.ArrayList;
import java.util.List;

public class Jugador {
    private static final int ManoInicial = 5;

    private String nombre;
    private Mazo mazo;
    private List<Carta> mano;
    private Campo campo;
    private int puntosPremio;
    private TipoEnergia tipoZona;
    private TipoEnergia energiaDisponible;
    private boolean yaUsoSoporte;
    private boolean yaAtaco;

    public Jugador(String nombre, Mazo mazo, TipoEnergia tipoZona) {
        this.nombre = nombre;
        this.mazo = mazo;
        this.tipoZona = tipoZona;
        this.mano = new ArrayList<>();
        this.campo = new Campo();
        this.puntosPremio = 0;
        this.energiaDisponible = null;
        this.yaUsoSoporte = false;
        this.yaAtaco = false;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Carta> getMano() {
        return new ArrayList<>(mano);
    }

    public void agregarAMano(Carta carta) {
        mano.add(carta);
    }

    public void descartarMano() {
        mano.clear();
    }

    public Campo getCampo() {
        return campo;
    }

    public Mazo getMazo() {
        return mazo;
    }

    public int getPuntosPremio() {
        return puntosPremio;
    }

    public TipoEnergia getTipoZona() {
        return tipoZona;
    }

    public TipoEnergia getEnergiaDisponible() {
        return energiaDisponible;
    }

    // Reinicia lo que cambia cada turno y genera la energía de la zona
    public void iniciarTurno() {
        yaUsoSoporte = false;
        yaAtaco = false;
        energiaDisponible = tipoZona;

        if (campo.getActivo() != null) {
            campo.getActivo().reiniciarEfectosTurno();
        }
        for (PokemonEnJuego p : campo.getBanco()) {
            p.reiniciarEfectosTurno();
        }
    }

    // false si el mazo está vacío (Partida decide que pierde)
    public boolean robarCarta() {
        if (mazo.estaVacio()) {
            return false;
        }
        mano.add(mazo.robar());
        return true;
    }

    public String adjuntarEnergiaDeZona(PokemonEnJuego destino) {
        if (energiaDisponible == null) {
            return "Ya usaste la energía de zona de este turno.";
        }
        if (destino == null) {
            return "Debes elegir un Pokémon destino.";
        }
        boolean esPropio = destino == campo.getActivo() || campo.getBanco().contains(destino);
        if (!esPropio) {
            return "Solo puedes adjuntar energía a uno de tus Pokémon.";
        }
        destino.adjuntarEnergia(energiaDisponible);
        energiaDisponible = null;
        return null;
    }

    public String jugarEntrenador(Entrenador c, Jugador rival) {
        return jugarEntrenador(c, rival, null);
    }

    public boolean puedeJugarEntrenador(Entrenador c) {
        return mano.contains(c) && !(c.esSoporte() && yaUsoSoporte);
    }

    // objetivo: Pokemon elegido por el jugador (null si la carta no lo necesita)
    public String jugarEntrenador(Entrenador c, Jugador rival, PokemonEnJuego objetivo) {
        if (c == null || !mano.contains(c)) {
            return "Esa carta no está en tu mano.";
        }
        if (c.esSoporte() && yaUsoSoporte) {
            return "Ya jugaste una carta de Soporte en este turno.";
        }

        boolean seJugo = c.activar(this, rival, objetivo);
        if (!seJugo) {
            return "No se cumplen las condiciones para jugar " + c.getNombre() + ".";
        }

        if (c.esSoporte()) {
            yaUsoSoporte = true;
        }
        mano.remove(c);
        return null;
    }

    public String colocarEnBanco(Pokemon c, int turnoActual) {
        if (c == null || !mano.contains(c)) {
            return "Esa carta no está en tu mano.";
        }
        if (!c.esBasico()) {
            return c.getNombre() + " no es un Pokémon básico.";
        }
        if (!campo.hayEspacioEnBanco()) {
            return "Tu banco está lleno.";
        }
        campo.colocarEnBanco(new PokemonEnJuego(c, turnoActual));
        mano.remove(c);
        return null;
    }

    // Devuelve null si la evolución se hizo; si no, devuelve el motivo por el que falló.
    public String evolucionar(PokemonEvolucion carta, PokemonEnJuego objetivo, int turnoActual) {
        if (turnoActual == 1) {
            return "No puedes evolucionar en el turno 1.";
        }
        if (!mano.contains(carta)) {
            return "Esa carta no está en tu mano.";
        }
        boolean esPropio = objetivo != null
                && (objetivo == campo.getActivo() || campo.getBanco().contains(objetivo));
        if (!esPropio) {
            return "Debes elegir uno de tus Pokémon en juego.";
        }
        if (!carta.getEvolucionaDe().equals(objetivo.getCarta().getNombre())) {
            return carta.getNombre() + " solo evoluciona de " + carta.getEvolucionaDe() + ".";
        }
        if (objetivo.getTurnoEntrada() == turnoActual) {
            return "No puedes evolucionar un Pokémon que entró en juego este turno.";
        }
        if (objetivo.getUltimoTurnoEvolucion() == turnoActual) {
            return "Ese Pokémon ya evolucionó este turno.";
        }

        objetivo.evolucionar(carta, turnoActual);
        mano.remove(carta);
        return null;
    }

    public String retirarActivo(int indiceBanco) {
        PokemonEnJuego activo = campo.getActivo();
        if (activo == null) {
            return "No tienes un Pokémon activo para retirar.";
        }
        if (indiceBanco < 0 || indiceBanco >= campo.getBanco().size()) {
            return "Debes elegir un Pokémon válido del banco.";
        }
        int costo = activo.getCostoRetiradaActual();
        if (activo.getEnergias().size() < costo) {
            return activo.getCarta().getNombre() + " necesita " + costo + " energía(s) para retirarse.";
        }
        activo.descartarEnergias(costo);
        campo.intercambiarActivo(indiceBanco);
        return null;
    }

    public String atacar(Jugador rival, Ataque ataque) {
        PokemonEnJuego atacante = campo.getActivo();
        PokemonEnJuego defensor = rival.getCampo().getActivo();

        if (yaAtaco) {
            return "Ya atacaste en este turno.";
        }
        if (atacante == null || defensor == null) {
            return "Debe haber un Pokémon activo en ambos lados para atacar.";
        }
        if (ataque == null || !atacante.getCarta().getAtaques().contains(ataque)) {
            return atacante.getCarta().getNombre() + " no conoce ese ataque.";
        }
        if (!ataque.puedeUsarse(atacante.getEnergias())) {
            return atacante.getCarta().getNombre() + " no tiene la energía necesaria para " + ataque.getNombre() + ".";
        }

        int dano = ataque.getDano();
        if (defensor.getCarta().getTipo().esDebilA(atacante.getCarta().getTipo())) {
            dano += 20;
        }
        defensor.recibirDano(dano);
        yaAtaco = true;

        if (defensor.estaDerrotado()) {
            sumarPuntos(defensor.getCarta().getPuntosPremio());
            rival.getCampo().eliminarActivo();
        }
        return null;
    }

    public void sumarPuntos(int n) {
        puntosPremio += n;
    }

    public boolean tieneBasicoEnMano() {
        for (Carta c : mano) {
            if (c instanceof Pokemon && ((Pokemon) c).esBasico()) {
                return true;
            }
        }
        return false;
    }

    // Mulligan
    public void hacerMulligan() {
        mazo.devolverCartas(mano);
        mano.clear();
        mazo.barajar();
        for (int i = 0; i < ManoInicial; i++) {
            robarCarta();
        }
    }

    public boolean colocarActivoDesdeMano(Pokemon c, int turnoActual) {
        if (!mano.contains(c) || !c.esBasico() || campo.getActivo() != null) {
            return false;
        }
        campo.setActivo(new PokemonEnJuego(c, turnoActual));
        mano.remove(c);
        return true;
    }

    public boolean necesitaReemplazo() {
        return campo.getActivo() == null && campo.tienePokemonEnJuego();
    }

    public boolean reemplazarActivo(int indiceBanco) {
        if (campo.getActivo() != null) {
            return false;
        }
        if (indiceBanco < 0 || indiceBanco >= campo.getBanco().size()) {
            return false;
        }
        campo.promoverDelBanco(indiceBanco);
        return true;
    }
}