package com.dns.taxchina.service.download;import android.content.ContentProvider;import android.content.ContentValues;import android.content.Context;import android.content.UriMatcher;import android.database.Cursor;import android.database.sqlite.SQLiteDatabase;import android.net.Uri;import android.util.Log;/** * @author henzil * @version create time:2014-5-4_下午3:49:58 * @Description 视频 ContentProvider */public class VideoContentProvider extends ContentProvider {	private SQLiteDatabase database;	public static int DATABASE_VERSION = 2;	private static final String PROVIDER_NAME = "com.dns.taxchina_pad";	private static final String CONTENT_URI_PREFIX = "content://" + PROVIDER_NAME;	private static UriMatcher uriMatcher;	private static final int DOWNLOAD_VIDEO = 1;	private static final int DOWNLOAD_VIDEOS = 2;	private static final int TDOWNLOAD_VIDEO = 3;	private static final int UPDATE_DOWNLOAD_VIDEO = 4;	private static final int DELETE_DOWNLOAD_VIDEO = 5;	private static final int ADD_DOWNLOAD_VIDEO = 6;	private static final int VIDEO = 7;	private static final int VIDEOS = 8;	private static final int TVIDEO = 9;	private static final int UPDATE_VIDEO = 10;	private static final int DELETE_VIDEO = 11;	private static final int ADD_VIDEO = 12;	/**	 * download video	 */	public static final String FILE_ID = "fileId";	public static final int FILE_ID_INDEX = 0;	public static final String FILE_LENGTH = "fileLength";	public static final int FILE_LENGTH_INDEX = FILE_ID_INDEX + 1;	public static final String FILE_PATH = "filePath";	public static final int FILE_PATH_INDEX = FILE_LENGTH_INDEX + 1;	public static final String VIDEO_ID = "video_id";	public static final int VIDEO_ID_INDEX = FILE_PATH_INDEX + 1;	/**	 * video	 */	public static final String ID = "id";	public static final int ID_INDEX = 0;	public static final String VIDEO_TITLE = "title";	public static final int VIDEO_TITLE_INDEX = ID_INDEX + 1;	public static final String VIDEO_URL = "url";	public static final int VIDEO_URL_INDEX = VIDEO_TITLE_INDEX + 1;	public static final String VIDEO_VIDEO_PATH = "videoPath";	public static final int VIDEO_VIDEO_PATH_INDEX = VIDEO_URL_INDEX + 1;	public static final String VIDEO_IS_DOWNLOAD_COMPLETE = "isDownloadComplete";	public static final int VIDEO_IS_DOWNLOAD_COMPLETE_INDEX = VIDEO_VIDEO_PATH_INDEX + 1;	public static final String VIDEO_DOWNLOAD_PERCENT = "downloadPercent";	public static final int VIDEO_DOWNLOAD_PERCENT_INDEX = VIDEO_IS_DOWNLOAD_COMPLETE_INDEX + 1;	public static final String VIDEO_DOWNLOAD_ED_SIZE = "downloadedSize";	public static final int VIDEO_DOWNLOAD_ED_SIZE_INDEX = VIDEO_DOWNLOAD_PERCENT_INDEX + 1;		public static final String VIDEO_SIZE = "videoSize";	public static final int VIDEO_SIZE_INDEX = VIDEO_DOWNLOAD_ED_SIZE_INDEX + 1;	public static final String VIDEO_DOWNLOAD_TIME = "downloadTime";	public static final int VIDEO_DOWNLOAD_TIME_INDEX = VIDEO_SIZE_INDEX + 1;	/**	 * tables name	 * */	private static final String DOWNLOAD_VIDEO_TABLE_NAME = "download_video";	private static final String VIDEO_TABLE_NAME = "video";	public static final Uri CONTENT_URI_TDOWNLOAD_VIDEO = Uri.parse(CONTENT_URI_PREFIX + "/download_video");	public static final Uri CONTENT_URI_TDOWNLOAD_VIDEO_BROWSES = Uri.parse(CONTENT_URI_PREFIX			+ "/download_video_browse");	public static final Uri CONTENT_URI_TDOWNLOAD_VIDEO_BROWSE = Uri.parse(CONTENT_URI_PREFIX			+ "/download_video_browse/#");	public static final Uri CONTENT_URI_TDOWNLOAD_VIDEO_ADD = Uri.parse(CONTENT_URI_PREFIX + "/download_video_add");	public static final Uri CONTENT_URI_TDOWNLOAD_VIDEO_UPDATE = Uri.parse(CONTENT_URI_PREFIX			+ "/download_video_update");	public static final Uri CONTENT_URI_TDOWNLOAD_VIDEO_DELETE = Uri.parse(CONTENT_URI_PREFIX			+ "/download_video_delete");	public static final Uri CONTENT_URI_VIDEO = Uri.parse(CONTENT_URI_PREFIX + "/video");	public static final Uri CONTENT_URI_VIDEO_BROWSES = Uri.parse(CONTENT_URI_PREFIX + "/video_browse");	public static final Uri CONTENT_URI_VIDEO_BROWSE = Uri.parse(CONTENT_URI_PREFIX + "/video_browse/#");	public static final Uri CONTENT_URI_VIDEO_ADD = Uri.parse(CONTENT_URI_PREFIX + "/video_add");	public static final Uri CONTENT_URI_VIDEO_UPDATE = Uri.parse(CONTENT_URI_PREFIX + "/video_update");	public static final Uri CONTENT_URI_VIDEO_DELETE = Uri.parse(CONTENT_URI_PREFIX + "/video_delete");	static {		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);		uriMatcher.addURI(PROVIDER_NAME, "download_video", TDOWNLOAD_VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "download_video_browse", DOWNLOAD_VIDEOS);		uriMatcher.addURI(PROVIDER_NAME, "download_video_browse/*", DOWNLOAD_VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "download_video_update", UPDATE_DOWNLOAD_VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "download_video_delete", DELETE_DOWNLOAD_VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "download_video_add", ADD_DOWNLOAD_VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "video", TVIDEO);		uriMatcher.addURI(PROVIDER_NAME, "video_browse", VIDEOS);		uriMatcher.addURI(PROVIDER_NAME, "video_browse/*", VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "video_update", UPDATE_VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "video_delete", DELETE_VIDEO);		uriMatcher.addURI(PROVIDER_NAME, "video_add", ADD_VIDEO);	}	@Override	public int delete(Uri uri, String selection, String[] selectionArgs) {		switch (uriMatcher.match(uri)) {		case DELETE_DOWNLOAD_VIDEO:			/**			 * 删除数据库内容			 */			return database.delete(DOWNLOAD_VIDEO_TABLE_NAME, selection, selectionArgs);		case DELETE_VIDEO:			/**			 * 删除数据库内容			 */			return database.delete(VIDEO_TABLE_NAME, selection, selectionArgs);		default:			throw new IllegalArgumentException("delete data >>unknown uri: " + uri);		}	}	@Override	public String getType(Uri uri) {		switch (uriMatcher.match(uri)) {		case DOWNLOAD_VIDEOS:			return "vnd.android.cursor.dir/" + PROVIDER_NAME;		case DOWNLOAD_VIDEO:			return "vnd.android.cursor.item/" + PROVIDER_NAME;		default:			throw new IllegalArgumentException("Unsupported URI: " + uri);		}	}	@Override	public Uri insert(Uri uri, ContentValues values) {		switch (uriMatcher.match(uri)) {		case ADD_DOWNLOAD_VIDEO:			database.insert(DOWNLOAD_VIDEO_TABLE_NAME, FILE_ID, values);			return uri;		case ADD_VIDEO:			database.insert(VIDEO_TABLE_NAME, ID, values);			return uri;		default:			throw new IllegalArgumentException("insert data >>unknown uri: " + uri);		}	}	@Override	public boolean onCreate() {		database = getContext().openOrCreateDatabase(PROVIDER_NAME, Context.MODE_PRIVATE, null);		database.close();		database = new VideoSQLiteOpenHelper(getContext(), PROVIDER_NAME, null, DATABASE_VERSION).getWritableDatabase();		return database != null;	}	@Override	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {		Log.v("VideoContentProvider", "uriMatcher.match(uri)===========" + uriMatcher.match(uri));		switch (uriMatcher.match(uri)) {		case DOWNLOAD_VIDEOS:			return database.query(DOWNLOAD_VIDEO_TABLE_NAME, projection, selection, selectionArgs, null, null,					sortOrder);		case TDOWNLOAD_VIDEO:			return database.query(DOWNLOAD_VIDEO_TABLE_NAME, projection, selection, selectionArgs, null, null,					sortOrder);		case DOWNLOAD_VIDEO:			return database.query(DOWNLOAD_VIDEO_TABLE_NAME, projection, FILE_ID + "=" + uri.getPathSegments().get(1),					selectionArgs, null, null, null);		case VIDEOS:			return database.query(VIDEO_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);		case TVIDEO:			return database.query(VIDEO_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);		case VIDEO:			return database.query(VIDEO_TABLE_NAME, projection, ID + "=" + uri.getPathSegments().get(1), selectionArgs,					null, null, null);		default:			throw new IllegalArgumentException("unknown uri: " + uri);		}	}	@Override	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {		switch (uriMatcher.match(uri)) {		case UPDATE_DOWNLOAD_VIDEO:			return database.update(DOWNLOAD_VIDEO_TABLE_NAME, values, selection, selectionArgs);		case UPDATE_VIDEO:			return database.update(VIDEO_TABLE_NAME, values, selection, selectionArgs);		default:			throw new IllegalArgumentException("update data >>unknown uri: " + uri);		}	}}