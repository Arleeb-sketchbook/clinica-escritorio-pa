package persistencia;

import java.util.List;

import jakarta.persistence.EntityManager;
import logica.Usuario;

public class UsuarioDAO {

	public void guardar(Usuario usuario) {
		ejecutarEnTransaccion(entityManager -> entityManager.persist(usuario));
	}

	public Usuario buscarPorEmail(String email) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			return entityManager.find(Usuario.class, email);
		}
	}

	public boolean existeEmail(String email) {
		return buscarPorEmail(email) != null;
	}

	public List<Usuario> listarTodos() {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			return entityManager.createQuery(
					"SELECT usuario FROM Usuario usuario ORDER BY usuario.email", Usuario.class)
					.getResultList();
		}
	}

	public Usuario actualizar(Usuario usuario) {
		try (EntityManager entityManager = Conexion.crearEntityManager()) {
			entityManager.getTransaction().begin();
			try {
				Usuario actualizado = entityManager.merge(usuario);
				entityManager.getTransaction().commit();
				return actualizado;
			} catch (RuntimeException exception) {
				rollback(entityManager);
				throw exception;
			}
		}
	}

	public void eliminarPorEmail(String email) {
		ejecutarEnTransaccion(entityManager -> {
			Usuario usuario = entityManager.find(Usuario.class, email);
			if (usuario != null) {
				entityManager.remove(usuario);
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
