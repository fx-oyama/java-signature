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
 * Signature�̐������Ǘ�����N���X
 *
 * -----------�����⑫-----------
 * HmacSHA1�A���S���Y�����g���āA��ʓI��REST�F�؂�API�Ŏg�p�����OAuth�F�؂�
 * �F�؃f�[�^�iSignature�j���쐬����B
 * SignatureVersion�Ȃ���̂��w�肷��̂���ʂ炵��0, 1, 2�ǂ�ɂ��Ή�����Signature�͍쐬�ł��邪
 * main�̒��ɂ���data�̕ϐ��̕������eSignatureVersion�ɉ����ēK�X�ύX����K�v������̂ŁA
 * ����Ɋւ��Ă͉�����ꂽ�炢�����͓K���ɒ��ׂĂق����B
 * ���Ȃ݂ɍ���̃T���v���R�[�h��SignaureVersion1�ɑΉ����Ă���R�[�h��
 * data�Ɋւ��ẮuAPI���v+�uTimeStamp�v��A�������������Signature�𐶐����Ă���B
 * �܂��A�Í����̃A���S���Y�����{�R�[�h�ł́uHmacSHA1�v�A���S���Y�����̗p���Ă��邪
 * ���ɂ��uHmacsSHA256�v�A���S���Y���Ƃ������̂�����炵���A�Í����̃A���S���Y����SignatureVersion��ύX��data������������ΑΉ��ł���i�炵���j
 * Hmac�̈Í����ȊO�ɂ��Abase64�G���R�[�h��URL�G���R�[�h���\�[�X�R�[�h���ōs���Ă���B
 *
 * -----------�K�v�ȃ��C�u����-----------
 * �i���L�A�S�Ẵ��C�u�������N���X�p�X�֒ʂ��K�v����j
 * 1. commons-codec�i2012/01/23���_�ł̍ŐV�o�[�W����1.6�j�yhttp://commons.apache.org/codec/download_codec.cgi�z
 * �ʂ��p�X��lib�ȉ��Ȃǂ�jar�t�@�C����S�Ă�ʂ��Ă����΂���
 *
 * -----------�G�L-----------
 * ��{�I�ɂ�AWS�̃y�[�W����p�N���܂���
 * �R�[�f�b�N��Apache�̃R�[�f�b�N���g���悤�ɕύX�������炢�ł�
 * ������������𐶐����Ȃ���REST�ʐM��API�����Ȃ��Ƃ����\�ǂ��낶��Ȃ��đ����S�{����
 *
 * @author takayuki
 *
 */
public class SignatureCreator {

	/**
	 * HmacSHA1�A���S���Y��
	 */
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	/**
	 * HmacsSHA1���A���S���Y�����g�p���āASignature�𐶐�����
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
	 * Signature�������ɕK�v�ȃ^�C���X�^���v���擾����
	 *
	 * @return date
	 */
	public static String getFormattedDateForSignature() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss.256'Z'");
		return sdf.format(d).toString();
	}

	/**
	 * ���s���C�����\�b�h
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
