/*
 * 版权说明：
 *1.中国银联股份有限公司（以下简称“中国银联”）对该代码保留全部知识产权权利， 包括但不限于版权、专利、商标、商业秘密等。
 *  任何人对该代码的任何使用都要 受限于在中国银联成员机构服务平台（http://member.unionpay.com/）与中国银
 *  联签 署的协议之规定。中国银联不对该代码的错误或疏漏以及由此导致的任何损失负 任何责任。中国银联针  对该代码放弃所有明
 *  示或暗示的保证,包括但不限于不侵 犯第三方知识产权。
 *  
 *2.未经中国银联书面同意，您不得将该代码用于与中国银联合作事项之外的用途和目的。未经中国银联书面同意，不得下载、
 *  转发、公开或以其它任何形式向第三方提供该代码。如果您通过非法渠道获得该代码，请立即删除，并通过合法渠道 向中国银
 *  联申请。
 *  
 *3.中国银联对该代码或与其相关的文档是否涉及第三方的知识产权（如加密算法可 能在某些国家受专利保护）不做任何声明和担
 *  保，中国银联对于该代码的使用是否侵犯第三方权利不承担任何责任，包括但不限于对该代码的部分或全部使用。
 *
 */
package com.cup.security.rsa;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.cup.security.certification.RsaCertUtils;
import com.cup.security.security.Constants;

public class RsaUtilsTest {

	private static Logger logger = Logger.getLogger(RsaUtilsTest.class);
	private static String path = ClassLoader.getSystemResource("").getPath();
	
	@Test
	public void testCypCert() {
		String charset = "utf-8";
		String src = "12345abcde";
		//从证书中获取公私钥
		String keyfile,keypwd,type;
		keyfile = path + "/rsa_private_1.pfx";
		keypwd = "000000";
		type = "PKCS12";
		PrivateKey privateKey = RsaCertUtils.getPriKeyPkcs12(keyfile, keypwd, type);
		String filePath;
		filePath = path + "/rsa_public_1.cer";
		PublicKey publicKey = RsaCertUtils.getPubKey(filePath);
		
		String encode = null, decode = null;
		
		byte[] srcHex = src.getBytes();
		byte[] result = RsaUtils.rsaEcbEncrypt((RSAPublicKey) publicKey, srcHex, Constants.PAD_PKCS1);	
		try {
			encode = new String(Base64.encodeBase64(result), charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("Fail: ", e);
		}
		logger.info("encode:" + encode);
		
		byte[] desrcHex = Base64.decodeBase64(encode);
		byte[] deresult = RsaUtils.rsaEcbDecrypt((RSAPrivateKey) privateKey, desrcHex, Constants.PAD_PKCS1);
		try {
			decode = new String(deresult, charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("Fail:", e);
		}
		logger.info("decode:" + decode);
	}
	
	@Test
	public void testSignCert() {
		String pfxkeyfile,keypwd,type;
		pfxkeyfile = path + "/rsa_private_1.pfx";
		keypwd = "000000";
		type = "PKCS12";
		PrivateKey privateKey = RsaCertUtils.getPriKeyPkcs12(pfxkeyfile, keypwd, type);
		String filePath;
		filePath = path + "/rsa_public_1.cer";
		PublicKey publicKey = RsaCertUtils.getPubKey(filePath);
		
		String charset = "utf-8";
		String src = "12345abcde";
		String sign = null;
		boolean verify = false;
		
		byte[] srcHex = src.getBytes();
		byte[] result = RsaUtils.signWithSha256((RSAPrivateKey) privateKey, srcHex);	
		try {
			sign = new String(Base64.encodeBase64(result), charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("Fail: ", e);
		}
		logger.info("sign:" + sign);
		
		byte[] desrcHex = Base64.decodeBase64(sign);
		verify = RsaUtils.verifyWithSha256((RSAPublicKey) publicKey, srcHex, desrcHex);
		logger.info("verify:" + verify);
	}

}
