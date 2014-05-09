package com.dns.taxchina.service.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dns.taxchina.service.model.BaseItemModel;

/**
 * @author fubiao
 * @version create time:2014-3-19_上午11:56:23
 * @Description 我的收藏数据库操作类
 */
public class CollectDBUtil extends BaseCollectDBUtil {

	private static CollectDBUtil dbUtil;

	private CollectDBUtil(Context context, String imei) {
		super(context, imei);
	}

	public static CollectDBUtil getInstance(Context context, String imei) {
		if (dbUtil == null)
			dbUtil = new CollectDBUtil(context, imei);
		return dbUtil;
	}

	/**
	 * @param courseModel
	 * @return 收藏单个课程
	 */
	public boolean saveCourse(BaseItemModel courseModel) {
		try {
			openWriteableDB();
			insertContent(courseModel);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeWriteableDB();
		}
		return true;
	}

	private void insertContent(BaseItemModel courseModel) {
		ContentValues cv = new ContentValues();
		cv.put(DATA_ID, courseModel.getId());
		cv.put(DATA_TITLE, courseModel.getTitle());
		cv.put(DATA_INFO, courseModel.getInfo());
		cv.put(DATA_IMAGE, courseModel.getImage());
		cv.put(DATA_ISVIDEO, courseModel.isVideo() + "");
		cv.put(DATA_URL, courseModel.getUrl());
		cv.put(DATA_TIME, System.currentTimeMillis());
		if (!isRowExisted(writableDatabase, T_DATA, DATA_ID + "=?", new String[] { courseModel.getId() })) {
			writableDatabase.insert(T_DATA, null, cv);
			Log.d("", "insert courseId = " + courseModel.getId());
		} else {
			writableDatabase.update(T_DATA, cv, DATA_ID + "=?", new String[] { courseModel.getId() });
			Log.d("", "update courseId = " + courseModel.getId());
		}
	}

	/**
	 * 是否已经收藏了该课程
	 * 
	 * @param courseId
	 * @return
	 */
	public boolean isExistCourse(String courseId) {
		try {
			openWriteableDB();
			if (isRowExisted(writableDatabase, T_DATA, DATA_ID + "=?", new String[] { courseId }))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeWriteableDB();
		}
		return false;
	}

	/**
	 * @param courseModels
	 * @return 收藏多个课程
	 */
	public boolean saveCourses(List<BaseItemModel> courseModels) {
		try {
			openWriteableDB();
			writableDatabase.beginTransaction();
			for (BaseItemModel sm : courseModels) {
				insertContent(sm);
			}
			writableDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			writableDatabase.endTransaction();
			closeWriteableDB();
		}
		return true;
	}

	/**
	 * @param courseId
	 * @return 取消收藏单个课程
	 */
	public boolean removeCourse(String courseId) {
		try {
			openWriteableDB();
			writableDatabase.delete(T_DATA, DATA_ID + "=?", new String[] { courseId });
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			closeWriteableDB();
		}
		return true;
	}

	/**
	 * 获取所有收藏课程
	 * 
	 * @return
	 */
	public List<BaseItemModel> getCourses() {
		Cursor c = null;
		List<BaseItemModel> courses = null;
		try {
			openReadableDB();
			c = readableDatabase.query(T_DATA, null, null, null, null, null, null);
			courses = new ArrayList<BaseItemModel>();
			BaseItemModel course = null;
			while (c.moveToNext()) {
				course = new BaseItemModel();
				course.setId(c.getString(c.getColumnIndex(DATA_ID)));
				course.setTitle(c.getString(c.getColumnIndex(DATA_TITLE)));
				course.setInfo(c.getString(c.getColumnIndex(DATA_INFO)));
				course.setImage(c.getString(c.getColumnIndex(DATA_IMAGE)));
				if (c.getString(c.getColumnIndex(DATA_ISVIDEO)).equals("true")) {
					course.setVideo(true);
				} else {
					course.setVideo(false);
				}
				course.setUrl(c.getString(c.getColumnIndex(DATA_URL)));
				courses.add(course);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (c != null)
				c.close();
			closeReadableDB();
		}
		return courses;
	}

	/**
	 * 清除所有收藏课程
	 * 
	 * @return
	 */
	public boolean cleanCourses() {
		return removeAllEntries(T_DATA);
	}
}
