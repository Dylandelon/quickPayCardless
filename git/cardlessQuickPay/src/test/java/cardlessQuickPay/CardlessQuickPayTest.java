package cardlessQuickPay;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.bouncycastle.crypto.CipherParameters;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import com.cup.security.cupsec.CupSec;
import com.cup.security.sm2.Sm2Utils;

import cardlessQuickPay.utils.HttpClientHelper;

public class CardlessQuickPayTest {
	private static Logger logger = Logger.getLogger(CardlessQuickPayTest.class);
	private static String path = ClassLoader.getSystemResource("").getPath();
	@Test
	public void testSm2SignWithSm3() {
		byte[] pri = Base64.decodeBase64("nbXbTHQUGrAzEywesR7Wpcd8D3ZS3wcVpnNdYGph/OU=");
		logger.debug("private key:" + Hex.encodeHexString(pri));
		CipherParameters privateKey = Sm2Utils.sm2PriKeyGet(Hex.encodeHexString(pri));
		String xml1 ="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		StringBuffer sb = new StringBuffer();
		// 创建Document对象
        Document document = DocumentHelper.createDocument();
        // 创建根节点
        Element root = document.addElement("root");
        // 创建报文头
        Element MsgHeader = root.addElement("MsgHeader");
        
        // 报文版本
        Element MsgVer = MsgHeader.addElement("MsgVer");
        MsgVer.setText("1000");
        // 交易类型
        Element Trxtyp = MsgHeader.addElement("Trxtyp");
        Trxtyp.setText("0001");
        // 报文方向
        Element Drctn = MsgHeader.addElement("Drctn");
        Drctn.setText("11");
        // 报文发起日期时间
        Element SndDt = MsgHeader.addElement("SndDt");
        SndDt.setText(LocalDateTime.now().toString());
        // 交易金额
//        Element TrxAmt = MsgHeader.addElement("TrxAmt");
//        TrxAmt.setText("");
        // 发起方所属机构标识
        Element IssrId = MsgHeader.addElement("IssrId");
        IssrId.setText("49360000");
        // 签名和密钥加密算法类型
        Element SignEncAlgo = MsgHeader.addElement("SignEncAlgo");
        SignEncAlgo.setText("1");
        // 签名证书序列号
        Element SignSN = MsgHeader.addElement("SignSN");
        SignSN.setText("4000370700");
        // 摘要算法类型
        Element MDAlgo = MsgHeader.addElement("MDAlgo");
        MDAlgo.setText("1");
        // 摘要算法类型
        Element EncKey = MsgHeader.addElement("EncKey");
        EncKey.setText("1234567890ABCDEF1234567890ABCDEF");
        // 加密证书序列号
        Element EncSN = MsgHeader.addElement("EncSN");
        EncSN.setText("4000370687");
        // 对称加密算法类型
        Element EncAlgo = MsgHeader.addElement("EncAlgo");
        EncAlgo.setText("1");
        
     // 创建报文体
//        Element MsgBody = root.addElement("MsgBody");
        

        // 创建输出格式(OutputFormat对象)
        OutputFormat format = OutputFormat.createPrettyPrint();

        ///设置输出文件的编码
        format.setEncoding("utf-8");
        format.setNewlines(false);
        format.setIndent(false);

        // stringWriter字符串是用来保存XML文档的  
        StringWriter stringWriter = new StringWriter(); 
        try {
        	 
            // xmlWriter是用来把XML文档写入字符串的(工具)  
            // 创建XMLWriter对象
//            XMLWriter writer = new XMLWriter(new FileOutputStream(dest), format);
            XMLWriter writer = new XMLWriter(stringWriter, format);

            //设置不自动进行转义
            writer.setEscapeText(false);

            // 生成XML文件
            writer.write(document);
            // 打印字符串,即是XML文档  
            System.out.println(stringWriter.toString());  

            //关闭XMLWriter对象
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("document.asXML:"+document.getRootElement().asXML());
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
		sign = CupSec.sm2SignWithSm3(privateKey, "4000370700".getBytes(), document.getRootElement().asXML().getBytes());
		logger.info("sign:" + sign);
		
		sign="8pJkkbE9vCV/PDl/9zH19G63SdUTDFb+pjIJzOGUfT79+jsKeCj306z92Swq8dfh8WJ20O6I/LQHEuaZ1gd7rg==";

		
//		Document dom=DocumentHelper.parseText(xml);
//		  Element root=dom.getRootElement();
//		  String weighTime=root.element("weighTime").getText();
//		  String cardNum=root.element("cardNum").getText();
//		  String cfid=root.element("cfid").getText();
//		  System.out.println(weighTime);
//		  System.out.println(cardNum);
//		  System.out.println(cfid);

		
//		String serkey = "{S:Dt5By64GzjYtknYMtRtIYtsJmC0XpP1io5iqPW3OoTeSKGKjS0L0icC6IxOm+NPFx3MW83pk50Tyu6lPd++H2lCW/TlIulVKViiA40r+bBtWS02dfOQC8pYEz/lxWwQtJtUCZ3DCV0ObAQg7dfesf9oTDdyAT5OX7yJENa5CaDqG2fT2tVtRNqgpnTaPSrKab2LmJt7mIXDj1pE+PftlKGbDnhvv1bAhAUfWYP/ngCQegOl6bvhA0N7MahtpDIi8yamepMf8rALs9NnMqSGEAN3MDl94GbHIKOkCPJkkkV9PlLoyrU45QjSiTZb0ETExxaFqQVAMFet27lo2RyC69w==}";

		String serkey ="{S:"+sign+"}";
//		String xmlsum = stringWriter.toString() + serkey;
		String xmlsum = msg + serkey;
		logger.info("xmlsum:" + xmlsum);
		String urlParam = "http://127.0.0.1:7888/QPay/ReceiveMerchantTrxReqServlet";
		Map<String, String> params = new HashMap<>();
		params.put("sendxml", xmlsum);
		HttpClientHelper.sendPost2(urlParam, params, "UTF-8");
//		System.out.println(xmlStr.length());
//		System.out.println(xmlStr.getBytes().length);
	}
}
