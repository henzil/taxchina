package com.dns.taxchina.ui;

import java.util.HashMap;

import netlib.helper.DataServiceHelper;
import netlib.model.BaseModel;
import netlib.model.ErrorModel;
import netlib.net.DataAsyncTaskPool;
import netlib.net.DataJsonAsyncTask;
import netlib.util.ErrorCodeUtil;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dns.taxchina.R;
import com.dns.taxchina.service.helper.ModelHelper;
import com.dns.taxchina.service.model.LoginResultModel;
import com.dns.taxchina.ui.util.LoginUtil;

/**
 * @author fubiao
 * @version create time:2014-5-8_下午5:14:03
 * @Description 登录 activity
 */
public class LoginActivity extends BaseActivity {

	protected DataJsonAsyncTask asyncTask;
	protected DataAsyncTaskPool dataPool;
	protected DataServiceHelper dataServiceHelper;

	protected ModelHelper jsonHelper;

	private TextView title, back;
	private String userName, password;
	private EditText userNameEdit, pswdEdit;
	private Button login;

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

		dataPool = new DataAsyncTaskPool();
		jsonHelper = new ModelHelper(LoginActivity.this);
		dataServiceHelper = new DataServiceHelper() {

			@Override
			public void preExecute() {
				if (loadingDialog != null && !loadingDialog.isShowing()) {
					loadingDialog.show();
				}
			}

			@Override
			public void postExecute(String TAG, Object result, Object... params) {
				Log.v("tag", "updateView");
				updateView(result);
			}

			@Override
			public Object doInBackground(Object... params) {

//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return jsonHelper.parseJson(LibIOUtil.convertStreamToStr(getResources().openRawResource(R.raw.login_activity_json)));
				return null;
			}
		};
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.login_activity);
		title = (TextView) findViewById(R.id.title_text);
		back = (TextView) findViewById(R.id.back_text);
		userNameEdit = (EditText) findViewById(R.id.user_name_edit);
		pswdEdit = (EditText) findViewById(R.id.pswd_edit);
		login = (Button) findViewById(R.id.login_btn);
		title.setText("登录");
	}

	@Override
	protected void initWidgetActions() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				userName = userNameEdit.getText().toString().trim();
				password = pswdEdit.getText().toString().trim();
				if (TextUtils.isEmpty(userName)) {
					Toast.makeText(LoginActivity.this, "请输入您的用户名！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(LoginActivity.this, "请输入您的密码！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!LoginUtil.validatorString(userName)) {
					Toast.makeText(LoginActivity.this, "您的用户名格式不正确！", Toast.LENGTH_SHORT).show();
					return;
				}
				doNet();
			}
		});
	}

	public void doNet() {
		HashMap<String, String> reqMap = new HashMap<String, String>();
		reqMap.put("mode", "8");
		reqMap.put("userName", userName);
		reqMap.put("password", password);
		jsonHelper.updateParams(getString(R.string.base_url), reqMap, "com.dns.taxchina.service.model.LoginResultModel");
		asyncTask = new DataJsonAsyncTask(TAG, dataServiceHelper, jsonHelper);
		dataPool.execute(asyncTask);
	}

	protected void updateView(Object object) {
		if (loadingDialog != null) {
			if (loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
		}
//		if (mode == LOAD_MODE || mode == REFRESH_MODE) {
//		} else if (mode == MORE_MODE) {
//		}

		if (object == null) {
//			errorData(mode);
			return;
		}
		if (object instanceof ErrorModel) {// 网络连接失败
			ErrorModel errorModel = (ErrorModel) object;
//			errorData(mode);
			// TODO 提示出网络错误
			Toast.makeText(LoginActivity.this, ErrorCodeUtil.convertErrorCode(LoginActivity.this, errorModel.getErrorCode()), Toast.LENGTH_SHORT).show();
			return;
		}
		BaseModel m = (BaseModel) object;// 服务器返回错误
		if (m.getResult() > 0) {
			// TODO 提示出服务器端错误。
//			errorData(mode);
			Toast.makeText(LoginActivity.this, m.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}

		LoginResultModel loginResultModel = (LoginResultModel) object;
		LoginUtil.saveUserId(LoginActivity.this, loginResultModel.getUserId());
		LoginUtil.saveUserName(LoginActivity.this, userName);
		finish();
	}

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