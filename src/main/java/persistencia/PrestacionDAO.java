package persistencia;

import java.util.List;

import jakarta.persistence.EntityManager;
import logica.Prestacion;

public class PrestacionDAO {

	public void guardar(Prestacion prestacion) {
		ejecutarEnTransaccion(entityManager -> entityManager.persist(prestacion));
	}

	public Prestacion buscarPorId(Long id) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			return entityManager.find(Prestacion.class, id);
		}
	}

	public boolean existeNombre(String nombre) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			Long cantidad = entityManager.createQuery(
					"SELECT COUNT(prestacion) FROM Prestacion prestacion "
							+ "WHERE LOWER(prestacion.nombre) = LOWER(:nombre)", Long.class)
					.setParameter("nombre", nombre)
					.getSingleResult();
			return cantidad > 0;
		}
	}

	public List<Prestacion> listarTodas() {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			return entityManager.createQuery(
					"SELECT prestacion FROM Prestacion prestacion ORDER BY prestacion.nombre",
					Prestacion.class).getResultList();
		}
	}

	public Prestacion actualizar(Prestacion prestacion) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			entityManager.getTransaction().begin();
			try {
				Prestacion actualizada = entityManager.merge(prestacion);
				entityManager.getTransaction().commit();
				return actualizada;
			} catch (RuntimeException exception) {
				rollback(entityManager);
				throw exception;
			}
		}
	}

	public void eliminarPorId(Long id) {
		ejecutarEnTransaccion(entityManager -> {
			Prestacion prestacion = entityManager.find(Prestacion.class, id);
			if (prestacion != null) {
				entityManager.remove(prestacion);
			}
		});
	}

	private void ejecutarEnTransaccion(java.util.function.Consumer<EntityManager> accion) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			entityManager.getTransaction().begin();
			try {
				accion.accept(entityManager);
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
