package com.dns.taxchina.service.download;import android.content.Context;import android.database.sqlite.SQLiteDatabase;import android.database.sqlite.SQLiteDatabase.CursorFactory;import android.database.sqlite.SQLiteOpenHelper;import android.util.Log;/** * @author henzil * @version create time:2014-5-4_下午3:57:15 * @Description TODO */public class VideoSQLiteOpenHelper extends SQLiteOpenHelper {	/**	 * download_video	 */	private final static String CREATE_DOWNLOAD_VIDEO_TABLE = "create table if not exists download_video("			+ "fileId text, " 			+ "fileLength long, " 			+ "filePath text, " 			+ "video_id integer, "			+ "book_versionFlag float)";	/**	 * video	 */	private final static String CREATE_VIDEO_TABLE = "create table if not exists video(" 			+ "id text, " 			+ "title text, "			+ "url text, " 			+ "videoPath text, " 			+ "isDownloadComplete integer, " 			+ "downloadPercent integer, "			+ "videoSize text, " 			+ "downloadTime integer )";	/**	 * create	 */	private final static String[] CREATE_TABLES = {CREATE_DOWNLOAD_VIDEO_TABLE, CREATE_VIDEO_TABLE };	void createTables(SQLiteDatabase database) {		for (String sql : CREATE_TABLES) {			database.execSQL(sql);		}	}	public VideoSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {		super(context, name, factory, version);	}	@Override	public void onCreate(SQLiteDatabase database) {		Log.d("VideoSQLiteOpenHelper", "create database ...");		createTables(database);	}	@Override	public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {		Log.d("VideoSQLiteOpenHelper", "upgrade database ...");		createTables(database);	}}