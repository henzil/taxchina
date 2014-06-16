package com.dns.taxchina.ui;

import netlib.util.ResourceUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dns.taxchina_pad.R;
import com.dns.taxchina.service.download.DownloadTaskManager;
import com.dns.taxchina.service.util.NetChangeFilter;
import com.dns.taxchina.ui.widget.LoadingDialog;

public abstract class BaseFragmentActivity extends FragmentActivity {

	protected LoadingDialog loadingDialog;
	protected ResourceUtil resourceUtil;
	protected String TAG;
	protected RelativeLayout failBox;
	protected ImageView failImg;
	protected TextView failText;

	protected Dialog netDialog;

	protected NetStatReceiver netStatReceiver;
	
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

	protected void initFailView(View view) {
		failBox = (RelativeLayout) view.findViewById(R.id.no_data_box);
		failImg = (ImageView) view.findViewById(R.id.no_data_img);
		failText = (TextView) view.findViewById(R.id.no_data_text);
	}

	protected void emptyFailView() {
		emptyFailView(getString(R.string.no_content));
	}

	protected void emptyFailView(String emptyStr) {
		failBox.setVisibility(View.VISIBLE);
		failImg.setBackgroundResource(R.drawable.no_data_img);
		failText.setText(emptyStr);
	}

	public interface OnEmptyFailViewClickedListener {
		void onFailViewClicked();
	}

	protected void errorFailView(final OnEmptyFailViewClickedListener listener) {
		failBox.setVisibility(View.VISIBLE);
		failImg.setBackgroundResource(R.drawable.no_data_img);
		failText.setText(getString(R.string.cache_load_more));
		failBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onFailViewClicked();
			}
		});
	}

	protected void resetFailView() {
		failBox.setVisibility(View.GONE);
	}

	protected void initData() {
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		netStatReceiver = new NetStatReceiver(this);
		registerReceiver(netStatReceiver, intentFilter);
		netDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("已切换到3G状态，是否继续下载？")
				.setPositiveButton("继续", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton("停止", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 停止下载。
						// 如果还有下载队列中还有正在进行的任务时。
						DownloadTaskManager.getInstance(BaseFragmentActivity.this).stop();

					}
				}).create();
	}

	protected abstract void initViews();

	protected abstract void initWidgetActions();
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(netStatReceiver);
		super.onDestroy();
	}

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
	
	public class NetStatReceiver extends BroadcastReceiver {

		private NetChangeFilter netChangeFilter = null;

		public NetStatReceiver(Context context) {
			netChangeFilter = new NetChangeFilter(context);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// Intent中ConnectivityManager.EXTRA_NO_CONNECTIVITY这个关键字表示着当前是否连接上了网络
			Log.d("tag", "收到一次网络改变请求。。。。。。。。");
			boolean b = netChangeFilter.netChangeMobile(context);
			if (b && !netDialog.isShowing()) {
				DownloadTaskManager downloadTaskManager = DownloadTaskManager.getInstance(BaseFragmentActivity.this);
				Log.e("tag",
						"!!!!!~~~~~~(downloadTaskManager.downloadingId() != null) = "
								+ (downloadTaskManager.downloadingId() != null));
				Log.e("tag", "!!!!!~~~~~~downloadTaskManager.getCurrentDownLoadSet().size() = "
						+ downloadTaskManager.getCurrentDownLoadSet().size());
				if (downloadTaskManager.downloadingId() != null
						|| downloadTaskManager.getCurrentDownLoadSet().size() > 0) {
					showNetDialog();
				}
			}
		}
	}
	
	protected abstract void showNetDialog();
	
}
