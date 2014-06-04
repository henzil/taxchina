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
import com.dns.taxchina.service.db.fileDB.ManagerSqliteDao;
import com.dns.taxchina.service.model.BaseItemModel;
import com.dns.taxchina.service.model.PVideoModel;
import com.dns.taxchina.service.model.VideoModel;
import com.dns.taxchina.ui.adapter.ColumnListAdapter;
import com.dns.taxchina.ui.adapter.VideoListAdapter;
import com.dns.taxchina.ui.util.SdCardUtil;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午1:36:57
 * @Description 内置课程 Activity
 */
public class InstalledCourseActivity extends BaseActivity {

	private TextView back, sd;

	private SdCardUtil sdCardUtil;

	private ListView columnListView;
	private ListView videoListView;

	private ColumnListAdapter columnListAdapter;
	private VideoListAdapter videoListAdapter;

	private ManagerSqliteDao managerSqliteDao;

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
		managerSqliteDao = new ManagerSqliteDao(this);
		sdCardUtil = new SdCardUtil(InstalledCourseActivity.this);
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.installed_course_activity);
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);
		sd = (TextView) findViewById(R.id.available_text);

		columnListView = (ListView) findViewById(R.id.column_list_view);
		videoListView = (ListView) findViewById(R.id.video_list_view);
		List<PVideoModel> pVideoModels = managerSqliteDao.getPVideoModel();
		columnListAdapter = new ColumnListAdapter(InstalledCourseActivity.this, TAG, pVideoModels);
		if (pVideoModels != null && pVideoModels.size() > 1) {
			videoListAdapter = new VideoListAdapter(InstalledCourseActivity.this, TAG,
					managerSqliteDao.getInternalVideoListByPId(pVideoModels.get(0).getpId()));
		}

		columnListView.setAdapter(columnListAdapter);
		videoListView.setAdapter(videoListAdapter);

		getSDCard();
	}

//	public void initDBData() {
//		doneList.clear();
//		VideoDAO videoDAO = new VideoDAO(this);
//		List<VideoModel> list = videoDAO.findAll();
//		for (VideoModel model : list) {
//			if (model.getDownloadPercent() < 100) {
//			} else {
//				doneList.add(model);
//			}
//		}
//	}

	@SuppressWarnings("static-access")
	public void getSDCard() {
		if (sdCardUtil.isSDCardExist()) {
			sd.setText(String.format(getString(R.string.sd_info), sdCardUtil.getSDTotalSize(),
					sdCardUtil.getSDAvailableSize(), sdCardUtil.getSDAvailableFormat()));
		} else {
			sd.setText(String.format(getString(R.string.sd_info), sdCardUtil.getRomTotalSize(),
					sdCardUtil.getRomAvailableSize(), sdCardUtil.getRomAvailableSizeFormat()));
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

		columnListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});

		videoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (view.getTag() instanceof VideoListAdapter.ViewHolder) {
					VideoListAdapter.ViewHolder holder = (VideoListAdapter.ViewHolder) view.getTag();
					Log.e("tag", "holder.model.getVideoPath(); = " + holder.model.getVideoName());
					Intent intent = new Intent(InstalledCourseActivity.this, VideoActivity.class);
					intent.putExtra(VideoActivity.VIDEO_MODEL_KEY, holder.model);
					startActivity(intent);
				}
			}
		});

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
		if (AppUtil.isActivityTopStartThisProgram(this, InstalledCourseActivity.class.getName())) {
			netDialog.show();
		}
	}
}