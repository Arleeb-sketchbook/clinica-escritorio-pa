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
		JButton registrar = new JButton("Registrar");
		registrar.addActionListener(event -> elegirTipoRegistro());

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

	private void elegirTipoRegistro() {
		Object[] opciones = { "Médico", "Paciente" };
		int seleccion = JOptionPane.showOptionDialog(this,
				"¿Qué tipo de usuario querés registrar?", "Tipo de registro",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				opciones, opciones[1]);
		if (seleccion == 0) {
			registrarUsuario(true);
		} else if (seleccion == 1) {
			registrarUsuario(false);
		}
	}

	private void registrarUsuario(boolean medico) {
		JTextField nombre = new JTextField();
		JTextField emailRegistro = new JTextField();
		JTextField dato = new JTextField();
		JPasswordField clave = new JPasswordField();
		String etiquetaDato = medico ? "Especialidad" : "Mutualista";
		Object[] campos = { "Nombre", nombre, "Email", emailRegistro, "Contraseña", clave,
				etiquetaDato, dato };

		String titulo = medico ? "Registrar médico" : "Registrar paciente";
		if (JOptionPane.showConfirmDialog(this, campos, titulo,
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
			return;
		}

		try {
			if (medico) {
				controlador.registrarMedico(emailRegistro.getText(), nombre.getText(),
						new String(clave.getPassword()), dato.getText());
			} else {
				controlador.registrarPaciente(emailRegistro.getText(), nombre.getText(),
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
