package com.dns.taxchina.service.model;

import java.util.List;

import netlib.model.BaseModel;

/**
 * @author fubiao
 * @version create time:2014-5-8_下午2:46:37
 * @Description 找课程结果model
 */
public class FindCourseModel extends BaseModel {

	private static final long serialVersionUID = -941226222374193649L;
	private List<ConditionModel> dataList;

	public List<ConditionModel> getDataList() {
		return dataList;
	}

	public void setDataList(List<ConditionModel> dataList) {
		this.dataList = dataList;
	}
}