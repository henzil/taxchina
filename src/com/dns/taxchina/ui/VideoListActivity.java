package com.dns.taxchina.ui;

import java.util.ArrayList;
import java.util.List;

import netlib.util.AppUtil;
import netlib.util.TouchUtil;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.VideoDAO;
import com.dns.taxchina.service.model.VideoModel;
import com.dns.taxchina.ui.adapter.StudyRecordAdapter;
import com.dns.taxchina.ui.adapter.VideoListAdapter;
import com.dns.taxchina.ui.util.SdCardUtil;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午1:36:57
 * @Description 视频列表Activity
 */
public class VideoListActivity extends BaseActivity {

	private TextView back, sd, title;
	
	private String titleStr;

	private SdCardUtil sdCardUtil;

	private ListView listView;
	private VideoListAdapter adapter;

	private List<VideoModel> doneList = new ArrayList<VideoModel>();
	
	public static final String LIST_ID = "list_id";
	public static final String LIST_TITLE = "list_title";

	@Override
	protected void initData() {
		Intent intent = getIntent();
		titleStr = intent.getStringExtra(LIST_TITLE);
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

		sdCardUtil = new SdCardUtil(VideoListActivity.this);
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.video_list_activity);
		title = (TextView) findViewById(R.id.title_text);
		title.setText(titleStr);
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);
		sd = (TextView) findViewById(R.id.available_text);

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new VideoListAdapter(VideoListActivity.this, TAG);
		listView.setAdapter(adapter);

		getSDCard();

	}

	public void initDBData() {
		doneList.clear();
		VideoDAO videoDAO = new VideoDAO(this);
		List<VideoModel> list = videoDAO.findAll();
		for (VideoModel model : list) {
			if (model.getDownloadPercent() < 100) {
			} else {
				doneList.add(model);
			}
		}
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

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg1.getTag() instanceof StudyRecordAdapter.ViewHolder) {
					StudyRecordAdapter.ViewHolder holder = (StudyRecordAdapter.ViewHolder) arg1.getTag();
					Log.e("tag", "holder.model.getVideoPath(); = " + holder.model.getVideoPath());
					Intent intent = new Intent(VideoListActivity.this, VideoActivity.class);
					intent.putExtra(VideoActivity.VIDEO_MODEL_KEY, holder.model);
					startActivity(intent);
				}
			}
		});

		initDBData();
		
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (loadingDialog != null) {
			loadingDialog = null;
		}
	}

	@Override
	protected void showNetDialog() {
		if (AppUtil.isActivityTopStartThisProgram(this, VideoListActivity.class.getName())) {
			netDialog.show();
		}
	}
}