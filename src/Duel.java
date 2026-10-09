import java.util.Random;
public class Duel {
    Random random = new Random();
    private Card J1;
    private Card J2;
    private String jugadorInicial;

    public Duel(Card J1, Card J2) {
        this.J1 = J1;
        this.J2 = J2;
    }

    //Comienza el juego
    public void turnoInicial () {

        if(random.nextBoolean()) {
            this.jugadorInicial = J1.getNombre();
            System.out.println("Comenzo j1");
        }
        else {
            this.jugadorInicial = J2.getNombre();
            System.out.println("Comenzo j2");
        }
    }
    public static void main(String[] args){
        Card card1 = new Card();
        Card card2 = new Card();
        Duel duel = new Duel(card1, card2);
    }

}
