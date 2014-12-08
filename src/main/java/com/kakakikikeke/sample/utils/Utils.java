package com.kakakikikeke.sample.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Base64;

public class Utils {

	public static final String UTF8_CHARSET = "UTF-8";

	public static String getTimestamp() {
		String timestamp = null;
		Calendar cal = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dfm.setTimeZone(TimeZone.getTimeZone("JST"));
		timestamp = dfm.format(cal.getTime());
		return timestamp;
	}

	public static String canonicalize(SortedMap<String, String> sortedParamMap) {
		if (sortedParamMap.isEmpty()) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();

		while (iter.hasNext()) {
			Map.Entry<String, String> kvpair = iter.next();
			buffer.append(percentEncodeRfc3986(kvpair.getKey()));
			buffer.append("=");
			if (checkEncode(kvpair.getValue())) {
				buffer.append(replaceEncodeAnother(kvpair.getValue()));
			} else {
				buffer.append(percentEncodeRfc3986(kvpair.getValue()));
			}
			if (iter.hasNext()) {
				buffer.append("&");
			}
		}
		String canonical = buffer.toString();
		return canonical;
	}

	public static String percentEncodeRfc3986(String s) {
		String out;
		try {
			out = replaceEncodeAnother(URLEncoder.encode(s, UTF8_CHARSET));
		} catch (UnsupportedEncodingException e) {
			out = s;
		}
		return out;
	}

	public static String replaceEncodeAnother(String s) {
		String out;
		out = replaceNotEncodeAnother(s.replace("+", "%20").replace("*", "%2A"));
		return out;
	}

	public static String replaceNotEncodeAnother(String s) {
		String out;
		out = s.replace("%7E", "~").replace("%2D", "-");
		return out;
	}

	public static String hmac(String stringToSign, Mac mac) {
		String signature = null;
		byte[] data;
		byte[] rawHmac;
		try {
			data = stringToSign.getBytes(UTF8_CHARSET);
			rawHmac = mac.doFinal(data);
			Base64 encoder = new Base64();
			signature = new String(encoder.encode(rawHmac));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
		}
		return signature;
	}

	public static boolean checkEncode(String value) {
		String regex = ".*%[0-9A-Z][0-9A-Z].*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		if(m.find()) {
			return true;
		} else {
			return false;
		}
	}

}
