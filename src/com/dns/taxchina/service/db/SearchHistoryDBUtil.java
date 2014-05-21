package com.dns.taxchina.service.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * @author fubiao
 * @version create time:2014-3-20_下午4:17:27
 * @Description 搜索历史数据库工具
 */
public class SearchHistoryDBUtil extends BaseCollectDBUtil {
	
	private static SearchHistoryDBUtil searchHistoryUtil;

	private SearchHistoryDBUtil(Context context, String user) {
		super(context, user);
	}
	
	public static SearchHistoryDBUtil getInstance(Context context, String user){
		if(searchHistoryUtil == null)
			searchHistoryUtil = new SearchHistoryDBUtil(context, user);
		return searchHistoryUtil;
	}
	
	public boolean saveHistory(String history){
		try {
			openWriteableDB();
			ContentValues values = new ContentValues();
			values.put(DATA_HISTORY, history);
			values.put(DATA_TIME, System.currentTimeMillis());
			if (!isRowExisted(writableDatabase, T_SEARCH, DATA_HISTORY + "=?", new String[] { history })) {
				writableDatabase.insert(T_SEARCH, null, values);
			} else {
				writableDatabase.update(T_SEARCH, values, DATA_HISTORY + "=?", new String[] { history });
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeWriteableDB();
		}
		return true;
	}
	
	public List<String> getAllHistories(){
		Cursor c = null;
		List<String> histories = null;
		try {
			openReadableDB();
			c = readableDatabase.query(T_SEARCH, null, null, null, null, null, DATA_TIME + " desc", "20");
			histories = new ArrayList<String>();
			while (c.moveToNext()) {
				histories.add(c.getString(c.getColumnIndex(DATA_HISTORY)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(c != null)
				c.close();
			closeReadableDB();
		}
		return histories;
	}
	
	public boolean clearnHistories(){
		return removeAllEntries(T_SEARCH);
	}

}
