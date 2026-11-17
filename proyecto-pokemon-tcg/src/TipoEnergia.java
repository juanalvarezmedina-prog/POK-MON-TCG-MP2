public enum TipoEnergia {
    Fuego,
    Agua,
    Electrico,
    Psiquico,
    Planta,
    Lucha,
    Metal,
    Siniestro,
    Incoloro;

    public boolean esDebilA(TipoEnergia atacante) {
        switch (this) {
            case Agua:
                return atacante == Fuego || atacante == Electrico;
            case Fuego:
                return atacante == Agua;
            case Electrico:
                return atacante == Lucha;
            case Psiquico:
                return atacante == Siniestro;
            case Planta:
                return atacante == Fuego;
            case Lucha:
                return atacante == Psiquico || atacante == Planta;
            case Metal:
                return atacante == Fuego;
            case Siniestro:
                return atacante == Lucha || atacante == Planta;
            case Incoloro:
                return atacante == Psiquico || atacante == Lucha;
            default:
                return false;
        }
    }
}
