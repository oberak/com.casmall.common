package com.casmall.biz.domain;

import com.casmall.common.BaseObject;

/**
 * 전문 상세정보 DTO
 * @author OBERAK
 */
public class CmMsgAttrDTO extends BaseObject {
	private static final long serialVersionUID = 9153422035699135726L;
	
	/** 전문 ID */
	private String msg_id;
	/** 항목 일련번호 */
	private int attr_seq;
	/** 항목 순서 */
	private int attr_prior;
	/** 항목 명 */
	private String attr_nm;
	/** 시작 위치 */
	private int stt_pos;
	/** 길이 */
	private int attr_len;
	/** 데이터 타입 */
	private String attr_type_cd;
	
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public int getAttr_seq() {
		return attr_seq;
	}
	public void setAttr_seq(int attr_seq) {
		this.attr_seq = attr_seq;
	}
	public int getAttr_prior() {
		return attr_prior;
	}
	public void setAttr_prior(int attr_prior) {
		this.attr_prior = attr_prior;
	}
	public String getAttr_nm() {
		return attr_nm;
	}
	public void setAttr_nm(String attr_nm) {
		this.attr_nm = attr_nm;
	}
	public int getAttr_len() {
		return attr_len;
	}
	public void setAttr_len(int attr_len) {
		this.attr_len = attr_len;
	}
	public String getAttr_type_cd() {
		return attr_type_cd;
	}
	public void setAttr_type_cd(String attr_type_cd) {
		this.attr_type_cd = attr_type_cd;
	}
	public int getStt_pos() {
		return stt_pos;
	}
	public void setStt_pos(int stt_pos) {
		this.stt_pos = stt_pos;
	}
}
