package presentacion;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import excepciones.UsuarioRepetidoException;
import interfaces.IControlador;

public class RegistroUsuario extends JFrame {

    private final IControlador controlador;

    private final JTextField nombre = new JTextField();
    private final JTextField email = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JComboBox<String> tipo = new JComboBox<>(new String[] { "Paciente", "Médico" });
    private final JTextField datoExtra = new JTextField();

    public RegistroUsuario(IControlador controlador) {
        super("Registro de usuario");
        this.controlador = controlador;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(430, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nombre:"));
        form.add(nombre);
        form.add(new JLabel("Email:"));
        form.add(email);
        form.add(new JLabel("Contraseña:"));
        form.add(password);
        form.add(new JLabel("Tipo:"));
        form.add(tipo);
        form.add(new JLabel("Mutualista / especialidad:"));
        form.add(datoExtra);

        JButton guardar = new JButton("Registrar");
        guardar.addActionListener(event -> registrar());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(event -> dispose());

        JPanel botones = new JPanel();
        botones.add(guardar);
        botones.add(cancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void registrar() {
        try {
            String nombreTexto = nombre.getText().trim();
            String emailTexto = email.getText().trim();
            String passwordTexto = new String(password.getPassword());
            String datoTexto = datoExtra.getText().trim();

            if (nombreTexto.isEmpty() || emailTexto.isEmpty() || passwordTexto.isEmpty()) {
                throw new IllegalArgumentException("Completá los campos obligatorios");
            }

            if ("Médico".equals(tipo.getSelectedItem())) {
                controlador.registrarMedico(emailTexto, nombreTexto, passwordTexto, datoTexto);
            } else {
                controlador.registrarPaciente(emailTexto, nombreTexto, passwordTexto, datoTexto);
            }

            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente");
            dispose();
        } catch (IllegalArgumentException | UsuarioRepetidoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
