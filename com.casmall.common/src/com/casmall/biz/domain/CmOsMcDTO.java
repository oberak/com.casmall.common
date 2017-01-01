package com.casmall.biz.domain;

import com.casmall.common.BaseObject;

public class CmOsMcDTO extends BaseObject {
	private static final long serialVersionUID = -201234109945546165L;
	
	/** 기기 ID */
	private String mc_id;
	/** 기기 명 */
	private String mc_nm;
	/** 포트 명 */
	private String port_nm;
	/** 전송 속도 */
	private int baud_rate;
	/** DATA BITS */
	private int data_bits;
	/** PARITY BITS */
	private int parity_bits;
	/** STOP BITS */
	private int stop_bits;
	/** PORT 오픈 대기 시간 */
	private int port_open_wait_time;
	/** Read 버퍼 크기 */
	private int read_buff_size;
	/** Read 대기 시간 */
	private int read_wait_time;
	/** Read 재시도 횟수 */
	private int read_retry_cnt;
	
	public String getMc_id() {
		return mc_id;
	}
	public void setMc_id(String eqm_id) {
		this.mc_id = eqm_id;
	}
	public String getPort_nm() {
		return port_nm;
	}
	public void setPort_nm(String port_nm) {
		this.port_nm = port_nm;
	}
	public String getMc_nm() {
		return mc_nm;
	}
	public void setMc_nm(String eqm_nm) {
		this.mc_nm = eqm_nm;
	}
	public int getBaud_rate() {
		return baud_rate;
	}
	public int getData_bits() {
		return data_bits;
	}
	public void setData_bits(int data_bits) {
		this.data_bits = data_bits;
	}
	public int getParity_bits() {
		return parity_bits;
	}
	public void setParity_bits(int parity_bits) {
		this.parity_bits = parity_bits;
	}
	public int getStop_bits() {
		return stop_bits;
	}
	public void setStop_bits(int stop_bits) {
		this.stop_bits = stop_bits;
	}
	public int getPort_open_wait_time() {
		return port_open_wait_time;
	}
	public void setPort_open_wait_time(int port_open_wait_time) {
		this.port_open_wait_time = port_open_wait_time;
	}
	public int getRead_buff_size() {
		return read_buff_size;
	}
	public void setRead_buff_size(int read_buff_size) {
		this.read_buff_size = read_buff_size;
	}
	public int getRead_wait_time() {
		return read_wait_time;
	}
	public void setRead_wait_time(int read_wait_time) {
		this.read_wait_time = read_wait_time;
	}
	public int getRead_retry_cnt() {
		return read_retry_cnt;
	}
	public void setRead_retry_cnt(int read_retry_cnt) {
		this.read_retry_cnt = read_retry_cnt;
	}
	public void setBaud_rate(int baud_rate) {
		this.baud_rate = baud_rate;
	}
}
