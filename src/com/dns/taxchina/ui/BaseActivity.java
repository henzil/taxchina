package com.dns.taxchina.ui;import android.os.Bundle;/** * @author henzil * @version create time:2014-4-24_上午10:53:55 * @Description TODO */public abstract class BaseActivity extends BaseFragmentActivity {	@Override	protected void onCreate(Bundle arg0) {		super.onCreate(arg0);		initData();		initViews();		initWidgetActions();	}}