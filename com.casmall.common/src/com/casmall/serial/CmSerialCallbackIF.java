package com.casmall.serial;

import java.util.HashMap;

public interface CmSerialCallbackIF {
	public boolean isWork();
	public void notify(HashMap<String, Object> o);
	public boolean isDisposed();
}
