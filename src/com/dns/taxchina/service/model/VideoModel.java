package com.dns.taxchina.service.model;import netlib.model.BaseModel;/** * @author henzil * @version create time:2014-5-4_下午3:37:06 * @Description 视频model。 */public class VideoModel extends BaseModel {	/**	 * 	 */	private static final long serialVersionUID = -86345920977570841L;	private String id;	private String title;	private String url;	// 视频路径	private String videoPath;	// 是否下载完成 0表示未下载完成，1表示已下载，	private int isDownloadComplete;	// 下载百分比 如果isDownloadComplete==1时，此为100%。	private int downloadPercent;	private String videoSize; // 书籍大小	public String getId() {		return id;	}	public void setId(String id) {		this.id = id;	}	public String getTitle() {		return title;	}	public void setTitle(String title) {		this.title = title;	}	public String getUrl() {		return url;	}	public void setUrl(String url) {		this.url = url;	}	public String getVideoPath() {		return videoPath;	}	public void setVideoPath(String videoPath) {		this.videoPath = videoPath;	}	public int getIsDownloadComplete() {		return isDownloadComplete;	}	public void setIsDownloadComplete(int isDownloadComplete) {		this.isDownloadComplete = isDownloadComplete;	}	public int getDownloadPercent() {		return downloadPercent;	}	public void setDownloadPercent(int downloadPercent) {		this.downloadPercent = downloadPercent;	}	public String getVideoSize() {		return videoSize;	}	public void setVideoSize(String videoSize) {		this.videoSize = videoSize;	}	@Override	public String toString() {		return "VideoModel [id=" + id + ", title=" + title + ", url=" + url + ", videoPath=" + videoPath				+ ", isDownloadComplete=" + isDownloadComplete + ", downloadPercent=" + downloadPercent				+ ", videoSize=" + videoSize + "]";	}}