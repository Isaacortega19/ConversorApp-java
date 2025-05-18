import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner lectura = new Scanner(System.in);
        ConsultaDeMoneda consulta = new ConsultaDeMoneda();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("=== Conversor de Monedas ===");
        System.out.print("Ingresa el código de la moneda base (por ejemplo USD, EUR, COP): ");
        String monedaBase = lectura.nextLine().toUpperCase();

        try {
            Moneda moneda = consulta.buscarMoneda(monedaBase);

            // Mostrar todas las tasas disponibles iterando el Map
            System.out.println("\nTasas de cambio desde " + monedaBase + ":");
            for (Map.Entry<String, Double> entry : moneda.conversion_rates().entrySet()) {
                System.out.printf("A %s: %.4f%n", entry.getKey(), entry.getValue());
            }

            // Pedir monto y moneda destino para hacer la conversión
            System.out.print("\nIngresa el monto que quieres convertir: ");
            double monto = Double.parseDouble(lectura.nextLine());

            System.out.print("Ingresa el código de la moneda destino (por ejemplo USD, EUR, COP): ");
            String monedaDestino = lectura.nextLine().toUpperCase();

            Double tasaConversion = moneda.conversion_rates().get(monedaDestino);

            if (tasaConversion == null) {
                System.out.println("Moneda destino no válida o no disponible.");
            } else {
                double resultado = monto * tasaConversion;
                System.out.printf("%.2f %s equivalen a %.2f %s%n", monto, monedaBase, resultado, monedaDestino);

                // Guardar historial de conversiones (modo avanzado)
                Conversion conversion = new Conversion(monedaBase, monedaDestino, monto, resultado);
                HistorialConversiones.guardarConversion(conversion);
                System.out.println("Historial de conversion guardado.");
            }

            // Guardar el objeto moneda completo en archivo JSON con fecha/hora
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = "moneda_" + moneda.base_code() + "_" + timestamp + ".json";

            try (FileWriter writer = new FileWriter(nombreArchivo)) {
                gson.toJson(moneda, writer);
            }

            System.out.println("\nArchivo JSON guardado correctamente como: " + nombreArchivo);

        } catch (RuntimeException | IOException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Finalizando la aplicación.");
        }
    }
}

// Clase para guardar una sola conversión
class Conversion {
    String monedaOrigen;
    String monedaDestino;
    double montoOriginal;
    double montoConvertido;
    String fechaHora;

    public Conversion(String origen, String destino, double montoOriginal, double montoConvertido) {
        this.monedaOrigen = origen;
        this.monedaDestino = destino;
        this.montoOriginal = montoOriginal;
        this.montoConvertido = montoConvertido;
        this.fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

// Clase para manejar historial de conversiones en un archivo JSON
class HistorialConversiones {

    private static final String NOMBRE_ARCHIVO = "historial_conversiones.json";

    public static void guardarConversion(Conversion conversion) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Leer el archivo actual si existe y agregar la nueva conversión
        java.nio.file.Path path = java.nio.file.Paths.get(NOMBRE_ARCHIVO);
        java.util.List<Conversion> listaConversiones;

        if (java.nio.file.Files.exists(path)) {
            String contenido = java.nio.file.Files.readString(path);
            java.lang.reflect.Type tipoLista = new com.google.gson.reflect.TypeToken<java.util.List<Conversion>>(){}.getType();
            listaConversiones = gson.fromJson(contenido, tipoLista);
            if (listaConversiones == null) {
                listaConversiones = new java.util.ArrayList<>();
            }
        } else {
            listaConversiones = new java.util.ArrayList<>();
        }

        listaConversiones.add(conversion);

        // Guardar la lista actualizada de conversiones
        try (FileWriter writer = new FileWriter(NOMBRE_ARCHIVO)) {
            gson.toJson(listaConversiones, writer);
        }
    }
}
