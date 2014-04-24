package com.dns.taxchina.ui.fragment;import java.util.HashMap;import netlib.helper.DataServiceHelper;import netlib.model.BaseModel;import netlib.model.ErrorModel;import netlib.net.DataAsyncTaskPool;import netlib.net.DataJsonAsyncTask;import netlib.net.DataMode;import netlib.util.ErrorCodeUtil;import android.content.DialogInterface;import android.content.DialogInterface.OnKeyListener;import android.os.Bundle;import android.util.Log;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.Toast;import com.dns.taxchina.R;import com.dns.taxchina.service.helper.ModelHelper;import com.dns.taxchina.ui.constant.GetDataModeCostant;import com.dns.taxchina.ui.widget.XListView;/** * @author henzil * @version create time:2014-4-24_下午1:59:19 * @Description 微课程 fragment */public class MicroCourseFragment extends BaseFragment implements XListView.IXListViewListener, GetDataModeCostant {	protected int begin = 1;	protected final static int LIMIT = 10;	private XListView listView;	protected DataJsonAsyncTask asyncTask;	protected DataAsyncTaskPool dataPool;	protected DataServiceHelper dataServiceHelper;	protected ModelHelper jsonHelper;	@Override	protected void initData() {		loadingDialog.setOnKeyListener(new OnKeyListener() {			@Override			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {				if (keyCode == KeyEvent.KEYCODE_BACK) {					if (loadingDialog != null)						loadingDialog.cancel();				}				return true;			}		});		dataPool = new DataAsyncTaskPool();		dataServiceHelper = new DataServiceHelper() {			@Override			public void preExecute() {				if (loadingDialog != null && !loadingDialog.isShowing()) {					loadingDialog.show();				}			}			@Override			public void postExecute(String TAG, Object result, Object... params) {				Log.v("tag", "updateView");				int mode = (Integer) params[0];				updateView(result, mode);			}			@Override			public Object doInBackground(Object... params) {				return null;			}		};		jsonHelper = new ModelHelper(context);	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.micro_course_fragment, container, false);		return view;	}	@Override	protected void initWidgetActions() {		// TODO Auto-generated method stub		initNet();	}	private void initNet() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "getSysMessageList");		reqMap.put("begin", "" + "1");		reqMap.put("limit", "" + LIMIT);		jsonHelper.updateParams(getString(R.string.base_url), reqMap,				"com.dns.taxchina.service.model.MicroCourseModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER_LOCAL, true);		dataPool.execute(asyncTask, LOAD_MODE);	}	@Override	public void onRefresh() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("command", "getSysMessageList");		reqMap.put("begin", "" + "1");		reqMap.put("limit", "" + LIMIT);		jsonHelper.updateParams(getString(R.string.base_url), reqMap,				"com.dns.taxchina.service.model.MicroCourseModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER, true);		dataPool.execute(asyncTask, REFRESH_MODE);	}	@Override	public void onLoadMore() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("command", "getSysMessageList");		reqMap.put("begin", "" + (begin + LIMIT));		reqMap.put("limit", "" + LIMIT);		jsonHelper.updateParams(getString(R.string.base_url), reqMap,				"com.dns.taxchina.service.model.MicroCourseModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER);// 加载更多不需要缓存		dataPool.execute(asyncTask, MORE_MODE);	}	protected void updateView(Object object, int mode) {		if (loadingDialog != null) {			if (loadingDialog.isShowing()) {				loadingDialog.dismiss();			}			loadingDialog = null;		}		if (mode == LOAD_MODE || mode == REFRESH_MODE) {			listView.stopRefresh();		} else if (mode == MORE_MODE) {			listView.stopLoadMore();		}		if (object == null) {			errorData(mode);			return;		}		if (object instanceof ErrorModel) {// 网络连接失败			ErrorModel errorModel = (ErrorModel) object;			errorData(mode);			// TODO 提示出网络错误			Toast.makeText(context, ErrorCodeUtil.convertErrorCode(context, errorModel.getErrorCode()),					Toast.LENGTH_SHORT).show();			return;		}		BaseModel m = (BaseModel) object;// 服务器返回错误		if (m.getCode() > 0) {			// TODO 提示出服务器端错误。			errorData(mode);			Toast.makeText(context, m.getMessage(), Toast.LENGTH_SHORT).show();			return;		}//		MessageListModel messageListModel = (MessageListModel) object;		// TODO 执行一系列 的方法。	}	protected void errorData(int mode) {// 数据加载失败		if (mode == LOAD_MODE) {			// TODO 未完成 网络失败 界面显示，//			errorView.show();		}	}}