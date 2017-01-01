package com.casmall.serial;

public interface PConstants {
	
	/** config.xml */
	public static final String CATEGORY_DEFAULT = "general";
	public static final String CATEGORY_INDECATOR = "INDECATOR";
	public static final String CATEGORY_PRINTER = "PRINTER";
	public static final String CATEGORY_RFID = "RFID";
	public static final String MSG_READ = "READ";
	
	// Port
	/** 포트명 */
	public static final String PORT_NAME = "PORT_NAME";
	/** 전송속도 */
	public static final String BAUD_RATE = "BAUD_RATE";
	/** 데이터 비트 */
	public static final String DATA_BITS = "DATA_BITS";
	/** 패리티 비트 */
	public static final String PARITY_BITS = "PARITY_BITS";
	/** 스톱 비트 */
	public static final String STOP_BITS = "STOP_BITS";

	// Message
	/** 전문 길이 */
	public static final String MSG_LENGTH = "LENGTH";
	/** 시작문자 */
	public static final String MSG_START = "START";
	/** 종료문자 */
	public static final String MSG_END = "END";
	/** 항목명 */
	public static final String MSG_ATTR_NAME = "ATTR";
	/** 항목길이 */
	public static final String MSG_ATTR_SIZE = "SIZE";
	/** 항목Type */
	public static final String MSG_ATTR_TYPE = "TYPE";
	

	/** 데이터 읽기 실패시 재시도 회수 */
	public static final String TRY_CNT = "TRY_CNT";
	
	/** 전문 읽기 대기 시간 */
	public static final String READ_WAIT = "READ_WAIT";
	
	/** msg type */
	public static final String MSG_TYPE_N = "N"; // 숫자
	public static final String MSG_TYPE_S = "S"; // 문자열
	public static final String MSG_TYPE_B = "B"; // 바이트
	
	/** config fild */
	public static final String CONFIG_MSG = "msg";
	public static final String CONFIG_SERIAL = "serial";
}
