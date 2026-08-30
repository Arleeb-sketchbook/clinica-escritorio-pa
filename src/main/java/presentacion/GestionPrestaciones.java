package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import datatypes.DTUsuario;
import interfaces.IControlador;

public class GestionPrestaciones extends JFrame {

    public GestionPrestaciones(IControlador controlador, DTUsuario usuario) {
        super("Gestión de prestaciones");

        final IControlador control = controlador;
        final DTUsuario usuarioActual = usuario;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new FlowLayout());

        JButton alta = new JButton("Alta");
        alta.addActionListener(event -> new AltaPrestacion(control, usuarioActual.getEmail()).setVisible(true));

        JButton modificar = new JButton("Modificar");
        modificar.addActionListener(event -> new ModificarPrestacion(control, usuarioActual.getEmail()).setVisible(true));

        JButton verCatalogo = new JButton("Ver catálogo");
        verCatalogo.addActionListener(event -> new VerCatalogo(control).setVisible(true));

        panel.add(alta);
        panel.add(modificar);
        panel.add(verCatalogo);

        add(panel, BorderLayout.CENTER);
    }
}
