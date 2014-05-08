package com.dns.taxchina.service.model;

import netlib.model.BaseModel;

/**
 * @author fubiao
 * @version create time:2014-5-8_下午5:39:43
 * @Description 登录结果model
 */
public class LoginResultModel extends BaseModel{

	private static final long serialVersionUID = -761439814260166466L;
	private String userId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}