package com.dns.taxchina.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import netlib.net.AsyncTaskLoaderImage;
import netlib.net.AsyncTaskLoaderImage.BitmapImageCallback;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dns.taxchina_pad.R;
import com.dns.taxchina.service.model.BaseItemModel;

/**
 * @author fubiao
 * @version create time:2013-10-16_上午11:35:00
 * @Description 课程收藏列表 adapter
 */
public class CollectionListAdapter extends BaseAdapter {

	private Context context;
	private String TAG;
	private Handler mHandler = new Handler();
	private Map<String, String> urlMap;
	private List<BaseItemModel> list = new ArrayList<BaseItemModel>();
	public static final int DISMISS_DELETE = 0;
	public static final int SHOW_DELETE = 1;
	public int type;

	public CollectionListAdapter(Context context, String TAG) {
		this.context = context;
		this.TAG = TAG;
		urlMap = new HashMap<String, String>();
	}

	public void setType(int type) {
		this.type = type;
		notifyDataSetChanged();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.collection_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.update(list.get(position), position);
		return convertView;
	}

	public class ViewHolder {
		private ImageView icon, delete;
		private TextView title, text;
		private View line;
		public BaseItemModel model;

		public ViewHolder(View view) {
			icon = (ImageView) view.findViewById(R.id.collection_item_img);
			title = (TextView) view.findViewById(R.id.collection_item_title_text);
			text = (TextView) view.findViewById(R.id.collection_item_info_text);
			delete = (ImageView) view.findViewById(R.id.delete_img);
			line = view.findViewById(R.id.collection_item_line);
		}

		public void update(final BaseItemModel baseItemModel, int positon) {
			model = baseItemModel;
			title.setText(model.getTitle());
			text.setText(model.getInfo());
			if (type == DISMISS_DELETE) {
				delete.setVisibility(View.GONE);
				delete.setClickable(false);
			} else {
				delete.setVisibility(View.VISIBLE);
				delete.setClickable(true);
			}

			icon.setImageResource(R.drawable.default_116x75);
			if (model.getImage() != null && !model.getImage().equals("")) {
				AsyncTaskLoaderImage.getInstance(context).loadAsync(TAG, model.getImage(), icon, new BitmapImageCallback() {
					@Override
					public void onImageLoaded(final Bitmap bitmap, final String url) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (bitmap != null && !bitmap.isRecycled()) {
									if (context != null) {
										urlMap.put(model.getImage(), "");
										TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(android.R.color.transparent),
												new BitmapDrawable(context.getResources(), bitmap) });
										icon.setImageDrawable(td);
										td.startTransition(200);
									} else {
										bitmap.recycle();
									}
								} else {
									icon.setImageResource(R.drawable.default_116x75);
								}
							}
						});
					}
				});
			}

			delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (collectionDeleteListener != null) {
						collectionDeleteListener.delete(model.getId());
					}
				}
			});

			if (positon == getCount() - 1) {
				line.setVisibility(View.INVISIBLE);
			} else {
				line.setVisibility(View.VISIBLE);
			}
		}
	}

	private CollectionDeleteListener collectionDeleteListener;

	public void setCollectionDeleteListener(CollectionDeleteListener collectionDeleteListener) {
		this.collectionDeleteListener = collectionDeleteListener;
	}

	public interface CollectionDeleteListener {
		public void delete(String id);
	}

	public void recycleBitmaps() {
		AsyncTaskLoaderImage.getInstance(context).recycleBitmaps(TAG, urlMap);
	}
}