package cardlessQuickPay.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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

public class QuickPay3101 {
	private static String path = ClassLoader.getSystemResource("").getPath();
	/**
	 * 交易状态及信息查询-成功-原交易成功或处理中
	 */
	public Map<String,String> pay3101(Map<String,String> map) {
		// 从证书中获取 RSA 公私钥
		String pfxkeyfile, keypwd, type;
		pfxkeyfile = path + "/jg_4000370693.pfx";
		keypwd = "11111111";
		type = "PKCS12";
		PrivateKey privateKey = RsaCertUtils.getPriKeyPkcs12(pfxkeyfile, keypwd, type);
		StringBuffer sb = new StringBuffer();
		// 创建Document对象
        Document document = DocumentHelper.createDocument();
        Element root = createPostXML(document,map);

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
        String sign = null;
		sign = CupSec.rsaSignWithSha256(privateKey, document.getRootElement().asXML().getBytes());
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{S:");
		stringBuffer.append(sign);
		stringBuffer.append("}");
//		String serkey ="{S:"+sign+"}";
		stringWriter.append(stringBuffer);
//		String urlParam = "http://127.0.0.1:7888/QPay/ReceiveMerchantTrxReqServlet";
//		String urlParam = "http://127.0.0.1:7888/";
		String urlParam = map.get("urlParam");
//		String urlParam = "http://localhost:7080/cardlessQuickPay/serhttp";
		Map<String, String> params = new HashMap<>();
		params.put("sendxml", stringWriter.toString());
		params.put("MsgTp", "3101");
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
		Map<String, String> resMap = new HashMap<>();
		try {
			dom = DocumentHelper.parseText(xmlStr);
			//从证书中获取公钥
			String filePath;
//			filePath = path + "/yl-rsa-签名证书.cer";
			filePath = path + "/4000370686.cer";
			PublicKey publicKey = RsaCertUtils.getPubKey(filePath);
			boolean verify = false;
			
			verify = CupSec.rsaVerifyWithSha256(publicKey, xmlStr.getBytes(), signb);
			System.out.println("verify:" + verify);
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
//			String ValBtMp = SysRtnInf.element("ValBtMp").getText();
			// 业务相应信息
			Element BizInf = MsgBody.element("BizInf");
			// 收/付标识
			String RPFlg = BizInf.element("RPFlg").getText();
			// 交易金额
			String TrxAmt = BizInf.element("TrxAmt").getText();
			// 接收方账户号
			String RcverAcctId = BizInf.element("RcverAcctId").getText();
			// 持卡人账户等级 1
			String AcctLvl = BizInf.element("AcctLvl").getText();
			// 柜面合身状态  2
			String ChkStat = BizInf.element("ChkStat").getText();
			// 动态短信关联码
			String Smskey = BizInf.element("Smskey").getText();
			// 签约协议号
			String SgnNo = BizInf.element("SgnNo").getText();
			// 原业务种类
			String OriBizTp = BizInf.element("OriBizTp").getText();
			// 原交易流水号
			String OriTrxId = BizInf.element("OriTrxId").getText();
			// 原交易系统返回码
			String OriSysRtnCd = BizInf.element("OriSysRtnCd").getText();
			// 原交易状态   01
			String TrxStatus = BizInf.element("TrxStatus").getText();
			// 发起方信息
			Element SderInf = MsgBody.element("SderInf");
			// 发送机构标识
			String SderIssrId = SderInf.element("SderIssrId").getText();
			// 发起方账户所属机构标识
			String SderAcctIssrId = SderInf.element("SderAcctIssrId").getText();
			
			resMap.put("SysRtnCd", SysRtnCd);
			resMap.put("SysRtnDesc", SysRtnDesc);
			resMap.put("OriSysRtnCd", OriSysRtnCd);
			resMap.put("TrxStatus", TrxStatus);
			
		}
		return resMap;
	    
	}
	/**
	 * @param document
	 * @return
	 */
	private Element createPostXML(Document document,Map<String,String> map) {
		// 创建根节点
        Element root = document.addElement("root");
        // 创建报文头
        createHeaderXML(root,map);
        
        createBodyXML(root,map);
		return root;
	}
	/**
	 * @param root
	 */
	private void createBodyXML(Element root,Map<String,String> map) {
		// 创建报文体
        Element MsgBody = root.addElement("MsgBody");
        // 业务种类
        Element BizTp = MsgBody.addElement("BizTp");
        BizTp.setText(map.get("BizTp"));
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
        // 收/付标识
        Element RPFlg = TrxInf.addElement("RPFlg");
        RPFlg.setText("");
        // 原交易信息
        Element OriTrxInf = MsgBody.addElement("OriTrxInf");
        // 原交易类型  肯定不是3101   -- 0001短信  0301解约0201签约  1101退货  0003直接支付触发短信  1002 直接支付  2001贷记支付
        Element OriTrxTp = OriTrxInf.addElement("OriTrxTp");
        OriTrxTp.setText(map.get("OriTrxTp"));
        // 原业务种类
        Element OriBizTp = OriTrxInf.addElement("OriBizTp");
        OriBizTp.setText(map.get("OriBizTp"));
        // 原交易日期时间
        Element OriTrxDtTm = OriTrxInf.addElement("OriTrxDtTm");
        OriTrxDtTm.setText(map.get("OriTrxDtTm"));
        // 原交易流水号
        Element OriTrxId = OriTrxInf.addElement("OriTrxId");
        OriTrxId.setText(map.get("OriTrxId"));
        // 发起方信息
        Element SderInf = MsgBody.addElement("SderInf");
        // 发起机构标识
        Element SderIssrId = SderInf.addElement("SderIssrId");
        SderIssrId.setText(map.get("SderIssrId"));
        // 发起方账户所属机构标识
        Element SderAcctIssrId = SderInf.addElement("SderAcctIssrId");
        SderAcctIssrId.setText(map.get("SderAcctIssrId"));
//        // 支付账号信息
//        Element SderAcctIssrNm = SderInf.addElement("SderAcctIssrNm");
//        SderAcctIssrNm.setText("111");
//        // 发起方账户所属机构名称
//        Element SderAcctInf = SderInf.addElement("SderAcctInf");
//        SderAcctInf.setText("111");
        // 清算日期
//        Element SettlmtDt = TrxInf.addElement("SettlmtDt");
//        SettlmtDt.setText("");
        // 接收方信息
//        Element RcverInf = MsgBody.addElement("RcverInf");
//        // 接收方账户所属机构标识
//        Element RcverAcctIssrId = RcverInf.addElement("RcverAcctIssrId");
//        RcverAcctIssrId.setText("111");
//        // 持卡人账户
//        Element RcverAcctId = RcverInf.addElement("RcverAcctId");
//        RcverAcctId.setText("6212143000000000011");
//        // 接收方名称
//        Element RcverNm = RcverInf.addElement("RcverNm");
//        RcverNm.setText("银联一");
//        // 接收方证件类型
//        Element IDTp = RcverInf.addElement("IDTp");
//        IDTp.setText("01");
//        // 接收方名称
//        Element IDNo = RcverInf.addElement("IDNo");
//        IDNo.setText("310115198903261113");
//        // 接收方预留手机号
//        Element MobNo = RcverInf.addElement("MobNo");
//        MobNo.setText("13111111111");
//        // 动态验证码
//        Element AuthMsg = RcverInf.addElement("AuthMsg");
//        AuthMsg.setText("123456");
//        // 动态短信关联码
//        Element Smskey = RcverInf.addElement("Smskey");
//        Smskey.setText("20171115111844000019");
//        // 敏感信息
//        Element SensInf = MsgBody.addElement("SensInf");
//        
//        
//        // 单位结算卡信息
//        Element CorpCard = MsgBody.addElement("CorpCard");
//        // 单位结算卡完整账户名称
//        Element CorpName = CorpCard.addElement("CorpName");
//        CorpName.setText("111");
//        // 营业执照注册号或者统一社会信用代码
//        Element USCCode = CorpCard.addElement("USCCode");
//        USCCode.setText("111");
//        // 产品信息
//        Element ProductInf = MsgBody.addElement("ProductInf");
//        // 产品类型
//        Element ProductTp = ProductInf.addElement("ProductTp");
//        ProductTp.setText("00000000");
//        // 产品辅助信息
//        Element ProductAssInformation= ProductInf.addElement("ProductAssInformation");
//        ProductAssInformation.setText("00000000");
//        // 商户信息
//        Element MrchntInf= MsgBody.addElement("MrchntInf");
//        // 商户编码
//        Element MrchntNo= MrchntInf.addElement("MrchntNo");
//        MrchntNo.setText("123456789123456");
//        // 商户类别
//        Element MrchntTpId= MrchntInf.addElement("MrchntTpId");
//        MrchntTpId.setText("1234");
//        // 商户名称
//        Element MrchntPltfrmNm= MrchntInf.addElement("MrchntPltfrmNm");
//        MrchntPltfrmNm.setText("特约商户的名字");
//        // 二级商户信息
//        Element SubMrchntInf= MsgBody.addElement("SubMrchntInf");
//        // 二级商户编码
//        Element SubMrchntNo= SubMrchntInf.addElement("SubMrchntNo");
//        SubMrchntNo.setText("111");
//        // 二级商户类别
//        Element SubMrchntTpId= SubMrchntInf.addElement("SubMrchntTpId");
//        SubMrchntTpId.setText("1234");
//        // 二级商户名称
//        Element SubMrchntPltfrmNm= SubMrchntInf.addElement("SubMrchntPltfrmNm");
//        SubMrchntPltfrmNm.setText("111");
        // 风险监控信息
        Element RskInf= MsgBody.addElement("RskInf");
        // 设备型号
        Element deviceMode= RskInf.addElement("deviceMode");
        deviceMode.setText("");
        // 设备语言
        Element deviceLanguage= RskInf.addElement("deviceLanguage");
        deviceLanguage.setText("");
        // IP地址
        Element sourceIP= RskInf.addElement("sourceIP");
        sourceIP.setText("");
        // MAC地址
        Element MAC= RskInf.addElement("MAC");
        MAC.setText("");
        // 设备号
        Element devId= RskInf.addElement("devId");
        devId.setText("");
        // GPS设置
        Element extensiveDeviceLocation= RskInf.addElement("extensiveDeviceLocation");
        extensiveDeviceLocation.setText("");
        // SIM卡号码
        Element deviceNumber= RskInf.addElement("deviceNumber");
        deviceNumber.setText("");
        // SIM卡数量
        Element deviceSIMNumber= RskInf.addElement("deviceSIMNumber");
        deviceSIMNumber.setText("");
        // 账户ID
        Element accountIDHash= RskInf.addElement("accountIDHash");
        accountIDHash.setText("");
        // 风险评分
        Element riskScore= RskInf.addElement("riskScore");
        riskScore.setText("");
        // 原因码
        Element riskReasonCode= RskInf.addElement("riskReasonCode");
        riskReasonCode.setText("");
        // 收单端用户注册日期
        Element mchntUsrRgstrTm= RskInf.addElement("mchntUsrRgstrTm");
        mchntUsrRgstrTm.setText("");
        // 收单端用户注册邮箱
        Element mchntUsrRgstrEmail= RskInf.addElement("mchntUsrRgstrEmail");
        mchntUsrRgstrEmail.setText("");
        // 收货省
        Element rcvProvince= RskInf.addElement("rcvProvince");
        rcvProvince.setText("");
        // 收货市
        Element rcvCity= RskInf.addElement("rcvCity");
        rcvCity.setText("");
        // 商品类型
        Element goodsClass= RskInf.addElement("goodsClass");
        goodsClass.setText("");
	}
	/**
	 * @param root
	 */
	private void createHeaderXML(Element root,Map<String,String> map) {
		Element MsgHeader = root.addElement("MsgHeader");
        
        // 报文版本
        Element MsgVer = MsgHeader.addElement("MsgVer");
        MsgVer.setText("1000");
        // 交易类型
        Element Trxtyp = MsgHeader.addElement("Trxtyp");
        Trxtyp.setText("3101");
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
        IssrId.setText(map.get("IssrId"));
        // 签名和密钥加密算法类型
        Element SignEncAlgo = MsgHeader.addElement("SignEncAlgo");
        SignEncAlgo.setText("0");
        // 签名证书序列号
        Element SignSN = MsgHeader.addElement("SignSN");
        SignSN.setText(map.get("SignSN"));
        // 摘要算法类型
        Element MDAlgo = MsgHeader.addElement("MDAlgo");
        MDAlgo.setText("0");
        // 摘要算法类型
        Element EncKey = MsgHeader.addElement("EncKey");
        EncKey.setText(map.get("encKey"));
        // 加密证书序列号
        Element EncSN = MsgHeader.addElement("EncSN");
        EncSN.setText(map.get("EncSN"));
        // 对称加密算法类型
        Element EncAlgo = MsgHeader.addElement("EncAlgo");
        EncAlgo.setText("0");
	}
}
