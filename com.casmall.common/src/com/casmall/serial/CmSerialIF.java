package com.casmall.serial;

import java.io.IOException;
//TODO NONE 현재 미사용됨

public interface CmSerialIF {
	public CmSerialIF getInstance() throws IOException;
	public CmSerialIF getInstance(String ID) throws IOException;
	
	public void loadPortEnv(String id);
	public void loadMsgEnv(String id);

	public void init();
	public void init(String id);
	
	public void writeData(String id, Object o);
	public void writeData(Object o);
	public void writeData(String id, Object o, int mcNo);
	public void writeData(Object o, int mcNo);
	public Object readData(String id);
	public Object readData();
	public Object readData(String id, int mcNo);
	public Object readData(int mcNo);
	
	/**
	 * Callback Event 추가 
	 * @param o
	 */
	public void addCallback(Object o);
}
