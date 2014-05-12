package com.dns.taxchina.ui.fragment;import netlib.net.AsyncTaskLoaderImage;import netlib.net.AsyncTaskLoaderImage.BitmapImageCallback;import android.graphics.Bitmap;import android.graphics.drawable.BitmapDrawable;import android.graphics.drawable.ColorDrawable;import android.graphics.drawable.Drawable;import android.graphics.drawable.TransitionDrawable;import android.os.Bundle;import android.os.Handler;import android.view.LayoutInflater;import android.view.MotionEvent;import android.view.View;import android.view.View.OnTouchListener;import android.view.ViewGroup;import android.widget.ImageView;import com.dns.taxchina.R;import com.dns.taxchina.service.model.BaseItemModel;/** * @author fubiao * @version create time:2014-3-10_下午3:17:52 * @Description 新闻 item head fragment */public class NewsItemHeadFragment extends BaseFragment {	private BaseItemModel ad;	public static final String INTENT = "intent";	private ImageView imageView;	// private TextView title, indexView;	private int index;	private Handler mHandler = new Handler();	public static NewsItemHeadFragment newInstance(BaseItemModel model, int index) {		NewsItemHeadFragment f = new NewsItemHeadFragment();		Bundle args = new Bundle();		args.putSerializable(INTENT, model);		args.putInt("index", index);		f.setArguments(args);		return f;	}	@SuppressWarnings("unchecked")	@Override	public void onCreate(Bundle savedInstanceState) {		super.onCreate(savedInstanceState);		index = getArguments().getInt("index");		this.ad = (BaseItemModel) getArguments().getSerializable(INTENT);	}	@Override	protected void initData() {	}	@Override	protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		View view = inflater.inflate(R.layout.news_item_head_fragment, container, false);		imageView = (ImageView) view.findViewById(R.id.imageView);		// title = (TextView) view.findViewById(R.id.title);		// indexView = (TextView) view.findViewById(R.id.page_index);		return view;	}	@Override	protected void initWidgetActions() {		// title.setText(ad.getTitle());		String imgUrl = ad.getImage();		// indexView.setText((index + 1) + "/" + list.size());		imageView.setImageResource(R.drawable.default_360x125);		if (imgUrl != null && !imgUrl.equals("")) {			AsyncTaskLoaderImage.getInstance(context).loadAsync(TAG, imgUrl, imageView, new BitmapImageCallback() {				@Override				public void onImageLoaded(final Bitmap bitmap, final String url) {					mHandler.post(new Runnable() {						@Override						public void run() {							if (bitmap != null && !bitmap.isRecycled()) {								if (getActivity() != null) {									TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(android.R.color.transparent),											new BitmapDrawable(context.getResources(), bitmap) });									imageView.setImageDrawable(td);									td.startTransition(200);								} else {									bitmap.recycle();								}							} else {								imageView.setImageResource(R.drawable.default_360x125);							}						}					});				}			});		}		imageView.setOnClickListener(new View.OnClickListener() {			@Override			public void onClick(View v) {				onClickItem();			}		});		imageView.setOnTouchListener(new OnTouchListener() {			@Override			public boolean onTouch(View arg0, MotionEvent arg1) {				// HomeContentFragment f = (HomeContentFragment)				// getActivity().getSupportFragmentManager()				// .findFragmentByTag(KYMConstant.HOME_FRAGMENT_TAG);				// f.shutdownADPlay();				return false;			}		});	}	@Override	public void onDestroyView() {		String imgUrl = ad.getImage();		if (imgUrl != null && !imgUrl.equals("")) {			AsyncTaskLoaderImage.getInstance(context).recycleBitmap(TAG, imgUrl);		}		super.onDestroyView();	}	private void onClickItem() {		// switch (ad.getType()) {		// case 0:		// // 网址		// Intent webIntent = new Intent(context, ADWebActivity.class);		// webIntent.putExtra(ActivityConstants.AD_WEB_TITLE, ad.getTitle());		// webIntent.putExtra(ActivityConstants.AD_WEB_URL, ad.getUrl());		// startActivity(webIntent);		// break;		// case 1:		// // 商家		// Intent intent = new Intent(context, ShopDetailsActivity.class);		// intent.putExtra(ActivityConstants.SHOP_DETAILS_ID, ad.getId());		// startActivity(intent);		// break;		// case 2:		// // 活动		// // TODO		// ToastUtil.warnMessageByStr(context, "跳转活动");		// break;		// case 3:		// // 会员卡详情		// Intent i = new Intent(context, UserCardDetailActivity.class);		// i.putExtra(UserCardDetailActivity.CARD_ID_KEY, ad.getId() + "");		// i.putExtra(UserCardDetailActivity.CARD_STYLE_KEY, ad.getCardStyle());		// startActivity(i);		// break;		// default:		// break;		// }	}}