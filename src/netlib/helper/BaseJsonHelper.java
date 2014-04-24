package netlib.helper;

import java.util.HashMap;

import android.content.Context;

public abstract class BaseJsonHelper {

	protected Context context;
	
	protected String DEVICE_ID = "deviceId";

	public BaseJsonHelper(Context context) {
		this.context = context;
	}

	public abstract String createReqUrl();

	public abstract HashMap<String, String> createReqParams();

	public abstract Object parseJson(String result);

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}