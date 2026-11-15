public class PokemonEvolucion extends Pokemon {

    private Etapa etapa;
    private String evolucionaDe;

    public PokemonEvolucion(String nombre, TipoEnergia tipo, int psMaximos, int costoRetirada,
                            Etapa etapa, String evolucionaDe) {
        super(nombre, tipo, psMaximos, costoRetirada);
        this.etapa = etapa;
        this.evolucionaDe = evolucionaDe;
    }

    @Override
    public int getPuntosPremio() {
        return 1;
    }

    @Override
    public boolean esBasico() {
        return false;
    }

    public Etapa getEtapa() {
        return etapa;
    }

    public String getEvolucionaDe() {
        return evolucionaDe;
    }
}