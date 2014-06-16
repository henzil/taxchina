package com.dns.taxchina.service.SharedPreferences;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 
 * @author henzil
 * @version create time:2014-4-24_上午10:25:48
 * @Description 登陆成功保存用户信息
 */
public class LoginSharedpreferences {
	@SuppressLint("CommitPrefEdits")
	public static void saveUserInfo(Context context, String userId, String name, String phone, String avatar,
			String iden, String type) {
		// TODO
	}

	// TODO
	
//	public static Map<String, String> getUserInfo(Context context) {
//		Map<String, String> userMap = new HashMap<String, String>();
//		SharedPreferences shaPreferences = context.getSharedPreferences("login", Activity.MODE_PRIVATE);
//		userMap.put("userId", shaPreferences.getString("userId", ""));
//		userMap.put("userName", shaPreferences.getString("userName", ""));
//		userMap.put("userPhone", shaPreferences.getString("userPhone", ""));
//		userMap.put("userIden", shaPreferences.getString("userIden", ""));
//		userMap.put("userType", shaPreferences.getString("userType", ""));
//		return userMap;
//	}
//	
//	public static String getUserId(Context context) {
//		SharedPreferences shaPreferences = context.getSharedPreferences("login", Activity.MODE_PRIVATE);
//		return shaPreferences.getString("userId", null);
//	}
	
	
}
