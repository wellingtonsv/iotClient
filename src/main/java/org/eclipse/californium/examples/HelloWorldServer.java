/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 *    Kai Hudalla (Bosch Software Innovations GmbH) - add endpoints for all IP addresses
 ******************************************************************************/
package org.eclipse.californium.examples;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;


public class HelloWorldServer extends CoapServer {

	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
	
	private String answerfull;
	
	/*
     * Application entry point.
     */
    public static void main(String[] args) {
    	HelloWorldServer server = null;
        try {

            // create server
            server = new HelloWorldServer();
            // add endpoints on all IP addresses
            server.addEndpoints();
            server.start();
            for(int i=0;i<server.getEndpoints().size();i++) {
            	System.out.println(server.getEndpoints().get(i).getAddress());
            	System.out.println(server.getRoot().getName());
            }
            server.setupObserve("helloObserve");//define um nome para ser observado 
            
            } catch (Exception e) {
            System.err.println("Falha ao iniciar o Servidor COAP: " + e.getMessage());
        }
    }

    /**
     * Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
     */
    private void addEndpoints() {
    	for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
    		// only binds to IPv4 addresses and localhost
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
    }

    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public HelloWorldServer() throws SocketException {
        
    }
    
    /**
     * Metodo que define o observado e adiciona ele dentro do servidor.
     * @param name
     */
    public void setupObserve(String name) {
    	System.out.println("Iniciou Observe <CoapObserve> ...");
    	add(new CoapObserve(name, this));
    }
    
    //Getters and Setters.
    public String getAnswerfull() {
		return answerfull;
	}

	public void setAnswerfull(String answerfull) {
		this.answerfull = answerfull;
	}
}