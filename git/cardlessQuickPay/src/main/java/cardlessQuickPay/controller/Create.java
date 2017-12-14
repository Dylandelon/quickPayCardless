package cardlessQuickPay.controller;

import java.util.HashMap;
import java.util.Map;

import cardlessQuickPay.utils.HttpClientHelper;

public class Create {
	
	String xml1 ="<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	String xmlStr =
			"<root><MsgHeader><MsgVer>1000</MsgVer><Trxtyp>0001</Trxtyp><Drctn>11</Drctn><SndDt>2017-03-02T20:54:04</SndDt><TrxAmt>CNY100000.00</TrxAmt><IssrId>01020000</IssrId><SignEncAlgo>1</SignEncAlgo><SignSN>11111111111</SignSN><MDAlgo>1</MDAlgo><EncKey>0123456789AAABBB</EncKey><EncSN>11111111111</EncSN><EncAlgo>1</EncAlgo></MsgHeader><MsgBody><BizTp>100004</BizTp><TrxInf><TrxDtTm>2017-03-02T20:54:04</TrxDtTm><TrxId>0630062958111354</TrxId><TrxAmt>CNY100000.00</TrxAmt></TrxInf><SderInf><SderAcctIssrId>01020000</SderAcctIssrId></SderInf><MrchntInf><MrchntNo>0001002000101</MrchntNo><MrchntTpId>4722</MrchntTpId><MrchntPltfrmNm>acbd123</MrchntPltfrmNm></MrchntInf><SubMrchntInf><SubMrchntNo>00012300011</SubMrchntNo><SubMrchntTpId>4722</SubMrchntTpId><SubMrchntPltfrmNm>cin123456</SubMrchntPltfrmNm></SubMrchntInf><RcverInf><MobNo>18812345678</MobNo><IDNo>98765432109876543</IDNo><RcverAcctIssrId>01020000</RcverAcctIssrId><RcverAcctId>6991234567890123</RcverAcctId><IDTp>01</IDTp><RcverNm>pengqiu</RcverNm></RcverInf><RskInf><deviceLanguage>ch</deviceLanguage><extensiveDeviceLocation>+37.12/-121.23</extensiveDeviceLocation><riskScore>9</riskScore><mchntUsrRgstrTm>20170302205404</mchntUsrRgstrTm><mchntUsrRgstrEmail>hello@world.com</mchntUsrRgstrEmail><rcvCity>0001</rcvCity><goodsClass>01</goodsClass><riskReasonCode>1234</riskReasonCode><deviceSIMNumber>2</deviceSIMNumber><sourceIP>172.17.17.111</sourceIP><MAC>00-24-7E-0A-6C-2E</MAC><devId>fsafwegdfsdf</devId><rcvProvince>0001</rcvProvince><deviceNumber>18812345678</deviceNumber><deviceMode>asdfghjkl12345677</deviceMode><accountIDHash>123</accountIDHash></RskInf></MsgBody></root>{S:Dt5By64GzjYtknYMtRtIYtsJmC0XpP1io5iqPW3OoTeSKGKjS0L0icC6IxOm+NPFx3MW83pk50Tyu6lPd++H2lCW/TlIulVKViiA40r+bBtWS02dfOQC8pYEz/lxWwQtJtUCZ3DCV0ObAQg7dfesf9oTDdyAT5OX7yJENa5CaDqG2fT2tVtRNqgpnTaPSrKab2LmJt7mIXDj1pE+PftlKGbDnhvv1bAhAUfWYP/ngCQegOl6bvhA0N7MahtpDIi8yamepMf8rALs9NnMqSGEAN3MDl94GbHIKOkCPJkkkV9PlLoyrU45QjSiTZb0ETExxaFqQVAMFet27lo2RyC69w==}";

	String xmlsum = xml1 + xmlStr;
	public static void main(String[] args) {
		String xml1 ="<?xml version=\"1.0\" encoding=\"utf-8\"?>";

		String xmlStr =
				"<root><MsgHeader><MsgVer>1000</MsgVer><Trxtyp>0001</Trxtyp><Drctn>11</Drctn><SndDt>2017-03-02T20:54:04</SndDt><TrxAmt>CNY100000.00</TrxAmt><IssrId>01020000</IssrId><SignEncAlgo>1</SignEncAlgo><SignSN>11111111111</SignSN><MDAlgo>1</MDAlgo><EncKey>0123456789AAABBB</EncKey><EncSN>11111111111</EncSN><EncAlgo>1</EncAlgo></MsgHeader><MsgBody><BizTp>100004</BizTp><TrxInf><TrxDtTm>2017-03-02T20:54:04</TrxDtTm><TrxId>0630062958111354</TrxId><TrxAmt>CNY100000.00</TrxAmt></TrxInf><SderInf><SderAcctIssrId>01020000</SderAcctIssrId></SderInf><MrchntInf><MrchntNo>0001002000101</MrchntNo><MrchntTpId>4722</MrchntTpId><MrchntPltfrmNm>acbd123</MrchntPltfrmNm></MrchntInf><SubMrchntInf><SubMrchntNo>00012300011</SubMrchntNo><SubMrchntTpId>4722</SubMrchntTpId><SubMrchntPltfrmNm>cin123456</SubMrchntPltfrmNm></SubMrchntInf><RcverInf><MobNo>18812345678</MobNo><IDNo>98765432109876543</IDNo><RcverAcctIssrId>01020000</RcverAcctIssrId><RcverAcctId>6991234567890123</RcverAcctId><IDTp>01</IDTp><RcverNm>pengqiu</RcverNm></RcverInf><RskInf><deviceLanguage>ch</deviceLanguage><extensiveDeviceLocation>+37.12/-121.23</extensiveDeviceLocation><riskScore>9</riskScore><mchntUsrRgstrTm>20170302205404</mchntUsrRgstrTm><mchntUsrRgstrEmail>hello@world.com</mchntUsrRgstrEmail><rcvCity>0001</rcvCity><goodsClass>01</goodsClass><riskReasonCode>1234</riskReasonCode><deviceSIMNumber>2</deviceSIMNumber><sourceIP>172.17.17.111</sourceIP><MAC>00-24-7E-0A-6C-2E</MAC><devId>fsafwegdfsdf</devId><rcvProvince>0001</rcvProvince><deviceNumber>18812345678</deviceNumber><deviceMode>asdfghjkl12345677</deviceMode><accountIDHash>123</accountIDHash></RskInf></MsgBody></root>";
		String serkey = "{S:Dt5By64GzjYtknYMtRtIYtsJmC0XpP1io5iqPW3OoTeSKGKjS0L0icC6IxOm+NPFx3MW83pk50Tyu6lPd++H2lCW/TlIulVKViiA40r+bBtWS02dfOQC8pYEz/lxWwQtJtUCZ3DCV0ObAQg7dfesf9oTDdyAT5OX7yJENa5CaDqG2fT2tVtRNqgpnTaPSrKab2LmJt7mIXDj1pE+PftlKGbDnhvv1bAhAUfWYP/ngCQegOl6bvhA0N7MahtpDIi8yamepMf8rALs9NnMqSGEAN3MDl94GbHIKOkCPJkkkV9PlLoyrU45QjSiTZb0ETExxaFqQVAMFet27lo2RyC69w==}";

		StringBuffer sb = new StringBuffer();
		sb.append(xml1);
		String xmlsum = xml1 + xmlStr;
		String urlParam = "http://127.0.0.1:7080/QPay/ReceiveMerchantTrxReqServlet";
		Map<String, String> params = new HashMap<>();
		params.put("sendxml", xmlsum);
//		HttpClientHelper.sendPost2(urlParam, params, "UTF-8");
		System.out.println(xmlStr.length());
		System.out.println(xmlStr.getBytes().length);
		
		
	}
}
