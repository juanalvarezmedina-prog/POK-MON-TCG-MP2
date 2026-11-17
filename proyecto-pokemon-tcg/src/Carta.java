import java.util.Random;

public abstract class Carta {
    private String nombre;
    private String rutaImagen = "media/default.jpeg";

    public Carta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // juantails: con esto ya podemos poner imagenes al azar :D
    public void asignarImagenAleatoria(String... rutas) {
        if (rutas != null && rutas.length > 0) {
            int indice = new Random().nextInt(rutas.length);
            this.rutaImagen = rutas[indice];
        }
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public abstract String obtenerInformacion();
}