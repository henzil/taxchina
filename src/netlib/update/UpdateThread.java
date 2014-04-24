package netlib.update;

import java.util.HashMap;

import netlib.model.UpdateModel;
import netlib.util.ResourceUtil;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Handler;

public class UpdateThread extends Thread {

	private Context context;
	private String urlString;
	private HashMap<String, String> params;
	private UpdateImplHelper helper;
	private UpdateServiceImpl updateService;
	private Handler mHandler;

	private Builder builder;
	private ResourceUtil resourceUtil;

	public UpdateThread(Context context, String urlString, HashMap<String, String> params, UpdateImplHelper helper) {
		super();
		this.context = context;
		this.urlString = urlString;
		this.params = params;
		this.helper = helper;
		this.updateService = new UpdateServiceImpl(context);
		this.mHandler = new Handler();
		this.builder = new AlertDialog.Builder(context);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		this.resourceUtil = ResourceUtil.getInstance(context);
	}

	@Override
	public void run() {
		super.run();
		helper.onPreExecute();
		final Object object = updateService.getUpdateModel(urlString, params);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (object != null && object instanceof UpdateModel) {
					if(dataBack != null){
						dataBack.successData((UpdateModel) object);
					}
//					final UpdateModel updateModel = (UpdateModel) object;
//					Log.e("tag", "updateModel.isHaveUpdate() = " + updateModel.isHaveUpdate());
//					if (updateModel.isHaveUpdate()) {
//						builder.setTitle(context.getResources().getString(resourceUtil.getStringId("update_title")));
//						builder.setMessage(updateModel.getDescribe());
//						builder.setPositiveButton(
//								context.getResources().getString(resourceUtil.getStringId("update_ok")),
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int id) {
//										// 弹出提示，显示更新
//										SystemIntentUtil.openBrowser(context, updateModel.getUrl());
//										dialog.cancel();
//									}
//								});
//						builder.setNegativeButton(
//								context.getResources().getString(resourceUtil.getStringId("update_cancel")),
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog, int id) {
//										dialog.cancel();
//									}
//								});
//						builder.create();
//						if (!((Activity) context).isFinishing()) {
//							builder.show();
//						}
//					} else {
//						helper.onPostExecute(object);
//					}
				} else {
					helper.onPostExecute(object);
				}
			}
		});
	}
	
	private DataBack dataBack;
	
	public void setDataBack(DataBack dataBack){
		this.dataBack = dataBack;
	}
	
	public interface DataBack {
		public void successData(UpdateModel updateModel);
	}

}