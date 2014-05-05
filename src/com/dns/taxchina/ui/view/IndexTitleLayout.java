package com.dns.taxchina.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dns.taxchina.R;
import com.dns.taxchina.service.model.IndexTitleModel;

/**
 * @author fubiao
 * @version create time:2014-5-5_上午11:39:47
 * @Description 首页 listView item title 布局
 */
public class IndexTitleLayout extends LinearLayout implements IndexListElement {

	private View view;
	private TextView title;
	private Button moreBtn;

	public IndexTitleLayout(Context context) {
		super(context);
		initView(context);
	}

	@Override
	public void initView(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.index_title_layout, this, true);
		title = (TextView) view.findViewById(R.id.index_title_text);
		moreBtn = (Button) view.findViewById(R.id.index_more_btn);
	}

	@Override
	public void updateView(Object object, String TAG) {
		IndexTitleModel indexTitleModel = (IndexTitleModel) object;
		title.setText(indexTitleModel.getTitle());
		if (indexTitleModel.isHasMore()) {
			moreBtn.setVisibility(View.VISIBLE);
			moreBtn.setClickable(true);
		} else {
			moreBtn.setVisibility(View.GONE);
			moreBtn.setClickable(false);
		}
	}
}