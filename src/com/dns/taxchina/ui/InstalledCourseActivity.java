package com.dns.taxchina.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import netlib.util.AppUtil;
import netlib.util.LibIOUtil;
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
import com.dns.taxchina.service.model.InternalVideoModel;
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

//	private List<PVideoModel> pVideoModels = new ArrayList<PVideoModel>();

	private List<Map<String, Object>> pList = new ArrayList<Map<String, Object>>();

	private List<InternalVideoModel> list = new ArrayList<InternalVideoModel>();

	private ColumnListAdapter columnListAdapter;
	private VideoListAdapter videoListAdapter;

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
//		managerSqliteDao = new ManagerSqliteDao(this);

		sdCardUtil = new SdCardUtil(InstalledCourseActivity.this);
		super.initData();
		initFileData();
		if (pList.size() > 0) {
			Map<String, Object> map = pList.get(0);
			getVideoData(map.get("name").toString());
		}

	}

	private void initFileData() {
		String videoPath = LibIOUtil.getVideoPath(this);
		File filePath = new File(videoPath);
		Log.e("tag", "filePath.getAbsolutePath() = " + filePath.getAbsolutePath());
		Log.e("tag", "filePath.exists() = " + filePath.exists());
		Log.e("tag", "filePath.isDirectory() = " + filePath.isDirectory());
		String childs[] = filePath.list();
		if (childs == null) {
			return;
		}
		for (String str : childs) {
			Log.e("tag", "str = " + str);
			Map<String, Object> map = new HashMap<String, Object>();
			String childName = str;
			map.put("name", childName);
			String childPath = filePath.getPath() + File.separator + childName;
			File childFile = new File(childPath);
			if (childFile.exists() && childFile.isDirectory()) {
				String childs2[] = childFile.list();
				int count = 0;
				for (String str2 : childs2) {
					Log.e("tag", "str2 = " + str2);
					if (!str2.endsWith(".tmp")) {
						count++;
					}
				}
				map.put("count", count);
			}
			pList.add(map);
		}
	}

	private void getVideoData(String parentPath) {
		list.clear();
		String videoPath = LibIOUtil.getVideoPath(this);
		File filePath = new File(videoPath + parentPath);
		String childs[] = filePath.list();
		for (String str : childs) {
			Log.e("tag", "str = " + str);
			if (!str.endsWith(".tmp")) {
				String childName = str;
				String childPath = filePath.getPath() + File.separator + childName;
				File childFile = new File(childPath);
				if (childFile.exists() && childFile.isFile()) {
					InternalVideoModel model = new InternalVideoModel();
					model.setTitle(childName);
					model.setVideoName(parentPath + File.separator + childName);
					model.setSize(childFile.length() + "");
					list.add(model);
				}
			}
		}
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.installed_course_activity);
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);
		sd = (TextView) findViewById(R.id.available_text);

		columnListView = (ListView) findViewById(R.id.column_list_view);
		videoListView = (ListView) findViewById(R.id.video_list_view);
		columnListAdapter = new ColumnListAdapter(InstalledCourseActivity.this, TAG, pList);
		videoListAdapter = new VideoListAdapter(InstalledCourseActivity.this, TAG, list);

		columnListView.setAdapter(columnListAdapter);
		videoListView.setAdapter(videoListAdapter);

		getSDCard();
	}

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
				Map<String, Object> map = pList.get(position);
				getVideoData(map.get("name").toString());
				videoListAdapter.refresh(list);
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