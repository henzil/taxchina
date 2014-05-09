package com.dns.taxchina.ui.fragment;import java.util.HashMap;import java.util.List;import netlib.helper.DataServiceHelper;import netlib.model.BaseModel;import netlib.model.ErrorModel;import netlib.net.DataAsyncTaskPool;import netlib.net.DataJsonAsyncTask;import netlib.net.DataMode;import netlib.util.ErrorCodeUtil;import netlib.util.LibIOUtil;import android.content.DialogInterface;import android.content.DialogInterface.OnKeyListener;import android.content.Intent;import android.os.Bundle;import android.util.Log;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.AdapterView;import android.widget.AdapterView.OnItemClickListener;import android.widget.TextView;import android.widget.Toast;import com.dns.taxchina.R;import com.dns.taxchina.service.helper.ModelHelper;import com.dns.taxchina.service.model.BaseItemModel;import com.dns.taxchina.service.model.MicroCourseModel;import com.dns.taxchina.ui.CourseListActivity;import com.dns.taxchina.ui.adapter.MicroCourseAdapter;import com.dns.taxchina.ui.adapter.MicroCourseAdapter.ViewHolder;import com.dns.taxchina.ui.constant.GetDataModeCostant;import com.dns.taxchina.ui.widget.XListView;/** * @author henzil * @version create time:2014-4-24_下午1:59:19 * @Description 微课程 fragment */public class MicroCourseFragment extends BaseFragment implements XListView.IXListViewListener, GetDataModeCostant {	protected int begin = 1;	protected final static int LIMIT = 10;	private XListView listView;	private MicroCourseAdapter adapter;	private TextView title, back;	protected DataJsonAsyncTask asyncTask;	protected DataAsyncTaskPool dataPool;	protected DataServiceHelper dataServiceHelper;	protected ModelHelper jsonHelper;	@Override	protected void initData() {		loadingDialog.setOnKeyListener(new OnKeyListener() {			@Override			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {				if (keyCode == KeyEvent.KEYCODE_BACK) {					if (loadingDialog != null)						loadingDialog.cancel();				}				return true;			}		});		dataPool = new DataAsyncTaskPool();		dataServiceHelper = new DataServiceHelper() {			@Override			public void preExecute() {			}			@Override			public void postExecute(String TAG, Object result, Object... params) {				Log.v("tag", "updateView");				int mode = (Integer) params[0];				updateView(result, mode);			}			@Override			public Object doInBackground(Object... params) {				try {					Thread.sleep(1000);				} catch (InterruptedException e) {					e.printStackTrace();				}				return jsonHelper.parseJson(LibIOUtil.convertStreamToStr(getResources().openRawResource(R.raw.micro_course_fragment_json)));//				return null;			}		};		jsonHelper = new ModelHelper(context);		adapter = new MicroCourseAdapter(context, TAG);	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.micro_course_fragment, container, false);		title = (TextView) view.findViewById(R.id.title_text);		title.setText("微课程");		back = (TextView) view.findViewById(R.id.back_text);		back.setVisibility(View.GONE);		listView = (XListView) view.findViewById(R.id.listView);		listView.setAdapter(adapter);		return view;	}	@Override	protected void initWidgetActions() {		listView.setXListViewListener(this);		listView.setOnItemClickListener(new OnItemClickListener() {			@Override			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {				if (arg1.getTag() instanceof ViewHolder) {					BaseItemModel model = ((ViewHolder) arg1.getTag()).model;					Intent intent = new Intent(context, CourseListActivity.class);					intent.putExtra(CourseListActivity.LIST_ID, model.getId());					intent.putExtra(CourseListActivity.LIST_TITLE, model.getTitle());					startActivity(intent);				}			}		});		initNet();	}	private void initNet() {		if (loadingDialog != null && !loadingDialog.isShowing()) {			loadingDialog.show();		}		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "getSysMessageList");		reqMap.put("begin", "" + "1");		reqMap.put("limit", "" + LIMIT);		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.MicroCourseModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER_LOCAL, true);		dataPool.execute(asyncTask, LOAD_MODE);	}	@Override	public void onRefresh() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("command", "getSysMessageList");		reqMap.put("begin", "" + "1");		reqMap.put("limit", "" + LIMIT);		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.MicroCourseModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER, true);		dataPool.execute(asyncTask, REFRESH_MODE);	}	@Override	public void onLoadMore() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("command", "getSysMessageList");		reqMap.put("begin", "" + (begin + LIMIT));		reqMap.put("limit", "" + LIMIT);		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.MicroCourseModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER);// 加载更多不需要缓存		dataPool.execute(asyncTask, MORE_MODE);	}	protected void updateView(Object object, int mode) {		if (loadingDialog != null) {			if (loadingDialog.isShowing()) {				loadingDialog.dismiss();			}		}		if (mode == LOAD_MODE || mode == REFRESH_MODE) {			listView.stopRefresh();		} else if (mode == MORE_MODE) {			listView.stopLoadMore();		}		if (object == null) {			errorData(mode);			return;		}		if (object instanceof ErrorModel) {// 网络连接失败			ErrorModel errorModel = (ErrorModel) object;			errorData(mode);			// TODO 提示出网络错误			Toast.makeText(context, ErrorCodeUtil.convertErrorCode(context, errorModel.getErrorCode()), Toast.LENGTH_SHORT).show();			return;		}		BaseModel m = (BaseModel) object;// 服务器返回错误		if (m.getCode() > 0) {			// TODO 提示出服务器端错误。			errorData(mode);			Toast.makeText(context, m.getMessage(), Toast.LENGTH_SHORT).show();			return;		}		MicroCourseModel microCourseModel = (MicroCourseModel) object;		if (mode == REFRESH_MODE || mode == LOAD_MODE) {			onRefreshPostExecute(microCourseModel.getDataList(), mode, microCourseModel.isHasNext());			begin = 1;		} else {			onMorePostExecute(microCourseModel.getDataList(), mode, microCourseModel.isHasNext());			begin = begin + 1;		}	}	protected void onRefreshPostExecute(List<BaseItemModel> result, int mode, boolean hasNext) {		if (result.isEmpty()) {//			listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_NO_DATA);//			emptyView(mode);			adapter.refresh(result);		} else {			adapter.refresh(result);			result.clear();			result = null;			if (hasNext) {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_MORE);			} else {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);			}		}	}	protected void onMorePostExecute(List<BaseItemModel> result, int mode, boolean hasNext) {		if (result.isEmpty()) {//			listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);		} else {			adapter.addData(result);			result.clear();			result = null;			if (hasNext) {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_MORE);			} else {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);			}		}	}	protected void errorData(int mode) {// 数据加载失败		if (mode == LOAD_MODE) {			// TODO 未完成 网络失败 界面显示，//			errorView.show();		}	}	@Override	public void onDestroy() {		super.onDestroy();		if (asyncTask != null) {			asyncTask.cancel(true);		}		if (adapter != null) {			adapter.recycleBitmaps();		}		if (loadingDialog != null) {			loadingDialog = null;		}	}}