import java.util.Random;

public class Duel {
    Random random = new Random();
    private Card J1;
    private Card J2;
    private String jugadorInicial;
    private int puntosJ1;
    private int puntosJ2;
    private Card[] manoJ1;
    private Card[] manoMaquina;

    public Duel(Card J1, Card J2,Card[] manoJ1, Card[] manoMaquina) {
        this.J1 = J1;
        this.J2 = J2;
        this.manoJ1 = manoJ1;
        this.manoMaquina = manoMaquina;
    }

    //Comienza el juego
    public void turnoInicial() {

        if (random.nextBoolean()) {
            this.jugadorInicial = J1.getNombre();
            System.out.println("Comenzo j1");
        } else {
            this.jugadorInicial = J2.getNombre();
            System.out.println("Comenzo j2");
        }
    }

    //que jugador gana
    public void evaluarRonda(Card carta1, Card carta2, boolean ataque1, boolean ataque2) {

        if (ataque1 && ataque2) {
            if (carta1.getAtk() > carta2.getAtk()) {
                this.puntosJ1++;
            } else
                this.puntosJ2++;
        } else if (ataque1 && !ataque2) {
            if (carta1.getAtk() > carta2.getDef()) {
                this.puntosJ1++;
            } else
                this.puntosJ2++;
        } else if (!ataque1 && ataque2) {
            if(carta2.getAtk() > carta1.getDef()) {
                this.puntosJ2++;
            }
            else
                this.puntosJ1++;
        }

        //Ganador del duelo
        if (puntosJ1 >= 2) {
            System.out.println(this.J1.getNombre());
        } else if (puntosJ2 >= 2) {
            System.out.println(this.J2.getNombre());
        }
    }

    public static void main(String[] args) {
        Card card1 = new Card();
        Card card2 = new Card();
       // Duel duel = new Duel(card1, card2);
       // duel.turnoInicial();
    }
    public Card elegirCartaMaquina(){
        int indiceAzar = random.nextInt(manoMaquina.length);
        return manoMaquina[indiceAzar];
    }
    public boolean elegirModoMaquina() {
        return random.nextBoolean();
    }

}
