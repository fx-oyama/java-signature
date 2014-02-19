package com.kakakikikeke.sample;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.kakakikikeke.sample.utils.KeyPropertiesManager;
import com.kakakikikeke.sample.utils.Utils;

public class CallAws {

	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	private static final String REQUEST_URI = "/";
	private static final String REQUEST_METHOD = "GET";
	private static final String SIGNATURE_VERSION = "2";
	private static final String DEFAULT_ENDPOINT = "mq.jp-east-1.api.cloud.nifty.com";

	private String endpoint;
	private String action;
	private String body;
	private String accessKey;
	private String secretKey;

	private SecretKeySpec secretKeySpec = null;
	private Mac mac = null;

	public CallAws(String endpoint, String action, String body) {
		checkAction(action);
		if (endpoint.equals("") || endpoint == null) {
			this.setEndpoint(DEFAULT_ENDPOINT);
		} else {
			this.setEndpoint(endpoint);;
		}
		this.setAction(action);
		this.setBody(body);
		init();
	}

	public CallAws(String action, String body) {
		checkAction(action);
		this.setEndpoint(DEFAULT_ENDPOINT);
		this.setAction(action);
		this.setBody(body);
		init();
	}

	public CallAws(String action) {
		checkAction(action);
		this.setEndpoint(DEFAULT_ENDPOINT);
		this.setAction(action);
		this.setBody("");
		init();
	}

	public String call() {
		Map<String, String> params = convertMap(this.getBody());
		params.put("Action", this.getAction());
		params.put("AccessKeyId", accessKey);
		params.put("Timestamp", Utils.getTimestamp());
		params.put("SignatureVersion", SIGNATURE_VERSION);
		params.put("SignatureMethod", HMAC_SHA256_ALGORITHM);
		SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(params);
		String canonicalQS = Utils.canonicalize(sortedParamMap);
		String toSign = REQUEST_METHOD + "\n" + this.getEndpoint() + "\n" + REQUEST_URI + "\n" + canonicalQS;
		String hmac = Utils.hmac(toSign, mac);
		String sig = Utils.percentEncodeRfc3986(hmac);
		String url = "https://" + this.getEndpoint() + REQUEST_URI + "?" + canonicalQS + "&Signature=" + sig;
		return url;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	private void checkAction(String action) {
		if (action.equals("") || action == null) {
			System.err.println("Do not set Action");
			System.exit(1);
		}
	}

	private void init() {
		setKey();
		byte[] secretyKeyBytes = null;
		try {
			secretyKeyBytes = secretKey.getBytes(Utils.UTF8_CHARSET);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		secretKeySpec = new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
		try {
			mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		try {
			mac.init(secretKeySpec);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	private void setKey() {
		KeyPropertiesManager kpm = new KeyPropertiesManager();
		this.accessKey = (String) kpm.get("access_key");
		this.secretKey = (String) kpm.get("secret_key");
	}

	private Map<String, String> convertMap(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (json.equals("") || json == null) {
				return new HashMap<String, String>();
			} else {
				return mapper.readValue(json, new TypeReference<HashMap<String, String>>() {});
			}
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String endpoint = "";
		String action = "ListQueues";
		String body = "";
		String url = new CallAws(endpoint, action, body).call();
		System.out.println(url);
	}

}