package interfaces;

import java.util.List;
import java.util.Map;

import datatypes.DTOrdenMedica;
import datatypes.DTPrestacion;
import datatypes.DTSeguido;
import datatypes.DTUsuario;
import excepciones.AccesoNoAutorizadoException;
import excepciones.CredencialesInvalidasException;
import excepciones.OrdenSinPrestacionesException;
import excepciones.PrestacionEnOrdenException;
import excepciones.PrestacionRepetidaException;
import excepciones.SeguidoRepetidoException;
import excepciones.UsuarioRepetidoException;
import logica.Franja;

public interface IControlador {
    void registrarMedico(String email, String nombre, String password, String especialidad)
            throws UsuarioRepetidoException;

    void registrarPaciente(String email, String nombre, String password, String mutualista)
            throws UsuarioRepetidoException;

    DTUsuario iniciarSesion(String email, String password) throws CredencialesInvalidasException;

    Long altaEstudio(String emailMedico, String nombre, double precio, Franja franja,
            int duracionMinutos) throws AccesoNoAutorizadoException, PrestacionRepetidaException;

    Long altaTerapia(String emailMedico, String nombre, double precio, Franja franja,
            boolean requiereDerivacion, int cantidadSesiones)
            throws AccesoNoAutorizadoException, PrestacionRepetidaException;

    List<DTPrestacion> listarPrestaciones();

    List<DTPrestacion> listarPrestacionesPorNombre(String texto);

        List<DTPrestacion> listarPrestacionesPorNombre(boolean ascendente);

    List<DTPrestacion> listarPrestacionesPorPrecio(boolean ascendente);

    DTPrestacion obtenerPrestacion(Long id);

    void modificarPrestacion(String emailMedico, Long id, String nombre, double precio,
            Franja franja) throws AccesoNoAutorizadoException, PrestacionRepetidaException;

    void eliminarPrestacion(String emailMedico, Long id)
            throws AccesoNoAutorizadoException, PrestacionEnOrdenException;

    void agregarSeguido(String emailPaciente, Long prestacionId)
            throws AccesoNoAutorizadoException, SeguidoRepetidoException;

    void quitarSeguido(String emailPaciente, Long prestacionId)
            throws AccesoNoAutorizadoException;

    List<DTSeguido> listarSeguidas(String emailPaciente)
            throws AccesoNoAutorizadoException;

    void confirmarOrdenMedica(String emailPaciente, Map<Long, Integer> cantidades)
            throws AccesoNoAutorizadoException, OrdenSinPrestacionesException;

    List<DTOrdenMedica> listarOrdenesPaciente(String emailPaciente)
            throws AccesoNoAutorizadoException;
}