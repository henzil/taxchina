package com.dns.taxchina.service.model;

/**
 * @author fubiao
 * @version create time:2014-5-8_下午2:43:08
 * @Description 找课程 条件 model
 */
public class ConditionModel extends BaseItemModel {

	private static final long serialVersionUID = -941226222374193649L;
	private int type;
	private String searchKey;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
}