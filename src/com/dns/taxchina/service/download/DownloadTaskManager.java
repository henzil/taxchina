package com.dns.taxchina.service.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import netlib.net.AsyncTaskLoaderImage;
import netlib.util.LibIOUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.dns.taxchina.service.model.DownloadTask;
import com.dns.taxchina.service.model.VideoModel;

public class DownloadTaskManager {
	
	private static DownloadTaskManager downloadTaskManager;

	private Activity context;

	// 线程池，用于管理多个下载线程
	private ExecutorService executorService;
	
	// 正在下载的 videoId；
	private String videoId = null;
	
	private DownloadMode dataMode = DownloadMode.DOWNLOAD_END; 

	public static DownloadTaskManager getInstance(Activity context) {
		if (downloadTaskManager == null) {
			downloadTaskManager = new DownloadTaskManager(context);
		}
		return downloadTaskManager;
	}

	private DownloadTaskManager(Activity context) {
		this.context = context;
	}

	// 添加下载任务
	public void addTask(DownloadTask downloadTask, VideoModel videoModel) {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		// 向数据库中写入任务信息
		downloadTaskDAO.add(downloadTask);
		// 将新任务加入线程池
		executorService.submit(new DownLoadBytes(context, downloadTask, videoModel));
	}

	// 删除数据库中的任务
	public void deleteTask(String fileId) {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		downloadTaskDAO.remove(fileId);
	}

	// 开始数据库中未完成的任务
	private void startTasks() {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		VideoDAO videoDAO = new VideoDAO(context);
		// 获取数据库中的所有未完成的任务
		ArrayList<DownloadTask> arrayList = downloadTaskDAO.findAll();
		// 将所有未完成的任务一一加入线程池
		Log.d("DownloadTaskManager", "unfinished task=" + arrayList.size());
		for (int i = 0; i < arrayList.size(); i++) {
			DownloadTask downloadTask = arrayList.get(i);
			Log.v("DownloadTaskManager", "downloadTask.toString() = " + downloadTask.toString());
			VideoModel video = videoDAO.findById(downloadTask.getVideo().getId());
			executorService.submit(new DownLoadBytes(context, downloadTask, video));
		}
	}
	
	// 执行暂停某一个
	public void pauseCurrentTask(String videoId){
		dataMode = DownloadMode.PAUSE;
	}

	// 线程管理器开始工作
	public void start() {
		// 创建线程池
		executorService = Executors.newFixedThreadPool(1);
		// 开始数据库中未完成的任务
		startTasks();
	}

	public void stop() {
		// 关闭线程池
		executorService.shutdownNow();
	}

	// 判断线程池是否停止
	public boolean isStop() {
		return executorService.isShutdown();
	}
	
	public String downloadingId(){
		return videoId;
	}

	// 下载线程（内部类）
	private class DownLoadBytes implements Runnable {
		// 应用程序环境上下文，用于创建SQLiteOpenHelper
		private Activity context;
		// 已经下载的字节数
		private long total;
		// 一共能下载的字节数
		private long fileLength;
		// 文件ID号
		private String fileId;
		// 文件存储位置
		private File downloadFile;
		// 下载任务，更新和删除时使用
		private DownloadTask downloadTask;

		private VideoModel video;

		public DownLoadBytes(Activity context, DownloadTask downloadTask, VideoModel videoModel) {
			this.context = context;
			this.fileId = downloadTask.getFileId();
			this.fileLength = downloadTask.getFileLength();
			this.downloadTask = downloadTask;
			this.video = videoModel;
			// 获取文件已经下载的字节数
			if (downloadTask.getFilePath() == null) {
				total = 0;
			} else {
				Log.v("DownloadTaskManager", "downloadTask.getFilePath() = " + downloadTask.getFilePath());
				downloadFile = new File(downloadTask.getFilePath());
				total = downloadFile.length();
			}
		}

		@Override
		public void run() {
			VideoDAO videoDAO = new VideoDAO(context);
			dataMode = DownloadMode.START;
			DefaultHttpClient httpClient = new DefaultHttpClient();
			String url = video.getUrl();
			Log.e("DownloadTaskManager", "#执行到这里~~~~~~~~ url = " + url);
			HttpGet httpGet = new HttpGet(url);
			videoId = video.getId();
			
			try {
				// TODO 状态处理 500 200
				int res = 0;
				HttpResponse httpResponse = httpClient.execute(httpGet);
				res = httpResponse.getStatusLine().getStatusCode();
				Log.e("DownloadTaskManager", "#执行到这里~~~~~~~~ res = " + res);
				if (res == 200) {
					Header headerLength = httpResponse.getFirstHeader("Content-Length");
					Header headerDisposition = httpResponse.getFirstHeader("Content-Disposition");
					String lengthStr = headerLength.getValue();
					for (Header header : httpResponse.getAllHeaders()) {
						Log.i("DownloadTaskManager", "###############header.toString() = " + header.toString());
					}

					String path = AsyncTaskLoaderImage.getHash(url);
					fileLength = Long.parseLong(lengthStr);
					Log.i("DownloadTaskManager", "###############" + fileLength);
					Log.i("DownloadTaskManager", "###############" + path);
					httpGet.addHeader("Range", "bytes=" + total + "-");
					httpResponse = httpClient.execute(httpGet);
					res = httpResponse.getStatusLine().getStatusCode();
					Log.i("DownloadTaskManager", "###############" + res);
					if (res == 206) {
						/*
						 * 当返回码为200时，做处理 得到服务器端返回json数据，并做处理
						 */
						InputStream inputStream = httpResponse.getEntity().getContent();
						if (downloadFile == null) {
							downloadFile = new File(LibIOUtil.getDownloadPath(context) + path);
							if (!downloadFile.exists()) {
								downloadFile.createNewFile();
							}
							downloadTask.setFilePath(LibIOUtil.getDownloadPath(context) + path);
							DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
							downloadTaskDAO.update(downloadTask);

							video.setVideoPath(LibIOUtil.getDownloadPath(context) + path);
							videoDAO.update(video);
						}
						FileOutputStream fileOutputStream = new FileOutputStream(downloadFile, true);
						byte[] b = new byte[1024 * 8];
						int count = 0;
						int oldCount = 0;

						for (; total < fileLength;) {
							// 如何停止线程？
							if(dataMode == DownloadMode.PAUSE){
								dataMode = DownloadMode.DOWNLOAD_END;
								fileOutputStream.close();
								httpClient.getConnectionManager().shutdown();
								inputStream.close();
								return;
							}
							if (isStop()) {
								dataMode = DownloadMode.DOWNLOAD_END;
								fileOutputStream.close();
								httpClient.getConnectionManager().shutdown();
								inputStream.close();
								return;
							} else {
								count = inputStream.read(b);
								if (count != -1) {
									total += count;
									oldCount += count;
									fileOutputStream.write(b, 0, count);
									if ((Float.parseFloat(Integer.toString(oldCount)) / Float.parseFloat(Long
											.toString(fileLength))) > 0.05) {
										oldCount = 0;
										// 广播通知主线程重新绘制相应的进度条
										int progressBarState = (int) ((Float.parseFloat(Long.toString(total)) / Float
												.parseFloat(Long.toString(fileLength))) * 100);

										video.setDownloadPercent(progressBarState);
										videoDAO.update(video);
										Log.d("DownloadTaskManager", "pregressBarState=" + progressBarState);
										Log.v("DownloadTaskManager", "total = " + total);
										Log.v("DownloadTaskManager", "fileId = " + fileId);
										// 广播进度
										Intent intent = new Intent(
												DownloadTaskContact.DOWNLOADING_PERCENT_INTENT_FILTER);
										intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY,
												DownloadTaskContact.DOWNLOADING_TYPE_PERCENT_VALUE);
										intent.putExtra(DownloadTaskContact.DOWNLOADING_VIDEO_PERCENT, progressBarState);
										intent.putExtra(DownloadTaskContact.DOWNLOADING_VIDEO_PERCENT_ID, video.getId());
										context.sendBroadcast(intent);
										
									}
								}
							}
						}
						DownloadTaskManager.this.deleteTask(fileId);
						video.setIsDownloadComplete(1);
						video.setDownloadPercent(100);
						videoDAO.update(video);
						dataMode = DownloadMode.DOWNLOAD_END;
						// 读取下载完毕
						fileOutputStream.close();
						httpClient.getConnectionManager().shutdown();
						inputStream.close();
						// 下载完成通知
						Intent intent = new Intent(DownloadTaskContact.DOWNLOADING_PERCENT_INTENT_FILTER);
						intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY,
								DownloadTaskContact.DOWNLOADING_TYPE_END_VALUE);
						intent.putExtra(DownloadTaskContact.DOWNLOADING_VIDEO_PERCENT_ID, video.getId());
						context.sendBroadcast(intent);
						videoId = null;
					}
				}

			} catch (Exception e) {
				Log.e("DownloadTaskManager", e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
	}
}