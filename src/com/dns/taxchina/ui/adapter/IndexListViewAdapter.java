package com.dns.taxchina.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.BaseItemModel;

/**
 * @author fubiao
 * @version create time:2014-4-25_上午10:54:04
 * @Description 首页  横向listview item adapter
 */
public class IndexListViewAdapter extends BaseAdapter {

	private Context context;
	private String TAG;

	private List<BaseItemModel> list = new ArrayList<BaseItemModel>();

	public IndexListViewAdapter(Context context, String TAG) {
		this.context = context;
		this.TAG = TAG;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.index_listview_item, null);
		}

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		return convertView;
	}

	public void refresh(List<BaseItemModel> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
	}
}