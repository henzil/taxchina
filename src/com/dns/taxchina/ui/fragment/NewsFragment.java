package com.dns.taxchina.ui.fragment;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import netlib.helper.DataServiceHelper;import netlib.model.BaseModel;import netlib.model.ErrorModel;import netlib.net.DataAsyncTaskPool;import netlib.net.DataJsonAsyncTask;import netlib.net.DataMode;import netlib.util.ErrorCodeUtil;import android.content.DialogInterface;import android.content.DialogInterface.OnKeyListener;import android.os.Bundle;import android.util.Log;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.Button;import android.widget.TextView;import android.widget.Toast;import com.dns.taxchina.R;import com.dns.taxchina.service.helper.ModelHelper;import com.dns.taxchina.service.model.BaseItemModel;import com.dns.taxchina.service.model.NewsModel;import com.dns.taxchina.ui.adapter.NewsPagerAdapter;import com.dns.taxchina.ui.constant.GetDataModeCostant;import com.dns.taxchina.ui.shared_preferences.NewsSharedPreferences;import com.dns.taxchina.ui.widget.MainViewPager;import com.dns.taxchina.ui.widget.TitleTabHorizontalScrollView;/** * @author henzil * @version create time:2014-4-24_下午1:59:19 * @Description 咨讯fragment */public class NewsFragment extends BaseFragment implements GetDataModeCostant {	protected DataJsonAsyncTask asyncTask;	protected DataAsyncTaskPool dataPool;	protected DataServiceHelper dataServiceHelper;	protected ModelHelper jsonHelper;	private MainViewPager viewPager;	private NewsPagerAdapter pagerAdapter;	private TextView title, back;	private NewsModel newsModel = null;	private Button left, right;	private int index, max;	public static final int PAGE_SIZE = 10;	private TitleTabHorizontalScrollView scrollView;	@Override	protected void initData() {		loadingDialog.setOnKeyListener(new OnKeyListener() {			@Override			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {				if (keyCode == KeyEvent.KEYCODE_BACK) {					if (loadingDialog != null)						loadingDialog.cancel();				}				return true;			}		});		newsModel = new NewsModel();		dataPool = new DataAsyncTaskPool();		jsonHelper = new ModelHelper(context);		dataServiceHelper = new DataServiceHelper() {			@Override			public void preExecute() {			}			@Override			public void postExecute(String TAG, Object result, Object... params) {				Log.v("tag", "updateView");				updateView(result, (Integer) params[0]);			}			@Override			public Object doInBackground(Object... params) {				// try {				// Thread.sleep(1000);				// } catch (InterruptedException e) {				// e.printStackTrace();				// }				// return				// jsonHelper.parseJson(LibIOUtil.convertStreamToStr(getResources().openRawResource(R.raw.news_fragment_json)));				return null;			}		};	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.news_fragment, container, false);		title = (TextView) view.findViewById(R.id.title_text);		title.setText("咨询");		back = (TextView) view.findViewById(R.id.back_text);		back.setVisibility(View.GONE);		scrollView = (TitleTabHorizontalScrollView) view.findViewById(R.id.scrollView);		viewPager = (MainViewPager) view.findViewById(R.id.mainViewPager);		left = (Button) view.findViewById(R.id.left_btn);		right = (Button) view.findViewById(R.id.right_btn);		viewPager.packConflictViewId("mainScollLayout");		pagerAdapter = new NewsPagerAdapter(getChildFragmentManager(), newsModel, new ArrayList<BaseItemModel>());		viewPager.setAdapter(pagerAdapter);		scrollView.packConflictViewId("mainScollLayout");		scrollView.setViewPager(viewPager);		scrollView.setAnim(true);		scrollView.setOnItemClickListener(new TitleTabHorizontalScrollView.OnItemClickListener() {			@Override			public void clickItem(int postion) {				index = postion;				viewPager.setCurrentItem(postion);			}		});		initNet();		return view;	}	public void initNet() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "4");		reqMap.put("nowPage", 1 + "");		reqMap.put("pageSize", PAGE_SIZE + "");		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.NewsModel");		switch (NewsSharedPreferences.needReRequestData(context)) {		case FIRST_REQUEST:			if (loadingDialog != null && !loadingDialog.isShowing()) {				loadingDialog.show();			}			asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER, true);			dataPool.execute(asyncTask, LOAD_MODE);			break;		case NEXT_REQUEST:			asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.LOCAL);			dataPool.execute(asyncTask, UPDATE_MODE);			break;		default:			break;		}	}	@Override	protected void initWidgetActions() {		left.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				if (index > 0 && index <= max) {					viewPager.setCurrentItem(--index);				}			}		});		right.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				if (index >= 0 && index < max) {					viewPager.setCurrentItem(++index);				}			}		});	}	protected void updateView(Object object, int mode) {		if (loadingDialog != null) {			if (loadingDialog.isShowing()) {				loadingDialog.dismiss();			}		}		if (mode == ONLY_LOAD_MODE) {			return;		}		if (mode == LOAD_MODE || mode == REFRESH_MODE) {		} else if (mode == MORE_MODE) {		}		if (object == null) {			errorData(mode);			return;		}		if (object instanceof ErrorModel) {// 网络连接失败			if (mode == UPDATE_MODE) {				if (loadingDialog != null && !loadingDialog.isShowing()) {					loadingDialog.show();				}				HashMap<String, String> reqMap = new HashMap<String, String>();				reqMap.put("mode", "4");				reqMap.put("nowPage", 1 + "");				reqMap.put("pageSize", PAGE_SIZE + "");				jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.NewsModel");				asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER, true);				dataPool.execute(asyncTask, LOAD_MODE);				return;			}			ErrorModel errorModel = (ErrorModel) object;			errorData(mode);			// TODO 提示出网络错误			Toast.makeText(context, ErrorCodeUtil.convertErrorCode(context, errorModel.getErrorCode()), Toast.LENGTH_SHORT).show();			return;		}		BaseModel m = (BaseModel) object;// 服务器返回错误		if (m.getResult() > 0) {			// TODO 提示出服务器端错误。			errorData(mode);			Toast.makeText(context, m.getMessage(), Toast.LENGTH_SHORT).show();			return;		}		NewsModel newsModel = (NewsModel) object;		if (newsModel.getCategory() != null && !newsModel.getCategory().isEmpty()) {			max = newsModel.getCategory().size();			drawCategoryView(newsModel.getCategory());		}		pagerAdapter.setNewsModel(newsModel);		pagerAdapter.setCategory(newsModel.getCategory());		NewsSharedPreferences.saveLastRequestDate(context);		if (mode == UPDATE_MODE) {			HashMap<String, String> reqMap = new HashMap<String, String>();			reqMap.put("mode", "4");			reqMap.put("nowPage", 1 + "");			reqMap.put("pageSize", PAGE_SIZE + "");			jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.NewsModel");			asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER, true);			dataPool.execute(asyncTask, ONLY_LOAD_MODE);		}	}	private void drawCategoryView(List<BaseItemModel> categoryList) {		// List<BaseItemModel> list = categoryList.getCategoryList();		// if (list.size() <= 0) {		// errorView.updateView(MyType.Empty,		// resourceUtil.getString("data_no_str"));		// errorView.show();		// }		List<String> tits = new ArrayList<String>();		for (BaseItemModel category : categoryList) {			tits.add(category.getTitle());		}		scrollView.setAllTitle(tits);	}	protected void errorData(int mode) {// 数据加载失败		if (mode == LOAD_MODE) {			// TODO 未完成 网络失败 界面显示，			// errorView.show();		}	}	@Override	public void onDestroy() {		super.onDestroy();		if (asyncTask != null) {			asyncTask.cancel(true);		}		if (loadingDialog != null) {			loadingDialog = null;		}	}}