package com.dns.taxchina.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.IndexTitleModel;
import com.dns.taxchina.ui.CourseListActivity;

/**
 * @author fubiao
 * @version create time:2014-5-5_上午11:39:47
 * @Description 首页 listView item title 布局
 */
public class IndexTitleLayout extends LinearLayout implements IndexListElement {

	private View view;
	private TextView title;
	private Button more;
	private IndexTitleModel indexTitleModel;

	public IndexTitleLayout(Context context) {
		super(context);
		initView(context);
	}

	@Override
	public void initView(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.index_title_layout, this, true);
		title = (TextView) view.findViewById(R.id.index_title_text);
		more = (Button) view.findViewById(R.id.index_more_btn);

		more.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), CourseListActivity.class);
				intent.putExtra(CourseListActivity.LIST_ID, indexTitleModel.getId());
				intent.putExtra(CourseListActivity.LIST_TITLE, indexTitleModel.getTitle());
				getContext().startActivity(intent);
				((Activity) getContext()).overridePendingTransition(R.anim.push_right_in, R.anim.no_anim);
			}
		});
	}

	@Override
	public void updateView(Object object, String TAG, String titleStr) {
		indexTitleModel = (IndexTitleModel) object;
		title.setText(indexTitleModel.getTitle());
		if (indexTitleModel.isHasMore()) {
			more.setVisibility(View.VISIBLE);
			more.setClickable(true);
		} else {
			more.setVisibility(View.GONE);
			more.setClickable(false);
		}
	}
}