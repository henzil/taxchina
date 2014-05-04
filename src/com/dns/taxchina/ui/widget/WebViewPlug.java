package com.dns.taxchina.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressWarnings("deprecation")
public class WebViewPlug {
	private Context context;

	public WebViewPlug(Context context) {
		super();
		this.context = context;
	}

	public void webViewPlug(String url, WebView mWebView) {
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
			
		}
	}

	final class LoginClickListener {
		@JavascriptInterface
		public void doLogin() {
			// TODO
			Log.e("tag", "执行登陆");
		}
	}

}