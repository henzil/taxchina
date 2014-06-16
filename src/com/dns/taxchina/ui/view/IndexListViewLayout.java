package com.dns.taxchina.ui.view;

import it.sephiroth.android.library.widget.HorizontalVariableListView;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.dns.taxchina_pad.R;
import com.dns.taxchina.service.model.BaseItemModel;
import com.dns.taxchina.ui.DetailActivity;
import com.dns.taxchina.ui.adapter.IndexListViewAdapter;
import com.dns.taxchina.ui.adapter.IndexListViewAdapter.ViewHolder;

/**
 * @author fubiao
 * @version create time:2014-5-5_上午11:39:47
 * @Description 首页 listView item 布局
 */
public class IndexListViewLayout extends LinearLayout implements IndexListElement {

	private View view;
	private Context context;
	private String titleStr;
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

		listView.setOnItemClickedListener(new HorizontalVariableListView.OnItemClickedListener() {

			@Override
			public boolean onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg1.getTag() instanceof ViewHolder) {
					BaseItemModel model = ((ViewHolder) arg1.getTag()).model;
					Intent intent = new Intent(getContext(), DetailActivity.class);
					intent.putExtra(DetailActivity.DETAIL_MODEL, model);
					intent.putExtra(DetailActivity.DETAIL_TITLE, titleStr);
					getContext().startActivity(intent);
					((Activity) getContext()).overridePendingTransition(R.anim.push_right_in, R.anim.no_anim);
				}
				return false;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateView(Object object, String TAG, String titleStr) {
		this.titleStr = titleStr;
		List<BaseItemModel> list = (List<BaseItemModel>) object;
		indexListViewAdapter = new IndexListViewAdapter(context, TAG);
		listView.setAdapter(indexListViewAdapter);
		indexListViewAdapter.refresh(list);
	}

	public void goneView() {
		indexListViewAdapter.recycleBitmaps();
	}
}