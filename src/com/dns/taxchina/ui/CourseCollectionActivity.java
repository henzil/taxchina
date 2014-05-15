package com.dns.taxchina.ui;

import java.util.List;

import netlib.util.PhoneUtil;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.db.CollectDBUtil;
import com.dns.taxchina.service.model.BaseItemModel;
import com.dns.taxchina.ui.adapter.CollectionListAdapter;
import com.dns.taxchina.ui.adapter.CollectionListAdapter.CollectionDeleteListener;
import com.dns.taxchina.ui.adapter.CollectionListAdapter.ViewHolder;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午1:36:57
 * @Description 课程收藏 Activity
 */
public class CourseCollectionActivity extends BaseActivity {

	private TextView back, edit;

	private ListView listView;
	private CollectDBUtil db;
	private CollectionListAdapter adapter;

	private List<BaseItemModel> courseModels;
	private boolean isClick;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (loadingDialog != null) {
				if (loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			}
			if (courseModels == null || courseModels.size() == 0) {
				emptyFailView(getString(R.string.collect_empty));
				adapter.refresh(courseModels);
			} else {
				adapter.refresh(courseModels);
			}
		};
	};

	@Override
	protected void initData() {
		loadingDialog.setOnKeyListener(new OnKeyListener() {
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

	@Override
	protected void initViews() {
		setContentView(R.layout.course_collection_activity);
		initFailView(findViewById(R.id.no_data_box));
		back = (TextView) findViewById(R.id.back_text);
		edit = (TextView) findViewById(R.id.edit_btn);

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new CollectionListAdapter(CourseCollectionActivity.this, TAG);
		listView.setAdapter(adapter);
	}

	@Override
	protected void initWidgetActions() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isClick = isClick ? false : true;
				if (isClick) {
					adapter.setType(CollectionListAdapter.SHOW_DELETE);
				} else {
					adapter.setType(CollectionListAdapter.DISMISS_DELETE);
				}
				isClick = isClick ? true : false;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg1.getTag() instanceof ViewHolder) {
					BaseItemModel model = ((ViewHolder) arg1.getTag()).model;
					Intent intent = new Intent(CourseCollectionActivity.this, DetailActivity.class);
					intent.putExtra(DetailActivity.DETAIL_MODEL, model);
					startActivity(intent);
				}
			}
		});

		adapter.setCollectionDeleteListener(new CollectionDeleteListener() {

			@Override
			public void delete(String id) {
				db.removeCourse(id);
				queryCollect();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		queryCollect();
	}

	private void queryCollect() {
		db = CollectDBUtil.getInstance(CourseCollectionActivity.this, PhoneUtil.getIMEI(CourseCollectionActivity.this));
		if (loadingDialog != null && !loadingDialog.isShowing()) {
			loadingDialog.show();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				courseModels = db.getCourses();
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loadingDialog != null) {
			loadingDialog = null;
		}
	}
}