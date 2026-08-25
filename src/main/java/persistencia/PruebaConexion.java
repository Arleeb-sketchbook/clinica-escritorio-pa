package persistencia;

import jakarta.persistence.EntityManager;

public class PruebaConexion {

    public static void main(String[] args) {
        EntityManager entityManager = null;
        try {
            entityManager = Conexion.crearEntityManager();
            System.out.println("Conexión a PostgreSQL establecida correctamente.");
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            Conexion.cerrar();
        }
    }
}
