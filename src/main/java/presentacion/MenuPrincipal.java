package presentacion;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import datatypes.DTEstudio;
import datatypes.DTPrestacion;
import datatypes.DTTerapia;
import datatypes.DTUsuario;
import excepciones.AccesoNoAutorizadoException;
import excepciones.PrestacionEnOrdenException;
import excepciones.PrestacionRepetidaException;
import interfaces.IControlador;
import logica.Franja;

public class MenuPrincipal extends JFrame {

	private final IControlador controlador;
	private final DTUsuario usuario;
	private final DefaultTableModel modelo = new DefaultTableModel(
			new Object[] { "ID", "Tipo", "Nombre", "Precio", "Franja", "Detalle" }, 0) {
		@Override
		public boolean isCellEditable(int row, int column) { return false; }
	};
	private final JTable tabla = new JTable(modelo);

	public MenuPrincipal(IControlador controlador, DTUsuario usuario) {
		super("Clínica - Catálogo");
		this.controlador = controlador;
		this.usuario = usuario;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(760, 420);
		setLocationRelativeTo(null);
		add(new JLabel("Usuario: " + usuario.getNombre() + " (" + usuario.getEmail() + ")"),
				BorderLayout.NORTH);
		add(new JScrollPane(tabla), BorderLayout.CENTER);

		JPanel acciones = new JPanel();
		JButton actualizar = new JButton("Actualizar");
		actualizar.addActionListener(event -> cargarCatalogo());
		acciones.add(actualizar);
		if (usuario instanceof datatypes.DTMedico) {
			JButton alta = new JButton("Nueva prestación");
			alta.addActionListener(event -> altaPrestacion());
			JButton modificar = new JButton("Modificar seleccionada");
			modificar.addActionListener(event -> modificarPrestacion());
			JButton eliminar = new JButton("Eliminar seleccionada");
			eliminar.addActionListener(event -> eliminarPrestacion());
			acciones.add(alta);
			acciones.add(modificar);
			acciones.add(eliminar);
		}
		add(acciones, BorderLayout.SOUTH);
		cargarCatalogo();
	}

	private void cargarCatalogo() {
		modelo.setRowCount(0);
		List<DTPrestacion> prestaciones = controlador.listarPrestaciones();
		for (DTPrestacion prestacion : prestaciones) {
			if (prestacion == null) continue;
			String tipo;
			String detalle;
			switch (prestacion) {
				case DTEstudio estudio -> {
					tipo = "Estudio";
					detalle = estudio.getDuracionMinutos() + " minutos";
				}
				case DTTerapia terapia -> {
					tipo = "Terapia";
					detalle = terapia.getCantidadSesiones() + " sesiones";
				}
				default -> throw new IllegalStateException("Tipo de prestación no reconocido");
			}
			modelo.addRow(new Object[] { prestacion.getId(), tipo, prestacion.getNombre(),
					prestacion.getPrecio(), prestacion.getFranja(), detalle });
		}
	}

	private void altaPrestacion() {
		JTextField nombre = new JTextField();
		JTextField precio = new JTextField();
		JTextField tipo = new JTextField("estudio");
		JTextField franja = new JTextField("MANANA");
		JTextField detalle = new JTextField("30");
		Object[] campos = { "Tipo (estudio/terapia)", tipo, "Nombre", nombre, "Precio", precio,
				"Franja (MANANA/TARDE/NOCHE)", franja, "Duración o sesiones", detalle };
		if (JOptionPane.showConfirmDialog(this, campos, "Nueva prestación",
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
		try {
			if (tipo.getText().equalsIgnoreCase("terapia")) {
				controlador.altaTerapia(usuario.getEmail(), nombre.getText(), Double.parseDouble(precio.getText()),
						Franja.valueOf(franja.getText().toUpperCase()), false, Integer.parseInt(detalle.getText()));
			} else {
				controlador.altaEstudio(usuario.getEmail(), nombre.getText(), Double.parseDouble(precio.getText()),
						Franja.valueOf(franja.getText().toUpperCase()), Integer.parseInt(detalle.getText()));
			}
			cargarCatalogo();
		} catch (IllegalArgumentException | AccesoNoAutorizadoException
				| PrestacionRepetidaException exception) {
			mostrarError(exception.getMessage());
		}
	}

	private void modificarPrestacion() {
		int fila = tabla.getSelectedRow();
		if (fila < 0) { mostrarError("Seleccioná una prestación"); return; }
		JTextField nombre = new JTextField((String) modelo.getValueAt(fila, 2));
		JTextField precio = new JTextField(String.valueOf(modelo.getValueAt(fila, 3)));
		JTextField franja = new JTextField(String.valueOf(modelo.getValueAt(fila, 4)));
		Object[] campos = { "Nombre", nombre, "Precio", precio, "Franja", franja };
		if (JOptionPane.showConfirmDialog(this, campos, "Modificar prestación",
				JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
		try {
			controlador.modificarPrestacion(usuario.getEmail(), (Long) modelo.getValueAt(fila, 0),
					nombre.getText(), Double.parseDouble(precio.getText()),
					Franja.valueOf(franja.getText().toUpperCase()));
			cargarCatalogo();
		} catch (IllegalArgumentException | AccesoNoAutorizadoException
				| PrestacionRepetidaException exception) {
			mostrarError(exception.getMessage());
		}
	}

	private void eliminarPrestacion() {
		int fila = tabla.getSelectedRow();
		if (fila < 0) { mostrarError("Seleccioná una prestación"); return; }
		try {
			controlador.eliminarPrestacion(usuario.getEmail(), (Long) modelo.getValueAt(fila, 0));
			cargarCatalogo();
		} catch (AccesoNoAutorizadoException | PrestacionEnOrdenException exception) {
			mostrarError(exception.getMessage());
		}
	}

	private void mostrarError(String mensaje) {
		JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
	}
}