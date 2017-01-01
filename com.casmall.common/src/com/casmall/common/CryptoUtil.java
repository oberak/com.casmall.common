package com.casmall.common;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.salt.RandomSaltGenerator;
import org.jasypt.util.password.BasicPasswordEncryptor;

public class CryptoUtil {
	protected static Log logger = LogFactory.getLog(CryptoUtil.class);
	private static final String KEY_DES = "dnjsrlfghakstp";
	private static final String ALGORITHM_DES = "PBEWithMD5AndDES";// "PBEWithMD5AndTripleDES";
	private static final String PROVIDER_DES = "SunJCE";

	private static final String KEY_3DES = "koreaisone@#!dontworry!!";
	private static final String TRANSFORMATION_3DES = "DESede/ECB/PKCS5Padding";
	private static final String ALGORITHM_3DES = "DESede";
	private static final String CHAR_SET_3DES = "UTF8";
    
    /**
     * get 3DES Key
     * Require Key Size : 24 bytes
     * @return
     * @throws Exception
     */
    public static Key getKey3Des() throws Exception {
        DESedeKeySpec desKeySpec = new DESedeKeySpec(KEY_3DES.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_3DES);
        Key key = keyFactory.generateSecret(desKeySpec);
        return key;
    }

    /**
     * 3DES 암호화
     *
     * @param plainedText 평문
     * @return String 암호화된 문자열
     * @exception Exception
     */
    public static String encrypt3DES(String plainedText) throws Exception {
        if (plainedText == null || plainedText.length() == 0)
            return "";
        
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(TRANSFORMATION_3DES);
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, getKey3Des());

        byte[] enc = cipher.doFinal(plainedText.getBytes(CHAR_SET_3DES));
        return StringUtil.toHexString(enc);
    }

    /**
     * 3DES 복호화
     *
     * @param encryptedText 암호화된 문자열
     * @return String 복호화된 문자열
     * @exception Exception
     */
    public static String decrypt3DES(String encryptedText) throws Exception {
        if (encryptedText == null || encryptedText.length() == 0)
            return "";
        
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(TRANSFORMATION_3DES);
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, getKey3Des());
        byte[] dec = cipher.doFinal(StringUtil.toBytes(encryptedText,16));
        
        return  new String(dec, CHAR_SET_3DES);
    }
    
	/**
	 * MD5 암호화(단방향)
	 * @param plainedText 평문
	 * @return 암호화된 문자열
	 */
	public static String encryptMD5(String plainedText) {
		MessageDigest clsMd5;

		try {
			clsMd5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		clsMd5.update(plainedText.getBytes());
		byte[] arrBuf = clsMd5.digest();
		return StringUtil.toHexString(arrBuf);
	}

	/**
	 * 비밀번호 암호화(단방향)
	 * @param userPassword 평문
	 * @return 암호화된 비밀번호
	 */
	public static String encryptPassword(String userPassword) {
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		return passwordEncryptor.encryptPassword(userPassword);
	}

	/**
	 * 비밀번호 확인
	 * @param inputPassword 확인할 비밀번호(평문)
	 * @param encryptedPassword 암호화된 비밀번호
	 * @return 일치여부
	 */
	public static boolean checkPassword(String inputPassword, String encryptedPassword) {
		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		if (passwordEncryptor.checkPassword(inputPassword, encryptedPassword)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * DES 암호화
	 * @param plainedText 평문
	 * @return 암호화된 문자열
	 * @throws Exception
	 */
	public static String encryptDES(String plainedText) throws Exception {
		String encryptedText = null;
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(KEY_DES);

		encryptor.setAlgorithm(ALGORITHM_DES);
		RandomSaltGenerator salt = new RandomSaltGenerator();
		salt.includePlainSaltInEncryptionResults();
		encryptor.setSaltGenerator(salt);
		encryptor.setProvider(Security.getProvider(PROVIDER_DES));
		encryptor.setKeyObtentionIterations(2000);

		try {
			encryptedText = encryptor.encrypt(plainedText);
		} catch (EncryptionOperationNotPossibleException ee) {
			if (logger.isErrorEnabled()) {
				logger.error("encrypt Error!! - " + ee.getMessage());
			}
			throw new Exception(ee);
		}
		return encryptedText;
	}

	/**
	 * DES 복호화
	 * @param encryptedText 암호화된 문자열
	 * @return 복호화된 문자열
	 * @throws Exception
	 */
	public static String decryptDES(String encryptedText) throws Exception {
		String plainedText = null;
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(KEY_DES); // we HAVE TO set a password

		encryptor.setAlgorithm(ALGORITHM_DES);
		RandomSaltGenerator salt = new RandomSaltGenerator();
		salt.includePlainSaltInEncryptionResults();
		encryptor.setSaltGenerator(salt);
		encryptor.setProvider(Security.getProvider(PROVIDER_DES));
		encryptor.setKeyObtentionIterations(2000);

		try {
			plainedText = encryptor.decrypt(encryptedText);
		} catch (EncryptionOperationNotPossibleException ee) {
			if (logger.isErrorEnabled()) {
				logger.error("decrypt Error!! - " + ee.getMessage());
			}
			throw new Exception(ee);
		}
		return plainedText;
	}

	public static void main(String[] args) {
		/*
		for (Provider provider : Security.getProviders()) {
			System.out.println("Provider: " + provider.getName());
			for (Provider.Service service : provider.getServices()) {
				System.out.println("  Algorithm: " + service.getAlgorithm());
			}
		}
		*/
		
		String plain = "one3500";
		try {
//			System.out.println(CryptoUtil.encryptPassword("one3500"));
//			System.out.println(CryptoUtil.encryptPassword("6183500"));
			String enc = encryptDES(plain);
			String dec = decryptDES(enc);
			if (plain.equals(dec)) {
				System.out.println("OK-" + enc);
			}
			
			String enc3 = encrypt3DES(plain);
			String dec3 = decrypt3DES(enc3);
			if (plain.equals(dec3)) {
				System.out.println("OK-" + enc3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
