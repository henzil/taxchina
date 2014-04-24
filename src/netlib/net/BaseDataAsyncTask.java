package netlib.net;

import netlib.helper.DataServiceHelper;
import android.content.Context;
import android.os.AsyncTask;

public abstract class BaseDataAsyncTask extends AsyncTask<Object, Void, Object> {
	protected DataServiceHelper dataServiceHelper;
	private Object[] params;
	protected String TAG;
	protected Context context;
	protected boolean serverMode;// true:网络模式，false：本地模式

	public BaseDataAsyncTask(String TAG, DataServiceHelper dataServiceHelper) {
		super();
		this.TAG = TAG;
		this.dataServiceHelper = dataServiceHelper;
	}

	@Override
	protected void onPreExecute() {
		if (dataServiceHelper != null) {
			dataServiceHelper.preExecute();
		}
	}

	@Override
	protected Object doInBackground(Object... params) {
		this.params = params;
		Object object = null;
		if (dataServiceHelper != null) {
			object = dataServiceHelper.doInBackground(params);
		}
		if (object == null) {
			object = getData();
		}
		return object;
	}

	@Override
	protected void onPostExecute(Object result) {
		if (dataServiceHelper != null) {
			dataServiceHelper.postExecute(TAG, result, params);
		}
	}

	protected abstract Object getData();

	public boolean isServerMode() {
		return serverMode;
	}

	public void setServerMode(boolean serverMode) {
		this.serverMode = serverMode;
	}

}