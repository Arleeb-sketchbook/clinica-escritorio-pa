package publicadores;
import javax.swing.SwingUtilities;
import jakarta.xml.ws.Endpoint;
import presentacion.Login;

public class Servidor {
    public static void main(String[] args) {
        String url = System.getProperty("ws.url", "http://localhost:9876/controlador");
        Endpoint endpoint = Endpoint.publish(url, new ControladorPublish());
        Runtime.getRuntime().addShutdownHook(new Thread(endpoint::stop));
        System.out.println("Servicio publicado: " + url + "?wsdl");
        if (!java.awt.GraphicsEnvironment.isHeadless())
            SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
