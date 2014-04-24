package netlib.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class SystemIntentUtil {

	// 访问浏览器
	public static void openBrowser(Context context, String url) {
		try {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
			context.startActivity(intent);
		} catch (Exception e) {
			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				context.startActivity(intent);
			} catch (Exception e1) {
			}
		}
	}

	// 将应用放至后台
	public static void goHome(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);
	}

	// 发送短信
	public static void goSMS(Context context, String tel, String content) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + tel));
		intent.putExtra("sms_body", content);
		context.startActivity(intent);
	}

	// 拨打电话(直接拨打)
	public static void goTel(Context context, String tel, String content) {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	// 拨打电话(显示拨打界面)
	public static void gotoTel(Context context, String tel) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	// 系统分享
	public static void goSysShare(Context context, String title, String content) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, content);
		context.startActivity(Intent.createChooser(intent, title));
	}

	// 发邮件
	public static void goEmail(Context context, String title, String content) {
		goEmail(context, "", title, content, null, null);
	}

	// 发邮件
	public static void goEmail(Context context, String recevier, String title, String content, String filePath, String imgUrl) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { recevier });
		if (!TextUtils.isEmpty(filePath)) {
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
		} else if (!TextUtils.isEmpty(imgUrl)) {
			intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgUrl));
		}
		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.setType("plain/text");
		context.startActivity(intent);
	}
}