package com.dns.taxchina.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.dns.taxchina.R;
import com.dns.taxchina.ui.fragment.BaseFragment;
import com.dns.taxchina.ui.util.FragmentUtil;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

	private View indexBut, microCourseBut, newsBut, findBut, centerBut;

	private BaseFragment currentFragment;

	private int currentTag;

	@Override
	protected void initData() {

		
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
		currentFragment = (BaseFragment) (new FragmentUtil()).fragmentByIndex(index);
		if (currentFragment != null) {
			currentTag = index;
			Bundle bundle = new Bundle();
			currentFragment.setArguments(bundle);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.mainLayout, currentFragment, "" + currentTag);
			ft.commit();// 提交
		}
	}

	private void initFragment() {
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
		changeFragment(0);
	}

}
