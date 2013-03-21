package com.kakakikikeke.sample;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * Signatureの生成を管理するクラス
 *
 * -----------説明補足-----------
 * HmacSHA1アルゴリズムを使って、一般的なREST認証のAPIで使用されるOAuth認証の
 * 認証データ（Signature）を作成する。
 * SignatureVersionなるものを指定するのが一般らしく0, 1, 2どれにも対応してSignatureは作成できるが
 * mainの中にあるdataの変数の部分を各SignatureVersionに応じて適宜変更する必要があるので、
 * それに関しては何を入れたらいいかは適当に調べてほしい。
 * ちなみに今回のサンプルコードはSignaureVersion1に対応しているコードで
 * dataに関しては「API名」+「TimeStamp」を連結した文字列でSignatureを生成している。
 * また、暗号化のアルゴリズムも本コードでは「HmacSHA1」アルゴリズムを採用しているが
 * 他にも「HmacsSHA256」アルゴリズムというものがあるらしく、暗号化のアルゴリズムもSignatureVersionを変更しdataを書き換えれば対応できる（らしい）
 * Hmacの暗号化以外にも、base64エンコードとURLエンコードをソースコード内で行っている。
 *
 * -----------必要なライブラリ-----------
 * （下記、全てのライブラリをクラスパスへ通す必要あり）
 * 1. commons-codec（2012/01/23時点での最新バージョン1.6）【http://commons.apache.org/codec/download_codec.cgi】
 * 通すパスはlib以下などのjarファイルを全てを通しておけばおｋ
 *
 * -----------雑記-----------
 * 基本的にはAWSのページからパクリました
 * コーデックにApacheのコーデックを使うように変更したくらいです
 * そもそもこれを生成しないとREST通信のAPI着かないとか結構どころじゃなくて相当鬼畜だろ
 *
 * @author takayuki
 *
 */
public class SignatureCreator {

	/**
	 * HmacSHA1アルゴリズム
	 */
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	/**
	 * HmacsSHA1をアルゴリズムを使用して、Signatureを生成する
	 *
	 * @param data
	 * @param key
	 * @return
	 * @throws java.security.SignatureException
	 */
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

	/**
	 * Signature生成時に必要なタイムスタンプを取得する
	 *
	 * @return date
	 */
	public static String getFormattedDateForSignature() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.256'Z'");
		return sdf.format(d).toString();
	}

	/**
	 * 実行メインメソッド
	 *
	 * @param args
	 */
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
