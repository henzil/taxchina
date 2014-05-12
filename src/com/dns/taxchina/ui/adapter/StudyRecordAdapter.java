package com.dns.taxchina.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.DownloadTaskManager;
import com.dns.taxchina.service.model.VideoModel;
import com.dns.taxchina.ui.StudyRecordActivity;

/**
 * @author fubiao
 * @version create time:2013-10-16_上午11:35:00
 * @Description 学习记录 列表 adapter
 */
public class StudyRecordAdapter extends BaseAdapter {

	private StudyRecordActivity context;
	private List<VideoModel> list = new ArrayList<VideoModel>();
	
	private String currentVideoId = null;

	public StudyRecordAdapter(StudyRecordActivity context, String TAG) {
		this.context = context;
		currentVideoId = DownloadTaskManager.getInstance(context).downloadingId();
	}

	public void refresh(List<VideoModel> arg0) {
		list.clear();
		list.addAll(arg0);
		notifyDataSetChanged();
	}

	public void addData(List<VideoModel> arg0) {
		list.addAll(arg0);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public VideoModel getItem(int position) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.study_record_list_item, null);
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
		public VideoModel model;
		private Button studyRecordBtn;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.study_record_item_title_text);
			text = (TextView) view.findViewById(R.id.study_record_item_info_text);
			line = view.findViewById(R.id.study_record_item_line);
			studyRecordBtn = (Button) view.findViewById(R.id.study_record_item_btn);
		}

		public void update(final VideoModel baseItemModel, int positon) {
			model = baseItemModel;
			title.setText(model.getTitle());
			text.setText("" + model.getDownloadPercent() + "%");

			if (positon == getCount() - 1) {
				line.setVisibility(View.INVISIBLE);
			} else {
				line.setVisibility(View.VISIBLE);
			}
			if(context.type == StudyRecordActivity.ALREADOVER_TYEP){
				studyRecordBtn.setVisibility(View.GONE);
			} else {
				studyRecordBtn.setVisibility(View.VISIBLE);
				if(currentVideoId != null && currentVideoId.equals(model.getId())){
					studyRecordBtn.setText("暂停");
					studyRecordBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO 暂停一个下载
							DownloadTaskManager.getInstance(context).pauseCurrentTask();
						}
					});
				} else {
					studyRecordBtn.setText("下载");
					studyRecordBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO 暂停一个下载并去下载一个新的。
							DownloadTaskManager.getInstance(context).pauseCurrentTask(model);
						}
					});
				}
			}
		}
	}
}