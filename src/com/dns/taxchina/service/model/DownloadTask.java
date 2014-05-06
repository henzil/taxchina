package com.dns.taxchina.service.model;

public class DownloadTask {
	// 下载文件的ID
	private VideoModel video;
	// 下载文件的长度
	private long fileLength;

	private String filePath;

	// 下载文件的ID
	private String fileId;

	public DownloadTask() {
		super();
	}

	public DownloadTask(VideoModel video, long fileLength) {
		this.video = video;
		this.fileLength = fileLength;
	}

	public VideoModel getVideo() {
		return video;
	}

	public void setVideo(VideoModel video) {
		this.video = video;
	}

	// 获取下载文件的长度
	public long getFileLength() {
		return fileLength;
	}

	// 设置文件长度
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadTask other = (DownloadTask) obj;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DownloadTask [video=" + video + ", fileLength=" + fileLength + ", filePath=" + filePath + ", fileId="
				+ fileId + "]";
	}
}
