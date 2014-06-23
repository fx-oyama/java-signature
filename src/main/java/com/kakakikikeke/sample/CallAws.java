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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.kakakikikeke.sample.utils.KeyPropertiesManager;
import com.kakakikikeke.sample.utils.Utils;

public class CallAws {

	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	private static final String DEFAULT_VERSION = "2012-11-05";
	private static final String DEFAULT_REQUEST_URI = "/";
	private static final String GET_METHOD = "GET";
	private static final String POST_METHOD = "POST";
	private static final String SIGNATURE_VERSION = "2";
	private static final String DEFAULT_ENDPOINT = "mq.jp-east-1.api.cloud.nifty.com";

	private String endpoint;
	private String action;
	private String body;
	private String protocol = "https://";
	private String requestUri;
	private String version;
	private boolean versionUseFlag;
	private String accessKey;
	private String secretKey;
	private String requestURL;
	private int responceCode;
	private String responceBody;
	private HttpClient hc;

	private SecretKeySpec secretKeySpec = null;
	private Mac mac = null;

	public CallAws(String endpoint, String action, String body, boolean unSecureFlag, String requestUri, String version, boolean versionUseFlag, String accesskey, String secretkey) {
		checkAction(action);
		checkJson(body);
		this.hc = new HttpClient();
		if (endpoint.equals("") || endpoint == null) {
			this.setEndpoint(DEFAULT_ENDPOINT);
		} else {
			this.setEndpoint(endpoint);
			;
		}
		if (unSecureFlag) {
			this.setProtocol("http://");
		}
		if (requestUri.equals("") || requestUri == null) {
			this.setRequestUri(DEFAULT_REQUEST_URI);
		} else {
			this.setRequestUri(DEFAULT_REQUEST_URI + requestUri);
		}
		if (version.equals("") || version == null) {
			this.setVersion(DEFAULT_VERSION);
		} else {
			this.setVersion(version);
		}
		this.setAction(action);
		this.setBody(body);
		this.setVersionUseFlag(versionUseFlag);
		this.setAccessKey(accesskey);
		this.setSecretKey(secretkey);
		init();
	}

	public void call() {
		Map<String, String> params = convertMap(this.getBody());
		params.put("Action", this.getAction());
		params.put("AWSAccessKeyId", accessKey);
		params.put("Timestamp", Utils.getTimestamp());
		params.put("SignatureVersion", SIGNATURE_VERSION);
		params.put("SignatureMethod", HMAC_SHA256_ALGORITHM);
		if (!isVersionUseFlag()) {
			params.put("Version", this.getVersion());
		}
		SortedMap<String, String> sortedParamMap = new TreeMap<String, String>(params);
		String canonicalQS = Utils.canonicalize(sortedParamMap);
		String toSign = GET_METHOD + "\n" + this.getEndpoint() + "\n" + getRequestUri() + "\n" + canonicalQS;
		String hmac = Utils.hmac(toSign, mac);
		String sig = Utils.percentEncodeRfc3986(hmac);
		this.setRequestURL(getProtocol() + this.getEndpoint() + getRequestUri() + "?" + canonicalQS + "&Signature=" + sig);
		execUrl("GET");
	}

	public void setProxy(String proxyHostName, int portNumber) {
		this.hc.getHostConfiguration().setProxy(proxyHostName, portNumber);
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isVersionUseFlag() {
		return versionUseFlag;
	}

	public void setVersionUseFlag(boolean versionUseFlag) {
		this.versionUseFlag = versionUseFlag;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public int getResponceCode() {
		return responceCode;
	}

	public void setResponceCode(int responceCode) {
		this.responceCode = responceCode;
	}

	public String getResponceBody() {
		return responceBody;
	}

	public void setResponceBody(String responceBody) {
		this.responceBody = responceBody;
	}

	private void execUrl(String method) {
		HttpMethodBase hmb;
		if (method.equals(GET_METHOD)) {
			hmb = new GetMethod(this.getRequestURL());
		} else if (method.equals(POST_METHOD)) {
			hmb = new PostMethod(this.getRequestURL());
		} else {
			hmb = new GetMethod(this.getRequestURL());
		}
		try {
			this.setResponceCode(hc.executeMethod(hmb));
			this.setResponceBody(hmb.getResponseBodyAsString());
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void checkAction(String action) {
		if (action.equals("") || action == null) {
			System.err.println("Do not set Action");
			System.exit(1);
		}
	}

	private void checkJson(String body) {
		if (!body.equals("") && body != null) {
			try {
				JsonParser parser = new ObjectMapper().getJsonFactory().createJsonParser(body);
				while (parser.nextToken() != null) {
				}
			} catch (JsonParseException e) {
				System.err.println("Invalid Json format error");
				System.exit(1);
			} catch (IOException e) {
				System.err.println("Invalid Json format error");
				System.exit(1);
			}
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
		if (getAccessKey().equals("") || getAccessKey() == null) {
			setAccessKey((String) kpm.get("access_key"));
		}
		if (getSecretKey().equals("") || getSecretKey() == null) {
			setSecretKey((String) kpm.get("secret_key"));
		}
	}

	private Map<String, String> convertMap(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (json.equals("") || json == null) {
				return new HashMap<String, String>();
			} else {
				return mapper.readValue(json, new TypeReference<HashMap<String, String>>() {
				});
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

	public static void main(String... args) {
		String endpoint = "";
		String action = "";
		String body = "";
		boolean secureFlag = false;
		boolean versionUseFlag = false;
		String proxy = null;
		String version = "";
		String requestUri = "";
		String accesskey = "";
		String secretkey = "";
		boolean onlyXmlFlag = false;
		boolean onlyResponseCodeFlag = false;
		if (args.length == 0) {
			showErrorAndExit();
		}
		try {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-e")) {
					endpoint = args[i + 1];
				} else if (args[i].equals("-a")) {
					action = args[i + 1];
				} else if (args[i].equals("-b")) {
					body = args[i + 1];
				} else if (args[i].equals("-u")) {
					secureFlag = true;
				} else if (args[i].equals("-r")) {
					requestUri = args[i + 1];
				} else if (args[i].equals("-p")) {
					proxy = args[i + 1];
				} else if (args[i].equals("-v")) {
					version = args[i + 1];
				} else if (args[i].equals("-nv")) {
					versionUseFlag = true;
				} else if (args[i].equals("--accesskey")) {
					accesskey = args[i + 1];
				} else if (args[i].equals("--secretkey")) {
					secretkey = args[i + 1];
				} else if (args[i].equals("--only-xml")) {
					onlyXmlFlag = true;
				} else if (args[i].equals("--only-res-code")) {
					onlyResponseCodeFlag = true;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			showErrorAndExit();
		}
		CallAws ca = new CallAws(endpoint, action, body, secureFlag, requestUri, version, versionUseFlag, accesskey, secretkey);
		if (proxy != null && !proxy.equals("")) {
			try {
				String[] proxyInfo = proxy.split(":");
				String proxyHost = proxyInfo[0];
				int proxyPort = Integer.parseInt(proxyInfo[1]);
				ca.setProxy(proxyHost, proxyPort);
			} catch (Exception e) {
				System.err.print("Invalid proxy format error : ex) -p proxy_host:8080");
				System.exit(1);
			}
		}
		ca.call();
		if (onlyXmlFlag) {
			System.out.println(ca.getResponceBody());
		} else if (onlyResponseCodeFlag) {
			System.out.println(ca.getResponceCode());
		} else {
			System.out.println(ca.getRequestURL());
			System.out.println(ca.getResponceCode());
			System.out.println(ca.getResponceBody());
		}
	}

	private static void showErrorAndExit() {
		System.err.println("Usage : java -jar CallAws-jar-with-dependencies.jar -e endpoint -a actionname -b {\\\"key\\\";\\\"value\\\"}");
		System.err.println("  Options : -u");
		System.err.println("          : unused ssl");
		System.err.println("  Options : -r request/path");
		System.err.println("          : specify request path");
		System.err.println("  Options : -p proxy-server:8080");
		System.err.println("          : specify your proxy server info");
		System.err.println("  Options : -v version");
		System.err.println("          : specify Version parameter value");
		System.err.println("  Options : -nv");
		System.err.println("          : not use Version parameter");
		System.err.println("  Options : --accesskey xxxxxxxxx, --secretkey xxxxxxxxx");
		System.err.println("          : if you want to use other key or do not write key.properties, you specify these parameters");
		System.err.println("  Options : --only-xml");
		System.err.println("          : show only xml in stdout");
		System.err.println("  Options : --only-res-code");
		System.err.println("          : show only response code in stdout, --only-xml is priority than --only-res-code");
		System.exit(1);
	}

}
