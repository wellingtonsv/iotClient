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

/**
 * Classe Responsável por obsevar qualquer mudança na temperatura e umidade.
 * 
 * @author wvieira
 *
 */
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
	ResourceTemperature resourceTemperature;
	ResourceHumidity resourceHumidity;

	public CoapObserve(String name, HelloWorldServer server) {
		super(name);
		setObservable(true); // permitir a observação.
		setObserveType(Type.CON); // configurar o tipo de notificação para CONs.
		getAttributes().setObservable(); // marca como observável no formato de link.
		
		// Agendador de tarefa de atualização periódica, verifica a mudança. Tempo definido em mls.
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0L, 60000L);

		this.server = server; //Seta o servidor já instanciado na Classe HelloWordServer.
		this.resource = new HelloWorldResource(); // Inicializa Recurso de resposta do servidor.
		this.resourceTemperature = new ResourceTemperature(); //Inicializa o Recuso da temperatura.
		this.resourceHumidity = new ResourceHumidity();//Inicializa o Recurso da Umidade.
		// Adiciona o Recursos ao servidor Coap.
		this.server.add(new Resource[] { this.resource, this.resourceTemperature, this.resourceHumidity });
		this.controlGpioExample = new ControlGpioExample();// Instancia a classe da GPIO.
	}
	
	/**
	 * Classe interna que verifica a mudança de estado na temperatura e umidade.
	 * 
	 * @author wvieira
	 *
	 */
	private class UpdateTask extends TimerTask {
		
		private UpdateTask() {
		}
		
		//Atualização periódica do recurso
		public void run() {
			try {
				//Metodo interno para aferir a temperatura e umidade
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
		//Recupera do cliente Coap o valores para definir o valor padrão
		//para acionamento dos LED's.(25&30) 
		//25-Valor correspondente a temperatura
		//30-Valor correspondente a umidade
		String retrieve = exchange.getRequestText();
		System.out.println(retrieve);
		//Separação do valores...
		String[] tempHumi = retrieve.split("&");
		
		if (null != tempHumi[0]) {
			this.tempDefUser = tempHumi[0];
		} else {
			this.tempDefUser = "30.0"; //Define o valor para padrão da temperatura
		}
		
		System.out.println(this.tempDefUser);
		
		if (null != tempHumi[1]) {
			this.humiDefUser = tempHumi[1];
		} else {
			this.humiDefUser = "70.0"; //Define o valor para padrão da umidade
		}
		
		System.out.println(this.humiDefUser);
		
		/*
		 * Logica de desligar o LED 1 (vermelho), se eles estão em estado de ligado.
		 */
		if (this.ledOne) {
			System.out.println("Desligou o led 1...");
			try {
				this.controlGpioExample.offLedOne();
				this.ledOne = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * Logica de desligar o LED 2 (verde), se eles estão em estado de ligado.
		 */
		if (this.ledTwo) {
			System.out.println("Desligou o led 2...");
			try {
				this.controlGpioExample.offLedTwo();
				this.ledTwo = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		exchange.respond(ResponseCode.CHANGED);//Adiciona a mudança para ser notificada
		changed();//Notifica todos os observadores da mudança
	}

	/**
	 * Metodo que verifica a tempetura e humidade, para saber se estão dentro dos padrões.
	 * 
	 */
	private synchronized void getResultsTempHumi() throws IOException, ScriptException, InterruptedException {
		System.out.println("Entrou no metodo de verificar a Temperatura e Umidade....");
		
		/*
		 * Inicialização e verificação no Python, para acionar o sensor DHT
		 * 1º parametro determina o tipo de modelo de sensor (11)
		 * 2º parametro define a porta na GPIO (4)
		 */
		Process p = Runtime.getRuntime().exec("python /home/pi/Adafruit_Python_DHT/examples/AdafruitDHT.py 11 4");
		
		/*
		 * leia a saída do comando
		 */
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		if (stdInput != null) {
			//Saída padrão do comando.
			System.out.println("Aqui está a saída padrão do comando:\n");
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
			
			//System.out.println("Tamanho Lista: " + resultRegex.size());
			this.temp = resultRegex.get(0);
			this.humi = resultRegex.get(1);

			this.server.setAnswerfull("Temperatura: " + this.temp + "Cº" + "\tUmidade: " + this.humi + "%");
			//System.out.println("Obteve recurso: " + this.server.getAnswerfull());
			this.resource.setAnswer(this.server.getAnswerfull()); //Adiciona a resposta no HelloWorldResource 
			//para o HelloWorldServer Servido COAP.
			
			//Defensiva para não inicializar a GPIO novamente
			//Evita um exeção de IllegalStateException
			if (!this.controlGpioExample.isIniciou()) {
				this.controlGpioExample.setup();
			}
			
			/*
			 * Logica de verificar se a temperatura e humidade estão dentro do padrão 
			 * inicial ou dentro do valores definidos pelo usuario, atraves do cliente Coap.
			 */
			System.out.println("Temperatura padrão: " + Float.parseFloat(this.tempDefUser));
			if (Float.parseFloat(this.temp) > Float.parseFloat(this.tempDefUser)) {
				this.controlGpioExample.onLedone();
				changed(); //notificar todos os observadores
				this.ledOne = true;
			}
			
			System.out.println("Umidade padrão: " + Float.parseFloat(this.humiDefUser));
			if (Float.parseFloat(this.humi) > Float.parseFloat(this.humiDefUser)) {
				this.controlGpioExample.onLedTwo();
				changed();//notificar todos os observadores
				this.ledTwo = true;
			}
			
			this.resourceTemperature.setTemperatura(this.temp);
			//System.out.println(this.resourceTemperature.getTemperatura());
			this.resourceHumidity.setHumidade(this.humi);
			//System.out.println(this.resourceHumidity.getHumidade());
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