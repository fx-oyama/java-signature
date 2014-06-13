package com.kakakikikeke.sample.v1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class SignatureCreator {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public String createSignature(String data, String key) throws java.security.SignatureException {
		String signature;
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			String base64 = Base64.encodeBase64String(rawHmac);
			signature = URLEncoder.encode(base64, "UTF-8");
		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return signature;
	}

	public static String getFormattedDateForSignature() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.256'Z'");
		return sdf.format(d).toString();
	}

	public static void main(String[] args) {
		String key = "Your Secret AccessKey";
		String action = "API name";
		// String timestamp = "SSSSS";
		String timestamp = getFormattedDateForSignature();
		try {
			System.out.println("Timestamp:\t" + URLEncoder.encode(timestamp, "UTF-8"));
			String signature = new SignatureCreator().createSignature(action + timestamp, key);
			System.out.println("Signature:\t" + signature);
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
