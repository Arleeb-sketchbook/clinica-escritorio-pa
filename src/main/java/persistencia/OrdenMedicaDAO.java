package persistencia;

import java.util.List;

import jakarta.persistence.EntityManager;
import logica.OrdenMedica;

public class OrdenMedicaDAO {

	public void guardar(OrdenMedica orden) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			entityManager.getTransaction().begin();
			try {
				entityManager.persist(orden);
				entityManager.getTransaction().commit();
			} catch (RuntimeException exception) {
				rollback(entityManager);
				throw exception;
			}
		}
	}

	public List<OrdenMedica> listarPorPaciente(String email) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			return entityManager.createQuery(
					"SELECT DISTINCT orden FROM OrdenMedica orden "
							+ "LEFT JOIN FETCH orden.lineas lineas "
							+ "LEFT JOIN FETCH lineas.prestacion "
							+ "WHERE orden.paciente.email = :email ORDER BY orden.fecha DESC",
					OrdenMedica.class)
					.setParameter("email", email)
					.getResultList();
		}
	}

	public boolean existeLineaParaPrestacion(Long prestacionId) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			Long cantidad = entityManager.createQuery(
					"SELECT COUNT(linea) FROM LineaOrden linea "
							+ "WHERE linea.prestacion.id = :prestacionId", Long.class)
					.setParameter("prestacionId", prestacionId)
					.getSingleResult();
			return cantidad > 0;
		}
	}

	private void rollback(EntityManager entityManager) {
		if (entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().rollback();
		}
	}
}
