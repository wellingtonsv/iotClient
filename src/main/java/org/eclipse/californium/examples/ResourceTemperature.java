package org.eclipse.californium.examples;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * Recurso para temperatura.
 * 
 * @author wvieira
 *
 */
public class ResourceTemperature extends CoapResource{
	
	private String temperatura;

	public ResourceTemperature() {
		super("Temperatura");
		getAttributes().setTitle("Recurso_Temperatura");
	}

	public String getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(String temperatura) {
		this.temperatura = temperatura;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		// respond to the request
		exchange.respond("Temperatura: "+this.temperatura);
	}
}