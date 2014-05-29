package com.dns.taxchina.ui;

import netlib.util.AppUtil;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.BaseItemModel;
import com.dns.taxchina.ui.widget.TouchLinearLayout;

/**
 * @author fubiao
 * @version create time:2014-3-27_上午11:16:05
 * @Description 资讯详情页面
 */
public class NewsDetailActivity extends BaseActivity {

	private TextView back, title;
	private BaseItemModel baseItemModel;
	private WebView webView;
	protected ProgressBar progressBar;
	private TouchLinearLayout touchLinearLayout;
	protected Handler mHandler = new Handler();
	public static final String NEWS_DETAIL_MODEL = "news_detail_model";

	@Override
	protected void initData() {
		baseItemModel = (BaseItemModel) getIntent().getSerializableExtra(NEWS_DETAIL_MODEL);
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.news_detail_activity);
		back = (TextView) findViewById(R.id.back_text);
		title = (TextView) findViewById(R.id.title_text);
		touchLinearLayout = (TouchLinearLayout) findViewById(R.id.touchLinearLayout);
		webView = (WebView) findViewById(R.id.web_view);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		title.setText(baseItemModel.getDetailTitle());
		
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initWidgetActions() {
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				progressBar.setProgress(progress);
				if (progress == 100) {
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							progressBar.setVisibility(View.GONE);
						}
					}, 300);
				} else {
					progressBar.setVisibility(View.VISIBLE);
				}
			}
		});
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setInitialScale(100);
		String url = baseItemModel.getUrl();
		url = url + "?from=Android&docId=" + baseItemModel.getId();
		
		Log.d("tag", "url = " + url);
		webView.loadUrl(url);
		
		touchLinearLayout.OnLayoutGestureListener(new TouchLinearLayout.OnLayoutGestureListener() {

			@Override
			public void next() {
			}

			@Override
			public void click() {
			}

			@Override
			public void back() {
				finish();
			}
		});
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
		if (AppUtil.isActivityTopStartThisProgram(this, NewsDetailActivity.class.getName())) {
			netDialog.show();
		}
	}
}