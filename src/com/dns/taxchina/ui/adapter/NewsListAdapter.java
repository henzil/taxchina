package com.dns.taxchina.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dns.taxchina_pad.R;
import com.dns.taxchina.service.model.BaseItemModel;

/**
 * @author fubiao
 * @version create time:2013-10-16_上午11:35:00
 * @Description 咨询 列表 adapter
 */
public class NewsListAdapter extends BaseAdapter {

	private Context context;
	private List<BaseItemModel> list = new ArrayList<BaseItemModel>();

	public NewsListAdapter(Context context, String TAG) {
		this.context = context;
	}

	public void refresh(List<BaseItemModel> arg0) {
		list.clear();
		list.addAll(arg0);
		notifyDataSetChanged();
	}

	public void addData(List<BaseItemModel> arg0) {
		list.addAll(arg0);
		notifyDataSetChanged();
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.news_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.update(list.get(position), position);
		return convertView;
	}

	public class ViewHolder {
		private TextView title, text;
		private View line;
		public BaseItemModel model;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.course_item_title_text);
			text = (TextView) view.findViewById(R.id.course_item_info_text);
			line = view.findViewById(R.id.course_item_line);
		}

		public void update(final BaseItemModel baseItemModel, int positon) {
			model = baseItemModel;
			title.setText(model.getTitle());
			text.setText(model.getInfo());

			if (positon == getCount() - 1) {
				line.setVisibility(View.INVISIBLE);
			} else {
				line.setVisibility(View.VISIBLE);
			}
		}
	}
}