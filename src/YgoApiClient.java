import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public class YgoApiClient {
    private static final String URL_CARTA_RANDOM = "https://db.ygoprodeck.com/api/v7/randomcard.php";

    // Consultar
    public Card constultar() throws CardExption {


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_CARTA_RANDOM))
                .timeout(Duration.ofSeconds(10))
                .build();

        return null;
    }

   // Parse JSON
    private Card parear(JSONObject json) {
        Card c = new Card();
        return null;
    }

}
