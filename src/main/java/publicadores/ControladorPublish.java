package publicadores;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import datatypes.*;
import excepciones.*;
import interfaces.Fabrica;
import interfaces.IControlador;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

/** Adaptador SOAP: traduce datos y delega las reglas en la lógica de la parte 1. */
@WebService
public class ControladorPublish {
    private final IControlador icon = Fabrica.getControlador();

    @WebMethod
    public void registrarMedico(String email, String nombre, String password, String especialidad)
            throws UsuarioRepetidoException {
        icon.registrarMedico(email, nombre, password, especialidad);
    }
    @WebMethod
    public void registrarPaciente(String email, String nombre, String password, String mutualista)
            throws UsuarioRepetidoException {
        icon.registrarPaciente(email, nombre, password, mutualista);
    }
    @WebMethod
    public UsuarioWS iniciarSesion(String email, String password) throws CredencialesInvalidasException {
        DTUsuario dt = icon.iniciarSesion(email, password);
        UsuarioWS ws = new UsuarioWS();
        ws.setEmail(dt.getEmail()); ws.setNombre(dt.getNombre());
        ws.setTipo(dt instanceof DTMedico ? TipoUsuario.MEDICO : TipoUsuario.PACIENTE);
        return ws;
    }
    @WebMethod
    public void agregarPrestacion(String emailMedico, PrestacionWS ws)
            throws AccesoNoAutorizadoException, PrestacionRepetidaException {
        if (ws.getTipo() == TipoPrestacion.ESTUDIO)
            icon.altaEstudio(emailMedico, ws.getNombre(), ws.getPrecio(), ws.getFranja(),
                    ws.getDuracionMinutos() == null ? 0 : ws.getDuracionMinutos());
        else
            icon.altaTerapia(emailMedico, ws.getNombre(), ws.getPrecio(), ws.getFranja(),
                    ws.isRequiereDerivacion(), ws.getCantidadSesiones() == null ? 0 : ws.getCantidadSesiones());
    }
    @WebMethod
    public void modificarPrestacion(String emailMedico, PrestacionWS ws)
            throws AccesoNoAutorizadoException, PrestacionRepetidaException {
        icon.modificarPrestacion(emailMedico, ws.getId(), ws.getNombre(), ws.getPrecio(), ws.getFranja());
    }
    @WebMethod
    public void eliminarPrestacion(String emailMedico, Long id)
            throws AccesoNoAutorizadoException, PrestacionEnOrdenException {
        icon.eliminarPrestacion(emailMedico, id);
    }
    @WebMethod
    public List<PrestacionWS> listarCatalogo(OrdenPrestacion orden) {
        List<DTPrestacion> datos = orden == OrdenPrestacion.PRECIO
                ? icon.listarPrestacionesPorPrecio(true) : icon.listarPrestacionesPorNombre(true);
        return datos.stream().map(this::prestacionWS).toList();
    }
    @WebMethod
    public List<PrestacionWS> buscarPrestaciones(String texto) {
        return icon.listarPrestacionesPorNombre(texto).stream().map(this::prestacionWS).toList();
    }
    @WebMethod
    public PrestacionWS obtenerPrestacion(Long id) {
        DTPrestacion dt = icon.obtenerPrestacion(id);
        return dt == null ? null : prestacionWS(dt);
    }
    @WebMethod
    public void agregarSeguido(String email, Long id)
            throws AccesoNoAutorizadoException, SeguidoRepetidoException { icon.agregarSeguido(email, id); }
    @WebMethod
    public void quitarSeguido(String email, Long id)
            throws AccesoNoAutorizadoException { icon.quitarSeguido(email, id); }
    @WebMethod
    public List<SeguidoWS> listarSeguidas(String email) throws AccesoNoAutorizadoException {
        List<SeguidoWS> resultado = new ArrayList<>();
        for (DTSeguido dt : icon.listarSeguidas(email)) {
            SeguidoWS ws = new SeguidoWS();
            ws.setPrestacion(prestacionWS(dt.getPrestacion())); ws.setFecha(dt.getFecha().toString());
            resultado.add(ws);
        }
        return resultado;
    }
    @WebMethod
    public void confirmarOrdenMedica(String email, List<ItemSolicitudWS> items)
            throws AccesoNoAutorizadoException, OrdenSinPrestacionesException {
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        if (items != null)
            for (ItemSolicitudWS item : items) cantidades.put(item.getIdPrestacion(), item.getCantidad());
        icon.confirmarOrdenMedica(email, cantidades);
    }
    @WebMethod
    public int eliminarTodasOrdenesMedicas(String emailMedico)
            throws AccesoNoAutorizadoException {
        return icon.eliminarTodasOrdenesMedicas(emailMedico);
    }
    @WebMethod
    public List<OrdenMedicaWS> listarOrdenes(String email) throws AccesoNoAutorizadoException {
        List<OrdenMedicaWS> resultado = new ArrayList<>();
        for (DTOrdenMedica dt : icon.listarOrdenesPaciente(email)) {
            OrdenMedicaWS ws = new OrdenMedicaWS();
            ws.setId(dt.getId()); ws.setFecha(dt.getFecha().withSecond(0).withNano(0).toString());
            double total = 0;
            for (DTLineaOrden linea : dt.getLineas()) {
                LineaOrdenWS l = new LineaOrdenWS();
                l.setNombrePrestacion(linea.getPrestacion().getNombre()); l.setCantidad(linea.getCantidad());
                l.setPrecioUnitario(linea.getPrecioUnitario()); l.setSubtotal(linea.getSubtotal());
                total += linea.getSubtotal(); ws.getLineas().add(l);
            }
            ws.setTotal(total); resultado.add(ws);
        }
        return resultado;
    }
    private PrestacionWS prestacionWS(DTPrestacion dt) {
        PrestacionWS ws = new PrestacionWS();
        ws.setId(dt.getId()); ws.setNombre(dt.getNombre()); ws.setPrecio(dt.getPrecio()); ws.setFranja(dt.getFranja());
        if (dt instanceof DTEstudio estudio) {
            ws.setTipo(TipoPrestacion.ESTUDIO); ws.setDuracionMinutos(estudio.getDuracionMinutos());
        } else if (dt instanceof DTTerapia terapia) {
            ws.setTipo(TipoPrestacion.TERAPIA); ws.setCantidadSesiones(terapia.getCantidadSesiones());
            ws.setRequiereDerivacion(terapia.isRequiereDerivacion());
        }
        return ws;
    }
}
