package com.casmall.serial;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CmSerialManager {
	protected static Log log = LogFactory.getLog(CmSerialManager.class);
	private static HashMap<String, CmSerial> insMap = new HashMap<String, CmSerial>();
	private static final String DEFAULT_ID = "DEFAULT";

	/**
	 * 싱글톤 객체 반환
	 * 
	 * @return CmSerial
	 */
	public static synchronized CmSerial getInstance() throws IOException {
		if (insMap.get(DEFAULT_ID) == null) {
			insMap.put(DEFAULT_ID, CmSerial.getInstance());
		}
		return (CmSerial) insMap.get(DEFAULT_ID);
	}

	/**
	 * 싱글톤 객체 반환
	 * 
	 * @return CmSerial
	 */
	public static synchronized CmSerial getInstance(String ID) throws IOException {
		if (insMap.get(ID) == null) {
			insMap.put(ID, CmSerial.getInstance(ID));
		}
		return (CmSerial) insMap.get(ID);
	}
}
