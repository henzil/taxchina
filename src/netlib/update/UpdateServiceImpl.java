package netlib.update;

import java.util.HashMap;

import netlib.net.HttpClientUtil;
import netlib.util.ErrorCodeUtil;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UpdateServiceImpl{
	private Context context;

	public UpdateServiceImpl(Context context) {
		super();
		this.context = context;
	}

	public Object getUpdateModel(String urlString, HashMap<String, String> params) {
		String jsonStr = HttpClientUtil.doPostRequest(urlString, params, context);
		Object model = null;
		if (jsonStr != null && !ErrorCodeUtil.isError(jsonStr)) {
			try {
				model = new Gson().fromJson(jsonStr, Class.forName("netlib.model.UpdateModel"));
				return model;
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (model == null) {
			return ErrorCodeUtil.getError(jsonStr);
		}
		return model;
	}

}
