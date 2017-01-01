package com.casmall.serial.read;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author 아라한
 *
 */
public class ReadThread implements Runnable, SerialPortEventListener {
	/** Read Buffer size */
	private final int READ_BUFF_SIZE = 1024;
	/** read wait time */
	private final int READ_WAIT_TIME = 100;
	InputStream inputStream;
	
	UserListener ul;

	public ReadThread(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void UserListener(UserListener sr) {
		this.ul = sr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
		}
	}

	/* (non-Javadoc)
	 * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
	 */
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;

		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[READ_BUFF_SIZE];
			try {
				Thread.sleep(READ_WAIT_TIME);
				while (inputStream.available() > 0) {
					inputStream.read(readBuffer);
				}
				if(ul != null)
					ul.event(readBuffer);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			break;
		}
	}
}
