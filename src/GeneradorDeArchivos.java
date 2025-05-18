import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;

public class GeneradorDeArchivos {

    public void guardarJson(Moneda moneda) throws IOException {
        Gson gson = new Gson();
        FileWriter escritura = new FileWriter("moneda_" + moneda.base_code() + ".json");
        escritura.write(gson.toJson(moneda));
        escritura.close();
    }
}
