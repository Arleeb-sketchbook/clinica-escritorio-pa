package presentacion;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
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
}
