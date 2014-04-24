package netlib.db;

import netlib.constant.DataSqlDBConstant;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

//单例模式
public class DataDBUtil extends BaseDataDBUtil implements DataSqlDBConstant {
	private static DataDBUtil dataDBUtil;

	private DataDBUtil(Context context) {
		super(context);
	}

	public static DataDBUtil getInstance(Context context) {
		if (dataDBUtil == null) {
			dataDBUtil = new DataDBUtil(context);
		}
		return dataDBUtil;
	}

	public boolean saveList(String TAG, String url, String jsonStr) {
		try {
			openWriteableDB();
			ContentValues values = new ContentValues();
			values.put(DATA_TAG, TAG);
			values.put(DATA_URL, url);
			values.put(DATA_JSON_STR, jsonStr);
			if (!isRowExisted(writableDatabase, T_DATA, DATA_URL_WHERE, new String[] { url + "" })) {
				writableDatabase.insertOrThrow(T_DATA, null, values);
			} else {
				writableDatabase.update(T_DATA, values, DATA_URL_WHERE, new String[] { url + "" });
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeWriteableDB();
		}
	}

	public boolean cleanList() {
		return removeAllEntries(T_DATA);
	}

	public boolean removeList(String TAG) {
		try {
			openWriteableDB();
			writableDatabase.delete(T_DATA, DATA_TAG + "=" + TAG, null);
			Log.i("", "remove data where boardId= " + TAG);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeWriteableDB();
		}
		return true;
	}

	public String getList(String TAG, String url) {
		Cursor c = null;
		try {
			openReadableDB();
			c = readableDatabase.query(T_DATA, null, DATA_URL_WHERE, new String[] { url + "" }, null, null, null);
			Log.i("", "get data from local :");
			if (c.moveToFirst()) {
				Log.i("", "get data from local " + c.getString(3));
				return c.getString(3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
			closeReadableDB();
		}
		return null;
	}

}
