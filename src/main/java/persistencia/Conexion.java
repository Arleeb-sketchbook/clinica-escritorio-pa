package persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Conexion {

	private static final EntityManagerFactory FACTORIA =
			Persistence.createEntityManagerFactory("clinica");

	private Conexion() {
	}

	public static EntityManager crearEntityManager() {
		if (!FACTORIA.isOpen()) {
			throw new IllegalStateException("La conexión de persistencia está cerrada");
		}
		return FACTORIA.createEntityManager();
	}

	public static void cerrar() {
		if (FACTORIA.isOpen()) {
			FACTORIA.close();
		}
	}
}
