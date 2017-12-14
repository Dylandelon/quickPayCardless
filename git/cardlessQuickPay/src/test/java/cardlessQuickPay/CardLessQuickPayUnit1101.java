package cardlessQuickPay;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cardlessQuickPay.Service.QuickPay2001;
import cardlessQuickPay.Service.QuickPay3101;
import configEnum.CardlessQuickPayEnum;

public class CardLessQuickPayUnit1101 {
	/**
	 * 借记直接支付
	 */
	@Test
	public void testQuickPay2001() {
		Map<String, String> configMap = new HashMap<>();
		configMap.put("urlParam", "https://127.0.0.2:443/");
		// 0003 发短信
		QuickPay2001 quickPay2001 = new QuickPay2001();
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
		map.put("AcctInTp", "01");//账户输入方式
		map.put("TrxTrmTp", "07");//交易终端类型
		map.put("TrxTrmNo", "00000010");//交易终端编码
		// 收款方/发起方信息
		map.put("PyeeAcctIssrId", "49360000");//收款方账户所属机构标识
		map.put("PyeeAcctId", "6212143000000000011");//收款方账户
		map.put("PyeeNm", "银联一");//收款方名称
		map.put("IDTp", "01");//接收方证件类型
		map.put("IDNo", "310115198903261113");//接收方证件号
//		map.put("MobNo", "18444444444");//接收方预留手机号
//		付款方/发起方信息
		map.put("PyeeIssrId", "49360000");//发送机构标识
		map.put("PyerAcctIssrId", "01020000");//付款方账户所属机构标识
		map.put("PyerAcctId", "6212143000000000011");//付款方账户
		map.put("PyerNm", "高汇通的");//付款方名称
		// 备付金信息  非金机构必选传递
		map.put("ResfdAcctIssrId", "01010102000");// 备付金银行机构标识
		map.put("InstgAcctId", "621234000011000223");// 备付金银行账户
		map.put("InstgAcctNm", "bank123456");// 备付金银行账户名称
		// 单位结算卡信息
		map.put("CorpName", "");//单位结算卡完整账户名称
		map.put("USCCode", "");//营业执照注册号或者统一社会信用代码
		
		// 渠道方信息
		map.put("ChannelIssrId", "");// 渠道方机构标识   空默认发起方机构标识
		map.put("SgnNo", "");// 签约协议号
		// 产品信息
		map.put("ProductTp", "");//产品类型   00000000
		map.put("ProductAssInformation", "");//产品辅助信息   00000000
		// 订单信息  系统内
		map.put("OrdrId", "493600004128655291000010"+LocalDateTime.now().withNano(0).format(dateTimeFormatter));//订单号  支付系统内的订单号
		map.put("OrdrDesc", "支付系统内的订单号");
		// 商户信息   特约商户
		map.put("MrchntNo", "123456789123456");//商户编码   123456789123456
		map.put("MrchntTpId", "1234");//商户类别
		map.put("MrchntPltfrmNm", "商户名称啊");//商户名称
		Map<String,String> resMap = quickPay2001.pay2001(map);
		if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(resMap.get("SysRtnCd"))){
			System.err.println("2001交易成功"+resMap.get("SysRtnDesc"));
		}else{
			System.err.println("2001交易异常"+resMap.get("SysRtnDesc"));
			mySleep();
			Map<String,String> queryMap = queryResult(map,resMap);
			if("0".equals(queryMap.get("rescode"))){
				
			}else{
				return;
			}
			
//					return;
		}
		mySleep();
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
			// 0 交易成功 1 交易失败 2交易处理中  贷记交易存在有缺陷的成功
			if("0".equals(queryMap.get("TrxStatus"))){
				System.out.println(map.get("OriTrxTp")+"交易成功TrxStatus:"+queryMap.get("TrxStatus")+"OriSysRtnCd"+queryMap.get("OriSysRtnCd"));
				rescode = queryMap.get("TrxStatus");
			}else{
				System.out.println(map.get("OriTrxTp")+"交易失败TrxStatus:"+queryMap.get("TrxStatus")+"OriSysRtnCd"+queryMap.get("OriSysRtnCd"));
				rescode = queryMap.get("TrxStatus");
			}
//			if(CardlessQuickPayEnum.CARDLESS_PAY_SUCCESS.getCode().equalsIgnoreCase(queryMap.get("OriSysRtnCd"))){
//				
//			}
		}
		Map<String, String> dealMap = new HashMap<>();
		dealMap.put("rescode", rescode);
		return dealMap;

		
	}

}
