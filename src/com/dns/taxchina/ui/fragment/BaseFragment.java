package com.dns.taxchina.ui.fragment;

import netlib.helper.DataServiceHelper;
import netlib.net.DataAsyncTaskPool;
import netlib.net.DataJsonAsyncTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dns.taxchina.R;
import com.dns.taxchina.service.helper.ModelHelper;
import com.dns.taxchina.ui.widget.LoadingDialog;

/**
 * @author henzil
 * @version create time:2014-4-24_上午11:09:30
 * @Description 所有fragment 基类
 */
@SuppressLint("NewApi")
public abstract class BaseFragment extends Fragment {
	protected String TAG;
	protected Context context;

	protected LoadingDialog loadingDialog;

	protected DataJsonAsyncTask asyncTask;
	protected DataAsyncTaskPool dataPool;
	protected DataServiceHelper dataServiceHelper;
	protected ModelHelper jsonHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBaseData();
		initBaseViews();
	}
	
	protected void initBaseData() {
		context = getActivity();
		initDialog();
	}

	protected void initBaseViews() {
		((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.no_anim);
	}

	@SuppressLint("NewApi")
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initData();
		View view = initViews(inflater, container, savedInstanceState);
		initWidgetActions();
		return view;
	}

	protected abstract void initData();

	protected abstract View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	protected abstract void initWidgetActions();

	private void initDialog() {
		if (loadingDialog == null) {
			loadingDialog = new LoadingDialog(context, R.style.my_dialog);
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
