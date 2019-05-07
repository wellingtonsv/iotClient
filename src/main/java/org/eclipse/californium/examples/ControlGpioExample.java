package org.eclipse.californium.examples;
/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  ControlGpioExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.concurrent.Future;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * This example code demonstrates how to perform simple state
 * control of a GPIO pin on the Raspberry Pi.
 *
 * @author Robert Savage
 */
public class ControlGpioExample {

	private GpioController gpio;
	private GpioPinDigitalOutput pin;
	private GpioPinDigitalOutput pin2;
	private boolean iniciou = false;
	
	/**
	 * Metodo de iniciar a GPIO
	 */
    public synchronized void setup() {

		try {
			System.out.println("<--Pi4J--> GPIO Control Example ... started.");

			// create gpio controller
			this.gpio = GpioFactory.getInstance();
			System.out.println("instance GpioController...");

			// provision gpio pin #01 #02 as an output pin and turn on
			this.pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.LOW);
			this.pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "MyLED", PinState.LOW);

			// set shutdown state for this pin
			this.pin.setShutdownOptions(true, PinState.LOW);
			this.pin2.setShutdownOptions(true, PinState.LOW);
			
			this.iniciou = true;
			System.out.println("Iniciou a GPIO: "+iniciou);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		//teste Leds
        // toggle the current state of gpio pin #01 (should turn on)
        //onLedone(pin);
        //onLedone(pin2);

        // toggle the current state of gpio pin #01  (should turn off)
        //offLed(pin);
        //offLed(pin2);

        // turn on gpio pin #01 for 1 second and then off
        //System.out.println("--> GPIO state should be: ON for only 1 second");
        //pulseLed(pin); // set second argument to 'true' use a blocking call
        //pulseLed(pin2);

        //shutdownGpio(gpio);

        //System.out.println("Exiting ControlGpioExample");
    }

	public Future<?> pulseLed(final GpioPinDigitalOutput pin) {
		return pin.pulse(1000, true);
	}

	public synchronized void shutdownGpio(final GpioController gpio) {
		// stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        gpio.shutdown();
        System.out.println("Exiting ControlGpioExample");
	}

	/**
	 * Desliga o LED one.
	 * @throws InterruptedException
	 */
	public synchronized void offLedOne() throws InterruptedException {
		this.pin.toggle();
		this.pin.setState(PinState.LOW);
        System.out.println("--> GPIO state should be led one: OFF");
        //Thread.sleep(2000);
	}
	
	/**
	 * Desliga o LED two.
	 * @throws InterruptedException
	 */
	public synchronized void offLedTwo() throws InterruptedException {
		this.pin2.toggle();
		this.pin2.setState(PinState.LOW);
        System.out.println("--> GPIO state should be led two: OFF");
        //Thread.sleep(2000);
	}
	
	/**
	 * Liga o LED one red
	 * @throws InterruptedException
	 */
	public synchronized void onLedone() throws InterruptedException {
		this.pin.toggle();
		this.pin.setState(PinState.HIGH);
        System.out.println("--> GPIO state should be led one: ON");
        //Thread.sleep(2000);
	}
	
	/**
	 * Aciona o led two green.
	 * @throws InterruptedException
	 */
	public synchronized void onLedTwo() throws InterruptedException {
		this.pin2.toggle();
		this.pin2.setState(PinState.HIGH);
        System.out.println("--> GPIO state should be two: ON");
        //Thread.sleep(2000);
	}
	
	/*
	 * Getters and Setters.
	 */
	public GpioController getGpio() {
		return gpio;
	}

	public void setGpio(GpioController gpio) {
		this.gpio = gpio;
	}

	public GpioPinDigitalOutput getPin() {
		return pin;
	}

	public void setPin(GpioPinDigitalOutput pin) {
		this.pin = pin;
	}

	public GpioPinDigitalOutput getPin2() {
		return pin2;
	}

	public void setPin2(GpioPinDigitalOutput pin2) {
		this.pin2 = pin2;
	}

	public boolean isIniciou() {
		return iniciou;
	}

	public void setIniciou(boolean iniciou) {
		this.iniciou = iniciou;
	}
}
//END SNIPPET: control-gpio-snippet