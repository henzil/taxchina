package com.dns.taxchina.ui.shared_preferences;

import java.text.SimpleDateFormat;
import java.util.Date;

import netlib.util.AppUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

/**
 * @author fubiao
 * @version create time:2014-3-27_下午2:03:44
 * @Description 记录上次微课程请求时间
 */
public class MicroCourseSharedPreferences {

	private static final String MICRO_COURSE_REQUEST_DATE = "micro_course_request_date";
	private static final String DATE = "date";
	
	public enum REQUEST_STATE{
		//第一次进入微课程
		FIRST_REQUEST,
		//下一次进入微课程
		NEXT_REQUEST,
	}
	
	public static void saveLastRequestDate(Context context){
		SharedPreferences sp = context.getSharedPreferences(AppUtil.getPackageName(context) + MICRO_COURSE_REQUEST_DATE, Context.MODE_WORLD_WRITEABLE);
		Editor editor = sp.edit();
		editor.putString(DATE, getNowDate());
		editor.commit();
	}
	
	public static REQUEST_STATE needReRequestData(Context context){
		SharedPreferences sp = context.getSharedPreferences(AppUtil.getPackageName(context) + MICRO_COURSE_REQUEST_DATE, Context.MODE_WORLD_WRITEABLE);
		String date = sp.getString(DATE, "");
		if(TextUtils.isEmpty(date))
			return REQUEST_STATE.FIRST_REQUEST;
		else 
			return REQUEST_STATE.NEXT_REQUEST;
	}
	
	public static void clearHomeCache(Context context){
		SharedPreferences sp = context.getSharedPreferences(AppUtil.getPackageName(context) + MICRO_COURSE_REQUEST_DATE, Context.MODE_WORLD_WRITEABLE);
		sp.edit().clear().commit();
	}

	private static String getNowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
}