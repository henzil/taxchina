package com.dns.taxchina.service.db.fileDB;


/**
 * Created with IntelliJ IDEA. User: wangjun Date: 13-4-17 Time: 下午9:29 To
 * change this template use File | Settings | File Templates.
 */
public class SQL {
	
	public static final String SQLite_MASTER_TABLE = "internal_video.db";
	public static final String SQLITE_DATABASE_NAME = "internal_video";

	public static final String SQL_P_VIDEO_SELECT = "select pId,pTitle,count(pId) as count from internal_video group by pId ";

	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String P_ID = "pId";
	public static final String P_TITLE = "pTitle";
	public static final String VIDEO_NAME = "videoName";
	public static final String SIZE = "size";
	public static final String COUNT = "count";

	/**
	 * 获取视频数据
	 * */
	public static final String SQL_VIDEO_SELECT = "select id,title,videoName,size from internal_video where pId={0}";

}
