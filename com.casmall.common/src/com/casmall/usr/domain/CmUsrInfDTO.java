package com.casmall.usr.domain;

import com.casmall.common.BaseObject;

public class CmUsrInfDTO extends BaseObject{
	private static final long serialVersionUID = 6576226585833090170L;
	/** 일련번호 */
	private int usr_seq;
	/** 로그인 ID */
	private String lgn_id;
	/** 비밀번호 */
	private String lgn_pw;
	/** 사용자명 */
	private String usr_nm;
	/** 연락처 */
	private String tel;
	/** 등급 구분 */
	private String ath_grd;
	/** 등급 구분 명 */
	private String ath_grd_nm;
	/** 권한 코드 */
	private int ath_cd;
	/** 비고 */
	private String nt;
	
	private int no;
	private String search;
	
	public int getUsr_seq() {
		return usr_seq;
	}
	public void setUsr_seq(int usr_seq) {
		this.usr_seq = usr_seq;
	}
	public String getLgn_id() {
		return lgn_id;
	}
	public void setLgn_id(String lgn_id) {
		this.lgn_id = lgn_id;
	}
	public String getLgn_pw() {
		return lgn_pw;
	}
	public void setLgn_pw(String lgn_pw) {
		this.lgn_pw = lgn_pw;
	}
	public String getUsr_nm() {
		return usr_nm;
	}
	public void setUsr_nm(String usr_nm) {
		this.usr_nm = usr_nm;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAth_grd() {
		return ath_grd;
	}
	public void setAth_grd(String ath_grd) {
		this.ath_grd = ath_grd;
	}
	public int getAth_cd() {
		return ath_cd;
	}
	public void setAth_cd(int ath_cd) {
		this.ath_cd = ath_cd;
	}
	public String getNt() {
		return nt;
	}
	public void setNt(String nt) {
		this.nt = nt;
	}
	public int getNo() {
    	return no;
    }
	public void setNo(int no) {
    	this.no = no;
    }
	public String getSearch() {
    	return search;
    }
	public void setSearch(String search) {
    	this.search = search;
    }
	public String getAth_grd_nm() {
    	return ath_grd_nm;
    }
	public void setAth_grd_nm(String ath_grd_nm) {
    	this.ath_grd_nm = ath_grd_nm;
    }
	public boolean hasAuth(int auth){
		if((ath_cd & auth)>0){
			return true;
		}
		return false;
	}
}
