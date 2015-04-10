package com.kakakikikeke.sample.v4;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.kakakikikeke.sample.utils.Utils;

public class Signature4Creator {

	static byte[] getHmacSHA256(String data, byte[] key) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(key, "HmacSHA256"));
		return mac.doFinal(data.getBytes("UTF-8"));
	}

	static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
		byte[] kSecret = ("AWS4" + key).getBytes("UTF-8");
		byte[] kDate = getHmacSHA256(dateStamp, kSecret);
		byte[] kRegion = getHmacSHA256(regionName, kDate);
		byte[] kService = getHmacSHA256(serviceName, kRegion);
		byte[] kSigning = getHmacSHA256("aws4_request", kService);
		return kSigning;
	}

	static String getHmacSHA256Digest(String data, byte[] key) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(key, "HmacSHA256"));
		byte[] digest = mac.doFinal(data.getBytes("UTF-8"));
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			String tmp = Integer.toHexString(digest[i] & 0xff);
			if (tmp.length() == 1) {
				buffer.append("0").append(tmp);
			} else {
				buffer.append(tmp);
			}
		}
		return buffer.toString();
	}

	static String getDigest(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(str.getBytes("UTF-8"));
		byte[] digest = md.digest();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			String tmp = Integer.toHexString(digest[i] & 0xff);
			if (tmp.length() == 1) {
				buffer.append("0").append(tmp);
			} else {
				buffer.append(tmp);
			}
		}
		return buffer.toString();
	}

	static void execUrl(String url, Map<String, String> headers, String body) throws HttpException, IOException {
		// HttpMethodBase hmb = new GetMethod(url);
		// for POST Method
		PostMethod hmb = new PostMethod(url);
		for (Entry<String, String> entry : headers.entrySet()) {
			hmb.setRequestHeader(entry.getKey(), entry.getValue());
		}
		if (body != null) {
			StringRequestEntity entity = new StringRequestEntity(body, "application/x-www-form-urlencoded", "UTF-8");
			hmb.setRequestEntity(entity);
		}
		System.out.println("======url======");
		System.out.println(url);
		System.out.println("======headers======");
		for (Header header : hmb.getRequestHeaders()) {
			System.out.println(header.getName());
			System.out.println(header.getValue());
		}
		HttpClient hc = new HttpClient();
		hc.getHostConfiguration().setProxy("sample.proxy.com", 8080);
		int responseCode = hc.executeMethod(hmb);
		String responseBody = hmb.getResponseBodyAsString();
		System.out.println(responseCode);
		System.out.println(responseBody);
	}

	public static void main(String[] args) throws Exception {
		// String method = "GET";
		// for POST Method
		String method = "POST";
		String service = "ec2";
		String host = "ec2.amazonaws.com";
		String region = "us-east-1";
		String endpoint = "https://ec2.amazonaws.com";
		String requestParameters = "Action=DescribeRegions&Version=2013-10-15";
		String accessKey = "Please input your aws accessKey";
		String secretKey = "Please input your aws secretKey";
		String amzdate = Utils.getTimestamp("yyyyMMdd'T'HHmmss'Z'", "UTC");
		String datestamp = Utils.getTimestamp("yyyyMMdd", "UTC");
		String body = null;
		String amzTarget = "";
		// Take 1
		String canonicalURI = "/";
		String canonicalQueryString = requestParameters;
		String canonicalHeaders = "host:" + host + "\n" + "x-amz-date:" + amzdate + "\n" + "x-amz-target:" + amzTarget + "\n";
		String signedHeaders = "host;x-amz-date;x-amz-target";
		String payloadHash = getDigest("");
		String canonicalRequest = method + "\n" + canonicalURI + "\n" + canonicalQueryString + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + payloadHash;
		System.out.println("======canonicalRequest======");
		System.out.println(canonicalRequest);
		// Take 2
		String algorithm = "AWS4-HMAC-SHA256";
		String credentialScope = datestamp + "/" + region + "/" + service + "/" + "aws4_request";
		String stringToSign = algorithm + "\n" + amzdate + "\n" + credentialScope + "\n" + getDigest(canonicalRequest);
		System.out.println("======stringToSign======");
		System.out.println(stringToSign);
		// Take 3
		byte[] signingKey = getSignatureKey(secretKey, datestamp, region, service);
		String signature = getHmacSHA256Digest(stringToSign, signingKey);
		System.out.println("======signature======");
		System.out.println(signature);
		// Take 4
		String authorizationHeader = algorithm + " " + "Credential=" + accessKey + "/" + credentialScope + ", " + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;
		System.out.println("======authorizationHeader======");
		System.out.println(authorizationHeader);
		// Send Request
		String requestURL = endpoint + "?" + canonicalQueryString;
		System.out.println("======requestURL======");
		System.out.println(requestURL);
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("host", host);
		headers.put("x-amz-date", amzdate);
		headers.put("x-amz-target", amzTarget);
		headers.put("Authorization", authorizationHeader);
		execUrl(requestURL, headers, body);
	}

}
