package com.dns.taxchina.ui.fragment;import java.util.HashMap;import netlib.helper.DataServiceHelper;import netlib.model.BaseModel;import netlib.model.ErrorModel;import netlib.net.DataAsyncTaskPool;import netlib.net.DataJsonAsyncTask;import netlib.util.AppUtil;import netlib.util.ErrorCodeUtil;import netlib.util.SettingUtil;import netlib.util.SystemIntentUtil;import android.app.Activity;import android.app.AlertDialog;import android.content.BroadcastReceiver;import android.content.Context;import android.content.DialogInterface;import android.content.DialogInterface.OnKeyListener;import android.content.Intent;import android.content.IntentFilter;import android.net.wifi.WifiManager;import android.os.Bundle;import android.view.KeyEvent;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.Button;import android.widget.CheckBox;import android.widget.CompoundButton;import android.widget.CompoundButton.OnCheckedChangeListener;import android.widget.TextView;import android.widget.Toast;import com.dns.taxchina.R;import com.dns.taxchina.service.download.DownloadTaskManager;import com.dns.taxchina.service.helper.ModelHelper;import com.dns.taxchina.service.model.UpdateModel;import com.dns.taxchina.ui.CenterDetailActivity;import com.dns.taxchina.ui.CourseCollectionActivity;import com.dns.taxchina.ui.StudyRecordActivity;import com.dns.taxchina.ui.constant.LoginBroadcastConstant;import com.dns.taxchina.ui.util.LoginUtil;/** * @author henzil * @version create time:2014-4-24_下午1:59:19 * @Description 学员中心 fragment */public class CenterFragment extends BaseFragment {	protected DataJsonAsyncTask asyncTask;	protected DataAsyncTaskPool dataPool;	protected DataServiceHelper dataServiceHelper;	protected ModelHelper jsonHelper;	private TextView title, back, userName;	private String packageName;	private CheckBox wifiBtn;	private Button studyRecord, collection, activationBtn, suggestionBtn, aboutBtn, contanctBtn, updateBtn, loginOut;	private LoginBroadReciever loginBroadReciever;	@Override	protected void initData() {		packageName = AppUtil.getPackageName(context);		IntentFilter filter = new IntentFilter(packageName + LoginBroadcastConstant.LOGIN_INTENT_FILTER);		loginBroadReciever = new LoginBroadReciever();		context.registerReceiver(loginBroadReciever, filter);		loadingDialog.setOnKeyListener(new OnKeyListener() {			@Override			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {				if (keyCode == KeyEvent.KEYCODE_BACK) {					if (loadingDialog != null)						loadingDialog.cancel();				}				return true;			}		});		dataPool = new DataAsyncTaskPool();		jsonHelper = new ModelHelper(context);		dataServiceHelper = new DataServiceHelper() {			@Override			public void preExecute() {				if (loadingDialog != null && !loadingDialog.isShowing()) {					loadingDialog.show();				}			}			@Override			public void postExecute(String TAG, Object result, Object... params) {				updateView(result);			}			@Override			public Object doInBackground(Object... params) {				return null;			}		};	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.center_fragment, container, false);		title = (TextView) view.findViewById(R.id.title_text);		title.setText("学员中心");		back = (TextView) view.findViewById(R.id.back_text);		back.setVisibility(View.GONE);		studyRecord = (Button) view.findViewById(R.id.study_record_btn);		collection = (Button) view.findViewById(R.id.course_collection_btn);		activationBtn = (Button) view.findViewById(R.id.activation_btn);		suggestionBtn = (Button) view.findViewById(R.id.suggestion_btn);		aboutBtn = (Button) view.findViewById(R.id.about_us_btn);		contanctBtn = (Button) view.findViewById(R.id.contact_us_btn);		wifiBtn = (CheckBox) view.findViewById(R.id.wifi_checkBox);		userName = (TextView) view.findViewById(R.id.user_text);		updateBtn = (Button) view.findViewById(R.id.update_btn);		loginOut = (Button) view.findViewById(R.id.login_out_btn);		loginOut.setText(LoginUtil.isLogin(context) ? "退出登录" : "登录");		if (LoginUtil.isLogin(context)) {			userName.setVisibility(View.VISIBLE);			userName.setText(LoginUtil.getUserName(context));		} else {			userName.setVisibility(View.GONE);		}		getWifiSetting();		return view;	}	public void updateNet() {		HashMap<String, String> reqMap = new HashMap<String, String>();		reqMap.put("mode", "10");		reqMap.put("versionName", AppUtil.getVersionName(context));		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.UpdateModel");		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper);		dataPool.execute(asyncTask);	}	@Override	protected void initWidgetActions() {		studyRecord.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				Intent intent = new Intent(context, StudyRecordActivity.class);				startActivity(intent);			}		});		collection.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				Intent intent = new Intent(context, CourseCollectionActivity.class);				startActivity(intent);			}		});		activationBtn.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				if (LoginUtil.isLogin(context)) {					Intent intent = new Intent(context, CenterDetailActivity.class);					intent.putExtra(CenterDetailActivity.DETAIL_TYPE, CenterDetailActivity.ACTIVATION_TYPE);					startActivity(intent);				} else {					LoginUtil.gotoLogin(context);				}			}		});		suggestionBtn.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				if (LoginUtil.isLogin(context)) {					Intent intent = new Intent(context, CenterDetailActivity.class);					intent.putExtra(CenterDetailActivity.DETAIL_TYPE, CenterDetailActivity.SUGGESTION_TYPE);					startActivity(intent);				} else {					LoginUtil.gotoLogin(context);				}			}		});		aboutBtn.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				Intent intent = new Intent(context, CenterDetailActivity.class);				intent.putExtra(CenterDetailActivity.DETAIL_TYPE, CenterDetailActivity.ABOUT_US_TYPE);				startActivity(intent);			}		});		contanctBtn.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				Intent intent = new Intent(context, CenterDetailActivity.class);				intent.putExtra(CenterDetailActivity.DETAIL_TYPE, CenterDetailActivity.CONTANCT_US_TYPE);				startActivity(intent);			}		});		wifiBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {			@Override			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {				if (isChecked) {					new AlertDialog.Builder(context).setCancelable(false).setTitle(context.getString(R.string.wifi_tip))							.setMessage(context.getString(R.string.wifi_tip_string))							.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {								@Override								public void onClick(DialogInterface dialog, int which) {								}							}).create().show();				}				SettingUtil.setWifiDoSomeThing(context, isChecked);				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);				if (!isChecked && !wifiManager.isWifiEnabled()) {					DownloadTaskManager.getInstance((Activity) context).stop();				}			}		});		updateBtn.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				updateNet();			}		});		loginOut.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				if (LoginUtil.isLogin(context)) {					new AlertDialog.Builder(context).setCancelable(false).setTitle(context.getString(R.string.login_out_tip))							.setMessage(context.getString(R.string.login_out_string))							.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {								@Override								public void onClick(DialogInterface dialog, int which) {									LoginUtil.delUser(context);									Intent intent = new Intent(packageName + LoginBroadcastConstant.LOGIN_INTENT_FILTER);									Bundle bundle = new Bundle();									bundle.putInt(LoginBroadcastConstant.IS_LOGIN_BUNDLE_KEY, LoginBroadcastConstant.LOGIN_FAIL_TAG);									intent.putExtras(bundle);									context.sendBroadcast(intent);								}							}).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {								@Override								public void onClick(DialogInterface dialog, int which) {								}							}).create().show();				} else {					LoginUtil.gotoLogin(context);				}			}		});	}	private void getWifiSetting() {		// 得到 wifi模式下显示下载的设置，使用SharedPreferences		wifiBtn.setChecked(SettingUtil.getWifiDoSomeThing(context));	}	// 登录结束后操作广播	public class LoginBroadReciever extends BroadcastReceiver {		@Override		public void onReceive(Context context, Intent intent) {			Bundle bundle = intent.getExtras();			int isLoginBundle = bundle.getInt(LoginBroadcastConstant.IS_LOGIN_BUNDLE_KEY, -1);			switch (isLoginBundle) {			case LoginBroadcastConstant.LOGIN_SUCCESS_TAG:				loginOut.setText("退出登录");				userName.setVisibility(View.VISIBLE);				userName.setText(LoginUtil.getUserName(context));				break;			case LoginBroadcastConstant.LOGIN_FAIL_TAG:				loginOut.setText("登录");				userName.setVisibility(View.GONE);				break;			default:				break;			}		}	}	protected void updateView(Object object) {		if (loadingDialog != null) {			if (loadingDialog.isShowing()) {				loadingDialog.dismiss();			}		}		// if (mode == LOAD_MODE || mode == REFRESH_MODE) {		// } else if (mode == MORE_MODE) {		// }		if (object == null) {			// errorData(mode);			return;		}		if (object instanceof ErrorModel) {// 网络连接失败			ErrorModel errorModel = (ErrorModel) object;			// errorData(mode);			// TODO 提示出网络错误			Toast.makeText(context, ErrorCodeUtil.convertErrorCode(context, errorModel.getErrorCode()), Toast.LENGTH_SHORT).show();			return;		}		BaseModel m = (BaseModel) object;// 服务器返回错误		if (m.getResult() > 0) {			// TODO 提示出服务器端错误。			// errorData(mode);			Toast.makeText(context, m.getMessage(), Toast.LENGTH_SHORT).show();			return;		}		final UpdateModel updateModel = (UpdateModel) object;		if (updateModel.isUpdate()) {			// 更新提示框			new AlertDialog.Builder(context).setCancelable(false).setTitle(context.getString(R.string.tip))					.setMessage(String.format(context.getString(R.string.sure_to_update), updateModel.getDescribe()))					.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {						@Override						public void onClick(DialogInterface dialog, int which) {							// 跳转到浏览器							SystemIntentUtil.openBrowser(context, updateModel.getUrl());						}					}).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {						@Override						public void onClick(DialogInterface dialog, int which) {						}					}).create().show();		}	}	@Override	public void onDestroy() {		super.onDestroy();		if (asyncTask != null) {			asyncTask.cancel(true);		}		if (loadingDialog != null) {			loadingDialog = null;		}		context.unregisterReceiver(loginBroadReciever);	}}