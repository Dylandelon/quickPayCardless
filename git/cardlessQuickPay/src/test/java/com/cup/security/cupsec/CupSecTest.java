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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.CipherParameters;
import org.junit.Test;

import com.cup.security.certification.RsaCertUtils;
import com.cup.security.certification.Sm2CertUtils;
import com.cup.security.sm2.Sm2Utils;

/**
 * 说明：
 * 
 * 1）本 test 描述了《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中加解密
 * （签名、验签、敏感信息加解密与敏感信息加密密钥的加解密，包含国际和国密两种算法）的相关功能。
 * 2）为可视化显示，文中用到的非对称密钥均使用二进制字符串表示，实际使用前需要转化为十六进制字符串格式，
 * 二进制字符串--每个字符代表一个字节的字符串，如二进制字符串"3082"，由4个字符'3'、'0'、'8'、'2'组成；
 * 十六进制字符串--每两个字符代表一个字节的字符串，如十六进制字符串"3082"，由字符0x30与0x82组成。
 */
public class CupSecTest {
	private static Logger logger = Logger.getLogger(CupSecTest.class);
	private static String path = ClassLoader.getSystemResource("").getPath();

	/**
	 * 敏感信息 3DES加解密， 敏感信息密钥 RSA加解密
	 * 
	 * 示例数据说明： 
	 * 3DES敏感信息加密示例输入： 
	 *     待加密敏感数据：<ExpDt>a022</ExpDt><CvnNo></CvnNo>
	 *     待加密敏感数据(16进制表示): 3c45787044743e613032323c2f45787044743e3c43766e4e6f3e3c2f43766e4e6f3e
	 *     待加密敏感数据长度：34
	 *     敏感信息加密所用密钥: 123456782234567832345678(字符串, 24byte)
	 *     敏感信息加密算法类型: 3DES
	 *     需要填充的数据长度: 6 
	 *     填充后的敏感数据长度: 40 
	 *     填充后的敏感数据: 3c45787044743e613032323c2f45787044743e3c43766e4e6f3e3c2f43766e4e6f3e000000000000
	 *     加密模式: CBC模式
	 *     初始向量: 8个0（16进制表示0000000000000000）
	 * 3DES敏感信息加密示例输出（包括中间关键结果）： 
	 *     加密后的数据长度: 40 
	 *     加密后的数据(16进制表示):7209a56e1d5c31cb80e25e3a9c61e98e3ae517b3c187472eb2ee97bb7abafdb3515317a139563839
	 *     对加密后的敏感信息进行base64编码后的数据长度: 56 
	 *     对加密后的敏感信息进行base64编码后的数据: cgmlbh1cMcuA4l46nGHpjjrlF7PBh0cusu6Xu3q6/bNRUxehOVY4OQ==
	 * 
	 * 
	 * 对敏感信息加密密钥进行RSA加密的示例输入： 
	 *     敏感信息加密所用密钥: 123456782234567832345678(字符串, 24byte)
	 *     加密敏感信息密钥算法类型: RSA 
	 *     加密敏感信息密钥算法所用填充模式: RSA_PKCS1_PADDING
	 *     加密所用RSA公钥:
	       30819f300d06092a864886f70d010101050003818d003081890281810093d5a7ae0fa32aff1648d
	       e9ecab9b61b965de27c286547d38f94b92eef49bcdce0e73331ff3bd0a878c63c203f07c0732987
	       714030e7cca99e273ca4b2fb77cf8e669add848f360bb96b945ea9cbd09b8a102a355e406760a5e
	       e3b66c48cd4810e17a932a0a7c359fd1916d00052812f1ee5914be338b04e56be5252f18b15bd02
	       03010001
	 * 对敏感信息加密密钥进行RSA加密的示例输出： 
	 *     RSA加密后的密钥长度:128 
	 *     RSA加密后的对称密钥(16进制表示):
	       6e6301673b5f713123790d032faaaa64e6d9dc2a4f4376b130cc9006de44f4bb8dd31032d887e647
	       cf0ca9ef68f48086fd04299495b68090dd4153b1ca7bcfcde62ebf59aaefd013567806bc1ba3c1cb
	       bc251711b730bb15ae97f8fcba57568d2a2f9202a6bd8b1a1c810075e97cd00e7615c169a9c9a907
	       dee89885d1aeb1bb
	 *     加密后的密钥信息base64编码后的长度:172
	 *     加密后的密钥信息base64编码后的数据:
	       bmMBZztfcTEjeQ0DL6qqZObZ3CpPQ3axMMyQBt5E9LuN0xAy2IfmR88Mqe9o9ICG/QQplJW2gJDd
	       QVOxynvPzeYuv1mq79ATVngGvBujwcu8JRcRtzC7Fa6X+Py6V1aNKi+SAqa9ixocgQB16XzQDnYV
	       wWmpyakH3uiYhdGusbs=
	   
	 * 注：
	 * 1）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的3DES加密敏感信息与RSA加密敏感信息密钥，
	 * 	    实际使用时，敏感信息加密算法（国际3DES算法、国密SM4算法）的选择需要与标签<EncAlgo>的取值保持一致， <EncAlgo>取值为0
	 *   时，表示使用国际3DES算法进行加密，取值为1时表示使用国密SM4算法进行加密； 敏感信息加密密钥的算法（国际RSA算法、国密SM2算法）
	 *   选择需要与标签<SignEncAlgo>保持一致，<SignEncAlgo>取值为0时，表示使用RSA算法， 取值为1时表示使用SM2算法；最后，
	 *   将base64编码后的敏感信息加密结果放在报文标签<EncKey>中，将加密敏感信息密钥所用证书序列号(16进制表示)放在放在 标签<EncSN>
	 *   中，将base64编码后的敏感信息加密结果放在标签<SensInf>中。
	 * 2）标签<EncAlgo>的取值需要与标签<SignEncAlgo>的取值保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAl
	 *   go>与<EncAlgo>的取值 要么均为0，要么均为1。
	 * 3）非对称算法RSA与SM2用到的公钥与私钥的编码方式均为DER格式。
	 * 4）3DES加解密需要保证加解密数据长度是 24byte ，SM4加解密需要保证加解密数据长度是16byte。
	 */
	@Test
	public void testSensInf3DESEnc() {
		String sensInf = "<ExpDt>2209</ExpDt><CvnNo></CvnNo>";
		String encKey = "123456782234567832345678";// 24byte
		// 从证书中获取 RSA 公钥
		String filePath;
		filePath = path + "/4000370671.cer";
		PublicKey publicKey = RsaCertUtils.getPubKey(filePath);

		String[] encs = CupSec.sensInf3DEKeySM4SEncrypt(publicKey, sensInf.getBytes(), encKey.getBytes());
		logger.info("sensInfoEB:" + encs[0]);
		logger.info("encKeyEB:" + encs[1]);
	}
	
	/**
	 * 对敏感信息加密密钥进行RSA解密的示例输入：
	 *     敏感信息解密所用密钥: 
	       bmMBZztfcTEjeQ0DL6qqZObZ3CpPQ3axMMyQBt5E9LuN0xAy2IfmR88Mqe9o9ICG/QQplJW2gJDd
	       QVOxynvPzeYuv1mq79ATVngGvBujwcu8JRcRtzC7Fa6X+Py6V1aNKi+SAqa9ixocgQB16XzQDnYV
	       wWmpyakH3uiYhdGusbs=
	 *     敏感信息密钥的解密算法类型: RSA 
	 *     解密敏感信息密钥算法所用填充模式: RSA_PKCS1_PADDING 
	 *     解密敏感信息加密密钥所用RSA私钥: 
	       30820276020100300d06092a864886f70d0101010500048202603082025c0201000281810093d5a7ae
	       0fa32aff1648de9ecab9b61b965de27c286547d38f94b92eef49bcdce0e73331ff3bd0a878c63c203f
	       07c0732987714030e7cca99e273ca4b2fb77cf8e669add848f360bb96b945ea9cbd09b8a102a355e40
	       6760a5ee3b66c48cd4810e17a932a0a7c359fd1916d00052812f1ee5914be338b04e56be5252f18b15
	       bd020301000102818078f1a26cd47e38fed7cc65d4a325abc6860de8ec8a5fd1935333e90c6cd7769b
	       7da3a84e8ef0f093e3baca15d77b1007274297745c8e46f1bd617c4b81e218f186a537449dc2d0319c
	       2560ada2fdd7a63e0dba9435b010020ce739faa80ccf90baf54e4a8806f8111fc9581076bf7f0d4541
	       99caa09876a607afccee58cff841024100c8da27f797cd86e8db95524f7c6f2c2c5448b49a07c0875d
	       7cdcd27e6af2a97bf7dc4668d3e42be809356d1f13d786604b98060e68ef428c5e34260370b2f33b02
	       4100bc6ce858cc8f0b3f612073ee4f4d8aab7565b576be4cd0e0433a2c030090f6da8f985087d73916
	       38eca87edceed94ccb98a308da09bdf91c0c737631876c1b67024077b8518a6b99be889e1a6b6da5a6
	       3e964dc6e89fc76f2340be6481b388dc0bda30ebc3ac4861012ad6125a70e3cadbf61a190bc31b1942
	       7998cd4cbb5039da710240638d9f0d1bb710184276e509aa38abe57f3767b2ff4492af8a95779f7673
	       2119c4f892c3d2c0c4aefacae38c535dd82751d401df417a6fdd93ee852a1204da4d024100889d800b
	       90df6988340803fe3472a50baa5ed0b20910d9c53e457d26a57d3f727fcd7cc1180be1eefca6b7f055
	       00bf12a390d43de364a5268bcc63dba6268904
	 * 对敏感信息加密密钥进行RSA解密的示例输出:
	 *     RSA解密后的密钥长度: 24
	 *     RSA解密后的密钥原文: 123456782234567832345678
	 * 
	 * 3DES敏感信息解密示例输入：
	 *     需要解密的信息长度: 56
	 *     需要解密的信息: cgmlbh1cMcuA4l46nGHpjjrlF7PBh0cusu6Xu3q6/bNRUxehOVY4OQ==
	 *     敏感信息解密算法类型: 3DES
	 *     加密模式: CBC模式
	 *     初始向量: 8个0（16进制表示0000000000000000）            
	 * 3DES敏感信息加密示例结果（包括中间关键结果）：
	 *     解密后的敏感信息长度: 40
	 *     解密后的敏感信息: <ExpDt>a022</ExpDt><CvnNo></CvnNo>	
	 * 
	 * 注：
	 * 1）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的3DES加密敏感信息与RSA加密敏感信息密钥，
	 * 	    实际使用时，敏感信息加密算法（国际3DES算法、国密SM4算法）的选择需要与标签<EncAlgo>的取值保持一致， <EncAlgo>取值为0
	 *   时，表示使用国际3DES算法进行加密，取值为1时表示使用国密SM4算法进行加密； 敏感信息加密密钥的算法（国际RSA算法、国密SM2算法）
	 *   选择需要与标签<SignEncAlgo>保持一致，<SignEncAlgo>取值为0时，表示使用RSA算法， 取值为1时表示使用SM2算法；最后，
	 *   将base64编码后的敏感信息加密结果放在报文标签<EncKey>中，将加密敏感信息密钥所用证书序列号(16进制表示)放在放在 标签<EncSN>
	 *   中，将base64编码后的敏感信息加密结果放在标签<SensInf>中。
	 * 2）标签<EncAlgo>的取值需要与标签<SignEncAlgo>的取值保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAl
	 *   go>与<EncAlgo>的取值 要么均为0，要么均为1。
	 * 3）非对称算法RSA与SM2用到的公钥与私钥的编码方式均为DER格式。
	 */
	@Test
	public void testSensInf3DESDec() {
		// 从证书中获取 RSA 私钥
		String keyfile, keypwd, type;
		keyfile = path + "/rsa_private_1.pfx";
		keypwd = "000000";
		type = "PKCS12";
		PrivateKey privateKey = RsaCertUtils.getPriKeyPkcs12(keyfile, keypwd, type);
		String sensInfo = "cgmlbh1cMcuA4l46nGHpjjrlF7PBh0cusu6Xu3q6/bNRUxehOVY4OQ==";
		String encKey = "bmMBZztfcTEjeQ0DL6qqZObZ3CpPQ3axMMyQBt5E9LuN0xAy2IfmR88Mqe9o9ICG/QQplJW2gJDd"
				 + "QVOxynvPzeYuv1mq79ATVngGvBujwcu8JRcRtzC7Fa6X+Py6V1aNKi+SAqa9ixocgQB16XzQDnYV"
				+ "wWmpyakH3uiYhdGusbs=";
		
		byte[][] decs = CupSec.sensInf3DESKeySM4Decrypt(privateKey, sensInfo, encKey);
		logger.info("sensInfo:" + new String(decs[0]));
		logger.info("encKey:" + new String(decs[1]));
	}

	/**
	 * 敏感信息 SM4 加解密， 敏感信息密钥 SM2 加解密
	 * 示例数据说明： 
	 * SM4敏感信息加密示例输入： 
	 *     待加密敏感数据：<ExpDt>a022</ExpDt><CvnNo></CvnNo>
	 *     待加密敏感数据(16进制表示): 3c45787044743e613032323c2f45787044743e3c43766e4e6f3e3c2f43766e4e6f3e
	 *     待加密敏感数据长度：34
	 *     敏感信息加密所用密钥: 1234567890123456
	 *     敏感信息加密算法类型: SM4
	 *     需要填充的数据长度: 14 
	 *     填充后的敏感数据长度: 48 
	 *     填充后的敏感数据: 
	       3c45787044743e613032323c2f45787044743e3c43766e4e6f3e3c2f43766e4e6f3e00000000000000000000
	       00000000
	 *     加密模式: CBC模式
	 *     初始向量: 16个0（16进制表示00000000000000000000000000000000）             
	 * SM4敏感信息加密示例结果（包括中间关键结果）： 
	 *     加密后的数据长度: 48 
	 *     加密后的数据(16进制表示): 
	       53b39e8951529d84c89b1e498e1be585440cf06c43d8f2c27950fd309e97e70e48e0644c9b62724b31a7c71
	       b47c9bf9f
	 *     对加密后的敏感信息进行base64编码后的数据长度: 64 
	 *     对加密后的敏感信息进行base64编码后的数据: 
	       U7OeiVFSnYTImx5JjhvlhUQM8GxD2PLCeVD9MJ6X5w5I4GRMm2JySzGnxxtHyb+f
	 * 
	 * 对敏感信息加密密钥进行SM2加密的示例输入： 
	 *     敏感信息加密所用密钥: 1234567890123456
	 *     加密敏感信息密钥算法类型: SM2 
	 *     加密对称密钥所用SM2公钥(16进制): 
	       af0ea01e61236c863009b4174d1ec550de327db602ae49a29ebaa4c2583e6443bac6735f06888d4516484d5
	       bfc575ee7f5e8b6dd7f5bdc0d172b2568148a2f2e
	  
	 * 对敏感信息加密密钥进行SM2加密的示例输出： 
	 *     SM2加密后的密钥长度:113 
	 *     SM2加密后的对称密钥(16进制表示):
	       04d555b2a15c01c22f52b433ac6dd497b885cca421b819519a1150a5f6f62d4ff4f3f862ac6cab3fb179d3
	       b357bdbd3e37f184906ab7ad23ae88edc1e052cf7039d2650bce5f2fef7bb1d3c6164acd274b02766502e8
	       ae4dc47ab7d5ccc63df1e17d62f1497ffdd384cbbc9988beb643ad
	 *     加密后的密钥信息base64编码后的长度:152
	 *     加密后的密钥信息base64编码后的数据:
	       BNVVsqFcAcIvUrQzrG3Ul7iFzKQhuBlRmhFQpfb2LU/08/hirGyrP7F507NXvb0+N/GEkGq3rSOu
	       iO3B4FLPcDnSZQvOXy/ve7HTxhZKzSdLAnZlAuiuTcR6t9XMxj3x4X1i8Ul//dOEy7yZiL62Q60=
	 * 注：
	 * 1）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的3DES加密敏感信息与RSA加密敏感信息密钥，
	 * 	    实际使用时，敏感信息加密算法（国际3DES算法、国密SM4算法）的选择需要与标签<EncAlgo>的取值保持一致， <EncAlgo>取值为0
	 *   时，表示使用国际3DES算法进行加密，取值为1时表示使用国密SM4算法进行加密； 敏感信息加密密钥的算法（国际RSA算法、国密SM2算法）
	 *   选择需要与标签<SignEncAlgo>保持一致，<SignEncAlgo>取值为0时，表示使用RSA算法， 取值为1时表示使用SM2算法；最后，
	 *   将base64编码后的敏感信息加密结果放在报文标签<EncKey>中，将加密敏感信息密钥所用证书序列号(16进制表示)放在放在 标签<EncSN>
	 *   中，将base64编码后的敏感信息加密结果放在标签<SensInf>中。
	 * 2）标签<EncAlgo>的取值需要与标签<SignEncAlgo>的取值保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAl
	 *   go>与<EncAlgo>的取值 要么均为0，要么均为1。
	 * 3）非对称算法RSA与SM2用到的公钥与私钥的编码方式均为DER格式。
	 * 4）3DES加解密需要保证加解密数据长度是 24byte ，SM4加解密需要保证加解密数据长度是16byte。
	 */
	@Test
	public void testSensInfSM4EncEnc() {
		String sensInf = "<ExpDt>a022</ExpDt><CvnNo></CvnNo>";
		String encKey = "1234567890123456";// 16byte
		// 从证书中获取 SM2 公钥
		String filePath;
		filePath = path + "/sm2Pub.cer";
		CipherParameters publicKey = Sm2CertUtils.getPubKeyFPubCert(filePath);

		String[] encs = CupSec.sensInfSM4KeySM2Encrypt(publicKey, sensInf.getBytes(), encKey.getBytes());
		logger.info("sensInfoEB:" + encs[0]);
		logger.info("encKeyEB:" + encs[1]);
	}
	
	/**
	 * 对敏感信息加密密钥进行SM2解密的示例输入： 
	 *     敏感信息解密所用密钥（上方的加密结果）:
	       BNVVsqFcAcIvUrQzrG3Ul7iFzKQhuBlRmhFQpfb2LU/08/hirGyrP7F507NXvb0+N/GEkGq3rSOu
	       iO3B4FLPcDnSZQvOXy/ve7HTxhZKzSdLAnZlAuiuTcR6t9XMxj3x4X1i8Ul//dOEy7yZiL62Q60=
	 *     敏感信息密钥的解密算法类型: SM2 
	 *     解密敏感信息加密密钥所用SM2私钥: 9db5db4c74141ab033132c1eb11ed6a5c77c0f7652df0715a6735d606a61fce5
	 * 对敏感信息加密密钥进行SM2解密的示例输出:		
	 *     SM2解密后的密钥长度: 16 
	 *     SM2解密后的密钥原文: 1234567890123456
	 * 
	 * SM4敏感信息解密示例输入：
	 *     需要解密的信息长度: 64
	 *     需要解密的信息: U7OeiVFSnYTImx5JjhvlhUQM8GxD2PLCeVD9MJ6X5w5I4GRMm2JySzGnxxtHyb+f
	 *     敏感信息解密算法类型: SM4
	 *     加密模式: CBC模式 
	 *     初始向量: 16个0（16进制表示00000000000000000000000000000000）
	 * 
	 * SM4敏感信息加密示例结果（包括中间关键结果）： 
	 *     解密后的敏感信息长度: 48 
	 *     解密后的敏感信息: <ExpDt>a022</ExpDt><CvnNo></CvnNo> 
	 *
	 * 注：
	 * 1）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的3DES加密敏感信息与RSA加密敏感信息密钥，
	 * 	    实际使用时，敏感信息加密算法（国际3DES算法、国密SM4算法）的选择需要与标签<EncAlgo>的取值保持一致， <EncAlgo>取值为0
	 *   时，表示使用国际3DES算法进行加密，取值为1时表示使用国密SM4算法进行加密； 敏感信息加密密钥的算法（国际RSA算法、国密SM2算法）
	 *   选择需要与标签<SignEncAlgo>保持一致，<SignEncAlgo>取值为0时，表示使用RSA算法， 取值为1时表示使用SM2算法；最后，
	 *   将base64编码后的敏感信息加密结果放在报文标签<EncKey>中，将加密敏感信息密钥所用证书序列号(16进制表示)放在放在 标签<EncSN>
	 *   中，将base64编码后的敏感信息加密结果放在标签<SensInf>中。
	 * 2）标签<EncAlgo>的取值需要与标签<SignEncAlgo>的取值保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAl
	 *   go>与<EncAlgo>的取值 要么均为0，要么均为1。
	 * 3）非对称算法RSA与SM2用到的公钥与私钥的编码方式均为DER格式。
	 */
	@Test
	public void testSensInfSM4EncDec() {
		String sensInfo = "U7OeiVFSnYTImx5JjhvlhUQM8GxD2PLCeVD9MJ6X5w5I4GRMm2JySzGnxxtHyb+f";
		String encKey = "BNVVsqFcAcIvUrQzrG3Ul7iFzKQhuBlRmhFQpfb2LU/08/hirGyrP7F507NXvb0+N/GEkGq3rSOu"
					+  "iO3B4FLPcDnSZQvOXy/ve7HTxhZKzSdLAnZlAuiuTcR6t9XMxj3x4X1i8Ul//dOEy7yZiL62Q60=";
		
		byte[] privateK = Base64.decodeBase64("nbXbTHQUGrAzEywesR7Wpcd8D3ZS3wcVpnNdYGph/OU=");
		logger.debug("private key:" + Hex.encodeHexString(privateK));
		CipherParameters privateKey = Sm2Utils.sm2PriKeyGet(Hex.encodeHexString(privateK));
		
		byte[][] decs = CupSec.sensInfSM4KeySM2Decrypt(privateKey, sensInfo , encKey);
		logger.info("sensInfo:" + new String(decs[0]));
		logger.info("encKey:" + new String(decs[1]));
	}

	/**
	 * 消息 RSA签名
	 * 示例输入：
	 *     待签名数据：
	 *     <root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</SndDt><Trxtyp>0001</Tr
	       xtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>11111111111</EncSN><EncKey>bsXn+G/
	       /Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98
	       nPoc0fzjKv8HUkKm8X7Fur9jCVELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhh
	       gxLfXnbwZp4kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4MLk3
	       mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3MP5ug==</EncKey><Si
	       gnSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo>0</EncA
	       lgo></MsgHeader><MsgBody><BizTp>100001</BizTp><TrxInf><TrxId>0216180040179177</TrxId><Tr
	       xDtTm>2017-02-16T14:00:00</TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAc
	       ctIssrId><RcverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>01</I
	       DTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></RcverInf><SderInf><SderA
	       cctIssrId>00080008</SderAcctIssrId><SderIssrId>00040004</SderIssrId></SderInf><SensInf><
	       ExpDt>a022</ExpDt><CvnNo></CvnNo></SensInf><CorpCard><CorpName>wangwang</CorpName><USCCo
	       de>01111111</USCCode></CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInfo
	       rmation>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</deviceMode><
	       deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sourceIP><MAC>00247e0a6c2e00
	       247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>1235455855903434939</devId><extensiveDevi
	       ceLocation>11111111</extensiveDeviceLocation><deviceNumber>1380000000</deviceNumber><devi
	       ceSIMNumber>a</deviceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScor
	       e>1a</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216140004</mchn
	       tUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgstrEmail><rcvProvince>003</rcv
	       Province><rcvCity>1200</rcvCity><goodsClass>01</goodsClass></RskInf><test>hello world</te
	       st></MsgBody></root> 
	 *     RSA私钥明文（16进制字符串形式）:
	 *     30820276020100300d06092a864886f70d0101010500048202603082025c0201000281810093d5a7ae0fa32af
	       f1648de9ecab9b61b965de27c286547d38f94b92eef49bcdce0e73331ff3bd0a878c63c203f07c07329877140
	       30e7cca99e273ca4b2fb77cf8e669add848f360bb96b945ea9cbd09b8a102a355e406760a5ee3b66c48cd4810
	       e17a932a0a7c359fd1916d00052812f1ee5914be338b04e56be5252f18b15bd020301000102818078f1a26cd47
	       e38fed7cc65d4a325abc6860de8ec8a5fd1935333e90c6cd7769b7da3a84e8ef0f093e3baca15d77b100727429
	       7745c8e46f1bd617c4b81e218f186a537449dc2d0319c2560ada2fdd7a63e0dba9435b010020ce739faa80ccf9
	       0baf54e4a8806f8111fc9581076bf7f0d454199caa09876a607afccee58cff841024100c8da27f797cd86e8db9
	       5524f7c6f2c2c5448b49a07c0875d7cdcd27e6af2a97bf7dc4668d3e42be809356d1f13d786604b98060e68ef4
	       28c5e34260370b2f33b024100bc6ce858cc8f0b3f612073ee4f4d8aab7565b576be4cd0e0433a2c030090f6da8
	       f985087d7391638eca87edceed94ccb98a308da09bdf91c0c737631876c1b67024077b8518a6b99be889e1a6b6
	       da5a63e964dc6e89fc76f2340be6481b388dc0bda30ebc3ac4861012ad6125a70e3cadbf61a190bc31b1942799
	       8cd4cbb5039da710240638d9f0d1bb710184276e509aa38abe57f3767b2ff4492af8a95779f76732119c4f892c
	       3d2c0c4aefacae38c535dd82751d401df417a6fdd93ee852a1204da4d024100889d800b90df6988340803fe347
	       2a50baa5ed0b20910d9c53e457d26a57d3f727fcd7cc1180be1eefca6b7f05500bf12a390d43de364a5268bcc6
	       3dba6268904
	 * 示例结果（包括中间关键结果）：
	 *     SHA-256摘要长度:32 
	 *     SHA-256摘要结果: 8a832653b2af780a66bde8fe00c3927e28acc8bcd7bd296e86d0bd23c1ad5e8a
	 *     RSA签名结果（16进制字符串形式）:
	       914d5244acd4aa951fd8ad97f42edac068159201da5013ff765427cd0d219466bca8017a9aa87707991e616aaa
	       0a897dad66766d2f06bbfb15175f7371002af39e8bd3bd4c3345644683228201a35dce37b9cd4a0bf12d0c5a35
	       7a626803f5364d6b210c3e4ebe11c139579e6d7665bc2043e829cc913653689c356bea0ecb7d
	 *     RSA签名base64编码结果:
	 *     kU1SRKzUqpUf2K2X9C7awGgVkgHaUBP/dlQnzQ0hlGa8qAF6mqh3B5keYWqqCol9rWZ2bS8Gu/sV
	       F19zcQAq856L071MM0VkRoMiggGjXc43uc1KC/EtDFo1emJoA/U2TWshDD5OvhHBOVeebXZlvCBD
	       6CnMkTZTaJw1a+oOy30=
	 * 
	 * 注：
	 * 1）因签名算法中有随机因子，因此相同的待签名数据每次生成的签名结果可能不同，但其摘要是唯一的。
	 * 2）先对报文内容做摘要，然后对摘要进行签名，在签名时对签名内容（即摘要）再次计算摘要然后才进行加密得到签名，
	 *   因此，签名结果是对两次摘要结果进行加密后的结果，后面的验签也是同样的逻辑，上面计算出的SHA-256摘要结果
	 *   是第一次摘要结果，是对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）的摘要值。
	 * 3）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的RSA签名，
	 *   实际使用时，签名算法（国际RSA算法、国密SM2算法）的选择需要与标签<SignEncAlgo>的取值保持一致，
	 *   <SignEncAlgo>取值为0时，表示使用国际RSA算法进行签名，取值为1时表示使用国密SM2算法进行签名；
	 *   摘要算法的选择需要与标签<MDAlgo>的取值保持一致，<MDAlgo>取值为0时，表示使用国际SHA-256算法计算摘要，
	 *   取值为1时表示使用国密SM3算法计算摘要；同时，将签名所用证书id（16进制字符串格式，字母为小写）放在标签<SignSN>中；
	 *   最后，将base64编码后的签名结果放在报文结尾处，格式为{S:base64编码后的签名}。
	 * 4）摘要算法与签名算法的算法类型需要保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAlgo>与<MDAlgo>的取值
	 *   要么均为0，要么均为1。
	 * 5）私钥编码方式使用的是DER格式。
	 */
	@Test
	public void testRsaSign() {
		// 从证书中获取 RSA 公私钥
		String pfxkeyfile, keypwd, type;
		pfxkeyfile = path + "/rsa_private_1.pfx";
		keypwd = "000000";
		type = "PKCS12";
		PrivateKey privateKey = RsaCertUtils.getPriKeyPkcs12(pfxkeyfile, keypwd, type);

		String msg = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</Snd"
				+ "Dt><Trxtyp>0001</Trxtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>"
				+ "11111111111</EncSN><EncKey>bsXn+G//Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4"
				+ "LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98nPoc0fzjKv8HUkKm8X7Fur9jC"
				+ "VELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhhgxLfXnbwZp4"
				+ "kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4"
				+ "MLk3mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3M"
				+ "P5ug==</EncKey><SignSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo"
				+ ">0</SignEncAlgo><EncAlgo>0</EncAlgo></MsgHeader><MsgBody><BizTp>100001</B"
				+ "izTp><TrxInf><TrxId>0216180040179177</TrxId><TrxDtTm>2017-02-16T14:00:00<"
				+ "/TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAcctIssrId><R"
				+ "cverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>0"
				+ "1</IDTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></Rcve"
				+ "rInf><SderInf><SderAcctIssrId>00080008</SderAcctIssrId><SderIssrId>000400"
				+ "04</SderIssrId></SderInf><SensInf><ExpDt>a022</ExpDt><CvnNo></CvnNo></Sen"
				+ "sInf><CorpCard><CorpName>wangwang</CorpName><USCCode>01111111</USCCode></"
				+ "CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInformatio"
				+ "n>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</de"
				+ "viceMode><deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sou"
				+ "rceIP><MAC>00247e0a6c2e00247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>12"
				+ "35455855903434939</devId><extensiveDeviceLocation>11111111</extensiveDevi"
				+ "ceLocation><deviceNumber>1380000000</deviceNumber><deviceSIMNumber>a</devi"
				+ "ceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScore>1a"
				+ "</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216"
				+ "140004</mchntUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgst"
				+ "rEmail><rcvProvince>003</rcvProvince><rcvCity>1200</rcvCity><goodsClass>01"
				+ "</goodsClass></RskInf><test>hello world</test></MsgBody></root>";
		String sign = null;

		sign = CupSec.rsaSignWithSha256(privateKey, msg.getBytes());
		logger.info("sign:" + sign);
	}
	
	/**
	 * 消息 RSA签名验签
	 * 示例输入：
	 *     待验签数据：
	 *     <root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</SndDt><Trxtyp>0001</Tr
	       xtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>11111111111</EncSN><EncKey>bsXn+G/
	       /Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98
	       nPoc0fzjKv8HUkKm8X7Fur9jCVELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhh
	       gxLfXnbwZp4kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4MLk3
	       mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3MP5ug==</EncKey><Si
	       gnSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo>0</EncA
	       lgo></MsgHeader><MsgBody><BizTp>100001</BizTp><TrxInf><TrxId>0216180040179177</TrxId><Tr
	       xDtTm>2017-02-16T14:00:00</TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAc
	       ctIssrId><RcverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>01</I
	       DTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></RcverInf><SderInf><SderA
	       cctIssrId>00080008</SderAcctIssrId><SderIssrId>00040004</SderIssrId></SderInf><SensInf><
	       ExpDt>a022</ExpDt><CvnNo></CvnNo></SensInf><CorpCard><CorpName>wangwang</CorpName><USCCo
	       de>01111111</USCCode></CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInfo
	       rmation>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</deviceMode><
	       deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sourceIP><MAC>00247e0a6c2e00
	       247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>1235455855903434939</devId><extensiveDevi
	       ceLocation>11111111</extensiveDeviceLocation><deviceNumber>1380000000</deviceNumber><devi
	       ceSIMNumber>a</deviceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScor
	       e>1a</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216140004</mchn
	       tUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgstrEmail><rcvProvince>003</rcv
	       Province><rcvCity>1200</rcvCity><goodsClass>01</goodsClass></RskInf><test>hello
	       world</te st></MsgBody></root> 
	 *     待验证的签名数据(即前面RSA生成的签名):
	 *     kU1SRKzUqpUf2K2X9C7awGgVkgHaUBP/dlQnzQ0hlGa8qAF6mqh3B5keYWqqCol9rWZ2bS8Gu/sV
	       F19zcQAq856L071MM0VkRoMiggGjXc43uc1KC/EtDFo1emJoA/U2TWshDD5OvhHBOVeebXZlvCBD
	       6CnMkTZTaJw1a+oOy30=
	 *     RSA公钥明文（16进制字符串形式）:
	 *     30819f300d06092a864886f70d010101050003818d003081890281810093d5a7ae0fa32aff1648de9ecab9b6
	       1b965de27c286547d38f94b92eef49bcdce0e73331ff3bd0a878c63c203f07c0732987714030e7cca99e273c
	       a4b2fb77cf8e669add848f360bb96b945ea9cbd09b8a102a355e406760a5ee3b66c48cd4810e17a932a0a7c3
	       59fd1916d00052812f1ee5914be338b04e56be5252f18b15bd0203010001
	 * 示例结果（包括中间关键结果）： 
	 *     SHA-256摘要长度:32 
	 *     SHA-256摘要结果: 8a832653b2af780a66bde8fe00c3927e28acc8bcd7bd296e86d0bd23c1ad5e8a 
	 *     RSA验签结果: 验签成功 （true）
	 * 注： 
	 * 1）先对报文内容做摘要，然后对摘要进行验签，验签时对验签内容（即摘要）再次计算摘要然后才进行解密得到验签结果，
	 *   因此，验签结果是对两次摘要结果进行解密后的结果，与前面的签名是同样的逻辑，上面计算出的SHA-256摘要结果
	 *   是第一次摘要结果，是对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）的摘要值。
	 * 2）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的RSA验签，
	 *   实际使用时，验签时的签名数据来自于报文结尾处，即{S:base64编码后的签名数据}中的签名数据，验签算法
	 *   （国际RSA算法、国密SM2算法）的选择是根据标签<SignEncAlgo>的取值，<SignEncAlgo>取值为0时，
	 *   表示使用国际RSA算法进行验签，取值为1时表示使用国密SM2算法进行验签；摘要算法的选择是根据标签<MDAlgo>的取值保持一致，
	 *   <MDAlgo>取值为0时，表示使用国际SHA-256算法计算摘要，取值为1时表示使用国密SM3算法计算摘要；
	 * 3）摘要算法与验签算法的算法类型需要保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAlgo>与<MDAlgo>的取值
	 *   要么均为0，要么均为1。 
	 * 4）公钥编码方式使用的是DER格式。
	 */
	@Test
	public void testRsaVerify() {
		//从证书中获取公钥
		String filePath;
		filePath = path + "/rsa_public_1.cer";
		PublicKey publicKey = RsaCertUtils.getPubKey(filePath);
		
		String msg = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</Snd"
				+ "Dt><Trxtyp>0001</Trxtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>"
				+ "11111111111</EncSN><EncKey>bsXn+G//Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4"
				+ "LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98nPoc0fzjKv8HUkKm8X7Fur9jC"
				+ "VELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhhgxLfXnbwZp4"
				+ "kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4"
				+ "MLk3mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3M"
				+ "P5ug==</EncKey><SignSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo"
				+ ">0</SignEncAlgo><EncAlgo>0</EncAlgo></MsgHeader><MsgBody><BizTp>100001</B"
				+ "izTp><TrxInf><TrxId>0216180040179177</TrxId><TrxDtTm>2017-02-16T14:00:00<"
				+ "/TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAcctIssrId><R"
				+ "cverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>0"
				+ "1</IDTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></Rcve"
				+ "rInf><SderInf><SderAcctIssrId>00080008</SderAcctIssrId><SderIssrId>000400"
				+ "04</SderIssrId></SderInf><SensInf><ExpDt>a022</ExpDt><CvnNo></CvnNo></Sen"
				+ "sInf><CorpCard><CorpName>wangwang</CorpName><USCCode>01111111</USCCode></"
				+ "CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInformatio"
				+ "n>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</de"
				+ "viceMode><deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sou"
				+ "rceIP><MAC>00247e0a6c2e00247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>12"
				+ "35455855903434939</devId><extensiveDeviceLocation>11111111</extensiveDevi"
				+ "ceLocation><deviceNumber>1380000000</deviceNumber><deviceSIMNumber>a</devi"
				+ "ceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScore>1a"
				+ "</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216"
				+ "140004</mchntUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgst"
				+ "rEmail><rcvProvince>003</rcvProvince><rcvCity>1200</rcvCity><goodsClass>01"
				+ "</goodsClass></RskInf><test>hello world</test></MsgBody></root>";
		String sign = null;
		boolean verify = false;
		
		sign = "kU1SRKzUqpUf2K2X9C7awGgVkgHaUBP/dlQnzQ0hlGa8qAF6mqh3B5keYWqqCol9rWZ2bS8Gu/sV"
				+ "F19zcQAq856L071MM0VkRoMiggGjXc43uc1KC/EtDFo1emJoA/U2TWshDD5OvhHBOVeebXZlvCBD"
				+ "6CnMkTZTaJw1a+oOy30=";
		
		verify = CupSec.rsaVerifyWithSha256(publicKey, msg.getBytes(), sign);
		logger.info("verify:" + verify);
	}

	/**
	 * 消息 SM2 签名
	 * 示例输入：
	 *     待签名数据：
	 *     <root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</SndDt><Trxtyp>0001</Tr
	       xtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>11111111111</EncSN><EncKey>bsXn+G/
	       /Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98
	       nPoc0fzjKv8HUkKm8X7Fur9jCVELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhh
	       gxLfXnbwZp4kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4MLk3
	       mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3MP5ug==</EncKey><Si
	       gnSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo>0</EncA
	       lgo></MsgHeader><MsgBody><BizTp>100001</BizTp><TrxInf><TrxId>0216180040179177</TrxId><Tr
	       xDtTm>2017-02-16T14:00:00</TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAc
	       ctIssrId><RcverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>01</I
	       DTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></RcverInf><SderInf><SderA
	       cctIssrId>00080008</SderAcctIssrId><SderIssrId>00040004</SderIssrId></SderInf><SensInf><
	       ExpDt>a022</ExpDt><CvnNo></CvnNo></SensInf><CorpCard><CorpName>wangwang</CorpName><USCCo
	       de>01111111</USCCode></CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInfo
	       rmation>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</deviceMode><
	       deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sourceIP><MAC>00247e0a6c2e00
	       247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>1235455855903434939</devId><extensiveDevi
	       ceLocation>11111111</extensiveDeviceLocation><deviceNumber>1380000000</deviceNumber><devi
	       ceSIMNumber>a</deviceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScor
	       e>1a</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216140004</mchn
	       tUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgstrEmail><rcvProvince>003</rcv
	       Province><rcvCity>1200</rcvCity><goodsClass>01</goodsClass></RskInf><test>hello world</te
	       st></MsgBody></root> 
	 *     SM2私钥明文（16进制字符串形式）: 9db5db4c74141ab033132c1eb11ed6a5c77c0f7652df0715a6735d606a61fce5
	 * 示例结果（包括中间关键结果）：
	 *     SM3摘要长度:32 
	 *     SM3摘要: 322c722c393c7cc01f32ae346e5d37ee67d1f74988a3f384751d25e7369998a6
	 *     SM2签名base64编码结果:
	 *     IUu0jFXHRGrPweCezQY9/Ntef3AVNWkmKmA5QUksCdGRPhtSx8MKBec9hOCq3FIthpaGctYzZLeKlkzZmEnpLQ==
	 * 
	 * 注：
	 * 1）因签名算法中有随机因子，因此相同的待签名数据每次生成的签名结果可能不同，但其摘要是唯一的。
	 * 2）先对报文内容做摘要，然后对摘要进行签名，在签名时对签名内容（即摘要）再次计算摘要然后才进行加密得到签名，
	 *   因此，签名结果是对两次摘要结果进行加密后的结果，后面的验签也是同样的逻辑，上面计算出的SM3摘要结果
	 *   是第一次摘要结果，是对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）的摘要值。
	 * 3）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的RSA签名，
	 *   实际使用时，签名算法（国际RSA算法、国密SM2算法）的选择需要与标签<SignEncAlgo>的取值保持一致，
	 *    <SignEncAlgo>取值为0时，表示使用国际RSA算法进行签名，取值为1时表示使用国密SM2算法进行签名；
	 *   摘要算法的选择需要与标签<MDAlgo>的取值保持一致，<MDAlgo>取值为0时，表示使用国际SHA-256算法计算摘要，
	 *   取值为1时表示使用国密SM3算法计算摘要；同时，将签名所用证书id（16进制字符串格式，字母为小写）放在标签<SignSN>中；
	 *   最后，将base64编码后的签名结果放在报文结尾处，格式为{S:base64编码后的签名}。
	 * 4）摘要算法与签名算法的算法类型需要保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAlgo>与<MDAlgo>的取值
	 *   要么均为0，要么均为1。 
	 * 5）公钥与私钥的编码方式使用的是DER格式。 
	 * 6)SM2签名需要使用16进制表示的SM2证书序列号（即证书id），若证书序列号长度超过32字节，则只取前32字节。
	 */
	@Test
	public void testSm2SignWithSm3() {
		byte[] pri = Base64.decodeBase64("nbXbTHQUGrAzEywesR7Wpcd8D3ZS3wcVpnNdYGph/OU=");
		logger.debug("private key:" + Hex.encodeHexString(pri));
		CipherParameters privateKey = Sm2Utils.sm2PriKeyGet(Hex.encodeHexString(pri));

		String msg = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</Snd"
				+ "Dt><Trxtyp>0001</Trxtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>"
				+ "11111111111</EncSN><EncKey>bsXn+G//Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4"
				+ "LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98nPoc0fzjKv8HUkKm8X7Fur9jC"
				+ "VELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhhgxLfXnbwZp4"
				+ "kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4"
				+ "MLk3mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3M"
				+ "P5ug==</EncKey><SignSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo"
				+ ">0</SignEncAlgo><EncAlgo>0</EncAlgo></MsgHeader><MsgBody><BizTp>100001</B"
				+ "izTp><TrxInf><TrxId>0216180040179177</TrxId><TrxDtTm>2017-02-16T14:00:00<"
				+ "/TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAcctIssrId><R"
				+ "cverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>0"
				+ "1</IDTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></Rcve"
				+ "rInf><SderInf><SderAcctIssrId>00080008</SderAcctIssrId><SderIssrId>000400"
				+ "04</SderIssrId></SderInf><SensInf><ExpDt>a022</ExpDt><CvnNo></CvnNo></Sen"
				+ "sInf><CorpCard><CorpName>wangwang</CorpName><USCCode>01111111</USCCode></"
				+ "CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInformatio"
				+ "n>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</de"
				+ "viceMode><deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sou"
				+ "rceIP><MAC>00247e0a6c2e00247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>12"
				+ "35455855903434939</devId><extensiveDeviceLocation>11111111</extensiveDevi"
				+ "ceLocation><deviceNumber>1380000000</deviceNumber><deviceSIMNumber>a</devi"
				+ "ceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScore>1a"
				+ "</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216"
				+ "140004</mchntUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgst"
				+ "rEmail><rcvProvince>003</rcvProvince><rcvCity>1200</rcvCity><goodsClass>01"
				+ "</goodsClass></RskInf><test>hello world</test></MsgBody></root>";
		String sign = null;
		//用户标识，目前实际应使用证书序列号
		byte[] userId = new byte[1];
		sign = CupSec.sm2SignWithSm3(privateKey, userId, msg.getBytes());
		logger.info("sign:" + sign);
	}
	
	/**
	 * 消息 SM2 验签 ：
	 * 示例输入：
	 *     待验签数据：
	 *     <root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</SndDt><Trxtyp>0001</Tr
	       xtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>11111111111</EncSN><EncKey>bsXn+G/
	       /Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98
	       nPoc0fzjKv8HUkKm8X7Fur9jCVELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhh
	       gxLfXnbwZp4kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4MLk3
	       mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3MP5ug==</EncKey><Si
	       gnSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo>0</SignEncAlgo><EncAlgo>0</EncA
	       lgo></MsgHeader><MsgBody><BizTp>100001</BizTp><TrxInf><TrxId>0216180040179177</TrxId><Tr
	       xDtTm>2017-02-16T14:00:00</TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAc
	       ctIssrId><RcverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>01</I
	       DTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></RcverInf><SderInf><SderA
	       cctIssrId>00080008</SderAcctIssrId><SderIssrId>00040004</SderIssrId></SderInf><SensInf><
	       ExpDt>a022</ExpDt><CvnNo></CvnNo></SensInf><CorpCard><CorpName>wangwang</CorpName><USCCo
	       de>01111111</USCCode></CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInfo
	       rmation>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</deviceMode><
	       deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sourceIP><MAC>00247e0a6c2e00
	       247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>1235455855903434939</devId><extensiveDevi
	       ceLocation>11111111</extensiveDeviceLocation><deviceNumber>1380000000</deviceNumber><devi
	       ceSIMNumber>a</deviceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScor
	       e>1a</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216140004</mchn
	       tUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgstrEmail><rcvProvince>003</rcv
	       Province><rcvCity>1200</rcvCity><goodsClass>01</goodsClass></RskInf><test>hello world</te
	       st></MsgBody></root> 
	 *     待验证的签名数据(即签名SM2生成的签名):
	 *     IUu0jFXHRGrPweCezQY9/Ntef3AVNWkmKmA5QUksCdGRPhtSx8MKBec9hOCq3FIthpaGctYzZLeKlkzZmEnpLQ==
	 *     SM2验签所用公钥(16进制输出):
	 *     af0ea01e61236c863009b4174d1ec550de327db602ae49a29ebaa4c2583e6443bac6735f06888d4516484d5bf
	 *     c575ee7f5e8b6dd7f5bdc0d172b2568148a2f2e
	 * 示例结果（包括中间关键结果）：
	 *     SM3摘要长度: 32
	 *     SM3摘要: 322c722c393c7cc01f32ae346e5d37ee67d1f74988a3f384751d25e7369998a6
	 *     SM2验签结果: 验签成功 （ true）
	 * 注： 
	 * 1）先对报文内容做摘要，然后对摘要进行验签，验签时对验签内容（即摘要）再次计算摘要然后才进行解密得到验签结果，
	 *   因此，验签结果是对两次摘要结果进行解密后的结果，与前面的签名是同样的逻辑，上面计算出的SM3摘要结果
	 *   是第一次摘要结果，是对报文中<root></root>标签中的所有内容（包括<root>和</root>标签自身）的摘要值。
	 * 2）此处只是为了演示《中国银联无卡快捷支付技术规范 第1部分 交易处理及报文接口规范》中用到的RSA验签，
	 *   实际使用时，验签时的签名数据来自于报文结尾处，即{S:base64编码后的签名数据}中的签名数据，验签算法
	 *   （国际RSA算法、国密SM2算法）的选择是根据标签<SignEncAlgo>的取值，<SignEncAlgo>取值为0时，
	 *   表示使用国际RSA算法进行验签，取值为1时表示使用国密SM2算法进行验签；摘要算法的选择是根据标签<MDAlgo>的取值保持一致，
	 *   <MDAlgo>取值为0时，表示使用国际SHA-256算法计算摘要，取值为1时表示使用国密SM3算法计算摘要；
	 * 3）摘要算法与验签算法的算法类型需要保持一致，即要么均为国际算法，要么均为国密算法，故标签<SignEncAlgo>与<MDAlgo>的取值
	 *   要么均为0，要么均为1。 
	 * 4）公钥编码方式使用的是DER格式。
	 * 5)SM2验签需要使用16进制表示的SM2证书序列号（即证书id），若证书序列号长度超过32字节，则只取前32字节。
	 */		
	@Test
	public void testSm2VerifyWithSm3() {
		// 从证书中获取公钥
		String filePath;
		filePath = path + "/sm2Pub.cer";
		CipherParameters publicKey = Sm2CertUtils.getPubKeyFPubCert(filePath);
		
		String msg = "<root><MsgHeader><MsgVer>1000</MsgVer><SndDt>2017-02-16T14:00:00</Snd"
				+ "Dt><Trxtyp>0001</Trxtyp><IssrId>00040004</IssrId><Drctn>11</Drctn><EncSN>"
				+ "11111111111</EncSN><EncKey>bsXn+G//Zxghw8sZDmQ9iHCJW0gxKpYMkMUaG/USL0yK+4"
				+ "LwbpKIjmZQF1E40zyeSub2rK5TyysyXj6nsNgWbPDb6ILP98nPoc0fzjKv8HUkKm8X7Fur9jC"
				+ "VELqYI4mU9BsfMvJ1nfZ+FIDlk6GDHXUT+jiaIdFzflT+P3H2x0saiUQwrVNhhgxLfXnbwZp4"
				+ "kNoAJ/M9yOCf7bI7JU8blRymt56vWeudFIqyvozZKTZtnQj8EbX7OKgC4gNItaoaRD/93AhR4"
				+ "MLk3mp1L6ZSHy8dt6lnXcILeVv+MK9Id3lh1wu7ipYn+Fe0xkunChiScdGEE1GjFO14NXvc3M"
				+ "P5ug==</EncKey><SignSN>11111111111</SignSN><MDAlgo>0</MDAlgo><SignEncAlgo"
				+ ">0</SignEncAlgo><EncAlgo>0</EncAlgo></MsgHeader><MsgBody><BizTp>100001</B"
				+ "izTp><TrxInf><TrxId>0216180040179177</TrxId><TrxDtTm>2017-02-16T14:00:00<"
				+ "/TrxDtTm></TrxInf><RcverInf><RcverAcctIssrId>99999999</RcverAcctIssrId><R"
				+ "cverAcctId>6260000000000000</RcverAcctId><RcverNm>fuxuan</RcverNm><IDTp>0"
				+ "1</IDTp><IDNo>00000000000000111111</IDNo><MobNo>13012345678</MobNo></Rcve"
				+ "rInf><SderInf><SderAcctIssrId>00080008</SderAcctIssrId><SderIssrId>000400"
				+ "04</SderIssrId></SderInf><SensInf><ExpDt>a022</ExpDt><CvnNo></CvnNo></Sen"
				+ "sInf><CorpCard><CorpName>wangwang</CorpName><USCCode>01111111</USCCode></"
				+ "CorpCard><ProductInf><ProductTp>00000000</ProductTp><ProductAssInformatio"
				+ "n>haahah</ProductAssInformation></ProductInf><RskInf><deviceMode>1111</de"
				+ "viceMode><deviceLanguage>001</deviceLanguage><sourceIP>172.17.254.243</sou"
				+ "rceIP><MAC>00247e0a6c2e00247e0a6c2e00247e0a6c2e00247e0a6c2e</MAC><devId>12"
				+ "35455855903434939</devId><extensiveDeviceLocation>11111111</extensiveDevi"
				+ "ceLocation><deviceNumber>1380000000</deviceNumber><deviceSIMNumber>a</devi"
				+ "ceSIMNumber><accountIDHash>1a0e0c0246809fe5c2</accountIDHash><riskScore>1a"
				+ "</riskScore><riskReasonCode>0069</riskReasonCode><mchntUsrRgstrTm>2017s216"
				+ "140004</mchntUsrRgstrTm><mchntUsrRgstrEmail>www.unionpay.com</mchntUsrRgst"
				+ "rEmail><rcvProvince>003</rcvProvince><rcvCity>1200</rcvCity><goodsClass>01"
				+ "</goodsClass></RskInf><test>hello world</test></MsgBody></root>";
		boolean verify = false;
		//用户标识，目前实际应使用证书序列号
		byte[] userId = new byte[1];
		String sign = "IUu0jFXHRGrPweCezQY9/Ntef3AVNWkmKmA5QUksCdGRPhtSx8MKBec9hOCq3FIthpaGctYz"
				+ "ZLeKlkzZmEnpLQ==";
		
		verify = CupSec.sm2VerifyWithSm3(publicKey, userId, msg.getBytes(), sign);
		logger.info("verify:" + verify);
	}

}
