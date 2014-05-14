package com.dns.taxchina.ui;

import netlib.util.ResourceUtil;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.ui.widget.LoadingDialog;

public abstract class BaseFragmentActivity extends FragmentActivity {

	protected LoadingDialog loadingDialog;
	protected ResourceUtil resourceUtil;
	protected String TAG;
	protected RelativeLayout failBox;
	protected ImageView failImg;
	protected TextView failText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBaseData(savedInstanceState);
		initBaseViews();
	}

	protected void initBaseData(Bundle savedInstanceState) {
		resourceUtil = ResourceUtil.getInstance(getApplicationContext());
		TAG = toString();
		initDialog();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	protected void initBaseViews() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	protected void initFailView(View view) {
		failBox = (RelativeLayout) view.findViewById(R.id.no_data_box);
		failImg = (ImageView) view.findViewById(R.id.no_data_img);
		failText = (TextView) view.findViewById(R.id.no_data_text);
	}
	
	protected void emptyFailView() {
		emptyFailView(getString(R.string.no_content));
	}
	
	protected void emptyFailView(String emptyStr) {
		failBox.setVisibility(View.VISIBLE);
		failImg.setBackgroundResource(R.drawable.no_data_img);
		failText.setText(emptyStr);
	}
	
	public interface OnEmptyFailViewClickedListener{
		void onFailViewClicked();
	}

	protected void errorFailView(final OnEmptyFailViewClickedListener listener) {
		failBox.setVisibility(View.VISIBLE);
		failImg.setBackgroundResource(R.drawable.no_data_img);
		failText.setText(getString(R.string.cache_load_more));
		failBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(listener != null)
					listener.onFailViewClicked();
			}
		});
	}

	protected void resetFailView() {
		failBox.setVisibility(View.GONE);
	}

	protected abstract void initData();

	protected abstract void initViews();

	protected abstract void initWidgetActions();
	
	private void initDialog() {
		if (loadingDialog == null) {
			loadingDialog = new LoadingDialog(BaseFragmentActivity.this, R.style.my_dialog);
			loadingDialog.setCancelable(true);
			loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (loadingDialog != null)
							loadingDialog.cancel();
					}
					return true;
				}
			});
		}
	}
}
