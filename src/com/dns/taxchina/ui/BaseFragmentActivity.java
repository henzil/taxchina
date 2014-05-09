package com.dns.taxchina.ui;

import netlib.util.ResourceUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public abstract class BaseFragmentActivity extends FragmentActivity {

	protected ResourceUtil resourceUtil;
	protected String TAG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBaseData(savedInstanceState);
		initBaseViews();
	}

	protected void initBaseData(Bundle savedInstanceState) {
		resourceUtil = ResourceUtil.getInstance(getApplicationContext());
		TAG = toString();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	protected void initBaseViews() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	protected abstract void initData();

	protected abstract void initViews();

	protected abstract void initWidgetActions();

}
