package com.dns.taxchina.ui;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dns.taxchina.R;

/**
 * @author fubiao
 * @version create time:2014-5-9_下午1:36:57
 * @Description 学习记录Activity
 */
public class StudayRecordActivity extends BaseActivity {

	private TextView back;
	private Button alreadOver, notOver;

	@Override
	protected void initData() {

	}

	@Override
	protected void initViews() {
		setContentView(R.layout.study_record_activity);
		back = (TextView) findViewById(R.id.back_text);
		alreadOver = (Button) findViewById(R.id.already_over_btn);
		notOver = (Button) findViewById(R.id.not_over_btn);

		alreadOver.setSelected(true);
	}

	@Override
	protected void initWidgetActions() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		alreadOver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn(v);
			}
		});

		notOver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				updateBtn(v);
			}
		});

	}

	private void updateBtn(View v) {
		alreadOver.setSelected(false);
		notOver.setSelected(false);
		v.setSelected(true);
	}
}