package com.dns.taxchina.service.download;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.dns.taxchina.service.model.DownloadTask;
import com.dns.taxchina.service.model.VideoModel;

public class DownloadTaskDAO {
	// 需要操作的数据库
	private Activity activity;

	public DownloadTaskDAO(Activity activity) {
		this.activity = activity;
	}

	// 添加一条下载信息
	public void add(DownloadTask downloadTask) {
		Log.d("fred", "insert:fileId=" + downloadTask.getFileId() + "---fileLength=" + downloadTask.getFileLength());
		ContentValues values = new ContentValues();
		values.put(VideoContentProvider.FILE_ID, downloadTask.getFileId());
		values.put(VideoContentProvider.FILE_LENGTH, downloadTask.getFileLength());

		values.put(VideoContentProvider.ID, downloadTask.getVideo().getId());
		activity.getContentResolver().insert(VideoContentProvider.CONTENT_URI_TDOWNLOAD_VIDEO_ADD, values);
	}

	// 删除一条下载信息
	public void remove(String fileId) {
		String[] strs = { fileId };
		activity.getContentResolver().delete(VideoContentProvider.CONTENT_URI_TDOWNLOAD_VIDEO_DELETE,
				VideoContentProvider.FILE_ID + "=?", strs);
	}

	// 更新一条下载信息
	public void update(DownloadTask downloadTask) {
		ContentValues values = new ContentValues();
		values.put(VideoContentProvider.FILE_ID, downloadTask.getFileId());
		values.put(VideoContentProvider.FILE_LENGTH, downloadTask.getFileLength());
		values.put(VideoContentProvider.FILE_PATH, downloadTask.getFilePath());
		values.put(VideoContentProvider.VIDEO_ID, downloadTask.getVideo().getId());

		String[] strs = { downloadTask.getFileId() };
		activity.getContentResolver().update(VideoContentProvider.CONTENT_URI_TDOWNLOAD_VIDEO_UPDATE, values,
				VideoContentProvider.FILE_ID + "=?", strs);

	}

	// 获取所有下载信息
	public ArrayList<DownloadTask> findAll() {
		ArrayList<DownloadTask> arrayList = new ArrayList<DownloadTask>();
//		Cursor cursor = database.query("yacc", null, null, null, null,
//				null, null);
		Cursor cursor = activity.managedQuery(VideoContentProvider.CONTENT_URI_TDOWNLOAD_VIDEO_BROWSES, null, null,
				null, null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			DownloadTask downloadTask = new DownloadTask();
			downloadTask.setFileId(cursor.getString(VideoContentProvider.FILE_ID_INDEX));
			downloadTask.setFileLength(cursor.getLong(VideoContentProvider.FILE_LENGTH_INDEX));
			downloadTask.setFilePath(cursor.getString(VideoContentProvider.FILE_PATH_INDEX));
			VideoModel Video = new VideoModel();
			Video.setId(cursor.getString(VideoContentProvider.VIDEO_ID_INDEX));
			downloadTask.setVideo(Video);
			arrayList.add(downloadTask);
		}
		cursor.close();
		return arrayList;
	}

	public DownloadTask findById(String fileId) {
		Cursor cursor = null;

		Uri url = Uri.withAppendedPath(VideoContentProvider.CONTENT_URI_TDOWNLOAD_VIDEO_BROWSE, "" + fileId);
		cursor = activity.managedQuery(url, null, null, null, null);
		while (cursor.moveToNext()) {
			DownloadTask downloadTask = new DownloadTask();
			downloadTask.setFileId(cursor.getString(VideoContentProvider.FILE_ID_INDEX));
			downloadTask.setFileLength(cursor.getLong(VideoContentProvider.FILE_LENGTH_INDEX));
			downloadTask.setFilePath(cursor.getString(VideoContentProvider.FILE_PATH_INDEX));
			VideoModel Video = new VideoModel();
			Video.setId(cursor.getString(VideoContentProvider.VIDEO_ID_INDEX));
			downloadTask.setVideo(Video);
			cursor.close();
			return downloadTask;
		}
		cursor.close();
		return null;
	}

	public boolean hasFindById(String fileId) {
		Cursor cursor = null;
		Uri url = Uri.withAppendedPath(VideoContentProvider.CONTENT_URI_TDOWNLOAD_VIDEO_BROWSE, fileId);
		cursor = activity.managedQuery(url, null, null, null, null);
		while (cursor.moveToNext()) {
			cursor.close();
			return true;
		}
		cursor.close();
		return false;
	}
}
