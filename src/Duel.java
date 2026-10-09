import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Duel {
    private static final int PUNTOS_PARA_GANAR = 2;

    private final Random random = new Random();
    private final List<Card> manoJugador;
    private final List<Card> manoMaquina;
    private int puntosJugador;
    private int puntosMaquina;

    public Duel(Card[] manoJugador, Card[] manoMaquina) {
        this.manoJugador = new ArrayList<>(Arrays.asList(manoJugador));
        this.manoMaquina = new ArrayList<>(Arrays.asList(manoMaquina));
    }


    public String turnoInicial() {
        return random.nextBoolean() ? "Comienza el jugador." : "Comienza la máquina.";
    }


    public String jugarRonda(Card cartaJugador, boolean ataqueJugador) {
        if (terminado()) {
            return "El duelo ya terminó.";
        }
        if (!manoJugador.remove(cartaJugador)) {
            return "Esa carta ya no está disponible.";
        }

        // La máquina elige carta y modo al azar; la carta sale de su mano
        Card cartaMaquina = manoMaquina.remove(random.nextInt(manoMaquina.size()));
        boolean ataqueMaquina = random.nextBoolean();

        StringBuilder log = new StringBuilder();
        log.append("Jugador juega ").append(describir(cartaJugador, ataqueJugador)).append('\n');
        log.append("Máquina juega ").append(describir(cartaMaquina, ataqueMaquina)).append('\n');

        // 1 = gana jugador, 2 = gana máquina, 0 = nadie
        int ganador = evaluar(cartaJugador, ataqueJugador, cartaMaquina, ataqueMaquina);
        if (ganador == 1) {
            puntosJugador++;
            log.append("Resultado: gana el jugador.\n");
        } else if (ganador == 2) {
            puntosMaquina++;
            log.append("Resultado: gana la máquina.\n");
        } else {
            log.append("Resultado: nadie suma punto.\n");
        }
        log.append("Puntaje -> Jugador ").append(puntosJugador)
                .append(" - Máquina ").append(puntosMaquina);

        if (terminado()) {
            log.append("\n*** Ganador del duelo: ").append(getGanador()).append(" ***");
        }
        return log.toString();
    }

    private int evaluar(Card c1, boolean ataque1, Card c2, boolean ataque2) {
        if (ataque1 && ataque2) {
            if (c1.getAtk() > c2.getAtk()) return 1;
            if (c2.getAtk() > c1.getAtk()) return 2;
            return 0;
        }
        if (ataque1) {
            return c1.getAtk() > c2.getDef() ? 1 : 2;
        }
        if (ataque2) {
            return c2.getAtk() > c1.getDef() ? 2 : 1;
        }
        return 0;
    }

    private String describir(Card c, boolean ataque) {
        return c.getNombre() + " (ATK " + c.getAtk() + " / DEF " + c.getDef() + ") en "
                + (ataque ? "ATAQUE" : "DEFENSA");
    }



    public boolean terminado() {
        return puntosJugador >= PUNTOS_PARA_GANAR
                || puntosMaquina >= PUNTOS_PARA_GANAR
                || manoJugador.isEmpty();
    }

    public String getGanador() {
        if (!terminado()) return null;
        if (puntosJugador > puntosMaquina) return "Jugador";
        if (puntosMaquina > puntosJugador) return "Máquina";
        return "Empate";
    }

    public int getPuntosJugador() { return puntosJugador; }

    public int getPuntosMaquina() { return puntosMaquina; }

    public List<Card> getManoJugador() {
        return Collections.unmodifiableList(manoJugador);
    }

}
