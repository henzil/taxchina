package com.dns.taxchina.service.db.constant;
/**
 * @author fubiao
 * @version create time:2014-3-19_上午11:40:23
 * @Description 数据库常量
 */
public interface MyCollectDBConstant {

	// 数据库
	public static final int DB_VERSION = 7;

	public static final String DATABASE_NAME = "_taxchina_cache.db";
	
	String T_DATA = "t_collect";
	String T_SEARCH = "t_search";

	String DATA_ID = "id";
	String DATA_TITLE = "title";
	String DATA_INFO = "info";
	String DATA_IMAGE = "image";
	String DATA_ISVIDEO = "isVideo";
	String DATA_URL = "url";
	String DATA_TIME = "time";
	String DATA_HISTORY = "history";
	
	
	public static final String NEW_TABLE_DATA = "CREATE TABLE IF NOT EXISTS "
			+ T_DATA + "("
			+ DATA_ID + " integer primary key autoincrement,"
			+ DATA_TITLE + " TEXT,"
			+ DATA_INFO + " TEXT,"
			+ DATA_IMAGE + " TEXT,"
			+ DATA_ISVIDEO +" TEXT,"
			+ DATA_URL + " TEXT,"
			+ DATA_TIME + " LONG);";
	
	public static final String NEW_TABLE_SEARCH = "CREATE TABLE IF NOT EXISTS "
			+ T_SEARCH + "("
			+ DATA_ID + " integer primary key autoincrement,"
			+ DATA_HISTORY + " TEXT,"
			+ DATA_TIME + " LONG);";
}
