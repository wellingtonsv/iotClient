package org.eclipse.californium.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

public class CoapObserve extends CoapResource {
	private String temp;
	private String humi;
	private String tempDefUser = "30.0";
	private String humiDefUser = "70.0";
	private static String answer;
	HelloWorldServer server;
	HelloWorldResource resource;
	ControlGpioExample controlGpioExample;
	boolean ledOne = false;
	boolean ledTwo = false;

	public CoapObserve(String name, HelloWorldServer server) {
		super(name);
		setObservable(true);
		setObserveType(Type.CON);
		getAttributes().setObservable();

		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0L, 60000L);

		this.server = server;
		this.resource = new HelloWorldResource();
		this.server.add(new Resource[] { this.resource });
		this.controlGpioExample = new ControlGpioExample();
	}

	private class UpdateTask extends TimerTask {
		private UpdateTask() {
		}

		public void run() {
			try {
				CoapObserve.this.getResultsTempHumi();
			} catch (IOException | ScriptException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void handleGET(CoapExchange exchange) {
		exchange.setMaxAge(1L);
		exchange.respond("update");
	}

	public void handleDELETE(CoapExchange exchange) {
		delete();
		exchange.respond(ResponseCode.DELETED);
	}

	public void handlePUT(CoapExchange exchange) {
		String retrieve = exchange.getRequestText();
		System.out.println(retrieve);
		String[] tempHumi = retrieve.split("&");
		if (null != tempHumi[0]) {
			this.tempDefUser = tempHumi[0];
		} else {
			this.tempDefUser = "30.0";
		}
		System.out.println(this.tempDefUser);
		if (null != tempHumi[1]) {
			this.humiDefUser = tempHumi[1];
		} else {
			this.humiDefUser = "70.0";
		}
		System.out.println(this.humiDefUser);
		exchange.respond(ResponseCode.CHANGED);
		changed();
		
		if (this.ledOne) {
			System.out.println("Desligou o led 1...");
			try {
				this.controlGpioExample.offLedOne();
				this.ledOne = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (this.ledTwo) {
			System.out.println("Desligou o led 2...");
			try {
				this.controlGpioExample.offLedTwo();
				this.ledTwo = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		exchange.respond(ResponseCode.CHANGED);
		changed();
	}

	private synchronized void getResultsTempHumi() throws IOException, ScriptException, InterruptedException {
		System.out.println("Entrou no metodo de verificar a Temperatura e Umidade....");

		Process p = Runtime.getRuntime().exec("python /home/pi/Adafruit_Python_DHT/examples/AdafruitDHT.py 11 4");

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		if (stdInput != null) {
			System.out.println("Here is the standard output of the command:\n");
			Pattern pt = Pattern.compile("\\d+\\.\\d+");
			Matcher m = null;
			this.temp = "0.0";
			this.humi = "0.0";
			String answerTemp = null;
			while ((answer = stdInput.readLine()) != null) {
				System.out.println("Resultado do Python: " + answer);
				answerTemp = answer;
			}
			List<String> resultRegex = new ArrayList<String>();
			m = pt.matcher(answerTemp);
			while (m.find()) {
				resultRegex.add(m.group());
			}
			System.out.println("Tamanho Lista: " + resultRegex.size());
			this.temp = ((String) resultRegex.get(0));
			this.humi = ((String) resultRegex.get(1));

			this.server.setAnswerfull("Temperatura: " + this.temp + "Cº" + "\tUmidade: " + this.humi + "%");
			System.out.println("Obteve recurso: " + this.server.getAnswerfull());
			this.resource.setAnswer(this.server.getAnswerfull());
			if (!this.controlGpioExample.isIniciou()) {
				this.controlGpioExample.setup();
			}
			System.out.println("Temperatura padrão: " + Float.parseFloat(this.tempDefUser));
			if (Float.parseFloat(this.temp) > Float.parseFloat(this.tempDefUser)) {
				this.controlGpioExample.onLedone();
				changed();
				this.ledOne = true;
			}
			System.out.println("Umidade padrão: " + Float.parseFloat(this.humiDefUser));
			if (Float.parseFloat(this.humi) > Float.parseFloat(this.humiDefUser)) {
				this.controlGpioExample.onLedTwo();
				changed();
				this.ledTwo = true;
			}
		}
	}

	public String getTempDefUser() {
		return this.tempDefUser;
	}

	public void setTempDefUser(String tempDefUser) {
		this.tempDefUser = tempDefUser;
	}

	public String getHumiDefUser() {
		return this.humiDefUser;
	}

	public void setHumiDefUser(String humiDefUser) {
		this.humiDefUser = humiDefUser;
	}
}