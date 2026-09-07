package presentacion;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import datatypes.DTEstudio;
import datatypes.DTPrestacion;
import datatypes.DTTerapia;
import interfaces.IControlador;

public class VerCatalogo extends JFrame {

    private final IControlador controlador;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[] { "ID", "Tipo", "Nombre", "Precio", "Franja", "Detalle" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public VerCatalogo(IControlador controlador) {
        super("Catálogo");
        this.controlador = controlador;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        JButton verDetalles = new JButton("Ver detalles");
        verDetalles.addActionListener(event -> verDetallesPrestacion());
        JPanel acciones = new JPanel();
        acciones.add(verDetalles);
        add(acciones, BorderLayout.SOUTH);
        cargarCatalogo();
    }

    private void cargarCatalogo() {
        modelo.setRowCount(0);
        List<DTPrestacion> prestaciones = controlador.listarPrestaciones();
        for (DTPrestacion prestacion : prestaciones) {
            if (prestacion == null) {
                continue;
            }

            String tipo;
            String detalle;
            if (prestacion.getClass() == DTEstudio.class) {
                DTEstudio estudio = (DTEstudio) prestacion;
                tipo = "Estudio";
                detalle = estudio.getDuracionMinutos() + " min";
            } else if (prestacion.getClass() == DTTerapia.class) {
                DTTerapia terapia = (DTTerapia) prestacion;
                tipo = "Terapia";
                detalle = terapia.getCantidadSesiones() + " sesiones";
            } else {
                tipo = "Prestación";
                detalle = "-";
            }

            modelo.addRow(new Object[] {
                    prestacion.getId(),
                    tipo,
                    prestacion.getNombre(),
                    prestacion.getPrecio(),
                    prestacion.getFranja(),
                    detalle
            });
        }
    }

    private void verDetallesPrestacion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccioná una prestación", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
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
}
