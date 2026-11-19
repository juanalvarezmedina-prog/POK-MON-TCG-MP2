import java.util.ArrayList;
import java.util.List;

public abstract class Pokemon extends Carta {

    private TipoEnergia tipo;
    private int psMaximos;
    private int costoRetirada;
    private List<Ataque> ataques;

    public Pokemon(String nombre, TipoEnergia tipo, int psMaximos, int costoRetirada) {
        super(nombre);
        this.tipo = tipo;
        this.psMaximos = psMaximos;
        this.costoRetirada = costoRetirada;
        this.ataques = new ArrayList<>();
    }

    public List<Ataque> getAtaques() {
        return new ArrayList<>(ataques);
    }

    protected void agregarAtaque(Ataque ataque) {
        ataques.add(ataque);
    }

    public TipoEnergia getTipo() {
        return tipo;
    }

    public int getPsMaximos() {
        return psMaximos;
    }

    public int getCostoRetirada() {
        return costoRetirada;
    }

    public boolean esBasico() {
        return true;
    }
    
    public static final int BONO_DEBILIDAD = 20;

    public boolean esDebilA(Pokemon atacante) {
    return tipo.esDebilA(atacante.getTipo());
    }

    public abstract int getPuntosPremio();

    @Override
    public String obtenerInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(getNombre()).append("\n");
        sb.append("Tipo: ").append(getTipo()).append("\n");
        sb.append("PS Máximos: ").append(getPsMaximos()).append("\n");
        sb.append("Costo de Retirada: ").append(getCostoRetirada());
        for (int i = 0; i < ataques.size(); i++) {
            Ataque ataque = ataques.get(i);
            sb.append("\nAtaque ").append(i + 1).append(": ").append(ataque.getNombre())
            .append(", Tipo: ").append(ataque.getTipoEnergia())
            .append(", Costo: ").append(ataque.getCosto())
            .append(", Daño: ").append(ataque.getDano());
        }
        return sb.toString();
    }
}