package com.dns.taxchina.service.model;import netlib.model.BaseModel;/** * @author henzil * @version create time:2014-6-4_下午8:58:36 * @Description 父级栏目 model */public class PVideoModel extends BaseModel {	/**	 * 	 */	private static final long serialVersionUID = -2172489880176888570L;	private String pId;	private String pTitle;	private int count;	public String getpId() {		return pId;	}	public void setpId(String pId) {		this.pId = pId;	}	public String getpTitle() {		return pTitle;	}	public void setpTitle(String pTitle) {		this.pTitle = pTitle;	}	public int getCount() {		return count;	}	public void setCount(int count) {		this.count = count;	}}