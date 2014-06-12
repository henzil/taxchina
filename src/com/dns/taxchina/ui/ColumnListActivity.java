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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dns.taxchina.R;
import com.dns.taxchina.ui.adapter.ColumnListAdapter;
import com.dns.taxchina.ui.adapter.ColumnListAdapter.ViewHolder;

/**
 * @author fubiao
 * @version create time:2014-5-6_下午1:39:04
 * @Description 栏目列表 Activity
 */
public class ColumnListActivity extends BaseActivity {

	private TextView back, title;

	private ListView listView;
	private ColumnListAdapter adapter;

	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private RelativeLayout failBox;
	private ImageView failImg;
	private TextView failText;

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
		initFileData();
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
			String childPath = filePath.getPath() + File.separator + childName;
			File childFile = new File(childPath);
			if (childFile.exists() && childFile.isDirectory()) {
				map.put("name", childName);
				String childs2[] = childFile.list();
				int count = 0;
				for (String str2 : childs2) {
					Log.e("tag", "str2 = " + str2);
					if (!str2.endsWith(".tmp")) {
						count++;
					}
				}
				map.put("count", count);
				list.add(map);
			}
		}
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.column_list_activity);
		initFailView(findViewById(R.id.no_data_box));
		title = (TextView) findViewById(R.id.title_text);
		title.setText("课程目录");
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);

		failBox = (RelativeLayout) findViewById(R.id.no_data_box);
		failImg = (ImageView) findViewById(R.id.no_data_img);
		failText = (TextView) findViewById(R.id.no_data_text);

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ColumnListAdapter(ColumnListActivity.this, TAG, list);
		listView.setAdapter(adapter);
		if (adapter.getCount() == 0) {
			Toast.makeText(this, R.string.no_video_content, Toast.LENGTH_LONG).show();
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
				if (arg1.getTag() instanceof ViewHolder) {
					Map<String, Object> model = ((ViewHolder) arg1.getTag()).mapData;
					Intent intent = new Intent(ColumnListActivity.this, VideoListActivity.class);
					intent.putExtra(VideoListActivity.PARENT_PATH, model.get("name").toString());
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
		if (AppUtil.isActivityTopStartThisProgram(this, CourseCollectionActivity.class.getName())) {
			netDialog.show();
		}
	}

	protected void emptyView() {
		listView.setVisibility(View.GONE);
		failBox.setVisibility(View.VISIBLE);
		failImg.setBackgroundResource(R.drawable.no_data_img);
		failText.setText(getString(R.string.no_video_content));
		failBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

}