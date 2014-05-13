package com.dns.taxchina.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.DownloadTaskManager;
import com.dns.taxchina.service.download.VideoDAO;
import com.dns.taxchina.service.model.BaseItemModel;
import com.dns.taxchina.service.model.DownloadTask;
import com.dns.taxchina.service.model.VideoModel;
import com.dns.taxchina.ui.util.LoginUtil;

@SuppressLint("SetJavaScriptEnabled")
@SuppressWarnings("deprecation")
public class WebViewPlug {
	private Activity context;
	
	private String title;

	public WebViewPlug(Activity context) {
		super();
		this.context = context;
	}

	public void webViewPlug(String url, WebView mWebView) {
		this.title = "";
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.requestFocus(View.FOCUSABLES_TOUCH_MODE);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过js打开新的窗口
		mWebView.setHorizontalScrollBarEnabled(false);

		mWebView.addJavascriptInterface(new VideoClickListener(), "video");
		mWebView.addJavascriptInterface(new LoginClickListener(), "user");
		if (url == null) {
			url = "";
		}
		mWebView.loadUrl(url);
	}
	
	public void webViewPlug(String url, WebView mWebView, BaseItemModel model) {
		this.title = model.getTitle();
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setSupportZoom(false);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.requestFocus(View.FOCUSABLES_TOUCH_MODE);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // 支持通过js打开新的窗口
		mWebView.setHorizontalScrollBarEnabled(false);

		mWebView.addJavascriptInterface(new VideoClickListener(), "video");
		mWebView.addJavascriptInterface(new LoginClickListener(), "user");
		if (url == null) {
			url = "";
		}
		url = url + "?from=Android&docId="+model.getId();
		String userId = LoginUtil.getUserId(context);
		if(userId != null && !userId.equals("") ){
			url = url + "&userId=" + userId;
		}
		
		Log.e("tag", "url = " + url);
		mWebView.loadUrl(url);

	}

	private WebViewClient mWebViewClient = new WebViewClient() {
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			if (loadWebViewListener != null) {
				loadWebViewListener.onPageError();
			}
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (loadWebViewListener != null) {
				loadWebViewListener.onPageFinished();
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (view != null) {
				view.loadUrl(url);
			}
			return true;
		}
	};

	public interface LoadWebViewListener {
		void onPageFinished();

		void onPageError();
	}

	private LoadWebViewListener loadWebViewListener;

	public LoadWebViewListener getLoadWebViewListener() {
		return loadWebViewListener;
	}

	public void setLoadWebViewListener(LoadWebViewListener loadWebViewListener) {
		this.loadWebViewListener = loadWebViewListener;
	}


	final class VideoClickListener {
		@JavascriptInterface
		public void download(String url, String type, String id) {
			// TODO 下载视频方法
			Log.e("tag", "url = "+url);
			Log.e("tag", "type = "+type);
			Log.e("tag", "id = "+id);
			VideoDAO videoDAO = new VideoDAO(context);
			
			VideoModel videoModel = videoDAO.findById(id);
			if(videoModel == null){
				// 去下载
				videoModel = new VideoModel();
				videoModel.setId(id);
				videoModel.setUrl(url);
				videoModel.setTitle(title);
				videoDAO.add(videoModel);
				DownloadTask downloadTask = new DownloadTask();
	        	downloadTask.setFileId(id);
		        downloadTask.setVideo(videoModel);
				DownloadTaskManager.getInstance(context).addTask(downloadTask, videoModel);
			} else {
				if(videoModel.getDownloadPercent() < 100){
					// 正在下载中，弹出提示。
					Toast.makeText(context, R.string.this_video_downloading, Toast.LENGTH_LONG).show();
					
				} else {
					// TODO 去播放页面
					
				}
			}
			
		}
	}

	final class LoginClickListener {
		@JavascriptInterface
		public void doLogin() {
			Log.e("tag", "执行登陆");
			LoginUtil.gotoLogin(context);
		}
	}

}