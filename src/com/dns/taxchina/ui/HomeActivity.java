package com.dns.taxchina.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.DownloadTaskManager;
import com.dns.taxchina.ui.fragment.BaseFragment;
import com.dns.taxchina.ui.util.FragmentUtil;

public class HomeActivity extends BaseFragmentActivity implements View.OnClickListener {

	private View indexBut, microCourseBut, newsBut, findBut, centerBut;

	private BaseFragment currentFragment;

	private int currentTag = -1;

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
		DownloadTaskManager.getInstance(this).start();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.activity_home);
		indexBut = findViewById(R.id.indexBut);
		microCourseBut = findViewById(R.id.microCourseBut);
		newsBut = findViewById(R.id.newsBut);
		findBut = findViewById(R.id.findBut);
		centerBut = findViewById(R.id.centerBut);
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

	private void clickBut(int index) {
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
//		Log.e("tag", "manager = " + manager.findFragmentById(R.id.contentLayout));
		if (manager.findFragmentById(R.id.mainLayout) != null) {
			FragmentTransaction removeFt = manager.beginTransaction();
//			Log.e("tag", "执行一次删除");
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
		super.onDestroy();
	}

}
