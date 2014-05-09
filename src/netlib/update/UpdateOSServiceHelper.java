package netlib.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import netlib.model.UpdateModel;
import netlib.util.AppUtil;
import netlib.util.DataUtil;
import netlib.util.LibIOUtil;
import netlib.util.ResourceUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dns.taxchina.R;


public class UpdateOSServiceHelper {

	private static Context mContext;

	// 提示语
	private static String updateMsg = "";

	// 返回的安装包url
	private static String apkUrl = "";

	private static Dialog noticeDialog;

	private static Dialog downloadDialog;
	/* 下载包安装路径 */
	private static final String savePath = Environment.getExternalStorageDirectory() + "/community/";

	private static final String saveFileName = savePath + "UpdateDemoRelease.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private static ProgressBar mProgress;

	private static final int DOWN_UPDATE = 1;

	private static final int DOWN_OVER = 2;

	private static final String SAVE_DATA = "saveData";

	private static int progress;

	private static Thread downLoadThread;

	private static boolean interceptFlag = false;

	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				downloadDialog.dismiss();
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public static void checkUpdate(Context context, String urlString, HashMap<String, String> params) {
		checkUpdate(context, urlString, params, false);
	}

	public static void checkUpdate(Context context, String urlString, HashMap<String, String> params,
			boolean isForcedCheck) {
		mContext = context;
		checkUpdate(context, urlString, params, isForcedCheck, new UpdateImplHelper() {

			@Override
			public void onPreExecute() {

			}

			@Override
			public void onPostExecute(Object object) {

			}
		});
	}

	public static void checkUpdate(Context context, String urlString, HashMap<String, String> params,
			final boolean isForcedCheck, UpdateImplHelper helper) {
		UpdateThread updateThread = new UpdateThread(context, urlString, params, helper);
		updateThread.setDataBack(new UpdateThread.DataBack() {

			@Override
			public void successData(UpdateModel updateModel) {
				if (!updateModel.getVersion().equals(AppUtil.getVersionName(mContext))) {
					apkUrl = updateModel.getUrl();
//					int type = updateModel.getType();
//					if (type == 1) {
//						// 强制更新
//						showUpdateDialog();
//					} else if (type == 0) {
//						// 不强制更新
//						if(isForcedCheck){
//							showNoticeDialog();
//						} else{
//							if (isShowDialog()) {
//								showNoticeDialog();
//							}
//						}
//					}
					showNoticeDialog();
				} else {
					// 提示用户，当前已是最新版本，。
					if (isForcedCheck) {
						Toast.makeText(mContext, "当前已是最新的版本", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		updateThread.start();
//		new UpdateThread(context, urlString, params, helper).start();
	}

	// 获取是否今天更新数据
	private static boolean isShowDialog() {
		boolean flage = false;
		SharedPreferences sp = mContext.getSharedPreferences("updateVersionData", 0);
		String updateData = sp.getString(SAVE_DATA, null);
		if (updateData == null || !updateData.equalsIgnoreCase(DataUtil.getStringMMDDDateShort())) {
			flage = true;
		}
		// 存入数据
		Editor editor = sp.edit();
		editor.putString(SAVE_DATA, DataUtil.getStringMMDDDateShort());
		editor.commit();
		return flage;
	}

	private static void saveDialog() {
		SharedPreferences sp = mContext.getSharedPreferences("updateVersionData", 0);
		Editor editor = sp.edit();
		editor.putString(SAVE_DATA, null);
		editor.commit();
	}

	// 强制更新 界面
	private static void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		builder.setCancelable(false);
		if (!isWifi()) {
			builder.setMessage(ResourceUtil.getInstance(mContext).getString("update_not_wifi_notice"));
		} else {
			builder.setMessage(updateMsg);
		}
		builder.setPositiveButton("更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("退出", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				((Activity) mContext).finish();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private static void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		if (!isWifi()) {
			builder.setMessage(ResourceUtil.getInstance(mContext).getString("update_not_wifi_notice"));
		} else {
			builder.setMessage(updateMsg);
		}
		builder.setPositiveButton("下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}

	private static void showDownloadDialog() {
		saveDialog();
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setCancelable(false);
		builder.setTitle("软件版本更新");
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);
		builder.setView(v);
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();
	}

	private static Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);

				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				LibIOUtil.createFileDir(savePath);
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) ((float) count * 100 / length);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param url
	 */

	private static void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param url
	 */
	private static void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
		((Activity) mContext).finish();
	}

	private static boolean isWifi() {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		Log.e("tag", "wifiManager.isWifiEnabled() = " + wifiManager.isWifiEnabled());
		return wifiManager.isWifiEnabled();
	}

}
