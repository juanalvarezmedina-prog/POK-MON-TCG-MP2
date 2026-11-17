import java.util.ArrayList;
import java.util.List;

public class CartaFactory {

    public static Carta crearCarta(String nombre) {
        switch (nombre.toLowerCase()) {
            case "pikachu":
                PokemonBasico p = new PokemonBasico("Pikachu", TipoEnergia.Electrico, 60, 1);
                p.agregarAtaque(new Ataque("Mordisco", TipoEnergia.Electrico, (byte) 1, (short) 20));
                p.asignarImagenAleatoria("Pikachu.png", "Pikachu2.png");
                return p;
            case "charmander":
                PokemonBasico c = new PokemonBasico("Charmander", TipoEnergia.Fuego, 60, 1);
                c.agregarAtaque(new Ataque("Ascuas", TipoEnergia.Fuego, (byte) 1, (short) 30));
                c.asignarImagenAleatoria("Charmander.png", "Charmander2.png");
                return c;
            case "squirtle":
                PokemonBasico s = new PokemonBasico("Squirtle", TipoEnergia.Agua, 60, 1);
                s.agregarAtaque(new Ataque("Placaje", TipoEnergia.Agua, (byte) 1, (short) 20));
                s.asignarImagenAleatoria("Squirtle.png", "Squirtle2.png");
                return s;
            case "jigglypuff":
                PokemonBasico j = new PokemonBasico("Jigglypuff", TipoEnergia.Incoloro, 70, 1);
                j.agregarAtaque(new Ataque("Golpe", TipoEnergia.Incoloro, (byte) 1, (short) 20));
                j.asignarImagenAleatoria("Jigglypuff.png", "Jigglypuff2.png");
                return j;
            case "magikarp":
                PokemonBasico m = new PokemonBasico("Magikarp", TipoEnergia.Agua, 30, 1);
                m.agregarAtaque(new Ataque("Chapoteo", TipoEnergia.Agua, (byte) 1, (short) 10));
                m.asignarImagenAleatoria("Magikarp.png", "Magikarp2.png");
                return m;
            case "snorlax":
                PokemonBasico s2 = new PokemonBasico("Snorlax", TipoEnergia.Incoloro, 150, 4);
                s2.agregarAtaque(new Ataque("Golpe Cuerpo", TipoEnergia.Incoloro, (byte) 3, (short) 100));
                s2.asignarImagenAleatoria("Snorlax.png", "Snorlax2.png");
                return s2;
            case "mewtwo ex":
                PokemonEx m2 = new PokemonEx("Mewtwo EX", TipoEnergia.Psiquico, 150, 1);
                m2.agregarAtaque(new Ataque("Ataque Psíquico", TipoEnergia.Psiquico, (byte) 2, (short) 50));
                m2.agregarAtaque(new Ataque("Psicorrayo", TipoEnergia.Psiquico, (byte) 3, (short) 150));
                m2.asignarImagenAleatoria("Mewtwo.png", "Mewtwo2.png");
                return m2;
            case "zapdos ex":
                PokemonEx z = new PokemonEx("Zapdos EX", TipoEnergia.Electrico, 130, 1);
                z.agregarAtaque(new Ataque("Picotazo", TipoEnergia.Incoloro, (byte) 1, (short) 30));
                z.agregarAtaque(new Ataque("Tormenta Eléctrica", TipoEnergia.Electrico, (byte) 3, (short) 100));
                z.asignarImagenAleatoria("Zapdos.png", "Zapdos2.png");
                return z;
            case "moltres ex":
                PokemonEx m3 = new PokemonEx("Moltres EX", TipoEnergia.Fuego, 130, 2);
                m3.agregarAtaque(new Ataque("Aleteo", TipoEnergia.Incoloro, (byte) 1, (short) 30));
                m3.agregarAtaque(new Ataque("Danza Infernal", TipoEnergia.Fuego, (byte) 3, (short) 70));
                m3.asignarImagenAleatoria("Moltres.png", "Moltres2.png");
                return m3;
            case "articuno ex":
                PokemonEx a = new PokemonEx("Articuno EX", TipoEnergia.Agua, 130, 2);
                a.agregarAtaque(new Ataque("Viento Helado", TipoEnergia.Agua, (byte) 1, (short) 30));
                a.agregarAtaque(new Ataque("Ventisca", TipoEnergia.Agua, (byte) 3, (short) 100));
                a.asignarImagenAleatoria("Articuno.png", "Articuno2.png");
                return a;
            case "raichu":
                PokemonEvolucion r = new PokemonEvolucion("Raichu", TipoEnergia.Electrico, 100, 1,
                        Etapa.FASE1, "Pikachu");
                r.agregarAtaque(new Ataque("Trueno", TipoEnergia.Electrico, (byte) 2, (short) 60));
                r.asignarImagenAleatoria("Raichu.png", "Raichu2.png");
                return r;
            case "charmeleon":
                PokemonEvolucion cm = new PokemonEvolucion("Charmeleon", TipoEnergia.Fuego, 90, 2,
                        Etapa.FASE1, "Charmander");
                cm.agregarAtaque(new Ataque("Garra Ígnea", TipoEnergia.Fuego, (byte) 2, (short) 50));
                cm.asignarImagenAleatoria("Charmeleon.png", "Charmeleon2.png");
                return cm;
            case "charizard":
                PokemonEvolucion cz = new PokemonEvolucion("Charizard", TipoEnergia.Fuego, 150, 2,
                        Etapa.FASE2, "Charmeleon");
                cz.agregarAtaque(new Ataque("Giro Fuego", TipoEnergia.Fuego, (byte) 4, (short) 120));
                cz.asignarImagenAleatoria("Charizard.png", "Charizard2.png");
                return cz;
            case "wartortle":
                PokemonEvolucion w = new PokemonEvolucion("Wartortle", TipoEnergia.Agua, 80, 1,
                        Etapa.FASE1, "Squirtle");
                w.agregarAtaque(new Ataque("Salpicadura", TipoEnergia.Agua, (byte) 2, (short) 40));
                w.asignarImagenAleatoria("Wartortle.png", "Wartortle2.png");
                return w;
            case "blastoise":
                PokemonEvolucion b = new PokemonEvolucion("Blastoise", TipoEnergia.Agua, 150, 3,
                        Etapa.FASE2, "Wartortle");
                b.agregarAtaque(new Ataque("Hidrobomba", TipoEnergia.Agua, (byte) 3, (short) 100));
                b.asignarImagenAleatoria("Blastoise.png", "Blastoise2.png");
                return b;
            case "wigglytuff":
                PokemonEvolucion wf = new PokemonEvolucion("Wigglytuff", TipoEnergia.Incoloro, 110, 2,
                        Etapa.FASE1, "Jigglypuff");
                wf.agregarAtaque(new Ataque("Bofetón", TipoEnergia.Incoloro, (byte) 2, (short) 50));
                wf.asignarImagenAleatoria("Wigglytuff.png", "Wigglytuff2.png");
                return wf;
            case "gyarados":
                PokemonEvolucion g = new PokemonEvolucion("Gyarados", TipoEnergia.Agua, 150, 4,
                        Etapa.FASE1, "Magikarp");
                g.agregarAtaque(new Ataque("Hiperrayo", TipoEnergia.Agua, (byte) 4, (short) 100));
                g.asignarImagenAleatoria("Gyarados.png", "Gyarados2.png");
                return g;
///////////////////////////////////////////////////////////////////////////////////

            case "investigacion del profesor":
                return new Entrenador(
                    "Investigación del Profesor",
                    true,
                    "Descarta tu mano actual y roba 2 cartas de tu mazo.",
                    new Activable() {
                        @Override
                        public boolean activar(Jugador jugador, Jugador rival) {
                            jugador.descartarMano();
                            jugador.robarCarta();
                            jugador.robarCarta();
                            return true;
                        }
                    }
                );

            case "pocion":
                return new Entrenador(
                    "Poción",
                    false,
                    "Cura 20 PS al Pokémon Activo de tu lado del campo.",
                    new Activable() {
                        @Override
                        public boolean activar(Jugador jugador, Jugador rival) {
                            PokemonEnJuego activo = jugador.getCampo().getActivo();
                            if (activo == null) {
                                return false;
                            }
                            activo.curar(20);
                            return true;
                        }
                    }
                );

            case "turbo velocidad":
                return new Entrenador(
                    "Turbo Velocidad",
                    false,
                    "Reduce en 1 el costo de retirada del Pokémon Activo este turno.",
                    new Activable() {
                        @Override
                        public boolean activar(Jugador jugador, Jugador rival) {
                            PokemonEnJuego activo = jugador.getCampo().getActivo();
                            if (activo == null) {
                                return false;
                            }
                            activo.aplicarTurbo();
                            return true;
                        }
                    }
                );

            case "pokeball":
                return new Entrenador(
                    "Poké Ball",
                    false,
                    "Busca un Pokémon Básico en tu mazo y añádelo a tu mano.",
                    new Activable() {
                        @Override
                        public boolean activar(Jugador jugador, Jugador rival) {
                            Pokemon basico = jugador.getMazo().buscarBasico();
                            if (basico == null) {
                                return false;
                            }
                            jugador.agregarAMano(basico);
                            jugador.getMazo().barajar();
                            return true;
                        }
                    }
                );

            case "misty":
                return new Entrenador(
                    "Misty",
                    true,
                    "Adjunta 3 Energías de Agua a un Pokémon de Agua en tu campo.",
                    new Activable() {
                        @Override
                        public List<PokemonEnJuego> objetivosPosibles(Jugador jugador) {
                            List<PokemonEnJuego> objetivos = new ArrayList<>();
                            Campo campo = jugador.getCampo();
                            if (campo.getActivo() != null && campo.getActivo().getCarta().getTipo() == TipoEnergia.Agua) {
                                objetivos.add(campo.getActivo());
                            }
                            for (PokemonEnJuego p : campo.getBanco()) {
                                if (p.getCarta().getTipo() == TipoEnergia.Agua) {
                                    objetivos.add(p);
                                }
                            }
                            return objetivos;
                        }

                        @Override
                        public boolean activar(Jugador jugador, Jugador rival) {
                            List<PokemonEnJuego> objetivos = objetivosPosibles(jugador);
                            if (objetivos.isEmpty()) {
                                return false;
                            }
                            return activar(jugador, rival, objetivos.get(0));
                        }

                        @Override
                        public boolean activar(Jugador jugador, Jugador rival, PokemonEnJuego objetivo) {
                            if (objetivo == null || !objetivosPosibles(jugador).contains(objetivo)) {
                                return false;
                            }
                            for (int i = 0; i < 3; i++) {
                                objetivo.adjuntarEnergia(TipoEnergia.Agua);
                            }
                            return true;
                        }
                    }
                );
            
            default:
                System.out.println("Carta no reconocida: " + nombre);
                return null;
        }
    }
    public static  List<String> nombresDisponibles() {
        return List.of(
                "Pikachu",
                "Charmander",
                "Squirtle",
                "Jigglypuff",
                "Magikarp",
                "Snorlax",
                "Mewtwo EX",
                "Zapdos EX",
                "Moltres EX",
                "Articuno EX",
                "Raichu",
                "Charmeleon",
                "Charizard",
                "Wartortle",
                "Blastoise",
                "Wigglytuff",
                "Gyarados",
                "Investigacion del Profesor",
                "Pocion",
                "Pokeball",
                "Turbo Velocidad",
                "Misty"
        );
    }
    public static Mazo crearMazo(List<String> nombres) {
        Mazo mazo = new Mazo();
        for (String nombre : nombres) {
            mazo.agregar(crearCarta(nombre));
        }
        return mazo;
    }

    public static Mazo mazoFuego() {
        return crearMazo(List.of(
            "Charmander", "Charmander", "Charmeleon", "Charmeleon",
            "Charizard", "Charizard", "Moltres EX", "Moltres EX",
            "Jigglypuff", "Jigglypuff", "Wigglytuff", "Snorlax", "Snorlax",
            "Investigacion del Profesor", "Investigacion del Profesor",
            "Pocion", "Pocion", "Pokeball", "Pokeball",
            "Turbo Velocidad"));
    }

    public static Mazo mazoAgua() {
        return crearMazo(List.of(
            "Squirtle", "Squirtle", "Wartortle", "Wartortle",
            "Blastoise", "Blastoise", "Magikarp", "Magikarp", "Gyarados",
            "Articuno EX", "Articuno EX", "Snorlax",
            "Misty", "Misty", "Pokeball", "Pokeball",
            "Investigacion del Profesor", "Investigacion del Profesor",
            "Pocion", "Turbo Velocidad"));
    }

    public static Mazo mazoElectrico() {
        return crearMazo(List.of(
            "Pikachu", "Pikachu", "Raichu", "Raichu",
            "Zapdos EX", "Zapdos EX", "Jigglypuff", "Jigglypuff",
            "Wigglytuff", "Wigglytuff", "Snorlax", "Snorlax",
            "Investigacion del Profesor", "Investigacion del Profesor",
            "Pocion", "Pocion", "Pokeball", "Pokeball",
            "Turbo Velocidad", "Turbo Velocidad"));
    }
    
    public static Mazo mazoPsiquico() {
        return crearMazo(List.of(
            "Mewtwo EX", "Mewtwo EX",
            "Jigglypuff", "Jigglypuff", "Wigglytuff", "Wigglytuff",
            "Snorlax", "Snorlax",
            "Zapdos EX", "Zapdos EX", "Moltres EX", "Moltres EX",
            "Investigacion del Profesor", "Investigacion del Profesor",
            "Pocion", "Pocion", "Pokeball", "Pokeball",
            "Turbo Velocidad", "Turbo Velocidad"));
    }

public static List<String> nombresMazos() {
        return List.of("Fuego", "Agua", "Eléctrico", "Psíquico");
    }

    public static Jugador crearJugador(String nombreJugador, String nombreMazo) {
        if (nombreMazo == null) {
            return new Jugador(nombreJugador, mazoPsiquico(), TipoEnergia.Psiquico);
        }
        return switch (nombreMazo) {
            case "Fuego" -> new Jugador(nombreJugador, mazoFuego(), TipoEnergia.Fuego);
            case "Agua" -> new Jugador(nombreJugador, mazoAgua(), TipoEnergia.Agua);
            case "Eléctrico" -> new Jugador(nombreJugador, mazoElectrico(), TipoEnergia.Electrico);
            default -> new Jugador(nombreJugador, mazoPsiquico(), TipoEnergia.Psiquico);
        };
    }
}