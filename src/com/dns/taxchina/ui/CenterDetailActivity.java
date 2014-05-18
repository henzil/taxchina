package com.dns.taxchina.ui;

import netlib.util.AppUtil;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import com.dns.taxchina.R;
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

	private int type = -1;

	private TextView title, back;

	private String url = "file:///android_asset/index.html";

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
		webView = (WebView) findViewById(R.id.web_view);

		if (type == ACTIVATION_TYPE) {
			title.setText("会员卡激活");
			// TODO
			plug.webViewPlug(url, webView);
		} else if (type == SUGGESTION_TYPE) {
			title.setText("意见反馈");
			// TODO
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