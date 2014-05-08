package com.dns.taxchina.service.model;

import netlib.model.BaseModel;

/**
 * @author 付彪 
 *    更新 model
 */
public class UpdateModel extends BaseModel {

	private static final long serialVersionUID = -271242829711978759L;
	private boolean update;
	private String describe;
	private String url;

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}