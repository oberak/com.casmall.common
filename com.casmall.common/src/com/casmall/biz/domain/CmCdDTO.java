package com.casmall.biz.domain;

import com.casmall.common.BaseObject;

/**
 * 공통코드
 * @author OBERAK
 */
public class CmCdDTO extends BaseObject {
    private static final long serialVersionUID = 1L;
    private String cd_id;
    private String cd_val;
    private String cd_nm;
    private int mark_prior;
	public String getCd_id() {
    	return cd_id;
    }
	public void setCd_id(String cd_id) {
    	this.cd_id = cd_id;
    }
	public String getCd_val() {
    	return cd_val;
    }
	public void setCd_val(String cd_val) {
    	this.cd_val = cd_val;
    }
	public String getCd_nm() {
    	return cd_nm;
    }
	public void setCd_nm(String cd_nm) {
    	this.cd_nm = cd_nm;
    }
	public int getMark_prior() {
    	return mark_prior;
    }
	public void setMark_prior(int mark_prior) {
    	this.mark_prior = mark_prior;
    }
}
