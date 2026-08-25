package presentacion;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import datatypes.DTUsuario;
import excepciones.CredencialesInvalidasException;
import excepciones.UsuarioRepetidoException;
import interfaces.Fabrica;
import interfaces.IControlador;

public class Login extends JFrame {

	private final IControlador controlador = Fabrica.getControlador();
	private final JTextField email = new JTextField();
	private final JPasswordField password = new JPasswordField();

	public Login() {
		super("Clínica - Iniciar sesión");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(380, 190);
		setLocationRelativeTo(null);

		JPanel campos = new JPanel(new GridLayout(2, 2, 8, 8));
		campos.add(new JLabel("Email:"));
		campos.add(email);
		campos.add(new JLabel("Contraseña:"));
		campos.add(password);

		JButton ingresar = new JButton("Ingresar");
		ingresar.addActionListener(event -> iniciarSesion());
		JButton registrar = new JButton("Registrarse");
		registrar.addActionListener(event -> registrarUsuario());

		JPanel botones = new JPanel();
		botones.add(ingresar);
		botones.add(registrar);

		add(campos, BorderLayout.CENTER);
		add(botones, BorderLayout.SOUTH);
	}

	private void iniciarSesion() {
		try {
			DTUsuario usuario = controlador.iniciarSesion(email.getText(),
					new String(password.getPassword()));
			new MenuPrincipal(controlador, usuario).setVisible(true);
			dispose();
		} catch (CredencialesInvalidasException exception) {
			JOptionPane.showMessageDialog(this, exception.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void registrarUsuario() {
		JTextField nombre = new JTextField();
		JTextField tipo = new JTextField("paciente");
		JTextField dato = new JTextField();
		JPasswordField clave = new JPasswordField();
		Object[] campos = { "Nombre", nombre, "Email", email, "Contraseña", clave,
				"Tipo (paciente/medico)", tipo, "Mutualista o especialidad", dato };

		if (JOptionPane.showConfirmDialog(this, campos, "Registrar usuario",
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
			return;
		}

		try {
			if (tipo.getText().equalsIgnoreCase("medico")) {
				controlador.registrarMedico(email.getText(), nombre.getText(),
						new String(clave.getPassword()), dato.getText());
			} else {
				controlador.registrarPaciente(email.getText(), nombre.getText(),
						new String(clave.getPassword()), dato.getText());
			}
			JOptionPane.showMessageDialog(this, "Usuario registrado correctamente");
		} catch (UsuarioRepetidoException | IllegalArgumentException exception) {
			JOptionPane.showMessageDialog(this, exception.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Login().setVisible(true));
	}
}
