package com.dns.taxchina.ui.fragment;import netlib.net.AsyncTaskLoaderImage;import netlib.net.AsyncTaskLoaderImage.BitmapImageCallback;import android.content.Intent;import android.graphics.Bitmap;import android.os.Bundle;import android.os.Handler;import android.util.Log;import android.view.LayoutInflater;import android.view.MotionEvent;import android.view.View;import android.view.View.OnTouchListener;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.TextView;import com.dns.taxchina_pad.R;import com.dns.taxchina.service.model.NewsItemHeadModel;import com.dns.taxchina.ui.NewsDetailActivity;/** * @author fubiao * @version create time:2014-3-10_下午3:17:52 * @Description 新闻 item head fragment */public class NewsItemHeadFragment extends BaseFragment {	private NewsItemHeadModel ad;	public static final String INTENT = "intent";	private ImageView imageView;	private TextView title;		@SuppressWarnings("unused")	private String column;	private Handler mHandler = new Handler();	public static NewsItemHeadFragment newInstance(NewsItemHeadModel model, String column) {		NewsItemHeadFragment f = new NewsItemHeadFragment();		Bundle args = new Bundle();		args.putSerializable(INTENT, model);		args.putString("column", column);		f.setArguments(args);		return f;	}	@Override	protected void initData() {		this.ad = (NewsItemHeadModel) getArguments().getSerializable(INTENT);		this.column = getArguments().getString("column");	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.news_item_head_fragment, container, false);		imageView = (ImageView) view.findViewById(R.id.imageView);		title = (TextView) view.findViewById(R.id.title);		return view;	}	@Override	protected void initWidgetActions() {		title.setText(ad.getTitle());		String imgUrl = ad.getBigImage();		imageView.setImageResource(R.drawable.default_360x125);		if (imgUrl != null && !imgUrl.equals("")) {			AsyncTaskLoaderImage.getInstance(context).loadAsync(TAG, imgUrl, imageView, new BitmapImageCallback() {				@Override				public void onImageLoaded(final Bitmap bitmap, final String url) {					mHandler.post(new Runnable() {						@Override						public void run() {							if (bitmap != null && !bitmap.isRecycled()) {								if (getActivity() != null) {									Log.e("tag", "执行到这里~~~~~~~~bitmap.isRecycled() = " + bitmap.isRecycled());//									TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(android.R.color.transparent),//											new BitmapDrawable(context.getResources(), bitmap) });//									imageView.setImageDrawable(td);//									td.startTransition(200);									imageView.setImageBitmap(bitmap);									Log.e("tag", "执行到这里~~~~~~~~bitmap.isRecycled() = " + bitmap.isRecycled());								}							} else {								imageView.setImageResource(R.drawable.default_360x125);							}						}					});				}			});		}		imageView.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				Intent intent = new Intent(context, NewsDetailActivity.class);				intent.putExtra(NewsDetailActivity.NEWS_DETAIL_MODEL, ad);				startActivity(intent);			}		});		imageView.setOnTouchListener(new OnTouchListener() {			@Override			public boolean onTouch(View arg0, MotionEvent arg1) {				return false;			}		});	}	@Override	public void onDestroyView() {		String imgUrl = ad.getBigImage();		if (imgUrl != null && !imgUrl.equals("")) {			AsyncTaskLoaderImage.getInstance(context).recycleBitmap(TAG, imgUrl);		}		super.onDestroyView();	}}