package serial;


import gnu.io.PortInUseException;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.casmall.serial.CmSerial;
import com.casmall.serial.CmSerialManager;

public class TestCmSerial {
	static CmSerial csm;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		csm = CmSerialManager.getInstance();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Test
	public void testReadData(){
		try {
			csm.open();
			Assert.assertNotNull(csm.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PortInUseException e) {
	        e.printStackTrace();
        }
	}
	
}
