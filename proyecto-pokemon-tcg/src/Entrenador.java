import java.util.List;

public class Entrenador extends Carta implements Activable {
    private final boolean soporte;
    private final String descripcion;
    private final Activable estrategia;

    public Entrenador(String nombre, boolean soporte, String descripcion, Activable estrategia) {
        super(nombre);
        this.soporte = soporte;
        this.descripcion = descripcion;
        this.estrategia = estrategia;
    }

    public boolean esSoporte() {
        return soporte;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public boolean activar(Jugador jugador, Jugador rival) {
        return estrategia != null && estrategia.activar(jugador, rival);
    }

    public List<PokemonEnJuego> objetivosPosibles(Jugador jugador) {
        return estrategia != null ? estrategia.objetivosPosibles(jugador) : List.of();
    }

    public boolean activar(Jugador jugador, Jugador rival, PokemonEnJuego objetivo) {
        return estrategia != null && estrategia.activar(jugador, rival, objetivo);
    }

    @Override
    public String obtenerInformacion() {
        return "Nombre: " + getNombre() + "\nEfecto: " + descripcion;
    }
}