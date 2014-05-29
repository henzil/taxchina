package com.dns.taxchina.ui;

import java.util.ArrayList;
import java.util.List;

import netlib.helper.DataServiceHelper;
import netlib.net.DataAsyncTaskPool;
import netlib.net.DataJsonAsyncTask;
import netlib.util.AppUtil;
import netlib.util.TouchUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.DownloadTaskContact;
import com.dns.taxchina.service.download.VideoDAO;
import com.dns.taxchina.service.helper.ModelHelper;
import com.dns.taxchina.service.model.VideoModel;
import com.dns.taxchina.ui.adapter.CollectionListAdapter;
import com.dns.taxchina.ui.adapter.StudyRecordAdapter;
import com.dns.taxchina.ui.adapter.StudyRecordAdapter.DeleteListener;
import com.dns.taxchina.ui.util.SdCardUtil;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午1:36:57
 * @Description 学习记录Activity
 */
public class StudyRecordActivity extends BaseActivity {

	private TextView back, sd, edit;
	private Button alreadOver, notOver;

	public static final int ALREADOVER_TYEP = 0;
	public static final int NOTOVER_TYPE = 1;

	public int type = 0;

	private SdCardUtil sdCardUtil;

	private ListView listView;
	private StudyRecordAdapter adapter;

	protected DataJsonAsyncTask asyncTask;
	protected DataAsyncTaskPool dataPool;
	protected DataServiceHelper dataServiceHelper;

	protected ModelHelper jsonHelper;

	private boolean isClick;

	private List<VideoModel> doneList = new ArrayList<VideoModel>();
	private List<VideoModel> unDoneList = new ArrayList<VideoModel>();

	private DownLoadBroadcastReceiver broadcastReceiver;

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

		sdCardUtil = new SdCardUtil(StudyRecordActivity.this);
		broadcastReceiver = new DownLoadBroadcastReceiver();
		registerReceiver(broadcastReceiver, new IntentFilter(DownloadTaskContact.DOWNLOADING_PERCENT_INTENT_FILTER));
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.study_record_activity);
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);
		alreadOver = (Button) findViewById(R.id.already_over_btn);
		notOver = (Button) findViewById(R.id.not_over_btn);
		sd = (TextView) findViewById(R.id.available_text);
		edit = (TextView) findViewById(R.id.edit_btn);

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new StudyRecordAdapter(StudyRecordActivity.this, TAG);
		listView.setAdapter(adapter);

		getSDCard();

		alreadOver.setSelected(true);
	}

	public void initDBData() {
		// 数据库拿取数据
		unDoneList.clear();
		doneList.clear();
		VideoDAO videoDAO = new VideoDAO(this);
		List<VideoModel> list = videoDAO.findAll();
		for (VideoModel model : list) {
			if (model.getDownloadPercent() < 100) {
				unDoneList.add(model);
			} else {
				doneList.add(model);
			}
		}
		adapter.refresh(type == ALREADOVER_TYEP ? doneList : unDoneList);
	}

	@SuppressWarnings("static-access")
	public void getSDCard() {
		if (sdCardUtil.isSDCardExist()) {
			sd.setText(String.format(getString(R.string.sd_info), sdCardUtil.getSDTotalSize(), sdCardUtil.getSDAvailableSize(),
					sdCardUtil.getSDAvailableFormat()));
		} else {
			sd.setText(String.format(getString(R.string.sd_info), sdCardUtil.getRomTotalSize(), sdCardUtil.getRomAvailableSize(),
					sdCardUtil.getRomAvailableSizeFormat()));
		}
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
					edit.setText("完成");
				} else {
					adapter.setType(CollectionListAdapter.DISMISS_DELETE);
					edit.setText("编辑");
				}
				isClick = isClick ? true : false;
			}
		});

		alreadOver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn(v);
				type = ALREADOVER_TYEP;
				adapter.refresh(doneList);
			}
		});

		notOver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn(v);
				type = NOTOVER_TYPE;
				adapter.refresh(unDoneList);
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (type == ALREADOVER_TYEP && arg1.getTag() instanceof StudyRecordAdapter.ViewHolder) {
					StudyRecordAdapter.ViewHolder holder = (StudyRecordAdapter.ViewHolder) arg1.getTag();
					Log.e("tag", "holder.model.getVideoPath(); = " + holder.model.getVideoPath());
					Intent intent = new Intent(StudyRecordActivity.this, VideoActivity.class);
					intent.putExtra(VideoActivity.VIDEO_MODEL_KEY, holder.model);
					startActivity(intent);
				}
			}
		});

		initDBData();
		
		adapter.setDeleteListener(new DeleteListener() {
			
			@Override
			public void doDelete() {
				getSDCard();
			}
		});
	}

	private void updateBtn(View v) {
		alreadOver.setSelected(false);
		notOver.setSelected(false);
		v.setSelected(true);
	}

	private class DownLoadBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				int type = intent.getIntExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY, -1);
				if (type == DownloadTaskContact.DOWNLOADING_TYPE_PERCENT_VALUE || type == DownloadTaskContact.DOWNLOADING_TYPE_END_VALUE) {
					initDBData();
					getSDCard();
				} else if (type == DownloadTaskContact.DOWNLOADING_TYPE_START_VALUE || type == DownloadTaskContact.DOWNLOADING_TYPE_ERROR_VALUE) {
					adapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (asyncTask != null) {
			asyncTask.cancel(true);
		}
		if (loadingDialog != null) {
			loadingDialog = null;
		}
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void showNetDialog() {
		if (AppUtil.isActivityTopStartThisProgram(this, StudyRecordActivity.class.getName())) {
			netDialog.show();
		}
	}
}