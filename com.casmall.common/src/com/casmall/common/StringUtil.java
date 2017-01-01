package com.casmall.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringUtil {
	protected static Log logger = LogFactory.getLog(StringUtil.class);

	/**
	 * byte => Hex String
	 * 
	 * @param b
	 * @return
	 */
	public static String printHex(byte[] b) {
		StringBuffer sb = new StringBuffer();
		if (b != null) {
			for (int i = 0; i < b.length; i++) {
				sb.append(String.format("%02X", b[i]));
			}
		}
		return sb.toString();
	}

	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return ba;
	}

	/**
	 * <p>unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.</p>
	 * 
	 * <pre>
	 * StringUtils.toHexString(null)                   = null
	 * StringUtils.toHexString([(byte)1, (byte)255])   = "01ff"
	 * </pre>
	 * 
	 * @param bytes unsigned byte's array
	 * @return
	 * @see HexUtils.toString(byte[])
	 */
	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		
		StringBuffer result = new StringBuffer();
		for (byte b : bytes) {
			result.append(Integer.toString((b & 0xF0) >> 4, 16));
			result.append(Integer.toString(b & 0x0F, 16));
		}
		return result.toString();
	}
	
	/**
	 * <p>8, 10, 16진수 문자열을 바이트 배열로 변환한다.</p>
	 * <p>8, 10진수인 경우는 문자열의 3자리가, 16진수인 경우는 2자리가, 하나의 byte로 바뀐다.</p>
	 * 
	 * <pre>
	 * StringUtils.toBytes(null, 16)     = null
	 * StringUtils.toBytes("0E1F4E", 16) = [0x0e, 0xf4, 0x4e]
	 * StringUtils.toBytes("48414e", 16) = [0x48, 0x41, 0x4e]
	 * </pre>
	 * 
	 * @param digits 문자열
	 * @param radix 진수(8, 10, 16만 가능)
	 * @return
	 * @throws NumberFormatException
	 */
	public static byte[] toBytes(String digits, int radix) throws IllegalArgumentException, NumberFormatException {
		if (digits == null) {
			return null;
		}
		if (radix != 16 && radix != 10 && radix != 8) {
			throw new IllegalArgumentException("For input radix: \"" + radix + "\"");
		}
		int divLen = (radix == 16) ? 2 : 3;
    	int length = digits.length();
    	if (length % divLen == 1) {
    		throw new IllegalArgumentException("For input string: \"" + digits + "\"");
    	}
    	length = length / divLen;
    	byte[] bytes = new byte[length];
    	for (int i = 0; i < length; i++) {
    		int index = i * divLen;
    		bytes[i] = (byte)(Short.parseShort(digits.substring(index, index+divLen), radix));
    	}
    	return bytes;
	}
	
	// byte[] to hex
	public static String byteArrayToHex(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return "";
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);
		String hexNumber;
		for (int x = 0; x < ba.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}

	/**
	 * String => byte[]로 변경(0x로 시작하는 문자 처리용)
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] convirtByte(String str) {
		byte[] rtn = null;
		boolean isHex = false;
		if (str == null || str.length() == 0)
			return rtn;
		int cnt = str.length() / 4;
		if (str.length() % 4 == 0) { // 4의 배수
			isHex = true;
			for (int i = 0; i < cnt; i++) {
				if (!str.substring(i * 4, i * 4 + 2).equals("0x")) {
					isHex = false;
					break;
				}
			}
			if (isHex) {
				rtn = new byte[cnt];
				for (int i = 0; i < cnt; i++) {
					rtn[i] = (byte) Integer.parseInt(str.substring(i * 4 + 2, i * 4 + 4), 16);
				}
			}
		}
		if (!isHex) {
			rtn = str.getBytes();
		}

		return rtn;
	}

	public static int find(byte[] src, byte[] find, int start) {
		int pos = -1;
		int cnt = 0;
		if (src != null && src.length > start) {
			for (int i = start; i <= src.length - find.length; i++) {
				cnt = 0;
				if (src[i] == 0) {
					break;
				}
				for (int j = 0; j < find.length; j++) {
					if (src[i + j] == find[j]) {
						cnt++;
					}
				}
				if (cnt == find.length) {
					pos = i;
					break;
				}
			}
		}
		return pos;
	}

	public static int find(byte[] src, byte[] find) {
		return find(src, find, 0);
	}

	/**
	 * 문자열에서 숫자열 찾기
	 * 
	 * @param src
	 * @return
	 */
	public static ArrayList<HashMap<String, Object>> findDigit(String src) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		Pattern p = Pattern.compile("[+-]?[ ]*\\d+(\\.?\\d*)");
		Matcher m = p.matcher(src);
		while (m.find()) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			row.put("length", m.end() - m.start() + 1);
			row.put("start", m.start());
			row.put("end", m.end() - 1);
			row.put("data", src.substring(m.start(), m.end()));
			data.add(row);
		}
		HashMapCompare hmc = new HashMapCompare();
		Collections.sort(data, hmc);
		return data;
	}

	private static class HashMapCompare implements Comparator<HashMap<String, Object>> {
		@Override
		public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {

			return ((Integer) o1.get("length") > (Integer) o2.get("length") ? 0 : 1);
		}
	}

	public static String makeGetter(String attr) {
		return "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
	}

	public static String makeSetter(String attr) {
		return "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
	}

	public static String nullToBlank(String src){
		if(src == null)
			return "";
		return src;
	}
	
	/**
	 * 날자 format yyyy, MM, dd, HH, mm, ss, SSS
	 * 
	 * @param fmt
	 * @return
	 */
	public static String getDate(String fmt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(new Date());
	}

	/**
	 * yyyyMMdd 형식 String 변환
	 * 
	 * @param date
	 * @return
	 */
	public static String getDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}

	public static String getString(Object o) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		DecimalFormat df = new DecimalFormat();
		String e = "";
		if (o instanceof Integer) {
			e = String.valueOf(o);
		} else if (o instanceof String) {
			e = (String) o;
		} else if (o instanceof Timestamp) {
			e = sdf.format(new Time(((Timestamp) o).getTime()));
		} else if (o instanceof Double) {
			e = df.format((Double) o);
		} else {
			e = "Data type mismatch";
		}
		return e;
	}
	
	public static String getString(Object o, String fmt) {
		String e = "";
		if(o == null){
			return e;
		}
		if (o instanceof Integer) {
			if(fmt !=null && !"".equals(fmt)){
				DecimalFormat df = new DecimalFormat(fmt);
				e = df.format((Integer)o);
			}else{
				e = String.valueOf((Integer)o);
			}
		} else if (o instanceof String) {
			e = (String) o;
			if(fmt !=null && !"".equals(fmt)){
				e = String.format(fmt, o);
			}
		} else if (o instanceof Timestamp) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(fmt !=null && !"".equals(fmt)){
				sdf = new SimpleDateFormat(fmt);
			}
			e = sdf.format(new Time(((Timestamp) o).getTime()));
		} else if (o instanceof Date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(fmt !=null && !"".equals(fmt)){
				sdf = new SimpleDateFormat(fmt);
			}
			e = sdf.format(((Date) o).getTime());
		} else if (o instanceof Double) {
			if(fmt !=null && !"".equals(fmt)){
				DecimalFormat df = new DecimalFormat(fmt);
				e = df.format((Double)o);
			}else{
				e = String.valueOf((Double)o);
			}
		} else if (o instanceof Long) {
			if(fmt !=null && !"".equals(fmt)){
				DecimalFormat df = new DecimalFormat(fmt);
				e = df.format((Long)o);
			}else{
				e = String.valueOf((Long)o);
			}
		} else {
			e = "Data type mismatch";
		}
		return e;
	}
	
	public static long getLong(Object o) {
		if (o instanceof Integer) {
			return (Integer)o;
		} else if (o instanceof Long) {
			return (Long)o;
		} 
		return 0;
	}

	public static String getNumber(String str) {
		return getNumber(str, 0);
	}
	
	public static String extractDate(Object o, String fmt){
		if(o instanceof Date){
			SimpleDateFormat sdf = new SimpleDateFormat(fmt);
			return sdf.format(new Time(((Date) o).getTime()));
		}else{
			return "data type is wrong!!";
		}
	}
	
	public static String extractTimestamp(Object o){
		if(o instanceof Date){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(new Time(((Date) o).getTime()));
		}else{
			return "data type is wrong!!";
		}
	}

	public static String extractTime(Object o){
		if(o instanceof Date){
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			return sdf.format((Date) o);
		}else{
			return "data type is wrong!!";
		}
	}
	public static String extractString(Object o, int pos) {
		String fmt = "#,##0";
		if (o == null) {
			return "";
		}
		if(o instanceof Integer){
			DecimalFormat df = new DecimalFormat(fmt);
			return df.format((Integer)o);
		}else if(o instanceof Double){
			if (pos > 0) {
				fmt = String.format(String.format("#,##0.%%0%dd", pos), 0);
			}
			DecimalFormat df = new DecimalFormat(fmt);
			return df.format((Double)o);
		} else if (o instanceof Timestamp) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(new Time(((Timestamp) o).getTime()));
		}else if(o instanceof Date){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(new Time(((Date) o).getTime()));
		}else if (o instanceof String) {
			return (String) o;
		}else{
			return "data type is wrong!!";
		}
	}
	
	public static String getNumber(String str, int pos) {
		String fmt = "#,##0";
		if (pos > 0) {
			fmt = String.format(String.format("#,##0.%%0%dd", pos), 0);
		}
		if (str == null || "".equals(str.trim())) {
			return "0";
		}
		DecimalFormat df = new DecimalFormat(fmt);
		return df.format(Double.parseDouble(str.replaceAll(",", "")));
	}

	/**
	 * 
	 * @param obj
	 * @param methodName
	 * @param paramList
	 * @return
	 */
	public static Object invoke(Object obj, String methodName, Object[] paramList) {
		Method[] methods = obj.getClass().getMethods();

		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName)) {
				try {
					if (methods[i].getReturnType().getName().equals("void")) {
						methods[i].invoke(obj, paramList);
					} else {
						return methods[i].invoke(obj, paramList);
					}
				} catch (IllegalAccessException lae) {
					logger.fatal(methodName + ":" + lae.getMessage());
				} catch (InvocationTargetException ite) {
					logger.fatal(methodName + ":" + ite.getMessage());
				}
			}
		}
		return null;
	}
	
	
	/**
	 * 미완성 글자를 제외한 한글 얻기
	 * @param str
	 * @return
	 */
	public static String getCompleteHangle(String str){
	    if(str == null || "".equals(str))
	    	return str;
	    
	    char c = str.charAt(str.length()-1);
	    
	    // 마지막 글자가  한글이면
	    if (isHangle(c)) {
	    	int[] sp = split(c);
		    String rest = str.substring(0,str.length()-1);
	    	if(sp[0]>-1 && sp[1]>-1 && sp[2]>-1){
	    		return rest+c;
	    	}
		    return rest;
	    }else{
	    	return str;
	    }
	}
	
	/**
	 * 한글여부 확인
	 * @param c
	 * @return
	 */
	public static boolean isHangle(int c){
		return (0xAC00 <= c && c <= 0xD7A3) || (0x3131 <= c && c <= 0x318E);
	}
    /**
     * 한글 초성/중성/종성 분리
     * @param c
     * @return
     */
    private static int[] split(char c){
        int sub[] = new int[3];
        sub[0] = (c - 0xAC00) / (21*28); //초성의 위치
        sub[1] = ((c - 0xAC00) % (21*28)) / 28; //중성의 위치
        sub[2] = (c -0xAC00) % (28);//종성의 위치
        return sub;
    } 
	
    /**
     * Padding right
     * @param s
     * @param n
     * @return
     */
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
	}

	/**
	 * Padding left
	 * @param s
	 * @param n
	 * @return
	 */
	public static String padLeft(String s, int n) {
       return String.format("%1$#" + n + "s", s);  
	}
	
	/**
	 * Zero padding
	 * @param src
	 * @param len
	 * @return
	 */
	public static String padZero(int src, int len){
		if(len<1){
		   len = 1;
		}
		return String.format("%0" + len + "d", src);
	}
   
	/**
	 * 진수 변환
	 * @param nDec
	 * @param nLog
	 * @return
	 */
	public static String toAntilog(long nDec, int nLog) {
		String strRet = "";
		long t;
		char c;

		if (nDec <= 0) // 음수는 변환 불가
			return "Invalid Decimal : " + nDec;

		if (nLog < 2 || nLog > 36) // 표현 가능 범위 : 2~36진수
			return "Invalid Antilogarithm : " + nLog;

		while (nDec > 0) {
			t = nDec % nLog;

			if (t < 10)
				c = (char) (t + '0'); // int를 char로 변환
			else
				c = (char) (t - 10 + 'A'); // 10 이상의 수는 알파벳으로 표기

			strRet = c + strRet;
			nDec /= nLog;
		}

		return strRet;
	}
	
	/**
	 * 32진수 문자열 변환
	 * 
	 * @param src
	 * @return
	 */
	public static String decToThsc(int src){
		return toAntilog(src, 32);
	}
	
	/**
	 * 
	 * @param src
	 * @return
	 */
	public static int thscToDec(String src){
		if (src == null || src.length() != 1){
			return -1;
		}
		if(src.charAt(0) >= '0' && src.charAt(0) <= '9'){
			return Integer.parseInt(src);
		}
		if(src.charAt(0) >= 'A' && src.charAt(0) <= 'Z'){
			return 9 + src.charAt(0) - 'A' + 1;
		}
		return -1;
	}
	
	/**
	 * Integer => 2진 문자열로 변경
	 * @param src
	 * @return
	 */
	public static String convertByteString(int src){
		StringBuffer sb = new StringBuffer(10);
		while(true){
			if(src == 0 || src == 1){
				sb.append(String.valueOf(src));
				break;
			}
			sb.append(String.valueOf(src%2));
			src /= 2;
		}
		return sb.reverse().toString();
	}

	/**
	 * 2진 문자열에 해당 position flag
	 * 
	 * @param src
	 * @param pos Zero base
	 * @return
	 */
	public static boolean getBinaryFlag(int src, int pos){
		StringBuffer sb = new StringBuffer(convertByteString(src));
		String str = sb.reverse().toString();
		if(pos >= str.length()){
			return false;
		}
		if(str.charAt(pos) == '1'){
			return true;
		}
		return false;
	}
	
	public static double parseDouble(String src){
		String rtn = "";
		if(src == null){
			if(logger.isWarnEnabled()){
				logger.warn("src is null!!");
			}
			return -1;
		}
		if(!isDigit(src)){
			if(logger.isWarnEnabled()){
				logger.warn("src is not digit!!");
			}
			return -1;
		}
		String[] s = src.split("\\.");
		if(s[0].startsWith("+") || s[0].startsWith("-")){
			if(s[0].endsWith(" ")){
				if(logger.isWarnEnabled()){
					logger.warn("src ended space!!");
				}
				return -1;
			}
		}
		rtn = s[0].replaceAll(" ","0");
		rtn += ".";
		if(s.length==2){
			rtn += s[1].replaceAll(" ", "");
		}
		
		return Double.parseDouble(rtn);
	}
	
	/**
	 * 숫자 체크(+/-와 숫자사이 공백 허용)
	 * 
	 * @param src
	 * @return
	 */
	public static boolean isDigit(String src){
		if(src == null)
			return false;
		
		String pattern = "[+-]?[ ]*\\d+(\\.?\\d*)";
		return src.matches(pattern);
	}
	
	/**
	 * Pipe("|")로 자르기
	 * @param src
	 * @return
	 */
	public static ArrayList<Double> splitWithPipe(String src){
		ArrayList<Double> rtn = new ArrayList<Double>();
		if(src != null && !"".equals(src.trim())){
			String[] split = src.trim().split("\\|");
			for(String tmp:split){
				rtn.add(Double.parseDouble(tmp));
			}
		}
		
		return rtn;
	}
	
	/**
	 * long 형 time 형식 변환
	 * @param lTime
	 * @return
	 */
	public static String formatTime(long lTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(lTime - c.get(Calendar.ZONE_OFFSET));
        return String.format("%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
    }

	public static void main(String[] agrs) {
//		System.out.println(StringUtil.parseDouble("+  10.12 "));
		
		System.out.println(StringUtil.splitWithPipe("2.9|8.28|7.1|0.8"));
//		System.out.println(toAntilog(33,32));
		// System.out.println(StringUtil.byteArrayToHex("ST".getBytes()));
		// byte[] src = {0x10, 0x20, 0x30, 0x40, 0x50};
		// byte[] tgt = {0x44,0x50};
		// System.out.println(find(src,tgt,1));
//		ArrayList<HashMap<String, Object>> data = StringUtil.findDigit("ST,NT,01,002,+ 2039.44kg");
//		for (HashMap<String, Object> d : data) {
//			System.out.println(d.get("length") + ":" + d.get("data"));
//		}
//		System.out.println("smiles".substring(1, 5));
//		System.out.println(getCompleteHangle("홍길동입ㅕ"));
//		System.out.println(convertByteString(4));
//		System.out.println(thscToDec("F"));
	}
}
