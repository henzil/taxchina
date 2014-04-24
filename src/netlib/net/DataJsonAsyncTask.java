package netlib.net;

import java.util.HashMap;

import netlib.db.DataDBUtil;
import netlib.helper.BaseJsonHelper;
import netlib.helper.DataServiceHelper;
import netlib.util.ErrorCodeUtil;
import android.app.Application;
import android.content.Context;

public class DataJsonAsyncTask extends BaseDataAsyncTask {

	protected BaseJsonHelper jsonHelper;
	protected DataMode dataMode;
	private boolean saveToDB;

	public DataJsonAsyncTask(String TAG, DataServiceHelper dataServiceHelper, BaseJsonHelper jsonHelper, DataMode dataMode, boolean saveToDB) {
		super(TAG, dataServiceHelper);
		this.jsonHelper = jsonHelper;
		this.context = jsonHelper.getContext();
		this.dataMode = dataMode;
		this.saveToDB = saveToDB;
	}

	// 命令模式
	public DataJsonAsyncTask(String TAG, DataServiceHelper dataServiceHelper, BaseJsonHelper jsonHelper, DataMode dataMode) {
		super(TAG, dataServiceHelper);
		this.jsonHelper = jsonHelper;
		this.context = jsonHelper.getContext();
		this.dataMode = dataMode;
		this.saveToDB = false;
	}

	// 命令模式
	public DataJsonAsyncTask(String TAG, DataServiceHelper dataServiceHelper, BaseJsonHelper jsonHelper) {
		this(TAG, dataServiceHelper, jsonHelper, DataMode.SERVER);
	}

	@Override
	protected Object getData() {
		final String urlString = jsonHelper.createReqUrl();
		final HashMap<String, String> params = jsonHelper.createReqParams();
		Context mContext = null;
		if (context instanceof Application) {
			mContext = context;
		} else {
			mContext = context.getApplicationContext();
		}
		Object model = null;
		if (dataMode == DataMode.LOCAL || dataMode == DataMode.LOCAL_SERVER) {
			// 本地模式
			serverMode = false;
			String localStr = DataDBUtil.getInstance(mContext).getList(TAG, urlString + params.toString());
			if (localStr == null || ErrorCodeUtil.isError(localStr)) {
				if (dataMode == DataMode.LOCAL_SERVER) {
					// 网络模式
					String serverStr = HttpClientUtil.doPostRequest(urlString, params, mContext);
					if (serverStr != null && !ErrorCodeUtil.isError(serverStr)) {
						model = jsonHelper.parseJson(serverStr);
					}
					if (model == null) {
						// 本地模式
						return ErrorCodeUtil.getError(localStr);
					} else {
						// 网络模式
						serverMode = true;
						if (saveToDB) {
							DataDBUtil.getInstance(mContext).saveList(TAG, urlString + params.toString(), serverStr);
						}
					}
				} else {
					// 本地模式-读取数据失败
					return ErrorCodeUtil.getError(localStr);
				}
			} else {
				// 本地模式
				return jsonHelper.parseJson(localStr);
			}

		} else if (dataMode == DataMode.SERVER || dataMode == DataMode.SERVER_LOCAL) {
			// 网络模式
			serverMode = true;
			String serverStr = HttpClientUtil.doPostRequest(urlString, params, context);
			if (serverStr != null && !ErrorCodeUtil.isError(serverStr)) {
				model = jsonHelper.parseJson(serverStr);
			}
			if (model == null) {
				if (dataMode == DataMode.SERVER) {
					// 网络模式-网络请求失败
					return ErrorCodeUtil.getError(serverStr);
				} else {
					String localStr = DataDBUtil.getInstance(mContext).getList(TAG, urlString + params.toString());
					// 本地模式-数据读取失败
					if (localStr == null || ErrorCodeUtil.isError(localStr)) {
						// 网络模式
						return ErrorCodeUtil.getError(serverStr);
					}
					// 本地模式
					serverMode = false;
					return jsonHelper.parseJson(localStr);
				}
			} else {
				// 网络模式-存储数据
				if (saveToDB) {
					DataDBUtil.getInstance(mContext).saveList(TAG, urlString + params.toString(), serverStr);
				}
			}

		}
		return model;
	}

	public DataMode getDataMode() {
		return dataMode;
	}

}