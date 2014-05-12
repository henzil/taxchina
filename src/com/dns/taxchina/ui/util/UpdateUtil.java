package com.dns.taxchina.ui.util;import java.util.HashMap;import netlib.net.HttpClientUtil;import netlib.util.AppUtil;import netlib.util.ErrorCodeUtil;import netlib.util.SystemIntentUtil;import android.app.Activity;import android.app.AlertDialog;import android.content.DialogInterface;import android.os.Handler;import com.dns.taxchina.R;import com.dns.taxchina.service.helper.ModelHelper;import com.dns.taxchina.service.model.UpdateModel;import com.google.gson.Gson;/** * @author fubiao * @version create time:2014-5-5_下午1:51:55 * @Description 更新工具类 */public class UpdateUtil {	private Activity context;	private ModelHelper jsonHelper;	private Handler updateHandler;	public UpdateUtil(Activity activity) {		this.context = activity;		jsonHelper = new ModelHelper(context);		updateHandler = new Handler();	}	public void doUpdateCheck() {		new Thread(new Runnable() {			@Override			public void run() {				HashMap<String, String> params = new HashMap<String, String>();				params.put("mode", "9");				params.put("versionName", AppUtil.getVersionName(context));				String serverStr = HttpClientUtil.doPostRequest(context.getString(R.string.base_url), params, context);				if (serverStr != null && !ErrorCodeUtil.isError(serverStr)) {					final UpdateModel model = (UpdateModel) new Gson().fromJson(serverStr, UpdateModel.class);					updateHandler.post(new Runnable() {						@Override						public void run() {							getUpdateMesSuc(model);						}					});				}			}		}).start();	}	private void getUpdateMesSuc(final UpdateModel model) {		if (!context.isFinishing() && model != null && model.getResult() == 0 && model.isUpdate()) {			// 更新提示框			new AlertDialog.Builder(context).setCancelable(false).setTitle(context.getString(R.string.tip))					.setMessage(String.format(context.getString(R.string.sure_to_update), model.getDescribe()))					.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {						@Override						public void onClick(DialogInterface dialog, int which) {							// 跳转到浏览器							SystemIntentUtil.openBrowser(context, model.getUrl());						}					}).setNegativeButton(context.getString(R.string.share_cancel_btn), new DialogInterface.OnClickListener() {						@Override						public void onClick(DialogInterface dialog, int which) {						}					}).create().show();		}	}}