package com.casmall.usr.dao;

import java.util.ArrayList;

import com.casmall.usr.domain.CmUsrInfDTO;

public interface CmUsrInfDAO {
	/**
	 * 사용자 정보 조회
	 * @param dto
	 * @return
	 */
	public ArrayList<CmUsrInfDTO> selectCmUsrInf(CmUsrInfDTO dto);
	
	/**
	 * 사용자 SEQ 채번
	 * @return
	 */
	public String selectCmUsrInfKey();
	
	/**
	 * 사용자 정보 등록
	 * @param dto
	 * @return
	 */
	public int insertCmUsrInf(CmUsrInfDTO dto);
	
	/**
	 * 사용자 정보 수정
	 * @param dto
	 * @return
	 */
	public int updateCmUsrInf(CmUsrInfDTO dto);
}
