package com.dns.taxchina.ui.fragment;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import netlib.helper.DataServiceHelper;import netlib.model.BaseModel;import netlib.model.ErrorModel;import netlib.net.DataAsyncTaskPool;import netlib.net.DataJsonAsyncTask;import netlib.net.DataMode;import netlib.util.ErrorCodeUtil;import netlib.util.LibIOUtil;import android.os.Bundle;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.Toast;import com.dns.taxchina.R;import com.dns.taxchina.service.helper.ModelHelper;import com.dns.taxchina.service.model.BaseItemModel;import com.dns.taxchina.service.model.NewsModel;import com.dns.taxchina.ui.adapter.IndexPagerAdapter;import com.dns.taxchina.ui.adapter.NewsListAdapter;import com.dns.taxchina.ui.constant.GetDataModeCostant;import com.dns.taxchina.ui.widget.IndexViewPager;import com.dns.taxchina.ui.widget.XListView;/** * @author henzil * @version create time:2014-4-24_下午1:59:19 * @Description 咨讯 item fragment */public class NewsItemFragment extends BaseFragment implements XListView.IXListViewListener, GetDataModeCostant {	private XListView listView;	private NewsListAdapter adapter;	private IndexViewPager viewPager;	protected DataJsonAsyncTask asyncTask;	protected DataAsyncTaskPool dataPool;	protected DataServiceHelper dataServiceHelper;	protected ModelHelper jsonHelper;	private int pageNum, position;	private String categoryId;	private NewsModel newsModel;	public static final int PAGE_SIZE = 20;	public static final String INTENT = "intent";	public static final String CATEGORY_ID = "categoryId";	public static final String POSITION = "position";	public static NewsItemFragment newInstance(NewsModel newsModel, String categoryId, int position) {		NewsItemFragment f = new NewsItemFragment();		Bundle args = new Bundle();		args.putSerializable(INTENT, newsModel);		args.putString(CATEGORY_ID, categoryId);		args.putInt(POSITION, position);		f.setArguments(args);		return f;	}	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		this.newsModel = (NewsModel) getArguments().getSerializable(INTENT);		this.position = getArguments().getInt(POSITION, -1);		this.categoryId = getArguments().getString(CATEGORY_ID);	}	@Override	protected void initData() {//		loadingDialog.setOnKeyListener(new OnKeyListener() {//			@Override//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {//				if (keyCode == KeyEvent.KEYCODE_BACK) {//					if (loadingDialog != null)//						loadingDialog.cancel();//				}//				return true;//			}//		});		dataPool = new DataAsyncTaskPool();		jsonHelper = new ModelHelper(context);		dataServiceHelper = new DataServiceHelper() {			@Override			public void preExecute() {				if (loadingDialog != null && !loadingDialog.isShowing()) {					loadingDialog.show();				}			}			@Override			public void postExecute(String TAG, Object result, Object... params) {				Log.v("tag", "updateView");				updateView(result, (Integer) params[0]);			}			@Override			public Object doInBackground(Object... params) {				try {					Thread.sleep(1000);				} catch (InterruptedException e) {					e.printStackTrace();				}				return jsonHelper.parseJson(LibIOUtil.convertStreamToStr(getResources().openRawResource(R.raw.news_fragment_json)));				// return null;			}		};	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.news_item_fragment, container, false);		View headView = inflater.inflate(R.layout.news_fragment_header_view, null);		viewPager = (IndexViewPager) headView.findViewById(R.id.view_pager);		listView = (XListView) view.findViewById(R.id.x_list_view);		listView.addHeaderView(headView);		adapter = new NewsListAdapter(context, TAG);		listView.setAdapter(adapter);		if (position == 0) {			IndexPagerAdapter indexPagerAdapter = new IndexPagerAdapter(context, getChildFragmentManager(), (ArrayList<BaseItemModel>) newsModel.getHead());			viewPager.setAdapter(indexPagerAdapter);			adapter.refresh(newsModel.getDataList());		} else {			onLoadEvent();		}		return view;	}	@Override	protected void initWidgetActions() {		listView.setXListViewListener(this);	}	// 程序加载数据	private void onLoadEvent() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "4");		reqMap.put("categoryId", categoryId);		reqMap.put("nowPage", 1 + "");		reqMap.put("pageSize", PAGE_SIZE + "");		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.NewsModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER_LOCAL, true);		dataPool.execute(asyncTask, LOAD_MODE);	}	// 下拉刷新加载数据	public void onRefresh() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "4");		reqMap.put("categoryId", categoryId);		reqMap.put("nowPage", 1 + "");		reqMap.put("pageSize", PAGE_SIZE + "");		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.NewsModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER, true);		dataPool.execute(asyncTask, REFRESH_MODE);	}	// 点击更多加载数据	public void onLoadMore() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "4");		reqMap.put("categoryId", categoryId);		reqMap.put("nowPage", pageNum + 1 + "");		reqMap.put("pageSize", PAGE_SIZE + "");		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.NewsModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER);// 加载更多不需要缓存		dataPool.execute(asyncTask, MORE_MODE);	}	protected void updateView(Object object, int mode) {		if (loadingDialog != null) {			if (loadingDialog.isShowing()) {				loadingDialog.dismiss();			}			loadingDialog = null;		}		if (mode == LOAD_MODE || mode == REFRESH_MODE) {			listView.stopRefresh();		} else if (mode == MORE_MODE) {			listView.stopLoadMore();		}		if (object == null) {			errorData(mode);			return;		}		if (object instanceof ErrorModel) {// 网络连接失败			ErrorModel errorModel = (ErrorModel) object;			errorData(mode);			// TODO 提示出网络错误			Toast.makeText(context, ErrorCodeUtil.convertErrorCode(context, errorModel.getErrorCode()), Toast.LENGTH_SHORT).show();			return;		}		BaseModel m = (BaseModel) object;// 服务器返回错误		if (m.getResult() > 0) {			// TODO 提示出服务器端错误。			errorData(mode);			Toast.makeText(context, m.getMessage(), Toast.LENGTH_SHORT).show();			return;		}		NewsModel newsModel = (NewsModel) object;		IndexPagerAdapter indexPagerAdapter = new IndexPagerAdapter(context, getChildFragmentManager(), (ArrayList<BaseItemModel>) newsModel.getHead());		viewPager.setAdapter(indexPagerAdapter);		adapter.refresh(newsModel.getDataList());	}	protected void onRefreshPostExecute(List<BaseItemModel> result, int mode, boolean hasNext) {		if (result.isEmpty()) {//			listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_NO_DATA);//			emptyView(mode);			adapter.refresh(result);		} else {			adapter.refresh(result);			result.clear();			result = null;			if (hasNext) {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_MORE);			} else {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);			}		}	}	protected void onMorePostExecute(List<BaseItemModel> result, int mode, boolean hasNext) {		if (result.isEmpty()) {//			listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);		} else {			adapter.addData(result);			result.clear();			result = null;			if (hasNext) {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_MORE);			} else {//				listView.onBottomRefreshComplete(PullToRefreshListView.BOTTOM_LOAD_FINISH);			}		}	}	protected void errorData(int mode) {// 数据加载失败		if (mode == LOAD_MODE) {			// TODO 未完成 网络失败 界面显示，//			errorView.show();		}	}	@Override	public void onDestroy() {		super.onDestroy();		if (asyncTask != null) {			asyncTask.cancel(true);		}	}}