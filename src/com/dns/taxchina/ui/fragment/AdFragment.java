package com.dns.taxchina.ui.fragment;import netlib.net.AsyncTaskLoaderImage;import netlib.net.AsyncTaskLoaderImage.BitmapImageCallback;import android.content.Intent;import android.graphics.Bitmap;import android.graphics.drawable.BitmapDrawable;import android.graphics.drawable.ColorDrawable;import android.graphics.drawable.Drawable;import android.graphics.drawable.TransitionDrawable;import android.os.Bundle;import android.os.Handler;import android.view.LayoutInflater;import android.view.MotionEvent;import android.view.View;import android.view.View.OnTouchListener;import android.view.ViewGroup;import android.widget.ImageView;import com.dns.taxchina.R;import com.dns.taxchina.service.model.IndexHeadModel;import com.dns.taxchina.ui.NewsDetailActivity;/** * @author fubiao * @version create time:2014-3-10_下午3:17:52 * @Description 广告 fragment */public class AdFragment extends BaseFragment {	private IndexHeadModel ad;	public static final String INTENT = "intent";	private ImageView imageView;//	private TextView title, indexView;	private int index;	private Handler mHandler = new Handler();	public static AdFragment newInstance(IndexHeadModel model, int index) {		AdFragment f = new AdFragment();		Bundle args = new Bundle();		args.putSerializable(INTENT, model);		args.putInt("index", index);		f.setArguments(args);		return f;	}	@SuppressWarnings("unchecked")	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		index = getArguments().getInt("index");		this.ad = (IndexHeadModel) getArguments().getSerializable(INTENT);	}	@Override	protected void initData() {	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.ad_fragment, container, false);		imageView = (ImageView) view.findViewById(R.id.imageView);//		title = (TextView) view.findViewById(R.id.title);//		indexView = (TextView) view.findViewById(R.id.page_index);		return view;	}	@Override	protected void initWidgetActions() {//		title.setText(ad.getTitle());		String imgUrl = ad.getBigImage();//		indexView.setText((index + 1) + "/" + list.size());		imageView.setImageResource(R.drawable.default_360x175);		if (imgUrl != null && !imgUrl.equals("")) {			AsyncTaskLoaderImage.getInstance(context).loadAsync(TAG, imgUrl, imageView, new BitmapImageCallback() {				@Override				public void onImageLoaded(final Bitmap bitmap, final String url) {					mHandler.post(new Runnable() {						@Override						public void run() {							if (bitmap != null && !bitmap.isRecycled()) {								if (getActivity() != null) {									TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(android.R.color.transparent),											new BitmapDrawable(context.getResources(), bitmap) });									imageView.setImageDrawable(td);									td.startTransition(200);								}							} else {								imageView.setImageResource(R.drawable.default_360x175);							}						}					});				}			});		}		imageView.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				Intent intent = new Intent(context, NewsDetailActivity.class);				intent.putExtra(NewsDetailActivity.NEWS_DETAIL_MODEL, ad);				startActivity(intent);			}		});		imageView.setOnTouchListener(new OnTouchListener() {			@Override			public boolean onTouch(View arg0, MotionEvent arg1) {//				HomeContentFragment f = (HomeContentFragment) getActivity().getSupportFragmentManager()//						.findFragmentByTag(KYMConstant.HOME_FRAGMENT_TAG);//				f.shutdownADPlay();				return false;			}		});	}	@Override	public void onDestroyView() {		String imgUrl = ad.getBigImage();		if (imgUrl != null && !imgUrl.equals("")) {			AsyncTaskLoaderImage.getInstance(context).recycleBitmap(TAG, imgUrl);		}		super.onDestroyView();	}}