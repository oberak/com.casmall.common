package com.casmall.usr.mgr;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;

import com.casmall.common.CryptoUtil;
import com.casmall.common.db.DBManager;
import com.casmall.usr.dao.CmUsrInfDAO;
import com.casmall.usr.domain.CmUsrInfDTO;

public class CmUsrInfMgr {
	protected static Log logger = LogFactory.getLog(CmUsrInfMgr.class);
	private DBManager dbm = DBManager.getInstance();
	
	public ArrayList<CmUsrInfDTO> selectUsrInf(CmUsrInfDTO dto){
		SqlSession session = dbm.openSession();
		CmUsrInfDAO dao = session.getMapper(CmUsrInfDAO.class);
		try{
			return dao.selectCmUsrInf(dto);
		}finally{
			session.close();
		} // try
	} // selectUsrInf
	
	/**
	 * 로그인 체크
	 * @param id
	 * @param password
	 * @return
	 */
	public boolean checkLogin(String id, String password){
		CmUsrInfDTO param = new CmUsrInfDTO();
		param.setLgn_id(id.trim());
		param.setDel_yn("N");
		ArrayList<CmUsrInfDTO> list = selectUsrInf(param);
		if(list == null || list.size() == 0){
			if(logger.isDebugEnabled()){
				logger.debug("ID not exists!!");
			}
			return false;
		}
		
		if(CryptoUtil.checkPassword(password.trim(), list.get(0).getLgn_pw())){
			// 세션에 Usr 정보 저장
			SessionManager sm = SessionManager.getInstance();
			sm.setUsr(list.get(0));
			return true;
		}
		return false;
	}
	
	/**
	 * 사용자 SEQ 채번
	 * @return
	 */
	public String selectCmUsrInfKey(){
		SqlSession session = dbm.openSession();
		CmUsrInfDAO dao = session.getMapper(CmUsrInfDAO.class);
		try{
			return dao.selectCmUsrInfKey();
		}finally{
			session.close();
		} // try
	}
	
	/**
	 * 사용자 정보 등록
	 * @param dto
	 * @return
	 * @throws IOException 
	 */
	public int insertCmUsrInf(CmUsrInfDTO dto) throws IOException{
		SqlSession session = dbm.openSession();
		CmUsrInfDAO dao = session.getMapper(CmUsrInfDAO.class);
		try{
			// 암호화
			dto.setLgn_pw(CryptoUtil.encryptPassword(dto.getLgn_pw()));
			int chk = dao.insertCmUsrInf(dto);
			if(chk == 0){
				throw new IOException("사용자  정보가 등록되지 않았습니다.");
			}
			session.commit();
			return chk;
		}catch(Exception e){
			throw new IOException(e);
		}finally{
			session.close();
		} // try
	}
	
	/**
	 * 사용자 정보 수정
	 * @param dto
	 * @return
	 * @throws IOException 
	 */
	public int updateCmUsrInf(CmUsrInfDTO dto) throws IOException{
		SqlSession session = dbm.openSession();
		CmUsrInfDAO dao = session.getMapper(CmUsrInfDAO.class);
		try{
			if(dto.getLgn_pw() != null && !"".equals(dto.getLgn_pw()))
				dto.setLgn_pw(CryptoUtil.encryptPassword(dto.getLgn_pw()));
			int chk = dao.updateCmUsrInf(dto);
			if(chk == 0){
				throw new IOException("사용자  정보가 수정되지 않았습니다.");
			}
			session.commit();
			return chk;
		}catch(Exception e){
			throw new IOException(e);
		}finally{
			session.close();
		} // try
	}
	
	/**
	 * 사용자 정보 삭제
	 * @param dto
	 * @return
	 * @throws IOException 
	 */
	public int deleteCmUsrInf(CmUsrInfDTO dto) throws IOException{
		SqlSession session = dbm.openSession();
		CmUsrInfDAO dao = session.getMapper(CmUsrInfDAO.class);
		try{
			dto.setDel_yn("Y");
			int chk = dao.updateCmUsrInf(dto);
			if(chk == 0){
				throw new IOException("사용자  정보가 삭제되지 않았습니다.");
			}
			session.commit();
			return chk;
		}catch(Exception e){
			throw new IOException(e);
		}finally{
			session.close();
		} // try
	}
	
	/**
	 * 사용자 ID 중복체크
	 * @return
	 */
	public boolean existCmUsrInfLgnId(CmUsrInfDTO dto){
		SqlSession session = dbm.openSession();
		CmUsrInfDAO dao = session.getMapper(CmUsrInfDAO.class);
		try{
			ArrayList<CmUsrInfDTO> list = dao.selectCmUsrInf(dto);
			if(list == null || list.size() == 0){
				return false;
			}
			if( dto.getUsr_seq() == list.get(0).getUsr_seq() )
				return false;
			return true;
		}finally{
			session.close();
		} // try
	}
}
