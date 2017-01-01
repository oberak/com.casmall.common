package com.casmall.biz.dao;

import java.util.ArrayList;

import com.casmall.biz.domain.CmCdDTO;
import com.casmall.biz.domain.CmMsgAttrDTO;
import com.casmall.biz.domain.CmMsgDTO;
import com.casmall.biz.domain.CmOsMcDTO;

/**
 * 공통 DAO
 * @author OBERAK
 */
public interface CommonDAO {
	/**
	 * 외부 연결 기기정보 조회
	 * @return
	 */
	public ArrayList<CmOsMcDTO> selectCmOsMc();
	
	/**
	 * 외부 연결 기기정보 Update
	 * @param dto
	 * @return
	 */
	public int updateCmOsMc(CmOsMcDTO dto);
	
	/**
	 * 전문 설정정보 조회
	 * @return
	 */
	public ArrayList<CmMsgDTO> selectCmMsg();

	/**
	 * 전문정보 Update
	 * @param dto
	 * @return
	 */
	public int updateCmMsg(CmMsgDTO dto);

	/**
	 * 전문 상세정보 Update
	 * @param cmMsgAttrDTO
	 * @return
	 */
	public int updateCmMsgAttr(CmMsgAttrDTO cmMsgAttrDTO);
	
	/**
	 * 마스터 코드 상세 조회
	 * @param dto
	 * @return
	 */
	public ArrayList<CmCdDTO> selectCmCd(CmCdDTO dto);
	
	
	/**
	 * 마스터코드 상세 수정
	 * @param dto
	 * @return
	 */
	public int updateCmCd(CmCdDTO dto);
}
