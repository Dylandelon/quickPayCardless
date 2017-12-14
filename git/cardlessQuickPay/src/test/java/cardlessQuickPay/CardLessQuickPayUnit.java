package cardlessQuickPay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cardlessQuickPay.Service.QuickPay0001;
import cardlessQuickPay.Service.QuickPay0201;
import cardlessQuickPay.Service.QuickPay0301;
import cardlessQuickPay.Service.QuickPay1001;
import cardlessQuickPay.Service.QuickPay1101;
import cardlessQuickPay.Service.QuickPay3101;
import configEnum.CardlessQuickPayEnum;

public class CardLessQuickPayUnit {

	/**
	 * 协议支付流程
	 */
	@Test
	public void testQuickPay0001() {
		Map<String, String> configMap = new HashMap<>();
		configMap.put("urlParam", "https://127.0.0.2:443/");
		// 0001 发短信
		QuickPay0001 quickPay0001 = new QuickPay0001();
		Map<String,String> map = new HashMap<>();
		map.put("urlParam", configMap.get("urlParam"));
		map.put("ExpDt", "");
		map.put("CvnNo", "");
		map.put("encKey", "123456782234567832345678");
		// header
//		map.put("MsgVer", "1000");//报文版本
//		map.put("Trxtyp", "0001");//交易类型
		map.put("IssrId", "49360000");// 发起方所属机构标识
		map.put("SignSN", "4000370693");//签名证书序列号
		map.put("EncSN", "4000370671");//加密证书序列号
		// body
		map.put("BizTp", "100005");//业务种类
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddhhmmss");
		map.put("TrxId", LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");//交易流水号
		map.put("TrxAmt", "CNY100.00");//交易金额
		map.put("TrxDtTm", LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));//交易日期时间
		// 接收方信息
		map.put("RcverAcctIssrId", "");//接收方账户所属机构标识
		map.put("RcverAcctId", "6224243900050025");//持卡人账户
		map.put("RcverNm", "华夏二");//接收方名称
		map.put("IDTp", "01");//接收方证件类型
		map.put("IDNo", "310115198903261113");//接收方证件号
		map.put("MobNo", "18444444444");//接收方预留手机号
		// 发起方信息
		map.put("SderIssrId", "49360000");//发起机构标识
		map.put("SderAcctIssrId", "110");//发起方账户所属机构标识
		map.put("SderAcctIssrNm", "");//发起方账户所属机构名称
		// 单位结算卡信息
		map.put("CorpName", "");//单位结算卡完整账户名称
		map.put("USCCode", "");//营业执照注册号或者统一社会信用代码
		// 产品信息
		map.put("ProductTp", "");//产品类型   00000000
		map.put("ProductAssInformation", "");//产品辅助信息   00000000
		// 商户信息
		map.put("MrchntNo", "");//商户编码   123456789123456
		map.put("MrchntTpId", "");//商户类别
		map.put("MrchntPltfrmNm", "");//商户类别
		Map<String,String> resMap = quickPay0001.pay0001(map);
		if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap.get("SysRtnCd"))){
			System.err.println("0001交易成功"+resMap.get("SysRtnDesc"));
		}else{
			System.err.println("0001交易异常"+resMap.get("SysRtnDesc"));
			mySleep();
			Map<String,String> queryMap = queryResult(map,resMap);
			if("0".equals(queryMap.get("rescode"))){
				
			}else{
				return;
			}
			
//			return;
		}
		mySleep();
		// 0201 签约
		Map<String,String> map0201 = new HashMap<>();
		map0201.put("urlParam", configMap.get("urlParam"));
		map0201.put("OriTrxId", resMap.get("TrxId"));
		map0201.put("Smskey", resMap.get("Smskey"));
		map0201.put("AuthMsg", "123456");
		map0201.put("RcverAcctId", resMap.get("RcverAcctId"));
		
		
		map0201.put("ExpDt", "2209");
		map0201.put("CvnNo", "810");
		map0201.put("encKey", "123456782234567832345678");
		// header
//		map.put("MsgVer", "1000");//报文版本
//		map.put("Trxtyp", "0001");//交易类型
		// header
		map0201.put("IssrId", "49360000");// 发起方所属机构标识
		map0201.put("SignSN", "4000370693");//签名证书序列号
		map0201.put("EncSN", "4000370671");//加密证书序列号
		map0201.put("BizTp", "100005");//业务种类
		// body
		map0201.put("TrxId", LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");//交易流水号
		map0201.put("TrxDtTm", LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));//交易日期
		// 接收方信息
		map0201.put("RcverAcctIssrId", "110");//接收方账户所属机构标识
		map0201.put("RcverNm", "华夏二");//接收方名称
		map0201.put("IDTp", "01");//接收方证件类型
		map0201.put("IDNo", "310115198903261113");//接收方证件号
		map0201.put("MobNo", "18444444444");//接收方预留手机号
		// 发起方信息
		map0201.put("SderIssrId", "49360000");//发起机构标识
		map0201.put("SderAcctIssrId", "110");//发起方账户所属机构标识
		// 可以重复签约的，因为目前签约协议号是由发起方账户所属机构标识、卡号和支付账号信息三者决定的，所以如果收单机构发送的报文里面，如果两笔报文中这三者都是相同的，就会被视为重复签约
		map0201.put("SderAcctInf", "");//支付账号信息
		// 单位结算卡信息  接收方为单位结算卡时必上送
		map0201.put("CorpName", "");//单位结算卡完整账户名称
		map0201.put("USCCode", "");//营业执照注册号或者统一社会信用代码
		// 产品信息
		map0201.put("ProductTp", "");//产品类型   00000000
		map0201.put("ProductAssInformation", "");//产品辅助信息   00000000
		QuickPay0201 quickPay0201 = new QuickPay0201();
		Map<String,String> resMap0201 =quickPay0201.pay0201(map0201);
		boolean signFlag = false;
		if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap0201.get("SysRtnCd"))){
			System.err.println("0201交易成功"+resMap0201.get("SysRtnDesc"));
			signFlag = true;
		}else{
			System.err.println("0201交易异常"+resMap0201.get("SysRtnDesc"));
			mySleep();
			Map<String,String> queryMap = queryResult(map0201,resMap0201);
			if ("0".equals(queryMap.get("rescode"))) {
				signFlag = true;
			} else {
				return;
			}
//			return;
		}
		if(signFlag){
			mySleep();
			Map<String,String> map0301 = new HashMap<>();
			map0301.put("urlParam", configMap.get("urlParam"));
			map0301.put("SgnNo", resMap0201.get("SgnNo"));
			
			map0301.put("ExpDt", "2209");
			map0301.put("CvnNo", "810");
			map0301.put("encKey", "123456782234567832345678");
			// header
//			map.put("MsgVer", "1000");//报文版本
//			map.put("Trxtyp", "0001");//交易类型
			// header
			map0301.put("IssrId", "49360000");// 发起方所属机构标识
			map0301.put("SignSN", "4000370693");//签名证书序列号
			map0301.put("EncSN", "4000370671");//加密证书序列号
			map0301.put("BizTp", "100005");//业务种类
			// body
			map0301.put("TrxId", LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");//交易流水号
			map0301.put("TrxDtTm", LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));//交易日期
			// 接收方信息
			map0301.put("RcverAcctIssrId", "");//接收方账户所属机构标识
			map0301.put("RcverAcctId", "6212143000000000011");//持卡人账户
			map0301.put("RcverAcctTp", "");//接收方银行账户类型
			map0301.put("RcverNm", "");//接收方名称
			map0301.put("IDTp", "");//接收方证件类型
			map0301.put("IDNo", "");//接收方证件号
			// 发起方信息
			map0301.put("SderIssrId", "49360000");//发起机构标识
			map0301.put("SderAcctIssrId", "110");//发起方账户所属机构标识
		    QuickPay0301 quickPay0301 = new QuickPay0301();
		    Map<String, String> resMap0301 = quickPay0301.pay0301(map0301);
			if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap0301.get("SysRtnCd"))){
				System.err.println("0301交易成功"+resMap0301.get("SysRtnDesc"));
			}else{
				System.err.println("0301交易异常"+resMap0301.get("SysRtnDesc"));
				mySleep();
				Map<String,String> queryMap = queryResult(map0301,resMap0301);
				if ("0".equals(queryMap.get("rescode"))) {
				} else {
					return;
				}
				return;
			}
			
		}
		mySleep();
		// 1001 协议支付
		Map<String,String> map1001 = new HashMap<>();
		map1001.put("urlParam", configMap.get("urlParam"));
		map1001.put("SgnNo", resMap0201.get("SgnNo"));
		
		map1001.put("IssrId", "49360000");// 发起方所属机构标识
		map1001.put("SignSN", "4000370693");//签名证书序列号
		map1001.put("EncSN", "4000370671");//加密证书序列号
		// header
		map1001.put("ExpDt", "");
		map1001.put("CvnNo", "");
		map1001.put("encKey", "123456782234567832345678");
		// body
		map1001.put("BizTp", "100005");//业务种类
		map1001.put("TrxId", LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");//交易流水号
		map1001.put("TrxAmt", "CNY100.00");//交易金额
		map1001.put("TrxDtTm", LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));//交易日期时间
		map1001.put("AcctInTp", "01");//账户输入方式
		map1001.put("TrxTrmTp", "07");//交易终端类型
		map1001.put("TrxTrmNo", "00000010");//交易终端编码
		map1001.put("PyerAcctId", "111111111111");// 付款方账户
		map1001.put("PyerAcctTp", "");
		map1001.put("PyerNm", "");
		map1001.put("IDTp", "");
		map1001.put("IDNo", "");
		map1001.put("MobNo", "");
		map1001.put("PyeeIssrId", "49360000");
		map1001.put("PyeeAcctIssrId", "110");
		// 备付金信息  非金机构必选传递
		map1001.put("ResfdAcctIssrId", "01010102000");// 备付金银行机构标识
		map1001.put("InstgAcctId", "621234000011000223");// 备付金银行账户
		map1001.put("InstgAcctNm", "bank123456");// 备付金银行账户名称
		// 渠道方信息
		map1001.put("ChannelIssrId", "");// 渠道方机构标识   空默认发起方机构标识
		// 产品信息
		map1001.put("ProductTp", "");//产品类型   00000000
		map1001.put("ProductAssInformation", "");//产品辅助信息   00000000
		// 订单信息  系统内
		map1001.put("OrdrId", "493600004128655291000010"+LocalDateTime.now().withNano(0).format(dateTimeFormatter));//订单号  支付系统内的订单号
		map1001.put("OrdrDesc", "支付系统内的订单号");
		// 商户信息   特约商户
		map1001.put("MrchntNo", "123456789123456");//商户编码   123456789123456
		map1001.put("MrchntTpId", "1234");//商户类别
		map1001.put("MrchntPltfrmNm", "商户名称啊");//商户名称
		QuickPay1001 quickPay1001 = new QuickPay1001();
		Map<String, String> resMap1001 = quickPay1001.pay1001(map1001);
		boolean successFlaf = false;
		if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap1001.get("SysRtnCd"))){
			System.err.println("1001交易成功"+resMap1001.get("SysRtnDesc"));
			successFlaf = true;
		}else{
			System.err.println("1001交易异常"+resMap1001.get("SysRtnDesc"));
			mySleep();
			Map<String,String> queryMap = queryResult(map1001,resMap1001);
			if ("0".equals(queryMap.get("rescode"))) {
				successFlaf = true;
			} else {
				return;
			}
			return;
		}
		if(successFlaf){
			mySleep();
			// 1101 退货
			Map<String,String> map1101= new HashMap<>();
			map1101.put("urlParam", configMap.get("urlParam"));
			map1101.put("SgnNo", map1001.get("SgnNo"));
			map1101.put("PyeeAcctId", "6224243900020028");//收款方账户  与签约协议号至少上送一个 ---
			
			map1101.put("IssrId", "49360000");// 发起方所属机构标识
			map1101.put("SignSN", "4000370693");//签名证书序列号
			map1101.put("EncSN", "4000370671");//加密证书序列号
			// header
			map1101.put("ExpDt", "");
			map1101.put("CvnNo", "");
			map1101.put("encKey", "123456782234567832345678");
			// body
			map1101.put("BizTp", "100005");//业务种类
			map1101.put("TrxId", LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");//交易流水号
			map1101.put("TrxAmt", "CNY30.00");//交易金额
			map1101.put("TrxDtTm", LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));//交易日期时间
			map1101.put("AcctInTp", "01");//账户输入方式
			map1101.put("TrxTrmTp", "07");//交易终端类型
			map1101.put("TrxTrmNo", "00000010");//交易终端编码
			// 收款方/接受方信息
			map1101.put("PyeeAcctIssrId", "49360000");// 收款方账户所属机构标识
			
			// 付款方/发起方信息
			map1101.put("PyeeIssrId", "49360000");// 发送机构标识
			map1101.put("PyerAcctIssrId", "01020000");// 付款方账户所属机构标识
			// 备付金信息  非金机构必选传递
			map1101.put("ResfdAcctIssrId", "01010102000");// 备付金银行机构标识
			map1101.put("InstgAcctId", "621234000011000223");// 备付金银行账户
			map1101.put("InstgAcctNm", "bank123456");// 备付金银行账户名称
			// 渠道方信息
			map1101.put("ChannelIssrId", "");// 渠道方机构标识   空默认发起方机构标识
			// 商户信息   特约商户
			map1101.put("MrchntNo", "123456789123456");//商户编码   123456789123456
			map1101.put("MrchntTpId", "1234");//商户类别
			map1101.put("MrchntPltfrmNm", "商户名称啊");//商户名称
			// 原交易信息  
			map1101.put("OriTrxId", map1001.get("TrxId"));//
			map1101.put("OriTrxAmt", map1001.get("TrxAmt"));//
			map1101.put("OriOrdrId", map1001.get("OrdrId"));//
			map1101.put("OriTrxDtTm", map1001.get("TrxDtTm"));//
			// 产品信息
			map1101.put("ProductTp", "");//产品类型   00000000
			map1101.put("ProductAssInformation", "");//产品辅助信息   00000000
			QuickPay1101 quickPay1101 = new QuickPay1101();
			Map<String,String> resMap1101 = quickPay1101.pay1101(map1101);
			if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap1101.get("SysRtnCd"))){
				System.err.println("1101交易成功"+resMap1101.get("SysRtnDesc"));
			}else{
				System.err.println("1101交易异常"+resMap1101.get("SysRtnDesc"));
				mySleep();
				Map<String,String> queryMap = queryResult(map1101,resMap1101);
				if ("0".equals(queryMap.get("rescode"))) {
				} else {
					return;
				}
				return;
			}
			
		}
		
	}
//	@Test
//	public void test1001(){
//		// 1001 协议支付
//				Map<String,String> map1001 = new HashMap<>();
//				map1001.put("IssrId", "49360000");// 发起方所属机构标识
//				map1001.put("SignSN", "4000370693");//签名证书序列号
//				map1001.put("EncSN", "4000370671");//加密证书序列号
//				// header
//				map1001.put("ExpDt", "");
//				map1001.put("CvnNo", "");
//				map1001.put("encKey", "123456782234567832345678");
//				// body
//				map1001.put("BizTp", "100005");//业务种类
//				map1001.put("TrxId", LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");//交易流水号
//				map1001.put("TrxAmt", "CNY100.00");//交易金额
//				map1001.put("TrxDtTm", LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));//交易日期时间
//				map1001.put("AcctInTp", "01");//账户输入方式
//				map1001.put("TrxTrmTp", "07");//交易终端类型
//				map1001.put("TrxTrmNo", "00000010");//交易终端编码
//				map1001.put("PyerAcctId", "");
//				map1001.put("PyerAcctTp", "");
//				map1001.put("PyerNm", "");
//				map1001.put("IDTp", "");
//				map1001.put("IDNo", "");
//				map1001.put("MobNo", "");
//				map1001.put("PyeeIssrId", "49360000");
//				map1001.put("PyeeAcctIssrId", "110");
//				// 备付金信息  非金机构必选传递
//				map1001.put("ResfdAcctIssrId", "01010102000");// 备付金银行机构标识
//				map1001.put("InstgAcctId", "621234000011000223");// 备付金银行账户
//				map1001.put("InstgAcctNm", "bank123456");// 备付金银行账户名称
//				// 渠道方信息
//				map1001.put("ChannelIssrId", "");// 渠道方机构标识   空默认发起方机构标识
//				// 产品信息
//				map1001.put("ProductTp", "");//产品类型   00000000
//				map1001.put("ProductAssInformation", "");//产品辅助信息   00000000
//				// 订单信息  系统内
//				map1001.put("OrdrId", "493600004128655291000010"+LocalDateTime.now().withNano(0).format(dateTimeFormatter));//订单号  支付系统内的订单号
//				map1001.put("OrdrDesc", "支付系统内的订单号");
//				// 商户信息
//				map1001.put("MrchntNo", "");//商户编码   123456789123456
//				map1001.put("MrchntTpId", "");//商户类别
//				map1001.put("MrchntPltfrmNm", "");//商户名称
//				QuickPay1001 quickPay1001 = new QuickPay1001();
//				Map<String, String> resMap1001 = quickPay1001.pay1001(map1001);
//				if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap1001.get("SysRtnCd"))){
//					System.err.println("1001交易成功"+resMap1001.get("SysRtnDesc"));
//				}else{
//					System.err.println("1001交易异常"+resMap1001.get("SysRtnDesc"));
//					return;
//				}
//	}


	public void mySleep() {
		try {
			Thread.sleep(15*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public Map<String, String> queryResult(Map<String, String> reqMap,Map<String, String> resMap){
		Map<String, String> map = new HashMap<>();
		map.put("urlParam", reqMap.get("urlParam"));
		map.put("OriTrxDtTm", reqMap.get("TrxDtTm"));
		map.put("OriTrxId", reqMap.get("TrxId"));
		map.put("OriTrxTp", resMap.get("Trxtyp"));
		map.put("OriBizTp", resMap.get("BizTp"));
		map.put("IssrId", "49360000");// 发起方所属机构标识
		map.put("SignSN", "4000370693");//签名证书序列号
		map.put("EncSN", "4000370671");//加密证书序列号
		// header
		map.put("encKey", "123456782234567832345678");
		// body
		map.put("BizTp", "100005");
		// 发起方信息
		map.put("SderIssrId", "49360000");//发起机构标识
		map.put("SderAcctIssrId", "110");//发起方账户所属机构标识
		
		QuickPay3101 quickPay3101 = new QuickPay3101();
		Map<String, String> queryMap = quickPay3101.pay3101(map);
		String rescode = "00";
		if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(queryMap.get("SysRtnCd"))){
			// 退货有缺陷的成功
			if("1101".equals(map.get("OriTrxTp"))){
				// 0 交易成功 1 交易失败 2交易处理中  
				if("0".equals(queryMap.get("TrxStatus"))){
					System.out.println(map.get("OriTrxTp")+"交易成功"+queryMap.get("TrxStatus"));
					rescode = queryMap.get("TrxStatus");
				}else{
					System.out.println(map.get("OriTrxTp")+"交易失败"+queryMap.get("TrxStatus"));
					rescode = queryMap.get("TrxStatus");
				}
			}
			else if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(queryMap.get("OriSysRtnCd"))){
				// 0 交易成功 1 交易失败 2交易处理中
				if("0".equals(queryMap.get("TrxStatus"))){
					System.out.println(map.get("OriTrxTp")+"交易成功"+queryMap.get("TrxStatus"));
					rescode = queryMap.get("TrxStatus");
				}else{
					System.out.println(map.get("OriTrxTp")+"交易失败"+queryMap.get("TrxStatus"));
					rescode = queryMap.get("TrxStatus");
				}
			}
		}
		Map<String, String> dealMap = new HashMap<>();
		dealMap.put("rescode", rescode);
		return dealMap;

		
	}
}
