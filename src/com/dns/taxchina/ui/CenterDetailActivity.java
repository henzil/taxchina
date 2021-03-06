package com.dns.taxchina.ui;

import netlib.util.AppUtil;
import netlib.util.TouchUtil;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.ui.util.LoginUtil;
import com.dns.taxchina.ui.widget.WebViewPlug;
import com.dns.taxchina.ui.widget.WebViewPlug.LoadWebViewListener;

/**
 * @author fubiao
 * @version create time:2014-5-6_下午1:39:04
 * @Description 学员中心 webview Activity
 */
public class CenterDetailActivity extends BaseActivity {

	private WebView webView;

	protected WebViewPlug plug;
	
	protected ProgressBar progressBar;
	
	protected Handler mHandler = new Handler();

	private int type = -1;

	private TextView title, back;

	public static final String DETAIL_TYPE = "detail_type";
	public static final int ACTIVATION_TYPE = 0;
	public static final int SUGGESTION_TYPE = 1;
	public static final int ABOUT_US_TYPE = 2;
	public static final int CONTANCT_US_TYPE = 3;

	@Override
	protected void initData() {
		Intent intent = getIntent();

		type = intent.getIntExtra(DETAIL_TYPE, -1);
		plug = new WebViewPlug(this);
		super.initData();
	}

	@Override
	protected void initViews() {
		setContentView(R.layout.center_detail_activity);
		title = (TextView) findViewById(R.id.title_text);
		back = (TextView) findViewById(R.id.back_text);
		TouchUtil.createTouchDelegate(back, 30);
		webView = (WebView) findViewById(R.id.web_view);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		
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

		if (type == ACTIVATION_TYPE) {
			title.setText("会员卡激活");
			String url = "http://tz1.taxchina.com/wcm/cswsp/app/appCard/CardActivate.aspx";
			url = url + "?from=Android&userId=" + LoginUtil.getUserId(CenterDetailActivity.this);
			plug.webViewPlug(url, webView);
		} else if (type == SUGGESTION_TYPE) {
			title.setText("意见反馈");
			String url = "http://tz1.taxchina.com/wcm/cswsp/app/yijian.aspx";
			if (LoginUtil.isLogin(CenterDetailActivity.this)) {
				url = url + "?userId=" + LoginUtil.getUserId(CenterDetailActivity.this);
			}
			plug.webViewPlug(url, webView);
		} else if (type == ABOUT_US_TYPE) {
			title.setText("关于我们");
			plug.webViewPlug("http://tz1.taxchina.com/wcm/cswsp/app/GuanYuWoMen.aspx", webView);
		} else if (type == CONTANCT_US_TYPE) {
			title.setText("联系我们");
			plug.webViewPlug("http://tz1.taxchina.com/wcm/cswsp/app/LianXiWoMen.aspx", webView);
		}
	}

	@Override
	protected void initWidgetActions() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		plug.setLoadWebViewListener(new LoadWebViewListener() {
			@Override
			public void onPageFinished() {

			}

			@Override
			public void onPageError() {

			}
		});
	}

	@Override
	protected void showNetDialog() {
		if (AppUtil.isActivityTopStartThisProgram(this, CenterDetailActivity.class.getName())) {
			netDialog.show();
		}
	}
}