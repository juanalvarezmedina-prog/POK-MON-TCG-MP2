import javax.swing.*;
import java.awt.Color;


public class InterfazPelea {
  public static void main(String[] args) {

     JFrame frame = new JFrame("Batalla Pokemon");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
     JPanel panel = new JPanel();
     panel.setLayout(null);
     panel.setBackground(Color.decode("#2DAEFD"));
     frame.add(panel);
     frame.setVisible(true);

     //botones de pokemones

     JButton activo= Estilo.crearCarta("Pokemon Activo", 100, 200);
      panel.add(activo);

    JButton banco1= Estilo.crearCarta("Banco 1", 100, 200);
      panel.add(banco1);

    JButton banco2= Estilo.crearCarta("Banco 2", 100, 200);
      panel.add(banco2);
    
    JButton activoRival= Estilo.crearCarta("Pokemon Rival Activo", 100, 200);
      panel.add(activoRival);

    JButton banco1Rival= Estilo.crearCarta("Banco 1 Rival", 100, 200);
      panel.add(banco1Rival);
    
    JButton banco2Rival= Estilo.crearCarta("Banco 2 Rival", 100, 200);
      panel.add(banco2Rival); 
      
    // botones de acciones

    JButton adjuntarEnergia= Estilo.crearBotonAccion("Adjuntar Energia", 100, 50);
      panel.add(adjuntarEnergia);
    
    JButton atacar= Estilo.crearBotonAccion("Atacar", 100, 50);
      panel.add(atacar);

    JButton colocarPokemonActivo= Estilo.crearBotonAccion("Colocar Pokemon Activo", 100, 50);
      panel.add(colocarPokemonActivo);

    JButton colocarPokemonEnBanco= Estilo.crearBotonAccion("Retirar Pokemon", 100, 50);
      panel.add(colocarPokemonEnBanco);
    
    JButton usarObjeto= Estilo.crearBotonAccion("Usar Objeto", 100, 50);
      panel.add(usarObjeto);
    
    JButton Evolucionar= Estilo.crearBotonAccion("Evolucionar Pokemon", 100, 50);
      panel.add(Evolucionar);
    
    JButton pasarTurno= Estilo.crearBotonAccion("Pasar Turno", 100, 50);
      panel.add(pasarTurno);

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

    panel.revalidate();
    panel.repaint();
    

  }
}