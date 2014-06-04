package com.dns.taxchina.service.db.fileDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import netlib.util.AppUtil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA. User: wangjun Date: 13-4-17 Time: 下午9:28 To
 * change this template use File | Settings | File Templates.
 */
public class SqliteUtil {

	private static SqliteUtil mInstance;

	/**
     *
     */
	private SqliteUtil() {
	}

	/**
	 * @return instance of SQLiteUtil
	 */
	public static SqliteUtil getInstance() {
		if (mInstance == null) {
			mInstance = new SqliteUtil();
		}
		return mInstance;
	}

	private static SQLiteDatabase mSQLiteDatabase;

	/**
	 * open or create a database with the given dbName:
	 * /data/data/packagename/databases/databasefilename
	 * /sdcard/databasefilename
	 * 
	 * @param dbName
	 * @return SQLiteDatabase
	 */
	private SQLiteDatabase openDB(String dbName) {
		if (mSQLiteDatabase == null) {
			File file = new File(dbName);
			if (file.exists() == true) {
				mSQLiteDatabase = SQLiteDatabase.openDatabase(dbName, null, SQLiteDatabase.OPEN_READWRITE);
			} else {
				File datafiledir = file.getParentFile();
				if (!datafiledir.exists()) {
					datafiledir.mkdirs();
				}
				mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbName, null);
			}
		}
		return mSQLiteDatabase;
	}

	/**
	 * close database
	 * 
	 */
	public static void closeDB() {
		if (mSQLiteDatabase != null) {
			mSQLiteDatabase.close();
		}
		mSQLiteDatabase = null;
	}

	/**
	 * delete database
	 * 
	 * @param dbName
	 * @return boolean
	 */
	private static boolean deleteDB(String dbName) {
		File file = new File(dbName);
		if (file.exists() == true) {
			return file.delete();
		}
		return true;
	}

	/**
	 * execute sql command
	 * 
	 * @param dbName
	 * @param sql
	 */
	public void execQuery(String dbName, String sql) {
		SQLiteDatabase db = openDB(dbName);
		db.execSQL(sql);
//		closeDB(db);
	}

	/**
	 * execute sql command and return Cursor
	 * 
	 * @param dbName
	 * @return Cursor
	 */
	public Cursor openQuery(String dbName, String sql) {
		SQLiteDatabase db = openDB(dbName);
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
//		closeDB(db);
		return cursor;
	}

	/**
	 * execute sql command and return Cursor
	 * 
	 * @param dbName
	 * @return Cursor
	 */
	public Cursor openAndQuery(String dbName, String sql) {
		SQLiteDatabase db = openDB(dbName);
		Cursor cursor = db.rawQuery(sql, null);
//		closeDB(db);
		return cursor;
	}

	public Cursor openQuery(String dbName, String tableName, String condStr) {
		SQLiteDatabase db = openDB(dbName);
		Cursor cursor = db.query(tableName, null, condStr, null, null, null, null);
		cursor.moveToFirst();
//		closeDB(db);
		return (cursor);
	}

	public int getRowsCount(Cursor cursor) {
		return cursor.getCount();
	}

	public int getColumnsCount(Cursor cursor) {
		return cursor.getColumnCount();
	}

	public String getColumnNameBy(Cursor cursor, int index) {
		return cursor.getColumnName(index);
	}

	public boolean isBOF(Cursor cursor) {
		return cursor.isBeforeFirst();
	}

	public boolean isEOF(Cursor cursor) {
		return cursor.isAfterLast();
	}

	public boolean moveNext(Cursor cursor) {
		return cursor.moveToNext();
	}

	public String getField(Cursor cursor, int index) {
		return cursor.getString(index);
	}

	public void closeQuery(Cursor cursor) {
		cursor.close();
	}

	public boolean isTableExists(String dbName, String tableName) {
		Cursor cursor = openQuery(dbName, SQL.SQLite_MASTER_TABLE, "(tbl_name='" + tableName + "')");
		int recordCount = cursor.getCount();
		cursor.close();
		return (recordCount > 0);
	}

	public boolean isDatabaseExists(String dbName) {
		return new File(dbName).exists();
	}

	public static void copyDatabase(Context context, String dbName, int resourceId) {
		try {
			// TODO 判断版本号，如果不一致，则删除掉数据库重新拷贝。
			String versionName = AppUtil.getVersionName(context);
			if(!versionName.equals(DBVersionSharedPreferences.getDBVersion(context))){
				deleteDB(dbName);
			}
			if (!(new File(dbName)).exists()) {
				InputStream is = context.getResources().openRawResource(resourceId);
				FileOutputStream fos = new FileOutputStream(dbName);
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				DBVersionSharedPreferences.saveVersionName(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
};
