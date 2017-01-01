package com.casmall.biz.domain;

import java.util.List;

import com.casmall.common.BaseObject;
import com.casmall.common.StringUtil;

public class CmMsgDTO extends BaseObject {
	private static final long serialVersionUID = -4967429245062605038L;
	
	private String msg_id;
	private String stt_char;
	private String end_char;
	private int msg_tot_len;
	private List<CmMsgAttrDTO> attrList;
	
	private byte[] start;
	private byte[] end;
	private int[] size;
	private String[] attr;
	private String[] type;
	
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public String getStt_char() {
		return stt_char;
	}
	public void setStt_char(String stt_char) {
		this.stt_char = stt_char;
		start = StringUtil.hexToByteArray(stt_char);
	}
	public String getEnd_char() {
		return end_char;
	}
	public void setEnd_char(String end_char) {
		this.end_char = end_char;
		end = StringUtil.hexToByteArray(end_char);
	}
	public int getMsg_tot_len() {
		return msg_tot_len;
	}
	public void setMsg_tot_len(int msg_tot_len) {
		this.msg_tot_len = msg_tot_len;
	}
	public List<CmMsgAttrDTO> getAttrList() {
		return attrList;
	}
	public void setAttrList(List<CmMsgAttrDTO> attrList) {
		this.attrList = attrList;
		
		size = new int[attrList.size()];
		attr = new String[attrList.size()];
		type = new String[attrList.size()];
		
		int idx = 0;
		for(CmMsgAttrDTO dto : attrList){
			size[idx] = dto.getAttr_len();
			attr[idx] = dto.getAttr_nm();
			type[idx] = dto.getAttr_type_cd();
			idx++;
		}
	}
	public byte[] getStart() {
		return start;
	}
	public byte[] getEnd() {
		return end;
	}
	public int[] getSize() {
		return size;
	}
	public String[] getAttr() {
		return attr;
	}
	public String[] getType() {
		return type;
	}
	public void setStart(byte[] start) {
		this.start = start;
		this.stt_char = StringUtil.byteArrayToHex(start);
	}
	public void setEnd(byte[] end) {
		this.end = end;
		this.end_char = StringUtil.byteArrayToHex(end);
	}
}
