package com.dns.taxchina.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import com.dns.taxchina.ui.adapter.VideoListAdapter;
import com.dns.taxchina.ui.util.SdCardUtil;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午1:36:57
 * @Description 视频列表Activity
 */
public class VideoListActivity extends BaseActivity {

	private TextView back, sd, title;

	private String parentPath;
	
	private SdCardUtil sdCardUtil;

	private ListView listView;
	private VideoListAdapter adapter;

	public static final String PARENT_PATH = "parentPath";

	private List<InternalVideoModel> list = new ArrayList<InternalVideoModel>();
	
	@Override
	protected void initData() {
		Intent intent = getIntent();
		parentPath = intent.getStringExtra(PARENT_PATH);
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
		getVideoData(parentPath);
	}
	
	private void getVideoData(String parentPath){
		String videoPath = LibIOUtil.getVideoPath(this);
		File filePath = new File(videoPath + parentPath);
		String childs[] = filePath.list();
		for(String str : childs){
			Log.e("tag", "str = " + str);
			if(!str.endsWith(".tmp")){
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
		setContentView(R.layout.video_list_activity);
		title = (TextView) findViewById(R.id.title_text);
		title.setText(parentPath);
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);
		sd = (TextView) findViewById(R.id.available_text);

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new VideoListAdapter(VideoListActivity.this, TAG, list);
		listView.setAdapter(adapter);
		getSDCard();

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
				if (arg1.getTag() instanceof VideoListAdapter.ViewHolder) {
					VideoListAdapter.ViewHolder holder = (VideoListAdapter.ViewHolder) arg1.getTag();
					Log.e("tag", "holder.model.getVideoPath(); = " + holder.model.getVideoName());
					Intent intent = new Intent(VideoListActivity.this, VideoActivity.class);
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
		if (AppUtil.isActivityTopStartThisProgram(this, VideoListActivity.class.getName())) {
			netDialog.show();
		}
	}
}