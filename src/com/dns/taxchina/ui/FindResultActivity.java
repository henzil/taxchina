package com.dns.taxchina.ui;

import java.util.HashMap;
import java.util.List;

import netlib.helper.DataServiceHelper;
import netlib.model.BaseModel;
import netlib.model.ErrorModel;
import netlib.net.DataAsyncTaskPool;
import netlib.net.DataJsonAsyncTask;
import netlib.net.DataMode;
import netlib.util.AppUtil;
import netlib.util.ErrorCodeUtil;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dns.taxchina.R;
import com.dns.taxchina.service.helper.ModelHelper;
import com.dns.taxchina.service.model.BaseItemModel;
import com.dns.taxchina.service.model.FindResultModel;
import com.dns.taxchina.ui.adapter.FindResultListAdapter;
import com.dns.taxchina.ui.adapter.FindResultListAdapter.ViewHolder;
import com.dns.taxchina.ui.constant.GetDataModeCostant;
import com.dns.taxchina.ui.widget.XListView;

/**
 * @author fubiao
 * @version create time:2014-5-6_下午1:39:04
 * @Description 搜索结果 Activity
 */
public class FindResultActivity extends BaseActivity implements XListView.IXListViewListener, GetDataModeCostant {

	private XListView listView;
	private FindResultListAdapter adapter;

	protected DataJsonAsyncTask asyncTask;
	protected DataAsyncTaskPool dataPool;
	protected DataServiceHelper dataServiceHelper;

	private int pageNum;

	private String titleStr;
	private TextView title, back;

	protected ModelHelper jsonHelper;

	public static final int PAGE_SIZE = 10;
	public static final String LIST_TITLE = "list_title";

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

		Intent intent = getIntent();
		titleStr = intent.getStringExtra(LIST_TITLE);
		dataPool = new DataAsyncTaskPool();
		jsonHelper = new ModelHelper(FindResultActivity.this);
		dataServiceHelper = new DataServiceHelper() {

			@Override
			public void preExecute() {
			}

			@Override
			public void postExecute(String TAG, Object result, Object... params) {
				Log.v("tag", "updateView");
				updateView(result, (Integer) params[0]);
			}

			@Override
			public Object doInBackground(Object... params) {

//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return jsonHelper.parseJson(LibIOUtil.convertStreamToStr(getResources().openRawResource(R.raw.find_result_activity_json)));
				return null;
			}
		};
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.find_result_activity);
		title = (TextView) findViewById(R.id.title_text);
		back = (TextView) findViewById(R.id.back_text);
		title.setText(titleStr);

		listView = (XListView) findViewById(R.id.x_list_view);
		adapter = new FindResultListAdapter(FindResultActivity.this, TAG);
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

		listView.setXListViewListener(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (view.getTag() instanceof ViewHolder) {
					BaseItemModel model = ((ViewHolder) view.getTag()).model;
					Intent intent = new Intent(FindResultActivity.this, DetailActivity.class);
					intent.putExtra(DetailActivity.DETAIL_MODEL, model);
					startActivity(intent);
				}
			}
		});
		
		listView.setPullLoadEnable(false);

		onLoadEvent();
	}

	// 程序加载数据
	private void onLoadEvent() {
		if (loadingDialog != null && !loadingDialog.isShowing()) {
			loadingDialog.show();
		}

		HashMap<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("mode", "7");
		reqMap.put("searchKey", titleStr);
		reqMap.put("nowPage", 1 + "");
		reqMap.put("pageSize", PAGE_SIZE + "");
		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.FindResultModel");
		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER_LOCAL, true);
		dataPool.execute(asyncTask, LOAD_MODE);
	}

	// 下拉刷新加载数据
	public void onRefresh() {
		HashMap<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("mode", "7");
		reqMap.put("searchKey", titleStr);
		reqMap.put("nowPage", 1 + "");
		reqMap.put("pageSize", PAGE_SIZE + "");
		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.FindResultModel");
		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER, true);
		dataPool.execute(asyncTask, REFRESH_MODE);
	}

	// 点击更多加载数据
	public void onLoadMore() {
		HashMap<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("mode", "7");
		reqMap.put("searchKey", titleStr);
		reqMap.put("nowPage", pageNum + 1 + "");
		reqMap.put("pageSize", PAGE_SIZE + "");
		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.FindResultModel");
		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER);// 加载更多不需要缓存
		dataPool.execute(asyncTask, MORE_MODE);
	}

	protected void updateView(Object object, int mode) {
		if (loadingDialog != null) {
			if (loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
		}

		if (mode == LOAD_MODE || mode == REFRESH_MODE) {
			listView.stopRefresh();
		} else if (mode == MORE_MODE) {
			listView.stopLoadMore();
		}

		if (object == null) {
			errorData(mode);
			return;
		}
		if (object instanceof ErrorModel) {// 网络连接失败
			ErrorModel errorModel = (ErrorModel) object;
			errorData(mode);
			// TODO 提示出网络错误
			Toast.makeText(FindResultActivity.this, ErrorCodeUtil.convertErrorCode(FindResultActivity.this, errorModel.getErrorCode()), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		BaseModel m = (BaseModel) object;// 服务器返回错误
		if (m.getResult() > 0) {
			// TODO 提示出服务器端错误。
			errorData(mode);
			Toast.makeText(FindResultActivity.this, m.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}

		FindResultModel model = (FindResultModel) object;
		if (mode == REFRESH_MODE || mode == LOAD_MODE) {
			onRefreshPostExecute(model.getDataList(), mode, model.isHasNext());
			pageNum = 1;
		} else {
			onMorePostExecute(model.getDataList(), mode, model.isHasNext());
			pageNum = pageNum + 1;
		}
	}

	protected void onRefreshPostExecute(List<BaseItemModel> result, int mode, boolean hasNext) {
		if (result.isEmpty()) {
//			listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_NO_DATA);
//			emptyView(mode);
			listView.setPullLoadEnable(false);
			adapter.refresh(result);
		} else {
			adapter.refresh(result);
			result.clear();
			result = null;
			if (hasNext) {
				listView.setPullLoadEnable(true);
//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_MORE);
			} else {
				listView.setPullLoadEnable(false);
//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);
			}

		}
	}

	protected void onMorePostExecute(List<BaseItemModel> result, int mode, boolean hasNext) {
		if (result.isEmpty()) {
			listView.setPullLoadEnable(false);
//			listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);
		} else {
			adapter.addData(result);
			result.clear();
			result = null;
			if (hasNext) {
				listView.setPullLoadEnable(true);
//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_MORE);
			} else {
				listView.setPullLoadEnable(false);
//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);
			}
		}
	}

	protected void errorData(int mode) {// 数据加载失败
		if (mode == LOAD_MODE) {
			// TODO 未完成 网络失败 界面显示，
//			errorView.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (asyncTask != null) {
			asyncTask.cancel(true);
		}
		if (adapter != null) {
			adapter.recycleBitmaps();
		}
		if (loadingDialog != null) {
			loadingDialog = null;
		}
	}
	
	@Override
	protected void showNetDialog() {
		if (AppUtil.isActivityTopStartThisProgram(this, FindResultActivity.class.getName())) {
			netDialog.show();
		}
	}
}