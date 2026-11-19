import java.util.Random;

public abstract class Carta {
    private static final String CARPETA_IMAGENES = "media/";

    private String nombre;
    private String rutaImagen = CARPETA_IMAGENES + "default.jpeg"; // en caso que no exista la imagen

    public Carta(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void asignarImagenAleatoria(String... rutas) {
        
        int indice = new Random().nextInt(rutas.length);
        if (rutas[indice] != null && !rutas[indice].isBlank()) {
            this.rutaImagen = CARPETA_IMAGENES + rutas[indice];
        }
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public abstract String obtenerInformacion();
}