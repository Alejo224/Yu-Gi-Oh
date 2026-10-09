import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class YgoApiClient {
    private static final String URL_CARTA_RANDOM = "https://db.ygoprodeck.com/api/v7/randomcard.php";

    // Un solo cliente HTTP reutilizable
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    // Consultar
    public Card constultar() throws CardException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_CARTA_RANDOM))
                .timeout(Duration.ofSeconds(10))
                .build();


        HttpResponse<String> respuesta;
        try {
            respuesta = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new CardException("Error de red: no se pudo contactar a YgoProDeck.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CardException("La consulta fue cancelada.", e);
        }

        if (respuesta.statusCode() != 200) {
            throw new CardException("PokeAPI respondió con código " + respuesta.statusCode() + ".");
        }

        try {
            return parsear(new JSONObject(respuesta.body()));
        } catch (RuntimeException e) {
            throw new CardException("La respuesta de YgoApiClient no tiene el formato esperado.", e);
        }
    }

   // Parse JSON
    private Card parsear(JSONObject json) {
        Card c = new Card();

        // Data
        JSONArray data = json.getJSONArray("data");

        data.forEach(dato -> {
            // Acedder al objecto vasio
            JSONObject dataJson = (JSONObject) dato;

            System.out.println(dataJson);
            c.setId(dataJson.getInt("id"));
            c.setNombre(dataJson.getString("name"));
            //c.setAtk(dataJson.getInt("atk"));
            c.setDef(dataJson.getInt("def"));


        });

        System.out.println(c.toString());
       return c;
    }



/** Descarga y escala la imagen. Bloquea el hilo: llamar SIEMPRE desde un worker. */
    public Image descargarImagen(String url) {
        if (url == null) return null;
            try {
                BufferedImage img = ImageIO.read(new java.net.URL(url));
                    return img == null ? null
                    : img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            } catch (IOException e) {
        return null; // sin sprite no se cae toda la carga
    }
}

    static void main() {

        YgoApiClient ygoApiClient = new YgoApiClient();

        try {
            ygoApiClient.constultar();
        } catch (CardException e) {
            throw new RuntimeException(e);
        }
    }

}
