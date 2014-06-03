package com.dns.taxchina.ui;

import java.util.List;

import netlib.util.AppUtil;
import netlib.util.TouchUtil;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.BaseItemModel;
import com.dns.taxchina.ui.adapter.ColumnListAdapter;
import com.dns.taxchina.ui.adapter.ColumnListAdapter.ViewHolder;

/**
 * @author fubiao
 * @version create time:2014-5-6_下午1:39:04
 * @Description 栏目列表Activity
 */
public class ColumnListActivity extends BaseActivity {

	private TextView back, title;

	private ListView listView;
	private ColumnListAdapter adapter;

	private List<BaseItemModel> columnModels;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (loadingDialog != null) {
				if (loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			}
			if (columnModels == null || columnModels.size() == 0) {
//				emptyFailView(getString(R.string.collect_empty));
				adapter.refresh(columnModels);
			} else {
				adapter.refresh(columnModels);
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
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.column_list_activity);
		initFailView(findViewById(R.id.no_data_box));
		title = (TextView) findViewById(R.id.title_text);
		title.setText("内置课程");
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ColumnListAdapter(ColumnListActivity.this, TAG);
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

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg1.getTag() instanceof ViewHolder) {
					BaseItemModel model = ((ViewHolder) arg1.getTag()).model;
					Intent intent = new Intent(ColumnListActivity.this, VideoListActivity.class);
					intent.putExtra(VideoListActivity.LIST_ID, model.getId());
					intent.putExtra(VideoListActivity.LIST_TITLE, model.getTitle());
					startActivity(intent);
				}
			}
		});

	}

//	private void queryCollect() {
//		db = CollectDBUtil.getInstance(CourseCollectionActivity.this, PhoneUtil.getIMEI(CourseCollectionActivity.this));
//		if (loadingDialog != null && !loadingDialog.isShowing()) {
//			loadingDialog.show();
//		}
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				courseModels = db.getCourses();
//				handler.sendEmptyMessage(0);
//			}
//		}).start();
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loadingDialog != null) {
			loadingDialog = null;
		}
	}
	
	@Override
	protected void showNetDialog() {
		if (AppUtil.isActivityTopStartThisProgram(this, CourseCollectionActivity.class.getName())) {
			netDialog.show();
		}
	}
}