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
package com.cup.security.cupsec;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.util.encoders.Hex;

import com.cup.security.des.DesUtils;
import com.cup.security.msgdigest.ShaUtils;
import com.cup.security.msgdigest.Sm3Utils;
import com.cup.security.rsa.RsaUtils;
import com.cup.security.security.Constants;
import com.cup.security.sm2.Sm2Utils;
import com.cup.security.sm4.Sm4Utils;

/**
 * 无卡规范涉及的安全算法： 国际国密加解密 国际国密签名验签 敏感信息及其密钥加解密
 */
public class CupSec {

	private static Logger logger = Logger.getLogger(CupSec.class);

	/**
	 * 敏感信息3DES加密，敏感信息密钥RSA加密
	 * 
	 * 过程说明：先用对称密钥对敏感信息进行加密并进行base64编码， 然后对该对称密钥进行加密并进行base64编码。
	 * 敏感信息是位于XML标签 &amplt;SensInf></SensInf>内的内容(不包括<SensInf>与</SensInf>标签)
	 * 
	 * @param publicKey
	 *            加密敏感信息密钥的公钥
	 * @param sensInf
	 *            敏感信息明文
	 * @param encKey
	 *            敏感信息对称密钥明文
	 * @return 数组 {加密且base64的敏感信息, 加密且base64的敏感信息对称密钥}
	 */
	public static String[] sensInf3DEKeySM4SEncrypt(PublicKey publicKey, byte[] sensInf, byte[] encKey) {
		// sensInf: 3des cbc nopadding
		logger.debug("sensInfo hex:" + Hex.toHexString(sensInf));
		byte[] sensEnc = DesUtils.desEdeCbcEncrypt(encKey, sensInf, Constants.PAD_NO);
		logger.debug("sensInfo enc hex:" + Hex.toHexString(sensEnc));
		String sensInfEB = Base64.getEncoder().encodeToString(sensEnc);
		logger.debug("sensInfo enc base64:" + sensInfEB);
		// EncKey: rsa ecb PKCS1Padding
		logger.debug("encKey hex:" + Hex.toHexString(encKey));
		logger.debug("public key hex: " + Hex.toHexString(publicKey.getEncoded()));
		byte[] encKeyE = RsaUtils.rsaEcbEncrypt((RSAPublicKey) publicKey, encKey, Constants.PAD_PKCS1);
		logger.debug("encKey enc hex:" + Hex.toHexString(encKeyE));
		String encKeyEB = Base64.getEncoder().encodeToString(encKeyE);
		logger.debug("encKey enc base64:" + encKeyEB);

		return new String[] { sensInfEB, encKeyEB };
	}

	/**
	 * 敏感信息3DES解密，敏感信息密钥RSA解密
	 * 
	 * 过程说明： 先解密出所用密钥（即用于敏感信息解密的密钥），再对报文中<SensInf></SensInf>标签中的所有内容
	 * （不包括包括<SensInf>和</SensInf>标签自身）进行解密
	 * 
	 * @param privateKey
	 *            解密敏感信息的私钥
	 * @param sensInfEB
	 *            敏感信息密文
	 * @param encKeyEB
	 *            敏感信息对称密钥密文
	 * @return 数组 {敏感信息, 敏感信息对称密钥}
	 */
	public static byte[][] sensInf3DESKeySM4Decrypt(PrivateKey privateKey, String sensInfEB, String encKeyEB) {
		// EncKey: rsa ecb PKCS1Padding
		logger.debug("encKey ciphertext:" + encKeyEB);
		byte[] encKeyE = Base64.getDecoder().decode(encKeyEB);
		byte[] encKey = RsaUtils.rsaEcbDecrypt((RSAPrivateKey) privateKey, encKeyE, Constants.PAD_PKCS1);
		logger.debug("encKey hex:" + Hex.toHexString(encKey));
		// sensInf: 3des cbc nopadding
		logger.debug("sensInfo ciphertext:" + sensInfEB);
		byte[] sensInfE = Base64.getDecoder().decode(sensInfEB);
		byte[] sensInf = DesUtils.desEdeCbcDecrypt(encKey, sensInfE, Constants.PAD_NO);
		logger.debug("sensInfo hex:" + Hex.toHexString(sensInf));

		return new byte[][] { sensInf, encKey };
	}

	/**
	 * 敏感信息SM4加密，敏感信息密钥SM2加密
	 * 
	 * 过程说明：先对报文中<SensInf></SensInf>标签中的所有内容（不包括包括<SensInf>和</SensInf>标签自身）
	 * 进行加密，再对加密所用密钥进行加密，最后对加密后的敏感信息与加密后的敏感信息加密密钥进行base64编码
	 * 
	 * @param publicKey
	 *            加密敏感信息密钥的公钥
	 * @param sensInf
	 *            敏感信息明文
	 * @param encKey
	 *            敏感信息对称密钥明文
	 * @return 数组 {加密且base64的敏感信息, 加密且base64的敏感信息对称密钥}
	 */
	public static String[] sensInfSM4KeySM2Encrypt(CipherParameters publicKey, byte[] sensInf, byte[] encKey) {
		// sensInf: SM4 cbc nopadding
		logger.debug("sensInfo hex:" + Hex.toHexString(sensInf));
		byte[] sensEnc = Sm4Utils.sm4CbcEncrypt(encKey, sensInf, Constants.PAD_NO);
		logger.debug("sensInfo enc hex:" + Hex.toHexString(sensEnc));
		String sensInfEB = Base64.getEncoder().encodeToString(sensEnc);
		logger.debug("sensInfo enc base64:" + sensInfEB);
		// EncKey: SM2
		logger.debug("encKey hex:" + Hex.toHexString(encKey));
		logger.debug("public key: " + publicKey);
		byte[] encKeyE = Sm2Utils.Encrypt(publicKey, encKey);
		logger.debug("encKey enc hex:" + Hex.toHexString(encKeyE));
		String encKeyEB = Base64.getEncoder().encodeToString(encKeyE);
		logger.debug("encKey enc base64:" + encKeyEB);

		return new String[] { sensInfEB, encKeyEB };
	}

	/**
	 * 敏感信息SM4解密，敏感信息密钥SM2解密
	 * 
	 * 过程说明： 先解密出所用密钥（即用于敏感信息解密的密钥），再对报文中<SensInf></SensInf>标签中的所有内容
	 * （不包括包括<SensInf>和</SensInf>标签自身）进行解密
	 * 
	 * @param privateKey
	 *            解密敏感信息的私钥
	 * @param sensInfEB
	 *            敏感信息密文
	 * @param encKeyEB
	 *            敏感信息对称密钥密文
	 * @return 数组 {敏感信息, 敏感信息对称密钥}
	 */
	public static byte[][] sensInfSM4KeySM2Decrypt(CipherParameters privateKey, String sensInfEB, String encKeyEB) {
		// EncKey: SM2
		logger.debug("encKey ciphertext:" + encKeyEB);
		byte[] encKeyE = Base64.getDecoder().decode(encKeyEB);
		byte[] encKey = Sm2Utils.Decrypt(privateKey, encKeyE);
		logger.debug("encKey hex:" + Hex.toHexString(encKey));
		// sensInf: SM4 cbc nopadding
		logger.debug("sensInfo ciphertext:" + sensInfEB);
		byte[] sensInfE = Base64.getDecoder().decode(sensInfEB);
		byte[] sensInf = Sm4Utils.sm4CbcDecrypt(encKey, sensInfE, Constants.PAD_NO);
		logger.debug("sensInfo hex:" + Hex.toHexString(sensInf));

		return new byte[][] { sensInf, encKey };
	}

	/**
	 * RSA私钥签名
	 * 
	 * 过程说明： 先对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）
	 * 生成SHA-256摘要，再对摘要进行RSA签名，最后对生成的签名进行base64编码
	 * 
	 * @param privateKey
	 *            签名私钥
	 * @param msg
	 *            待签名消息
	 * @return base64后的签名结果
	 */
	public static String rsaSignWithSha256(PrivateKey privateKey, byte[] msg) {
		String result;
		byte[] shaMsg = ShaUtils.sha256(msg);
		logger.debug("digest: " + Hex.toHexString(shaMsg));
		byte[] sign = RsaUtils.signWithSha256((RSAPrivateKey) privateKey, shaMsg);
		logger.debug("sign: " + Hex.toHexString(sign));
		result = Base64.getEncoder().encodeToString(sign);
		logger.debug("base64:" + result);
		return result;
	}

	/**
	 * RSA公钥验签
	 * 
	 * 过程说明：先对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）
	 * 生成SHA-256摘要，然后将base64编码后的签名数据进行base64解码， 最后使用摘要和base64解码后的签名进行RSA验签
	 * 
	 * @param publicKey
	 *            验签公钥
	 * @param msg
	 *            被签名消息
	 * @param signB
	 *            待验证签名
	 * @return 验签结果：true(验签成功) 或 false(验签失败)
	 */
	public static boolean rsaVerifyWithSha256(PublicKey publicKey, byte[] msg, String signB) {
		boolean result;
		byte[] shaMsg = ShaUtils.sha256(msg);
		logger.debug("digest: " + Hex.toHexString(shaMsg));
		byte[] sign = Base64.getDecoder().decode(signB);
		result = RsaUtils.verifyWithSha256((RSAPublicKey) publicKey, shaMsg, sign);
		logger.debug("verify: " + result);
		return result;
	}

	/**
	 * SM2私钥签名
	 * 
	 * 过程说明：先对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）
	 * 生成SM3摘要，再对摘要进行SM2签名，最后对生成的签名进行base64编码
	 * 
	 * @param privateKey
	 *            签名私钥
	 * @param userId
	 *            用户标识
	 * @param msg
	 *            待签名消息
	 * @return base64后的签名结果
	 */
	public static String sm2SignWithSm3(CipherParameters privateKey, byte[] userId, byte[] msg) {
		String result;
		// 数据摘要
		byte[] sm3Msg = Sm3Utils.digest(msg);
		logger.debug("digest: " + Hex.toHexString(sm3Msg));
		// 签名
		byte[] sign = Sm2Utils.SignWithSm3(privateKey, userId, sm3Msg);
		logger.debug("sign: " + Hex.toHexString(sign));
		// base64
		result = Base64.getEncoder().encodeToString(sign);
		logger.debug("base64:" + result);
		return result;
	}

	/**
	 * SM2公钥验签
	 * 
	 * 过程说明：先对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）
	 * 生成SM3摘要，然后将base64编码后的签名数据进行base64解码， 最后使用摘要和base64解码后的签名进行SM2验签
	 * 
	 * @param publicKey
	 *            验签公钥
	 * @param userId
	 *            用户标识
	 * @param msg
	 *            被签名消息
	 * @param signB
	 *            待验证签名
	 * @return 验签结果：true(验签成功) 或 false(验签失败)
	 */
	public static boolean sm2VerifyWithSm3(CipherParameters publicKey, byte[] userId, byte[] msg, String signB) {
		boolean result;
		byte[] sm3Msg = Sm3Utils.digest(msg);
		logger.debug("digest: " + Hex.toHexString(sm3Msg));
		byte[] sign = Base64.getDecoder().decode(signB);
		result = Sm2Utils.VerifyWithSm3(publicKey, userId, sm3Msg, sign);
		logger.debug("verify: " + result);
		return result;
	}

}
