package com.dns.taxchina.ui;

import netlib.util.AppUtil;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.dns.taxchina.R;

/**
 * @author fubiao
 * @version create time:2014-3-27_上午11:16:05
 * @Description 网址广告
 */
public class ADWebActivity extends BaseActivity {

	private String url;
	private WebView webView;
	public static final String AD_WEB_URL = "ad_web_url";

	@Override
	protected void initData() {
		url = getIntent().getStringExtra(AD_WEB_URL);
		Log.d(TAG, "laoding website url : " + url);
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.activity_ad_website_activity);
		webView = (WebView) findViewById(R.id.wv_ad_website);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initWidgetActions() {
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void showNetDialog() {
		if (AppUtil.isActivityTopStartThisProgram(this, ADWebActivity.class.getName())) {
			netDialog.show();
		}
	}
}