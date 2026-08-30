package presentacion;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import datatypes.DTPrestacion;
import excepciones.AccesoNoAutorizadoException;
import excepciones.PrestacionRepetidaException;
import interfaces.IControlador;
import logica.Franja;

public class ModificarPrestacion extends JFrame {

    private final IControlador controlador;
    private final String emailMedico;

    private final JComboBox<DTPrestacion> prestaciones = new JComboBox<>();
    private final JTextField nombre = new JTextField();
    private final JTextField precio = new JTextField();
    private final JComboBox<Franja> franja = new JComboBox<>(Franja.values());

    public ModificarPrestacion(IControlador controlador, String emailMedico) {
        super("Modificar prestación");
        this.controlador = controlador;
        this.emailMedico = emailMedico;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        cargarPrestaciones();
        prestaciones.addActionListener(event -> cargarDatosSeleccionados());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Prestación:"));
        form.add(prestaciones);
        form.add(new JLabel("Nombre:"));
        form.add(nombre);
        form.add(new JLabel("Precio:"));
        form.add(precio);
        form.add(new JLabel("Franja:"));
        form.add(franja);

        JButton guardar = new JButton("Guardar cambios");
        guardar.addActionListener(event -> guardarCambios());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(event -> dispose());

        JPanel botones = new JPanel();
        botones.add(guardar);
        botones.add(cancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        cargarDatosSeleccionados();
    }

    private void cargarPrestaciones() {
        prestaciones.removeAllItems();
        List<DTPrestacion> lista = controlador.listarPrestaciones();
        for (DTPrestacion prestacion : lista) {
            prestaciones.addItem(prestacion);
        }
    }

    private void cargarDatosSeleccionados() {
        DTPrestacion prestacion = (DTPrestacion) prestaciones.getSelectedItem();
        if (prestacion == null) {
            return;
        }
        nombre.setText(prestacion.getNombre());
        precio.setText(String.valueOf(prestacion.getPrecio()));
        franja.setSelectedItem(prestacion.getFranja());
    }

    private void guardarCambios() {
        DTPrestacion prestacion = (DTPrestacion) prestaciones.getSelectedItem();
        if (prestacion == null) {
            return;
        }

        try {
            String nuevoNombre = nombre.getText().trim();
            double nuevoPrecio = Double.parseDouble(precio.getText().trim());
            Franja nuevaFranja = (Franja) franja.getSelectedItem();

            controlador.modificarPrestacion(emailMedico, prestacion.getId(), nuevoNombre, nuevoPrecio, nuevaFranja);
            JOptionPane.showMessageDialog(this, "Prestación modificada correctamente");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (AccesoNoAutorizadoException | PrestacionRepetidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
