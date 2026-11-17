import javax.swing.*;
import java.awt.Color;


public class InterfazPelea {    

  private Jugador jugador;
  private Jugador rival;

  //botones de pokemones

     JButton activo= Estilo.crearCarta("Pokemon Activo", 100, 200);
    

    JButton banco1= Estilo.crearCarta("Banco 1", 100, 200);

    JButton banco2= Estilo.crearCarta("Banco 2", 100, 200);
    
    JButton activoRival= Estilo.crearCarta("Pokemon Rival Activo", 100, 200);

    JButton banco1Rival= Estilo.crearCarta("Banco 1 Rival", 100, 200);
    
    JButton banco2Rival= Estilo.crearCarta("Banco 2 Rival", 100, 200);
      
    // botones de acciones

    JButton adjuntarEnergia= Estilo.crearBotonAccion("Adjuntar Energia", 100, 50);
    
    JButton atacar= Estilo.crearBotonAccion("Atacar", 100, 50);

    JButton colocarPokemonActivo= Estilo.crearBotonAccion("Colocar Pokemon Activo", 100, 50);

    JButton colocarPokemonEnBanco= Estilo.crearBotonAccion("Retirar Pokemon", 100, 50);
    
    JButton usarObjeto= Estilo.crearBotonAccion("Usar Objeto", 100, 50);
    
    JButton Evolucionar= Estilo.crearBotonAccion("Evolucionar Pokemon", 100, 50);
    
    JButton pasarTurno= Estilo.crearBotonAccion("Pasar Turno", 100, 50);

    // texto
    JLabel turno = Estilo.crearTexto("Turno del jugador ", 100, 50);
    
    //Rival

    JLabel nombreRival = Estilo.crearTexto("Nombre del rival:", 100, 50);
    
    JLabel puntosJugadorRival = Estilo.crearTexto("Puntos del rival: 0", 100, 50);
    
    JLabel mazoRival = Estilo.crearTexto("Mazo del rival: 0", 100, 50);
    
    JLabel manoRival = Estilo.crearTexto("Mano del rival: 0", 100, 50);

    //Jugador

    JLabel nombreJugador = Estilo.crearTexto("Nombre del jugador:", 100, 50);
    
    JLabel puntosJugador = Estilo.crearTexto("Puntos del jugador: 0", 100, 50);
    
    JLabel mazoJugador = Estilo.crearTexto("Mazo del jugador: 0", 100, 50);
    
    JLabel manoJugador = Estilo.crearTexto("Mano del jugador: 0", 100, 50);

  public void anadirComponentes(JPanel panel) {
    panel.add(activo);
    panel.add(banco1);
    panel.add(banco2);
    panel.add(activoRival);
    panel.add(banco1Rival);
    panel.add(banco2Rival);
    panel.add(adjuntarEnergia);
    panel.add(atacar);
    panel.add(colocarPokemonActivo);
    panel.add(colocarPokemonEnBanco);
    panel.add(usarObjeto);
    panel.add(Evolucionar);
    panel.add(pasarTurno);
    panel.add(turno);
    panel.add(nombreRival);
    panel.add(puntosJugadorRival);
    panel.add(mazoRival);
    panel.add(manoRival);
    panel.add(nombreJugador);
    panel.add(puntosJugador);
    panel.add(mazoJugador);
    panel.add(manoJugador);
  }

  public void establecerPosiciones() {
    //pokemones aliados  
    activo.setBounds(506, 340, 160, 224); 
    banco1.setBounds(356, 361, 120, 168);
    banco2.setBounds(686, 361, 120, 168);

    //pokemones enemigos
    activoRival.setBounds(506, 56, 160, 224);
    banco1Rival.setBounds(356, 77, 120, 168);
    banco2Rival.setBounds(686, 77, 120, 168);

    //acciones
    adjuntarEnergia.setBounds(1160, 56, 320, 44);
    atacar.setBounds(1160, 108, 320, 44);
    colocarPokemonActivo.setBounds(1160, 160, 320, 44);
    colocarPokemonEnBanco.setBounds(1160, 212, 320, 44);
    usarObjeto.setBounds(1160, 264, 320, 44);
    Evolucionar.setBounds(1160, 316, 320, 44);
    pasarTurno.setBounds(1160, 368, 320, 44);

    // texto
    turno.setBounds(20, 8, 1122, 36);

    nombreRival.setBounds(20, 56, 260, 25);
    puntosJugadorRival.setBounds(20, 81, 260, 25);
    mazoRival.setBounds(20, 106, 260, 25);
    manoRival.setBounds(20, 131, 260, 25);

    nombreJugador.setBounds(20, 450, 260, 25);
    puntosJugador.setBounds(20, 475, 260, 25);
    mazoJugador.setBounds(20, 500, 260, 25);
    manoJugador.setBounds(20, 525, 260, 25);

   }

  public void interfaz(Jugador jugador, Jugador rival) {
    this.jugador = jugador;
    this.rival = rival;
    nombreJugador.setText("Nombre del jugador: " + jugador.getNombre());
    puntosJugador.setText("Puntos del jugador: " + jugador.getPuntosPremio());
    mazoJugador.setText("Mazo del jugador: " + jugador.getMazo().cantidadCartas());
    manoJugador.setText("Mano del jugador: " + jugador.getMano().size());

    nombreRival.setText("Nombre del rival: " + rival.getNombre());
    puntosJugadorRival.setText("Puntos del rival: " + rival.getPuntosPremio());
    mazoRival.setText("Mazo del rival: " + rival.getMazo().cantidadCartas());
    manoRival.setText("Mano del rival: " + rival.getMano().size());
  }



  public static void main(String[] args) {
     JFrame frame = new JFrame("Batalla Pokemon");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setSize(1500, 820);
     JPanel panel = new JPanel();
     panel.setLayout(null);
     panel.setBackground(Color.decode("#2DAEFD"));
     frame.add(panel);
     frame.setVisible(true);

    InterfazPelea interfaz = new InterfazPelea();
    interfaz.anadirComponentes(panel);
    interfaz.establecerPosiciones();


    panel.revalidate();
    panel.repaint();

  }
 }