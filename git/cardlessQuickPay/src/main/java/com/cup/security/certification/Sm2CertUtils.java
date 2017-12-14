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
package com.cup.security.certification;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.encoders.Hex;

import com.cup.security.sm2.Sm2Utils;
/**
 * SM2 证书工具类
 *
 */
public class Sm2CertUtils {

	private static final Logger logger = Logger.getLogger(Sm2CertUtils.class);

	/**
	 * 从公钥证书中获取公钥
	 * @param filePath
	 * @return 公钥参数对象
	 */
	public static CipherParameters getPubKeyFPubCert(String filePath) {
		CipherParameters cipherParameters = null;
		try {
			byte[] csCert = readFile(filePath);
			csCert = Base64.decodeBase64(csCert);
			Certificate cert = certFrom(csCert);	
			logger.debug("public cert：" + Hex.toHexString(getPublicKey(cert)));

			SubjectPublicKeyInfo subjectPublicKeyInfo = cert.getSubjectPublicKeyInfo();
			DERBitString publicKeyData = subjectPublicKeyInfo.getPublicKeyData();
			byte[] publicKey = publicKeyData.getEncoded();
			byte[] encodedPublicKey = publicKey;
			byte[] ecP = new byte[64];
			System.arraycopy(encodedPublicKey, 4, ecP, 0, ecP.length);
			logger.debug("public Key：" + Hex.toHexString(ecP));
			
			byte[] certPKX = new byte[32];
			byte[] certPKY = new byte[32];
			System.arraycopy(ecP, 0, certPKX, 0, 32);
			System.arraycopy(ecP, 32, certPKY, 0, 32);
			cipherParameters = Sm2Utils.sm2PubKeyGet(certPKX, certPKY);
		} catch (Exception e) {
			logger.error("Fail: get public key from certificate", e);
		}
		return cipherParameters;
	}

	private static byte[] readFile(String filePath) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(filePath));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.startsWith("--")) {
					continue;
				}
				sb.append(readLine);
			}
		} catch (Exception e) {
			logger.error("Fail: readFile", e);
		}
		return sb.toString().getBytes();
	}

	
	
	
	private static final Certificate certFrom(byte[] certData) throws Exception {
		Certificate cert = null;
		try {
			ASN1Sequence seq = ASN1Sequence.getInstance(certData);
			cert = Certificate.getInstance(seq);
		} catch (Exception ex) {
			throw new Exception(ex);
		}
		
		return cert;
	}

	private static byte[] getPublicKey(Certificate cert) {
		byte[] pubData = cert.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
		byte[] pk = new byte[64];
		if (pubData.length == 64) {
			System.arraycopy(pubData, 0, pk, 0, 64);
		} else if (pubData.length == 65) {
			System.arraycopy(pubData, 1, pk, 0, 64);
		} else if (pubData.length == 66) {
			System.arraycopy(pubData, 2, pk, 0, 64);
		} else {
			return null;
		}
		return pk;
	}

	

}
