

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

    public class ConsultaDeMoneda {

        public Moneda buscarMoneda(String codigoBase) {
            URI direccion = URI.create("https://v6.exchangerate-api.com/v6/579e7d92b312e4c00365afc6/latest/" + codigoBase);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(direccion)
                    .build();

            try {
                HttpResponse<String> response = client
                        .send(request, HttpResponse.BodyHandlers.ofString());
                return new Gson().fromJson(response.body(), Moneda.class);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("No fue posible obtener los datos de la moneda base: " + codigoBase);
            }
        }
    }



