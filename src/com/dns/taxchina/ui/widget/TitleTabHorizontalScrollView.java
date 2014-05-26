package com.dns.taxchina.ui.widget;

import java.util.ArrayList;
import java.util.List;

import netlib.util.ResourceUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dns.taxchina.R;

/**
 * @author henzil.jack E-mail:henzil.jack@gmail.com
 * @version 创建时间：2013-7-1 下午12:00:36
 * @Description 新闻页tab组建
 * 
 *              用于新闻及类似新闻页的tab组件，如果有左右的手势冲突，则调用packConflictViewId方法，把id传进去。
 */
@SuppressLint("ResourceAsColor")
public class TitleTabHorizontalScrollView extends HorizontalScrollView {

	private ViewPager viewPager;

	private int left;

	private String viewId = null;

	// 选择某个标签时，viewpager切换是否有动画
	private boolean isAnim;

	private List<String> titleList = new ArrayList<String>();

	private Activity mContext;

	private LinearLayout horizontalLayout;

	// 当前在第几屏
	private int currentIndex;

	// 之前在第几屏
	private int oldIndex;

	private ViewGroup parentView;

	public TitleTabHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = (Activity) context;
		initView(mContext);
	}

	public TitleTabHorizontalScrollView(Context context) {
		super(context);
		mContext = (Activity) context;
		initView(mContext);
	}

	private void initView(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (horizontalLayout == null) {
					horizontalLayout = (LinearLayout) getChildAt(0);
				}
				if (horizontalLayout.getChildCount() > 0) {
//					width = horizontalLayout.getChildAt(0).getWidth();
//					scrollViewWidth = getWidth();
//					Log.e("tag", "width = " + width);
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});
	}

	public void packConflictViewId(String viewId) {
		this.viewId = viewId;
		if (viewId != null && !viewId.equals("")) {
			ResourceUtil resourceUtil = ResourceUtil.getInstance(getContext());
			int id = resourceUtil.getViewId(viewId);
			if (id > 0) {
				View v = getRootView().findViewById(id);
				if (v instanceof ViewGroup) {
					parentView = (ViewGroup) v;
					return;
				}
			}
		}
		parentView = null;
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
//				Log.e("tag", "left = "+left );
				// 左中点
				int tempLeft = 0;
				for (int i = 0; i < arg0; i++) {
					tempLeft = tempLeft + horizontalLayout.getChildAt(i).getWidth();
				}
//				Log.e("tag", "left = "+left );
				int leftCenterX = tempLeft + (horizontalLayout.getChildAt(arg0).getWidth() / 2) - left;
//				int leftCenterX = arg0 * width + (width / 2) - left;
//				Log.e("tag", "leftCenterX = "+leftCenterX );
//				int rightCenterX = right - (arg0 * width + (width/2));
//				Log.e("tag", "rightCenterX = "+rightCenterX );
				int center = getWidth() / 2;
//				Log.e("tag", "center = "+center );
				int scollX = leftCenterX - center;
//				Log.e("tag", "scollX = "+scollX );
				currentIndex = arg0;
				setLine();
				scrollBy(scollX, 0);
				if (itemClickListener != null) {
					itemClickListener.clickItem(arg0);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		left = l;
//		right = left + scrollViewWidth;
	}

	public boolean isAnim() {
		return isAnim;
	}

	public void setAnim(boolean isAnim) {
		this.isAnim = isAnim;
	}

	public void setAllTitle(List<String> list) {
		this.titleList.clear();
		this.titleList.addAll(list);
		if (horizontalLayout == null) {
			horizontalLayout = (LinearLayout) getChildAt(0);
		}
		horizontalLayout.removeAllViews();
		for (int i = 0; i < list.size(); i++) {

			View view = mContext.getLayoutInflater().inflate(R.layout.title_tab_item, null);
			TextView textView = (TextView) view.findViewById(R.id.item_text);
			view.setTag(i);
			textView.setText(list.get(i));
			
			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int item = (Integer) v.getTag();
					viewPager.setCurrentItem(item, true);
					if (itemClickListener != null) {
						itemClickListener.clickItem(item);
					}
				}
			});
			horizontalLayout.addView(view);
		}
		currentIndex = 0;
		setLine();
	}

	private void setLine() {
		if (oldIndex < 0 || currentIndex < 0) {
			return;
		}
		View oldV = horizontalLayout.getChildAt(oldIndex);
		if (oldV != null) {
			oldV.findViewById(R.id.line).setVisibility(View.GONE);
			((TextView) oldV.findViewById(R.id.item_text)).setTextColor(getContext().getResources().getColor(
					R.color.title_tab_horizontal_text_default));
		}
		View currentView = horizontalLayout.getChildAt(currentIndex);
		if (currentView != null) {
			currentView.findViewById(R.id.line).setVisibility(View.VISIBLE);
			((TextView) currentView.findViewById(R.id.item_text)).setTextColor(getContext().getResources().getColor(
					R.color.title_tab_horizontal_text_press));
		}
		oldIndex = currentIndex;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		postParentMoveViewNotification(true);
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 用于屏蔽和打开底部id为mainScollLayout的父视图左右滑动手势
	 * 
	 * 如果底部没有id为mainScollLayout的view。则不执行任何方法。
	 * 
	 * */
	private void postParentMoveViewNotification(boolean flage) {
		if (parentView == null && viewId != null) {
			// 有可能初始化时没找到view，再去找一遍。
			packConflictViewId(viewId);
		}
		if (parentView != null) {
			parentView.requestDisallowInterceptTouchEvent(flage);
		}
	}

	private OnItemClickListener itemClickListener;

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.itemClickListener = listener;
	}

	public interface OnItemClickListener {
		public void clickItem(int postion);
	}

}