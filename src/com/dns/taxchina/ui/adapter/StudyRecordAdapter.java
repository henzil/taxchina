package com.dns.taxchina.ui.adapter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import netlib.util.SettingUtil;
import netlib.util.TouchUtil;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dns.taxchina.R;
import com.dns.taxchina.service.download.DownloadTaskManager;
import com.dns.taxchina.service.download.VideoDAO;
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
	public static final int DISMISS_DELETE = 0;
	public static final int SHOW_DELETE = 1;
	public int type;
	
	private HashSet<VideoModel> set = new HashSet<VideoModel>();

	public StudyRecordAdapter(StudyRecordActivity context, String TAG) {
		this.context = context;
	}

	public void setType(int type) {
		this.type = type;
		notifyDataSetChanged();
	}

	public void refresh(List<VideoModel> arg0) {
		list.clear();
		list.addAll(arg0);
		set = DownloadTaskManager.getInstance(context).getCurrentDownLoadSet();
		notifyDataSetChanged();
	}
	
	public void onlyRefresh() {
		set = DownloadTaskManager.getInstance(context).getCurrentDownLoadSet();
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
		private ImageView delete;
		private Button studyRecordBtn;
		private View deleteLayout;

		public ViewHolder(View view) {
			title = (TextView) view.findViewById(R.id.study_record_item_title_text);
			text = (TextView) view.findViewById(R.id.study_record_item_info_text);
			line = view.findViewById(R.id.study_record_item_line);
			delete = (ImageView) view.findViewById(R.id.delete_img);
			studyRecordBtn = (Button) view.findViewById(R.id.study_record_item_btn);
			deleteLayout = view.findViewById(R.id.deleteLayout);
		}

		public void update(final VideoModel baseItemModel, final int positon) {
			model = baseItemModel;
			title.setText(model.getTitle());
			text.setText(changeMB(model.getDownloadedSize()) + "/" + changeMB(model.getVideoSize()) + "  ("
					+ model.getDownloadPercent() + "%)");
			TouchUtil.createTouchDelegate(delete, 50);
			TouchUtil.createTouchDelegate(studyRecordBtn, 30);
			if (type == DISMISS_DELETE) {
				studyRecordBtn.setVisibility(View.VISIBLE);
				studyRecordBtn.setClickable(true);
				delete.setVisibility(View.GONE);
				deleteLayout.setVisibility(View.GONE);
				delete.setClickable(false);
				deleteLayout.setClickable(false);
				TouchUtil.createTouchDelegate(delete, 50);
				TouchUtil.createTouchDelegate(studyRecordBtn, 30);
				if (context.type == StudyRecordActivity.ALREADOVER_TYEP) {
					studyRecordBtn.setVisibility(View.GONE);
				} else {
					studyRecordBtn.setVisibility(View.VISIBLE);
					String currentVideoId = DownloadTaskManager.getInstance(context).downloadingId();
					if (currentVideoId != null && currentVideoId.equals(model.getId())) {
						studyRecordBtn.setText(context.getResources().getString(R.string.pause));
						studyRecordBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 暂停一个下载，并移除当前下载队列中。
								DownloadTaskManager.getInstance(context).stopOneTask(model);
							}
						});
					} else if (set.contains(model)) {
						studyRecordBtn.setText(context.getResources().getString(R.string.wait));
						studyRecordBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO
							}
						});
					} else {
						studyRecordBtn.setText(context.getResources().getString(R.string.downlaod));
						studyRecordBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// 将一个下载加载到下载队列中。
								startTask(baseItemModel);
							}
						});
					}
				}
			} else {
				studyRecordBtn.setVisibility(View.GONE);
				studyRecordBtn.setClickable(false);
				delete.setVisibility(View.VISIBLE);
				deleteLayout.setVisibility(View.VISIBLE);
				delete.setClickable(true);
				deleteLayout.setClickable(true);
				TouchUtil.createTouchDelegate(delete, 50);
				TouchUtil.createTouchDelegate(studyRecordBtn, 30);
			}
			deleteLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteDialog(baseItemModel);
				}
			});
			delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteDialog(baseItemModel);
				}
			});
			if (positon == getCount() - 1) {
				line.setVisibility(View.INVISIBLE);
			} else {
				line.setVisibility(View.VISIBLE);
			}
		}
	}

	private void startTask(final VideoModel model) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			DownloadTaskManager.getInstance(context).startOneTask(model);
		} else {
			if (SettingUtil.getWifiDoSomeThing(context)) {
				new AlertDialog.Builder(context)
						.setTitle(context.getString(R.string.wifi_tip))
						.setMessage(context.getResources().getString(R.string.study_record_3G_tip))
						.setPositiveButton(context.getResources().getString(R.string.downlaod),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										DownloadTaskManager.getInstance(context).startOneTask(model);
									}
								})
						.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).create().show();
			} else {
				new AlertDialog.Builder(context)
						.setTitle(context.getString(R.string.wifi_tip))
						.setMessage(context.getResources().getString(R.string.this_video_not_to_download))
						.setPositiveButton(context.getResources().getString(R.string.sure),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
//										DownloadTaskManager.getInstance(context).startOneTask(model);
									}
								}).create().show();
			}
		}
	}

	private void deleteDialog(final VideoModel model) {
		new AlertDialog.Builder(context).setTitle(context.getString(R.string.wifi_tip))
				.setMessage(context.getString(R.string.study_record_delete_tip))
				.setPositiveButton(context.getString(R.string.sure), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						DownloadTaskManager.getInstance(context).deleteTask(model);
						VideoDAO videoDAO = new VideoDAO(context);
						videoDAO.remove(model.getId());
						if (model.getVideoPath() != null) {
							File file = new File(model.getVideoPath());
							if (file != null && file.exists()) {
								file.delete();
							}
						}
						context.initDBData();
						if (deleteListener != null) {
							deleteListener.doDelete();
						}
					}
				}).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).create().show();
	}

	private String changeMB(String size) {
		float f = 0;
		if (size != null && !size.equals("")) {
			f = Float.parseFloat(size);
			DecimalFormat fnum = new DecimalFormat("##0.00");
			String dd = fnum.format(f / 1000 / 1000);
			size = dd + "MB";
		} else {
			size = "0MB";
		}
		return size;
	}

	public DeleteListener deleteListener;

	public void setDeleteListener(DeleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}

	public interface DeleteListener {
		public void doDelete();
	}
}