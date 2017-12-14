package cardlessQuickPay.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.cup.security.certification.RsaCertUtils;
import com.cup.security.cupsec.CupSec;

@Controller
public class ReceiveController {
	private static Logger logger = Logger.getLogger(ReceiveController.class);
	@Resource
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	// HttpRequest httpRequest
	@RequestMapping("/ser")
	public void receiveServer( ){
	System.out.println(requestMappingHandlerMapping.toString());
	System.out.println("-------------------------");
	}
	@RequestMapping("/serhttp")
	public void receiveServer(HttpServletRequest req, HttpServletResponse response, Writer out ) throws IOException{
		
		//获取请求方式
        System.out.println(req.getMethod());
        //获取项目名称
        System.out.println(req.getContextPath());
        //获取完整请求路径
        System.out.println(req.getRequestURL());
        //获取除了域名外的请求数据
        System.out.println(req.getRequestURI());
        //获取请求参数
        System.out.println(req.getQueryString());
        System.out.println("----------------------------------------------------------");
        
        //获取请求头
        String header = req.getHeader("user-Agent");
        System.out.println(header);
        header = header.toLowerCase();
        //根据请求头数据判断浏览器类型
        if(header.contains("chrome")){
            System.out.println("您的访问浏览器为谷歌浏览器");
        }else if(header.contains("msie")){
            System.out.println("您的访问浏览器为IE浏览器");
        }else if(header.contains("firefox")){
            System.out.println("您的访问浏览器为火狐浏览器");
        }else{
            System.out.println("您的访问浏览器为其它浏览器");
        }
        System.out.println("----------------------------------------------------------");
        //获取所有的消息头名称
        Enumeration<String> headerNames = req.getHeaderNames();
        //获取获取的消息头名称，获取对应的值，并输出
        while(headerNames.hasMoreElements()){
            String nextElement = headerNames.nextElement();
            System.out.println(nextElement+":"+req.getHeader(nextElement));
        }
        System.out.println("----------------------------------------------------------");
        //根据名称获取此重名的所有数据
        Enumeration<String> headers = req.getHeaders("accept");
        while (headers.hasMoreElements()) {
            String string = (String) headers.nextElement();
            System.out.println(string);
        }
        System.out.println("------------------");
        String res = charReader(req);
        System.out.println("收到请求报文："+res);
        System.out.println("------------------");
		try {
			out.write("访问成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 银联通知   协议支付解约通知 0303
	 * @param req
	 * @param response
	 * @param out
	 * @throws IOException
	 */
	@RequestMapping("/unionPayhttp")
	public void unionPayhttpServer(HttpServletRequest req, HttpServletResponse response) throws IOException{
		
		//获取请求方式
        System.out.println(req.getMethod());
        //获取项目名称
        System.out.println(req.getContextPath());
        //获取完整请求路径
        System.out.println(req.getRequestURL());
        //获取除了域名外的请求数据
        System.out.println(req.getRequestURI());
        //获取请求参数
        System.out.println(req.getQueryString());
        System.out.println("----------------------------------------------------------");
        
        //获取请求头
        /*String header = req.getHeader("user-Agent");
        System.out.println(header);
        header = header.toLowerCase();
        //根据请求头数据判断浏览器类型
        if(header.contains("chrome")){
            System.out.println("您的访问浏览器为谷歌浏览器");
        }else if(header.contains("msie")){
            System.out.println("您的访问浏览器为IE浏览器");
        }else if(header.contains("firefox")){
            System.out.println("您的访问浏览器为火狐浏览器");
        }else{
            System.out.println("您的访问浏览器为其它浏览器");
        }*/
        System.out.println("----------------------------------------------------------");
        Map map = new HashMap<String,String>();
        map.put("OriIssrId", req.getHeader("OriIssrId"));
		map.put("SderReserved", req.getHeader("SderReserved"));
		map.put("RcverReserved", req.getHeader("RcverReserved"));
		map.put("CupsReserved", req.getHeader("CupsReserved"));
        //获取所有的消息头名称
        Enumeration<String> headerNames = req.getHeaderNames();
        //获取获取的消息头名称，获取对应的值，并输出
        while(headerNames.hasMoreElements()){
            String nextElement = headerNames.nextElement();
            System.out.println(nextElement+":"+req.getHeader(nextElement));
        }
        System.out.println("----------------------------------------------------------");
        //根据名称获取此重名的所有数据
        Enumeration<String> headers = req.getHeaders("accept");
        while (headers.hasMoreElements()) {
            String string = (String) headers.nextElement();
            System.out.println(string);
        }
        System.out.println("------------------");
        String result = charReader(req);
        System.out.println("------------------");
        System.out.println("-------打印返回结果："+result);
		String xmlStr = result.substring(0, result.indexOf("</root>")+"</root>".length());
		System.out.println("-------打印返回结果："+xmlStr);
		String signb = result.substring(result.indexOf("</root>{S:")+"</root>{S:".length(), result.length()-1);
		System.out.println("截取signb："+signb);
		Document dom = null;
		try {
			dom = DocumentHelper.parseText(xmlStr);
			//从证书中获取公钥
			String filePath = null;
			
			String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			path = URLDecoder.decode(path, "utf-8");
			
//			filePath = path + "/yl-rsa-签名证书.cer";
			filePath = path + "4000370686.cer";
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
			String IssrId = "";
			if(MsgHeader.element("IssrId").hasContent()){
				IssrId =  MsgHeader.element("IssrId").getText();
			}
			// 12
			String Drctn = "";
			if(MsgHeader.element("Drctn").hasContent()){
				Drctn =  MsgHeader.element("Drctn").getText();
			}
			// 
			Element MsgBody = rootRes.element("MsgBody");
			// 100005   
			String BizTp = MsgBody.element("BizTp").getText();
			Element TrxInf = MsgBody.element("TrxInf");
			// 交易流水号
			String TrxId = TrxInf.element("TrxId").getText();
			// 清算日期 
			String SettlmtDt = TrxInf.element("SettlmtDt").getText();
			// 接收方信息
			Element RcverInf = MsgBody.element("RcverInf");
			// 接收方账户所属机构标识
			String RcverAcctIssrId = RcverInf.element("RcverAcctIssrId").getText();
			// 签约协议号
			String SgnNo = RcverInf.element("SgnNo").getText();
			
			// 发起方信息
			Element SderInf = MsgBody.element("SderInf");
			// 发起机构标识
			String SderIssrId = SderInf.element("SderIssrId").getText();
			// 发起方账户所属机构标识
			String SderAcctIssrId = SderInf.element("SderAcctIssrId").getText();
			// 发起方账户
			String SderAcctId = SderInf.element("SderAcctId").getText();
			// 发起方账户类型
			String SderAcctTp = SderInf.element("SderAcctTp").getText();
			
			map.put("BizTp", BizTp);
			map.put("TrxId", TrxId);
			map.put("SettlmtDt", SettlmtDt);
			map.put("RcverAcctIssrId", RcverAcctIssrId);
			map.put("SderAcctIssrId", SderAcctIssrId);
			map.put("SderIssrId", SderIssrId);
			map.put("SgnNo", SgnNo);
			
		}
		PrintWriter out = null;
		try {
			String postXML = postMessByRSA(map);
			response.setContentType("application/xml; charset=utf-8");
			response.setCharacterEncoding("utf-8");
			response.setContentLength(postXML.getBytes().length);
			response.setHeader("MsgTp", "0303");
			response.setHeader("OriIssrId", map.get("OriIssrId").toString());
			response.setHeader("SderReserved", map.get("SderReserved") == null?"":map.get("SderReserved").toString());
			response.setHeader("RcverReserved",map.get("RcverReserved") == null?"":map.get("RcverReserved").toString());
			response.setHeader("CupsReserved", map.get("CupsReserved") == null?"":map.get("CupsReserved").toString());
			response.setHeader("Content-Encoding", "");
			out = response.getWriter();
			out.print(postXML);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally {
			if(out != null){
				out.close();
			}
		}
	}
	public String postMessByRSA(Map<String,String> map) throws UnsupportedEncodingException {
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		path = URLDecoder.decode(path, "utf-8");
		// 从证书中获取 RSA 公私钥
		String pfxkeyfile, keypwd, type;
		pfxkeyfile = path + "jg_4000370693.pfx";
		keypwd = "11111111";
		type = "PKCS12";
		PrivateKey privateKey = RsaCertUtils.getPriKeyPkcs12(pfxkeyfile, keypwd, type);
		System.out.println("privateKey:"+privateKey);
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

        logger.info("document.asXML:"+document.getRootElement().asXML());
        String sign = null;

		sign = CupSec.rsaSignWithSha256(privateKey, document.getRootElement().asXML().getBytes());
		logger.info("sign:" + sign);

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{S:");
		stringBuffer.append(sign);
		stringBuffer.append("}");
		stringWriter.append(stringBuffer);
		return stringWriter.toString();
	}
    //	HTTP请求中的是字符串数据：
	//字符串读取
	private String charReader(HttpServletRequest request) throws IOException {

    BufferedReader	br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
//	BufferedReader br = request.getReader();

    StringBuffer stringBuffer = new StringBuffer();
	String str;
	while((str = br.readLine()) != null){
		stringBuffer.append(str);
	}

	return stringBuffer.toString();
	}

	//二进制读取

	void binaryReader(HttpServletRequest request) throws IOException {

	int len = request.getContentLength();
	ServletInputStream iii = request.getInputStream();
	byte[] buffer = new byte[len];
	iii.read(buffer, 0, len);

	}

	private Element createPostXML(Document document,Map<String,String> map) {
		// 创建根节点
        Element root = document.addElement("root");
        createHeaderXML(root);
        
        createBodyXML(root, map);
		return root;
	}
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
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddhhmmss");
        TrxId.setText(map.get("TrxId"));
        // 交易金额
//        Element TrxAmt = TrxInf.addElement("TrxAmt");
//        TrxAmt.setText("CNY10.00");
        // 交易日期时间
//        Element TrxDtTm = TrxInf.addElement("TrxDtTm");
//        TrxDtTm.setText(map.get("SettlmtDt"));
        // 清算日期
        Element SettlmtDt = TrxInf.addElement("SettlmtDt");
        SettlmtDt.setText(map.get("SettlmtDt"));
        // 系统响应信息
        Element SysRtnInf = MsgBody.addElement("SysRtnInf");
        // 系统返回码
        Element SysRtnCd = SysRtnInf.addElement("SysRtnCd");
        SysRtnCd.setText("00000000");
        // 系统返回说明
        Element SysRtnDesc = SysRtnInf.addElement("SysRtnDesc");
        SysRtnDesc.setText("交易成功");
        // 系统返回时间
        Element SysRtnTm = SysRtnInf.addElement("SysRtnTm");
        SysRtnTm.setText(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        // 接收方信息
        Element RcverInf = MsgBody.addElement("RcverInf");
        // 接收方账户所属机构标识
        Element RcverAcctIssrId = RcverInf.addElement("RcverAcctIssrId");
        RcverAcctIssrId.setText(map.get("RcverAcctIssrId"));
        // 持卡人账户
//        Element RcverAcctId = RcverInf.addElement("RcverAcctId");
//        RcverAcctId.setText("6212143000000000011");
//        // 接收方名称
//        Element RcverNm = RcverInf.addElement("RcverNm");
//        RcverNm.setText("银联一");
//        // 接收方名称
//        Element IDTp = RcverInf.addElement("IDTp");
//        IDTp.setText("01");
//        // 接收方证件号
//        Element IDNo = RcverInf.addElement("IDNo");
//        IDNo.setText("310115198903261113");
//        // 接收方预留手机号
//        Element MobNo = RcverInf.addElement("MobNo");
//        MobNo.setText("13111111111");
        // 敏感信息
//        Element SensInf = MsgBody.addElement("SensInf");
        // 发起方信息
        Element SderInf = MsgBody.addElement("SderInf");
        // 发起机构标识
        Element SderIssrId = SderInf.addElement("SderIssrId");
        SderIssrId.setText(map.get("SderIssrId"));
        // 发起方账户所属机构标识
        Element SderAcctIssrId = SderInf.addElement("SderAcctIssrId");
        SderAcctIssrId.setText(map.get("SderAcctIssrId"));
        // 业务响应信息
        Element BizInf = MsgBody.addElement("BizInf");
        Element SgnNo = BizInf.addElement("SgnNo");
        SgnNo.setText(map.get("SgnNo"));
//        // 发起方账户所属机构名称
//        Element SderAcctIssrNm = SderInf.addElement("SderAcctIssrNm");
//        SderAcctIssrNm.setText("111");
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
//        // 风险监控信息
//        Element RskInf= MsgBody.addElement("RskInf");
//        // 设备型号
//        Element deviceMode= RskInf.addElement("deviceMode");
//        deviceMode.setText("111");
//        // 设备语言
//        Element deviceLanguage= RskInf.addElement("deviceLanguage");
//        deviceLanguage.setText("111");
//        // IP地址
//        Element sourceIP= RskInf.addElement("sourceIP");
//        sourceIP.setText("111");
//        // MAC地址
//        Element MAC= RskInf.addElement("MAC");
//        MAC.setText("111");
//        // 设备号
//        Element devId= RskInf.addElement("devId");
//        devId.setText("111");
//        // GPS设置
//        Element extensiveDeviceLocation= RskInf.addElement("extensiveDeviceLocation");
//        extensiveDeviceLocation.setText("111");
//        // SIM卡号码
//        Element deviceNumber= RskInf.addElement("deviceNumber");
//        deviceNumber.setText("111");
//        // SIM卡数量
//        Element deviceSIMNumber= RskInf.addElement("deviceSIMNumber");
//        deviceSIMNumber.setText("111");
//        // 账户ID
//        Element accountIDHash= RskInf.addElement("accountIDHash");
//        accountIDHash.setText("111");
//        // 风险评分
//        Element riskScore= RskInf.addElement("riskScore");
//        riskScore.setText("111");
//        // 原因码
//        Element riskReasonCode= RskInf.addElement("riskReasonCode");
//        riskReasonCode.setText("111");
//        // 收单端用户注册日期
//        Element mchntUsrRgstrTm= RskInf.addElement("mchntUsrRgstrTm");
//        mchntUsrRgstrTm.setText("111");
//        // 收单端用户注册邮箱
//        Element mchntUsrRgstrEmail= RskInf.addElement("mchntUsrRgstrEmail");
//        mchntUsrRgstrEmail.setText("111");
//        // 收货省
//        Element rcvProvince= RskInf.addElement("rcvProvince");
//        rcvProvince.setText("111");
//        // 收货市
//        Element rcvCity= RskInf.addElement("rcvCity");
//        rcvCity.setText("111");
//        // 商品类型
//        Element goodsClass= RskInf.addElement("goodsClass");
//        goodsClass.setText("2");
	}
	private void createHeaderXML(Element root) {
		// 创建报文头
        Element MsgHeader = root.addElement("MsgHeader");
        
        // 报文版本
        Element MsgVer = MsgHeader.addElement("MsgVer");
        MsgVer.setText("1000");
        // 交易类型
        Element Trxtyp = MsgHeader.addElement("Trxtyp");
        Trxtyp.setText("0303");
        // 报文方向  必须是12
        Element Drctn = MsgHeader.addElement("Drctn");
        Drctn.setText("12");
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
