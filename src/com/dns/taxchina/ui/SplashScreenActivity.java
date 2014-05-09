package com.dns.taxchina.ui;

import java.util.HashMap;

import netlib.model.BaseModel;
import netlib.net.HttpClientUtil;
import netlib.util.ErrorCodeUtil;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;

import com.dns.taxchina.R;
import com.dns.taxchina.service.helper.ModelHelper;
import com.dns.taxchina.service.model.UpdateModel;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午2:59:26
 * @Description 启动页
 */
public class SplashScreenActivity extends BaseActivity {

	private ModelHelper jsonHelper;
	private Handler initHandler;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
				startActivity(intent);
				break;
			case 1:
				if (!isFinishing()) {
					finish();
				}
			default:
				break;
			}
		};
	};

	@Override
	protected void initData() {
		jsonHelper = new ModelHelper(SplashScreenActivity.this);
		initHandler = new Handler();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.splash_screen_activity);

		initNet();

		new Thread() {
			public void run() {

				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mHandler.sendEmptyMessage(0);
					}
				}, 1000);
			};
		}.start();
	}

	@Override
	protected void initWidgetActions() {

	}

	public void initNet() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("mode", "1");
				String serverStr = HttpClientUtil.doPostRequest(getString(R.string.base_url), params, SplashScreenActivity.this);
				if (serverStr != null && !ErrorCodeUtil.isError(serverStr)) {
					final BaseModel model = (UpdateModel) jsonHelper.parseJson(serverStr);
					initHandler.post(new Runnable() {

						@Override
						public void run() {
							mHandler.sendEmptyMessage(1);
						}
					});
				}
			}
		}).start();
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
}