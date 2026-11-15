import java.util.ArrayList;
import java.util.List;

public class Campo {
    private static final int MAX_BANCO = 2;

    private PokemonEnJuego activo;
    private List<PokemonEnJuego> banco;

    public Campo() {
        this.activo = null;
        this.banco = new ArrayList<>();
    }

    public PokemonEnJuego getActivo() {
        return activo;
    }

    
    public List<PokemonEnJuego> getBanco() {
        return new ArrayList<>(banco); 
    }

    public boolean setActivo(PokemonEnJuego p) {
        if (p == null || activo != null|| banco.contains(p)) {
            return false;
        }
        activo = p;
        return true;
    }

    public boolean hayEspacioEnBanco() {
        return banco.size() < MAX_BANCO;
    }

    public boolean colocarEnBanco(PokemonEnJuego p) {
        if (!hayEspacioEnBanco()) {
            return false;
        }
        banco.add(p);
        return true;
    }

    // el del banco pasa a ser el nuevo activo
    public void promoverDelBanco(int indice) {  
        this.activo = banco.remove(indice);
    }

    // Retirada
    public boolean intercambiarActivo(int indice) {
        if (activo == null || !indiceValido(indice)) {
            return false;
        }
        PokemonEnJuego nuevoActivo = banco.get(indice);
        banco.set(indice, activo);
        activo = nuevoActivo;
        return true;
    }

    public void eliminarActivo() {
        this.activo = null;
    }

    public boolean tienePokemonEnJuego() {
        return activo != null || !banco.isEmpty();
    }

    private boolean indiceValido(int indice) {
        return indice >= 0 && indice < banco.size();
    }
}
