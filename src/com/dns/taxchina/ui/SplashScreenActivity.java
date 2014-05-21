package com.dns.taxchina.ui;

import java.util.HashMap;

import netlib.constant.BaseApiConstant;
import netlib.helper.DataServiceHelper;
import netlib.net.DataAsyncTaskPool;
import netlib.net.DataJsonAsyncTask;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;

import com.dns.taxchina.R;
import com.dns.taxchina.service.helper.ModelHelper;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午2:59:26
 * @Description 启动页
 */
public class SplashScreenActivity extends BaseActivity {

	protected DataJsonAsyncTask asyncTask;
	protected DataAsyncTaskPool dataPool;
	protected DataServiceHelper dataServiceHelper;

	private ModelHelper jsonHelper;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
				startActivity(intent);
				finish();
			default:
				break;
			}
		};
	};

	@Override
	protected void initData() {
		dataPool = new DataAsyncTaskPool();
		jsonHelper = new ModelHelper(SplashScreenActivity.this);
		dataServiceHelper = new DataServiceHelper() {

			@Override
			public void preExecute() {

			}

			@Override
			public void postExecute(String TAG, Object result, Object... params) {
				updateView(result);
			}

			@Override
			public Object doInBackground(Object... params) {
				return null;
			}
		};
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.splash_screen_activity);
	}

	@Override
	protected void initWidgetActions() {
		initNet();
	}

	public void initNet() {
		HashMap<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("mode", "1");
		reqMap.put(BaseApiConstant.CONNECTION_TIMEOUT, "" + 5000);
		reqMap.put(BaseApiConstant.SOCKET_TIMEOUT, "" + 5000);
		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.LoadingModel");
		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper);
		dataPool.execute(asyncTask);
	}

	protected void updateView(Object object) {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		}, 1000);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK: {
			return false;
		}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void showNetDialog() {
		
	}
}