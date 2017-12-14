package configEnum;

public enum CardlessQuickPayEnum {
	
	CARDLESS_TIME_OUT("ES000033","接收方超过系统响应时限未向平台返回应答回执"),
	CARDLESS_CANCEL("PB511005","签约人账户状态为已注销"),
	CARDLESS_FREEZE("PB511006","签约人账户状态为冻结中"),
	CARDLESS_LOSS ("PB511010","签约人账户状态为挂失"),
	CARDLESS_UNDEFINITION ("PB511098","其他因账户状态导致的失败默认使用此返回码"),
	CARDLESS_CELLPHONE_ERROR("PB511013","请求中预留手机号与签约人在接收方机构预留的协议支付手机号不符"),
	CARDLESS_CELLPHONE_UNSET("PB511014","签约人账户未登记预留手机"),
	CARDLESS_SMS_CLOSE("PB511015","签约人未在接收方机构开通短信功能"),
	CARDLESS_NAME_ERROR("PB511017","签约人账户名称与接收方机构记录不符"),
	CARDLESS_ID_ERROR("PB511019","签约人证件号与接收方机构记录不符"),
	CARDLESS_SMS_ERROR("PB511027","短信验证码不符"),
	CARDLESS_SMS_EXPIRY("PB511028","短信验证码已超过有效时间"),
	CARDLESS_SMS_OVERRUN("PB511025","同账号当日签约次数超过接收方机构限制"),
	CARDLESS_SMS_FAITH_OVERRUN("PB511026","同账号当日签约失败次数超过接收方机构限制"),
	CARDLESS_SMS_UNDEFINITION("PB511099","其他原因签约失败"),
	// 错误的短信验证码
	CARDLESS_SMS_AUTH_ERROR("EB005000","身份认证失败"),
	// 解约 协议号发送错误
	CARDLESS_RECISSION_ERROR("PB512001","接收方机构查无此签约协议号"),
	// 重复解约
	CARDLESS_RECISSION_REPEAT("PB512002","协议状态为已解约"),
	// 协议支付异常/直接支付异常
	CARDLESS_PAY_CANCEL("PB520005","接收方账户状态为已注销"),
	CARDLESS_PAY_FREEZE("PB520006","接收方账户状态为已冻结"),
	CARDLESS_PAY_EXPIRY("PB520007","接收方账户超过有效期"),
	CARDLESS_PAY_ERROR("PB521014","接收方机构查无此签约协议号"),
	CARDLESS_SIGN_EXPIRY("PB521015","签约协议号对应支付协议已失效（协议超期）"),
	CARDLESS_SIGN_RECISSION("PB521013","签约协议号对应支付协议已解约"),
	// 除以上原因外的其他因协议状态原因导致的失败默认使用此返回码
	CARDLESS_SIGN_UNDEFINITION("PB521096","其他原因协议状态异常"),
	CARDLESS_PAY_BALANCE("PB520011","接收方账户可用余额不足"),
	CARDLESS_PAY_LIMIT("PB521012","接收方账户为信用卡账户时可用额度不足"),
	CARDLESS_REFUNDS_TIME_OUT("AB0A2000","接收方超过系统响应时限未向平台返回应答回执"),
	CARDLESS_CREDIT_ERROR("PB005203","身份认证失败"),
	CARDLESS_CREDIT_SIGN_ERROR("PB005212","身份认证失败"),
	CARDLESS_PAY_SUCCESS("00000000","交易成功");
	
	private String code;
	
	private String value;
	
	private CardlessQuickPayEnum(String code,String value){
		
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}
	

}
