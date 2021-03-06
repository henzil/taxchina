package com.dns.taxchina.service.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import netlib.util.SettingUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dns.taxchina.service.model.DownloadTask;
import com.dns.taxchina.service.model.VideoModel;

@SuppressLint("HandlerLeak")
public class DownloadTaskManager {

	private static DownloadTaskManager downloadTaskManager;

	private Activity context;

	// 线程池，用于管理多个下载线程
	private ExecutorService executorService;
	
	//  当前的线程，用来操作自己。让自己自生自灭
	private DownLoadBytes currentThread = null;

	// 任务列表，包括未使用的。
	private List<DownloadTask> taskList = new ArrayList<DownloadTask>();

	// 激活的任务队列，只是属于等待状态的任务。
	private Queue<DownloadTask> queue = new LinkedList<DownloadTask>();

	private Handler findHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				startNext();
				break;

			default:
				break;
			}
		}
	};

	public static DownloadTaskManager getInstance(Activity context) {
		if (downloadTaskManager == null) {
			downloadTaskManager = new DownloadTaskManager(context);
		}
		return downloadTaskManager;
	}

	private DownloadTaskManager(Activity context) {
		this.context = context;
		init();
	}

	// 线程管理器开始工作 初始化
	public void init() {
		// 创建线程池
		executorService = Executors.newFixedThreadPool(1);
		// 开始数据库中未完成的任务
		initTasks();
	}

	// 将数据库中未完成的任务 放在taskList中
	private void initTasks() {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		// 获取数据库中的所有未完成的任务
		taskList = downloadTaskDAO.findAll();
		// 将所有未完成的任务一一加入线程池
		Log.d("DownloadTaskManager", "unfinished task=" + taskList.size());
	}

	// 添加下载任务并存储在数据库中
	public void addTask(DownloadTask downloadTask, VideoModel videoModel) {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		// 向数据库中写入任务信息
		downloadTaskDAO.add(downloadTask);
		taskList.add(downloadTask);
		queue.add(downloadTask);
		if(currentThread == null){
			DownLoadBytes downLoadBytes = new DownLoadBytes(context, queue.poll(), videoModel);
			currentThread = downLoadBytes;
			executorService.submit(downLoadBytes);
		} 
	}

	// 开始一个下载任务，放在队列中
	public void startOneTask(VideoModel videoModel) {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		DownloadTask downloadTask = downloadTaskDAO.findById(videoModel.getId());
		queue.add(downloadTask);
		Log.d("tag", "startOneTask  ---------queue size = " + queue.size());
		if(currentThread == null){
			DownLoadBytes downLoadBytes = new DownLoadBytes(context, queue.poll(), videoModel);
			currentThread = downLoadBytes;
			executorService.submit(downLoadBytes);
		} 
		Intent intent = new Intent(DownloadTaskContact.DOWNLOADING_PERCENT_INTENT_FILTER);
		intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY, DownloadTaskContact.DOWNLOADING_TYPE_START_VALUE);
		context.sendBroadcast(intent);
	}

	public void stopOneTask(VideoModel videoModel) {
		Log.e("tag", "~~~~~~~~videoModel.getId() = " + videoModel.getId());
		if(currentThread != null){
			if(currentThread.getVideoId().equals(videoModel.getId())){
				if(currentThread != null){
					currentThread.stop();
					currentThread = null;
				}
				//  一个刷新广播
				Intent intent = new Intent(DownloadTaskContact.DOWNLOADING_PERCENT_INTENT_FILTER);
				intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY, DownloadTaskContact.DOWNLOADING_TYPE_ERROR_VALUE);
				context.sendBroadcast(intent);
			} else {
				// 单纯移除一个任务
				for (int i = 0; i < taskList.size(); i++) {
					DownloadTask downloadTask = taskList.get(i);
					if (downloadTask.getFileId().equals(videoModel.getId())) {
						queue.remove(downloadTask);
						return;
					}
				}
			}
		}
	}

	// 删除数据库中的任务
	public void deleteTask(String fileId) {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		downloadTaskDAO.remove(fileId);
		for (int i = 0; i < taskList.size(); i++) {
			DownloadTask downloadTask = taskList.get(i);
			if (downloadTask.getFileId().equals(fileId)) {
				taskList.remove(i);
				return;
			}
		}
	}

	// 手动删除数据库中的数据
	public void deleteTask(VideoModel videoModel) {
		DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
		downloadTaskDAO.remove(videoModel.getId());
		for (int i = 0; i < taskList.size(); i++) {
			DownloadTask downloadTask = taskList.get(i);
			if (downloadTask.getFileId().equals(videoModel.getId())) {
				taskList.remove(i);
				break;
			}
		}
		// 如果在正在下载的队列中，则删除掉。
		stopOneTask(videoModel);
	}

	// 执行下一个任务。
	private void startNext() {
		DownloadTask downloadTask = queue.poll();
		if (downloadTask != null) {
			VideoDAO videoDAO = new VideoDAO(context);
			VideoModel videoModel = videoDAO.findById(downloadTask.getVideo().getId());
			DownLoadBytes downLoadBytes = new DownLoadBytes(context, downloadTask, videoModel);
			if(currentThread != null){
				currentThread.stop();
				currentThread = null;
			}
			currentThread = downLoadBytes;
			executorService.submit(downLoadBytes);
		} else {
			// 如果队列中无任务了，则把当前的线程置空。
			if(currentThread != null){
				currentThread.stop();
				currentThread = null;
			}
			//  一个刷新广播
			Intent intent = new Intent(DownloadTaskContact.DOWNLOADING_PERCENT_INTENT_FILTER);
			intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY, DownloadTaskContact.DOWNLOADING_TYPE_ERROR_VALUE);
			context.sendBroadcast(intent);
		}
	}

	// TODO 算法有问题。
	public HashSet<VideoModel> getCurrentDownLoadSet() {
		HashSet<VideoModel> set = new HashSet<VideoModel>();
		Iterator<DownloadTask> iterator = queue.iterator();
		Log.d("tag", "getCurrentDownLoadSet   --------- 队列的size = " + queue.size());
		while (iterator.hasNext()) {
			VideoModel videoModel = iterator.next().getVideo();
			set.add(videoModel);
		}
		return set;
	}

	public void stop() {
		// 关闭线程池
		queue.clear();
		executorService.shutdownNow();
		executorService = Executors.newFixedThreadPool(1);
		if(currentThread != null){
			currentThread.stop();
			currentThread = null;
		}
	}

	public void destroy() {
		queue.clear();
		executorService.shutdownNow();
		queue = null;
		taskList = null;
		executorService = null;
		if(currentThread != null){
			currentThread.stop();
			currentThread = null;
		}
	}

	// 判断线程池是否停止
	public boolean isStop() {
		return executorService.isShutdown();
	}

	public String downloadingId() {
		if(currentThread != null){
			return currentThread.getVideoId();
		}
		return null;
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

		private DefaultHttpClient httpClient;

		private InputStream inputStream;

		private FileOutputStream fileOutputStream;

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
		
		public String getVideoId(){
			return this.fileId;
		}

		public void stop() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						httpClient.getConnectionManager().shutdown();
						inputStream.close();
						fileOutputStream.close();
					} catch (Exception exception) {
						Log.e("tag", exception.toString(), exception);
					}
				}
			}).start();
		}

		@Override
		public void run() {
			VideoDAO videoDAO = new VideoDAO(context);
			httpClient = new DefaultHttpClient();
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			Log.e("tag", "wifiManager.isWifiEnabled() = " + wifiManager.isWifiEnabled());

			String url = video.getUrl();
			Log.d("DownloadTaskManager", "#执行到这里~~~~~~~~ url = " + url);
			HttpGet httpGet = new HttpGet(url);
			// 开始下载广播
			Intent intent = new Intent(DownloadTaskContact.DOWNLOADING_PERCENT_INTENT_FILTER);
			intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY, DownloadTaskContact.DOWNLOADING_TYPE_START_VALUE);
			intent.putExtra(DownloadTaskContact.DOWNLOADING_VIDEO_PERCENT_ID, video.getId());
			context.sendBroadcast(intent);
			try {
				// 状态处理 500 200
				int res = 0;
				HttpResponse httpResponse = httpClient.execute(httpGet);
				res = httpResponse.getStatusLine().getStatusCode();
				Log.d("DownloadTaskManager", "#执行到这里~~~~~~~~ res = " + res);
				if (res == 200) {
					Header headerLength = httpResponse.getFirstHeader("Content-Length");
//					Header headerDisposition = httpResponse.getFirstHeader("Content-Disposition");
					String lengthStr = headerLength.getValue();
//					for (Header header : httpResponse.getAllHeaders()) {
//						Log.i("DownloadTaskManager", "###############header.toString() = " + header.toString());
//					}

//					String path = AsyncTaskLoaderImage.getHash(url);
					String path = video.getVideoPath();
					fileLength = Long.parseLong(lengthStr);
					Log.i("DownloadTaskManager", "############total = " + total);
					httpGet.addHeader("Range", "bytes=" + total + "-");
					httpResponse = httpClient.execute(httpGet);
					res = httpResponse.getStatusLine().getStatusCode();
					Log.i("DownloadTaskManager", "###############" + res);
					if (res == 206) {
						/*
						 * 当返回码为200时，做处理 得到服务器端返回json数据，并做处理
						 */
						inputStream = httpResponse.getEntity().getContent();
						if (downloadFile == null) {
//							downloadFile = new File(LibIOUtil.getDownloadPath(context) + path);
							downloadFile = new File(path);
							if (!downloadFile.exists()) {
								downloadFile.createNewFile();
							}
//							downloadTask.setFilePath(LibIOUtil.getDownloadPath(context) + path);
							downloadTask.setFilePath(path);
							DownloadTaskDAO downloadTaskDAO = new DownloadTaskDAO(context);
							downloadTaskDAO.update(downloadTask);
							video.setDownloadedSize("" + total);
							video.setVideoSize(lengthStr);
//							video.setVideoPath(LibIOUtil.getDownloadPath(context) + path);
							videoDAO.update(video);
						}
						fileOutputStream = new FileOutputStream(downloadFile, true);
						byte[] b = new byte[1024 * 2];
						int count = 0;
						int oldCount = 0;

						for (; total < fileLength;) {
							// 如何停止线程？
							if (isStop()) {
								fileOutputStream.close();
								httpClient.getConnectionManager().shutdown();
								inputStream.close();
								findHandler.sendEmptyMessage(0);
								return;
							} else {
								if (!wifiManager.isWifiEnabled()) {
									if (!SettingUtil.getWifiDoSomeThing(context)) {
										fileOutputStream.close();
										httpClient.getConnectionManager().shutdown();
										inputStream.close();
										findHandler.sendEmptyMessage(0);
										return;
									}
								}
								count = inputStream.read(b);
								if (count != -1) {
									total += count;
									oldCount += count;
									fileOutputStream.write(b, 0, count);
									if ((Float.parseFloat(Integer.toString(oldCount)) / Float.parseFloat(Long
											.toString(fileLength))) > 0.005) {
										oldCount = 0;
										// 广播通知主线程重新绘制相应的进度条
										int progressBarState = (int) (Float.parseFloat(Long.toString(total))
												/ Float.parseFloat(Long.toString(fileLength)) * 100);
										video.setDownloadedSize("" + total);
										video.setVideoSize(lengthStr);
										video.setDownloadPercent(progressBarState);
										videoDAO.update(video);
										Log.d("DownloadTaskManager", "pregressBarState=" + progressBarState);
										Log.v("DownloadTaskManager", "total = " + total);
										Log.v("DownloadTaskManager", "fileId = " + fileId);
										// 广播进度
										intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY,
												DownloadTaskContact.DOWNLOADING_TYPE_PERCENT_VALUE);
										intent.putExtra(DownloadTaskContact.DOWNLOADING_VIDEO_PERCENT, progressBarState);
										intent.putExtra(DownloadTaskContact.DOWNLOADING_VIDEO_PERCENT_ID, video.getId());
										context.sendBroadcast(intent);
									}
								}
							}
						}
						// 下载完成后，重命名。
						String newPath = path.substring(0, path.indexOf(".tmp"));
						downloadFile.renameTo(new File(newPath));
						DownloadTaskManager.this.deleteTask(fileId);
						video.setIsDownloadComplete(1);
						video.setDownloadPercent(100);
						video.setDownloadedSize(lengthStr);
						video.setVideoSize(lengthStr);
						video.setVideoPath(newPath);
						videoDAO.update(video);
						// 读取下载完毕
						fileOutputStream.close();
						httpClient.getConnectionManager().shutdown();
						inputStream.close();
						// 下载完成广播
						intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY,
								DownloadTaskContact.DOWNLOADING_TYPE_END_VALUE);
						intent.putExtra(DownloadTaskContact.DOWNLOADING_VIDEO_PERCENT_ID, video.getId());
						context.sendBroadcast(intent);
						findHandler.sendEmptyMessage(0);

					}
				} else {
					// 下载错误广播
					intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY,
							DownloadTaskContact.DOWNLOADING_TYPE_ERROR_VALUE);
					context.sendBroadcast(intent);
					findHandler.sendEmptyMessage(0);
				}
			} catch (Exception e) {
				Log.e("DownloadTaskManager", e.getMessage(), e);
				// 下载完成广播
				intent.putExtra(DownloadTaskContact.DOWNLOADING_TYPE_KEY,
						DownloadTaskContact.DOWNLOADING_TYPE_ERROR_VALUE);
				context.sendBroadcast(intent);
				findHandler.sendEmptyMessage(0);
//				throw new RuntimeException(e);
			}
		}
	}
}