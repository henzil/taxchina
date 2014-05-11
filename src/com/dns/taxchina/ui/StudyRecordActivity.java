package com.dns.taxchina.ui;

import java.util.ArrayList;
import java.util.List;

import netlib.helper.DataServiceHelper;
import netlib.net.DataAsyncTaskPool;
import netlib.net.DataJsonAsyncTask;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.VideoDAO;
import com.dns.taxchina.service.helper.ModelHelper;
import com.dns.taxchina.service.model.VideoModel;
import com.dns.taxchina.ui.adapter.StudyRecordAdapter;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午1:36:57
 * @Description 学习记录Activity
 */
public class StudyRecordActivity extends BaseActivity {

	private TextView back;
	private Button alreadOver, notOver;

	public static final int ALREADOVER_TYEP = 0;
	public static final int NOTOVER_TYPE = 1;

	public int type = 0;

	private ListView listView;
	private StudyRecordAdapter adapter;

	protected DataJsonAsyncTask asyncTask;
	protected DataAsyncTaskPool dataPool;
	protected DataServiceHelper dataServiceHelper;

	protected ModelHelper jsonHelper;
	
	private List<VideoModel> doneList = new ArrayList<VideoModel>();
	private List<VideoModel> unDoneList = new ArrayList<VideoModel>();

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

//		dataPool = new DataAsyncTaskPool();
//		jsonHelper = new ModelHelper(StudyRecordActivity.this);
//		dataServiceHelper = new DataServiceHelper() {
//
//			@Override
//			public void preExecute() {
//				if (loadingDialog != null && !loadingDialog.isShowing()) {
//					loadingDialog.show();
//				}
//			}
//
//			@Override
//			public void postExecute(String TAG, Object result, Object... params) {
//				Log.v("tag", "updateView");
//				updateView(result);
//			}
//
//			@Override
//			public Object doInBackground(Object... params) {
//
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return jsonHelper.parseJson(LibIOUtil.convertStreamToStr(getResources().openRawResource(R.raw.study_record_list_json)));
//				// return null;
//			}
//		};
		
		
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.study_record_activity);
		back = (TextView) findViewById(R.id.back_text);
		alreadOver = (Button) findViewById(R.id.already_over_btn);
		notOver = (Button) findViewById(R.id.not_over_btn);

		listView = (ListView) findViewById(R.id.list_view);
		adapter = new StudyRecordAdapter(StudyRecordActivity.this, TAG);
		listView.setAdapter(adapter);

		alreadOver.setSelected(true);
	}

	public void doNet() {
//		HashMap<String, String> reqMap = new HashMap<String, String>();
//		reqMap.put("mode", "");
//		reqMap.put("type", type + "");
//		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.StudyRecordModel");
//		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper);
//		dataPool.execute(asyncTask);
		// TODO 从数据库拿取数据
		unDoneList.clear();
		doneList.clear();
		VideoDAO videoDAO = new VideoDAO(this);
		List<VideoModel> list = videoDAO.findAll();
		for(VideoModel model : list){
			if(model.getDownloadPercent()<100){
				unDoneList.add(model);
			} else {
				doneList.add(model);
			}
		}
		adapter.refresh(type == ALREADOVER_TYEP? doneList : unDoneList);
	}

	@Override
	protected void initWidgetActions() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		alreadOver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn(v);
				type = ALREADOVER_TYEP;
				doNet();
			}
		});

		notOver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn(v);
				type = NOTOVER_TYPE;
				doNet();
			}
		});

		doNet();
	}

	private void updateBtn(View v) {
		alreadOver.setSelected(false);
		notOver.setSelected(false);
		v.setSelected(true);
	}

//	protected void updateView(Object object) {
//		if (loadingDialog != null) {
//			if (loadingDialog.isShowing()) {
//				loadingDialog.dismiss();
//			}
//		}
////		if (mode == LOAD_MODE || mode == REFRESH_MODE) {
////		} else if (mode == MORE_MODE) {
////		}
//
//		if (object == null) {
////			errorData(mode);
//			return;
//		}
//		if (object instanceof ErrorModel) {// 网络连接失败
//			ErrorModel errorModel = (ErrorModel) object;
////			errorData(mode);
//			// TODO 提示出网络错误
//			Toast.makeText(StudyRecordActivity.this, ErrorCodeUtil.convertErrorCode(StudyRecordActivity.this, errorModel.getErrorCode()), Toast.LENGTH_SHORT)
//					.show();
//			return;
//		}
//		BaseModel m = (BaseModel) object;// 服务器返回错误
//		if (m.getResult() > 0) {
//			// TODO 提示出服务器端错误。
////			errorData(mode);
//			Toast.makeText(StudyRecordActivity.this, m.getMessage(), Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		StudyRecordModel studyRecordModel = (StudyRecordModel) object;
//		adapter.refresh(studyRecordModel.getDataList());
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (asyncTask != null) {
			asyncTask.cancel(true);
		}
		if (loadingDialog != null) {
			loadingDialog = null;
		}
	}
}