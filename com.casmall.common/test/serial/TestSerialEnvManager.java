package serial;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.casmall.biz.domain.CmCdDTO;
import com.casmall.biz.domain.CmOsMcDTO;
import com.casmall.biz.mgr.CommonManager;
import com.casmall.common.DConstants;

public class TestSerialEnvManager {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetInstance(){
		Assert.assertNotNull(CommonManager.getInstance());
	}
	
	@Test
	public void testGetMsgEnv() {
		CommonManager sem = CommonManager.getInstance();
		try {
			Assert.assertNotNull(sem.getMsgEnv());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetMsgEnvString() {
		CommonManager sem = CommonManager.getInstance();
		try {
			Assert.assertNotNull(sem.getMsgEnv(DConstants.DEFAULT_MC_ID));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOsMcEnv() {
		CommonManager sem = CommonManager.getInstance();
		try {
			Assert.assertNotNull(sem.getOsMcEnv());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetOsMcEnvString() {
		CommonManager sem = CommonManager.getInstance();
		try {
			Assert.assertNotNull(sem.getOsMcEnv(DConstants.DEFAULT_MC_ID));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSetOsMcEnvString() {
		CommonManager sem = CommonManager.getInstance();
		try {
			CmOsMcDTO dto = sem.getOsMcEnv();
			String id = dto.getEdt_id();
			String id2 = "TESTER";
			dto.setEdt_id(id2);
			Assert.assertEquals(1, sem.setOsMcEnv(dto));
			dto = sem.getOsMcEnv();
			Assert.assertEquals(id2, dto.getEdt_id());
			dto.setEdt_id(id);
			Assert.assertEquals(1, sem.setOsMcEnv(dto));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSelectCmCd() {
		CommonManager sem = CommonManager.getInstance();
			CmCdDTO dto = new CmCdDTO();
			Assert.assertNotNull(sem.selectCmCd(dto));
	}
}
