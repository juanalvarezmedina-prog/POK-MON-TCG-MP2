import java.util.List;

public interface Activable {
    boolean activar(Jugador jugador, Jugador rival);

    default boolean activar(Jugador jugador, Jugador rival, PokemonEnJuego objetivo) {
        return activar(jugador, rival);
    }
    
    default List<PokemonEnJuego> objetivosPosibles(Jugador jugador) {
        return List.of();
    }
}

