package logica;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Usuario {

	private static final int ITERACIONES = 120_000;
	private static final int TAMANO_SALT = 16;
	private static final int TAMANO_HASH = 256;

	@Id
	private String email;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String passwordHash;

	protected Usuario() {
	}

	protected Usuario(String email, String nombre, String password) {
		this.email = email;
		this.nombre = nombre;
		this.passwordHash = generarHash(password);
	}

	public String getEmail() {
		return email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean verificarPassword(String password) {
		String[] partes = passwordHash.split(":", 2);
		if (partes.length != 2) {
			return false;
		}

		try {
			byte[] salt = Base64.getDecoder().decode(partes[0]);
			byte[] hashEsperado = Base64.getDecoder().decode(partes[1]);
			byte[] hashIngresado = derivarHash(password, salt);
			return java.security.MessageDigest.isEqual(hashEsperado, hashIngresado);
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	private static String generarHash(String password) {
		byte[] salt = new byte[TAMANO_SALT];
		new SecureRandom().nextBytes(salt);
		byte[] hash = derivarHash(password, salt);
		return Base64.getEncoder().encodeToString(salt) + ":"
				+ Base64.getEncoder().encodeToString(hash);
	}

	private static byte[] derivarHash(String password, byte[] salt) {
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("La contraseña no puede estar vacía");
		}

		PBEKeySpec specification = new PBEKeySpec(
				password.toCharArray(), salt, ITERACIONES, TAMANO_HASH);
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			return factory.generateSecret(specification).getEncoded();
		} catch (GeneralSecurityException exception) {
			throw new IllegalStateException("No se pudo generar el hash", exception);
		} finally {
			specification.clearPassword();
		}
	}
}
