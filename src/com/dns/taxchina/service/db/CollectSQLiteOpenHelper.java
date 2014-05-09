package com.dns.taxchina.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dns.taxchina.service.db.constant.MyCollectDBConstant;

/**
 * @author fubiao
 * @version create time:2014-3-19_上午11:52:58
 * @Description TODO
 */
public class CollectSQLiteOpenHelper extends SQLiteOpenHelper {
	
	public CollectSQLiteOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
		Log.e("", "CollectSQLiteOpenHelper version : " + version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MyCollectDBConstant.NEW_TABLE_DATA);
		db.execSQL(MyCollectDBConstant.NEW_TABLE_SEARCH);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("", "CollectSQLiteOpenHelper onUpgrade :: oldVersion = " + oldVersion + ", newVersion = " + newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + MyCollectDBConstant.T_DATA);
		db.execSQL("DROP TABLE IF EXISTS " + MyCollectDBConstant.T_SEARCH);
		onCreate(db);
	}
}