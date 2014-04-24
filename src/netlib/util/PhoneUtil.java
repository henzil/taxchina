package netlib.util;

import java.lang.reflect.Method;
import java.util.Locale;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

public class PhoneUtil {

	public static String getUserAgent() {
		return getPhoneType();
	}

	/**
	 * 获取手机IMEI码
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (imei == null)
			imei = "";
		return imei;
	}

	/**
	 * 获取手机IMSI码
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (imsi == null)
			imsi = "";
		return imsi;
	}

	/**
	 * 获取手机网络型号
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkOperatorName(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getNetworkOperatorName();
	}

	/**
	 * 获取手机机型:i9250
	 * 
	 * @return
	 */
	public static String getPhoneType() {
		String type = Build.MODEL;
		if (type != null) {
			type = type.replace(" ", "");
		}
		return type.trim();
	}

	public static String getDevice() {
		return Build.DEVICE;
	}

	public static String getProduct() {
		return Build.PRODUCT;
	}

	public static String getType() {
		return Build.TYPE;
	}

	/**
	 * 获取手机操作系统版本名：如2.3.1
	 * 
	 * @return
	 */
	public static String getSDKVersionName() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取手机操作系统版本号：如4
	 * 
	 * @return
	 */
	public static String getSDKVersion() {
		return Build.VERSION.SDK;
	}

	/**
	 * 获取手机操作系统版本号：如4
	 * 
	 * @return
	 */
	public static int getAndroidSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getNativePhoneNumber(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String nativePhoneNumber = telephonyManager.getLine1Number();
		if (nativePhoneNumber == null) {
			nativePhoneNumber = "";
		}
		return nativePhoneNumber;
	}

	/**
	 * 获取屏幕尺寸，如:320x480
	 * 
	 * @param context
	 * @return
	 */
	public static String getResolution(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels + "x" + dm.heightPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisplayWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisplayHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取屏幕密度
	 * 
	 * @param context
	 * @return
	 */
	public static float getDisplayDensity(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.density;
	}

	/**
	 * 获取手机语言
	 * 
	 * @return
	 */
	public static String getPhoneLanguage() {
		String language = Locale.getDefault().getLanguage();
		if (language == null) {
			language = "";
		}
		return language;
	}

	/**
	 * 获取手机MAC地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info == null)
			return "";
		return info.getMacAddress();
	}

	/**
	 * 获取基带版本
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String getBaseand() {
		try {
			Class cl = Class.forName("android.os.SystemProperties");
			Object invoker = cl.newInstance();
			Method m = cl.getMethod("get", new Class[] { String.class, String.class });
			Object result = m.invoke(invoker, new Object[] { "gsm.version.baseband", "no message" });
			return result.toString();
		} catch (Exception e) {
		}
		return "";
	}

	public static int getCacheSize(Context context) {
		return 1024 * 1024 * ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 8;
	}

	/**
	 * 获取当前分辨率下指定单位对应的像素大小（根据设备信息） px,dip,sp -> px
	 * 
	 * @param context
	 * @param unit
	 * @param size
	 * @return
	 */
	public static int getRawSize(Context context, int unit, float size) {
		Resources resources;
		if (context == null) {
			resources = Resources.getSystem();
		} else {
			resources = context.getResources();
		}
		return (int) TypedValue.applyDimension(unit, size, resources.getDisplayMetrics());
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
