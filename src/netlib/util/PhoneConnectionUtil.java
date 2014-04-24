package netlib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class PhoneConnectionUtil {

	// 查询网络是否可用
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

//	// 查询网络状态
//	/**
//	 * 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap or wifi
//	 * @param context
//	 * @return
//	 */
//	public static String getNetworkType(Context context) {
//		ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo info = cManager.getActiveNetworkInfo();
//		if (info != null && info.isAvailable()) {
//			String typeName = info.getTypeName().toLowerCase(); // WIFI/MOBILE
//			if (typeName.equals("wifi")) {
//				typeName = "wifi";
//			} else {
//				typeName = info.getExtraInfo().toLowerCase(); // 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
//			}
//			return typeName;
//		} else {
//			return "wifi not available";
//		}
//	}

//	// 查询WIFI动态
//	public static WifiInfo getWifiStatus(Context context) {
//		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//		return wifiInfo;
//	}

//	//
//	public static String getAccessPointType(Context context) {
//		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
//		if (netWorkInfo.getTypeName().equalsIgnoreCase("MOBILE")) {
//			return netWorkInfo.getExtraInfo();
//		} else {
//			return null;
//		}
//	}

//	// 手机状态
//	public static boolean isMobileType(Context context) {
//		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (manager == null)
//			return false;
//		NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
//		if (netWorkInfo == null)
//			return false;
//		if (netWorkInfo.getTypeName().equalsIgnoreCase("mobile")) {
//			return true;
//		} else {
//			return false;
//		}
//	}

//	// 是否为中国移动Wap方式
//	public static boolean isCmwap(Context context) {
//		if (!isConnectChinaMobile(context) || !isMobileType(context))
//			return false;
//
//		String currentApnProxy = getCurrentApnProxy(context);
//		if (currentApnProxy == null) {
//			return false;
//		}
//		if (currentApnProxy.equals("10.0.0.172") || currentApnProxy.equals("010.000.000.172")) {
//			return true;
//		} else {
//			return false;
//		}
//	}

//	// 是否为中国联通Wap方式
//	public static boolean isUniwap(Context context) {
//		if (!isConnectChinaUnicom(context) || !isMobileType(context))
//			return false;
//
//		String currentApnProxy = getCurrentApnProxy(context);
//		if (currentApnProxy == null) {
//			return false;
//		}
//		if (currentApnProxy.equals("10.0.0.172") || currentApnProxy.equals("010.000.000.172")) {
//			return true;
//		} else {
//			return false;
//		}
//	}

//	//获取当前APN代理
//	public static String getCurrentApnProxy(Context context) {
//		Cursor c = null;
//		try {
//			Uri uri = Uri.parse("content://telephony/carriers/preferapn");
//			c = context.getContentResolver().query(uri, null, null, null, null);
//			if (c != null && c.moveToFirst()) {
//				return c.getString(c.getColumnIndex("proxy"));
//			}
//		} finally {
//			if (c != null)
//				c.close();
//		}
//		return null;
//	}

//	//获取当前代理IP
//	public static String getProxyIp(String apnId, Context context) {
//		if (apnId == null)
//			return null;
//		Cursor c = null;
//		try {
//			Uri uri = Uri.parse("content://telephony/carriers");
//			c = context.getContentResolver().query(uri, null, null, null, null);
//			while (c != null && c.moveToNext()) {
//				// APN id
//				String id = c.getString(c.getColumnIndex("_id"));
//				if (apnId.trim().equals(id)) {
//					return c.getString(c.getColumnIndex("proxy"));
//				}
//			}
//		} finally {
//			if (c != null)
//				c.close();
//		}
//		return null;
//	}

//	// 中国移动
//	public static boolean isConnectChinaMobile(Context context) {
//		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		String operator = telManager.getSimOperator();
//		if (operator != null) {
//			if (operator.startsWith("46000") || operator.startsWith("46002")) {
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}

//	// 中国联通
//	public static boolean isConnectChinaUnicom(Context context) {
//		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		String operator = telManager.getSimOperator();
//		if (operator != null) {
//			if (operator.startsWith("46001")) {
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}

//	// 中国电信
//	public static boolean isConnectChinaTelecom(Context context) {
//		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		String operator = telManager.getSimOperator();
//		if (operator != null) {
//			if (operator.startsWith("46003")) {
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}

}
