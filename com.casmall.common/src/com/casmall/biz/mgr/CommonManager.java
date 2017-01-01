package com.casmall.biz.mgr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import com.casmall.biz.dao.CommonDAO;
import com.casmall.biz.domain.CmCdDTO;
import com.casmall.biz.domain.CmMsgAttrDTO;
import com.casmall.biz.domain.CmMsgDTO;
import com.casmall.biz.domain.CmOsMcDTO;
import com.casmall.common.DConstants;
import com.casmall.common.db.DBManager;

/**
 * 외부 연결 기기 및 전문 정보 관리자
 * @author OBERAK
 */
public class CommonManager {
	protected static Log logger = LogFactory.getLog(CommonManager.class);
	private static CommonManager instance = null;
	
	private DBManager dbm = DBManager.getInstance();
	
	HashMap<String, CmMsgDTO> msgEnvMap = new HashMap<String, CmMsgDTO>();
	HashMap<String, CmOsMcDTO> osMcEnvMap = new HashMap<String, CmOsMcDTO>();
	
	/**
	 * private 생성자(싱글톤 용)<BR>
	 * 전문정보 및 외부 기기 정보 Load
	 */
	private CommonManager(){
		loadOsMcEnv();
		loadMsgEnv();
	} // EnvManager()
	
	/**
	 * 싱글톤 객체 반환
	 * @return
	 */
	public static synchronized CommonManager getInstance(){
		if (instance == null) {
			instance = new CommonManager();
		} // if
		return instance;
	} // getInstance()
	
	/**
	 * 전문 정보 Load(From DB)
	 */
	private void loadMsgEnv() {
		SqlSession session = dbm.openSession();
		CommonDAO dao = session.getMapper(CommonDAO.class);
		try{
			ArrayList<CmMsgDTO> list = dao.selectCmMsg();
			for( CmMsgDTO dto : list){
				msgEnvMap.put(dto.getMsg_id(), dto);
			} // for
		}finally{
			session.close();
		} // try
	} // loadMsgEnv()
	
	/**
	 * 외부 기기 설정정보 Load(From DB)
	 */
	private void loadOsMcEnv() {
		SqlSession session = dbm.openSession();
		CommonDAO dao = session.getMapper(CommonDAO.class);
		try{
			ArrayList<CmOsMcDTO> list = dao.selectCmOsMc();
			for( CmOsMcDTO dto : list){
				osMcEnvMap.put(dto.getMc_id(), dto);
			} // for
		}finally{
			session.close();
		} // try
	} // loadPortEnv()
	
	/**
	 * 기본 전문 정보 얻기
	 * @return CmMsgDTO
	 * @throws IOException
	 */
	public CmMsgDTO getMsgEnv() throws IOException{
		return this.getMsgEnv(DConstants.DEFAULT_MC_ID);
	} // getMsgEnv()
	
	/**
	 * 전문 ID를 이용한 전문 정보 얻기
	 * @param id
	 * @return CmMsgDTO
	 * @throws IOException
	 */
	public CmMsgDTO getMsgEnv(String id) throws IOException{
		CmMsgDTO env = msgEnvMap.get(id);
		if(env == null){
			throw new IOException("Not Exists Msg env id : " + id);
		} // if
		return env;
	} // getMsgEnv()
	
	
	public int setMsgEnv(CmMsgDTO dto){
		int cnt = 0, cntAttrs;
		
		SqlSession session = dbm.openSession();
		CommonDAO dao = session.getMapper(CommonDAO.class);
		try{
			msgEnvMap.remove(dto.getMsg_id());
			msgEnvMap.put(dto.getMsg_id(), dto);
			cnt = dao.updateCmMsg(dto);
			List<CmMsgAttrDTO> attrs = dto.getAttrList();
			if(attrs != null){
				for(int i=0;i<attrs.size();i++){
					cntAttrs = dao.updateCmMsgAttr(attrs.get(i));
					if(cntAttrs != 1){
						if(logger.isErrorEnabled()){
							logger.error("Udate error : updateMsgEnvAttr");
						}
					}
				}
			} // if
		}finally{
			session.commit();
			session.close();
		} // try
		return cnt;
	}
	/**
	 * 기본 외부 기기 정보 얻기
	 * @return CmOsMcDTO
	 * @throws IOException
	 */
	public CmOsMcDTO getOsMcEnv() throws IOException{
		return this.getOsMcEnv(DConstants.DEFAULT_MC_ID);
	} // getOsMcEnv()
	
	/**
	 * 외부 기기 ID를 이용한 정보 얻기
	 * @param id
	 * @return CmOsMcDTO
	 * @throws IOException
	 */
	public CmOsMcDTO getOsMcEnv(String id) throws IOException{
		CmOsMcDTO env = osMcEnvMap.get(id);
		if(env == null){
			throw new IOException("Not Exists Outside MC env id : " + id);
		} // if
		return env;
	} // getOsMcEnv()
	
	/**
	 * Setting and update OsMcEnv
	 * @param dto
	 * @return
	 */
	public int setOsMcEnv(CmOsMcDTO dto){
		int cnt = 0;
		
		SqlSession session = dbm.openSession();
		CommonDAO dao = session.getMapper(CommonDAO.class);
		try{
			osMcEnvMap.remove(dto.getMc_id());
			osMcEnvMap.put(dto.getMc_id(), dto);
			cnt = dao.updateCmOsMc(dto);
		}finally{
			session.commit();
			session.close();
		} // try
		return cnt;
	}
	
	/**
	 * 마스터 코드 상세 조회
	 * @param dto
	 * @return
	 */
	public ArrayList<CmCdDTO> selectCmCd(CmCdDTO dto){
		SqlSession session = dbm.openSession();
		CommonDAO dao = session.getMapper(CommonDAO.class);
		try{
			return dao.selectCmCd(dto);
		}finally{
			session.close();
		} // try
	}
	
	public int updateCmCd(CmCdDTO dto) throws IOException{
		int cnt = 0;
		
		SqlSession session = dbm.openSession();
		CommonDAO dao = session.getMapper(CommonDAO.class);
		try{
			cnt = dao.updateCmCd(dto);
			if(cnt != 1){
				session.rollback();
				throw new IOException("마스터코드 상세정보 수정 건수 오류 :"+cnt);
			}
			session.commit();
		}finally{
			session.close();
		} // try
		return cnt;
	}
	
	public static void main(String[] args){
		CommonManager sem = CommonManager.getInstance();
		try{
			if(logger.isDebugEnabled()){
				logger.debug("Default Msg:"+sem.getMsgEnv());
				logger.debug("Default Msg:"+sem.getOsMcEnv());
			} // if
		}catch(Exception e){
			e.printStackTrace();
		} // try
	} // main
	
} // class
