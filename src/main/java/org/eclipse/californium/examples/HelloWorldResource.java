package org.eclipse.californium.examples;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class HelloWorldResource extends CoapResource {
	
	private String answer;

	public HelloWorldResource() {

		// set resource identifier
		super("helloWorld");

		// set display name
		getAttributes().setTitle("Hello-World Resource");
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		// respond to the request
		exchange.respond("Response: "+this.answer);
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}