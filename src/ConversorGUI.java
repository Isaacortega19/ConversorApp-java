import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ConversorGUI extends JFrame {

    private JTextField montoField;
    private JComboBox<String> monedaBaseBox;
    private JComboBox<String> monedaDestinoBox;
    private JTextArea resultadoArea;
    private ConsultaDeMoneda consulta;

    public ConversorGUI() {
        super("Conversor de Monedas");

        consulta = new ConsultaDeMoneda();

        setLayout(new BorderLayout());

        // Panel superior con inputs
        JPanel panelInputs = new JPanel(new GridLayout(4, 2, 10, 10));

        panelInputs.add(new JLabel("Monto:"));
        montoField = new JTextField();
        panelInputs.add(montoField);

        panelInputs.add(new JLabel("Moneda base:"));
        monedaBaseBox = new JComboBox<>(new String[]{"USD", "EUR", "COP", "MXN", "BRL"});
        panelInputs.add(monedaBaseBox);

        panelInputs.add(new JLabel("Moneda destino:"));
        monedaDestinoBox = new JComboBox<>(new String[]{"USD", "EUR", "COP", "MXN", "BRL"});
        panelInputs.add(monedaDestinoBox);

        JButton btnConvertir = new JButton("Convertir");
        panelInputs.add(btnConvertir);

        add(panelInputs, BorderLayout.NORTH);

        // Área de resultados
        resultadoArea = new JTextArea(10, 30);
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        // Acción botón convertir
        btnConvertir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertirMoneda();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Centrar ventana
        setVisible(true);
    }

    private void convertirMoneda() {
        try {
            String base = (String) monedaBaseBox.getSelectedItem();
            String destino = (String) monedaDestinoBox.getSelectedItem();
            double monto = Double.parseDouble(montoField.getText());

            Moneda moneda = consulta.buscarMoneda(base);

            Map<String, Double> tasas = moneda.conversion_rates();

            if (!tasas.containsKey(destino)) {
                resultadoArea.setText("Moneda destino no disponible.");
                return;
            }

            double tasa = tasas.get(destino);
            double resultado = monto * tasa;

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%.2f %s equivalen a %.2f %s\n\n", monto, base, resultado, destino));
            sb.append("Tasas de cambio disponibles desde ").append(base).append(":\n");
            for (Map.Entry<String, Double> entry : tasas.entrySet()) {
                sb.append(String.format("A %s: %.4f\n", entry.getKey(), entry.getValue()));
            }

            resultadoArea.setText(sb.toString());

        } catch (NumberFormatException ex) {
            resultadoArea.setText("Por favor, ingresa un monto válido.");
        } catch (Exception ex) {
            resultadoArea.setText("Error al obtener tasas o convertir: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConversorGUI());
    }
}
