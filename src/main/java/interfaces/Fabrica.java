package interfaces;

import logica.Controlador;

public final class Fabrica {

	private Fabrica() {
	}

	public static IControlador getControlador() {
		return new Controlador();
	}
}
