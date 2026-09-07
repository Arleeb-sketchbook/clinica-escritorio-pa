package presentacion;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import excepciones.AccesoNoAutorizadoException;
import excepciones.PrestacionRepetidaException;
import interfaces.IControlador;
import logica.Franja;

public class AltaPrestacion extends JFrame {

    private final IControlador controlador;
    private final String emailMedico;

    private final boolean estudio;
    private final JTextField nombre = new JTextField();
    private final JTextField precio = new JTextField();
    private final JComboBox<Franja> franja = new JComboBox<>(Franja.values());
    private final JTextField detalle = new JTextField();
    private final JCheckBox requiereDerivacion = new JCheckBox("Requiere derivación");

    public AltaPrestacion(IControlador controlador, String emailMedico, boolean estudio) {
        super(estudio ? "Nuevo estudio" : "Nueva terapia");
        this.controlador = controlador;
        this.emailMedico = emailMedico;
        this.estudio = estudio;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nombre:"));
        form.add(nombre);
        form.add(new JLabel("Precio:"));
        form.add(precio);
        form.add(new JLabel("Franja:"));
        form.add(franja);
        form.add(new JLabel(estudio ? "Duración (minutos):" : "Cantidad de sesiones:"));
        form.add(detalle);
        if (!estudio) {
            form.add(new JLabel("Detalle adicional:"));
            form.add(requiereDerivacion);
        }

        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(event -> guardarPrestacion());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(event -> dispose());

        JPanel botones = new JPanel();
        botones.add(guardar);
        botones.add(cancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void guardarPrestacion() {
        try {
            String nombreTexto = nombre.getText().trim();
            if (nombreTexto.isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }

            double precioValor = Double.parseDouble(precio.getText().trim());
            Franja franjaValor = (Franja) franja.getSelectedItem();

            if (estudio) {
                int duracion = Integer.parseInt(detalle.getText().trim());
                controlador.altaEstudio(emailMedico, nombreTexto, precioValor, franjaValor, duracion);
            } else {
                int sesiones = Integer.parseInt(detalle.getText().trim());
                controlador.altaTerapia(emailMedico, nombreTexto, precioValor, franjaValor,
                        requiereDerivacion.isSelected(), sesiones);
            }

            JOptionPane.showMessageDialog(this, "Prestación registrada correctamente");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verificá que el precio y el valor de detalle sean numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | AccesoNoAutorizadoException | PrestacionRepetidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
