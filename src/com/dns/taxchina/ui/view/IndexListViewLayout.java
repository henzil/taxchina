package com.dns.taxchina.ui.view;

import it.sephiroth.android.library.widget.HorizontalVariableListView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.IndexContentItemModel;
import com.dns.taxchina.ui.adapter.IndexListViewAdapter;

/**
 * @author fubiao
 * @version create time:2014-5-5_上午11:39:47
 * @Description 首页 listView item 布局
 */
public class IndexListViewLayout extends LinearLayout implements IndexListElement {

	private View view;
	private Context context;
	private HorizontalVariableListView listView;
	private IndexListViewAdapter indexListViewAdapter;

	public IndexListViewLayout(Context context) {
		super(context);
		initView(context);
		this.context = context;
	}

	@Override
	public void initView(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.index_listview_layout, this, true);
		listView = (HorizontalVariableListView) view.findViewById(R.id.horizontal_list_view);
	}

	@Override
	public void updateView(Object object, String TAG) {
		IndexContentItemModel indexContentItemModel = (IndexContentItemModel) object;
		indexListViewAdapter = new IndexListViewAdapter(context, TAG);
		listView.setAdapter(indexListViewAdapter);
		indexListViewAdapter.refresh(indexContentItemModel.getSubList());
	}
}