package com.casmall.common;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class BaseObject implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String del_yn;
	private Date rgn_dt;
	private String rgn_id;
	private Date edt_dt;
	private String edt_id;
	private Date trn_dt;
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public String getDel_yn() {
		return del_yn;
	}

	public void setDel_yn(String del_yn) {
		this.del_yn = del_yn;
	}

	public Date getRgn_dt() {
		return rgn_dt;
	}

	public void setRgn_dt(Date rgn_dt) {
		this.rgn_dt = rgn_dt;
	}

	public String getRgn_id() {
		return rgn_id;
	}

	public void setRgn_id(String rgn_id) {
		this.rgn_id = rgn_id;
	}

	public Date getEdt_dt() {
		return edt_dt;
	}

	public void setEdt_dt(Date edt_dt) {
		this.edt_dt = edt_dt;
	}

	public String getEdt_id() {
		return edt_id;
	}

	public void setEdt_id(String edt_id) {
		this.edt_id = edt_id;
	}

	public Date getTrn_dt() {
    	return trn_dt;
    }

	public void setTrn_dt(Date trn_dt) {
    	this.trn_dt = trn_dt;
    }
}
