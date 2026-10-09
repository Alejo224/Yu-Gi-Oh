import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YuGioGUI {

    private JPanel jpanelYuGio;
    private JButton iniciarDueloButton;
    private JButton elegirCartaButton;

    private JLabel maquina_Carta1;
    private JLabel maquina_img_Carta2;
    private JLabel maquina_imagen_Carta3;
    private JLabel maquina_nombre_C1;
    private JLabel maquina_nombre_C2;
    private JLabel maquina_nombreC3;
    private JLabel maquina_Atk_C1;
    private JLabel maquina_C2_Atk;
    private JLabel maquina_Atk_C3;
    private JLabel maquina_Def_C1;
    private JLabel maquina_Def_C2;
    private JLabel maquina_Def_C3_J3;

    private JLabel Carta1_J2;
    private JLabel imgCarta2_J2;
    private JLabel Carta3_J2;
    private JLabel nombre_C1_J1;
    private JLabel nombre_C2_J1;
    private JLabel nombre_C3_J3;
    private JLabel def_C1_J1;    // OJO: no hay Atk_C1_J1 en el form
    private JLabel Atk_C2_J2;
    private JLabel Def_C2_J2;
    private JLabel Atk_C3_J2;
    private JLabel Def_C3_J2;
    private JLabel Atk1_C1_J1;

    // ==== Estado del juego ====
    private final YgoApiClient api = new YgoApiClient();
    private Duel duel;
    private List<Card> cartasJugador;
    private List<Card> cartasMaquina;

    private Card cartaSeleccionada;
    private int indiceSeleccionado = -1;
    private final boolean[] usadaJugador = new boolean[3];
    private final boolean[] usadaMaquina = new boolean[3];

    // ==== Log de batalla ====
    private final JTextArea logArea = new JTextArea(8, 40);
    private final JScrollPane logScroll = new JScrollPane(logArea);

    // ==== Arrays de JLabels para iterar ====
    private JLabel[] imgJugador, nomJugador, atkJugador, defJugador;
    private JLabel[] imgMaquina, nomMaquina, atkMaquina, defMaquina;

    // ==== Bordes para resaltar selección ====
    private static final Border BORDE_NORMAL = BorderFactory.createEmptyBorder(3, 3, 3, 3);
    private static final Border BORDE_SEL    = BorderFactory.createLineBorder(Color.ORANGE, 3);

    public YuGioGUI() {
        inicializarArrays();
        configurarLog();
        configurarListeners();

        elegirCartaButton.setEnabled(false);
        setEstadoCartas(false); // sin cartas aún
    }

    //  Inicialización

    private void inicializarArrays() {
        imgJugador = new JLabel[]{ Carta1_J2, imgCarta2_J2, Carta3_J2 };
        nomJugador = new JLabel[]{ nombre_C1_J1, nombre_C2_J1, nombre_C3_J3 };
        atkJugador = new JLabel[]{ null, Atk_C2_J2, Atk_C3_J2 };
        defJugador = new JLabel[]{ def_C1_J1, Def_C2_J2, Def_C3_J2 };

        imgMaquina = new JLabel[]{ maquina_Carta1, maquina_img_Carta2, maquina_imagen_Carta3 };
        nomMaquina = new JLabel[]{ maquina_nombre_C1, maquina_nombre_C2, maquina_nombreC3 };
        atkMaquina = new JLabel[]{ maquina_Atk_C1, maquina_C2_Atk, maquina_Atk_C3 };
        defMaquina = new JLabel[]{ maquina_Def_C1, maquina_Def_C2, maquina_Def_C3_J3 };
    }

    private void configurarLog() {
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logScroll.setPreferredSize(new Dimension(400, 120));

        // Si ya tienes un JTextArea en el diseñador, borra este bloque.
        if (jpanelYuGio != null) {
            try {
                jpanelYuGio.add(logScroll);
                jpanelYuGio.revalidate();
            } catch (Exception ignored) {
                // el layout del form no admite añadir por código
            }
        }
    }

    private void configurarListeners() {
        iniciarDueloButton.addActionListener(e -> iniciarDuelo());
        elegirCartaButton.addActionListener(e -> jugarTurno());

        for (int i = 0; i < imgJugador.length; i++) {
            if (imgJugador[i] == null) continue;
            final int idx = i;
            imgJugador[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            imgJugador[i].setBorder(BORDE_NORMAL);
            imgJugador[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { seleccionarCarta(idx); }
            });
        }
    }

    //  Acciones de los botones

    private void iniciarDuelo() {
        iniciarDueloButton.setEnabled(false);
        elegirCartaButton.setEnabled(false);
        logArea.setText("");
        log("Cargando 6 cartas desde YGOProDeck...");

        // Se ejecuta fuera del EDT para no congelar la UI
        new SwingWorker<List<Card>, Void>() {
            @Override protected List<Card> doInBackground() throws Exception {
                return api.cargarMazo(6);
            }

            @Override protected void done() {
                try {
                    List<Card> todas = get();
                    cartasJugador = new ArrayList<>(todas.subList(0, 3));
                    cartasMaquina = new ArrayList<>(todas.subList(3, 6));

                    duel = new Duel(cartasJugador.toArray(new Card[0]),
                                    cartasMaquina.toArray(new Card[0]));

                    Arrays.fill(usadaJugador, false);
                    Arrays.fill(usadaMaquina, false);
                    indiceSeleccionado = -1;
                    cartaSeleccionada = null;

                    mostrarCartas();
                    setEstadoCartas(true);

                    log(duel.turnoInicial());
                    log("Selecciona una carta y pulsa 'Elegir carta'.");
                    elegirCartaButton.setEnabled(true);

                } catch (Exception ex) {
                    Throwable causa = (ex.getCause() != null) ? ex.getCause() : ex;
                    log("No se pudo cargar la carta: " + causa.getMessage());
                    iniciarDueloButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void jugarTurno() {
        if (duel == null || duel.terminado()) return;
        if (cartaSeleccionada == null || indiceSeleccionado < 0) {
            log("Selecciona una carta primero.");
            return;
        }
        if (usadaJugador[indiceSeleccionado]) return;

        // Pedir modo de juego
        Object[] opciones = {"Ataque", "Defensa"};
        int op = JOptionPane.showOptionDialog(
                jpanelYuGio,
                "¿Cómo juegas \"" + cartaSeleccionada.getNombre() + "\"?",
                "Modo de batalla",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (op == JOptionPane.CLOSED_OPTION) return;

        boolean ataqueJugador = (op == 0);
        int idxJug = indiceSeleccionado;

        // Ejecutar ronda
        String resumen = duel.jugarRonda(cartaSeleccionada, ataqueJugador);
        log(resumen);

        // Marcar la carta jugada del lado del jugador
        marcarJugada(idxJug, true);

        // Deducir la carta que jugó la máquina (Duel no la expone)
        int idxMaq = deducirIndiceMaquina(resumen);
        if (idxMaq >= 0) marcarJugada(idxMaq, false);

        // Reset selección
        if (imgJugador[idxJug] != null) imgJugador[idxJug].setBorder(BORDE_NORMAL);
        indiceSeleccionado = -1;
        cartaSeleccionada = null;

        if (duel.terminado()) {
            elegirCartaButton.setEnabled(false);
            iniciarDueloButton.setEnabled(true);
        }
    }

    //  Selección de carta del jugador

    private void seleccionarCarta(int idx) {
        if (duel == null || duel.terminado()) return;
        if (cartasJugador == null || idx < 0 || idx >= cartasJugador.size()) return;
        if (usadaJugador[idx]) return;

        if (indiceSeleccionado >= 0 && imgJugador[indiceSeleccionado] != null) {
            imgJugador[indiceSeleccionado].setBorder(BORDE_NORMAL);
        }

        indiceSeleccionado = idx;
        cartaSeleccionada = cartasJugador.get(idx);
        if (imgJugador[idx] != null) imgJugador[idx].setBorder(BORDE_SEL);

        log("Seleccionada: " + cartaSeleccionada.getNombre());
    }

    //  Pintado de la UI

    private void mostrarCartas() {
        for (int i = 0; i < 3; i++) {
            pintarCarta(cartasJugador.get(i), imgJugador[i], nomJugador[i], atkJugador[i], defJugador[i]);
            pintarCarta(cartasMaquina.get(i), imgMaquina[i], nomMaquina[i], atkMaquina[i], defMaquina[i]);
        }
    }

    private void pintarCarta(Card c, JLabel img, JLabel nom, JLabel atk, JLabel def) {
        if (c == null) return;
        if (img != null) {
            if (c.getImagen() != null) {
                img.setIcon(new ImageIcon(c.getImagen()));
                img.setText("");
            } else {
                img.setIcon(null);
                img.setText("(sin imagen)");
            }
            img.setHorizontalAlignment(SwingConstants.CENTER);
        }
        if (nom != null) nom.setText(c.getNombre());
        if (atk != null) atk.setText("ATK " + c.getAtk());
        if (def != null) def.setText("DEF " + c.getDef());
    }

    private void marcarJugada(int idx, boolean esJugador) {
        boolean[] usadas = esJugador ? usadaJugador : usadaMaquina;
        JLabel[] imgs = esJugador ? imgJugador : imgMaquina;
        if (idx < 0 || idx >= usadas.length || usadas[idx]) return;
        usadas[idx] = true;
        if (imgs[idx] != null) {
            imgs[idx].setIcon(null);
            imgs[idx].setText("JUGADA");
            imgs[idx].setBorder(BORDE_NORMAL);
        }
    }

    private void setEstadoCartas(boolean habilitado) {
        for (JLabel[] fila : new JLabel[][]{imgJugador, imgMaquina}) {
            for (JLabel l : fila) {
                if (l != null) l.setEnabled(habilitado);
            }
        }
    }

    //  Utilidades

    private int deducirIndiceMaquina(String logRonda) {
        final String pref = "Máquina juega ";
        int pos = logRonda.indexOf(pref);
        if (pos < 0) return -1;
        String resto = logRonda.substring(pos + pref.length());
        int fin = resto.indexOf(" (ATK");
        if (fin < 0) fin = resto.length();
        String nombre = resto.substring(0, fin);

        for (int i = 0; i < cartasMaquina.size(); i++) {
            if (cartasMaquina.get(i).getNombre().equals(nombre)) return i;
        }
        return -1;
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Yu-Gi-Oh! Duel Lite");
            f.setContentPane(new YuGioGUI().jpanelYuGio);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}