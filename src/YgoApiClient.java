import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**  Todos los métodos bloquean: llamar desde un worker, nunca desde el EDT. */
public class YgoApiClient {
    private static final String URL_CARTA_RANDOM = "https://db.ygoprodeck.com/api/v7/randomcard.php";
    private static final int MAX_INTENTOS = 10;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /** Devuelve una carta Monster aleatoria (reintenta si sale Spell/Trap). */
    public Card consultar() throws CardException {
        for (int i = 0; i < MAX_INTENTOS; i++) {
            Card c = pedirCartaRandom();
            if (c != null) return c;
        }
        throw new CardException("No se pudo cargar la carta: sin Monster tras " + MAX_INTENTOS + " intentos.");
    }

    /** Mazo de n cartas Monster con imagen ya descargada. */
    public List<Card> cargarMazo(int n) throws CardException {
        List<Card> mazo = new ArrayList<>();
        while (mazo.size() < n) {
            Card c = consultar();
            c.setImagen(descargarImagen(c.getImageUrl()));
            mazo.add(c);
        }
        return mazo;
    }

    /** @return Card si es Monster, null si no lo es. */
    private Card pedirCartaRandom() throws CardException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_CARTA_RANDOM))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> resp;
        try {
            resp = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new CardException("Error de red: no se pudo contactar a YGOProDeck.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CardException("La consulta fue cancelada.", e);
        }

        if (resp.statusCode() != 200) {
            throw new CardException("YGOProDeck respondió con código " + resp.statusCode() + ".");
        }

        try {
            return parsear(new JSONObject(resp.body()));
        } catch (JSONException e) {
            throw new CardException("La respuesta de YGOProDeck no tiene el formato esperado.", e);
        }
    }

    private Card parsear(JSONObject json) {
        JSONObject d = json.getJSONArray("data").getJSONObject(0);

        if (!d.optString("type", "").contains("Monster")) return null;

        Card c = new Card();
        c.setId(d.getInt("id"));
        c.setNombre(d.getString("name"));
        c.setAtk(d.optInt("atk", 0));
        c.setDef(d.optInt("def", 0)); // Link Monsters no tienen def

        JSONArray imgs = d.optJSONArray("card_images");
        if (imgs != null && !imgs.isEmpty()) {
            c.setImageUrl(imgs.getJSONObject(0).optString("image_url", null));
        }
        return c;
    }

    /** Descarga y escala la imagen. Bloquea: llamar siempre desde un worker. */
    private Image descargarImagen(String url) {
        try {
            BufferedImage img = ImageIO.read(new java.net.URL(url));
            return img == null ? null : img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            return null; // sin sprite no es motivo para fallar toda la carga
        }
    }

    public static void main(String[] args) throws CardException {
        List<Card> mazo = new YgoApiClient().cargarMazo(3);
        mazo.forEach(System.out::println);
    }
}