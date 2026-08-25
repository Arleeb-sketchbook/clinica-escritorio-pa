package persistencia;

import java.util.List;

import jakarta.persistence.EntityManager;
import logica.Paciente;
import logica.Prestacion;
import logica.Seguido;

public class SeguidoDAO {

	public void guardar(Seguido seguido) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			entityManager.getTransaction().begin();
			try {
				entityManager.persist(seguido);
				entityManager.getTransaction().commit();
			} catch (RuntimeException exception) {
				rollback(entityManager);
				throw exception;
			}
		}
	}

	public boolean existe(Paciente paciente, Prestacion prestacion) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			Long cantidad = entityManager.createQuery(
					"SELECT COUNT(seguido) FROM Seguido seguido "
							+ "WHERE seguido.paciente.email = :email "
							+ "AND seguido.prestacion.id = :id", Long.class)
					.setParameter("email", paciente.getEmail())
					.setParameter("id", prestacion.getId())
					.getSingleResult();
			return cantidad > 0;
		}
	}

	public List<Seguido> listarPorPaciente(String email) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			return entityManager.createQuery(
					"SELECT seguido FROM Seguido seguido "
							+ "JOIN FETCH seguido.prestacion "
							+ "WHERE seguido.paciente.email = :email ORDER BY seguido.fecha DESC",
					Seguido.class)
					.setParameter("email", email)
					.getResultList();
		}
	}

	public void eliminarPorPrestacion(Long prestacionId) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			entityManager.getTransaction().begin();
			try {
				entityManager.createQuery("DELETE FROM Seguido seguido "
						+ "WHERE seguido.prestacion.id = :prestacionId")
						.setParameter("prestacionId", prestacionId)
						.executeUpdate();
				entityManager.getTransaction().commit();
			} catch (RuntimeException exception) {
				rollback(entityManager);
				throw exception;
			}
		}
	}

	public void eliminar(Paciente paciente, Prestacion prestacion) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			entityManager.getTransaction().begin();
			try {
				entityManager.createQuery("DELETE FROM Seguido seguido "
						+ "WHERE seguido.paciente.email = :email "
						+ "AND seguido.prestacion.id = :id")
						.setParameter("email", paciente.getEmail())
						.setParameter("id", prestacion.getId())
						.executeUpdate();
				entityManager.getTransaction().commit();
			} catch (RuntimeException exception) {
				rollback(entityManager);
				throw exception;
			}
		}
	}

	private void rollback(EntityManager entityManager) {
		if (entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().rollback();
		}
	}
}
