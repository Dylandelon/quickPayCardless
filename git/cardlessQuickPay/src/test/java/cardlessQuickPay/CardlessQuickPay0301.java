package cardlessQuickPay;

import java.io.IOException;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import com.cup.security.certification.RsaCertUtils;
import com.cup.security.cupsec.CupSec;

import cardlessQuickPay.utils.HttpsClientHelper;



public class CardlessQuickPay0301 {
	private static Logger logger = Logger.getLogger(CardlessQuickPay0301.class);
	private static String path = ClassLoader.getSystemResource("").getPath();
	/**
	 * 协议支付解约  0301
	 */
	@Test
	public void testSm2SignWithSm3() {
		// 从证书中获取 RSA 公私钥
		String pfxkeyfile, keypwd, type;
		pfxkeyfile = path + "/jg_4000370693.pfx";
		keypwd = "11111111";
		type = "PKCS12";
		PrivateKey privateKey = RsaCertUtils.getPriKeyPkcs12(pfxkeyfile, keypwd, type);
		String xml1 ="<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		StringBuffer sb = new StringBuffer();
		// 创建Document对象
        Document document = DocumentHelper.createDocument();
        Element root = createPostXML(document);

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
        String sign = null;

		sign = CupSec.rsaSignWithSha256(privateKey, document.getRootElement().asXML().getBytes());
		logger.info("sign:" + sign);
		

		
//		String serkey = "{S:Dt5By64GzjYtknYMtRtIYtsJmC0XpP1io5iqPW3OoTeSKGKjS0L0icC6IxOm+NPFx3MW83pk50Tyu6lPd++H2lCW/TlIulVKViiA40r+bBtWS02dfOQC8pYEz/lxWwQtJtUCZ3DCV0ObAQg7dfesf9oTDdyAT5OX7yJENa5CaDqG2fT2tVtRNqgpnTaPSrKab2LmJt7mIXDj1pE+PftlKGbDnhvv1bAhAUfWYP/ngCQegOl6bvhA0N7MahtpDIi8yamepMf8rALs9NnMqSGEAN3MDl94GbHIKOkCPJkkkV9PlLoyrU45QjSiTZb0ETExxaFqQVAMFet27lo2RyC69w==}";

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{S:");
		stringBuffer.append(sign);
		stringBuffer.append("}");
//		String serkey ="{S:"+sign+"}";
		String xmlsum = stringWriter.toString() + stringBuffer.toString();
		stringWriter.append(stringBuffer);
		logger.info("xmlsum:" + xmlsum);
//		String urlParam = "http://127.0.0.1:7888/QPay/ReceiveMerchantTrxReqServlet";
//		String urlParam = "http://127.0.0.1:7888/";
		String urlParam = "https://127.0.0.1:443/";
//		String urlParam = "http://localhost:7080/cardlessQuickPay/serhttp";
		Map<String, String> params = new HashMap<>();
		params.put("sendxml", stringWriter.toString());
		params.put("MsgTp", "0301");
//		String result = HttpClientHelper.sendPost(urlParam, params, "UTF-8");
		String result = HttpsClientHelper.sendPost(urlParam, params, "UTF-8");
//		System.out.println(xmlStr.length());
//		System.out.println(xmlStr.getBytes().length);
		System.out.println("-------打印返回结果："+result);
		String xmlStr = result.substring(0, result.indexOf("</root>")+"</root>".length());
		System.out.println("-------打印返回结果："+xmlStr);
		String signb = result.substring(result.indexOf("</root>{S:")+"</root>{S:".length(), result.length()-1);
		System.out.println("截取signb："+signb);
		Document dom = null;
		try {
			dom = DocumentHelper.parseText(xmlStr);
			//从证书中获取公钥
			String filePath;
//			filePath = path + "/yl-rsa-签名证书.cer";
			filePath = path + "/4000370686.cer";
			PublicKey publicKey = RsaCertUtils.getPubKey(filePath);
			boolean verify = false;
			
			verify = CupSec.rsaVerifyWithSha256(publicKey, xmlStr.getBytes(), signb);
			logger.info("verify:" + verify);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dom != null){
			// 解析并校验  返回的数据合法性 和校验原来一致的主要字段
						Element rootRes = dom.getRootElement();
						Element MsgHeader = rootRes.element("MsgHeader");
						// 00010000
						String IssrId = MsgHeader.element("IssrId").getText();
						// 12
						String Drctn = MsgHeader.element("Drctn").getText();
						// 
						Element MsgBody = rootRes.element("MsgBody");
						// 100005   
						String BizTp = MsgBody.element("BizTp").getText();
						Element TrxInf = MsgBody.element("TrxInf");
						// 交易流水号
						String TrxId = TrxInf.element("TrxId").getText();
						// 清算日期 
						String SettlmtDt = TrxInf.element("SettlmtDt").getText();
						// 系统相应信息
						Element SysRtnInf = MsgBody.element("SysRtnInf");
						// 系统指令级别的处理编码
						String SysRtnCd = SysRtnInf.element("SysRtnCd").getText();
						// 返回码对应的说明
						String SysRtnDesc = SysRtnInf.element("SysRtnDesc").getText();
						// 系统相应的时间
						String SysRtnTm = SysRtnInf.element("SysRtnTm").getText();
						// 验证要素位图
//						String ValBtMp = SysRtnInf.element("ValBtMp").getText();
						
						// 接收方信息
						Element RcverInf = MsgBody.element("RcverInf");
						// 接收方账户所属机构标识
						String RcverAcctIssrId = RcverInf.element("RcverAcctIssrId").getText();
						// 发起方信息
						Element SderInf = MsgBody.element("SderInf");
						// 发送机构标识
						String SderIssrId = SderInf.element("SderIssrId").getText();
						// 发起方账户所属机构标识
						String SderAcctIssrId = SderInf.element("SderAcctIssrId").getText();
						// 业务相应信息
						Element BizInf = MsgBody.element("BizInf");
						// 签约协议号
						String SgnNo = BizInf.element("SgnNo").getText();
		}
	    
	}
	/**
	 * @param document
	 * @return
	 */
	private Element createPostXML(Document document) {
		// 创建根节点
        Element root = document.addElement("root");
        // 创建报文头
        createHeaderXML(root);
        
        createBodyXML(root);
		return root;
	}
	/**
	 * @param root
	 */
	private void createBodyXML(Element root) {

		// 创建报文体
        Element MsgBody = root.addElement("MsgBody");
        // 业务种类
        Element BizTp = MsgBody.addElement("BizTp");
        BizTp.setText("100005");
        // 交易信息
        Element TrxInf = MsgBody.addElement("TrxInf");
        // 交易流水号
        Element TrxId = TrxInf.addElement("TrxId");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddhhmmss");
        TrxId.setText(LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");
        // 交易金额
//        Element TrxAmt = TrxInf.addElement("TrxAmt");
//        TrxAmt.setText("CNY10.00");
        // 交易日期时间
        Element TrxDtTm = TrxInf.addElement("TrxDtTm");
        TrxDtTm.setText(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        // 清算日期
        Element SettlmtDt = TrxInf.addElement("SettlmtDt");
        SettlmtDt.setText("");
        // 接收方信息
        Element RcverInf = MsgBody.addElement("RcverInf");
        // 接收方账户所属机构标识
        Element RcverAcctIssrId = RcverInf.addElement("RcverAcctIssrId");
        RcverAcctIssrId.setText("111");
        // 持卡人账户
        Element RcverAcctId = RcverInf.addElement("RcverAcctId");
        RcverAcctId.setText("6212143000000000011");
        // 接收方银行账户类型
        Element RcverAcctTp = RcverInf.addElement("RcverAcctTp");
        RcverAcctTp.setText("");
        // 接收方名称
        Element RcverNm = RcverInf.addElement("RcverNm");
        RcverNm.setText("com");
        // 接收方证件类型
        Element IDTp = RcverInf.addElement("IDTp");
        IDTp.setText("01");
        // 接收方证件号
        Element IDNo = RcverInf.addElement("IDNo");
        IDNo.setText("310115197803261111");
        // 签约协议号
        Element SgnNo = RcverInf.addElement("SgnNo");
        SgnNo.setText("UPW0ISS0014936000001049360000W0ISS001201711200000000100");
        // 发起方信息
        Element SderInf = MsgBody.addElement("SderInf");
        // 发起机构标识
        Element SderIssrId = SderInf.addElement("SderIssrId");
        SderIssrId.setText("111");
        // 发起方账户所属机构标识
        Element SderAcctIssrId = SderInf.addElement("SderAcctIssrId");
        SderAcctIssrId.setText("111");
        // 发起方账户所属机构名称
//        Element SderAcctIssrNm = SderInf.addElement("SderAcctIssrNm");
//        SderAcctIssrNm.setText("111");
       
        // 风险监控信息
        Element RskInf= MsgBody.addElement("RskInf");
        // 设备型号
        Element deviceMode= RskInf.addElement("deviceMode");
        deviceMode.setText("111");
        // 设备语言
        Element deviceLanguage= RskInf.addElement("deviceLanguage");
        deviceLanguage.setText("111");
        // IP地址
        Element sourceIP= RskInf.addElement("sourceIP");
        sourceIP.setText("111");
        // MAC地址
        Element MAC= RskInf.addElement("MAC");
        MAC.setText("111");
        // 设备号
        Element devId= RskInf.addElement("devId");
        devId.setText("111");
        // GPS设置
        Element extensiveDeviceLocation= RskInf.addElement("extensiveDeviceLocation");
        extensiveDeviceLocation.setText("111");
        // SIM卡号码
        Element deviceNumber= RskInf.addElement("deviceNumber");
        deviceNumber.setText("111");
        // SIM卡数量
        Element deviceSIMNumber= RskInf.addElement("deviceSIMNumber");
        deviceSIMNumber.setText("111");
        // 账户ID
        Element accountIDHash= RskInf.addElement("accountIDHash");
        accountIDHash.setText("111");
        // 风险评分
        Element riskScore= RskInf.addElement("riskScore");
        riskScore.setText("111");
        // 原因码
        Element riskReasonCode= RskInf.addElement("riskReasonCode");
        riskReasonCode.setText("111");
        // 收单端用户注册日期
        Element mchntUsrRgstrTm= RskInf.addElement("mchntUsrRgstrTm");
        mchntUsrRgstrTm.setText("111");
        // 收单端用户注册邮箱
        Element mchntUsrRgstrEmail= RskInf.addElement("mchntUsrRgstrEmail");
        mchntUsrRgstrEmail.setText("111");
        // 收货省
        Element rcvProvince= RskInf.addElement("rcvProvince");
        rcvProvince.setText("111");
        // 收货市
        Element rcvCity= RskInf.addElement("rcvCity");
        rcvCity.setText("111");
        // 商品类型
        Element goodsClass= RskInf.addElement("goodsClass");
        goodsClass.setText("2");
	
	}
	/**
	 * @param root
	 */
	private void createHeaderXML(Element root) {
		Element MsgHeader = root.addElement("MsgHeader");
        
        // 报文版本
        Element MsgVer = MsgHeader.addElement("MsgVer");
        MsgVer.setText("1000");
        // 交易类型
        Element Trxtyp = MsgHeader.addElement("Trxtyp");
        Trxtyp.setText("0301");
        // 报文方向
        Element Drctn = MsgHeader.addElement("Drctn");
        Drctn.setText("11");
        // 报文发起日期时间
        Element SndDt = MsgHeader.addElement("SndDt");
        SndDt.setText(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        // 交易金额
//        Element TrxAmt = MsgHeader.addElement("TrxAmt");
//        TrxAmt.setText("CNY10.00");
        // 发起方所属机构标识
        Element IssrId = MsgHeader.addElement("IssrId");
        IssrId.setText("49360000");
        // 签名和密钥加密算法类型
        Element SignEncAlgo = MsgHeader.addElement("SignEncAlgo");
        SignEncAlgo.setText("0");
        // 签名证书序列号
        Element SignSN = MsgHeader.addElement("SignSN");
        SignSN.setText("4000370693");
        // 摘要算法类型
        Element MDAlgo = MsgHeader.addElement("MDAlgo");
        MDAlgo.setText("0");
        // 摘要算法类型
        Element EncKey = MsgHeader.addElement("EncKey");
        EncKey.setText("1234567890ABCDEF1234567890ABCDEF");
        // 加密证书序列号
        Element EncSN = MsgHeader.addElement("EncSN");
        EncSN.setText("4000370687");
        // 对称加密算法类型
        Element EncAlgo = MsgHeader.addElement("EncAlgo");
        EncAlgo.setText("0");
	}
}
