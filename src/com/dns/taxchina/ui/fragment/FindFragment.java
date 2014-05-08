package com.dns.taxchina.ui.fragment;import java.util.HashMap;import java.util.List;import netlib.helper.DataServiceHelper;import netlib.model.BaseModel;import netlib.model.ErrorModel;import netlib.net.DataAsyncTaskPool;import netlib.net.DataJsonAsyncTask;import netlib.net.DataMode;import netlib.util.ErrorCodeUtil;import netlib.util.LibIOUtil;import netlib.util.PhoneUtil;import android.content.DialogInterface;import android.content.DialogInterface.OnKeyListener;import android.os.Bundle;import android.util.Log;import android.util.TypedValue;import android.view.Gravity;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.view.ViewGroup.LayoutParams;import android.widget.LinearLayout;import android.widget.TextView;import android.widget.Toast;import com.dns.taxchina.R;import com.dns.taxchina.service.helper.ModelHelper;import com.dns.taxchina.service.model.ConditionModel;import com.dns.taxchina.service.model.FindCourseModel;/** * @author henzil * @version create time:2014-4-24_下午1:59:19 * @Description 找课程fragment */public class FindFragment extends BaseFragment {	protected DataJsonAsyncTask asyncTask;	protected DataAsyncTaskPool dataPool;	protected DataServiceHelper dataServiceHelper;	protected ModelHelper jsonHelper;	private LinearLayout attrContentBox;	private TextView title, back;	@Override	protected void initData() {		loadingDialog.setOnKeyListener(new OnKeyListener() {			@Override			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {				if (keyCode == KeyEvent.KEYCODE_BACK) {					if (loadingDialog != null)						loadingDialog.cancel();				}				return true;			}		});		dataPool = new DataAsyncTaskPool();		jsonHelper = new ModelHelper(context);		dataServiceHelper = new DataServiceHelper() {			@Override			public void preExecute() {				if (loadingDialog != null && !loadingDialog.isShowing()) {					loadingDialog.show();				}			}			@Override			public void postExecute(String TAG, Object result, Object... params) {				Log.v("tag", "updateView");				updateView(result);			}			@Override			public Object doInBackground(Object... params) {				try {					Thread.sleep(1000);				} catch (InterruptedException e) {					e.printStackTrace();				}				return jsonHelper.parseJson(LibIOUtil.convertStreamToStr(getResources().openRawResource(R.raw.find_fragment_json)));				// return null;			}		};	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.find_fragment, container, false);		title = (TextView) view.findViewById(R.id.title_text);		title.setText("找课程");		back = (TextView) view.findViewById(R.id.back_text);		back.setVisibility(View.GONE);		attrContentBox = (LinearLayout) view.findViewById(R.id.attr_content_box);		initNet();		return view;	}	public void initNet() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "6");		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.FindCourseModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER_LOCAL, true);		dataPool.execute(asyncTask);	}	@Override	protected void initWidgetActions() {	}	private void initAttrsView(List<ConditionModel> conditionList) {		if (conditionList != null) {			int margin = (int) (5 * PhoneUtil.getDisplayDensity(context));			final LinearLayout verticalBox = new LinearLayout(context);			LinearLayout.LayoutParams verticalBoxParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);			verticalBoxParams.setMargins(0, 0, 0, (int) (7 * PhoneUtil.getDisplayDensity(context)));			verticalBox.setOrientation(LinearLayout.HORIZONTAL);			verticalBox.setLayoutParams(verticalBoxParams);			final LinearLayout verticalContentBox1 = new LinearLayout(context);			LinearLayout.LayoutParams verticalContentBoxpParams1 = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);			verticalContentBoxpParams1.weight = 1;			verticalContentBoxpParams1.setMargins(0, 0, margin, 0);			verticalContentBox1.setOrientation(LinearLayout.VERTICAL);			verticalContentBox1.setLayoutParams(verticalContentBoxpParams1);			final LinearLayout verticalContentBox2 = new LinearLayout(context);			LinearLayout.LayoutParams verticalContentBoxpParams2 = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);			verticalContentBoxpParams2.weight = 1;			verticalContentBoxpParams2.setMargins(margin, 0, margin, 0);			verticalContentBox2.setOrientation(LinearLayout.VERTICAL);			verticalContentBox2.setLayoutParams(verticalContentBoxpParams2);			final LinearLayout verticalContentBox3 = new LinearLayout(context);			LinearLayout.LayoutParams verticalContentBoxpParams3 = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);			verticalContentBoxpParams3.weight = 1;			verticalContentBoxpParams3.setMargins(margin, 0, 0, 0);			verticalContentBox3.setOrientation(LinearLayout.VERTICAL);			verticalContentBox3.setLayoutParams(verticalContentBoxpParams3);			for (int k = 0, lenth = conditionList.size(); k < lenth; k++) {				String attrContent = conditionList.get(k).getTitle();				final TextView attrText = new TextView(context);				attrText.setText(attrContent);				attrText.setBackgroundResource(R.drawable.condition_btn);				attrText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);				attrText.setId(k);				attrText.setTag(k);				attrText.setTextColor(context.getResources().getColor(R.color.condition_btn_color));				attrText.setGravity(Gravity.CENTER);				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);				params.topMargin = (int) (10 * PhoneUtil.getDisplayDensity(context));				if (k % 3 == 0) {					verticalContentBox1.addView(attrText, params);				} else if (k % 3 == 1) {					verticalContentBox2.addView(attrText, params);				} else if (k % 3 == 2) {					verticalContentBox3.addView(attrText, params);				}				if (lenth <= 1) {					attrText.setSelected(true);				}				attrText.setOnClickListener(new View.OnClickListener() {					@Override					public void onClick(View v) {						// TODO 跳转					}				});			}			verticalBox.addView(verticalContentBox1);			verticalBox.addView(verticalContentBox2);			verticalBox.addView(verticalContentBox3);			attrContentBox.addView(verticalBox);		}	}	protected void updateView(Object object) {		if (loadingDialog != null) {			if (loadingDialog.isShowing()) {				loadingDialog.dismiss();			}			loadingDialog = null;		}//		if (mode == LOAD_MODE || mode == REFRESH_MODE) {//		} else if (mode == MORE_MODE) {//		}		if (object == null) {//			errorData(mode);			return;		}		if (object instanceof ErrorModel) {// 网络连接失败			ErrorModel errorModel = (ErrorModel) object;//			errorData(mode);			// TODO 提示出网络错误			Toast.makeText(context, ErrorCodeUtil.convertErrorCode(context, errorModel.getErrorCode()), Toast.LENGTH_SHORT).show();			return;		}		BaseModel m = (BaseModel) object;// 服务器返回错误		if (m.getResult() > 0) {			// TODO 提示出服务器端错误。//			errorData(mode);			Toast.makeText(context, m.getMessage(), Toast.LENGTH_SHORT).show();			return;		}		FindCourseModel findCourseModel = (FindCourseModel) object;		initAttrsView(findCourseModel.getDataList());	}	protected void errorData(int mode) {// 数据加载失败//		if (mode == LOAD_MODE) {//			// TODO 未完成 网络失败 界面显示，////			errorView.show();//		}	}}