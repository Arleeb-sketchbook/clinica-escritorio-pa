package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import datatypes.DTEstudio;
import datatypes.DTLineaOrden;
import datatypes.DTMedico;
import datatypes.DTOrdenMedica;
import datatypes.DTPrestacion;
import datatypes.DTSeguido;
import datatypes.DTTerapia;
import datatypes.DTUsuario;
import excepciones.AccesoNoAutorizadoException;
import excepciones.OrdenSinPrestacionesException;
import excepciones.PrestacionEnOrdenException;
import excepciones.PrestacionRepetidaException;
import excepciones.SeguidoRepetidoException;
import interfaces.IControlador;
import logica.Franja;

public class MenuPrincipal extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA_SEGUIDO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final IControlador controlador;
    private final DTUsuario usuario;
    private final JTextField campoBusqueda = new JTextField(18);
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[] { "ID", "Tipo", "Nombre", "Precio", "Franja" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JPanel direccionesOrden = new JPanel(new FlowLayout(FlowLayout.CENTER));
    private boolean ordenarPorPrecio = true;

    public MenuPrincipal(IControlador controlador, DTUsuario usuario) {
        super("Clínica - Catálogo");
        this.controlador = controlador;
        this.usuario = usuario;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 480);
        setLocationRelativeTo(null);

        JPanel cabecera = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cabecera.add(new JLabel("Usuario: " + usuario.getNombre() + " (" + usuario.getEmail() + ")"));
        cabecera.add(new JLabel("      Búsqueda:"));
        cabecera.add(campoBusqueda);
        JButton buscar = new JButton("Buscar");
        buscar.addActionListener(event -> {
            ocultarOpcionesOrden();
            cargarCatalogo();
        });
        cabecera.add(buscar);
        JButton limpiar = new JButton("Mostrar todo");
        limpiar.addActionListener(event -> {
            ocultarOpcionesOrden();
            campoBusqueda.setText("");
            cargarCatalogo();
        });
        cabecera.add(limpiar);
        JButton cerrarSesion = new JButton("Cerrar sesión");
        cerrarSesion.addActionListener(event -> cerrarSesion());
        cabecera.add(cerrarSesion);
        add(cabecera, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel();
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));

        JPanel filaAccionesBase = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton actualizar = new JButton("Actualizar");
        actualizar.addActionListener(event -> {
            ocultarOpcionesOrden();
            cargarCatalogo();
        });
        filaAccionesBase.add(actualizar);

        JButton verDetalles = new JButton("Ver detalles");
        verDetalles.addActionListener(event -> {
            ocultarOpcionesOrden();
            verDetallesPrestacion();
        });
        filaAccionesBase.add(verDetalles);

        JButton ordenar = new JButton("Ordenar");
        ordenar.addActionListener(event -> elegirCriterioOrden());
        filaAccionesBase.add(ordenar);

        direccionesOrden.setVisible(false);
        JButton ascendente = new JButton("↑");
        ascendente.setToolTipText("Orden ascendente");
        ascendente.addActionListener(event -> cargarCatalogoOrdenado(true));
        direccionesOrden.add(ascendente);

        JButton descendente = new JButton("↓");
        descendente.setToolTipText("Orden descendente");
        descendente.addActionListener(event -> cargarCatalogoOrdenado(false));
        direccionesOrden.add(descendente);
        acciones.add(direccionesOrden);

        acciones.add(filaAccionesBase);

        JPanel filaAccionesEspecificas = new JPanel(new FlowLayout(FlowLayout.CENTER));
        if (usuario instanceof DTMedico) {
            JButton nuevoEstudio = new JButton("Nuevo estudio");
            nuevoEstudio.addActionListener(event -> {
                ocultarOpcionesOrden();
                altaPrestacion(true);
            });
            filaAccionesEspecificas.add(nuevoEstudio);

            JButton nuevaTerapia = new JButton("Nueva terapia");
            nuevaTerapia.addActionListener(event -> {
                ocultarOpcionesOrden();
                altaPrestacion(false);
            });
            filaAccionesEspecificas.add(nuevaTerapia);

            JButton modificar = new JButton("Modificar seleccionada");
            modificar.addActionListener(event -> {
                ocultarOpcionesOrden();
                modificarPrestacion();
            });
            filaAccionesEspecificas.add(modificar);

            JButton eliminar = new JButton("Eliminar seleccionada");
            eliminar.addActionListener(event -> {
                ocultarOpcionesOrden();
                eliminarPrestacion();
            });
            filaAccionesEspecificas.add(eliminar);
        } else {
            JButton seguidas = new JButton("Seguidas");
            seguidas.addActionListener(event -> elegirOpcionSeguidas());
            filaAccionesEspecificas.add(seguidas);

            JButton nuevaOrden = new JButton("Generar orden");
            nuevaOrden.addActionListener(event -> {
                ocultarOpcionesOrden();
                generarOrden();
            });
            filaAccionesEspecificas.add(nuevaOrden);

            JButton historial = new JButton("Historial");
            historial.addActionListener(event -> {
                ocultarOpcionesOrden();
                verHistorial();
            });
            filaAccionesEspecificas.add(historial);
        }

        acciones.add(filaAccionesEspecificas);
        add(acciones, BorderLayout.SOUTH);

        cargarCatalogo();
    }

    private void cerrarSesion() {
        dispose();
        new Login().setVisible(true);
    }

    private void cargarCatalogo() {
        String texto = campoBusqueda.getText() == null ? "" : campoBusqueda.getText().trim();
        List<DTPrestacion> prestaciones = texto.isEmpty()
                ? controlador.listarPrestaciones()
                : controlador.listarPrestacionesPorNombre(texto);
        mostrarPrestaciones(prestaciones);
    }

    private void cargarCatalogoPorPrecio(boolean ascendente) {
        mostrarPrestaciones(controlador.listarPrestacionesPorPrecio(ascendente));
    }

    private void elegirCriterioOrden() {
        Object[] opciones = { "Por precio", "Alfabéticamente" };
        int seleccion = JOptionPane.showOptionDialog(this, "Elegí el criterio de ordenamiento:",
                "Ordenar", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                opciones, opciones[0]);
        switch (seleccion) {
            case 0 -> mostrarOpcionesOrden(true);
            case 1 -> mostrarOpcionesOrden(false);
            default -> {
            }
        }
    }

    private void elegirOpcionSeguidas() {
        ocultarOpcionesOrden();
        Object[] opciones = { "Agregar", "Quitar", "Ver lista" };
        int seleccion = JOptionPane.showOptionDialog(this, "Elegí una acción:", "Seguidas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                opciones, opciones[2]);
        switch (seleccion) {
            case 0 -> seguirPrestacion();
            case 1 -> quitarPrestacionesSeguidas();
            case 2 -> verSeguidas();
            default -> {
            }
        }
    }

    private void mostrarOpcionesOrden(boolean porPrecio) {
        ordenarPorPrecio = porPrecio;
        direccionesOrden.setVisible(true);
        revalidate();
        repaint();
    }

    private void ocultarOpcionesOrden() {
        direccionesOrden.setVisible(false);
        revalidate();
        repaint();
    }

    private void cargarCatalogoOrdenado(boolean ascendente) {
        if (ordenarPorPrecio) {
            cargarCatalogoPorPrecio(ascendente);
        } else {
            mostrarPrestaciones(controlador.listarPrestacionesPorNombre(ascendente));
        }
    }

    private void mostrarPrestaciones(List<DTPrestacion> prestaciones) {
        modelo.setRowCount(0);
        for (DTPrestacion prestacion : prestaciones) {
            if (prestacion == null) {
                continue;
            }
            String tipo;
            if (prestacion.getClass() == DTEstudio.class) {
                tipo = "Estudio";
            } else if (prestacion.getClass() == DTTerapia.class) {
                tipo = "Terapia";
            } else {
                tipo = "Prestación";
            }
            modelo.addRow(new Object[] {
                    prestacion.getId(),
                    tipo,
                    prestacion.getNombre(),
                    prestacion.getPrecio(),
                        prestacion.getFranja()
            });
        }
    }

    private void verDetallesPrestacion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccioná una prestación");
            return;
        }
        Long id = ((Number) modelo.getValueAt(fila, 0)).longValue();
        DTPrestacion prestacion = controlador.obtenerPrestacion(id);
        if (prestacion.getClass() == DTEstudio.class) {
            DTEstudio estudio = (DTEstudio) prestacion;
            JOptionPane.showMessageDialog(this,
                        "Nombre: " + estudio.getNombre() + "\nDuración: "
                            + estudio.getDuracionMinutos() + " minutos",
                    "Detalles del estudio", JOptionPane.INFORMATION_MESSAGE);
        } else if (prestacion.getClass() == DTTerapia.class) {
            DTTerapia terapia = (DTTerapia) prestacion;
            String derivacion = terapia.isRequiereDerivacion() ? "Sí" : "No";
            JOptionPane.showMessageDialog(this,
                    "Nombre: " + terapia.getNombre() + "\nRequiere derivación: " + derivacion
                            + "\nCantidad de sesiones: " + terapia.getCantidadSesiones(),
                    "Detalles de la terapia", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void altaPrestacion(boolean estudio) {
        new AltaPrestacion(controlador, usuario.getEmail(), estudio).setVisible(true);
    }

    private void modificarPrestacion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccioná una prestación");
            return;
        }
        JTextField nombre = new JTextField((String) modelo.getValueAt(fila, 2));
        JTextField precio = new JTextField(String.valueOf(modelo.getValueAt(fila, 3)));
        JTextField franja = new JTextField(String.valueOf(modelo.getValueAt(fila, 4)));
        Object[] campos = { "Nombre", nombre, "Precio", precio, "Franja", franja };
        if (JOptionPane.showConfirmDialog(this, campos, "Modificar prestación",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            controlador.modificarPrestacion(
                    usuario.getEmail(),
                    (Long) modelo.getValueAt(fila, 0),
                    nombre.getText(),
                    Double.parseDouble(precio.getText()),
                    Franja.valueOf(franja.getText().toUpperCase()));
            cargarCatalogo();
        } catch (IllegalArgumentException | AccesoNoAutorizadoException
                | PrestacionRepetidaException exception) {
            mostrarError(exception.getMessage());
        }
    }

    private void eliminarPrestacion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccioná una prestación");
            return;
        }
        try {
            controlador.eliminarPrestacion(usuario.getEmail(), (Long) modelo.getValueAt(fila, 0));
            cargarCatalogo();
        } catch (AccesoNoAutorizadoException | PrestacionEnOrdenException exception) {
            mostrarError(exception.getMessage());
        }
    }

    private void seguirPrestacion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccioná una prestación para seguir");
            return;
        }
        Long id = (Long) modelo.getValueAt(fila, 0);
        try {
            controlador.agregarSeguido(usuario.getEmail(), id);
            JOptionPane.showMessageDialog(this, "Prestación agregada a seguidas");
        } catch (AccesoNoAutorizadoException | SeguidoRepetidoException exception) {
            mostrarError(exception.getMessage());
        }
    }

    private void verSeguidas() {
        try {
            List<DTSeguido> seguidas = controlador.listarSeguidas(usuario.getEmail());
            if (seguidas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No tenés prestaciones seguidas");
                return;
            }
            StringBuilder texto = new StringBuilder();
            for (DTSeguido seguido : seguidas) {
                texto.append("- ")
                        .append(seguido.getPrestacion().getNombre())
                        .append(" (seguida el ")
                        .append(seguido.getFecha().format(FORMATO_FECHA_SEGUIDO))
                        .append(")")
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, texto.toString(), "Prestaciones seguidas",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (AccesoNoAutorizadoException exception) {
            mostrarError(exception.getMessage());
        }
    }

    private void quitarPrestacionesSeguidas() {
        try {
            List<DTSeguido> seguidas = controlador.listarSeguidas(usuario.getEmail());
            if (seguidas.isEmpty()) {
                mostrarError("No tenés prestaciones seguidas");
                return;
            }

            JPanel seleccion = new JPanel(new GridLayout(0, 1, 8, 8));
            List<JCheckBox> seleccionadas = new java.util.ArrayList<>();
            for (DTSeguido seguido : seguidas) {
                JCheckBox checkbox = new JCheckBox(seguido.getPrestacion().getNombre());
                seleccion.add(checkbox);
                seleccionadas.add(checkbox);
            }

            Object[] campos = { "Seleccioná las prestaciones que querés quitar:", seleccion };
            if (JOptionPane.showConfirmDialog(this, campos, "Quitar prestaciones seguidas",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                return;
            }

            boolean quitoAlguna = false;
            for (int indice = 0; indice < seguidas.size(); indice++) {
                if (seleccionadas.get(indice).isSelected()) {
                    controlador.quitarSeguido(usuario.getEmail(),
                            seguidas.get(indice).getPrestacion().getId());
                    quitoAlguna = true;
                }
            }
            if (!quitoAlguna) {
                mostrarError("Seleccioná al menos una prestación");
                return;
            }
            JOptionPane.showMessageDialog(this, "Prestaciones quitadas correctamente");
        } catch (AccesoNoAutorizadoException exception) {
            mostrarError(exception.getMessage());
        }
    }

    private void generarOrden() {
        try {
            List<DTSeguido> seguidas = controlador.listarSeguidas(usuario.getEmail());
            if (seguidas.isEmpty()) {
                mostrarError("Debes seguir al menos una prestación para generar una orden");
                return;
            }

            JPanel seleccion = new JPanel(new GridLayout(0, 3, 8, 8));
            seleccion.add(new JLabel("Seleccionar"));
            seleccion.add(new JLabel("Prestación"));
            seleccion.add(new JLabel("Cantidad"));
            List<JCheckBox> seleccionadas = new java.util.ArrayList<>();
            List<JSpinner> cantidadesIngresadas = new java.util.ArrayList<>();
            for (DTSeguido seguido : seguidas) {
                JCheckBox checkbox = new JCheckBox();
                JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
                cantidad.setEnabled(false);
                checkbox.addActionListener(event -> cantidad.setEnabled(checkbox.isSelected()));
                seleccion.add(checkbox);
                seleccion.add(new JLabel(seguido.getPrestacion().getNombre()));
                seleccion.add(cantidad);
                seleccionadas.add(checkbox);
                cantidadesIngresadas.add(cantidad);
            }

            Object[] campos = { "Elegí las prestaciones y la cantidad:", seleccion };
            if (JOptionPane.showConfirmDialog(this, campos, "Nueva orden médica",
                    JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
                return;
            }

            Map<Long, Integer> cantidades = new HashMap<>();
            for (int indice = 0; indice < seguidas.size(); indice++) {
                if (seleccionadas.get(indice).isSelected()) {
                    cantidades.put(seguidas.get(indice).getPrestacion().getId(),
                            (Integer) cantidadesIngresadas.get(indice).getValue());
                }
            }
            controlador.confirmarOrdenMedica(usuario.getEmail(), cantidades);
            JOptionPane.showMessageDialog(this, "Orden médica confirmada");
        } catch (IllegalArgumentException | AccesoNoAutorizadoException | OrdenSinPrestacionesException exception) {
            mostrarError(exception.getMessage());
        }
    }

    private void verHistorial() {
        try {
            List<DTOrdenMedica> ordenes = controlador.listarOrdenesPaciente(usuario.getEmail());
            if (ordenes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todavía no tenés órdenes médicas");
                return;
            }
            StringBuilder texto = new StringBuilder();
            for (DTOrdenMedica orden : ordenes) {
                texto.append("Orden #")
                        .append(orden.getId())
                        .append("\n");
                for (DTLineaOrden linea : orden.getLineas()) {
                    texto.append("  • ")
                            .append(linea.getPrestacion().getNombre())
                            .append(" x")
                            .append(linea.getCantidad())
                            .append(" @ $")
                            .append(linea.getPrecioUnitario())
                            .append(" = $")
                            .append(linea.getSubtotal())
                            .append("\n");
                }
                texto.append("\n");
            }
            JOptionPane.showMessageDialog(this, texto.toString(), "Historial de órdenes",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (AccesoNoAutorizadoException exception) {
            mostrarError(exception.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}