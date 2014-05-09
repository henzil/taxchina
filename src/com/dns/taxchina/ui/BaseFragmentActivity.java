package com.dns.taxchina.ui;

import com.dns.taxchina.R;
import com.dns.taxchina.ui.widget.LoadingDialog;

import netlib.util.ResourceUtil;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Window;

public abstract class BaseFragmentActivity extends FragmentActivity {

	protected ResourceUtil resourceUtil;
	protected String TAG;
	protected LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBaseData(savedInstanceState);
		initBaseViews();
	}

	protected void initBaseData(Bundle savedInstanceState) {
		resourceUtil = ResourceUtil.getInstance(getApplicationContext());
		TAG = toString();
		initDialog();
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
	
	private void initDialog() {
		if (loadingDialog == null) {
			loadingDialog = new LoadingDialog(BaseFragmentActivity.this, R.style.my_dialog);
			loadingDialog.setCancelable(true);
			loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (loadingDialog != null)
							loadingDialog.cancel();
					}
					return true;
				}
			});
		}
	}
}
