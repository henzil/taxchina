package com.dns.taxchina.service.db.fileDB;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import netlib.util.LibIOUtil;
import android.content.Context;
import android.database.Cursor;

import com.dns.taxchina.service.model.InternalVideoModel;
import com.dns.taxchina.service.model.PVideoModel;
import com.dns.taxchina_pad.R;

/**
 * Created with IntelliJ IDEA. User: wangjun Date: 13-4-17 Time: 下午9:33 To
 * change this template use File | Settings | File Templates. 此类主要是 操作数据库
 */
public class ManagerSqliteDao {

	private Context context;

	public ManagerSqliteDao(Context context) {
		// copyDatabase
		SqliteUtil.copyDatabase(context, LibIOUtil.getVideoPath(context) + SQL.SQLITE_DATABASE_NAME,
				R.raw.internal_video);
		this.context = context;
	}

	public List<PVideoModel> getPVideoModel() {

		List<PVideoModel> dataList = new ArrayList<PVideoModel>();
		Cursor cursor = SqliteUtil.getInstance().openQuery(LibIOUtil.getVideoPath(context) + SQL.SQLITE_DATABASE_NAME,
				SQL.SQL_P_VIDEO_SELECT);
		do {
			PVideoModel model = new PVideoModel();
			model.setpId(cursor.getString(cursor.getColumnIndex(SQL.P_ID)));
			model.setpTitle(cursor.getString(cursor.getColumnIndex(SQL.P_TITLE)));
			model.setCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex(SQL.COUNT))));
			dataList.add(model);
		} while (cursor.moveToNext());
		cursor.close();
		return dataList;
	}

	// 得到一级目录
	public List<InternalVideoModel> getInternalVideoListByPId(String pId) {
		List<InternalVideoModel> dataList = new ArrayList<InternalVideoModel>();
		String sql = MessageFormat.format(SQL.SQL_VIDEO_SELECT, "'" + pId + "'");
		Cursor cursor = SqliteUtil.getInstance().openQuery(LibIOUtil.getVideoPath(context) + SQL.SQLITE_DATABASE_NAME,
				sql);
		do {
			InternalVideoModel model = new InternalVideoModel();
			model.setId(cursor.getString(cursor.getColumnIndex(SQL.ID)));
			model.setTitle(cursor.getString(cursor.getColumnIndex(SQL.TITLE)));
			model.setVideoName(cursor.getString(cursor.getColumnIndex(SQL.VIDEO_NAME)));
			model.setSize(cursor.getString(cursor.getColumnIndex(SQL.SIZE)));
			dataList.add(model);
		} while (cursor.moveToNext());
		cursor.close();
		return dataList;
	}

}
