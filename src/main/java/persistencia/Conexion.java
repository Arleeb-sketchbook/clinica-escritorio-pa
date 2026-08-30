package persistencia;

import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Conexion {

	private static final String HOST = System.getProperty("db.host",
			System.getenv().getOrDefault("DB_HOST", "localhost"));
	private static final String USER = System.getProperty("db.user",
			System.getenv().getOrDefault("DB_USER", "postgres"));
	private static final String PASSWORD = System.getProperty("db.password",
			System.getenv().getOrDefault("DB_PASSWORD", "postgres"));

	private static final EntityManagerFactory FACTORIA = Persistence.createEntityManagerFactory(
			"clinica",
			Map.of(
					"jakarta.persistence.jdbc.url",
					"jdbc:postgresql://" + HOST + ":5432/clinica",
					"jakarta.persistence.jdbc.user", USER,
					"jakarta.persistence.jdbc.password", PASSWORD
			));

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
