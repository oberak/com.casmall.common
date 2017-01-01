package common;

import java.util.ArrayList;

import org.junit.Test;

import com.casmall.common.AuthUtil;
import com.casmall.common.CryptoUtil;
import com.casmall.usr.domain.CmUsrInfDTO;
import com.casmall.usr.mgr.CmUsrInfMgr;

public class TestCommon {
	private static CmUsrInfMgr mgr = new CmUsrInfMgr();
	@Test
	public void testInsertEmp(){
		CmUsrInfDTO param = new CmUsrInfDTO();
		param.setLgn_id("root");
		param.setDel_yn("N");
		ArrayList<CmUsrInfDTO> list = mgr.selectUsrInf(param);
		if(list == null || list.size() == 0){
			System.out.println("Data not found");
		}
		
		System.out.println("user password:casmall >> "+CryptoUtil.encryptPassword("casmall"));
		System.out.println("manager password:6183500 >> "+CryptoUtil.encryptPassword("6183500"));
		String encryptedPassword = CryptoUtil.encryptPassword("happy");
		System.out.println("root password:happy >> ["+encryptedPassword+"]");
		System.out.println(CryptoUtil.checkPassword("casmall", encryptedPassword));

	}
	@Test
	public void testAuth(){
		System.out.println(AuthUtil.genBase());
		System.out.println(AuthUtil.getSerialNumber(AuthUtil.genBase()));

	}
}
