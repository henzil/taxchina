package com.dns.taxchina.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.BaseItemModel;

/**
 * @author fubiao
 * @version create time:2014-4-25_上午10:54:04
 * @Description 首页 listview item adapter
 */
public class IndexListViewAdapter extends BaseAdapter {

	private Context context;
	private String title;
	private List<BaseItemModel> list;

	public IndexListViewAdapter(List<BaseItemModel> list, Context context) {
		this.list = list;
		this.context = context;
	}

	public List<BaseItemModel> getList() {
		return list;
	}

	public void setList(List<BaseItemModel> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public BaseItemModel getItem(int position) {
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

		TextView textView = (TextView) convertView.findViewById(R.id.textView1);
		textView.setText(list.get(position).getTitle());
		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		return convertView;
	}
}