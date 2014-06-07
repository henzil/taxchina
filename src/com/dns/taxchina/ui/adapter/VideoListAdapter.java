package com.dns.taxchina.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.InternalVideoModel;

/**
 * @author fubiao
 * @version create time:2013-10-16_上午11:35:00
 * @Description 视频列表 adapter
 */
public class VideoListAdapter extends BaseAdapter {

	private List<InternalVideoModel> list = new ArrayList<InternalVideoModel>();
	private Context context;

	public VideoListAdapter(Context context, String TAG, List<InternalVideoModel> list) {
		this.context = context;
		if(list != null){
			this.list.addAll(list);
		}
	}

	public void refresh(List<InternalVideoModel> arg0) {
		list.clear();
		list.addAll(arg0);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public InternalVideoModel getItem(int position) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
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
		public InternalVideoModel model;
		private ImageView delete;
		private Button studyRecordBtn;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.video_item_title_text);
			text = (TextView) view.findViewById(R.id.video_item_info_text);
			line = view.findViewById(R.id.video_item_line);
			delete = (ImageView) view.findViewById(R.id.delete_img);
			studyRecordBtn = (Button) view.findViewById(R.id.video_item_btn);
			delete.setVisibility(View.GONE);
			studyRecordBtn.setVisibility(View.GONE);
		}

		public void update(final InternalVideoModel baseItemModel, final int positon) {
			model = baseItemModel;
			title.setText(model.getTitle());
			text.setText(changeMB(model.getSize()));

			if (positon == getCount() - 1) {
				line.setVisibility(View.INVISIBLE);
			} else {
				line.setVisibility(View.VISIBLE);
			}
		}

	}

	private String changeMB(String size) {
		long l = 0;
		if (size != null && !size.equals("")) {
			l = Long.parseLong(size);
		}

		size = (l / 1000 / 1000) + "MB";

		return size;
	}
}