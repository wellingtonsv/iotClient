package org.eclipse.californium.examples;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * Recurso para temperatura.
 * 
 * @author wvieira
 *
 */
public class ResourceHumidity extends CoapResource{
	
	private String humidade;

	public ResourceHumidity() {
		super("Umidade");
		getAttributes().setTitle("Recurso_Umidade");
	}

	public String getHumidade() {
		return humidade;
	}

	public void setHumidade(String humidade) {
		this.humidade = humidade;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		// respond to the request
		exchange.respond("Umidade: "+this.humidade);
	}
}