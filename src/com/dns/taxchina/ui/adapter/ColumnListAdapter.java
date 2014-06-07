package com.dns.taxchina.ui.adapter;import java.util.ArrayList;import java.util.HashMap;import java.util.List;import java.util.Map;import netlib.net.AsyncTaskLoaderImage;import android.content.Context;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.BaseAdapter;import android.widget.ImageView;import android.widget.TextView;import com.dns.taxchina.R;/** * @author fubiao * @version create time:2013-10-16_上午11:35:00 * @Description 栏目列表 adapter */public class ColumnListAdapter extends BaseAdapter {	private Context context;	private String TAG;//	private Handler mHandler = new Handler();	private Map<String, String> urlMap;	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();	public ColumnListAdapter(Context context, String TAG, List<Map<String, Object>> list) {		this.context = context;		this.TAG = TAG;		urlMap = new HashMap<String, String>();		this.list = list;	}	public void refresh(List<Map<String, Object>> arg0) {		list.clear();		list.addAll(arg0);		notifyDataSetChanged();	}	public void addData(List<Map<String, Object>> arg0) {		list.addAll(arg0);		notifyDataSetChanged();	}	@Override	public int getCount() {		return list.size();	}	@Override	public Object getItem(int position) {		return list.get(position);	}	@Override	public long getItemId(int position) {		return position;	}	@Override	public View getView(int position, View convertView, ViewGroup parent) {		ViewHolder holder = null;		if (convertView == null) {			convertView = LayoutInflater.from(context).inflate(R.layout.column_list_item, null);			holder = new ViewHolder(convertView);			convertView.setTag(holder);		} else {			holder = (ViewHolder) convertView.getTag();		}		holder.update(list.get(position), position);		return convertView;	}	public class ViewHolder {		private ImageView icon;		private TextView title;		private View line;		public Map<String, Object> mapData;		public ViewHolder(View view) {			icon = (ImageView) view.findViewById(R.id.column_item_img);			title = (TextView) view.findViewById(R.id.column_item_title_text);			line = view.findViewById(R.id.column_item_line);		}		public void update(final Map<String, Object> data, int positon) {			mapData = data;			title.setText(mapData.get("name") + "（"+ mapData.get("count") +"）");			icon.setImageResource(R.drawable.default_116x75);//			if (model.getImage() != null && !model.getImage().equals("")) {//				AsyncTaskLoaderImage.getInstance(context).loadAsync(TAG, model.getImage(), icon, new BitmapImageCallback() {//					@Override//					public void onImageLoaded(final Bitmap bitmap, final String url) {//						mHandler.post(new Runnable() {//							@Override//							public void run() {//								if (bitmap != null && !bitmap.isRecycled()) {//									if (context != null) {//										urlMap.put(model.getImage(), "");//										TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(android.R.color.transparent),//												new BitmapDrawable(context.getResources(), bitmap) });//										icon.setImageDrawable(td);//										td.startTransition(200);//									}//								} else {//									icon.setImageResource(R.drawable.default_116x75);//								}//							}//						});//					}//				});//			}			if (positon == getCount() - 1) {				line.setVisibility(View.INVISIBLE);			} else {				line.setVisibility(View.VISIBLE);			}		}	}	public void recycleBitmaps() {		AsyncTaskLoaderImage.getInstance(context).recycleBitmaps(TAG, urlMap);	}}