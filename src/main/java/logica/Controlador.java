package logica;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import datatypes.DTEstudio;
import datatypes.DTLineaOrden;
import datatypes.DTMedico;
import datatypes.DTOrdenMedica;
import datatypes.DTPaciente;
import datatypes.DTPrestacion;
import datatypes.DTSeguido;
import datatypes.DTTerapia;
import datatypes.DTUsuario;
import excepciones.AccesoNoAutorizadoException;
import excepciones.CredencialesInvalidasException;
import excepciones.OrdenSinPrestacionesException;
import excepciones.PrestacionEnOrdenException;
import excepciones.PrestacionRepetidaException;
import excepciones.SeguidoRepetidoException;
import excepciones.UsuarioRepetidoException;
import interfaces.IControlador;
import persistencia.OrdenMedicaDAO;
import persistencia.PrestacionDAO;
import persistencia.SeguidoDAO;
import persistencia.UsuarioDAO;

public class Controlador implements IControlador {

	private final UsuarioDAO usuarioDAO = new UsuarioDAO();
	private final PrestacionDAO prestacionDAO = new PrestacionDAO();
	private final SeguidoDAO seguidoDAO = new SeguidoDAO();
	private final OrdenMedicaDAO ordenMedicaDAO = new OrdenMedicaDAO();

	@Override
	public void registrarMedico(String email, String nombre, String password, String especialidad)
			throws UsuarioRepetidoException {
		validarEmailDisponible(email);
		usuarioDAO.guardar(new Medico(email, nombre, password, especialidad));
	}

	@Override
	public void registrarPaciente(String email, String nombre, String password, String mutualista)
			throws UsuarioRepetidoException {
		validarEmailDisponible(email);
		usuarioDAO.guardar(new Paciente(email, nombre, password, mutualista));
	}

	@Override
	public DTUsuario iniciarSesion(String email, String password)
			throws CredencialesInvalidasException {
		Usuario usuario = usuarioDAO.buscarPorEmail(email);
		if (usuario == null || !usuario.verificarPassword(password)) {
			throw new CredencialesInvalidasException("Email o contraseña incorrectos");
		}
		return convertirUsuario(usuario);
	}

	@Override
	public Long altaEstudio(String emailMedico, String nombre, double precio, Franja franja,
			int duracionMinutos) throws AccesoNoAutorizadoException, PrestacionRepetidaException {
		verificarMedico(emailMedico);
		validarNombreDisponible(nombre);
		Estudio estudio = new Estudio(nombre, precio, franja, duracionMinutos);
		prestacionDAO.guardar(estudio);
		return estudio.getId();
	}

	@Override
	public Long altaTerapia(String emailMedico, String nombre, double precio, Franja franja,
			boolean requiereDerivacion, int cantidadSesiones)
			throws AccesoNoAutorizadoException, PrestacionRepetidaException {
		verificarMedico(emailMedico);
		validarNombreDisponible(nombre);
		Terapia terapia = new Terapia(nombre, precio, franja, requiereDerivacion, cantidadSesiones);
		prestacionDAO.guardar(terapia);
		return terapia.getId();
	}

	@Override
	public List<DTPrestacion> listarPrestaciones() {
		return convertirLista(prestacionDAO.listarTodas());
	}

	@Override
	public List<DTPrestacion> listarPrestacionesPorNombre(String texto) {
		String filtro = texto == null ? "" : texto.trim().toLowerCase();
		return ordenarPorNombre(prestacionDAO.listarTodas().stream()
				.filter(p -> filtro.isEmpty() || p.getNombre().toLowerCase().contains(filtro))
				.toList(), true);
	}

	@Override
	public List<DTPrestacion> listarPrestacionesPorNombre(boolean ascendente) {
		return ordenarPorNombre(prestacionDAO.listarTodas(), ascendente);
	}

	private List<DTPrestacion> ordenarPorNombre(List<Prestacion> prestaciones, boolean ascendente) {
		Comparator<Prestacion> comparador = Comparator.comparing(Prestacion::getNombre,
				String.CASE_INSENSITIVE_ORDER);
		if (!ascendente) {
			comparador = comparador.reversed();
		}
		return prestaciones.stream()
				.sorted(comparador)
				.map(this::convertirPrestacion)
				.toList();
	}

	@Override
	public List<DTPrestacion> listarPrestacionesPorPrecio(boolean ascendente) {
		Comparator<Prestacion> comparador = Comparator.comparingDouble(Prestacion::getPrecio);
		if (!ascendente) {
			comparador = comparador.reversed();
		}
		return prestacionDAO.listarTodas().stream()
				.sorted(comparador)
				.map(this::convertirPrestacion)
				.toList();
	}

	@Override
	public DTPrestacion obtenerPrestacion(Long id) {
		Prestacion prestacion = prestacionDAO.buscarPorId(id);
		return prestacion == null ? null : convertirPrestacion(prestacion);
	}

	@Override
	public void modificarPrestacion(String emailMedico, Long id, String nombre, double precio,
			Franja franja) throws AccesoNoAutorizadoException, PrestacionRepetidaException {
		verificarMedico(emailMedico);
		Prestacion prestacion = prestacionDAO.buscarPorId(id);
		if (prestacion == null) {
			throw new IllegalArgumentException("La prestación no existe");
		}
		if (!prestacion.getNombre().equalsIgnoreCase(nombre) && prestacionDAO.existeNombre(nombre)) {
			throw new PrestacionRepetidaException("Ya existe una prestación con ese nombre");
		}
		prestacion.setNombre(nombre);
		prestacion.setPrecio(precio);
		prestacion.setFranja(franja);
		prestacionDAO.actualizar(prestacion);
	}

	@Override
	public void eliminarPrestacion(String emailMedico, Long id)
			throws AccesoNoAutorizadoException, PrestacionEnOrdenException {
		verificarMedico(emailMedico);
		if (ordenMedicaDAO.existeLineaParaPrestacion(id)) {
			throw new PrestacionEnOrdenException(
					"No se puede eliminar una prestación incluida en una orden");
		}
		seguidoDAO.eliminarPorPrestacion(id);
		prestacionDAO.eliminarPorId(id);
	}

	@Override
	public void agregarSeguido(String emailPaciente, Long prestacionId)
			throws AccesoNoAutorizadoException, SeguidoRepetidoException {
		Paciente paciente = obtenerPaciente(emailPaciente);
		Prestacion prestacion = buscarPrestacion(prestacionId);
		if (seguidoDAO.existe(paciente, prestacion)) {
			throw new SeguidoRepetidoException("El paciente ya sigue esta prestación");
		}
		seguidoDAO.guardar(new Seguido(paciente, prestacion));
	}

	@Override
	public void quitarSeguido(String emailPaciente, Long prestacionId)
			throws AccesoNoAutorizadoException {
		Paciente paciente = obtenerPaciente(emailPaciente);
		Prestacion prestacion = buscarPrestacion(prestacionId);
		seguidoDAO.eliminar(paciente, prestacion);
	}

	@Override
	public List<DTSeguido> listarSeguidas(String emailPaciente)
			throws AccesoNoAutorizadoException {
		obtenerPaciente(emailPaciente);
		List<DTSeguido> resultado = new ArrayList<>();
		for (Seguido seguido : seguidoDAO.listarPorPaciente(emailPaciente)) {
			resultado.add(new DTSeguido(seguido.getId(), convertirPrestacion(seguido.getPrestacion()),
					seguido.getFecha()));
		}
		return resultado;
	}

	@Override
	public void confirmarOrdenMedica(String emailPaciente, Map<Long, Integer> cantidades)
			throws AccesoNoAutorizadoException, OrdenSinPrestacionesException {
		Paciente paciente = obtenerPaciente(emailPaciente);
		if (cantidades == null || cantidades.isEmpty()) {
			throw new OrdenSinPrestacionesException("La orden médica no tiene prestaciones");
		}

		OrdenMedica orden = new OrdenMedica(paciente);
		for (Map.Entry<Long, Integer> entrada : cantidades.entrySet()) {
			if (entrada.getValue() == null || entrada.getValue() <= 0) {
				continue;
			}
			Prestacion prestacion = buscarPrestacion(entrada.getKey());
			orden.agregarLinea(prestacion, entrada.getValue());
		}
		if (orden.getLineas().isEmpty()) {
			throw new OrdenSinPrestacionesException("La orden médica no tiene prestaciones");
		}

		ordenMedicaDAO.guardar(orden);
	}

	@Override
	public List<DTOrdenMedica> listarOrdenesPaciente(String emailPaciente)
			throws AccesoNoAutorizadoException {
		obtenerPaciente(emailPaciente);
		List<DTOrdenMedica> resultado = new ArrayList<>();
		for (OrdenMedica orden : ordenMedicaDAO.listarPorPaciente(emailPaciente)) {
			List<DTLineaOrden> lineas = orden.getLineas().stream()
					.map(linea -> new DTLineaOrden(
							linea.getId(),
							convertirPrestacion(linea.getPrestacion()),
							linea.getCantidad(),
							linea.getPrecioUnitario(),
							linea.getSubtotal()))
					.toList();
			resultado.add(new DTOrdenMedica(orden.getId(), orden.getPaciente().getEmail(), orden.getFecha(), lineas));
		}
		return resultado;
	}

	private void validarEmailDisponible(String email) throws UsuarioRepetidoException {
		if (usuarioDAO.existeEmail(email)) {
			throw new UsuarioRepetidoException("Ya existe un usuario con ese email");
		}
	}

	private void validarNombreDisponible(String nombre) throws PrestacionRepetidaException {
		if (prestacionDAO.existeNombre(nombre)) {
			throw new PrestacionRepetidaException("Ya existe una prestación con ese nombre");
		}
	}

	private void verificarMedico(String email) throws AccesoNoAutorizadoException {
		if (!(usuarioDAO.buscarPorEmail(email) instanceof Medico)) {
			throw new AccesoNoAutorizadoException("Solo un médico puede administrar prestaciones");
		}
	}

	private Paciente obtenerPaciente(String email) throws AccesoNoAutorizadoException {
		Usuario usuario = usuarioDAO.buscarPorEmail(email);
		if (usuario instanceof Paciente paciente) {
			return paciente;
		}
		throw new AccesoNoAutorizadoException("Solo un paciente puede gestionar sus seguidas");
	}

	private Prestacion buscarPrestacion(Long id) {
		Prestacion prestacion = prestacionDAO.buscarPorId(id);
		if (prestacion == null) {
			throw new IllegalArgumentException("La prestación no existe");
		}
		return prestacion;
	}

	private List<DTPrestacion> convertirLista(List<Prestacion> prestaciones) {
		List<DTPrestacion> resultado = new ArrayList<>();
		for (Prestacion prestacion : prestaciones) {
			resultado.add(convertirPrestacion(prestacion));
		}
		return resultado;
	}

	private DTUsuario convertirUsuario(Usuario usuario) {
		if (usuario instanceof Medico medico) {
			return new DTMedico(medico.getEmail(), medico.getNombre(), medico.getEspecialidad());
		}
		if (usuario instanceof Paciente paciente) {
			return new DTPaciente(paciente.getEmail(), paciente.getNombre(), paciente.getMutualista());
		}
		throw new IllegalStateException("Tipo de usuario no reconocido");
	}

	private DTPrestacion convertirPrestacion(Prestacion prestacion) {
		if (prestacion instanceof Estudio estudio) {
			return new DTEstudio(estudio.getId(), estudio.getNombre(), estudio.getPrecio(),
					estudio.getFranja(), estudio.getDuracionMinutos());
		}
		if (prestacion instanceof Terapia terapia) {
			return new DTTerapia(terapia.getId(), terapia.getNombre(), terapia.getPrecio(),
					terapia.getFranja(), terapia.isRequiereDerivacion(), terapia.getCantidadSesiones());
		}
		throw new IllegalStateException("Tipo de prestación no reconocido");
	}
}
