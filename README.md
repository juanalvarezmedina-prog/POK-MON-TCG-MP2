# POKEMON-TCG-MP2
Implementación gráfica e interactiva del juego de cartas coleccionables **Pokémon TCG**, desarrollada como evolución del **Mini Proyecto 1 (MP1)** para la asignatura de **Programación Orientada a Eventos**. 

El diseño visual e interactivo está inspirado en la estética y experiencia de usuario de **Pokémon TCG Pocket**, llevando la lógica base del proyecto anterior a una interfaz gráfica dinámica.

## Características Principales

* **Interfaz Gráfica Inspirada en Pokémon TCG Pocket:** Diseño visual enriquecido con recursos gráficos para ofrecer una experiencia intuitiva y atractiva.
* **Registro y Personalización:** Pantalla inicial que permite el registro de los nombres de ambos jugadores y la selección entre **mazos predeterminados** o **mazos personalizados**.
* **Sistema de Evolución:** Implementación de mecánicas de evolución para ciertos Pokémon dentro de la partida (con arquitectura escalable proyectada para añadir más especies y líneas evolutivas en futuras versiones).
* **Tablero Dinámico por Turnos:** Al iniciar el combate, la interfaz cambia automáticamente su orientación en cada turno para adaptarse a la perspectiva del jugador activo.
* **Panel de Comandos (Historial):** Registro visual en tiempo real que funciona como guía de todos los movimientos, ataques y acciones ejecutadas durante cada turno.

### Decisiones de Diseño Adicionales
Como aporte propio del equipo fuera de los requerimientos base del MP2, se integraron dos mecánicas clave para mejorar la jugabilidad:
1. **Lanzamiento de Moneda Inicial:** Al comenzar una partida, se ejecuta un lanzamiento de moneda aleatorio para determinar qué jugador toma el primer turno.
2. **Botón de Rendición:** Opción disponible durante el combate para que un jugador pueda conceder la partida de forma inmediata.

## Requisitos del Sistema

* **Java Development Kit (JDK):** Versión **21** o superior.
* **Estructura de Archivos:** El proyecto utiliza **rutas relativas** para la carga de imágenes y recursos visuales.

---

## Instalación y Ejecución

1. **Descarga el proyecto:** Clona este repositorio o descarga el archivo `.zip` completo asegurando que la carpeta del código fuente y la carpeta de imágenes permanezcan juntas en el mismo directorio raíz.
   ```bash
   git clone <https://github.com/juanalvarezmedina-prog/POKEMON-TCG-MP2.git>
