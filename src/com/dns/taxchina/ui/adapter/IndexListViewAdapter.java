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

import com.dns.taxchina.R;
import com.dns.taxchina.service.model.BaseItemModel;

/**
 * @author fubiao
 * @version create time:2014-4-25_上午10:54:04
 * @Description 首页 横向listview item adapter
 */
public class IndexListViewAdapter extends BaseAdapter {

	private Context context;
	private String TAG;
	private Handler mHandler = new Handler();
	private Map<String, String> urlMap;

	private List<BaseItemModel> list = new ArrayList<BaseItemModel>();

	public IndexListViewAdapter(Context context, String TAG) {
		this.context = context;
		this.TAG = TAG;
		urlMap = new HashMap<String, String>();
	}

	public void refresh(List<BaseItemModel> list) {
		this.list.clear();
		this.list.addAll(list);
		notifyDataSetChanged();
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.index_listview_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.update(list.get(position));

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		return convertView;
	}

	public class ViewHolder {
		private ImageView pic;
		private TextView title;
		public BaseItemModel model;

		public ViewHolder(View convertView) {
			pic = (ImageView) convertView.findViewById(R.id.index_item_img);
			title = (TextView) convertView.findViewById(R.id.index_item_text);
		}

		public void update(final BaseItemModel model) {
			this.model = model;
			title.setText(model.getTitle());
			pic.setImageResource(R.drawable.default_103x66);
			if (model.getImage() != null && !model.getImage().equals("")) {
				AsyncTaskLoaderImage.getInstance(context).loadAsync(TAG, model.getImage(), pic, new BitmapImageCallback() {
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
										pic.setImageDrawable(td);
										td.startTransition(200);
									} else {
										bitmap.recycle();
									}
								} else {
									pic.setImageResource(R.drawable.default_103x66);
								}
							}
						});
					}
				});
			}
		}
	}

	public void recycleBitmaps() {
		AsyncTaskLoaderImage.getInstance(context).recycleBitmaps(TAG, urlMap);
	}
}