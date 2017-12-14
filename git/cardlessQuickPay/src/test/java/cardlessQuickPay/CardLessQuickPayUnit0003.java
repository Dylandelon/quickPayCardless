package cardlessQuickPay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cardlessQuickPay.Service.QuickPay0003;
import cardlessQuickPay.Service.QuickPay1002;
import cardlessQuickPay.Service.QuickPay1101;
import cardlessQuickPay.Service.QuickPay3101;
import configEnum.CardlessQuickPayEnum;

public class CardLessQuickPayUnit0003 {
	/**
	 * 借记直接支付
	 */
	@Test
	public void testQuickPay0003() {
		Map<String, String> configMap = new HashMap<>();
		configMap.put("urlParam", "https://127.0.0.2:443/");
		// 0003 发短信
		QuickPay0003 quickPay0003 = new QuickPay0003();
		Map<String,String> map = new HashMap<>();
		map.put("urlParam", configMap.get("urlParam"));
		map.put("ExpDt", "");
		map.put("CvnNo", "");
		map.put("encKey", "123456782234567832345678");
		// header
//				map.put("MsgVer", "1000");//报文版本
//				map.put("Trxtyp", "0001");//交易类型
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
		map.put("MrchntNo", "123456789123456");//商户编码   123456789123456
		map.put("MrchntTpId", "1234");//商户类别
		map.put("MrchntPltfrmNm", "商户名称啊");//商户名称
		Map<String,String> resMap = quickPay0003.pay0003(map);
		if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap.get("SysRtnCd"))){
			System.err.println("0003交易成功"+resMap.get("SysRtnDesc"));
		}else{
			System.err.println("0003交易异常"+resMap.get("SysRtnDesc"));
			mySleep();
			Map<String,String> queryMap = queryResult(map,resMap);
			if("0".equals(queryMap.get("rescode"))){
				
			}else{
				return;
			}
			
//					return;
		}
		mySleep();
		// 1002 支付
		Map<String,String> map1002 = new HashMap<>();
//        map1002.put("SgnNo", resMap.get("SgnNo"));
		map1002.put("urlParam", configMap.get("urlParam"));
		map1002.put("ExpDt", "");
		map1002.put("CvnNo", "");
		map1002.put("encKey", "123456782234567832345678");
		
		map1002.put("IssrId", "49360000");// 发起方所属机构标识
		map1002.put("SignSN", "4000370693");//签名证书序列号
		map1002.put("EncSN", "4000370671");//加密证书序列号
		// header
//		map1002.put("encKey", "123456782234567832345678");
		// body
		map1002.put("BizTp", "100005");//业务种类
		map1002.put("TrxId", LocalDateTime.now().withNano(0).format(dateTimeFormatter)+"123456");//交易流水号
		map1002.put("TrxAmt", "CNY100.00");//交易金额
		map1002.put("TrxDtTm", LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));//交易日期时间
		map1002.put("AcctInTp", "01");//账户输入方式
		map1002.put("TrxTrmTp", "07");//交易终端类型
		map1002.put("TrxTrmNo", "00000010");//交易终端编码
		map1002.put("PyerAcctId", "6212143000000000011");// 付款方账户
		map1002.put("PyerAcctTp", "");
		map1002.put("PyerNm", "");
		map1002.put("IDTp", "");
		map1002.put("IDNo", "");
		map1002.put("MobNo", "");
		map1002.put("AuthMsg", "123456");
		map1002.put("Smskey", resMap.get("Smskey"));
		map1002.put("PyeeIssrId", "49360000");
		map1002.put("PyeeAcctIssrId", "110");
		map1002.put("PyeeAcctId", "");
		map1002.put("PyeeNm", "");
		map1002.put("PyeeAreaNo", "");
		// 备付金信息  非金机构必选传递
		map1002.put("ResfdAcctIssrId", "01010102000");// 备付金银行机构标识
		map1002.put("InstgAcctId", "621234000011000223");// 备付金银行账户
		map1002.put("InstgAcctNm", "bank123456");// 备付金银行账户名称
		// 渠道方信息
		map1002.put("ChannelIssrId", "");// 渠道方机构标识   空默认发起方机构标识
		// 产品信息
		map1002.put("ProductTp", "");//产品类型   00000000
		map1002.put("ProductAssInformation", "");//产品辅助信息   00000000
		// 订单信息  系统内
		map1002.put("OrdrId", "493600004128655291000010"+LocalDateTime.now().withNano(0).format(dateTimeFormatter));//订单号  支付系统内的订单号
		map1002.put("OrdrDesc", "支付系统内的订单号");
		// 商户信息   特约商户
		map1002.put("MrchntNo", "123456789123456");//商户编码   123456789123456
		map1002.put("MrchntTpId", "1234");//商户类别
		map1002.put("MrchntPltfrmNm", "商户名称啊");//商户名称
		QuickPay1002 quickPay1002 = new QuickPay1002();
		Map<String, String> resmap1002 = quickPay1002.pay1002(map1002);
		boolean successFlaf = false;
		if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resmap1002.get("SysRtnCd"))){
			System.err.println("1002交易成功"+resmap1002.get("SysRtnDesc"));
			successFlaf = true;
		}else{
			System.err.println("1002交易异常"+resmap1002.get("SysRtnDesc"));
			mySleep();
			Map<String,String> queryMap = queryResult(map1002,resmap1002);
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
			map1101.put("SgnNo", "");
			map1101.put("PyeeAcctId", map1002.get("PyerAcctId"));//收款方账户  与签约协议号至少上送一个 ---
			
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
			map1101.put("OriTrxId", map1002.get("TrxId"));//
			map1101.put("OriTrxAmt", map1002.get("TrxAmt"));//
			map1101.put("OriOrdrId", map1002.get("OrdrId"));//
			map1101.put("OriTrxDtTm", map1002.get("TrxDtTm"));//
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
