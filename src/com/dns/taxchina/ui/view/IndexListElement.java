package com.dns.taxchina.ui.view;

import android.content.Context;
/**
 * 
 * @author fubiao
 * @version create time:2014-2-25_下午5:57:04
 * @Description 首页listview 元素
 */
public interface IndexListElement {

	void initView(Context context);

	public void updateView(Object object, String TAG);
}