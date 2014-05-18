package com.dns.taxchina.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.DownloadTaskManager;
import com.dns.taxchina.service.util.NetChangeFilter;
import com.dns.taxchina.ui.fragment.BaseFragment;
import com.dns.taxchina.ui.util.FragmentUtil;

public class HomeActivity extends BaseFragmentActivity implements View.OnClickListener {

	private View indexBut, microCourseBut, newsBut, findBut, centerBut;

	private BaseFragment currentFragment;

	private int currentTag = -1, index;

	private long exitTime = 0;

	private NetStatReceiver netStatReceiver;

	private Dialog netDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
		if (savedInstanceState != null) {
			currentTag = savedInstanceState.getInt("currentTag", -1);
		}
		initViews();
		initWidgetActions();
	}

	@Override
	protected void initData() {
		DownloadTaskManager.getInstance(this);
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
						DownloadTaskManager.getInstance(HomeActivity.this).stop();
						
					}
				}).create();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.activity_home);
		indexBut = findViewById(R.id.indexBut);
		microCourseBut = findViewById(R.id.microCourseBut);
		newsBut = findViewById(R.id.newsBut);
		findBut = findViewById(R.id.findBut);
		centerBut = findViewById(R.id.centerBut);

		indexBut.setSelected(true);
	}

	@Override
	protected void initWidgetActions() {
		indexBut.setOnClickListener(this);
		microCourseBut.setOnClickListener(this);
		newsBut.setOnClickListener(this);
		findBut.setOnClickListener(this);
		centerBut.setOnClickListener(this);
		initFragment();
	}

	@Override
	public void onClick(View v) {
		updateBtn(v);
		switch (v.getId()) {
		case R.id.indexBut:
			clickBut(0);
			break;
		case R.id.microCourseBut:
			clickBut(1);
			break;
		case R.id.newsBut:
			clickBut(2);
			break;
		case R.id.findBut:
			clickBut(3);
			break;
		case R.id.centerBut:
			clickBut(4);
			break;
		default:
			break;
		}
	}

	private void updateBtn(View v) {
		indexBut.setSelected(false);
		microCourseBut.setSelected(false);
		newsBut.setSelected(false);
		findBut.setSelected(false);
		centerBut.setSelected(false);
		v.setSelected(true);
	}

	private void clickBut(int index) {
		if (this.index == index) {
			return;
		}
		this.index = index;
		changeFragment(index);

	}

	private void changeFragment(int index) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		if (currentFragment != null && currentTag != index) {
			BaseFragment subViewFragment = null;
			if (manager.findFragmentByTag("" + index) != null) {
				subViewFragment = (BaseFragment) manager.findFragmentByTag("" + index);
				ft.hide(currentFragment).show(subViewFragment).commit();
				currentTag = index;
				currentFragment = subViewFragment;
			} else {
				subViewFragment = (BaseFragment) (new FragmentUtil()).fragmentByIndex(index);
				if (subViewFragment != null) {
					Bundle bundle = new Bundle();
					subViewFragment.setArguments(bundle);
					ft.hide(currentFragment).add(R.id.mainLayout, subViewFragment, "" + index).commit();
					currentTag = index;
					currentFragment = subViewFragment;
				}
			}
		} else {
			BaseFragment subViewFragment = null;
			subViewFragment = (BaseFragment) (new FragmentUtil()).fragmentByIndex(index);
			if (subViewFragment != null) {
				Bundle bundle = new Bundle();
				subViewFragment.setArguments(bundle);
				ft.replace(R.id.mainLayout, subViewFragment, "" + index);
				ft.commit();// 提交
				currentTag = index;
				currentFragment = subViewFragment;
			}
		}
	}

	private void initFragment() {
		if (currentTag == -1) {
			changeFragment(0);
			return;
		}
		FragmentManager manager = getSupportFragmentManager();
		// Clear all back stack.
		int backStackCount = manager.getBackStackEntryCount();
		for (int i = 0; i < backStackCount; i++) {
			// Get the back stack fragment id.
			int backStackId = manager.getBackStackEntryAt(i).getId();
			manager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} /* end of for */
		// Log.e("tag", "manager = " +
		// manager.findFragmentById(R.id.contentLayout));
		if (manager.findFragmentById(R.id.mainLayout) != null) {
			FragmentTransaction removeFt = manager.beginTransaction();
			// Log.e("tag", "执行一次删除");
			removeFt.remove(manager.findFragmentById(R.id.mainLayout));
			removeFt.commit();// 提交
		}
		changeFragment(currentTag);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentTag", currentTag);
	};

	@Override
	protected void onDestroy() {
		DownloadTaskManager.getInstance(this).stop();
		unregisterReceiver(netStatReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
//			Bundle bundle = intent.getExtras();
//			for (String key : bundle.keySet()) {
//				Log.e("tag", "!!!!!!@@@@@~~~~~~~~~~~~key = " + key);
//				Log.e("tag", "!!!!!!@@@@@~~~~~~~~~~~~bundle.get(" + key + ").toString() = "
//						+ bundle.get(key).toString());
//			}
//			if (bundle.containsKey(ConnectivityManager.EXTRA_NETWORK_INFO)) {
//				NetworkInfo networkInfo = (NetworkInfo) bundle.get(ConnectivityManager.EXTRA_NETWORK_INFO);
//				int type = networkInfo.getType();
//				Log.e("tag", "!!!!!~~~~~~~~~~~~networkInfo.getType() = " + type);
//				Log.e("tag", "!!!!!~~~~~~~~~~~~networkInfo.getTypeName() = " + networkInfo.getTypeName());
//			}
//			if (bundle.containsKey(ConnectivityManager.EXTRA_EXTRA_INFO)) {
//				String extraInfo = bundle.getString(ConnectivityManager.EXTRA_EXTRA_INFO);
//				Log.e("tag", "!!!!!~~~~~~~~~~~~extraInfo = " + extraInfo);
//			}
			boolean b = netChangeFilter.netChangeMobile(context);
			if (b && !netDialog.isShowing()) {
				DownloadTaskManager downloadTaskManager = DownloadTaskManager.getInstance(HomeActivity.this);
				Log.e("tag", "!!!!!~~~~~~(downloadTaskManager.downloadingId() != null) = " + (downloadTaskManager.downloadingId() != null));
				Log.e("tag", "!!!!!~~~~~~downloadTaskManager.getCurrentDownLoadSet().size() = " + downloadTaskManager.getCurrentDownLoadSet().size());
				if(downloadTaskManager.downloadingId() != null || downloadTaskManager.getCurrentDownLoadSet().size() > 0){
					netDialog.show();
				}
			}
		}
	}
}
