package org.eclipse.californium.examples;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.Request;

/**
 * Cliente COAP.
 * 
 * @author wvieira
 *
 */
public class ClientCOAP {

	public static void main(String[] args) {
		URI uri = null; // URI parameter of the request

		try {
			uri = new URI(args[0]);
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		}
		
		CoapClient client = new CoapClient(uri);

		CoapResponse response = client.get();
		
		if (response!=null) {
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
		}
		
		Request request = new Request(Code.GET);
		CoapResponse coapResp = client.advanced(request);
		System.out.println(Utils.prettyPrint(coapResp));
		Request requestOne = new Request(Code.GET);
		CoapResponse coapRespOne = client.advanced(requestOne);
		System.out.println(Utils.prettyPrint(coapRespOne));
	}
}