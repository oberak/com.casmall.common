package com.casmall.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class AuthUtil {

	public static String getMacAddress() throws SocketException {
		Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();
		StringBuffer sb = new StringBuffer();
		ArrayList<String> mac = new ArrayList<String>();
		while (nienum.hasMoreElements()) {
			sb = new StringBuffer();
			NetworkInterface ni = nienum.nextElement();
			byte[] hardwareAddress = ni.getHardwareAddress();
			if (hardwareAddress != null) {
				for (byte b : hardwareAddress) {
					sb.append(String.format("%02X", b));
				}
				mac.add(sb.toString());
			}
		}
		if (mac.size() > 0) {
			Collections.sort(mac);
			return mac.get(mac.size() - 1);
		}
		return null;
	}

	public static String getSerialNumber(String drive) {
		String result = "";
		try {
			File file = File.createTempFile("realhowto", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n" + "Set colDrives = objFSO.Drives\n"
			        + "Set objDrive = colDrives.item(\"" + drive + "\")\n" + "Wscript.Echo objDrive.SerialNumber"; // see
			                                                                                                       // note
			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return result.trim().replaceAll("-", "");
	}

	public static String calculateSecurityHash(String stringInput, String algorithmName)
	        throws java.security.NoSuchAlgorithmException {
		String hexMessageEncode = "";
		byte[] buffer = stringInput.getBytes();
		java.security.MessageDigest messageDigest = java.security.MessageDigest.getInstance(algorithmName);
		messageDigest.update(buffer);
		byte[] messageDigestBytes = messageDigest.digest();
		for (int index = 0; index < messageDigestBytes.length; index++) {
			int countEncode = messageDigestBytes[index] & 0xff;
			if (Integer.toHexString(countEncode).length() == 1)
				hexMessageEncode = hexMessageEncode + "0";
			hexMessageEncode = hexMessageEncode + Integer.toHexString(countEncode);
		}
		return hexMessageEncode;
	}

	public static String rotate(String src, int cnt) {
		String base = "S1ZNM2Y9BLT0DCRXA5K4UGWH6OI7VP3Q8EFJ";
		String use = "SCDAYPQT3LRWZ9H4X7KGF6BJUV8"; // 1=I, 2=E, 5=O=0, N=M, 
		int idx, r;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src.length(); i++) {
			r = (i + 1) * 7 - 3;
			idx = base.indexOf(src.charAt(i));
			idx = (idx + cnt + r) % use.length();
			sb.append(use.charAt(idx));
		}

		return sb.toString();
	}

	public static String genBase() {
		StringBuffer sb = new StringBuffer();
		try {
			String src = getMacAddress();
			if(src == null){
				src = getSerialNumber("C");
				src.replaceAll("-", "");
				src = src + "592486123453";
				src = src.substring(0,12);
			}
			sb = new StringBuffer(rotate(src, 7));
			sb.insert(8, '-');
			sb.insert(4, '-');
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String genSerial() {
//		System.out.println("genBase():"+genBase());
		
		StringBuffer sb = new StringBuffer(rotate(genSerialBase(genBase().replaceAll("-", "")).toUpperCase(), 3));
//		System.out.println("sb:"+sb.toString());
		
		sb.insert(8, '-');
		sb.insert(4, '-');
		return sb.toString();
	}

	public static String genSerial(String src) {
		StringBuffer sb = new StringBuffer(rotate(genSerialBase(src.replaceAll("-", "")).toUpperCase(), 3));
		sb.insert(8, '-');
		sb.insert(4, '-');
		return sb.toString();
	}

	public static String genSerialBase(String src) {
		String serialNumberEncoded = null;
		try {
			serialNumberEncoded = calculateSecurityHash(src, "MD2") + calculateSecurityHash(src, "MD5")
			        + calculateSecurityHash(src, "SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (serialNumberEncoded != null) {
			String serialNumber = "" + serialNumberEncoded.charAt(32) + serialNumberEncoded.charAt(76)
			        + serialNumberEncoded.charAt(100) + serialNumberEncoded.charAt(50) + serialNumberEncoded.charAt(91)
			        + serialNumberEncoded.charAt(73) + serialNumberEncoded.charAt(72) + serialNumberEncoded.charAt(18)
			        + serialNumberEncoded.charAt(85) + serialNumberEncoded.charAt(102) + serialNumberEncoded.charAt(15)
			        + serialNumberEncoded.charAt(99);

			return serialNumber;
		} else {
			return "";
		}
	}

	public static void main(String[] args) {
		// 6SCH-7K82-4GLP
		System.out.println("인증번호:" + genBase());
		System.out.println("시리얼넘버:" + genSerial());
		try {
	        System.out.println("시리얼넘버:" + CryptoUtil.encrypt3DES(genSerial()));
        } catch (Exception e) {
	        e.printStackTrace();
        }
		System.out.println("시리얼넘버:" + genSerial(genBase()));
		System.out.println("시리얼넘버:" + genSerial("BDLR-G8QLGSGR"));
		System.out.println("시리얼넘버:" + genSerial("GRAR-GVTG-XLXY"));
		System.out.println("시리얼넘버:" + genSerial("BD8U-7UDJ-XPU9"));
		System.out.println("시리얼넘버:" + genSerial("BD8Z-98XK-VPA4"));
		System.out.println("시리얼넘버:" + genSerial("UT8R-YVRJ-RVG4"));
	}
}
