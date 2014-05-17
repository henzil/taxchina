package com.dns.taxchina.service.download;import java.util.ArrayList;import android.app.Activity;import android.content.ContentValues;import android.database.Cursor;import android.net.Uri;import android.support.v4.content.CursorLoader;import android.util.Log;import com.dns.taxchina.service.model.VideoModel;/** * @author henzil * @version create time:2014-5-4_下午3:45:18 * @Description 视频 dao */public class VideoDAO {	// 需要操作的数据库	private Activity activity;	public VideoDAO(Activity activity) {		this.activity = activity;	}	// 添加一条下载信息	public void add(VideoModel video) {		Log.d("fred", "insert:uuid=" + video.getId() + "---ChBookName=" + video.getTitle());		ContentValues values = new ContentValues();		values.put(VideoContentProvider.ID, video.getId());		values.put(VideoContentProvider.VIDEO_TITLE, video.getTitle());		values.put(VideoContentProvider.VIDEO_URL, video.getUrl());		values.put(VideoContentProvider.VIDEO_VIDEO_PATH, video.getVideoPath());		values.put(VideoContentProvider.VIDEO_IS_DOWNLOAD_COMPLETE, video.getIsDownloadComplete());		values.put(VideoContentProvider.VIDEO_DOWNLOAD_PERCENT, video.getDownloadPercent());		values.put(VideoContentProvider.VIDEO_DOWNLOAD_ED_SIZE, video.getDownloadedSize());		values.put(VideoContentProvider.VIDEO_SIZE, video.getVideoSize());		values.put(VideoContentProvider.VIDEO_DOWNLOAD_TIME, System.currentTimeMillis());		activity.getContentResolver().insert(VideoContentProvider.CONTENT_URI_VIDEO_ADD, values);	}	// 删除一条下载信息	public void remove(String id) {		String[] strs = { id };		activity.getContentResolver().delete(VideoContentProvider.CONTENT_URI_VIDEO_DELETE,				VideoContentProvider.ID + "=?", strs);	}	// 更新一条下载信息	public void update(VideoModel video) {		ContentValues values = new ContentValues();		values.put(VideoContentProvider.ID, video.getId());		values.put(VideoContentProvider.VIDEO_TITLE, video.getTitle());		values.put(VideoContentProvider.VIDEO_URL, video.getUrl());		values.put(VideoContentProvider.VIDEO_VIDEO_PATH, video.getVideoPath());		values.put(VideoContentProvider.VIDEO_IS_DOWNLOAD_COMPLETE, video.getIsDownloadComplete());		values.put(VideoContentProvider.VIDEO_DOWNLOAD_PERCENT, video.getDownloadPercent());		values.put(VideoContentProvider.VIDEO_DOWNLOAD_ED_SIZE, video.getDownloadedSize());		values.put(VideoContentProvider.VIDEO_SIZE, video.getVideoSize());		String[] strs = { video.getId() };		activity.getContentResolver().update(VideoContentProvider.CONTENT_URI_VIDEO_UPDATE, values,				VideoContentProvider.ID + "=?", strs);	}	// 获取所有下载信息	public ArrayList<VideoModel> findAll() {		ArrayList<VideoModel> arrayList = new ArrayList<VideoModel>();		CursorLoader cursorLoader = new CursorLoader(activity, VideoContentProvider.CONTENT_URI_VIDEO_BROWSES, null, null, null, VideoContentProvider.VIDEO_DOWNLOAD_TIME + " desc");		Cursor cursor = cursorLoader.loadInBackground();		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {			VideoModel video = new VideoModel();			video.setId(cursor.getString(VideoContentProvider.ID_INDEX));			video.setTitle(cursor.getString(VideoContentProvider.VIDEO_TITLE_INDEX));			video.setUrl(cursor.getString(VideoContentProvider.VIDEO_URL_INDEX));			video.setVideoPath(cursor.getString(VideoContentProvider.VIDEO_VIDEO_PATH_INDEX));			video.setIsDownloadComplete(cursor.getInt(VideoContentProvider.VIDEO_IS_DOWNLOAD_COMPLETE_INDEX));			video.setDownloadPercent(cursor.getInt(VideoContentProvider.VIDEO_DOWNLOAD_PERCENT_INDEX));			video.setDownloadedSize(cursor.getString(VideoContentProvider.VIDEO_DOWNLOAD_ED_SIZE_INDEX));			video.setVideoSize(cursor.getString(VideoContentProvider.VIDEO_SIZE_INDEX));			arrayList.add(video);		}		cursor.close();		return arrayList;	}	public VideoModel findById(String id) {		Cursor cursor = null;		Uri url = Uri.withAppendedPath(VideoContentProvider.CONTENT_URI_VIDEO_BROWSE, id);		CursorLoader cursorLoader = new CursorLoader(activity, url, null, null, null, null);		cursor = cursorLoader.loadInBackground();		while (cursor.moveToNext()) {			VideoModel video = new VideoModel();			video.setId(cursor.getString(VideoContentProvider.ID_INDEX));			video.setTitle(cursor.getString(VideoContentProvider.VIDEO_TITLE_INDEX));			video.setUrl(cursor.getString(VideoContentProvider.VIDEO_URL_INDEX));			video.setVideoPath(cursor.getString(VideoContentProvider.VIDEO_VIDEO_PATH_INDEX));			video.setIsDownloadComplete(cursor.getInt(VideoContentProvider.VIDEO_IS_DOWNLOAD_COMPLETE_INDEX));			video.setDownloadPercent(cursor.getInt(VideoContentProvider.VIDEO_DOWNLOAD_PERCENT_INDEX));			video.setDownloadedSize(cursor.getString(VideoContentProvider.VIDEO_DOWNLOAD_ED_SIZE_INDEX));			video.setVideoSize(cursor.getString(VideoContentProvider.VIDEO_SIZE_INDEX));			cursor.close();			return video;		}		cursor.close();		return null;	}	public void findById(String id, VideoModel video) {		Cursor cursor = null;		Uri url = Uri.withAppendedPath(VideoContentProvider.CONTENT_URI_VIDEO_BROWSE, id);		CursorLoader cursorLoader = new CursorLoader(activity, url, null, null, null, null);		cursor = cursorLoader.loadInBackground();		while (cursor.moveToNext()) {			video.setId(cursor.getString(VideoContentProvider.ID_INDEX));			video.setTitle(cursor.getString(VideoContentProvider.VIDEO_TITLE_INDEX));			video.setUrl(cursor.getString(VideoContentProvider.VIDEO_URL_INDEX));			video.setVideoPath(cursor.getString(VideoContentProvider.VIDEO_VIDEO_PATH_INDEX));			video.setIsDownloadComplete(cursor.getInt(VideoContentProvider.VIDEO_IS_DOWNLOAD_COMPLETE_INDEX));			video.setDownloadPercent(cursor.getInt(VideoContentProvider.VIDEO_DOWNLOAD_PERCENT_INDEX));			video.setDownloadedSize(cursor.getString(VideoContentProvider.VIDEO_DOWNLOAD_ED_SIZE_INDEX));			video.setVideoSize(cursor.getString(VideoContentProvider.VIDEO_SIZE_INDEX));			cursor.close();		}	}	public boolean hasFindById(String id) {		Cursor cursor = null;		Uri url = Uri.withAppendedPath(VideoContentProvider.CONTENT_URI_VIDEO_BROWSE, id);		CursorLoader cursorLoader = new CursorLoader(activity, url, null, null, null, null);		cursor = cursorLoader.loadInBackground();		while (cursor.moveToNext()) {			cursor.close();			return true;		}		cursor.close();		return false;	}}