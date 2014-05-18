package com.dns.taxchina.service.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetChangeFilter {

	private NetworkInfo mOldNet;

	public NetChangeFilter(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		this.mOldNet = cm.getActiveNetworkInfo();
	}

	public boolean netChangeMobile(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo tNet = cm.getActiveNetworkInfo();

		if (mOldNet == null) {
			if (tNet == null) {
				Log.e("tag", "null ————> null");
				return false;
			}
			if (tNet.getType() == ConnectivityManager.TYPE_MOBILE) {
				Log.e("tag", "null ————> mobile");
				// TODO 
				return true;
			} else if (tNet.getType() == ConnectivityManager.TYPE_WIFI) {
				Log.e("tag", "null ————> wifi");
			} else {
				Log.e("tag", "null ————> " + tNet.getTypeName());
			}
		} else {
			if (tNet == null) {
				if (mOldNet.getType() == ConnectivityManager.TYPE_MOBILE) {
					Log.e("tag", "mobile ————> null");
				} else if (mOldNet.getType() == ConnectivityManager.TYPE_WIFI) {
					Log.e("tag", "wifi ————> null");
				} else {
					Log.e("tag", mOldNet.getTypeName() + " ————> null");
				}
			} else {
				int tOld = mOldNet.getType();
				int tNew = tNet.getType();

				if (tOld != tNew) {
					if (tOld == ConnectivityManager.TYPE_MOBILE && tNew == ConnectivityManager.TYPE_WIFI) {
						Log.e("tag", "mobile ————> wifi");
					} else if (tOld == ConnectivityManager.TYPE_WIFI && tNew == ConnectivityManager.TYPE_MOBILE) {
						Log.e("tag", "wifi ————> mobile");
						// TODO 
						return true;
					} else {
						Log.e("tag", tOld + " ————> " + tNew);
					}
				} else {
					Log.e("tag", "same===============" + mOldNet.getTypeName());
				}
			}
		}
		mOldNet = tNet;
		return false;
	}

}
