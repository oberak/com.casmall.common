package com.casmall.usr.mgr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.casmall.usr.domain.CmUsrInfDTO;

/**
 * 세션정보 관리자
 * @author OBERAK
 */
public class SessionManager {
	protected static Log logger = LogFactory.getLog(SessionManager.class);
	private static SessionManager instance = null;
	
	private CmUsrInfDTO usr = null;
	
	/**
	 * private 생성자(싱글톤 용)<BR>
	 * 전문정보 및 외부 기기 정보 Load
	 */
	private SessionManager(){

	} // EnvManager()
	
	/**
	 * 싱글톤 객체 반환
	 * @return
	 */
	public static synchronized SessionManager getInstance(){
		if (instance == null) {
			instance = new SessionManager();
		} // if
		return instance;
	} // getInstance()

	public CmUsrInfDTO getUsr() {
		if(usr == null){
			usr = new CmUsrInfDTO();
			usr.setLgn_id("DEFAULT");
		}
		return usr;
	}

	public void setUsr(CmUsrInfDTO usr) {
		this.usr = usr;
	}
} // class
