import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PokemonEnJuego {
    private Pokemon carta;
    private int psActuales;
    private List<TipoEnergia> energias;
    private int reduccionRetirada;
    private int turnoEntrada;            // NUEVO: turno en que entró al campo
    private int ultimoTurnoEvolucion;    // NUEVO: 0 = nunca ha evolucionado

    public PokemonEnJuego(Pokemon carta, int turnoActual) {
        this.carta = carta;
        this.psActuales = carta.getPsMaximos();
        this.energias = new ArrayList<>();
        this.reduccionRetirada = 0;
        this.turnoEntrada = turnoActual;
        this.ultimoTurnoEvolucion = 0;
    }

    public Pokemon getCarta() {
        return carta;
    }

    public int getPsActuales() {
        return psActuales;
    }

    // para agregar o quitar energías hay que usar los métodos de abajo
    public List<TipoEnergia> getEnergias() {
        return new ArrayList<>(energias);
    }

    // NUEVO
    public int getTurnoEntrada() {
        return turnoEntrada;
    }

    // NUEVO
    public int getUltimoTurnoEvolucion() {
        return ultimoTurnoEvolucion;
    }

    public void recibirDano(int cantidad) {
        psActuales = Math.max(0, psActuales - cantidad);
    }

    // No puede pasarse de los PS máximos de la carta
    public void curar(int cantidad) {
        psActuales = Math.min(carta.getPsMaximos(), psActuales + cantidad);
    }

    public void adjuntarEnergia(TipoEnergia tipo) {
        energias.add(tipo);
    }

    // Para pagar la retirada
    public void descartarEnergias(int cantidad) {
        int aDescartar = Math.min(cantidad, energias.size());
        for (int i = 0; i < aDescartar; i++) {
            energias.remove(0);
        }
    }
    
    public boolean estaDerrotado() {
        return psActuales <= 0;
    }

    // Costo de la carta menos lo que reduzca Turbo Velocidad
    public int getCostoRetiradaActual() {
        return Math.max(0, carta.getCostoRetirada() - reduccionRetirada);
    }

    public void aplicarTurbo() {
        reduccionRetirada++;
    }

    // porque Turbo Velocidad solo dura ese turno
    public void reiniciarEfectosTurno() {
        reduccionRetirada = 0;
    }

    // NUEVO: la evolución conserva el daño recibido y las energías
    public void evolucionar(PokemonEvolucion cartaNueva, int turnoActual) {
        int danoAcumulado = carta.getPsMaximos() - psActuales;
        carta = cartaNueva;
        psActuales = cartaNueva.getPsMaximos() - danoAcumulado;
        ultimoTurnoEvolucion = turnoActual;
    }
}