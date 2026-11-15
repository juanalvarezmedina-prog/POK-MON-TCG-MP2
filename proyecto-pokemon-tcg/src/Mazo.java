import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mazo {
    private List<Carta> mazo;

    public Mazo() {
        this.mazo = new ArrayList<>();
    }

    public void agregar(Carta carta) {
        mazo.add(carta);
    }

    public void barajar() {

        Random azar = new Random();

        for (int i = mazo.size() - 1; i > 0; i--) {
            int j = azar.nextInt(i + 1);
            Carta temporal = mazo.get(i);
            mazo.set(i, mazo.get(j));
            mazo.set(j, temporal);
        }
    }

    // Roba la carta de arriba. Devuelve null si el mazo está vacío.
    public Carta robar() {
        if (mazo.isEmpty()) {
            return null;
        }
        return mazo.remove(0);
    }

    // Poké Ball: saca del mazo un Pokémon básico y lo devuelve.

    public Pokemon buscarBasico() {
        for (int i = 0; i < mazo.size(); i++) {
            if (mazo.get(i) instanceof Pokemon && ((Pokemon) mazo.get(i)).esBasico()) {
                return (Pokemon) mazo.remove(i);
            }
        }
        return null;
    }

    // Mulligan: la mano vuelve al mazo (después se baraja)

    public void devolverCartas(List<Carta> cartas) {
        mazo.addAll(cartas);
    }

    public boolean esValido() {
        if (mazo.size() != 20) {
            return false;
        }

        int basicos = 0;
        for (int i = 0; i < mazo.size(); i++) {
            int copias = 0;
            for (int j = 0; j < mazo.size(); j++) {
                if (mazo.get(i).getNombre().equals(mazo.get(j).getNombre())) {
                    copias++;
                }
            }
            if (copias > 2) {
                return false;
            }
            if (mazo.get(i) instanceof Pokemon && ((Pokemon) mazo.get(i)).esBasico()) {
                basicos++;
            }
        }
        return basicos >= 6;
    }

    public boolean estaVacio() {
        return mazo.isEmpty();
    }

    public int cantidadCartas() {
        return mazo.size();
    }
}